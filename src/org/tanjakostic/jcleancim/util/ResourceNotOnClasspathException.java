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
 * @author tatjana.kostic@ieee.org
 * @version $Id: ResourceNotOnClasspathException.java 1519 2011-03-20 11:46:04Z
 *          tatjana.kostic@ieee.org $
 */
public class ResourceNotOnClasspathException extends RuntimeException {
	private static final long serialVersionUID = 1L;

	public static final String CLASSPATH = System.getProperty("java.class.path");

	/** */
	public ResourceNotOnClasspathException(String filePath) {
		this(filePath, null);
	}

	public ResourceNotOnClasspathException(String filePath, Throwable cause) {
		super("'" + filePath + "' (classpath = '" + CLASSPATH + "')", cause);
	}

	public ResourceNotOnClasspathException(Throwable cause) {
		super(cause);
	}
}
