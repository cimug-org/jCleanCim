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

package org.tanjakostic.jcleancim.docgen.writer.word.docx;

import org.tanjakostic.jcleancim.docgen.writer.AbstractRange;
import org.tanjakostic.jcleancim.util.Util;

/**
 * Wrapper for MS Word range.
 *
 * @author tatjana.kostic@ieee.org
 * @version $Id: DocxWordRange.java 21 2019-08-12 15:44:50Z dev978 $
 */
class DocxWordRange extends AbstractRange<Object> {

	private final Object _obj;

	/**
	 * Constructor.
	 *
	 * @param obj
	 */
	public DocxWordRange(Object obj) {
		super();
		Util.ensureNotNull(obj, "obj");

		_obj = obj;
	}

	@Override
	public int getStart() {
		throw new RuntimeException("Not implemented yet.");
		// return Dispatch.get(_obj, "Start").getInt();
	}

	@Override
	public void setStart(int idx) {
		throw new RuntimeException("Not implemented yet.");
		// Dispatch.put(_obj, "Start", DocWordWriter.getVariant(idx));
	}

	@Override
	public int getEnd() {
		throw new RuntimeException("Not implemented yet.");
		// return Dispatch.get(_obj, "End").getInt();
	}

	@Override
	public void setEnd(int idx) {
		throw new RuntimeException("Not implemented yet.");
		// Dispatch.put(_obj, "End", DocWordWriter.getVariant(idx));
	}

	@Override
	public String getText() {
		// StringBuilder sb = new StringBuilder();

		throw new RuntimeException("Not implemented yet.");
		// return sb.toString();
	}

	@Override
	public void setText(String newText) {
		throw new RuntimeException("Not implemented yet.");
		// Dispatch.put(_obj, "Text", newText);
	}

	@Override
	public Object getObject() {
		return _obj;
	}
}
