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

package org.tanjakostic.jcleancim.util;

/**
 * Simple wrapper for RT exception, used instead of asserts to indicate where the implementation
 * does not respect some contract.
 *
 * @author tatjana.kostic@ieee.org
 * @version $Id: ProgrammerErrorException.java 21 2019-08-12 15:44:50Z dev978 $
 */
public class ProgrammerErrorException extends RuntimeException {
	private static final long serialVersionUID = 1L;

	public ProgrammerErrorException(String message) {
		super(message);
	}

	public ProgrammerErrorException(Throwable cause) {
		super(cause);
	}

	public ProgrammerErrorException(String message, Throwable cause) {
		super(message, cause);
	}
}
