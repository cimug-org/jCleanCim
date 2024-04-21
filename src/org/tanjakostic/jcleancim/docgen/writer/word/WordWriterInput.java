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

package org.tanjakostic.jcleancim.docgen.writer.word;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.tanjakostic.jcleancim.common.Config;
import org.tanjakostic.jcleancim.docgen.collector.BookmarkRegistry;
import org.tanjakostic.jcleancim.docgen.collector.ClassDoc;
import org.tanjakostic.jcleancim.docgen.collector.FreeFormDocumentation;
import org.tanjakostic.jcleancim.docgen.collector.ModelFinder;
import org.tanjakostic.jcleancim.docgen.collector.PackageDoc;
import org.tanjakostic.jcleancim.docgen.writer.WriterInput;
import org.tanjakostic.jcleancim.util.Util;

/**
 * @author tatjana.kostic@ieee.org
 * @author laurent.guise@art-et-histoire.com
 * @version $Id: WordWriterInput.java 34 2019-12-20 18:37:17Z dev978 $
 */
public class WordWriterInput extends WriterInput {

	private final Map<String, PackageDoc> _packageDocs;
	private final Map<String, ClassDoc> _classDocs;
	private final ModelFinder _finder;
	private final String _inTemplatePath;
	private final String _outFilePath;
	private final boolean _useBinaryDoc;
	private final boolean _introToFigureBefore;
	private final int _saveRecloseEvery;
	private final boolean _deep;
	private final boolean _useHyperlinks;
	private final List<String> _tocStylePrefixes;
	private final List<String> _headingStylePrefixes;
	private final List<String> _paraStyles;
	private final List<String> _figStyles;
	private final List<String> _tabheadStyles;
	private final List<String> _tabcellStyles;
	private final List<String> _figcaptStyles;
	private final List<String> _tabcaptStyles;
	private final BookmarkRegistry _bmRegistry;

	/**
	 * Constructor.
	 *
	 * @param cfg
	 * @param freeFormDoc
	 *            "flattened" map of package documentation instances, with package name as key (to
	 *            allow to quickly find the package name from what is read in the placeholder). If
	 *            null or empty, placeholders dealing with packages will all have error and empty
	 *            content.
	 */
	public WordWriterInput(Config cfg, FreeFormDocumentation freeFormDoc) {
		this(cfg, null, cfg.isAppSkipTiming(), null, freeFormDoc.getPackageDocs(),
				freeFormDoc.getClassDocs(), freeFormDoc.getModelFinder(),
				cfg.getDocgenWordInTemplateFileAbsPath(), cfg.getDocgenWordOutDocumentFileAbsPath(),
				cfg.isDocgenWordUseDocFormat(), cfg.isDocgenWordIntroToFigureBefore(),
				cfg.getDocgenWordSaveReopenEvery(), !cfg.isDocgenWordAnalysePlaceholders(),
				cfg.isDocgenWordUseHyperlinks(), cfg.getDocgenWordStylesPrefixToc(),
				cfg.getDocgenWordStylesPrefixHead(), cfg.getDocgenWordStylesPara(),
				cfg.getDocgenWordStylesFig(), cfg.getDocgenWordStylesTabhead(),
				cfg.getDocgenWordStylesTabcell(), cfg.getDocgenWordStylesFigcapt(),
				cfg.getDocgenWordStylesTabcapt(), freeFormDoc.getBmRegistry());
	}

	/**
	 * Constructor, useful to create an instance if you don't have configuration.
	 *
	 * @param appVersion
	 * @param modelFileName
	 * @param skipTiming
	 * @param packageDocs
	 *            "flattened" map of package documentation instances, with package name as key (to
	 *            allow to quickly find the package name from what is read in the placeholder). If
	 *            null or empty, placeholders dealing with packages will all have error and empty
	 *            content.
	 * @param classDocs
	 * @param finder
	 *            model facade; if null, most of placeholders will have error.
	 * @param inTemplatePath
	 * @param outFilePath
	 * @param useBinaryDoc
	 * @param introToFigureBefore
	 * @param saveRecloseEvery
	 * @param isDeep
	 * @param useHyperlinks
	 * @param tocStylePrefixes
	 * @param headingStylePrefixes
	 * @param paraStyles
	 * @param figStyles
	 * @param tabheadStyles
	 * @param tabcellStyles
	 * @param figcaptStyles
	 * @param tabcaptStyles
	 * @param bmRegistry
	 */
	public WordWriterInput(String appVersion, String modelFileName, boolean skipTiming,
			Map<String, PackageDoc> packageDocs, Map<String, ClassDoc> classDocs,
			ModelFinder finder, String inTemplatePath, String outFilePath, boolean useBinaryDoc,
			boolean introToFigureBefore, int saveRecloseEvery, boolean isDeep,
			boolean useHyperlinks, List<String> tocStylePrefixes, List<String> headingStylePrefixes,
			List<String> paraStyles, List<String> figStyles, List<String> tabheadStyles,
			List<String> tabcellStyles, List<String> figcaptStyles, List<String> tabcaptStyles,
			BookmarkRegistry bmRegistry) {
		this(null, appVersion, skipTiming, modelFileName, packageDocs, classDocs, finder,
				inTemplatePath, outFilePath, useBinaryDoc, introToFigureBefore, saveRecloseEvery,
				isDeep, useHyperlinks, tocStylePrefixes, headingStylePrefixes, paraStyles,
				figStyles, tabheadStyles, tabcellStyles, figcaptStyles, tabcaptStyles, bmRegistry);
	}

	private WordWriterInput(Config cfg, String appVersion, boolean skipTiming, String modelFileName,
			Map<String, PackageDoc> packageDocs, Map<String, ClassDoc> classDocs,
			ModelFinder finder, String inTemplatePath, String outFilePath, boolean useBinaryDoc,
			boolean introToFigureBefore, int saveRecloseEvery, boolean isDeep,
			boolean useHyperlinks, List<String> tocStylePrefixes, List<String> headingStylePrefixes,
			List<String> paraStyles, List<String> figStyles, List<String> tabheadStyles,
			List<String> tabcellStyles, List<String> figcaptStyles, List<String> tabcaptStyles,
			BookmarkRegistry bmRegistry) {
		super(cfg, appVersion, modelFileName, skipTiming);

		Util.ensureNotEmpty(outFilePath, "outFilePath");

		_packageDocs = (packageDocs == null || packageDocs.isEmpty())
				? new LinkedHashMap<String, PackageDoc>()
				: packageDocs;
		_classDocs = (classDocs == null || classDocs.isEmpty())
				? new LinkedHashMap<String, ClassDoc>()
				: classDocs;

		_finder = finder;
		_inTemplatePath = Util.null2empty(inTemplatePath);
		_outFilePath = outFilePath;
		_useBinaryDoc = useBinaryDoc;
		_introToFigureBefore = introToFigureBefore;
		_saveRecloseEvery = saveRecloseEvery;
		_deep = isDeep;
		_useHyperlinks = useHyperlinks;
		_tocStylePrefixes = new ArrayList<String>(tocStylePrefixes);
		_headingStylePrefixes = new ArrayList<String>(headingStylePrefixes);
		_paraStyles = new ArrayList<String>(paraStyles);
		_figStyles = new ArrayList<String>(figStyles);
		_tabheadStyles = new ArrayList<String>(tabheadStyles);
		_tabcellStyles = new ArrayList<String>(tabcellStyles);
		_figcaptStyles = new ArrayList<String>(figcaptStyles);
		_tabcaptStyles = new ArrayList<String>(tabcaptStyles);
		_bmRegistry = bmRegistry;
	}

	/**
	 * Returns "flattened" map of package documentation instances, with package name as key (to
	 * allow to quickly find the package name from what is read in the placeholder). If null or
	 * empty, placeholders dealing with packages will by definition all report error.
	 */
	public final Map<String, PackageDoc> getPackageDocs() {
		return Collections.unmodifiableMap(_packageDocs);
	}

	/**
	 * Returns "flattened" map of class documentation instances, with <i>qualified</i> class name as
	 * key (to allow to quickly find the class name from what is read in the placeholder). If null
	 * or empty, placeholders dealing with classes will by definition all report error.
	 */
	public final Map<String, ClassDoc> getClassDocs() {
		return Collections.unmodifiableMap(_classDocs);
	}

	/**
	 * Returns object that can find in the UML model items specified in placeholders. If null, most
	 * of placeholders will by definition report error.
	 */
	public ModelFinder getFinder() {
		return _finder;
	}

	/**
	 * Returns absolute path of the file used as template for documentation, empty string if
	 * template is not used.
	 */
	public String getInTemplatePath() {
		return _inTemplatePath;
	}

	/** Returns absolute path of the file to which to write documentation. */
	public String getOutFilePath() {
		return _outFilePath;
	}

	/** Returns whether to force use of COM API (binary .doc format). */
	public boolean isUseBinaryDoc() {
		return _useBinaryDoc;
	}

	/** Returns whether to force figure introduction sentence before the figure. */
	public boolean isIntroToFigureBefore() {
		return _introToFigureBefore;
	}

	/**
	 * Returns the number of tables (captions) to print before closing and reopening the file; this
	 * is an optimisation option that may not be applicable to all writers.
	 */
	public int getSaveRecloseEvery() {
		return _saveRecloseEvery;
	}

	/**
	 * Returns whether to write content for UML packages. Value false is useful for analysing
	 * placeholders in the template document, without writing the whole content.
	 */
	public boolean isDeep() {
		return _deep;
	}

	/** Returns whether to generate hyperlinks (for types of properties). */
	public boolean isUseHyperlinks() {
		return _useHyperlinks;
	}

	/**
	 * Returns non-empty list of prefixes for TOC style in preferred order of use (last is default).
	 */
	public List<String> getTocStylePrefixes() {
		return _tocStylePrefixes;
	}

	/**
	 * Returns non-empty list of prefixes for heading style in preferred order of use (last is
	 * default).
	 */
	public List<String> getHeadingStylePrefixes() {
		return _headingStylePrefixes;
	}

	/**
	 * Returns non-empty list of paragraph text styles in preferred order of use (last is default).
	 */
	public List<String> getParaStyles() {
		return _paraStyles;
	}

	/**
	 * Returns non-empty list of figure styles in preferred order of use (last is default).
	 */
	public List<String> getFigStyles() {
		return _figStyles;
	}

	/**
	 * Returns non-empty list of table head styles in preferred order of use (last is default).
	 */
	public List<String> getTabheadStyles() {
		return _tabheadStyles;
	}

	/**
	 * Returns non-empty list of table cell styles in preferred order of use (last is default).
	 */
	public List<String> getTabcellStyles() {
		return _tabcellStyles;
	}

	/**
	 * Returns non-empty list of figure caption styles in preferred order of use (last is default).
	 */
	public List<String> getFigcaptStyles() {
		return _figcaptStyles;
	}

	/**
	 * Returns non-empty list of table caption styles in preferred order of use (last is default).
	 */
	public List<String> getTabcaptStyles() {
		return _tabcaptStyles;
	}

	/** Returns bookmarks registry (used for hyperlinks). */
	public BookmarkRegistry getBookmarkRegistry() {
		return _bmRegistry;
	}
}
