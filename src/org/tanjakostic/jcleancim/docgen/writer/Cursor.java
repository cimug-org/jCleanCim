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

package org.tanjakostic.jcleancim.docgen.writer;

import org.tanjakostic.jcleancim.util.Util;

/**
 * Simple association of placeholder and range.
 *
 * @param <O>
 *            technology-specific type to access range object.
 * @author tatjana.kostic@ieee.org
 * @version $Id: Cursor.java 21 2019-08-12 15:44:50Z dev978 $
 */
public class Cursor<O> {

	private final Placeholder _placeholder;
	private final Range<O> _range;

	public Cursor(Placeholder placeholder, Range<O> range) {
		Util.ensureNotNull(placeholder, "placeholder");
		Util.ensureNotNull(range, "range");

		_placeholder = placeholder;
		_range = range;
	}

	public final Placeholder getPlaceholder() {
		return _placeholder;
	}

	public Range<O> getRange() {
		return _range;
	}

	@Override
	public String toString() {
		return String.format("%s %s", getRange().toString(), getPlaceholder().toString());
	}
}
