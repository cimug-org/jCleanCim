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
 * Caption for figure or table.
 *
 * @param <O>
 *            technology-specific type to access range object.
 * @author tatjana.kostic@ieee.org
 * @version $Id: Caption.java 26 2019-11-12 18:50:35Z dev978 $
 */
public class Caption<O> {

	private final CaptionKind _kind;
	private final Range<O> _range;

	/**
	 * Constructor.
	 *
	 * @param kind
	 */
	public Caption(CaptionKind kind, Range<O> range) {
		Util.ensureNotNull(kind, "kind");
		Util.ensureNotNull(range, "range");

		_kind = kind;
		_range = range;
	}

	public final CaptionKind getKind() {
		return _kind;
	}

	public Range<O> getRange() {
		return _range;
	}

	public Style getStyle() {
		return getKind().getStyle();
	}

	@Override
	public String toString() {
		return String.format(" %s: %s caption", getRange().toString(), getKind().getLabel());
	}
}
