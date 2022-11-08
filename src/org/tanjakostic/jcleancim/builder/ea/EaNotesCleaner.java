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

package org.tanjakostic.jcleancim.builder.ea;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.tanjakostic.jcleancim.util.HTMLUtil;
import org.tanjakostic.jcleancim.util.Util;

/**
 * Helper class supporting a limitted number of HTML tags, i.e. only those that get returned by
 * various EA getNotes() methods. This implementation is much faster than the call to the EA API
 * mehods GetFormatFromField(TXT) and GetFormatFromField(HTML).
 *
 * @author tatjana.kostic@ieee.org
 * @version $Id: EaNotesCleaner.java 21 2019-08-12 15:44:50Z dev978 $
 */
class EaNotesCleaner {
	private static final Logger _logger = Logger.getLogger(EaNotesCleaner.class.getName());

	/**
	 * Constructor.
	 */
	public EaNotesCleaner() {
		// prevents default ctor
	}

	// ------------------------------ EA markup to valid HTML snippet --------------------------

	/**
	 * Fixes text in <code>eaGetNotesText</code> to produce valid HTML snippet, as follows:
	 * <ul>
	 * <li>A paragraph (text line) having list markup are not modified at all.</li>
	 * <li>A paragraph (text line) that does not start with any markup at all is enclosed between
	 * {@value org.tanjakostic.jcleancim.util.HTMLUtil#P_START} and
	 * {@value org.tanjakostic.jcleancim.util.HTMLUtil#P_END} tags. If its original content has only
	 * space characters, the content gets replaced with
	 * {@value org.tanjakostic.jcleancim.util.HTMLUtil#NBSP}.</li>
	 * </ul>
	 * For null or empty <code>eaGetNotesText</code>, returns empty string.
	 */
	public String cleanHtml(String eaGetNotesText) {
		return cleanHtml(eaGetNotesText, false);
	}

	/**
	 * Use this method instead of EA GetFormatFromField(HTML).
	 *
	 * @param eaGetNotesText
	 *            text returned by EA GetNotes() method (and variations).
	 */
	public String cleanAndCompactHtml(String eaGetNotesText) {
		return cleanHtml(eaGetNotesText, true);
	}

	private String cleanHtml(String eaGetNotesText, boolean removeEmptyParas) {
		if (eaGetNotesText == null || eaGetNotesText.isEmpty()) {
			return "";
		}

		StringBuilder sb = new StringBuilder();
		BufferedReader buf = new BufferedReader(new StringReader(eaGetNotesText));
		try {
			String line;
			while ((line = buf.readLine()) != null) {
				String processedLine = !removeEmptyParas ? processParaHtml(line)
						: processParaAndCompactHtml(line);
				sb.append(processedLine);
				if (!removeEmptyParas) {
					sb.append(Util.NL); // needs to be removed before return for very last line
				}
			}
		} catch (IOException e) {
			_logger.warn(e.getMessage());
		}

		String result = sb.toString();
		if (!removeEmptyParas) {
			int idx = result.lastIndexOf(Util.NL);
			result = result.substring(0, idx);
		}

		return result;
	}

	private String processParaHtml(String line) {
		String processedLine = line.trim();
		if (isListRelatedHtml(processedLine)) {
			return line;
		}
		return HTMLUtil.P_START + (processedLine.isEmpty() ? HTMLUtil.NBSP : line) + HTMLUtil.P_END;
	}

	private String processParaAndCompactHtml(String line) {
		String processedLine = line.trim();
		if (processedLine.isEmpty()) {
			return "";
		}
		if (isListRelatedHtml(processedLine)) {
			return processedLine;
		}
		return HTMLUtil.P_START + processedLine + HTMLUtil.P_END;
	}

	private boolean isListRelatedHtml(String trimmedLine) {
		for (String listTag : HTMLUtil.LIST_TAGS) {
			if (trimmedLine.startsWith(listTag)) {
				return true;
			}
		}
		return false;
	}

	// ------------------------------ EA markup to raw text ------------------------------

	/**
	 * Use this method instead of EA GetFormatFromField(TXT).
	 *
	 * @param eaGetNotesText
	 *            text returned by EA GetNotes() method (and variations).
	 */
	public List<String> cleanAndCompactText(String eaGetNotesText) {
		if (eaGetNotesText == null || eaGetNotesText.isEmpty()) {
			return Collections.emptyList();
		}

		List<String> eaLines = Util.splitLines(eaGetNotesText, true);

		List<String> rawLines = new ArrayList<String>(eaLines.size());
		for (int i = 0; i < eaLines.size(); ++i) {
			String trimmedLine = eaLines.get(i).trim();
			if (trimmedLine.isEmpty() || trimmedLine.startsWith(HTMLUtil.UL_END)
					|| trimmedLine.startsWith(HTMLUtil.OL_END)) {
				continue;
			}

			if (trimmedLine.startsWith(HTMLUtil.OL_START)) {
				i = cleanListText(true, i, eaLines, rawLines);
			} else if (trimmedLine.startsWith(HTMLUtil.UL_START)) {
				i = cleanListText(false, i, eaLines, rawLines);
			} else {
				rawLines.add(removeHtml(trimmedLine));
			}
		}
		return rawLines;
	}

	/**
	 * (package private for testing) Receives <code>eaLines</code> and index
	 * <code>listStartTagIdx</code> and continues from the next index in <code>eaLines</code>, until
	 * it finds the list ending tag. Returns the advanced value for <code>listStartTagIdx</code>, at
	 * the last list item; the caller needs to skip the list end tag. Formats the content found
	 * between list item tags, depending whether list is <code>numbered</code> or not. In case the
	 * extracted list item contains non-breaking space, preserves the list item with the empty
	 * content. The result is added to <code>outputRawLines</code>.
	 */
	int cleanListText(boolean numbered, int listStartTagIdx, List<String> eaLines,
			List<String> outputRawLines) {
		int k = listStartTagIdx + 1;
		for (int li = 1; k < eaLines.size(); ++k, ++li) {
			String line = eaLines.get(k).trim();
			if (!line.startsWith(HTMLUtil.LI_START)) {
				break;
			}
			String extracted = StringUtils.substringBetween(line, HTMLUtil.LI_START,
					HTMLUtil.LI_END);
			String bullet = (numbered) ? li + "." : "-";
			String oListItem = bullet;
			if (extracted != null && !extracted.isEmpty() && !HTMLUtil.NBSP.equals(extracted)) {
				oListItem += " " + extracted;
			}
			outputRawLines.add(removeHtml(oListItem));
		}
		return --k;
	}

	private static final Pattern REMOVE_TAGS = Pattern.compile("\\<.*?\\>"/* "<.+?>" */);

	private String removeHtml(String trimmedLine) {
		Matcher m = REMOVE_TAGS.matcher(trimmedLine);
		String removedTags = m.replaceAll("");
		return StringEscapeUtils.unescapeHtml(removedTags);
	}
}
