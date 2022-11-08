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

package org.tanjakostic.jcleancim.common;

/**
 * Nature of the UML object, determining the modelling and validation rules to apply.
 * <p>
 * Implementation note: We keep this class in the common package instead of model package because
 * {@link OwningWg} depends on it.
 *
 * @author tatjana.kostic@ieee.org
 * @version $Id: Nature.java 31 2019-12-08 01:19:54Z dev978 $
 */
public enum Nature {
	/** Canonical CIM domain. */
	CIM,

	/** Domain of CIM profiles. */
	// CIM_PROFILE,

	/** Pure IEC 61850 domain. */
	IEC61850;

	public static boolean isAnyCim(Nature nature) {
		return (CIM == nature /* || CIM_PROFILE == nature */);
	}

	public static boolean isIec61850(Nature nature) {
		return (IEC61850 == nature);
	}
}
