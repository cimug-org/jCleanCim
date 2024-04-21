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

package org.tanjakostic.jcleancim.docgen.collector.impl;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.tanjakostic.jcleancim.common.Nature;
import org.tanjakostic.jcleancim.docgen.collector.BookmarkRegistry;
import org.tanjakostic.jcleancim.docgen.collector.DocgenConfig;
import org.tanjakostic.jcleancim.docgen.collector.IDHelper;
import org.tanjakostic.jcleancim.docgen.collector.ObjectDoc;
import org.tanjakostic.jcleancim.docgen.collector.PlaceholderSpec;
import org.tanjakostic.jcleancim.docgen.collector.RawData;
import org.tanjakostic.jcleancim.docgen.collector.RawDataImpl;
import org.tanjakostic.jcleancim.docgen.collector.WAX;
import org.tanjakostic.jcleancim.model.AbstractUmlObject;
import org.tanjakostic.jcleancim.model.TextDescription;
import org.tanjakostic.jcleancim.model.UML;
import org.tanjakostic.jcleancim.model.UmlClass;
import org.tanjakostic.jcleancim.model.UmlObject;
import org.tanjakostic.jcleancim.model.UmlPackage;
import org.tanjakostic.jcleancim.model.UmlVisibility;
import org.tanjakostic.jcleancim.util.Util;

/**
 * Common implementation for any kind of object documentation.
 *
 * @author tatjana.kostic@ieee.org
 * @author laurent.guise@art-et-histoire.com
 * @version $Id: AbstractObjectDoc.java 34 2019-12-20 18:37:17Z dev978 $
 */
abstract public class AbstractObjectDoc implements ObjectDoc {
	private static final Logger _logger = Logger.getLogger(AbstractObjectDoc.class.getName());
	private static final Level CTOR_LOG_LEVEL = null;

	private final DocgenConfig _docgenCfg;
	private final String _headingText;
	private final TextDescription _description;
	private final RawData _rawData;

	private final BookmarkRegistry _bmRegistry;
	private final String _bookmarkID;

	protected static void log(Logger logger, String message) {
		if (CTOR_LOG_LEVEL != null) {
			logger.log(CTOR_LOG_LEVEL, message);
		}
	}

	/**
	 * Returns deduced string formatted according to <code>fmt</code>, from deprecated and
	 * informative status, potentially more qualifiers, and potentially custom stereotypes. You will
	 * typically use this with a format <code>fmt</code> that has some enclosure for a string (e.g.,
	 * (), "") and ends with a white space, to be usable as prefix.
	 * <p>
	 * FIXME: This became a terrible method, but I had to move forward... Needs refactoring !
	 *
	 * @param o
	 *            UML object
	 * @param builtIns
	 *            map of built-in stereotypes applicable to <code>o</code> per model nature
	 * @param moreTokens
	 *            (potentially null or empty) list of additional tokens, to append immediately after
	 * @param withCustomStereotypes
	 *            whether to include custom (non-built-in) stereotypes
	 * @param parent
	 *            when non-null, using custom stereotypes of parent instead of <code>o</code>; note
	 *            that you must take care of appropriately passing in the <code>builtIns</code>
	 * @param fmt
	 *            String format for single string
	 */
	protected static String deduceQualifiersPrefix(UmlObject o, Map<Nature, Set<String>> builtIns,
			List<String> moreTokens, boolean withCustomStereotypes, UmlObject parent, String fmt) {
		List<String> tokens = AbstractUmlObject.addDeprecAndInf(o);
		if (moreTokens != null && !moreTokens.isEmpty()) {
			tokens.addAll(moreTokens);
		}
		if (withCustomStereotypes) {
			UmlObject customStereosObject = (parent != null) ? parent : o;
			AbstractUmlObject.appendRemainingCustomStereotypes(tokens, customStereosObject,
					builtIns);
		}
		String concat = Util.concatCharSeparatedTokens(",", tokens);
		return concat.isEmpty() ? "" : String.format(fmt, concat);
	}

	/**
	 * Invokes {@link #deduceQualifiersPrefix(UmlObject, Map, List, boolean, UmlObject, String)}
	 * without <code>moreTokens</code> and <code>parent</code>.
	 */
	protected static String deduceQualifiersPrefix(UmlObject o, Map<Nature, Set<String>> builtIns,
			boolean withCustomStereotypes, String fmt) {
		return deduceQualifiersPrefix(o, builtIns, null, withCustomStereotypes, null, fmt);
	}

	/**
	 * Invokes {@link #deduceQualifiersPrefix(UmlObject, Map, List, boolean, UmlObject, String)}
	 * without <code>moreTokens</code>.
	 */
	protected static String deduceQualifiersPrefix(UmlObject o, Map<Nature, Set<String>> builtIns,
			boolean withCustomStereotypes, UmlObject parent, String fmt) {
		return deduceQualifiersPrefix(o, builtIns, null, withCustomStereotypes, parent, fmt);
	}

	/**
	 * Invokes {link
	 * {@link #deduceQualifiersPrefix(UmlObject, Map, List, boolean, UmlObject, String)} without
	 * <code>parent</code>.
	 */
	protected static String deduceQualifiersPrefix(UmlObject o, Map<Nature, Set<String>> builtIns,
			List<String> moreTokens, boolean withCustomStereotypes, String fmt) {
		return deduceQualifiersPrefix(o, builtIns, moreTokens, withCustomStereotypes, null, fmt);
	}

	/**
	 * "Centralised" constructor, allowing for instantiation both with and without a UML object, and
	 * with and without descriptions, as follows:
	 * <p>
	 * If <code>object</code> is non-null (and <code>ignoreDesc=false</code>), its text and HTML
	 * description fields will be used, and raw data will be added for object name, alias,
	 * description (as HTML) and heading text; if <code>ignoreDesc=true</code>, no description raw
	 * data will be added.
	 * <p>
	 * If <code>object</code> is null, no raw data will be created at all; if
	 * <code>ignoreDesc=false</code>, the explicit <code>description</code> and
	 * <code>htmlDescription</code> will be used instead.
	 * <p>
	 * Instance description (returned by {@link #getDescription()}) retained will be in HTML format
	 * only if configuration enables HTML printing and <code>htmlDescription</code> is not empty.
	 * Otherwise, the text description is retained. If <code>ignoreDesc=true</code>, returned values
	 * are just empty text or HTML description.
	 *
	 * @param docgenCfg
	 *            non-null document generation specific configuration.
	 * @param o
	 *            (possibly null) UML object.
	 * @param what
	 *            (possibly null) describes kind of properties of <code>object</code>; used to
	 *            ensure unique aliases, descriptions, etc. when an object may have multiple groups
	 *            of properties (in particular, class with its attributes, associations and
	 *            operations).
	 * @param description
	 *            (possibly null) text format description.
	 * @param htmlDescription
	 *            (possibly null) HTML format description.
	 * @param ignoreDesc
	 *            whether to ignore description altogether.
	 * @param headingText
	 *            (possibly null) heading text, to be used as chapter title.
	 * @param bookmarkID
	 *            (possibly null) bookmark ID.
	 * @param bmRegistry
	 *            non-null (but potentially empty) bookmark registry.
	 */
	protected AbstractObjectDoc(DocgenConfig docgenCfg, UmlObject o, String what,
			TextDescription description, TextDescription htmlDescription, boolean ignoreDesc,
			String headingText, String bookmarkID, BookmarkRegistry bmRegistry) {
		Util.ensureNotNull(docgenCfg, "docgenCfg");
		Util.ensureNotNull(bmRegistry, "bmRegistry");

		String whatPref = (what != null) ? (what + ".") : "";

		_docgenCfg = docgenCfg;
		_bmRegistry = bmRegistry;
		_headingText = Util.null2empty(headingText);
		_rawData = new RawDataImpl();

		if (o != null) {
			putCell(WAX.LOC_tag, o.getKind().getTag());
			putCell(WAX.A_name, o.getName());
			putCell(WAX.A_aliasID, createDocId(o, whatPref + "alias"));
			putCell(WAX.A_alias, o.getAlias());
			if (o.isInformative()) {
				putCell(WAX.A_informative, "true");
			}
			if (o.isDeprecated()) {
				putCell(WAX.A_deprecated, "true");
			}

			putCell(WAX.A_titleID, createDocId(o, whatPref + "title"));
			putCell(WAX.A_title, _headingText);

			String s = o.getTaggedValues().get(UML.TVN_nsuri);
			if (s != null) {
				putCell(UML.TVN_nsuri, s);
			}
			s = o.getTaggedValues().get(UML.TVN_nsprefix);
			if (s != null) {
				putCell(UML.TVN_nsprefix, s);
			}
		}

		_bookmarkID = bookmarkID;
		if (bookmarkID != null) {
			putCell(WAX.A_bookmarkID, bookmarkID);
		}

		TextDescription txtDesc = null;
		TextDescription htmlDesc = null;
		if (o != null) {
			txtDesc = o.getDescription();
			htmlDesc = o.getHtmlDescription();
			if (!ignoreDesc) {
				putCell(WAX.A_descID, createDocId(o, whatPref + "desc"));
				putCell(WAX.A_desc, useHtml(htmlDesc) ? htmlDesc.text : txtDesc.text);
			}
		} else {
			txtDesc = description;
			htmlDesc = htmlDescription;
		}
		if (ignoreDesc) {
			_description = useHtml(htmlDesc) ? TextDescription.EMPTY_HTML
					: TextDescription.EMPTY_TXT;
		} else {
			_description = useHtml(htmlDesc) ? htmlDesc
					: (txtDesc != null ? txtDesc : TextDescription.EMPTY_TXT);
		}
	}

	protected static final String createDocId(UmlObject obj, String ending) {
		Util.ensureNotNull(obj, "obj");
		return IDHelper.instance().createDocID(obj.getQualifiedName(), ending);
	}

	protected static String deduceBookmark(BookmarkRegistry bmRegistry, UmlObject obj) {
		Util.ensureNotNull(bmRegistry, "bmRegistry");
		Util.ensureNotNull(obj, "obj");
		return bmRegistry.findID(obj);
	}

	/**
	 * If hyperlink option is enabled, creates a hyperlink placeholder for <code>targetObj</code>
	 * whose text will be written instead of name, to be replaced by a hyperlink in additional pass.
	 * Otherwise, returns the name of <code>targetObj</code>.
	 */
	protected final String prepareForHyperlink(UmlObject targetObj) {
		return prepareForHyperlinkAdjustedName(targetObj, null);
	}

	/**
	 * Same as {@link #prepareForHyperlink(UmlObject)} except that it creates the hyperlink
	 * placeholder with
	 */
	protected final String prepareForHyperlinkAdjustedName(UmlObject targetObj,
			String nameToDisplay) {
		boolean replaceName = (nameToDisplay != null);

		String name = (replaceName) ? nameToDisplay : targetObj.getName();

		if (_docgenCfg.useHyperlinks) {
			String bookmarkID = getBmRegistry().findID(targetObj);
			if (bookmarkID != null) {
				String hphText = PlaceholderSpec.constructInternalHyperlinkPlaceholderText(name,
						bookmarkID);
				_logger.trace("+++ prepareForHyperlink, targetObj = " + name + ", hphSpec = '"
						+ hphText + "'");
				return hphText;
			}
		}
		return name;
	}

	/** Returns true if printing HTML is enabled and <code>htmlDescription</code> is not empty. */
	protected final boolean useHtml(TextDescription htmlDescription) {
		return getDocgenCfg().keepHtml && htmlDescription != null && !htmlDescription.isEmpty();
	}

	/** Returns whether to skip object <code>o</code>, according to configuration. */
	protected final boolean toSkip(UmlObject o) {
		if (!getDocgenCfg().includeInf && o.isInformative()) {
			return true;
		}
		if (!getDocgenCfg().includeNonPublic && o.getVisibility() != UmlVisibility.PUBLIC) {
			return true;
		}
		if (o.getStereotype().containsAnyOf(getDocgenCfg().skipForCustomStereotypes)) {
			return true;
		}
		return false;
	}

	protected final void filterClasses(UmlPackage p, Collection<UmlClass> retainedNatives) {
		for (UmlClass c : p.getClasses()) {
			if (!toSkip(c)) {
				retainedNatives.add(c);
			}
		}
	}

	// ===== impl. of org.tanjakostic.jcleancim.docgen.collector.ObjectDoc =====

	@Override
	public final DocgenConfig getDocgenCfg() {
		return _docgenCfg;
	}

	@Override
	public final String getHeadingText() {
		return _headingText;
	}

	/**
	 * This default implementation returns what has been explicitly initialised in the call to the
	 * constructor. Override in case you need some special processing.
	 */
	@Override
	public TextDescription getDescription() {
		return _description;
	}

	@Override
	public BookmarkRegistry getBmRegistry() {
		return _bmRegistry;
	}

	@Override
	public final String getBookmarkID() {
		return _bookmarkID;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder(super.toString());
		sb.append(_headingText).append(Util.NL);
		sb.append(_description.text).append(Util.NL);
		return sb.toString();
	}

	// ===== impl. of org.tanjakostic.jcleancim.docgen.collector.RawData =====

	@Override
	public final String putCell(String key, String value) {
		return _rawData.putCell(key, value);
	}

	@Override
	public final String copyCell(RawData src, String key) {
		return _rawData.copyCell(src, key);
	}

	@Override
	public final String copyNonEmptyCell(RawData src, String key) {
		return _rawData.copyNonEmptyCell(src, key);
	}

	@Override
	public final String putCellNonEmpty(String key, String value) {
		return _rawData.putCellNonEmpty(key, value);
	}

	@Override
	public final boolean hasKey(String key) {
		return _rawData.hasKey(key);
	}

	@Override
	public final Map<String, String> getCells() {
		return Collections.unmodifiableMap(_rawData.getCells());
	}

	@Override
	public final String getCell(String key) {
		return _rawData.getCell(key);
	}
}
