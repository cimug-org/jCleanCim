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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

import org.tanjakostic.jcleancim.util.Util;

/**
 * @author tatjana.kostic@ieee.org
 * @version $Id: NameDecomposition.java 21 2019-08-12 15:44:50Z dev978 $
 */
public class NameDecomposition {
	private static final String UNKNOWN_VALUE = "?";
	private static final char _REPLACEMENT_CHAR = ' ';

	private final String _inputName;
	private final List<Map<String, String>> _decomposedTerms = new ArrayList<>();
	private final boolean _matched;

	// ---------------------------- static helpers -----------------------

	/** Returns true if the description part in <code>termAndDesc</code> is not known. */
	public static boolean isUnknown(Map<String, String> termAndDesc) {
		return Util.getKeyByValue(termAndDesc, UNKNOWN_VALUE) != null;
	}

	/** Returns term with unknown description. */
	public static Map<String, String> createUnknownTerm(String term) {
		return Util.createKeyValuePair(term, UNKNOWN_VALUE);
	}

	/** Returns term with known description. */
	public static Map<String, String> createTerm(String term, String desc) {
		return Util.createKeyValuePair(term, desc);
	}

	// ---------------------------- instance -----------------------

	/**
	 * Constructor.
	 */
	public NameDecomposition(String inputName, Map<String, String> sortedAbbrTerms) {
		Util.ensureNotEmpty(inputName, "inputName");
		Util.ensureNotEmpty(sortedAbbrTerms, "sortedAbbrTerms");

		_inputName = inputName;
		_matched = matchesAbbreviatedTerms(sortedAbbrTerms);
	}

	/**
	 * Returns whether {@link #_inputName} is the concatenation of keys from
	 * <code>sortedTerms</code>, and collects the matched key/value pairs in
	 * {@link #_decomposedTerms}.
	 *
	 * @param sortedTerms
	 *            non-empty map of sorted terms to check against (terms are keys).
	 */
	private boolean matchesAbbreviatedTerms(Map<String, String> sortedTerms) {

		// collector for matched terms, sorted per their position in the input string:
		Map<Integer, Map<String, String>> collector = new TreeMap<Integer, Map<String, String>>();

		// the copy of the input string that we manipulate as we go
		String inputNameCopy = matchHappyPath(_inputName, sortedTerms, collector);

		boolean allTermsAndEndingNumbersMatched = inputNameCopy.trim().isEmpty();
		if (allTermsAndEndingNumbersMatched) {
			for (Entry<Integer, Map<String, String>> entry : collector.entrySet()) {
				_decomposedTerms.add(entry.getValue());
			}
			return true;
		}

		extractUnrecognisedTerms(inputNameCopy, collector);
		for (Entry<Integer, Map<String, String>> entry : collector.entrySet()) {
			_decomposedTerms.add(entry.getValue());
		}
		return false;
	}

	/**
	 * Returns string where the terms that have been matched are replaced with
	 * {@link #_REPLACEMENT_CHAR}, and the collector is filled with matched terms sorted per
	 * position in the <code>toCheck</code>.
	 */
	private static String matchHappyPath(String toCheck, Map<String, String> sortedTerms,
			Map<Integer, Map<String, String>> collector) {
		String result = toCheck;
		// System.out.println(">>>>> toCheck = " + toCheck + "; collector = " +
		// collector.toString());
		for (Entry<String, String> entry : sortedTerms.entrySet()) {
			String term = entry.getKey();
			int startIdx = result.indexOf(term);
			// System.out.println("      term=" + term + "; startIdx=" + startIdx);

			boolean matched = startIdx != -1;
			if (matched) {
				collector.put(Integer.valueOf(startIdx),
						Util.createKeyValuePair(term, entry.getValue()));
				result = result.replaceFirst(term,
						Util.fillString(term.length(), _REPLACEMENT_CHAR));
				// System.out.println("          collector=" + collector.toString());
				// System.out.println("          result=" + result.toString());
				if (!Util.hasContent(result)) {
					return "";
				}
			}
		}
		return result;
	}

	/**
	 * We parse the remaining, unrecognised characters and fill the collector; we don't use regex
	 * because we need the startIdx (for sorting according to the position in the original string).
	 */
	private static void extractUnrecognisedTerms(String toCheck,
			Map<Integer, Map<String, String>> collector) {
		for (int cursor = 0; cursor < toCheck.length(); ++cursor) {
			char c = toCheck.charAt(cursor);
			if (c == _REPLACEMENT_CHAR) {
				continue;
			}
			int startIdx = cursor;
			while (cursor < toCheck.length() && c != _REPLACEMENT_CHAR) {
				++cursor;
				if (cursor < toCheck.length()) {
					c = toCheck.charAt(cursor);
				}
			}
			String term = toCheck.substring(startIdx, cursor);
			collector.put(Integer.valueOf(startIdx), createUnknownTerm(term));
		}
	}

	public String getInputName() {
		return _inputName;
	}

	public List<Map<String, String>> getDecomposedTerms() {
		return Collections.unmodifiableList(_decomposedTerms);
	}

	public boolean isMatched() {
		return _matched;
	}

	@Override
	public String toString() {
		return _inputName + " = " + _decomposedTerms.toString();
	}
}
