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

package org.tanjakostic.jcleancim.builder.ea;

import java.util.List;
import java.util.Map;

import org.tanjakostic.jcleancim.common.EAProjType;
import org.tanjakostic.jcleancim.util.ApplicationException;

/**
 * @author tatjana.kostic@ieee.org
 * @version $Id: EaSelector.java 21 2019-08-12 15:44:50Z dev978 $
 */
public interface EaSelector {

	/** Select <code>columnNames</code> from <code>tableName</code>. */
	public List<Map<String, String>> select(String tableName, String[] columnNames,
			boolean skipTiming) throws ApplicationException;

	/**
	 * This is important as it determines if the columns require special syntax 
	 * when building SQL queries. For example, EA 16.x .qea/.qeax project files  
	 * utilize SQLite as the underlying database. The SQLLite SQL dialect has  
	 * 'Constraint' and 'Default' as keywords. These keywords happen to also be 
	 * column names within the EA schema of a project file and therefore require 
	 * they appear in the SQL statement as:  [Constraint]
	 */
	default String[] convert(EAProjType type, String[] columnNames) {
		switch (type) {
		case QEA:
		case QEAX:
			for (int i = 0; i < columnNames.length; i++) {
				if (EA.ATTR_DEFAULT.equals(columnNames[i]) || EA.CLASS_CONSTR_NAME.equals(columnNames[i]))
					columnNames[i] = "[" + columnNames[i] + "]";
			}
			break;
		default:
			break;
		}
		return columnNames;
	}
}
