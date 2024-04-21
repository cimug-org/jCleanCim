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

package org.tanjakostic.jcleancim.docgen.writer.word.doc;

import org.tanjakostic.jcleancim.docgen.writer.AbstractRange;
import org.tanjakostic.jcleancim.util.Util;

import com.jacob.com.Dispatch;

/**
 * Wrapper for MS Word range.
 *
 * @author tatjana.kostic@ieee.org
 * @version $Id: DocWordRange.java 26 2019-11-12 18:50:35Z dev978 $
 */
class DocWordRange extends AbstractRange<Dispatch> {

	private final Dispatch _obj;

	/**
	 * Constructor.
	 *
	 * @param obj
	 */
	public DocWordRange(Dispatch obj) {
		super();
		Util.ensureNotNull(obj, "obj");

		_obj = obj;
	}

	@Override
	public int getStart() {
		return Dispatch.get(_obj, "Start").getInt();
	}

	@Override
	public void setStart(int idx) {
		Dispatch.put(_obj, "Start", DocWordHelper.vInt(idx));
	}

	@Override
	public int getEnd() {
		return Dispatch.get(_obj, "End").getInt();
	}

	@Override
	public void setEnd(int idx) {
		Dispatch.put(_obj, "End", DocWordHelper.vInt(idx));
	}

	@Override
	public String getText() {
		return Dispatch.get(_obj, "Text").getString();
	}

	@Override
	public void setText(String newText) {
		Dispatch.put(_obj, "Text", newText);
	}

	@Override
	public Dispatch getObject() {
		return _obj;
	}
}
