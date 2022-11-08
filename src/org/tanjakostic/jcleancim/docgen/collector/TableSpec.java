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

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.tanjakostic.jcleancim.common.Nature;
import org.tanjakostic.jcleancim.util.Util;

/**
 * Information required to describe a table and its columns for generating documentation.
 *
 * @author tatjana.kostic@ieee.org
 * @version $Id: TableSpec.java 21 2019-08-12 15:44:50Z dev978 $
 */
public class TableSpec {

	private static final Map<String, TableSpec> TABLES = new LinkedHashMap<String, TableSpec>();
	private static final Map<Nature, List<TableSpec>> TABS_PER_NATURE = new LinkedHashMap<Nature, List<TableSpec>>();

	public static final String KEY_trgOp = "trgOp";

	public static final TableSpec FCS = createFunctionalConstraintsTable(Nature.IEC61850);
	public static final TableSpec TRG_OPS = createTriggerOptionsTable(Nature.IEC61850);
	public static final TableSpec PRES_CONDS = createPresenceConditionsTable(Nature.IEC61850);
	public static final TableSpec ABBREVS = createAbbreviationsTable(Nature.IEC61850);
	public static final TableSpec FUNCTIONS = createFunctionsTable(Nature.IEC61850);
	public static final TableSpec CTA_ATTRS = createDATable(WAX.E_CoreTypeAttributesTable, "ct",
			Nature.IEC61850);
	public static final TableSpec CDA_ATTRS = createDATable(WAX.E_ConstructedDAsTable, "da",
			Nature.IEC61850);
	public static final TableSpec CDC_ATTRS = createCDCAttributesTable(Nature.IEC61850);
	public static final TableSpec LN_ATTRS = createLNAttributesTable(Nature.IEC61850);
	public static final TableSpec ATTR_INDEX = createAttributeIndexTable(Nature.IEC61850);
	public static final TableSpec CUSTOM_LITERALS = createCustomLiteralsTable(Nature.IEC61850);
	public static final TableSpec ODA_ATTRS = createDATable(WAX.E_OtherAttributesTable, "other",
			Nature.IEC61850);
	public static final TableSpec CUSTOM_OPERATIONS = createOperationsTable("61850",
			Nature.IEC61850);
	public static final TableSpec CUSTOM_ASSOC_ENDS = createCustomAssociationEndsTable(Nature.IEC61850);

	public static final TableSpec LITERALS = createLiteralsTable(Nature.CIM);
	public static final TableSpec ATTRS = createAttributesTable(Nature.CIM);
	public static final TableSpec ASSOC_ENDS = createAssocEndsTable(Nature.CIM);
	public static final TableSpec OPERATIONS = createOperationsTable("_", Nature.CIM);

	private static TableSpec createFunctionalConstraintsTable(Nature nature) {
		List<ColumnSpec> cols = new ArrayList<ColumnSpec>();
		cols.add(ColumnSpec.createUnfmted(8, WAX.A_name, "fcNameCol", "FC"));
		cols.add(ColumnSpec.createUnfmted(17, WAX.A_aliasID, "fcAliasCol", "Semantic"));
		cols.add(ColumnSpec.createFmted(75, WAX.A_descID, "fcDescCol",
				"Description (services allowed, initial values, storage)"));

		return createAndStoreTableSpec(WAX.E_FunctionalConstraintsTable, cols, nature);
	}

	private static TableSpec createTriggerOptionsTable(Nature nature) {
		List<ColumnSpec> cols = new ArrayList<ColumnSpec>();
		cols.add(ColumnSpec.createUnfmted(8, WAX.A_name, "toNameCol", "TrgOp"));
		cols.add(ColumnSpec.createUnfmted(17, WAX.A_aliasID, "toAliasCol", "Semantic"));
		cols.add(ColumnSpec.createFmted(75, WAX.A_descID, "toDescCol", "Description"));

		return createAndStoreTableSpec(WAX.E_TriggerOptionsTable, cols, nature);
	}

	private static TableSpec createPresenceConditionsTable(Nature nature) {
		List<ColumnSpec> cols = new ArrayList<ColumnSpec>();
		cols.add(ColumnSpec.createUnfmted(22, WAX.A_name, "pcNameCol", "Condition name"));
		cols.add(ColumnSpec.createFmted(78, WAX.A_descID, "pcDescCol", "Definition"));

		return createAndStoreTableSpec(WAX.E_PresenceConditionsTable, cols, nature);
	}

	/** Widths are scaled to 49%, because they are printed in a 2-column section. */
	private static TableSpec createAbbreviationsTable(Nature nature) {
		List<ColumnSpec> cols = new ArrayList<ColumnSpec>();
		cols.add(ColumnSpec.createUnfmted(10, WAX.A_name, "abbrNameCol", "Term"));
		cols.add(ColumnSpec.createFmted(39, WAX.A_descID, "abbrDescCol", "Description"));

		return createAndStoreTableSpec(WAX.E_AbbreviationsTable, cols, nature);
	}

	private static TableSpec createFunctionsTable(Nature nature) {
		List<ColumnSpec> cols = new ArrayList<ColumnSpec>();
		cols.add(ColumnSpec.createUnfmted(24, WAX.A_name, "funcNameCol", "Functionality"));
		cols.add(ColumnSpec.createUnfmted(9, WAX.A_ieeeRef, "funcIeeeCol", "IEEE C37.2"));
		cols.add(ColumnSpec.createUnfmted(9, WAX.A_iecRef, "funcIecCol", "IEC 60617"));
		cols.add(ColumnSpec.createUnfmted(9, WAX.A_rsName, "funcRsCol", "LN IEC 61850-5"));
		cols.add(ColumnSpec.createUnfmted(9, WAX.A_lns, "funcLnsCol", "LN IEC 61850-7-4"));
		cols.add(ColumnSpec.createFmted(40, WAX.A_descID, "funcDescCol", "Description"));

		return createAndStoreTableSpec(WAX.E_FunctionsTable, cols, nature);
	}

	private static TableSpec createDATable(String name, String idPref, Nature nature) {
		List<ColumnSpec> cols = new ArrayList<ColumnSpec>();
		cols.add(ColumnSpec.createUnfmted(24, WAX.A_name, idPref + "NameCol", "Attribute name"));
		cols.add(ColumnSpec.createUnfmted(20, WAX.A_type, idPref + "TypeCol", "Attribute type"));
		cols.add(ColumnSpec.createFmted(42, WAX.A_descID, idPref + "DescCol",
				"(Value/Value range) Description"));
		cols.add(ColumnSpec.createUnfmted(14, WAX.A_presCond, idPref + "CondCol", "PresCond"));

		return createAndStoreTableSpec(name, cols, nature);
	}

	private static TableSpec createCDCAttributesTable(Nature nature) {
		List<ColumnSpec> cols = new ArrayList<ColumnSpec>();
		cols.add(ColumnSpec.createUnfmted(14, WAX.A_name, "cdcNameCol", "Attribute name"));
		cols.add(ColumnSpec.createUnfmted(23, WAX.A_type, "cdcTypeCol", "Attribute type"));
		cols.add(ColumnSpec.createUnfmted(6, WAX.A_fc, "cdcFcCol", "FC"));
		cols.add(ColumnSpec.createUnfmted(7, TableSpec.KEY_trgOp, "cdcTrgOpCol", "TrgOp"));
		cols.add(ColumnSpec.createFmted(38, WAX.A_descID, "cdcDescCol",
				"(Value/Value range) Description"));
		cols.add(ColumnSpec.createUnfmted(12, WAX.A_presCond, "cdcCondCol", "PresCond"));

		return createAndStoreTableSpec(WAX.E_CDCAttributesTable, cols, nature);
	}

	private static TableSpec createLNAttributesTable(Nature nature) {
		List<ColumnSpec> cols = new ArrayList<ColumnSpec>();
		cols.add(ColumnSpec.createUnfmted(13, WAX.A_name, "lnDoCol", "Data object name"));
		cols.add(ColumnSpec.createUnfmted(22, WAX.A_type, "lnCdcCol", "Common data class"));
		cols.add(ColumnSpec.createUnfmted(4, WAX.A_transient, "lnTransCol", "T"));
		cols.add(ColumnSpec.createFmted(47, WAX.A_descID, "lnDescCol", "Explanation"));
		cols.add(ColumnSpec.createUnfmted(14, WAX.A_presCond, "lnCondCol", "PresCond nds/ds"));

		return createAndStoreTableSpec(WAX.E_LNAttributesTable, cols, nature);
	}

	/** This one is used only for Word doc; XML tags are not from schema, but follow the others. */
	private static TableSpec createAttributeIndexTable(Nature nature) {
		List<ColumnSpec> cols = new ArrayList<ColumnSpec>();
		cols.add(ColumnSpec.createUnfmted(16, WAX.A_name, "diNameCol", "Name"));
		cols.add(ColumnSpec.createUnfmted(18, WAX.A_type, "diTypeCol", "Type"));
		cols.add(ColumnSpec.createFmted(66, WAX.A_descID, "diDescCol", "(Used in) Description"));

		return createAndStoreTableSpec(WAX.E_DataIndexTable, cols, nature);
	}

	private static TableSpec createCustomLiteralsTable(Nature nature) {
		List<ColumnSpec> cols = new ArrayList<ColumnSpec>();
		cols.add(ColumnSpec.createUnfmted(45, WAX.A_name, "litEnumsNameCol", "enumeration item"));
		cols.add(ColumnSpec.createUnfmted(10, WAX.A_literalVal, "litEnumsValueCol", "value"));
		cols.add(ColumnSpec.createFmted(45, WAX.A_descID, "litEnumsDescCol", "description"));

		return createAndStoreTableSpec(WAX.E_LiteralsTable, cols, nature);
	}

	private static TableSpec createOperationsTable(String idPref, Nature nature) {
		List<ColumnSpec> cols = new ArrayList<ColumnSpec>();
		cols.add(ColumnSpec.createUnfmted(55, WAX.A_signature, idPref + "OpsSignCol",
				WAX.A_signature));
		cols.add(ColumnSpec.createFmted(45, WAX.A_descID, idPref + "OpsDescCol", "description"));

		return createAndStoreTableSpec(WAX.E_OperationsTable, cols, nature);
	}

	private static TableSpec createCustomAssociationEndsTable(Nature nature) {
		List<ColumnSpec> cols = new ArrayList<ColumnSpec>();
		cols.add(ColumnSpec.createUnfmted(24, WAX.A_name, "relNameCol", "Name"));
		cols.add(ColumnSpec.createUnfmted(7, WAX.A_mult, "relMultCol", "Mult"));
		cols.add(ColumnSpec.createUnfmted(24, WAX.A_type, "relTypeCol", "Type"));
		cols.add(ColumnSpec.createFmted(45, WAX.A_descID, "relDescCol", "Description"));
		// cols.add(ColumnSpec.createUnfmted(7, WAX.A_presCond, "relCondCol", "PresCond"));

		return createAndStoreTableSpec(WAX.E_AssociationEndsTable, cols, nature);
	}

	// --------------- default / CIM tables -------------

	private static TableSpec createLiteralsTable(Nature nature) {
		List<ColumnSpec> cols = new ArrayList<ColumnSpec>();
		cols.add(ColumnSpec.createUnfmted(45, WAX.A_name, "litNameCol", "literal"));
		cols.add(ColumnSpec.createUnfmted(10, WAX.A_literalVal, "litValueCol", "value"));
		cols.add(ColumnSpec.createFmted(45, WAX.A_descID, "litDescCol", "description"));

		return createAndStoreTableSpec(WAX.E_LiteralsTable, cols, nature);
	}

	private static TableSpec createAttributesTable(Nature nature) {
		List<ColumnSpec> cols = new ArrayList<ColumnSpec>();
		cols.add(ColumnSpec.createUnfmted(24, WAX.A_name, "attNameCol", "name"));
		cols.add(ColumnSpec.createUnfmted(7, WAX.A_mult, "attMultCol", "mult"));
		cols.add(ColumnSpec.createUnfmted(24, WAX.A_type, "attTypeCol", "type"));
		cols.add(ColumnSpec.createFmted(45, WAX.A_descID, "attDescCol", "description"));

		return createAndStoreTableSpec(WAX.E_AttributesTable, cols, nature);
	}

	private static TableSpec createAssocEndsTable(Nature nature) {
		List<ColumnSpec> cols = new ArrayList<ColumnSpec>();
		cols.add(ColumnSpec.createUnfmted(7, WAX.A_myMult, "aeMyMultCol", "mult from"));
		cols.add(ColumnSpec.createUnfmted(24, WAX.A_name, "aeNameCol", "name"));
		cols.add(ColumnSpec.createUnfmted(7, WAX.A_mult, "aeMultCol", "mult to"));
		cols.add(ColumnSpec.createUnfmted(24, WAX.A_type, "aeTypeCol", "type"));
		cols.add(ColumnSpec.createFmted(38, WAX.A_descID, "aeDescCol", "description"));

		return createAndStoreTableSpec(WAX.E_AssociationEndsTable, cols, nature);
	}

	private static TableSpec createAndStoreTableSpec(String name, List<ColumnSpec> cols,
			Nature nature) {
		TableSpec result = new TableSpec(name, nature, cols);
		putPredefined(result);
		return result;
	}

	private static void putPredefined(TableSpec tab) {
		TABLES.put(tab.getName(), tab);

		List<TableSpec> tabsOfNature = TABS_PER_NATURE.get(tab.getNature());
		if (tabsOfNature == null) {
			tabsOfNature = new ArrayList<TableSpec>();
			TABS_PER_NATURE.put(tab.getNature(), tabsOfNature);
		}
		tabsOfNature.add(tab);
	}

	/** Returns all the predefined table formats. */
	public static Map<String, TableSpec> getPredefinedTableSpecs() {
		return Collections.unmodifiableMap(TABLES);
	}

	public static List<TableSpec> getTableSpecs(Nature nature) {
		return TABS_PER_NATURE.get(nature);
	}

	// -----------------------------------------------------------

	private final String _name;
	private final Nature _nature;
	private final Integer _fmtIdx;
	private final String[] _labels;
	private final int[] _relativeWidths;

	private final List<ColumnSpec> _colSpecs;

	public TableSpec(String name, Nature nature, List<ColumnSpec> colSpecs) {
		Util.ensureNotEmpty(name, "name");
		Util.ensureNotNull(nature, "nature");
		Util.ensureContainsNoNull(colSpecs, "colSpecs");
		Util.ensureNotEmpty(colSpecs, "colSpecs");

		_name = name;
		_nature = nature;
		_colSpecs = new ArrayList<ColumnSpec>(colSpecs);

		Integer fmtIdx = null;
		_labels = new String[colSpecs.size()];
		_relativeWidths = new int[colSpecs.size()];
		for (int i = 0; i < colSpecs.size(); ++i) {
			ColumnSpec cs = colSpecs.get(i);
			_labels[i] = cs.getLabel();
			_relativeWidths[i] = cs.getRelWidth();
			if (cs.isFormatted()) {
				if (fmtIdx != null) {
					throw new IllegalArgumentException("Column " + fmtIdx
							+ " already marked as formatted.");
				}
				fmtIdx = Integer.valueOf(i);
			}
		}
		_fmtIdx = fmtIdx;
	}

	// =============================== API ===================================

	/** Returns name of this table type. */
	public String getName() {
		return _name;
	}

	/** Returns name of this table type. */
	public Nature getNature() {
		return _nature;
	}

	/**
	 * Returns the index of the column that may be formatted, null if no column needs formatting.
	 * <p>
	 * Note that this is just the specification about the data, but the actual formatting needs to
	 * be enabled by the application (according to e.g. configuration), and then processed as
	 * desired.
	 */
	public Integer getFmtIdx() {
		return _fmtIdx;
	}

	/** Returns the number of columns described with this instance. */
	public int colCount() {
		return _colSpecs.size();
	}

	/**
	 * Returns (cloned) labels of columns for the table; if the table does not have a name, this may
	 * be used as the first and only heading row, otherwise this will be the second heading row.
	 */
	public String[] getLabels() {
		return _labels.clone();
	}

	/**
	 * Returns (cloned) widths of columns in percentage of the full table width. It is up to the
	 * implementation to ensure that the sum of values does not exceed 100.
	 */
	public int[] getRelativeWidths() {
		return _relativeWidths.clone();
	}

	/** Returns (unmodifiable) list of its column specs. */
	public List<ColumnSpec> getColSpecs() {
		return Collections.unmodifiableList(_colSpecs);
	}
}
