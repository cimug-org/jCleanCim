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

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.tanjakostic.jcleancim.util.ApplicationException;
import org.tanjakostic.jcleancim.util.Util;

/**
 * Maps document in-built styles as replacement for our desired styles (in IEC template), to allow
 * for doc generation even with a non-IEC template.
 *
 * @author tatjana.kostic@ieee.org
 * @version $Id: Style.java 34 2019-12-20 18:37:17Z dev978 $
 */
public enum Style {
	// custom:
	para,
	fig,
	tabhead,
	tabcell,
	figcapt,
	tabcapt,
	// built-ins:
	toc1,
	toc2,
	toc3,
	toc4,
	toc5,
	toc6,
	toc7,
	toc8,
	toc9,
	h1,
	h2,
	h3,
	h4,
	h5,
	h6,
	h7,
	h8,
	h9;

	private static final Logger _logger = Logger.getLogger(Style.class.getName());

	/** Detailed logging for Style initialisation. Turn to INFO for debugging only. */
	public static Level LLEVEL = Level.TRACE;

	public static Style getTOCStyle(int outlineLevel) {
		switch (outlineLevel) {
			case 1:
				return toc1;
			case 2:
				return toc2;
			case 3:
				return toc3;
			case 4:
				return toc4;
			case 5:
				return toc5;
			case 6:
				return toc6;
			case 7:
				return toc7;
			case 8:
				return toc8;
			case 9:
				return toc9;
			default:
				return null;
		}
	}

	public static Style getHeadingStyle(int outlineLevel) {
		switch (outlineLevel) {
			case 1:
				return h1;
			case 2:
				return h2;
			case 3:
				return h3;
			case 4:
				return h4;
			case 5:
				return h5;
			case 6:
				return h6;
			case 7:
				return h7;
			case 8:
				return h8;
			case 9:
				return h9;
			default:
				return null;
		}
	}

	/** From configuration, in order of preferrence (first is best); we know the kind. */
	private final List<String> _preferredNames;

	/** From open document; we deduce the kind (from built-in and custom). Text is the key. */
	private final Map<String, ExistingStyle> _usableStyles;

	/** Configured, usable plus default name; cached for quick look-up. */
	private final Set<String> _allNames;

	/** The one retained to be used on write in a document. */
	private ExistingStyle _style; // deduced from above two collections/maps

	Style() {
		_preferredNames = new ArrayList<String>();
		_usableStyles = new LinkedHashMap<String, ExistingStyle>();
		_allNames = new HashSet<String>();
	}

	// ------------------ lifecycle static methods ------------------------

	/** Adds style names from user configuration in order of preferrence (first is best). */
	public static void initPreferred(List<String> tocStylePrefixes,
			List<String> headingStylePrefixes, List<String> paraStyles, List<String> figStyles,
			List<String> tabheadStyles, List<String> tabcellStyles, List<String> figcaptStyles,
			List<String> tabcaptStyles) {
		Style.para.addPreferredStyles(paraStyles, null);
		Style.fig.addPreferredStyles(figStyles, null);
		Style.tabhead.addPreferredStyles(tabheadStyles, null);
		Style.tabcell.addPreferredStyles(tabcellStyles, null);
		Style.figcapt.addPreferredStyles(figcaptStyles, null);
		Style.tabcapt.addPreferredStyles(tabcaptStyles, null);
		Style.toc1.addPreferredStyles(tocStylePrefixes, Integer.valueOf(1));
		Style.toc2.addPreferredStyles(tocStylePrefixes, Integer.valueOf(2));
		Style.toc3.addPreferredStyles(tocStylePrefixes, Integer.valueOf(3));
		Style.toc4.addPreferredStyles(tocStylePrefixes, Integer.valueOf(4));
		Style.toc5.addPreferredStyles(tocStylePrefixes, Integer.valueOf(5));
		Style.toc6.addPreferredStyles(tocStylePrefixes, Integer.valueOf(6));
		Style.toc7.addPreferredStyles(tocStylePrefixes, Integer.valueOf(7));
		Style.toc8.addPreferredStyles(tocStylePrefixes, Integer.valueOf(8));
		Style.toc9.addPreferredStyles(tocStylePrefixes, Integer.valueOf(9));
		Style.h1.addPreferredStyles(headingStylePrefixes, Integer.valueOf(1));
		Style.h2.addPreferredStyles(headingStylePrefixes, Integer.valueOf(2));
		Style.h3.addPreferredStyles(headingStylePrefixes, Integer.valueOf(3));
		Style.h4.addPreferredStyles(headingStylePrefixes, Integer.valueOf(4));
		Style.h5.addPreferredStyles(headingStylePrefixes, Integer.valueOf(5));
		Style.h6.addPreferredStyles(headingStylePrefixes, Integer.valueOf(6));
		Style.h7.addPreferredStyles(headingStylePrefixes, Integer.valueOf(7));
		Style.h8.addPreferredStyles(headingStylePrefixes, Integer.valueOf(8));
		Style.h9.addPreferredStyles(headingStylePrefixes, Integer.valueOf(9));
	}

	/** Trims and adds non-empty, non-duplicate names to consider as style names on read. */
	private void addPreferredStyles(List<String> preferredStyles, Integer outlineLevel) {
		Util.ensureContainsNoNull(preferredStyles, "preferredStyles");
		_logger.log(LLEVEL, "=== before " + this.name() + ".addPreferredStyles(" + preferredStyles
				+ "): " + Util.NL + this.toString());

		String outlineEnding = (outlineLevel == null) ? "" : (" " + outlineLevel.intValue());
		for (String name : preferredStyles) {
			name = name.trim();
			if (!name.isEmpty() && !_preferredNames.contains(name)) {
				name += outlineEnding;
				_preferredNames.add(name);
				_allNames.add(name);
			}
		}
		logAfter();
	}

	/**
	 * Initialises styles obtained from the document. Ensure to call this after you open the
	 * document to avoid exceptions on write to the document.
	 *
	 * @param existingStyles
	 * @throws ApplicationException
	 *             if cannot find a matching (at least built-in) style in the open document
	 */
	public static void initUsable(Map<String, ExistingStyle> existingStyles)
			throws ApplicationException {
		Util.ensureContainsNoNull(existingStyles.values(), "existingStyles");

		for (Style s : values()) {
			s.assignTheStyleFromExistingAccordingToPreferred(existingStyles);
		}
	}

	private void assignTheStyleFromExistingAccordingToPreferred(
			Map<String, ExistingStyle> existingStyles) throws ApplicationException {
		_logger.log(LLEVEL,
				"=== before " + this.name() + ".assignTheStyleFromExistingAccordingToPreferred("
						+ existingStyles.keySet() + "): " + Util.NL + this.toString());
		List<String> preferredNames = getPreferredNames();
		Set<ExistingStyle> existingSelection = new LinkedHashSet<>(existingStyles.values());

		// here we loop to collect all and intitialise with the first found (preference)
		ExistingStyle found = null;
		for (String name : preferredNames) {
			found = existingStyles.get(name);
			if (found != null) {
				existingSelection.remove(found);
				_allNames.add(name);
				_usableStyles.put(name, found);
				if (_style == null) { // if not yet initialised:
					_style = found; // the first from preferred wins
					_style.setCustomKindFrom(this);

					_logger.info(
							"Assigned from preferred names [full match]: " + this.toShortString());
				}
			}
		}

		if (_style != null) {
			logAfter();
			return;
		}

		// here we pick the first match, if any, and break:
		for (String name : preferredNames) {
			for (ExistingStyle es : existingSelection) {
				if (es.name.startsWith(name)) {
					existingSelection.remove(es);
					_allNames.add(name);
					_usableStyles.put(name, es);
					if (_style == null) { // if not yet initialised:
						_style = es; // the first from preferred wins
						_style.setCustomKindFrom(this);

						_logger.info("Assigned from preferred names [matching start with"
								+ " preferred] :" + this.toString());
					}
					break;
				}
			}
			if (_style != null) {
				_logger.info("        NOTE: If not happy with this assignment, add custom style(s)"
						+ " to your document manually.");
				logAfter();
				return;
			}
		}

		// assign defaults from remaining built-in existing styles:
		for (ExistingStyle es : existingSelection) {
			if (es.isBuiltIn && es.isUsableFor(this)) {
				_allNames.add(es.name);
				_usableStyles.put(es.name, es);
				if (_style == null) { // if still not initialised:
					_style = es; // the first from remaining existing wins
					_logger.warn("Assigned style [from built-in]:" + this.toShortString());
				}
			}
		}

		if (_style == null) {
			String message = "There are no usable " + name() + " built-in styles !"
					+ " Document may be corrupt.";
			_logger.error(message);
			logAfter();
			throw new ApplicationException(message);
		}
		logAfter();
	}

	public void logAfter() {
		_logger.log(LLEVEL, "=== after" + Util.NL + this.toString() + Util.NL);
	}

	public static boolean isTOC(String styleName) {
		for (int i = 0; i < 9; ++i) {
			int outlineLevel = i + 1;
			Style tocStyle = getTOCStyle(outlineLevel);
			if (tocStyle.isRecognised(styleName)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Resets all dynamically set values and collections; ensure to call as a part of clean-up
	 * process when existing the app.
	 * <p>
	 * Implementation note: Because this is an enumeration, instances are static and when running
	 * tests, for example, the collections do NOT get cleaned up. If changing implementation, to not
	 * use enumeration, then this would not be needed.
	 */
	public static void reset() {
		for (Style s : values()) {
			s._preferredNames.clear();
			s._usableStyles.clear();
			s._allNames.clear();
			s._style = null;
		}
	}

	// ------------------ public API --------------------------

	/**
	 * Returns non-empty list of style names, configured by user for this style. Values are only
	 * indicative for writing: see {@link #getUsableStyles(boolean, boolean)}.
	 */
	public List<String> getPreferredNames() {
		return Collections.unmodifiableList(_preferredNames);
	}

	/** Returns all usable styles found in the open document. */
	public Map<String, ExistingStyle> getUsableStyles() {
		return Collections.unmodifiableMap(_usableStyles);
	}

	/**
	 * Returns map of existing styles as found in an open document, which can be used for writing
	 * into document.
	 *
	 * @param inclBuiltIn
	 *            if <code>true</code>, collection includes built-in styles
	 * @param inclCustom
	 *            if <code>true</code>, collection includes custom styles
	 */
	public Map<String, ExistingStyle> getUsableStyles(boolean inclBuiltIn, boolean inclCustom) {
		if (inclBuiltIn && inclCustom) {
			return getUsableStyles();
		}

		Map<String, ExistingStyle> result = new LinkedHashMap<>();
		for (ExistingStyle s : _usableStyles.values()) {
			if (s.isBuiltIn && inclBuiltIn) {
				result.put(s.name, s);
			}
			if (!s.isBuiltIn && inclCustom) {
				result.put(s.name, s);
			}
		}
		return result;
	}

	/** Returns <em>the</em> style name to be used for this style. */
	public String getName() {
		return (_style != null) ? _style.name : "null";
	}

	/** Returns whether <code>name</code> can be identified as a style name. */
	public boolean isRecognised(String name) {
		return _allNames.contains(name);
	}

	/** Returns whether this is a TOC style (one out of 9). */
	public boolean isTOC() {
		for (int i = 0; i < 9; ++i) {
			int outlineLevel = i + 1;
			if (this == getTOCStyle(outlineLevel)) {
				return true;
			}
		}
		return false;
	}

	/** Returns whether this is a heading style (one out of 9). */
	public boolean isHeading() {
		for (int i = 0; i < 9; ++i) {
			int outlineLevel = i + 1;
			if (this == getHeadingStyle(outlineLevel)) {
				return true;
			}
		}
		return false;
	}

	public StringBuilder toShortString() {
		StringBuilder sb = new StringBuilder();
		sb.append("Style.").append(name());
		sb.append("=").append(getName());
		sb.append("; cfg-preferred names=").append(getPreferredNames());
		return sb;
	}

	@Override
	public String toString() {
		StringBuilder sb = toShortString();
		sb.append("; doc-usable styles=").append(_usableStyles);
		sb.append("; all recognised names=").append(_allNames);
		return sb.toString();
	}
}
