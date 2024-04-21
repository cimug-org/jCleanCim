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
import java.io.IOException;

import org.apache.log4j.Logger;
import org.sparx.Project;
import org.sparx.Repository;
import org.tanjakostic.jcleancim.builder.AbstractDiagramExporter;
import org.tanjakostic.jcleancim.builder.UmlObjectBuilder;
import org.tanjakostic.jcleancim.common.Config;
import org.tanjakostic.jcleancim.util.Util;
import org.tanjakostic.jcleancim.util.Util.ImageFormat;

/**
 * @author tatjana.kostic@ieee.org
 * @version $Id: JapiDiagramExporter.java 21 2019-08-12 15:44:50Z dev978 $
 */
class JapiDiagramExporter extends AbstractDiagramExporter {
	private static final Logger _logger = Logger.getLogger(JapiDiagramExporter.class.getName());

	private final Repository _eaRep;
	private final Project _eaProj;

	/** Constructor. */
	public JapiDiagramExporter(Config cfg, Repository eaRep) {
		super(cfg);
		_eaRep = eaRep;
		_eaProj = _eaRep.GetProjectInterface();
	}

	@Override
	public File saveToFile(UmlObjectBuilder<?> dia, ImageFormat format, boolean throughClipboard)
			throws IOException {

		ImageFormat retainedFormat = (throughClipboard) ? ImageFormat.BMP : format;

		String uuid = dia.getObjData().getUuid();
		int id = dia.getObjData().getId().intValue();

		String fileName = String.format("eaDiag-%s", uuid) + retainedFormat.getExtensionWithDot();
		String dirPath = getCfg().getPicsDirAbsPath();

		long start = System.currentTimeMillis();
		try {
			boolean removeAfterExit = getCfg().isRemovePicsAfterExit();
			File pic = (removeAfterExit) ? Util.createTempImageFile(dirPath, fileName,
					retainedFormat, removeAfterExit) : new File(dirPath, fileName);
			if (throughClipboard) {
				_logger.info("    saving image from clipboard to '" + pic.getAbsolutePath() + ".");
				_eaProj.PutDiagramImageOnClipboard(uuid, 1);
				Util.saveImageFromClipboard(pic);
			} else {
				_logger.info("    saving exported image to '" + pic.getAbsolutePath() + ".");
				_eaProj.PutDiagramImageToFile(uuid, pic.getAbsolutePath(), 1);
			}
			return pic;
		} finally {
			_eaRep.CloseDiagram(id);

			long duration = System.currentTimeMillis() - start;
			_logger.debug(String.format("... saved in %s ms", Long.valueOf(duration)));
		}
	}
}
