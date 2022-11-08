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
import java.util.EnumSet;

import org.apache.log4j.Logger;
import org.tanjakostic.jcleancim.common.Config;
import org.tanjakostic.jcleancim.common.XMIDialect;
import org.tanjakostic.jcleancim.util.ApplicationException;
import org.tanjakostic.jcleancim.util.Util;

/**
 * Here we implement all the logic except for actual exporting.
 *
 * @author tatjana.kostic@ieee.org
 * @version $Id: AbstractXMIExporter.java 21 2019-08-12 15:44:50Z dev978 $
 */
abstract public class AbstractXMIExporter implements XMIExporter {
	private static final Logger _logger = Logger.getLogger(AbstractXMIExporter.class.getName());

	private final Config _cfg;

	/**
	 * Constructor.
	 */
	protected AbstractXMIExporter(Config cfg) {
		Util.ensureNotNull(cfg, "cfg");
		_cfg = cfg;
	}

	@Override
	public final Config getCfg() {
		return _cfg;
	}

	@Override
	public final void exportToXMIs(String rootUuid) throws ApplicationException {
		Util.ensureNotNull(rootUuid, "rootUuid");

		String modelFileAbsPath = this.getCfg().getModelFileAbsPath();
		if (modelFileAbsPath == null) {
			_logger.warn("You've enabled export to XMI, but there is no .eap file "
					+ "- skipping XMI export.");
			return;
		}
		String eaModelFileName = new File(modelFileAbsPath).getName();
		if (!eaModelFileName.endsWith(".eap")) {
			_logger.warn("Expecting an .eap model file, and don't know how to export XMI from "
					+ eaModelFileName + " - skipping XMI export.");
			return;
		}

		// remove the extension ".eap"
		String outFileName = eaModelFileName.substring(0, eaModelFileName.length() - 4);

		EnumSet<XMIDialect> xmiexportDialects = getCfg().getXmiexportDialects();
		if (xmiexportDialects.contains(XMIDialect.ea_xmi11)) {
			File file = createOutputFileWithBackup(outFileName + XMIDialect.ea_xmi11.getAsSuffix());
			toXmi(rootUuid, XMIDialect.ea_xmi11, true, file, "XMI 1.1");
		}
		if (xmiexportDialects.contains(XMIDialect.ea_xmi21)) {
			File file = createOutputFileWithBackup(outFileName + XMIDialect.ea_xmi21.getAsSuffix());
			toXmi(rootUuid, XMIDialect.ea_xmi21, true, file, "XMI 2.1");
		}
		if (xmiexportDialects.contains(XMIDialect.cimtool)) {
			File file = createOutputFileWithBackup(outFileName + XMIDialect.cimtool.getAsSuffix());
			toXmi(rootUuid, XMIDialect.cimtool, false, file, "XMI 1.4 (CIMTool)");
		}
	}

	private static File createOutputFileWithBackup(String fileName) throws ApplicationException {
		return Util.getOutputFileRenameIfExists(Config.OUTPUT_DIR_NAME, fileName);
	}

	/**
	 * Actual export to XMI.
	 *
	 * @param rootUuid
	 * @param dialect
	 * @param exportDiagrams
	 * @param file
	 * @param detail
	 * @throws ApplicationException
	 */
	abstract protected void toXmi(String rootUuid, XMIDialect dialect, boolean exportDiagrams,
			File file, String detail) throws ApplicationException;
}
