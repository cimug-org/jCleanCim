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

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.tanjakostic.jcleancim.common.Nature;
import org.tanjakostic.jcleancim.model.UmlObject;
import org.tanjakostic.jcleancim.util.Util;

/**
 * Implements logging uniformly for all concrete implementations.
 *
 * @author tatjana.kostic@ieee.org
 * @version $Id: AbstractRule.java 27 2019-11-23 16:29:38Z dev978 $
 */
abstract public class AbstractRule implements Rule {
	private static final Logger _logger = Logger.getLogger(AbstractRule.class.getName());

	private final Logger _extLogger;
	private final Level _level;
	private final Category _category;
	private final Severity _severity;
	private final String _hypothesis;
	private final String _howToFix;
	private final String _what;

	/**
	 * Constructor; default level is ERROR.
	 *
	 * @param extLogger
	 *            logger to use; if null, abstract class logger is used.
	 * @param hypothesis
	 *            non-null, non-empty string to use as a title of logging entries.
	 * @param howToFix
	 *            non-null, non-empty string to use as a title of logging entries.
	 */
	protected AbstractRule(Logger extLogger, String hypothesis, String howToFix) {
		this(extLogger, null, null, null, hypothesis, howToFix);
	}

	/**
	 * Constructor.
	 *
	 * @param extLogger
	 *            logger to use; if null, abstract class logger is used.
	 * @param level
	 *            logging level; if null, default level is ERROR.
	 * @param severity
	 *            severity; if null, default severity is high.
	 * @param category
	 *            category; if null, default category is modellingRule.
	 * @param hypothesis
	 *            non-null, non-empty string to use as a title of logging entries.
	 * @param howToFix
	 *            non-null, non-empty string to use as a title of logging entries.
	 */
	protected AbstractRule(Logger extLogger, Level level, Severity severity, Category category,
			String hypothesis, String howToFix) {
		this(extLogger, level, severity, category, hypothesis, howToFix, null);
	}

	/**
	 * Constructor.
	 *
	 * @param extLogger
	 *            logger to use; if null, abstract class logger is used.
	 * @param level
	 *            logging level; if null, default level is ERROR.
	 * @param severity
	 *            severity; if null, default severity is high.
	 * @param category
	 *            category; if null, default category is modellingRule.
	 * @param hypothesis
	 *            non-null, non-empty string to use as a title of logging entries.
	 * @param howToFix
	 *            non-null, non-empty string to use as a title of logging entries.
	 * @param what
	 *            for those rules that apply to different UML elements, this should be the
	 *            designation of the UML element to be prepended to the message; null otherwise.
	 */
	private AbstractRule(Logger extLogger, Level level, Severity severity, Category category,
			String hypothesis, String howToFix, String what) {
		Util.ensureNotEmpty(hypothesis, "hypothesis");
		Util.ensureNotEmpty(howToFix, "howToFix");

		_extLogger = (extLogger == null) ? _logger : extLogger;
		_level = (level == null) ? Level.ERROR : level;
		_category = (category == null) ? Category.modellingRule : category;
		_severity = (severity == null) ? Severity.high : severity;
		_hypothesis = hypothesis;
		_howToFix = howToFix;
		_what = what;
	}

	// ===== Impl. of org.tanjakostic.jcleancim.validation.Rule methods =====

	@Override
	public Category getCategory() {
		return _category;
	}

	@Override
	public Severity getSeverity() {
		return _severity;
	}

	@Override
	public String getHypothesis() {
		return _hypothesis;
	}

	@Override
	public String getHowToFix() {
		return _howToFix;
	}

	@Override
	public final void logDiagnosis(boolean verbose, ModelIssues issues) {
		List<String> items = issues.getDiagnosisItems(this.getClass().getSimpleName());
		String msg = getHypothesis() + " - " + getHowToFix();
		String diagnosisTitle = (_what != null) ? (_what + " " + msg) : msg;
		String ending = (items.size() == 0) ? "." : ":";
		String title = "Found " + items.size() + " " + diagnosisTitle + ending;

		Level actualLevel = null;
		if (items.size() == 0) {
			actualLevel = verbose ? Level.INFO : Level.DEBUG;
		} else {
			actualLevel = _level;
		}

		_extLogger.log(actualLevel, title);
		for (String item : items) {
			_extLogger.log(actualLevel, item);
		}
	}

	@Override
	public final Level getLogLevel() {
		return _level;
	}

	protected final ModelIssue createIssue(UmlObject subject) {
		return createIssue(subject, null, null, null);
	}

	protected final ModelIssue createIssue(UmlObject subject, String evidence) {
		return createIssue(subject, evidence, null, null);
	}

	protected final ModelIssue createIssue(UmlObject subject, String evidence,
			String subjectDescription, String groupTag) {
		return new ModelIssue(subject, this, subjectDescription, evidence, groupTag);
	}

	/**
	 * Common superclass where a rule applies to multiple UML object types (e.g., package and
	 * association) and their sub-objects (e.g., association ends) . Allows also to skip validation
	 * by letting subtypes override {@link #skipValidation(UmlObject)} and/or
	 * {@link #skipSubobjectValidation(UmlObject)}. This is necessary in particular for complex
	 * IEC61850 models, to avoid lots of noise where e.g. we don't care about a doc for something
	 * that is just a modelling artefact, but not really part of the official specification.
	 *
	 * @param <T>
	 * @author tatjana.kostic@ieee.org
	 * @version $Id: AbstractRule.java 27 2019-11-23 16:29:38Z dev978 $
	 */
	abstract public static class AbstractRuleWithSubobjectsAndSkips<T extends UmlObject>
			extends AbstractRule implements SimpleRule<T> {

		protected AbstractRuleWithSubobjectsAndSkips(Logger extLogger, Level level,
				Severity severity, Category category, String hypothesis, String howToFix,
				String what) {
			super(extLogger, level, severity, category, hypothesis, howToFix, what);
		}

		@Override
		public final void validate(T o, ModelIssues issues) {
			List<? extends UmlObject> subObjects = getSubObjects(o);
			if (!subObjects.isEmpty() && !skipSubobjectValidation(o)) {
				for (UmlObject subObject : subObjects) {
					doValidate(subObject, issues);
				}
			} else {
				if (!skipValidation(o)) {
					doValidate(o, issues);
				}
			}
		}

		abstract protected void doValidate(UmlObject o, ModelIssues issues);

		/**
		 * Used for validation of "sub-objects" (such as association ends and operation parameters)
		 * that do not have their own validators, but get validated through their container.
		 * <p>
		 * This default implementation returns empty list; subtypes having contained objects that
		 * need to be validated against this rule have to override this method.
		 */
		protected List<UmlObject> getSubObjects(@SuppressWarnings("unused") T o) {
			return Collections.emptyList();
		}

		/**
		 * This default implementation returns false (no skipping); override if sub-objects don't
		 * need validation.
		 */
		protected boolean skipSubobjectValidation(@SuppressWarnings("unused") T o) {
			return false;
		}

		/**
		 * This default implementation returns false (no skipping); override if main object doesn't
		 * need validation.
		 */
		protected boolean skipValidation(@SuppressWarnings("unused") T o) {
			return false;
		}
	}

	// ==================== simple rules =====================

	abstract public static class UmlObjectsWithUnallowedStereotype<T extends UmlObject>
			extends AbstractRuleWithSubobjectsAndSkips<T> {

		private static final String HYPO_END = "with unallowed stereotype(s)";
		private static final String HOWTO_START = "remove offending stereotype(s) OR use one or more of ";
		// TODO: insert as second option when custom implemented: " OR extend the list of allowed
		// stereotypes"
		private final Map<Nature, Set<String>> _alloweds;

		protected UmlObjectsWithUnallowedStereotype(Logger logger, String what,
				Map<Nature, Set<String>> alloweds) {
			super(logger, Level.ERROR, Severity.high, Category.modellingRule, HYPO_END,
					HOWTO_START + alloweds.toString(), what);
			_alloweds = alloweds;
		}

		private Map<Nature, Set<String>> getAllowedStereotypes() {
			return _alloweds;
		}

		/**
		 * {@inheritDoc}
		 * <p>
		 * Matches any stereotype not in the set passed in at creation, as allowed stereotype for
		 * this <code>o</code>'s concrete type, depending on its (model) nature.
		 */
		@Override
		protected final void doValidate(UmlObject o, ModelIssues issues) {
			Set<String> undefineds = o.getStereotype()
					.getTokensOtherThan(getAllowedStereotypes().get(o.getNature()));
			if (undefineds.isEmpty()) {
				return;
			}
			String evidence = "offending stereotypes = " + undefineds.toString();
			issues.add(o, createIssue(o, evidence));
		}
	}

	abstract public static class UmlObjectsWithUnallowedTagNames<T extends UmlObject>
			extends AbstractRuleWithSubobjectsAndSkips<T> {
		private static final String HYPO_END = "with unallowed tag names";
		private static final String HOWTO = "remove tags";

		public UmlObjectsWithUnallowedTagNames(Logger logger, String what) {
			super(logger, Level.ERROR, Severity.high, Category.modellingRule, HYPO_END, HOWTO,
					what);
		}

		@Override
		protected void doValidate(UmlObject o, ModelIssues issues) {
			Set<String> unknowns = o.getUnallowedTagNames();
			if (!unknowns.isEmpty()) {
				String evidence = unknowns.toString();
				issues.add(o, createIssue(o, evidence));
			}
		}
	}

	abstract public static class UmlObjectsMissingDoc<T extends UmlObject>
			extends AbstractRuleWithSubobjectsAndSkips<T> {
		private static final String HYPO_END = "missing documentation";
		private static final String HOWTO = "add documentation";

		protected UmlObjectsMissingDoc(Logger logger, String what) {
			super(logger, Level.WARN, Severity.medium, Category.documentationRule, HYPO_END, HOWTO,
					what);
		}

		/**
		 * {@inheritDoc}
		 * <p>
		 * Matches normative items that miss description.
		 */
		@Override
		protected final void doValidate(UmlObject o, ModelIssues issues) {
			if (!o.isInformative() && o.getDescription().isEmpty()) {
				issues.add(o, createIssue(o));
			}
		}
	}

	abstract public static class UmlObjectsWithBadDocStart<T extends UmlObject>
			extends AbstractRuleWithSubobjectsAndSkips<T> {
		static final List<String> DOCSTART_CHARS = Arrays.asList("'", "\"", "(");

		private static final String HYPO_END = "whose documentation starts with unallowed character";
		private static final String HOWTO = "fix the first character: valid ones are any upper case"
				+ " letter or punctuation marks " + DOCSTART_CHARS.toString();

		protected UmlObjectsWithBadDocStart(Logger logger, String what) {
			super(logger, Level.WARN, Severity.low, Category.documentationRule, HYPO_END, HOWTO,
					what);
		}

		/**
		 * {@inheritDoc}
		 * <p>
		 * Matches objects with non-empty description starting with a non-upper case letter or
		 * another character not in the allowed list {@link #DOCSTART_CHARS}.
		 */
		@Override
		protected final void doValidate(UmlObject o, ModelIssues issues) {
			if (o.getDescription().isEmpty()) {
				return;
			}
			char first = o.getDescription().text.charAt(0);
			if (Character.isUpperCase(first) || DOCSTART_CHARS.contains(String.valueOf(first))) {
				return;
			}
			String evidence = "doc='" + Util.truncateEnd(o.getDescription().text) + "'";
			issues.add(o, createIssue(o, evidence));
		}
	}

	abstract public static class UmlObjectsWithBadDocEnd<T extends UmlObject>
			extends AbstractRuleWithSubobjectsAndSkips<T> {
		private static final String HYPO_END = "whose documentation does not end with a dot ('.')";
		private static final String HOWTO = "fix the last character: add a dot";

		protected UmlObjectsWithBadDocEnd(Logger logger, String what) {
			super(logger, Level.WARN, Severity.low, Category.documentationRule, HYPO_END, HOWTO,
					what);
		}

		/**
		 * {@inheritDoc}
		 * <p>
		 * Matches objects with non-empty description ending with a character different than '.'.
		 */
		@Override
		protected final void doValidate(UmlObject o, ModelIssues issues) {
			if (o.getDescription().isEmpty() || o.getDescription().text.endsWith(".")) {
				return;
			}
			String evidence = "doc='" + Util.truncateStart(o.getDescription().text) + "'";
			issues.add(o, createIssue(o, evidence));
		}
	}

	abstract public static class UmlObjectsWithBadCharacterInName<T extends UmlObject>
			extends AbstractRuleWithSubobjectsAndSkips<T> {

		private static final String HYPO_END = "whose name contains illegal character(s)";
		private static final String HOWTO = "rename by removing invalid character(s)";

		protected UmlObjectsWithBadCharacterInName(Logger logger, String what) {
			super(logger, Level.ERROR, Severity.high, Category.namingRule, HYPO_END, HOWTO, what);
		}

		/**
		 * {@inheritDoc}
		 * <p>
		 * Matches non-empty name that has one or more characters as provided by
		 * {@link #getInvalidCharacterFinder(UmlObject)}.
		 */
		@Override
		protected final void doValidate(UmlObject o, ModelIssues issues) {
			if (o.getName().isEmpty()) {
				return;
			}
			List<String> chars = getInvalidCharacterFinder(o).findInvalidCharacters(o.getName());
			if (chars.isEmpty()) {
				return;
			}
			String evidence = "invalid characters = " + chars.toString();
			issues.add(o, createIssue(o, evidence));
		}

		abstract protected InvalidCharactersFinder getInvalidCharacterFinder(UmlObject o);
	}
}
