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

import java.io.File;

import org.apache.log4j.Logger;
import org.tanjakostic.jcleancim.common.Config;
import org.tanjakostic.jcleancim.common.XMIDialect;

/**
 * This one doesn't know how to export XMI; useful when working without EA repository.
 *
 * @author tatjana.kostic@ieee.org
 * @version $Id: EmptyXMIExporter.java 21 2019-08-12 15:44:50Z dev978 $
 */
public class EmptyXMIExporter extends AbstractXMIExporter {
	private static final Logger _logger = Logger.getLogger(EmptyXMIExporter.class.getName());

	public EmptyXMIExporter(Config cfg) {
		super(cfg);
	}

	/** This default implementation does nothing (well, that's why we are empty exporter). */
	@Override
	protected void toXmi(String rootUuid, XMIDialect dialect, boolean exportDiagrams, File file,
			String detail) {
		_logger.warn(String.format(
				"%s: Don't know how to export %s (with diagrams=%s) to '%s' - nothing done.",
				detail, dialect.toString(), Boolean.valueOf(exportDiagrams), file));
	}
}
