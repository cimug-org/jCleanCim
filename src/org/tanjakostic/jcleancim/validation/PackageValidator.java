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
import java.util.Arrays;
import java.util.Collection;
import java.util.EnumSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.tanjakostic.jcleancim.common.Config;
import org.tanjakostic.jcleancim.common.Nature;
import org.tanjakostic.jcleancim.model.AbstractUmlObject;
import org.tanjakostic.jcleancim.model.UML;
import org.tanjakostic.jcleancim.model.UmlObject;
import org.tanjakostic.jcleancim.model.UmlPackage;
import org.tanjakostic.jcleancim.model.UmlSkipped;
import org.tanjakostic.jcleancim.model.UmlStereotype;
import org.tanjakostic.jcleancim.model.VersionInfo;
import org.tanjakostic.jcleancim.util.MapOfCollections;
import org.tanjakostic.jcleancim.validation.AbstractRule.UmlObjectsMissingDoc;
import org.tanjakostic.jcleancim.validation.AbstractRule.UmlObjectsWithBadCharacterInName;
import org.tanjakostic.jcleancim.validation.AbstractRule.UmlObjectsWithBadDocEnd;
import org.tanjakostic.jcleancim.validation.AbstractRule.UmlObjectsWithBadDocStart;
import org.tanjakostic.jcleancim.validation.AbstractRule.UmlObjectsWithUnallowedStereotype;
import org.tanjakostic.jcleancim.validation.AbstractRule.UmlObjectsWithUnallowedTagNames;

/**
 * Validates packages.
 *
 * @author tatjana.kostic@ieee.org
 * @version $Id: PackageValidator.java 27 2019-11-23 16:29:38Z dev978 $
 */
public class PackageValidator extends AbstractValidator<UmlPackage> {
	private static final Logger _logger = Logger.getLogger(PackageValidator.class.getName());

	private final List<UmlPackage> _scopedUmlObjects;

	public PackageValidator(Config cfg, Collection<UmlPackage> allPackages, ModelIssues issues) {
		super(cfg, allPackages.size(), "packages", issues);

		_scopedUmlObjects = AbstractUmlObject.collectForScope(allPackages,
				cfg.getValidationScope());

		addSimpleRule(new PackageUnexpectedElements());
		addSimpleRule(new PackageUnexpectedConnectors());
		addSimpleRule(new PackagesWithSelfDependency());
		addSimpleRule(new PackagesWithUnallowedStereotype());
		addSimpleRule(new PackagesTopLevelWithoutVersionClass());
		addSimpleRule(new Iec61850PackagesThatShouldHaveAliasAsTitle());
		addSimpleRule(new PackagesWithUnallowedTagNames());
		addSimpleRule(new PackagesMissingDoc());
		addSimpleRule(new PackagesWithBadDocStart());
		addSimpleRule(new PackagesWithBadDocEnd());
		addSimpleRule(new PackagesWithBadCharacterInName());

		addCrossRule(new PackagesWithSameName(allPackages));
	}

	@Override
	public boolean enabled() {
		return getCfg().isValidationPackagesOn();
	}

	@Override
	public List<UmlPackage> getScopedUmlObjects() {
		return _scopedUmlObjects;
	}

	// ==================== simple rules

	public static class PackageUnexpectedElements extends AbstractRule
			implements SimpleRule<UmlPackage> {
		// EA-specific names for elements that are expected, but not processed
		private static final List<String> SKIPPED_ELEMENTS = Arrays.asList(UmlSkipped.EA_NOTE,
				UmlSkipped.EA_TEXT, UmlSkipped.EA_BOUNDARY);
		private static final List<String> SKIPPED_61850_ELEMENTS = Arrays
				.asList(UmlSkipped.EA_STATE_MACHINE, UmlSkipped.EA_STATE, UmlSkipped.EA_STATE_NODE);

		private static final String HYPO = "packages with unexpected embedded elements (they are "
				+ "present in the model repository, but not kept in the in-memory model)";
		private static final String HOWTO = "remove unexpected embedded elements";

		public PackageUnexpectedElements() {
			super(PackageValidator._logger, Level.WARN, Severity.medium, Category.modellingRule,
					HYPO, HOWTO);
		}

		@Override
		public EnumSet<Nature> getApplicability() {
			return EnumSet.allOf(Nature.class);
		}

		private boolean isAcceptedElement(UmlSkipped skipped) {
			if (skipped.isConnector()) {
				return true; // pass thru for connector in this rule
			}
			if (UML.DetailedDiagrams.equals(skipped.getContainer().getName())) {
				return true;
			}
			return SKIPPED_ELEMENTS.contains(skipped.getKind().getValue())
					|| (skipped.getNature() != Nature.CIM
							&& SKIPPED_61850_ELEMENTS.contains(skipped.getKind().getValue()));
		}

		@Override
		public void validate(UmlPackage o, ModelIssues issues) {
			List<String> evidence = new ArrayList<String>();
			for (UmlSkipped item : o.getSkippedUmlItems()) {
				if (!isAcceptedElement(item)) {
					evidence.add(item.toShortString(true, true));
				}
			}
			if (!evidence.isEmpty()) {
				issues.add(o, createIssue(o, evidence.toString()));
			}
		}
	}

	public static class PackageUnexpectedConnectors extends AbstractRule
			implements SimpleRule<UmlPackage> {
		// EA-specific names for connectors that are expected, but not processed
		private static final List<String> SKIPPED_CONNECTORS = Arrays
				.asList(UmlSkipped.EA_NOTE_LINK);

		private static final String HYPO = "packages with unexpected embedded connectors (they are "
				+ "present in the model repository, but not kept in the in-memory model)";
		private static final String HOWTO = "remove unexpected embedded elements";

		public PackageUnexpectedConnectors() {
			super(PackageValidator._logger, Level.WARN, Severity.medium, Category.modellingRule,
					HYPO, HOWTO);
		}

		@Override
		public EnumSet<Nature> getApplicability() {
			return EnumSet.allOf(Nature.class);
		}

		private boolean isAcceptedConnector(UmlSkipped skipped) {
			if (!skipped.isConnector()) {
				return true; // pass thru for element in this rule
			}
			return SKIPPED_CONNECTORS.contains(skipped.getKind().getValue());
		}

		@Override
		public void validate(UmlPackage o, ModelIssues issues) {
			List<String> evidence = new ArrayList<String>();
			for (UmlSkipped item : o.getSkippedUmlItems()) {
				if (!isAcceptedConnector(item)) {
					evidence.add(item.toShortString(true, true));
				}
			}
			if (!evidence.isEmpty()) {
				issues.add(o, createIssue(o, evidence.toString()));
			}
		}
	}

	public static class PackagesWithSelfDependency extends AbstractRule
			implements SimpleRule<UmlPackage> {
		private static final String WHAT = "packages";
		private static final String HYPO = WHAT + " with self-dependency (UML tool allows to create"
				+ " such links, but they are not kept in the the in-memory model";
		private static final String HOWTO = "remove self-dependency";

		public PackagesWithSelfDependency() {
			super(PackageValidator._logger, Level.ERROR, Severity.high, Category.permissiveTool,
					HYPO, HOWTO);
		}

		@Override
		public EnumSet<Nature> getApplicability() {
			return EnumSet.allOf(Nature.class);
		}

		@Override
		public void validate(UmlPackage o, ModelIssues issues) {
			if (o.isSelfDependent()) {
				issues.add(o, createIssue(o));
			}
		}
	}

	public static class PackagesWithUnallowedStereotype
			extends UmlObjectsWithUnallowedStereotype<UmlPackage> {
		// doing this way to allow later on adding custom
		private static final Map<Nature, Set<String>> ALLOWED_STEREOS = new TreeMap<>(
				UmlStereotype.getPackageBuiltIns());

		public PackagesWithUnallowedStereotype() {
			super(PackageValidator._logger, "packages", ALLOWED_STEREOS);
		}

		@Override
		public EnumSet<Nature> getApplicability() {
			return EnumSet.allOf(Nature.class);
		}
	}

	public static class PackagesTopLevelWithoutVersionClass extends AbstractRule
			implements SimpleRule<UmlPackage> {
		private static final String HYPO = "top level packages without version class";
		private static final String HOWTO = "add Version class";

		public PackagesTopLevelWithoutVersionClass() {
			super(PackageValidator._logger, HYPO, HOWTO);
		}

		@Override
		public EnumSet<Nature> getApplicability() {
			return EnumSet.allOf(Nature.class);
		}

		@Override
		public void validate(UmlPackage o, ModelIssues issues) {
			if (o.isTop() && o.getVersionInfo() == null) {
				String evidence = "missing class '"
						+ VersionInfo.getExpectedVersionClassName(o.getNature(), o.getName()) + "'";
				issues.add(o, createIssue(o, evidence));
			}
		}
	}

	public static class Iec61850PackagesThatShouldHaveAliasAsTitle extends AbstractRule
			implements SimpleRule<UmlPackage> {
		private static final String HYPO = "IEC61850 packages used for generating parts 7-3 or 7-4 "
				+ "that are missing alias";
		private static final String HOWTO = "add UML alias that will be used for clause heading in"
				+ " auto-generated documents";

		public Iec61850PackagesThatShouldHaveAliasAsTitle() {
			super(PackageValidator._logger, HYPO, HOWTO);
		}

		@Override
		public EnumSet<Nature> getApplicability() {
			return EnumSet.of(Nature.IEC61850);
		}

		@Override
		public void validate(UmlPackage o, ModelIssues issues) {
			boolean isIec74or73 = false;
			if (o.getNature() == Nature.IEC61850) {
				Config cfg = o.getModel().getCfg();
				Collection<String> packageNames = new LinkedHashSet<String>(
						cfg.getValidationIec61850Packages73());
				packageNames.addAll(cfg.getValidationIec61850Packages74());
				for (String parentName : packageNames) {
					if (o.isInOrUnderPackage(parentName)) {
						isIec74or73 = true;
					}
				}
			}

			if (isIec74or73 && !o.isInformative() && o.getAlias().isEmpty()) {
				issues.add(o, createIssue(o));
			}
		}
	}

	public static class PackagesWithUnallowedTagNames
			extends UmlObjectsWithUnallowedTagNames<UmlPackage> {
		public PackagesWithUnallowedTagNames() {
			super(PackageValidator._logger, "packages");
		}

		@Override
		public EnumSet<Nature> getApplicability() {
			return EnumSet.allOf(Nature.class);
		}
	}

	public static class PackagesMissingDoc extends UmlObjectsMissingDoc<UmlPackage> {
		public PackagesMissingDoc() {
			super(PackageValidator._logger, "packages");
		}

		@Override
		public EnumSet<Nature> getApplicability() {
			return EnumSet.allOf(Nature.class);
		}
	}

	public static class PackagesWithBadDocStart extends UmlObjectsWithBadDocStart<UmlPackage> {
		public PackagesWithBadDocStart() {
			super(PackageValidator._logger, "packages");
		}

		@Override
		public EnumSet<Nature> getApplicability() {
			return EnumSet.allOf(Nature.class);
		}
	}

	public static class PackagesWithBadDocEnd extends UmlObjectsWithBadDocEnd<UmlPackage> {
		public PackagesWithBadDocEnd() {
			super(PackageValidator._logger, "packages");
		}

		@Override
		public EnumSet<Nature> getApplicability() {
			return EnumSet.allOf(Nature.class);
		}
	}

	public static class PackagesWithBadCharacterInName
			extends UmlObjectsWithBadCharacterInName<UmlPackage> {
		public PackagesWithBadCharacterInName() {
			super(PackageValidator._logger, "packages");
		}

		@Override
		public EnumSet<Nature> getApplicability() {
			return EnumSet.allOf(Nature.class);
		}

		@Override
		protected InvalidCharactersFinder getInvalidCharacterFinder(UmlObject o) {
			return o.getNature() == Nature.CIM ? InvalidCharactersFinder.STRICT
					: InvalidCharactersFinder.STRICT_UNDERSCORE_DASH;
		}
	}

	// ==================== cross rules

	public static class PackagesWithSameName extends AbstractRule implements CrossRule<UmlPackage> {
		private static final String HYPO = "packages that have non-unique name";
		private static final String HOWTO = "rename packages to have unique names within model";

		private final Collection<UmlPackage> _allPackages;

		public PackagesWithSameName(Collection<UmlPackage> allPackages) {
			super(PackageValidator._logger, HYPO, HOWTO);
			_allPackages = allPackages;
		}

		@Override
		public EnumSet<Nature> getApplicability() {
			return EnumSet.allOf(Nature.class);
		}

		@Override
		public Collection<UmlPackage> getObjsToTestAgainst() {
			return _allPackages;
		}

		/**
		 * {@inheritDoc}
		 * <p>
		 * Ignores packages with name {@link UML#DetailedDiagrams} - this is reserved name, repeated
		 * on purpose in the model.
		 */
		@Override
		public void validate(List<UmlPackage> packages, ModelIssues issues) {
			MapOfCollections<String, UmlPackage> allDuplicatesPerName = AbstractUmlObject
					.collectDuplicateNames(_allPackages);
			MapOfCollections<String, UmlPackage> scopedDuplicatesPerName = AbstractUmlObject
					.collectDuplicateNames(packages);

			int group = 1;
			for (String name : scopedDuplicatesPerName.keys()) {
				if (UML.DetailedDiagrams.equals(name)) {
					continue;
				}
				Collection<UmlPackage> allDups = allDuplicatesPerName.subCollection(name);
				String evidence = "all with the same name = "
						+ AbstractUmlObject.collectQNames(allDups, true).toString();

				Collection<UmlPackage> scopedDups = scopedDuplicatesPerName.subCollection(name);
				for (UmlPackage o : scopedDups) {
					ModelIssue issue = createIssue(o, evidence, o.toShortString(false, true),
							String.valueOf(group));
					issues.add(o, issue);
				}
				++group;
			}
		}
	}
}
