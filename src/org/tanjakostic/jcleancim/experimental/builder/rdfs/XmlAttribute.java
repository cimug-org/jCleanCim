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

import org.tanjakostic.jcleancim.xml.XmlNs;
import org.w3c.dom.Element;

/**
 * XML attributes used in the CIM RDF/OWL Schema.
 *
 * @author tatjana.kostic@ieee.org
 * @version $Id: XmlAttribute.java 21 2019-08-12 15:44:50Z dev978 $
 */
public class XmlAttribute extends XmlTag {

	/**
	 * (OWL) applicable to rdf:Description element
	 * <p>
	 * (RDF) applicable to all elements
	 */
	static final XmlAttribute about = new XmlAttribute(XmlNamespace.rdf, "about");

	/**
	 * applicable to rdfs:subClassOf, rdfs:domain, rdfs:range, rdf:type, ---
	 * <p>
	 * (OWL) applicable to owl:inverseOf, --- (and j.1:hasStereotype, owl:sameAs, ---)
	 * <p>
	 * (RDF) applicable to cims:inverseRoleName, --- cims:belongsToCategory, cims:dataType,
	 * cims:multiplicity
	 */
	public static final XmlAttribute resource = new XmlAttribute(XmlNamespace.rdf, "resource");

	/** applicable to rdfs:label child element */
	static final XmlAttribute lang = new XmlAttribute(XmlNamespace.xml, "lang");

	/** applicable to rdf:RDF root element */
	static final XmlAttribute base = new XmlAttribute(XmlNamespace.xml, "base");

	/* (OWL) no attributes: applicable to rdfs:comment, j.1:id chi elems */
	/* (RDF) no attributes: applicable to rdfs:comment, cims:profile, cims:isAggregate chi elems */

	private XmlAttribute(XmlNs ns, String name) {
		super(ns, name);
	}

	/**
	 * Returns the value of this attribute on <code>parent</code> element, or null if the parent is
	 * null or does not have this attribute.
	 *
	 * @param parent
	 * @return value of this attribute on <code>parent</code> element
	 */
	public String getValue(Element parent) {
		return (parent == null) ? null : parent.getAttribute(getQName());
	}
}
