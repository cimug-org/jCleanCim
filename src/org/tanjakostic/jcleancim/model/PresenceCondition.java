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
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.log4j.Logger;
import org.tanjakostic.jcleancim.util.Util;

/**
 * @author tatjana.kostic@ieee.org
 * @version $Id: PresenceCondition.java 25 2019-11-02 17:21:28Z dev978 $
 */
public class PresenceCondition {
	private static final Logger _logger = Logger.getLogger(PresenceCondition.class.getName());

	/** Stem end for presence conditions with 'condID' argument. */
	public static final String STEM_END_COND = "cond";
	public static final String ARG_CONDID = "(condID)";
	public static final String ARG_N = "(n)";
	public static final String ARG_SIBLING = "(sibling)";

	// TODO maybe cache the created ones and reuse (as we do for M, O, F and NA?
	// private static Map<String, PresenceCondition> _PCS = new LinkedHashMap<String,
	// PresenceCondition>();

	public static final PresenceCondition M = new PresenceCondition(
			UmlMultiplicity.Kind.M.toString());
	public static final PresenceCondition O = new PresenceCondition(
			UmlMultiplicity.Kind.O.toString());
	public static final PresenceCondition NA = new PresenceCondition(UML.PC_na);
	public static final PresenceCondition F = new PresenceCondition(UML.PC_F);

	private static Set<PresenceCondition> IMPLICITS = Collections
			.unmodifiableSet(new LinkedHashSet<>(Arrays.asList(M, O, NA)));

	public static Set<String> getNamesOfImplicits() {
		Set<String> result = new LinkedHashSet<>();
		for (PresenceCondition pc : IMPLICITS) {
			result.add(pc.getStem());
		}
		return result;
	}

	// static {
	// _PCS.put(key, value)
	// }

	private final UmlConstraint _constr;
	private final UmlAttribute _pcDefinitionLiteral;
	private final String _stem;
	private final String _args;
	private final String _text;

	// ---------------------

	static PresenceCondition create(UmlConstraint constr) {
		return new PresenceCondition(constr);
	}

	private PresenceCondition(String mo) {
		_constr = null;
		_pcDefinitionLiteral = null;
		_stem = mo;
		_args = "";
		_text = "";
	}

	// FIXME: Do this more elegantly...
	private PresenceCondition(UmlConstraint constr) {
		Util.ensureNotNull(constr, "constr");

		_constr = constr;
		_text = Util.null2empty(constr.getCondition());

		Map<String, UmlAttribute> presConditions = constr.getContainingClass().getModel()
				.findPresenceConditionLiterals();
		UmlAttribute pcLiteral = presConditions.get(constr.getName());
		boolean constraintSeemsLikeHavingArgument = seemsLikeHavingArgument(constr.getName());
		if (pcLiteral != null && !constraintSeemsLikeHavingArgument) {
			// this is case where the constraint name matches exactly literal from PresenceCondition
			// class and there are no arguments:
			_pcDefinitionLiteral = pcLiteral;
			_stem = pcLiteral.getName();
			_args = "";
			return;
		}

		// we didn't find exact match without arguments, so we try to match something with
		// arguments:
		if (constraintSeemsLikeHavingArgument) {
			// this constraint/presence condition:
			int openIdx = constr.getName().indexOf("(");
			String stem = constr.getName().substring(0, openIdx);
			String args = constr.getName().substring(openIdx + 1, constr.getName().length() - 1);

			if (pcLiteral != null) {
				// case of wrong usage in the model if exact match with arguments is found,
				// e.g. AtLeastOne(n) instead of AtLeastOne(1)
				_pcDefinitionLiteral = pcLiteral;
				_stem = stem;
				_args = args;
				return;
			}

			// all presence condition literals, to match this stem against:
			List<String> withArgs = new ArrayList<String>();
			for (Entry<String, UmlAttribute> entry : presConditions.entrySet()) {
				String pcLiteralName = entry.getKey();
				if (seemsLikeHavingArgument(pcLiteralName) && pcLiteralName.startsWith(stem)) {
					withArgs.add(pcLiteralName);
				}
			}
			if (!withArgs.isEmpty()) {
				_stem = stem;
				_args = args;

				String pcName = findPcName(_args, withArgs);
				boolean foundWithSibling = pcName != null;
				if (foundWithSibling) {
					_pcDefinitionLiteral = presConditions.get(pcName);
				} else {
					_pcDefinitionLiteral = presConditions.get(withArgs.get(0));
					if (withArgs.size() > 1) {
						_logger.warn("found more than one possibility for arguments ("
								+ withArgs.toString() + ") in " + constr.toShortString(false, true)
								+ " - keeping first");
					}
				}
				return;
			}
		}

		// this is case where we don't seem to match either stem or full name of constraint/presence
		// condition with anything from PresenceCondition class:
		_logger.warn("presence condition literal not found, or invalid class constraint name format"
				+ " (should be 'name(arguments)'): " + constr.toShortString(false, true));
		_pcDefinitionLiteral = null;
		_stem = constr.getName();
		_args = "";
	}

	private String findPcName(String arg, List<String> withArgs) {
		String pcName = null;
		for (int i = 0; i < withArgs.size(); ++i) {
			String name = withArgs.get(i);
			if (name.endsWith(ARG_SIBLING)) {
				if (!isInteger(arg)) {
					pcName = name;
					break;
				}
				withArgs.remove(i);
				--i;
			}
		}
		return pcName;
	}

	private static boolean seemsLikeHavingArgument(String token) {
		return token.contains("(") && token.endsWith(")");
	}

	// FIXME: quick & dirty !
	private boolean isInteger(String text) {
		if (text.contains(",")) { // min, max
			return true;
		}
		try {
			Integer.parseInt(text); // n, condId
			return true;
		} catch (NumberFormatException e) {
			return false; // sibling
		}
	}

	// --------------------- API ----------------

	/** Returns potentially null constraint from which this presence condition has been created. */
	public UmlConstraint getConstraint() {
		return _constr;
	}

	/**
	 * Returns (potentially null) UML literal defining this presence condition; it is null in case
	 * there is an error in the model and the presence condition found in the class is not a
	 * standard one.
	 */
	public UmlAttribute getDefinitionLiteral() {
		return _pcDefinitionLiteral;
	}

	public String getStem() {
		return _stem;
	}

	public String getArgs() {
		return _args;
	}

	public String getText() {
		return _text;
	}

	/** Returns <code>stem(args)</code> if there are arguments, otherwise just <code>stem</code>. */
	public String getStemAndArgs() {
		StringBuilder sb = new StringBuilder();
		sb.append(_stem);
		if (!getArgs().isEmpty()) {
			sb.append("(").append(_args).append(")");
		}
		return sb.toString();
	}

	/**
	 * Returns true if this is a presence condition with the non-machine-processable argument
	 * {@link #ARG_CONDID} (meaning the stem ends with {@link #STEM_END_COND}), false otherwise.
	 */
	public boolean isWithCondID() {
		return getStem().endsWith(STEM_END_COND);
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("Presence condition ");
		builder.append(getStemAndArgs());
		if (!getText().isEmpty()) {
			builder.append(": ").append(_text);
		}
		builder.append(".");
		return builder.toString();
	}
}
