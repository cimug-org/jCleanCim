package org.tanjakostic.jcleancim.docgen.writer.word;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.tanjakostic.jcleancim.common.Nature;
import org.tanjakostic.jcleancim.docgen.UnsupportedInputFormatException;
import org.tanjakostic.jcleancim.docgen.UnsupportedOutputFormatException;
import org.tanjakostic.jcleancim.docgen.collector.AGSpec;
import org.tanjakostic.jcleancim.docgen.collector.BookmarkRegistry;
import org.tanjakostic.jcleancim.docgen.collector.ClassDoc;
import org.tanjakostic.jcleancim.docgen.collector.ColumnSpec;
import org.tanjakostic.jcleancim.docgen.collector.DocgenConfig;
import org.tanjakostic.jcleancim.docgen.collector.FigureDoc;
import org.tanjakostic.jcleancim.docgen.collector.FormatInfo;
import org.tanjakostic.jcleancim.docgen.collector.ModelFinder;
import org.tanjakostic.jcleancim.docgen.collector.PackageDoc;
import org.tanjakostic.jcleancim.docgen.collector.PropertiesDoc;
import org.tanjakostic.jcleancim.docgen.collector.TableSpec;
import org.tanjakostic.jcleancim.docgen.collector.WAX;
import org.tanjakostic.jcleancim.docgen.collector.impl.AbstractPropertiesDoc;
import org.tanjakostic.jcleancim.docgen.collector.impl.EntryDocImpl;
import org.tanjakostic.jcleancim.docgen.collector.impl.FigureDocImpl;
import org.tanjakostic.jcleancim.docgen.writer.CaptionKind;
import org.tanjakostic.jcleancim.docgen.writer.Cursor;
import org.tanjakostic.jcleancim.docgen.writer.CursorList;
import org.tanjakostic.jcleancim.docgen.writer.Placeholder;
import org.tanjakostic.jcleancim.docgen.writer.Range;
import org.tanjakostic.jcleancim.docgen.writer.Style;
import org.tanjakostic.jcleancim.docgen.writer.word.WordHelper.PostProcessor;
import org.tanjakostic.jcleancim.model.TextDescription;
import org.tanjakostic.jcleancim.model.TextDescription.TextKind;
import org.tanjakostic.jcleancim.util.Util;

/**
 * @param <O>
 *            technology-specific type to access range object.
 * @author tatjana.kostic@ieee.org
 * @version $Id: AbstractWordWriterTestCase.java 34 2019-12-20 18:37:17Z dev978 $
 */
abstract public class AbstractWordWriterTestCase<O> {
	private static final Logger _logger = Logger
			.getLogger(AbstractWordWriterTestCase.class.getName());

	abstract protected AbstractWordWriter<O> createWordWriter(String eapFileName,
			boolean skipTiming, Map<String, PackageDoc> packageDocs,
			Map<String, ClassDoc> classDocs, ModelFinder mf, String inFile, String outFile,
			int closeReopenEvery, List<String> tocStylePrefixes, List<String> headingStylePrefixes,
			List<String> paraStyles, List<String> figStyles, List<String> tabheadStyles,
			List<String> tabcellStyles, List<String> figcaptStyles, List<String> tabcaptStyles)
			throws IOException, UnsupportedInputFormatException, UnsupportedOutputFormatException;

	// ----------------------------------

	static final String IN_DIR = "test/input";
	static final String OUT_DIR = "test/output";
	static final String DIAGRAM_FILENAME = "Core.Main.png";

	// FIXME: TRACE
	static final Level LEVEL = Level.INFO;

	private static int _suffix; // to have separate output for each test

	@Before
	public void setUp() {
		incrementSuffix();
	}

	private static void incrementSuffix() {
		_suffix++;
	}

	@After
	public void tearDown() {
		// nothing to clean
	}

	private File getDiagramFile() {
		File file = new File(IN_DIR, DIAGRAM_FILENAME);
		if (!file.exists()) {
			throw new RuntimeException("No file '" + file.getPath() + "'.");
		}
		return file;
	}

	// ---------------- Mocks, to avoid reading the model from EA -------------

	private static final String ATTR_VAL_INSERTED = "IEC61970CIM14v28 has been inserted";
	private static final String CLASS_NAME = "IEC61970CIMVersion";
	private static final String ATTR_NAME = "version";

	private static final String CORE_PCKG_NAME = "Core";
	private static final String DOMAIN_PCKG_NAME = "Domain";
	private static final List<String> PCK_NAMES = Arrays.asList(CORE_PCKG_NAME, DOMAIN_PCKG_NAME);
	private static final String FOO_CLS_NAME = "Foo";
	private static final String BAR_CLS_NAME = "Bar";
	private static final List<String> CLS_NAMES = Arrays.asList(FOO_CLS_NAME, BAR_CLS_NAME);
	private static final String DIAG_NAME = "Main";
	private static final TextDescription DIAG_NOTE = new TextDescription("Some description.");

	/**
	 * We pre-define what are successful "find"-s - everything else is a failure.
	 *
	 * @author tatjana.kostic@ieee.org
	 * @version $Id: AbstractWordWriterTestCase.java 2260 2012-09-02 19:08:00Z
	 *          tatjana.kostic@ieee.org $
	 */
	class ModelFacadeWithoutModel implements ModelFinder {

		@Override
		public String findAttributeValue(String className, String attributeName) {
			if (className.equals(CLASS_NAME) && attributeName.equals(ATTR_NAME)) {
				return ATTR_VAL_INSERTED;
			}
			return null;
		}

		@Override
		public File findDiagramFile(String packageName, String diagramName) {
			if (packageName.equals(CORE_PCKG_NAME) && diagramName.equals(DIAG_NAME)) {
				return null;
			}
			return getDiagramFile();
		}

		@Override
		public TextDescription findDiagramNote(String packageName, String diagramName) {
			if (packageName.equals(CORE_PCKG_NAME) && diagramName.equals(DIAG_NAME)) {
				return null;
			}
			return DIAG_NOTE;
		}

		@Override
		public String findClassName(String packageName, String className) {
			for (String pName : PCK_NAMES) {
				if (pName.equals(packageName) && className.equals(CLASS_NAME)) {
					return CLASS_NAME;
				}
			}
			return null;
		}

		@Override
		public String findIec61850NsName(String className) {
			return null;
		}
	}

	AbstractWordWriter<O> createWriter(String callerName, String templateName) throws Exception {
		return createWriter(callerName, null, templateName);
	}

	private static final String EAP_FILENAME_INSERTED = "something longer than the placeholder itself.eap";
	private static final int CLOSE_REOPEN_EVERY = 1; // ensure we do open/close

	// FIXME: uncomment when other tests work
	private static final String FRENCH_TOC_PREFIX = "TM";
	private static final String GERMAN_TOC_PREFIX = "Verzeichnis";
	private static final String DEFAULT_TOC_PREFIX = "TOC";
	private static final List<String> TOC_PREFIXES = Arrays.asList(FRENCH_TOC_PREFIX,
			GERMAN_TOC_PREFIX, DEFAULT_TOC_PREFIX);
	private static final String FRENCH_HEAD_PREFIX = "Titre";
	private static final String GERMAN_HEAD_PREFIX = "Überschrift";
	private static final String DEFAULT_HEAD_PREFIX = "Heading";
	private static final List<String> HEAD_PREFIXES = Arrays.asList(FRENCH_HEAD_PREFIX,
			GERMAN_HEAD_PREFIX, DEFAULT_HEAD_PREFIX);

	private static final String PARA_STYLE = "PARAGRAPH";
	private static final String GERMAN_PARA_STYLE = "Standard";
	private static final String DEFAULT_PARA_STYLE = "Normal";
	private static final List<String> PARA_STYLES = Arrays.asList(PARA_STYLE, GERMAN_PARA_STYLE,
			DEFAULT_PARA_STYLE);
	private static final String FIG_STYLE = "FIGURE";
	private static final String OLD_FIG_STYLE = "Picture";
	private static final String GERMAN_FIG_STYLE = "Standard";
	private static final String DEFAULT_FIG_STYLE = "Normal";
	private static final List<String> FIG_STYLES = Arrays.asList(FIG_STYLE, OLD_FIG_STYLE,
			GERMAN_FIG_STYLE, DEFAULT_FIG_STYLE);
	private static final String TABHEAD_STYLE = "TABLE-col-heading";
	private static final String GERMAN_TABHEAD_STYLE = "Standard";
	private static final String DEFAULT_TABHEAD_STYLE = "Normal";
	private static final List<String> TABHEAD_STYLES = Arrays.asList(TABHEAD_STYLE,
			GERMAN_TABHEAD_STYLE, DEFAULT_TABHEAD_STYLE);
	private static final String TABCELL_STYLE = "TABLE-cell";
	private static final String GERMAN_TABCELL_STYLE = "Standard";
	private static final String DEFAULT_TABCELL_STYLE = "Normal";
	private static final List<String> TABCELL_STYLES = Arrays.asList(TABCELL_STYLE,
			GERMAN_TABCELL_STYLE, DEFAULT_TABCELL_STYLE);
	private static final String FIGCAPT_STYLE = "FIGURE-title";
	private static final String GERMAN_FIGCAPT_STYLE = "Beschriftung";
	private static final String DEFAULT_FIGCAPT_STYLE = "Caption";
	private static final List<String> FIGCAPT_STYLES = Arrays.asList(FIGCAPT_STYLE,
			GERMAN_FIGCAPT_STYLE, DEFAULT_FIGCAPT_STYLE);
	private static final String TABCAPT_STYLE = "TABLE-title";
	private static final String FRENCH_TABCAPT_STYLE = "Légende";
	private static final String DEFAULT_TABCAPT_STYLE = "Caption";
	private static final List<String> TABCAPT_STYLES = Arrays.asList(TABCAPT_STYLE,
			FRENCH_TABCAPT_STYLE, DEFAULT_TABCAPT_STYLE);

	AbstractWordWriter<O> createWriter(String callerName, ModelFinder mf, String templateName)
			throws Exception {
		File inFile = new File(IN_DIR, templateName);

		String outFileName = _suffix + "-" + callerName + "-" + templateName;
		File outDir = Util.getDirectory(OUT_DIR, true);
		File outFile = new File(outDir, outFileName);

		Map<String, PackageDoc> pDocs = new LinkedHashMap<String, PackageDoc>();
		for (String name : PCK_NAMES) {
			pDocs.put(name, null);
		}
		Map<String, ClassDoc> cDocs = new LinkedHashMap<String, ClassDoc>();
		for (String name : CLS_NAMES) {
			cDocs.put(name, null);
		}

		return createWordWriter(EAP_FILENAME_INSERTED, false, pDocs, cDocs, mf, inFile.getPath(),
				outFile.getPath(), CLOSE_REOPEN_EVERY, TOC_PREFIXES, HEAD_PREFIXES, PARA_STYLES,
				FIG_STYLES, TABHEAD_STYLES, TABCELL_STYLES, FIGCAPT_STYLES, TABCAPT_STYLES);
	}

	// ========== Tests for scanning and replacing placeholders with text/diagrams ======
	// Depending on whether we do backward or forward scanning and placeholder replacement,
	// tests may differ.
	// ==================================================================================

	private void logWordTest(Level level, String testName, String fileName) {
		_logger.log(level,
				Util.NL + Util.NL + "### Word " + testName + " with " + fileName + " ###");
	}

	static final String TEST_PLACEHOLDERS_FILENAME = "testPlaceholders.doc";

	@Test
	public void testCtorPackageNames() throws Exception {
		String testName = "testCtorPackageNames";
		logWordTest(LEVEL, testName, TEST_PLACEHOLDERS_FILENAME);

		AbstractWordWriter<O> writer = createWriter(testName, TEST_PLACEHOLDERS_FILENAME);
		assertEquals(2, writer.getInput().getPackageDocs().size());
	}

	@Test
	public void testScanPlaceholderCount() throws Exception {
		String testName = "testScanPlaceholderCount";
		logWordTest(LEVEL, testName, TEST_PLACEHOLDERS_FILENAME);
		AbstractWordWriter<O> writer = createWriter(testName, TEST_PLACEHOLDERS_FILENAME);

		writer.writeByTest(null);

		CursorList<O> cursors = writer.getCursors();
		assertEquals(11, cursors.size());
	}

	@Test
	public void testScanPlaceholderFigureCaptionCounts() throws Exception {
		String testName = "testScanPlaceholderFigureCaptionCounts";
		logWordTest(LEVEL, testName, TEST_PLACEHOLDERS_FILENAME);
		AbstractWordWriter<O> writer = createWriter(testName, new ModelFacadeWithoutModel(),
				TEST_PLACEHOLDERS_FILENAME);

		writer.writeByTest(null);

		boolean isFigure = true;
		CursorList<O> cursors = writer.getCursors();
		assertEquals(11, cursors.size());
		assertCaptionCountCorrect(isFigure, 0, cursors, 1);
		assertCaptionCountCorrect(isFigure, 1, cursors, 1);
		assertCaptionCountCorrect(isFigure, 2, cursors, 1);
		assertCaptionCountCorrect(isFigure, 3, cursors, 1);
		assertCaptionCountCorrect(isFigure, 4, cursors, 1);
		assertCaptionCountCorrect(isFigure, 5, cursors, 2);
		assertCaptionCountCorrect(isFigure, 6, cursors, 2);
		assertCaptionCountCorrect(isFigure, 7, cursors, 2);
		assertCaptionCountCorrect(isFigure, 8, cursors, 2);
		assertCaptionCountCorrect(isFigure, 9, cursors, 2);
		assertCaptionCountCorrect(isFigure, 10, cursors, 2);
	}

	@Test
	public void testScanPlaceholderTableCaptionCounts() throws Exception {
		String testName = "testScanPlaceholderTableCaptionCounts";
		logWordTest(LEVEL, testName, TEST_PLACEHOLDERS_FILENAME);
		AbstractWordWriter<O> writer = createWriter(testName, new ModelFacadeWithoutModel(),
				TEST_PLACEHOLDERS_FILENAME);

		writer.writeByTest(null);

		boolean isFigure = false;
		CursorList<O> cursors = writer.getCursors();
		assertEquals(11, cursors.size());
		assertCaptionCountCorrect(isFigure, 0, cursors, 1);
		assertCaptionCountCorrect(isFigure, 1, cursors, 1);
		assertCaptionCountCorrect(isFigure, 2, cursors, 1);
		assertCaptionCountCorrect(isFigure, 3, cursors, 1);
		assertCaptionCountCorrect(isFigure, 4, cursors, 1);
		assertCaptionCountCorrect(isFigure, 5, cursors, 1);
		assertCaptionCountCorrect(isFigure, 6, cursors, 1);
		assertCaptionCountCorrect(isFigure, 7, cursors, 1);
		assertCaptionCountCorrect(isFigure, 8, cursors, 1);
		assertCaptionCountCorrect(isFigure, 9, cursors, 1);
		assertCaptionCountCorrect(isFigure, 10, cursors, 2);
	}

	private void assertCaptionCountCorrect(boolean isFigure, int idx, CursorList<O> cursors,
			int expected) {
		Placeholder ph = cursors.get(idx).getPlaceholder();
		int actual = (isFigure) ? ph.getFigureCount() : ph.getTableCount();
		assertEquals("" + idx, expected, actual);
	}

	@Test
	public void testReplacePlaceholders() throws Exception {
		String testName = "testReplacePlaceholders";
		logWordTest(LEVEL, testName, TEST_PLACEHOLDERS_FILENAME);
		AbstractWordWriter<O> writer = createWriter(testName, new ModelFacadeWithoutModel(),
				TEST_PLACEHOLDERS_FILENAME);

		writer.write();

		CursorList<O> cursors = writer.getCursors();
		assertEquals(11, cursors.size());
		assertEquals("failure", getPhError(cursors, 0), getPhReplacedText(cursors, 0));
		assertEquals("failure", getPhError(cursors, 1), getPhReplacedText(cursors, 1));
		assertEquals("success", ATTR_VAL_INSERTED, getPhReplacedText(cursors, 2));
		assertEquals("failure", getPhError(cursors, 3), getPhReplacedText(cursors, 3));
		assertEquals("success (img inserted)", 1, getPhReplacedText(cursors, 4).length());
		assertEquals("success", writer.getInput().getModelFileName(),
				getPhReplacedText(cursors, 5));
		assertEquals("success", CORE_PCKG_NAME, getPhReplacedText(cursors, 6));
		assertEquals("failure", getPhError(cursors, 7), getPhReplacedText(cursors, 7));
		assertEquals("failure", getPhError(cursors, 8), getPhReplacedText(cursors, 8));
		assertEquals("success", writer.getInput().getModelFileName(),
				getPhReplacedText(cursors, 9));
		assertEquals("success", DOMAIN_PCKG_NAME, getPhReplacedText(cursors, 10));
	}

	private String getPhError(CursorList<O> cursors, int i) {
		return cursors.get(i).getPlaceholder().getPlaceholderSpec().getErrorText();
	}

	private String getPhReplacedText(CursorList<O> cursors, int i) {
		return cursors.get(i).getPlaceholder().getReplacedText();
	}

	// ============= Tests for pure Word processing (exploration tests) =================
	// We call scanAndPostprocess, so as to obtain an open document. The placeholders are
	// scanned and replaced normally, but we do the write that we want (as postprocessing).
	// ==================================================================================

	private static final String TEST_STYLES_FILENAME = "testStyles.doc";

	@Test
	public void testAvailableStyles() throws Exception {
		String testName = "testAvailableStyles";
		logWordTest(LEVEL, testName, TEST_STYLES_FILENAME);
		final AbstractWordWriter<O> writer = createWriter(testName, TEST_STYLES_FILENAME);
		PostProcessor pp = new PostProcessor() {

			@Override
			public void postProcess() {
				assertEquals(24, Style.values().length);

				Style si = Style.para;
				assertFalse("inexisting style", si.getName().equals("PARAGRAPH"));

				si = Style.h4;
				assertTrue("existing style", si.getName().equals("Heading 4"));

				assertNull(Style.getTOCStyle(0));
				assertSame(Style.toc1, Style.getTOCStyle(1));
				assertSame(Style.toc2, Style.getTOCStyle(2));
				assertSame(Style.toc3, Style.getTOCStyle(3));
				assertSame(Style.toc4, Style.getTOCStyle(4));
				assertSame(Style.toc5, Style.getTOCStyle(5));
				assertSame(Style.toc6, Style.getTOCStyle(6));
				assertSame(Style.toc7, Style.getTOCStyle(7));
				assertSame(Style.toc8, Style.getTOCStyle(8));
				assertSame(Style.toc9, Style.getTOCStyle(9));
				assertNull(Style.getTOCStyle(10));
				assertNull(Style.getHeadingStyle(0));
				assertSame(Style.h1, Style.getHeadingStyle(1));
				assertSame(Style.h2, Style.getHeadingStyle(2));
				assertSame(Style.h3, Style.getHeadingStyle(3));
				assertSame(Style.h4, Style.getHeadingStyle(4));
				assertSame(Style.h5, Style.getHeadingStyle(5));
				assertSame(Style.h6, Style.getHeadingStyle(6));
				assertSame(Style.h7, Style.getHeadingStyle(7));
				assertSame(Style.h8, Style.getHeadingStyle(8));
				assertSame(Style.h9, Style.getHeadingStyle(9));
				assertNull(Style.getHeadingStyle(10));
			}
		};
		writer.writeByTest(pp);
	}

	@Test
	public void testRangeParagraphCount() throws Exception {
		String testName = "testRangeParagraphCount";
		logWordTest(LEVEL, testName, TEST_STYLES_FILENAME);
		final AbstractWordWriter<O> writer = createWriter(testName, TEST_STYLES_FILENAME);
		PostProcessor pp = new PostProcessor() {

			@Override
			public void postProcess() {
				CursorList<O> cursors = writer.getCursors();
				assertEquals(1, cursors.size());

				Cursor<O> cursor = cursors.get(0);
				Range<O> range = cursor.getRange();
				assertEquals(cursor.toString(), 1, writer.getRangeParagraphCount(range));

				writer.appendNewLine(range);
				assertEquals("append new line alone does NOT include paragraph character"
						+ cursor.toString(), 1, writer.getRangeParagraphCount(range));

				writer.appendText(range, "some text");
				assertEquals(
						"adding some text to the above empty line DOES include paragraph character"
								+ cursor.toString(),
						2, writer.getRangeParagraphCount(range));

				String txt = writer.appendTextInNewParagraph(range, "appendTextInNewPara");
				assertEquals(cursor.toString(), 3, writer.getRangeParagraphCount(range));
				assertTrue("range cummulated all the text added",
						txt.endsWith("appendTextInNewPara"));

				writer.prependNewLine(range);
				assertEquals(cursor.toString(), 4, writer.getRangeParagraphCount(range));
			}
		};
		writer.writeByTest(pp);
	}

	@Test
	public void testRangeParagraphOutlineLevel() throws Exception {
		String testName = "testRangeParagraphOutlineLevel";
		logWordTest(LEVEL, testName, TEST_STYLES_FILENAME);
		final AbstractWordWriter<O> writer = createWriter(testName, TEST_STYLES_FILENAME);
		PostProcessor pp = new PostProcessor() {

			@Override
			public void postProcess() {
				assertEquals(1, writer.getCursors().size());

				Cursor<O> cursor = writer.getCursors().get(0);
				Range<O> range = cursor.getRange();
				assertEquals(1, writer.getRangeParagraphCount(range));
				assertEquals("H2", 2, writer.getRangeParagraphOutlineLevel(range, 1));
				assertEquals("<1", -1, writer.getRangeParagraphOutlineLevel(range, 0));
				assertEquals(">max", -1, writer.getRangeParagraphOutlineLevel(range, 2));

				writer.appendTextInNewParagraph(range, "some text");
				assertEquals(2, writer.getRangeParagraphCount(range));
				writer.prependNewLine(range);
				assertEquals(3, writer.getRangeParagraphCount(range));

				assertEquals("H2", 2, writer.getRangeParagraphOutlineLevel(range, 1));
				assertEquals("H2", 2, writer.getRangeParagraphOutlineLevel(range, 2));
				assertEquals("body", 10, writer.getRangeParagraphOutlineLevel(range, 3));
			}
		};
		writer.writeByTest(pp);
	}

	@Test
	public void testAppendTextInNewParagraphWithStyle() throws Exception {
		String testName = "testAppendTextInNewParagraphWithStyle";
		logWordTest(LEVEL, testName, TEST_STYLES_FILENAME);
		final AbstractWordWriter<O> writer = createWriter(testName, TEST_STYLES_FILENAME);
		PostProcessor pp = new PostProcessor() {

			@Override
			public void postProcess() {
				assertEquals(1, writer.getCursors().size());

				Cursor<O> cursor = writer.getCursors().get(0);
				Range<O> range = cursor.getRange();
				assertEquals(1, writer.getRangeParagraphCount(range));
				assertEquals("Heading 2", writer.getRangeParagraphStyleName(range, 1));

				int end = range.getEnd();
				String text = "should be H4";
				Style style = Style.h4;
				String styleName = style.getName();
				writer.appendTextInNewParagraphWithStyle(range, new TextDescription(text), style);

				assertEquals("start moved to end+1", end + 1, range.getStart());
				assertEquals("again single para", 1, writer.getRangeParagraphCount(range));
				assertEquals("H4", 4, writer.getRangeParagraphOutlineLevel(range, 1));
				assertEquals("H4", styleName, writer.getRangeParagraphStyleName(range, 1));

				text = "I" + Util.NL + "have" + Util.NL + "been inserted";
				style = Style.para;
				styleName = style.getName();
				end = range.getEnd();
				writer.appendTextInNewParagraphWithStyle(range, new TextDescription(text), style);

				assertEquals("start moved to end+1", end + 1, range.getStart());
				assertEquals("3 paras", 3, writer.getRangeParagraphCount(range));
				assertEquals("body", 10, writer.getRangeParagraphOutlineLevel(range, 1));
				assertEquals("body", styleName, writer.getRangeParagraphStyleName(range, 1));
			}
		};
		writer.writeByTest(pp);
	}

	// ============= Tests on inserting captions for tables/figures and referring to them =====
	// We call scanAndPostprocess, so the placeholders are scanned and replaced normally,
	// but we do the write that we want (as postprocessing).
	// ==================================================================================

	private static final String TEST_CAPTIONS_FILENAME = "testCaptions.doc";

	@Test
	public void testInsertFigureCaption() throws Exception {
		String testName = "testInsertFigureCaption";
		logWordTest(LEVEL, testName, TEST_CAPTIONS_FILENAME);
		final AbstractWordWriter<O> writer = createWriter(testName, TEST_CAPTIONS_FILENAME);
		PostProcessor pp = new PostProcessor() {

			@Override
			public void postProcess() {
				CursorList<O> cursors = writer.getCursors();
				assertEquals(1, cursors.size());
				Cursor<O> cursor = cursors.get(0);
				Range<O> range = cursor.getRange();

				int insertStart = range.getEnd() + 1; // next para
				String txtAfterRef = " shows <content of first figure>";
				// add all that comes after anticipated ref "Figure #":
				String initText = writer.appendTextInNewParagraphWithStyle(range,
						new TextDescription(txtAfterRef), Style.para);

				Range<O> insertPoint = writer.duplicateRange(range);
				insertPoint.setStart(insertStart);
				assertEquals(initText, insertPoint.getText());

				writer.appendTextInNewParagraphWithStyle(range,
						new TextDescription("place for pic"), Style.fig);

				int figNumber = cursors.captionAdded(CaptionKind.Figure, cursor);
				writer.insertFigureCaption(range, figNumber, "Figure caption text");

				Range<O> insertPointCopy = writer.duplicateRange(insertPoint);
				writer.insertCaptionRef(insertPointCopy, CaptionKind.Figure, 1,
						writer.getInput().isIntroToFigureBefore());
				insertPoint.setStart(insertStart);
				String updateTxt = insertPoint.getText();
				assertTrue("starts with Fig", updateTxt.startsWith("Fig"));
				assertTrue("ends with inital txt", updateTxt.endsWith(initText));

				// continue normal flow with last range
				int end = range.getEnd();
				writer.appendTextInNewParagraphWithStyle(range, new TextDescription("some text"),
						Style.para);
				assertEquals("start moved to end+1", end + 1, range.getStart());
			}
		};
		writer.writeByTest(pp);
	}

	private static final String TAB_NAME = "Table with name and two subheads";
	private static final String HEADING_TXT = "Heading for table type ";
	private static final TextDescription DESC_TXT = new TextDescription("Description\nwith NL",
			TextKind.textWithNL);
	private static final TextDescription DESC_HTML = new TextDescription(
			"<p>Description</p><p>with NL</p>", TextKind.htmlSnippet);
	private static final TableSpec TAB_SPEC = new TableSpec("TAB_SPEC", Nature.CIM,
			Arrays.asList(
					new ColumnSpec[] { ColumnSpec.createUnfmted(15, "attrTag1", "docID1", "name"),
							ColumnSpec.createFmted(25, "attrTag2", "docID2", "type"),
							ColumnSpec.createUnfmted(60, "attrTag3", "docID3", "note") }));

	private static final boolean[] IS_SUBHEAD = new boolean[] { true, false, true, false, false,
			false };
	private static final boolean[] IS_SPEC_SUBHEAD = new boolean[] { false, false, true, false,
			false, false };
	private static final TextKind[] FORMATS = new TextKind[] { null, TextKind.textWithNL, null,
			null, null, null };
	private static final String[][] CELL_VALUES = new String[][] { { "first head", "", "" },
			{ "name-1,", "type-1'" + Util.NL + "'continued", "note-1" }, { "second head", "", "" },
			{ "name-2", "type-2", "note-2" }, { "name-3", "type-3", "note-3" },
			{ "name-4", "type-4", "note-4" } };

	@Test
	public void testInsertTable() throws Exception {
		String testName = "testInsertTable";
		logWordTest(LEVEL, testName, TEST_CAPTIONS_FILENAME);
		final AbstractWordWriter<O> writer = createWriter(testName, TEST_CAPTIONS_FILENAME);
		final List<PropertiesDoc> propsDocs = initPropertiesDoc();
		PostProcessor pp = new PostProcessor() {

			@Override
			public void postProcess() {
				assertEquals(1, writer.getCursors().size());
				Cursor<O> cursor = writer.getCursors().get(0);
				Range<O> range = cursor.getRange();

				// add text that comes after anticipated "Table x"
				String txtAfterRef = " defines <content of table>";
				writer.appendTextInNewParagraphWithStyle(range, new TextDescription(txtAfterRef),
						Style.para);

				int tableEnd = writer.insertTable(range, propsDocs.get(0), Style.tabhead, true);
				assertEquals("end of table", tableEnd, range.getEnd());

				// continue normal flow with last range without new line, which has been added
				// in insertTable
				writer.appendTextWithStyle(range, "some text", Style.para);
				assertEquals("start moved to end (no new line)", tableEnd, range.getStart());
			}
		};
		writer.writeByTest(pp);
	}

	@Test
	public void testInsertTableCaption() throws Exception {
		String testName = "testInsertTableCaption";
		logWordTest(LEVEL, testName, TEST_CAPTIONS_FILENAME);
		final AbstractWordWriter<O> writer = createWriter(testName, TEST_CAPTIONS_FILENAME);
		final List<PropertiesDoc> propsDocs = initPropertiesDoc();
		PostProcessor pp = new PostProcessor() {

			@Override
			public void postProcess() {
				CursorList<O> cursors = writer.getCursors();
				assertEquals(1, cursors.size());
				Cursor<O> cursor = cursors.get(0);
				Range<O> range = cursor.getRange();

				int insertStart = range.getEnd() + 1; // next para

				String txtAfterRef = " defines content of table";
				// add all that comes after anticipated "Table/Tableau/etc. x"
				String initText = writer.appendTextInNewParagraphWithStyle(range,
						new TextDescription(txtAfterRef), Style.para);

				Range<O> insertPoint = writer.duplicateRange(range);
				insertPoint.setStart(insertStart);
				assertEquals(initText, insertPoint.getText());

				writer.appendNewLine(range); // must add, otherwise no table contained
				writer.collapseRangeToEnd(range);
				writer.appendText(range, "place for table");
				assertEquals("place for table", range.getText());

				int tableEnd = writer.insertTable(range, propsDocs.get(0), Style.tabhead, true);
				assertEquals("range copy extended after table", tableEnd, range.getEnd());
				assertFalse("init text overwritten", range.getText().startsWith("place for table"));
				assertTrue("range has table", writer.isRangeWithTable(range.getObject()));

				int tabNumber = cursors.captionAdded(CaptionKind.Table, cursor);
				writer.insertTableCaption(range, tabNumber, "Table caption text");

				Range<O> insertPointCopy = writer.duplicateRange(insertPoint);
				writer.insertCaptionRef(insertPointCopy, CaptionKind.Table, 1, true);
				insertPoint.setStart(insertStart);
				String updateTxt = insertPoint.getText();
				assertTrue("starts with Tab", updateTxt.startsWith("Tab"));
				assertTrue("ends with inital txt", updateTxt.endsWith(initText));

				// continue normal flow with last range
				int end = range.getEnd();
				writer.appendTextWithStyle(range, "<some text>", Style.para);
				assertEquals("append with NO new line ", end, range.getStart());
			}
		};
		writer.writeByTest(pp);
	}

	// ==== Tests to debug diff in printing between placehoder at end of file and elsewhere ====
	// We call scanWithPostprocessing, so the placeholders are scanned and replaced normally,
	// but we do the write that we want (as postprocessing).
	// =========================================================================================

	private static final String TAB_NAME2 = null;
	private static final String INTRO_TXT = " shows properties ";
	private static final String CAPT_TXT = "Properties ";
	private static final TextDescription DESC_TXT2 = new TextDescription("Description2\nwith NL",
			TextKind.textWithNL);
	private static final TextDescription DESC_HTML2 = new TextDescription(
			"<p>Description2</p><p>with NL</p>", TextKind.htmlSnippet);
	private static final boolean[] IS_SUBHEAD2 = new boolean[] { false, false, false };
	private static final boolean[] IS_SPEC_SUBHEAD2 = new boolean[] { false, false, false };
	private static final TableSpec TAB_SPEC2 = new TableSpec("TAB_SPEC2", Nature.CIM,
			Arrays.asList(
					new ColumnSpec[] { ColumnSpec.createUnfmted(30, "attrTag1", "docID1", "x"),
							ColumnSpec.createUnfmted(70, "attrTag2", "docID2", "y") }));
	private static final TextKind[] FORMATS2 = null;
	private static final String[][] CELL_VALUES2 = new String[][] { { "x-1", "y-1" },
			{ "x-2", "y-2" }, { "x-3", "y-3" } };

	/**
	 * We fill a couple of items to test tables printing.
	 *
	 * @author tatjana.kostic@ieee.org
	 * @version $Id: AbstractWordWriterTestCase.java 2260 2012-09-02 19:08:00Z
	 *          tatjana.kostic@ieee.org $
	 */
	static class MyTable extends AbstractPropertiesDoc {

		public static MyTable create(int idx) {
			return new MyTable(new DocgenConfig(false, false, false, false,
					Collections.<String> emptySet(), Collections.<String> emptySet(), false), idx);
		}

		public MyTable(DocgenConfig cfg, int idx) {
			this(cfg, deduceRawDescription(idx), deduceHtmlDescription(idx), deduceHeadingText(idx),
					deduceIntroText(idx), deduceCaptionText(idx), deduceColumnDescription(idx),
					deduceTableName(idx), deduceIsRowGroupSubhead(idx),
					deduceIsRowGroupSpecSubhead(idx), deduceFormats(idx), deduceCellValues(idx));
		}

		/**
		 * Creates fully populated instance, with descriptions, but without any raw data; use when
		 * you have all values for
		 * {@link org.tanjakostic.jcleancim.docgen.collector.EntryDoc.Kind#data} (and optionally,
		 * {@link org.tanjakostic.jcleancim.docgen.collector.EntryDoc.Kind#groupSubhead}) entries
		 * ready, and if you need raw data, ensure you initialise them before proceeding.
		 *
		 * @param docgenCfg
		 * @param description
		 * @param htmlDescription
		 * @param headingText
		 * @param introText
		 * @param captionText
		 * @param colSpec
		 * @param tableName
		 * @param subheads
		 * @param specialSubheads
		 *            flags, true if subheads[i] is special
		 * @param formats
		 *            null value considers no formatting at all
		 * @param values
		 * @throws IllegalArgumentException
		 */
		protected MyTable(DocgenConfig docgenCfg, TextDescription description,
				TextDescription htmlDescription, String headingText, String introText,
				String captionText, TableSpec colSpec, String tableName, boolean[] subheads,
				boolean[] specialSubheads, TextKind[] formats, String[][] values)
				throws IllegalArgumentException {
			super(docgenCfg, null, null, description, htmlDescription, false, headingText,
					introText, captionText, colSpec, tableName, new BookmarkRegistry());

			Util.ensureNotEmpty(subheads, "subheads");
			Util.ensureNotEmpty(values, "values");

			if (subheads.length != values.length) {
				throw new IllegalArgumentException(
						String.format("subheads (%d) and values (%d) must have same size.",
								Integer.valueOf(subheads.length), Integer.valueOf(values.length)));
			}
			for (int i = 0; i < values.length; ++i) {
				Util.ensureContainsNoNull(values[i], "values[" + i + "]");
				if (values[i].length != colSpec.colCount()) {
					throw new IllegalArgumentException(
							String.format("values[%d] length (%d) must match column count (%d).",
									Integer.valueOf(i), Integer.valueOf(values[i].length),
									Integer.valueOf(colSpec.colCount())));
				}

				if (subheads[i]) {
					AGSpec agSpec = (specialSubheads[i])
							? AGSpec.createSpecial("DOCategory", WAX.CAT_doDesc, values[i][0])
							: AGSpec.create("DOCategory", WAX.CAT_doDesc, values[i][0]);
					addEntry(EntryDocImpl.createGroupSubhead(agSpec, colSpec.colCount()));
				} else {
					FormatInfo fmt = (formats != null && formats[i] != null)
							? new FormatInfo(formats[i], colSpec.getFmtIdx())
							: null;
					addEntry(EntryDocImpl.createData(null, fmt, values[i]));
				}
			}
		}

		private static String deduceHeadingText(int idx) {
			return HEADING_TXT + (idx + 1) + ".";
		}

		private static TextDescription deduceRawDescription(int idx) {
			return (idx == 0) ? DESC_TXT : (idx == 1) ? DESC_TXT2 : DESC_TXT3;
		}

		private static TextDescription deduceHtmlDescription(int idx) {
			return (idx == 0) ? DESC_HTML : (idx == 1) ? DESC_HTML2 : DESC_HTML3;
		}

		private static String deduceIntroText(int idx) {
			return INTRO_TXT + (idx + 1) + ".";
		}

		private static String deduceCaptionText(int idx) {
			return CAPT_TXT + (idx + 1);
		}

		private static String deduceTableName(int idx) {
			return (idx == 0) ? TAB_NAME : (idx == 1) ? TAB_NAME2 : TAB_NAME3;
		}

		private static TableSpec deduceColumnDescription(int idx) {
			return (idx == 0) ? TAB_SPEC : (idx == 1) ? TAB_SPEC2 : TAB_SPEC3;
		}

		private static boolean[] deduceIsRowGroupSubhead(int idx) {
			return (idx == 0) ? IS_SUBHEAD : (idx == 1) ? IS_SUBHEAD2 : IS_SUBHEAD3;
		}

		private static boolean[] deduceIsRowGroupSpecSubhead(int idx) {
			return (idx == 0) ? IS_SPEC_SUBHEAD : (idx == 1) ? IS_SPEC_SUBHEAD2 : IS_SPEC_SUBHEAD3;
		}

		private static TextKind[] deduceFormats(int idx) {
			return (idx == 0) ? FORMATS : (idx == 1) ? FORMATS2 : FORMATS3;
		}

		private static String[][] deduceCellValues(int idx) {
			return (idx == 0) ? CELL_VALUES : (idx == 1) ? CELL_VALUES2 : CELL_VALUES3;
		}

		@Override
		public String[] getBookmarkIDs() {
			return new String[getRowCount()];
		}
	}

	private static List<PropertiesDoc> initPropertiesDoc() {
		List<PropertiesDoc> result = new ArrayList<PropertiesDoc>();
		result.add(MyTable.create(0));
		result.add(MyTable.create(1));
		result.add(MyTable.create(2));
		return result;
	}

	@Test
	public void testWritePropertiesPlaceholderAtEOF() throws Exception {
		String testName = "testWritePropertiesPlaceholderAtEOF";
		logWordTest(LEVEL, testName, TEST_CAPTIONS_FILENAME);
		final AbstractWordWriter<O> writer = createWriter(testName, TEST_CAPTIONS_FILENAME);
		final List<PropertiesDoc> propsDocs = initPropertiesDoc();

		PostProcessor pp = new PostProcessor() {

			@Override
			public void postProcess() {
				assertEquals("single placeholder", 1, writer.getCursors().size());
				Cursor<O> cursor = writer.getCursors().get(0);
				int insertStart = cursor.getRange().getEnd() + 1; // pass EOL

				Range<O> docRange = writer.getDocumentAsRange();
				assertEquals("placeholder at the EOF (doc has implicit paragraph char at the end)",
						docRange.getEnd() - 1, cursor.getRange().getEnd());

				cursor = writer.writeProperties(cursor, propsDocs.get(0));

				docRange = writer.getDocumentAsRange();
				assertEquals("contents at the EOF (doc has implicit paragraph char at the end)",
						docRange.getEnd() - 1, cursor.getRange().getEnd());

				Range<O> rangeCopy = writer.duplicateRange(cursor.getRange());

				rangeCopy.setStart(insertStart);
				assertTrue(rangeCopy.getText().startsWith("Tab"));
			}
		};
		writer.writeByTest(pp);
	}

	private static final String TEST_WRITE_PROPERTIES_FILENAME = "testWriteProperties.docx";

	@Test
	public void testWritePropertiesPlaceholderBeforeEOF() throws Exception {
		String testName = "testWritePropertiesPlaceholderBeforeEOF";
		logWordTest(LEVEL, testName, TEST_WRITE_PROPERTIES_FILENAME);
		final AbstractWordWriter<O> writer = createWriter(testName, TEST_WRITE_PROPERTIES_FILENAME);
		final List<PropertiesDoc> propsDocs = initPropertiesDoc();

		PostProcessor pp = new PostProcessor() {

			@Override
			public void postProcess() {
				assertEquals("two placeholders", 2, writer.getCursors().size());
				int lastRangeIdx = 1;
				Cursor<O> cursor = writer.getCursors().get(lastRangeIdx);

				Range<O> lastRange = cursor.getRange();
				Range<O> docRange = writer.getDocumentAsRange();
				assertEquals("focus placeholder not at the EOF", docRange.getEnd() - 1,
						lastRange.getEnd());

				int insertStart = cursor.getRange().getEnd() + 1; // next para

				cursor = writer.writeProperties(cursor, propsDocs.get(0));

				Range<O> rangeCopy = writer.duplicateRange(cursor.getRange());
				rangeCopy.setStart(insertStart);
				_logger.info("new range copy: " + rangeCopy);
				assertTrue(rangeCopy.getText().startsWith("Tab"));
			}
		};
		writer.writeByTest(pp);
	}

	@Test
	public void testWritePropertiesMultiple() throws Exception {
		String testName = "testWritePropertiesMultiple";
		logWordTest(LEVEL, testName, TEST_WRITE_PROPERTIES_FILENAME);
		final AbstractWordWriter<O> writer = createWriter(testName, TEST_WRITE_PROPERTIES_FILENAME);
		final List<PropertiesDoc> propsDocs = initPropertiesDoc();
		PostProcessor pp = new PostProcessor() {

			@Override
			public void postProcess() {
				assertEquals("two placeholders", 2, writer.getCursors().size());

				// --------- now to first range (backwards)

				Cursor<O> firstCursor = writer.getCursors().get(0);
				int firstInsertStart = firstCursor.getRange().getEnd() + 1; // include EOL

				firstCursor = writer.writeProperties(firstCursor, propsDocs.get(0));
				writer.appendTextInNewParagraphWithStyle(firstCursor.getRange(),
						new TextDescription("(some text)"), Style.para);
				firstCursor = writer.writeProperties(firstCursor, propsDocs.get(1));
				writer.appendTextInNewParagraphWithStyle(firstCursor.getRange(),
						new TextDescription("(some text)"), Style.para);

				Range<O> firstRangeCopy = writer.duplicateRange(firstCursor.getRange());
				firstRangeCopy.setStart(firstInsertStart);
				assertTrue(firstRangeCopy.getText(),
						firstRangeCopy.getText().startsWith(CaptionKind.Table.getLabel() + " 1"));

				// --------- now to first range (backwards)

				Cursor<O> secondCursor = writer.getCursors().get(1);
				Range<O> docRange = writer.getDocumentAsRange();
				assertEquals("second placeholder at the EOF", docRange.getEnd() - 1,
						secondCursor.getRange().getEnd());

				int secondInsertStart = secondCursor.getRange().getEnd() + 1; // next para

				secondCursor = writer.writeProperties(secondCursor, propsDocs.get(2));
				secondCursor = writer.writeProperties(secondCursor, propsDocs.get(0));

				Range<O> secondRangeCopy = writer.duplicateRange(secondCursor.getRange());
				secondRangeCopy.setStart(secondInsertStart);
				assertTrue(
						secondRangeCopy.getText().startsWith(CaptionKind.Table.getLabel() + " 3"));

			}
		};
		writer.writeByTest(pp);
	}

	// ------------------------------

	private static final String TEST_HTML_TABLE = "testHtmlTable.docx";

	private static final String TAB_NAME3 = "MyTable";
	private static final TextDescription DESC_TXT3 = new TextDescription("Description3\nwith NL",
			TextKind.textWithNL);
	private static final TextDescription DESC_HTML3 = new TextDescription(
			"<p>Description3</p><p>with NL</p>", TextKind.htmlSnippet);
	private static final TableSpec TAB_SPEC3 = new TableSpec("TAB_SPEC3", Nature.CIM,
			Arrays.asList(
					new ColumnSpec[] { ColumnSpec.createUnfmted(30, "attrTag1", "docID1", "name"),
							ColumnSpec.createFmted(30, "attrTag2", "docID2", "fdoc"),
							ColumnSpec.createUnfmted(40, "attrTag3", "docID3", "anything") }));

	private static final boolean[] IS_SUBHEAD3 = new boolean[] { true, false, false, false };
	private static final boolean[] IS_SPEC_SUBHEAD3 = new boolean[] { false, false, false, false };
	private static final TextKind[] FORMATS3 = new TextKind[] { null, TextKind.htmlSnippet, null,
			TextKind.textWithNL };
	private static final String[][] CELL_VALUES3 = new String[][] { { "subhead", "", "" },
			{ "a-1", "<p>html a1 text<p><ul><li>first<li>sec</ul><p>more text",
					"a1 non-formatted" },
			{ "a-2", "", "" }, { "a-3", "txt'" + Util.NL + "'plus NL", "a3 non-formatted" } };

	// FIXME - drive implementation of pasting HTML into word;
	// ensure you initialise the writer with printHtml option

	@Ignore(value = "Fails with ant 1.9.2, java 8 (on my new machine)...")
	@Test
	public void testWritePropertiesHTML() throws Exception {
		String testName = "testWritePropertiesHTML";
		logWordTest(LEVEL, testName, TEST_HTML_TABLE);
		final AbstractWordWriter<O> writer = createWriter(testName, TEST_HTML_TABLE);
		final List<PropertiesDoc> propsDocs = initPropertiesDoc();
		PostProcessor pp = new PostProcessor() {

			@Override
			public void postProcess() {
				PropertiesDoc tableWithFormat = propsDocs.get(2);
				assertEquals("1 placeholder", 1, writer.getCursors().size());

				Cursor<O> cursor = writer.getCursors().get(0);
				Range<O> lastRange = cursor.getRange();
				Range<O> docRange = writer.getDocumentAsRange();
				assertEquals("focus placeholder not at the EOF", docRange.getEnd() - 1,
						lastRange.getEnd());

				Range<O> range = cursor.getRange();
				int insertStart = range.getEnd() + 1; // selects EOL as well

				String someMarkup = "<p>Some text.<ol><li>item 1<li>item2</ol><p>More text.";
				writer.appendTextInNewParagraphWithStyle(range,
						new TextDescription(someMarkup, TextKind.htmlSnippet), Style.para);

				cursor = writer.writeProperties(cursor, tableWithFormat);

				Range<O> rangeCopy = writer.duplicateRange(cursor.getRange());
				rangeCopy.setStart(insertStart);
				_logger.info("+++ new range copy: " + rangeCopy);
				assertTrue(rangeCopy.getText().startsWith("Some"));
			}
		};
		writer.writeByTest(pp);
	}

	// -------------------------

	private static final String DIAG_INTRO = "<diagram introduction>";
	private static final String DIAG_CAPTION = "diagram caption";
	private static final TextDescription DIAG_DOC = new TextDescription(
			"Some doc." + Util.NL + "Second para.", TextKind.textWithNL);
	private static final TextDescription DIAG_HTML_DOC = new TextDescription(
			"<p>Some doc.</p><p>Second para.</p>", TextKind.textWithNL);

	private FigureDoc initDiagramDoc(boolean printHtml) {
		DocgenConfig docgenCfg = new DocgenConfig(false, false, printHtml, false,
				Collections.<String> emptySet(), Collections.<String> emptySet(), false);
		return new FigureDocImpl(docgenCfg, null, DIAG_DOC, DIAG_HTML_DOC, getDiagramFile(),
				DIAG_INTRO, DIAG_CAPTION, new BookmarkRegistry());
	}

	@Test
	public void testWriteDiagramPlaceholderAtEOF() throws Exception {
		String testName = "testWriteDiagramPlaceholderAtEOF";
		logWordTest(LEVEL, testName, TEST_CAPTIONS_FILENAME);
		final AbstractWordWriter<O> writer = createWriter(testName, TEST_CAPTIONS_FILENAME);
		final FigureDoc diagDoc = initDiagramDoc(false);
		PostProcessor pp = new PostProcessor() {

			@Override
			public void postProcess() {
				assertEquals("single placeholder", 1, writer.getCursors().size());
				Cursor<O> cursor = writer.getCursors().get(0);
				Range<O> range = cursor.getRange();
				// FIXME: This is true only if _input.isIntroToFigureBefore() == true
				int insertStart = range.getEnd() + 1; // next para

				Range<O> docRange = writer.getDocumentAsRange();
				assertEquals("placeholder at the EOF (doc has implicit paragraph char at the end)",
						docRange.getEnd() - 1, range.getEnd());

				writer.writeDiagram(cursor, diagDoc);

				docRange = writer.getDocumentAsRange();
				assertEquals("contents at the EOF (doc has implicit paragraph char at the end)",
						docRange.getEnd() - 1, range.getEnd());

				Range<O> rangeCopy = writer.duplicateRange(range);
				rangeCopy.setStart(insertStart);
				assertTrue(rangeCopy.getText().startsWith(CaptionKind.Figure.getLabel() + " 2"));
			}
		};
		writer.writeByTest(pp);
	}

	@Test
	public void testWriteDiagramMultiple() throws Exception {
		String testName = "testWriteDiagramMultiple";
		logWordTest(LEVEL, testName, TEST_WRITE_PROPERTIES_FILENAME);
		final AbstractWordWriter<O> writer = createWriter(testName, TEST_WRITE_PROPERTIES_FILENAME);
		final FigureDoc diagDoc = initDiagramDoc(false);
		PostProcessor pp = new PostProcessor() {

			@Override
			public void postProcess() {
				assertEquals("two placeholders", 2, writer.getCursors().size());

				// --------- first range
				Cursor<O> firstCursor = writer.getCursors().get(0);
				Range<O> firstRange = firstCursor.getRange();
				int firstInsertStart = firstRange.getEnd() + 1; // next para

				writer.writeDiagram(firstCursor, diagDoc);
				writer.writeDiagram(firstCursor, diagDoc);

				Range<O> firstRangeCopy = writer.duplicateRange(firstRange);
				firstRangeCopy.setStart(firstInsertStart);
				String firstRangeCopyText = firstRangeCopy.getText();
				assertTrue(firstRangeCopyText,
						firstRangeCopyText.startsWith(CaptionKind.Figure.getLabel() + " 1"));

				// --------- second range
				Cursor<O> secondCursor = writer.getCursors().get(1);
				Range<O> secondRange = secondCursor.getRange();
				int secondInsertStart = secondRange.getEnd() + 1; // next para

				Range<O> docRange = writer.getDocumentAsRange();
				assertEquals("second placeholder at the EOF", docRange.getEnd() - 1,
						secondRange.getEnd());

				writer.writeDiagram(secondCursor, diagDoc);
				writer.writeDiagram(secondCursor, diagDoc);

				Range<O> secondRangeCopy = writer.duplicateRange(secondRange);
				secondRangeCopy.setStart(secondInsertStart);
				assertTrue(
						secondRangeCopy.getText().startsWith(CaptionKind.Figure.getLabel() + " 3"));

			}
		};
		writer.writeByTest(pp);
	}

	// ==== Tests to debug diff in printing of multiple fig/tab, with and without IEC styles ====
	// We call scanWithPostprocessing, so the placeholders are scanned and replaced normally,
	// but we do the write that we want (as postprocessing).
	// =========================================================================================

	private static final String TEST_CAPTIONS_WITH_PREEXISITNG_FILENAME = "testCaptionsWithPreexisting.doc";
	private static final String TEST_CAPTIONS_WITH_PREEXISITNG_FILENAME_IECSTYLES = "testCaptionsWithPreexistingIecStyles.doc";

	@Test
	public void testMultipleWriteDiagramWritePropertiesPlaceholderBetweenExistingFigsTabsNoIecStyles()
			throws Exception {
		String testName = "testMultipleWriteDiagramWritePropertiesPlaceholderBetweenExistingFigsTabsNoIecStyles";
		String fileName = TEST_CAPTIONS_WITH_PREEXISITNG_FILENAME;

		doMultipleWriteDiagramWritePropertiesPlaceholderBetweenExistingFigsTabs(testName, fileName);
	}

	@Test
	public void testMultipleWriteDiagramWritePropertiesPlaceholderBetweenExistingFigsTabsWithIecStyles()
			throws Exception {
		String testName = "testMultipleWriteDiagramWritePropertiesPlaceholderBetweenExistingFigsTabsWithIecStyles";
		String fileName = TEST_CAPTIONS_WITH_PREEXISITNG_FILENAME_IECSTYLES;

		doMultipleWriteDiagramWritePropertiesPlaceholderBetweenExistingFigsTabs(testName, fileName);
	}

	private void doMultipleWriteDiagramWritePropertiesPlaceholderBetweenExistingFigsTabs(
			String testName, String fileName) throws Exception {
		logWordTest(LEVEL, testName, fileName);
		final AbstractWordWriter<O> writer = createWriter(testName, fileName);

		final FigureDoc diagDoc = initDiagramDoc(false);
		final List<PropertiesDoc> propsDocs = initPropertiesDoc();
		PostProcessor pp = new PostProcessor() {

			@Override
			public void postProcess() {
				assertEquals("two placeholders", 2, writer.getCursors().size());

				Cursor<O> cursor = writer.getCursors().get(0);
				assertEquals(2, cursor.getPlaceholder().getFigureCountBefore());
				assertEquals(2, cursor.getPlaceholder().getTableCountBefore());

				// ---------------- figure ---------------
				int insertStart = cursor.getRange().getEnd() + 1; // include EOL
				writer.writeDiagram(cursor, diagDoc);
				writer.appendTextInNewParagraphWithStyle(cursor.getRange(),
						new TextDescription("[txt Fig.3]"), Style.para);
				Range<O> rangeCopy = writer.duplicateRange(cursor.getRange());
				rangeCopy.setStart(insertStart);
				assertTrue(rangeCopy.getText().startsWith(CaptionKind.Figure.getLabel() + " 3"));

				// --------------- table ----------------
				insertStart = cursor.getRange().getEnd() + 1; // next para
				cursor = writer.writeProperties(cursor, propsDocs.get(0));
				writer.appendTextInNewParagraphWithStyle(cursor.getRange(),
						new TextDescription("[txt Tab.3]"), Style.para);
				rangeCopy = writer.duplicateRange(cursor.getRange());
				rangeCopy.setStart(insertStart);
				assertTrue(rangeCopy.getText().startsWith(CaptionKind.Table.getLabel() + " 3"));

				// --------------- figure --------------
				insertStart = cursor.getRange().getEnd() + 1; // next para
				writer.writeDiagram(cursor, diagDoc);
				writer.appendTextInNewParagraphWithStyle(cursor.getRange(),
						new TextDescription("[txt Fig.4]"), Style.para);
				rangeCopy = writer.duplicateRange(cursor.getRange());
				rangeCopy.setStart(insertStart);
				assertTrue(rangeCopy.getText().startsWith(CaptionKind.Figure.getLabel() + " 4"));

				// --------------- table ----------------
				insertStart = cursor.getRange().getEnd() + 1; // next para
				cursor = writer.writeProperties(cursor, propsDocs.get(1));
				writer.appendTextInNewParagraphWithStyle(cursor.getRange(),
						new TextDescription("[txt Tab.4]"), Style.para);
				rangeCopy = writer.duplicateRange(cursor.getRange());
				rangeCopy.setStart(insertStart);
				assertTrue(rangeCopy.getText().startsWith(CaptionKind.Table.getLabel() + " 4"));

				// --------------- table ----------------
				insertStart = cursor.getRange().getEnd() + 1; // next para
				cursor = writer.writeProperties(cursor, propsDocs.get(1));
				writer.appendTextInNewParagraphWithStyle(cursor.getRange(),
						new TextDescription("[txt Tab.5]"), Style.para);
				rangeCopy = writer.duplicateRange(cursor.getRange());
				rangeCopy.setStart(insertStart);
				assertTrue(rangeCopy.getText().startsWith(CaptionKind.Table.getLabel() + " 5"));
			}
		};
		writer.writeByTest(pp);
	}
}
