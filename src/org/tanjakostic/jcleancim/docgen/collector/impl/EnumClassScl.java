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

package org.tanjakostic.jcleancim.docgen.collector.impl;

import java.util.ArrayList;
import java.util.List;

import org.tanjakostic.jcleancim.docgen.collector.ClassScl;
import org.tanjakostic.jcleancim.model.UmlAttribute;
import org.tanjakostic.jcleancim.model.UmlClass;
import org.tanjakostic.jcleancim.util.Util;

/**
 * Simple implementation of an enumeration element, with literals as subelements.
 *
 * @author tatjana.kostic@ieee.org
 * @version $Id: EnumClassScl.java 21 2019-08-12 15:44:50Z dev978 $
 */
class EnumClassScl implements ClassScl {
	private static final int INDENT_COUNT = 2;
	private final String _start;
	private final List<String> _data = new ArrayList<String>();
	private final String _end;

	private static final String ENUM_TYPE_EL = "EnumType";
	private static final String ID_ATT = "id";
	private static final String ENUM_VAL_EL = "EnumVal";
	private static final String ORD_ATT = "ord";

	// <EnumType id="AdjSt">
	// <EnumVal ord="1">Completed</EnumVal>
	// <EnumVal ord="2">Cancelled </EnumVal>
	// <EnumVal ord="3">New adjustments </EnumVal>
	// <EnumVal ord="4">Under way </EnumVal>
	// </EnumType>
	/**
	 * Constructor.
	 */
	EnumClassScl(UmlClass clazz) {
		_start = "<" + ENUM_TYPE_EL + " " + ID_ATT + "=\"" + clazz.getName() + "\">";
		for (UmlAttribute att : clazz.getAttributes()) {
			String name = att.displayEmptyValue() ? "" : att.getName();
			String literal = "<" + ENUM_VAL_EL + " " + ORD_ATT + "=\"" + att.getInitValue();
			literal += "\">" + name + "</" + ENUM_VAL_EL + ">";
			_data.add(literal);
		}
		_end = "</" + ENUM_TYPE_EL + ">";
	}

	// ===== Impl. of org.tanjakostic.jcleancim.docgen.collector.ClassScl methods =====

	@Override
	public String getStart() {
		return _start;
	}

	@Override
	public List<String> getData() {
		return _data;
	}

	@Override
	public String getEnd() {
		return _end;
	}

	@Override
	public String toXml(boolean prettyPrint) {
		StringBuilder sb = new StringBuilder();
		sb.append(getStart()).append(Util.NL);
		for (String literal : getData()) {
			sb.append(Util.getIndentSpaces(INDENT_COUNT)).append(literal).append(Util.NL);
		}
		sb.append(getEnd()).append(Util.NL);
		return sb.toString();
	}

	@Override
	public String toString() {
		return toXml(true);
	}
}
