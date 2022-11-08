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
 * Kind for model builder.
 *
 * @author tatjana.kostic@ieee.org
 * @version $Id: ModelBuilderKind.java 21 2019-08-12 15:44:50Z dev978 $
 */
public enum ModelBuilderKind {
	/** Reads an Access DB, jet3.5 format as read-only. */
	db("Access DB"),

	/** Uses EA Java API to open repository and do bulk queries instead of iterations. */
	sqlxml("SQL + XML"),

	/** Original implementation (extremely slow EA API). */
	japi("EA Java API + ea.dll");

	private final String _text;

	ModelBuilderKind(String text) {
		_text = text;
	}

	public String getText() {
		return _text;
	}
}
