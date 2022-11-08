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

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.tanjakostic.jcleancim.common.Config;
import org.tanjakostic.jcleancim.docgen.UnsupportedInputFormatException;
import org.tanjakostic.jcleancim.docgen.UnsupportedOutputFormatException;
import org.tanjakostic.jcleancim.docgen.collector.BookmarkRegistry;
import org.tanjakostic.jcleancim.docgen.collector.ClassDoc;
import org.tanjakostic.jcleancim.docgen.collector.FigureDoc;
import org.tanjakostic.jcleancim.docgen.collector.PackageDoc;
import org.tanjakostic.jcleancim.docgen.collector.PackageScl;
import org.tanjakostic.jcleancim.docgen.collector.PlaceholderSpec;
import org.tanjakostic.jcleancim.docgen.collector.PropertiesDoc;
import org.tanjakostic.jcleancim.docgen.writer.AbstractWriter;
import org.tanjakostic.jcleancim.docgen.writer.Caption;
import org.tanjakostic.jcleancim.docgen.writer.CaptionKind;
import org.tanjakostic.jcleancim.docgen.writer.Cursor;
import org.tanjakostic.jcleancim.docgen.writer.CursorList;
import org.tanjakostic.jcleancim.docgen.writer.Placeholder;
import org.tanjakostic.jcleancim.docgen.writer.Range;
import org.tanjakostic.jcleancim.docgen.writer.Style;
import org.tanjakostic.jcleancim.model.TextDescription;
import org.tanjakostic.jcleancim.model.TextDescription.TextKind;
import org.tanjakostic.jcleancim.util.ProgressBar;
import org.tanjakostic.jcleancim.util.Util;

/**
 * @param <O>
 *            technology-specific type to access range object.
 * @author tatjana.kostic@ieee.org
 * @author laurent.guise@art-et-histoire.com
 * @version $Id: AbstractWordWriter.java 34 2019-12-20 18:37:17Z dev978 $
 */
abstract public class AbstractWordWriter<O> extends AbstractWriter
		implements WordWriter<O>, WordHelper<O> {
	private static final Logger _logger = Logger.getLogger(AbstractWordWriter.class.getName());

	private final WordWriterInput _input;

	/**
	 * Constructor (a) initialises caption labels, and (b) copies input template into output file
	 * that will be filled with this writer.
	 *
	 * @param input
	 */
	protected AbstractWordWriter(WordWriterInput input)
			throws UnsupportedInputFormatException, UnsupportedOutputFormatException, IOException {
		super(input);
		_input = input;

		Style.initPreferred(input.getTocStylePrefixes(), input.getHeadingStylePrefixes(),
				input.getParaStyles(), input.getFigStyles(), input.getTabheadStyles(),
				input.getTabcellStyles(), input.getFigcaptStyles(), input.getTabcaptStyles());

		File src = new File(getInput().getInTemplatePath());
		File dst = new File(getInput().getOutFilePath());

		if (!getSupportedFormats().contains(Util.getFileExtensionWithDot(src.getName()))) {
			throw new UnsupportedInputFormatException("Supporting " + getSupportedFormats()
					+ " as template, received " + src.getName() + ".");
		}
		if (!getSupportedFormats().contains(Util.getFileExtensionWithDot(dst.getName()))) {
			throw new UnsupportedOutputFormatException("Supporting " + getSupportedFormats()
					+ " as output, required to process " + dst.getName() + ".");
		}

		copyTemplateAsOutput(src, dst);
		_logger.info("Created " + getClass().getSimpleName() + ".");
	}

	private void copyTemplateAsOutput(File src, File dst) throws IOException {
		Util.copy(src, dst);
	}

	/**
	 * The method that actually executes the whole workflow, from creating Word application, to
	 * writing into document, to shutting down Word application. It provides hooks to plug-in test
	 * write instead of real one.
	 * <p>
	 * When called for normal processing, <code>readOnly</code> shall be false and <code>pp</code>
	 * shall be null.
	 * <p>
	 * When called for tests, <code>readOnly</code> is always true, i.e., nothing gets written /
	 * inserted into template placeholders as normal. If test wants to only scan and not write,
	 * <code>pp</code> can be null. Otherwise, test will want to pass a non-null post-processor
	 * <code>pp</code> to execute test-specific write (that your test implements) and thus
	 * short-circuit the normal implementation.
	 */
	private void doIt(boolean readOnly, PostProcessor pp) {
		try {
			createWordApp();
			openDoc();
			_logger.info(getWordAppName() + " version: " + getWordAppVersion() + Util.NL);

			initDocgenOptimisationOptions();
			setCustomDocProperties(getDocumentMetadata());
			setDocgenOptimisationOptions();

			Style.initUsable(getExistingStyles()); // from here, we have final styles

			List<Range<O>> figCaptionRanges = new ArrayList<Range<O>>();
			List<Range<O>> tabCaptionRanges = new ArrayList<Range<O>>();
			collectFigureAndTableCaptionRanges(figCaptionRanges, tabCaptionRanges);
			_cursors = scanPlaceholderRanges(PlaceholderSpec.MS_PATTERN, figCaptionRanges,
					tabCaptionRanges);

			if (!readOnly) {
				insertDocIntoPlaceholders();
			}

			if (getInput().isUseHyperlinks()) {
				// in last step, there were new, hyperlink placeholders written; here we scan again.
				// note: not using _cursors as tests would fail (they don't expect this reset):
				CursorList<O> cursors = scanHyperlinkPlaceholderRanges(
						PlaceholderSpec.HL_MS_PATTERN);
				if (!readOnly) {
					insertHyperlinksIntoHyperlinksPlaceholders(cursors);
				}
			}
			logReplacementFailures();

			if (pp != null) {
				pp.postProcess();
			}

			unsetDocgenOptimisationOptions();
			updateAllFields(getInput().isSkipTiming());

		} catch (Exception e) {
			_logger.error("##### Prematurely closing document" + " (and exiting MS Word)"
					+ " due to exception: " + e.getMessage());
			if (pp != null) {
				throw new RuntimeException(e);
			}
			e.printStackTrace(System.err);
		} finally {
			try {
				closeDoc();
				Style.reset();
				exitAppAndSaveDocument();
			} catch (IOException e) {
				_logger.warn(e.getMessage());
			}
		}
	}

	private void logReplacementFailures() {
		List<String> replacementFailures = getCursors().getReplacementFailures();
		if (replacementFailures.isEmpty()) {
			return;
		}

		_logger.error(String.format("There were %d placeholder replacement failures:",
				Integer.valueOf(replacementFailures.size())));
		for (String failure : replacementFailures) {
			_logger.error(failure);
		}
		_logger.info("Supported placeholder formats:");
		_logger.info(
				Util.concatCharSeparatedTokens(Util.NL, PlaceholderSpec.getSupportedFormats()));
	}

	// --------- Word app and document lifecycle -------------

	/**
	 * {@inheritDoc}
	 * <p>
	 * This default implementation does nothing.
	 */
	@Override
	public void createWordApp() {
		// no-op
	}

	/**
	 * {@inheritDoc}
	 * <p>
	 * This default implementation returns empty string.
	 */
	@Override
	public String getWordAppName() {
		return "";
	}

	/**
	 * {@inheritDoc}
	 * <p>
	 * This default implementation returns empty string.
	 */
	@Override
	public String getWordAppVersion() {
		return "";
	}

	/**
	 * {@inheritDoc}
	 * <p>
	 * This default implementation does nothing.
	 */
	@Override
	public void exitAppAndSaveDocument() {
		// no-op
	}

	// ------------------- application & document optimisation options ---------------

	/**
	 * {@inheritDoc}
	 * <p>
	 * This default implementation does nothing.
	 */
	@Override
	public void initDocgenOptimisationOptions() {
		// no-op
	}

	/**
	 * {@inheritDoc}
	 * <p>
	 * This default implementation does nothing.
	 */
	@Override
	public void setDocgenOptimisationOptions() {
		// no-op
	}

	/**
	 * {@inheritDoc}
	 * <p>
	 * This default implementation does nothing.
	 */
	@Override
	public void unsetDocgenOptimisationOptions() {
		// no-op
	}

	// -------------------

	/**
	 * Important: When using the close-reopen optimisation ({@link #applyCloseReopen()}), we MUST
	 * NOT do field updates immediately after opening it; if we do, in CIM Word templates we have,
	 * the last heading (Bibliography) gets wrongly picked immediatly after the first close/reopen.
	 * In other templates, similar happens, because Word will internally increase range for the TOC
	 * in the document before saving it, but after reopening, the doc is shorter ! We have no
	 * programmatical means to track this condition.
	 *
	 * @param skipTiming
	 */
	private void updateAllFields(boolean skipTiming) {
		_logger.info("   updating document fields (takes a while)...");
		long start = System.currentTimeMillis();
		updateFields();
		updateTablesOf("TablesOfContents");
		updateTablesOf("TablesOfFigures");
		Util.logCompletion(Level.INFO, "   updated document fields", start, skipTiming);
	}

	// ----------------- main processing steps -----------------

	/**
	 * Gets the map from Word and initialises caption range lists - homegrown range housekeeping for
	 * performance reasons.
	 */
	private void collectFigureAndTableCaptionRanges(List<Range<O>> figCaptionRanges,
			List<Range<O>> tabCaptionRanges) {
		Util.logSubtitle(Level.INFO,
				"collecting used existing figure/table caption ranges from template...");
		long start = System.currentTimeMillis();

		Map<CaptionKind, List<Caption<O>>> captions = collectCaptionParagraphsAndFixLabelsAlsoInTOCs();

		for (Caption<O> cd : captions.get(CaptionKind.Figure)) {
			figCaptionRanges.add(createRange(cd.getRange().getObject()));
		}
		for (Caption<O> cd : captions.get(CaptionKind.Table)) {
			tabCaptionRanges.add(createRange(cd.getRange().getObject()));
		}

		Util.logCompletion(Level.INFO, "collected used figure/table caption ranges from template.",
				start, getInput().isSkipTiming());
	}

	@Override
	public CursorList<O> scanPlaceholderRanges(String pattern, List<Range<O>> figCaptionRanges,
			List<Range<O>> tabCaptionRanges) {
		Util.logSubtitle(Level.INFO, "scanning placeholders...");
		long start = System.currentTimeMillis();

		int figCountBefore = 0;
		int tabCountBefore = 0;
		int i = 0;
		CursorList<O> result = new CursorList<O>();

		WordPatternFinder<O> finder = createPatternFinder(pattern);
		Range<O> range = finder.getRange();

		while (finder.hasMore()) {
			if (isInTOC(range)) {
				_logger.info("   skipping TOC: " + range.getText());
				continue;
			}

			if (i != 0) {
				Cursor<O> prevCursor = result.get(i - 1);
				Range<O> prevRange = prevCursor.getRange();
				figCountBefore = prevCursor.getPlaceholder().getFigureCount();
				for (Range<O> capRange : figCaptionRanges) {
					if (prevRange.getEnd() < capRange.getStart()
							&& capRange.getEnd() < range.getStart()) {
						figCountBefore++;
					}
				}

				tabCountBefore = prevCursor.getPlaceholder().getTableCount();
				for (Range<O> capRange : tabCaptionRanges) {
					if (prevRange.getEnd() < capRange.getStart()
							&& capRange.getEnd() < range.getStart()) {
						tabCountBefore++;
					}
				}
			} else {
				figCountBefore = 0;
				for (Range<O> capRange : figCaptionRanges) {
					if (capRange.getEnd() < range.getStart()) {
						figCountBefore++;
					}
				}

				tabCountBefore = 0;
				for (Range<O> capRange : tabCaptionRanges) {
					if (capRange.getEnd() < range.getStart()) {
						tabCountBefore++;
					}
				}
			}

			String foundMatch = range.getText();
			PlaceholderSpec phSpec = new PlaceholderSpec(foundMatch);
			Placeholder ph = new Placeholder(phSpec, figCountBefore, tabCountBefore);
			Cursor<O> cursor = createCursor(ph, duplicateRange(range));
			result.add(cursor);
			_logger.info("   saved placeholder '" + range.getText() + "' " + ph.toString());
			++i;
		}
		_logger.info(result.size() + " placeholders found.");

		Util.logCompletion(Level.INFO, "scanned placeholders.", start, getInput().isSkipTiming());
		return result;
	}

	// FIXME could merge with the above one?
	@Override
	public CursorList<O> scanHyperlinkPlaceholderRanges(String pattern) {
		Util.logSubtitle(Level.INFO, "scanning placeholders...");
		long start = System.currentTimeMillis();

		CursorList<O> result = new CursorList<O>();

		WordPatternFinder<O> finder = createPatternFinder(pattern);
		Range<O> range = finder.getRange();

		while (finder.hasMore()) {
			String foundMatch = range.getText();
			PlaceholderSpec phSpec = new PlaceholderSpec(foundMatch);
			Placeholder ph = new Placeholder(phSpec);
			Cursor<O> cursor = createCursor(ph, duplicateRange(range));
			result.add(cursor);
			_logger.trace(
					"   saved hyperlink placeholder '" + range.getText() + "' " + ph.toString());
		}
		_logger.info(result.size() + " hyperlink placeholders found.");

		Util.logCompletion(Level.INFO, "scanned hyperlink placeholders.", start,
				getInput().isSkipTiming());
		return result;
	}

	// ---------------- actual writing into output document -------------------

	/**
	 * When actually writing packages, we catch the exceptions and try to continue, so you see the
	 * most of result possible.
	 */
	private void insertDocIntoPlaceholders() {
		Util.logSubtitle(Level.INFO, "inserting documentation into placeholders");

		boolean isDeep = getInput().isDeep();
		for (int i = 0; i < getCursors().size(); ++i) {
			Cursor<O> cursor = getCursors().get(i);

			long start = System.currentTimeMillis();
			_logger.info(String.format("  replacing %s...", cursor));

			PlaceholderSpec phSpec = cursor.getPlaceholder().getPlaceholderSpec();
			switch (phSpec.getKind()) {
				case FILE: {
					String newText = getInput().getModelFileName();
					cursor.getRange().setText(newText);
					newText = cursor.getRange().getText();
					cursor.getPlaceholder().setReplacedText(newText);
					break;
				}
				case ATTRIBUTE: {
					String newText = determineTextForAttributePh(phSpec);
					cursor.getRange().setText(newText);
					newText = cursor.getRange().getText();
					cursor.getPlaceholder().setReplacedText(newText);
					break;
				}
				case IEC61850_NSNAME: {
					String newText = determineTextForIec61850NsNamePh(phSpec);
					cursor.getRange().setText(newText);
					newText = cursor.getRange().getText();
					cursor.getPlaceholder().setReplacedText(newText);
					break;
				}
				case DIAGRAM: {
					String newText = determineTextForDiagramPhAndInsertDiagram(cursor.getRange(),
							phSpec);
					if (newText.isEmpty()) {
						prependText(cursor.getRange(), newText);
					} else {
						cursor.getRange().setText(newText);
					}
					newText = cursor.getRange().getText();
					cursor.getPlaceholder().setReplacedText(newText);
					break;
				}
				case DIAG_NOTE: {
					String newText = determineTextForDiagNote(phSpec);
					cursor.getRange().setText(newText);
					newText = cursor.getRange().getText();
					cursor.getPlaceholder().setReplacedText(newText);
					break;
				}
				case PRES_CONDITIONS: {
					String newText = determineTextForPackagePh(phSpec);
					cursor.getRange().setText(newText);
					newText = cursor.getRange().getText();
					cursor.getPlaceholder().setReplacedText(newText);
					if (isDeep && phSpec.getErrorText() == null) {
						try {
							cursor = writePresCondTable(cursor,
									getInput().getPackageDocs().get(newText));
						} catch (Exception e) {
							logCaughtExceptionFromWord(e);
						}
					}
					break;
				}
				case FCS: {
					String newText = determineTextForPackagePh(phSpec);
					cursor.getRange().setText(newText);
					newText = cursor.getRange().getText();
					cursor.getPlaceholder().setReplacedText(newText);
					if (isDeep && phSpec.getErrorText() == null) {
						try {
							cursor = writeFcTable(cursor, getInput().getPackageDocs().get(newText));
						} catch (Exception e) {
							logCaughtExceptionFromWord(e);
						}
					}
					break;
				}
				case TRGOPS: {
					String newText = determineTextForPackagePh(phSpec);
					cursor.getRange().setText(newText);
					newText = cursor.getRange().getText();
					cursor.getPlaceholder().setReplacedText(newText);
					if (isDeep && phSpec.getErrorText() == null) {
						try {
							cursor = writeTrgOpTable(cursor,
									getInput().getPackageDocs().get(newText));
						} catch (Exception e) {
							logCaughtExceptionFromWord(e);
						}
					}
					break;
				}
				case ABBREVIATIONS: {
					String newText = determineTextForPackagePh(phSpec);
					cursor.getRange().setText("");
					String emptyText = cursor.getRange().getText();
					cursor.getPlaceholder().setReplacedText(emptyText);
					if (isDeep && phSpec.getErrorText() == null) {
						try {
							cursor = writeAbbrTable(cursor,
									getInput().getPackageDocs().get(newText));
						} catch (Exception e) {
							logCaughtExceptionFromWord(e);
						}
					}
					break;
				}
				case SCL_ENUMS: {
					String newText = determineTextForPackagePh(phSpec);
					cursor.getRange().setText(newText);
					newText = cursor.getRange().getText();
					cursor.getPlaceholder().setReplacedText(newText);
					if (isDeep && phSpec.getErrorText() == null) {
						try {
							cursor = writeSclEnum(cursor, getInput().getPackageDocs().get(newText));
						} catch (Exception e) {
							logCaughtExceptionFromWord(e);
						}
					}
					break;
				}
				case PACKAGE: {
					String newText = determineTextForPackagePh(phSpec);
					cursor.getRange().setText(newText);
					newText = cursor.getRange().getText();
					cursor.getPlaceholder().setReplacedText(newText);
					if (isDeep && phSpec.getErrorText() == null) {
						try {
							PackageDoc packageDoc = getInput().getPackageDocs().get(newText);
							if (packageDoc == null) {
								_logger.warn("No packageDoc for '" + newText + "'.");
								break;
							}
							// this writes all the contents of a root package found as placeholder:
							cursor = writePackage(cursor, packageDoc, true);
						} catch (Exception e) {
							logCaughtExceptionFromWord(e);
						}
					}
					break;
				}
				case CLASS: {
					String newText = determineTextForClassPh(phSpec);
					cursor.getRange().setText(newText);
					newText = cursor.getRange().getText();
					cursor.getPlaceholder().setReplacedText(newText);
					// By here, we replaced placeholder with <pckg.class>; class heading may not be
					// only the class name, so we'll replace that below with the relevant method
					// return value.
					if (isDeep && phSpec.getErrorText() == null) {
						try {
							ClassDoc cDoc = getInput().getClassDocs().get(phSpec.getText());
							if (cDoc == null) {
								_logger.warn("No classDoc for '" + phSpec.getText() + "'.");
								break;
							}
							int outlineLevel = getRangeParagraphOutlineLevel(cursor.getRange(), 1);
							_logger.info("   writing doc for class "
									+ Util.getIndentSpaces(outlineLevel) + phSpec + " ...");

							cursor = writeExplicitClass(cursor, cDoc);
						} catch (Exception e) {
							logCaughtExceptionFromWord(e);
						}
					}
					break;
				}
				case LNMAP_PACKAGE: {
					String newText = determineTextForPackagePh(phSpec);
					cursor.getRange().setText(newText);
					newText = cursor.getRange().getText();
					cursor.getPlaceholder().setReplacedText(newText);
					if (isDeep && phSpec.getErrorText() == null) {
						try {
							cursor = writeLnMapPackage(cursor,
									getInput().getPackageDocs().get(newText));
						} catch (Exception e) {
							logCaughtExceptionFromWord(e);
						}
					}
					break;
				}
				case DATA_INDEX: {
					String newText = determineTextForPackagePh(phSpec);
					cursor.getRange().setText(newText);
					newText = cursor.getRange().getText();
					cursor.getPlaceholder().setReplacedText(newText);
					if (isDeep && phSpec.getErrorText() == null) {
						try {
							cursor = writeDataIndex(cursor,
									getInput().getPackageDocs().get(newText));
						} catch (Exception e) {
							logCaughtExceptionFromWord(e);
						}
					}
					break;
				}
				case UNSUPPORTED:
				default: {
					String newText = phSpec.getErrorText();
					cursor.getRange().setText(newText);
					newText = cursor.getRange().getText();
					cursor.getPlaceholder().setReplacedText(newText);
				}
			}

			Util.logCompletion(Level.INFO, String.format("replaced %s...", cursor), start,
					getInput().isSkipTiming());
		}
	}

	private void logCaughtExceptionFromWord(Exception e) {
		_logger.warn("#######  Caught exception - skipping: " + e.getMessage());
		e.printStackTrace(System.err);
	}

	private void insertHyperlinksIntoHyperlinksPlaceholders(CursorList<O> cursors) {
		int potentialHyperlinkCount = cursors.size();
		Util.logSubtitle(Level.INFO, "inserting " + potentialHyperlinkCount
				+ " hyperlinks (or text only) into hyperlink placeholders");
		long overallStart = System.currentTimeMillis();

		ProgressBar bar = new ProgressBar(potentialHyperlinkCount, 10);
		for (int i = 0; i < potentialHyperlinkCount; ++i) {
			Cursor<O> cursor = cursors.get(i);

			_logger.trace(String.format("  replacing hyperlink %s...", cursor));

			PlaceholderSpec hphSpec = cursor.getPlaceholder().getPlaceholderSpec();

			// we always replace placeholder with the text in the first token:
			String newText = hphSpec.getFirstToken();
			cursor.getRange().setText(newText);
			newText = cursor.getRange().getText();
			cursor.getPlaceholder().setReplacedText(newText);

			// we will be inserting hyperlinks only where we have bookmarks to refer to:
			String ID = hphSpec.getSecondToken();
			BookmarkRegistry bmRegistry = getInput().getBookmarkRegistry();
			if (bmRegistry.isAvailableInDocument(ID)) {
				String url = "#" + ID;
				try {
					insertHyperlink(cursor.getRange(), newText, url);
				} catch (Exception e) {
					logCaughtExceptionFromWord(e);
				}
			} else {
				_logger.trace("No hyperlink added: '" + newText + "' for bookmark '" + ID
						+ "' not in this document.");
			}
			bar.update(i);
		}
		Util.logCompletion(Level.INFO,
				"replaced " + potentialHyperlinkCount + " hyperlink placeholders", overallStart,
				getInput().isSkipTiming());
	}

	private String determineTextForAttributePh(PlaceholderSpec phSpec) {
		if (phSpec.getErrorText() != null) {
			return phSpec.getErrorText();
		}

		String attrVal = getInput().getFinder().findAttributeValue(phSpec.getFirstToken(),
				phSpec.getSecondToken());
		if (attrVal != null) {
			return attrVal;
		}

		phSpec.updateModelErrorText();
		return phSpec.getErrorText();
	}

	private String determineTextForIec61850NsNamePh(PlaceholderSpec phSpec) {
		if (phSpec.getErrorText() != null) {
			return phSpec.getErrorText();
		}

		String nsName = getInput().getFinder().findIec61850NsName(phSpec.getFirstToken());
		if (nsName != null) {
			return nsName;
		}

		phSpec.updateModelErrorText();
		return phSpec.getErrorText();
	}

	private String determineTextForDiagramPhAndInsertDiagram(Range<O> range,
			PlaceholderSpec phSpec) {
		if (phSpec.getErrorText() != null) {
			return phSpec.getErrorText();
		}

		File pic = getInput().getFinder().findDiagramFile(phSpec.getFirstToken(),
				phSpec.getSecondToken());
		if (pic != null) {
			insertFigure(range, pic);
			return "";
		}

		phSpec.updateModelErrorText();
		return phSpec.getErrorText();
	}

	private String determineTextForDiagNote(PlaceholderSpec phSpec) {
		if (phSpec.getErrorText() != null) {
			return phSpec.getErrorText();
		}

		TextDescription diagNote = getInput().getFinder().findDiagramNote(phSpec.getFirstToken(),
				phSpec.getSecondToken());
		if (diagNote != null) {
			return diagNote.text;
		}

		phSpec.updateModelErrorText();
		return phSpec.getErrorText();
	}

	private String determineTextForPackagePh(PlaceholderSpec phSpec) {
		if (phSpec.getErrorText() != null) {
			return phSpec.getErrorText();
		}

		String pckName = phSpec.getFirstToken();
		if (getInput().getPackageDocs().keySet().contains(pckName)) {
			return pckName;
		}

		phSpec.updateModelErrorText();
		return phSpec.getErrorText();
	}

	private String determineTextForClassPh(PlaceholderSpec phSpec) {
		if (phSpec.getErrorText() != null) {
			return phSpec.getErrorText();
		}

		String clsQName = phSpec.getText();
		if (getInput().getClassDocs().keySet().contains(clsQName)) {
			return clsQName;
		}

		phSpec.updateModelErrorText();
		return phSpec.getErrorText();
	}

	protected static double pointForPerc(int perc) {
		double perc2cm = perc * PAGE_WIDTH / 100.;
		return perc2cm * POINTS_FOR_1CM;
	}

	/**
	 * {@inheritDoc}
	 * <p>
	 * This default implementation does nothing.
	 */
	@Override
	public void clearUndoCache() {
		// no-op
	}

	// ===== Impl. of org.tanjakostic.jcleancim.docgen.writer.word.WordHelper methods =====

	private CursorList<O> _cursors;
	private int _closeReopenEveryCounter = 0;

	@Override
	public final void writeByTest(PostProcessor pp) {
		doIt(true, pp);
	}

	@Override
	public final CursorList<O> getCursors() {
		return _cursors;
	}

	@Override
	public final boolean isInTOC(Range<O> range) {
		return Style.isTOC(getRangeParagraphStyleName(range, 1));
	}

	@Override
	public final Cursor<O> createCursor(Placeholder ph, Range<O> limited) {
		return new Cursor<O>(ph, limited);
	}

	@Override
	public final Caption<O> createCaption(CaptionKind figure, Range<O> range) {
		return new Caption<O>(figure, range);
	}

	/**
	 * {@inheritDoc}
	 * <p>
	 * This default implementation just returns <code>currentCursor</code>, without any
	 * closing/reopening. If you need to actually close/reopen the document (as a means of
	 * optimising performance), override this method.
	 */
	@Override
	public Cursor<O> closeAndReopenDoc(CursorList<O> cursors, Cursor<O> currentCursor) {
		return currentCursor;
	}

	/**
	 * {@inheritDoc}
	 * <p>
	 * You'll always use this one for regular text and tables.
	 */
	@Override
	public String appendTextInNewParagraphWithStyle(Range<O> range, TextDescription newText,
			Style style) {
		boolean isHtml = (newText.kind == TextKind.htmlSnippet);
		return (isHtml) ? appendHtmlTextInNewParagraphWithStyle(range, newText.text, style)
				: appendRawTextInNewParagraphWithStyle(range, newText.text, style);
	}

	// ===== Impl. of org.tanjakostic.jcleancim.docgen.writer.word.WordWriter methods =====

	@Override
	public Cursor<O> writePackage(Cursor<O> initCursor, PackageDoc doc, boolean isRoot) {
		Cursor<O> cursor = initCursor;

		Util.ensureNotNull(cursor, "cursor");
		Util.ensureNotNull(doc, "doc");
		if (isRoot) {
			String headingText = doc.getHeadingText();
			if (!headingText.equals(cursor.getRange().getText())) {
				cursor.getRange().setText(headingText);
			}
		}

		int outlineLevel = getRangeParagraphOutlineLevel(cursor.getRange(), 1);
		_logger.info("   writing doc for package " + Util.getIndentSpaces(outlineLevel)
				+ doc.getPackageName() + " ...");

		Style genHeadStyle = Style.getHeadingStyle(outlineLevel + 1);
		if (doc.getClassDocs().size() > 0 || doc.getChildPackageDocs().size() > 0) {
			appendTextInNewParagraphWithStyle(cursor.getRange(),
					new TextDescription(doc.getGenHeadingText()), genHeadStyle);
		}

		if (doc.getDocgenCfg().showNamespacePackages.contains(doc.getPackageName())
				&& !doc.getNsUriAndPrefix().isEmpty()) {
			appendTextInNewParagraphWithStyle(cursor.getRange(), doc.getNsUriAndPrefix(),
					Style.para);
		}

		appendTextInNewParagraphWithStyle(cursor.getRange(), doc.getDescription(), Style.para);

		for (int i = 0; i < doc.getFigureDocs().size(); ++i) {
			cursor = writeDiagram(cursor, doc.getFigureDocs().get(i));
		}

		for (int i = 0; i < doc.getClassDocs().size(); ++i) {
			cursor = writeClassFromPackage(cursor, doc.getClassDocs().get(i), genHeadStyle);
		}
		for (int i = 0; i < doc.getChildPackageDocs().size(); ++i) {
			PackageDoc pDoc = doc.getChildPackageDocs().get(i);
			appendTextInNewParagraphWithStyle(cursor.getRange(),
					new TextDescription(pDoc.getHeadingText()), genHeadStyle);
			cursor = writePackage(cursor, pDoc, false); // <-- recursion
		}
		return cursor;
	}

	@Override
	public Cursor<O> writeDataIndex(Cursor<O> initCursor, PackageDoc packageDoc) {
		Cursor<O> cursor = initCursor;
		String packageName = packageDoc.getPackageName();
		PropertiesDoc doc = packageDoc.getDataIndexDoc();
		if (doc == null) {
			_logger.error("Data index doc null for package " + packageName
					+ " - template contains placeholder "
					+ cursor.getPlaceholder().getPlaceholderSpec().getText() + ","
					+ " whereas property '" + Config.KEY_VALIDATION_PACKAGES_DATA_INDEX
					+ "' does not contain package '" + packageName + "'.");
			return cursor;
		}

		_logger.info("   writing data index doc for package " + packageName + " ...");
		cursor.getRange().setText(doc.getHeadingText());
		cursor = writeProperties(cursor, doc);
		return cursor;
	}

	@Override
	public Cursor<O> writeLnMapPackage(Cursor<O> initCursor, PackageDoc packageDoc) {
		Cursor<O> cursor = initCursor;
		String packageName = packageDoc.getPackageName();
		PropertiesDoc doc = packageDoc.getLnMapPackageDoc();
		if (doc == null) {
			_logger.error("LN mappings doc null for package " + packageName + " - property '"
					+ Config.KEY_VALIDATION_IEC61850_PACKAGE_LN_MAPS + "' not specified or empty.");
			return cursor;
		}

		_logger.info("   writing LN mappings doc for package " + packageName + " ...");
		cursor.getRange().setText(doc.getHeadingText());
		appendTextInNewParagraphWithStyle(cursor.getRange(), doc.getDescription(), Style.para);
		cursor = writeProperties(cursor, doc);
		return cursor;
	}

	@Override
	public Cursor<O> writePresCondTable(Cursor<O> initCursor, PackageDoc packageDoc) {
		Cursor<O> cursor = initCursor;
		String packageName = packageDoc.getPackageName();
		PropertiesDoc doc = packageDoc.getPresCondPackageDoc();
		if (doc == null) {
			_logger.error("Presence conditions doc null for package " + packageName
					+ " - property '" + Config.KEY_VALIDATION_IEC61850_PACKAGE_PRES_COND
					+ "' not specified or empty.");
			return cursor;
		}

		_logger.info("   writing presence conditions from package " + packageName + " ...");
		cursor.getRange().setText(doc.getHeadingText());
		appendTextInNewParagraphWithStyle(cursor.getRange(), doc.getDescription(), Style.para);
		cursor = writeProperties(cursor, doc);
		return cursor;
	}

	@Override
	public Cursor<O> writeFcTable(Cursor<O> initCursor, PackageDoc packageDoc) {
		Cursor<O> cursor = initCursor;
		String packageName = packageDoc.getPackageName();
		PropertiesDoc doc = packageDoc.getFcPackageDoc();
		if (doc == null) {
			_logger.error("Functional constraints doc null for package " + packageName
					+ " - property '" + Config.KEY_VALIDATION_IEC61850_PACKAGE_FC
					+ "' not specified or empty.");
			return cursor;
		}

		_logger.info("   writing functional constraints from package " + packageName + " ...");
		cursor.getRange().setText(doc.getHeadingText());
		appendTextInNewParagraphWithStyle(cursor.getRange(), doc.getDescription(), Style.para);
		cursor = writeProperties(cursor, doc);
		return cursor;
	}

	@Override
	public Cursor<O> writeTrgOpTable(Cursor<O> initCursor, PackageDoc packageDoc) {
		Cursor<O> cursor = initCursor;
		String packageName = packageDoc.getPackageName();
		PropertiesDoc doc = packageDoc.getTrgOpPackageDoc();
		if (doc == null) {
			_logger.error("Trigger options doc null for package " + packageName + " - property '"
					+ Config.KEY_VALIDATION_IEC61850_PACKAGE_TRGOP + "' not specified or empty.");
			return cursor;
		}

		_logger.info("   writing trigger options from package " + packageName + " ...");
		cursor.getRange().setText(doc.getHeadingText());
		appendTextInNewParagraphWithStyle(cursor.getRange(), doc.getDescription(), Style.para);
		cursor = writeProperties(cursor, doc);
		return cursor;
	}

	@Override
	public Cursor<O> writeAbbrTable(Cursor<O> initCursor, PackageDoc packageDoc) {
		Cursor<O> cursor = initCursor;
		String packageName = packageDoc.getPackageName();
		PropertiesDoc doc = packageDoc.getAbbrPackageDoc();
		if (doc == null) {
			_logger.error("Abbreviations doc null for package " + packageName + " - property '"
					+ Config.KEY_VALIDATION_IEC61850_PACKAGES_DO_ABBR
					+ "' not specified or empty.");
			return cursor;
		}

		_logger.info("   writing abbreviations from package " + packageName + " ...");
		cursor.getRange().setText("");
		cursor = writeProperties(cursor, doc);
		return cursor;
	}

	@Override
	public Cursor<O> writeSclEnum(Cursor<O> cursor, PackageDoc packageDoc) {
		String packageName = packageDoc.getPackageName();
		PackageScl doc = packageDoc.getEnumsPackageScl();
		if (doc == null) {
			_logger.error("Enum XML doc null for package " + packageName + " - property '"
					+ Config.KEY_VALIDATION_IEC61850_PACKAGES_ENUMS_XML
					+ "' not specified or empty.");
			return cursor;
		}

		_logger.info("   writing SCL for enums from package " + packageName + " ...");
		cursor.getRange().setText(doc.getHeadingText());
		appendRawTextInNewParagraphWithStyle(cursor.getRange(), doc.toString(), Style.tabcell);
		return cursor;
	}

	@Override
	public Cursor<O> writeExplicitClass(Cursor<O> cursor, ClassDoc doc) {
		return writeClass(cursor, doc, null, true);
	}

	@Override
	public Cursor<O> writeClassFromPackage(Cursor<O> cursor, ClassDoc doc, Style headStyle) {
		return writeClass(cursor, doc, headStyle, false);
	}

	/**
	 * @param initCursor
	 * @param doc
	 * @param headStyle
	 *            (can be null) non-null in case the class is printed from within the package.
	 * @param overwriteHeading
	 *            set true if you need to overwrite the title (such as in the case of explicit class
	 *            placeholder); set false for class printed from within the package.
	 */
	private Cursor<O> writeClass(Cursor<O> initCursor, ClassDoc doc, Style headStyle,
			boolean overwriteHeading) {
		Cursor<O> cursor = initCursor;

		if (overwriteHeading) {
			cursor.getRange().setText(doc.getHeadingText());
		} else {
			appendTextInNewParagraphWithStyle(cursor.getRange(),
					new TextDescription(doc.getHeadingText()), headStyle);
		}

		if (getInput().isUseHyperlinks() && (doc.getBookmarkID() != null)) {
			insertBookmark(cursor.getRange(), doc.getBookmarkID());
			getInput().getBookmarkRegistry().markAsAvailableInDocument(doc.getBookmarkID());
			_logger.trace("  AbstractWordWriter.writeClass(): added bookmark '"
					+ doc.getBookmarkID() + "' to Word document and to used list.");
		}

		clearUndoCache(); // to avoid word showing "memory insufficient..."

		if (doc.getDocgenCfg().includeInheritancePath && !doc.getInheritancePath().isEmpty()) {
			appendTextInNewParagraphWithStyle(cursor.getRange(), doc.getInheritancePath(),
					Style.para);
		}

		appendTextInNewParagraphWithStyle(cursor.getRange(), doc.getDescription(), Style.para);

		for (int i = 0; i < doc.getDiagramDocs().size(); ++i) {
			cursor = writeDiagram(cursor, doc.getDiagramDocs().get(i));
		}

		if (doc.getAttributesDoc().notEmpty()) {
			cursor = writeProperties(cursor, doc.getAttributesDoc());
		}
		if (doc.getAssocEndsDoc().notEmpty()) {
			cursor = writeProperties(cursor, doc.getAssocEndsDoc());
		}
		if (doc.getOperationsDoc().notEmpty()) {
			cursor = writeProperties(cursor, doc.getOperationsDoc());
		}

		return cursor;
	}

	@Override
	public Cursor<O> writeProperties(Cursor<O> initCursor, PropertiesDoc doc) {
		Cursor<O> cursor = initCursor;
		if (_closeReopenEveryCounter == getInput().getSaveRecloseEvery()) {
			_closeReopenEveryCounter = 0;
			cursor = closeAndReopenDoc(getCursors(), cursor);
		}

		Range<O> range = cursor.getRange();
		int insertStart = range.getEnd() + 1; // selects EOL as well

		appendTextInNewParagraphWithStyle(range, new TextDescription(doc.getIntroText()),
				Style.para);

		Range<O> insertPoint = duplicateRange(range);
		insertPoint.setStart(insertStart);

		insertTable(range, doc, Style.tabhead, getInput().isUseHyperlinks());

		int tabNumber = getCursors().captionAdded(CaptionKind.Table, cursor);
		insertTableCaption(range, tabNumber, doc.getCaptionText());

		insertCaptionRef(insertPoint, CaptionKind.Table, tabNumber, true);

		++_closeReopenEveryCounter;
		return cursor;
	}

	@Override
	public Cursor<O> writeDiagram(Cursor<O> cursor, FigureDoc doc) {
		Range<O> range = cursor.getRange();
		int insertStart = range.getEnd() + 1; // selects EOL as well
		Range<O> insertPoint = null;

		if (_input.isIntroToFigureBefore()) {
			appendTextInNewParagraphWithStyle(range, new TextDescription(doc.getIntroText()),
					Style.para);
			insertPoint = duplicateRange(range);
			insertPoint.setStart(insertStart);
		}

		appendTextInNewParagraphWithStyle(range, new TextDescription(), Style.fig);
		insertFigure(range, doc.getFigureFile());

		int figNumber = getCursors().captionAdded(CaptionKind.Figure, cursor);
		insertFigureCaption(range, figNumber, doc.getCaptionText());

		if (_input.isIntroToFigureBefore()) {
			appendTextInNewParagraphWithStyle(range, doc.getDescription(), Style.para);
		} else {
			int insPt = range.getEnd() + 1; // selects EOL as well
			// we have to ensure non-empty text, so we have at least the separator
			appendTextInNewParagraphWithStyle(range,
					new TextDescription(": " + doc.getDescription().text), Style.para);

			insertPoint = duplicateRange(range);
			insertPoint.setStart(insPt);
		}

		insertCaptionRef(insertPoint, CaptionKind.Figure, figNumber,
				_input.isIntroToFigureBefore());
		range.setStart(insertStart);

		return cursor;
	}

	// ===== Impl. of org.tanjakostic.jcleancim.docgen.writer.Writer methods =====

	@Override
	public final WordWriterInput getInput() {
		return _input;
	}

	@Override
	public final String getInputFileNames() {
		return getInput().getInTemplatePath();
	}

	@Override
	public final String getOutputFileNames() {
		return getInput().getOutFilePath();
	}

	@Override
	public final void write() {
		doIt(false, null);
	}

	@Override
	public String toString() {
		return String.format("%s [modelFileName=%s, model=%s, outFilePath=%s]",
				getClass().getSimpleName(), getInput().getModelFileName(), getInput().getFinder(),
				getInput().getOutFilePath());
	}
}
