/**
 * Copyright (C) 2022-2026 UCA International Users Group and contributors
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
 * EAProjType defines an enum for all supported types of EA Project files. 
 *
 * @author tviegut@ucaiug.org
 * @version $Id: EAProjType.java 21 2024-09-11 15:44:50Z dev978 $
 */
public enum EAProjType {
	EAP, EAPX, QEA, QEAX;

	public static EAProjType toEAProjType(String extension) {
		extension = (extension != null ? extension.toUpperCase() : extension);
		EAProjType type;
		try {
			type = EAProjType.valueOf(extension);
		} catch (Exception e) {
			type = null;
		}
		return type;
	}

}
