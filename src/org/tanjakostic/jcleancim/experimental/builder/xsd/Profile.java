/**
 * Copyright (C) 2009-2019 Tatjana (Tanja) Kostic
 * <p>
 * This file belongs to jCleanCim, a tool supporting tasks of UML model managers for IEC TC57 CIM
 * and 61850 models.
 * <p>
 * This program is free software: you can redistribute it and/or modify it under the terms of the
 * GNU Lesser General Public License version 3.
 * <p>
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without
 * even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details. You should have received a copy of the GNU Lesser
 * General Public License along with this program. If not, see <http://www.gnu.org/licenses/>.
 */

package org.tanjakostic.jcleancim.experimental.builder.xsd;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;
import org.tanjakostic.jcleancim.common.Config;
import org.tanjakostic.jcleancim.common.OwningWg;
import org.tanjakostic.jcleancim.model.UmlPackage;
import org.tanjakostic.jcleancim.util.Util;
import org.tanjakostic.jcleancim.xml.JaxpHelper;
import org.tanjakostic.jcleancim.xml.XmlNs;
import org.tanjakostic.jcleancim.xml.XmlSchemaDOM;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

/**
 * Class that parses and analyses an .xsd profile and stores its content in-memory. It is then used
 * to build one package in the regular in-memory model.
 * <p>
 * Implementation note: I'm using dumb and trivial XPath expressions, which might not be optimal...
 * <p>
 * FIXME: Consider using new {@link XmlSchemaDOM}!
 *
 * @author tatjana.kostic@ieee.org
 * @version $Id: Profile.java 21 2019-08-12 15:44:50Z dev978 $
 */
public class Profile {

	private static final Logger _logger = Logger.getLogger(Profile.class.getName());

	public static final String TARGET_NS_PREFIX = "m";
	public static final char FRAGMENT_SEPARATOR = '#';

	// private static final Map<String, String> NAMESPACES = new LinkedHashMap<String, String>();
	private static final Map<String, String> PRIMITIVES = new LinkedHashMap<String, String>();

	static {
		// these are now initialise at the schema read:
		// NAMESPACES.put("xs", "http://www.w3.org/2001/XMLSchema");
		// NAMESPACES.put("sawsdl", "http://www.w3.org/ns/sawsdl");
		// NAMESPACES.put(TARGET_NS_PREFIX, "http://iec.ch/TC57/profile#");

		PRIMITIVES.put("xs:boolean", "Boolean");
		PRIMITIVES.put("xs:integer", "Integer");
		PRIMITIVES.put("xs:float", "Float");
		PRIMITIVES.put("xs:string", "String");
		PRIMITIVES.put("xs:decimal", "Decimal");
		PRIMITIVES.put("xs:dateTime", "DateTime");
		PRIMITIVES.put("xs:date", "Date");
		PRIMITIVES.put("xs:time", "Time");
		PRIMITIVES.put("xs:duration", "Duration");
	}

	public static Map<String, String> getPrimitives() {
		return Collections.unmodifiableMap(PRIMITIVES);
	}

	private final Config _cfg;
	private final File _xsdFile;
	private final List<String> _subdirNames;
	private final XmlSchemaDOM _schema;

	// static final String XPATH_TARGETNS = "/xs:schema/@targetNamespace";
	private final Map<String, String> _targetNamespace;

	private static final String XPATH_MODEL_REFS = "//@sawsdl:modelReference";
	private final Map<String, String> _cimNamespaces;

	private static final String XPATH_ENVELOPE = "/xs:schema/xs:element/@name";
	private final String _envelopeName;

	public static final String XPATH_LOCAL_DOC = "xs:annotation/xs:documentation";

	public static final String XPATH_CLASSES_AND_COMPOUNDS = "//@sawsdl:modelReference/parent::xs:complexType";
	private final Map<String, List<Element>> _classesAndCompounds = new LinkedHashMap<String, List<Element>>();

	private final Map<String, List<ProfileEnumeration>> _enums = new LinkedHashMap<String, List<ProfileEnumeration>>();
	private final Map<String, List<ProfileDatatype>> _datatypes = new LinkedHashMap<String, List<ProfileDatatype>>();

	private final Set<String> _cimPrimitives = new LinkedHashSet<String>();

	public static final String XPATH_ATTR_AND_ASSOC_ENDS = "//@sawsdl:modelReference/parent::xs:element";
	public static final String XPATH_CHOICE_ASSOC_ENDS = "//@sawsdl:modelReference/parent::xs:choice";
	private final Map<String, List<Element>> _attributesAndAssocEnds = new LinkedHashMap<String, List<Element>>();

	public static final String XPATH_LITERALS = "//xs:restriction/child::xs:enumeration";
	private final Map<String, List<Element>> _literals = new LinkedHashMap<String, List<Element>>();

	// subclasses of X: //xs:complexContent/child::xs:extension[@base="m:X"]
	// or equivalent: //*[@base="m:X"]

	private final Set<Element> _unclassifieds = new LinkedHashSet<Element>();

	/**
	 * Constructor.
	 * <p>
	 * TODO: ctor from XmlString
	 *
	 * @param cfg
	 * @param xsdFile
	 */
	public Profile(Config cfg, File xsdFile) {
		Util.ensureNotNull(cfg, "cfg");
		Util.ensureNotNull(xsdFile, "xsdFile");

		_cfg = cfg;
		_xsdFile = xsdFile;
		_subdirNames = Util.splitDirAndFileNames(cfg.getProfilesRelpath(), xsdFile.getPath());

		_logger.info("-- processing profile from " + xsdFile.getPath());
		_schema = new XmlSchemaDOM(xsdFile.getAbsolutePath());

		_targetNamespace = Util.createKeyValuePair(_schema.getTargetNs().getPrefix(), _schema
				.getTargetNs().getUri());
		_cimNamespaces = initCimNamespacesAndStoreParentElements(_schema.getDocument());
		_envelopeName = initEnvelopeName(_schema.getDocument());

		initEnumsAndDatatypes(_schema.getDocument());

		_logger.info(this.toString());
	}

	private String initEnvelopeName(Document doc) {
		Node envelopeNode = JaxpHelper.selectNode(Profile.XPATH_ENVELOPE, doc,
				getNamespacesVararg());
		return envelopeNode.getTextContent();
	}

	/**
	 * Figures out CIM standard and extension namespaces, and stores elements containing that
	 * reference as unclassified; later on, we'll move the elements we recognise into maps, and what
	 * is left in that set will really be unclassified.
	 */
	private Map<String, String> initCimNamespacesAndStoreParentElements(Document doc) {
		List<Element> selecteds = JaxpHelper.selectElements(XPATH_MODEL_REFS, doc,
				getNamespacesVararg());

		List<String> modelRefValues = new ArrayList<String>();
		for (Node modelRefNode : selecteds) {
			_unclassifieds.add((Element) modelRefNode.getParentNode());
			modelRefValues.add(modelRefNode.getTextContent());
		}

		return collectModelNamespaces(modelRefValues, "cim");
	}

	// FIXME: write test
	static Map<String, String> collectModelNamespaces(List<String> modelRefValues, String nsPrefix) {
		Map<String, String> result = new LinkedHashMap<String, String>();
		Set<String> baseUris = new LinkedHashSet<String>();
		for (String uriText : modelRefValues) {
			List<String> uriParts = Util.splitCharSeparatedTokens(uriText, FRAGMENT_SEPARATOR);
			if (!uriParts.isEmpty()) {
				String baseUri = uriParts.get(0) + FRAGMENT_SEPARATOR;
				if (!baseUris.contains(baseUri)) {
					String prefix = (baseUris.isEmpty()) ? nsPrefix : (nsPrefix + baseUris.size());
					baseUris.add(baseUri); // tracing progress
					result.put(prefix, baseUri);
				}
			}
		}
		return result;
	}

	private void initEnumsAndDatatypes(Document doc) {
		XmlNs[] namespaces = getNamespacesVararg();
		List<Element> simpleTypes = JaxpHelper.selectElements(
				"//@sawsdl:modelReference/parent::xs:simpleType", doc, namespaces);

		for (Element simpleType : simpleTypes) {
			Node modelRefNode = JaxpHelper.selectNode("@sawsdl:modelReference", simpleType,
					namespaces);
			List<String> modelRefSplitUri = Util.splitCharSeparatedTokens(
					modelRefNode.getTextContent(), FRAGMENT_SEPARATOR);

			String nsUri = (modelRefSplitUri.size() > 0) ? modelRefSplitUri.get(0) : null;
			String name = (modelRefSplitUri.size() > 1) ? modelRefSplitUri.get(1) : null;
			String nsPrefix = _schema.getNsCache().getPrefix(nsUri);
			List<String> typeDocParas = initDescription(simpleType, namespaces);

			List<Element> literalNodes = JaxpHelper.selectElements("xs:restriction/xs:enumeration",
					simpleType, namespaces);
			if (!literalNodes.isEmpty()) {
				ProfileEnumeration penum = new ProfileEnumeration(simpleType, nsPrefix, nsUri,
						name, typeDocParas);
				if (!_enums.containsKey(name)) {
					_enums.put(name, new ArrayList<ProfileEnumeration>());
				}
				_enums.get(name).add(penum);

				for (Node literalNode : literalNodes) {
					Node valueAttr = JaxpHelper.selectNode("@value", literalNode, namespaces);
					List<String> literalDocParas = initDescription(literalNode);
					penum.addLiteral(new ProfileLiteral((Element) literalNode, nsPrefix, nsUri,
							valueAttr.getTextContent(), literalDocParas));
				}
			} else {
				ProfileDatatype pdatatype = new ProfileDatatype(simpleType, nsPrefix, nsUri, name,
						typeDocParas);
				if (!_datatypes.containsKey(name)) {
					_datatypes.put(name, new ArrayList<ProfileDatatype>());
				}
				_datatypes.get(name).add(pdatatype);

				String primitiveXPath = "xs:restriction/@base";
				Node primitiveNode = JaxpHelper.selectNode(primitiveXPath, simpleType, namespaces);
				if (PRIMITIVES.containsKey(primitiveNode.getTextContent())) {
					_cimPrimitives.add(PRIMITIVES.get(primitiveNode.getTextContent()));
				}
			}
			_unclassifieds.remove(simpleType);
		}

	}

	private List<String> initDescription(Node node, XmlNs... namespaces) {
		List<Node> docNodes = JaxpHelper.selectNodes(XPATH_LOCAL_DOC, node, namespaces);
		List<String> docParas = new ArrayList<String>();
		for (Node docNode : docNodes) {
			docParas.add(docNode.getTextContent());
		}
		return docParas;
	}

	// =========================== API =============================

	/**
	 * Returns list of names, deduced from the profile file path. The first element is the name of
	 * the {@link OwningWg} for this profile, the remaining names correspond to the names of
	 * {@link UmlPackage}-s to be created recursiverly.
	 */
	public List<String> getSubdirNames() {
		return _subdirNames;
	}

	/**
	 * Returns profile name (deduced from the file name, without extension); for envelope name, use
	 * {@link #getEnvelopeName()}.
	 */
	public String getName() {
		return getSubdirNames().get(getSubdirNames().size() - 1);
	}

	/** Returns envelope name (in instance file, this will be the root element). */
	public String getEnvelopeName() {
		return _envelopeName;
	}

	/**
	 * Returns the file that has been used for initalisation, null if the profile has been created
	 * from a string.
	 */
	public File getXsdFile() {
		return _xsdFile;
	}

	/**
	 * Returns the target namespace information, with prefix {@value #TARGET_NS_PREFIX} as key, and
	 * URI as value.
	 */
	public Map<String, String> getTargetNamespace() {
		return _targetNamespace;
	}

	// FIXME: cache this somewhere
	public List<XmlNs> getNamespaces() {
		return _schema.getNsCache().getAllXmlNs();
	}

	private XmlNs[] getNamespacesVararg() {
		List<XmlNs> namespaces = getNamespaces();
		return namespaces.toArray(new XmlNs[namespaces.size()]);
	}

	/**
	 * Returns namespace information for all model references found in the profile, with prefix as
	 * key, and URI as value.
	 */
	public Map<String, String> getCimNamespaces() {
		return _cimNamespaces;
	}

	public Map<String, List<Element>> getClassesAndCompounds() {
		return _classesAndCompounds;
	}

	public Collection<String> getCimPrimitives() {
		return Collections.unmodifiableCollection(_cimPrimitives);
	}

	public Map<String, List<ProfileEnumeration>> getEnums() {
		return _enums;
	}

	public Map<String, List<ProfileDatatype>> getDatatypes() {
		return _datatypes;
	}

	public Map<String, List<Element>> getAttributesAndAssocEnds() {
		return _attributesAndAssocEnds;
	}

	public Map<String, List<Element>> getLiterals() {
		return _literals;
	}

	public Set<Element> getUnclassifieds() {
		return _unclassifieds;
	}

	/**
	 * Returns whether this profile follows the convention to have the envelope name same as the
	 * profile name.
	 */
	public boolean hasInconsistentEnvelopeName() {
		return !getEnvelopeName().equals(getName());
	}

	/**
	 * Returns whether this profile follows the convention to have the namespace end with the
	 * profile name (followed by the URI fragment separator {@value #FRAGMENT_SEPARATOR} ).
	 */
	public boolean hasInconsistentNamespace() {
		String uri = getTargetNamespace().get(TARGET_NS_PREFIX);
		return uri.endsWith(getName() + FRAGMENT_SEPARATOR);
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("Profile: ").append(getName()).append(Util.NL);
		if (getXsdFile() != null) {
			sb.append("  from file: ").append(getXsdFile().getAbsolutePath()).append(Util.NL);
		}
		sb.append("  envelope name: ").append(getEnvelopeName()).append(Util.NL);
		sb.append("  target namespace: ")
		.append(getTargetNamespace().entrySet().iterator().next().toString())
		.append(Util.NL);
		sb.append("  CIM namespaces: ").append(getCimNamespaces().toString()).append(Util.NL);

		sb = appendCollectionInfo(sb, getCimPrimitives().size(), "CIM primitive types");
		sb = appendCollectionInfo(sb, getEnums().size(), "enumerated types");
		sb = appendCollectionInfo(sb, getDatatypes().size(), "CIM datatypes");
		sb = appendCollectionInfo(sb, getUnclassifieds().size(),
				"unclassified CIM model references");

		return sb.toString();
	}

	private StringBuilder appendCollectionInfo(StringBuilder sb, int size, String what) {
		return sb.append("  ").append(size).append(" ").append(what).append(Util.NL);
	}

}
