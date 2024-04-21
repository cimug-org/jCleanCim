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

import java.util.List;

import org.tanjakostic.jcleancim.model.TextDescription.TextKind;

/**
 * Set of methods to allow table generation for constituents.
 * <p>
 * Here the layout you may want to use:
 *
 * <pre>
 *   [Table # ] + getIntroText()
 *   [Table # - ] + getCaptionText()
 *   // create table of size getColumnCount() x getRowCount()
 *   if getTableName() non null // merge cells (the first head row) and print
 *   getColumnNames()           // print regular table head
 *   getCellValues()            // loop [i,j]. In row i: if isRowGroupSubhead() then merge cells
 * </pre>
 * <p>
 * For IEC 61850 tables, we need to support a "table title", and thus 2 heading rows, with both
 * table title/name and actual column names. That row is present if {@link #getTableName()} returns
 * non-null value.
 * <p>
 * The method {@link #getEntryDocs()} provides the list of individual entries, in case you need
 * formatting other than table.
 *
 * @author tatjana.kostic@ieee.org
 * @version $Id: PropertiesDoc.java 21 2019-08-12 15:44:50Z dev978 $
 */
public interface PropertiesDoc extends ObjectDoc {

	/** Text to use to indicate inherited members. */
	public static final String INHERITED_FROM = "inherited from: ";

	/**
	 * Returns whether there are any non-heading entries ({@link EntryDoc.Kind#groupSubhead} or
	 * {@link EntryDoc.Kind#data}) in this instance. Use this one to determine whether to print
	 * table at all.
	 */
	public boolean notEmpty();

	/**
	 * Returns text that will introduce the table (e.g. "Table 23 <b>[introText]</b>").
	 */
	public String getIntroText();

	/**
	 * Returns text that describes the caption (e.g., "Table 23 - <b>[captionText]</b>").
	 */
	public String getCaptionText();

	/**
	 * Returns the number of header entries ({@link EntryDoc.Kind#tableName} or
	 * {@link EntryDoc.Kind#columnLabels}) in this instance.
	 */
	public int getHeadingEntriesCount();

	/**
	 * Returns (unmodifiable list of) all entries.
	 */
	List<? extends EntryDoc> getEntryDocs();

	/**
	 * Returns (unmodifiable list of) data entries only.
	 */
	List<? extends EntryDoc> getDataEntryDocs();

	/**
	 * Returns the name of the table; this will be the first heading row, when non-null.
	 */
	public String getTableName();

	/**
	 * Returns columns specification. If {@link #getTableName()} is null, labels from the columns
	 * spec (or the corresponding entry) will be the first and only heading row, otherwise the
	 * second heading row.
	 */
	public TableSpec getTableSpec();

	/**
	 * Returns number of rows for the table.
	 */
	public int getRowCount();

	/**
	 * Returns number of columns for the table; must be same as {@link TableSpec#colCount()} from
	 * the columns spec returned by {@link #getTableSpec()}.
	 */
	public int getColumnCount();

	/**
	 * Returns the full table values, including heading rows (table name and column names), any
	 * potential sub-head and the actual rows with values. This is suitable to have uniform access
	 * for matrix-like kind of access.
	 */
	public String[][] getCellValues();

	/**
	 * Returns kinds for rows from {@link #getCellValues()}.
	 */
	public EntryDoc.Kind[] getRowKinds();

	/**
	 * Returns array of applied formattings for the formattable cells in every row <code>i</code>.
	 * If the formattable cell in a row contains no formatting at all, returns null at index
	 * <code>i</code>. If no row contains formatting, returns null.
	 * <p>
	 * The formattable column index <code>j</code> is always the same for this table, and can be
	 * obtained from {@link TableSpec#getFmtIdx()}. This helps optimise writing content in a format
	 * other than raw text.
	 */
	public TextKind[] getFormats();

	/** Returns array of bookmark IDs for every row <code>i</code>, with potential null entries. */
	public String[] getBookmarkIDs();
}
