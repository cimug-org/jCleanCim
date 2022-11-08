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

package org.tanjakostic.jcleancim.model;

import org.apache.log4j.Logger;
import org.tanjakostic.jcleancim.common.Nature;
import org.tanjakostic.jcleancim.util.Util;

/**
 * Content of version class, expected to be found in top packages.
 *
 * @author tatjana.kostic@ieee.org
 * @version $Id: VersionInfo.java 21 2019-08-12 15:44:50Z dev978 $
 */
public class VersionInfo {
	private static final Logger _logger = Logger.getLogger(VersionInfo.class.getName());

	private final String _version;
	private final String _date;

	/** Returns the expected name for the version class, as per IEC TC57 UMl models rules. */
	public static String getExpectedVersionClassName(Nature nature, String name) {
		String suffix = (nature == Nature.CIM) ? UML.CIM_VERSION_CLASS_SUFFIX
				: UML.IEC61850_VERSION_CLASS_SUFFIX;
		return name + suffix;
	}

	/** Constructor. Logs error if the retained version class has unexpected attributes. */
	public VersionInfo(UmlClass versionClass) {
		Util.ensureNotNull(versionClass, "versionClass");
		String version = "";
		String date = "";
		for (UmlAttribute att : versionClass.getAttributes()) {
			String attName = att.getName();
			if (UML.ATTR_version.equals(attName) && version.isEmpty()) {
				version = att.getInitValue();
			} else if (UML.ATTR_date.equals(attName) && date.isEmpty()) {
				date = att.getInitValue();
			} else {
				_logger.error("Unexpected attribute '" + attName + "' in version class '"
						+ versionClass.getName() + "'.");
			}
		}
		_version = version;
		_date = date;
	}

	public VersionInfo(String version, String date) {
		_version = version;
		_date = date;
	}

	public String getVersion() {
		return _version;
	}

	public String getDate() {
		return _date;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("version={").append(_version).append(", ").append(_date).append("}");
		return sb.toString();
	}
}
