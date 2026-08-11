/**
 * Copyright (C) 2009-2019 Tatjana (Tanja) Kostic<br>
 * Copyright (C) 2022-2026 UCA International Users Group and contributors
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

import java.util.Arrays;
import java.util.HashSet;

/**
 * Utility methods.
 *
 * @author tviegut@ucaiug.org
 * @version $Id: JCleanCimUtils.java 31 2024-09-13 01:19:54Z dev978 $
 */
public final class JCleanCimUtils {
	
	private JCleanCimUtils() {};
	
	/** Note, we did not add .feap to the list of supported EA project types in jCleanCim as this type will not be supported. */
	private final static HashSet<String> _supportedModelTypes = new HashSet<>(Arrays.asList(new String[] {".eap", ".eapx", ".qea", "qeax"}));

	public static HashSet<String> getSupportedModelTypes() {
		return _supportedModelTypes;
	}
}
