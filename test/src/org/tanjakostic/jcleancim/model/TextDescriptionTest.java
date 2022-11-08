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

package org.tanjakostic.jcleancim.model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.tanjakostic.jcleancim.model.TextDescription;
import org.tanjakostic.jcleancim.model.TextDescription.TextKind;
import org.tanjakostic.jcleancim.util.HTMLUtil;
import org.tanjakostic.jcleancim.util.Util;

/**
 * @author tatjana.kostic@ieee.org
 * @version $Id: TextDescriptionTest.java 15 2016-07-12 15:11:42Z dev978 $
 */
public class TextDescriptionTest {

	// ============= Tests ===============

	@Test
	public final void testCtor() {
		TextDescription defTd = new TextDescription();

		assertEquals(TextDescription.DEFAULT_KIND, defTd.kind);
		assertEquals(TextDescription.DEFAULT_TEXT, defTd.text);
	}

	// -------------------

	@Test
	public final void testCtorText() {
		TextDescription defTd = new TextDescription("my text");

		assertEquals(TextDescription.DEFAULT_KIND, defTd.kind);
		assertEquals("my text", defTd.text);
	}

	@Test
	public final void testCtorTextTrimsWhitespace() {
		TextDescription defTd = new TextDescription("   my text   ");

		assertEquals(TextKind.textNoNL, defTd.kind);
		assertEquals("my text", defTd.text);
	}

	@Test
	public final void testCtorTextTrimsWhitespaceAndKeepsDefaultTextNoNL() {
		TextDescription defTd = new TextDescription(Util.NL + " \t  \t \r  " + Util.NL);

		assertEquals(TextKind.textNoNL, defTd.kind);
		assertEquals("", defTd.text);
	}

	// -------------------

	@Test
	public final void testCtorTextKindBothNull() {
		TextDescription td = new TextDescription(null, null);

		assertEquals(TextDescription.DEFAULT_KIND, td.kind);
		assertEquals(TextDescription.DEFAULT_TEXT, td.text);
		assertTrue("should be empty", td.isEmpty());
	}

	@Test
	public final void testCtorTextKind() {
		String textWithNewLine = "with" + Util.NL + "new line";
		TextDescription td = new TextDescription(textWithNewLine, TextKind.textWithNL);

		assertEquals(textWithNewLine, td.text);
		assertEquals(TextKind.textWithNL, td.kind);
		assertFalse("should not be empty", td.isEmpty());
	}

	@Test
	public final void testCtorTextKindTrimsWhitespace() {
		TextDescription td = new TextDescription("   my text   ", TextKind.textNoNL);

		assertEquals("my text", td.text);
		assertEquals(TextKind.textNoNL, td.kind);
		assertFalse("should not be empty", td.isEmpty());
	}

	@Test
	public final void testCtorTextKindTrimsWhitespaceAndKeepsDefaultTextNoNL() {
		TextDescription td = new TextDescription(Util.NL + " \t  \t \r  " + Util.NL,
				TextKind.textWithNL);

		assertEquals("", td.text);
		assertTrue("should be empty", td.isEmpty());
		assertEquals(TextKind.textNoNL, td.kind);
	}

	// --------------------------

	@Test
	public final void testPrependTextNoNL() {
		TextDescription td = new TextDescription("original", TextKind.textNoNL);
		String prefix = "pref ";

		TextDescription prepended = td.prepend(prefix);

		assertEquals("pref original", prepended.text);
		assertEquals(td.kind, prepended.kind);
	}

	@Test
	public final void testPrependHtmlSnippetDoesNotStartWithParaTag() {
		TextDescription td = new TextDescription("original</p>", TextKind.htmlSnippet);
		String prefix = "pref ";

		TextDescription prepended = td.prepend(prefix);

		assertEquals("<p>pref original</p>", prepended.text);
		assertEquals(td.kind, prepended.kind);
	}

	@Test
	public final void testPrependHtmlSnippetEmptyInitialContent() {
		TextDescription td = TextDescription.EMPTY_HTML;
		String prefix = "pref ";

		TextDescription prepended = td.prepend(prefix);

		assertEquals("<p>pref </p>", prepended.text);
		assertEquals(td.kind, prepended.kind);
	}

	@Test
	public final void testPrependHtmlSnippet() {
		TextDescription td = new TextDescription("<p>original</p>", TextKind.htmlSnippet);
		String prefix = "pref ";

		TextDescription prepended = td.prepend(prefix);

		assertEquals("<p>pref original</p>", prepended.text);
		assertEquals(td.kind, prepended.kind);
	}

	// --------------------------

	@Test
	public final void testAppendParagraphTxt() {
		TextDescription td = new TextDescription("original", TextKind.textNoNL);
		String paras = "New paragraph" + Util.NL + "One more";

		TextDescription appended = td.appendParagraph(paras);
		String expected = td.text + Util.NL + paras;

		assertEquals(expected, appended.text);
		assertEquals(TextKind.textWithNL, appended.kind);
	}

	@Test
	public final void testAppendParagraphHtml() {
		TextDescription td = new TextDescription(HTMLUtil.P_START + "original" + HTMLUtil.P_END,
				TextKind.htmlSnippet);
		String paras = "New paragraph" + HTMLUtil.P_START + "One more";

		TextDescription appended = td.appendParagraph(paras);
		String expected = td.text + HTMLUtil.P_START + paras + HTMLUtil.P_END;

		assertEquals(expected, appended.text);
		assertEquals(td.kind, appended.kind);
	}
}
