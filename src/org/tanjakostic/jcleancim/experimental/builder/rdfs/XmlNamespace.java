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

import org.tanjakostic.jcleancim.xml.NamespaceCache;
import org.tanjakostic.jcleancim.xml.XmlNs;

/**
 * Common namespaces found in CIM RDF/OWL Schema.
 *
 * @author tatjana.kostic@ieee.org
 * @version $Id: XmlNamespace.java 21 2019-08-12 15:44:50Z dev978 $
 */
public class XmlNamespace extends XmlNs {

	// xml:base="http://iec.ch/TC57/2010/CIM-schema-cim15"
	private static final String XML_BASE = "http://iec.ch/TC57/2010/CIM-schema-cim15";

	public static final XmlNs cim = new XmlNs("j.0", XML_BASE + "#");

	public static final XmlNs rdfs = new XmlNs("rdfs", "http://www.w3.org/2000/01/rdf-schema#");

	public static final XmlNs rdf = new XmlNs("rdf", "http://www.w3.org/1999/02/22-rdf-syntax-ns#");

	public static final XmlNs xml = new XmlNs("xml", "");

	/**
	 * (OWL) Note that the namespace is not read from the schema, but is used to replace CIM
	 * primitive types (Float, String, Integer, Boolean, Decimal, Date, Time, DateTime, Duration)
	 * with a child element such as e.g.,
	 * <code>&lt;rdfs:range rdf:resource="http://www.w3.org/2001/XMLSchema#string"/&gt;.</code>
	 */
	public static final XmlNs xsd = new XmlNs("xsd", "http://www.w3.org/2001/XMLSchema#");

	public static XmlNs owl = new XmlNs("owl", "http://www.w3.org/2002/07/owl#");

	public static final XmlNs cims = new XmlNs("cims",
			"http://iec.ch/TC57/1999/rdf-schema-extensions-19990926#");

	public static final XmlNs uml = new XmlNs("uml", "http://langdale.com.au/2005/UML#");

	public static XmlNs msg = new XmlNs("msg", "http://langdale.com.au/2005/Message#");

	public static XmlNs dc = new XmlNs("dc", "http://purl.org/dc/elements/1.1/");

	public XmlNamespace(String prefix, String uri) {
		super(prefix, uri);
	}

	public XmlNamespace(String prefix, String uri, NamespaceCache cache) {
		super(prefix, uri, cache);
	}
}
