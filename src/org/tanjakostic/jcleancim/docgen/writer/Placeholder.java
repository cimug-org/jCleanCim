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

import org.tanjakostic.jcleancim.docgen.collector.FreeFormDocumentation;
import org.tanjakostic.jcleancim.docgen.collector.PlaceholderSpec;

/**
 * When using {@link FreeFormDocumentation}, templates for doc generation have to use labels to
 * indicate where to insert the documentation of what element of the UML model into the output
 * document. The format and the read-only content of the placeholder is defined in
 * {@link PlaceholderSpec}, while the placeholder itself is used for writing and actual replacing of
 * the placeholder text.
 *
 * @author tatjana.kostic@ieee.org
 * @version $Id: Placeholder.java 26 2019-11-12 18:50:35Z dev978 $
 */
public class Placeholder {

	private final PlaceholderSpec _phSpec;

	private String _replacedText;
	private int _figureCountBefore;
	private int _tableCountBefore;
	private int _myFigureCount;
	private int _myTableCount;

	public Placeholder(PlaceholderSpec phSpec) {
		this(phSpec, 0, 0);
	}

	public Placeholder(PlaceholderSpec phSpec, int figureCountBefore, int tableCountBefore) {
		_phSpec = phSpec;
		_replacedText = null;
		_figureCountBefore = figureCountBefore;
		_tableCountBefore = tableCountBefore;
		_myFigureCount = 0;
		_myTableCount = 0;
	}

	// ----------------- API -----------------

	/**
	 * Returns the placeholder specification which holds text, kind etc. independent of writer.
	 */
	public PlaceholderSpec getPlaceholderSpec() {
		return _phSpec;
	}

	/**
	 * Use this setter to track progress and status of placeholder replacement.
	 */
	public void setReplacedText(String replacedText) {
		_replacedText = replacedText;
	}

	/**
	 * Returns number of figures with caption before this placeholder.
	 */
	public int getFigureCountBefore() {
		return _figureCountBefore;
	}

	/**
	 * Returns number of tables with caption before this placeholder.
	 */
	public int getTableCountBefore() {
		return _tableCountBefore;
	}

	/**
	 * Returns the replacement text, as set by the user with {@link #setReplacedText(String)}.
	 * Initial value is null and is never changed by this class itself.
	 */
	public String getReplacedText() {
		return _replacedText;
	}

	/**
	 * Increments the number of figures before this placeholder.
	 */
	public void incrementFigureBefore() {
		++_figureCountBefore;
	}

	/**
	 * Increments the number of tables before this placeholder.
	 */
	public void incrementTableBefore() {
		++_tableCountBefore;
	}

	/**
	 * Returns the index to be used to reference the added figure caption.
	 */
	public int addFigure() {
		++_myFigureCount;
		return getFigureCount();
	}

	/**
	 * Returns the last figure caption index by the current end of the range.
	 */
	public int getFigureCount() {
		return getFigureCountBefore() + _myFigureCount;
	}

	/**
	 * Returns the index to be used to reference the added table caption.
	 */
	public int addTable() {
		++_myTableCount;
		return getTableCount();
	}

	/**
	 * Returns the last table caption index by the current end of the range.
	 */
	public int getTableCount() {
		return getTableCountBefore() + _myTableCount;
	}

	@Override
	public String toString() {
		if (_phSpec.getErrorText() != null) {
			return _phSpec.getErrorText();
		}
		StringBuilder sb = new StringBuilder(_phSpec.toString());
		if (_figureCountBefore != 0 || _myFigureCount != 0) {
			sb.append(", figures (");
			if (_figureCountBefore != 0) {
				sb.append(_figureCountBefore + " before ");
			}
			if (_myFigureCount != 0) {
				sb.append(_myFigureCount + " mine");
			}
			sb.append(")");
		}
		if (_tableCountBefore != 0 || _myTableCount != 0) {
			sb.append(", tables (");
			if (_tableCountBefore != 0) {
				sb.append(_tableCountBefore + " before ");
			}
			if (_myTableCount != 0) {
				sb.append(_myTableCount + " mine");
			}
			sb.append(")");
		}
		return sb.toString();
	}
}
