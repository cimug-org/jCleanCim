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

package org.tanjakostic.jcleancim.experimental.builder.rdfs;

import java.net.URI;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.tanjakostic.jcleancim.xml.JaxpHelper;
import org.tanjakostic.jcleancim.xml.XmlNs;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * XML element, direct child of the RDF schema. All methods are static and take as argument DOM
 * Element.
 *
 * @author tatjana.kostic@ieee.org
 * @version $Id: XmlElement.java 21 2019-08-12 15:44:50Z dev978 $
 */
public class XmlElement extends XmlTag {

	/**
	 * The only used XML element.
	 */
	static final XmlElement Description = new XmlElement(XmlNamespace.rdf, "Description");

	/**
	 * Constructor.
	 */
	XmlElement(XmlNs ns, String name) {
		super(ns, name);
	}

	/**
	 * Returns all child elements of <code>parent</code> that have 'resource' attribute.
	 *
	 * @param parent
	 * @return all child elements of <code>parent</code> that have resource attribute.
	 */
	public static List<Element> getChildrenWithResource(Element parent, XmlNs ns) {
		String xpathExpression = "*[@" + XmlAttribute.resource.toString() + "]";
		return JaxpHelper.selectElements(xpathExpression, parent, ns);
	}

	/**
	 * Peforms several potential modifications:
	 * <ul>
	 * <li>removes "Package_" substring from 'about' attribute of <code>parent</code> and 'resource'
	 * attribute of its child, when applicable</li>
	 * <li>prepends the URI of <code>ns</code> to 'about' attribute of <code>parent</code> and
	 * 'resource' attribute of all its children, if they use default namespace syntax (i.e., start
	 * with "#")</li>
	 * <li>replaces XML primitive types in children with CIM primitive types</li>
	 * </ul>
	 *
	 * @param ns
	 *            namespace, used to prepend its URI.
	 * @param parent
	 */
	public static void normaliseToRdf(XmlNs ns, Element parent) {
		doReplace(parent, XmlAttribute.about, "Package_", "");
		doPrepend(ns, parent, XmlAttribute.about);
		for (Element chi : getChildrenWithResource(parent, ns)) {
			doReplace(chi, XmlAttribute.resource, "Package_", "");
			doPrepend(ns, chi, XmlAttribute.resource);
			Element root = (Element) parent.getParentNode();
			replaceXmlPrimitiveWithCimPrimitiveAndAddItToRoot(ns, chi, root);
		}
	}

	private static void doReplace(Element elem, XmlAttribute attr, String toReplace,
			String replacement) {
		String uri = attr.getValue(elem);
		if (uri == null) {
			return;
		}

		int idx = uri.indexOf(toReplace);
		if (idx == -1) {
			return;
		}

		String adjustedUri = uri.replace(toReplace, replacement);
		elem.setAttribute(attr.getQName(), adjustedUri);
	}

	private static void doPrepend(XmlNs ns, Element elem, XmlAttribute attr) {
		String uri = attr.getValue(elem);
		if (uri.startsWith("#")) {
			String adjustedUri = ns.getUriWithoutFragmentSeparator() + uri;
			elem.setAttribute(attr.getQName(), adjustedUri);
		}
	}

	private static Map<String, Element> _cimPrimitiveUris = new HashMap<String, Element>();

	private static void replaceXmlPrimitiveWithCimPrimitiveAndAddItToRoot(XmlNs ns, Element chi,
			Element root) {
		if (ns.qName(chi.getNodeName()).equals(XmlChildElement.range.getQName())) {
			String resourceAttrQname = XmlAttribute.resource.getQName();
			String xsUri = chi.getAttribute(resourceAttrQname);
			String cimPrimitiveUri = XmlResourceValue.getCimPrimitiveClassResourceValue(xsUri);
			if (cimPrimitiveUri != null) {
				if (!_cimPrimitiveUris.containsKey(cimPrimitiveUri)) {
					Element primElement = createCimPrimitiveClass(root, cimPrimitiveUri);
					_cimPrimitiveUris.put(cimPrimitiveUri, primElement);
				}
				chi.setAttribute(resourceAttrQname, cimPrimitiveUri);
			}
		}
	}

	/**
	 * RDF uses XSD primitive types and completely ignores those present in CIM. We add by hand CIM
	 * primitive types to the document, and replace the XSD primitive types with CIM primitives.
	 *
	 * @param about
	 *            about attribute of the primitive
	 */
	private static Element createCimPrimitiveClass(Element root, String about) {
		Document d = root.getOwnerDocument();
		Element primClass = JaxpHelper
				.createQSubElement(root, XmlElement.Description.getQName(), d);
		primClass.setAttribute(XmlAttribute.about.getQName(), about);

		Element comment = JaxpHelper.createQSubElement(primClass,
				XmlChildElement.comment.getQName(), d);
		comment.setTextContent("RDF translated this primitive CIM class to xs primitive type.");

		Element label = JaxpHelper
				.createQSubElement(primClass, XmlChildElement.label.getQName(), d);
		label.setAttribute(XmlAttribute.lang.getQName(), "en");
		label.setTextContent(URI.create(about).getFragment());

		Element belongsToCategory = JaxpHelper.createQSubElement(primClass,
				XmlChildElement.belongsToCategory.getQName(), d);
		String resValue = XmlNamespace.cim.getUri() + "Package_Domain";
		belongsToCategory.setAttribute(XmlAttribute.resource.getQName(), resValue);

		Element type = JaxpHelper.createQSubElement(primClass, XmlChildElement.type.getQName(), d);
		type.setAttribute(XmlAttribute.resource.getQName(), XmlResourceValue.Primitive.getURI());

		return primClass;
	}

	// ----- used to separate UML packages, classes, properties and enum literals -----

	public static boolean isPackage(Element parent) {
		return hasChildWithResourceValue(parent, XmlChildElement.type,
				XmlResourceValue.ClassCategory);
	}

	public static boolean isClass(Element parent) {
		return hasChildWithResourceValue(parent, XmlChildElement.type, XmlResourceValue.Class)
				|| hasChildWithResourceValue(parent, XmlChildElement.type,
						XmlResourceValue.Primitive);
	}

	public static boolean isProperty(Element parent) {
		return hasChildWithResourceValue(parent, XmlChildElement.type, XmlResourceValue.Property);
	}

	public static boolean isEnumLiteral(Element parent) {
		return !(isPackage(parent) || isClass(parent) || isProperty(parent));
	}

	private static boolean hasChildWithResourceValue(Element parent, XmlChildElement chi,
			XmlResourceValue val) {
		List<String> resourceValues = chi.getResourceValues(parent);
		return resourceValues.contains(val.getURI());
	}

	private static boolean hasChildren(Element parent, XmlChildElement chi) {
		return chi.getAllOfThisKind(parent).size() != 0;
	}

	// ----- used to determine detailed kinds of UML constructs from child elements -----

	/** Returns whether <code>parent</code> is a UML enumerated class. */
	public static boolean isPrimitiveClass(Element parent) {
		return hasChildWithResourceValue(parent, XmlChildElement.type, XmlResourceValue.Primitive);
	}

	/** Returns whether <code>parent</code> is a UML enumerated class. */
	public static boolean isEnumClass(Element parent) {
		return hasChildWithResourceValue(parent, XmlChildElement.hasStereotype,
				XmlResourceValue.enumeration);
	}

	/** Returns whether <code>parent</code> is a UML compound class. */
	public static boolean isCompoundClass(Element parent) {
		return hasChildWithResourceValue(parent, XmlChildElement.hasStereotype,
				XmlResourceValue.compound);
	}

	/** Returns whether <code>parent</code> is a UML datatype class. */
	public static boolean isDatatypeClass(Element parent) {
		return isClass(parent) && !hasChildren(parent, XmlChildElement.belongsToCategory);
	}

	/**
	 * Tries to deduce the name of the UML inverse association end from different child elements, to
	 * suit different RDF dialects. For ill-defined model, values may be multiple, thus we return a
	 * list of names. If none is found, returns empty list.
	 *
	 * @param parent
	 *            Parent element under whose child elements to search for inverse association end
	 *            name.
	 * @return name of the UML inverse association end.
	 */
	public static List<String> deduceInverseRoleNames(Element parent) {
		return XmlChildElement.getResourceNames(parent, XmlChildElement.inverseRoleName);
	}

	/**
	 * Tries to deduce the name of the UML class that is range for a property from different child
	 * elements, to suit different RDF dialects. For ill-defined model, values may be multiple, thus
	 * we return a list of names. If none is found, returns empty list.
	 *
	 * @param elem
	 *            Parent element under whose child elements to search for range name.
	 * @return name of the UML class that is range for a property.
	 */
	public static List<String> deduceRangeNames(Element elem) {
		if (isEnumLiteral(elem)) {
			return XmlChildElement.getResourceNames(elem, XmlChildElement.range);
		}
		return Collections.emptyList();
	}

	/**
	 * Tries to deduce the name of the UML package from different child elements. If none is found,
	 * returns null.
	 *
	 * @param elem
	 *            Parent element under whose child elements to search for package name.
	 * @return name of the UML package
	 */
	public static String deducePackageName(Element elem) {
		return XmlChildElement.getResourceName(elem, XmlChildElement.belongsToCategory);
	}
}
