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

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.tanjakostic.jcleancim.xml.JaxpHelper;
import org.tanjakostic.jcleancim.xml.WellformedDOM;
import org.tanjakostic.jcleancim.xml.XmlParsingException;
import org.tanjakostic.jcleancim.xml.XmlString;
import org.w3c.dom.Element;

// import org.tanjakostic.jcleancim.jaxp.XmlInstanceDOM;

/**
 * Reads RDF Schema and provides classified DOM elements to be used when building model.
 *
 * @author tatjana.kostic@ieee.org
 * @version $Id: XmlDocument.java 21 2019-08-12 15:44:50Z dev978 $
 */
public class XmlDocument extends WellformedDOM {
	private static final Logger _logger = Logger.getLogger(XmlDocument.class.getName());

	private Map<String, Element> _packages;
	private Map<String, Element> _classes;
	private Map<String, Element> _properties;
	private Map<String, Element> _enumLiterals;
	private Map<String, Element> _unclassifieds;

	// here we have list of elements as values
	private Map<String, List<Element>> _duplicates;

	/**
	 * Constructs the DOM Document from CIM RDF Schema file, determines the dialect, fixes URIs if
	 * they end with white space, and classifies DOM elements by moving them from DOM Document root
	 * into internal maps.
	 */
	public XmlDocument(File file) {
		this(file.getAbsolutePath(), null);
	}

	/** Same as {@link #XmlDocument(File)}, but constructs the DOM document from xml string. */
	public XmlDocument(String xml) {
		this(null, new XmlString(xml));
	}

	private XmlDocument(String filePath, XmlString content) throws XmlParsingException {
		super(filePath, content);
		moveElementsFromRootToMaps(getDocument().getDocumentElement());
	}

	private void addDuplicate(String about, Element... elem) {
		List<Element> elems = _duplicates.get(about);
		if (elems == null) {
			elems = new ArrayList<Element>();
			_duplicates.put(about, elems);
		}
		elems.addAll(Arrays.asList(elem));
	}

	private void moveElementsFromRootToMaps(Element root) {
		_packages = new HashMap<String, Element>();
		_classes = new HashMap<String, Element>();
		_properties = new HashMap<String, Element>();
		_enumLiterals = new HashMap<String, Element>();
		_unclassifieds = new HashMap<String, Element>();

		_duplicates = new HashMap<String, List<Element>>();

		List<Element> children = JaxpHelper.getSubElements(root);
		for (Iterator<Element> it = children.iterator(); it.hasNext();) {
			Element elem = it.next();

			// potentially modifies element
			XmlElement.normaliseToRdf(XmlNamespace.cim, elem);

			String about = XmlAttribute.about.getValue(elem);

			if (XmlElement.isProperty(elem)) {
				// When we read a name already in props, we remove the one from props and add
				// both to duplicate collection - this happens with RDF only.
				if (_properties.containsKey(about)) {
					Element first = _properties.remove(about);
					addDuplicate(about, first, elem);
					_logger.trace("Added to duplicates property '" + about + "'.");
				} else {
					List<Element> multipleInverseOfs = XmlChildElement.inverseRoleName
							.getAllOfThisKind(elem);
					if (multipleInverseOfs.size() > 1) {
						addDuplicate(about, elem);
						_logger.trace("Added to duplicates 2 assoc '" + about + "'.");
					} else {
						_properties.put(about, elem);
					}
				}
			} else if (XmlElement.isClass(elem) || XmlElement.isPrimitiveClass(elem)) {
				_classes.put(about, elem);
			} else if (XmlElement.isPackage(elem)) {
				_packages.put(about, elem);
			} else if (XmlElement.isEnumLiteral(elem)) {
				_enumLiterals.put(about, elem);
			} else {
				_unclassifieds.put(about, elem);
			}
			it.remove();
		}

		assert (JaxpHelper.getSubElements(root).isEmpty()) : "all elements should have been moved to maps";

		for (String about : _unclassifieds.keySet()) {
			_logger.warn("Programming error: Unclassified top element: about='" + about + "'.");
		}
	}

	// ----------------------- API ----------------------------

	/**
	 * Returns the total number of top elements in this document.
	 *
	 * @return total number of top elements in this document.
	 */
	public int getElementCount() {
		return _packages.size() + _classes.size() + _properties.size() + _enumLiterals.size()
				+ _unclassifieds.size();
	}

	/**
	 * Returns elements in this document that have same name.
	 *
	 * @return elements in this document that have same name.
	 */
	public Map<String, List<Element>> getDuplicates() {
		return Collections.unmodifiableMap(_duplicates);
	}

	/** Returns the CIM UML packages in this document. */
	public Map<String, Element> getPackages() {
		return Collections.unmodifiableMap(_packages);
	}

	/** Returns the CIM UML classes (including datatypes, enums and compounds) in this document. */
	public Map<String, Element> getClasses() {
		return Collections.unmodifiableMap(_classes);
	}

	/** Returns the CIM UML attributes and association ends in this document. */
	public Map<String, Element> getProperties() {
		return Collections.unmodifiableMap(_properties);
	}

	/** Returns the CIM UML enumeration literals in this document. */
	public Map<String, Element> getEnumLiterals() {
		return Collections.unmodifiableMap(_enumLiterals);
	}

	/** Returns the CIM UML enumeration literals in this document. */
	public Map<String, Element> getUnclassifiedTopElements() {
		return Collections.unmodifiableMap(_unclassifieds);
	}
}
