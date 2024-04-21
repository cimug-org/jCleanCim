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

package org.tanjakostic.jcleancim.docgen.writer.xml;

import org.tanjakostic.jcleancim.common.Config;
import org.tanjakostic.jcleancim.docgen.collector.FixedFormDocumentation;
import org.tanjakostic.jcleancim.docgen.writer.WriterInput;
import org.tanjakostic.jcleancim.util.Util;

/**
 * @author tatjana.kostic@ieee.org
 * @version $Id: WAXWriterInput.java 21 2019-08-12 15:44:50Z dev978 $
 */
public class WAXWriterInput extends WriterInput {

	private final FixedFormDocumentation _fixedFormDocumentation;
	private final String _inXsdWebaccessPath;
	private final String _outXmlSpecPath;
	private final String _outXmlDocPath;
	private final String _outXsdWebaccessPath;

	/**
	 * Constructor.
	 *
	 * @param cfg
	 * @param fixedFormDocumentation
	 *            FIXME "flattened" map of package documentation instances, with package name as key
	 *            (to allow to quickly find the package name from what is read in the placeholder).
	 *            If null or empty, placeholders dealing with packages will all have error and empty
	 *            content.
	 */
	public WAXWriterInput(Config cfg, FixedFormDocumentation fixedFormDocumentation) {
		this(cfg, null, null, cfg.isAppSkipTiming(), fixedFormDocumentation, cfg
				.getDocgenXsdInWebaccessFileAbsPath(), cfg.getDocgenXmlOutSpecFileAbsPath(), cfg
				.getDocgenXmlOutDocFileAbsPath(), cfg.getDocgenXsdOutWebaccessFileAbsPath());
	}

	/**
	 * Constructor, useful to create an instance if you don't have configuration.
	 *
	 * @param appVersion
	 * @param modelFileName
	 * @param fixedFormDocumentation
	 *            scoped package docs, categorised by nature, then by name space.
	 * @param inXsdWebaccessPath
	 * @param outXmlSpecPath
	 * @param outXmlDocPath
	 * @param outXsdWebaccessPath
	 */
	public WAXWriterInput(String appVersion, String modelFileName, boolean skipTiming,
			FixedFormDocumentation fixedFormDocumentation, String inXsdWebaccessPath,
			String outXmlSpecPath, String outXmlDocPath, String outXsdWebaccessPath) {
		this(null, appVersion, modelFileName, skipTiming, fixedFormDocumentation,
				inXsdWebaccessPath, outXmlSpecPath, outXmlDocPath, outXsdWebaccessPath);
	}

	private WAXWriterInput(Config cfg, String appVersion, String modelFileName, boolean skipTiming,
			FixedFormDocumentation fixedFormDocumentation, String inXsdWebaccessPath,
			String outXmlSpecPath, String outXmlDocPath, String outXsdWebaccessPath) {

		super(cfg, appVersion, modelFileName, skipTiming);

		Util.ensureNotNull(fixedFormDocumentation, "fixedFormDocumentation");
		Util.ensureNotNull(inXsdWebaccessPath, "inXsdWebaccessPath");
		Util.ensureNotNull(outXmlSpecPath, "outXmlSpecPath");
		Util.ensureNotNull(outXmlDocPath, "outXmlDocPath");
		Util.ensureNotNull(outXsdWebaccessPath, "outXsdWebaccessPath");

		_fixedFormDocumentation = fixedFormDocumentation;
		_inXsdWebaccessPath = inXsdWebaccessPath;
		_outXmlSpecPath = outXmlSpecPath;
		_outXmlDocPath = outXmlDocPath;
		_outXsdWebaccessPath = outXsdWebaccessPath;
	}

	public FixedFormDocumentation getFixedFormDocumentation() {
		return _fixedFormDocumentation;
	}

	public String getInXsdWebaccessPath() {
		return _inXsdWebaccessPath;
	}

	public String getOutXmlSpecPath() {
		return _outXmlSpecPath;
	}

	public String getOutXmlDocPath() {
		return _outXmlDocPath;
	}

	public String getOutXsdWebaccessPath() {
		return _outXsdWebaccessPath;
	}
}
