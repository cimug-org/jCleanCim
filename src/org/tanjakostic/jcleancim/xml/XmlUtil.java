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

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;

import org.tanjakostic.jcleancim.util.ResourceNotOnClasspathException;
import org.tanjakostic.jcleancim.util.Util;
import org.xml.sax.InputSource;

/**
 * @author tatjana.kostic@ieee.org
 * @version $Id: XmlUtil.java 21 2019-08-12 15:44:50Z dev978 $
 */
public class XmlUtil {

	public static final String ENCODING = "UTF-8";

	private XmlUtil() {
		// prevents creation
	}

	// ------------------ providing input streams -----------------------

	/**
	 * Returns input stream for file.
	 *
	 * @param xmlFile
	 *            non-null file.
	 * @param isOnClasspath
	 *            whether to search for <code>xmlFileUri</code> on the classpath.
	 * @throws ResourceNotOnClasspathException
	 * @throws XmlParsingException
	 */
	public static InputStream xmlAsInputStream(File xmlFile, boolean isOnClasspath)
			throws XmlParsingException, ResourceNotOnClasspathException {
		Util.ensureNotNull(xmlFile, "xmlFile");
		return xmlAsInputStream(xmlFile.getAbsolutePath(), isOnClasspath);
	}

	/**
	 * Returns input stream for file path, on classpath or not.
	 *
	 * @param xmlFileUri
	 *            non-null, non-empty file URI.
	 * @param isOnClasspath
	 *            whether to search for <code>xmlFileUri</code> on the classpath.
	 * @throws ResourceNotOnClasspathException
	 * @throws XmlParsingException
	 */
	public static InputStream xmlAsInputStream(String xmlFileUri, boolean isOnClasspath)
			throws XmlException, ResourceNotOnClasspathException {
		Util.ensureNotEmpty(xmlFileUri, "xmlFileUri");
		InputStream result;
		try {
			File file = new File(xmlFileUri);
			result = (isOnClasspath) ? Util.findResourceOnClasspath(file.getName())
					: new FileInputStream(file);
		} catch (FileNotFoundException e) {
			throw new XmlException("File not found: " + e.getMessage(), e);
		}
		return result;
	}

	/**
	 * Returns input stream for XML content.
	 *
	 * @param xmlText
	 * @throws XmlException
	 */
	public static InputStream xmlAsInputStream(XmlString xmlText) {
		String xml = xmlText.toString();
		Util.ensureNotEmpty(xml, "xmlText");
		try {
			return new ByteArrayInputStream(xml.getBytes(ENCODING));
		} catch (UnsupportedEncodingException e) {
			throw new XmlException("XML encoding problem: " + e.getMessage(), e);
		}
	}

	// ------------------ providing input sources -----------------------

	/**
	 * Returns input source with {@value #ENCODING} encoding for XML content.
	 *
	 * @param xmlText
	 * @throws XmlParsingException
	 */
	public static InputSource xmlAsInputSource(XmlString xmlText) {
		InputSource source = new InputSource(xmlAsInputStream(xmlText));
		source.setEncoding(ENCODING);
		return source;
	}

}
