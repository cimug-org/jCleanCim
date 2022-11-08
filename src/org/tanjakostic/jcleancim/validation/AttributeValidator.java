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
import org.tanjakostic.jcleancim.model.NameDecomposition;
import org.tanjakostic.jcleancim.model.PresenceCondition;
import org.tanjakostic.jcleancim.model.UML;
import org.tanjakostic.jcleancim.model.UmlAttribute;
import org.tanjakostic.jcleancim.model.UmlClass;
import org.tanjakostic.jcleancim.model.UmlConstraint;
import org.tanjakostic.jcleancim.model.UmlObject;
import org.tanjakostic.jcleancim.model.UmlPackage;
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
 * Validates attributes.
 *
 * @author tatjana.kostic@ieee.org
 * @version $Id: AttributeValidator.java 27 2019-11-23 16:29:38Z dev978 $
 */
public class AttributeValidator extends AbstractValidator<UmlAttribute> {
	private static final Logger _logger = Logger.getLogger(AttributeValidator.class.getName());

	/** Diagnostic common to several rules. */
	private static final String EA_ATTRNULL_HOWTO = "locate class in the model from attribute's"
			+ " 'Type' combo-box";

	private final List<UmlAttribute> _scopedUmlObjects;

	AttributeValidator(Config cfg, Collection<UmlAttribute> allAttributes, ModelIssues issues) {
		super(cfg, allAttributes.size(), "attributes", issues);

		_scopedUmlObjects = AbstractUmlObject.collectForScope(allAttributes,
				cfg.getValidationScope());

		addSimpleRule(new EnumLiteralsWithSuperfluousType());
		addSimpleRule(new EnumLiteralsWithoutEnumStereotype());
		addSimpleRule(new AttributesWithInvalidMultiplicity());
		addSimpleRule(new CimAttributesThatShouldBeOptional());
		addSimpleRule(new AttributesWithInvalidTypeNull());
		addSimpleRule(new AttributesWithInvalidTypeString());
		addSimpleRule(new AttributesWithTypeIdMismatch());
		addSimpleRule(new CimAttributesThatShouldBePublic());
		addSimpleRule(new AttributesThatAreStaticButNotConst());
		addSimpleRule(new CimAttributesThatAreNotStaticNonConstWithInitVal());
		addSimpleRule(new AttributesThatAreConstNonStatic());
		addSimpleRule(new AttributesWithUnallowedStereotype());
		addSimpleRule(new AttributesThatAreEnumsInNonEnumeratedClass());
		addSimpleRule(new CimAttributesThatShouldBeReplacedWithAssociation());
		addSimpleRule(new AttributesWhoseTypeIsInformative());
		addSimpleRule(new AttributesWithUnallowedTagNames());
		addSimpleRule(new Iec61850AttributesWithInexistingSibling());
		addSimpleRule(new CimAttributesWithFlagInName());
		addSimpleRule(new AttributesMissingDoc());
		addSimpleRule(new AttributesWithBadDocStart());
		addSimpleRule(new AttributesWithBadDocEnd());
		addSimpleRule(new CimAttributesWithBadCharacterInName());
		addSimpleRule(new Iec61850AttributesWithBadCharacterInName());
		addSimpleRule(new Iec61850DOAttributesWithTooLongName());
		addSimpleRule(new Iec61850FCDAAttributesWithMissingConstraint());
		addSimpleRule(new AttributesWithInexistingEnumLiteralAsInitValue());
		addSimpleRule(new Iec61850DOAttributesWithNameMissingAbbreviation());
		addSimpleRule(new CimAttributesNameStartingWithUpperCase());
		addSimpleRule(new CimAttributesNameShouldBeSingular());
		addSimpleRule(new CimAttributesNameShouldNotStartWithClassName());
		addSimpleRule(new Iec61850AbbreviationLiteralsNameStartingWithLowerCase());
		addSimpleRule(new Iec61850DOAttributesNameStartingWithLowerCase());
		addSimpleRule(new AttributesWithTypeFromUnallowedOwner());

		Collection<UmlAttribute> allAbbrLiterals = UmlAttribute
				.findAbbreviationLiterals(allAttributes);
		addCrossRule(new Iec61850DOAbbreviationLiteralsDuplicateName(allAbbrLiterals));
		addCrossRule(new Iec61850DOAbbreviationLiteralsDuplicateDescription(allAbbrLiterals));
		addCrossRule(new Iec61850DOAbbreviationLiteralsNeverUsedInDOName(allAbbrLiterals));

		addCrossRule(new Iec61850DOAttributesWithSameNameDifferentType(allAttributes));

		Collection<UmlAttribute> allPresCondLiterals = UmlAttribute
				.findPresenceConditionLiterals(allAttributes);
		addCrossRule(new Iec61850ConditionLiteralsNeverUsedAsConstraints(allPresCondLiterals));
	}

	@Override
	public boolean enabled() {
		return getCfg().isValidationAttributesOn();
	}

	@Override
	public List<UmlAttribute> getScopedUmlObjects() {
		return _scopedUmlObjects;
	}

	private static boolean skipValidationDocRelated(UmlAttribute a) {
		UmlClass containingClass = a.getContainingClass();
		if (a.getNature() == Nature.CIM) {
			return containingClass.isDatatype();
		}

		// now IEC61850:
		if (a.isInformative() || !a.isPublic()) {
			return true;
		}
		if (a.isLiteral() && !(containingClass.isAbbreviationEnumeration()
				|| containingClass.isConditionEnumeration())) {
			return true;
		}
		return false;
	}

	// ==================== simple rules

	public static class EnumLiteralsWithSuperfluousType extends AbstractRule
			implements SimpleRule<UmlAttribute> {
		private static final String HYPO = "enum literals that should have no type";
		private static final String HOWTO = "remove type";

		public EnumLiteralsWithSuperfluousType() {
			super(AttributeValidator._logger, HYPO, HOWTO);
		}

		@Override
		public EnumSet<Nature> getApplicability() {
			return EnumSet.allOf(Nature.class);
		}

		@Override
		public void validate(UmlAttribute o, ModelIssues issues) {
			if (o.isLiteral() && o.hasSuperfluousType()) {
				String evidence = "type = " + o.getEaTypeName();
				issues.add(o, createIssue(o, evidence));
			}
		}
	}

	public static class EnumLiteralsWithoutEnumStereotype extends AbstractRule
			implements SimpleRule<UmlAttribute> {
		private static final String HYPO = "enum literals that are missing <<" + UmlStereotype.ENUM
				+ ">> stereotype";
		private static final String HOWTO = "add <<" + UmlStereotype.ENUM + ">> stereotype";

		public EnumLiteralsWithoutEnumStereotype() {
			super(AttributeValidator._logger, HYPO, HOWTO);
		}

		@Override
		public EnumSet<Nature> getApplicability() {
			return EnumSet.allOf(Nature.class);
		}

		@Override
		public void validate(UmlAttribute o, ModelIssues issues) {
			if (o.isLiteral() && !o.getStereotype().contains(UmlStereotype.ENUM)) {
				String evidence = "stereotype = " + o.getStereotype().toString();
				issues.add(o, createIssue(o, evidence));
			}
		}
	}

	public static class AttributesWithInvalidMultiplicity extends AbstractRule
			implements SimpleRule<UmlAttribute> {
		private static final String HYPO = "attributes that use custom multiplicity format";
		private static final String HOWTO = "change bounds to [0..1]";

		public AttributesWithInvalidMultiplicity() {
			super(AttributeValidator._logger, HYPO, HOWTO);
		}

		@Override
		public EnumSet<Nature> getApplicability() {
			return EnumSet.allOf(Nature.class);
		}

		@Override
		public void validate(UmlAttribute o, ModelIssues issues) {
			if (o.isLiteral() || o.getContainingClass().isEnumeration()) {
				return;
			}
			if (o.getMultiplicity().isCustom()) {
				String evidence = "multiplicity = " + o.getMultiplicity().toString();
				issues.add(o, createIssue(o, evidence));
			}
		}
	}

	public static class CimAttributesThatShouldBeOptional extends AbstractRule
			implements SimpleRule<UmlAttribute> {
		private static final String HYPO = "CIM attributes that should be optional";
		private static final String HOWTO = "set multiplicity lower bound to 0";

		public CimAttributesThatShouldBeOptional() {
			super(AttributeValidator._logger, HYPO, HOWTO);
		}

		@Override
		public EnumSet<Nature> getApplicability() {
			return EnumSet.of(Nature.CIM);
		}

		@Override
		public void validate(UmlAttribute o, ModelIssues issues) {
			if (o.isLiteral() || o.getNature() != Nature.CIM) {
				return;
			}
			if (!o.isOptional()) {
				String evidence = "multiplicity = " + o.getMultiplicity().toString();
				issues.add(o, createIssue(o, evidence));
			}
		}
	}

	public static class AttributesWithInvalidTypeNull extends AbstractRule
			implements SimpleRule<UmlAttribute> {
		private static final String HYPO = "attributes that have as type class not in the model, or"
				+ " the class that may be in the model, but EA interprets the classifierID of the"
				+ " attribute as 0";

		public AttributesWithInvalidTypeNull() {
			super(AttributeValidator._logger, HYPO, EA_ATTRNULL_HOWTO);
		}

		@Override
		public EnumSet<Nature> getApplicability() {
			return EnumSet.allOf(Nature.class);
		}

		@Override
		public void validate(UmlAttribute o, ModelIssues issues) {
			if (o.isLiteral() || o.getContainingClass().isEnumeration()) {
				return;
			}
			if (o.getType().isNullClass() && o.getEaTypeId() == 0) {
				String evidence = "EA info for attr type (" + o.getEaTypeInfo() + ")";
				issues.add(o, createIssue(o, evidence));
			}
		}
	}

	public static class AttributesWithInvalidTypeString extends AbstractRule
			implements SimpleRule<UmlAttribute> {
		private static final String HYPO = "attributes that have as type class from the model,"
				+ " but retained in EA as string";

		public AttributesWithInvalidTypeString() {
			super(AttributeValidator._logger, HYPO, EA_ATTRNULL_HOWTO);
		}

		@Override
		public EnumSet<Nature> getApplicability() {
			return EnumSet.allOf(Nature.class);
		}

		@Override
		public void validate(UmlAttribute o, ModelIssues issues) {
			if (o.isLiteral() || o.getContainingClass().isEnumeration()) {
				return;
			}
			if (o.getType().isNullClass() && o.getEaTypeId() != 0) {
				String evidence = "EA info for attr type (" + o.getEaTypeInfo() + ")";
				issues.add(o, createIssue(o, evidence));
			}
		}
	}

	public static class AttributesWithTypeIdMismatch extends AbstractRule
			implements SimpleRule<UmlAttribute> {
		private static final String HYPO = "attributes that have a type ID with the class from the model,"
				+ " but the displayed string (type name) is not the name of that class";

		public AttributesWithTypeIdMismatch() {
			super(AttributeValidator._logger, HYPO, EA_ATTRNULL_HOWTO);
		}

		@Override
		public EnumSet<Nature> getApplicability() {
			return EnumSet.allOf(Nature.class);
		}

		@Override
		public void validate(UmlAttribute o, ModelIssues issues) {
			if (o.isLiteral() || o.getContainingClass().isEnumeration()) {
				return;
			}
			if (!o.getType().isNullClass()) {
				String typeNameFromId = o.getType().getName(); // from EA: attr.ClassifierID
				String typeNameFromString = o.getEaTypeName(); // from EA: attr.Type
				if (!typeNameFromId.equals(typeNameFromString)) {
					String evidence = "EA info for attr type (" + o.getEaTypeInfo()
							+ "); ID-based info for attr type ("
							+ o.getType().toShortString(true, true) + ")";
					issues.add(o, createIssue(o, evidence));
				}
			}
		}
	}

	public static class CimAttributesThatShouldBePublic extends AbstractRule
			implements SimpleRule<UmlAttribute> {
		private static final String HYPO = "CIM attributes that should be public";
		private static final String HOWTO = "CIM attributes that should be public";

		public CimAttributesThatShouldBePublic() {
			super(AttributeValidator._logger, HYPO, HOWTO);
		}

		@Override
		public EnumSet<Nature> getApplicability() {
			return EnumSet.of(Nature.CIM);
		}

		@Override
		public void validate(UmlAttribute o, ModelIssues issues) {
			if (o.isLiteral() || o.getNature() != Nature.CIM) {
				return;
			}
			if (!o.isPublic()) {
				issues.add(o, createIssue(o));
			}
		}
	}

	public static class AttributesThatAreStaticButNotConst extends AbstractRule
			implements SimpleRule<UmlAttribute> {
		/**
		 * Package SCL describes an XSD, where it is ok to have default initial value, so we exclude
		 * it from that validation.
		 */
		public static final String EXCLUDE_VALID_INITVAL_PCKG = "SCL";

		private static final String HYPO = "attributes that are static but not const (i.e., undefined value"
				+ " for all instances of the class)";
		private static final String HOWTO = "make attribute const";

		public AttributesThatAreStaticButNotConst() {
			super(AttributeValidator._logger, HYPO, HOWTO);
		}

		@Override
		public EnumSet<Nature> getApplicability() {
			return EnumSet.allOf(Nature.class);
		}

		@Override
		public void validate(UmlAttribute o, ModelIssues issues) {
			if (!o.isLiteral()) {
				UmlPackage attrPackage = o.getContainingClass().getContainingPackage();
				if (!attrPackage.isInOrUnderPackage(EXCLUDE_VALID_INITVAL_PCKG)) {
					if (o.isStatic() && !o.isConst()) {
						issues.add(o, createIssue(o));
					}
				}
			}
		}
	}

	public static class CimAttributesThatAreNotStaticNonConstWithInitVal extends AbstractRule
			implements SimpleRule<UmlAttribute> {

		private static final String HYPO = "CIM attributes that are not 'static const' but have init"
				+ " value";
		private static final String HOWTO = "remove init value OR make the attribute static const";

		public CimAttributesThatAreNotStaticNonConstWithInitVal() {
			super(AttributeValidator._logger, HYPO, HOWTO);
		}

		@Override
		public EnumSet<Nature> getApplicability() {
			return EnumSet.of(Nature.CIM);
		}

		@Override
		public void validate(UmlAttribute o, ModelIssues issues) {
			if (o.getNature() == Nature.CIM && !o.isLiteral()) {
				if (!o.isStatic() && !o.isConst() && !o.getInitValue().isEmpty()) {
					issues.add(o, createIssue(o));
				}
			}
		}
	}

	public static class AttributesThatAreConstNonStatic extends AbstractRule
			implements SimpleRule<UmlAttribute> {
		private static final String HYPO = "attributes that are const but not static (i.e., each instance "
				+ "of the class has same const value)";
		private static final String HOWTO = "remove const qualifier OR make attribute static";

		public AttributesThatAreConstNonStatic() {
			super(AttributeValidator._logger, HYPO, HOWTO);
		}

		@Override
		public EnumSet<Nature> getApplicability() {
			return EnumSet.allOf(Nature.class);
		}

		@Override
		public void validate(UmlAttribute o, ModelIssues issues) {
			if (!o.isLiteral() && !o.isStatic() && o.isConst()) {
				issues.add(o, createIssue(o));
			}
		}
	}

	public static class AttributesWithUnallowedStereotype
			extends UmlObjectsWithUnallowedStereotype<UmlAttribute> {
		// doing this way to allow later on adding custom
		private static final Map<Nature, Set<String>> ALLOWED_STEREOS = new TreeMap<>(
				UmlStereotype.getAttributeBuiltIns());

		public AttributesWithUnallowedStereotype() {
			super(AttributeValidator._logger, "attributes", ALLOWED_STEREOS);
		}

		@Override
		public EnumSet<Nature> getApplicability() {
			return EnumSet.allOf(Nature.class);
		}
	}

	public static class AttributesThatAreEnumsInNonEnumeratedClass extends AbstractRule
			implements SimpleRule<UmlAttribute> {
		private static final String HYPO = "attributes that have enum stereotype, but belong to a "
				+ "class that is not an enumerated type";
		private static final String HOWTO = "remove enum stereotype";

		public AttributesThatAreEnumsInNonEnumeratedClass() {
			super(AttributeValidator._logger, HYPO, HOWTO);
		}

		@Override
		public EnumSet<Nature> getApplicability() {
			return EnumSet.allOf(Nature.class);
		}

		@Override
		public void validate(UmlAttribute o, ModelIssues issues) {
			if (!o.isLiteral() && o.getStereotype().contains(UmlStereotype.ENUM)
					&& !o.getContainingClass().isEnumeratedType()) {
				issues.add(o, createIssue(o));
			}
		}
	}

	public static class CimAttributesThatShouldBeReplacedWithAssociation extends AbstractRule
			implements SimpleRule<UmlAttribute> {
		private static final String HYPO = "CIM attributes whose type is wrongly used as type for "
				+ "attribute";
		private static final String HOWTO = "replace attribute with association, or change type for"
				+ " attribute";

		public CimAttributesThatShouldBeReplacedWithAssociation() {
			super(AttributeValidator._logger, HYPO, HOWTO);
		}

		@Override
		public EnumSet<Nature> getApplicability() {
			return EnumSet.of(Nature.CIM);
		}

		@Override
		public void validate(UmlAttribute o, ModelIssues issues) {
			if (!o.isLiteral() && o.getNature() == Nature.CIM
					&& !o.getType().isUsedAsTypeForAttributes()) {
				issues.add(o, createIssue(o));
			}
		}
	}

	public static class AttributesWhoseTypeIsInformative extends AbstractRule
			implements SimpleRule<UmlAttribute> {
		private static final String HYPO = "normative attributes whose type is an informative class";
		private static final String HOWTO = "promote class to normative or make attribute informative";

		public AttributesWhoseTypeIsInformative() {
			super(AttributeValidator._logger, HYPO, HOWTO);
		}

		@Override
		public EnumSet<Nature> getApplicability() {
			return EnumSet.allOf(Nature.class);
		}

		@Override
		public void validate(UmlAttribute o, ModelIssues issues) {
			if (!o.isLiteral() && !o.isInformative() && o.getType().isInformative()) {
				issues.add(o, createIssue(o));
			}
		}
	}

	public static class AttributesWithUnallowedTagNames
			extends UmlObjectsWithUnallowedTagNames<UmlAttribute> {
		public AttributesWithUnallowedTagNames() {
			super(AttributeValidator._logger, "attribute");
		}

		@Override
		public EnumSet<Nature> getApplicability() {
			return EnumSet.allOf(Nature.class);
		}
	}

	public static class Iec61850AttributesWithInexistingSibling extends AbstractRule
			implements SimpleRule<UmlAttribute> {
		private static final String HYPO = "IEC61850-7-3 (FCDA) attributes that have "
				+ UML.TAG_moveAfter + " tag, but its value is the name "
				+ "of inexisting sibling attribute";
		private static final String HOWTO = "set value of " + UML.TAG_moveAfter + " tag, "
				+ "to an inexisting sibling attribute";

		public Iec61850AttributesWithInexistingSibling() {
			super(AttributeValidator._logger, HYPO, HOWTO);
		}

		@Override
		public EnumSet<Nature> getApplicability() {
			return EnumSet.of(Nature.IEC61850);
		}

		@Override
		public void validate(UmlAttribute o, ModelIssues issues) {
			if (o.getNature() == Nature.IEC61850
					&& o.getTaggedValues().containsKey(UML.TAG_moveAfter)
					&& o.getSiblingToMoveAfter() == null) {

				String evidence = "inexisting sibling '"
						+ o.getTaggedValues().get(UML.TAG_moveAfter) + "', use one of "
						+ AbstractUmlObject.collectNames(o.getAllSiblings()).toString();
				issues.add(o, createIssue(o, evidence));
			}
		}
	}

	public static class CimAttributesWithFlagInName extends AbstractRule
			implements SimpleRule<UmlAttribute> {
		public static final String FLAG = "flag";
		private static final String HYPO = "attributes that have word '" + FLAG + "'.";
		private static final String HOWTO = "rename (by removing word '" + FLAG
				+ "') and if they are not already Boolean, change their type";

		public CimAttributesWithFlagInName() {
			super(AttributeValidator._logger, Level.WARN, Severity.medium, Category.namingRule,
					HYPO, HOWTO);
		}

		@Override
		public EnumSet<Nature> getApplicability() {
			return EnumSet.of(Nature.CIM);
		}

		@Override
		public void validate(UmlAttribute o, ModelIssues issues) {
			if (o.getNature() == Nature.CIM
					&& o.getName().toLowerCase().contains(FLAG.toLowerCase())) {
				issues.add(o, createIssue(o));
			}
		}
	}

	public static class AttributesMissingDoc extends UmlObjectsMissingDoc<UmlAttribute> {
		public AttributesMissingDoc() {
			super(AttributeValidator._logger, "attributes");
		}

		@Override
		public EnumSet<Nature> getApplicability() {
			return EnumSet.allOf(Nature.class);
		}

		@Override
		protected boolean skipValidation(UmlAttribute o) {
			return skipValidationDocRelated(o);
		}
	}

	public static class AttributesWithBadDocStart extends UmlObjectsWithBadDocStart<UmlAttribute> {
		public AttributesWithBadDocStart() {
			super(AttributeValidator._logger, "attributes");
		}

		@Override
		public EnumSet<Nature> getApplicability() {
			return EnumSet.allOf(Nature.class);
		}

		@Override
		protected boolean skipValidation(UmlAttribute o) {
			return skipValidationDocRelated(o);
		}
	}

	public static class AttributesWithBadDocEnd extends UmlObjectsWithBadDocEnd<UmlAttribute> {
		public AttributesWithBadDocEnd() {
			super(AttributeValidator._logger, "attributes");
		}

		@Override
		public EnumSet<Nature> getApplicability() {
			return EnumSet.allOf(Nature.class);
		}

		@Override
		protected boolean skipValidation(UmlAttribute o) {
			if (o.getContainingClass().isAbbreviationEnumeration()) {
				return true;
			}
			return skipValidationDocRelated(o);
		}
	}

	public static class CimAttributesWithBadCharacterInName
			extends UmlObjectsWithBadCharacterInName<UmlAttribute> {
		public CimAttributesWithBadCharacterInName() {
			super(AttributeValidator._logger, "CIM attributes");
		}

		@Override
		public EnumSet<Nature> getApplicability() {
			return EnumSet.of(Nature.CIM);
		}

		@Override
		protected boolean skipValidation(UmlAttribute o) {
			// we skip any private attribute (we don't care for these; they are just some kind of
			// modelling support), and 61850 literals other than abbreviation literals, because they
			// may contain about anything...
			return o.getNature() != Nature.CIM;
		}

		@Override
		protected InvalidCharactersFinder getInvalidCharacterFinder(UmlObject o) {
			return InvalidCharactersFinder.STRICT;
		}
	}

	public static class Iec61850AttributesWithBadCharacterInName
			extends UmlObjectsWithBadCharacterInName<UmlAttribute> {
		public Iec61850AttributesWithBadCharacterInName() {
			super(AttributeValidator._logger, "IEC61850 attributes");
		}

		@Override
		public EnumSet<Nature> getApplicability() {
			return EnumSet.of(Nature.IEC61850);
		}

		@Override
		protected boolean skipValidation(UmlAttribute o) {
			// we skip any private attribute (we don't care for these; they are just some kind of
			// modelling support), and 61850 literals other than abbreviation literals, because they
			// may contain about anything...
			return o.getNature() != Nature.IEC61850 || !o.isPublic()
					|| (o.isLiteral() && !o.getContainingClass().isAbbreviationEnumeration());
		}

		@Override
		protected InvalidCharactersFinder getInvalidCharacterFinder(UmlObject o) {
			return InvalidCharactersFinder.STRICT_UNDERSCORE_DASH;
		}
	}

	public static class Iec61850DOAttributesWithTooLongName extends AbstractRule
			implements SimpleRule<UmlAttribute> {
		private static final String HYPO = "IEC61850-7-4xx attributes (DO) that have too long name";
		private static final String HOWTO = "rename the DO";

		public Iec61850DOAttributesWithTooLongName() {
			super(AttributeValidator._logger, Level.ERROR, Severity.high, Category.namingRule, HYPO,
					HOWTO);
		}

		@Override
		public EnumSet<Nature> getApplicability() {
			return EnumSet.of(Nature.IEC61850);
		}

		@Override
		public void validate(UmlAttribute o, ModelIssues issues) {
			if (o.isDO() && o.getName().length() > UmlAttribute.DO_MAX_LENGTH) {
				int overLength = o.getName().length() - UmlAttribute.DO_MAX_LENGTH;
				String evidence = " (+" + overLength + ")";
				issues.add(o, createIssue(o, evidence));
			}
		}
	}

	public static class Iec61850FCDAAttributesWithMissingConstraint extends AbstractRule
			implements SimpleRule<UmlAttribute> {
		private static final String HYPO = "IEC61850-7-3 (FCDA) multi-valued attributes "
				+ "that are missing " + UML.CONSTR_TXT_minIdx + " and/or " + UML.CONSTR_TXT_maxIdx
				+ " constraint(s)";
		private static final String HOWTO = "add the missing attribute constraint(s)";

		public Iec61850FCDAAttributesWithMissingConstraint() {
			super(AttributeValidator._logger, HYPO, HOWTO);
		}

		@Override
		public EnumSet<Nature> getApplicability() {
			return EnumSet.of(Nature.IEC61850);
		}

		@Override
		public void validate(UmlAttribute o, ModelIssues issues) {
			if (o.getNature() != Nature.IEC61850 || !o.getContainingClass().isAnyCDC()) {
				return;
			}
			Map<String, String> constraintValues = o.getConstraintValues();
			if (o.isMultivalued()) {
				String evidence = "";
				if (!constraintValues.containsKey(UML.CONSTR_TXT_minIdx)) {
					evidence += UML.CONSTR_TXT_minIdx + " ";
				}
				if (!constraintValues.containsKey(UML.CONSTR_TXT_maxIdx)) {
					if (!evidence.isEmpty()) {
						evidence += "and ";
					}
					evidence += UML.CONSTR_TXT_maxIdx + " ";
				}
				if (!evidence.isEmpty()) {
					evidence = "missing " + evidence;
					issues.add(o, createIssue(o, evidence));
				}
			}
		}
	}

	public static class AttributesWithInexistingEnumLiteralAsInitValue extends AbstractRule
			implements SimpleRule<UmlAttribute> {
		private static final String HYPO = "attributes (whose type is enumerated type) that use an"
				+ " inexisting enum literal as initial value";
		private static final String HOWTO = "change initial value to an existing enum literal";

		public AttributesWithInexistingEnumLiteralAsInitValue() {
			super(AttributeValidator._logger, Level.ERROR, Severity.high, Category.permissiveTool,
					HYPO, HOWTO);
		}

		@Override
		public EnumSet<Nature> getApplicability() {
			return EnumSet.allOf(Nature.class);
		}

		@Override
		public void validate(UmlAttribute o, ModelIssues issues) {
			String initValue = o.getInitValue();
			if (isOfEnumeratedType(o) && !initValue.isEmpty()) {
				Set<UmlAttribute> foundLiterals = o.getType().findAttributes(initValue);
				if (foundLiterals.isEmpty()) {
					String evidence = "valid literals = " + AbstractUmlObject
							.collectNames(o.getType().getAttributes()).toString();
					issues.add(o, createIssue(o, evidence));
				}
			}
		}

		private boolean isOfEnumeratedType(UmlAttribute attr) {
			return !attr.isLiteral() && attr.getType().isEnumeration();
		}
	}

	public static class Iec61850DOAttributesWithNameMissingAbbreviation extends AbstractRule
			implements SimpleRule<UmlAttribute> {
		private static final String HYPO = "(GROUP 1) IEC61850-7-4xx (DO) attributes with names "
				+ "containing undefined abbreviations";
		private static final String HOWTO = "add missing abbreviation OR rename the DO (and "
				+ "create a Tissue)";

		public Iec61850DOAttributesWithNameMissingAbbreviation() {
			super(AttributeValidator._logger, HYPO, HOWTO);
		}

		@Override
		public EnumSet<Nature> getApplicability() {
			return EnumSet.of(Nature.IEC61850);
		}

		@Override
		public void validate(UmlAttribute o, ModelIssues issues) {
			if (o.isDO()) {
				if (o.getName().startsWith(UML.PREF_DOName_Ieee)) {
					_logger.trace("Skipping abbreviation validation for: "
							+ o.toShortString(false, true));
					return;
				}
				Map<String, String> sortedAbbrTerms = o.getContainingClass().getModel()
						.getAbbreviatedTermsSortedPerDecreasingLength();
				NameDecomposition decomp = o.getNameDecomposition(sortedAbbrTerms);
				if (decomp != null && !decomp.isMatched()) {
					String evidence = decomp.getDecomposedTerms().toString();
					issues.add(o, createIssue(o, evidence));
				}
			}
		}
	}

	public static class CimAttributesNameStartingWithUpperCase extends AbstractRule
			implements SimpleRule<UmlAttribute> {
		private static final String HYPO = "CIM attributes whose name starts with an upper case "
				+ "letter";
		private static final String HOWTO = "rename attribute(s) to start with lower case letter";

		public CimAttributesNameStartingWithUpperCase() {
			super(AttributeValidator._logger, Level.ERROR, Severity.high, Category.namingRule, HYPO,
					HOWTO);
		}

		@Override
		public EnumSet<Nature> getApplicability() {
			return EnumSet.of(Nature.CIM);
		}

		@Override
		public void validate(UmlAttribute o, ModelIssues issues) {
			String containerName = o.getContainingClass().getName();
			if (o.getNature() == Nature.CIM) {
				char firstLetter = o.getName().charAt(0);
				if (Character.isUpperCase(firstLetter)
						&& !UML.IGNORE_CASE_ENUMS.contains(containerName)) {
					issues.add(o, createIssue(o));
				}
			}
		}
	}

	public static class CimAttributesNameShouldBeSingular extends AbstractRule
			implements SimpleRule<UmlAttribute> {
		private static final String HYPO = "CIM attributes that should be singular";
		private static final String HOWTO = "rename attribute to singular";

		public CimAttributesNameShouldBeSingular() {
			super(AttributeValidator._logger, Level.ERROR, Severity.high, Category.namingRule, HYPO,
					HOWTO);
		}

		@Override
		public EnumSet<Nature> getApplicability() {
			return EnumSet.of(Nature.CIM);
		}

		@Override
		public void validate(UmlAttribute o, ModelIssues issues) {
			if (o.getNature() == Nature.CIM && Util.looksLikePlural(o.getName())
					&& !UML.IGNORE_CASE_ENUMS.contains(o.getContainingClass().getName())) {
				issues.add(o, createIssue(o));
			}
		}
	}

	public static class CimAttributesNameShouldNotStartWithClassName extends AbstractRule
			implements SimpleRule<UmlAttribute> {
		private static final String HYPO = "CIM attributes whose name starts with the name of containing "
				+ "class";
		private static final String HOWTO = "this is considered redundant, rename attribute by removing "
				+ "the containing class name at the start";

		public CimAttributesNameShouldNotStartWithClassName() {
			super(AttributeValidator._logger, Level.WARN, Severity.medium, Category.namingRule,
					HYPO, HOWTO);
		}

		@Override
		public EnumSet<Nature> getApplicability() {
			return EnumSet.of(Nature.CIM);
		}

		@Override
		public void validate(UmlAttribute o, ModelIssues issues) {
			if (o.getNature() == Nature.CIM) {
				if (o.getName().toLowerCase()
						.startsWith(o.getContainingClass().getName().toLowerCase())) {
					issues.add(o, createIssue(o));
				}
			}
		}
	}

	public static class Iec61850AbbreviationLiteralsNameStartingWithLowerCase extends AbstractRule
			implements SimpleRule<UmlAttribute> {
		private static final String HYPO = "abbreviation terms starting with lower case letter; "
				+ "cannot be used as first term in DO name";
		private static final String HOWTO = "rename it to start with upper case letter";

		public Iec61850AbbreviationLiteralsNameStartingWithLowerCase() {
			super(AttributeValidator._logger, Level.ERROR, Severity.high, Category.namingRule, HYPO,
					HOWTO);
		}

		@Override
		public EnumSet<Nature> getApplicability() {
			return EnumSet.of(Nature.IEC61850);
		}

		@Override
		public void validate(UmlAttribute o, ModelIssues issues) {
			if (o.getContainingClass().isAbbreviationEnumeration()
					&& Character.isLowerCase(o.getName().charAt(0))) {
				issues.add(o, createIssue(o));
			}
		}
	}

	public static class Iec61850DOAttributesNameStartingWithLowerCase extends AbstractRule
			implements SimpleRule<UmlAttribute> {
		private static final String HYPO = "IEC61850-7-4xx (DO) attributes with name starting "
				+ "with lower case letter";
		private static final String HOWTO = "rename it to start with upper case letter";

		public Iec61850DOAttributesNameStartingWithLowerCase() {
			super(AttributeValidator._logger, Level.ERROR, Severity.high, Category.namingRule, HYPO,
					HOWTO);
		}

		@Override
		public EnumSet<Nature> getApplicability() {
			return EnumSet.of(Nature.IEC61850);
		}

		@Override
		public void validate(UmlAttribute o, ModelIssues issues) {
			if (o.isDO() && Character.isLowerCase(o.getName().charAt(0))) {
				issues.add(o, createIssue(o));
			}
		}
	}

	public static class AttributesWithTypeFromUnallowedOwner extends AbstractRule
			implements SimpleRule<UmlAttribute> {
		private static final String HYPO = "attributes with type from prohibited owner"
				+ " according to standard IEC TC57 rules (wrong direction for dependency)";
		private static final String HOWTO = "change attribute type OR move the containing class to that owner";

		public AttributesWithTypeFromUnallowedOwner() {
			super(_logger, HYPO, HOWTO);
		}

		@Override
		public EnumSet<Nature> getApplicability() {
			return EnumSet.allOf(Nature.class);
		}

		@Override
		public void validate(UmlAttribute o, ModelIssues issues) {
			if (o.isLiteral()) {
				return;
			}
			OwningWg owner = o.getOwner();
			EnumSet<OwningWg> allowedOtherOwners = owner.getAllowedOtherEndOwners();
			UmlClass type = o.getType();

			if (!allowedOtherOwners.contains(type.getOwner())) {
				String evidence = "offending type = " + type.getQualifiedName();
				issues.add(o, createIssue(o, evidence));
			}
		}
	}

	// ==================== cross rules

	public static class Iec61850DOAbbreviationLiteralsDuplicateName extends AbstractRule
			implements CrossRule<UmlAttribute> {
		private static final String HYPO = "(GROUP 2) IEC61850-7-4xx abbreviations present more than "
				+ "once";
		private static final String HOWTO = "consolidate all definitions into one abbreviation OR"
				+ " create new abbreviation if definitions differ";
		private final Collection<UmlAttribute> _allAbbrLiterals;

		public Iec61850DOAbbreviationLiteralsDuplicateName(
				Collection<UmlAttribute> allAbbrLiterals) {
			super(AttributeValidator._logger, HYPO, HOWTO);
			_allAbbrLiterals = allAbbrLiterals;
		}

		@Override
		public EnumSet<Nature> getApplicability() {
			return EnumSet.of(Nature.IEC61850);
		}

		@Override
		public Collection<UmlAttribute> getObjsToTestAgainst() {
			return _allAbbrLiterals;
		}

		@Override
		public void validate(List<UmlAttribute> attributes, ModelIssues issues) {
			Collection<UmlAttribute> abbrLiterals = getObjsToTestAgainst();
			MapOfCollections<String, UmlAttribute> dupsPerKey = AbstractUmlObject
					.collectDuplicateNames(abbrLiterals);
			int group = 1;
			for (String abbrName : dupsPerKey.keys()) {
				Collection<UmlAttribute> dups = dupsPerKey.subCollection(abbrName);

				List<String> subResult = new ArrayList<>();
				for (UmlAttribute o : dups) {
					subResult.add(String.format("{%s: %s}", o.getOwner(), o.getDescription().text));
				}
				String evidence = "all definitions for the same name = " + subResult.toString();
				for (UmlAttribute o : dups) {
					ModelIssue issue = createIssue(o, evidence, o.toShortString(false, true),
							String.valueOf(group));
					issues.add(o, issue);
				}
				++group;
			}
		}
	}

	public static class Iec61850DOAbbreviationLiteralsDuplicateDescription extends AbstractRule
			implements CrossRule<UmlAttribute> {
		private static final String HYPO = "(GROUP 5) IEC61850-7-4xx abbreviations with same "
				+ "definition";
		private static final String HOWTO = "consolidate all definitions into one abbreviation OR"
				+ " create new abbreviation if definitions differ";
		private final Collection<UmlAttribute> _allAbbrLiterals;

		public Iec61850DOAbbreviationLiteralsDuplicateDescription(
				Collection<UmlAttribute> allAbbrLiterals) {
			super(AttributeValidator._logger, HYPO, HOWTO);
			_allAbbrLiterals = allAbbrLiterals;
		}

		@Override
		public EnumSet<Nature> getApplicability() {
			return EnumSet.of(Nature.IEC61850);
		}

		@Override
		public Collection<UmlAttribute> getObjsToTestAgainst() {
			return _allAbbrLiterals;
		}

		@Override
		public void validate(List<UmlAttribute> attributes, ModelIssues issues) {
			Collection<UmlAttribute> abbrLiterals = getObjsToTestAgainst();
			MapOfCollections<String, UmlAttribute> dupsPerKey = AbstractUmlObject
					.collectDuplicateDescriptions(abbrLiterals);
			int group = 1;
			for (String abbrDesc : dupsPerKey.keys()) {
				Collection<UmlAttribute> dups = dupsPerKey.subCollection(abbrDesc);

				List<String> subResult = new ArrayList<>();
				for (UmlAttribute o : dups) {
					subResult.add(String.format("{%s: %s}", o.getOwner(), o.getName()));
				}
				String evidence = "all terms for the same definition = " + subResult.toString();
				for (UmlAttribute o : dups) {
					ModelIssue issue = createIssue(o, evidence,
							o.getDescription().text /*
													 * o. toShortString (false, true)
													 */, String.valueOf(group));
					issues.add(o, issue);
				}
				++group;
			}
		}
	}

	public static class Iec61850DOAbbreviationLiteralsNeverUsedInDOName extends AbstractRule
			implements CrossRule<UmlAttribute> {
		private static final String HYPO = "(GROUP 3) IEC61850-7-4xx abbreviations never used (in"
				+ " name of DO attributes for the chosen scope)";
		private static final String HOWTO = "remove unused abbreviations";
		private final Collection<UmlAttribute> _allAbbrLiterals;

		public Iec61850DOAbbreviationLiteralsNeverUsedInDOName(
				Collection<UmlAttribute> allAbbrLiterals) {
			super(AttributeValidator._logger, HYPO, HOWTO);
			_allAbbrLiterals = allAbbrLiterals;
		}

		@Override
		public EnumSet<Nature> getApplicability() {
			return EnumSet.of(Nature.IEC61850);
		}

		@Override
		public Collection<? extends UmlObject> getObjsToTestAgainst() {
			return _allAbbrLiterals;
		}

		@Override
		public void validate(List<UmlAttribute> attributes, ModelIssues issues) {
			// initialise with all known abbreviation terms
			Set<String> unusedTerms = new LinkedHashSet<String>(
					AbstractUmlObject.collectNames(_allAbbrLiterals));

			// remove all that are used for the given scope:
			for (UmlAttribute attr : attributes) {
				NameDecomposition decomp = attr.getNameDecomposition();
				if (decomp != null) {
					for (Map<String, String> term : decomp.getDecomposedTerms()) {
						for (Entry<String, String> entry : term.entrySet()) {
							unusedTerms.remove(entry.getKey());
						}
					}
				}
			}
			for (String term : unusedTerms) {
				for (UmlAttribute o : _allAbbrLiterals) {
					if (o.getName().equals(term)) {
						issues.add(o, createIssue(o));
					}
				}
			}
		}
	}

	public static class Iec61850DOAttributesWithSameNameDifferentType extends AbstractRule
			implements CrossRule<UmlAttribute> {
		private static final String HYPO = "IEC61850-7-4xx (DO) attributes with same names but "
				+ "different type (CDC)";
		private static final String HOWTO = "rename (DO) attribute OR change its type (CDC)";
		private final Collection<UmlAttribute> _allAttributes;

		public Iec61850DOAttributesWithSameNameDifferentType(
				Collection<UmlAttribute> allAttributes) {
			super(AttributeValidator._logger, HYPO, HOWTO);
			_allAttributes = allAttributes;
		}

		@Override
		public EnumSet<Nature> getApplicability() {
			return EnumSet.of(Nature.IEC61850);
		}

		@Override
		public Collection<UmlAttribute> getObjsToTestAgainst() {
			return _allAttributes;
		}

		@Override
		public void validate(List<UmlAttribute> attributes, ModelIssues issues) {
			// collect them all, classified by name:
			MapOfCollections<String, UmlAttribute> dosPerName = new MapOfLists<>();
			for (UmlAttribute attr : attributes) {
				if (attr.isDO()) {
					dosPerName.addValue(attr.getName(), attr);
				}
			}
			int group = 1;
			for (String name : dosPerName.keys()) {
				Collection<UmlAttribute> dosWithSameName = dosPerName.subCollection(name);
				if (dosWithSameName.size() == 1) {
					continue; // single name
				}

				// multiple names - classify by type:
				MapOfCollections<UmlClass, UmlAttribute> dosWithSameNamePerType = new MapOfLists<>();
				for (UmlAttribute attr : dosWithSameName) {
					dosWithSameNamePerType.addValue(attr.getType(), attr);
				}
				if (dosWithSameNamePerType.size() == 1) {
					continue; // all have the same type;
				}

				List<String> evidence = new ArrayList<>();
				for (UmlClass type : dosWithSameNamePerType.keys()) {
					List<String> subResult = new ArrayList<>();
					for (UmlAttribute o : dosWithSameNamePerType.subCollection(type)) {
						subResult.add(o.getOwner().toString() + " " + o.getQualifiedName());
					}
					String isOrAre = subResult.size() > 1 ? "are" : "is";
					evidence.add(subResult.toString() + " " + isOrAre + " " + type.getName());
				}
				for (UmlClass type : dosWithSameNamePerType.keys()) {
					for (UmlAttribute o : dosWithSameNamePerType.subCollection(type)) {
						ModelIssue issue = createIssue(o, evidence.toString(), o.getName(),
								String.valueOf(group));
						issues.add(o, issue);
					}
				}
				++group;
			}
		}
	}

	public static class Iec61850ConditionLiteralsNeverUsedAsConstraints extends AbstractRule
			implements CrossRule<UmlAttribute> {
		private static final String HYPO = "IEC61850 presence condition literals never used as "
				+ "class constraints";
		private static final String HOWTO = "if also not used in the larger scope: remove "
				+ "unused presence condition literals";
		private final Collection<UmlAttribute> _allPresCondLiterals;

		public Iec61850ConditionLiteralsNeverUsedAsConstraints(
				Collection<UmlAttribute> allPresCondLiterals) {
			super(AttributeValidator._logger, HYPO, HOWTO);
			_allPresCondLiterals = allPresCondLiterals;
		}

		@Override
		public EnumSet<Nature> getApplicability() {
			return EnumSet.of(Nature.IEC61850);
		}

		@Override
		public Collection<UmlAttribute> getObjsToTestAgainst() {
			return _allPresCondLiterals;
		}

		@Override
		public void validate(List<UmlAttribute> attributes, ModelIssues issues) {
			Set<UmlAttribute> usedCondLiterals = collectUsedCondLiterals(attributes);

			Set<UmlAttribute> nonUsedCondLiterals = new LinkedHashSet<UmlAttribute>(
					_allPresCondLiterals);
			Set<UmlAttribute> implicitCondLiterals = findPcLiteralsToIgnore(nonUsedCondLiterals);
			nonUsedCondLiterals.removeAll(usedCondLiterals);
			nonUsedCondLiterals.removeAll(implicitCondLiterals);

			for (UmlAttribute o : nonUsedCondLiterals) {
				issues.add(o, createIssue(o));
			}
		}

		private static Set<UmlAttribute> collectUsedCondLiterals(
				Collection<UmlAttribute> attributes) {
			Collection<UmlClass> classes = new LinkedHashSet<>();
			for (UmlAttribute a : attributes) {
				classes.add(a.getContainingClass());
			}

			Set<UmlAttribute> usedCondLiterals = new LinkedHashSet<>();
			for (UmlClass c : classes) {
				for (UmlConstraint cc : c.getConstraints().values()) {
					PresenceCondition pc = cc.getPresenceCondition();
					if (pc != null && pc.getDefinitionLiteral() != null) {
						usedCondLiterals.add(pc.getDefinitionLiteral());
					}
				}
			}
			return usedCondLiterals;
		}

		private static Set<UmlAttribute> findPcLiteralsToIgnore(Set<UmlAttribute> allCondLiterals) {
			Set<String> conditionsToIgnore = PresenceCondition.getNamesOfImplicits();

			Set<UmlAttribute> result = new LinkedHashSet<>();
			for (UmlAttribute literal : allCondLiterals) {
				if (conditionsToIgnore.contains(literal.getName())) {
					result.add(literal);
				}
			}
			return result;
		}
	}
}
