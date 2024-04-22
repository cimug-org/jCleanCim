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

/**
 * The EapDbRepo is the fastest builder of our in-memory model from EA. It is
 * based on the Jackcess open source library that allows reading MS Access file
 * in an OS-independent way, and independently of EA API.
 * <p>
 * <b>Limitation:</b> Note that with this implementation we don't have access to
 * the EA repository (API) methods, so we cannot export diagrams or XMI -
 * although we do provide "empty" exporters, so that this implementation can
 * hook into the existing framework.
 * <p>
 * This implementation should be used for very fast {edit UML - validate}
 * cycles. When you need to produce a UML release (with XMI) and/or generate any
 * kind of documentation with diagrams, ensure you swap this implementation with
 * the one that can export XMI and diagrams.
 *
 * @author todd.viegut@gmail.com
 * @version $Id: EapDbRepo.java 21 2024-04-21 15:44:50Z dev978 $
 */
abstract class AbstractDbRepo implements DbRepo {

	protected File file;

	public AbstractDbRepo(String modelFileAbsPath) {
		file = new File(modelFileAbsPath);
	}

	public String getDbType() {
		return file.getName().substring(file.getName().lastIndexOf(".") + 1).toUpperCase();
	}

}
