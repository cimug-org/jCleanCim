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

package org.tanjakostic.jcleancim.docgen.collector;

import java.util.Map;

import org.tanjakostic.jcleancim.util.Util;

/**
 * Column specification.
 *
 * @author tatjana.kostic@ieee.org
 * @version $Id: ColumnSpec.java 21 2019-08-12 15:44:50Z dev978 $
 */
public class ColumnSpec implements RawData {
	public static final String DEFVAL_TAG = "Col";

	private final String _tag;
	private final String _attrTag;
	private final String _label;
	private final String _docID;
	private final int _relWidth;
	private final boolean _formatted;

	private final RawData _rawData = new RawDataImpl();

	public static ColumnSpec createFmted(int relWidth, String attrTag, String docID, String label) {
		return new ColumnSpec(DEFVAL_TAG, attrTag, label, docID, relWidth, true);
	}

	public static ColumnSpec createUnfmted(int relWidth, String attrTag, String docID, String label) {
		return new ColumnSpec(DEFVAL_TAG, attrTag, label, docID, relWidth, false);
	}

	/**
	 * Constructor; allows to specify any <code>tag</code> name.
	 *
	 * @param tag
	 * @param attrTag
	 * @param label
	 * @param docID
	 * @param relativeWidth
	 * @param formatted
	 */
	public ColumnSpec(String tag, String attrTag, String label, String docID, int relativeWidth,
			boolean formatted) {
		_tag = Util.hasContent(tag) ? tag : DEFVAL_TAG;
		_attrTag = attrTag;
		_label = label;
		_docID = docID;
		_relWidth = relativeWidth;
		_formatted = formatted;

		// XML data:
		putCell(WAX.LOC_tag, _tag);
		putCell("attributeName", attrTag);
		putCell(WAX.A_text, label);
		putCell(WAX.A_textID, docID);
	}

	/** Word label = XML doc (translatable) */
	public final String getLabel() {
		return _label;
	}

	/** docID for label (XML only) */
	public String getDocID() {
		return _docID;
	}

	/** XML element name */
	public final String getTag() {
		return _tag;
	}

	/** name of XML attribute printed in this column */
	public String getAttrTag() {
		return _attrTag;
	}

	/** relative columns width; useful for Word only (not printed in XML) */
	public final int getRelWidth() {
		return _relWidth;
	}

	/** if true, the instance data content may be formatted (and is translatable) */
	public boolean isFormatted() {
		return _formatted;
	}

	// ===== Impl. of org.tanjakostic.jcleancim.docgen.collector.RawData methods =====

	@Override
	public final String putCell(String key, String value) {
		return _rawData.putCell(key, value);
	}

	@Override
	public final String putCellNonEmpty(String key, String value) {
		return _rawData.putCellNonEmpty(key, value);
	}

	@Override
	public final String copyCell(RawData src, String key) {
		return _rawData.copyCell(src, key);
	}

	@Override
	public final String copyNonEmptyCell(RawData src, String key) {
		return _rawData.copyNonEmptyCell(src, key);
	}

	@Override
	public final boolean hasKey(String key) {
		return _rawData.hasKey(key);
	}

	@Override
	public final Map<String, String> getCells() {
		return _rawData.getCells();
	}

	@Override
	public final String getCell(String key) {
		return _rawData.getCell(key);
	}
}
