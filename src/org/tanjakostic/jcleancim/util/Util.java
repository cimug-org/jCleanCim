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

package org.tanjakostic.jcleancim.util;

import java.awt.Image;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.io.StringReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.Set;
import java.util.Vector;

import javax.imageio.ImageIO;

import org.apache.commons.lang.time.DurationFormatUtils;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;

/**
 * Utility methods.
 *
 * @author tatjana.kostic@ieee.org
 * @version $Id: Util.java 31 2019-12-08 01:19:54Z dev978 $
 */
public class Util {
	private static final Logger _logger = Logger.getLogger(Util.class.getName());

	/**
	 * @author tatjana.kostic@ieee.org
	 * @version $Id: Util.java 31 2019-12-08 01:19:54Z dev978 $
	 */
	private static final class StringDecreasingLengthComparator
			implements Comparator<String>, Serializable {
		private static final long serialVersionUID = 1L;

		@Override
		public int compare(String s1, String s2) {
			int len1 = s1.length();
			int len2 = s2.length();
			return len1 > len2 ? -1 : (len1 == len2 ? 0 : 1);
		}
	}

	static final boolean WINDOWS = System.getProperty("os.name").startsWith("Windows");

	public static final int INDENT_COUNT = 2;
	public static final String NL = System.getProperty("line.separator");
	public static final String FILE_SEP = System.getProperty("file.separator");
	public static final String PATH_SEP = System.getProperty("path.separator");
	public static final String USER_DIR_KEY = "user.dir";
	public static final String USER_DIR = System.getProperty(USER_DIR_KEY);

	public static final int TRUNCATE_GREATER_THAN = 30;

	/** Character used to "enclose" a token that is to be concatenated with a separator. */
	public static final char TOKEN_DELIMITTER = '\"';

	/** IEC editors were replacing the regular dash "-" in captions with EN DASH "&#x2013;". */
	public static final char EN_DASH = '\u2013';

	/** This is what in MS Word looks like degree celsius...: '&#xa0;' */
	public static final char NON_BREAKING_WHITE_SPACE = '\u00A0';

	static final String UC_THETA = "\u03F4";
	static final String LC_THETA = "\u03D1";
	static final String LC_OMEGA = "\u03C9";
	static final String LC_PHI = "\u03C6";

	/**
	 * Known singular words, or word endings (such as 'ss' for e.g. class, loss, address); all in
	 * lower case.
	 */
	static final Set<String> SINGULAR_ENDS = new LinkedHashSet<>(
			Arrays.asList("ss", "ous", "ius", "status", "bus", "basis", "alias", "dynamics",
					"series", "gas", "francis", "axis", "rans", "diagnosis", "bias"));

	public static final Integer ZERO = Integer.valueOf(0);

	private Util() {
		// prevents creation
	}

	// ========================= strings =========================

	/**
	 * Splits comma-separated string into a list of non-empty tokens. If <code>input</code> is null
	 * or empty, returns empty collection.
	 */
	public static List<String> splitCommaSeparatedTokens(String input) {
		return splitCharSeparatedTokens(input, ',');
	}

	/**
	 * Splits <code>c</code>-separated string into a list of non-empty tokens. If <code>input</code>
	 * is null or empty, returns empty collection.
	 * <p>
	 * If you work with XML text and want to split text content to lines, use
	 * {@link #splitLines(String, boolean)} instead.
	 */
	public static List<String> splitCharSeparatedTokens(String input, char c) {
		return splitStringSeparatedTokens(input, Character.toString(c));
	}

	/**
	 * Uses buffered and string reader to identify lines in <code>input</code> and adds them to the
	 * result to return. If <code>compact</code> is true, every parsed line is trimmed and in case
	 * it is empty after trimming, that line is not added to the result.
	 * <p>
	 * If you work with XML text, use this method (rather than explicit
	 * {@link #splitCharSeparatedTokens(String, char)} or
	 * {@link Util#splitStringSeparatedTokens(String, String)}).
	 *
	 * @param input
	 * @param compact
	 *            whether to compact result
	 * @return <code>input</code> split to individual lines.
	 */
	public static List<String> splitLines(String input, boolean compact) {
		if (input == null || input.isEmpty()) {
			return Collections.emptyList();
		}
		List<String> result = new ArrayList<String>();

		BufferedReader reader = new BufferedReader(new StringReader(input));
		String str;
		try {
			while ((str = reader.readLine()) != null) {
				if (compact) {
					str = str.trim();
					if (str.isEmpty()) {
						continue;
					}
				}
				result.add(str);
			}
		} catch (IOException e) {
			e.printStackTrace();
			throw new RuntimeException("Failed to split input string to lines.");
		}
		return result;
	}

	/**
	 * Splits <code>c</code>-separated string into a list of non-empty tokens. If <code>input</code>
	 * is null or empty, returns empty collection.
	 * <p>
	 * If you work with XML text and want to split text content to lines, use
	 * {@link #splitLines(String, boolean)} instead.
	 */
	public static List<String> splitStringSeparatedTokens(String input, String separator) {
		if (input == null) {
			return new ArrayList<String>(0);
		}
		String[] names = input.split("\\" + separator);
		List<String> result = new ArrayList<String>();
		for (String excName : names) {
			String trimmedName = excName.trim();
			if (!trimmedName.isEmpty()) {
				result.add(trimmedName);
			}
		}
		return result;
	}

	/**
	 * Invokes {@link #concatStringSeparatedTokens(String, boolean, List)} for the case you don't
	 * expect separator be embedded into tokens.
	 *
	 * @see #concatStringSeparatedTokens(String, boolean, List)
	 */
	public static String concatCharSeparatedTokens(String separator, List<String> tokens) {
		return concatStringSeparatedTokens(separator, false, tokens);
	}

	/**
	 * Invokes {@link #concatStringSeparatedTokens(String, boolean, List)} and appends the
	 * <code>separatorChar</code> at the end of result, to return the string that is one record in a
	 * .csv format <em>without</em> new line character at the end.
	 *
	 * @see #concatStringSeparatedTokens(String, boolean, List)
	 */
	public static String toCsvRecord(char separatorChar, boolean delimitTokens,
			List<String> tokens) {
		return concatStringSeparatedTokens(String.valueOf(separatorChar), delimitTokens, tokens)
				+ separatorChar;
	}

	/**
	 * Concatenates <code>tokens</code> with the <code>separator</code> string between consecutive
	 * ones, but <em>not</em> at the end, and returns the resulting string. If <code>tokens</code>
	 * is null or empty, returns empty string.
	 * <p>
	 * This method is useful to create a line for e.g. logging and debugging with any desired
	 * separation (e.g. " | ", " / ").
	 * <p>
	 * For comma-separated format, use {@link #toCsvRecord(char, boolean, List)}, which appends the
	 * <code>separator</code> at the end of the result.
	 *
	 * @param separator
	 *            separator string; if null, considered as empty string.
	 * @param delimitTokens
	 *            whether to delimit tokens; set to true if any token may contain
	 *            <code>separator</code> as substring, in which case each token will be enclosed by
	 *            one character {@value #TOKEN_DELIMITTER} at its start, and one at its end.
	 * @param tokens
	 *            tokens to concatenate.
	 */
	public static String concatStringSeparatedTokens(String separator, boolean delimitTokens,
			List<String> tokens) {
		if (tokens == null || tokens.isEmpty()) {
			return "";
		}
		String nonNullSep = (separator == null) ? "" : separator;
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < tokens.size(); ++i) {
			sb = enclose(sb, tokens.get(i), delimitTokens);
			if (i < tokens.size() - 1) {
				sb.append(nonNullSep);
			}
		}
		return sb.toString();
	}

	private static StringBuilder enclose(StringBuilder sb, String content, boolean delimitTokens) {
		if (!delimitTokens) {
			return sb.append(content);
		}
		return sb.append(TOKEN_DELIMITTER).append(content).append(TOKEN_DELIMITTER);
	}

	/**
	 * Returns the string filled with number <code>count</code> of characters <code>c</code>.
	 *
	 * @param count
	 *            number of characters.
	 * @param ch
	 *            the character.
	 */
	public static String fillString(int count, char ch) {
		char[] chars = new char[count];
		Arrays.fill(chars, ch);
		return new String(chars);
	}

	/**
	 * Identical to {@link #truncateEnd(String, int)} with default value for <code>charCount</code>
	 * = {@value #TRUNCATE_GREATER_THAN}.
	 */
	public static String truncateEnd(String input) {
		return truncateEnd(input, TRUNCATE_GREATER_THAN);
	}

	/**
	 * Truncates <code>input</code> to first <code>charCount</code> characters and appends "...". If
	 * <code>input</code> is null or empty, returns empty string. If <code>charCount</code> is
	 * greater than the <code>input</code> length, returns <code>input</code> as is.
	 */
	public static String truncateEnd(String input, int charCount) {
		if (input == null) {
			return "";
		}
		return (input.length() > charCount) ? input.substring(0, charCount) + "..." : input;
	}

	/**
	 * Identical to {@link #truncateEnd(String, int)} with default value for <code>charCount</code>
	 * = {@value #TRUNCATE_GREATER_THAN}.
	 */
	public static String truncateStart(String input) {
		return truncateStart(input, TRUNCATE_GREATER_THAN);
	}

	/**
	 * Truncates <code>input</code> to last <code>charCount</code> characters and prepends "...". If
	 * <code>input</code> is null or empty, returns empty string. If <code>charCount</code> is
	 * greater than the <code>input</code> length, returns <code>input</code> as is.
	 */
	public static String truncateStart(String input, int charCount) {
		if (input == null) {
			return "";
		}
		if (input.length() <= charCount) {
			return input;
		}
		return "..." + input.substring(input.length() - charCount, input.length());
	}

	/**
	 * Returns string of spaces of the size equal to count * {@link #INDENT_COUNT}.
	 */
	public static String getIndentSpaces(int count) {
		if (count <= 0) {
			return "";
		}
		int indentCount = INDENT_COUNT * count;
		StringBuffer result = new StringBuffer(indentCount);
		for (int i = 0; i < indentCount; ++i) {
			result.append(" ");
		}
		return result.toString();
	}

	/**
	 * Returns string of <code>count</code> non-breaking spaces.
	 */
	public static String getNonBreakingSpaces(int count) {
		if (count <= 0) {
			return "";
		}
		StringBuffer result = new StringBuffer(count);
		for (int i = 0; i < count; ++i) {
			result.append(NON_BREAKING_WHITE_SPACE);
		}
		return result.toString();
	}

	/** Returns true is <code>value</code> is not null, and the trimmed content is not empty. */
	public static boolean hasContent(String value) {
		return value != null && !value.trim().isEmpty();
	}

	/**
	 * Returns string starting with upper-case letter, all the other letters lower-case. If
	 * <code>in</code> is null or empty string, returns empty string.
	 */
	public static String capitalise(String input) {
		if (!hasContent(input)) {
			return "";
		}
		return input.substring(0, 1).toUpperCase() + input.substring(1).toLowerCase();
	}

	/** Returns copy of <code>items</code> sorted by decreasing length (longest first). */
	public static String[] sortByDecreasingLength(String[] items) {
		Util.ensureNotEmpty(items, "items");
		String[] result = items.clone();
		Arrays.sort(result, new StringDecreasingLengthComparator());
		return result;
	}

	/** Returns copy of <code>items</code> sorted by decreasing length of keys (longest first). */
	public static <T extends Object> Map<String, T> sortByDecreasingLength(Map<String, T> items) {
		Util.ensureNotNull(items, "items");
		if (items.isEmpty()) {
			return Collections.emptyMap();
		}

		String[] sortedKeys = items.keySet().toArray(new String[0]);
		Arrays.sort(sortedKeys, new StringDecreasingLengthComparator());

		Map<String, T> result = new LinkedHashMap<>();
		for (String key : sortedKeys) {
			result.put(key, items.get(key));
		}
		return result;
	}

	/**
	 * Returns whether <code>token</code> looks like plural; returns false for null or empty arg.
	 */
	public static boolean looksLikePlural(String token) {
		if (!Util.hasContent(token)) {
			return false;
		}
		String lcToken = token.toLowerCase();
		if (lcToken.endsWith("s")) {
			for (String singular : SINGULAR_ENDS) {
				if (lcToken.endsWith(singular)) {
					return false;
				}
			}
			return true;
		}
		return false;
	}

	// =============== values =================

	/**
	 * Returns integer from <code>intStr</code> if it isn't null and isn't empty after trimming;
	 * otherwise returns null.
	 */
	public static Integer parseInt(String intStr) {
		return hasContent(intStr) ? Integer.valueOf(intStr) : null;
	}

	/**
	 * Returns integer from <code>intStr</code> if it isn't null and isn't empty after trimming;
	 * otherwise returns integer with value 0.
	 */
	public static Integer parseIntZero(String intStr) {
		return hasContent(intStr) ? Integer.valueOf(intStr) : Util.ZERO;
	}

	/** Returns empty string if <code>s</code> is null, <code>s</code> otherwise. */
	public static String null2empty(String s) {
		return s == null ? "" : s;
	}

	// ==================== maps ====================

	/** Retruns the map with a single key/value pair. Both areguments may be null. */
	public static <K, V> Map<K, V> createKeyValuePair(K key, V value) {
		Map<K, V> keyValuePair = new HashMap<K, V>(1);
		keyValuePair.put(key, value);
		return keyValuePair;
	}

	/** FIXME: tests */
	public static <K, V> Set<K> getKeysByValue(Map<K, V> map, V value) {
		if (map == null) {
			return null;
		}
		Set<K> keys = new LinkedHashSet<K>();
		for (Entry<K, V> entry : map.entrySet()) {
			if ((entry.getValue() == null && value == null)
					|| (entry.getValue() != null && entry.getValue().equals(value))) {
				keys.add(entry.getKey());
			}
		}
		return keys;
	}

	/** FIXME: tests */
	public static <K, V> K getKeyByValue(Map<K, V> map, V value) {
		Set<K> keys = getKeysByValue(map, value);
		if (keys.isEmpty()) {
			return null;
		}
		return keys.iterator().next();
	}

	// ==================== files ===================

	/**
	 * Returns properties loaded from file <code>propsFileName</code> expected to be on the
	 * classpath, empty properties if the file has not been found.
	 *
	 * @param propsFileName
	 *            name of the properties file expected to be on the classpath.
	 */
	public static Properties initPropsFromFile(String propsFileName) {
		Properties result = new Properties();
		if (hasContent(propsFileName)) {
			try (InputStream stream = Util.class.getResourceAsStream("/" + propsFileName);) {
				if (stream == null) {
					_logger.info(propsFileName + " not found");
				} else {
					result.load(stream);
					_logger.info("loaded properties from resource " + propsFileName);
				}
			} catch (IOException e) {
				_logger.error("problem loading properties from " + propsFileName);
			}
		}
		return result;
	}

	/**
	 * Returns absolute path of the resource found on the classpath.
	 *
	 * @param resourceName
	 *            name of the resource.
	 * @param detail
	 *            optional detail to display for logging.
	 * @return absolute path of the resource found on the classpath.
	 * @throws ApplicationException
	 *             if resource with <code>resourceName</code> is not on the classpath.
	 */
	public static String getResourceAbsPath(String resourceName, String detail)
			throws ApplicationException {
		File f = findResource(resourceName, detail);
		return f.getAbsolutePath();
	}

	private static File findResource(String resourceName, String detail)
			throws ApplicationException {
		String trimmedName = (resourceName != null) ? resourceName.trim() : null;

		URL url = Util.class.getResource("/" + trimmedName);
		if (url == null) {
			String pre = hasContent(detail) ? (detail.trim() + " : ") : "";
			throw new ApplicationException(pre + "could not find resource '" + trimmedName + "'");
		}
		String pathFixedSpaces = (WINDOWS) ? url.getFile().replace("%20", " ") : url.getFile();
		return new File(pathFixedSpaces);
	}

	/**
	 * Returns resource as input stream for its name, given that it is found on the classpath.
	 * <p>
	 * Note: In this project, we have set the following directories to be on the classpath:
	 * ./config, ./input, ./test/config and ./test/input.
	 * <p>
	 * FIXME: test
	 *
	 * @throws ResourceNotOnClasspathException
	 */
	public static InputStream findResourceOnClasspath(String resourceName)
			throws ResourceNotOnClasspathException {
		InputStream resource = Util.class.getResourceAsStream("/" + resourceName);
		if (resource == null) {
			throw new ResourceNotOnClasspathException(resourceName);
		}
		return resource;
	}

	/**
	 * Returns potentially empty list of files under <code>directory</code> (and its sub-directories
	 * if <code>recurse</code> is <code>true</code>), filtered with <code>filter</code>.
	 * <p>
	 * Adapted from http://snippets.dzone.com/posts/show/1875.
	 *
	 * @param directory
	 * @param filter
	 * @param recurse
	 */
	public static List<File> listFiles(File directory, FilenameFilter filter, boolean recurse) {
		List<File> result = new Vector<File>();
		if (!directory.exists()) {
			return result;
		}
		File[] entries = directory.listFiles();
		if (entries == null) {
			return result;
		}
		for (File entry : entries) {
			if (filter == null || filter.accept(directory, entry.getName())) {
				result.add(entry);
			}
			if (recurse && entry.isDirectory()) {
				result.addAll(listFiles(entry, filter, recurse));
			}
		}
		return result;
	}

	/**
	 * Returns (potentially empty) list of split members of <code>relPath</code>, starting
	 * immediately after the <code>basePath</code>. If the last member in the path has an extension
	 * (.extension), the name of that file is returned without extension. This is useful for
	 * creating e.g. object structure from the structure in the file system.
	 *
	 * @param basePath
	 *            <code>relPath</code> string is processed after this value; if null or empty, the
	 *            whole <code>relPath</code> is processed. It does <i>not</i> contain file
	 *            separator.
	 * @param relPath
	 *            actual path that should be split; if null, or (trimmed) empty string, this method
	 *            is no-op.
	 */
	public static List<String> splitDirAndFileNames(String basePath, String relPath) {
		if (hasContent(relPath)) {
			String trimmedRelPath = relPath.trim();
			int dotIdx = trimmedRelPath.lastIndexOf(".");
			if (dotIdx != -1) {
				trimmedRelPath = trimmedRelPath.substring(0, dotIdx);
			}
			if (basePath == null || basePath.trim().isEmpty()) {
				return Util.splitCharSeparatedTokens(trimmedRelPath, File.separatorChar);
			}

			File baseDir = new File(basePath.trim());
			String baseDirPath = baseDir.getPath();
			if (baseDirPath.equals(trimmedRelPath)) {
				return Collections.emptyList();
			}

			int fileSepIdx = trimmedRelPath.lastIndexOf(baseDirPath);
			if (fileSepIdx != -1) {
				// last +1 is for file separator:
				trimmedRelPath = trimmedRelPath.substring(fileSepIdx + baseDirPath.length() + 1);
				return Util.splitCharSeparatedTokens(trimmedRelPath, File.separatorChar);
			}
		}
		return Collections.emptyList();
	}

	/**
	 * Returns file {@value #USER_DIR_KEY}/<code>outDirName</code>/<code>outFileName</code> .
	 * Creates {@value #USER_DIR_KEY}/<code>outDirName</code> if it does not already exist. If the
	 * file with <code>outFileName</code> already exists, renames it by appending the system
	 * nanotime to its name.
	 * <p>
	 * This method is useful when generating some output files, as it ensures that the path returned
	 * on success will be valid and a potentially existing file will have been backed up.
	 *
	 * @param outDirName
	 *            subdirectory under {@value #USER_DIR_KEY} that will host <code>outFileName</code>
	 * @param outFileName
	 *            new file name
	 * @return absolute path {@value #USER_DIR_KEY}/<code>outDirName</code>/<code>outFileName</code>
	 *         .
	 * @throws ApplicationException
	 *             if fails to create <code>outFileName</code>, if fails to rename existing file
	 *             with name <code>outFileName</code>
	 */
	public static File getOutputFileRenameIfExists(String outDirName, String outFileName)
			throws ApplicationException {
		File parentDir = getDirectory(outDirName, true);
		File f;
		try {
			f = new File(parentDir, outFileName);
		} catch (Exception e) {
			throw new ApplicationException(
					"Could not create file " + parentDir.getAbsolutePath() + PATH_SEP + outFileName,
					e);
		}
		String filePath = f.getPath();

		if (f.exists()) {
			File backupFile = new File(f.getPath() + "." + System.nanoTime());
			_logger.info("Creating backup of existing file: " + backupFile.getAbsolutePath());
			boolean ok = f.renameTo(backupFile);
			if (!ok) {
				throw new ApplicationException("Could not rename " + f.getAbsolutePath() + " to "
						+ backupFile.getAbsolutePath());
			}
			// if it's a directory, we need to create again (with mkdir) the required original;
			// if it's a file, just create it with File ctor:
			f = (outFileName.trim().isEmpty()) ? getDirectory(outDirName, true)
					: new File(filePath);
		}
		return f;
	}

	/**
	 * Returns extension (after the last ".") if being part of <code>filePath</code>, null
	 * otherwise. Implementation from <a href=
	 * "http://stackoverflow.com/questions/4545937/java-splitting-the-filename-into-a-base-and-extension"
	 * >StackOverflow</a>
	 */
	public static String getFileExtension(String filePath) {
		Util.ensureNotEmpty(filePath, "filePath");
		String[] tokens = filePath.split("\\.(?=[^\\.]+$)");
		if (tokens.length == 2) {
			return tokens[1];
		}
		return null;
	}

	/** Returns extension with the "." if being part of <code>filePath</code>, null otherwise. */
	public static String getFileExtensionWithDot(String filePath) {
		String ext = getFileExtension(filePath);
		return (ext == null) ? null : ("." + ext);
	}

	/**
	 * Returns file representing directory <code>dirName</code> under {@value #USER_DIR_KEY}.
	 *
	 * @param dirRelPath
	 *            relative path of directory.
	 * @param createIfMissing
	 *            whether to create <code>dirRelPath</code> if currently not existing under
	 *            {@value #USER_DIR_KEY}
	 * @return file representing directory <code>dirRelPath</code> under {@value #USER_DIR_KEY}, or
	 *         null if there was an OS-related problem that didn't allow for creation of directory.
	 */
	public static File getDirectory(String dirRelPath, boolean createIfMissing) {
		// File f = new File(dirRelPath);
		File f = new File(Util.USER_DIR + Util.FILE_SEP + dirRelPath);

		if (f.exists()) {
			return f;
		} else if (createIfMissing) {
			boolean ok = f.mkdirs();
			if (ok) {
				return f;
			}
		}

		_logger.error("Failed to create directory '" + f.toString() + "'");
		return null;
	}

	/**
	 * Copies src file to dst file.
	 */
	public static void copy(File src, File dst) throws IOException {
		InputStream in = new FileInputStream(src);
		OutputStream out = new FileOutputStream(dst);

		// Transfer bytes from in to out
		byte[] buf = new byte[1024];
		int len;
		while ((len = in.read(buf)) > 0) {
			out.write(buf, 0, len);
		}
		in.close();
		out.close();
	}

	/**
	 * Wrapper for the {@link File#delete()} that accepts null argument and returns nothing. In case
	 * delete failed, just logs the failure.
	 *
	 * @param f
	 *            potentially null
	 */
	public static void delete(File f) {
		if (f != null) {
			boolean ok = f.delete();
			if (!ok) {
				_logger.error("failed to delete '" + f.toString() + "'.");
			}
		}
	}

	/**
	 * Saves <code>content</code> to <code>filePath</code> and logs the confirmation with
	 * <code>level</code> and return the file.
	 * <p>
	 * TODO: test
	 *
	 * @throws IOException
	 */
	public static File saveToFile(String filePath, String content) throws IOException {
		BufferedWriter fp = new BufferedWriter(new FileWriter(filePath));
		fp.write(content);
		fp.close();
		return new File(filePath);
	}

	/**
	 * Creates file in the given directory (or in default OS tmp directory) and returns the result.
	 *
	 * @param dirAbsPath
	 *            absolute path of the file; if null, temporary directory is used.
	 * @param fileName
	 *            name of the file (witout path, without extension).
	 * @param format
	 *            image format.
	 * @param deleteOnExit
	 *            whether to delete the file on application exit.
	 * @return created temporary file.
	 * @throws IOException
	 *             if a file could not be created.
	 */
	public static File createTempImageFile(String dirAbsPath, String fileName, ImageFormat format,
			boolean deleteOnExit) throws IOException {
		File dir = (dirAbsPath != null) ? new File(dirAbsPath) : null;
		File tmpFile = File.createTempFile(fileName, format.getExtensionWithDot(), dir);
		if (deleteOnExit) {
			tmpFile.deleteOnExit();
		}
		return tmpFile;
	}

	// ====================== clipboard =====================

	/**
	 * Clears system clipboard.
	 *
	 * @throws ApplicationException
	 */
	public static void clearClipboard() throws ApplicationException {
		Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
		// clipboard.setContents(null, null);
		try {
			clipboard.setContents(new Transferable() {
				@Override
				public DataFlavor[] getTransferDataFlavors() {
					return new DataFlavor[0];
				}

				@Override
				public boolean isDataFlavorSupported(DataFlavor flavor) {
					return false;
				}

				@Override
				public Object getTransferData(DataFlavor flavor) throws UnsupportedFlavorException {
					throw new UnsupportedFlavorException(flavor);
				}
			}, null);
		} catch (IllegalStateException e) {
			throw new ApplicationException("Util.clearClipboard failed: " + e.getMessage(), e);
		}
	}

	/**
	 * Copies non-empty, non-null <code>txt</code> to clipboard, no-op otherwise. Use this method if
	 * you have a raw text or a well-formed HTML document.
	 *
	 * @see #copyHtmlToClipboard(String)
	 * @param txt
	 *            text to put to the clipboard
	 */
	public static void copyTextToClipboard(String txt) {
		if (!hasContent(txt)) {
			return;
		}

		HTMLSelection htmlSelection = new HTMLSelection(txt, false);
		Toolkit.getDefaultToolkit().getSystemClipboard().setContents(htmlSelection, null);
	}

	/**
	 * Surrounds the non-empty, non-null <code>htmlBody</code> into doctype and html tags to produce
	 * a valid HTML document; no-op otherwise. Use this method if you have some markup snippet.
	 *
	 * @see #copyTextToClipboard(String)
	 * @param htmlBody
	 *            markup to put to the clipboard
	 */
	public static void copyHtmlToClipboard(String htmlBody) {
		if (!hasContent(htmlBody)) {
			return;
		}

		HTMLSelection htmlSelection = new HTMLSelection(htmlBody, true);
		Toolkit.getDefaultToolkit().getSystemClipboard().setContents(htmlSelection, null);
	}

	/**
	 * Returns text contained in the clipboard (text could be plain or markup), null if clipboard is
	 * empty.
	 *
	 * @throws ApplicationException
	 *             if the data is no longer available in the clipboard in the requested flavor.
	 */
	public static String fetchTextFromClipboard() throws ApplicationException {
		Transferable data = Toolkit.getDefaultToolkit().getSystemClipboard().getContents(null);
		if (data == null) {
			return null;
		}

		try {
			return (String) data.getTransferData(HTMLSelection.FLAVOR);
		} catch (UnsupportedFlavorException e) {
			// should never happen
			_logger.warn(e.getMessage());
			return null;
		} catch (IOException e) {
			throw new ApplicationException("Util.fetchTextFromClipboard failed: " + e.getMessage(),
					e);
		}
	}

	/**
	 * Holds a markup text while on clipboard.
	 *
	 * @author tatjana.kostic@ieee.org
	 * @version $Id: Util.java 31 2019-12-08 01:19:54Z dev978 $
	 */
	static class HTMLSelection implements Transferable {
		private static final String MIME_HTML = "text/html;class=java.lang.String";
		private static final DataFlavor FLAVOR;

		static {
			try {
				FLAVOR = new DataFlavor(MIME_HTML);
			} catch (ClassNotFoundException ex) {
				throw new ProgrammerErrorException(ex.getMessage());
			}
		}

		// the markup object which will be housed by this HTMLSelection
		private final String _html;

		/**
		 * Constructor for an HTML document.
		 *
		 * @param html
		 */
		public HTMLSelection(String html) {
			this(html, false);
		}

		/**
		 * Constructor for an HTML snippet.
		 *
		 * @param html
		 *            HTML text
		 * @param isSnippet
		 *            if true, <code>html</code> will automatically be surrounded by the tags to
		 *            produce a valid HTML document.
		 */
		public HTMLSelection(String html, boolean isSnippet) {
			_html = isSnippet ? String.format(HTMLUtil.HTML_DOC_FMT, html) : html;
		}

		@Override
		public DataFlavor[] getTransferDataFlavors() {
			return new DataFlavor[] { FLAVOR };
		}

		@Override
		public boolean isDataFlavorSupported(DataFlavor flavor) {
			return FLAVOR.equals(flavor);
		}

		@Override
		public Object getTransferData(DataFlavor flavor) throws UnsupportedFlavorException {
			if (!isDataFlavorSupported(flavor)) {
				throw new UnsupportedFlavorException(flavor);
			}
			return _html;
		}
	}

	/**
	 * Image formats supported for UML diagrams.
	 *
	 * @author tatjana.kostic@ieee.org
	 * @version $Id: Util.java 31 2019-12-08 01:19:54Z dev978 $
	 */
	public enum ImageFormat {
		BMP(".bmp"), JPG(".jpg"), PNG(".png");

		private ImageFormat(String extension) {
			_extension = extension;
		}

		private String _extension;

		public String getExtensionWithDot() {
			return _extension;
		}

		public String getExtensionName() {
			return toString().toLowerCase();
		}

		/** Default is {@link #PNG}. */
		public static ImageFormat getDefault() {
			return ImageFormat.PNG;
		}
	}

	/**
	 * Copies image in <code>pic</code> to clipboard.
	 */
	public static void copyImageToClipboard(File pic) {
		Image image = Toolkit.getDefaultToolkit().getImage(pic.getAbsolutePath());
		ImageSelection imageSelection = new ImageSelection(image);
		Toolkit.getDefaultToolkit().getSystemClipboard().setContents(imageSelection, null);
	}

	/**
	 * @param pic
	 *            file where to store the image
	 * @throws IOException
	 */
	public static void saveImageFromClipboard(File pic) throws IOException {
		int dot = pic.getName().lastIndexOf(".");
		String ext = pic.getName().substring(dot + 1);

		Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
		BufferedImage image = null;
		try {
			image = (BufferedImage) clipboard.getData(DataFlavor.imageFlavor);
		} catch (UnsupportedFlavorException e) {
			throw new ProgrammerErrorException(e);
		}

		if (ext.toLowerCase().endsWith(ImageFormat.BMP.getExtensionWithDot())) {
			BMPFile bmp = new BMPFile();
			bmp.saveBitmap(pic.getAbsolutePath(), image, image.getWidth(), image.getHeight());
		} else {
			boolean written = ImageIO.write(image, ext, pic);
			if (!written) {
				throw new IOException("No appropriate writer found for image " + pic.toString());
			}
		}
	}

	/**
	 * Holds an image while on the clipboard.
	 *
	 * @author tatjana.kostic@ieee.org
	 * @version $Id: Util.java 31 2019-12-08 01:19:54Z dev978 $
	 */
	static class ImageSelection implements Transferable {
		static final DataFlavor FLAVOR = DataFlavor.imageFlavor;

		// the Image object which will be housed by the ImageSelection
		private final Image _image;

		public ImageSelection(Image image) {
			_image = image;
		}

		@Override
		public DataFlavor[] getTransferDataFlavors() {
			return new DataFlavor[] { FLAVOR };
		}

		@Override
		public boolean isDataFlavorSupported(DataFlavor flavor) {
			return FLAVOR.equals(flavor);
		}

		@Override
		public Object getTransferData(DataFlavor flavor) throws UnsupportedFlavorException {
			if (!isDataFlavorSupported(flavor)) {
				throw new UnsupportedFlavorException(flavor);
			}
			return _image;
		}
	}

	// ====================== time format =====================

	public static String formatDuration(long millis) {
		return DurationFormatUtils.formatDurationHMS(millis);
	}

	// ==================== arg validation ================

	public static void ensureNotNull(Object arg, String name) {
		if (arg == null) {
			throw new NullPointerException(String.format("Argument '%s' cannot be null.", name));
		}
	}

	public static void ensureNotEmpty(String arg, String name) {
		ensureNotNull(arg, name);
		if (arg.trim().isEmpty()) {
			throw new IllegalArgumentException(
					String.format("Argument '%s' cannot be empty.", name));
		}
	}

	public static void ensureNotEmpty(Map<?, ?> arg, String name) {
		ensureNotNull(arg, name);
		if (arg.isEmpty()) {
			throw new IllegalArgumentException(
					String.format("Argument '%s' cannot be empty.", name));
		}
	}

	public static void ensureNotEmpty(Collection<?> arg, String name) {
		ensureNotNull(arg, name);
		if (arg.isEmpty()) {
			throw new IllegalArgumentException(
					String.format("Argument '%s' cannot be empty.", name));
		}
	}

	public static void ensureNotEmpty(boolean[] arg, String name) {
		ensureNotNull(arg, name);
		if (arg.length == 0) {
			throw new IllegalArgumentException(
					String.format("Argument '%s' cannot be empty.", name));
		}
	}

	public static void ensureNotEmpty(Object[] arg, String name) {
		ensureNotNull(arg, name);
		if (arg.length == 0) {
			throw new IllegalArgumentException(
					String.format("Argument '%s' cannot be empty.", name));
		}
	}

	public static void ensureContainsNoNull(Object[] arg, String name) {
		ensureNotNull(arg, name);
		for (int i = 0; i < arg.length; ++i) {
			if (arg[i] == null) {
				throw new NullPointerException(
						String.format("Argument %s[%d] is null.", name, Integer.valueOf(i)));
			}
		}
	}

	public static void ensureContainsNoNull(Collection<?> arg, String name) {
		ensureNotNull(arg, name);
		for (Object o : arg) {
			if (o == null) {
				throw new NullPointerException(
						String.format("%s contains null argument(s).", name));
			}
		}
	}

	public static void ensureNotEmpty(int[] arg, String name) {
		ensureNotNull(arg, name);
		if (arg.length == 0) {
			throw new IllegalArgumentException(
					String.format("Argument '%s' cannot be empty.", name));
		}
	}

	// ==================== logging / tracking (major steps in application) ================

	/** Logs <code>title</code> with <code>level</code> (for major steps in the application). */
	public static void logTitle(Level level, String... title) {
		_logger.log(level, "");
		_logger.log(level, "================================================");
		for (String t : title) {
			_logger.log(level, t);
		}
		_logger.log(level, "================================================");
	}

	/** Logs <code>subtitle</code> with <code>level</code> (for sub-steps in the application). */
	public static void logSubtitle(Level level, String subtitle) {
		_logger.log(level, "");
		_logger.log(level, "------------------------------------------------");
		_logger.log(level, subtitle);
	}

	/**
	 * Logs <code>text</code> with <code>level</code> (and if <code>skipTime=false</code>, duration
	 * since <code>startMillis</code>).
	 */
	public static void logCompletion(Level level, String text, long startMillis, boolean skipTime) {
		String time = "";
		if (!skipTime) {
			long durationMillis = System.currentTimeMillis() - startMillis;
			time = String.format("time=[%s] ", formatDuration(durationMillis));
		}
		_logger.log(level, time + text);
		_logger.log(level, "");
	}

	/**
	 * Logs each element in <code>objects</code>.
	 *
	 * @param level
	 *            logging level.
	 * @param objects
	 *            objects to log.
	 * @param what
	 *            title to print when <code>objects</code> is not empty.
	 */
	public static <T> void logCollection(Level level, Collection<T> objects, String what) {
		if (!objects.isEmpty()) {
			_logger.log(level, "");
			_logger.log(level, String.format("======= %d %s: ========",
					Integer.valueOf(objects.size()), what));
			for (T o : objects) {
				_logger.log(level, o.toString());
			}
		}
	}

	/**
	 * Logs each element in <code>objects</code>.
	 *
	 * @param level
	 *            logging level.
	 * @param objects
	 *            objects to log.
	 * @param what
	 *            title to print when <code>objects</code> is not empty.
	 */
	public static <T, V> void logMap(Level level, Map<T, ? extends Collection<V>> objects,
			String what) {
		if (!objects.isEmpty()) {
			_logger.log(level, "");
			_logger.log(level, String.format("======= %d %s: ========",
					Integer.valueOf(objects.size()), what));
			for (Entry<T, ? extends Collection<V>> o : objects.entrySet()) {
				_logger.log(level, o.getKey().toString() + ":");
				for (V value : o.getValue()) {
					_logger.log(level, "   " + value.toString());
				}
			}
		}
	}
}
