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

import org.w3c.dom.Document;
import org.xml.sax.InputSource;

/**
 * Configured DOM builder, containing potentially parser errors.
 * <p>
 * Implementations for configuring the readers using DOM are according to <a
 * href="http://www.edankert.com/validate.html">How to Validate XML using Java</a>.
 *
 * @author tatjana.kostic@ieee.org
 * @version $Id: ConfiguredDOMBuilder.java 21 2019-08-12 15:44:50Z dev978 $
 */
public interface ConfiguredDOMBuilder {

	/** Returns errors collected during parsing; may be empty but never null. */
	public SaxErrorData getParsingErrors();

	/**
	 * Reads and validates <code>xmlFile</code> and returns it as DOM document.
	 *
	 * @throws XmlParsingException
	 */
	public Document readAndValidate(File xmlFile) throws XmlParsingException;

	/**
	 * Reads and validates <code>xmlText</code> and returns it as DOM document.
	 *
	 * @throws XmlParsingException
	 */
	public Document readAndValidate(XmlString xmlText) throws XmlParsingException;

	/**
	 * Reads and validates <code>source</code> and returns it as DOM document.
	 *
	 * @throws XmlParsingException
	 */
	public Document readAndValidate(InputSource source) throws XmlParsingException;
}
