/**
 * Copyright (C) 2009-2024 Tatjana (Tanja) Kostic
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

package org.tanjakostic.jcleancim.builder.ea.db;

import java.io.File;
import java.util.Map;

import org.tanjakostic.jcleancim.util.ApplicationException;

/**
 * DbRepo defines the interface for all types of EA Project files. 
 * <p>
 * <b>Limitation:</b> Note that with this implementation we don't have access to the EA repository
 * (API) methods, so we cannot export diagrams or XMI - although we do provide "empty" exporters, so
 * that this implementation can hook into the existing framework.
 * <p>
 * This implementation should be used for very fast {edit UML - validate} cycles. When you need to
 * produce a UML release (with XMI) and/or generate any kind of documentation with diagrams, ensure
 * you swap this implementation with the one that can export XMI and diagrams.
 *
 * @author todd.viegut@gmail.com
 * @version $Id: DbModelBuilder.java 21 2024-04-21 15:44:50Z dev978 $
 */
interface DbRepo {

	String getVersion();
	
	String getDbType();

	void open() throws ApplicationException;

	void close() throws ApplicationException;

	Iterable<Map<String, Object>> getTable(String tableName) throws ApplicationException;
	
    // Simple static factory method.
    static DbRepo create(String modelFileAbsPath) {
    	File file = new File(modelFileAbsPath);
		String ext = file.getName().substring(file.getName().lastIndexOf(".") + 1).toLowerCase();
		switch (ext) {
			case "eap":
			case "eapx":
				return new EapDbRepo(modelFileAbsPath);
			case "qea":
			case "qeax":
				return new QeaDbRepo(modelFileAbsPath);
			default:
				throw new IllegalArgumentException("Unsupported EA project file type: " + ext);
			}
    }

}