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
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.tanjakostic.jcleancim.common.Config;
import org.tanjakostic.jcleancim.common.Nature;
import org.tanjakostic.jcleancim.common.OwningWg;
import org.tanjakostic.jcleancim.model.AbstractUmlObject;
import org.tanjakostic.jcleancim.model.PresenceCondition;
import org.tanjakostic.jcleancim.model.UML;
import org.tanjakostic.jcleancim.model.UmlAssociationEnd;
import org.tanjakostic.jcleancim.model.UmlAttribute;
import org.tanjakostic.jcleancim.model.UmlClass;
import org.tanjakostic.jcleancim.model.UmlConstraint;
import org.tanjakostic.jcleancim.model.UmlKind;
import org.tanjakostic.jcleancim.model.UmlObject;
import org.tanjakostic.jcleancim.model.UmlSkipped;
import org.tanjakostic.jcleancim.model.UmlStereotype;
import org.tanjakostic.jcleancim.util.MapOfCollections;
import org.tanjakostic.jcleancim.util.MapOfLists;
import org.tanjakostic.jcleancim.util.Util;
import org.tanjakostic.jcleancim.validation.AbstractRule.UmlObjectsMissingDoc;
import org.tanjakostic.jcleancim.validation.AbstractRule.UmlObjectsWithBadCharacterInName;
import org.tanjakostic.jcleancim.validation.AbstractRule.UmlObjectsWithBadDocEnd;
import org.tanjakostic.jcleancim.validation.AbstractRule.UmlObjectsWithBadDocStart;
import org.tanjakostic.jcleancim.validation.AbstractRule.UmlObjectsWithUnallowedStereotype;
import org.tanjakostic.jcleancim.validation.AbstractRule.UmlObjectsWithUnallowedTagNames;

/**
 * Validates classes.
 *
 * @author tatjana.kostic@ieee.org
 * @version $Id: ClassValidator.java 31 2019-12-08 01:19:54Z dev978 $
 */
public class ClassValidator extends AbstractValidator<UmlClass> {
	private static final Logger _logger = Logger.getLogger(ClassValidator.class.getName());

	private final List<UmlClass> _scopedUmlObjects;

	ClassValidator(Config cfg, Collection<UmlClass> allClasses, ModelIssues issues) {
		super(cfg, allClasses.size(), "classes", issues);

		_scopedUmlObjects = AbstractUmlObject.collectForScope(allClasses, cfg.getValidationScope());

		addSimpleRule(new CimClassesWithUnexpectedElements());
		addSimpleRule(new ClassesWithUnexpectedConnectors());
		addSimpleRule(new EnumClassesWithNoLiterals());
		addSimpleRule(new CimCompoundClassesWithNoAttributes());
		addSimpleRule(new EnumClassesWithSingleLiteral());
		addSimpleRule(new EnumClassesWithTwoLiterals());
		addSimpleRule(new EnumClassesWithBadName());
		addSimpleRule(new CimPrimitiveClassesWithAttributes());
		addSimpleRule(new CimPrimitiveClassesWithIllegalOwner());
		addSimpleRule(new ClassesWithDuplicateInheritedAttributeNames());
		addSimpleRule(new ClassesWithDuplicateOwnOrInheritedAssociationEndNames());
		addSimpleRule(new ClassesWithSelfInheritance());
		addSimpleRule(new ClassesWithSelfDependency());
		addSimpleRule(new ClassesWithLeafPropSet());
		addSimpleRule(new ClassesWithRootPropSet());
		addSimpleRule(new ClassesWithPersistentPropSet());
		addSimpleRule(new ClassesWithMultipleSuperclasses());
		addSimpleRule(new ClassesWithSuperclassesFromUnallowedOwner());
		addSimpleRule(new ClassesThatShouldNotBeAssociationClass());
		addSimpleRule(new ClassesWithUnallowedStereotype());
		addSimpleRule(new CimClassesWithOldDatatypeStereotype());
		addSimpleRule(new CimClassesUsedForAttributesButHaveAssociations());
		addSimpleRule(new CimClassesUsedForAttributesButHaveSubclasses());
		addSimpleRule(new CimClassesUsedForAttributesButHaveSuperclasses());
		addSimpleRule(new CimClassesThatShouldNotBeAbstract());
		addSimpleRule(new CimClassesThatShouldNotHaveOperations());
		addSimpleRule(new CimClassesThatShouldNotHaveExplicitDependencies());
		addSimpleRule(new ClassesThatShouldNotHaveNestingThroughAttribute());
		addSimpleRule(new Iec61850ClassesThatShouldHaveAliasAsTitle());
		addSimpleRule(new Iec61850ClassesThatShouldHaveTaggedValuesForDocgen());
		addSimpleRule(new CimClassesNeverUsedInRelationships());
		addSimpleRule(new ClassesWithUnallowedTagNames());
		addSimpleRule(new Iec61850ClassesWithInvalidConstraints());
		addSimpleRule(new Iec61850LNClassesWithSuperfluousConstraints());
		addSimpleRule(new Iec61850ClassesWithMissingCondIDTextInConstraints());
		addSimpleRule(new CimDatatypeClassesWithInvalidAttributes());
		addSimpleRule(new ClassesMissingDoc());
		addSimpleRule(new ClassesWithBadDocStart());
		addSimpleRule(new ClassesWithBadDocEnd());
		addSimpleRule(new ClassesWithBadCharacterInName());
		addSimpleRule(new CimClassesNameStartingWithLowerCase());
		addSimpleRule(new CimClassesNameShouldBeSingular());
		addSimpleRule(new Iec61850LNClassesInWrongGroup());
		addSimpleRule(new Iec61850LNClassesMalformedName());
		addSimpleRule(new EnumClassesWithSomeCodesMissing());
		addSimpleRule(new EnumClassesWithDuplicateCodes());

		addCrossRule(new ClassesWithSameName(allClasses));

		List<UmlClass> scopedClasses = _scopedUmlObjects;

		List<UmlAttribute> attributes = new ArrayList<UmlAttribute>();
		for (UmlClass c : scopedClasses) {
			attributes.addAll(c.getAttributes());
		}
		List<UmlAttribute> scopedAttributes = AbstractUmlObject.collectForScope(attributes,
				cfg.getValidationScope());
		addCrossRule(new CimClassesNeverUsedAsTypeForAttribute(scopedAttributes));

	}

	@Override
	public boolean enabled() {
		return getCfg().isValidationClassesOn();
	}

	@Override
	public List<UmlClass> getScopedUmlObjects() {
		return _scopedUmlObjects;
	}

	// ==================== simple rules

	public static class CimClassesWithUnexpectedElements extends AbstractRule
			implements SimpleRule<UmlClass> {
		// EA-specific names for connectors and elements that are expected, but not processed
		private static final List<String> SKIPPED_ELEMENTS = Arrays
				.asList(UmlSkipped.EA_STATE_MACHINE, UmlSkipped.EA_BOUNDARY);

		private static final String HYPO = "classes with unexpected embedded elements "
				+ "(present in the model repository, but not kept in the in-memory model)";
		private static final String HOWTO = "remove embedded elements, or move them out of class";

		public CimClassesWithUnexpectedElements() {
			super(ClassValidator._logger, Level.WARN, Severity.high, Category.permissiveTool, HYPO,
					HOWTO);
		}

		private boolean isAcceptedElement(UmlSkipped skipped) {
			if (skipped.isConnector()) {
				return true; // pass thru for connector in this validator
			}
			if (skipped.getNature() != Nature.CIM) {
				return true; // pass thru for non-CIM classes in this validator
			}
			return SKIPPED_ELEMENTS.contains(skipped.getKind().getValue());
		}

		@Override
		public EnumSet<Nature> getApplicability() {
			return EnumSet.of(Nature.CIM);
		}

		@Override
		public void validate(UmlClass o, ModelIssues issues) {
			List<String> evidence = new ArrayList<>();
			for (UmlSkipped skipped : o.getSkippedUmlItems()) {
				if (!isAcceptedElement(skipped)) {
					evidence.add(skipped.toShortString(true, true));
				}
			}
			if (!evidence.isEmpty()) {
				issues.add(o, createIssue(o, evidence.toString()));
			}
		}
	}

	public static class ClassesWithUnexpectedConnectors extends AbstractRule
			implements SimpleRule<UmlClass> {
		// EA-specific names for connectors and elements that are expected, but not processed
		private static final List<String> SKIPPED_CONNECTORS = Arrays
				.asList(UmlSkipped.EA_NOTE_LINK);

		private static final String HYPO = "classes with unexpected embedded connectors "
				+ "(present in the model repository, but not kept in the in-memory model)";
		private static final String HOWTO = "remove embedded connectors, or move them out of class";

		public ClassesWithUnexpectedConnectors() {
			super(ClassValidator._logger, Level.WARN, Severity.high, Category.permissiveTool, HYPO,
					HOWTO);
		}

		@Override
		public EnumSet<Nature> getApplicability() {
			return EnumSet.allOf(Nature.class);
		}

		private boolean isAcceptedConnector(UmlSkipped skipped) {
			return !skipped.isConnector() || (skipped.isConnector()
					&& SKIPPED_CONNECTORS.contains(skipped.getKind().getValue()));
		}

		@Override
		public void validate(UmlClass o, ModelIssues issues) {
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

	abstract public static class ClassesWithQuestionableAttributeCount extends AbstractRule
			implements SimpleRule<UmlClass> {

		public ClassesWithQuestionableAttributeCount(Level level, Severity severity, String hypo,
				String howToFix) {
			super(ClassValidator._logger, level, severity, Category.modellingRule, hypo, howToFix);
		}

		@Override
		public final void validate(UmlClass o, ModelIssues issues) {
			if (satisfiesCondition(o)) {
				List<UmlAttribute> involvedAttributes = new ArrayList<UmlAttribute>();
				for (UmlClass typeUsingClazz : o.getAttributeAfferentClasses()) {
					involvedAttributes.addAll(typeUsingClazz.findAttributes(o));
				}
				String evidence = "used by "
						+ AbstractUmlObject.collectQNames(involvedAttributes, true).toString();
				issues.add(o, createIssue(o, evidence));
			}
		}

		abstract protected boolean satisfiesCondition(UmlClass clazz);
	}

	public static class EnumClassesWithNoLiterals extends ClassesWithQuestionableAttributeCount {
		private static final String HYPO = "enumeration classes with no literals. Attributes with"
				+ " that type can only be null";
		private static final String HOWTO = "add some literals OR remove enumeration class (ensure"
				+ " to change type of attributes that potentially use it)";

		public EnumClassesWithNoLiterals() {
			super(Level.ERROR, Severity.high, HYPO, HOWTO);
		}

		@Override
		public EnumSet<Nature> getApplicability() {
			return EnumSet.allOf(Nature.class);
		}

		@Override
		protected boolean satisfiesCondition(UmlClass o) {
			return o.isEnumeratedType() && !o.isAbbreviationEnumeration()
					&& o.getAttributes().isEmpty();
		}
	}

	public static class CimCompoundClassesWithNoAttributes
			extends ClassesWithQuestionableAttributeCount {
		private static final String HYPO = "CIM compound classes with no attributes. Attributes with"
				+ " that type can only be null";
		private static final String HOWTO = "add some attributes OR remove coumpound class (ensure"
				+ " to change type of attributes that potentially use it)";

		public CimCompoundClassesWithNoAttributes() {
			super(Level.ERROR, Severity.high, HYPO, HOWTO);
		}

		@Override
		public EnumSet<Nature> getApplicability() {
			return EnumSet.of(Nature.CIM);
		}

		@Override
		protected boolean satisfiesCondition(UmlClass o) {
			return o.isCompound() && o.getAttributes().isEmpty();
		}
	}

	public static class EnumClassesWithSingleLiteral extends ClassesWithQuestionableAttributeCount {
		private static final String HYPO = "enumeration classes with single literal. Does it make "
				+ "sense to keep the enumerated type with a single literal?";
		private static final String HOWTO = "add some literals OR remove enumeration class (ensure"
				+ " to change type of attributes that potentially use it)";

		public EnumClassesWithSingleLiteral() {
			super(Level.WARN, Severity.medium, HYPO, HOWTO);
		}

		@Override
		public EnumSet<Nature> getApplicability() {
			return EnumSet.allOf(Nature.class);
		}

		@Override
		protected boolean satisfiesCondition(UmlClass o) {
			return o.isEnumeratedType() && !o.isAbbreviationEnumeration()
					&& !o.isConditionEnumeration() && o.getAttributes().size() == 1;
		}
	}

	public static class EnumClassesWithTwoLiterals extends ClassesWithQuestionableAttributeCount {
		private static final String HYPO = "enumeration classes with two literals. Could Boolean "
				+ "be used as type for attributes?";
		private static final String HOWTO = "consider using Boolean instead (then remove the"
				+ " enumeration class)";

		public EnumClassesWithTwoLiterals() {
			super(Level.WARN, Severity.low, HYPO, HOWTO);
		}

		@Override
		public EnumSet<Nature> getApplicability() {
			return EnumSet.allOf(Nature.class);
		}

		@Override
		protected boolean satisfiesCondition(UmlClass o) {
			return o.isEnumeratedType() && !o.isAbbreviationEnumeration()
					&& o.getAttributes().size() == 2;
		}
	}

	public static class EnumClassesWithBadName extends AbstractRule
			implements SimpleRule<UmlClass> {
		public static final String TYPE = "type";
		public static final String ENUM = "enum";
		public static final String KIND = "Kind";
		private static final String HYPO = "enumeration classes with bad name: name should not"
				+ " contain '" + TYPE + "' and/or '" + ENUM + "', and should end with '" + KIND
				+ "'";
		private static final String HOWTO = "rename the enumeration class";

		public EnumClassesWithBadName() {
			super(ClassValidator._logger, HYPO, HOWTO);
		}

		@Override
		public EnumSet<Nature> getApplicability() {
			return EnumSet.allOf(Nature.class);
		}

		@Override
		public void validate(UmlClass o, ModelIssues issues) {
			if (o.isEnumeratedType() && !o.isAbbreviationEnumeration()
					&& !o.isConditionEnumeration()) {
				String name = o.getName();
				if (name.toLowerCase().contains(TYPE.toLowerCase())
						|| name.toLowerCase().contains(ENUM.toLowerCase())
						|| !name.endsWith(KIND)) {
					issues.add(o, createIssue(o));
				}
			}
		}
	}

	public static class ClassesWithSelfInheritance extends AbstractRule
			implements SimpleRule<UmlClass> {
		private static final String HYPO = "classes with self-inheritance; model repository "
				+ "allows to create such links, but they are not kept in the the in-memory model";
		private static final String HOWTO = "remove self-inheritance connector";

		public ClassesWithSelfInheritance() {
			super(ClassValidator._logger, Level.ERROR, Severity.high, Category.permissiveTool, HYPO,
					HOWTO);
		}

		@Override
		public EnumSet<Nature> getApplicability() {
			return EnumSet.allOf(Nature.class);
		}

		@Override
		public void validate(UmlClass o, ModelIssues issues) {
			if (o.isSelfInherited()) {
				issues.add(o, createIssue(o));
			}
		}
	}

	public static class CimPrimitiveClassesWithAttributes extends AbstractRule
			implements SimpleRule<UmlClass> {
		private static final String HYPO = "CIM primitive classes that should, by definition, "
				+ "have no attributes";
		private static final String HOWTO = "remove attributes if the class is primitive OR "
				+ "remove " + UmlStereotype.PRIMITIVE + "stereotype if attributes are needed";

		public CimPrimitiveClassesWithAttributes() {
			super(ClassValidator._logger, HYPO, HOWTO);
		}

		@Override
		public EnumSet<Nature> getApplicability() {
			return EnumSet.of(Nature.CIM);
		}

		@Override
		public void validate(UmlClass o, ModelIssues issues) {
			if (o.isPrimitive() && !o.getAttributes().isEmpty()) {
				issues.add(o, createIssue(o));
			}
		}
	}

	public static class CimPrimitiveClassesWithIllegalOwner extends AbstractRule
			implements SimpleRule<UmlClass> {
		private static final String HYPO = "CIM primitive classes that should belong to "
				+ OwningWg.WG13.toString() + ", according to TC57 rules";
		private static final String HOWTO = "move class to a " + OwningWg.WG13.toString()
				+ " package";

		public CimPrimitiveClassesWithIllegalOwner() {
			super(ClassValidator._logger, HYPO, HOWTO);
		}

		@Override
		public EnumSet<Nature> getApplicability() {
			return EnumSet.of(Nature.CIM);
		}

		@Override
		public void validate(UmlClass o, ModelIssues issues) {
			if (o.isPrimitive() && o.getOwner() != OwningWg.WG13) {
				issues.add(o, createIssue(o));
			}
		}
	}

	public static class ClassesWithDuplicateInheritedAttributeNames extends AbstractRule
			implements SimpleRule<UmlClass> {
		private static final String HYPO = "classes that have duplicate inherited attribute names";
		private static final String HOWTO = "rename offending native attribute(s)";

		public ClassesWithDuplicateInheritedAttributeNames() {
			super(ClassValidator._logger, HYPO, HOWTO);
		}

		@Override
		public EnumSet<Nature> getApplicability() {
			return EnumSet.allOf(Nature.class);
		}

		@Override
		public void validate(UmlClass o, ModelIssues issues) {
			List<UmlAttribute> attributes = new ArrayList<UmlAttribute>(o.getAttributes());
			attributes.addAll(o.getInheritedAttributes());

			MapOfCollections<String, UmlAttribute> duplicateObjects = AbstractUmlObject
					.collectDuplicateNames(attributes);
			if (duplicateObjects.isEmpty()) {
				return;
			}

			List<String> fmtDups = new ArrayList<String>();
			for (String dupName : duplicateObjects.keys()) {
				Collection<UmlAttribute> attrs = duplicateObjects.subCollection(dupName);
				List<String> dupQNames = AbstractUmlObject.collectQNames(attrs, true);
				fmtDups.add(dupQNames.toString());
			}
			String evidence = "duplicate attributes = " + fmtDups.toString();
			issues.add(o, createIssue(o, evidence));
		}
	}

	public static class ClassesWithDuplicateOwnOrInheritedAssociationEndNames extends AbstractRule
			implements SimpleRule<UmlClass> {
		private static final String HYPO = "classes that have duplicate own or inherited"
				+ " association end names";
		private static final String HOWTO = "rename offending native association end(s)";

		public ClassesWithDuplicateOwnOrInheritedAssociationEndNames() {
			super(ClassValidator._logger, HYPO, HOWTO);
		}

		@Override
		public EnumSet<Nature> getApplicability() {
			return EnumSet.allOf(Nature.class);
		}

		@Override
		public void validate(UmlClass o, ModelIssues issues) {
			ArrayList<UmlAssociationEnd> assocEnds = new ArrayList<UmlAssociationEnd>(
					o.getOtherSideAssociationEnds());
			assocEnds.addAll(o.getInheritedOtherSideAssociationEnds());

			MapOfCollections<String, UmlAssociationEnd> duplicateObjects = AbstractUmlObject
					.collectDuplicateNames(assocEnds);
			if (duplicateObjects.isEmpty()) {
				return;
			}

			List<String> fmtDups = new ArrayList<String>();
			for (String dupName : duplicateObjects.keys()) {
				Collection<UmlAssociationEnd> aes = duplicateObjects.subCollection(dupName);
				List<String> dupQNames = AbstractUmlObject.collectQNames(aes, true);
				fmtDups.add(dupQNames.toString());
			}
			String evidence = "duplicate association ends = " + fmtDups.toString();
			issues.add(o, createIssue(o, evidence));
		}
	}

	public static class ClassesWithSelfDependency extends AbstractRule
			implements SimpleRule<UmlClass> {
		private static final String WHAT = "classes";
		private static final String HYPO = WHAT + " with self-dependency (UML tool allows to create"
				+ " such links, but they are not kept in the the in-memory model";
		private static final String HOWTO = "remove self-dependency";

		public ClassesWithSelfDependency() {
			super(ClassValidator._logger, Level.ERROR, Severity.high, Category.permissiveTool, HYPO,
					HOWTO);
		}

		@Override
		public EnumSet<Nature> getApplicability() {
			return EnumSet.allOf(Nature.class);
		}

		@Override
		public void validate(UmlClass o, ModelIssues issues) {
			if (o.isSelfDependent()) {
				issues.add(o, createIssue(o));
			}
		}
	}

	public static class ClassesWithLeafPropSet extends AbstractRule
			implements SimpleRule<UmlClass> {
		private static final String HYPO = "classes with leaf property";
		private static final String HOWTO = "remove the leaf property in EA class property "
				+ "editor->advanced";

		public ClassesWithLeafPropSet() {
			super(ClassValidator._logger, HYPO, HOWTO);
		}

		@Override
		public EnumSet<Nature> getApplicability() {
			return EnumSet.allOf(Nature.class);
		}

		@Override
		public void validate(UmlClass o, ModelIssues issues) {
			if (o.isEaLeafPropSet()) {
				issues.add(o, createIssue(o));
			}
		}
	}

	public static class ClassesWithRootPropSet extends AbstractRule
			implements SimpleRule<UmlClass> {
		private static final String HYPO = "classes with root property";
		private static final String HOWTO = "remove the root property in EA class property "
				+ "editor->advanced";

		public ClassesWithRootPropSet() {
			super(ClassValidator._logger, HYPO, HOWTO);
		}

		@Override
		public EnumSet<Nature> getApplicability() {
			return EnumSet.allOf(Nature.class);
		}

		@Override
		public void validate(UmlClass o, ModelIssues issues) {
			if (o.isEaRootPropSet()) {
				issues.add(o, createIssue(o));
			}
		}
	}

	public static class ClassesWithPersistentPropSet extends AbstractRule
			implements SimpleRule<UmlClass> {
		private static final String HYPO = "classes with persistence property";
		private static final String HOWTO = "remove the persistence property in EA class property "
				+ "editor";

		public ClassesWithPersistentPropSet() {
			super(ClassValidator._logger, HYPO, HOWTO);
		}

		@Override
		public EnumSet<Nature> getApplicability() {
			return EnumSet.allOf(Nature.class);
		}

		@Override
		public void validate(UmlClass o, ModelIssues issues) {
			if (o.isEaPersistentPropSet()) {
				issues.add(o, createIssue(o));
			}
		}
	}

	public static class ClassesWithMultipleSuperclasses extends AbstractRule
			implements SimpleRule<UmlClass> {
		private static final String HYPO = "classes with multiple superclasses - multiple inheritance"
				+ " is prohibited in standard IEC TC57";
		private static final String HOWTO = "keep only one superclass";

		public ClassesWithMultipleSuperclasses() {
			super(ClassValidator._logger, HYPO, HOWTO);
		}

		@Override
		public EnumSet<Nature> getApplicability() {
			return EnumSet.allOf(Nature.class);
		}

		@Override
		public void validate(UmlClass o, ModelIssues issues) {
			if (o.getSuperclasses().size() > 1) {
				String evidence = "all superclasses = "
						+ AbstractUmlObject.collectQNames(o.getSuperclasses(), false).toString();
				issues.add(o, createIssue(o, evidence));
			}
		}
	}

	public static class ClassesWithSuperclassesFromUnallowedOwner extends AbstractRule
			implements SimpleRule<UmlClass> {
		private static final String HYPO = "classes inheriting from classes of prohibited owner"
				+ " according to standard IEC TC57 rules (wrong direction for dependency)";
		private static final String HOWTO = "remove inheritance OR move the class to that owner";

		public ClassesWithSuperclassesFromUnallowedOwner() {
			super(ClassValidator._logger, HYPO, HOWTO);
		}

		@Override
		public EnumSet<Nature> getApplicability() {
			return EnumSet.allOf(Nature.class);
		}

		@Override
		public void validate(UmlClass o, ModelIssues issues) {
			OwningWg owner = o.getOwner();
			EnumSet<OwningWg> allowedOtherOwners = owner.getAllowedOtherEndOwners();
			List<UmlClass> supers = new ArrayList<UmlClass>();
			for (UmlClass sup : o.getSuperclasses()) {
				if (!allowedOtherOwners.contains(sup.getOwner())) {
					supers.add(sup);
				}
			}
			if (!supers.isEmpty()) {
				String evidence = "offending superclass(es) = "
						+ AbstractUmlObject.collectQNames(supers, true).toString();
				issues.add(o, createIssue(o, evidence));
			}
		}
	}

	public static class ClassesThatShouldNotBeAssociationClass extends AbstractRule
			implements SimpleRule<UmlClass> {
		private static final String HYPO = "association classes";
		private static final String HOWTO = "transform into a regular class with two explicit"
				+ " associations";

		public ClassesThatShouldNotBeAssociationClass() {
			super(ClassValidator._logger, HYPO, HOWTO);
		}

		@Override
		public EnumSet<Nature> getApplicability() {
			return EnumSet.allOf(Nature.class);
		}

		@Override
		public void validate(UmlClass o, ModelIssues issues) {
			if (o.isAssociationClass()) {
				issues.add(o, createIssue(o));
			}
		}
	}

	public static class ClassesWithUnallowedStereotype
			extends UmlObjectsWithUnallowedStereotype<UmlClass> {
		// doing this way to allow later on adding custom
		private static final Map<Nature, Set<String>> ALLOWED_STEREOS = new TreeMap<>(
				UmlStereotype.getClassBuiltIns());

		public ClassesWithUnallowedStereotype() {
			super(ClassValidator._logger, "classes", ALLOWED_STEREOS);
		}

		@Override
		public EnumSet<Nature> getApplicability() {
			return EnumSet.allOf(Nature.class);
		}
	}

	public static class CimClassesWithOldDatatypeStereotype extends AbstractRule
			implements SimpleRule<UmlClass> {
		private static final String HYPO = "CIM classes with old (pre-CIM15) datatype stereotype '"
				+ UmlStereotype.OLD_DATATYPE + "'";
		private static final String HOWTO = "change stereotype to '" + UmlStereotype.CIMDATATYPE
				+ "'";

		public CimClassesWithOldDatatypeStereotype() {
			super(ClassValidator._logger, Level.WARN, Severity.medium, Category.modellingRule, HYPO,
					HOWTO);
		}

		@Override
		public EnumSet<Nature> getApplicability() {
			return EnumSet.of(Nature.CIM);
		}

		@Override
		public void validate(UmlClass o, ModelIssues issues) {
			if (o.getNature() == Nature.CIM && o.isWithOldDatatypeStereotype()) {
				issues.add(o, createIssue(o));
			}
		}
	}

	public static class CimClassesUsedForAttributesButHaveAssociations extends AbstractRule
			implements SimpleRule<UmlClass> {
		private static final String HYPO = "CIM classes that should be used as type for "
				+ "attributes, but have associations";
		private static final String HOWTO = "replace associations with attributes OR "
				+ "remove stereotype from this class";

		public CimClassesUsedForAttributesButHaveAssociations() {
			super(ClassValidator._logger, HYPO, HOWTO);
		}

		@Override
		public EnumSet<Nature> getApplicability() {
			return EnumSet.of(Nature.CIM);
		}

		@Override
		public void validate(UmlClass o, ModelIssues issues) {
			if (o.getNature() == Nature.CIM) {
				if (o.isUsedAsTypeForAttributes() && !o.getAssociations().isEmpty()) {
					issues.add(o, createIssue(o));
				}
			}
		}
	}

	public static class CimClassesUsedForAttributesButHaveSubclasses extends AbstractRule
			implements SimpleRule<UmlClass> {
		private static final String HYPO = "CIM classes that should not participate in "
				+ "inheritance but have subclasses";
		private static final String HOWTO = "remove inheritance links OR remove stereotype from "
				+ "this class";

		public CimClassesUsedForAttributesButHaveSubclasses() {
			super(ClassValidator._logger, HYPO, HOWTO);
		}

		@Override
		public EnumSet<Nature> getApplicability() {
			return EnumSet.of(Nature.CIM);
		}

		@Override
		public void validate(UmlClass o, ModelIssues issues) {
			if (o.getNature() == Nature.CIM) {
				if (o.isUsedAsTypeForAttributes() && !o.getSubclasses().isEmpty()) {
					String evidence = "offending subclass(es) = "
							+ AbstractUmlObject.collectQNames(o.getSubclasses(), true).toString();
					issues.add(o, createIssue(o, evidence));
				}
			}
		}
	}

	public static class CimClassesUsedForAttributesButHaveSuperclasses extends AbstractRule
			implements SimpleRule<UmlClass> {
		private static final String HYPO = "CIM classes that should not participate in "
				+ "inheritance but have superclasses";
		private static final String HOWTO = "remove inheritance links OR remove stereotype from "
				+ "this class";

		public CimClassesUsedForAttributesButHaveSuperclasses() {
			super(ClassValidator._logger, HYPO, HOWTO);
		}

		@Override
		public EnumSet<Nature> getApplicability() {
			return EnumSet.of(Nature.CIM);
		}

		@Override
		public void validate(UmlClass o, ModelIssues issues) {
			if (o.getNature() == Nature.CIM) {
				if (o.isUsedAsTypeForAttributes() && !o.getSuperclasses().isEmpty()) {
					String evidence = "offending superclass(es) = "
							+ AbstractUmlObject.collectQNames(o.getSuperclasses(), true).toString();
					issues.add(o, createIssue(o, evidence));
				}
			}
		}
	}

	public static class CimClassesThatShouldNotBeAbstract extends AbstractRule
			implements SimpleRule<UmlClass> {
		private static final String HYPO = "CIM classes that are abstract - in standard CIM, no "
				+ "classes should be abstract";
		private static final String HOWTO = "edit class properties in EA and unselect 'abstract'";

		public CimClassesThatShouldNotBeAbstract() {
			super(ClassValidator._logger, HYPO, HOWTO);
		}

		@Override
		public EnumSet<Nature> getApplicability() {
			return EnumSet.of(Nature.CIM);
		}

		@Override
		public void validate(UmlClass o, ModelIssues issues) {
			if (o.getNature() == Nature.CIM && o.isAbstract()) {
				issues.add(o, createIssue(o));
			}
		}
	}

	public static class CimClassesThatShouldNotHaveOperations extends AbstractRule
			implements SimpleRule<UmlClass> {
		private static final String HYPO = "CIM classes that have operations - in standard CIM, no "
				+ "classes should have operations";
		private static final String HOWTO = "remove operations from the class";

		public CimClassesThatShouldNotHaveOperations() {
			super(ClassValidator._logger, HYPO, HOWTO);
		}

		@Override
		public EnumSet<Nature> getApplicability() {
			return EnumSet.of(Nature.CIM);
		}

		@Override
		public void validate(UmlClass o, ModelIssues issues) {
			if (o.getNature() == Nature.CIM && !o.getOperations().isEmpty()) {
				issues.add(o, createIssue(o));
			}
		}
	}

	// FIXME: Distinguish between canonical and profile classes, allow for UML profiled ones
	public static class CimClassesThatShouldNotHaveExplicitDependencies extends AbstractRule
			implements SimpleRule<UmlClass> {
		private static final String HYPO = "CIM classes that have explicit UML dependencies on other "
				+ "classes - in standard CIM, no classes should have explicit dependencies";
		private static final String HOWTO = "remove inter-class UML dependencies";

		public CimClassesThatShouldNotHaveExplicitDependencies() {
			super(ClassValidator._logger, HYPO, HOWTO);
		}

		@Override
		public EnumSet<Nature> getApplicability() {
			return EnumSet.of(Nature.CIM);
		}

		@Override
		public void validate(UmlClass o, ModelIssues issues) {
			if (o.getNature() == Nature.CIM && !o.collectDependencyAfferentStructures().isEmpty()) {
				issues.add(o, createIssue(o));
			}
		}
	}

	public static class ClassesThatShouldNotHaveNestingThroughAttribute extends AbstractRule
			implements SimpleRule<UmlClass> {
		private static final String HYPO = "classes that use themselves as type for their attribute "
				+ "- nesting (recursion) through attributes is not allowed";
		private static final String HOWTO = "change type for those attributes that has the same"
				+ " type as their containing class";

		public ClassesThatShouldNotHaveNestingThroughAttribute() {
			super(ClassValidator._logger, HYPO, HOWTO);
		}

		@Override
		public EnumSet<Nature> getApplicability() {
			return EnumSet.allOf(Nature.class);
		}

		@Override
		public void validate(UmlClass o, ModelIssues issues) {
			if (o.getAttributeAfferentClasses().contains(o)) {
				issues.add(o, createIssue(o));
			}
		}
	}

	public static class Iec61850ClassesThatShouldHaveAliasAsTitle extends AbstractRule
			implements SimpleRule<UmlClass> {
		private static final String HYPO = "classes used for generating IEC61850 parts 7-3 or 7-4 "
				+ "that are missing alias - needed as title in the doc";
		private static final String HOWTO = "add alias, otherwise the title in the doc will be odd";

		public Iec61850ClassesThatShouldHaveAliasAsTitle() {
			super(ClassValidator._logger, HYPO, HOWTO);
		}

		@Override
		public EnumSet<Nature> getApplicability() {
			return EnumSet.of(Nature.IEC61850);
		}

		@Override
		public void validate(UmlClass o, ModelIssues issues) {
			if (o.needsAlias() && o.getAlias().isEmpty()) {
				issues.add(o, createIssue(o));
			}
		}
	}

	public static class Iec61850ClassesThatShouldHaveTaggedValuesForDocgen extends AbstractRule
			implements SimpleRule<UmlClass> {
		private static final String HYPO = "classes used for generating LN mappings between "
				+ "IEC 61850-5 and IEC 61850-7-4 that are missing tagged values needed for doc "
				+ "geneneration";
		private static final String HOWTO = "add required tagged values";

		public Iec61850ClassesThatShouldHaveTaggedValuesForDocgen() {
			super(ClassValidator._logger, HYPO, HOWTO);
		}

		@Override
		public EnumSet<Nature> getApplicability() {
			return EnumSet.of(Nature.IEC61850);
		}

		@Override
		public void validate(UmlClass o, ModelIssues issues) {
			if (o.needsTags()) {
				if (o.getRsName() == null || o.getIeeeRef() == null || o.getIecRef() == null) {
					issues.add(o, createIssue(o));
				}
			}
		}
	}

	public static class CimClassesNeverUsedInRelationships extends AbstractRule
			implements SimpleRule<UmlClass> {
		private static final String HYPO = "CIM classes never used in any association or inheritance";
		private static final String HOWTO = "should this class be removed?";

		public CimClassesNeverUsedInRelationships() {
			super(ClassValidator._logger, HYPO, HOWTO);
		}

		@Override
		public EnumSet<Nature> getApplicability() {
			return EnumSet.of(Nature.CIM);
		}

		@Override
		public void validate(UmlClass o, ModelIssues issues) {
			if (o.getNature() == Nature.CIM && !(o.isUsedAsTypeForAttributes() || o.isVersionClass()
					|| o.isNamespaceClass())) {
				if (o.getAssociations().isEmpty() && o.getSubclasses().isEmpty()
						&& o.getSuperclasses().isEmpty()) {
					issues.add(o, createIssue(o));
				}
			}
		}
	}

	public static class ClassesWithUnallowedTagNames
			extends UmlObjectsWithUnallowedTagNames<UmlClass> {
		public ClassesWithUnallowedTagNames() {
			super(ClassValidator._logger, "class");
		}

		@Override
		public EnumSet<Nature> getApplicability() {
			return EnumSet.allOf(Nature.class);
		}
	}

	public static class Iec61850ClassesWithInvalidConstraints extends AbstractRule
			implements SimpleRule<UmlClass> {
		private static final String HYPO = "IEC61850 classes that have constraints not defined as "
				+ "literals";
		private static final String HOWTO = "fix constraint(s) name OR add new presence condition";

		public Iec61850ClassesWithInvalidConstraints() {
			super(ClassValidator._logger, HYPO, HOWTO);
		}

		@Override
		public EnumSet<Nature> getApplicability() {
			return EnumSet.of(Nature.IEC61850);
		}

		@Override
		public void validate(UmlClass o, ModelIssues issues) {
			if (!getApplicability().contains(o.getNature())) {
				return;
			}
			List<UmlConstraint> ccs = new ArrayList<>();
			for (UmlConstraint cc : o.getConstraints().values()) {
				PresenceCondition pc = cc.getPresenceCondition();
				if (pc != null && pc.getDefinitionLiteral() == null) {
					ccs.add(cc);
				}
			}
			if (!ccs.isEmpty()) {
				String evidence = "offending constraints = "
						+ AbstractUmlObject.collectNames(ccs).toString();
				issues.add(o, createIssue(o, evidence));
			}
		}
	}

	public static class Iec61850LNClassesWithSuperfluousConstraints extends AbstractRule
			implements SimpleRule<UmlClass> {
		private static final String HYPO = "IEC61850 classes that have constraints with siblings "
				+ "that do not exist";
		private static final String HOWTO = "fix constraint(s) argument OR add DO matching the "
				+ "argument OR remove constraint";

		public Iec61850LNClassesWithSuperfluousConstraints() {
			super(ClassValidator._logger, HYPO, HOWTO);
		}

		@Override
		public EnumSet<Nature> getApplicability() {
			return EnumSet.of(Nature.IEC61850);
		}

		@Override
		public void validate(UmlClass o, ModelIssues issues) {
			if (!getApplicability().contains(o.getNature())) {
				return;
			}
			Set<String> attrNamesFromClass = new HashSet<String>(
					AbstractUmlObject.collectNames(o.getAttributes()));
			Set<String> inhAttrNamesFromClass = new HashSet<String>(
					AbstractUmlObject.collectNames(o.getInheritedAttributes()));

			Map<UmlConstraint, List<String>> ccs = new LinkedHashMap<>();
			for (UmlConstraint cc : o.getConstraints().values()) {
				List<String> attrNamesFromConstraint = cc.getAttrNames();
				for (String attrName : attrNamesFromConstraint) {
					if (!attrNamesFromClass.contains(attrName)
							&& !inhAttrNamesFromClass.contains(attrName)) {
						if (!ccs.containsKey(cc)) {
							ccs.put(cc, new ArrayList<String>());
						}
						ccs.get(cc).add(attrName);
					}
				}
			}
			if (!ccs.isEmpty()) {
				StringBuilder evidence = new StringBuilder();
				for (Map.Entry<UmlConstraint, List<String>> diagEntry : ccs.entrySet()) {
					String ccName = diagEntry.getKey().getName();
					String DOnames = Util.concatCharSeparatedTokens(",", diagEntry.getValue());
					evidence.append(
							"[constraint '" + ccName + "' for inexisting '" + DOnames + "']");
				}
				issues.add(o, createIssue(o, evidence.toString()));
			}
		}
	}

	public static class Iec61850ClassesWithMissingCondIDTextInConstraints extends AbstractRule
			implements SimpleRule<UmlClass> {
		private static final String HYPO = "IEC61850 classes that have constraints with 'condID' "
				+ "argument, but no text associated with it";
		private static final String HOWTO = "add text to constraint(s) note OR use presence "
				+ "condition that does not require 'condID'";

		public Iec61850ClassesWithMissingCondIDTextInConstraints() {
			super(ClassValidator._logger, HYPO, HOWTO);
		}

		@Override
		public EnumSet<Nature> getApplicability() {
			return EnumSet.of(Nature.IEC61850);
		}

		@Override
		public void validate(UmlClass o, ModelIssues issues) {
			List<UmlConstraint> ccs = new ArrayList<>();
			for (UmlConstraint cc : o.getConstraints().values()) {
				PresenceCondition pc = cc.getPresenceCondition();
				if (pc != null && pc.isWithCondID() && !Util.hasContent(pc.getText())) {
					ccs.add(cc);
				}
			}
			if (!ccs.isEmpty()) {
				String evidence = "offending constraints = "
						+ AbstractUmlObject.collectNames(ccs).toString();
				issues.add(o, createIssue(o, evidence));
			}
		}
	}

	public static class CimDatatypeClassesWithInvalidAttributes extends AbstractRule
			implements SimpleRule<UmlClass> {
		private static final String HYPO = "CIM datatype classes that should have at least "
				+ "attributes with type kinds " + UmlClass.getCimDataTypeMinSpec();

		private static final String HOWTO = "remove stereotype OR add missing attributes";

		public CimDatatypeClassesWithInvalidAttributes() {
			super(ClassValidator._logger, HYPO, HOWTO);
		}

		@Override
		public EnumSet<Nature> getApplicability() {
			return EnumSet.of(Nature.CIM);
		}

		@Override
		public void validate(UmlClass o, ModelIssues issues) {
			if (!o.isDatatype()) {
				return;
			}

			Map<String, UmlKind> allAttrNames = collectAttributeNamesWithTypeKind(o);

			// attr name as key, attribute's type's kind as value (if null, attr. is missing)
			Map<String, UmlKind> invalids = new LinkedHashMap<String, UmlKind>();
			for (Map.Entry<String, UmlKind> entry : UmlClass.getCimDataTypeMinSpec().entrySet()) {
				String specAttrName = entry.getKey();
				UmlKind specAttrKind = entry.getValue();

				UmlKind actualKind = allAttrNames.get(specAttrName);

				if (actualKind == null) {
					invalids.put(specAttrName, null);
				} else if (!actualKind.equals(specAttrKind)) {
					invalids.put(specAttrName, actualKind);
				}
			}
			if (!invalids.isEmpty()) {
				StringBuilder evidence = new StringBuilder();
				for (Map.Entry<String, UmlKind> diagEntry : invalids.entrySet()) {
					String attrName = diagEntry.getKey();
					UmlKind attrTypeKind = diagEntry.getValue();
					evidence.append((attrTypeKind == null) ? ("[missing '" + attrName + "'] ")
							: ("[wrong kind '" + attrTypeKind + "' for type of '" + attrName
									+ "']"));
				}
				issues.add(o, createIssue(o, evidence.toString()));
			}
		}

		private Map<String, UmlKind> collectAttributeNamesWithTypeKind(UmlClass clazz) {
			Map<String, UmlKind> result = new LinkedHashMap<>();
			for (UmlAttribute attr : clazz.getAttributes()) {
				result.put(attr.getName(), attr.getType().getKind());
			}
			return result;
		}
	}

	public static class ClassesMissingDoc extends UmlObjectsMissingDoc<UmlClass> {
		public ClassesMissingDoc() {
			super(ClassValidator._logger, "classes");
		}

		@Override
		public EnumSet<Nature> getApplicability() {
			return EnumSet.allOf(Nature.class);
		}

		@Override
		protected boolean skipValidation(UmlClass o) {
			return o.getNature() == Nature.IEC61850 && o.isAbbreviationEnumeration();
		}
	}

	public static class ClassesWithBadDocStart extends UmlObjectsWithBadDocStart<UmlClass> {
		public ClassesWithBadDocStart() {
			super(ClassValidator._logger, "classes");
		}

		@Override
		public EnumSet<Nature> getApplicability() {
			return EnumSet.allOf(Nature.class);
		}
	}

	public static class ClassesWithBadDocEnd extends UmlObjectsWithBadDocEnd<UmlClass> {
		public ClassesWithBadDocEnd() {
			super(ClassValidator._logger, "classes");
		}

		@Override
		public EnumSet<Nature> getApplicability() {
			return EnumSet.allOf(Nature.class);
		}
	}

	public static class ClassesWithBadCharacterInName
			extends UmlObjectsWithBadCharacterInName<UmlClass> {
		public ClassesWithBadCharacterInName() {
			super(ClassValidator._logger, "classes");
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

	public static class CimClassesNameStartingWithLowerCase extends AbstractRule
			implements SimpleRule<UmlClass> {
		private static final String HYPO = "CIM classes whose name starts with a lower case letter";
		private static final String HOWTO = "rename class(es) to start with upper case letter";

		public CimClassesNameStartingWithLowerCase() {
			super(ClassValidator._logger, Level.ERROR, Severity.high, Category.namingRule, HYPO,
					HOWTO);
		}

		@Override
		public EnumSet<Nature> getApplicability() {
			return EnumSet.of(Nature.CIM);
		}

		@Override
		public void validate(UmlClass o, ModelIssues issues) {
			if (o.getNature() == Nature.CIM) {
				char firstLetter = o.getName().charAt(0);
				if (Character.isLowerCase(firstLetter)) {
					issues.add(o, createIssue(o));
				}
			}
		}
	}

	public static class CimClassesNameShouldBeSingular extends AbstractRule
			implements SimpleRule<UmlClass> {
		private static final String HYPO = "CIM classes that should be singular";
		private static final String HOWTO = "rename class to singular";

		public CimClassesNameShouldBeSingular() {
			super(ClassValidator._logger, Level.ERROR, Severity.high, Category.namingRule, HYPO,
					HOWTO);
		}

		@Override
		public EnumSet<Nature> getApplicability() {
			return EnumSet.of(Nature.CIM);
		}

		@Override
		public void validate(UmlClass o, ModelIssues issues) {
			if (o.getNature() == Nature.CIM && Util.looksLikePlural(o.getName())) {
				issues.add(o, createIssue(o));
			}
		}
	}

	public static class Iec61850LNClassesInWrongGroup extends AbstractRule
			implements SimpleRule<UmlClass> {
		private static final String HYPO = "IEC61850 concrete LN classes that, according to the "
				+ "first letter in the name, should be in another LN group";
		private static final String HOWTO = "rename the LN OR move it to corresponding LN group";

		public Iec61850LNClassesInWrongGroup() {
			super(ClassValidator._logger, Level.ERROR, Severity.medium, Category.namingRule, HYPO,
					HOWTO);
		}

		@Override
		public EnumSet<Nature> getApplicability() {
			return EnumSet.of(Nature.IEC61850);
		}

		@Override
		public void validate(UmlClass o, ModelIssues issues) {
			if (o.is74LN() && !o.isAbstract()) {
				char firstChar = o.getName().charAt(0);
				String expectedPackageName = UML.PREF_LNGroup + firstChar;
				if (!o.getContainingPackage().getName().equals(expectedPackageName)) {
					String evidence = "expected to be in package '" + expectedPackageName + "'";
					issues.add(o, createIssue(o, evidence));
				}
			}
		}
	}

	public static class Iec61850LNClassesMalformedName extends AbstractRule
			implements SimpleRule<UmlClass> {
		private static final String HYPO = "IEC61850 concrete LN classes with name different than "
				+ "4 upper case letters";
		private static final String HOWTO = "rename the LN to 4 upper case letters";

		public Iec61850LNClassesMalformedName() {
			super(ClassValidator._logger, Level.ERROR, Severity.high, Category.namingRule, HYPO,
					HOWTO);
		}

		@Override
		public EnumSet<Nature> getApplicability() {
			return EnumSet.of(Nature.IEC61850);
		}

		@Override
		public void validate(UmlClass o, ModelIssues issues) {
			if (o.is74LN() && !o.isAbstract()) {
				String name = o.getName();
				if (name.length() > 4) {
					issues.add(o, createIssue(o));
					return;
				}

				for (int i = 0; i < name.length(); i++) {
					if (!Character.isUpperCase(name.charAt(i))) {
						if (!name.equals(UML.LLN0)) {
							issues.add(o, createIssue(o));
							break;
						}
					}
				}
			}
		}
	}

	public static class EnumClassesWithSomeCodesMissing extends AbstractRule
			implements SimpleRule<UmlClass> {
		private static final String HYPO = "enum classes that have some codes, but not for all literals";
		private static final String HOWTO = "add missing code(s); alternatively, remove all codes";

		private static final String NO_CODE = "";

		public EnumClassesWithSomeCodesMissing() {
			super(ClassValidator._logger, HYPO, HOWTO);
		}

		@Override
		public EnumSet<Nature> getApplicability() {
			return EnumSet.allOf(Nature.class);
		}

		@Override
		public void validate(UmlClass o, ModelIssues issues) {
			if (!o.isEnumeratedType()) {
				return;
			}
			Map<String, List<UmlAttribute>> literalsPerCode = o.findAttributesPerInitialValue();
			if (hasNoCodes(literalsPerCode) || hasOnlyNonEmptyCodes(literalsPerCode)) {
				return;
			}

			List<UmlAttribute> attrs = literalsPerCode.get(NO_CODE);
			List<String> litNames = AbstractUmlObject.collectNames(attrs);

			String evidence = "literals missing code = " + litNames.toString();
			issues.add(o, createIssue(o, evidence));
		}

		private static boolean hasOnlyNonEmptyCodes(
				Map<String, List<UmlAttribute>> literalsPerCode) {
			return !literalsPerCode.containsKey(NO_CODE);
		}

		private static boolean hasNoCodes(Map<String, List<UmlAttribute>> literalsPerCode) {
			return literalsPerCode.containsKey(NO_CODE) && literalsPerCode.size() == 1;
		}
	}

	public static class EnumClassesWithDuplicateCodes extends AbstractRule
			implements SimpleRule<UmlClass> {
		private static final String HYPO = "enum classes that have duplicate codes for some literals";
		private static final String HOWTO = "change codes so as to have them unique";

		private static final String NO_CODE = "";

		public EnumClassesWithDuplicateCodes() {
			super(ClassValidator._logger, HYPO, HOWTO);
		}

		@Override
		public EnumSet<Nature> getApplicability() {
			return EnumSet.allOf(Nature.class);
		}

		@Override
		public void validate(UmlClass o, ModelIssues issues) {
			if (!o.isEnumeratedType()) {
				return;
			}
			Map<String, List<UmlAttribute>> literalsPerCode = o.findAttributesPerInitialValue();
			if (!hasOnlyNonEmptyCodes(literalsPerCode)) {
				return;
			}

			MapOfLists<String, String> fmtDups = new MapOfLists<>();
			for (Entry<String, List<UmlAttribute>> entry : literalsPerCode.entrySet()) {
				String code = entry.getKey();
				List<UmlAttribute> attrs = entry.getValue();
				if (attrs.size() == 1 || code.trim().isEmpty()) {
					continue;
				}
				List<String> dupNames = AbstractUmlObject.collectNames(attrs);
				fmtDups.addValue(code, dupNames.toString());
			}
			if (!fmtDups.isEmpty()) {
				String allCodesUsed = o.findInitialValuesOrdered().toString();
				String evidence = "duplicate codes = " + fmtDups.toString() + "; codes in use = "
						+ allCodesUsed + ", so pick some unused code to fix the problem";
				issues.add(o, createIssue(o, evidence));
			}
		}

		private static boolean hasOnlyNonEmptyCodes(
				Map<String, List<UmlAttribute>> literalsPerCode) {
			return !literalsPerCode.containsKey(NO_CODE);
		}
	}

	// ==================== cross rules

	public static class ClassesWithSameName extends AbstractRule implements CrossRule<UmlClass> {
		private static final String HYPO = "classes that have non-unique name";
		private static final String HOWTO = "rename classes to have unique names within model";

		private final Collection<UmlClass> _allClasses;

		public ClassesWithSameName(Collection<UmlClass> allClasses) {
			super(ClassValidator._logger, HYPO, HOWTO);
			_allClasses = allClasses;
		}

		@Override
		public EnumSet<Nature> getApplicability() {
			return EnumSet.allOf(Nature.class);
		}

		@Override
		public Collection<UmlClass> getObjsToTestAgainst() {
			return _allClasses;
		}

		@Override
		public void validate(List<UmlClass> classes, ModelIssues issues) {
			MapOfCollections<String, UmlClass> allDuplicatesPerName = AbstractUmlObject
					.collectDuplicateNames(_allClasses);
			MapOfCollections<String, UmlClass> scopedDuplicatesPerName = AbstractUmlObject
					.collectDuplicateNames(classes);

			int group = 1;
			for (String name : scopedDuplicatesPerName.keys()) {
				Collection<UmlClass> allDups = allDuplicatesPerName.subCollection(name);
				String evidence = "all with the same name = "
						+ AbstractUmlObject.collectQNames(allDups, true).toString();

				Collection<UmlClass> scopedDups = scopedDuplicatesPerName.subCollection(name);
				for (UmlClass o : scopedDups) {
					ModelIssue issue = createIssue(o, evidence, o.toShortString(false, true),
							String.valueOf(group));
					issues.add(o, issue);
				}
				++group;
			}
		}
	}

	public static class CimClassesNeverUsedAsTypeForAttribute extends AbstractRule
			implements CrossRule<UmlClass> {
		private static final String HYPO = "CIM classes never used as type for attributes";
		private static final String HOWTO = "if also not used in the larger scope: move class(es) "
				+ "to an informative package (if you anticipate future use) OR simply remove";
		private final List<UmlAttribute> _scopedWrtUmlAttributes;

		public CimClassesNeverUsedAsTypeForAttribute(List<UmlAttribute> scopedWrtUmlAttributes) {
			super(ClassValidator._logger, HYPO, HOWTO);
			_scopedWrtUmlAttributes = scopedWrtUmlAttributes;
		}

		@Override
		public Collection<? extends UmlObject> getObjsToTestAgainst() {
			return _scopedWrtUmlAttributes;
		}

		@Override
		public EnumSet<Nature> getApplicability() {
			return EnumSet.of(Nature.CIM);
		}

		@Override
		public void validate(List<UmlClass> classes, ModelIssues issues) {
			Set<UmlClass> usedClasses = new LinkedHashSet<UmlClass>();
			for (UmlAttribute attr : _scopedWrtUmlAttributes) {
				if (!attr.isLiteral() && !attr.getType().isNullClass()
						&& attr.getType().getNature() == Nature.CIM) {
					usedClasses.add(attr.getType());
				}
			}

			Set<UmlClass> allClasses = new LinkedHashSet<UmlClass>(classes);
			allClasses.removeAll(usedClasses);
			Set<UmlClass> nonUsedClasses = new LinkedHashSet<UmlClass>(allClasses);

			for (UmlClass o : nonUsedClasses) {
				if (o.isUsedAsTypeForAttributes() && o.getNature() == Nature.CIM) {
					issues.add(o, createIssue(o));
				}
			}
		}
	}
}
