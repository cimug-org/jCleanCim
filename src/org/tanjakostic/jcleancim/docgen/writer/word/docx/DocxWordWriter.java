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

package org.tanjakostic.jcleancim.docgen.writer.word.docx;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;
import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFStyles;
import org.tanjakostic.jcleancim.docgen.UnsupportedInputFormatException;
import org.tanjakostic.jcleancim.docgen.UnsupportedOutputFormatException;
import org.tanjakostic.jcleancim.docgen.collector.PropertiesDoc;
import org.tanjakostic.jcleancim.docgen.writer.Caption;
import org.tanjakostic.jcleancim.docgen.writer.CaptionKind;
import org.tanjakostic.jcleancim.docgen.writer.ExistingStyle;
import org.tanjakostic.jcleancim.docgen.writer.Range;
import org.tanjakostic.jcleancim.docgen.writer.Style;
import org.tanjakostic.jcleancim.docgen.writer.word.AbstractWordWriter;
import org.tanjakostic.jcleancim.docgen.writer.word.WordPatternFinder;
import org.tanjakostic.jcleancim.docgen.writer.word.WordWriterInput;

/**
 * @author tatjana.kostic@ieee.org
 * @author laurent.guise@art-et-histoire.com
 * @version $Id: DocxWordWriter.java 34 2019-12-20 18:37:17Z dev978 $
 */
public class DocxWordWriter extends AbstractWordWriter<Object> {
	private static final Logger _logger = Logger.getLogger(DocxWordWriter.class.getName());

	private XWPFDocument _wordDoc;

	private XWPFStyles _styles;
	private Object _bookmarks;
	private Object _hyperlinks;

	/**
	 * Constructor.
	 *
	 * @param input
	 * @throws UnsupportedInputFormatException
	 * @throws UnsupportedOutputFormatException
	 * @throws IOException
	 */
	public DocxWordWriter(WordWriterInput input)
			throws UnsupportedInputFormatException, UnsupportedOutputFormatException, IOException {
		super(input);
	}

	// ===== Impl. of org.tanjakostic.jcleancim.docgen.writer.word.WordHelper methods =====

	@Override
	public void openDoc() throws IOException {
		_logger.info("  opening MSWord (.docx) file '" + getInput().getOutFilePath() + "'");
		_wordDoc = new XWPFDocument(OPCPackage.create(getInput().getOutFilePath()));

		_styles = _wordDoc.getStyles();
	}

	@Override
	public void closeDoc() throws IOException {
		if (_wordDoc != null) {
			_logger.info("closing MSWord file '" + getInput().getOutFilePath() + "'");
			_wordDoc.close();
		}
	}

	// --------- bookmarks and hyperlinks -------------

	@Override
	public void insertBookmark(Range<Object> range, String label) {
		// TODO Auto-generated method stub

	}

	@Override
	public void insertHyperlink(Range<Object> range, String textToDisplay, String url) {
		// TODO Auto-generated method stub

	}

	// --------- styles and caption labels -------------

	@Override
	public Map<String, ExistingStyle> getExistingStyles() {
		throw new RuntimeException("Not implemented !");
	}

	// ---------------- TOCs ---------------

	@Override
	public void updateFields() {
		// TODO Auto-generated method stub

	}

	@Override
	public void updateTablesOf(String what) {
		// TODO Auto-generated method stub

	}

	// ----------------------- custom properties -----------------------

	@Override
	public Map<String, String> getCustomDocProperties() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setCustomDocProperties(Map<String, String> newCustomProps) {
		// TODO Auto-generated method stub

	}

	// --------------------- cursors ---------------------

	@Override
	public Map<CaptionKind, List<Caption<Object>>> collectCaptionParagraphsAndFixLabelsAlsoInTOCs() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Range<Object> createRange(Object object) {
		return new DocxWordRange(object);
	}

	@Override
	public WordPatternFinder<Object> createPatternFinder(String pattern) {
		// TODO Auto-generated method stub
		return null;
	}

	// ---------------- actual writing into output document -------------------

	@Override
	public Range<Object> getDocumentAsRange() {
		// TODO Should this go into abstract supertype?
		return null;
	}

	@Override
	public Range<Object> duplicateRange(Range<Object> range) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void prependText(Range<Object> range, String newText) {
		// TODO Auto-generated method stub

	}

	@Override
	public int getRangeParagraphOutlineLevel(Range<Object> range, int paraIdx) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public String appendRawTextInNewParagraphWithStyle(Range<Object> range, String newText,
			Style style) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String appendHtmlTextInNewParagraphWithStyle(Range<Object> range, String newMarkup,
			Style style) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String appendTextWithStyle(Range<Object> range, String newText, Style style) {
		// TODO Auto-generated method stub
		return null;
	}

	// ------ inserting a figure/table, its caption, and references to caption ------

	@Override
	public Object insertFigureCaption(Range<Object> range, int expectedSeqNum, String captionText) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void insertTableCaption(Range<Object> range, int expectedSeqNum, String captionText) {
		// TODO Auto-generated method stub

	}

	@Override
	public void insertCaptionRef(Range<Object> insertPointRange, CaptionKind kind, int figNumber,
			boolean introBeforeCaption) {
		// TODO Auto-generated method stub

	}

	@Override
	public void insertFigure(Range<Object> range, File pic) {
		// TODO Auto-generated method stub

	}

	@Override
	public int insertTable(Range<Object> range, PropertiesDoc doc, Style tabhead,
			boolean addBookmarks) {
		// TODO Auto-generated method stub
		return 0;
	}

	// ------------------------------ tests only ---------------------------

	@Override
	public String getRangeParagraphStyleName(Range<Object> range, int paraIdx) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int getRangeParagraphCount(Range<Object> range) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void prependNewLine(Range<Object> range) {
		// TODO Auto-generated method stub

	}

	@Override
	public void appendNewLine(Range<Object> range) {
		// TODO Auto-generated method stub

	}

	@Override
	public String appendText(Range<Object> range, String newText) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String appendTextInNewParagraph(Range<Object> range, String newText) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean isRangeWithTable(Object range) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void collapseRangeToEnd(Range<Object> range) {
		// TODO Auto-generated method stub

	}

	@Override
	public void collapseRangeToStart(Range<Object> range) {
		// TODO Auto-generated method stub

	}

	@Override
	public void moveStartChar(Range<Object> range, int count) {
		// TODO Auto-generated method stub

	}

	// ===== Impl. of org.tanjakostic.jcleancim.docgen.writer.word.WordWriter methods =====

	@Override
	public boolean applyCloseReopen() {
		return false;
	}

	// ===== Impl. of org.tanjakostic.jcleancim.docgen.writer.Writer methods =====

	public static final List<String> FILE_EXTENSIONS = Arrays.asList(".docx");

	@Override
	public Set<String> getSupportedFormats() {
		return new HashSet<String>(FILE_EXTENSIONS);
	}
}
