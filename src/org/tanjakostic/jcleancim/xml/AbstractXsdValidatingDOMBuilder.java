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

import java.io.IOException;
import java.io.InputStream;

import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.validation.Schema;
import javax.xml.validation.Validator;

import org.tanjakostic.jcleancim.util.ProgrammerErrorException;
import org.tanjakostic.jcleancim.util.Util;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

/**
 * Common implementation for readers that validate against the XML schema, internal or external.
 *
 * @author tatjana.kostic@ieee.org
 * @version $Id: AbstractXsdValidatingDOMBuilder.java 1940 2011-08-21 23:16:38Z
 *          tatjana.kostic@ieee.org $
 */
abstract public class AbstractXsdValidatingDOMBuilder extends AbstractConfiguredDOMBuilder {

	public static final String W3C_XML_SCHEMA = XMLConstants.W3C_XML_SCHEMA_NS_URI;

	private final InputStream _externalSchema;

	protected AbstractXsdValidatingDOMBuilder(InputStream externalSchema,
			boolean saxReaderSetValidate) throws XmlParsingException {
		super(saxReaderSetValidate);

		_externalSchema = externalSchema;

		configureBuilderFactoryWithSchema(getDOMBuilderFactory());
	}

	abstract protected void configureBuilderFactoryWithSchema(DocumentBuilderFactory factory)
			throws XmlParsingException;

	protected final InputStream getExternalSchema() {
		return _externalSchema;
	}

	/**
	 * Validates existing DOM <code>document</code> against XML schema.
	 *
	 * @throws XmlParsingException
	 */
	public void validate(Document document) throws XmlParsingException {
		Util.ensureNotNull(document, "document");
		Schema schema = getDOMBuilderFactory().getSchema();
		if (schema == null) {
			throw new ProgrammerErrorException("DOM builder factory should have been configured"
					+ " with an XML schema.");
		}

		Validator validator = schema.newValidator();
		DOMSource source = new DOMSource(document);
		validator.setErrorHandler(getErrorHandler());
		try {
			validator.validate(source);
		} catch (SAXException | IOException e) {
			throw new XmlParsingException("Invalid content in DOM document:", getParsingErrors(), e);
		}
	}
}
