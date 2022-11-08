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

import org.tanjakostic.jcleancim.util.Util;

/**
 * Record (table row) representation of an object.
 *
 * @author tatjana.kostic@ieee.org
 * @version $Id: EntryDoc.java 21 2019-08-12 15:44:50Z dev978 $
 */
public interface EntryDoc extends RawData {

	/**
	 * Describes the kind of an entry, to facilitate document generation formatting.
	 *
	 * @author tatjana.kostic@ieee.org
	 * @version $Id: EntryDoc.java 21 2019-08-12 15:44:50Z dev978 $
	 */
	public enum Kind {

		/**
		 * For IEC61850, we need to sometimes print fancy tables, with the very first heading row
		 * containing the name of the table. An entry of this kind represents the row that is the
		 * table name, so the first value contains that name and the remaining values are empty
		 * strings.
		 * <p>
		 * When printing a row from this entry, the cells of the table need to be merged, the
		 * shadding needs to be applied, the style of the row needs to be set to column heading
		 * style, and the row needs to be set as heading row.
		 */
		tableName,

		/**
		 * An entry of this kind represents the row that contains labels for columns of the table.
		 * <p>
		 * When printing a row from this entry, the shadding needs to be applied, the style of the
		 * row needs to be set to column heading style, and the row needs to be set as heading row.
		 */
		columnLabels,

		/**
		 * For IEC61850, we need to sometimes print fancy tables, with the attributes categorised
		 * into groups. Each such group has a name and is followed by entries that describe
		 * attributes. An entry of this kind represents the row that is a sub-head of the group, so
		 * the first value contains that name and the remaining values are empty strings.
		 * <p>
		 * When printing a row from this entry, the cells of the table need to be merged, the
		 * shadding needs to be applied, and the style of the row needs to be set to column heading
		 * style (but not as heading row).
		 */
		groupSubhead,

		/**
		 * An entry of this kind represents the row with data for properties of a class, or an
		 * arbitrary set of values. One of the values (typically: description) may contain
		 * formatting. If the configuration requires to respect formatting, it needs to be processed
		 * appropriately before actual printing.
		 */
		data
	}

	/** Separator of "cells" for poor-man printing in e.g. toString() method. */
	public static final String SEPARATOR = " | ";

	/** Returns array of values for columns (i.e., row content). */
	public String[] getValues();

	/** Returns the kind of this entry; useful for formatting. */
	public EntryDoc.Kind getKind();

	/** Returns non-null instance if this is {@link EntryDoc.Kind#groupSubhead}, null otherwise. */
	public AGSpec getAttrGroupSpec();

	/**
	 * Returns format information about the formattable cell at index <code>j</code>, null if this
	 * entry has no formattable cell, or if the formattable cell is actually not formatted.
	 */
	public FormatInfo getFormatInfo();

	/** Returns (potentially null) bookmark ID for referenceable items, e.g., enum literals. */
	public String getBookmarkID();

	/**
	 * Returns a single string of comma-separated items in a row, and {@link Util#NL} between the
	 * rows. The last character is <i>not</i> {@link Util#NL}, but the last value in the last cell.
	 */
	public String toCsv();
}
