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

package org.tanjakostic.jcleancim.builder.ea.db;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;
import org.tanjakostic.jcleancim.builder.ea.EaSelector;
import org.tanjakostic.jcleancim.util.ApplicationException;

/**
 * EA repository supports a method to perform an SQL query and return the result set as XML. This
 * class is a wrapper to that EA functionality without dependency on EA.
 *
 * @author tatjana.kostic@ieee.org
 * @version $Id: DbSelector.java 21 2019-08-12 15:44:50Z dev978 $
 */
class DbSelector implements EaSelector {
	private static final Logger _logger = Logger.getLogger(DbSelector.class.getName());

	private final DbRepo _repo;

	public DbSelector(DbRepo repo) {
		_repo = repo;
	}

	// ===== Impl. of org.tanjakostic.jcleancim.builder.ea.EaSelector methods =====

	@Override
	public List<Map<String, String>> select(String tableName, String[] columnNames,
			boolean skipTiming) throws ApplicationException {

		long start = System.currentTimeMillis();
		_logger.info("loading table " + tableName);

		List<Map<String, String>> result = new ArrayList<Map<String, String>>();
		Set<String> tags = new HashSet<String>(Arrays.asList(columnNames));

		Iterable<Map<String, Object>> table = _repo.getTable(tableName);
		for (Map<String, Object> dbRow : table) {
			Map<String, String> row = new HashMap<String, String>();
			result.add(row);
			for (String colName : tags) {
				Object obj = dbRow.get(colName);
				String value = (obj == null) ? "" : obj.toString();
				row.put(colName, value);
			}
		}

		String time = skipTiming ? "" : ((System.currentTimeMillis() - start) + " ms: ");
		_logger.info(time + "populated " + result.size() + " items with tags: " + tags);
		_logger.info("..........");
		return result;
	}
}
