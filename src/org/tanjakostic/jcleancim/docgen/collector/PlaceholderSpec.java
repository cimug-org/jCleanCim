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

package org.tanjakostic.jcleancim.docgen.collector;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;

/**
 * When using {@link FreeFormDocumentation}, templates for doc generation have to use labels to
 * indicate where to insert the documentation of what element of the UML model into the output
 * document. Currently needed and recognised labels to be used in the input templates are in the
 * following format:
 *
 * <pre>
 *   startUmlDiagram.{packageName}.{diagramName}.endUml
 *   startUmlDiagNote.{packageName}.{diagramName}.endUml
 *   startUmlAttribute.{className}.{attributeName}.endUml
 *   startUmlIec61850NsName.{className}.endUml		 (IEC 61850-7-*, for name space name)
 *   startUmlFile..endUml
 *
 *   startUmlPresenceConditions.{packageName}.endUml (IEC 61850-7-3, for presence conditions table)
 *   startUmlFCs.{packageName}.endUml                (IEC 61850-7-3 and IEC 61850-7-2, for FC table)
 *   startUmlTrgOps.{packageName}.endUml             (IEC 61850-7-2, for TrgOp table)
 *   startUmlAbbreviations.{packageName}.endUml      (IEC 61850-7-4, for DO abbreviations table)
 *   startUmlSclEnums.{packageName}.endUml           (IEC 61850-7-4, 7-3, for enums as XML)
 *
 *   startUmlPackage.{packageName}.endUml
 *   startUmlClass.{packageName}.{className}.endUml
 *   startUmlDataIndex.{packageName}.endUml          (for IEC 61850-7-4,3, for data semantics tables)
 *   startUmlLNMapPackage.{packageName}.endUml       (for IEC 61850-7-4, for function/LN map tables)
 * </pre>
 *
 * The tokens enlosed in curly braces are the names of UML elements designating what needs to be
 * inserted in place of the whole above string.
 * <p>
 * This format avoids us the need to define bookmarks in the input document (tedious and
 * error-prone) and makes it simple to sequentially search the input document and insert the text
 * and diagrams as they come.
 * <p>
 * <b>Usage</b>
 * <p>
 * Instance of kind {@link Kind#UNSUPPORTED} always returns non-null error that you may want to use
 * to replace the placeholder to indicate failure. Instances of kind {@link Kind#FILE} are the
 * simplest as they need not parse anything, so no errors and no saved tokens.
 * <p>
 * The instances of other kinds do need to parse the placeholder and will have tokens set as
 * follows:
 * <ul>
 * <li>first token ({@link Kind#PRES_CONDITIONS}, {@link Kind#ABBREVIATIONS}, {@link Kind#SCL_ENUMS}
 * , {@link Kind#PACKAGE}, {@link Kind#LNMAP_PACKAGE}, {@link Kind#DATA_INDEX}, {@link Kind#FCS},
 * {@link Kind#TRGOPS}) or
 * <li>both tokens ({@link Kind#ATTRIBUTE}, {@link Kind#DIAGRAM}, {@link Kind#DIAG_NOTE},
 * {@link Kind#CLASS}).
 * </ul>
 * In case the parsed token(s) is null or empty, the instance will contain a non-null error string.
 * So, you will want to check first for error (null means no errors) before passing tokens for
 * search, and in case of error, you may want to replace the placeholder to indicate failure.
 * <p>
 * <b>Important:</b> To have correct headings and paragraph formats, ensure you use the following
 * placeholders in a heading paragraph:
 * <ul>
 * <li>{@link Kind#PACKAGE},
 * <li>{@link Kind#CLASS},
 * <li>{@link Kind#PRES_CONDITIONS},
 * <li>{@link Kind#FCS},
 * <li>{@link Kind#TRGOPS},
 * <li>{@link Kind#SCL_ENUMS},
 * <li>{@link Kind#LNMAP_PACKAGE}, and,
 * <li>{@link Kind#DATA_INDEX}.
 * </ul>
 *
 * @author tatjana.kostic@ieee.org
 * @version $Id: PlaceholderSpec.java 21 2019-08-12 15:44:50Z dev978 $
 */
public class PlaceholderSpec {

	public enum Kind {
		FILE,
		DIAGRAM,
		/**
		 * Note: We intentionally do not use 'DiagramNote' to avoid overlap with 'Diagram', for text
		 * search.
		 */
		DIAG_NOTE,
		ATTRIBUTE,
		IEC61850_NSNAME,
		PRES_CONDITIONS,
		FCS,
		TRGOPS,
		ABBREVIATIONS,
		SCL_ENUMS,
		PACKAGE,
		CLASS,
		DATA_INDEX,
		LNMAP_PACKAGE,
		/** This one is internal, never used by client. */
		HYPERLINK, /** Anything not matched by others. */
		UNSUPPORTED;

		/** Returns whether <code>kind</code> is expected to be in the heading paragraph. */
		public static boolean isForHeading(Kind kind) {
			return EnumSet.of(PACKAGE, CLASS, PRES_CONDITIONS, FCS, TRGOPS, SCL_ENUMS,
					LNMAP_PACKAGE, DATA_INDEX).contains(kind);
		}
	}

	protected static final String START_UML = "startUml";
	protected static final String END_UML = "endUml";
	public static final String SEPARATOR = ".";
	protected static final String WILDCARD = "*";

	protected static final String PH_LETTERS = "[ACDFILTPS]";
	public static final String MS_PATTERN = String.format("%s%s%s%s%s%s%s",
			PlaceholderSpec.START_UML, PH_LETTERS, PlaceholderSpec.WILDCARD,
			PlaceholderSpec.SEPARATOR, PlaceholderSpec.WILDCARD, PlaceholderSpec.SEPARATOR,
			PlaceholderSpec.END_UML);

	private static final String UML_FILE = START_UML + "File";
	private static final String UML_DIAGRAM = START_UML + "Diagram";
	private static final String UML_DIAG_NOTE = START_UML + "DiagNote";
	private static final String UML_ATTRIBUTE = START_UML + "Attribute";
	private static final String UML_IEC61850_NSNAME = START_UML + "Iec61850NsName";
	private static final String UML_PRES_CONDITIONS = START_UML + "PresenceConditions";
	private static final String UML_FCS = START_UML + "FCs";
	private static final String UML_TRGOPS = START_UML + "TrgOps";
	private static final String UML_ABBREVIATIONS = START_UML + "Abbreviations";
	private static final String UML_SCL_ENUMS = START_UML + "SclEnums";
	private static final String UML_PACKAGE = START_UML + "Package";
	private static final String UML_CLASS = START_UML + "Class";
	private static final String UML_LNMAP_PACKAGE = START_UML + "LNMapPackage";
	private static final String UML_DATA_INDEX = START_UML + "DataIndex";

	private static final String TOKEN_ERROR_TEXT = "Null or empty token";
	private static final String PLACEHOLDER_ERROR_TEXT = "Unrecognised placeholder";
	private static final String NOT_IN_MODEL_ERROR_TEXT = " not found in model";

	public static String constructFilePlaceholderText() {
		return UML_FILE + SEPARATOR + SEPARATOR + END_UML;
	}

	public static String constructDiagramPlaceholderText(String containerName, String diagName) {
		return UML_DIAGRAM + SEPARATOR + containerName + SEPARATOR + diagName + SEPARATOR + END_UML;
	}

	public static String constructDiagNotePlaceholderText(String containerName, String diagName) {
		return UML_DIAG_NOTE + SEPARATOR + containerName + SEPARATOR + diagName + SEPARATOR
				+ END_UML;
	}

	public static String constructAttributePlaceholderText(String className, String attrName) {
		return UML_ATTRIBUTE + SEPARATOR + className + SEPARATOR + attrName + SEPARATOR + END_UML;
	}

	public static String constructIec61850NsNamePlaceholderText(String className) {
		return UML_IEC61850_NSNAME + SEPARATOR + className + SEPARATOR + END_UML;
	}

	public static String constructPresConditionsPackagePlaceholderText(String pckName) {
		return UML_PRES_CONDITIONS + SEPARATOR + pckName + SEPARATOR + END_UML;
	}

	public static String constructFcsPackagePlaceholderText(String pckName) {
		return UML_FCS + SEPARATOR + pckName + SEPARATOR + END_UML;
	}

	public static String constructTrgOpsPackagePlaceholderText(String pckName) {
		return UML_TRGOPS + SEPARATOR + pckName + SEPARATOR + END_UML;
	}

	public static String constructAbbrPackagePlaceholderText(String pckName) {
		return UML_ABBREVIATIONS + SEPARATOR + pckName + SEPARATOR + END_UML;
	}

	public static String constructEnumPackagePlaceholderText(String pckName) {
		return UML_SCL_ENUMS + SEPARATOR + pckName + SEPARATOR + END_UML;
	}

	public static String constructPackagePlaceholderText(String pckName) {
		return UML_PACKAGE + SEPARATOR + pckName + SEPARATOR + END_UML;
	}

	public static String constructClassPlaceholderText(String pckName, String className) {
		return UML_CLASS + SEPARATOR + pckName + SEPARATOR + className + SEPARATOR + END_UML;
	}

	public static String constructLNMapPackagePlaceholderText(String pckName) {
		return UML_LNMAP_PACKAGE + SEPARATOR + pckName + SEPARATOR + END_UML;
	}

	public static String constructDataIndexPlaceholderText(String pckName) {
		return UML_DATA_INDEX + SEPARATOR + pckName + SEPARATOR + END_UML;
	}

	// ---------- these are for hyperlinks; never used by client -------------
	private static final String HL_START = "aa";
	private static final String HL_SEP = "ù";
	private static final String HL_END = "dd";
	public static final String HL_MS_PATTERN = String.format("%s%s%s%s%s%s%s",
			PlaceholderSpec.HL_START, PlaceholderSpec.HL_SEP, PlaceholderSpec.WILDCARD,
			PlaceholderSpec.HL_SEP, PlaceholderSpec.WILDCARD, PlaceholderSpec.HL_SEP,
			PlaceholderSpec.HL_END);

	public static String constructInternalHyperlinkPlaceholderText(String umlObjectName,
			String bookmarkID) {
		return HL_START + HL_SEP + umlObjectName + HL_SEP + bookmarkID + HL_SEP + HL_END;
	}

	public static List<String> getSupportedFormats() {
		List<String> result = new ArrayList<String>();
		result.add(constructFilePlaceholderText());
		result.add(constructAttributePlaceholderText("className", "attrName"));
		result.add(constructIec61850NsNamePlaceholderText("className"));
		result.add(constructDiagramPlaceholderText("containerName", "diagName"));
		result.add(constructDiagNotePlaceholderText("containerName", "diagName"));
		result.add(constructPresConditionsPackagePlaceholderText("pckName"));
		result.add(constructFcsPackagePlaceholderText("pckName"));
		result.add(constructTrgOpsPackagePlaceholderText("pckName"));
		result.add(constructAbbrPackagePlaceholderText("pckName"));
		result.add(constructEnumPackagePlaceholderText("pckName"));
		result.add(constructPackagePlaceholderText("pckName"));
		result.add(constructClassPlaceholderText("pckName", "className"));
		result.add(constructLNMapPackagePlaceholderText("pckName"));
		result.add(constructDataIndexPlaceholderText("pckName"));
		result.add(constructInternalHyperlinkPlaceholderText("umlObjectName", "bookmarkID"));
		return result;
	}

	// ---------------------------- instance ---------------------------

	private final String _text;
	private Kind _kind;
	private String _firstToken;
	private String _secondToken;
	private String _errorText;

	public PlaceholderSpec(String text) {
		_text = text;
		if (text.startsWith(PlaceholderSpec.UML_FILE)) {
			init(Kind.FILE, null, null, null);
		} else if (text.startsWith(PlaceholderSpec.UML_ATTRIBUTE)) {
			String[] body = parseNames(PlaceholderSpec.SEPARATOR);
			String className = (body != null && body.length == 2) ? body[0] : null;
			String attrName = (body != null && body.length == 2) ? body[1] : null;
			String error = (isNullOrEmpty(className) || isNullOrEmpty(attrName))
					? formatErrMessage() : null;
			init(Kind.ATTRIBUTE, className, attrName, error);
		} else if (text.startsWith(PlaceholderSpec.UML_IEC61850_NSNAME)) {
			String[] body = parseNames(PlaceholderSpec.SEPARATOR);
			String className = (body != null && body.length == 1) ? body[0] : null;
			String error = isNullOrEmpty(className) ? formatErrMessage() : null;
			init(Kind.IEC61850_NSNAME, className, null, error);
		} else if (text.startsWith(PlaceholderSpec.UML_DIAGRAM)) {
			String[] body = parseNames(PlaceholderSpec.SEPARATOR);
			String containerName = (body != null && body.length == 2) ? body[0] : null;
			String diagramName = (body != null && body.length == 2) ? body[1] : null;
			String error = (isNullOrEmpty(containerName) || isNullOrEmpty(diagramName))
					? formatErrMessage() : null;
			init(Kind.DIAGRAM, containerName, diagramName, error);
		} else if (text.startsWith(PlaceholderSpec.UML_DIAG_NOTE)) {
			String[] body = parseNames(PlaceholderSpec.SEPARATOR);
			String containerName = (body != null && body.length == 2) ? body[0] : null;
			String diagramName = (body != null && body.length == 2) ? body[1] : null;
			String error = (isNullOrEmpty(containerName) || isNullOrEmpty(diagramName))
					? formatErrMessage() : null;
			init(Kind.DIAG_NOTE, containerName, diagramName, error);
		} else if (text.startsWith(PlaceholderSpec.UML_PRES_CONDITIONS)) {
			String[] body = parseNames(PlaceholderSpec.SEPARATOR);
			String packageName = (body != null && body.length == 1) ? body[0] : null;
			String error = isNullOrEmpty(packageName) ? formatErrMessage() : null;
			init(Kind.PRES_CONDITIONS, packageName, null, error);
		} else if (text.startsWith(PlaceholderSpec.UML_FCS)) {
			String[] body = parseNames(PlaceholderSpec.SEPARATOR);
			String packageName = (body != null && body.length == 1) ? body[0] : null;
			String error = isNullOrEmpty(packageName) ? formatErrMessage() : null;
			init(Kind.FCS, packageName, null, error);
		} else if (text.startsWith(PlaceholderSpec.UML_TRGOPS)) {
			String[] body = parseNames(PlaceholderSpec.SEPARATOR);
			String packageName = (body != null && body.length == 1) ? body[0] : null;
			String error = isNullOrEmpty(packageName) ? formatErrMessage() : null;
			init(Kind.TRGOPS, packageName, null, error);
		} else if (text.startsWith(PlaceholderSpec.UML_ABBREVIATIONS)) {
			String[] body = parseNames(PlaceholderSpec.SEPARATOR);
			String packageName = (body != null && body.length == 1) ? body[0] : null;
			String error = isNullOrEmpty(packageName) ? formatErrMessage() : null;
			init(Kind.ABBREVIATIONS, packageName, null, error);
		} else if (text.startsWith(PlaceholderSpec.UML_SCL_ENUMS)) {
			String[] body = parseNames(PlaceholderSpec.SEPARATOR);
			String packageName = (body != null && body.length == 1) ? body[0] : null;
			String error = isNullOrEmpty(packageName) ? formatErrMessage() : null;
			init(Kind.SCL_ENUMS, packageName, null, error);
		} else if (text.startsWith(PlaceholderSpec.UML_PACKAGE)) {
			String[] body = parseNames(PlaceholderSpec.SEPARATOR);
			String packageName = (body != null && body.length == 1) ? body[0] : null;
			String error = isNullOrEmpty(packageName) ? formatErrMessage() : null;
			init(Kind.PACKAGE, packageName, null, error);
		} else if (text.startsWith(PlaceholderSpec.UML_CLASS)) {
			String[] body = parseNames(PlaceholderSpec.SEPARATOR);
			String packageName = (body != null && body.length == 2) ? body[0] : null;
			String className = (body != null && body.length == 2) ? body[1] : null;
			String error = (isNullOrEmpty(packageName) || isNullOrEmpty(className))
					? formatErrMessage() : null;
			init(Kind.CLASS, packageName, className, error);
		} else if (text.startsWith(PlaceholderSpec.UML_LNMAP_PACKAGE)) {
			String[] body = parseNames(PlaceholderSpec.SEPARATOR);
			String packageName = (body != null && body.length == 1) ? body[0] : null;
			String error = isNullOrEmpty(packageName) ? formatErrMessage() : null;
			init(Kind.LNMAP_PACKAGE, packageName, null, error);
		} else if (text.startsWith(PlaceholderSpec.UML_DATA_INDEX)) {
			String[] body = parseNames(PlaceholderSpec.SEPARATOR);
			String packageName = (body != null && body.length == 1) ? body[0] : null;
			String error = isNullOrEmpty(packageName) ? formatErrMessage() : null;
			init(Kind.DATA_INDEX, packageName, null, error);
		} else if (text.startsWith(PlaceholderSpec.HL_START)) {
			String[] body = parseNames(PlaceholderSpec.HL_SEP);
			String umlObjectName = (body != null && body.length == 2) ? body[0] : null;
			String bookmarkID = (body != null && body.length == 2) ? body[1] : null;
			String error = (isNullOrEmpty(umlObjectName) || isNullOrEmpty(bookmarkID))
					? formatErrMessage() : null;
			init(Kind.HYPERLINK, umlObjectName, bookmarkID, error);
		} else {
			init(Kind.UNSUPPORTED, null, null, formatUnsupportedMessage());
		}
	}

	private boolean isNullOrEmpty(String token) {
		return token == null || "null".equals(token) || token.isEmpty();
	}

	/** Splits text {@link #getText()} with <code>separator</code>. */
	private String[] parseNames(String separator) {
		String splitRegex = "\\" + separator;
		String[] body = _text.split(splitRegex);
		List<String> result = new ArrayList<String>();
		for (int i = 1; i < body.length - 1; ++i) {
			result.add(body[i]);
		}
		return result.toArray(new String[0]);
	}

	private String formatErrMessage() {
		return formatErrMessage(TOKEN_ERROR_TEXT);
	}

	private String formatUnsupportedMessage() {
		return formatErrMessage(PLACEHOLDER_ERROR_TEXT);
	}

	private String formatErrMessage(String errorText) {
		return "$ERROR " + _text + ": " + errorText + "$";
	}

	private String formatModelErrMessage() {
		return "$ERROR " + toString() + NOT_IN_MODEL_ERROR_TEXT + "$";
	}

	private void init(Kind kind, String name1, String name2, String error) {
		_kind = kind;
		_firstToken = name1;
		_secondToken = name2;
		_errorText = error;
	}

	// ----------------- API -----------------

	/**
	 * Returns the placeholder text.
	 */
	public String getText() {
		return _text;
	}

	public Kind getKind() {
		return _kind;
	}

	public String getFirstToken() {
		return _firstToken;
	}

	public String getSecondToken() {
		return _secondToken;
	}

	/**
	 * Returns null if there are no parsing errors, the error message otherwise.
	 */
	public String getErrorText() {
		return _errorText;
	}

	/**
	 * Use this setter when finder could not find valid tokens in the model (e.g., format of the
	 * placeholder is ok, but the names do not match elements in the model).
	 */
	public void updateModelErrorText() {
		_errorText = formatModelErrMessage();
	}

	@Override
	public String toString() {
		if (_errorText != null) {
			return _errorText;
		}
		StringBuilder sb = new StringBuilder(_kind.toString());
		if (_firstToken != null) {
			sb.append(" ").append(_firstToken);
			if (_secondToken != null) {
				sb.append(", ").append(_secondToken);
			}
		}
		return sb.toString();
	}
}
