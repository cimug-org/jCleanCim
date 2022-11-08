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

import org.tanjakostic.jcleancim.util.Util;

/**
 * We should use only 4 multiplicities in EA: [1], [1..*], [0..1] and [0..*]. Use
 * {@link #parseBounds(String, String)} to obtain one of those standard ones, or whatever is defined
 * as custom. For validation purposes, use {@link #isCustom()} to identify custom ones that should
 * be fixed in the model.
 *
 * @author tatjana.kostic@ieee.org
 * @version $Id: UmlMultiplicity.java 21 2019-08-12 15:44:50Z dev978 $
 */
public class UmlMultiplicity {

	public static final UmlMultiplicity ONE = new UmlMultiplicity("1", "1", false);
	public static final UmlMultiplicity ONE_TO_MANY = new UmlMultiplicity("1", "*", false);
	public static final UmlMultiplicity OPT_ONE = new UmlMultiplicity("0", "1", false);
	public static final UmlMultiplicity OPT_MANY = new UmlMultiplicity("0", "*", false);
	public static final UmlMultiplicity EMPTY = new UmlMultiplicity("", "", true);

	/**
	 * Facilitates handling of IEC61850 class constraints (to ignore presence condition literals
	 * which must be printed in the documentation, but are not actually used as constraints, rather
	 * deduced from multiplicity of attribute).
	 *
	 * @author tatjana.kostic@ieee.org
	 * @version $Id: UmlMultiplicity.java 21 2019-08-12 15:44:50Z dev978 $
	 */
	public enum Kind {
		/** Mandatory. */
		M, /** Optional. */
		O;

		public static boolean isMember(String value) {
			try {
				return valueOf(value) != null;
			} catch (Exception e) {
				return false;
			}
		}
	}

	private final String _lower;
	private final String _upper;
	private final boolean _isCustom;

	private UmlMultiplicity(String lower, String upper, boolean isCustom) {
		_lower = lower;
		_upper = upper;
		_isCustom = isCustom;
	}

	public String getLower() {
		return _lower;
	}

	public String getUpper() {
		return _upper;
	}

	/**
	 * Returns whether this is a custom instance returned from {@link #parseBounds(String, String)}
	 * that should be fixed in the EA model.
	 */
	public boolean isCustom() {
		return _isCustom;
	}

	/** Returns true if the lower bound is 0 or empty. */
	public boolean isOptional() {
		return getLower().trim().equals("0") || getLower().trim().isEmpty();
	}

	/** Returns true if the upper bound is not empty and different than 1. */
	public boolean isMultivalue() {
		String upper = getUpper().trim();
		return !upper.equals("1") && !upper.isEmpty();
	}

	/**
	 * Returns multiplicity object from lower and upper bounds
	 */
	public static UmlMultiplicity parseBounds(String lower, String upper) {
		String lo = Util.null2empty(lower);
		String up = Util.null2empty(upper);
		if ("1".equals(lo)) {
			if ("".equals(up) || "1".equals(up)) {
				return ONE;
			} else if ("*".equals(up)) {
				return ONE_TO_MANY;
			}
		} else if ("0".equals(lo)) {
			if ("1".equals(up)) {
				return OPT_ONE;
			} else if ("*".equals(up)) {
				return OPT_MANY;
			}
		} else if (lo.isEmpty() && up.isEmpty()) {
			return EMPTY;
		}
		return new UmlMultiplicity(lo, up, true);
	}

	/**
	 * Returns multiplicity object from formatted string "lower..upper".
	 */
	public static UmlMultiplicity parseFromString(String mult) {
		if ("".equals(mult) || mult == null) {
			return EMPTY;
		}
		if ("1".equals(mult)) {
			return ONE;
		}
		if ("*".equals(mult)) {
			return OPT_MANY;
		}
		// now we have to parse...
		String[] tokens = mult.split("\\.+"); // + is for multiple period characters
		String lower = "";
		String upper = "";
		if (tokens.length == 2) {
			lower = tokens[0];
			upper = tokens[1];
		} else if (tokens.length == 1) {
			lower = tokens[0];
		}
		return parseBounds(lower, upper);
	}

	public String getBounds() {
		String lower = (getLower().isEmpty()) ? "?" : getLower();
		String upper = (this == ONE && getUpper().isEmpty()) ? ("")
				: (getUpper().isEmpty() ? "..?" : (".." + getUpper()));
		return lower + upper;
	}

	@Override
	public String toString() {
		return "[" + getBounds() + "]";
	}
}
