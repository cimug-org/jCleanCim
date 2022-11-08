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

package org.tanjakostic.jcleancim.docgen;

import java.io.IOException;

import org.tanjakostic.jcleancim.common.Config;
import org.tanjakostic.jcleancim.docgen.collector.DocCollector;
import org.tanjakostic.jcleancim.docgen.collector.FreeFormDocumentation;
import org.tanjakostic.jcleancim.docgen.writer.Writer;
import org.tanjakostic.jcleancim.docgen.writer.word.WordWriterInput;
import org.tanjakostic.jcleancim.docgen.writer.word.doc.DocWordWriter;
import org.tanjakostic.jcleancim.docgen.writer.word.docx.DocxWordWriter;
import org.tanjakostic.jcleancim.docgen.writer.xml.WAXWriter;
import org.tanjakostic.jcleancim.docgen.writer.xml.WAXWriterInput;
import org.tanjakostic.jcleancim.util.Util;

/**
 * Factory class, allowing us to specify creation of concrete writers in one place and avoid
 * undesired dependencies.
 *
 * @author tatjana.kostic@ieee.org
 * @version $Id: WriterFactory.java 21 2019-08-12 15:44:50Z dev978 $
 */
public class WriterFactory {

	private WriterFactory() {
		// prevents creation
	}

	/**
	 * Creates writer initialised with <code>input</code> data.
	 *
	 * @throws UnsupportedInputFormatException
	 *             if the requested format (extension) of the input file(s) is not supported.
	 * @throws UnsupportedOutputFormatException
	 *             if the requested format (extension) of the output file(s) is not supported.
	 * @throws IOException
	 *             on any file system-related problem.
	 */
	public static Writer createWriter(Config cfg, DocCollector collector)
			throws UnsupportedInputFormatException, UnsupportedOutputFormatException, IOException {
		Util.ensureNotNull(cfg, "cfg");

		String retainedPath = "";

		// FIXME: see whether possible to refactor to something more elegant...

		String outXmlSpecPath = cfg.getDocgenXmlOutSpecFileAbsPath();
		if (outXmlSpecPath != null) {
			for (String ext : WAXWriter.FILE_EXTENSIONS) {
				if (outXmlSpecPath.endsWith(ext)) {
					WAXWriterInput input = new WAXWriterInput(cfg,
							collector.getFixedFormDocumentation());
					return new WAXWriter(input);
				}
			}
			retainedPath = outXmlSpecPath;
		}

		String outFilePath = cfg.getDocgenWordOutDocumentFileAbsPath();
		if (outFilePath != null) {
			String ext = Util.getFileExtensionWithDot(outFilePath);
			if (ext != null) {
				FreeFormDocumentation freeFormDocumentation = collector.getFreeFormDocumentation();
				WordWriterInput input = new WordWriterInput(cfg, freeFormDocumentation);
				if (cfg.isDocgenWordUseDocFormat() && DocWordWriter.FILE_EXTENSIONS.contains(ext)) {
					return new DocWordWriter(input);
				}
				if (DocxWordWriter.FILE_EXTENSIONS.contains(ext)) {
					return new DocxWordWriter(input);
				}
			}
			retainedPath = outFilePath;
		}

		throw new UnsupportedOutputFormatException(
				"Format (extension) of the output file '" + retainedPath + "' not supported.");
	}
}
