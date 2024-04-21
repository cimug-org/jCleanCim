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

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.tanjakostic.jcleancim.util.Util;

/**
 * Utility class with various constants applicable to Enterprise Architect internal data model.
 *
 * @author tatjana.kostic@ieee.org
 * @version $Id: EA.java 34 2019-12-20 18:37:17Z dev978 $
 */
public class EA {

	public static final String DEDUCED_STEREOS = "DeducedStereotypes"; // our own key: value derived
	public static final String EA_GUID = "ea_guid";
	protected static final String PARENT_ID = "ParentID";

	protected static final String XREF_NAME = "Name";
	protected static final String XREF_NAME_STEREOS = "Stereotypes"; // cell val
	protected static final String XREF_TYPE = "Type";
	protected static final String XREF_TYPE_CONN_SRC = "connectorSrcEnd property"; // cell val
	protected static final String XREF_TYPE_CONN_DEST = "connectorDestEnd property"; // cell val
	protected static final String XREF_CLIENT = "Client";
	protected static final String XREF_DESCRIPTION = "Description";
	static final String[] XREF_TAGS = new String[] { XREF_NAME, XREF_TYPE, XREF_CLIENT,
			XREF_DESCRIPTION };

	public static final String PACKAGE_OWNER_ID = "Parent_ID";
	public static final String PACKAGE_ID = "Package_ID";
	public static final String PACKAGE_NAME = "Name";
	public static final String PACKAGE_NOTE = "Notes";
	public static final String PACKAGE_POS = "TPos";
	/** these are proper to package table; see more in PACKAGE_AS_ELEM_TAGS below */
	static final String[] PACKAGE_TAGS = new String[] { PACKAGE_OWNER_ID, PACKAGE_ID, EA_GUID,
			PACKAGE_NAME, PACKAGE_NOTE, PACKAGE_POS };

	protected static final String DIA_OWNER_ID = PARENT_ID; // ID of containing elem (0 for package)
	protected static final String DIA_PCKG_ID = PACKAGE_ID; // pckID of containing pck (always != 0)
	public static final String DIA_ID = "Diagram_ID";
	public static final String DIA_NAME = "Name";
	public static final String DIA_NOTE = "Notes";
	public static final String DIA_STEREO = "Stereotype";
	public static final String DIA_ORIENT = "Orientation";
	public static final String DIA_TYPE = "Diagram_Type";
	protected static final String DIA_POS = "TPos";
	static final String[] DIA_TAGS = new String[] { DIA_OWNER_ID, DIA_PCKG_ID, DIA_ID, DIA_NAME,
			DIA_NOTE, EA_GUID, DIA_STEREO, DIA_ORIENT, DIA_TYPE, DIA_POS };

	public static final String ELEM_ID = "Object_ID";
	public static final String ELEM_NAME = "Name";
	public static final String ELEM_ALIAS = "Alias";
	public static final String ELEM_NOTE = "Note";
	public static final String ELEM_SCOPE = "Scope";
	public static final String ELEM_TYPE = "Object_Type";
	public static final String ELEM_ABSTRACT = "Abstract";
	public static final String ELEM_ROOT = "IsRoot"; // TRUE/FALSE
	public static final String ELEM_LEAF = "IsLeaf"; // TRUE/FALSE
	public static final String ELEM_PERSIST = "Persistence";
	public static final String ELEM_SUBTYPE = "NType"; // integer ("magic" number...)
	public static final String ELEM_POS = "TPos"; // integer or empty string (meaning 0)
	static final String[] ELEM_TAGS = new String[] { ELEM_ID, PARENT_ID, PACKAGE_ID, EA_GUID,
			ELEM_NAME, ELEM_ALIAS, ELEM_SCOPE, ELEM_NOTE, ELEM_TYPE, ELEM_ABSTRACT, ELEM_ROOT,
			ELEM_LEAF, ELEM_PERSIST, ELEM_SUBTYPE, ELEM_POS };
	protected static final String[] ELEM_TAGS_OUT = new String[] { DEDUCED_STEREOS }; // <
	/** these are defined only on elements (i.e., package type in elements table - sic!) */
	static final String[] PACKAGE_AS_ELEM_TAGS = new String[] { ELEM_ID, ELEM_ALIAS, ELEM_SCOPE,
			DEDUCED_STEREOS };

	public static final String CLASS_CONSTR_NAME = "Constraint";
	public static final String CLASS_CONSTR_NOTE = "Notes";
	static final String[] CLASS_CONSTR_TAGS = new String[] { ELEM_ID, CLASS_CONSTR_NAME,
			CLASS_CONSTR_NOTE };

	public static final String ELEM_TGVAL_NAME = "Property";
	public static final String ELEM_TGVAL_VALUE = "Value";
	static final String[] ELEM_TGVAL_TAGS = new String[] { ELEM_ID, ELEM_TGVAL_NAME,
			ELEM_TGVAL_VALUE };

	public static final String ATTR_ID = "ID";
	public static final String ATTR_POSITION = "Pos";
	public static final String ATTR_NAME = "Name";
	public static final String ATTR_NOTE = "Notes";
	public static final String ATTR_STYLE = "Style";
	public static final String ATTR_SCOPE = "Scope";
	public static final String ATTR_CONST = "Const";
	public static final String ATTR_STATIC = "IsStatic";
	public static final String ATTR_LOBOUND = "LowerBound";
	public static final String ATTR_UPBOUND = "UpperBound";
	public static final String ATTR_DEFAULT = "Default";
	public static final String ATTR_CLASSIF = "Classifier";
	public static final String ATTR_TYPE = "Type";
	static final String[] ATTR_TAGS = new String[] { ELEM_ID, ATTR_ID, ATTR_NOTE, EA_GUID,
			ATTR_NAME, ATTR_STYLE, ATTR_SCOPE, ATTR_POSITION, ATTR_CONST, ATTR_STATIC, ATTR_LOBOUND,
			ATTR_UPBOUND, ATTR_DEFAULT, ATTR_CLASSIF, ATTR_TYPE };
	protected static final String[] ATTR_TAGS_OUT = new String[] { DEDUCED_STEREOS }; // <

	protected static final String ATTR_TGVAL_OWNER_ID = "ElementID";
	public static final String ATTR_TGVAL_NAME = ELEM_TGVAL_NAME;
	public static final String ATTR_TGVAL_VALUE = "VALUE";
	static final String[] ATTR_TGVAL_TAGS = new String[] { ATTR_TGVAL_OWNER_ID, ATTR_TGVAL_NAME,
			ATTR_TGVAL_VALUE };

	public static final String ATTR_CONSTR_NAME = CLASS_CONSTR_NAME;
	public static final String ATTR_CONSTR_NOTE = CLASS_CONSTR_NOTE;
	static final String[] ATTR_CONSTR_TAGS = new String[] { ATTR_ID, ATTR_CONSTR_NAME,
			ATTR_CONSTR_NOTE };

	protected static final String OP_OWNER_ID = ELEM_ID;
	public static final String OP_ID = "OperationID";
	public static final String OP_NAME = "Name";
	public static final String OP_ALIAS = "Style";
	public static final String OP_NOTE = "Notes";
	public static final String OP_SCOPE = "Scope";
	public static final String OP_POS = "Pos";
	public static final String OP_RET_TYPE_NAME = "Type";
	public static final String OP_RET_ARRAY = "ReturnArray"; // 0 / 1
	public static final String OP_RET_TYPE_ID = "Classifier";
	public static final String OP_STATIC = "IsStatic"; // 0 / 1
	public static final String OP_ABSTRACT = "Abstract"; // 0 / 1
	public static final String OP_FINAL = "IsLeaf"; // FALSE / TRUE
	static final String[] OP_TAGS = new String[] { OP_OWNER_ID, OP_ID, OP_NAME, OP_ALIAS, OP_NOTE,
			EA_GUID, OP_SCOPE, OP_POS, OP_RET_TYPE_NAME, OP_RET_ARRAY, OP_RET_TYPE_ID, OP_STATIC,
			OP_ABSTRACT, OP_FINAL };
	protected static final String[] OP_TAGS_OUT = new String[] { DEDUCED_STEREOS }; // <

	protected static final String OP_TGVAL_OWNER_ID = "ElementID";
	public static final String OP_TGVAL_NAME = "Property";
	public static final String OP_TGVAL_VALUE = "VALUE";
	static final String[] OP_TGVAL_TAGS = new String[] { OP_TGVAL_OWNER_ID, OP_TGVAL_NAME,
			OP_TGVAL_VALUE };

	protected static final String PAR_OWNER_ID = "OperationID";
	public static final String PAR_NAME = "Name";
	public static final String PAR_POS = "Pos";
	public static final String PAR_NOTE = "Notes";
	protected static final String PAR_STYLE = "StyleEx"; // EA burries alias here
	public static final String PAR_ALIAS = "Alias"; // our own key; value derived from style
	public static final String PAR_TYPE = "Type";
	public static final String PAR_CLASSIF = "Classifier";
	static final String[] PAR_TAGS = new String[] { PAR_OWNER_ID, PAR_NAME, PAR_POS, PAR_NOTE,
			EA_GUID, PAR_STYLE, PAR_TYPE, PAR_CLASSIF };
	protected static final String[] PAR_TAGS_OUT = new String[] { DEDUCED_STEREOS, PAR_ALIAS };

	public static final String CONN_ID = "Connector_ID";
	public static final String CONN_NAME = "Name";
	protected static final String CONN_STYLEEX = "StyleEx"; // EA burries connector alias here
	public static final String CONN_ALIAS = "Alias"; // our key; value derived from style
	public static final String CONN_NOTE = "Notes";
	public static final String CONN_TYPE = "Connector_Type";
	public static final String CONN_DIR = "Direction";
	public static final String CONN_FROM_ID = "Start_Object_ID";
	public static final String CONN_TO_ID = "End_Object_ID";
	public static final String CONN_FROM_NAME = "SourceRole";
	public static final String CONN_TO_NAME = "DestRole";
	public static final String CONN_FROM_STYLE = "SourceStyle"; // EA burries role alias here
	public static final String CONN_TO_STYLE = "DestStyle"; // EA burries role alias here
	public static final String CONN_FROM_ALIAS = "SrcAlias"; // our key; value derived from style
	public static final String CONN_TO_ALIAS = "EndAlias"; // our key; value derived from style
	public static final String CONN_FROM_SCOPE = "SourceAccess";
	public static final String CONN_TO_SCOPE = "DestAccess";
	public static final String CONN_FROM_STEREOS = "SrcStereo"; // our key; value derived from xref
	public static final String CONN_TO_STEREOS = "DestStereo"; // our key; value derived from xref
	public static final String CONN_FROM_NOTE = "SourceRoleNote";
	public static final String CONN_TO_NOTE = "DestRoleNote";
	public static final String CONN_FROM_AGGREG = "SourceIsAggregate";
	public static final String CONN_TO_AGGREG = "DestIsAggregate";
	public static final String CONN_FROM_CARD = "SourceCard";
	public static final String CONN_TO_CARD = "DestCard";
	public static final String CONN_FROM_NAV = "SrcNav"; // our key; value derived from style
	public static final String CONN_TO_NAV = "DestNav"; // our key; value derived from style
	static final String[] CONN_TAGS = new String[] { CONN_ID, CONN_NAME, CONN_STYLEEX, CONN_NOTE,
			EA_GUID, CONN_TYPE, CONN_DIR, CONN_FROM_ID, CONN_TO_ID, CONN_FROM_NAME, CONN_TO_NAME,
			CONN_FROM_STYLE, CONN_TO_STYLE, CONN_FROM_SCOPE, CONN_TO_SCOPE, CONN_FROM_NOTE,
			CONN_TO_NOTE, CONN_FROM_AGGREG, CONN_TO_AGGREG, CONN_FROM_CARD, CONN_TO_CARD };
	static final String[] CONN_TAGS_OUT = new String[] { DEDUCED_STEREOS, CONN_ALIAS,
			CONN_FROM_STEREOS, CONN_TO_STEREOS, CONN_FROM_ALIAS, CONN_TO_ALIAS, CONN_FROM_NAV,
			CONN_TO_NAV };

	protected static final String CONN_TGVAL_OWNER_ID = ATTR_TGVAL_OWNER_ID;
	public static final String CONN_TGVAL_NAME = ELEM_TGVAL_NAME;
	public static final String CONN_TGVAL_VALUE = ATTR_TGVAL_VALUE;
	static final String[] CONN_TGVAL_TAGS = new String[] { CONN_TGVAL_OWNER_ID, CONN_TGVAL_NAME,
			CONN_TGVAL_VALUE };

	protected static final String ROLE_TGVAL_OWNER_ID = ATTR_TGVAL_OWNER_ID;
	protected static final String ROLE_TGVAL_BASECLASS = "BaseClass";
	public static final String ROLE_TGVAL_NAME = "TagValue";
	public static final String ROLE_TGVAL_VALUE = "Notes";
	static final String[] ROLE_TGVAL_TAGS = new String[] { ROLE_TGVAL_OWNER_ID,
			ROLE_TGVAL_BASECLASS, ROLE_TGVAL_NAME, ROLE_TGVAL_VALUE };

	// -------------- support for patterns, to dig data from inobvious places ------------

	static final Pattern STEREO_PATTERN = Pattern.compile("@STEREO;Name=(.*?);");

	/** Exctracts stereotypes if existing, otherwise returns empty string. */
	public static String extractStereotypes(String description) {
		String stereos = matchPattern(EA.STEREO_PATTERN, description);
		if (!stereos.contains(",")) {
			return stereos;
		}
		List<String> list = Util.splitCommaSeparatedTokens(stereos);
		Set<String> noDups = new LinkedHashSet<String>(list);
		return Util.concatCharSeparatedTokens(",", new ArrayList<String>(noDups));
	}

	static final Pattern ALIAS_PATTERN = Pattern.compile("alias=(.+?);");
	static final Pattern ROLE_NAVIGABLE_PATTERN = Pattern.compile("Navigable=(.+?);");

	/**
	 * EA burries in certain cases alias information in its Style/StyleEx table columns; this method
	 * interprets and returns such an item.
	 *
	 * @param burried
	 */
	public static String extractAlias(String burried) {
		return matchPattern(EA.ALIAS_PATTERN, burried);
	}

	/**
	 * EA burries navigability information for association ends in the connector table's
	 * Style/StyleEx columns; this method interprets and returns such an item.
	 *
	 * @param burried
	 */
	public static String extractNavigability(String burried) {
		return matchPattern(EA.ROLE_NAVIGABLE_PATTERN, burried);
	}

	private static String matchPattern(Pattern pattern, String input) {
		if (input == null) {
			return "";
		}

		StringBuilder result = new StringBuilder();
		Matcher matcher = pattern.matcher(input);
		while (matcher.find()) {
			result.append(",").append(matcher.group(1));
		}
		if (result.toString().startsWith(",")) {
			result = new StringBuilder(result.substring(1));
		}
		return result.toString();
	}

	protected EA() {
		// prevents creation
	}
}
