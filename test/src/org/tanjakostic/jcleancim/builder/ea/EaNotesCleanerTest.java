/**
 * Redistribution and use in source and binary forms, with or without modification, are permitted.
 * <p>
 * THIS SOFTWARE IS PROVIDED BY THE AUTHORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES,
 * INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A
 * PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE AUTHORS BE LIABLE FOR ANY DIRECT,
 * INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR ONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT
 * LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package org.tanjakostic.jcleancim.builder.ea;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.tanjakostic.jcleancim.util.HTMLUtil;
import org.tanjakostic.jcleancim.util.Util;

/**
 * The fixture data corresponds to what EA GetNotes() method (and variations) return, and contains
 * all of formatting available with EA 9.3.
 *
 * @author tatjana.kostic@ieee.org
 * @version $Id: EaNotesCleanerTest.java 31 2019-12-08 01:19:54Z dev978 $
 */
public class EaNotesCleanerTest {

	static final String[] eaGetNotesLines = new String[41];
	static {
		eaGetNotesLines[0] = Util.NL;
		eaGetNotesLines[1] = "(paraStart)Some intro (&amp; &lt; &gt;) - next line is empty.(paraEnd)"
				+ Util.NL;
		eaGetNotesLines[2] = Util.NL;
		eaGetNotesLines[3] = "(paraStart)This is a <font color=\"#ff0000\">(red)</font> test class to exercise the \"quasi\" mark-up that EA java API returns from its various getNotes() methods. Using its provided method to return HTML is very slow.(paraEnd)"
				+ Util.NL;
		eaGetNotesLines[4] = "(paraStart)Here is the unordered list with two items:(paraEnd)"
				+ Util.NL;
		eaGetNotesLines[5] = "<ul>" + Util.NL;
		eaGetNotesLines[6] = "	<li>list test</li>" + Util.NL;
		eaGetNotesLines[7] = "	<li><b>bold</b> list test</li>" + Util.NL;
		eaGetNotesLines[8] = "</ul>" + Util.NL;
		eaGetNotesLines[9] = "(paraStart)This the paragraph after a list.(paraEnd)" + Util.NL;
		eaGetNotesLines[10] = "(paraStart)Follows the numbered list:(paraEnd)" + Util.NL;
		eaGetNotesLines[11] = "<ol>" + Util.NL;
		eaGetNotesLines[12] = "	<li>first numbered item</li>" + Util.NL;
		eaGetNotesLines[13] = "	<li><i>italic</i> numbered test</li>" + Util.NL;
		eaGetNotesLines[14] = "	<li><i>RTY</i><sup>-8</sup>-dws<i><sub>2</sub></i>=56 (RTY**-8-dws subscript 2=56</li>"
				+ Util.NL;
		eaGetNotesLines[15] = "</ol>" + Util.NL;
		eaGetNotesLines[16] = "(paraStart)Below we test one bulleted and one numbered item:(paraEnd)"
				+ Util.NL;
		eaGetNotesLines[17] = "<ul>" + Util.NL;
		eaGetNotesLines[18] = "	<li>bulleted</li>" + Util.NL;
		eaGetNotesLines[19] = "</ul>" + Util.NL;
		eaGetNotesLines[20] = "<ol>" + Util.NL;
		eaGetNotesLines[21] = "	<li>numbered</li>" + Util.NL;
		eaGetNotesLines[22] = "</ol>" + Util.NL;
		eaGetNotesLines[23] = "(paraStart)And the inverse, with two numbered items (second is empty) and two bulleted items (first is empty):(paraEnd)"
				+ Util.NL;
		eaGetNotesLines[24] = "<ol>" + Util.NL;
		eaGetNotesLines[25] = "	<li>this one is numbered</li>" + Util.NL;
		eaGetNotesLines[26] = "	<li>&nbsp;</li>" + Util.NL;
		eaGetNotesLines[27] = "</ol>" + Util.NL;
		eaGetNotesLines[28] = "<ul>" + Util.NL;
		eaGetNotesLines[29] = "	<li>&nbsp;</li>" + Util.NL;
		eaGetNotesLines[30] = "	<li>and this one is bulleted</li>" + Util.NL;
		eaGetNotesLines[31] = "</ul>" + Util.NL;
		eaGetNotesLines[32] = "(paraStart)Last <i><u>normal </u></i>para test - there are two empty paragraphs below.(paraEnd)"
				+ Util.NL;
		eaGetNotesLines[33] = Util.NL;
		eaGetNotesLines[34] = Util.NL;
		eaGetNotesLines[35] = "(note) J/<b>m²</b>." + Util.NL;
		eaGetNotesLines[36] = "\t\tPara with white space around\t\t" + Util.NL;
		eaGetNotesLines[37] = "(note) <b>bold µ</b>. Last three lines empty, second with a tab in the middle."
				+ Util.NL;
		eaGetNotesLines[38] = Util.NL;
		eaGetNotesLines[39] = "   \t  " + Util.NL;
		eaGetNotesLines[40] = Util.NL;
	}

	/** Concatenates specified lines from {@link #eaGetNotesLines} fixture into single string. */
	static String concatGetNotes(int... lineNumbers) {
		List<String> lines = new ArrayList<String>();
		for (int lineNum : lineNumbers) {
			lines.add(eaGetNotesLines[lineNum]);
		}
		return Util.concatCharSeparatedTokens(null, lines);
	}

	static final List<String> eaGetFormatFromFieldTxts = new ArrayList<String>();
	static {
		eaGetFormatFromFieldTxts
				.add("(paraStart)Some intro (& < >) - next line is empty.(paraEnd)");
		eaGetFormatFromFieldTxts.add(
				"(paraStart)This is a (red) test class to exercise the \"quasi\" mark-up that EA java API returns from its various getNotes() methods. Using its provided method to return HTML is very slow.(paraEnd)");
		eaGetFormatFromFieldTxts
				.add("(paraStart)Here is the unordered list with two items:(paraEnd)");
		eaGetFormatFromFieldTxts.add("- list test");
		eaGetFormatFromFieldTxts.add("- bold list test");
		eaGetFormatFromFieldTxts.add("(paraStart)This the paragraph after a list.(paraEnd)");
		eaGetFormatFromFieldTxts.add("(paraStart)Follows the numbered list:(paraEnd)");

		eaGetFormatFromFieldTxts.add("1. first numbered item");
		eaGetFormatFromFieldTxts.add("2. italic numbered test");
		eaGetFormatFromFieldTxts.add("3. RTY-8-dws2=56 (RTY**-8-dws subscript 2=56");
		eaGetFormatFromFieldTxts
				.add("(paraStart)Below we test one bulleted and one numbered item:(paraEnd)");
		eaGetFormatFromFieldTxts.add("- bulleted");
		eaGetFormatFromFieldTxts.add("1. numbered");
		eaGetFormatFromFieldTxts.add(
				"(paraStart)And the inverse, with two numbered items (second is empty) and two bulleted items (first is empty):(paraEnd)");
		eaGetFormatFromFieldTxts.add("1. this one is numbered");
		eaGetFormatFromFieldTxts.add("2."); // <li>&nbsp;</li>
		eaGetFormatFromFieldTxts.add("-"); // <li>&nbsp;</li>
		eaGetFormatFromFieldTxts.add("- and this one is bulleted");
		eaGetFormatFromFieldTxts.add(
				"(paraStart)Last normal para test - there are two empty paragraphs below.(paraEnd)");
		eaGetFormatFromFieldTxts.add("(note) J/m².");
		eaGetFormatFromFieldTxts.add("Para with white space around");
		eaGetFormatFromFieldTxts
				.add("(note) bold µ. Last three lines empty, second with a tab in the middle.");
	}

	private EaNotesCleaner _cleaner;

	@Before
	public void setUp() {
		_cleaner = new EaNotesCleaner();
	}

	// ============= Tests ===============

	@Test
	public final void testCleanHtmlInputNull() {
		assertEquals("", _cleaner.cleanHtml(null));
	}

	@Test
	public final void testCleanHtmlInputWhitespaceOnly() {
		// eaLines[38]: Util.NL);
		// eaLines[39]: " \t " + Util.NL);
		// eaLines[40]: Util.NL);
		String input = concatGetNotes(38, 39, 40);

		List<String> expecteds = new ArrayList<String>();
		expecteds.add(HTMLUtil.P_START + HTMLUtil.NBSP + HTMLUtil.P_END + Util.NL);
		expecteds.add(HTMLUtil.P_START + HTMLUtil.NBSP + HTMLUtil.P_END + Util.NL);
		expecteds.add(HTMLUtil.P_START + HTMLUtil.NBSP + HTMLUtil.P_END);
		String expected = Util.concatCharSeparatedTokens(null, expecteds);

		assertEquals(expected, _cleaner.cleanHtml(input));
	}

	@Test
	public final void testCleanHtmlInputParaAndList() {
		// eaLines[0]: Util.NL);
		// eaLines[1]: "(paraStart)Some intro (&amp; &lt; &gt;) - next line is empty.(paraEnd)"
		// eaLines[5]: "<ul>" + Util.NL);
		// eaLines[6]: " <li>list test</li>" + Util.NL);
		// eaLines[8]: "</ul>" + Util.NL);
		// eaLines[24]: "<ol>" + Util.NL);
		// eaLines[25]: " <li>this one is numbered</li>" + Util.NL);
		// eaLines[26]: " <li>&nbsp;</li>" + Util.NL);
		// eaLines[27]: "</ol>" + Util.NL);
		// eaLines[36]: "\t\tPara with white space around\t\t" + Util.NL);
		String input = concatGetNotes(0, 1, 5, 6, 8, 24, 25, 26, 27, 36);

		List<String> expecteds = new ArrayList<String>();
		expecteds.add(HTMLUtil.P_START + HTMLUtil.NBSP + HTMLUtil.P_END + Util.NL);
		expecteds.add(HTMLUtil.P_START
				+ "(paraStart)Some intro (&amp; &lt; &gt;) - next line is empty.(paraEnd)"
				+ HTMLUtil.P_END + Util.NL);
		expecteds.add("<ul>" + Util.NL);
		expecteds.add("	<li>list test</li>" + Util.NL);
		expecteds.add("</ul>" + Util.NL);
		expecteds.add("<ol>" + Util.NL);
		expecteds.add("	<li>this one is numbered</li>" + Util.NL);
		expecteds.add("	<li>&nbsp;</li>" + Util.NL);
		expecteds.add("</ol>" + Util.NL);
		expecteds.add(HTMLUtil.P_START + "\t\tPara with white space around\t\t" + HTMLUtil.P_END);
		String expected = Util.concatCharSeparatedTokens(null, expecteds);

		assertEquals(expected, _cleaner.cleanHtml(input));
	}

	// -------------------------

	@Test
	public final void testCleanAndCompactHtmlInputNull() {
		assertEquals("", _cleaner.cleanAndCompactHtml(null));
	}

	@Test
	public final void testCleanAndCompactHtmlInputWhitespaceOnly() {
		// eaLines[38]: Util.NL);
		// eaLines[39]: " \t " + Util.NL);
		// eaLines[40]: Util.NL);
		String input = concatGetNotes(38, 39, 40);

		assertEquals("", _cleaner.cleanAndCompactHtml(input));
	}

	@Test
	public final void testCleanAndCompactHtmlInputParaAndList() {
		// eaLines[0]: Util.NL);
		// eaLines[1]: "(paraStart)Some intro (&amp; &lt; &gt;) - next line is empty.(paraEnd)"
		// eaLines[5]: "<ul>" + Util.NL);
		// eaLines[6]: " <li>list test</li>" + Util.NL);
		// eaLines[8]: "</ul>" + Util.NL);
		// eaLines[24]: "<ol>" + Util.NL);
		// eaLines[25]: " <li>this one is numbered</li>" + Util.NL);
		// eaLines[26]: " <li>&nbsp;</li>" + Util.NL);
		// eaLines[27]: "</ol>" + Util.NL);
		// eaLines[36]: "\t\tPara with white space around\t\t" + Util.NL);
		// eaLines[38]: Util.NL);
		// eaLines[39]: " \t " + Util.NL);
		// eaLines[40]: Util.NL);
		String input = concatGetNotes(0, 1, 5, 6, 8, 24, 25, 26, 27, 36, 38, 39, 40);

		StringBuilder expected = new StringBuilder();
		expected.append(HTMLUtil.P_START
				+ "(paraStart)Some intro (&amp; &lt; &gt;) - next line is empty.(paraEnd)"
				+ HTMLUtil.P_END);
		expected.append("<ul>");
		expected.append("<li>list test</li>");
		expected.append("</ul>");
		expected.append("<ol>");
		expected.append("<li>this one is numbered</li>");
		expected.append("<li>&nbsp;</li>");
		expected.append("</ol>");
		expected.append(HTMLUtil.P_START + "Para with white space around" + HTMLUtil.P_END);

		String expString = expected.toString();
		assertEquals(expString, _cleaner.cleanAndCompactHtml(input));
	}

	// -----------------

	@Test
	public final void testCleanAndCompactTextInputNull() {
		assertEquals(Collections.emptyList(), _cleaner.cleanAndCompactText(null));
	}

	@Test
	public final void testCleanAndCompactTextInputWhitespaceOnly() {
		// eaLines[38]: Util.NL);
		// eaLines[39]: " \t " + Util.NL);
		// eaLines[40]: Util.NL);
		String input = concatGetNotes(38, 39, 40);

		assertEquals(Collections.emptyList(), _cleaner.cleanAndCompactText(input));
	}

	// -----------------

	@Test
	public final void testCleanListTextUnorderedList() {
		List<String> eaLines = new ArrayList<String>(4);
		eaLines.add("<ul>");
		eaLines.add(
				"	<li>list <font color=\"#ff0000\">(red)</font> test with <sub>sub</sub></li>");
		eaLines.add("	<li><b>bold</b> \"list\" test</li>");
		eaLines.add("</ul>");

		List<String> expectedRawLines = new ArrayList<String>(2);
		expectedRawLines.add("- list (red) test with sub");
		expectedRawLines.add("- bold \"list\" test");

		List<String> outputRawLines = new ArrayList<String>();

		assertEquals("idx of last </li>", 2,
				_cleaner.cleanListText(false, 0, eaLines, outputRawLines));
		assertEquals("two list items", 2, outputRawLines.size());
		assertEquals(expectedRawLines, outputRawLines);
	}

	@Test
	public final void testCleanListTextOrderedList() {
		List<String> eaLines = new ArrayList<String>(5);
		eaLines.add("(paraStart)This is a test class ...(paraEnd)");
		eaLines.add("<ol>");
		eaLines.add(
				"	<li>this one (<i><u>italic-underlined</u></i>) is &lt;100, <sup>sup</sup> &gt;1 numbered</li>");
		eaLines.add("	<li>&nbsp;</li>");
		eaLines.add("</ol>");

		List<String> expectedRawLines = new ArrayList<String>(2);
		expectedRawLines.add("1. this one (italic-underlined) is <100, sup >1 numbered");
		expectedRawLines.add("2.");

		List<String> outputRawLines = new ArrayList<String>();

		assertEquals("idx of last </li>", 3,
				_cleaner.cleanListText(true, 1, eaLines, outputRawLines));
		assertEquals("two list items", 2, outputRawLines.size());
		assertEquals(expectedRawLines, outputRawLines);
	}

	// -----------------

	@Test
	public final void testCleanAndCompactTextInputParaAndList() {
		List<String> lines = Arrays.asList(eaGetNotesLines);
		String input = Util.concatCharSeparatedTokens(null, lines);

		assertEquals(eaGetFormatFromFieldTxts, _cleaner.cleanAndCompactText(input));
	}
}
