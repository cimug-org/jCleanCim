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

package org.tanjakostic.jcleancim.docgen.collector;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.tanjakostic.jcleancim.docgen.collector.EntryDoc.Kind;
import org.tanjakostic.jcleancim.model.TextDescription.TextKind;
import org.tanjakostic.jcleancim.util.Util;

/**
 * @author tatjana.kostic@ieee.org
 * @version $Id: EntryDocTestCase.java 15 2016-07-12 15:11:42Z dev978 $
 */
abstract public class EntryDocTestCase {

	static final String BOOKMARK_ID = "toto";
	static final String[] VALUES_3 = new String[] { "name3", "33", "desc" };
	static final String[] VALUES_4 = new String[] { "name4", "type", "desc", "moc" };
	static final String[] VALUES_WITHNULL_3 = new String[] { "c1", null, "c3" };
	static final String[] VALUES_WITHEMPTY_3 = new String[] { "c1", "c2", "" };

	abstract public EntryDoc createTableName(String name, int columnCount);

	abstract public EntryDoc createGroupSubhead(String subheadTxt, int columnCount);

	abstract public EntryDoc createColumnLabels(String... values);

	abstract public EntryDoc createData(String bookmarkID, FormatInfo formatInfo, String... values);

	// ============= Tests ===============

	@Test(expected = NullPointerException.class)
	public final void testCtorTableNameNull() {
		createTableName(null, 3);
	}

	@Test(expected = IllegalArgumentException.class)
	public final void testCtorTableNameEmpty() {
		createTableName("  \n  \t ", 3);
	}

	@Test(expected = IllegalArgumentException.class)
	public final void testCtorTableNameColCount0() {
		createTableName("name", 0);
	}

	@Test(expected = IllegalArgumentException.class)
	public final void testCtorTableNameColCountNegative() {
		createTableName("name", -1);
	}

	@Test
	public final void testCtorTableNameGetters() {
		String name = "table name";
		EntryDoc entryDoc = createTableName(name, 3);

		assertTitleNameOrSubheadCreatedCorrectly(entryDoc, name, EntryDoc.Kind.tableName);
	}

	// assumes column count is 3
	private void assertTitleNameOrSubheadCreatedCorrectly(EntryDoc entryDoc, String expectedName,
			Kind expectedKind) {
		assertEquals(expectedKind, entryDoc.getKind());

		assertEquals(3, entryDoc.getValues().length);
		assertEquals("first value is name", expectedName, entryDoc.getValues()[0]);
		assertTrue("other values (2) are empty", entryDoc.getValues()[1].isEmpty());
		assertTrue("other values (3) are empty", entryDoc.getValues()[2].isEmpty());

		assertNull("should have no formatting", entryDoc.getFormatInfo());
	}

	// -----------------------------------

	@Test(expected = NullPointerException.class)
	public final void testCtorGroupSubheadNull() {
		createGroupSubhead(null, 3);
	}

	@Test(expected = IllegalArgumentException.class)
	public final void testCtorGroupSubheadEmpty() {
		createGroupSubhead("  \n  \t ", 3);
	}

	@Test(expected = IllegalArgumentException.class)
	public final void testCtorGroupSubheadColCount0() {
		createGroupSubhead("name", 0);
	}

	@Test(expected = IllegalArgumentException.class)
	public final void testCtorGroupSubheadColCountNegative() {
		createGroupSubhead("name", -1);
	}

	@Test
	public final void testCtorGroupSubheadGetters() {
		String name = "subhead";
		EntryDoc entryDoc = createGroupSubhead(name, 3);

		assertTitleNameOrSubheadCreatedCorrectly(entryDoc, name, EntryDoc.Kind.groupSubhead);
	}

	// -----------------------------------

	@Test(expected = NullPointerException.class)
	public final void testCtorColLabelsDataNull() {
		createColumnLabels((String[]) null);
	}

	@Test(expected = IllegalArgumentException.class)
	public final void testCtorColLabelsDataEmpty() {
		createColumnLabels(new String[0]);
	}

	@Test(expected = NullPointerException.class)
	public final void testCtorColLabelsDataHasNull() {
		createColumnLabels(VALUES_WITHNULL_3);
	}

	@Test
	public final void testCtorColLabelsDataWithEmptyString() {
		createColumnLabels(VALUES_WITHEMPTY_3);
	}

	@Test
	public final void testColLabelsGetValuesCloned() {
		EntryDoc entryDoc = createColumnLabels(VALUES_3);
		assertNotSame(VALUES_3, entryDoc.getValues());
		assertArrayEquals(VALUES_3, entryDoc.getValues());
	}

	@Test
	public final void testCtorColLabelsGetters() {
		EntryDoc entryDoc = createColumnLabels(VALUES_3);

		assertEquals(EntryDoc.Kind.columnLabels, entryDoc.getKind());

		assertEquals(3, entryDoc.getValues().length);
		assertEquals(VALUES_3[0], entryDoc.getValues()[0]);
		assertEquals(VALUES_3[1], entryDoc.getValues()[1]);
		assertEquals(VALUES_3[2], entryDoc.getValues()[2]);

		assertNull("should have no formatting", entryDoc.getFormatInfo());
	}

	// -----------------------------------

	@Test(expected = NullPointerException.class)
	public final void testCtorDataDataNull() {
		createData(EntryDocTestCase.BOOKMARK_ID, null, (String[]) null);
	}

	@Test(expected = IllegalArgumentException.class)
	public final void testCtorDataDataEmpty() {
		createData(EntryDocTestCase.BOOKMARK_ID, null, new String[0]);
	}

	@Test(expected = NullPointerException.class)
	public final void testCtorDataDataHasNull() {
		createData(EntryDocTestCase.BOOKMARK_ID, null, VALUES_WITHNULL_3);
	}

	@Test
	public final void testCtorDataDataWithEmptyString() {
		createData(EntryDocTestCase.BOOKMARK_ID, null, VALUES_WITHEMPTY_3);
	}

	@Test
	public final void testCtorDataBookmarkNullIsOk() {
		createData(null, null, VALUES_WITHEMPTY_3);
	}

	static final String[] HTML_VALUES_3 = new String[] { "name3", "33", "<p>desc</p>" };
	static final String[] TXT_NL_VALUES_3 = new String[] { "name3", "33",
		"desc'" + Util.NL + "'continued" };

	@Test(expected = ArrayIndexOutOfBoundsException.class)
	public final void testCtorDataFmtIdxNegative() {
		createData(EntryDocTestCase.BOOKMARK_ID,
				new FormatInfo(TextKind.htmlSnippet, Integer.valueOf(-1)), HTML_VALUES_3);
	}

	@Test(expected = ArrayIndexOutOfBoundsException.class)
	public final void testCtorDataFmtIdxGreaterThanValuesLength() {
		createData(EntryDocTestCase.BOOKMARK_ID,
				new FormatInfo(TextKind.htmlSnippet, Integer.valueOf(3)), HTML_VALUES_3);
	}

	@Test
	public final void testCtorDataGettersTextWithNL() {
		FormatInfo formatInfo = null;
		assertCtorDataCreatesCorrectFormatAndValues(formatInfo, VALUES_3);
	}

	@Test
	public final void testCtorDataGettersHtmlSnippet() {
		FormatInfo formatInfo = new FormatInfo(TextKind.htmlSnippet, Integer.valueOf(2));
		assertCtorDataCreatesCorrectFormatAndValues(formatInfo, HTML_VALUES_3);
	}

	@Test
	public final void testCtorDataGettersNoFormatting() {
		FormatInfo formatInfo = new FormatInfo(TextKind.textWithNL, Integer.valueOf(2));
		assertCtorDataCreatesCorrectFormatAndValues(formatInfo, VALUES_3);
	}

	private void assertCtorDataCreatesCorrectFormatAndValues(FormatInfo formatInfo, String[] values3) {
		EntryDoc entryDoc = createData(EntryDocTestCase.BOOKMARK_ID, formatInfo, values3);

		assertEquals(EntryDoc.Kind.data, entryDoc.getKind());

		assertEquals(EntryDocTestCase.BOOKMARK_ID, entryDoc.getBookmarkID());

		assertEquals(3, entryDoc.getValues().length);
		assertEquals(values3[0], entryDoc.getValues()[0]);
		assertEquals(values3[1], entryDoc.getValues()[1]);
		assertEquals(values3[2], entryDoc.getValues()[2]);

		assertEquals(formatInfo, entryDoc.getFormatInfo());
	}

	@Test
	public final void testDataGetValuesCloned() {
		EntryDoc entryDoc = createData(EntryDocTestCase.BOOKMARK_ID, null, VALUES_3);
		assertNotSame(VALUES_3, entryDoc.getValues());
		assertArrayEquals(VALUES_3, entryDoc.getValues());
	}

	// -----------------------------------

	@Test(expected = NullPointerException.class)
	public final void testRawFieldsAccessosrsKeyNull() {
		EntryDoc entryDoc = createData(EntryDocTestCase.BOOKMARK_ID, null, VALUES_3);
		entryDoc.putCell(null, "value");
	}

	@Test(expected = IllegalArgumentException.class)
	public final void testRawFieldsAccessosrsKeyEmpty() {
		EntryDoc entryDoc = createData(EntryDocTestCase.BOOKMARK_ID, null, VALUES_3);
		entryDoc.putCell("   ", "value");
	}

	@Test(expected = NullPointerException.class)
	public final void testRawFieldsAccessosrsValueNull() {
		EntryDoc entryDoc = createData(EntryDocTestCase.BOOKMARK_ID, null, VALUES_3);
		entryDoc.putCell("key", null);
	}

	public final void testGetRawFieldForNullKey() {
		EntryDoc entryDoc = createData(EntryDocTestCase.BOOKMARK_ID, null, VALUES_3);
		entryDoc.putCell("key", "value");
		assertNull("returns null if passed null key", entryDoc.getCell(null));
	}

	@Test
	public final void testRawFieldsAddInexisting() {
		EntryDoc entryDoc = createData(EntryDocTestCase.BOOKMARK_ID, null, VALUES_3);
		String result = entryDoc.putCell("key", "value");
		assertNull("sucsessfully added", result);
	}

	@Test
	public final void testRawFieldsAddExisting() {
		EntryDoc entryDoc = createData(EntryDocTestCase.BOOKMARK_ID, null, VALUES_3);
		entryDoc.putCell("key", "value");
		String oldValue = entryDoc.putCell("key", "newValue");
		assertEquals("value", oldValue);
		assertEquals("newValue", entryDoc.getCells().get("key"));
		assertEquals("newValue", entryDoc.getCell("key"));
	}
}
