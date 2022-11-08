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

import org.tanjakostic.jcleancim.docgen.writer.Range;
import org.tanjakostic.jcleancim.docgen.writer.word.WordPatternFinder;

import com.jacob.com.Dispatch;

/**
 * @author tatjana.kostic@ieee.org
 * @version $Id: DocWordPatternFinder.java 26 2019-11-12 18:50:35Z dev978 $
 */
class DocWordPatternFinder implements WordPatternFinder<Dispatch> {
	Dispatch _find;
	Range<Dispatch> _range;

	DocWordPatternFinder(Dispatch doc, String msPattern) {
		Dispatch docContent = Dispatch.get(doc, "Content").toDispatch();

		_find = Dispatch.call(docContent, "Find").toDispatch();
		Dispatch.call(_find, "ClearFormatting");
		Dispatch.put(_find, "Text", DocWordHelper.vString(msPattern));
		Dispatch.put(_find, "MatchWildcards", DocWordHelper.getVariant(true));
		Dispatch.put(_find, "Forward", DocWordHelper.getVariant(true));

		_range = new DocWordRange(docContent);
	}

	@Override
	public Range<Dispatch> getRange() {
		return _range;
	}

	@Override
	public boolean hasMore() {
		return Dispatch.call(_find, "Execute").getBoolean();
	}
}
