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

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.tanjakostic.jcleancim.xml.XmlNs;

/**
 * Values used for resource attribute (), used in RDF and OWL dialects.
 *
 * @author tatjana.kostic@ieee.org
 * @version $Id: XmlResourceValue.java 21 2019-08-12 15:44:50Z dev978 $
 */
public class XmlResourceValue extends XmlTag {

	public static final XmlResourceValue Package = new XmlResourceValue(XmlNamespace.uml, "Package");
	// not considered as Class:
	public static final XmlResourceValue Datatype = new XmlResourceValue(XmlNamespace.rdfs,
			"Datatype");

	// TODO: attribute (if has stereotype=attribute) or association end (no stereotype)
	public static final XmlResourceValue Property = new XmlResourceValue(XmlNamespace.rdf,
			"Property");

	// ---------------------------

	/** UML class with rdf:type rdf:resource="http://www.w3.org/2000/01/rdf-schema#Class" */
	public static final XmlResourceValue Class = new XmlResourceValue(XmlNamespace.rdfs, "Class");

	/**
	 * Added by hand after parsing RDF, to bring back CIM primitive types: UML primitive with
	 * rdf:type rdf:resource="http://www.w3.org/2000/01/rdf-schema#Primitive"
	 */
	static final XmlResourceValue Primitive = new XmlResourceValue(XmlNamespace.rdfs, "Primitive");

	// ---------------------------

	/** UML package: {packageName} */
	static final XmlResourceValue ClassCategory = new XmlResourceValue(XmlNamespace.cims,
			"ClassCategory");

	/** (rdf) UML multiplicity [1]: TODO with cims:multiplicity rdf:resource="...#M:1 */
	static final XmlElement M1 = new XmlElement(XmlNamespace.cims, "M:1");

	/** (rdf) UML multiplicity [0..1]: TODO with cims:multiplicity rdf:resource="...#M:0..1 */
	static final XmlElement M01 = new XmlElement(XmlNamespace.cims, "M:0..1");

	/** (rdf) UML multiplicity [0..*]: TODO with cims:multiplicity rdf:resource="...#M:0..n */
	static final XmlElement M0n = new XmlElement(XmlNamespace.cims, "M:0..n");

	/** (rdf) UML multiplicity [1..*]: TODO with cims:multiplicity rdf:resource="...#M:1..n */
	static final XmlElement M1n = new XmlElement(XmlNamespace.cims, "M:1..n");

	// ----------------------------

	// TODO with cims:stereotype (rdf)/uml:hasStereotype (owl) rdf:resource="...#enumeration"
	public static final XmlResourceValue enumeration = new XmlResourceValue(XmlNamespace.uml,
			"enumeration");

	// TODO with cims:stereotype (rdf)/uml:hasStereotype (owl) rdf:resource="...#attribute"
	public static final XmlResourceValue attribute = new XmlResourceValue(XmlNamespace.uml,
			"attribute");

	// TODO with cims:stereotype (rdf)/uml:hasStereotype (owl) rdf:resource="...#compound"
	static final XmlResourceValue compound = new XmlResourceValue(XmlNamespace.uml, "compound");

	// TODO with cims:stereotype (rdf)/uml:hasStereotype (owl) rdf:resource="...#concrete"
	static final XmlResourceValue concrete = new XmlResourceValue(XmlNamespace.uml, "concrete");

	// TODO with cims:stereotype (rdf)/uml:hasStereotype (owl) rdf:resource="...#byreference"
	static final XmlResourceValue byreference = new XmlResourceValue(XmlNamespace.uml,
			"byreference");

	// TODO with cims:stereotype (rdf)/uml:hasStereotype (owl) rdf:resource="...#aggregateOf"
	static final XmlResourceValue aggregateOf = new XmlResourceValue(XmlNamespace.uml,
			"aggregateOf");

	// TODO with cims:stereotype (rdf)/uml:hasStereotype (owl) rdf:resource="...#ofAggregate"
	static final XmlResourceValue ofAggregate = new XmlResourceValue(XmlNamespace.uml,
			"ofAggregate");

	// -------------------------------

	// Below are primitive types, where the profile replaces CIM primitives with XSD types:

	// TODO with cims:dataType rdf:resource="...#boolean"
	static final XmlResourceValue xboolean = new XmlResourceValue(XmlNamespace.xsd, "boolean");
	static final XmlResourceValue xinteger = new XmlResourceValue(XmlNamespace.xsd, "integer");
	static final XmlResourceValue xfloat = new XmlResourceValue(XmlNamespace.xsd, "float");
	static final XmlResourceValue xstring = new XmlResourceValue(XmlNamespace.xsd, "string");
	static final XmlResourceValue xdecimal = new XmlResourceValue(XmlNamespace.xsd, "decimal");
	static final XmlResourceValue xdateTime = new XmlResourceValue(XmlNamespace.xsd, "dateTime");
	static final XmlResourceValue xdate = new XmlResourceValue(XmlNamespace.xsd, "date");
	static final XmlResourceValue xtime = new XmlResourceValue(XmlNamespace.xsd, "time");
	static final XmlResourceValue xduration = new XmlResourceValue(XmlNamespace.xsd, "duration");

	private static final Map<String, String> _cimPrimitiveClassResourceValues = new HashMap<String, String>();

	static {
		_cimPrimitiveClassResourceValues.put(XmlResourceValue.xboolean.getURI(),
				XmlNamespace.cim.getUri() + "Boolean");
		_cimPrimitiveClassResourceValues.put(XmlResourceValue.xinteger.getURI(),
				XmlNamespace.cim.getUri() + "Integer");
		_cimPrimitiveClassResourceValues.put(XmlResourceValue.xfloat.getURI(),
				XmlNamespace.cim.getUri() + "Float");
		_cimPrimitiveClassResourceValues.put(XmlResourceValue.xstring.getURI(),
				XmlNamespace.cim.getUri() + "String");
		_cimPrimitiveClassResourceValues.put(XmlResourceValue.xdecimal.getURI(),
				XmlNamespace.cim.getUri() + "Decimal");
		_cimPrimitiveClassResourceValues.put(XmlResourceValue.xdateTime.getURI(),
				XmlNamespace.cim.getUri() + "DateTime");
		_cimPrimitiveClassResourceValues.put(XmlResourceValue.xdate.getURI(),
				XmlNamespace.cim.getUri() + "Date");
		_cimPrimitiveClassResourceValues.put(XmlResourceValue.xtime.getURI(),
				XmlNamespace.cim.getUri() + "Time");
		_cimPrimitiveClassResourceValues.put(XmlResourceValue.xduration.getURI(),
				XmlNamespace.cim.getUri() + "Duration");
	}

	public static String getCimPrimitiveClassResourceValue(String xsUri) {
		return _cimPrimitiveClassResourceValues.get(xsUri);
	}

	public static Collection<String> getCimPrimitiveClassResourceValues() {
		return Collections.unmodifiableCollection(_cimPrimitiveClassResourceValues.values());
	}

	private XmlResourceValue(XmlNs ns, String name) {
		super(ns, name);
	}
}
