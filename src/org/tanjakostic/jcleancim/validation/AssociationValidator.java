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
import org.tanjakostic.jcleancim.common.OwningWg;
import org.tanjakostic.jcleancim.model.AbstractUmlObject;
import org.tanjakostic.jcleancim.model.UmlAssociation;
import org.tanjakostic.jcleancim.model.UmlAssociation.Direction;
import org.tanjakostic.jcleancim.model.UmlAssociationEnd;
import org.tanjakostic.jcleancim.model.UmlObject;
import org.tanjakostic.jcleancim.model.UmlPackage;
import org.tanjakostic.jcleancim.model.UmlStereotype;
import org.tanjakostic.jcleancim.model.UmlVisibility;
import org.tanjakostic.jcleancim.util.Util;
import org.tanjakostic.jcleancim.validation.AbstractRule.UmlObjectsMissingDoc;
import org.tanjakostic.jcleancim.validation.AbstractRule.UmlObjectsWithBadCharacterInName;
import org.tanjakostic.jcleancim.validation.AbstractRule.UmlObjectsWithBadDocEnd;
import org.tanjakostic.jcleancim.validation.AbstractRule.UmlObjectsWithBadDocStart;
import org.tanjakostic.jcleancim.validation.AbstractRule.UmlObjectsWithUnallowedStereotype;
import org.tanjakostic.jcleancim.validation.AbstractRule.UmlObjectsWithUnallowedTagNames;

/**
 * Validates associations.
 *
 * @author tatjana.kostic@ieee.org
 * @version $Id: AssociationValidator.java 27 2019-11-23 16:29:38Z dev978 $
 */
public class AssociationValidator extends AbstractValidator<UmlAssociation> {
	private static final Logger _logger = Logger.getLogger(AssociationValidator.class.getName());

	private final List<UmlAssociation> _scopedUmlObjects;

	AssociationValidator(Config cfg, Collection<UmlAssociation> allAssociations,
			ModelIssues issues) {
		super(cfg, allAssociations.size(), "associations", issues);

		_scopedUmlObjects = AbstractUmlObject.collectForScope(allAssociations,
				cfg.getValidationScope());

		addSimpleRule(new AssociationsWithExplicitDirection());
		addSimpleRule(new AssociationsWithRoleBadDirection());
		addSimpleRule(new AssociationsWithDoc());
		addSimpleRule(new AssociationsWithSameDocOnBothEnds());
		addSimpleRule(new AssociationsWithName());
		addSimpleRule(new AssociationsWithUnallowedStereotype());
		addSimpleRule(new AssociationEndsWithUnallowedStereotype());
		addSimpleRule(new AssociationsMissingInformativeStereotype());
		addSimpleRule(new AssociationsWithUnallowedTagNames());
		addSimpleRule(new AssociationEndsWithUnallowedTagNames());
		addSimpleRule(new AssociationsWithNoMultiplicity());
		addSimpleRule(new AssociationsWithWrongSource());
		addSimpleRule(new Iec61850AssociationsThatShouldBePrivate());
		addSimpleRule(new Iec61850AssociationsWithDifferentEndVisibility());
		addSimpleRule(new AssociationEndsMissingDoc());
		addSimpleRule(new AssociationEndsWithBadDocStart());
		addSimpleRule(new AssociationEndsWithBadDocEnd());
		addSimpleRule(new AssociationEndsWithBadCharacterInName());
		addSimpleRule(new CimAssociationEndsNameStartingWithLowerCase());
		addSimpleRule(new CimAssociationEndsNameShouldBePlural());
		addSimpleRule(new CimAssociationEndsNameShouldBeSingular());
	}

	@Override
	public boolean enabled() {
		return getCfg().isValidationAssociationsOn();
	}

	@Override
	public List<UmlAssociation> getScopedUmlObjects() {
		return _scopedUmlObjects;
	}

	private static boolean includeSubObjectDocRelated(UmlAssociationEnd ae) {
		if (ae.getVisibility() != UmlVisibility.PUBLIC) {
			return false;
		}
		// we always check everything for CIM; and for IEC61850, only if it's named:
		return ae.getNature() == Nature.CIM || Util.hasContent(ae.getName());
	}

	private static final List<UmlObject> getSubObjectsDocRelated(UmlAssociation assoc) {
		List<UmlObject> result = new ArrayList<>();
		UmlAssociationEnd sourceEnd = assoc.getSourceEnd();
		if (includeSubObjectDocRelated(sourceEnd)) {
			result.add(sourceEnd);
		}
		UmlAssociationEnd targetEnd = assoc.getTargetEnd();
		if (includeSubObjectDocRelated(targetEnd)) {
			result.add(targetEnd);
		}
		return result;
	}

	private static List<UmlObject> collectEndsAsUmlObjects(UmlAssociation o) {
		List<UmlObject> result = new ArrayList<>();
		result.add(o.getSourceEnd());
		result.add(o.getTargetEnd());
		return result;
	}

	// ==================== simple rules

	public static class AssociationsWithExplicitDirection extends AbstractRule
			implements SimpleRule<UmlAssociation> {
		private static final String HYPO = "associations that should have direction '"
				+ Direction.unspecified + "'";
		private static final String HOWTO = "change associations direction to '"
				+ Direction.unspecified + "'";

		public AssociationsWithExplicitDirection() {
			super(AssociationValidator._logger, HYPO, HOWTO);
		}

		@Override
		public EnumSet<Nature> getApplicability() {
			return EnumSet.allOf(Nature.class);
		}

		@Override
		public void validate(UmlAssociation o, ModelIssues issues) {
			if (o.getNature() == Nature.CIM && o.getNavigability() == Direction.unspecified) {
				return;
			}
			boolean b = o.getNavigability() != Direction.unspecified && o.isAtLeastOneEndPublic();
			if (o.getNature() == Nature.IEC61850 && o.getNavigability() != Direction.biDirectional
					&& !b) {
				return;
			}

			String evidence = "direction = '" + o.getNavigability() + "'";
			ModelIssue issue = createIssue(o, evidence, o.toString(), null);
			issues.add(o, issue);
		}
	}

	public static class AssociationsWithRoleBadDirection extends AbstractRule
			implements SimpleRule<UmlAssociation> {
		private static final String HYPO = "association ends that should have direction 'Unspecified'";
		private static final String HOWTO = "change association end direction to 'Unspecified'";

		public AssociationsWithRoleBadDirection() {
			super(AssociationValidator._logger, HYPO, HOWTO);
		}

		@Override
		public EnumSet<Nature> getApplicability() {
			return EnumSet.allOf(Nature.class);
		}

		@Override
		public void validate(UmlAssociation o, ModelIssues issues) {
			if (o.isDirectionMismatchForEnds()) {
				String evidence = "srcNavig='" + o.getSourceEnd().getNavigable()
						+ "', targetNavig='" + o.getTargetEnd().getNavigable() + "'";
				ModelIssue issue = createIssue(o, evidence, o.toString(), null);
				issues.add(o, issue);
			}
		}
	}

	public static class AssociationsWithDoc extends AbstractRule
			implements SimpleRule<UmlAssociation> {
		private static final String HYPO = "associations that should have empty doc (we document "
				+ "association ends, not association itself)";
		private static final String HOWTO = "delete doc of association";

		public AssociationsWithDoc() {
			super(AssociationValidator._logger, Level.WARN, Severity.low,
					Category.documentationRule, HYPO, HOWTO);
		}

		@Override
		public EnumSet<Nature> getApplicability() {
			return EnumSet.allOf(Nature.class);
		}

		@Override
		public void validate(UmlAssociation o, ModelIssues issues) {
			if (!o.getDescription().isEmpty()) {
				String evidence = Util.truncateEnd(o.getDescription().text);
				ModelIssue issue = createIssue(o, evidence, o.toString(), null);
				issues.add(o, issue);
			}
		}
	}

	public static class AssociationsWithSameDocOnBothEnds extends AbstractRule
			implements SimpleRule<UmlAssociation> {
		private static final String HYPO = "associations that have same documentation for both "
				+ "association ends - one side has the wrong focus";
		private static final String HOWTO = "change documentation for one association end";

		public AssociationsWithSameDocOnBothEnds() {
			super(AssociationValidator._logger, Level.WARN, Severity.medium,
					Category.documentationRule, HYPO, HOWTO);
		}

		@Override
		public EnumSet<Nature> getApplicability() {
			return EnumSet.allOf(Nature.class);
		}

		@Override
		public void validate(UmlAssociation o, ModelIssues issues) {
			String sourceEndDoc = o.getSourceEnd().getDescription().text;
			String targetEndDoc = o.getTargetEnd().getDescription().text;
			if (!sourceEndDoc.isEmpty() && sourceEndDoc.equals(targetEndDoc)) {
				String evidence = Util.truncateEnd(o.getDescription().text);
				ModelIssue issue = createIssue(o, evidence, o.toString(), null);
				issues.add(o, issue);
			}
		}
	}

	public static class AssociationsWithName extends AbstractRule
			implements SimpleRule<UmlAssociation> {
		private static final String HYPO = "associations that should have empty name (we name "
				+ "association ends, not association itself)";
		private static final String HOWTO = "remove association name";

		public AssociationsWithName() {
			super(AssociationValidator._logger, Level.ERROR, Severity.medium, Category.namingRule,
					HYPO, HOWTO);
		}

		@Override
		public EnumSet<Nature> getApplicability() {
			return EnumSet.allOf(Nature.class);
		}

		@Override
		public void validate(UmlAssociation o, ModelIssues issues) {
			if (!o.getName().isEmpty()) {
				ModelIssue issue = createIssue(o);
				issues.add(o, issue);
			}
		}
	}

	public static class AssociationsWithUnallowedStereotype
			extends UmlObjectsWithUnallowedStereotype<UmlAssociation> {
		// doing this way to allow later on adding custom
		private static final Map<Nature, Set<String>> ALLOWED_STEREOS = new TreeMap<>(
				UmlStereotype.getAssociationBuiltIns());

		public AssociationsWithUnallowedStereotype() {
			super(AssociationValidator._logger, "associations", ALLOWED_STEREOS);
		}

		@Override
		public EnumSet<Nature> getApplicability() {
			return EnumSet.allOf(Nature.class);
		}
	}

	public static class AssociationEndsWithUnallowedStereotype
			extends UmlObjectsWithUnallowedStereotype<UmlAssociation> {
		// doing this way to allow later on adding custom
		private static final Map<Nature, Set<String>> ALLOWED_STEREOS = new TreeMap<>(
				UmlStereotype.getAssociationEndBuiltIns());

		@Override
		public EnumSet<Nature> getApplicability() {
			return EnumSet.allOf(Nature.class);
		}

		public AssociationEndsWithUnallowedStereotype() {
			super(AssociationValidator._logger, "association ends", ALLOWED_STEREOS);
		}

		@Override
		protected List<UmlObject> getSubObjects(UmlAssociation o) {
			return collectEndsAsUmlObjects(o);
		}
	}

	public static class AssociationsMissingInformativeStereotype extends AbstractRule
			implements SimpleRule<UmlAssociation> {
		private static final String HYPO = "informative associations missing "
				+ UmlStereotype.INFORMATIVE + " stereotype";
		private static final String HOWTO = "add missing " + UmlStereotype.INFORMATIVE
				+ " stereotype";

		public AssociationsMissingInformativeStereotype() {
			super(AssociationValidator._logger, HYPO, HOWTO);
		}

		@Override
		public EnumSet<Nature> getApplicability() {
			return EnumSet.allOf(Nature.class);
		}

		@Override
		public void validate(UmlAssociation o, ModelIssues issues) {
			if (!o.isInformative()) {
				if (o.getSourceEnd().isInformative() || o.getTargetEnd().isInformative()) {
					ModelIssue issue = createIssue(o);
					issues.add(o, issue);
				}
			}
		}
	}

	public static class AssociationsWithUnallowedTagNames
			extends UmlObjectsWithUnallowedTagNames<UmlAssociation> {
		public AssociationsWithUnallowedTagNames() {
			super(AssociationValidator._logger, "associations");
		}

		@Override
		public EnumSet<Nature> getApplicability() {
			return EnumSet.allOf(Nature.class);
		}
	}

	public static class AssociationEndsWithUnallowedTagNames
			extends UmlObjectsWithUnallowedTagNames<UmlAssociation> {
		public AssociationEndsWithUnallowedTagNames() {
			super(AssociationValidator._logger, "association ends");
		}

		@Override
		public EnumSet<Nature> getApplicability() {
			return EnumSet.allOf(Nature.class);
		}
	}

	public static class AssociationsWithNoMultiplicity extends AbstractRule
			implements SimpleRule<UmlAssociation> {
		private static final String HYPO = "associations missing multiplicity for named "
				+ "association ends";
		private static final String HOWTO = "add multiplicity to the named association end(s)";

		public AssociationsWithNoMultiplicity() {
			super(AssociationValidator._logger, HYPO, HOWTO);
		}

		@Override
		public EnumSet<Nature> getApplicability() {
			return EnumSet.allOf(Nature.class);
		}

		@Override
		public void validate(UmlAssociation o, ModelIssues issues) {
			if (o.getSourceEnd().isNamedWithoutMultiplicity()
					|| o.getTargetEnd().isNamedWithoutMultiplicity()) {
				ModelIssue issue = createIssue(o);
				issues.add(o, issue);
			}
		}
	}

	public static class AssociationsWithWrongSource extends AbstractRule
			implements SimpleRule<UmlAssociation> {
		private static final String HYPO = "associations that should have source and target swapped"
				+ " according to standard IEC TC57 rules (wrong direction for dependency)";
		private static final String HOWTO = "swap source and target classes";

		public AssociationsWithWrongSource() {
			super(AssociationValidator._logger, HYPO, HOWTO);
		}

		@Override
		public EnumSet<Nature> getApplicability() {
			return EnumSet.allOf(Nature.class);
		}

		@Override
		public void validate(UmlAssociation o, ModelIssues issues) {
			if (!o.isWithinSameWg()) {
				if (o.getSource().getOwner() != o.getOwner()) {
					ModelIssue issue = createIssue(o);
					issues.add(o, issue);
				}
			}
		}
	}

	public static class Iec61850AssociationsThatShouldBePrivate extends AbstractRule
			implements SimpleRule<UmlAssociation> {
		/**
		 * Package SCL describes an XSD, where it is ok to have public associations, so we exclude
		 * it from that validation.
		 */
		public static final String VALID_PUBLIC_ASSOC = "SCL";

		private static final String HYPO = "IEC61850 associations that should have private ends";
		private static final String HOWTO = "change visibility of association ends to private";

		public Iec61850AssociationsThatShouldBePrivate() {
			super(AssociationValidator._logger, HYPO, HOWTO);
		}

		@Override
		public EnumSet<Nature> getApplicability() {
			return EnumSet.of(Nature.IEC61850);
		}

		@Override
		public void validate(UmlAssociation o, ModelIssues issues) {
			if (o.getOwner() != OwningWg.WG10) {
				return;
			}
			UmlPackage sourcePackage = o.getSource().getContainingPackage();
			UmlPackage targetPackage = o.getTarget().getContainingPackage();
			if (!sourcePackage.isInOrUnderPackage(VALID_PUBLIC_ASSOC)
					&& targetPackage.isInOrUnderPackage(VALID_PUBLIC_ASSOC)) {
				if (o.isNonPrivate()) {
					ModelIssue issue = createIssue(o);
					issues.add(o, issue);
				}
			}
		}
	}

	public static class Iec61850AssociationsWithDifferentEndVisibility extends AbstractRule
			implements SimpleRule<UmlAssociation> {
		private static final String HYPO = "IEC61850 associations that should have ends with the "
				+ "same visibility";
		private static final String HOWTO = "change visibility on one association end";

		public Iec61850AssociationsWithDifferentEndVisibility() {
			super(AssociationValidator._logger, HYPO, HOWTO);
		}

		@Override
		public EnumSet<Nature> getApplicability() {
			return EnumSet.of(Nature.IEC61850);
		}

		@Override
		public void validate(UmlAssociation o, ModelIssues issues) {
			if (o.getOwner() != OwningWg.WG10 || o.areEndVisibilitiesSame()) {
				return;
			}
			// these are meta-model associations:
			UmlAssociationEnd source = o.getSourceEnd();
			UmlAssociationEnd target = o.getTargetEnd();
			if ((source.getName().isEmpty() && source.getVisibility() == UmlVisibility.PRIVATE
					&& target.getVisibility() == UmlVisibility.PUBLIC)
					|| (target.getName().isEmpty()
							&& target.getVisibility() == UmlVisibility.PRIVATE
							&& source.getVisibility() == UmlVisibility.PUBLIC)) {
				return;
			}
			ModelIssue issue = createIssue(o);
			issues.add(o, issue);
		}
	}

	public static class AssociationEndsMissingDoc extends UmlObjectsMissingDoc<UmlAssociation> {
		public AssociationEndsMissingDoc() {
			super(AssociationValidator._logger, "association ends");
		}

		@Override
		public EnumSet<Nature> getApplicability() {
			return EnumSet.allOf(Nature.class);
		}

		@Override
		protected boolean skipValidation(UmlAssociation o) {
			return true; // skip all associations - we don't document them, but their ends
		}

		@Override
		protected final List<UmlObject> getSubObjects(UmlAssociation assoc) {
			return getSubObjectsDocRelated(assoc);
		}
	}

	public static class AssociationEndsWithBadDocStart
			extends UmlObjectsWithBadDocStart<UmlAssociation> {
		public AssociationEndsWithBadDocStart() {
			super(AssociationValidator._logger, "association ends");
		}

		@Override
		public EnumSet<Nature> getApplicability() {
			return EnumSet.allOf(Nature.class);
		}

		@Override
		protected final List<UmlObject> getSubObjects(UmlAssociation assoc) {
			return getSubObjectsDocRelated(assoc);
		}
	}

	public static class AssociationEndsWithBadDocEnd
			extends UmlObjectsWithBadDocEnd<UmlAssociation> {
		public AssociationEndsWithBadDocEnd() {
			super(AssociationValidator._logger, "association ends");
		}

		@Override
		public EnumSet<Nature> getApplicability() {
			return EnumSet.allOf(Nature.class);
		}

		@Override
		protected final List<UmlObject> getSubObjects(UmlAssociation assoc) {
			return getSubObjectsDocRelated(assoc);
		}
	}

	public static class AssociationEndsWithBadCharacterInName
			extends UmlObjectsWithBadCharacterInName<UmlAssociation> {
		public AssociationEndsWithBadCharacterInName() {
			super(AssociationValidator._logger, "association ends");
		}

		@Override
		public EnumSet<Nature> getApplicability() {
			return EnumSet.allOf(Nature.class);
		}

		@Override
		protected List<UmlObject> getSubObjects(UmlAssociation assoc) {
			return getSubObjectsDocRelated(assoc);
		}

		@Override
		protected InvalidCharactersFinder getInvalidCharacterFinder(UmlObject o) {
			return InvalidCharactersFinder.STRICT;
			// return o.getNature() == Nature.CIM ? InvalidCharactersFinder.STRICT
			// : InvalidCharactersFinder.STRICT_UNDERSCORE_DASH;
		}
	}

	public static class CimAssociationEndsNameStartingWithLowerCase extends AbstractRule
			implements SimpleRule<UmlAssociation> {
		private static final String HYPO = "CIM association ends whose name starts with a lower "
				+ "case letter";
		private static final String HOWTO = "rename association end(s) to start with upper case "
				+ "letter";

		public CimAssociationEndsNameStartingWithLowerCase() {
			super(AssociationValidator._logger, Level.ERROR, Severity.high, Category.namingRule,
					HYPO, HOWTO);
		}

		@Override
		public EnumSet<Nature> getApplicability() {
			return EnumSet.of(Nature.CIM);
		}

		@Override
		public void validate(UmlAssociation ae, ModelIssues issues) {
			doValidate(ae.getSourceEnd(), issues);
			doValidate(ae.getTargetEnd(), issues);
		}

		private void doValidate(UmlAssociationEnd o, ModelIssues issues) {
			if (o.getNature() == Nature.CIM && Util.hasContent(o.getName())) {
				char firstLetter = o.getName().charAt(0);
				if (Character.isLowerCase(firstLetter)) {
					issues.add(o, createIssue(o, null, o.toString(), null));
				}
			}
		}
	}

	public static class CimAssociationEndsNameShouldBePlural extends AbstractRule
			implements SimpleRule<UmlAssociation> {
		private static final String HYPO = "CIM association ends that should be plural according "
				+ "to multiplicity";
		private static final String HOWTO = "rename association end(s) to plural";

		public CimAssociationEndsNameShouldBePlural() {
			super(AssociationValidator._logger, Level.ERROR, Severity.high, Category.namingRule,
					HYPO, HOWTO);
		}

		@Override
		public EnumSet<Nature> getApplicability() {
			return EnumSet.of(Nature.CIM);
		}

		@Override
		public void validate(UmlAssociation ae, ModelIssues issues) {
			doValidate(ae.getSourceEnd(), issues);
			doValidate(ae.getTargetEnd(), issues);
		}

		private void doValidate(UmlAssociationEnd o, ModelIssues issues) {
			if (o.getNature() == Nature.CIM && o.getMultiplicity().isMultivalue()
					&& !Util.looksLikePlural(o.getName())) {
				issues.add(o, createIssue(o, null, o.toString(), null));
			}
		}
	}

	public static class CimAssociationEndsNameShouldBeSingular extends AbstractRule
			implements SimpleRule<UmlAssociation> {
		private static final String HYPO = "CIM association ends that should be singular according "
				+ "to multiplicity";
		private static final String HOWTO = "rename association end(s) to singular";

		public CimAssociationEndsNameShouldBeSingular() {
			super(AssociationValidator._logger, Level.ERROR, Severity.high, Category.namingRule,
					HYPO, HOWTO);
		}

		@Override
		public EnumSet<Nature> getApplicability() {
			return EnumSet.of(Nature.CIM);
		}

		@Override
		public void validate(UmlAssociation ae, ModelIssues issues) {
			doValidate(ae.getSourceEnd(), issues);
			doValidate(ae.getTargetEnd(), issues);
		}

		private void doValidate(UmlAssociationEnd o, ModelIssues issues) {
			if (o.getNature() == Nature.CIM && !o.getMultiplicity().isMultivalue()
					&& Util.looksLikePlural(o.getName())) {
				issues.add(o, createIssue(o, null, o.toString(), null));
			}
		}
	}
}
