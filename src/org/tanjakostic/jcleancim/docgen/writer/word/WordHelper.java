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
import java.util.List;
import java.util.Map;

import org.tanjakostic.jcleancim.docgen.collector.PropertiesDoc;
import org.tanjakostic.jcleancim.docgen.writer.Caption;
import org.tanjakostic.jcleancim.docgen.writer.CaptionKind;
import org.tanjakostic.jcleancim.docgen.writer.Cursor;
import org.tanjakostic.jcleancim.docgen.writer.CursorList;
import org.tanjakostic.jcleancim.docgen.writer.ExistingStyle;
import org.tanjakostic.jcleancim.docgen.writer.Placeholder;
import org.tanjakostic.jcleancim.docgen.writer.Range;
import org.tanjakostic.jcleancim.docgen.writer.Style;
import org.tanjakostic.jcleancim.model.TextDescription;

/**
 * @author tatjana.kostic@ieee.org
 * @author laurent.guise@art-et-histoire.com
 * @version $Id: WordHelper.java 34 2019-12-20 18:37:17Z dev978 $
 */
public interface WordHelper<O> {

	/** Page width in cm. */
	public static final int PAGE_WIDTH = 16;

	/** Number of points for 1cm (from vba doc). */
	public static final double POINTS_FOR_1CM = 28.35;

	// -------------- Test enablers --------------

	/**
	 * Used for testing only, to play with pure Word stuff, without the notion of the model.
	 *
	 * @author tatjana.kostic@ieee.org
	 * @version $Id: WordHelper.java 34 2019-12-20 18:37:17Z dev978 $
	 */
	public interface PostProcessor {
		void postProcess();
	}

	/**
	 * If test wants to only read, pass a null <code>pp</code>. Otherwise, to perform writing
	 * different from the real implementation (for testing purposes), ensure to pass in non-null
	 * <code>pp</code> that implements test-specific writing.
	 */
	public void writeByTest(PostProcessor pp);

	// --------- Word app and document lifecycle -------------

	/** Where applicable, launches (and caches) the MS Word application. */
	public void createWordApp();

	/** Returns the MS Word application name. */
	public String getWordAppName();

	/** Returns the MS Word application version. */
	public String getWordAppVersion();

	/** Creates (and caches) the MS Word document. */
	public void openDoc() throws IOException;

	/** Closes and saves the MS Word document. */
	public void closeDoc() throws IOException;

	/** Saves MS Word document (and where applicable, exits MS Word application). */
	public void exitAppAndSaveDocument() throws IOException;

	// ------------------- application & document optimisation options ---------------

	/**
	 * Where applicable, initialises and stores MS Word application options (speed of doc
	 * generation) to original values.
	 */
	public void initDocgenOptimisationOptions();

	/**
	 * Where applicable, sets MS Word application options to speed performance of doc generation.
	 */
	public void setDocgenOptimisationOptions();

	/**
	 * Where applicable, brings back the original MS Word application options to values stored with
	 * the call to {@link #initDocgenOptimisationOptions()}.
	 */
	public void unsetDocgenOptimisationOptions();

	// --------------------- bookmark & hyperlinks ---------------------

	public void insertBookmark(Range<O> range, String label);

	public void insertHyperlink(Range<O> range, String textToDisplay, String url);

	// ---------------- styles and caption labels ---------------

	/**
	 * Returns non-empty map of non-null styles read from an open document, with style name as key.
	 */
	public Map<String, ExistingStyle> getExistingStyles();

	// ---------------- TOCs ---------------

	public void updateFields();

	public void updateTablesOf(String what);

	// ----------------------- custom properties -----------------------

	public Map<String, String> getCustomDocProperties();

	public void setCustomDocProperties(Map<String, String> newCustomProps);

	// --------------------- cursors ---------------------

	public CursorList<O> getCursors();

	public boolean isInTOC(Range<O> range);

	public Cursor<O> closeAndReopenDoc(CursorList<O> cursors, Cursor<O> currentCursor);

	/**
	 * Expected to be called after styles and caption labels get properly initialised from the
	 * current open document. To support non-English versions of Word, we have lot of fixes to
	 * apply, in particular with respect to how Word handles caption labels and TOCs of figures and
	 * tables. We must handle document with pre-existing captions and TOCs, so we must retrofit
	 * those to work with the deduced caption labels and styles.
	 */
	public Map<CaptionKind, List<Caption<O>>> collectCaptionParagraphsAndFixLabelsAlsoInTOCs();

	public Range<O> createRange(O object);

	public Cursor<O> createCursor(Placeholder ph, Range<O> limited);

	public Caption<O> createCaption(CaptionKind figure, Range<O> range);

	/**
	 * This one scans the whole initial document and initialises placeholders, with text, ranges and
	 * counts of existing tables and figures (captions) before each of them. This is essential for
	 * correct references to table and figure captions that we create on the fly. Returns the list
	 * of cursors correctly initialised.
	 */
	public CursorList<O> scanPlaceholderRanges(String pattern, List<Range<O>> figCaptionRanges,
			List<Range<O>> tabCaptionRanges);

	/**
	 * This one scans for the 3rd time the document, for hyperlinks, after all the writing has
	 * completed after the 2nd scan.
	 */
	public CursorList<O> scanHyperlinkPlaceholderRanges(String pattern);

	public WordPatternFinder<O> createPatternFinder(String pattern);

	// ---------------- actual writing into output document -------------------

	/**
	 * When you have large documents and you use a binary (COM) API, you will want to call this one
	 * regularly (e.g., for each class doc), so you don't get Word pop-up windows "memory
	 * insufficient. Do you want to continue?"
	 */
	public void clearUndoCache();

	public Range<O> getDocumentAsRange();

	/** FIXME: could go to Range? */
	public Range<O> duplicateRange(Range<O> range);

	/**
	 * Prepends text; prepended paragraph will have the same style as the one in range. FIXME: move
	 * to Range?
	 */
	public void prependText(Range<O> range, String newText);

	public int getRangeParagraphOutlineLevel(Range<O> range, int paraIdx);

	public String appendTextInNewParagraphWithStyle(Range<O> range, TextDescription newText,
			Style style);

	public String appendRawTextInNewParagraphWithStyle(Range<O> range, String newText, Style style);

	public String appendHtmlTextInNewParagraphWithStyle(Range<O> range, String newMarkup,
			Style style);

	public String appendTextWithStyle(Range<O> range, String newText, Style style);

	public O insertFigureCaption(Range<O> range, int expectedSeqNum, String captionText);

	public void insertTableCaption(Range<O> range, int expectedSeqNum, String captionText);

	/**
	 * Inserts reference to the caption <code>tabNumber</code> at the start of
	 * <code>insertPointRange</code>.
	 *
	 * @param introBeforeCaption
	 *            TODO
	 */
	public void insertCaptionRef(Range<O> insertPointRange, CaptionKind kind, int tabNumber,
			boolean introBeforeCaption);

	/**
	 * Input <code>range</code> includes any potential text within a paragraph. The method will
	 * overwrite that text with the figure from <code>pic</code>, and resulting range spans to the
	 * start of the paragraph following the inserted <code>pic</code>.
	 */
	public void insertFigure(Range<O> range, File pic);

	public int insertTable(Range<O> range, PropertiesDoc doc, Style tabhead, boolean addBookmarks);

	// ------------------------ Util methods ------------------------

	public String getRangeParagraphStyleName(Range<O> range, int paraIdx);

	public int getRangeParagraphCount(Range<O> range);

	public void prependNewLine(Range<O> range);

	public void appendNewLine(Range<O> range);

	public String appendText(Range<O> range, String newText);

	public String appendTextInNewParagraph(Range<O> range, String newText);

	public boolean isRangeWithTable(O range);

	public void collapseRangeToEnd(Range<O> range);

	public void collapseRangeToStart(Range<O> range);

	public void moveStartChar(Range<O> range, int count);

}
