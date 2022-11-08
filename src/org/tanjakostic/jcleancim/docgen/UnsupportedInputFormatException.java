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

package org.tanjakostic.jcleancim.docgen;

import org.tanjakostic.jcleancim.util.ApplicationException;

/**
 * Used when the format for input document is a non-supported one.
 *
 * @author tatjana.kostic@ieee.org
 * @version $Id: UnsupportedOutputFormatException.java 1850 2011-07-24 10:38:26Z
 *          tatjana.kostic@ieee.org $
 */
public class UnsupportedInputFormatException extends ApplicationException {
	private static final long serialVersionUID = 1L;

	public UnsupportedInputFormatException() {
		super();
	}

	public UnsupportedInputFormatException(String message, Throwable cause) {
		super(message, cause);
	}

	public UnsupportedInputFormatException(String message) {
		super(message);
	}

	public UnsupportedInputFormatException(Throwable cause) {
		super(cause);
	}
}
