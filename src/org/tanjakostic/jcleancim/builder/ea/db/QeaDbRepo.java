/**
 * Copyright (C) 2009-2024 Tatjana (Tanja) Kostic
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

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.tanjakostic.jcleancim.util.ApplicationException;

/**
 * The QeaDbRepo is the SQLite 64-bit implementation for reading in EA 16.x .QEA
 * and QEAX files. It is the fastest builder of our in-memory model from EA. It
 * is based on the open source library that allows reading MS Access file in an
 * OS-independent way, and independently of EA API.
 * <p>
 * <b>Limitation:</b> Note that with this implementation we don't have access to
 * the EA repository (API) methods, so we cannot export diagrams or XMI -
 * although we do provide "empty" exporters, so that this implementation can
 * hook into the existing framework.
 * <p>
 * This implementation should be used for very fast {edit UML - validate}
 * cycles. When you need to produce a UML release (with XMI) and/or generate any
 * kind of documentation with diagrams, ensure you swap this implementation with
 * the one that can export XMI and diagrams.
 *
 * @author todd.viegut@gmail.com
 * @version $Id: QeaDbRepo.java 21 2024-04-21 15:44:50Z dev978 $
 */
class QeaDbRepo extends AbstractDbRepo {

	private Connection connection;
	private Statement statement;

	public QeaDbRepo(String modelFileAbsPath) {
		super(modelFileAbsPath);
	}

	@Override
	public String getVersion() {
		return "n/a";
	}

	@Override
	public void open() throws ApplicationException {
		// NOTE: Connection and Statement are AutoClosable. Don't forget to close them
		// both in order to avoid leaks.
		try {
			// create a database connection
			connection = DriverManager.getConnection("jdbc:sqlite:" + file.getAbsolutePath());
			statement = connection.createStatement();
			if (statement != null) {
				statement.setQueryTimeout(30); // set timeout to 60 sec.
			}
		} catch (SQLException e) {
			// If the error message is "out of memory", it typically
			// indicates the specified SQLite database file was not found
			throw new ApplicationException(
					"Failed to open SQLite file (EA repository) '" + file.getAbsolutePath() + "'.", e);
		}
	}

	@Override
	public void close() throws ApplicationException {
		try {
			if (statement != null)
				statement.close();
			if (connection != null)
				connection.close();
		} catch (SQLException e) {
			throw new ApplicationException("Failed to close SQLite file (EA repository).", e);
		}
	}

	@Override
	public Iterable<Map<String, Object>> getTable(String tableName) throws ApplicationException {
		try {
			List<Map<String, Object>> result = new ArrayList<Map<String, Object>>();
			Map<String, Object> row = null;

			String query = "select * from " + tableName;
			ResultSet resultSet = statement.executeQuery(query);

			// Get ResultSet metadata
			ResultSetMetaData metaData = resultSet.getMetaData();
			int columnCount = metaData.getColumnCount();

			// Iterate over the ResultSet and retrieve metadata for each column
			while (resultSet.next()) {
				row = new HashMap<String, Object>();
				for (int i = 1; i <= columnCount; i++) {
					String columnName = metaData.getColumnName(i);
					Object columnValue = resultSet.getObject(i);
					row.put(columnName, columnValue);
				}
				result.add(row);
			}
			return result;
		} catch (SQLException e) {
			throw new ApplicationException("Cannot find table '" + tableName + "' in '" + file.getAbsolutePath() + "'",
					e);
		}
	}
}
