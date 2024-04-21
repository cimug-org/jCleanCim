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
import java.util.List;

import org.apache.log4j.Logger;
import org.w3c.dom.Element;

/**
 * Representaton of an XML schema document; provides some helper methods to facilitate creating
 * instance XML documents compliant with this schema.
 *
 * @author tatjana.kostic@ieee.org
 * @version $Id: XmlSchemaDOM.java 21 2019-08-12 15:44:50Z dev978 $
 */
public class XmlSchemaDOM extends WellformedDOM {
	private static final Logger _logger = Logger.getLogger(XmlSchemaDOM.class.getName());

	private final String _rootTag;
	private final XmlNs _targetNs;

	/**
	 * Constructs schema document from the file <code>schemaPath</code>.
	 *
	 * @param schemaPath
	 *            non-null valid path of the schema.
	 */
	public XmlSchemaDOM(String schemaPath) throws XmlParsingException {
		this(schemaPath, null);
	}

	/**
	 * Constructs schema document from the string content <code>schemaContent</code>;
	 * {@link #getFile()} will return null.
	 *
	 * @param schemaContent
	 *            non-null, non-empty schema content as XML string.
	 */
	public XmlSchemaDOM(XmlString schemaContent) throws XmlParsingException {
		this(null, schemaContent);
	}

	private XmlSchemaDOM(String schemaPath, XmlString schemaContent) throws XmlParsingException {
		super(schemaPath, schemaContent);

		Element schemaRoot = getDocument().getDocumentElement();
		String targetNsUri = schemaRoot.getAttribute("targetNamespace");
		_targetNs = getNsCache().getXmlNs(targetNsUri);
		if (_targetNs == null) {
			_logger.warn("    target ns = null");
		} else {
			_logger.debug("    target ns = " + _targetNs.toString());
		}

		List<Element> elems = JaxpHelper.getNamedSubElements(schemaRoot, "xs:element");
		if (elems.isEmpty()) {
			throw new XmlException("Invalid schema document: no xs:element.");
		}
		_rootTag = elems.get(0).getAttribute("name");
	}

	public String getRootTag() {
		return _rootTag;
	}

	public XmlNs getTargetNs() {
		return _targetNs;
	}

	/** Validates <code>instanceDOM</code> against this schema DOM and returns parsing errors. */
	public SaxErrorData validate(XmlInstanceDOM instanceDOM) {
		return validate(instanceDOM, this);
	}

	/** Validates <code>instance</code> against <code>schema</code> and returns parsing errors. */
	public static SaxErrorData validate(XmlInstanceDOM instance, XmlSchemaDOM schema) {
		InputStream schemaInputStream = schema.asInputStream();
		AbstractXsdValidatingDOMBuilder builder = new ExternalXsdInternalDtdValidatingDOMBuilder(
				schemaInputStream);
		builder.getParsingErrors().reset();
		builder.validate(instance.getDocument());
		return builder.getParsingErrors();
	}
}
