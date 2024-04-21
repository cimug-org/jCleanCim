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
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import org.tanjakostic.jcleancim.common.Nature;
import org.tanjakostic.jcleancim.util.Util;

/**
 * UML stereotype.
 * <p>
 * It is essentially a set of comma-separated string values. To avoid the application to do string
 * parsing and adding e.g. "&lt;&lt;" and "&gt;&gt;" around the stereotypes, this simple class does
 * it in one place.
 * <p>
 * FIXME: Improve initialisation and add modifiers ...
 *
 * @author tatjana.kostic@ieee.org
 * @version $Id: UmlStereotype.java 34 2019-12-20 18:37:17Z dev978 $
 */
public class UmlStereotype {

	/** UML stereotype for enumeration class. */
	public static final String ENUMERATION = "enumeration";

	/** UML stereotype for enumeration literals. */
	public static final String ENUM = "enum";

	/** UML stereotype for deprecated UML objects of any kind. */
	public static final String DEPRECATED = "deprecated";

	/** UML stereotype for non-normative UML objects of any kind. */
	public static final String INFORMATIVE = "informative";

	/** UML stereotype for import dependency between UML packages. */
	public static final String IMPORT = "import";

	// CIM class stereotypes; 'enumeration' is UML standard, others are ours.
	public static final String PRIMITIVE = "Primitive";
	public static final String OLD_DATATYPE = "Datatype"; // for backwards compatibility
	public static final String CIMDATATYPE = "CIMDatatype";
	public static final String COMPOUND = "Compound";

	/** CIM stereotype for UML profiles, applicable to UML dependency. */
	public static final String ISBASEDON = "IsBasedOn";

	// ==== WG10 class stereotypes. 'interface' is UML standard, others are ours
	public static final String EA_INTERFACE = "interface";
	public static final String PACKED = "packed";
	public static final String BASIC = "basic";
	public static final String STRUCTURED = "structured";

	/**
	 * Used for some abstract LN classes (61850-7-4) to tag that presence conditions of its data
	 * objects do not change in the context of derived statistic instance. Not inheritable (i.e.,
	 * should not be printed in subclasses).
	 **/
	public static final String ADMIN = "admin";

	/**
	 * Used for CDCs (61850-7-3) allowed for use as type in DOs of derived statistics LNs; CDCs
	 * without this stereotype are forbidden for use in derived statistics context. It is
	 * inheritable (i.e., should be printed in subclasses).
	 **/
	public static final String STATISTICS = "statistics";

	/** Used for enumerations that represent presence conditions (modelled as class constraints). */
	public static final String COND = "cond";

	/** Used for enumerations that represent abbreviations. */
	public static final String ABBR = "abbr";

	/** Used for operations. */
	public static final String EVENT = "event";

	// -------------------------------- static API -----------------------------------

	@SuppressWarnings("serial")
	private static final Map<Nature, Set<String>> PACKAGE_BUILT_INS = new TreeMap<Nature, Set<String>>() {
		{
			put(Nature.CIM, new HashSet<>(
					Arrays.asList(UmlStereotype.DEPRECATED, UmlStereotype.INFORMATIVE)));
			put(Nature.IEC61850, new HashSet<>(
					Arrays.asList(UmlStereotype.DEPRECATED, UmlStereotype.INFORMATIVE)));
		}
	};

	public static Map<Nature, Set<String>> getPackageBuiltIns() {
		return getBuiltIns(PACKAGE_BUILT_INS);
	}

	@SuppressWarnings("serial")
	private static final Map<Nature, Set<String>> CLASS_BUILT_INS = new TreeMap<Nature, Set<String>>() {
		{
			put(Nature.CIM,
					new HashSet<>(Arrays.asList(UmlStereotype.DEPRECATED, UmlStereotype.INFORMATIVE,
							UmlStereotype.ENUMERATION, UmlStereotype.PRIMITIVE,
							UmlStereotype.OLD_DATATYPE, UmlStereotype.CIMDATATYPE,
							UmlStereotype.COMPOUND)));
			put(Nature.IEC61850,
					new HashSet<>(Arrays.asList(UmlStereotype.DEPRECATED, UmlStereotype.INFORMATIVE,
							UmlStereotype.ENUMERATION, UmlStereotype.EA_INTERFACE,
							UmlStereotype.PACKED, UmlStereotype.BASIC, UmlStereotype.STRUCTURED,
							UmlStereotype.ADMIN, UmlStereotype.STATISTICS, UmlStereotype.COND,
							UmlStereotype.ABBR)));
		}
	};

	public static Map<Nature, Set<String>> getClassBuiltIns() {
		return getBuiltIns(CLASS_BUILT_INS);
	}

	@SuppressWarnings("serial")
	private static final Map<Nature, Set<String>> ATTR_BUILT_INS = new TreeMap<Nature, Set<String>>() {
		{
			put(Nature.CIM, new HashSet<>(Arrays.asList(UmlStereotype.DEPRECATED,
					UmlStereotype.INFORMATIVE, UmlStereotype.ENUM)));
			put(Nature.IEC61850, new HashSet<>(Arrays.asList(UmlStereotype.DEPRECATED,
					UmlStereotype.INFORMATIVE, UmlStereotype.ENUM)));
		}
	};

	public static Map<Nature, Set<String>> getAttributeBuiltIns() {
		return getBuiltIns(ATTR_BUILT_INS);
	}

	@SuppressWarnings("serial")
	private static final Map<Nature, Set<String>> ASSOC_BUILT_INS = new TreeMap<Nature, Set<String>>() {
		{
			put(Nature.CIM, new HashSet<>(
					Arrays.asList(UmlStereotype.DEPRECATED, UmlStereotype.INFORMATIVE)));
			put(Nature.IEC61850, new HashSet<>(
					Arrays.asList(UmlStereotype.DEPRECATED, UmlStereotype.INFORMATIVE)));
		}
	};

	public static Map<Nature, Set<String>> getAssociationBuiltIns() {
		return getBuiltIns(ASSOC_BUILT_INS);
	}

	@SuppressWarnings("serial")
	private static final Map<Nature, Set<String>> ASSOC_END_BUILT_INS = new TreeMap<Nature, Set<String>>() {
		{
			put(Nature.CIM, new HashSet<>(
					Arrays.asList(UmlStereotype.DEPRECATED, UmlStereotype.INFORMATIVE)));
			put(Nature.IEC61850, new HashSet<>(
					Arrays.asList(UmlStereotype.DEPRECATED, UmlStereotype.INFORMATIVE)));
		}
	};

	public static Map<Nature, Set<String>> getAssociationEndBuiltIns() {
		return getBuiltIns(ASSOC_END_BUILT_INS);
	}

	@SuppressWarnings("serial")
	private static final Map<Nature, Set<String>> DEPS_BUILT_INS = new TreeMap<Nature, Set<String>>() {
		{
			put(Nature.CIM, new HashSet<>(Arrays.asList(UmlStereotype.DEPRECATED,
					UmlStereotype.IMPORT, UmlStereotype.ISBASEDON)));
			put(Nature.IEC61850, new HashSet<>(Arrays.asList(UmlStereotype.DEPRECATED)));
		}
	};

	public static Map<Nature, Set<String>> getDependencyBuiltIns() {
		return getBuiltIns(DEPS_BUILT_INS);
	}

	@SuppressWarnings("serial")
	private static final Map<Nature, Set<String>> DIAG_BUILT_INS = new TreeMap<Nature, Set<String>>() {
		{
			put(Nature.CIM, new HashSet<>(
					Arrays.asList(UmlStereotype.DEPRECATED, UmlStereotype.INFORMATIVE)));
			put(Nature.IEC61850, new HashSet<>(
					Arrays.asList(UmlStereotype.DEPRECATED, UmlStereotype.INFORMATIVE)));
		}
	};

	public static Map<Nature, Set<String>> getDiagramBuiltIns() {
		return getBuiltIns(DIAG_BUILT_INS);
	}

	@SuppressWarnings("serial")
	private static final Map<Nature, Set<String>> OPER_BUILT_INS = new TreeMap<Nature, Set<String>>() {
		{
			put(Nature.CIM, new HashSet<>(Arrays.asList(UmlStereotype.DEPRECATED,
					UmlStereotype.INFORMATIVE, UmlStereotype.EVENT)));
			put(Nature.IEC61850, new HashSet<>(Arrays.asList(UmlStereotype.DEPRECATED,
					UmlStereotype.INFORMATIVE, UmlStereotype.EVENT)));
		}
	};

	public static Map<Nature, Set<String>> getOperationBuiltIns() {
		return getBuiltIns(OPER_BUILT_INS);
	}

	@SuppressWarnings("serial")
	private static final Map<Nature, Set<String>> OPER_PAR_BUILT_INS = new TreeMap<Nature, Set<String>>() {
		{
			put(Nature.CIM, new HashSet<>(
					Arrays.asList(UmlStereotype.DEPRECATED, UmlStereotype.INFORMATIVE)));
			put(Nature.IEC61850, new HashSet<>(
					Arrays.asList(UmlStereotype.DEPRECATED, UmlStereotype.INFORMATIVE)));
		}
	};

	public static Map<Nature, Set<String>> getOperationParameterBuiltIns() {
		return getBuiltIns(OPER_PAR_BUILT_INS);
	}

	private static Map<Nature, Set<String>> getBuiltIns(Map<Nature, Set<String>> builtins) {
		Map<Nature, Set<String>> result = new TreeMap<Nature, Set<String>>();
		for (Nature nature : Nature.values()) {
			result.put(nature, Collections.unmodifiableSet(builtins.get(nature)));
		}
		return result;
	}

	// -------------------

	private final Set<String> _tokens = new LinkedHashSet<String>();
	private String _commaSeparatedTokens; // cache

	/**
	 * Constructor.
	 *
	 * @param tokens
	 *            (optional) desired number of individual non-null stereotype tokens; empty tokens
	 *            are skipped. A token that contains comma-separated items will be split and each of
	 *            those items will be kept as a stereotype token.
	 */
	public UmlStereotype(String... tokens) {
		Util.ensureContainsNoNull(tokens, "tokens");
		for (String token : tokens) {
			List<String> splitSubtokens = Util.splitCommaSeparatedTokens(token);
			_tokens.addAll(splitSubtokens);
		}
	}

	/** Returns whether this instance is empty (has no tokens). */
	public boolean isEmpty() {
		return _tokens.isEmpty();
	}

	/** Returns whether this instance contains <code>token</code>). */
	public boolean contains(String token) {
		return _tokens.contains(token);
	}

	/** Returns whether this instance contains any token from <code>token</code>). */
	public boolean containsAnyOf(Collection<String> tokens) {
		for (String token : tokens) {
			if (_tokens.contains(token)) {
				return true;
			}
		}
		return false;
	}

	/** Returns whether all the tokens of this instance are contained in <code>tokens</code>. */
	public boolean memberOf(Set<String> tokens) {
		return getTokensOtherThan(tokens).isEmpty();
	}

	/** Returns all the tokens of this instance "minus" those contained in <code>tokens</code>. */
	public Set<String> getTokensOtherThan(Set<String> tokens) {
		Set<String> result = new LinkedHashSet<String>(_tokens);
		result.removeAll(tokens);
		return result;
	}

	/** Returns comma-separated list of stereotype tokens. */
	public String value() {
		if (_commaSeparatedTokens == null) {
			_commaSeparatedTokens = buildCommaSeparatedToken();
		}
		return _commaSeparatedTokens.toString();
	}

	private String buildCommaSeparatedToken() {
		return Util.concatCharSeparatedTokens(", ", new ArrayList<String>(_tokens));
	}

	/**
	 * {@inheritDoc}
	 * <p>
	 * Ensloses {@link #value()} into "<<" and ">>".
	 */
	@Override
	public String toString() {
		return (_tokens.isEmpty()) ? "" : ("<<" + value() + ">>");
	}
}
