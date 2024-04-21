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

import java.util.List;

import org.apache.log4j.Logger;
import org.tanjakostic.jcleancim.util.Util;

/**
 * WG10 CDC and DA attributes specify sometimes allowed ranges in the initial value.
 *
 * @author tatjana.kostic@ieee.org
 * @version $Id: ValueRange.java 21 2019-08-12 15:44:50Z dev978 $
 */
public class ValueRange {
	private static final Logger _logger = Logger.getLogger(ValueRange.class.getName());
	public static final String RANGE_TOKEN = "...";
	private final String _min;
	private final String _max;

	/**
	 * Returns whether the intial value in UML repository has required format (i.e., whether it
	 * includes '{@value #RANGE_TOKEN}' with at least one of min and max).
	 */
	public static boolean isValidRangeFormat(String initialValue) {
		List<String> tokens = Util.splitStringSeparatedTokens(initialValue.trim(), RANGE_TOKEN);
		if (tokens.size() < 1) {
			_logger.trace("'" + initialValue + "' has no values.");
			return false;
		}
		if (tokens.size() > 2) {
			_logger.trace("'" + initialValue + "' has more than two values.");
			return false;
		}
		if (!initialValue.contains(RANGE_TOKEN)) {
			_logger.trace("'" + initialValue + "' has no separator indicating range.");
			return false;
		}
		return true;
	}

	/** Constructor; at least one of two arguments must be non-null, non-empty. */
	public ValueRange(String min, String max) {
		if ((min == null || min.trim().isEmpty()) && (max == null || max.trim().isEmpty())) {
			throw new IllegalArgumentException("At least one of min (" + min + ") or max (" + max
					+ ") must be non-null and non-empty.");
		}
		_min = min;
		_max = max;
	}

	public ValueRange(String initialValue) {
		if (!isValidRangeFormat(initialValue)) {
			throw new IllegalArgumentException("Invalid format :'" + initialValue + "'.");
		}

		String inVal = initialValue.trim();
		String min = null;
		String max = null;
		List<String> tokens = Util.splitStringSeparatedTokens(inVal, RANGE_TOKEN);
		if (inVal.startsWith(RANGE_TOKEN)) {
			max = tokens.get(0);
		} else if (inVal.endsWith(RANGE_TOKEN)) {
			min = tokens.get(0);
		} else {
			min = tokens.get(0);
			max = tokens.get(1);
		}

		_min = min;
		_max = max;
	}

	/** Returns (potentially null) minimum value. */
	public String min() {
		return _min;
	}

	/** Returns (potentially null) maximum value. */
	public String max() {
		return _max;
	}

	@Override
	public String toString() {
		return Util.null2empty(min()) + RANGE_TOKEN + Util.null2empty(max());
	}
}
