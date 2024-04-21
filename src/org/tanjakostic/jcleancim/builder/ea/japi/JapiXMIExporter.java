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

package org.tanjakostic.jcleancim.builder.ea.japi;

import java.io.File;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.sparx.EnumXMIType;
import org.sparx.Project;
import org.sparx.Repository;
import org.tanjakostic.jcleancim.builder.EmptyXMIExporter;
import org.tanjakostic.jcleancim.common.Config;
import org.tanjakostic.jcleancim.common.XMIDialect;
import org.tanjakostic.jcleancim.util.Util;

/**
 * Implementation using EA XMI export capability.
 *
 * @author tatjana.kostic@ieee.org
 * @version $Id: JapiXMIExporter.java 21 2019-08-12 15:44:50Z dev978 $
 */
class JapiXMIExporter extends EmptyXMIExporter {
	private static final Logger _logger = Logger.getLogger(JapiXMIExporter.class.getName());

	private final Project _eaProj;

	/** Constructor. */
	public JapiXMIExporter(Config cfg, Repository eaRep) {
		super(cfg);
		Util.ensureNotNull(eaRep, "eaRep");

		_eaProj = eaRep.GetProjectInterface();
	}

	/**
	 * Here we first convert between EA-independent enum holding XMI dialects and the EA specific
	 * type, and then actually call EA exporter.
	 */
	@Override
	protected void toXmi(String rootUuid, XMIDialect dialect, boolean exportDiagrams, File file,
			String detail) {

		EnumXMIType xmiFormat = null;
		switch (dialect) {
			case ea_xmi11:
				xmiFormat = EnumXMIType.xmiEA11;
				break;
			case cimtool:
				xmiFormat = EnumXMIType.xmiRose12;
				break;
			case ea_xmi21:
			default:
				xmiFormat = EnumXMIType.xmiEA21;
				break;
		}
		int exportDiagramsInt = exportDiagrams ? 1 : 0;

		export(rootUuid, xmiFormat, exportDiagramsInt, file, detail);
	}

	private void export(String rootUuid, EnumXMIType xmiFormat, int exportDiagramsXmi, File file,
			String detail) {
		Util.logSubtitle(Level.INFO, "exporting EA model to XMI ...");
		_logger.info(detail + ": '" + file.getPath() + "'");

		long start = System.currentTimeMillis();

		int diagramImagePicFormat = -1;
		int formatXml = 1;
		int useDtd = 0;
		_eaProj.ExportPackageXMI(rootUuid, xmiFormat, exportDiagramsXmi, diagramImagePicFormat,
				formatXml, useDtd, file.getAbsolutePath());

		Util.logCompletion(Level.INFO, String.format("exported to %s.", xmiFormat.toString()),
				start, getCfg().isAppSkipTiming());
	}
}
