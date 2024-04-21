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

package org.tanjakostic.jcleancim.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;
import org.tanjakostic.jcleancim.common.Nature;
import org.tanjakostic.jcleancim.common.OwningWg;
import org.tanjakostic.jcleancim.model.UmlClass.InheritedKind;
import org.tanjakostic.jcleancim.util.ProgrammerErrorException;
import org.tanjakostic.jcleancim.util.Util;

/**
 * UML attribute or enumeration literal.
 * <p>
 * Implementation note: We distinguish among kinds of attributes by their {@link Kind}, which
 * implements the {@link UmlKind} interface and internally piggy-backs kinds of {@link UmlClass}.
 * Knowing the kinds of attributes allows us to do model validation and also to correctly print
 * documentation (and on the fly, calculate detailed statistics).
 * <p>
 * A cleaner design would be to effectively create subclasses instead of using the above kinds, but
 * it would be overkill for minor differences in functionality per kind.
 *
 * @author tatjana.kostic@ieee.org
 * @version $Id: UmlAttribute.java 34 2019-12-20 18:37:17Z dev978 $
 */
public class UmlAttribute extends AbstractUmlObject {
	private static final Logger _logger = Logger.getLogger(UmlAttribute.class.getName());

	/** Allowed tags for IEC 61850 attributes. */
	private static final List<String> IEC61850_TAGS = Arrays.asList(UML.TAG_moveAfter,
			UML.TAG_SCL_emptyValue, UML.TVN_nsuri, UML.TVN_nsprefix);

	/** Allowed tags for CIM attributes. */
	private static final List<String> CIM_TAGS = Arrays.asList(UML.TVN_nsuri, UML.TVN_nsprefix);

	/** Allowed tags for CIM profile attributes. */
	private static final List<String> CIM_PROFILE_TAGS = Arrays.asList(UML.TVN_nsuri,
			UML.TVN_nsprefix, UML.TVN_GUIDBasedOn);

	public static final int DO_MAX_LENGTH = 12;

	/**
	 * Kinds of UML attributes - correspond mainly to the kind of the class used as the attribute's
	 * type.
	 * <p>
	 * Implementation note: We piggy-back here the kinds defined for {@link UmlClass} as much as
	 * possible. For XML doc generation we need different tags (generic), while preserving detailed
	 * description for statistics.
	 *
	 * @author tatjana.kostic@ieee.org
	 * @version $Id: UmlAttribute.java 34 2019-12-20 18:37:17Z dev978 $
	 */
	public enum Kind implements UmlKind {
		PRIM(UmlClass.CimKind.PRIM, "Attribute", "primitive attribute"),

		DT(UmlClass.CimKind.DT, "Attribute", "CIM datatype attribute"),

		COMP(UmlClass.CimKind.COMP, "Attribute", "compound attribute"),

		ENUMCIM(UmlClass.CimKind.ENUM, "Attribute", "enumerated attribute"),

		// ----------------------------------------------

		LITERAL(UmlStereotype.ENUM, "literal", "Literal", "other enumeration literal"),

		ATTRIBUTE("Attribute", "attribute", "Attribute", "other attribute"),

		// ----------------------------------------------

		ABBR_LITERAL("Term", "abbreviated term", "Term", "abbreviation enumeration literal"),

		COND_LITERAL("Presence condition", "presence condition", "Condition",
				"presence condition enumeration literal"),

		PACKED_LITERAL("Coded literal", "coded literal", "Literal", "coded enumeration literal"),

		IF(UmlClass.Iec61850Kind.IF, "Attribute", "attribute whose type is interface"),

		BASIC(UmlClass.Iec61850Kind.BASIC, "Attribute", "basic attribute"),

		STRUCTURED(UmlClass.Iec61850Kind.STRUCTURED, "Attribute", "structured attribute"),

		PACKED_BASIC(UmlClass.Iec61850Kind.PACKED_BASIC, "Attribute", "packed attribute"),

		ENUM61850(UmlClass.Iec61850Kind.ENUM, "Attribute", " (61850) enumeration literal"),

		PACKED_ENUM(UmlClass.Iec61850Kind.PACKED_ENUM, "Attribute", "coded enumeration literal"),

		PACKED_ENUM_DA(UmlClass.Iec61850Kind.PACKED_ENUM_DA, "CodedEnumDA",
				"coded enumeration DA attribute"),

		ENUM_DA(UmlClass.Iec61850Kind.ENUM_DA, "DA", "enumeration DA attribute"),

		PACKED_PRIM_DA(UmlClass.Iec61850Kind.PACKED_PRIM_DA, "Attribute", "packed list DA class"),

		PRIM_DA(UmlClass.Iec61850Kind.PRIM_DA, "DA", "attribute on any DA whose type is primitive"),

		COMP_DA(UmlClass.Iec61850Kind.COMP_DA, "DA",
				"attribute on composed DA whose type is another compoased DA"),

		DA("DA", "data attribute", "DA", "data attribute"),

		PACKED_ENUM_FCDA(UmlClass.Iec61850Kind.CODED_ENUM_FCDA, "FCDA",
				"coded enumeration FCDA attribute"),

		ENUM_FCDA(UmlClass.Iec61850Kind.ENUM_FCDA, "FCDA", "enumeration FCDA attribute"),

		PACKED_LIST_FCDA(UmlClass.Iec61850Kind.PACKED_LIST_FCDA, "FCDA",
				"packed list FCDA attribute"),

		COMP_FCDA(UmlClass.Iec61850Kind.COMP_FCDA, "FCDA", "composed FCDA attribute"),

		FCDA(UmlClass.Iec61850Kind.FCDA, "FCDA", "FCDA attribute"),

		SDO("SDO", "sub-data object", "SDO",
				"sub-data object (attribute on composed CDC whose type is another CDC)"),

		// ENUM_DO(UmlClass.Iec61850Kind.ENUM_CDC, "DO", "enumerated DO (derived)"),
		ENUM_DO("enum DO", "enumerated DO", "DO", "enumerated DO (derived)"),

		// CTS_DO(UmlClass.Iec61850Kind.CTS_CDC, "DO", "control tracking DO (derived)"),
		CTS_DO("CTS DO", "control tracking DO", "DO", "control tracking DO (derived)"),

		// TRANS_DO(UmlClass.Iec61850Kind.TRANS_CDC, "DO", "transient DO (derived)"),
		TRANS_DO("transient DO", "transient data object", "DO", "transient DO (derived)"),

		DO("DO", "data object", "DO", "data object (attribute on LN)");

		/**
		 * Returns all values as UmlKind list.
		 */
		public static List<UmlKind> getUmlKinds() {
			List<UmlKind> result = new ArrayList<UmlKind>();
			for (UmlKind kind : values()) {
				result.add(kind);
			}
			return result;
		}

		private Kind(UmlKind kind, String tag, String desc) {
			this(kind.getValue(), kind.getValue() + " attribute", tag, desc);
		}

		private Kind(String value, String label, String tag, String desc) {
			_value = value;
			_label = label;
			_tag = tag;
			_desc = desc;
		}

		private final String _value;
		private final String _label;
		private final String _tag;
		private final String _desc;

		@Override
		public String getValue() {
			return _value;
		}

		@Override
		public String getLabel() {
			return _label;
		}

		@Override
		public String getTag() {
			return _tag;
		}

		@Override
		public String getDesc() {
			return _desc;
		}
	}

	/**
	 * Returns available classifications (kinds) for attributes.
	 *
	 * @param nature
	 *            ignored in this method.
	 */
	public static List<UmlKind> getKinds(Nature nature) {
		return Kind.getUmlKinds();
	}

	public static Collection<UmlAttribute> findEnumLiterals(Collection<UmlAttribute> attributes) {
		List<UmlAttribute> result = new ArrayList<UmlAttribute>();
		for (UmlAttribute attr : attributes) {
			if (attr.isLiteral()) {
				result.add(attr);
			}
		}
		return result;
	}

	public static Collection<UmlAttribute> findAbbreviationLiterals(
			Collection<UmlAttribute> attributes) {
		Collection<UmlAttribute> result = new ArrayList<>();
		for (UmlAttribute attr : findEnumLiterals(attributes)) {
			if (attr.getContainingClass().isAbbreviationEnumeration()) {
				result.add(attr);
			}
		}
		return result;
	}

	public static Collection<UmlAttribute> findPresenceConditionLiterals(
			Collection<UmlAttribute> attributes) {
		Collection<UmlAttribute> result = new ArrayList<>();
		for (UmlAttribute attr : findEnumLiterals(attributes)) {
			if (attr.getContainingClass().isConditionEnumeration()) {
				result.add(attr);
			}
		}
		return result;
	}

	/**
	 * Data from the UML model repository specific to {@link UmlAttribute}.
	 *
	 * @author tatjana.kostic@ieee.org
	 * @version $Id: UmlAttribute.java 34 2019-12-20 18:37:17Z dev978 $
	 */
	public static class Data {
		private static final Data DEFAULT = new Data();

		/**
		 * Used for testing only: Returns empty instance with init value; sets default multiplicity
		 * to {@link UmlMultiplicity#ONE}.
		 */
		static Data withInitValue(String initValue) {
			return new Data(false, false, UmlMultiplicity.ONE, initValue, 0, "", false);
		}

		/** Returns empty instance; sets default multiplicity to {@link UmlMultiplicity#ONE}. */
		public static Data empty() {
			return DEFAULT;
		}

		private final boolean _isConst;
		private final boolean _isStatic;
		private final UmlMultiplicity _multiplicity;
		private final String _initValue;
		private final int _eaTypeId;
		private final String _eaTypeName;
		private final boolean _eaTypeSuperfluous;

		private Data() {
			this(false, false, UmlMultiplicity.ONE, "", 0, "", false);
		}

		/**
		 * Constructor.
		 *
		 * @param isConst
		 * @param isStatic
		 * @param multiplicity
		 * @param initValue
		 * @param eaTypeId
		 * @param eaTypeName
		 * @param isEaTypeSuperfluous
		 */
		public Data(boolean isConst, boolean isStatic, UmlMultiplicity multiplicity,
				String initValue, int eaTypeId, String eaTypeName, boolean isEaTypeSuperfluous) {
			_isConst = isConst;
			_isStatic = isStatic;
			_multiplicity = multiplicity;
			_initValue = initValue;
			_eaTypeId = eaTypeId;
			_eaTypeName = eaTypeName;
			_eaTypeSuperfluous = isEaTypeSuperfluous;
		}

		public boolean isConst() {
			return _isConst;
		}

		public boolean isStatic() {
			return _isStatic;
		}

		public UmlMultiplicity getMultiplicity() {
			return _multiplicity;
		}

		public String getInitValue() {
			return _initValue;
		}

		public int getEaTypeId() {
			return _eaTypeId;
		}

		public String getEaTypeName() {
			return _eaTypeName;
		}

		public boolean isEaTypeSuperfluous() {
			return _eaTypeSuperfluous;
		}
	}

	private final UmlClass _containingClass;
	private final UmlClass _type;
	private final Data _data;

	private final UmlKind _kind;
	private final ValueRange _valueRange;
	private final Integer _initValueAsInteger;

	private final List<UmlConstraint> _ownConstraints = new ArrayList<UmlConstraint>();

	/** For tests only: Constructs attribute (that is not literal) with given name. */
	static UmlAttribute basic(UmlClass containingClass, UmlClass type, String name) {
		return new UmlAttribute(containingClass, type, new UmlObjectData(name), Data.empty());
	}

	/** For tests only: Constructs literal with given name. */
	static UmlAttribute basicLiteral(UmlClass containingClass, String name) {
		return new UmlAttribute(containingClass, null,
				new UmlObjectData(name, new UmlStereotype(UmlStereotype.ENUM)), Data.empty());
	}

	/** For tests only: Constructs attribute with given multiplicity. */
	static UmlAttribute basic(UmlClass containingClass, UmlClass type, String name,
			UmlMultiplicity mult) {
		return new UmlAttribute(containingClass, type, new UmlObjectData(name),
				new Data(false, false, mult, "", 0, "", false));
	}

	/** For tests only: Constructs attribute with given constant value (static and const true). */
	static UmlAttribute basic(UmlClass containingClass, UmlClass type, String name,
			String initVal) {
		return new UmlAttribute(containingClass, type, new UmlObjectData(name),
				new Data(true, true, UmlMultiplicity.ONE, initVal, 0, "", false));
	}

	/**
	 * Intended to be called by {@link UmlClass} and tests only.
	 *
	 * @throws IllegalArgumentException
	 *             if <code>containingClass</code> and non-null <code>type</code> are from different
	 *             models, or if <code>type</code> is null and <code>containingClass</code> is not
	 *             an enumerated type.
	 */
	UmlAttribute(UmlClass containingClass, UmlClass type, UmlObjectData objData, Data data) {
		super(objData);

		Util.ensureNotNull(containingClass, "containingClass");
		Util.ensureNotNull(data, "data");

		if (type != null && containingClass.getModel() != type.getModel()) {
			throw new IllegalArgumentException(
					String.format("Containing class model (%s) and type's model (%s) must be same.",
							containingClass.getModel().getUuid(), type.getModel().getUuid()));
		}

		_containingClass = containingClass;
		_data = data;
		_type = type;

		_kind = determineKind(containingClass, type);
		_valueRange = initValueRange(getInitValue());
		_initValueAsInteger = initInitialValueAsInteger(getInitValue());

		_logger.trace(String.format("created %s", toString()));
	}

	private Kind determineKind(UmlClass containingClass, UmlClass type) {
		Kind kind = null;

		boolean isLiteral = containingClass.isEnumeratedType();
		if (type == null) {
			if (!isLiteral) {
				throw new ProgrammerErrorException(String.format(
						"This is not an enum literal (containing class = %s) and"
								+ " you should use as type argument the null-class (%s)"
								+ " obtained from the model instead of null.",
						containingClass.getQualifiedName(),
						containingClass.getModel().getNullClasses().get(containingClass.getNature())
								.getQualifiedName()));
			}
			if (containingClass.isAbbreviationEnumeration()) {
				kind = Kind.ABBR_LITERAL;
			} else if (containingClass.isConditionEnumeration()) {
				kind = Kind.COND_LITERAL;
			} else if (containingClass.isCodedEnum()) {
				kind = Kind.PACKED_LITERAL;
			} else {
				kind = Kind.LITERAL;
			}
		} else {
			if (containingClass.getNature() == Nature.CIM) {
				if (type.isPrimitive()) {
					kind = Kind.PRIM;
				} else if (type.isDatatype()) {
					kind = Kind.DT;
				} else if (type.isCompound()) {
					kind = Kind.COMP;
				} else if (type.isEnumeratedType()) {
					kind = Kind.ENUMCIM;
				} else {
					_logger.error("unclassified CIM attribute: " + toString());
					kind = Kind.ATTRIBUTE;
				}
			} else {
				if (type.isInterface()) {
					kind = Kind.IF;
				} else if (type.isBasic()) {
					kind = Kind.BASIC;
				} else if (type.isStructured()) {
					kind = Kind.STRUCTURED;
				} else if (type.isPackedList()) {
					kind = Kind.PACKED_BASIC;
				} else if (type.isEnumeration()) {
					kind = Kind.ENUM61850;
				} else if (type.isCodedEnum()) {
					kind = Kind.PACKED_ENUM;
				} else if (type.isCodedEnumDA()) {
					kind = Kind.PACKED_ENUM_DA;
				} else if (type.isEnumDA()) {
					kind = Kind.ENUM_DA;
				} else if (type.isPackedListDA()) {
					kind = Kind.PACKED_PRIM_DA;
				} else if (type.isPrimitiveDA()) {
					kind = Kind.PRIM_DA;
				} else if (type.isComposedDA()) {
					kind = Kind.COMP_DA;
				} else if (type.isAnyDA()) {
					kind = Kind.DA;
				} else if (type.isCodedEnumFCDA()) {
					kind = Kind.PACKED_ENUM_FCDA;
				} else if (type.isEnumFCDA()) {
					kind = Kind.ENUM_FCDA;
				} else if (type.isPackedListFCDA()) {
					kind = Kind.PACKED_LIST_FCDA;
				} else if (type.isComposedFCDA()) {
					kind = Kind.COMP_FCDA;
				} else if (type.isAnyFCDA()) {
					kind = Kind.FCDA;
				} else if (type.isTrackingDerivedCDC()) {
					kind = Kind.CTS_DO;
				} else if (type.isEnumCDC()) {
					kind = Kind.ENUM_DO;
				} else if (type.isTransientCDC()) {
					kind = Kind.TRANS_DO;
				} else if (type.isAnyCDC() && !type.isFromMetaModel()) {
					kind = (containingClass.isAnyLN()) ? Kind.DO : Kind.SDO;
				} else {
					if (!containingClass.isFrom72()) {
						_logger.error("unclassified 7-4/7-3 attribute: " + toString());
					} else {
						_logger.debug("unclassified 7-2 attribute: " + toString());
					}
					kind = Kind.ATTRIBUTE;
				}
			}
		}
		return kind;
	}

	private ValueRange initValueRange(String initValue) {
		if (ValueRange.isValidRangeFormat(initValue)) {
			return new ValueRange(initValue);
		}
		return null;
	}

	private Integer initInitialValueAsInteger(String initValue) {
		try {
			return Integer.valueOf(initValue.trim());
		} catch (NumberFormatException e) {
			return null;
		}
	}

	/** Returns class containing this attribute. */
	public UmlClass getContainingClass() {
		return _containingClass;
	}

	/**
	 * Returns {@link UmlClass} used as type of this attribute for a non-literal, null otherwise.
	 */
	public UmlClass getType() {
		return _type;
	}

	/** See {@link Data#isConst()}. */
	public boolean isConst() {
		return _data.isConst();
	}

	/** See {@link Data#isStatic()}. */
	public boolean isStatic() {
		return _data.isStatic();
	}

	/** See {@link Data#getMultiplicity()}. */
	public UmlMultiplicity getMultiplicity() {
		return _data.getMultiplicity();
	}

	/** See {@link Data#getInitValue()}. */
	public String getInitValue() {
		return _data.getInitValue();
	}

	/**
	 * (Special handling for IEC61850) Returns empty string as initial value for the case of an
	 * attribute that returns <code>true</code> from {@link #displayEmptyValue()}. Otherwise,
	 * returns {@link #getInitValue()}.
	 */
	public String getInitValueWithPotentialOverrideForSCL() {
		if (displayEmptyValue()) {
			return "''";
		}
		return getInitValue();
	}

	/** See {@link Data#getEaTypeId()}. */
	public int getEaTypeId() {
		return _data.getEaTypeId();
	}

	/** See {@link Data#getEaTypeName()}. */
	public String getEaTypeName() {
		return _data.getEaTypeName();
	}

	/**
	 * Returns known (string) info from EA; useful to display in case the type of this attribute in
	 * EA model is not a valid UML class, so the model can be corrected.
	 */
	public String getEaTypeInfo() {
		return String.format("'%s' (id=%d)", getEaTypeName(), Integer.valueOf(getEaTypeId()));
	}

	/** See {@link Data#isEaTypeSuperfluous()}. */
	public boolean hasSuperfluousType() {
		return _data.isEaTypeSuperfluous();
	}

	/** Returns whether the multiplicity is optional. */
	public boolean isOptional() {
		return getMultiplicity().isOptional();
	}

	/** Returns whether this attribute is multivalued. */
	public boolean isMultivalued() {
		return getMultiplicity().isMultivalue();
	}

	/** Returns whether this attribute is public. */
	public boolean isPublic() {
		return getVisibility() == UmlVisibility.PUBLIC;
	}

	// ---------------------------- kinds -----------------------------

	/** CIM and IEC61850 */
	public boolean isLiteral() {
		return getKind() == Kind.LITERAL || getKind() == Kind.ABBR_LITERAL
				|| getKind() == Kind.COND_LITERAL || getKind() == Kind.PACKED_LITERAL;
	}

	/** IEC61850 */
	public boolean isDO() {
		return getContainingClass().is74LN();
	}

	// --------- constraints from class (used for presence conditions in IEC 61850) ------------

	private List<PresenceCondition> _presConditions;

	public List<PresenceCondition> getPresConditions() {
		if (_presConditions == null) {
			_presConditions = new ArrayList<PresenceCondition>();
			for (UmlConstraint constr : getConstraintsFromClass()) {
				PresenceCondition pc = constr.getPresenceCondition();
				if (pc != null) {
					_presConditions.add(pc);
				}
			}
			if (_presConditions.isEmpty()) { // if none from class, we're simply M/O:
				PresenceCondition mo = isOptional() ? PresenceCondition.O : PresenceCondition.M;
				_presConditions.add(mo);
			}
		}
		return Collections.unmodifiableList(_presConditions);
	}

	/** Returns constraints defined on the containing class, involving this attribute. */
	List<UmlConstraint> getConstraintsFromClass() {
		return getContainingClass().findConstraintsForAttribute(getName());
	}

	/** Returns whether this attribute has presence condition derived from its containing class. */
	public boolean isConditional() {
		return !getConstraintsFromClass().isEmpty();
	}

	/**
	 * (IEC 61850) Returns derived statistics presence conditions.
	 *
	 * @param context
	 *            class for which this attribute is considered; i.e., it could be native to
	 *            <code>context</code> or inherited (from a class other than <code>context</code>).
	 */
	public List<PresenceCondition> getDsPresConditions(UmlClass context) {

		// for attributes outside logical-nodes, the ds does not apply at all:
		if (!isDO()) {
			return Collections.emptyList();
		}

		// these two are mandatory for ds in any context (native or inherited):
		if (UML.ClcMth.equals(getName()) || UML.ClcSrc.equals(getName())) {
			return Arrays.asList(PresenceCondition.M);
		}

		// if my owning LN does not inherit from statistics, ds is not applicable:
		if (!context.isAdmin() && !context.inheritsFromStatisticsLN()) {
			return Arrays.asList(PresenceCondition.NA);
		}

		// for DOs from admin logical nodes, presence conditions never change:
		if (getContainingClass().isAdmin()) {
			return getPresConditions();
		}

		// if my type is a statistics CDC, ds is optional:
		if (getType().isUsableForStatistics()) {
			return Arrays.asList(PresenceCondition.O);
		}

		// otherwise, ds is forbidden:
		return Arrays.asList(PresenceCondition.F);
	}

	// ------------------ abbreviated terms (if applicable) decomposition -------------------

	private NameDecomposition _decomposedTerms;

	/** Equivalent to <code>getNameDecomposition(null)</code>. */
	public NameDecomposition getNameDecomposition() {
		return getNameDecomposition(null);
	}

	/**
	 * In case of a data object (attribute on LN, in IEC61850), returns decomposition of the
	 * attribute name to abbreviated terms, null otherwise.
	 *
	 * @param sortedAbbrTerms
	 *            (potentially null) abbreviated terms sorted correctly for comparison; if null,
	 *            this instance needs to find access to those abbreviated terms internally.
	 */
	public NameDecomposition getNameDecomposition(Map<String, String> sortedAbbrTerms) {
		UmlClass containingClass = getContainingClass();
		if (isDO() && _decomposedTerms == null) {
			Map<String, String> terms = sortedAbbrTerms != null ? sortedAbbrTerms
					: containingClass.getModel().getAbbreviatedTermsSortedPerDecreasingLength();
			_decomposedTerms = new NameDecomposition(getName(), terms);
		}
		return _decomposedTerms;
	}

	// ------------------ own constraints (used for array bounds in IEC 61850) ----------------

	/**
	 * Creates attribute constraint from arguments, adds it to this attribute and returns the new
	 * constraint.
	 */
	public UmlConstraint addOwnConstraint(UmlObjectData objData, UmlConstraint.Data data) {
		UmlConstraint result = new UmlConstraint(this, objData, data);
		_ownConstraints.add(result);
		return result;
	}

	/** Returns constraints defined on this attribute. */
	public List<UmlConstraint> getOwnConstraints() {
		return Collections.unmodifiableList(_ownConstraints);
	}

	/**
	 * Returns formatted string "minId...maxId" created from attribute constraints if existing,
	 * empty string otherwise. This format is useful for doc generation where a multivalued
	 * attribute has bounds specified as constraints (IEC 61850-7-3).
	 */
	public String getArrayBounds() {
		Map<String, String> constraints = getConstraintValues();
		String minIdx = Util.null2empty(constraints.get(UML.CONSTR_TXT_minIdx));
		String maxIdx = Util.null2empty(constraints.get(UML.CONSTR_TXT_maxIdx));

		if (minIdx.isEmpty() && maxIdx.isEmpty()) {
			return "";
		}
		return String.format("%s...%s", minIdx, maxIdx);
	}

	public String getArrayBoundsWithBrackets() {
		return String.format("[%s]", getArrayBounds());
	}

	/** Returns map of {name, constraint} pairs defined as attribute constraints. */
	public Map<String, String> getConstraintValues() {
		Map<String, String> result = new HashMap<String, String>();
		List<UmlConstraint> cs = _ownConstraints;
		for (UmlConstraint c : cs) {
			result.put(c.getName(), c.getCondition());
		}
		return result;
	}

	// ------------------ initial values / ranges / defaults ----------------

	/** Returns whether this attribute has an initial value, as defined in UML repository. */
	boolean hasInitialValue() {
		return !getInitValue().trim().isEmpty();
	}

	/** Returns whether the attribute has a range specified. */
	public boolean hasValueRange() {
		return getValueRange() != null;
	}

	/** Returns value range if specified, null otherwise. */
	public ValueRange getValueRange() {
		return _valueRange;
	}

	/** Returns whether the attribute has a constant value (for all instances of the class). */
	public boolean hasConstValue() {
		return hasInitialValue() && isConst() && isStatic();
	}

	/**
	 * Returns whether the attribute has a default initial value (semantic: value applies to any
	 * instance of a class and can be changed later).
	 */
	public boolean hasDefaultValue() {
		return hasInitialValue() && !isConst() && !isStatic() && !hasValueRange();
	}

	/** Returns the initial value if it can be interpreted as integer, null otherwise. */
	public Integer getInitialValueAsInteger() {
		return _initValueAsInteger;
	}

	/** Returns whether the initial value (when present) was interpreted as integer. */
	public boolean hasInitialValueAsInteger() {
		return getInitialValueAsInteger() != null;
	}

	// ------------------ processing based on tagged values -------------------

	/**
	 * Returns (native or inherited) sibling attribute whose name is defined as value of the tag
	 * {@link UML#TAG_moveAfter} if found, null otherwise; in case there are multiple sibling
	 * attributes with that same name, returns the first one.
	 */
	public UmlAttribute getSiblingToMoveAfter() {
		String moveAfterWhichName = getTaggedValues().get(UML.TAG_moveAfter);
		if (moveAfterWhichName != null) {
			Collection<UmlAttribute> siblings = getContainingClass()
					.findAttributes(moveAfterWhichName, InheritedKind.all);
			if (siblings.isEmpty()) {
				return null;
			}
			if (siblings.size() > 1) {
				_logger.warn(String.format(
						"Retaining first among multiple sibling attributes for %s (%s).",
						getQualifiedName(), AbstractUmlObject.collectQNames(siblings, true)));
			}
			return siblings.iterator().next();
		}
		return null;
	}

	/**
	 * Returns true if this is an enumeration literal whose name needs to be translated as empty
	 * string in SCL XML, or simply an attribute with a default value that again needs to be
	 * translated as empty string in SCL XML (and in both Word and XML auto-generated docs).
	 */
	public boolean displayEmptyValue() {
		return getTaggedValues().containsKey(UML.TAG_SCL_emptyValue);
	}

	/** Returns all (native and inherited) sibling attributes. */
	public Collection<UmlAttribute> getAllSiblings() {
		UmlClass containingClass = getContainingClass();
		Collection<UmlAttribute> siblings = new ArrayList<>(containingClass.getAttributes());
		for (Iterator<UmlAttribute> it = siblings.iterator(); it.hasNext();) {
			UmlAttribute attr = it.next();
			if (attr == this) {
				it.remove();
				break;
			}
		}
		siblings.addAll(containingClass.getInheritedAttributes());
		return siblings;
	}

	// =========== org.tanjakostic.jcleancim.model.UmlObject ============

	@Override
	public OwningWg getOwner() {
		return getContainingClass().getOwner();
	}

	/**
	 * {@inheritDoc}
	 * <p>
	 * Returns own namespace initialised from tagged values if not empty. Otherwise, returns the
	 * namespace of the containing class.
	 */
	@Override
	public Namespace getNamespace() {
		Namespace ns = initFromTags();
		if (ns != Namespace.EMPTY) {
			return ns;
		}

		return getContainingClass().getNamespace();
	}

	/**
	 * {@inheritDoc}
	 * <p>
	 * Returns the nature of containing clas.
	 */
	@Override
	public Nature getNature() {
		return getContainingClass().getNature();
	}

	@Override
	public boolean isInformative() {
		return super.isInformative() || getContainingClass().isInformative()
				|| (!isLiteral() && getType().isInformative());
	}

	@Override
	public UmlKind getKind() {
		return _kind;
	}

	@Override
	public String getQualifiedName() {
		return getContainingClass().getQualifiedName() + CLASS_SEPARATOR + getName();
	}

	@Override
	public Set<String> getPredefinedTagNames() {
		List<String> resultList = (getNature() == Nature.CIM) ? CIM_TAGS : IEC61850_TAGS;
		return new LinkedHashSet<String>(resultList);
	}

	// =========== java.lang.Object ============

	@Override
	public String toString() {
		String qualifier = "";
		if (!isLiteral()) {
			if (isStatic()) {
				qualifier += " static";
			}
			if (isConst()) {
				qualifier += " const";
			}
			qualifier += " " + getMultiplicity();
		}
		StringBuilder sb = new StringBuilder(toShortString(true, qualifier, true));
		if (!isLiteral()) {
			sb.append(": ").append(getType().getQualifiedName());
		}
		if (!getInitValue().isEmpty()) {
			sb.append(" ").append("=").append(" ").append(getInitValue());
		}
		List<UmlConstraint> cs = getConstraintsFromClass();
		if (!cs.isEmpty()) {
			sb.append(", presence conditions=").append(AbstractUmlObject.collectNames(cs));
		}
		String bounds = getArrayBounds();
		if (!bounds.isEmpty()) {
			sb.append(", array bounds=").append(bounds);
		}
		if (!getTaggedValues().isEmpty()) {
			sb.append(", tags=").append(getTaggedValues().toString());
		}
		return sb.toString();
	}
}
