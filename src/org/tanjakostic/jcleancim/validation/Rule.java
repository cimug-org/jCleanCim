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

import java.util.EnumSet;

import org.apache.log4j.Level;
import org.tanjakostic.jcleancim.common.Nature;

/**
 * Interface to be implemented by all rules.
 *
 * @author tatjana.kostic@ieee.org
 * @version $Id: Rule.java 21 2019-08-12 15:44:50Z dev978 $
 */
interface Rule {

	/**
	 * Category of the rule; usefull to classify problems.
	 *
	 * @author tatjana.kostic@ieee.org
	 * @version $Id: Rule.java 21 2019-08-12 15:44:50Z dev978 $
	 */
	public enum Category {
		/** Remaining thing from a legacy UML tool that should be cleaned up. */
		legacyTool,

		/** Construct that the current UML tool allows, but should be invalid UML. */
		permissiveTool,

		/** Naming rule. */
		namingRule,

		/** Modelling rule. */
		modellingRule,

		/** Documentation rule. */
		documentationRule,

		/** Formatting rule. */
		formatting;
	}

	/**
	 * How severe the violation of this rule is; useful to prioritise problems.
	 *
	 * @author tatjana.kostic@ieee.org
	 * @version $Id: Rule.java 21 2019-08-12 15:44:50Z dev978 $
	 */
	public enum Severity {
		high, medium, low;
	}

	/** Returns rule category. */
	public Category getCategory();

	/** Returns severity if this rule is violated. */
	public Severity getSeverity();

	/** Returns what this rule is enforcing (and what was violated). */
	public String getHypothesis();

	/** Returns the suggestion on how to fix the problem. */
	public String getHowToFix();

	// -------------------------

	/**
	 * Logs the diagnosis.
	 *
	 * @param verbose
	 *            whether to log on console also validation steps with no errors.
	 * @param issues
	 *            "collecting" parameter, to be filled within the method.
	 */
	public void logDiagnosis(boolean verbose, ModelIssues issues);

	/** Returns log level to use (likely: related with {@link #getSeverity()}). */
	public Level getLogLevel();

	/** Returns all the natures of model elements for which the rule is applicable. */
	public EnumSet<Nature> getApplicability();
}
