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

package org.tanjakostic.jcleancim.validation;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.tanjakostic.jcleancim.util.Util;

/**
 * Pattern compiled from the regular expression that will match characters invalid for the CIM
 * tokens, and most of IEC 61850 tokens. So, if the matcher returns a match from this pattern, this
 * will be the invalid character.
 * <p>
 * The valid token should start with a lower or upper case letter, and be followed by any number of
 * lower or upper case letters or numbers.
 *
 * @author tatjana.kostic@ieee.org
 * @version $Id: InvalidCharactersFinder.java 21 2019-08-12 15:44:50Z dev978 $
 */
public class InvalidCharactersFinder {

	public static final InvalidCharactersFinder STRICT = new InvalidCharactersFinder(
			"^\\d|[^a-zA-Z0-9]");
	public static final InvalidCharactersFinder STRICT_UNDERSCORE_DASH = new InvalidCharactersFinder(
			"^\\d|[^a-zA-Z0-9_-]");
	public static final InvalidCharactersFinder NUM_UNDERSCORE_DASH_SPACE_COMMA = new InvalidCharactersFinder(
			"[^a-zA-Z0-9 ,_-]");

	private final String _regexExpression;
	private Pattern _compiledRegexExpression; // lazy loaded

	/**
	 * Constructor.
	 */
	public InvalidCharactersFinder(String regexExpression) {
		Util.ensureNotNull(regexExpression, "regexExpression");
		_regexExpression = regexExpression;
	}

	public String getRegexExpression() {
		return _regexExpression;
	}

	public Pattern getCompiledRegexExpression() {
		if (_compiledRegexExpression == null) {
			_compiledRegexExpression = Pattern.compile(getRegexExpression());
		}
		return _compiledRegexExpression;
	}

	/**
	 * Returns the list of characters in <code>input</code> that are invalid according to regular
	 * expression passed at creation of this instance, empty list if all characters are valid or
	 * <code>input</code> is null or empty string.
	 */
	public List<String> findInvalidCharacters(String input) {
		if (input == null || input.isEmpty()) {
			return Collections.emptyList();
		}
		List<String> result = new ArrayList<String>();
		Matcher m = getCompiledRegexExpression().matcher(input);
		while (m.find()) {
			result.add(m.group());
		}
		return result;
	}
}
