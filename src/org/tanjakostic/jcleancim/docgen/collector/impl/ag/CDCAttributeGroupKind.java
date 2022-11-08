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

import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;

import org.tanjakostic.jcleancim.docgen.collector.WAX;
import org.tanjakostic.jcleancim.model.UML;
import org.tanjakostic.jcleancim.util.Util;

/**
 * Group of attributes (of a common data class) that needs to be printed for IEC61850-7-3 table.
 * <p>
 * Note: We could put these somehow into UML, e.g. with tag 'category' or 'group', but this will
 * just increase burden on model editor. It is much easier to have the simple rule here, so the
 * editor need not worry about adding tags for new classes.
 *
 * @author tatjana.kostic@ieee.org
 * @version $Id: CDCAttributeGroupKind.java 21 2019-08-12 15:44:50Z dev978 $
 */
enum CDCAttributeGroupKind implements CategoryKind {
	DATA(WAX.CAT_SDOs, UML.BasePrimitiveCDC),

	STATUS(WAX.CAT_daStatus, UML.FCDA_ST),

	MEAS(WAX.CAT_daMeas, UML.FCDA_MX),

	CTL_MIRROR(WAX.CAT_daCtlMirror, UML.FCDA_OR),

	SUBSTITUTION(WAX.CAT_daSubst, (UML.FCDA_SV + "," + UML.FCDA_BL)),

	SETTING(WAX.CAT_daSetting, (UML.FCDA_SP + "," + UML.FCDA_SE + "," + UML.FCDA_SG)),

	TRACKING(WAX.CAT_daTracking, UML.FCDA_SR),

	DESCRIPTION(WAX.CAT_daDesc, (UML.FCDA_CF + "," + UML.FCDA_DC + "," + UML.FCDA_EX)),

	CONTROL(WAX.CAT_ServiceParameters, UML.DA),

	NULL(WAX.CAT_daNull, "");

	private final String _kindTag;
	private final Set<String> _superclassNames;

	private CDCAttributeGroupKind(String name, String fcdaNames) {
		_kindTag = name;
		_superclassNames = new LinkedHashSet<String>(Util.splitCommaSeparatedTokens(fcdaNames));
	}

	@Override
	public String getKindTag() {
		return _kindTag;
	}

	@Override
	public Set<String> getTypesSuperclassNames() {
		return _superclassNames;
	}

	@Override
	public Set<String> getTypesPackageNames() {
		return Collections.emptySet();
	}

	@Override
	public String toString() {
		return _kindTag + "=" + _superclassNames.toString();
	}
}
