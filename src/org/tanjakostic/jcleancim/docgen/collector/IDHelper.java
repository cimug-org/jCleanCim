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
 * Creates identifiers. Primary usage is for referencing items in documents.
 *
 * @author tatjana.kostic@ieee.org
 * @version $Id: IDHelper.java 21 2019-08-12 15:44:50Z dev978 $
 */
public class IDHelper {

	private static final IDHelper _INSTANCE = new IDHelper();
	private static final String _ID_FMT = "%s.%s.%s";

	private long _counter = 0;

	private IDHelper() {
		// prevents creation
	}

	/** Accessor to a singleton instance. */
	public static IDHelper instance() {
		return _INSTANCE;
	}

	private String increment() {
		return String.valueOf(++_counter);
	}

	/** For testing purposes only. */
	long reset() {
		_counter = 0;
		return getDocIDCounter();
	}

	/** For testing purposes only. */
	long getDocIDCounter() {
		return _counter;
	}

	/**
	 * Returns a concatenation of qualified object name and <code>ending</code>arguments combined
	 * with an internal counter.
	 *
	 * @param prefix
	 *            (if not null or trimmed and empty) prefix to start the ID with; typically, a
	 *            qualified name of an item for which to create a document identifier.
	 * @param ending
	 *            (if not null or trimmed and empty) suffix to append, to enhance human readability.
	 */
	public String createDocID(String prefix, String ending) {
		String tPrefix = Util.null2empty(prefix).trim();
		String tEnding = Util.null2empty(ending).trim();
		return String.format(_ID_FMT, tPrefix, increment(), tEnding);
	}
}
