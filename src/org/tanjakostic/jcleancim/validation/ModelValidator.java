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

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.tanjakostic.jcleancim.common.Config;
import org.tanjakostic.jcleancim.common.Nature;
import org.tanjakostic.jcleancim.model.UmlModel;
import org.tanjakostic.jcleancim.util.ApplicationException;
import org.tanjakostic.jcleancim.util.Util;

/**
 * Creates validators per kind of UML element and allows to run the validation.
 *
 * @author tatjana.kostic@ieee.org
 * @version $Id: ModelValidator.java 21 2019-08-12 15:44:50Z dev978 $
 */
public class ModelValidator {
	private static final Logger _logger = Logger.getLogger(ModelValidator.class.getName());

	public static final String PROBLEMS_REPORT_PREFIX = "problemsReport-";

	private final List<AbstractValidator<?>> _validators;

	private final File _reportFile;
	private final ModelIssues _issues = new ModelIssues();

	public ModelValidator(UmlModel model) {
		Config cfg = model.getCfg();
		_validators = new ArrayList<AbstractValidator<?>>();
		_validators.add(new PackageValidator(cfg, model.getPackages(), _issues));
		_validators.add(new ClassValidator(cfg, model.getClasses(), _issues));
		_validators.add(new AttributeValidator(cfg, model.getAttributes(), _issues));
		_validators.add(new OperationValidator(cfg, model.getOperations(), _issues));
		_validators.add(new AssociationValidator(cfg, model.getAssociations(), _issues));
		_validators.add(new DependencyValidator(cfg, model.getDependencies(), _issues));
		_validators.add(new DiagramValidator(cfg, model.getDiagrams(), _issues));

		File reportFile = null;
		try {
			reportFile = deduceReportFilepath(cfg);
		} catch (ApplicationException e) {
			_logger.warn("Will not be able to save validation report as .csv: " + e.getMessage());
		}
		_reportFile = reportFile;
	}

	public void logAllAvailableRuleNames(Level level) {
		_logger.log(level, "Model validation - below are all available rules:");
		for (AbstractValidator<?> v : _validators) {
			_logger.log(level, v.displayAllAvailableRuleNames());
		}
		_logger.log(level, "");
	}

	public void logAvailableRuleNamesWithCategoryAndSeverity(Level level) {
		StringBuilder sb = new StringBuilder();
		sb.append("Available rules per nature:").append(Util.NL);
		for (Nature nature : Nature.values()) {
			sb.append("  for ").append(nature.toString()).append(":").append(Util.NL);
			for (AbstractValidator<?> v : _validators) {
				sb.append(v.displayAvailableRuleNames(nature));
			}
		}
		sb.append(Util.NL);
		sb.append("Available rules - all:").append(Util.NL);
		for (AbstractValidator<?> v : _validators) {
			sb.append(v.displayAvailableRuleNames(null));
		}
		_logger.log(level, sb.toString());
		_logger.log(level, "");
	}

	/** Performs validation. */
	public void validate() {
		for (AbstractValidator<?> v : _validators) {
			v.validate();
		}
	}

	public void saveReport() {
		if (!_issues.getIssues().isEmpty() && _reportFile != null) {
			try {
				_logger.info("");
				_logger.info("Saving report to file: " + _reportFile.getAbsolutePath());
				Util.saveToFile(_reportFile.getAbsolutePath(), _issues.asCSV());
			} catch (IOException e) {
				_logger.warn("Failed to save validation report to file: " + e.getMessage());
				_logger.warn("Saving to detailed log file.");
				_logger.debug(_issues.asCSV());
			}
		}
	}

	File deduceReportFilepath(Config cfg) throws ApplicationException {
		String modelFileAbsPath = cfg.getModelFileAbsPath();
		if (modelFileAbsPath == null) {
			_logger.warn("Don't know how to report without .eap model.");
			return null;
		}
		String eaModelFileName = new File(modelFileAbsPath).getName();
		if (!eaModelFileName.endsWith(".eap")) {
			_logger.warn("Expecting an .eap model file, and don't know how to report from "
					+ eaModelFileName + ".");
			return null;
		}

		// remove the extension ".eap"
		String outFileName = eaModelFileName.substring(0, eaModelFileName.length() - 4);
		return Util.getOutputFileRenameIfExists(Config.OUTPUT_DIR_NAME,
				ModelValidator.PROBLEMS_REPORT_PREFIX + outFileName + ".csv");
	}
}
