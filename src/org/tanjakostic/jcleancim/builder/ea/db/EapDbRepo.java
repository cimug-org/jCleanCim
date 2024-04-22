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

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.tanjakostic.jcleancim.util.ApplicationException;

import com.healthmarketscience.jackcess.Database;
import com.healthmarketscience.jackcess.DatabaseBuilder;
import com.healthmarketscience.jackcess.Table;

/**
 * The EapDbRepo is the fastest builder of our in-memory model from EA. It is
 * based on the Jackcess open source library that allows reading MS Access file
 * in an OS-independent way, and independently of EA API.
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
 * @author tatjana.kostic@ieee.org
 * @version $Id: EapDbRepo.java 21 2024-04-21 15:44:50Z dev978 $
 */
class EapDbRepo extends AbstractDbRepo {

	private Database _db;

	public EapDbRepo(String modelFileAbsPath) {
		super(modelFileAbsPath);
	}

	@Override
	public String getVersion() {
		return "n/a";
	}

	@Override
	public void open() throws ApplicationException {
		try {
			DatabaseBuilder dbBuilder = new DatabaseBuilder().setReadOnly(true).setFile(file);
			_db = dbBuilder.open();
		} catch (Exception e) {
			throw new ApplicationException(
					"Failed to open Access file (EA repository) '" + file.getAbsolutePath() + "'.", e);
		}
	}

	@Override
	public void close() throws ApplicationException {
		try {
			_db.close();
		} catch (Exception e) {
			throw new ApplicationException("Failed to close Access file (EA repository).", e);
		} finally {
			_db = null;
		}
	}

	@Override
	public Iterable<Map<String, Object>> getTable(String tableName) throws ApplicationException {
		try {
			List<Map<String, Object>> result = new ArrayList<Map<String, Object>>();

			Table table = _db.getTable(tableName);
			for (Map<String, Object> dbRow : table) {
				result.add(dbRow);
			}

			return result;
		} catch (IOException e) {
			throw new ApplicationException("Cannot find table '" + tableName + "' in '" + _db.getFile() + "'", e);
		}
	}
}
