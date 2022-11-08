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
import java.util.Collection;
import java.util.EnumSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.tanjakostic.jcleancim.common.Config;
import org.tanjakostic.jcleancim.common.Nature;
import org.tanjakostic.jcleancim.model.AbstractUmlObject;
import org.tanjakostic.jcleancim.model.UmlClass;
import org.tanjakostic.jcleancim.model.UmlObject;
import org.tanjakostic.jcleancim.model.UmlOperation;
import org.tanjakostic.jcleancim.model.UmlParameter;
import org.tanjakostic.jcleancim.model.UmlStereotype;
import org.tanjakostic.jcleancim.validation.AbstractRule.UmlObjectsMissingDoc;
import org.tanjakostic.jcleancim.validation.AbstractRule.UmlObjectsWithBadCharacterInName;
import org.tanjakostic.jcleancim.validation.AbstractRule.UmlObjectsWithBadDocEnd;
import org.tanjakostic.jcleancim.validation.AbstractRule.UmlObjectsWithBadDocStart;
import org.tanjakostic.jcleancim.validation.AbstractRule.UmlObjectsWithUnallowedStereotype;
import org.tanjakostic.jcleancim.validation.AbstractRule.UmlObjectsWithUnallowedTagNames;

/**
 * Validates operations.
 *
 * @author tatjana.kostic@ieee.org
 * @version $Id: OperationValidator.java 27 2019-11-23 16:29:38Z dev978 $
 */
public class OperationValidator extends AbstractValidator<UmlOperation> {
	private static final Logger _logger = Logger.getLogger(OperationValidator.class.getName());

	private final List<UmlOperation> _scopedUmlObjects;

	OperationValidator(Config cfg, Collection<UmlOperation> allOperations, ModelIssues issues) {
		super(cfg, allOperations.size(), "operations", issues);

		_scopedUmlObjects = AbstractUmlObject.collectForScope(allOperations,
				cfg.getValidationScope());

		addSimpleRule(new OperationsWithUpperCaseName());
		addSimpleRule(new OperationsWithUnallowedStereotype());
		addSimpleRule(new OperationParametersWithUnallowedStereotype());
		addSimpleRule(new OperationsWithInvalidReturnTypeNull());
		addSimpleRule(new OperationsWithInvalidArgTypeNull());
		addSimpleRule(new OperationsWithInvalidExcTypeNull());
		addSimpleRule(new OperationsWithUnallowedTagNames());
		addSimpleRule(new OperationParametersWithUnallowedTagNames());
		addSimpleRule(new OperationsMissingDoc());
		addSimpleRule(new OperationParametersMissingDoc());
		addSimpleRule(new OperationsWithBadDocStart());
		addSimpleRule(new OperationParametersWithBadDocStart());
		addSimpleRule(new OperationsWithBadDocEnd());
		addSimpleRule(new OperationParametersWithBadDocEnd());
		addSimpleRule(new OperationsWithBadCharacterInName());
		addSimpleRule(new OperationParametersWithBadCharacterInName());
	}

	@Override
	public boolean enabled() {
		return getCfg().isValidationOperationsOn();
	}

	@Override
	public List<UmlOperation> getScopedUmlObjects() {
		return _scopedUmlObjects;
	}

	private static List<UmlObject> collectParametersAsUmlObjects(UmlOperation op) {
		List<UmlObject> result = new ArrayList<>();
		for (UmlObject obj : op.getParameters()) {
			result.add(obj);
		}
		return result;
	}

	// ==================== simple rules

	public static class OperationsWithUpperCaseName extends AbstractRule
			implements SimpleRule<UmlOperation> {
		private static final String HYPO = "operations that should have name starting with lower case";
		private static final String HOWTO = "operations that should have name starting with lower case";

		public OperationsWithUpperCaseName() {
			super(OperationValidator._logger, Level.WARN, Severity.medium, Category.namingRule,
					HYPO, HOWTO);
		}

		@Override
		public EnumSet<Nature> getApplicability() {
			return EnumSet.allOf(Nature.class);
		}

		@Override
		public void validate(UmlOperation o, ModelIssues issues) {
			if (Character.isUpperCase(o.getName().charAt(0))) {
				ModelIssue issue = createIssue(o);
				issues.add(o, issue);
			}
		}
	}

	public static class OperationsWithUnallowedStereotype
			extends UmlObjectsWithUnallowedStereotype<UmlOperation> {
		// doing this way to allow later on adding custom
		private static final Map<Nature, Set<String>> ALLOWED_STEREOS = new TreeMap<>(
				UmlStereotype.getOperationBuiltIns());

		public OperationsWithUnallowedStereotype() {
			super(OperationValidator._logger, "operations", ALLOWED_STEREOS);
		}

		@Override
		public EnumSet<Nature> getApplicability() {
			return EnumSet.allOf(Nature.class);
		}
	}

	public static class OperationParametersWithUnallowedStereotype
			extends UmlObjectsWithUnallowedStereotype<UmlOperation> {
		// doing this way to allow later on adding custom
		private static final Map<Nature, Set<String>> ALLOWED_STEREOS = new TreeMap<>(
				UmlStereotype.getOperationParameterBuiltIns());

		public OperationParametersWithUnallowedStereotype() {
			super(OperationValidator._logger, "operation parameters", ALLOWED_STEREOS);
		}

		@Override
		public EnumSet<Nature> getApplicability() {
			return EnumSet.allOf(Nature.class);
		}

		@Override
		protected List<UmlObject> getSubObjects(UmlOperation op) {
			return collectParametersAsUmlObjects(op);
		}
	}

	public static class OperationsWithInvalidReturnTypeNull extends AbstractRule
			implements SimpleRule<UmlOperation> {
		private static final String HYPO = "operations that have as return type the class not "
				+ "present in the model";
		private static final String HOWTO = "locate class in the model from the combo-box on "
				+ "operation return type";

		public OperationsWithInvalidReturnTypeNull() {
			super(OperationValidator._logger, HYPO, HOWTO);
		}

		@Override
		public EnumSet<Nature> getApplicability() {
			return EnumSet.allOf(Nature.class);
		}

		@Override
		public void validate(UmlOperation o, ModelIssues issues) {
			if (!o.isVoidReturned() && o.getReturnType().isNullClass()) {
				String evidence = "return type is inexisting class '" + o.getEaReturnTypeInfo()
						+ "'";
				issues.add(o, createIssue(o, evidence));
			}
		}
	}

	public static class OperationsWithInvalidArgTypeNull extends AbstractRule
			implements SimpleRule<UmlOperation> {
		private static final String HYPO = "operations that have parameters that use for their type "
				+ "classes not present in the model";
		private static final String HOWTO = "locate class(es) in the model from the combo-box on "
				+ "operation parameter";

		public OperationsWithInvalidArgTypeNull() {
			super(OperationValidator._logger, HYPO, HOWTO);
		}

		@Override
		public EnumSet<Nature> getApplicability() {
			return EnumSet.allOf(Nature.class);
		}

		@Override
		public void validate(UmlOperation o, ModelIssues issues) {
			List<String> ghostTypes = new ArrayList<>();
			for (UmlParameter par : o.getParameters()) {
				if (par.getType().isNullClass()) {
					ghostTypes.add(par.getEaTypeInfo());
				}
			}
			if (ghostTypes.isEmpty()) {
				return;
			}
			String evidence = "inexisting class(es) = '" + ghostTypes.toString();
			ModelIssue issue = createIssue(o, evidence, o.toString(), null);
			issues.add(o, issue);
		}
	}

	public static class OperationsWithInvalidExcTypeNull extends AbstractRule
			implements SimpleRule<UmlOperation> {
		private static final String HYPO = "operations that have exceptions not present in the model";
		private static final String HOWTO = "ensure that values in throws tag correspond to "
				+ "classes present in the model";

		public OperationsWithInvalidExcTypeNull() {
			super(OperationValidator._logger, HYPO, HOWTO);
		}

		@Override
		public EnumSet<Nature> getApplicability() {
			return EnumSet.allOf(Nature.class);
		}

		@Override
		public void validate(UmlOperation o, ModelIssues issues) {
			List<String> ghostTypes = new ArrayList<>();
			for (int i = 0; i < o.getExceptions().size(); ++i) {
				UmlClass exc = o.getExceptions().get(i);
				if (exc.isNullClass()) {
					ghostTypes.add(o.getEaExceptionTypeInfo(i));
				}
			}
			if (ghostTypes.isEmpty()) {
				return;
			}
			String evidence = "inexisting class(es) = '" + ghostTypes.toString();
			ModelIssue issue = createIssue(o, evidence, o.toString(), null);
			issues.add(o, issue);
		}
	}

	public static class OperationsWithUnallowedTagNames
			extends UmlObjectsWithUnallowedTagNames<UmlOperation> {
		public OperationsWithUnallowedTagNames() {
			super(OperationValidator._logger, "operations");
		}

		@Override
		public EnumSet<Nature> getApplicability() {
			return EnumSet.allOf(Nature.class);
		}
	}

	public static class OperationParametersWithUnallowedTagNames
			extends UmlObjectsWithUnallowedTagNames<UmlOperation> {
		public OperationParametersWithUnallowedTagNames() {
			super(OperationValidator._logger, "operation parameters");
		}

		@Override
		public EnumSet<Nature> getApplicability() {
			return EnumSet.allOf(Nature.class);
		}

		@Override
		protected List<UmlObject> getSubObjects(UmlOperation op) {
			return collectParametersAsUmlObjects(op);
		}
	}

	public static class OperationsMissingDoc extends UmlObjectsMissingDoc<UmlOperation> {
		public OperationsMissingDoc() {
			super(OperationValidator._logger, "operations");
		}

		@Override
		public EnumSet<Nature> getApplicability() {
			return EnumSet.allOf(Nature.class);
		}
	}

	public static class OperationParametersMissingDoc extends UmlObjectsMissingDoc<UmlOperation> {
		public OperationParametersMissingDoc() {
			super(OperationValidator._logger, "operation parameters");
		}

		@Override
		public EnumSet<Nature> getApplicability() {
			return EnumSet.allOf(Nature.class);
		}

		@Override
		protected List<UmlObject> getSubObjects(UmlOperation op) {
			return collectParametersAsUmlObjects(op);
		}
	}

	public static class OperationsWithBadDocStart extends UmlObjectsWithBadDocStart<UmlOperation> {
		public OperationsWithBadDocStart() {
			super(OperationValidator._logger, "operations");
		}

		@Override
		public EnumSet<Nature> getApplicability() {
			return EnumSet.allOf(Nature.class);
		}
	}

	public static class OperationParametersWithBadDocStart
			extends UmlObjectsWithBadDocStart<UmlOperation> {
		public OperationParametersWithBadDocStart() {
			super(OperationValidator._logger, "operation parameters");
		}

		@Override
		public EnumSet<Nature> getApplicability() {
			return EnumSet.allOf(Nature.class);
		}

		@Override
		protected List<UmlObject> getSubObjects(UmlOperation op) {
			return collectParametersAsUmlObjects(op);
		}
	}

	public static class OperationsWithBadDocEnd extends UmlObjectsWithBadDocEnd<UmlOperation> {
		public OperationsWithBadDocEnd() {
			super(OperationValidator._logger, "operations");
		}

		@Override
		public EnumSet<Nature> getApplicability() {
			return EnumSet.allOf(Nature.class);
		}
	}

	public static class OperationParametersWithBadDocEnd
			extends UmlObjectsWithBadDocEnd<UmlOperation> {
		public OperationParametersWithBadDocEnd() {
			super(OperationValidator._logger, "operation parameters");
		}

		@Override
		public EnumSet<Nature> getApplicability() {
			return EnumSet.allOf(Nature.class);
		}

		@Override
		protected List<UmlObject> getSubObjects(UmlOperation op) {
			return collectParametersAsUmlObjects(op);
		}
	}

	public static class OperationsWithBadCharacterInName
			extends UmlObjectsWithBadCharacterInName<UmlOperation> {
		public OperationsWithBadCharacterInName() {
			super(OperationValidator._logger, "operations");
		}

		@Override
		public EnumSet<Nature> getApplicability() {
			return EnumSet.allOf(Nature.class);
		}

		@Override
		protected InvalidCharactersFinder getInvalidCharacterFinder(UmlObject o) {
			return InvalidCharactersFinder.STRICT;
			// return o.getNature() == Nature.CIM ? InvalidCharactersFinder.STRICT
			// : InvalidCharactersFinder.STRICT_UNDERSCORE_DASH;
		}
	}

	public static class OperationParametersWithBadCharacterInName
			extends UmlObjectsWithBadCharacterInName<UmlOperation> {
		public OperationParametersWithBadCharacterInName() {
			super(OperationValidator._logger, "operation parameters");
		}

		@Override
		public EnumSet<Nature> getApplicability() {
			return EnumSet.allOf(Nature.class);
		}

		@Override
		protected List<UmlObject> getSubObjects(UmlOperation op) {
			return collectParametersAsUmlObjects(op);
		}

		@Override
		protected InvalidCharactersFinder getInvalidCharacterFinder(UmlObject o) {
			return InvalidCharactersFinder.STRICT;
			// return o.getNature() == Nature.CIM ? InvalidCharactersFinder.STRICT
			// : InvalidCharactersFinder.STRICT_UNDERSCORE_DASH;
		}
	}

}
