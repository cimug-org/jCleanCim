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

import org.tanjakostic.jcleancim.util.Util;

/**
 * Specific to IEC 61850 tables for logical nodes and common data classes, this simple data
 * structure holds definition for a valid group.
 *
 * @author tatjana.kostic@ieee.org
 * @version $Id: AGSpec.java 21 2019-08-12 15:44:50Z dev978 $
 */
public class AGSpec implements RawData {

	/**  */
	private static final String SUB_DATA_OBJECT = "SubDataObject";

	/**  */
	private static final String DA_TEXT = "DataAttribute";

	/** Default value for element name when used for pretty strings. */
	public static final String DEFVAL_TAG = WAX.E_PS;

	/** Element name when used for instance DAs. */
	public static final String DA_CATEGORY = WAX.E_DACategory;
	// DA categories
	public static final AGSpec DA_UNDEFINED;
	public static final AGSpec DA_SDO;
	public static final AGSpec DA_STATUS;
	public static final AGSpec DA_MEAS;
	public static final AGSpec DA_CTL_MIRROR;
	public static final AGSpec DA_SUBSTITUTION;
	public static final AGSpec DA_SETTING;
	public static final AGSpec DA_TRACKING;
	public static final AGSpec DA_DESCRIPTION;
	public static final AGSpec DA_SPAR;

	/** Element name when used for instance DOs. */
	public static final String DO_CATEGORY = WAX.E_DOCategory;
	// DO categories
	public static final AGSpec DO_UNDEFINED;
	public static final AGSpec DO_DESCRIPTION;
	public static final AGSpec DO_STATUS;
	public static final AGSpec DO_MEAS;
	public static final AGSpec DO_CONTROL;
	public static final AGSpec DO_SETTING;
	public static final AGSpec DO_TRACKING;

	private final static Map<String, AGSpec> AG_SPECS;
	static {
		AG_SPECS = new LinkedHashMap<String, AGSpec>();

		DA_UNDEFINED = createSpecial(DA_CATEGORY, WAX.CAT_daNull, "$ERROR - undefined DA category$");
		DA_SDO = createSpecial(DA_CATEGORY, WAX.CAT_SDOs, SUB_DATA_OBJECT);
		DA_STATUS = create(DA_CATEGORY, WAX.CAT_daStatus, DA_TEXT + " for status");
		DA_MEAS = create(DA_CATEGORY, WAX.CAT_daMeas, DA_TEXT + " for measured attributes");
		DA_CTL_MIRROR = create(DA_CATEGORY, WAX.CAT_daCtlMirror, DA_TEXT + " for control mirror");
		DA_SUBSTITUTION = create(DA_CATEGORY, WAX.CAT_daSubst, DA_TEXT
				+ " for substitution and blocked");
		DA_SETTING = create(DA_CATEGORY, WAX.CAT_daSetting, DA_TEXT + " for setting");
		DA_TRACKING = create(DA_CATEGORY, WAX.CAT_daTracking, DA_TEXT + " for service tracking");
		DA_DESCRIPTION = create(DA_CATEGORY, WAX.CAT_daDesc, DA_TEXT
				+ " for configuration, description and extension");
		DA_SPAR = createSpecial(DA_CATEGORY, WAX.CAT_ServiceParameters,
				"Parameters for control services");

		DO_UNDEFINED = createSpecial(DO_CATEGORY, WAX.CAT_doNull, "$ERROR - undefined DO category$");
		DO_DESCRIPTION = create(DO_CATEGORY, WAX.CAT_doDesc, "Descriptions");
		DO_STATUS = create(DO_CATEGORY, WAX.CAT_doStatus, "Status information");
		DO_MEAS = create(DO_CATEGORY, WAX.CAT_doMeas, "Measured and metered values");
		DO_CONTROL = create(DO_CATEGORY, WAX.CAT_doControl, "Controls");
		DO_SETTING = create(DO_CATEGORY, WAX.CAT_doSetting, "Settings");
		DO_TRACKING = create(DO_CATEGORY, WAX.CAT_doTracking, "Control and access service tracking");
	}

	public static AGSpec create(String instTag, String kindTag, String subhead) {
		AGSpec result = new AGSpec(DEFVAL_TAG, kindTag, subhead, false, instTag);
		putPredefined(result);
		return result;
	}

	public static AGSpec createSpecial(String instTag, String kindTag, String subhead) {
		AGSpec result = new AGSpec(DEFVAL_TAG, kindTag, subhead, true, instTag);
		putPredefined(result);
		return result;
	}

	private static void putPredefined(AGSpec agSpec) {
		String key = agSpec.getKindTag();
		if (!AG_SPECS.containsKey(key)) {
			AG_SPECS.put(key, agSpec);
		}
	}

	/** Returns all the predefined table formats: key=instTag (da vs. do). */
	public static Map<String, AGSpec> getPredefinedAGSpecs() {
		return Collections.unmodifiableMap(AG_SPECS);
	}

	public static List<AGSpec> getForInstTag(String instTag) {
		List<AGSpec> result = new ArrayList<AGSpec>();
		for (Map.Entry<String, AGSpec> entry : AG_SPECS.entrySet()) {
			AGSpec val = entry.getValue();
			if (instTag.equals(val.getInstTag())) {
				result.add(val);
			}
		}
		return result;
	}

	// ----------------------------------------

	private final String _tag; // elem name when used for pretty string description
	private final String _kindTag;
	private final String _label;
	private final String _labelID;
	private final boolean _special;
	private final String _instTag; // elem name when used for instance description

	private final RawData _rawData = new RawDataImpl();

	private AGSpec(String tag, String kindTag, String label, boolean special, String instTag) {
		Util.ensureNotNull(kindTag, "kindTag");
		Util.ensureNotEmpty(label, "subhead");
		Util.ensureNotEmpty(instTag, WAX.LOC_instTag);

		_tag = Util.hasContent(tag) ? tag : AGSpec.DEFVAL_TAG;
		_kindTag = kindTag;
		_label = label;
		_labelID = kindTag + "Label";
		_special = special;
		_instTag = instTag;

		// XML data:
		putCell(WAX.LOC_tag, _tag);
		putCell(WAX.A_kind, kindTag);
		putCell(WAX.A_text, label);
		putCell(WAX.A_textID, _labelID);
		putCell(WAX.LOC_isSpecial, Boolean.toString(special));
		putCell(WAX.LOC_instTag, instTag);
	}

	/** Returns element name used as container for pretty string. */
	public String getTag() {
		return _tag;
	}

	/** Returns kind. */
	public String getKindTag() {
		return _kindTag;
	}

	/** Returns subhead, i.e., value to be printed (translatable string). */
	public String getSubhead() {
		return _label;
	}

	/** Returns subhead identification. */
	public String getSubheadId() {
		return _labelID;
	}

	/** Returns whether this group is somehow special. */
	public boolean isSpecial() {
		return _special;
	}

	/** Returns element name used as container for instance data (DO or FCDA category/group). */
	public String getInstTag() {
		return _instTag;
	}

	// ===== Impl. of org.tanjakostic.jcleancim.docgen.collector.RawData methods =====

	@Override
	public final String putCell(String key, String value) {
		return _rawData.putCell(key, value);
	}

	@Override
	public final String putCellNonEmpty(String key, String value) {
		return _rawData.putCellNonEmpty(key, value);
	}

	@Override
	public final String copyCell(RawData src, String key) {
		return _rawData.copyCell(src, key);
	}

	@Override
	public final String copyNonEmptyCell(RawData src, String key) {
		return _rawData.copyNonEmptyCell(src, key);
	}

	@Override
	public final boolean hasKey(String key) {
		return _rawData.hasKey(key);
	}

	@Override
	public final Map<String, String> getCells() {
		return _rawData.getCells();
	}

	@Override
	public final String getCell(String key) {
		return _rawData.getCell(key);
	}
}
