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

package org.tanjakostic.jcleancim.docgen.writer;

import java.util.Map;
import java.util.Set;

/**
 * Interface to be implemented by all the UML documentation writers.
 *
 * @author tatjana.kostic@ieee.org
 * @version $Id: Writer.java 21 2019-08-12 15:44:50Z dev978 $
 */
public interface Writer {

	/** Name for custom document property holding the application version. */
	public static final String TOOL_CUSTOM_DOC_PROP = "jCleanCim";

	/** Name for custom document property holding the UML model file name. */
	public static final String UML_CUSTOM_DOC_PROP = "uml";

	/**
	 * Returns input used for writing. In addition to the actual UML model documentation, the input
	 * contains also the required configuration options.
	 */
	public WriterInput getInput();

	/** Return names of one or more input files used by this writer. */
	public String getInputFileNames();

	/** Return names of one or more output files created by this writer. */
	public String getOutputFileNames();

	/** Returns the set of supported formats, as file extensions; e.g., ".doc", ".xml". */
	public Set<String> getSupportedFormats();

	/**
	 * Returns (potentially empty) custom document properties. These may be useful to trace
	 * meta-information, such as application name and version, the source kind and version, etc.
	 */
	public Map<String, String> getDocumentMetadata();

	/**
	 * Writes the content from input.
	 */
	public void write();
}
