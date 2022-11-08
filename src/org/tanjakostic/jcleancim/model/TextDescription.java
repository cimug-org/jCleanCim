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

package org.tanjakostic.jcleancim.model;

import org.apache.log4j.Logger;
import org.tanjakostic.jcleancim.util.HTMLUtil;
import org.tanjakostic.jcleancim.util.Util;

/**
 * Simple data structure to hold together the text description content and its format to facilitate
 * writing of UML documentation that may be formatted.
 * <p>
 * Ensure you specify correct kind (text format), otherwise the result of writing the documentation
 * may have unexpected formatting.
 *
 * @author tatjana.kostic@ieee.org
 * @version $Id: TextDescription.java 21 2019-08-12 15:44:50Z dev978 $
 */
public class TextDescription {
	private static final Logger _logger = Logger.getLogger(TextDescription.class.getName());

	/**
	 * Kind of text formatting that helps to optimise writing text to various formats.
	 *
	 * @author tatjana.kostic@ieee.org
	 * @version $Id: TextDescription.java 21 2019-08-12 15:44:50Z dev978 $
	 */
	public enum TextKind {

		/** Text without any formatting, without any new line characters. */
		textNoNL,

		/** Text without any formatting, but having one or more new line characters. */
		textWithNL,

		/** HTML snippet, the content of the document <code>body</code> section. */
		htmlSnippet
	}

	public static final TextKind DEFAULT_KIND = TextKind.textNoNL;
	public static final String DEFAULT_TEXT = "";

	public static final TextDescription EMPTY_TXT = new TextDescription();
	public static final TextDescription EMPTY_HTML = new TextDescription("", TextKind.htmlSnippet);

	public final TextKind kind;
	public final String text;

	/**
	 * Creates an instance with defaults.
	 */
	public TextDescription() {
		this("", TextKind.textNoNL);
	}

	/**
	 * Creates an instance with the content in <code>text</code> trimmed of whitespace, and
	 * {@link #DEFAULT_KIND}.
	 *
	 * @param text
	 */
	public TextDescription(String text) {
		this(text, TextKind.textNoNL);
	}

	/**
	 * Constructor. If any argument is null, uses defaults.
	 *
	 * @param text
	 *            text; will be trimmed of whitespace.
	 * @param kind
	 *            kind of text; if <code>text</code> contains only whitespace and the argument is
	 *            {@link TextKind#textWithNL}, the kind stored will be {@link TextKind#textNoNL}. It
	 *            is the responsibility of the caller to properly set the kind of text according to
	 *            the content in <code>text</code>, otherwise any writing may produce undesired
	 *            results.
	 */
	public TextDescription(String text, TextKind kind) {
		this.text = (text == null) ? DEFAULT_TEXT : text.trim();
		this.kind = (kind == null) ? DEFAULT_KIND : (this.text.isEmpty()
				&& kind == TextKind.textWithNL ? TextKind.textNoNL : kind);
	}

	/**
	 * Returns new instance with the <code>prefix</code> prepended to the original text; or
	 * unmodified instance if <code>prefix</code> is null or empty.
	 *
	 * @param prefix
	 *            can be null/empty, but should not contain any markup or new line character
	 *            (otherwise, result is undefined).
	 */
	public TextDescription prepend(String prefix) {
		return prepend(prefix, null);
	}

	/**
	 * Returns new instance with the <code>prefix</code> prepended to the original text; or
	 * unmodified instance if <code>prefix</code> is null or empty.
	 *
	 * @param prefix
	 *            can be null/empty, but should not contain any markup or new line character
	 *            (otherwise, result is undefined).
	 * @param o
	 *            if not null, may be used for logging warning condition.
	 */
	public TextDescription prepend(String prefix, Object o) {
		if (!Util.hasContent(prefix)) {
			return this;
		}
		if (kind == TextKind.htmlSnippet) {
			String result = "";
			if (text.startsWith(HTMLUtil.P_START)) {
				result = HTMLUtil.P_START;
				result += prefix;
				result += text.substring(HTMLUtil.P_START.length());
			} else {
				if (text.isEmpty()) {
					result = HTMLUtil.P_START + prefix + HTMLUtil.P_END;
				} else {
					String msg = "HTML text does not start with <p>";
					if (o == null) {
						msg += ".";
					} else {
						msg += o.toString();
					}
					_logger.warn(msg);
					result = HTMLUtil.P_START + prefix + text;
				}
			}
			return new TextDescription(result, TextKind.htmlSnippet);
		}
		return new TextDescription(prefix + text, kind);
	}

	/**
	 * Returns new instance with the <code>paragraph</code> appended to the original text as a
	 * paragraph (for HTML, it will enclose <code>paragraph</code> into paragraph tags, and for
	 * text, it will first append a new line character then <code>paragraph</code>); or unmodified
	 * instance if <code>paragraph</code> is null or empty.
	 *
	 * @param paragraph
	 *            can be null/empty, can contain markup or new line character.
	 */
	public TextDescription appendParagraph(String paragraph) {
		if (!Util.hasContent(paragraph)) {
			return this;
		}
		if (kind == TextKind.htmlSnippet) {
			if (!text.endsWith(HTMLUtil.P_END)) {
				_logger.warn("HTML text does not end with " + HTMLUtil.P_END + ".");
			}
			String result = text + "<p>" + paragraph + "</p>";
			return new TextDescription(result, TextKind.htmlSnippet);
		}
		String result = text + Util.NL + paragraph;
		return new TextDescription(result, TextKind.textWithNL);
	}

	public boolean isEmpty() {
		return text.isEmpty();
	}

	@Override
	public String toString() {
		return text;
	}
}
