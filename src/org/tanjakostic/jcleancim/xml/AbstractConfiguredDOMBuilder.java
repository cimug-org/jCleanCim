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
import java.io.IOException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.validation.SchemaFactory;

import org.apache.log4j.Logger;
import org.tanjakostic.jcleancim.util.ProgrammerErrorException;
import org.tanjakostic.jcleancim.util.Util;
import org.w3c.dom.Document;
import org.xml.sax.ErrorHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

/**
 * Implementation for commons of all the DOM builders.
 *
 * @author tatjana.kostic@ieee.org
 * @version $Id: AbstractConfiguredDOMBuilder.java 1940 2011-08-21 23:16:38Z tatjana.kostic@ieee.org
 *          $
 */
abstract public class AbstractConfiguredDOMBuilder implements ConfiguredDOMBuilder {
	private static final Logger _logger = Logger.getLogger(AbstractConfiguredDOMBuilder.class
			.getName());

	private final DocumentBuilderFactory _builderFactory;
	private final SaxErrorCollector _errorHandler;

	protected AbstractConfiguredDOMBuilder(boolean builderFactorySetValidate) {
		_errorHandler = new SaxErrorCollector(new SaxErrorData(), true);

		_builderFactory = DocumentBuilderFactory.newInstance();
		_builderFactory.setNamespaceAware(true);
		_builderFactory.setValidating(builderFactorySetValidate);
	}

	protected final DocumentBuilderFactory getDOMBuilderFactory() {
		return _builderFactory;
	}

	protected final DocumentBuilder getDOMBuilder() throws ParserConfigurationException {
		DocumentBuilder builder = getDOMBuilderFactory().newDocumentBuilder();
		builder.setErrorHandler(getErrorHandler());
		return builder;
	}

	@Override
	public final SaxErrorData getParsingErrors() {
		return _errorHandler.getData();
	}

	@Override
	public final Document readAndValidate(File xmlFile) {
		Util.ensureNotNull(xmlFile, "xmlFile");

		return readAndValidate(xmlFile, null, "Invalid content in XML file:");
	}

	@Override
	public final Document readAndValidate(XmlString xmlText) {
		Util.ensureNotNull(xmlText, "xmlText");

		InputSource source = XmlUtil.xmlAsInputSource(xmlText);
		return readAndValidate(null, source, "Invalid content in XML text:");
	}

	@Override
	public final Document readAndValidate(InputSource source) {
		Util.ensureNotNull(source, "source");

		return readAndValidate(null, source, "Invalid content in XML input source:");
	}

	private Document readAndValidate(File xmlFile, InputSource src, String message) {
		if (xmlFile == null && src == null) {
			throw new ProgrammerErrorException("Both xmlFile and source are null.");
		}
		if (xmlFile != null && src != null) {
			throw new ProgrammerErrorException("Both xmlFile and source are non-null.");
		}

		Document document;
		try {
			document = (xmlFile != null) ? getDOMBuilder().parse(xmlFile) : getDOMBuilder().parse(
					src);
		} catch (SAXException e) {
			_logger.warn("1: " + getParsingErrors().toString());
			throw new XmlParsingException(message, getParsingErrors(), e);
		} catch (IOException | ParserConfigurationException e) {
			throw new XmlException(message, e);
		}

		if (getParsingErrors().hasErrorOrFatal()) {
			_logger.warn("2: " + getParsingErrors().toString());
			throw new XmlParsingException(message, getParsingErrors(), null);
		}
		return document;
	}

	protected final ErrorHandler getErrorHandler() {
		return _errorHandler;
	}

	/**
	 * This default implementation returns null; override to return configured schema factory.
	 */
	protected SchemaFactory getSchemaFactory() {
		return null;
	}

	/**
	 * Simple implementation of SAX error handler when validating against schema.
	 *
	 * @author tatjana.kostic@ieee.org
	 * @version $Id: AbstractConfiguredDOMBuilder.java 1940 2011-08-21 23:16:38Z
	 *          tatjana.kostic@ieee.org $
	 */
	protected static class SaxErrorCollector implements ErrorHandler {
		private final SaxErrorData _data;
		private final boolean _collectAll;

		/**
		 * Constructor.
		 *
		 * @param data
		 *            data structure to fill with warnings, errors and fatals.
		 * @param collectAll
		 *            whether to collect all the issues during parsing (i.e., do the parsing until
		 *            the end), or to throw exception at the first error or fatal error.
		 */
		SaxErrorCollector(SaxErrorData data, boolean collectAll) {
			_data = data;
			_collectAll = collectAll;
		}

		private String getParseExceptionInfo(SAXParseException e) {
			StringBuilder sb = new StringBuilder(128);
			String systemId = e.getSystemId();
			if (systemId != null) {
				sb.append("URI=").append(systemId).append(", ");
			}
			sb.append("line ").append(e.getLineNumber());
			sb.append(", column ").append(e.getColumnNumber());
			sb.append(": ").append(e.getMessage());
			return sb.toString();
		}

		public SaxErrorData getData() {
			return _data;
		}

		public boolean isCollectAll() {
			return _collectAll;
		}

		// ------------ impl. of org.xml.sax.ErrorHandler ------------

		@Override
		public void warning(SAXParseException e) {
			final String msg = "___warn: " + getParseExceptionInfo(e);
			_data.addWarn(msg);
		}

		@Override
		public void error(SAXParseException e) throws SAXException {
			final String msg = "___error: " + getParseExceptionInfo(e);
			_data.addError(msg);
			if (isCollectAll()) {
				return;
			}
			throw new SAXException(msg);
		}

		@Override
		public void fatalError(SAXParseException e) throws SAXException {
			final String msg = "___fatal: " + getParseExceptionInfo(e);
			_data.addFatal(msg);
			if (isCollectAll()) {
				return;
			}
			throw new SAXException(msg);
		}
	}
}
