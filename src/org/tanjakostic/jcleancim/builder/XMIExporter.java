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

package org.tanjakostic.jcleancim.builder;

import org.tanjakostic.jcleancim.common.Config;
import org.tanjakostic.jcleancim.util.ApplicationException;

/**
 * Interface to implement for XMI export capability.
 *
 * @author tatjana.kostic@ieee.org
 * @version $Id: XMIExporter.java 21 2019-08-12 15:44:50Z dev978 $
 */
public interface XMIExporter {

	/** Returns the configuration, containing also XMI export options. */
	public Config getCfg();

	/** Actually performs export for all the configured dialects. */
	public void exportToXMIs(String rootUuid) throws ApplicationException;
}
