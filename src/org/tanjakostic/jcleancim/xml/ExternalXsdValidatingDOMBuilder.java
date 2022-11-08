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

import java.io.InputStream;

import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;

import org.tanjakostic.jcleancim.util.Util;
import org.xml.sax.SAXException;

/**
 * DOM builder configured to validate against the external schema (specified programmatically, by
 * the constructor argument).
 *
 * @author tatjana.kostic@ieee.org
 * @version $Id: ExternalXsdValidatingDOMBuilder.java 1940 2011-08-21 23:16:38Z
 *          tatjana.kostic@ieee.org $
 */
public class ExternalXsdValidatingDOMBuilder extends AbstractXsdValidatingDOMBuilder {

	/**
	 * Constructor.
	 *
	 * @param externalSchema
	 *            non-null external schema as input stream.
	 */
	public ExternalXsdValidatingDOMBuilder(InputStream externalSchema) {
		this(externalSchema, false);
	}

	protected ExternalXsdValidatingDOMBuilder(InputStream externalSchema, boolean respectDtd) {
		super(externalSchema, respectDtd);
	}

	@Override
	protected void configureBuilderFactoryWithSchema(DocumentBuilderFactory builderFactory)
			throws XmlParsingException {
		Util.ensureNotNull(getExternalSchema(), "externalSchema");

		SchemaFactory schemaFactory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
		Source[] schemaSources = new Source[] { new StreamSource(getExternalSchema()) };
		try {
			Schema schema = schemaFactory.newSchema(schemaSources);
			builderFactory.setSchema(schema);
		} catch (SAXException e) {
			throw new XmlParsingException("Failed to read XML schema", getParsingErrors(), e);
		}
	}
}
