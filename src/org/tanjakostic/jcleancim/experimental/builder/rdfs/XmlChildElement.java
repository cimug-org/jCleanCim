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
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import org.tanjakostic.jcleancim.xml.JaxpHelper;
import org.tanjakostic.jcleancim.xml.XmlNs;
import org.w3c.dom.Element;

/**
 * XML child element (child of rdf:Description) used in the CIM RDF Schema.
 *
 * @author tatjana.kostic@ieee.org
 * @version $Id: XmlChildElement.java 21 2019-08-12 15:44:50Z dev978 $
 */
public class XmlChildElement extends XmlTag {

	/** applicable to all CimXmlElement-s */
	static final XmlChildElement label = new XmlChildElement(XmlNamespace.rdfs, "label");

	/** applicable to all CimXmlElement-s */
	static final XmlChildElement comment = new XmlChildElement(XmlNamespace.rdfs, "comment");

	/** applicable to (non-enum, non-dt, non-compound) class */
	public static final XmlChildElement subClassOf = new XmlChildElement(XmlNamespace.rdfs,
			"subClassOf");

	/** applicable to rdf:Property for association or attribute of non-enum class */
	static final XmlChildElement domain = new XmlChildElement(XmlNamespace.rdfs, "domain");

	/** applicable to rdf:Description (attribute of enum class) to designate enum class */
	public static final XmlChildElement type = new XmlChildElement(XmlNamespace.rdf, "type");

	/** applicable to association end or attribute having compound type */
	public static final XmlChildElement range = new XmlChildElement(XmlNamespace.rdfs, "range");

	/** applicable to enum classes */
	static final XmlChildElement hasStereotype = new XmlChildElement(XmlNamespace.uml,
			"hasStereotype");

	// ----------------------------

	/** applicable to association end (rdf:Description, with type=rdf:Property) */
	public static final XmlChildElement inverseRoleName = new XmlChildElement(XmlNamespace.cims,
			"inverseRoleName");

	/** applicable to rdfs:Class; it's CIM package of the class */
	public static final XmlChildElement belongsToCategory = new XmlChildElement(XmlNamespace.cims,
			"belongsToCategory");

	/** applicable to rdf:Property for attribute whose type is datatype class or primitive */
	static final XmlChildElement dataType = new XmlChildElement(XmlNamespace.cims, "dataType");

	/** applicable to rdf:Property for association */
	static final XmlChildElement multiplicity = new XmlChildElement(XmlNamespace.cims,
			"multiplicity");

	private XmlChildElement(XmlNs ns, String name) {
		super(ns, name);
	}

	// ----------------- API -------------

	/**
	 * Returns trimmed text contents of single <code>parent</code>'s child of this kind (tag) if it
	 * exists, empty string otherwise.
	 *
	 * @param parent
	 * @return text if element exists, empty string otherwise.
	 */
	public String getText(Element parent) {
		Element e = JaxpHelper.getFirstNamedSubElement(parent, this.getQName());
		return (e == null) ? "" : e.getTextContent().trim();
	}

	/** Returns all child elements of <code>parent</code> having this kind (tag). */
	public List<Element> getAllOfThisKind(Element parent) {
		return JaxpHelper.getNamedSubElements(parent, getName());
	}

	/**
	 * Returns value of resource attribute on single <code>parent</code>'s child of this kind if it
	 * exists, null otherwise.
	 *
	 * @param parent
	 * @return value of resource attribute on single <code>parent</code>'s child of this kind if it
	 *         exists, null otherwise.
	 */
	public String getResourceValue(Element parent) {
		Element chiElem = JaxpHelper.getFirstNamedSubElement(parent, this.getName());
		return XmlAttribute.resource.getValue(chiElem);
	}

	/**
	 * Returns values of resource attribute on all <code>parent</code>'s children of this kind if
	 * they exist, empty list otherwise.
	 */
	public List<String> getResourceValues(Element parent) {
		List<Element> chiElems = getAllOfThisKind(parent);

		List<String> result = new ArrayList<String>(chiElems.size());
		for (Element chiElem : chiElems) {
			String uri = chiElem.getAttribute(XmlAttribute.resource.getQName());
			result.add(uri);
		}
		return result;
	}

	// ------------------------------------

	/**
	 * Returns validated URI for given string.
	 *
	 * @param uri
	 * @return validated URI for given string.
	 * @throws CimSchemaException
	 *             if given string is syntactically invalid URI.
	 */
	public static URI getValidatedUri(String uri) throws CimSchemaException {
		try {
			return new URI(uri);
		} catch (URISyntaxException e) {
			throw new CimSchemaException(e);
		}
	}

	/**
	 * Returns the fragment of the uri which is the resource attribute on child element
	 * <code>chiElemKind</code> of <code>parent</code> if existing, null otherwise. Use this method
	 * only when sure that the URI has already been validated.
	 *
	 * @param parent
	 * @param chiElemKind
	 * @return name of the resource.
	 */
	public static String getResourceName(Element parent, XmlChildElement chiElemKind) {
		String uri = chiElemKind.getResourceValue(parent);
		return (uri == null) ? null : deduceFragment(uri);
	}

	private static String deduceFragment(String uri) {
		return uri.substring(uri.lastIndexOf("#") + 1);
	}

	/**
	 * Returns the fragments of the uris which are the resource attribute on all child elements
	 * <code>chiElemKind</code> of <code>parent</code>. This is the helper for those child element
	 * kinds that can be multiple for a parent (e.g., type, subClassOf).
	 *
	 * @param parent
	 * @param chiElemKind
	 * @return the names of the resources.
	 */
	public static List<String> getResourceNames(Element parent, XmlChildElement chiElemKind) {
		List<String> uris = chiElemKind.getResourceValues(parent);

		List<String> result = new ArrayList<String>(uris.size());
		for (String uri : uris) {
			result.add(deduceFragment(uri));
		}
		return result;
	}
}
