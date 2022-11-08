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

import java.io.File;
import java.io.IOException;

import org.tanjakostic.jcleancim.util.ApplicationException;

import com.healthmarketscience.jackcess.Database;
import com.healthmarketscience.jackcess.DatabaseBuilder;
import com.healthmarketscience.jackcess.Table;

/**
 * @author tatjana.kostic@ieee.org
 * @version $Id: DbRepo.java 21 2019-08-12 15:44:50Z dev978 $
 */
class DbRepo {

	private Database _db;

	/** Constructor. */
	public DbRepo() {
		// no-op
	}

	public String getVersion() {
		return "n/a";
	}

	public void open(String modelFileAbsPath) throws ApplicationException {
		try {
			DatabaseBuilder dbBuilder = new DatabaseBuilder().setReadOnly(true).setFile(
					new File(modelFileAbsPath));
			_db = dbBuilder.open();
		} catch (Exception e) {
			throw new ApplicationException("Failed to open Access file (EA repository) '"
					+ modelFileAbsPath + "'.", e);
		}
	}

	public void close() throws ApplicationException {
		try {
			_db.close();
		} catch (Exception e) {
			throw new ApplicationException("Failed to close Access file (EA repository).", e);
		} finally {
			_db = null;
		}
	}

	public Table getTable(String tableName) throws ApplicationException {
		try {
			return _db.getTable(tableName);
		} catch (IOException e) {
			throw new ApplicationException("Cannot find table '" + tableName + "' in '"
					+ _db.getFile() + "'", e);
		}
	}
}
