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

package org.tanjakostic.jcleancim.docgen.writer.word.doc;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.tanjakostic.jcleancim.docgen.UnsupportedInputFormatException;
import org.tanjakostic.jcleancim.docgen.UnsupportedOutputFormatException;
import org.tanjakostic.jcleancim.docgen.collector.ColumnSpec;
import org.tanjakostic.jcleancim.docgen.collector.EntryDoc;
import org.tanjakostic.jcleancim.docgen.collector.EntryDoc.Kind;
import org.tanjakostic.jcleancim.docgen.collector.PropertiesDoc;
import org.tanjakostic.jcleancim.docgen.writer.Caption;
import org.tanjakostic.jcleancim.docgen.writer.CaptionKind;
import org.tanjakostic.jcleancim.docgen.writer.Cursor;
import org.tanjakostic.jcleancim.docgen.writer.CursorList;
import org.tanjakostic.jcleancim.docgen.writer.ExistingStyle;
import org.tanjakostic.jcleancim.docgen.writer.Placeholder;
import org.tanjakostic.jcleancim.docgen.writer.Range;
import org.tanjakostic.jcleancim.docgen.writer.Style;
import org.tanjakostic.jcleancim.docgen.writer.word.AbstractWordWriter;
import org.tanjakostic.jcleancim.docgen.writer.word.WordPatternFinder;
import org.tanjakostic.jcleancim.docgen.writer.word.WordWriterInput;
import org.tanjakostic.jcleancim.model.TextDescription;
import org.tanjakostic.jcleancim.util.ApplicationException;
import org.tanjakostic.jcleancim.util.Util;

import com.jacob.activeX.ActiveXComponent;
import com.jacob.com.Dispatch;
import com.jacob.com.EnumVariant;
import com.jacob.com.Variant;

/**
 * This was a huuuuuuuuuge pain, but satisfies the needs of {@link DocWordWriter} ! Most of it
 * TTD-ed.
 * <p>
 * Because jacob has no API for constants, when we have to provide constants as arguments to VBA
 * methods, we found the values for those constants with ObjectType browser in VBA (macro) editor.
 * <p>
 * In general, we split the processing into two steps:
 * <ol>
 * <li>scanning placeholders (and recording ranges), and,</li>
 * <li>overwriting placeholders with the content.</li>
 * </ol>
 * For the case where hyperlink creation is enabled, in overwriting user-defined placeholder (with
 * the content from e.g. UML packages), we actually insert / write internal hyperlink placeholders
 * that have information about the text we'd like to see and the bookmark we'd like to link it to.
 * Then we repeat the above two steps once more, when we know all the content within the document
 * and all the bookmarks available to link to.
 *
 * @author tatjana.kostic@ieee.org
 * @author laurent.guise@art-et-histoire.com
 * @version $Id: DocWordWriter.java 34 2019-12-20 18:37:17Z dev978 $
 */
public class DocWordWriter extends AbstractWordWriter<Dispatch> {
	private static final Logger _logger = Logger.getLogger(DocWordWriter.class.getName());
	/**
	 * FIXME: Detailed logging for styles and caption labels in the open document, after
	 * initialisation of usable-s has been done (for initialisation, use the equivalent in Style and
	 * CaptionKind). Turn to INFO for debugging only.
	 */
	private static final Level STYLE_CAPTION_LEVEL = Level.TRACE;

	private Dispatch _wordApp;
	private Dispatch _wordDoc;

	private Dispatch _bookmarks;
	private Dispatch _hyperlinks;

	/**
	 * Constructs this instance and copies input file into output directory; this writer will write
	 * into that copy to produce the final document, by replacing placeholders found in it.
	 *
	 * @throws UnsupportedInputFormatException
	 *             if configured with template in unsupported format
	 * @throws UnsupportedOutputFormatException
	 *             if configured with output in unsupported format
	 * @throws IOException
	 *             if fails to copy input template into output directory.
	 */
	public DocWordWriter(WordWriterInput input)
			throws UnsupportedInputFormatException, UnsupportedOutputFormatException, IOException {
		super(input);
	}

	// ===============================

	private EnumVariant getIterator(Dispatch container, String collectionName) {
		Dispatch collection = Dispatch.get(container, collectionName).getDispatch();
		return new EnumVariant(collection);
	}

	/** Returns Word range. */
	private Dispatch getDocumentContent() {
		return Dispatch.get(_wordDoc, "Content").toDispatch();
	}

	private Dispatch getAllStyles() {
		return Dispatch.get(_wordDoc, "Styles").toDispatch();
	}

	/** Wraps <code>obj</code>'s "Range" property into our Range. */
	private Range<Dispatch> createRangeFromRangeOf(Dispatch obj) {
		return createRange(getRangeOf(obj));
	}

	private Dispatch getRangeOf(Dispatch obj) {
		return Dispatch.get(obj, "Range").toDispatch();
	}

	private void select(Dispatch obj) {
		Dispatch.call(obj, "Select");
	}

	// ---------------- Clipboard (Figures and markup text) -------------------

	private static void pasteFromClipboard(Dispatch range) throws ApplicationException {
		try {
			Dispatch.call(range, "Paste");
		} catch (Exception e) {
			e.printStackTrace();
			throw new ApplicationException("pasteFromClipboard failed", e);
		}
	}

	// private static final Variant wdPasteHTML = getVariant(10);
	// private static final Variant wdInLine = getVariant(0);
	private static final Variant wdFormatSurroundingFormattingWithEmphasis = DocWordHelper.vInt(20);

	private static void pasteHtmlFromClipboard(Dispatch range) throws ApplicationException {
		try {
			Dispatch.call(range, "PasteAndFormat", wdFormatSurroundingFormattingWithEmphasis);
		} catch (Exception e) {
			e.printStackTrace();
			throw new ApplicationException("pasteHtmlFromClipboard failed", e);
		}
	}

	// -------------------------------------

	private void setRangeStyle(Dispatch range, Style style) {
		Dispatch.put(range, "Style", style.getName());
	}

	// =============================== Tables ===================================

	public static final boolean ORIG_CREATE_TABLE = true;

	private int doInsertTable(Range<Dispatch> range, PropertiesDoc doc, Style tabhead,
			boolean addBookmarks) {
		Level level = Level.DEBUG;

		long totStart = System.currentTimeMillis();
		_logger.info("--- insertTable() " + doc.getRowCount() + " rows: " + doc.getCaptionText());

		long start = totStart;
		appendRawTextInNewParagraphWithStyle(range, "", Style.tabcell);
		long pause = System.currentTimeMillis();
		_logger.log(level,
				"  ... " + (pause - start) + " ms for appendRawTextInNewParagraphWithStyle()");

		Dispatch table = null;
		if (ORIG_CREATE_TABLE) {
			start = pause;
			table = createTable(range, doc.getColumnCount(), doc.getRowCount());
			pause = System.currentTimeMillis();
			_logger.log(level, "  ... " + (pause - start) + " ms for createTable()");

			start = pause;
			fillValues(table, _wordApp, doc, addBookmarks);
			pause = System.currentTimeMillis();
			_logger.log(level, "  ... " + (pause - start) + " ms for fillValues()");
		} else {
			start = pause;
			table = createTable(range, doc);
			pause = System.currentTimeMillis();
			_logger.log(level, "  ... " + (pause - start) + " ms for createTableWithValues()");
		}

		start = pause;
		formatTable(table, doc, tabhead, level); // format after filling: it may merge cells
		pause = System.currentTimeMillis();
		_logger.log(level, "  ... " + (pause - start) + " ms for formatTable()");

		int rangeEnd = createRangeFromRangeOf(table).getEnd();
		range.setEnd(rangeEnd);

		_logger.info("... " + (pause - totStart) + " ms total for insertTable()");
		return rangeEnd;
	}

	private static final Variant wdWord8TableBehavior = DocWordHelper.vInt(0);
	private static final Variant wdAutoFitFixed = DocWordHelper.vInt(0);

	private static Dispatch createTable(Range<Dispatch> range, int numColumns, int numRows) {
		Dispatch tables = Dispatch.get(range.getObject(), "Tables").toDispatch();
		return Dispatch
				.call(tables, "Add", range.getObject(), DocWordHelper.vInt(numRows),
						DocWordHelper.vInt(numColumns), wdWord8TableBehavior, wdAutoFitFixed)
				.getDispatch();
	}

	private static final Variant wdSeparateByCommas = DocWordHelper.vInt(2);

	// FIXME: We have tried this (which is effectively somewhat faster), but found no way
	// to have Word accept e.g. new line (row) or comma (cell separator) which is included in
	// cell value. So, the split to cells is wrong...
	private static Dispatch createTable(Range<Dispatch> range, PropertiesDoc doc) {
		String cellSep = ",";
		Variant cellSepVb = wdSeparateByCommas;

		String csvTxt = asCsv(doc, cellSep);
		// System.out.println(">>>+++ " + csvTxt);

		range.setText(csvTxt);

		// ConvertToTable([Separator],
		// [NumRows], [NumColumns], [InitialColumnWidth], [Format],
		// [ApplyBorders], [ApplyShading], [ApplyFont], [ApplyColor],
		// [ApplyHeadingRows], [ApplyLastRow], [ApplyFirstColumn], [ApplyLastColumn], [AutoFit],
		// [AutoFitBehavior], [DefaultTableBehavior]) As Table

		return Dispatch.call(range.getObject(), "ConvertToTable", cellSepVb,
				DocWordHelper.vInt(doc.getRowCount()), DocWordHelper.vInt(doc.getColumnCount()),
				Variant.DEFAULT, Variant.DEFAULT, Variant.DEFAULT, Variant.DEFAULT, Variant.DEFAULT,
				Variant.DEFAULT, Variant.DEFAULT, Variant.DEFAULT, Variant.DEFAULT, Variant.DEFAULT,
				Variant.DEFAULT, wdAutoFitFixed, wdWord8TableBehavior).getDispatch();
	}

	public static String asCsv(PropertiesDoc doc, String cellSep) {
		StringBuilder sb = new StringBuilder();

		char lineSep = 13; // ANSI13 for line separator

		int rowCount = doc.getRowCount();
		int colCount = doc.getColumnCount();
		String[][] values = doc.getCellValues();

		for (int i = 0; i < rowCount; ++i) {
			for (int j = 0; j < colCount; ++j) {
				sb.append(StringEscapeUtils.escapeCsv(values[i][j]));
				if (j != colCount - 1) {
					sb.append(cellSep);
				}
			}
			if (i != rowCount - 1) {
				sb.append(lineSep);
			}
		}
		return sb.toString();
	}

	private Dispatch getSelection(Dispatch app) {
		return Dispatch.get(app, "Selection").getDispatch();
	}

	static enum FillCellKind {
		/** new default; the fastest */
		range,

		rangeSelection,
		selection,

		/** original; the slowest */
		cellIndexing;
	}

	// if changing this value, ensure to implement bookmarkID handling!
	static final FillCellKind FILL_CELL_METHOD = FillCellKind.range;

	private void fillValues(Dispatch table, Dispatch app, PropertiesDoc doc, boolean addBookmarks) {
		int rowCount = doc.getRowCount();
		int colCount = doc.getColumnCount();
		String[][] values = doc.getCellValues();
		String[] bookmarkIDs = doc.getBookmarkIDs();

		switch (FILL_CELL_METHOD) {
			case range: {
				// Dispatch tabRange = asRange(table).getObject();
				// Dispatch cells = Dispatch.get(tabRange, "Cells").getDispatch();
				EnumVariant enumVariant = getIterator(getRangeOf(table), "Cells");

				for (int i = 0; i < rowCount; ++i) {
					String bookmarkID = bookmarkIDs[i];
					Range<Dispatch> singleCell = null;
					for (int j = 0; j < colCount; ++j) {
						if (enumVariant.hasMoreElements()) {
							Dispatch cell = enumVariant.nextElement().toDispatch();
							String value = values[i][j];
							if (!value.isEmpty()) {
								Range<Dispatch> cellAsRange = createRangeFromRangeOf(cell);
								cellAsRange.setText(value);
								if (singleCell == null && addBookmarks && (bookmarkID != null)) {
									singleCell = cellAsRange;
									insertBookmark(singleCell, bookmarkID);
									getInput().getBookmarkRegistry()
											.markAsAvailableInDocument(bookmarkID);
									_logger.trace("  DocWordWriter.fillValues(): added bookmark '"
											+ bookmarkID + "' to Word document and to used list.");
								}
							}
						}
					}
				}
				break;
			}
			case rangeSelection: {
				Dispatch tabRange = createRangeFromRangeOf(table).getObject();
				select(tabRange);
				Dispatch selection = getSelection(app);

				Dispatch cells = Dispatch.get(selection, "Cells").getDispatch();
				EnumVariant enumVariant = new EnumVariant(cells);

				for (int i = 0; i < rowCount; ++i) {
					for (int j = 0; j < colCount; ++j) {
						if (enumVariant.hasMoreElements()) {
							Dispatch cell = enumVariant.nextElement().toDispatch();
							String value = values[i][j];
							if (!value.isEmpty()) {
								createRangeFromRangeOf(cell).setText(value);
							}
						}
					}
				}
				break;
			}
			case selection: {
				select(table);
				Dispatch selection = getSelection(app);

				Dispatch cells = Dispatch.get(selection, "Cells").getDispatch();
				EnumVariant enumVariant = new EnumVariant(cells);

				for (int i = 0; i < rowCount; ++i) {
					for (int j = 0; j < colCount; ++j) {
						if (enumVariant.hasMoreElements()) {
							Dispatch cell = enumVariant.nextElement().toDispatch();
							String value = values[i][j];
							if (!value.isEmpty()) {
								createRangeFromRangeOf(cell).setText(value);
							}
						}
					}
				}
				break;
			}
			case cellIndexing: {
				for (int i = 0; i < rowCount; ++i) {
					int wordRowIdx = i + 1; // vba starts with 1
					for (int j = 0; j < colCount; ++j) {
						int wordColumnIdx = j + 1; // vba starts with 1
						Dispatch cell = Dispatch.call(table, "Cell", DocWordHelper.vInt(wordRowIdx),
								DocWordHelper.vInt(wordColumnIdx)).toDispatch();
						Range<Dispatch> cellRange = createRangeFromRangeOf(cell);
						cellRange.setText(values[i][j]);
					}
				}
				break;
			}
		}
	}

	private static final Variant wdLineStyleSingle = DocWordHelper.vInt(1);
	private static final Variant wdPreferredWidthPercent = DocWordHelper.vInt(2);
	private static final Variant wdPreferredWidthPoints = DocWordHelper.vInt(3);
	private static final Variant wdTexture10Percent = DocWordHelper.vInt(100);
	private static final Variant wdTexture25Percent = DocWordHelper.vInt(250);

	private void formatTable(Dispatch table, PropertiesDoc doc, Style tabhead, Level level) {
		long start = System.currentTimeMillis();
		Dispatch.put(table, "PreferredWidthType", wdPreferredWidthPercent);
		Dispatch.put(table, "PreferredWidth", DocWordHelper.vInt(100));
		long pause = System.currentTimeMillis();
		_logger.log(level, "    ... " + (pause - start) + " ms for widths (table)");

		start = pause;
		EnumVariant columns = getIterator(table, "Columns");
		for (ColumnSpec colSpec : doc.getTableSpec().getColSpecs()) {
			Dispatch column = columns.nextElement().getDispatch();
			Dispatch.put(column, "PreferredWidthType", wdPreferredWidthPoints);
			Dispatch.put(column, "Width", new Variant(pointForPerc(colSpec.getRelWidth())));
		}
		pause = System.currentTimeMillis();
		_logger.log(level, "    ... " + (pause - start) + " ms for widths (columns)");

		start = pause;
		// EnumVariant rows = getIterator(table, "Rows");
		// For the sake of optimisation, we don't use iterator here, but the direct access to
		// an item with index, because we need to access rows only in case of special formatting:
		Dispatch rows = Dispatch.call(table, "Rows").toDispatch();
		for (int i = 0; i < doc.getRowCount(); ++i) {
			Kind kind = doc.getEntryDocs().get(i).getKind();
			// for "pure" data, we don't need formatting, created table already uses cell style
			if (kind != EntryDoc.Kind.data) {
				Dispatch row = DocWordHelper.getItem(rows, i + 1);
				if (kind == EntryDoc.Kind.columnLabels) {
					Dispatch.put(row, "HeadingFormat", DocWordHelper.VARIANT_TRUE);
				} else if (kind == EntryDoc.Kind.tableName) {
					Dispatch.put(row, "HeadingFormat", DocWordHelper.VARIANT_TRUE);
					mergeCells(row);
					applyShaddingToRow(row, wdTexture25Percent);
				} else if (kind == EntryDoc.Kind.groupSubhead) {
					mergeCells(row);
					applyShaddingToRow(row, wdTexture10Percent);
				}
				setRangeStyle(createRangeFromRangeOf(row).getObject(), tabhead);
			}
		}
		pause = System.currentTimeMillis();
		_logger.log(level,
				"    ... " + (pause - start) + " ms for shadding/merging/styling (rows)");

		start = pause;
		Dispatch borders = Dispatch.get(table, "Borders").toDispatch();
		Dispatch.put(borders, "InsideLineStyle", wdLineStyleSingle);
		Dispatch.put(borders, "OutsideLineStyle", wdLineStyleSingle);
		pause = System.currentTimeMillis();
		_logger.log(level, "    ... " + (pause - start) + " ms for borders");
	}

	private static void mergeCells(Dispatch row) {
		Dispatch cells = Dispatch.get(row, "Cells").toDispatch();
		Dispatch.call(cells, "Merge");
	}

	private static void applyShaddingToRow(Dispatch row, Variant shadingLevel) {
		Dispatch shading = Dispatch.call(row, "Shading").toDispatch();
		Dispatch.put(shading, "Texture", shadingLevel);
	}

	// FIXME table HTML; should be private when done
	void fillCellWithHtml(Dispatch table, int i, int j, boolean isFormatted, String val,
			Style style) {
		Dispatch cell = Dispatch.call(table, "Cell", DocWordHelper.vInt(i), DocWordHelper.vInt(j))
				.toDispatch();

		Range<Dispatch> cellRange = createRangeFromRangeOf(cell);
		if (isFormatted) {
			cellRange.setEnd(cellRange.getEnd() + 1);
			setRangeStyle(cellRange.getObject(), style);
			// it's faster to check whether empty, then to clear clipboard:
			if (!val.trim().isEmpty()) {
				backspaceLast(cellRange); // eat EOL added above
				Util.copyHtmlToClipboard(val);
				try {
					pasteHtmlFromClipboard(cellRange.getObject());
				} catch (ApplicationException e) {
					_logger.warn(Arrays.deepToString(e.getStackTrace()));
				}
				backspaceLast(cellRange); // eat EOL - paste HTML from clipboard adds EOL
			}
			setRangeStyle(cellRange.getObject(), style);
		} else {
			setRangeStyle(cellRange.getObject(), style);
			cellRange.setText(val);
		}
	}

	private void backspaceLast(Range<Dispatch> range) {
		int oneBeforeEnd = range.getEnd() - 1;
		range.setStart(oneBeforeEnd);
		range.setEnd(oneBeforeEnd);
		Dispatch.call(range.getObject(), "delete");
	}

	// ------------------------------ for debugging only ---------------------------

	// private Range<Dispatch> getField(Dispatch scope, int idx) {
	// Dispatch fields = Dispatch.get(scope, "Fields").toDispatch();
	// int sz = DocWordHelper.getCount(fields);
	// if (sz < idx) {
	// return null;
	// }
	// Dispatch field = DocWordHelper.getItem(fields, idx);
	// return createRange(Dispatch.get(field, "Code").toDispatch());
	// }
	//
	// private String printFields(Dispatch scope) {
	// StringBuilder sb = new StringBuilder();
	// Dispatch fields = Dispatch.get(scope, "Fields").toDispatch();
	// int sz = DocWordHelper.getCount(fields);
	// for (int i = 1; i <= sz; ++i) {
	// Dispatch field = DocWordHelper.getItem(fields, i);
	// Range<Dispatch> code = createRange(Dispatch.get(field, "Code").toDispatch());
	// sb.append("'").append(code.getText()).append("'").append(Util.NL);
	// }
	// return sb.toString();
	// }

	// void addParaForTable(Range<Dispatch> range) {
	// appendNewLine(range); // does NOT create new paragraph!
	// collapseRangeToEnd(range);
	// prependNewLine(range); // this one DOES
	// collapseRangeToStart(range);
	// }

	// int getDocumentParagraphCount(Dispatch document) {
	// Dispatch paragraphs = Dispatch.get(document, "Paragraphs").toDispatch();
	// return getCount(paragraphs);
	// }

	// static void save(Dispatch doc, String outputFileAbsPath) {
	// _logger.info("... saving MSWord file '" + outputFileAbsPath + "'");
	// Dispatch.call(doc, "Save");
	// }

	// ----------------- document lifecycle -----------------

	@Override
	public void createWordApp() {
		_logger.info("creating MSWord application");
//		ComThread.InitSTA();
		ActiveXComponent ax = new ActiveXComponent("Word.Application");
		boolean isVisible = false;
		ax.setProperty("Visible", DocWordHelper.getVariant(isVisible));
		_wordApp = ax.getObject();
	}

	@Override
	public String getWordAppName() {
		return Dispatch.get(_wordApp, "Name").getString();
	}

	@Override
	public String getWordAppVersion() {
		return Dispatch.get(_wordApp, "Version").getString();
	}

	@Override
	public void openDoc() {
		_logger.info("  opening MSWord file '" + getInput().getOutFilePath() + "'");
		Dispatch documents = Dispatch.get(_wordApp, "Documents").toDispatch();
		_wordDoc = Dispatch.call(documents, "Open", getInput().getOutFilePath()).toDispatch();

		// this is somewhat faster (initialise once per opening):
		_bookmarks = Dispatch.call(_wordDoc, "Bookmarks").toDispatch();
		_hyperlinks = Dispatch.call(_wordDoc, "Hyperlinks").toDispatch();
	}

	@Override
	public void closeDoc() {
		if (_wordDoc != null) {
			_logger.info("closing MSWord file '" + getInput().getOutFilePath() + "'");
			boolean saveChanges = true;
			Dispatch.call(_wordDoc, "Close", DocWordHelper.getVariant(saveChanges));
		}
	}

	@Override
	public void exitAppAndSaveDocument() {
		_logger.info("exiting MSWord application");
		boolean saveOnExit = true;
		Dispatch.call(_wordApp, "Quit", DocWordHelper.getVariant(saveOnExit));
//		_wordApp.safeRelease();
//		ComThread.Release();
	}

	// ------------------- application & document optimisation options ---------------

	private OptimOptions _initialOptions;
	private OptimOptions _optimisedOptions;

	@Override
	public void initDocgenOptimisationOptions() {
		if (_wordDoc == null) {
			return;
		}

		_initialOptions = new OptimOptions(_wordDoc, _wordApp);
		_logger.debug("original(_initialOptions): " + _initialOptions.toString());
	}

	@Override
	public void setDocgenOptimisationOptions() {
		if (_wordDoc == null) {
			return;
		}

		// copy original state and change if necessary:
		_optimisedOptions = new OptimOptions(_initialOptions);
		_optimisedOptions.setForOptimisedExecution(_wordDoc, _wordApp);
		_logger.debug("current(_optimisedOptions): " + _optimisedOptions.toString());
	}

	@Override
	public void unsetDocgenOptimisationOptions() {
		if (_wordDoc == null) {
			return;
		}

		_logger.debug("  restoring original options");
		_optimisedOptions.restoreFrom(_initialOptions, _wordDoc, _wordApp);
		_logger.debug(">>  current/restored          : " + _optimisedOptions.toString());

		_optimisedOptions = null; // not used after this point
	}

	// --------- bookmarks and hyperlinks -------------

	@Override
	public void insertBookmark(Range<Dispatch> range, String label) {
		Dispatch.call(_bookmarks, "Add", label, range.getObject());
	}

	@Override
	public void insertHyperlink(Range<Dispatch> range, String textToDisplay, String url) {
		Dispatch.call(_hyperlinks, "Add", range.getObject(), url, textToDisplay);
	}

	// --------- (initialisation of) styles -------------

	private enum WordBuiltinStyles {
		wdStyleNormal(-1, Kind.normal, 0),
		wdStyleHeading1(-2, Kind.heading, 1),
		wdStyleHeading2(-3, Kind.heading, 2),
		wdStyleHeading3(-4, Kind.heading, 3),
		wdStyleHeading4(-5, Kind.heading, 4),
		wdStyleHeading5(-6, Kind.heading, 5),
		wdStyleHeading6(-7, Kind.heading, 6),
		wdStyleHeading7(-8, Kind.heading, 7),
		wdStyleHeading8(-9, Kind.heading, 8),
		wdStyleHeading9(-10, Kind.heading, 9),
		wdStyleTOC1(-20, Kind.toc, 1),
		wdStyleTOC2(-21, Kind.toc, 2),
		wdStyleTOC3(-22, Kind.toc, 3),
		wdStyleTOC4(-23, Kind.toc, 4),
		wdStyleTOC5(-24, Kind.toc, 5),
		wdStyleTOC6(-25, Kind.toc, 6),
		wdStyleTOC7(-26, Kind.toc, 7),
		wdStyleTOC8(-27, Kind.toc, 8),
		wdStyleTOC9(-28, Kind.toc, 9),
		wdStyleCaption(-35, Kind.caption, 0);

		enum Kind {
			normal, heading, toc, caption
		}

		/** Returns built-in item if found, null otherwise. */
		static WordBuiltinStyles findForId(int id) {
			for (WordBuiltinStyles s : values()) {
				if (id == s.getId()) {
					return s;
				}
			}
			return null;
		}

		/** Returns built-in item if found, null otherwise. */
		static WordBuiltinStyles findForName(String name) {
			for (WordBuiltinStyles s : values()) {
				if (name.equals(s.getName())) {
					return s;
				}
			}
			return null;
		}

		private final int _id;
		private final Kind _kind;
		private final int _outlineLevel;
		private String _name;

		WordBuiltinStyles(int id, Kind kind, int outlineLevel) {
			_id = id; // DocWordHelper.vInt(builtinId);
			_kind = kind;
			_outlineLevel = outlineLevel;
			_name = null;
		}

		String getName() {
			return _name;
		}

		void setName(String name) {
			_name = name;
		}

		int getId() {
			return _id;
		}

		Kind getKind() {
			return _kind;
		}

		int getOutlineLevel() {
			return _outlineLevel;
		}

		@Override
		public String toString() {
			return "built-in style: name=" + ((_name == null) ? "null" : _name) + ", id=" + _id
					+ ", outline = " + _outlineLevel + ", kind=" + _kind;
		}
	}

	// private static final int wdStyleTypeParagraph = 1;
	private static final int wdStyleTypeCharacter = 2;
	private static final int wdStyleTypeTable = 3;
	// private static final int wdStyleTypeList = 4;

	/**
	 * {@inheritDoc}
	 * <p>
	 * It will create all custom styles, and only those built-in styles we are interested in.
	 */
	@Override
	public Map<String, ExistingStyle> getExistingStyles() {
		Util.logSubtitle(Level.INFO,
				"collecting existing and retaining usable styles from open MS Word document....");
		long start = System.currentTimeMillis();

		Map<String, ExistingStyle> result = new LinkedHashMap<>();

		initBuiltinStylesNameFromId();

		/*
		 * Use Styles (Index), where Index is the style name, a WdBuiltinStyle constant or index
		 * number, to return a single Style object. You must exactly match the spelling and spacing
		 * of the style name, but not necessarily its capitalization.
		 */
		EnumVariant styles = getIterator(_wordDoc, "Styles");
		while (styles.hasMoreElements()) {
			Dispatch style = styles.nextElement().getDispatch();
			String name = Dispatch.get(style, "NameLocal").getString();
			boolean builtIn = Dispatch.get(style, "BuiltIn").getBoolean();
			int outline = Dispatch.get(style, "ListLevelNumber").getInt();
			int type = Dispatch.get(style, "Type").getInt();

			int id = 0;
			ExistingStyle.Kind kind = (type == wdStyleTypeCharacter || type == wdStyleTypeTable)
					? null
					: ExistingStyle.Kind.OTHER;

			if (builtIn) {
				WordBuiltinStyles builtInStyle = WordBuiltinStyles.findForName(name);
				if (builtInStyle == null) { // skipping built-ins other than what we need
					kind = null;
				} else {
					id = builtInStyle.getId();
					if (builtInStyle.getKind() == WordBuiltinStyles.Kind.heading) {
						kind = ExistingStyle.Kind.HEAD;
					} else if (builtInStyle.getKind() == WordBuiltinStyles.Kind.toc) {
						kind = ExistingStyle.Kind.TOC;
						// outline level for TOC returned by Word is 0 !!!
						outline = builtInStyle.getOutlineLevel();
					} else if (builtInStyle.getKind() == WordBuiltinStyles.Kind.normal) {
						kind = ExistingStyle.Kind.NORM;
					} else if (builtInStyle.getKind() == WordBuiltinStyles.Kind.caption) {
						kind = ExistingStyle.Kind.CAPT;
					}
				}
			}

			if (kind != null) {
				ExistingStyle es = new ExistingStyle(name, String.valueOf(id), builtIn, outline,
						kind);
				result.put(name, es);
			}
		}

		for (ExistingStyle s : result.values()) {
			_logger.info("      " + s);
		}

		Util.logCompletion(Level.INFO, "   collected existing and retained usable styles", start,
				getInput().isSkipTiming());
		return result;
	}

	private void initBuiltinStylesNameFromId() {
		Dispatch allStyles = getAllStyles();

		// init name of built-ins we're interested in - these are the ultimate default:
		for (WordBuiltinStyles builtin : WordBuiltinStyles.values()) {
			Dispatch style = DocWordHelper.getItem(allStyles, builtin.getId());
			String name = Dispatch.get(style, "NameLocal").getString();
			builtin.setName(name);
			_logger.log(Style.LLEVEL, "===== in DocWordWriter.initBuiltinStylesNameFromId():"
					+ " initialised relevant " + builtin);
		}
	}

	// ---------------- TOCs and fields ---------------

	private static final Variant wdFieldSequence = DocWordHelper.vInt(12);
	// private static final Variant wdFieldTOC = DocWordHelper.vInt(13);

	private static final String seqCodeFigTabFmt = " SEQ %s \\* ARABIC ";
	// private static final String tocCodeTit = "TOC \\o \\h \\z \\u";
	// private static final String tocCodeFigTabFmt = "TOC \\h \\z \\c \"%s\"";

	@Override
	public void updateFields() {
		long start = System.currentTimeMillis();

		Dispatch.call(getFields(_wordDoc), "Update");

		long pause = System.currentTimeMillis();
		_logger.info("   updateFields: " + (pause - start) + " ms");
	}

	@Override
	public void updateTablesOf(String what) {
		long start = System.currentTimeMillis();

		EnumVariant tocs = getIterator(_wordDoc, what);
		while (tocs.hasMoreElements()) {
			Dispatch toc = tocs.nextElement().getDispatch();
			updateField(toc);
		}

		long pause = System.currentTimeMillis();
		_logger.info("     update" + what + ": " + (pause - start) + " ms");
	}

	static Dispatch getFields(Dispatch obj) {
		return Dispatch.get(obj, "Fields").toDispatch();
	}

	static Dispatch getFieldCode(Dispatch field) {
		return Dispatch.get(field, "Code").getDispatch();
	}

	static String getFieldCodeText(Dispatch field) {
		Dispatch fieldCode = getFieldCode(field);
		return Dispatch.get(fieldCode, "Text").getString();
	}

	static void setFieldCode(Dispatch field, String txt, boolean withUpdate) {
		Dispatch fieldCode = getFieldCode(field);
		Dispatch.put(fieldCode, "Text", DocWordHelper.vString(txt));
		if (withUpdate) {
			updateField(field);
		}
	}

	static void updateField(Dispatch field) {
		Dispatch.call(field, "Update");
	}

	/** Returns the field result as range (Dispatch). */
	static Dispatch getFieldResult(Dispatch field) {
		return Dispatch.get(field, "Result").toDispatch();
	}

	// ----------------------- custom properties -----------------------

	// string as type for custom property:
	private static final Variant msoPropertyTypeString = DocWordHelper.vInt(4);

	@Override
	public Map<String, String> getCustomDocProperties() {
		Map<String, String> result = new HashMap<String, String>();
		EnumVariant customProps = getIterator(_wordDoc, "CustomDocumentProperties");
		while (customProps.hasMoreElements()) {
			Dispatch customProp = customProps.nextElement().toDispatch();
			String name = Dispatch.get(customProp, "Name").getString();
			String value = Dispatch.get(customProp, "Value").getString();
			result.put(name, value);
		}
		return result;
	}

	@Override
	public void setCustomDocProperties(Map<String, String> newCustomProps) {
		Map<String, String> newProps = new HashMap<String, String>(newCustomProps);
		Dispatch customProps = Dispatch.get(_wordDoc, "CustomDocumentProperties").toDispatch();
		int count = DocWordHelper.getCount(customProps);
		for (int i = 1; i <= count; ++i) {
			Dispatch customProp = DocWordHelper.getItem(customProps, i);
			String name = Dispatch.get(customProp, "Name").getString();
			if (newProps.containsKey(name)) {
				// "move" from requested map into Word document:
				String value = newProps.remove(name);
				_logger.debug(
						String.format("setting existing custom property: %s = %s ", name, value));
				Dispatch.put(customProp, "Value", DocWordHelper.vString(value));
			}
		}
		if (newProps.isEmpty()) {
			// all requested properties existed in the Word doc and their new values have been
			// set
			return;
		}

		for (Map.Entry<String, String> newProp : newProps.entrySet()) {
			String name = newProp.getKey();
			String value = newProp.getValue();
			_logger.info(String.format("adding new custom property: %s = %s ", name, value));

			Dispatch.call(customProps, "Add", DocWordHelper.vString(name),
					DocWordHelper.getVariant(false), msoPropertyTypeString,
					DocWordHelper.vString(value));
		}
	}

	// --------------------- cursors ---------------------

	/**
	 * {@inheritDoc}
	 * <p>
	 * Implementation of closing/reopening the MS Word document.
	 */
	@Override
	public Cursor<Dispatch> closeAndReopenDoc(CursorList<Dispatch> cursors,
			Cursor<Dispatch> currentCursor) {
		long t0 = System.currentTimeMillis();

		List<Map.Entry<Integer, Integer>> indexes = cursors.snapshotIndexes();
		closeDoc();

		openDoc();
		setDocgenOptimisationOptions();

		Range<Dispatch> range = getDocumentAsRange();

		Cursor<Dispatch> result = null;
		for (int i = 0; i < cursors.size(); ++i) {
			Cursor<Dispatch> oldCursor = cursors.get(i);
			Placeholder ph = oldCursor.getPlaceholder();
			Map.Entry<Integer, Integer> startAndEnd = indexes.get(i);
			int start = startAndEnd.getKey().intValue();
			int end = startAndEnd.getValue().intValue();

			Range<Dispatch> limited = duplicateRange(range);
			limited.setStartEnd(start, end);

			Cursor<Dispatch> newCursor = createCursor(ph, limited);
			cursors.set(i, newCursor);
			if (ph == currentCursor.getPlaceholder()) {
				result = newCursor;
			}
		}
		Util.logCompletion(Level.INFO, "closed and reopened document.", t0,
				getInput().isSkipTiming());

		return result;
	}

	// --------- (scanning of ranges with) styles and caption labels -------------

	/**
	 * {@inheritDoc}
	 * <p>
	 * Implementation note: In earlier versions, tried with GetCrossReferenceItems on document, but
	 * that was not reliable, so now doing manual house-keeping to achieve predictable results.
	 */
	@Override
	public Map<CaptionKind, List<Caption<Dispatch>>> collectCaptionParagraphsAndFixLabelsAlsoInTOCs() {
		Map<CaptionKind, List<Caption<Dispatch>>> result = new LinkedHashMap<CaptionKind, List<Caption<Dispatch>>>();
		List<Caption<Dispatch>> figures = new ArrayList<Caption<Dispatch>>();
		result.put(CaptionKind.Figure, figures);
		List<Caption<Dispatch>> tables = new ArrayList<Caption<Dispatch>>();
		result.put(CaptionKind.Table, tables);

		EnumVariant paras = getIterator(getDocumentContent(), "Paragraphs");
		while (paras.hasMoreElements()) {
			Dispatch para = paras.nextElement().getDispatch();
			Range<Dispatch> paraRange = createRangeFromRangeOf(para);

			String styleName = getParagraphStyleName(para);

			if (!CaptionKind.Figure.getStyle().isRecognised(styleName)
					&& !CaptionKind.Table.getStyle().isRecognised(styleName)) {
				continue;
			}

			String text = paraRange.getText();
			String msgStyle = "   found paragraph [" + text + "] with caption style [" + styleName
					+ "]";

			List<String> tokens = Util.splitStringSeparatedTokens(text, " ");
			if (tokens.size() < 2) {
				String msgTooShortForCaption = "text is too short for a well-formed caption."
						+ "   Before complaining, PLEASE FIX THE TEMPLATE SO:"
						+ "     change in template the style to a non-caption style and rerun docgen.";
				_logger.error(String.format("%s, BUT %s", msgStyle, msgTooShortForCaption));
				continue;
			}

			_logger.info(msgStyle);
			String label = tokens.get(0);
			if (CaptionKind.Figure.looksLikeCaption(styleName, label)) {
				figures.add(createCaption(CaptionKind.Figure, paraRange));
				continue;
			} else if (CaptionKind.Table.looksLikeCaption(styleName, label)) {
				tables.add(createCaption(CaptionKind.Table, paraRange));
				continue;
			}

			String msgBadLabel = "caption label [" + label
					+ "] used at the start of paragraph is not recognised." + Util.NL
					+ "   Before complaining, PLEASE FIX THE TEMPLATE SO: " + Util.NL
					+ "   - If this is NOT a caption, change the style" + Util.NL
					+ "   - If this IS a caption and you want to keep the label, create in your"
					+ " Word template custom label [" + label + "].";
			_logger.warn(String.format("%s, BUT %s", msgStyle, msgBadLabel));
		}
		return result;
	}

	@Override
	public Range<Dispatch> createRange(Dispatch object) {
		return new DocWordRange(object);
	}

	@Override
	public final WordPatternFinder<Dispatch> createPatternFinder(String msPattern) {
		return new DocWordPatternFinder(_wordDoc, msPattern);
	}

	// ---------------- actual writing into output document -------------------

	@Override
	public void clearUndoCache() {
		Dispatch.call(_wordDoc, "UndoClear");
	}

	@Override
	public Range<Dispatch> getDocumentAsRange() {
		return createRange(getDocumentContent());
	}

	@Override
	public Range<Dispatch> duplicateRange(Range<Dispatch> range) {
		return createRange(Dispatch.call(range.getObject(), "Duplicate").toDispatch());
	}

	@Override
	public void prependText(Range<Dispatch> range, String newText) {
		Dispatch.call(range.getObject(), "InsertBefore", newText);
	}

	@Override
	public int getRangeParagraphOutlineLevel(Range<Dispatch> range, int paraIdx) {
		Dispatch paragraph = getParagraph(range.getObject(), paraIdx);
		if (paragraph == null) {
			return -1;
		}
		return Dispatch.get(paragraph, "OutlineLevel").getInt();
	}

	@Override
	public String appendRawTextInNewParagraphWithStyle(Range<Dispatch> range, String newText,
			Style style) {
		appendNewLine(range);
		return appendTextWithStyle(range, newText, style);
	}

	@Override
	public String appendHtmlTextInNewParagraphWithStyle(Range<Dispatch> range, String newMarkup,
			Style style) {
		_logger.info(">>> : " + range.getText());
		_logger.info("      " + newMarkup);
		if (newMarkup.trim().isEmpty()) {
			return "";
		}
		appendNewLine(range);
		collapseRangeToEnd(range);
		range.setEnd(range.getEnd() + 1);
		setRangeStyle(range.getObject(), style);
		Util.copyHtmlToClipboard(newMarkup);
		try {
			pasteHtmlFromClipboard(range.getObject());
		} catch (ApplicationException e) {
			_logger.warn(Arrays.deepToString(e.getStackTrace()));
		}
		// backspaceLast(range); // eat EOL - pasting HTML from clipboard adds an EOL
		return range.getText();
	}

	@Override
	public String appendTextWithStyle(Range<Dispatch> range, String newText, Style style) {
		collapseRangeToEnd(range);
		setRangeStyle(range.getObject(), style);
		return appendText(range, newText);
	}

	// ------ inserting a figure/table, its caption, and references to caption ------

	private static final Variant wdCaptionPositionAbove = DocWordHelper.vInt(0);
	private static final Variant wdCaptionPositionBelow = DocWordHelper.vInt(1);
	private static final Variant wdCharacter = DocWordHelper.vInt(1);
	private static final Variant wdParagraph = DocWordHelper.vInt(4);
	private static final Variant wdOnlyLabelAndNumber = DocWordHelper.vInt(3);

	protected static final boolean OLD_CAPT = true; // although slow, this is in fact faster !

	@Override
	public Dispatch insertFigureCaption(Range<Dispatch> range, int expectedSeqNum, String text) {
		// FIXME remove
		if (OLD_CAPT) {
			insertCaption(range, CaptionKind.Figure.getLabel(), wdCaptionPositionBelow, text,
					expectedSeqNum);

			int paraCount = 2;
			Dispatch.call(range.getObject(), "MoveEnd", wdParagraph, DocWordHelper.vInt(paraCount));

			Dispatch secondPara = getParagraph(range.getObject(), paraCount);
			setRangeStyle(secondPara, Style.figcapt);

			int endMinusOne = range.getEnd() - 1;
			range.setEnd(endMinusOne);
			collapseRangeToEnd(range);
			return null;
		}

		_logger.info("in insertFigureCaption(): range.txt='" + range.getText() + "', " + range);
		collapseRangeToEnd(range);
		appendTextInNewParagraphWithStyle(range, new TextDescription(), Style.figcapt);

		String fieldCode = String.format(seqCodeFigTabFmt, CaptionKind.Figure.getLabel());

		Range<Dispatch> seqField = insertCaptionManually(range, CaptionKind.Figure.getLabel(),
				fieldCode, text, String.valueOf(expectedSeqNum));

		return range.getObject();

	}

	@Override
	public void insertTableCaption(Range<Dispatch> range, int expectedSeqNum, String text) {
		Range<Dispatch> tableRange = getFirstTableAsRange(range.getObject());

		insertCaption(tableRange, CaptionKind.Table.getLabel(), wdCaptionPositionAbove, text,
				expectedSeqNum);

		Dispatch firstPara = getParagraph(tableRange.getObject(), 1);
		setRangeStyle(firstPara, Style.tabcapt);

		collapseRangeToEnd(range);

		firstPara = getParagraph(range.getObject(), 1);
		setRangeStyle(firstPara, Style.para); // was tabcell, set by insertTable, using
												// keepWithNext
	}

	public static final String SEP_AFTER_CAPTION_NUM = " " + Util.EN_DASH + " ";

	// for table, input range is first table, output caption is first para in range
	private static void insertCaption(Range<Dispatch> range, String label, Variant position,
			String text, int expectedSeqNum) {
		long t0 = System.currentTimeMillis();

		String titleIsTextAfterCaptionNumber = SEP_AFTER_CAPTION_NUM + text;

		// Label, Title, [TitleAutoText, Position, ExcludeLabel]
		Dispatch.call(range.getObject(), "InsertCaption", DocWordHelper.vString(label),
				DocWordHelper.vString(titleIsTextAfterCaptionNumber),
				DocWordHelper.VARIANT_EMPTY_STRING, position, DocWordHelper.vInt(0));

		_logger.info("... " + (System.currentTimeMillis() - t0) + " ms for insertCaption(): "
				+ label + " " + expectedSeqNum + titleIsTextAfterCaptionNumber);
	}

	private Range<Dispatch> insertCaptionManually(Range<Dispatch> range, String label,
			String fieldText, String captionDescription, String expectedSeqNum) {
		long t0 = System.currentTimeMillis();
		int startIdx = range.getStart();

		Level level = Level.TRACE;
		// FIXME - TRACE
		_logger.log(level,
				"in insertCaptionManually: r='" + range.getText() + "', " + range + "', fieldText='"
						+ fieldText + "', captionDescription='" + captionDescription + "'");
		String textBeforeSeqNum = label + " ";
		appendText(range, textBeforeSeqNum);
		_logger.log(level, "appendText       r = " + range + " '" + range.getText() + "'");

		// These 3 lines to be kept together: they work by miracle !!! See
		// https://social.msdn.microsoft.com/Forums/office/en-US/6e066caa-6657-476a-9072-0b51ce1e4430/adding-text-after-inserting-a-field-with-vba
		collapseRangeToEnd(range);
		Range<Dispatch> seqField = insertSeqField(range.getObject(), fieldText, expectedSeqNum);
		_logger.log(level, "insertSeqField   f = " + seqField + " '" + seqField.getText() + "'");
		_logger.log(level, "                 r = " + range + " '" + range.getText() + "'");
		moveStartChar(range, seqField.getText().length());
		_logger.log(level, "moveStartChar    f = " + seqField + " '" + seqField.getText() + "'");
		_logger.log(level, "                 r = " + range + " '" + range.getText() + "'");

		range.setStart(startIdx); // after doing with fields, we can resize back to start

		appendText(range, SEP_AFTER_CAPTION_NUM + captionDescription);
		_logger.log(level, "appendText       f = " + seqField + " '" + seqField.getText() + "'");
		_logger.log(level, "                 r = " + range + " '" + range.getText() + "'");

		_logger.info("... " + (System.currentTimeMillis() - t0)
				+ " ms for insertCaptionManually(): '" + range.getText() + "' " + range);
		return seqField;
	}

	/**
	 * Returns result (range) of inserted SEQ field and collapses <code>range</code> at end.
	 *
	 * @param range
	 * @param fieldText
	 * @param expectedSeqNum
	 *            if not null, will be compared to result and logged as error in case of mismatch
	 */
	private Range<Dispatch> insertSeqField(Dispatch range, String fieldText,
			String expectedSeqNum) {
		Dispatch fields = getFields(range);
		// Range, Type, Text, PreserveFormatting
		Dispatch field = Dispatch.call(fields, "Add", range, wdFieldSequence,
				DocWordHelper.vString(fieldText), DocWordHelper.VARIANT_INT_TRUE).getDispatch();

		// Apparently, above "Add" does not set the field code, so we must do it. We also must
		// do
		// update, otherwise result is "Error - Reference not found!" instead of seqNum.
		setFieldCode(field, fieldText, true);
		Range<Dispatch> seqNumRange = createRange(getFieldResult(field));
		String seqNumber = seqNumRange.getText();
		if (!String.valueOf(expectedSeqNum).equals(seqNumber)) {
			_logger.error("  inserted seqNum (" + seqNumRange.getText()
					+ ") differs from expected (" + expectedSeqNum + ")");
		}
		return seqNumRange;
	}

	/**
	 * {@inheritDoc}
	 * <p>
	 * This will fail with exception if caption not found.
	 */
	@Override
	public void insertCaptionRef(Range<Dispatch> range, CaptionKind kind, int idx,
			boolean introBeforeCaption) {
		long t0 = System.currentTimeMillis();

		String label = kind.getLabel();
		if (range == null || range.getObject() == null) {
			String ref = label + " " + idx;
			_logger.error("in insertCaptionRef '" + ref + "', range null ! Just returning.");
			return;
		}
		_logger.trace("^^ in insertCaptionRef: range.txt = '" + range.getText() + "', " + range);
		String separator = (introBeforeCaption) ? (" ") : ("");
		prependText(range, separator);
		_logger.trace("   prepend            : range.txt = '" + range.getText() + "', " + range);
		collapseRangeToStart(range);
		_logger.trace("   collapseStart      : range.txt = '" + range.getText() + "', " + range);

		// String strRefID = ? (label + " " + idx?) (bookmark created from this?);

		// InsertCrossReference(ReferenceType, ReferenceKind,
		// ReferenceItem, InsertAsHyperlink,
		// IncludePosition, SeparateNumbers,
		// SeparatorString
		Dispatch.call(range.getObject(), "InsertCrossReference", label, wdOnlyLabelAndNumber,
				DocWordHelper.vInt(idx), DocWordHelper.getVariant(true),
				DocWordHelper.getVariant(false), DocWordHelper.getVariant(false),
				DocWordHelper.VARIANT_EMPTY_STRING);
		_logger.info("... " + (System.currentTimeMillis() - t0) + " ms for insertCaptionRef()");
		_logger.info("--------------------------------------------------");
	}

	@Override
	public void insertFigure(Range<Dispatch> range, File pic) {
		range.setText("");
		// "Link to File" = false, "Save with Document" = true"
		Dispatch inlineShapes = Dispatch.call(range.getObject(), "InlineShapes").toDispatch();
		Dispatch.call(inlineShapes, "AddPicture", pic.getAbsolutePath(),
				DocWordHelper.VARIANT_FALSE, DocWordHelper.VARIANT_TRUE);
		range.setEnd(range.getEnd() + 1);
	}

	/**
	 * {@inheritDoc}
	 * <p>
	 * Returns expanded range to the end of the table. We do not collapse original range here, as we
	 * assume the caption will need to be inserted before the table.
	 */
	@Override
	public int insertTable(Range<Dispatch> range, PropertiesDoc doc, Style tabhead,
			boolean addBookmarks) {
		return doInsertTable(range, doc, tabhead, addBookmarks);
	}

	// ------------------------------ paragraphs ---------------------------

	@Override
	public String getRangeParagraphStyleName(Range<Dispatch> range, int paraIdx) {
		Dispatch paragraph = getParagraph(range.getObject(), paraIdx);
		return (paragraph == null) ? null : getParagraphStyleName(paragraph);
	}

	private Dispatch getParagraph(Dispatch range, int paraIdx) {
		Dispatch paragraphs = Dispatch.get(range, "Paragraphs").toDispatch();
		int count = DocWordHelper.getCount(paragraphs);
		if (paraIdx < 1 || paraIdx > count) {
			return null;
		}
		return DocWordHelper.getItem(paragraphs, paraIdx);
	}

	private String getParagraphStyleName(Dispatch paragraph) {
		Dispatch style = Dispatch.get(paragraph, "Style").toDispatch();
		return Dispatch.get(style, "NameLocal").getString();
	}

	@Override
	public int getRangeParagraphCount(Range<Dispatch> range) {
		Dispatch paragraphs = Dispatch.get(range.getObject(), "Paragraphs").toDispatch();
		return DocWordHelper.getCount(paragraphs);
	}

	@Override
	public void prependNewLine(Range<Dispatch> range) {
		Dispatch.call(range.getObject(), "InsertParagraphBefore");
	}

	@Override
	public void appendNewLine(Range<Dispatch> range) {
		Dispatch.call(range.getObject(), "InsertParagraphAfter");
	}

	@Override
	public String appendText(Range<Dispatch> range, String newText) {
		Dispatch.call(range.getObject(), "InsertAfter", newText);
		return range.getText();
	}

	@Override
	public String appendTextInNewParagraph(Range<Dispatch> range, String newText) {
		appendNewLine(range);
		Dispatch.call(range.getObject(), "InsertAfter", newText);
		return range.getText();
	}

	@Override
	public boolean isRangeWithTable(Dispatch range) {
		return null != getFirstTableAsRange(range);
	}

	private Range<Dispatch> getFirstTableAsRange(Dispatch container) {
		Dispatch tables = Dispatch.get(container, "Tables").toDispatch();
		Dispatch table = DocWordHelper.getItem(tables, 1);
		return createRangeFromRangeOf(table);
	}

	private static final Variant wdCollapseStart = DocWordHelper.vInt(1);
	private static final Variant wdCollapseEnd = DocWordHelper.vInt(0);

	@Override
	public void collapseRangeToEnd(Range<Dispatch> range) {
		Dispatch.call(range.getObject(), "Collapse", wdCollapseEnd);
	}

	@Override
	public void collapseRangeToStart(Range<Dispatch> range) {
		Dispatch.call(range.getObject(), "Collapse", wdCollapseStart);
	}

	@Override
	public void moveStartChar(Range<Dispatch> range, int count) {
		Dispatch.call(range.getObject(), "MoveStart", wdCharacter, DocWordHelper.vInt(count));
	}

	// ===== Impl. of org.tanjakostic.jcleancim.docgen.writer.word.WordWriter methods =====

	@Override
	public boolean applyCloseReopen() {
		return getInput().getSaveRecloseEvery() != -1;
	}

	// ===== Impl. of org.tanjakostic.jcleancim.docgen.writer.Writer methods =====

	public static final List<String> FILE_EXTENSIONS = Arrays.asList(".doc", ".docx");

	@Override
	public Set<String> getSupportedFormats() {
		return new HashSet<String>(FILE_EXTENSIONS);
	}
}
