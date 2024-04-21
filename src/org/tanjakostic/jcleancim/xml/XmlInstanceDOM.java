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

package org.tanjakostic.jcleancim.xml;

import java.io.File;

import org.tanjakostic.jcleancim.util.ProgrammerErrorException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * Common implementation for all XML instance documents.
 *
 * @author tatjana.kostic@ieee.org
 * @version $Id: XmlInstanceDOM.java 21 2019-08-12 15:44:50Z dev978 $
 */
abstract public class XmlInstanceDOM {

	private final File _instanceFile;
	private final XmlSchemaDOM _schema;
	private final Document _document;

	/**
	 * FIXME: test
	 * <p>
	 * Constructs this instance with empty qualified root element, and with reference to schema
	 * found in <code>schemaPath</code>.
	 *
	 * @param comment
	 *            (potentially null or empty) document comment.
	 * @param instancePath
	 *            path where this document can be saved as file.
	 * @param schemaPath
	 *            path where the schema can be found as file.
	 * @param rootTag
	 *            root element name
	 * @throws XmlParsingException
	 */
	protected XmlInstanceDOM(String comment, String instancePath, String schemaPath, String rootTag)
			throws XmlParsingException {
		this(comment, instancePath, schemaPath, null, rootTag);
	}

	/**
	 * Constructs this instance with empty qualified root element, and with reference to schema
	 * found in <code>schemaPath</code>; the root element tag is deduced from the schema.
	 *
	 * @param comment
	 *            (potentially null or empty) document comment.
	 * @param instancePath
	 *            path where this document can be saved as file.
	 * @param schemaPath
	 *            path where the schema can be found as file.
	 * @throws XmlParsingException
	 */
	protected XmlInstanceDOM(String comment, String instancePath, String schemaPath)
			throws XmlParsingException {
		this(comment, instancePath, schemaPath, null, null);
	}

	/**
	 * FIXME: test
	 * <p>
	 * Constructs this instance with empty qualified root element, and with <code>schema</code>,
	 * which potentially does not have the file representation (e.g., it may have been created from
	 * XML text, and does not exist as a file). This one is useful for testing.
	 *
	 * @param comment
	 * @param instancePath
	 * @param schema
	 * @param rootTag
	 * @throws XmlParsingException
	 */
	protected XmlInstanceDOM(String comment, String instancePath, XmlSchemaDOM schema,
			String rootTag) throws XmlParsingException {
		this(comment, instancePath, null, schema, rootTag);
	}

	/**
	 * Constructs this instance with empty qualified root element, and with <code>schema</code>,
	 * which potentially does not have the file representation (e.g., it may have been created from
	 * XML text, and does not exist as a file); the root element tag is deduced from the schema.
	 * This one is useful for testing.
	 *
	 * @param comment
	 * @param instancePath
	 * @param schema
	 * @throws XmlParsingException
	 */
	protected XmlInstanceDOM(String comment, String instancePath, XmlSchemaDOM schema)
			throws XmlParsingException {
		this(comment, instancePath, schema, null);
	}

	private XmlInstanceDOM(String comment, String instancePath, String schemaPath,
			XmlSchemaDOM schema, String rootTag) throws XmlParsingException {
		if (schemaPath == null && schema == null) {
			throw new ProgrammerErrorException("Both schema path and content are null.");
		}
		if (schemaPath != null && schema != null) {
			throw new ProgrammerErrorException("Both schema path and content non-null.");
		}

		_instanceFile = new File(instancePath);
		_schema = (schemaPath != null) ? new XmlSchemaDOM(schemaPath) : schema;
		String root = (rootTag != null) ? rootTag : _schema.getRootTag();
		_document = JaxpHelper.createDocumentWithRoot(comment, root, _schema.getTargetNs());
		JaxpHelper.addNamespace(_document, XmlNs.xsi);
		if (_schema.getFile() != null) {
			getRoot().setAttribute("xsi:schemaLocation",
					_schema.getTargetNs().getUri() + " " + _schema.getFile().getName());
		}
	}

	/**
	 * If initialised with an external schema, validates this instance document against that schema.
	 * Otherwise, validates against the schema that may be specified in the instance document as
	 * schema location.
	 *
	 * @throws XmlParsingException
	 */
	public SaxErrorData validate() {
		if (getSchema() != null) {
			return getSchema().validate(this);
		}
		AbstractXsdValidatingDOMBuilder builder = new InternalXsdValidatingDOMBuilder();
		builder.getParsingErrors().reset();
		builder.validate(_document);
		return builder.getParsingErrors();
	}

	/**
	 */
	public final void save() {
		JaxpHelper.asPrettyXml(_document, getInstanceFile());
	}

	public final String qname(String name) {
		return getTargetNs().qName(name);
	}

	public final String getPrettyXml() {
		return JaxpHelper.asPrettyXml(_document, null).toString();
	}

	public File getInstanceFile() {
		return _instanceFile;
	}

	public XmlSchemaDOM getSchema() {
		return _schema;
	}

	public XmlNs getTargetNs() {
		return getSchema().getTargetNs();
	}

	public Element getRoot() {
		return getDocument().getDocumentElement();
	}

	protected Document getDocument() {
		return _document;
	}

	/**
	 * Creates new element (by qualifying its <code>name</code> with the target namespace prefix),
	 * adds it under document root and returns that new element.
	 */
	public Element createSubElementUnderRoot(String name) {
		return JaxpHelper.createQSubElement(getRoot(), qname(name), getDocument());
	}

	/**
	 * Creates new element (by qualifying its <code>name</code> with the target namespace prefix),
	 * adds it under <code>el</code> and returns that new element.
	 */
	public Element createSubElement(Element el, String name) {
		return JaxpHelper.createQSubElement(el, qname(name), getDocument());
	}

	/** Adds sub-element to <code>el</code> and returns modified <code>el</code>. */
	public Element addSubElement(Element el, String qname) {
		return JaxpHelper.addQSubElement(el, qname, getDocument());
	}

	/** Adds CDATA section to <code>el</code> and returns modified <code>el</code>. */
	public Element addCDATA(Element el, String cdata) {
		return JaxpHelper.addCDATA(el, cdata, _document);
	}
}
