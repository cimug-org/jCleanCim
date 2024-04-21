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

import java.util.Collection;
import java.util.EnumSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import org.apache.log4j.Logger;
import org.tanjakostic.jcleancim.common.Config;
import org.tanjakostic.jcleancim.common.Nature;
import org.tanjakostic.jcleancim.common.OwningWg;
import org.tanjakostic.jcleancim.model.AbstractUmlObject;
import org.tanjakostic.jcleancim.model.UmlDependency;
import org.tanjakostic.jcleancim.model.UmlStereotype;
import org.tanjakostic.jcleancim.validation.AbstractRule.UmlObjectsWithUnallowedStereotype;
import org.tanjakostic.jcleancim.validation.AbstractRule.UmlObjectsWithUnallowedTagNames;

/**
 * Validates (hand-drawn) dependencies.
 *
 * @author tatjana.kostic@ieee.org
 * @version $Id: DependencyValidator.java 27 2019-11-23 16:29:38Z dev978 $
 */
public class DependencyValidator extends AbstractValidator<UmlDependency> {
	private static final Logger _logger = Logger.getLogger(DependencyValidator.class.getName());

	private final List<UmlDependency> _scopedUmlObjects;

	DependencyValidator(Config cfg, Collection<UmlDependency> allDependencies, ModelIssues issues) {
		super(cfg, allDependencies.size(), "dependencies", issues);

		_scopedUmlObjects = AbstractUmlObject.collectForScope(allDependencies,
				cfg.getValidationScope());

		addSimpleRule(new DependenciesWithUnallowedStereotype());
		addSimpleRule(new DependenciesWithUnallowedDirection());
		addSimpleRule(new DependenciesWithUnallowedTagNames());
	}

	@Override
	public boolean enabled() {
		return getCfg().isValidationDependenciesOn();
	}

	@Override
	public List<UmlDependency> getScopedUmlObjects() {
		return _scopedUmlObjects;
	}

	// ==================== simple rules

	public static class DependenciesWithUnallowedStereotype
			extends UmlObjectsWithUnallowedStereotype<UmlDependency> {
		// doing this way to allow later on adding custom
		private static final Map<Nature, Set<String>> ALLOWED_STEREOS = new TreeMap<>(
				UmlStereotype.getDependencyBuiltIns());

		public DependenciesWithUnallowedStereotype() {
			super(DependencyValidator._logger, "dependencies", ALLOWED_STEREOS);
		}

		@Override
		public EnumSet<Nature> getApplicability() {
			return EnumSet.allOf(Nature.class);
		}
	}

	public static class DependenciesWithUnallowedDirection extends AbstractRule
			implements SimpleRule<UmlDependency> {
		private static final String HYPO = "dependencies on unallowed package"
				+ " according to standard IEC TC57 rules";
		private static final String HOWTO = "remove dependency or reverse its direction";

		public DependenciesWithUnallowedDirection() {
			super(DependencyValidator._logger, HYPO, HOWTO);
		}

		@Override
		public EnumSet<Nature> getApplicability() {
			return EnumSet.allOf(Nature.class);
		}

		@Override
		public void validate(UmlDependency d, ModelIssues issues) {
			OwningWg sourceOwner = d.getSource().getOwner();
			OwningWg targetOwner = d.getTarget().getOwner();
			EnumSet<OwningWg> allowedOtherOwners = sourceOwner.getAllowedOtherEndOwners();
			if (!allowedOtherOwners.contains(targetOwner)) {
				issues.add(d, createIssue(d));
			}
		}
	}

	public static class DependenciesWithUnallowedTagNames
			extends UmlObjectsWithUnallowedTagNames<UmlDependency> {
		public DependenciesWithUnallowedTagNames() {
			super(DependencyValidator._logger, "dependencies");
		}

		@Override
		public EnumSet<Nature> getApplicability() {
			return EnumSet.allOf(Nature.class);
		}
	}
}
