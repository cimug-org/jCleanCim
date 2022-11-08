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

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;
import org.tanjakostic.jcleancim.util.Util;

/**
 * Common implementation for all writers.
 *
 * @author tatjana.kostic@ieee.org
 * @version $Id: AbstractWriter.java 21 2019-08-12 15:44:50Z dev978 $
 */
abstract public class AbstractWriter implements Writer {
	private static final Logger _logger = Logger.getLogger(AbstractWriter.class.getName());

	private final Map<String, String> _customDocProps = new HashMap<String, String>();

	/** Constructor. */
	protected AbstractWriter(WriterInput input) {
		_logger.info("creating " + getClass().getSimpleName());
		Util.ensureNotNull(input, "input");

		_customDocProps.put(Writer.UML_CUSTOM_DOC_PROP, input.getModelFileName());
		_customDocProps.put(Writer.TOOL_CUSTOM_DOC_PROP, input.getAppVersion());
	}

	@Override
	public final Map<String, String> getDocumentMetadata() {
		return Collections.unmodifiableMap(_customDocProps);
	}
}
