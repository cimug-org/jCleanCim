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

/**
 * Simple wrapper for a map of key/value pairs, used to store raw, non-formatted and non- modified
 * data from the UML object for applications that do not work with UML objects and that may need to
 * provide formatting for printing different than the default. It is useful to communicate disparate
 * type information required for document generation in some formats.
 *
 * @author tatjana.kostic@ieee.org
 * @version $Id: RawData.java 21 2019-08-12 15:44:50Z dev978 $
 */
public interface RawData {

	/**
	 * Adds the <code>value</code> for <code>key</code>.
	 *
	 * @param key
	 *            non-null, non-empty key.
	 * @param value
	 *            non-null, potentially empty value for the key.
	 * @return null on success, replaced value if it existed for the given key.
	 */
	public String putCell(String key, String value);

	/**
	 * Adds the <code>value</code> for <code>key</code> if <code>value</code> is not empty.
	 *
	 * @param key
	 *            non-null, non-empty key.
	 * @param value
	 *            non-null, potentially empty value for the key.
	 * @return FIXME
	 */
	public String putCellNonEmpty(String key, String value);

	/**
	 * Copies value for <code>key</code> existing in <code>src</code>, into this provider; no-op if
	 * <code>src</code> does not contain the <code>key</code>.
	 */
	public String copyCell(RawData src, String key);

	/**
	 * Copies non-empty value for <code>key</code> existing in <code>src</code>, into this provider;
	 * no-op if <code>src</code> does not contain the <code>key</code>, or if it contains the
	 * <code>key</code> but the value for that key is empty.
	 */
	public String copyNonEmptyCell(RawData src, String key);

	/**
	 * If true, <code>key</code> is present.
	 */
	public boolean hasKey(String key);

	/**
	 * Returns potentially empty map of key/value pairs.
	 */
	public Map<String, String> getCells();

	/**
	 * Returns value for key, null if key does not exist or if <code>key</code> is null.
	 */
	public String getCell(String key);
}
