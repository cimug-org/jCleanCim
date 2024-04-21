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

package org.tanjakostic.jcleancim.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileFilter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.junit.Ignore;
import org.junit.Test;
import org.tanjakostic.jcleancim.common.Config;

/**
 * @author tatjana.kostic@ieee.org
 * @version $Id: UtilTest.java 31 2019-12-08 01:19:54Z dev978 $
 */
public class UtilTest {

	// ============= Tests ===============

	@Test
	public final void testSplitCommaSeparatedTokensNullArg() {
		assertEquals(new ArrayList<String>(), Util.splitCommaSeparatedTokens(null));
	}

	@Test
	public final void testSplitCommaSeparatedTokensEmptyArg() {
		assertEquals(new ArrayList<String>(), Util.splitCommaSeparatedTokens("   "));
	}

	@Test
	public final void testSplitCommaSeparatedTokensNoComma() {
		String input = "a";
		List<String> expected = Arrays.asList(new String[] { "a" });
		assertEquals(expected, Util.splitCommaSeparatedTokens(input));
	}

	@Test
	public final void testSplitCommaSeparatedTokensWithConsecutiveCommas() {
		String input = "a, b,, c,,d , ,e";
		List<String> expected = Arrays.asList(new String[] { "a", "b", "c", "d", "e" });
		assertEquals(expected, Util.splitCommaSeparatedTokens(input));
	}

	// --------------------

	@Test
	public final void testSplitCharSeparatedTokensWithDots() {
		String input = "a.b";
		List<String> expected = Arrays.asList(new String[] { "a", "b" });
		assertEquals(expected, Util.splitCharSeparatedTokens(input, '.'));
	}

	// --------------------

	// Note on tests for Util.splitLines:
	// I couldn't reproduce the string obtained from XML that contains a non-visible new line
	// character; however, manual comparison of real code proves the method works as designed...
	static final String LINE1 = "Line1 ... read.";
	static final String LINE2 = "line2";
	static final String TWO_LINES = UtilTest.LINE1 + Util.NL + UtilTest.LINE2;
	static final String FOUR_LINES_TWO_EMPTY = UtilTest.LINE1 + Util.NL + UtilTest.LINE2 + Util.NL
			+ "\t \t" + Util.NL + "     ";

	@Test
	public final void testSplitLinesNull() {
		assertTrue("should return empty list", Util.splitLines(null, true).isEmpty());
	}

	@Test
	public final void testSplitLinesEmpty() {
		assertTrue("should return empty list", Util.splitLines("\t  \n", true).isEmpty());
	}

	@Test
	public final void testSplitLinesCompactWithNewLine() {
		String input = TWO_LINES;
		List<String> expected = Arrays.asList(new String[] { LINE1, LINE2 });
		assertEquals(expected, Util.splitLines(input, true));
	}

	@Test
	public final void testSplitLinesNoCompact() {
		String input = FOUR_LINES_TWO_EMPTY;
		List<String> expected = Arrays.asList(new String[] { LINE1, LINE2, "\t \t", "     " });
		assertEquals(expected, Util.splitLines(input, false));
	}

	// --------------------

	@Test
	public final void testConcatCharSeparatedTokensTokensNull() {
		assertEquals("", Util.concatCharSeparatedTokens(",", null));
	}

	@Test
	public final void testConcatCharSeparatedTokensTokensEmpty() {
		assertEquals("", Util.concatCharSeparatedTokens(",", new ArrayList<String>()));
	}

	@Test
	public final void testConcatCharSeparatedTokensSeparatorNull() {
		List<String> tokens = Arrays.asList("a", "b | b", "c");
		String expected = "ab | bc";
		assertEquals(expected, Util.concatCharSeparatedTokens(null, tokens));
	}

	@Test
	public final void testConcatCharSeparatedTokensSeparatorEmpty() {
		List<String> tokens = Arrays.asList("a", "b | b", "c");
		String expected = "ab | bc";
		assertEquals(expected, Util.concatCharSeparatedTokens(null, tokens));
	}

	@Test
	public final void testConcatCharSeparatedTokensSeparatorTab() {
		List<String> tokens = Arrays.asList("a", "b | b", "c");
		String expected = "a\tb | b\tc";
		assertEquals(expected, Util.concatCharSeparatedTokens("\t", tokens));
	}

	@Test
	public final void testConcatCharSeparatedTokensNoDelimiter() {
		String separator = " | ";
		List<String> tokens = Arrays.asList("a", ("b" + separator + "b"), "c");
		String expected = "a | b | b | c";
		assertEquals(expected, Util.concatCharSeparatedTokens(separator, tokens));
	}

	// --------------------

	@Test
	public final void testConcatCharSeparatedTokensWithDelimiter() {
		String separator = " | ";
		List<String> tokens = Arrays.asList("a", ("b" + separator + "b"), "c");
		assertEquals("confirm assumption", '\"', Util.TOKEN_DELIMITTER);
		String expected = "\"a\" | \"b | b\" | \"c\"";
		assertEquals(expected, Util.concatStringSeparatedTokens(separator, true, tokens));
	}

	// --------------------

	@Test
	public final void testToCsvRecordWithDelimiterEndsWithSeparator() {
		char separatorChar = ',';
		List<String> tokens = Arrays.asList("a", ("b" + separatorChar + "b"), "c");
		assertEquals("confirm assumption", '\"', Util.TOKEN_DELIMITTER);
		String expected = "\"a\",\"b,b\",\"c\",";
		assertEquals(expected, Util.toCsvRecord(separatorChar, true, tokens));
	}

	// --------------------

	@Test
	public final void testFillString() {
		assertEquals("00000", Util.fillString(5, '0'));
	}

	// --------------------

	@Test
	public final void testTruncateEnd() {
		assertEquals("", Util.truncateEnd(null, 100));
		assertEquals("", Util.truncateEnd("", 100));
		assertEquals("Something longe...",
				Util.truncateEnd("Something longe than 15 characters", 15));
	}

	@Test
	public final void testTruncateStart() {
		assertEquals("", Util.truncateStart(null, 100));
		assertEquals("", Util.truncateStart("", 100));
		assertEquals("...n 15 characters",
				Util.truncateStart("Something longe than 15 characters", 15));
	}

	// --------------------

	@Test
	public final void testGetIndentSpacesArgNegative() {
		assertEquals("", Util.getIndentSpaces(-1));
	}

	@Test
	public final void testGetIndentSpacesArgZero() {
		assertEquals("", Util.getIndentSpaces(0));
	}

	@Test
	public final void testGetIndentSpaces() {
		StringBuilder result = new StringBuilder();
		int count = 3;
		for (int i = 0; i < count * Util.INDENT_COUNT; ++i) {
			result.append(" ");
		}
		assertEquals(result.toString(), Util.getIndentSpaces(count));
	}

	// --------------------

	@Test
	public final void testCapitaliseNullArg() {
		assertEquals("", Util.capitalise(null));
	}

	@Test
	public final void testCapitaliseEmptyArg() {
		assertEquals("", Util.capitalise("   "));
	}

	@Test
	public final void testCapitaliseAlreadyCapitalisedAllAfterReturnedAsLowerCase() {
		assertEquals("Tototiti", Util.capitalise("TotoTiti"));
	}

	@Test
	public final void testCapitalise() {
		assertEquals("Tototiti", Util.capitalise("totoTiti"));
	}

	// --------------------

	@Test(expected = NullPointerException.class)
	public final void testSortByDecreasingLengthArrayNullArgument() {
		Util.sortByDecreasingLength((String[]) null);
	}

	@Test(expected = IllegalArgumentException.class)
	public final void testSortByDecreasingLengthArrayEmptyArgument() {
		Util.sortByDecreasingLength(new String[0]);
	}

	@Test
	public final void testSortByDecreasingLengthArrayAllLengthsDifferent() {
		String[] input = new String[] { "aaa", "bbbbbbb", "cc", "dddd" };
		String[] expected = new String[] { "bbbbbbb", "dddd", "aaa", "cc" };
		assertTrue(Arrays.deepEquals(expected, Util.sortByDecreasingLength(input)));
	}

	@Test
	public final void testSortByDecreasingLengthArraySomeLengthsSame() {
		String[] input = new String[] { "aaa", "bbbbbbb", "ccc", "ddd" };
		String[] expected = new String[] { "bbbbbbb", "aaa", "ccc", "ddd" };
		assertTrue(Arrays.deepEquals(expected, Util.sortByDecreasingLength(input)));
	}

	// --------------------

	@Test(expected = NullPointerException.class)
	public final void testSortByDecreasingLengthMapNullArgument() {
		Util.sortByDecreasingLength((Map<String, Object>) null);
	}

	@Test
	public final void testSortByDecreasingLengthMapEmptyArgument() {
		assertTrue("should be empty",
				Util.sortByDecreasingLength(new HashMap<String, Object>()).isEmpty());
	}

	@Test
	public final void testSortByDecreasingLengthMapAllLengthsDifferent() {
		Map<String, String> input = new LinkedHashMap<String, String>();
		input.put("aaa", "3");
		input.put("bbbbbbb", "1");
		input.put("cc", "4");
		input.put("dddd", "2");
		Map<String, String> expected = new LinkedHashMap<String, String>();
		expected.put("bbbbbbb", "1");
		expected.put("dddd", "2");
		expected.put("aaa", "3");
		expected.put("cc", "4");
		assertEquals(expected, Util.sortByDecreasingLength(input));
	}

	@Test
	public final void testSortByDecreasingLengthMapSomeLengthsSame() {
		Map<String, String> input = new LinkedHashMap<String, String>();
		input.put("aaa", "2");
		input.put("bbbbbbb", "1");
		input.put("ccc", "3");
		input.put("ddd", "4");
		Map<String, String> expected = new LinkedHashMap<String, String>();
		expected.put("bbbbbbb", "1");
		expected.put("aaa", "2");
		expected.put("ccc", "3");
		expected.put("ddd", "4");
		assertEquals(expected, Util.sortByDecreasingLength(input));
	}

	// -----------------

	@Test
	public final void testLooksLikePlural() {
		assertFalse("should return F for null", Util.looksLikePlural(null));
		assertFalse("should return F for no-content", Util.looksLikePlural("\n  \t \n "));
		assertFalse("should return F for 'myStatus'", Util.looksLikePlural("myStatus"));
		assertFalse("should return F for 'yourSTATUS'", Util.looksLikePlural("yourSTATUS"));
		assertFalse("should return F for 'OurClass'", Util.looksLikePlural("OurClass"));

		assertTrue("should return T for 'myStatuses'", Util.looksLikePlural("myStatuses"));
		assertTrue("should return T for 'YourClasses'", Util.looksLikePlural("YourClasses"));
		assertTrue("should return T for 'crazyTests'", Util.looksLikePlural("crazyTests"));
	}

	// -----------------

	// FIXME
	@Test(expected = NumberFormatException.class)
	public final void testParseIntNotNumber() {
		Util.parseInt("value");
	}

	@Test
	public final void testParseIntNull() {
		assertEquals(null, Util.parseInt(null));
	}

	@Test
	public final void testParseIntEmpty() {
		assertEquals(null, Util.parseInt("\t  \n"));
	}

	@Test
	public final void testParseInt() {
		assertEquals(Integer.valueOf(33), Util.parseInt("33"));
	}

	// -----------------

	@Test(expected = NumberFormatException.class)
	public final void testParseIntZeroNotNumber() {
		Util.parseIntZero("value");
	}

	@Test
	public final void testParseIntZeroNull() {
		assertEquals(Util.ZERO, Util.parseIntZero(null));
	}

	@Test
	public final void testParseIntZeroEmpty() {
		assertEquals(Util.ZERO, Util.parseIntZero("\t  \n"));
	}

	@Test
	public final void testParseIntZero() {
		assertEquals(Integer.valueOf(33), Util.parseIntZero("33"));
	}

	// -----------------

	@Test
	public final void testNull2emptyNonNull() {
		assertEquals("33", Util.null2empty("33"));
	}

	@Test
	public final void testNull2emptyNull() {
		assertEquals("", Util.null2empty(null));
	}

	// -----------------

	@Test
	public final void testCreateKeyValuePairNullKeyOk() {
		Util.createKeyValuePair(null, "value");
	}

	@Test
	public final void testCreateKeyValuePairNullValueOk() {
		Util.createKeyValuePair("yuio", null);
	}

	@Test
	public final void testCreateKeyValuePairSingleEntry() {
		Map<String, String> keyValuePair = Util.createKeyValuePair("key", "value");
		assertEquals(1, keyValuePair.size());
	}

	// -----------------

	@Test
	public final void testGetKeysByValue() {
		Map<String, Object> map = new LinkedHashMap<String, Object>();
		Object value = new Object();
		map.put("a", value);
		map.put("b", new Object());
		map.put("c", value);

		LinkedHashSet<String> expected = new LinkedHashSet<String>();
		expected.add("a");
		expected.add("c");

		assertEquals(expected, Util.getKeysByValue(map, value));
		assertEquals("a", Util.getKeyByValue(map, value));
	}

	// -----------------

	private static final String MODEL_FILE = "testCombined.eap";
	private static final String FILE_ON_CP = "test-file.txt";

	@Test
	public final void testInitPropsFromFileNull() {
		Properties props = Util.initPropsFromFile(null);
		assertTrue("returns empty properties", props.isEmpty());
	}

	@Test
	public final void testInitPropsFromFileEmpty() {
		Properties props = Util.initPropsFromFile("   ");
		assertTrue("returns empty properties", props.isEmpty());
	}

	@Test
	public final void testInitPropsFromFileInexisting() {
		Properties props = Util.initPropsFromFile("inexisting.properties");
		assertTrue("returns empty properties", props.isEmpty());
	}

	@Test
	public final void testInitPropsFromFile() {
		Properties props = Util.initPropsFromFile("testConfig.properties");
		assertEquals(MODEL_FILE, props.get(Config.KEY_MODEL_FILENAME));
	}

	// -----------------

	@Test(expected = ApplicationException.class)
	public final void testGetResourceAbsPathResourceNull() throws ApplicationException {
		Util.getResourceAbsPath(null, "detail");
	}

	@Test
	public final void testGetResourceAbsPathResourceEmpty() throws ApplicationException {
		String path = Util.getResourceAbsPath("   ", "detail");
		File f = new File(path);
		assertTrue("path is directory", f.isDirectory());
	}

	@Test
	public final void testGetResourceAbsPathDetailNullOrEmptyOk() throws ApplicationException {
		Util.getResourceAbsPath(FILE_ON_CP, null);
	}

	@Test
	public final void testGetResourceAbsPath() throws ApplicationException {
		assertTrue(Util.getResourceAbsPath(FILE_ON_CP, "detail").endsWith(FILE_ON_CP));
	}

	// -----------------

	private static final String BASE_PATH = "sub1" + Util.FILE_SEP + "sub2";

	@Test
	public final void testSplitDirAndFileNamesRelPathNull() {
		assertTrue("should be empty", Util.splitDirAndFileNames(BASE_PATH, null).isEmpty());
	}

	@Test
	public final void testSplitDirAndFileNamesRelPathEmpty() {
		assertTrue("should be empty", Util.splitDirAndFileNames(BASE_PATH, " 	 ").isEmpty());
	}

	@Test
	public final void testSplitDirAndFileNamesBasePathNull() {
		String relPath = "myDir" + Util.FILE_SEP + "mySubdir" + Util.FILE_SEP + "myFile.ext";
		List<String> expecteds = Arrays.asList("myDir", "mySubdir", "myFile");
		assertEquals(expecteds, Util.splitDirAndFileNames(null, relPath));
	}

	@Test
	public final void testSplitDirAndFileNamesBasePathEmpty() {
		String relPath = "myDir" + Util.FILE_SEP + "mySubdir" + Util.FILE_SEP + "myFile.ext";
		List<String> expecteds = Arrays.asList("myDir", "mySubdir", "myFile");
		assertEquals(expecteds, Util.splitDirAndFileNames("  	  ", relPath));
	}

	@Test
	public final void testSplitDirAndFileNamesBothPathsSame() {
		assertTrue("should be empty", Util.splitDirAndFileNames(BASE_PATH, BASE_PATH).isEmpty());
	}

	@Test
	public final void testSplitDirAndFileNamesRelPathLongerForSeparatorOnly() {
		assertTrue("should be empty",
				Util.splitDirAndFileNames(BASE_PATH, BASE_PATH + Util.FILE_SEP).isEmpty());
	}

	@Test
	public final void testSplitDirAndFileNamesRelPathDoesNotContainBasePath() {
		String relPath = "myDir" + Util.FILE_SEP + "mySubdir" + Util.FILE_SEP + "myFile.ext";
		assertTrue("should be empty", Util.splitDirAndFileNames(BASE_PATH, relPath).isEmpty());
	}

	@Test
	public final void testSplitDirAndFileNames() {
		String relPath = BASE_PATH + Util.FILE_SEP + "myDir" + Util.FILE_SEP + "mySubdir"
				+ Util.FILE_SEP + "myFile.ext";
		List<String> expecteds = Arrays.asList("myDir", "mySubdir", "myFile");
		assertEquals(expecteds, Util.splitDirAndFileNames(BASE_PATH, relPath));
	}

	// -----------------

	@Test
	public final void testGetOutputFileAbsPathCreatesDirAndFileIfInexisting() throws Exception {
		File inexistingDir = null;
		File inexistingFile = null;
		File newFile = null;
		try {
			final String relPathName = "test" + Util.FILE_SEP + "tmp";
			final String fileName = "myFile";
			inexistingDir = new File(relPathName);
			inexistingFile = new File(relPathName, fileName);
			assertFalse("pre-condition: file should not exist", inexistingFile.exists());
			assertFalse("pre-condition: subdir should not exist", inexistingDir.exists());

			String newFilePath = Util.getOutputFileRenameIfExists(relPathName, fileName).getPath();
			newFile = new File(newFilePath);
			writeFirstLine(newFile, "something");
			assertTrue("created directory and file", newFile.exists());
		} catch (Exception e) {
			throw e;
		} finally {
			// In case any assert fails, this block ensures removal of potentially created
			// directory or file(s), and when the test is run for the second time, pre-conditions
			// should hold.
			Util.delete(newFile);
			Util.delete(inexistingFile);
			Util.delete(inexistingDir);
		}
	}

	private static final String TEST_FILE = "test-file.txt";

	@Test
	public final void testGetOutputFileAbsPathCreatesFileIfInexistingAndBackup()
			throws IOException, ApplicationException {
		File existingFile = null;
		File newFile = null;
		final String relInDirPath = "test" + Util.FILE_SEP + "input";
		final String relOutDirPath = "test" + Util.FILE_SEP + "output";
		final String fileName = TEST_FILE;
		existingFile = new File(relInDirPath, fileName);
		assertTrue("pre-condition: file '" + existingFile.getAbsolutePath() + "' should exist",
				existingFile.exists());
		String existingContent = readFirstLine(existingFile);

		try {
			String newFilePath = Util.getOutputFileRenameIfExists(relOutDirPath, fileName)
					.getPath();
			newFile = new File(newFilePath);
			String modifiedText = existingContent + " copy";
			writeFirstLine(newFile, modifiedText);
			assertTrue("should have created directory and file", newFile.exists());

			String textFromCopy = readFirstLine(newFile);
			assertEquals(modifiedText, textFromCopy);
		} finally {
			// In case any assert fails, this block ensures removal of potentially created
			// directory or file(s), and when the test is run for the second time, pre-conditions
			// should hold. It also cleans up backup files in case the test is successful.
			File dir = new File(relOutDirPath);
			FileFilter fileFilter = new FileFilter() {
				@Override
				public boolean accept(File file) {
					return file.getName().startsWith(TEST_FILE);
				}
			};
			File[] files = dir.listFiles(fileFilter);
			if (files != null) {
				for (int i = 0; i < files.length; ++i) {
					Util.delete(files[i]);
				}
			}
			Util.delete(dir);
		}
	}

	private static String readFirstLine(File file) throws IOException {
		BufferedReader in = new BufferedReader(new FileReader(file));
		String strLine = in.readLine();
		in.close();
		return strLine;
	}

	private static void writeFirstLine(File file, String txt) throws IOException {
		FileWriter out = new FileWriter(file);
		out.write(txt);
		out.close();
	}

	// -----------------
	@Test(expected = NullPointerException.class)
	public final void testGetFileExtensionNull() {
		Util.getFileExtension(null);
	}

	@Test(expected = IllegalArgumentException.class)
	public final void testGetFileExtensionEmpty() {
		Util.getFileExtension("   \t  \n  ");
	}

	@Test
	public final void testGetFileExtensionNoneReturnsNull() {
		assertNull(Util.getFileExtension("noExtFileName"));
	}

	@Test
	public final void testGetFileExtension() {
		assertEquals("txt", Util.getFileExtension("test.cool.awesome.txt"));
	}

	// -----------------

	@Test
	public final void testGetDirectoryExpectsExisting() {
		String dirName = "test" + Util.FILE_SEP + "dummy";
		File dummy = new File(Util.USER_DIR, dirName);
		assertFalse("pre-condition: should not exist", dummy.exists());

		File inexisting = Util.getDirectory(dirName, false);
		assertNull(inexisting);
		assertFalse("post-condition: should have not been created", dummy.exists());
	}

	@Test
	public final void testGetDirectoryCreateIfInexisting() {
		String dirName = "test" + Util.FILE_SEP + "subdir";
		File dir = new File(Util.USER_DIR, dirName);
		try {
			assertFalse("pre-condition: dir should not exist", dir.exists());

			dir = Util.getDirectory(dirName, true);
			assertTrue("should have been created", dir.exists());
		} catch (Exception e) {
			throw e;
		} finally {
			Util.delete(dir);
		}
	}

	// -----------------

	@Test
	public final void testCopy() {
		// Util.copy(src, dst);
		// FIXME
	}

	// -----------------

	@Test
	public final void testDeleteAcceptsNull() {
		Util.delete(null);
	}

	// -----------------

	@Test
	public final void testClearClipboard() throws ApplicationException {
		Util.copyTextToClipboard("some text");
		Util.clearClipboard();
		assertNull(Util.fetchTextFromClipboard());
	}

	// -----------------

	@Test
	public final void testFetchTextFromClipboard() throws ApplicationException {
		Util.copyTextToClipboard("some text");
		assertEquals("some text", Util.fetchTextFromClipboard());
	}

	// -----------------

	@Test
	public final void testCopyTextToClipboardNullIsNoOp() throws ApplicationException {
		Util.copyTextToClipboard("text before");
		Util.copyTextToClipboard(null);
		assertEquals("text before", Util.fetchTextFromClipboard());
	}

	@Test
	public final void testCopyTextToClipboardEmptyIsNoOp() throws ApplicationException {
		Util.copyTextToClipboard("text before");
		Util.copyTextToClipboard("    ");
		assertEquals("text before", Util.fetchTextFromClipboard());
	}

	@Test
	public final void testCopyTextToClipboard() throws ApplicationException {
		String txt = "<p>some txt</p>";
		Util.copyTextToClipboard(txt);
		assertEquals(txt, Util.fetchTextFromClipboard());
	}

	// -----------------

	@Test
	public final void testCopyHtmlToClipboardNullIsNoOp() throws ApplicationException {
		Util.copyTextToClipboard("text before");
		Util.copyHtmlToClipboard(null);
		assertEquals("text before", Util.fetchTextFromClipboard());
	}

	@Test
	public final void testCopyHtmlToClipboardEmptyIsNoOp() throws ApplicationException {
		Util.copyTextToClipboard("text before");
		Util.copyHtmlToClipboard("    ");
		assertEquals("text before", Util.fetchTextFromClipboard());
	}

	@Test
	public final void testCopyHtmlToClipboard() throws ApplicationException {
		String txt = "some txt";
		Util.copyHtmlToClipboard(txt);
		String clipboardContent = Util.fetchTextFromClipboard();
		assertTrue("starts with <!DOCTYPE", clipboardContent.startsWith("<!DOCTYPE"));
		assertTrue("contains input", clipboardContent.contains("<body>" + txt + "</body></html>"));
		assertTrue("ends with </body></html>", clipboardContent.endsWith("</body></html>"));
	}

	// -----------------

	@Ignore
	@Test
	public final void testCopyImageToClipboard() {
		// Util.copyImageToClipboard(pic);
		// FIXME
	}

	// -----------------

	@Test
	public final void testFormattDuration() {
		long millis = 3600 * 1000 * 1 + 60 * 1000 * 2 + 1000 * 3 + 456;
		assertEquals("1:02:03.456", Util.formatDuration(millis));
	}

	// -----------------

	@Test
	public final void testEnsureNotNullAcceptsNullName() {
		Util.ensureNotNull("object", null);
	}

	@Test(expected = NullPointerException.class)
	public final void testEnsureNotNullObjNull() {
		Util.ensureNotNull(null, "name");
	}

	// -----------------

	@Test
	public final void testEnsureNotEmptyStringAcceptsNullName() {
		Util.ensureNotEmpty("object", null);
	}

	@Test(expected = NullPointerException.class)
	public final void testEnsureNotEmptyStringNull() {
		Util.ensureNotEmpty((String) null, "name");
	}

	@Test(expected = IllegalArgumentException.class)
	public final void testEnsureNotEmptyStringString() {
		Util.ensureNotEmpty("   ", "name");
	}

	// -----------------

	@Test
	public final void testEnsureNotEmptyMapAcceptsNullName() {
		Map<String, String> input = new HashMap<String, String>();
		input.put("1", "one");
		Util.ensureNotEmpty(input, null);
	}

	@Test(expected = NullPointerException.class)
	public final void testEnsureNotEmptyMapNullMap() {
		Util.ensureNotEmpty((Map<?, ?>) null, "name");
	}

	@Test(expected = IllegalArgumentException.class)
	public final void testEnsureNotEmptyMap() {
		Util.ensureNotEmpty(new HashMap<String, String>(), "name");
	}

	// -----------------

	@Test
	public final void testEnsureNotEmptyCollectionAcceptsNullName() {
		List<String> input = new ArrayList<String>();
		input.add("1");
		Util.ensureNotEmpty(input, null);
	}

	@Test(expected = NullPointerException.class)
	public final void testEnsureNotEmptyCollectionNullCollection() {
		Util.ensureNotEmpty((List<?>) null, "name");
	}

	@Test(expected = IllegalArgumentException.class)
	public final void testEnsureNotEmptyCollection() {
		Util.ensureNotEmpty(new ArrayList<String>(), "name");
	}

	// -----------------

	@Test
	public final void testEnsureNotEmptyArrayAcceptsNullName() {
		Util.ensureNotEmpty(new String[] { "one" }, null);
	}

	@Test(expected = NullPointerException.class)
	public final void testEnsureNotEmptyArrayNullArray() {
		Util.ensureNotEmpty((Object[]) null, "name");
	}

	@Test(expected = IllegalArgumentException.class)
	public final void testEnsureNotEmptyArray() {
		Util.ensureNotEmpty(new String[0], "name");
	}
}
