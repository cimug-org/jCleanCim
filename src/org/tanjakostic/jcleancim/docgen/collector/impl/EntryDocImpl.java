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

import java.util.Arrays;
import java.util.Collections;
import java.util.Map;

import org.tanjakostic.jcleancim.docgen.collector.AGSpec;
import org.tanjakostic.jcleancim.docgen.collector.EntryDoc;
import org.tanjakostic.jcleancim.docgen.collector.FormatInfo;
import org.tanjakostic.jcleancim.docgen.collector.RawData;
import org.tanjakostic.jcleancim.docgen.collector.RawDataImpl;
import org.tanjakostic.jcleancim.util.ProgrammerErrorException;
import org.tanjakostic.jcleancim.util.Util;

/**
 * Single property entry (row with values).
 *
 * @author tatjana.kostic@ieee.org
 * @version $Id: EntryDocImpl.java 31 2019-12-08 01:19:54Z dev978 $
 */
public class EntryDocImpl implements EntryDoc {

	private final String[] _data;
	private final Kind _kind;
	private final FormatInfo _formatInfo;
	private final RawData _rawData;
	private final AGSpec _agSpec;
	private final String _bookmarkID;

	/**
	 * Creates {@link org.tanjakostic.jcleancim.docgen.collector.EntryDoc.Kind#tableName} entry.
	 *
	 * @param name
	 *            non-null, (trimmed) non-empty name for the table.
	 * @param columnCount
	 *            (positive) number of columns in the table; used to fill values with empty content
	 *            except for the first item.
	 * @throws IllegalArgumentException
	 *             if <code>name</code> is empty, or if <code>columnCount</code> is not positive.
	 */
	public static EntryDoc createTableName(String name, int columnCount)
			throws IllegalArgumentException {
		return new EntryDocImpl(name, null, Kind.tableName, null, null, columnCount,
				(String[]) null);
	}

	/**
	 * Creates {@link org.tanjakostic.jcleancim.docgen.collector.EntryDoc.Kind#columnLabels} entry.
	 *
	 * @param values
	 *            non-empty array of non-null values.
	 * @throws IllegalArgumentException
	 *             if <code>values</code> is empty.
	 */
	public static EntryDoc createColumnLabels(String... values) throws IllegalArgumentException {
		int count = (values != null) ? values.length : 1; // we let unique ctor catch NPE
		return new EntryDocImpl(null, null, Kind.columnLabels, null, null, count, values);
	}

	/**
	 * Creates {@link org.tanjakostic.jcleancim.docgen.collector.EntryDoc.Kind#groupSubhead} entry.
	 *
	 * @param agSpec
	 *            non-null spec for the group subhead.
	 * @param columnCount
	 *            (positive) number of columns in the table; used to fill values with empty content
	 *            except for the first item.
	 * @throws IllegalArgumentException
	 *             if <code>name</code> is empty, or if <code>columnCount</code> is not positive.
	 */
	public static EntryDoc createGroupSubhead(AGSpec agSpec, int columnCount)
			throws IllegalArgumentException {
		String name = (agSpec != null) ? agSpec.getSubhead() : ""; // we let unique ctor catch NPE
		return new EntryDocImpl(name, agSpec, Kind.groupSubhead, null, null, columnCount,
				(String[]) null);
	}

	/**
	 * Creates regular entry with data without any formatting (also, no new line character).
	 *
	 * @param bookmarkID
	 *            (potentially null) bookmark ID.
	 * @param values
	 *            non-empty array of non-null values.
	 * @throws IllegalArgumentException
	 *             if <code>values</code> is empty.
	 */
	public static EntryDoc createUnformattedData(String bookmarkID, String... values)
			throws IllegalArgumentException {
		int count = (values != null) ? values.length : 1; // we let unique ctor catch NPE
		return new EntryDocImpl(null, null, Kind.data, null, bookmarkID, count, values);
	}

	/**
	 * Creates regular entry with data.
	 *
	 * @param bookmarkID
	 *            (potentially null) bookmark ID.
	 * @param formatInfo
	 *            when non-null, index of the <code>values</code> whose content needs to preserve
	 *            formatting when printed (this corresponds to a formatted column, e.g. for
	 *            description of items).
	 * @param values
	 *            non-empty array of non-null values.
	 * @throws IllegalArgumentException
	 *             if <code>values</code> is empty.
	 */
	public static EntryDocImpl createData(String bookmarkID, FormatInfo formatInfo,
			String... values) throws IllegalArgumentException {
		int count = (values != null) ? values.length : 1; // we let unique ctor catch NPE
		return new EntryDocImpl(null, null, Kind.data, formatInfo, bookmarkID, count, values);
	}

	private EntryDocImpl(String singleValue, AGSpec agSpec, Kind kind, FormatInfo formatInfo,
			String bookmarkID, int columnCount, String... values) {
		ensureValid(singleValue, agSpec, kind, formatInfo, bookmarkID, columnCount, values);

		_agSpec = agSpec;
		_kind = kind;
		_formatInfo = formatInfo;
		_bookmarkID = bookmarkID;

		if (values != null) {
			_data = values.clone();
		} else {
			_data = new String[columnCount];
			Arrays.fill(_data, "");
			_data[0] = singleValue;
		}
		_rawData = new RawDataImpl();
	}

	private void ensureValid(String name, AGSpec agSpec, Kind kind, FormatInfo formatInfo,
			String bookmarkID, int columnCount, String... values) {
		Util.ensureNotNull(kind, "kind");

		if (kind == Kind.groupSubhead) {
			Util.ensureNotNull(agSpec, "agSpec");
		}

		if (name != null) {
			Util.ensureNotEmpty(name.trim(), "name");
			if (values != null) {
				throw new ProgrammerErrorException("One of name or values must be null.");
			}
			if (formatInfo != null) {
				throw new ProgrammerErrorException("Formatting cannot apply to " + kind.toString()
						+ ".");
			}
			if (columnCount < 1) {
				throw new IllegalArgumentException(String.format("columnCount (%d) must be >0",
						Integer.valueOf(columnCount)));
			}
			return;
		}

		Util.ensureNotEmpty(values, "values for " + kind + "");
		Util.ensureContainsNoNull(values, "values");
		if (columnCount != values.length) {
			throw new ProgrammerErrorException("columnCount (" + columnCount + ") and values ("
					+ values.length + ") must have same size");
		}
		if (formatInfo != null) {
			Integer fmtIdx = formatInfo.getFormattedColumnIdx();
			if (fmtIdx != null) {
				if (fmtIdx.intValue() < 0 || fmtIdx.intValue() > columnCount - 1) {
					throw new ArrayIndexOutOfBoundsException(fmtIdx.intValue());
				}
			}
		}
	}

	@Override
	public String toString() {
		return Util.concatStringSeparatedTokens(EntryDoc.SEPARATOR, true, Arrays.asList(_data));
	}

	// ===== org.tanjakostic.jcleancim.docgen.collector.EntryDoc =====

	@Override
	public final String[] getValues() {
		return _data.clone();
	}

	@Override
	public Kind getKind() {
		return _kind;
	}

	@Override
	public AGSpec getAttrGroupSpec() {
		return _agSpec;
	}

	@Override
	public final FormatInfo getFormatInfo() {
		return _formatInfo;
	}

	@Override
	public String getBookmarkID() {
		return _bookmarkID;
	}

	@Override
	public final String toCsv() {
		return Util.concatStringSeparatedTokens(",", true, Arrays.asList(_data));
	}

	// ===== org.tanjakostic.jcleancim.docgen.collector.RawData =====

	@Override
	public String putCell(String key, String value) {
		return _rawData.putCell(key, value);
	}

	@Override
	public String copyCell(RawData src, String key) {
		return _rawData.copyCell(src, key);
	}

	@Override
	public String copyNonEmptyCell(RawData src, String key) {
		return _rawData.copyNonEmptyCell(src, key);
	}

	@Override
	public String putCellNonEmpty(String key, String value) {
		return _rawData.putCellNonEmpty(key, value);
	}

	@Override
	public boolean hasKey(String key) {
		return _rawData.hasKey(key);
	}

	@Override
	public Map<String, String> getCells() {
		return Collections.unmodifiableMap(_rawData.getCells());
	}

	@Override
	public String getCell(String key) {
		return _rawData.getCell(key);
	}
}
