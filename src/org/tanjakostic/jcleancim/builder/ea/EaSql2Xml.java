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

/**
 * Isolates EA mechanism for SQL queries on the open repository (allows us to pass in a mock instead
 * of the EA repository for testing).
 *
 * @author tatjana.kostic@ieee.org
 * @version $Id: EaSql2Xml.java 21 2019-08-12 15:44:50Z dev978 $
 */
public interface EaSql2Xml {

	/** Returns result of the SQL <code>queryStatement</code> as EA XML. */
	public String sqlResultAsXml(String queryStatement);
}
