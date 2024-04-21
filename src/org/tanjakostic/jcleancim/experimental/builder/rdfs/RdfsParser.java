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

package org.tanjakostic.jcleancim.experimental.builder.rdfs;

import java.io.File;
import java.io.IOException;

import org.apache.log4j.Logger;
import org.tanjakostic.jcleancim.util.Util;

/**
 * Parses CIM RDF Schema file (created from CIM UML model with CIMTool).
 *
 * @author tatjana.kostic@ieee.org
 * @version $Id: RdfsParser.java 21 2019-08-12 15:44:50Z dev978 $
 */
public class RdfsParser {
	private static final Logger _logger = Logger.getLogger(RdfsParser.class.getName());

	private final RdfsModel _model;

	/**
	 * Constructs an instance from the file with the CIM RDF Schema.
	 *
	 * @param rdfSchemaFile
	 *            input: the CIM RDF Schema file.
	 * @throws CimSchemaException
	 *             if file cannot be found.
	 */
	public RdfsParser(File rdfSchemaFile) throws CimSchemaException {
		if (rdfSchemaFile == null) {
			throw new NullPointerException("CIM RDF schema file is null.");
		}

		_logger.info("=== Loading '" + rdfSchemaFile.getAbsolutePath() + "' ...");
		XmlDocument rdfSchema = new XmlDocument(rdfSchemaFile);
		_logger.info("=== Loading: Done.");

		_logger.info("=== Building model ...");
		_model = new RdfsModel(rdfSchemaFile.getName());
		_model.build(rdfSchema);
		rdfSchema = null;
		_logger.info("=== Created model " + _model.getName() + ".");
	}

	public RdfsModel getModel() {
		return _model;
	}

	/**
	 * Saves the differences in the given file.
	 *
	 * @param diffFile
	 *            file where to store the differences.
	 * @param otherModel
	 *            other model.
	 * @throws IOException
	 */
	public void diffSchemas(File diffFile, RdfsModel otherModel) throws IOException {
		Util.ensureNotNull(diffFile, "diffFile");

		String diffsAsCSV = getModel().getDiffsAsCSV(otherModel);
		Util.saveToFile(diffFile.getAbsolutePath(), diffsAsCSV);
		_logger.info("Diffs stored in '" + diffFile.getAbsolutePath() + "'.");
	}
}
