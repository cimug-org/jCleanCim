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

import org.tanjakostic.jcleancim.util.Util;

/**
 * Wrapper for the underlying parsing/validation exceptions. The first fatal or error condition is
 * available from {@link #getErrorData()}.
 *
 * @author tatjana.kostic@ieee.org
 * @version $Id: XmlParsingException.java 21 2019-08-12 15:44:50Z dev978 $
 */
public class XmlParsingException extends RuntimeException {
	private static final long serialVersionUID = 1L;

	private final SaxErrorData _errorData;

	/** Constructor - carries concrete XML validation errors that can be manipulated. */
	public XmlParsingException(String message, SaxErrorData errorData, Throwable cause) {
		super(message + Util.NL + errorData.toString(), cause);
		_errorData = errorData;
	}

	public SaxErrorData getErrorData() {
		return _errorData;
	}
}
