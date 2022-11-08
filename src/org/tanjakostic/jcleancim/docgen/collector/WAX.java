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

package org.tanjakostic.jcleancim.docgen.collector;

import org.tanjakostic.jcleancim.model.UML;
import org.tanjakostic.jcleancim.model.UmlStereotype;

/**
 * String constants used for XML doc generation.
 *
 * @author tatjana.kostic@ieee.org
 * @version $Id: WAX.java 31 2019-12-08 01:19:54Z dev978 $
 */
public class WAX {
	private WAX() {
		// prevents construction
	}

	public static final String E_IECDomainSpec = "IECDomainSpec";
	public static final String E_IECDomainDoc = "IECDomainDoc";

	public static final String E_Doc = "Doc";

	public static final String E_IEC61850Domain = "IEC61850Domain";

	public static final String A_name = "name";

	public static final String A_informative = UmlStereotype.INFORMATIVE;
	public static final String A_deprecated = UmlStereotype.DEPRECATED;
	public static final String A_inheritedFrom = "inheritedFrom";
	public static final String A_abstract = UML.CLASS_abstract;
	public static final String A_superClass = "superClass";

	public static final String A_alias = "alias";
	public static final String A_aliasID = "aliasID";
	public static final String A_desc = "desc";
	public static final String A_descID = "descID";
	public static final String A_title = "title";
	public static final String A_titleID = "titleID";
	public static final String A_subtitle = "subtitle";
	public static final String A_subtitleID = "subtitleID";

	public static final String A_introduction = "introduction";
	public static final String A_introductionID = "introductionID";
	public static final String A_caption = "caption";
	public static final String A_captionID = "captionID";
	public static final String A_img = "img";

	public static final String A_literalVal = "literalVal";
	public static final String A_type = "type";
	public static final String A_mult = "mult";
	public static final String A_myMult = "myMult";
	public static final String A_presCond = "presCond";
	public static final String A_presCondArgs = "presCondArgs";
	public static final String A_presCondArgsID = "presCondArgsID";
	public static final String A_cond = "cond";
	public static final String A_dsPresCond = "dsPresCond";
	public static final String A_dsPresCondArgs = "dsPresCondArgs";
	public static final String A_dsPresCondArgsID = "dsPresCondArgsID";
	public static final String A_dsCond = "dsCond";
	public static final String A_fc = "fc";
	public static final String A_transient = "transient";
	public static final String A_signature = "signature";
	public static final String A_underlyingType = "underlyingType";
	public static final String A_underlyingControlType = "underlyingControlType";
	public static final String A_kind = "kind";
	public static final String A_text = "text";
	public static final String A_textID = "textID";
	public static final String A_deducedTypeText = "deducedTypeText";
	public static final String A_typeKind = "typeKind";

	public static final String A_defaultValue = "defaultValue";
	public static final String A_minValue = "minValue";
	public static final String A_maxValue = "maxValue";

	public static final String A_bookmarkID = "bookmarkID";

	public static final String V_typeKind_BASIC = "BASIC";
	public static final String V_typeKind_ENUM = "ENUMERATED";
	public static final String V_typeKind_CODED_ENUM = "CODED ENUM";
	public static final String V_typeKind_PACKED_LIST = "PACKED LIST";
	public static final String V_typeKind_CONSTRUCTED = "CONSTRUCTED";

	// ..........

	public static final String A_rsName = UML.TVN_rsName;
	public static final String A_ieeeRef = UML.TVN_ieeeRef;
	public static final String A_iecRef = UML.TVN_iecRef;
	public static final String A_cdcId = UML.TVN_cdcId;
	public static final String A_lns = "lns";

	// -----------

	public static final String E_FunctionalConstraintsTable = "FunctionalConstraintsTable";
	public static final String E_TriggerOptionsTable = "TriggerOptionsTable";
	public static final String E_PresenceConditionsTable = "PresenceConditionsTable";
	public static final String E_AbbreviationsTable = "AbbreviationsTable";
	public static final String E_FunctionsTable = "FunctionsTable";
	public static final String E_CoreTypeAttributesTable = "CoreTypeAttributesTable";
	public static final String E_ConstructedDAsTable = "ConstructedDAsTable";
	public static final String E_CDCAttributesTable = "CDCAttributesTable";
	public static final String E_LNAttributesTable = "LNAttributesTable";
	public static final String E_DataIndexTable = "DataIndexTable";
	public static final String E_OtherAttributesTable = "OtherAttributesTable";

	public static final String E_LiteralsTable = "LiteralsTable";
	public static final String E_OperationsTable = "OperationsTable";
	public static final String E_AssociationEndsTable = "AssociationEndsTable";
	public static final String E_AttributesTable = "AttributesTable";

	// ------------------
	public static final String E_PS = "PS";

	public static final String E_DOCategory = "DOCategory";
	public static final String CAT_daNull = "daNull";
	public static final String CAT_SDOs = "SDOs";
	public static final String CAT_daStatus = "daStatus";
	public static final String CAT_daMeas = "daMeas";
	public static final String CAT_daCtlMirror = "daCtlMirror";
	public static final String CAT_daSubst = "daSubst";
	public static final String CAT_daSetting = "daSetting";
	public static final String CAT_daTracking = "daTracking";
	public static final String CAT_daDesc = "daDesc";
	public static final String CAT_ServiceParameters = "ServiceParameters";

	public static final String E_DACategory = "DACategory";
	public static final String CAT_doNull = "doNull";
	public static final String CAT_doDesc = "doDesc";
	public static final String CAT_doStatus = "doStatus";
	public static final String CAT_doMeas = "doMeas";
	public static final String CAT_doControl = "doControl";
	public static final String CAT_doSetting = "doSetting";
	public static final String CAT_doTracking = "doTracking";

	public static final String LOC_tag = "tag";
	public static final String LOC_instTag = "instTag";
	public static final String LOC_isSpecial = "isSpecial";
}
