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

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * All the names from UML models that we rely on for various processing.
 * <p>
 * Changing any of these in the UML will require change in the code, in this file and potentially in
 * one or more of the config.properties.
 *
 * @author tatjana.kostic@ieee.org
 * @author laurent.guise@art-et-histoire.com
 * @version $Id: UML.java 34 2019-12-20 18:37:17Z dev978 $
 */
public class UML {

	private UML() {
		// prevents construction
	}

	// WG10 meta-model names used for logical nodes, common data classes and data attributes
	public static final String SUPER_PACKED_ENUM_DA = "PackedEnumDA";
	public static final String SUPER_ENUM_DA = "EnumDA";
	public static final String SUPER_PACKED_PRIM_DA = "PackedPrimitiveDA";
	public static final String SUPER_PRIM_DA = "PrimitiveDA";
	public static final String SUPER_COMP_DA = "ComposedDA";
	public static final String SUPER_DA = "DA";

	public static final String SUPER_PACKED_ENUM_FCDA = "PackedEnumFCDA";
	public static final String SUPER_ENUM_FCDA = "EnumFCDA";
	public static final String SUPER_PACKED_FCDA = "PackedListFCDA";
	public static final String SUPER_COMPOSED_FCDA = "ComposedFCDA";
	public static final String SUPER_FCDA = "FCDA";

	public static final String SUPER_CDC = "CDC";

	// sub-packages of common data classes (for 61850 categories/attribute groups):
	public static final String CDCDescription = "CDCDescription";
	public static final String CDCStatusInfo = "CDCStatusInfo";
	public static final String CDCAnalogueInfo = "CDCAnalogueInfo";
	public static final String CDCControl = "CDCControl";
	public static final String CDCStatusSet = "CDCStatusSet";
	public static final String CDCAnalogueSet = "CDCAnalogueSet";
	public static final String CDCServiceTracking = "CDCServiceTracking";

	// CDCs as supertypes for enumerated specialisations:
	public static final String ENS = "ENS";
	public static final String ENC = "ENC";
	public static final String ENG = "ENG";
	public static final String ERY = "ERY";

	// CDCs as supertypes for transient specialisations:
	public static final String SPS = "SPS";
	public static final String ACT = "ACT";
	public static final String SPC = "SPC";

	// CDCs as supertypes for control service tracking specialisations:
	public static final String CTS = "CTS";

	// CDC as supertype for sub-data objects (within composed CDC)
	public static final String BasePrimitiveCDC = "BasePrimitiveCDC";

	// FCDAs as supertypes for attributes within CDCs
	public static final String FCDA_ST = "FCDA_ST";
	public static final String FCDA_MX = "FCDA_MX";
	public static final String FCDA_OR = "FCDA_OR";
	public static final String FCDA_SV = "FCDA_SV";
	public static final String FCDA_BL = "FCDA_BL";
	public static final String FCDA_SP = "FCDA_SP";
	public static final String FCDA_SE = "FCDA_SE";
	public static final String FCDA_SG = "FCDA_SG";
	public static final String FCDA_CF = "FCDA_CF";
	public static final String FCDA_DC = "FCDA_DC";
	public static final String FCDA_EX = "FCDA_EX";
	public static final String FCDA_SR = "FCDA_SR";

	// DA as supertype for service parameter (within control CDC)
	public static final String DA = "DA";

	/** WG10 has some enum types with literal "none" that should be printed as empty value. */
	public static final String TAG_SCL_emptyValue = "scl:emptyValue";

	/** WG10 CDC and DA attributes may have tagged value, to move their position for printing. */
	public static final String TAG_moveAfter = "moveAfter";

	/** WG10 CDC multi-valued attributes may have allowed max index as note in named constraints. */
	public static final String CONSTR_TXT_minIdx = "minIdx";

	/** WG10 CDC multi-valued attributes may have allowed min index as note in named constraints. */
	public static final String CONSTR_TXT_maxIdx = "maxIdx";

	// WG10-specific tagged values, used in classes of Part 5, to allow for generating tables
	public static final String TVN_rsName = "rsName"; // rs = requirements spec
	public static final String TVN_ieeeRef = "ieeeRef";
	public static final String TVN_iecRef = "iecRef";

	/**
	 * WG10-specific tagged value, used in DA tables (7-3).
	 * <p>
	 * Note that at present this is not really used as a tagged value but rather as a cludge for
	 * printing correct table title; it should actually be a part of some meta-model, similar to
	 * {@link #TVN_cdcId}. Until we do have effectively that in a meta-model, I keep this one here
	 * for uniform processing.
	 */
	public static final String TVN_datId = "datId";

	/** WG10-specific tagged value, used in CDC tables (7-3). */
	public static final String TVN_cdcId = "cdcId";

	/** WG10-specific tagged value to refer to old type name, used in core types only (7-2). */
	public static final String TVN_oldName = "oldName";

	// superclass name from the meta-model
	public static final String StatisticsLN = "StatisticsLN";

	// exception for LN naming rule (allowed to contain a number)
	public static final String LLN0 = "LLN0";

	// special DOs with hard-coded presence condition logic for derived statistics
	public static final String ClcMth = "ClcMth";
	public static final String ClcSrc = "ClcSrc";

	// implicit presence condition names
	public static final String PC_M = "M";
	public static final String PC_O = "O";
	public static final String PC_F = "F";
	public static final String PC_na = "na";

	// FIXME DA names starting with upper case
	public static final Set<String> IGNORE_CASE_DAS = new HashSet<>(
			Arrays.asList("T", "Test", "Check", "SIUnit", "Oper", "SBO", "SBOw", "Cancel"));

	// FIXME Abbreviations starting with lower case
	public static final Set<String> IGNORE_CASE_ABBREVS = new HashSet<>(Arrays.asList("ppm"));

	// FIXME Prefix for package names for LN groups
	public static final String PREF_LNGroup = "LNGroup";

	// WG18-specific prefix to refer to other standard; DO names not validated:
	public static final String PREF_DOName_Ieee = "Ieee";

	public static final String PREF_P_ = "P_";
	public static final String PREF_S_ = "S_";
	public static final String SUFF_Transient = "Transient";

	/** IEC61850-specific suffix for namespace class name. */
	public static final String IEC61850_NAMESPACE_CLASS_SUFFIX = "Namespace";

	/** IEC61850-specific suffix for version class name. */
	public static final String IEC61850_VERSION_CLASS_SUFFIX = "UMLVersion";

	public static final String ATTR_id = "id";
	public static final String ATTR_revision = "revision";
	public static final String ATTR_tissuesApplied = "tissuesApplied";

	/** Private attribute name on DA meta-model type, to hold basic type used. */
	public static final String ATTR_val = "val";

	/** Suffix for DerivedDA class name applicable for {@link UML#ENC}. */
	public static final String SUFF_CONTROL = "_control";

	/** Private attribute name on DA meta-model type, to hold basic type for control parameter. */
	public static final String ATTR_ctlVal = "ctlVal";

	/** Private attribute name on FCDA meta-model type, to hold actual DA type used. */
	public static final String ATTR_attr = "attr";

	// ===========================================
	// ***************** CIM only ****************
	// ===========================================

	public static final String CIM_DT_value = "value";
	public static final String CIM_DT_unit = "unit";
	public static final String CIM_DT_multiplier = "multiplier";

	/** Heavily used in CIM UML profiles, but never in canonical CIM. */
	public static final String TVN_GUIDBasedOn = "GUIDBasedOn";

	/** CIM classes for whose attributes/literals we don't verify upper/lower case and plural. */
	public static final Set<String> IGNORE_CASE_ENUMS = new HashSet<>(
			Arrays.asList("UnitSymbol", "UnitMultiplier", "Currency", "PhaseCode",
					"PhaseShuntConnectionKind", "SinglePhaseKind", "WindingConnection"));

	/** CIM-specific suffix for version class name. */
	public static final String CIM_VERSION_CLASS_SUFFIX = "CIMVersion";

	// **************** common **************

	/** Attribute in a top-package or CIM profile version class (and 61850 namespace class). */
	public static final String ATTR_version = "version";

	/** Attribute in a top-package or CIM profile version class (and 61850 namespace class). */
	public static final String ATTR_date = "date";

	/** Tagged value name to specify namespace URI for a UML object. */
	public static final String TVN_nsuri = "nsuri";

	/** Tagged value name to specify namespace prefix for a UML object. */
	public static final String TVN_nsprefix = "nsprefix";

	/** Qualifier for a class that is abstract. */
	public static final String CLASS_abstract = "abstract";

	/** Exceptions in EA must be specified as tagged value with the tag name 'throws'. */
	public static final String TVN_throws = "throws";

	/**
	 * Prefix for informative sub-packages. This is to avoid name clashes among normative and
	 * informative packages (and to make informative sub-packages obvious). Note that this will
	 * apply to all packages that start with this prefix (e.g., 'InfWork' as well as 'Informative'.
	 */
	public static final String INF_PREFIX = "Inf";

	/**
	 * Format string, including the prefix for packages reserved for diagrams used for the template
	 * only, and that should not be printed with the content of the regular model. The subpackage of
	 * "ABC" that has name "DocABC" will be matched, and considered as informative package.
	 */
	public static final String DOC_FORMAT_STRING = "Doc%s";

	/**
	 * Reserved name for a package that contains diagrams that may be useful for information but not
	 * for printing into any generated spec. Typically contains diagrams that illustrate open CIM
	 * issues, or that illustrate classes used in profiles. It is considered as informative.
	 * <p>
	 * In addition, because EA takes long to export diagrams, and we never want to reference these
	 * diagrams from within a template, they are never exported (i.e., they always have an empty
	 * pic).
	 */
	public static final String DetailedDiagrams = "DetailedDiagrams";
}
