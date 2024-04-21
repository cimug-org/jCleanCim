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

package org.tanjakostic.jcleancim.docgen.collector.impl.ag;

import java.util.LinkedHashSet;
import java.util.Set;

import org.tanjakostic.jcleancim.docgen.collector.WAX;
import org.tanjakostic.jcleancim.model.UML;
import org.tanjakostic.jcleancim.util.Util;

/**
 * Group of data objects (of a logical node) that needs to be printed for IEC61850-7-4 table.
 * <p>
 * Note: We could put these somehow into UML, e.g. with tag 'category' or 'group', but this will
 * just increase burden on model editor. It is much easier to have the simple rule here, so the
 * editor need not worry about adding tags for new classes.
 *
 * @author tatjana.kostic@ieee.org
 * @version $Id: LNDataObjectGroupKind.java 21 2019-08-12 15:44:50Z dev978 $
 */
enum LNDataObjectGroupKind implements CategoryKind {

	DESCRIPTION(WAX.CAT_doDesc, UML.CDCDescription, ""),

	STATUS(WAX.CAT_doStatus, UML.CDCStatusInfo,
			(UML.ENS + "," + UML.ERY + "," + UML.SPS + "," + UML.ACT)),

	MEAS(WAX.CAT_doMeas, UML.CDCAnalogueInfo, ""),

	CONTROL(WAX.CAT_doControl, UML.CDCControl, (UML.ENC + "," + UML.SPC)),

	SETTING(WAX.CAT_doSetting, (UML.CDCStatusSet + "," + UML.CDCAnalogueSet), UML.ENG),

	TRACKING(WAX.CAT_doTracking, UML.CDCServiceTracking, UML.CTS),

	NULL(WAX.CAT_doNull, "", "");

	private final String _kindTag;
	private final Set<String> _packageNames;
	private final Set<String> _cdcSuperNames;

	private LNDataObjectGroupKind(String name, String cdcPackageNames, String cdcSuperNames) {
		_kindTag = name;
		_packageNames = new LinkedHashSet<String>(Util.splitCommaSeparatedTokens(cdcPackageNames));
		_cdcSuperNames = new LinkedHashSet<String>(Util.splitCommaSeparatedTokens(cdcSuperNames));
	}

	@Override
	public String getKindTag() {
		return _kindTag;
	}

	@Override
	public Set<String> getTypesPackageNames() {
		return _packageNames;
	}

	@Override
	public Set<String> getTypesSuperclassNames() {
		return _cdcSuperNames;
	}

	@Override
	public String toString() {
		return _kindTag + "=" + _packageNames.toString() + "; " + _cdcSuperNames;
	}
}
