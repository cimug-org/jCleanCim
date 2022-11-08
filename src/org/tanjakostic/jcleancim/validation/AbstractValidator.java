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

import org.apache.log4j.Logger;
import org.tanjakostic.jcleancim.common.Config;
import org.tanjakostic.jcleancim.common.Nature;
import org.tanjakostic.jcleancim.model.UmlObject;
import org.tanjakostic.jcleancim.util.Util;

/**
 * Common implementation for all UML object validators (package, class, etc.). An element validator
 * instantiates concrete {@link Rule}-s.
 * <p>
 * This class controls the execution of validation with respect to:
 * <ul>
 * <li>enabled/disabled validators - this status is specified with separate validation properties in
 * the {@value org.tanjakostic.jcleancim.common.Config#DEFAULT_PROPS_FILE_NAME} file and available
 * through the 7 configuration instance methods on configuration (e.g.
 * {@link Config#isValidationAssociationsOn()} ), one per validator type. Setting one of these to
 * false at configuration disables validation for all the rules for that type of element (for this
 * example, all rules validating associations).</li>
 * <li>enabled/disabled status of individual rules - there is one configuration option whose value
 * gets returned from configuration with {@link Config#getValidationRulesOff()}. It contains
 * fine-grained filtering for individual rules: those specified in the configuration are
 * skipped.</li>
 * </ul>
 * <p>
 * Violated rules on UML objects from the model produce issues, and they can be logged and reported.
 *
 * @param <T>
 * @author tatjana.kostic@ieee.org
 * @version $Id: AbstractValidator.java 34 2019-12-20 18:37:17Z dev978 $
 */
abstract public class AbstractValidator<T extends UmlObject> {
	private static final Logger _logger = Logger.getLogger(AbstractValidator.class.getName());

	private final Config _cfg;
	private final int _totalCount;
	private final String _which;
	private final ModelIssues _issues;

	private final List<SimpleRule<T>> _allSimpleRules = new ArrayList<>();
	private final List<CrossRule<T>> _allCrossRules = new ArrayList<>();

	private final List<SimpleRule<T>> _checkedSimpleRules = new ArrayList<>();
	private final List<CrossRule<T>> _checkedCrossRules = new ArrayList<>();

	/**
	 * Constructor.
	 *
	 * @param cfg
	 *            configuration
	 * @param totalCount
	 *            total count of elements in the model
	 * @param which
	 *            kind of element - used only for logging
	 * @param issues
	 *            home for issues that get collected through validation
	 */
	protected AbstractValidator(Config cfg, int totalCount, String which, ModelIssues issues) {
		_cfg = cfg;
		_totalCount = totalCount;
		_which = which;
		_issues = issues;
	}

	protected final boolean addSimpleRule(SimpleRule<T> simpleRule) {
		boolean result = false;
		if (checkRule(simpleRule.getClass())) {
			result = _checkedSimpleRules.add(simpleRule);
		}
		_allSimpleRules.add(simpleRule);
		return result;
	}

	protected final boolean addCrossRule(CrossRule<T> crossRule) {
		boolean result = false;
		if (checkRule(crossRule.getClass())) {
			result = _checkedCrossRules.add(crossRule);
		}
		_allCrossRules.add(crossRule);
		return result;
	}

	private boolean checkRule(Class<?> ruleClass) {
		return !getCfg().getValidationRulesOff().contains(ruleClass.getSimpleName());
	}

	/** Returns configuration. */
	public final Config getCfg() {
		return _cfg;
	}

	public ModelIssues getCollectedIssues() {
		return _issues;
	}

	/**
	 * If validation has been enabled in the configuration for the type T of element, performs
	 * validation according to (in the configuration) non-disabled individual rules, and logs
	 * diagnosis.
	 */
	public final void validate() {
		if (!enabled()) {
			return;
		}

		logTitle();
		for (T o : getScopedUmlObjects()) {
			for (SimpleRule<T> r : getCheckedSimpleRules()) {
				r.validate(o, _issues);
			}
		}
		for (CrossRule<T> r : getCheckedCrossRules()) {
			r.validate(getScopedUmlObjects(), _issues);
		}
		logDiagnosis();
	}

	/** Returns whether the validation for this validator has been enabled (by configuration). */
	abstract public boolean enabled();

	private void logTitle() {
		_logger.info("");
		_logger.info("====== Validating " + getScopedUmlObjects().size() + " (of " + _totalCount
				+ ") " + _which + ":");
	}

	/** Returns the elements retained for validation, for the configured scope. */
	abstract public List<T> getScopedUmlObjects();

	private void logDiagnosis() {
		for (Rule r : getCheckedRules()) {
			r.logDiagnosis(getCfg().isValidationLoggingVerbose(), getCollectedIssues());
		}
	}

	public final List<Rule> getCheckedRules() {
		ArrayList<Rule> result = new ArrayList<Rule>();
		result.addAll(getCheckedSimpleRules());
		result.addAll(getCheckedCrossRules());
		return result;
	}

	public final List<Rule> getAllRules() {
		ArrayList<Rule> result = new ArrayList<Rule>();
		result.addAll(getAllSimpleRules());
		result.addAll(getAllCrossRules());
		return result;
	}

	/** Returns only checked (non-disabled) simple rules. */
	public final List<SimpleRule<T>> getCheckedSimpleRules() {
		return Collections.unmodifiableList(_checkedSimpleRules);
	}

	/** Returns only checked (non-disabled) cross rules. */
	public final List<CrossRule<T>> getCheckedCrossRules() {
		return Collections.unmodifiableList(_checkedCrossRules);
	}

	/** Returns all simple rules available. */
	public final List<SimpleRule<T>> getAllSimpleRules() {
		return Collections.unmodifiableList(_allSimpleRules);
	}

	/** Returns all cross rules available. */
	public final List<CrossRule<T>> getAllCrossRules() {
		return Collections.unmodifiableList(_allCrossRules);
	}

	/** Returns the list of strings, including heading, suitable for logging. */
	public String displayAllAvailableRuleNames() {
		String title = "Available rules in " + getClass().getSimpleName() + " = ";
		List<String> result = new ArrayList<String>();
		for (Rule rule : getAllRules()) {
			result.add(rule.getClass().getSimpleName());
		}
		return title + result.toString();
	}

	/**
	 * Returns flattened list of strings, including heading, with new line character as separator;
	 * suitable for pasting into a document (e.g., CIM model management or 61850 UML model
	 * management document).
	 * <p>
	 * If <code>nature</code> is null, then vistis simply all the rules, without concern about their
	 * applicability per nature.
	 */
	public String displayAvailableRuleNames(Nature nature) {
		StringBuilder sb = new StringBuilder();
		sb.append("  Available rules ");
		if (nature != null) {
			sb.append("for ").append(nature);
		}
		sb.append(" in ").append(getClass().getSimpleName());
		sb.append(" (category, severity):").append(Util.NL);
		for (Rule rule : getAllRules()) {
			String ruleName = rule.getClass().getSimpleName();
			if (nature == null || rule.getApplicability().contains(nature)) {
				sb.append("    ").append(ruleName);
				sb.append(" (").append(rule.getCategory());
				sb.append(", ").append(rule.getSeverity());
				sb.append(")").append(Util.NL);
			}
		}
		return sb.toString();
	}
}
