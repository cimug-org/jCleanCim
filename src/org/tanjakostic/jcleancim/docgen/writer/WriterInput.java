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

package org.tanjakostic.jcleancim.docgen.writer;

import java.io.File;

import org.tanjakostic.jcleancim.common.Config;
import org.tanjakostic.jcleancim.util.Util;

/**
 * Group of parameters to construct any documentation writer.
 *
 * @author tatjana.kostic@ieee.org
 * @version $Id: WriterInput.java 21 2019-08-12 15:44:50Z dev978 $
 */
abstract public class WriterInput {

	private final String _appVersion;
	private final String _modelFileName;
	private final boolean _skipTiming;

	/** Constructor for testing only. */
	protected WriterInput(Config cfg, String appVersion, String modelFileName, boolean skipTiming) {
		if (cfg != null) {
			_appVersion = cfg.getAppVersion();
			_modelFileName = getNameFromModelPath(cfg.getModelFileAbsPath());
			_skipTiming = cfg.isAppSkipTiming();
		} else {
			_appVersion = Util.null2empty(appVersion);
			_modelFileName = Util.null2empty(modelFileName);
			_skipTiming = skipTiming;
		}
	}

	protected static String getNameFromModelPath(String modelFileAbsPath) {
		return modelFileAbsPath == null ? "" : new File(modelFileAbsPath).getName();
	}

	/**
	 * Returns name of the model file whose documentation is to be written; potentially empty
	 * string.
	 */
	public final String getModelFileName() {
		return _modelFileName;
	}

	/** Returns application version. */
	public final String getAppVersion() {
		return _appVersion;
	}

	/** Returns whether to skip logging ellapsed times. */
	public final boolean isSkipTiming() {
		return _skipTiming;
	}
}
