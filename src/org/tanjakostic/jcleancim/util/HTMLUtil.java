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

package org.tanjakostic.jcleancim.util;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * @author tatjana.kostic@ieee.org
 * @version $Id: HTMLUtil.java 21 2019-08-12 15:44:50Z dev978 $
 */
public class HTMLUtil {

	private HTMLUtil() {
		// prevents construction
	}

	/**
	 * Format string, to enclose the content of the body element (HTML snippet) into a valid HTML
	 * document.
	 */
	public static final String HTML_DOC_FMT = "<!DOCTYPE HTML PUBLIC \"-//W3C//DTD HTML 4.0 Transitional//EN\">"
			+ "<html><head></head><body>%s</body></html>";

	public static final String UL_START = "<ul>";
	public static final String UL_END = "</ul>";
	public static final String OL_START = "<ol>";
	public static final String OL_END = "</ol>";
	public static final String LI_START = "<li>";
	public static final String LI_END = "</li>";
	public static final Set<String> LIST_TAGS = new HashSet<String>(Arrays.asList(UL_START, UL_END,
			OL_START, OL_END, LI_START, LI_END));

	public static final String B_START = "<b>";
	public static final String B_END = "</b>";
	public static final String I_START = "<i>";
	public static final String I_END = "</i>";
	public static final String U_START = "<u>";
	public static final String U_END = "</u>";
	public static final String FONT_COLOUR_START = "<font"; // contains attribute with value...
	public static final String FONT_COLOUR_END = "</font>";

	public static final String SUP_START = "<sup>";
	public static final String SUP_END = "</sup>";
	public static final String SUB_START = "<sub>";
	public static final String SUB_END = "</sub>";

	public static final String P_START = "<p>";
	public static final String P_END = "</p>";
	public static final String NBSP = "&nbsp;";
}
