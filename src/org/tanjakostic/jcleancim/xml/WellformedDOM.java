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
import java.io.InputStream;

import org.tanjakostic.jcleancim.util.ProgrammerErrorException;
import org.tanjakostic.jcleancim.util.Util;
import org.w3c.dom.Document;

/**
 * Abstract class as a supertype for a DOM document that will not use validation, such as for XML
 * schema or a simple XML where we don't care about the validation.
 *
 * @author tatjana.kostic@ieee.org
 * @version $Id: WellformedDOM.java 21 2019-08-12 15:44:50Z dev978 $
 */
abstract public class WellformedDOM {

	private final File _file;
	private final Document _document;
	private final NamespaceCache _nsCache;

	protected WellformedDOM(String filePath, XmlString content) throws XmlParsingException {
		if (filePath == null && content == null) {
			throw new ProgrammerErrorException("Both path and content are null.");
		}
		if (filePath != null && content != null) {
			throw new ProgrammerErrorException("Both path and content non-null.");
		}

		WellformedDOMBuilder domBuilder = new WellformedDOMBuilder();
		if (filePath != null) {
			Util.ensureNotEmpty(filePath, "filePath");
			_file = new File(filePath);
			_document = domBuilder.readAndValidate(_file);
		} else {
			Util.ensureNotNull(content, "content");
			Util.ensureNotEmpty(content.toString(), "content");
			_file = null;
			_document = domBuilder.readAndValidate(content);
		}

		_nsCache = new NamespaceCache(_document, true);
	}

	public File getFile() {
		return _file;
	}

	public XmlString asXmlString() {
		return JaxpHelper.asXml(_document, null);
	}

	public InputStream asInputStream() {
		if (getFile() != null) {
			return XmlUtil.xmlAsInputStream(getFile(), false);
		}
		return XmlUtil.xmlAsInputStream(asXmlString());
	}

	/** Returns DOM document. */
	public Document getDocument() {
		return _document;
	}

	public NamespaceCache getNsCache() {
		return _nsCache;
	}
}
