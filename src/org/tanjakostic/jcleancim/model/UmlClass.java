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
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.tanjakostic.jcleancim.common.Nature;
import org.tanjakostic.jcleancim.common.OwningWg;
import org.tanjakostic.jcleancim.util.Util;

/**
 * UML class, interface or enumerated type. In addition to UML features specific to classes, it
 * inherits implementation for features common with UML packages from {@link UmlStructure} (so we
 * avoid code duplication).
 * <p>
 * Implementation note: We distinguish among kinds of classes by their {@link CimKind} or
 * {@link Iec61850Kind}, where both these types implement the common {@link UmlKind} interface.
 * Knowing the kinds of classes allows us to do model validation and also to correctly print
 * documentation (and on the fly, calculate detailed statistics).
 * <p>
 * A cleaner design would be to effectively create subclasses instead of using the above kinds, but
 * it would be overkill for minor differences in functionality per kind.
 *
 * @author tatjana.kostic@ieee.org
 * @version $Id: UmlClass.java 34 2019-12-20 18:37:17Z dev978 $
 */
public class UmlClass extends UmlStructure {
	private static final Logger _logger = Logger.getLogger(UmlClass.class.getName());

	private static final Map<String, UmlKind> CIMDATATYPE_MIN_SPEC;

	static {
		CIMDATATYPE_MIN_SPEC = new LinkedHashMap<String, UmlKind>();
		CIMDATATYPE_MIN_SPEC.put(UML.CIM_DT_value, CimKind.PRIM);
		CIMDATATYPE_MIN_SPEC.put(UML.CIM_DT_unit, CimKind.ENUM);
		CIMDATATYPE_MIN_SPEC.put(UML.CIM_DT_multiplier, CimKind.ENUM);
	}

	/**
	 * Minimum requirement for a valid {@link UmlStereotype#CIMDATATYPE}: key is attribute name (
	 * {@link UmlAttribute#getName()}) and value is the kind of its type ((
	 * {@link UmlAttribute#getKind()})).
	 */
	public static Map<String, UmlKind> getCimDataTypeMinSpec() {
		return UmlClass.CIMDATATYPE_MIN_SPEC;
	}

	public static final String SUPER_PRIM_CDC = "PrimitiveCDC";
	public static final String SUPER_COMP_CDC = "ComposedCDC";
	public static final String SUPER_LN = "LN";

	public static final String TAG_FUNCTIONS = "Functions";
	public static final String TAG_PRESENCE_CONDITIONS = "PresenceConditions";

	static final List<String> SUPER_ENUM_LNS = Arrays
			.asList(new String[] { UML.ENS, UML.ENG, UML.ENC, UML.ERY });

	/**
	 * Kind of the UML class for CIM domain.
	 *
	 * @author tatjana.kostic@ieee.org
	 * @version $Id: UmlClass.java 34 2019-12-20 18:37:17Z dev978 $
	 */
	public enum CimKind implements UmlKind {
				PRIM(UmlStereotype.PRIMITIVE, "primitive", "Primitive", "primitive class"),

				ENUM(UmlStereotype.ENUMERATION, "enumeration", "Enumeration", "enumeration class"),

				DT(UmlStereotype.CIMDATATYPE, "datatype", "Datatype", "CIM datatype class"),

				COMP(UmlStereotype.COMPOUND, "compound", "Compound", "compound class"),

				ROOT_CLASS("root class", "root class", "CIMClass", "root class"),

				CLASS("class", "class", "CIMClass", "class"),

				NULL_CIM("null class", "null CIM class", "NullCIMClass", "null CIM class");

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

		private CimKind(String value, String label, String tag, String desc) {
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
	 * Kind of the UML class for IEC 61850 domain.
	 *
	 * @author tatjana.kostic@ieee.org
	 * @version $Id: UmlClass.java 34 2019-12-20 18:37:17Z dev978 $
	 */
	public enum Iec61850Kind implements UmlKind {
		IF(UmlStereotype.EA_INTERFACE, "interface", "Interface", "interface"),

		BASIC(UmlStereotype.BASIC, "basic", "Basic", "basic class"),

		/** 6.6.3.17 Originator (S_Originator unknown 61850) */
		STRUCTURED(UmlStereotype.STRUCTURED, "structured", "Structured", "structured class"),

		PACKED_BASIC(UmlStereotype.PACKED, "packed list", "PackedList", "packed class"),

		ENUM(UmlStereotype.ENUMERATION, "enumeration", "Enumeration", "enumeration class"),

		PACKED_ENUM(UmlStereotype.PACKED + "," + UmlStereotype.ENUMERATION, "coded enumeration",
				"CodedEnum", "coded enumeration class"),

		ABBR_ENUM(UmlStereotype.ABBR + "," + UmlStereotype.ENUMERATION, "abbreviation enumeration",
				"Abbreviations", "abbreviation enumeration class"),

		COND_ENUM(UmlStereotype.COND + "," + UmlStereotype.ENUMERATION,
				"presence condition enumeration", UmlClass.TAG_PRESENCE_CONDITIONS,
				"presence condition enumeration class"),

		PACKED_ENUM_DA(UML.SUPER_PACKED_ENUM_DA, "coded enumeration DA", "CodedEnumDA",
				"coded enumeration DA class"),

		ENUM_DA(UML.SUPER_ENUM_DA, "enumeration DA", "EnumerationDA", "enumeration DA class"),

		PACKED_PRIM_DA(UML.SUPER_PACKED_PRIM_DA, "packed list DA", /* "PackedListDA" */
				"PackedList", "packed list DA class"),

		PRIM_DA(UML.SUPER_PRIM_DA, "primitive DA", "BasicDA", "primitive DA class"),

		COMP_DA(UML.SUPER_COMP_DA, "composed DA", "ConstructedDA", "composed DA class"),

		CODED_ENUM_FCDA(UML.SUPER_PACKED_ENUM_FCDA, "coded enumeration FCDA", "FCDA",
				"coded enumeration FCDA class"),

		ENUM_FCDA(UML.SUPER_ENUM_FCDA, "enumeration FCDA", "FCDA", "enumeration FCDA class"),

		PACKED_LIST_FCDA(UML.SUPER_PACKED_FCDA, "packed list FCDA", "FCDA",
				"packed list FCDA class"),

		COMP_FCDA(UML.SUPER_COMPOSED_FCDA, "composed FCDA", "FCDA", "composed FCDA class"),

		FCDA(UML.SUPER_FCDA, "FCDA", "FCDA", "FCDA class"),

		CTS_CDC("CTS CDC", "control tracking CDC", "CDC", "control tracking CDC class (derived)"),

		ENUM_CDC("enumeration CDC", "enumeration CDC", "CDC", "enumeration CDC class (derived)"),

		TRANS_CDC("transient CDC", "transient CDC", "CDC", "transient CDC class (derived)"),

		PRIM_CDC(UmlClass.SUPER_PRIM_CDC, "primitive CDC", "CDC", "primitive CDC class"),

		COMP_CDC(UmlClass.SUPER_COMP_CDC, "composed CDC", "CDC", "composed CDC class"),

		LN(UmlClass.SUPER_LN, "LN", "LN", "LN class"),

		FUNCTION(UmlClass.TAG_FUNCTIONS, "function", "Function", "function (61850-5) class"),

		OTHER_61850("other 61850", "other 61850", "IEC61850Class", "other 61850 class"),

		NULL_61850("null class", "null 61850 class", "IEC61850Class", "null 61850 class"),

		UNKNOWN_61850("unknown 61850", "unknown 61850", "IEC61850Class", "unknown 61850 class");

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

		private Iec61850Kind(String value, String label, String tag, String desc) {
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

	/** Returns available classifications (kinds) for classes with <code>nature</code>. */
	public static List<UmlKind> getKinds(Nature nature) {
		Util.ensureNotNull(nature, "nature");
		if (nature == Nature.CIM) {
			return CimKind.getUmlKinds();
		}
		return Iec61850Kind.getUmlKinds();
	}

	/**
	 * Used in queries for attributes, association ends and operations.
	 *
	 * @author tatjana.kostic@ieee.org
	 * @version $Id: UmlClass.java 34 2019-12-20 18:37:17Z dev978 $
	 */
	public enum InheritedKind {
		own, inherited, all;
	}

	/** Allowed tags for IEC 61850 classes. */
	private static final List<String> IEC61850_TAGS = Arrays.asList(UML.TVN_rsName, UML.TVN_ieeeRef,
			UML.TVN_iecRef, UML.TVN_cdcId, UML.TVN_oldName, UML.TVN_nsuri, UML.TVN_nsprefix);

	/** Allowed tags for CIM classes. */
	private static final List<String> CIM_TAGS = Arrays.asList(UML.TVN_nsuri, UML.TVN_nsprefix);

	/** Allowed tags for CIM profile classes. */
	private static final List<String> CIM_PROFILE_TAGS = Arrays.asList(UML.TVN_nsuri,
			UML.TVN_nsprefix, UML.TVN_GUIDBasedOn);

	/**
	 * Data from the UML model repository specific to {@link UmlClass}.
	 *
	 * @author tatjana.kostic@ieee.org
	 * @version $Id: UmlClass.java 34 2019-12-20 18:37:17Z dev978 $
	 */
	public static class Data extends UmlStructure.Data {
		private static final Data DEFAULT = new Data();

		private final boolean _isAbstract;
		private final boolean _eaPersistentPropSet;
		private final boolean _eaLeafPropSet;
		private final boolean _eaRootPropSet;
		private final boolean _isEaInterface;
		private final boolean _associationClass;
		private final boolean _selfInherited;
		private final boolean _isEaEnumeration;

		/** Returns an empty instance. */
		public static Data empty() {
			return DEFAULT;
		}

		private Data() {
			this(false, false, false, false, false, false, false, false, false);
		}

		/**
		 * Constructor.
		 *
		 * @param selfDependent
		 * @param isAbstract
		 * @param eaPersistentPropSet
		 * @param eaLeafPropSet
		 * @param eaRootPropSet
		 * @param isEaInterface
		 * @param associationClass
		 * @param selfInherited
		 * @param isEaEnumeration
		 */
		public Data(boolean selfDependent, boolean isAbstract, boolean eaPersistentPropSet,
				boolean eaLeafPropSet, boolean eaRootPropSet, boolean isEaInterface,
				boolean associationClass, boolean selfInherited, boolean isEaEnumeration) {
			super(selfDependent);
			_isAbstract = isAbstract;
			_eaPersistentPropSet = eaPersistentPropSet;
			_eaLeafPropSet = eaLeafPropSet;
			_eaRootPropSet = eaRootPropSet;
			_isEaInterface = isEaInterface;
			_associationClass = associationClass;
			_selfInherited = selfInherited;
			_isEaEnumeration = isEaEnumeration;
		}

		/** Constructor. */
		public Data(UmlStructure.Data data, boolean isAbstract, boolean eaPersistentPropSet,
				boolean eaLeafPropSet, boolean eaRootPropSet, boolean isEaInterface,
				boolean associationClass, boolean selfInherited, boolean isEaEnumeration) {
			this(data.isSelfDependent(), isAbstract, eaPersistentPropSet, eaLeafPropSet,
					eaRootPropSet, isEaInterface, associationClass, selfInherited, isEaEnumeration);
		}

		public boolean isAbstract() {
			return _isAbstract;
		}

		public boolean isEaPersistentPropSet() {
			return _eaPersistentPropSet;
		}

		public boolean isEaLeafPropSet() {
			return _eaLeafPropSet;
		}

		public boolean isEaRootPropSet() {
			return _eaRootPropSet;
		}

		public boolean isEaInterface() {
			return _isEaInterface;
		}

		public boolean isAssociationClass() {
			return _associationClass;
		}

		public boolean isSelfInherited() {
			return _selfInherited;
		}

		public boolean isEaEnumeration() {
			return _isEaEnumeration;
		}
	}

	private final UmlPackage _containingPackage;
	private final Data _data;

	private final CimKind _cimKind;
	private final Iec61850Kind _iec61850Kind;

	private final Map<String, UmlConstraint> _constraints = new LinkedHashMap<String, UmlConstraint>();
	private final Collection<UmlAttribute> _attributes = new LinkedHashSet<UmlAttribute>();
	private final Collection<UmlOperation> _operations = new LinkedHashSet<UmlOperation>();
	private final Collection<UmlAssociation> _associationsAsSource = new LinkedHashSet<UmlAssociation>();
	private final Collection<UmlAssociation> _associationsAsTarget = new LinkedHashSet<UmlAssociation>();
	private final Collection<UmlAssociation> _associationsAsSourceAndTarget = new LinkedHashSet<UmlAssociation>();
	private final Collection<UmlClass> _superclasses = new LinkedHashSet<UmlClass>();
	private final Collection<UmlClass> _subclasses = new LinkedHashSet<UmlClass>();
	private final Collection<UmlClass> _classesUsingMeInAttributes = new LinkedHashSet<UmlClass>();
	private final Collection<UmlClass> _classesIUseInAttributes = new LinkedHashSet<UmlClass>();
	private final Collection<UmlClass> _classesUsingMeInOperationSignature = new LinkedHashSet<UmlClass>();
	private final Collection<UmlClass> _classesIUseInOperationSignature = new LinkedHashSet<UmlClass>();

	/** Constructs minimal root or stereotyped instance - useful for testing. */
	static UmlClass basic(UmlPackage containingPackage, String name, String... stereotypes) {
		return new UmlClass(containingPackage,
				new UmlObjectData(name, new UmlStereotype(stereotypes)), Data.empty());
	}

	/** Constructs minimal instance with a superclass - useful for testing. */
	static UmlClass basic(UmlPackage containingPackage, UmlClass superclass, String name) {
		Collection<UmlClass> supers = new LinkedHashSet<UmlClass>();
		supers.add(superclass);
		return new UmlClass(containingPackage, supers, new UmlObjectData(name), Data.empty());
	}

	/** Constructs minimal instance with a superclass and stereotype - useful for testing. */
	static UmlClass basic(UmlPackage containingPackage, UmlClass superclass, String name,
			String stereotype) {
		Collection<UmlClass> supers = new LinkedHashSet<UmlClass>();
		supers.add(superclass);
		return new UmlClass(containingPackage, supers,
				new UmlObjectData(name, new UmlStereotype(stereotype)), Data.empty());
	}

	/** Constructs minimal instance with 2 superclasses - useful for testing. */
	static UmlClass basic(UmlPackage containingPackage, Collection<UmlClass> supers, String name) {
		return new UmlClass(containingPackage, supers, new UmlObjectData(name), Data.empty());
	}

	/**
	 * Creates a class without superclasses; convenient for stereotyped and root classes. See
	 * {@link #UmlClass(UmlPackage, Collection, UmlObjectData, Data)}.
	 */
	public UmlClass(UmlPackage containingPackage, UmlObjectData objData, Data data) {
		this(containingPackage, new LinkedHashSet<UmlClass>(), objData, data);
	}

	/**
	 * Creates the instance and adds itself to the <code>containingPackage</code>, and as child to
	 * every object from <code>superclasses</code>. After creating this object, you may want to add
	 * tagged values, constraints, attributes, associations and operations (as well as other objects
	 * - see {@link UmlStructure#UmlStructure(UmlObjectData, UmlStructure.Data)}).
	 *
	 * @param containingPackage
	 *            parent UML package.
	 * @param superclasses
	 *            list of superclasses; could be empty but not null.
	 * @param objData
	 *            common data for any {@link UmlObject}.
	 * @param data
	 *            data proper to {@link UmlClass}.
	 */
	public UmlClass(UmlPackage containingPackage, Collection<UmlClass> superclasses,
			UmlObjectData objData, Data data) {
		super(objData, data);

		Util.ensureNotNull(containingPackage, "containingPackage");
		Util.ensureNotNull(superclasses, "superclasses");
		Util.ensureNotNull(data, "data");

		_containingPackage = containingPackage;
		_superclasses.addAll(superclasses);
		_data = data;

		for (UmlClass sup : superclasses) {
			sup._subclasses.add(this);
		}

		if (getNature() == Nature.CIM) {
			_cimKind = initCimKind();
			_iec61850Kind = null;
		} else {
			_cimKind = null;
			boolean isFunction = containingPackage.isInOrUnderPackage(UmlClass.TAG_FUNCTIONS);
			_iec61850Kind = initIec61850KindFromStereotypeOrPackage(isEaInterface(), isFunction,
					isEaEnumeration());
		}

		containingPackage.addClass(this);

		_logger.trace(String.format("created %s", toString()));
	}

	/**
	 * Reserved for use by {@link UmlModel} only: Creates the "null" class in the "null"
	 * <code>nullModelPackage</code>, for invalid types read from the model repository; it has no
	 * side-effects on the model itself.
	 *
	 * @param nullModelPackage
	 */
	UmlClass(UmlPackage nullModelPackage) {
		super(new UmlObjectData("Null" + nullModelPackage.getNature()), Data.empty());

		Util.ensureNotNull(nullModelPackage, "nullModelPackage");
		if (nullModelPackage.getKind() != UmlPackage.Kind.NULL_MODEL) {
			throw new IllegalArgumentException(
					"This ctor should be called by UmlModel" + " for unknown classes only.");
		}

		_containingPackage = nullModelPackage;
		_data = Data.empty();

		if (nullModelPackage.getNature() == Nature.CIM) {
			_cimKind = CimKind.NULL_CIM;
			_iec61850Kind = null;
		} else {
			_cimKind = null;
			_iec61850Kind = Iec61850Kind.NULL_61850;
		}

		nullModelPackage.addClass(this);

		_logger.trace(String.format("created %s", toString()));
	}

	/** Depends on {@link #getStereotype()} and {@link #getSuperclasses()}. */
	private CimKind initCimKind() {
		if (!getStereotype().isEmpty()) {
			if (getStereotype().contains(UmlClass.CimKind.PRIM.getValue())) {
				return UmlClass.CimKind.PRIM;
			} else if (getStereotype().contains(UmlClass.CimKind.DT.getValue())
					|| isWithOldDatatypeStereotype()) {
				return UmlClass.CimKind.DT;
			} else if (getStereotype().contains(UmlClass.CimKind.ENUM.getValue())) {
				return UmlClass.CimKind.ENUM;
			} else if (getStereotype().contains(UmlClass.CimKind.COMP.getValue())) {
				return UmlClass.CimKind.COMP;
			}
		}
		if (getSuperclasses().isEmpty()) {
			return UmlClass.CimKind.ROOT_CLASS;
		}
		return UmlClass.CimKind.CLASS;
	}

	private Iec61850Kind initIec61850KindFromStereotypeOrPackage(boolean isInterface,
			boolean isFunction, boolean isEaEnum) {
		if (isInterface || getStereotype().contains(UmlStereotype.EA_INTERFACE)) {
			// EA has special type for their Interface, and we can tag classes with stereotype
			return UmlClass.Iec61850Kind.IF;
		} else if (isFunction) {
			// all 61850 functions are contained in a separate package:
			return UmlClass.Iec61850Kind.FUNCTION;
		} else if (has61850StereotypesOtherThanInfDeprAdminStatistics() || isEaEnum) {
			if (getStereotype().contains(UmlStereotype.ENUMERATION) || isEaEnum) {
				if (getStereotype().contains(UmlStereotype.PACKED)) {
					return UmlClass.Iec61850Kind.PACKED_ENUM;
				} else if (getStereotype().contains(UmlStereotype.ABBR)) {
					return UmlClass.Iec61850Kind.ABBR_ENUM;
				} else if (getStereotype().contains(UmlStereotype.COND)) {
					return UmlClass.Iec61850Kind.COND_ENUM;
				} else {
					return UmlClass.Iec61850Kind.ENUM;
				}
			} else if (getStereotype().contains(UmlStereotype.PACKED)) {
				return UmlClass.Iec61850Kind.PACKED_BASIC;
			} else if (getStereotype().contains(UmlStereotype.STRUCTURED)) {
				return UmlClass.Iec61850Kind.STRUCTURED;
			} else if (getStereotype().contains(UmlStereotype.BASIC)) {
				return UmlClass.Iec61850Kind.BASIC;
			} else {
				return UmlClass.Iec61850Kind.UNKNOWN_61850;
			}
		} else {
			// ------------------- determining kind through inheritance -------------------
			if (isOrHasSuperclass(UmlClass.Iec61850Kind.PACKED_ENUM_DA.getValue())) {
				return Iec61850Kind.PACKED_ENUM_DA;
			} else if (isOrHasSuperclass(UmlClass.Iec61850Kind.ENUM_DA.getValue())) {
				return UmlClass.Iec61850Kind.ENUM_DA;
			} else if (isOrHasSuperclass(UmlClass.Iec61850Kind.PACKED_PRIM_DA.getValue())) {
				return UmlClass.Iec61850Kind.PACKED_PRIM_DA;
			} else if (isOrHasSuperclass(UmlClass.Iec61850Kind.PRIM_DA.getValue())) {
				return UmlClass.Iec61850Kind.PRIM_DA;
			} else if (isOrHasSuperclass(UmlClass.Iec61850Kind.COMP_DA.getValue())) {
				return UmlClass.Iec61850Kind.COMP_DA;
			} else if (determineIsSpecificFcda(UmlClass.Iec61850Kind.CODED_ENUM_FCDA.getValue())) {
				return UmlClass.Iec61850Kind.CODED_ENUM_FCDA;
			} else if (determineIsSpecificFcda(UmlClass.Iec61850Kind.ENUM_FCDA.getValue())) {
				return UmlClass.Iec61850Kind.ENUM_FCDA;
			} else if (determineIsSpecificFcda(UmlClass.Iec61850Kind.PACKED_LIST_FCDA.getValue())) {
				return UmlClass.Iec61850Kind.PACKED_LIST_FCDA;
			} else if (determineIsSpecificFcda(UmlClass.Iec61850Kind.COMP_FCDA.getValue())) {
				return UmlClass.Iec61850Kind.COMP_FCDA;
			} else if (isOrHasSuperclass(UmlClass.Iec61850Kind.FCDA.getValue())) {
				return UmlClass.Iec61850Kind.FCDA;
			} else if (determineIsEnumCdc()) {
				return UmlClass.Iec61850Kind.ENUM_CDC;
			} else if (determineIsTransCdc()) {
				return UmlClass.Iec61850Kind.TRANS_CDC;
			} else if (determineIsCtsCdc()) {
				return UmlClass.Iec61850Kind.CTS_CDC;
			} else if (isOrHasSuperclass(UmlClass.Iec61850Kind.PRIM_CDC.getValue())) {
				return UmlClass.Iec61850Kind.PRIM_CDC;
			} else if (isOrHasSuperclass(UmlClass.Iec61850Kind.COMP_CDC.getValue())) {
				return UmlClass.Iec61850Kind.COMP_CDC;
			} else if (isOrHasSuperclass(UmlClass.Iec61850Kind.LN.getValue())) {
				return UmlClass.Iec61850Kind.LN;
			} else {
				if (!isVersionClass() && !isNamespaceClass()) {
					if (!isFrom72()) {
						_logger.error("unclassified 7-4/7-3 class: " + toString());
					} else {
						_logger.debug("unclassified 7-2 class: " + toString());
					}
				}
				return UmlClass.Iec61850Kind.OTHER_61850;
			}
		}
	}

	private boolean has61850StereotypesOtherThanInfDeprAdminStatistics() {
		Set<String> toIgnore = new HashSet<String>();
		toIgnore.add(UmlStereotype.INFORMATIVE);
		toIgnore.add(UmlStereotype.DEPRECATED);
		Set<String> tokensOtherThanInformativeAndDeprecated = getStereotype()
				.getTokensOtherThan(toIgnore);
		return !tokensOtherThanInformativeAndDeprecated.isEmpty()
				&& !getStereotype().contains(UmlStereotype.ADMIN)
				&& !getStereotype().contains(UmlStereotype.STATISTICS);
	}

	private boolean determineIsSpecificFcda(String name) {
		if (!isOrHasSuperclass(UmlClass.Iec61850Kind.FCDA.getValue())) {
			return false;
		}
		if (getName().startsWith(name)) {
			return true;
		}
		List<String> superclassChainNames = AbstractUmlObject
				.collectNames(getAllSuperclassesFlattened(false));
		for (String supName : superclassChainNames) {
			if (supName.startsWith(name)) {
				return true;
			}
		}
		return false;
	}

	private boolean determineIsEnumCdc() {
		for (String sup : UmlClass.SUPER_ENUM_LNS) {
			if (isOrHasSuperclass(sup)) {
				return true;
			}
		}
		return false;
	}

	private boolean determineIsTransCdc() {
		return getName().endsWith(UML.SUFF_Transient) && hasSuperclass(UML.SUPER_CDC);
	}

	private boolean determineIsCtsCdc() {
		return isOrHasSuperclass(UML.CTS);
	}

	/**
	 * Starting from direct superclasses, returns all the superclasses up to all the roots (no
	 * explicit knowledge of multiple inheritance or branching due to it).
	 */
	public List<UmlClass> getAllSuperclassesFlattened(boolean skipInformative) {
		List<UmlClass> result = new ArrayList<UmlClass>();
		for (UmlClass sup : _superclasses) {
			if (!(sup.isInformative() && skipInformative)) {
				result.add(sup);
			}
			result.addAll(sup.getAllSuperclassesFlattened(skipInformative));
		}
		return result;
	}

	// --------------------

	public UmlPackage getContainingPackage() {
		return _containingPackage;
	}

	public boolean isNullClass() {
		return getKind() == CimKind.NULL_CIM || getKind() == Iec61850Kind.NULL_61850;
	}

	public boolean isVersionClass() {
		UmlPackage p = getContainingPackage();
		return getName()
				.equals(VersionInfo.getExpectedVersionClassName(p.getNature(), p.getName()));
	}

	public boolean isNamespaceClass() {
		UmlPackage p = getContainingPackage();
		return getName()
				.equals(NamespaceInfo.getExpectedNamespaceClassName(p.getNature(), p.getName()));
	}

	public boolean isAbstract() {
		return _data.isAbstract();
	}

	public boolean isWithOldDatatypeStereotype() {
		return getStereotype().contains(UmlStereotype.OLD_DATATYPE);
	}

	public boolean isEaPersistentPropSet() {
		return _data.isEaPersistentPropSet();
	}

	public boolean isEaLeafPropSet() {
		return _data.isEaLeafPropSet();
	}

	public boolean isEaRootPropSet() {
		return _data.isEaRootPropSet();
	}

	public boolean isEaInterface() {
		return _data.isEaInterface();
	}

	public boolean isEaEnumeration() {
		return _data.isEaEnumeration();
	}

	public boolean isAssociationClass() {
		return _data.isAssociationClass();
	}

	public boolean isSelfInherited() {
		return _data.isSelfInherited();
	}

	/**
	 * Initialised from tagged value, applicable to IEC61850-5 classes only; null if no tag defined.
	 */
	public String getRsName() {
		return getTaggedValues().get(UML.TVN_rsName);
	}

	/**
	 * Initialised from tagged value, applicable to IEC61850-5 classes only; null if no tag defined.
	 */
	public String getIeeeRef() {
		return getTaggedValues().get(UML.TVN_ieeeRef);
	}

	/**
	 * Initialised from tagged value, applicable to IEC61850-5 classes only; null if no tag defined.
	 */
	public String getIecRef() {
		return getTaggedValues().get(UML.TVN_iecRef);
	}

	/** Initialised from tagged value, applicable to 61850 CDC classes; null if no tag defined. */
	public String getCdcId() {
		return getTaggedValues().get(UML.TVN_cdcId);
	}

	/** Initialised from tagged value, applicable to 61850 7-2 classes; null if no tag defined. */
	public String getOldName() {
		return getTaggedValues().get(UML.TVN_oldName);
	}

	/** (61850) Returns whether this class needs alias, for doc generation purposes. */
	public boolean needsAlias() {
		UmlPackage p = getContainingPackage();
		if (p.getNature() == Nature.CIM) {
			return false;
		}
		Collection<String> extTitles = getModel().getCfg().getValidationIec61850PackagesExtTitle();
		for (String parentName : extTitles) {
			if (p.isInOrUnderPackage(parentName)) {
				return true;
			}
		}
		return false;
	}

	/** (61850) Returns whether this class needs tagged values, for doc generation purposes. */
	public boolean needsTags() {
		UmlPackage p = getContainingPackage();
		if (p.getNature() == Nature.CIM) {
			return false;
		}
		return p.isInOrUnderPackage(getModel().getCfg().getValidationIec61850PackageLnMaps());
	}

	/**
	 * (61850) Returns whether this class has stereotype
	 * {@value org.tanjakostic.jcleancim.model.UmlStereotype#ADMIN}
	 */
	public boolean isAdmin() {
		return getStereotype().contains(UmlStereotype.ADMIN);
	}

	/**
	 * (61850) Returns whether this class or any of its superclasses has stereotype
	 * {@value org.tanjakostic.jcleancim.model.UmlStereotype#STATISTICS}
	 */
	public boolean isUsableForStatistics() {
		if (getStereotype().contains(UmlStereotype.STATISTICS)) {
			return true;
		}
		for (UmlClass c : getSuperclasses()) {
			if (c.getStereotype().contains(UmlStereotype.STATISTICS)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * (61850) Returns whether this class inherits from
	 * {@value org.tanjakostic.jcleancim.model.UML#StatisticsLN}
	 */
	public boolean inheritsFromStatisticsLN() {
		return hasSuperclass(UML.StatisticsLN);
	}

	/**
	 * (61850) Returns whether this class is itself or inherits from
	 * {@value org.tanjakostic.jcleancim.model.UML#StatisticsLN}
	 */
	public boolean isOrInheritsFromStatisticsLN() {
		if (UML.StatisticsLN.equals(getName())) {
			return true;
		}
		return hasSuperclass(UML.StatisticsLN);
	}

	// ---------------

	private CimKind getCimKind() {
		return _cimKind;
	}

	private Iec61850Kind getIec61850Kind() {
		return _iec61850Kind;
	}

	/** CIM */
	public boolean isPrimitive() {
		return getCimKind() == CimKind.PRIM;
	}

	/** CIM and IEC61850 - simple enumeration with no other stereotypes */
	public boolean isEnumeration() {
		return getCimKind() == CimKind.ENUM || getIec61850Kind() == Iec61850Kind.ENUM;
	}

	/** CIM */
	public boolean isDatatype() {
		return getCimKind() == CimKind.DT;
	}

	/** CIM */
	public boolean isCompound() {
		return getCimKind() == CimKind.COMP;
	}

	/** CIM non-root class without stereotype. */
	public boolean isClass() {
		return getCimKind() == CimKind.CLASS;
	}

	/** CIM class with predefined stereotype, or null CIM class. */
	private boolean isUsedAsTypeForCimAttributes() {
		return getNature() == Nature.CIM && (isNullClass() || isDatatype() || isEnumeration()
				|| isPrimitive() || isCompound());
	}

	/** IEC 61850 */
	public boolean isInterface() {
		return getIec61850Kind() == Iec61850Kind.IF;
	}

	/** IEC 61850 */
	public boolean isCodedEnum() {
		return getIec61850Kind() == Iec61850Kind.PACKED_ENUM;
	}

	/** IEC 61850 */
	public boolean isAbbreviationEnumeration() {
		return getIec61850Kind() == Iec61850Kind.ABBR_ENUM;
	}

	/** IEC 61850 */
	public boolean isConditionEnumeration() {
		return getIec61850Kind() == Iec61850Kind.COND_ENUM;
	}

	/** IEC 61850 */
	public boolean isPackedList() {
		return getIec61850Kind() == Iec61850Kind.PACKED_BASIC;
	}

	/** IEC 61850 (P_*) */
	public boolean isBasic() {
		return getIec61850Kind() == Iec61850Kind.BASIC;
	}

	/** IEC 61850 (S_*); at present, S_Originator only */
	public boolean isStructured() {
		return getIec61850Kind() == Iec61850Kind.STRUCTURED;
	}

	/** IEC 61850 */
	public boolean isCodedEnumDA() {
		return getIec61850Kind() == Iec61850Kind.PACKED_ENUM_DA;
	}

	/** IEC 61850 */
	public boolean isEnumDA() {
		return getIec61850Kind() == Iec61850Kind.ENUM_DA;
	}

	/** IEC 61850 */
	public boolean isPackedListDA() {
		return getIec61850Kind() == Iec61850Kind.PACKED_PRIM_DA;
	}

	/** IEC 61850 */
	public boolean isPrimitiveDA() {
		return getIec61850Kind() == Iec61850Kind.PRIM_DA;
	}

	/** IEC 61850 */
	public boolean isComposedDA() {
		return getIec61850Kind() == Iec61850Kind.COMP_DA;
	}

	/** IEC 61850 (like PackedEnumFCDA_ST_dchg or DpStatus_ST_dchg) */
	public boolean isCodedEnumFCDA() {
		return getIec61850Kind() == Iec61850Kind.CODED_ENUM_FCDA;
	}

	/** IEC 61850 (like EnumDA_ST_dchg or CurveChar_SP_dchg) */
	public boolean isEnumFCDA() {
		return getIec61850Kind() == Iec61850Kind.ENUM_FCDA;
	}

	/** IEC 61850 (like Quality_ST_dchg) */
	public boolean isPackedListFCDA() {
		return getIec61850Kind() == Iec61850Kind.PACKED_LIST_FCDA;
	}

	/** IEC 61850 (like Analog_MX_dchg) */
	public boolean isComposedFCDA() {
		return getIec61850Kind() == Iec61850Kind.COMP_FCDA;
	}

	/** IEC 61850 (like PrimitiveCDC or SPS) */
	public boolean isPrimitiveCDC() {
		return getIec61850Kind() == Iec61850Kind.PRIM_CDC;
	}

	/** IEC 61850 (like ENS and its subtypes, derived from SPS) */
	public boolean isEnumCDC() {
		return getIec61850Kind() == Iec61850Kind.ENUM_CDC;
	}

	/** IEC 61850 (like ComposedCDC or WYE) */
	public boolean isComposedCDC() {
		return getIec61850Kind() == Iec61850Kind.COMP_CDC;
	}

	/** IEC 61850 (like SPCTransient, derived from SPC) */
	public boolean isTransientCDC() {
		return getIec61850Kind() == Iec61850Kind.TRANS_CDC;
	}

	/** IEC 61850 (like CTSINT32, derived from CTS) */
	public boolean isTrackingDerivedCDC() {
		return getIec61850Kind() == Iec61850Kind.CTS_CDC;
	}

	/** IEC 61850 (like DA or Vector) */
	public boolean isAnyDA() {
		return isOrHasSuperclass(UML.SUPER_DA);
	}

	/** IEC 61850 (like FCDA_ST_dchg or INT32_ST_dchg) */
	public boolean isAnyFCDA() {
		return isOrHasSuperclass(UML.SUPER_FCDA);
	}

	/** IEC 61850 (FCDA from the meta-model) */
	public boolean isFCDA() {
		return isOrHasSuperclass(UML.SUPER_FCDA);
	}

	/** IEC 61850 (like CDC or SPS) */
	public boolean isAnyCDC() {
		return isOrHasSuperclass(UML.SUPER_CDC);
	}

	/** IEC 61850 */
	public boolean isAnyLN() {
		return getIec61850Kind() == Iec61850Kind.LN;
	}

	/** IEC61850 - e.g., StatisticsLN or LPHD */
	public boolean is74LN() {
		return isAnyLN() && !isFromMetaModel();
	}

	/** IEC 61850 */
	public boolean isFunction() {
		return getIec61850Kind() == Iec61850Kind.FUNCTION;
	}

	/** IEC 61850 */
	public boolean isOther() {
		return getIec61850Kind() == Iec61850Kind.OTHER_61850;
	}

	/** IEC 61850 - with an unknown stereotype */
	public boolean isUnknown() {
		return getIec61850Kind() == Iec61850Kind.UNKNOWN_61850;
	}

	/** IEC 61850 */
	private boolean isUsedAsTypeForIec61850Attributes() {
		return getNature() == Nature.IEC61850 && !(isNullClass() || isInterface() || isAnyLN()
				|| isAbbreviationEnumeration() || isConditionEnumeration() || isCodedEnum()
				|| isFunction() || isAbstract() || isEnumCDC() || isOther() || isUnknown()
				|| !getOperationAfferentClasses().isEmpty());
	}

	public boolean isUsedAsTypeForAttributes() {
		return getNature() == Nature.CIM ? isUsedAsTypeForCimAttributes()
				: isUsedAsTypeForIec61850Attributes();
	}

	/**
	 * Returns whether this is an enumerated type. For CIM, the result is the same as by
	 * {@link #isEnumeration()}, while for IEC 61850, this method returns true also for those
	 * enumerations that have additional stereotype.
	 */
	public boolean isEnumeratedType() {
		return isEnumeration() || isCodedEnum() || isAbbreviationEnumeration()
				|| isConditionEnumeration();
	}

	/** IEC 61850 - returns whether the class is from the model supporting IEC 61850-7-2. */
	public boolean isFrom72() {
		return getContainingPackage()
				.isInOrUnderPackage(getModel().getCfg().getValidationIec61850Package72Top());
	}

	/** IEC 61850 - returns whether the class is from the meta-model (package and subpackages). */
	public boolean isFromMetaModel() {
		return getContainingPackage()
				.isInOrUnderPackage(getModel().getCfg().getValidationIec61850PackageMetaModel());
	}

	// --------------------- (hand-drawn) dependencies - downcasting ----------------------

	/** Returns all classes that I depend on through an explicit UML dependency in the model. */
	public Collection<UmlClass> collectDependencyEfferentClasses() {
		return downcast(collectDependencyEfferentStructures());
	}

	private static Collection<UmlClass> downcast(Collection<UmlStructure> items) {
		Collection<UmlClass> result = new LinkedHashSet<UmlClass>();
		for (UmlStructure struct : items) {
			result.add((UmlClass) struct);
		}
		return result;
	}

	// ---------------------- inheritance ---------------------

	/** Returns direct superclasses of this class. */
	public Collection<UmlClass> getSuperclasses() {
		return Collections.unmodifiableCollection(_superclasses);
	}

	/** Returns direct subclasses of this class. */
	public Collection<UmlClass> getSubclasses() {
		return Collections.unmodifiableCollection(_subclasses);
	}

	/** Returns whether <code>supName</code> is one of superclasses in the inheritance chain. */
	public boolean hasSuperclass(String supName) {
		for (UmlClass sup : _superclasses) {
			if (supName.equals(sup.getName())) {
				return true;
			}
			return sup.hasSuperclass(supName);
		}
		return false;
	}

	/**
	 * Returns whether <code>name</code> is this class or one of its superclasses in the inheritance
	 * chain.
	 */
	public boolean isOrHasSuperclass(String name) {
		return name.equals(getName()) || hasSuperclass(name);
	}

	// ----------------- operations --------------------------

	/** For testing only: Adds operation with default data. */
	final UmlOperation addOperation(UmlClass returnType, String name) {
		return addOperation(returnType, new UmlObjectData(name), UmlOperation.Data.empty());
	}

	/**
	 * Creates from arguments an operation, adds it to itself and to the model, populates
	 * afferent/efferent collections for this and for non-null <code>returnType</code>, and returns
	 * the newly created object. After that, you may want to add tagged values, exceptions, and
	 * parameters to the new operation.
	 * <p>
	 * For <code>returnType</code> you should provide null in case the operation returns void. If
	 * the return type could not be determined from the model repository, provide the "null" class
	 * obtained with {@link UmlModel#getNullClasses()} as argument for <code>returnType</code>.
	 * <p>
	 * In case the operation with the same UUID has already been added, returns the existing
	 * operation immediately.
	 *
	 * @param returnType
	 *            return type, null if the operation returns void
	 * @param objData
	 * @param data
	 * @throws IllegalArgumentException
	 *             if <code>returnType</code> is null but data.kind says it does not return void, or
	 *             if this and non-null <code>returnType</code> are from different models.
	 */
	public UmlOperation addOperation(UmlClass returnType, UmlObjectData objData,
			UmlOperation.Data data) {
		Util.ensureNotNull(objData, "objData");

		UmlOperation existing = AbstractUmlObject.findWithSameUuidAndLog(Level.WARN, this,
				_operations, objData.getUuid());
		if (existing != null) {
			return existing;
		}

		UmlOperation op = new UmlOperation(this, returnType, objData, data);
		_operations.add(op);
		getModel().addOperation(op);
		if (returnType != null && returnType != this) {
			returnType._classesUsingMeInOperationSignature.add(this);
			this._classesIUseInOperationSignature.add(returnType);
		}

		return op;
	}

	/** Returns native operations. */
	public Collection<UmlOperation> getOperations() {
		return Collections.unmodifiableCollection(_operations);
	}

	/** Returns inherited operations. */
	public Set<UmlOperation> getInheritedOperations() {
		Set<UmlOperation> result = new LinkedHashSet<UmlOperation>();
		for (UmlClass sup : getSuperclasses()) {
			result.addAll(sup.getOperations());
			result.addAll(sup.getInheritedOperations());
		}
		return result;
	}

	/** Returns classes that have operation parameters that use me as their type. */
	public Collection<UmlClass> getOperationAfferentClasses() {
		return Collections.unmodifiableCollection(_classesUsingMeInOperationSignature);
	}

	/** Returns classes that my operation parameters and exceptions use as their type. */
	public Collection<UmlClass> getOperationEfferentClasses() {
		return Collections.unmodifiableCollection(_classesIUseInOperationSignature);
	}

	// ----------------------- constraints --------------------

	/**
	 * Creates from arguments a constraint, adds it to itself, and returns the newly created object.
	 * In case the constraint with the same UUID has already been added, returns the existing item
	 * immediately. In case the constraint with the same name has already been added, overwrites the
	 * old constraint.
	 *
	 * @param objData
	 * @param data
	 */
	public UmlConstraint addConstraint(UmlObjectData objData, UmlConstraint.Data data) {
		Util.ensureNotNull(objData, "objData");

		UmlConstraint existing = AbstractUmlObject.findWithSameUuidAndLog(Level.WARN, this,
				_constraints.values(), objData.getUuid());
		if (existing != null) {
			return existing;
		}

		UmlConstraint constraint = new UmlConstraint(this, objData, data);
		if (_constraints.containsKey(constraint.getName())) {
			_logger.warn(String.format("Overwriting constraint '%s'", constraint.getName()));
		}
		_constraints.put(constraint.getName(), constraint);

		return constraint;
	}

	/** Returns potentially empty list of constraints involbing <code>attrName</code>. */
	List<UmlConstraint> findConstraintsForAttribute(String attrName) {
		List<UmlConstraint> result = new ArrayList<UmlConstraint>();
		for (UmlConstraint c : _constraints.values()) {
			if (c.getAttrNames().contains(attrName)) {
				result.add(c);
			}
		}
		return result;
	}

	public Map<String, UmlConstraint> getConstraints() {
		return Collections.unmodifiableMap(_constraints);
	}

	// ----------------------- attributes --------------------

	/** For testing only: Adds attribute with default data. */
	final UmlAttribute addAttribute(UmlClass type, String name, String... stereotypes) {
		return addAttribute(type, new UmlObjectData(name, new UmlStereotype(stereotypes)),
				UmlAttribute.Data.empty());
	}

	static UmlObjectData createLiteralObjData(String name) {
		return new UmlObjectData(name, new UmlStereotype(UmlStereotype.ENUM));
	}

	/** For testing only: Adds literal with default data. */
	final UmlAttribute addLiteral(String name) {
		return addAttribute(null, createLiteralObjData(name), UmlAttribute.Data.empty());
	}

	/** For testing only: Adds literal with init value and with default data. */
	final UmlAttribute addLiteral(String name, String initValue) {
		return addAttribute(null, createLiteralObjData(name),
				UmlAttribute.Data.withInitValue(initValue));
	}

	/**
	 * Creates from arguments an attribute or enumeration literal, adds it to itself and to the
	 * model, populates afferent/efferent collections for this and for <code>type</code>, and
	 * returns the newly created object. After that, you may want to add tagged values and
	 * constraints to the new attribute.
	 * <p>
	 * For <code>type</code> that cannot be determined from the model repository, provide the "null"
	 * class obtained with {@link UmlModel#getNullClasses()} as argument.
	 * <p>
	 * In case the attribute with the same UUID has already been added, returns the existing
	 * attribute immediately.
	 *
	 * @param type
	 *            type of the attribute if it is not a literal, null otherwise.
	 * @param objData
	 * @param data
	 * @throws IllegalArgumentException
	 *             if this and non-null <code>type</code> are from different models,or if
	 *             <code>type</code> is null and this not an enumerated type.
	 */
	public UmlAttribute addAttribute(UmlClass type, UmlObjectData objData, UmlAttribute.Data data) {
		Util.ensureNotNull(data, "data");
		UmlAttribute existing = AbstractUmlObject.findWithSameUuidAndLog(Level.WARN, this,
				_attributes, objData.getUuid());
		if (existing != null) {
			return existing;
		}

		UmlAttribute attr = new UmlAttribute(this, type, objData, data);
		_attributes.add(attr);
		getModel().addAttribute(attr);

		if (type != null) {
			attr.getType()._classesUsingMeInAttributes.add(this);
			this._classesIUseInAttributes.add(attr.getType());
		} else {
			// FIXME: for literals, find initial values
		}

		return attr;
	}

	/** Returns native attributes. */
	public Collection<UmlAttribute> getAttributes() {
		return Collections.unmodifiableCollection(_attributes);
	}

	/** Returns inherited attributes. */
	public Set<UmlAttribute> getInheritedAttributes() {
		Set<UmlAttribute> result = new LinkedHashSet<UmlAttribute>();
		for (UmlClass sup : getSuperclasses()) {
			result.addAll(sup.getAttributes());
			result.addAll(sup.getInheritedAttributes());
		}
		return result;
	}

	/** Returns classes that have attributes that use me as their type. */
	public Collection<UmlClass> getAttributeAfferentClasses() {
		return Collections.unmodifiableCollection(_classesUsingMeInAttributes);
	}

	/** Returns classes that my attributes use as their type. */
	public Collection<UmlClass> getAttributeEfferentClasses() {
		return Collections.unmodifiableCollection(_classesIUseInAttributes);
	}

	/** Returns (native) attributes with <code>attrName</code>. */
	public Set<UmlAttribute> findAttributes(String attrName) {
		return findAttributes(attrName, UmlClass.InheritedKind.own);
	}

	/** Returns (native) attributes whose type is <code>attrType</code>. */
	public Set<UmlAttribute> findAttributes(UmlClass attrType) {
		return findAttributes(attrType, UmlClass.InheritedKind.own);
	}

	/**
	 * Returns attributes with <code>attrName</code> selectively, according to inheritance criterion
	 * <code>inh</code>.
	 */
	public Set<UmlAttribute> findAttributes(String attrName, InheritedKind inh) {
		Set<UmlAttribute> result = new LinkedHashSet<UmlAttribute>();
		if (inh == InheritedKind.own || inh == InheritedKind.all) {
			for (UmlAttribute attr : _attributes) {
				if (attrName.equals(attr.getName())) {
					result.add(attr);
				}
			}
		}
		if (inh == InheritedKind.inherited || inh == InheritedKind.all) {
			for (UmlAttribute attr : getInheritedAttributes()) {
				if (attrName.equals(attr.getName())) {
					result.add(attr);
				}
			}
		}
		return result;
	}

	/**
	 * Returns attributes of type <code>attrType</code> selectively, according to inheritance
	 * criterion <code>inh</code>.
	 */
	public Set<UmlAttribute> findAttributes(UmlClass attrType, InheritedKind inh) {
		Set<UmlAttribute> result = new LinkedHashSet<UmlAttribute>();
		if (inh == InheritedKind.own || inh == InheritedKind.all) {
			for (UmlAttribute attr : _attributes) {
				if (attrType == attr.getType()) {
					result.add(attr);
				}
			}
		}
		if (inh == InheritedKind.inherited || inh == InheritedKind.all) {
			for (UmlAttribute attr : getInheritedAttributes()) {
				if (attrType == attr.getType()) {
					result.add(attr);
				}
			}
		}
		return result;
	}

	/** Returns (native) attributes indexed per their initial value. */
	public Map<String, List<UmlAttribute>> findAttributesPerInitialValue() {
		Map<String, List<UmlAttribute>> result = new TreeMap<>();
		for (UmlAttribute attr : getAttributes()) {
			String initVal = attr.getInitValue();
			if (!result.containsKey(initVal)) {
				result.put(initVal, new ArrayList<UmlAttribute>());
			}
			result.get(initVal).add(attr);
		}
		return result;
	}

	/**
	 * Returns (alphabetically) ordered initial values for (native) attributes; empty list in case
	 * there are no initial values.
	 */
	public Set<String> findInitialValuesOrdered() {
		Set<String> initValues = findAttributesPerInitialValue().keySet();
		if (initValues.size() == 1 && initValues.contains("")) {
			return Collections.emptySet();
		}
		return new TreeSet<String>(initValues);
	}

	// ------------------ attribute groups -------------------------

	// ------------------ associations -------------------------

	/**
	 * Creates from arguments an association with this as type of <code>sourceEnd</code>, adds it to
	 * both types of association ends and to the model, and returns the newly created object. After
	 * that, you may want to add tagged values to the new association.
	 * <p>
	 * In case the association with the same UUID has already been added to either type of
	 * association end, returns the existing association immediately.
	 * <p>
	 * It is the responsibility of the caller to call this method on the source end's type.
	 *
	 * @param sourceEnd
	 * @param targetEnd
	 * @param objData
	 * @param data
	 * @throws IllegalArgumentException
	 *             if the types of <code>sourceEnd</code> and <code>targetEnd</code> are from
	 *             different models, or if this is not the type of <code>sourceEnd</code>.
	 */
	public UmlAssociation addAssociation(UmlAssociationEnd sourceEnd, UmlAssociationEnd targetEnd,
			UmlObjectData objData, UmlAssociation.Data data) {
		Util.ensureNotNull(sourceEnd, "sourceEnd");
		Util.ensureNotNull(targetEnd, "targetEnd");
		Util.ensureNotNull(data, "data");

		UmlClass source = sourceEnd.getType();
		UmlClass target = targetEnd.getType();

		if (this != source) {
			throw new IllegalArgumentException(
					String.format("I (%s) am not the type of source end" + " (%s).",
							this.getQualifiedName(), sourceEnd.getQualifiedName()));
		}
		UmlAssociation assoc = new UmlAssociation(sourceEnd, targetEnd, objData, data);

		if (source == target) {
			UmlAssociation existingSelf = AbstractUmlObject.findWithSameUuidAndLog(Level.WARN, this,
					_associationsAsSourceAndTarget, objData.getUuid());
			if (existingSelf != null) {
				return existingSelf;
			}

			_associationsAsSourceAndTarget.add(assoc);
		} else {
			UmlAssociation existingAsSource = AbstractUmlObject.findWithSameUuidAndLog(Level.WARN,
					this, _associationsAsSource, objData.getUuid());
			if (existingAsSource != null) {
				return existingAsSource;
			}

			UmlAssociation existingAsTarget = AbstractUmlObject.findWithSameUuidAndLog(Level.WARN,
					this, target._associationsAsTarget, objData.getUuid());
			if (existingAsTarget != null) {
				return existingAsTarget;
			}

			_associationsAsSource.add(assoc);
			target._associationsAsTarget.add(assoc);
		}

		getModel().addAssociation(assoc);

		return assoc;
	}

	public Collection<UmlAssociation> getAssociations() {
		List<UmlAssociation> result = new ArrayList<UmlAssociation>(_associationsAsSource);
		result.addAll(_associationsAsTarget);
		result.addAll(_associationsAsSourceAndTarget);
		return result;
	}

	Collection<UmlAssociation> getAssociationsAsSource() {
		return Collections.unmodifiableCollection(_associationsAsSource);
	}

	Collection<UmlAssociation> getAssociationsAsTarget() {
		return Collections.unmodifiableCollection(_associationsAsTarget);
	}

	Collection<UmlAssociation> getAssociationsAsSourceAndTarget() {
		return Collections.unmodifiableCollection(_associationsAsSourceAndTarget);
	}

	public Collection<UmlAssociation> getInheritedAssociations() {
		List<UmlAssociation> result = new ArrayList<UmlAssociation>();
		for (UmlClass sup : getSuperclasses()) {
			result.addAll(sup.getAssociations());
			result.addAll(sup.getInheritedAssociations());
		}
		return result;
	}

	/**
	 * For associations where I'm on the target end, returns the list of source end classes. The
	 * result may include this if the association is recursive (both ends of the same type).
	 * <p>
	 * Implementation note: If you call {@link #toString()} from within this method, ensure you add
	 * a condition to avoid recursion (because {@link #toString()} calls this method).
	 */
	List<UmlClass> getAssociationSourceEndClasses() {
		List<UmlAssociation> asTargets = new ArrayList<UmlAssociation>(_associationsAsTarget);
		asTargets.addAll(_associationsAsSourceAndTarget);
		List<UmlClass> result = new ArrayList<UmlClass>(asTargets.size());
		for (UmlAssociation assoc : asTargets) {
			UmlClass other = assoc.getSource();
			result.add(other);
		}
		return result;
	}

	/**
	 * For associations where I'm on the source end, returns the list of target end classes. The
	 * result may includes this if the association is recursive (both ends of the same type).
	 * <p>
	 * Implementation note: If you call {@link #toString()} from within this method, ensure you add
	 * a condition to avoid recursion (because {@link #toString()} calls this method).
	 */
	List<UmlClass> getAssociationTargetEndClasses() {
		List<UmlAssociation> asSources = new ArrayList<UmlAssociation>(_associationsAsSource);
		asSources.addAll(_associationsAsSourceAndTarget);
		List<UmlClass> result = new ArrayList<UmlClass>(asSources.size());
		for (UmlAssociation assoc : asSources) {
			UmlClass other = assoc.getTarget();
			result.add(other);
		}
		return result;
	}

	// ------------------ association ends -------------------------

	private List<UmlAssociationEndPair> getAssociationEndPairsAsSource() {
		List<UmlAssociationEndPair> result = new ArrayList<UmlAssociationEndPair>();
		for (UmlAssociation assoc : _associationsAsSource) {
			result.add(assoc.getEndsAsSource(true));
		}
		for (UmlAssociation assoc : _associationsAsSourceAndTarget) {
			result.add(assoc.getEndsAsSource(true));
		}
		return result;
	}

	private List<UmlAssociationEndPair> getAssociationEndPairsAsTarget() {
		List<UmlAssociationEndPair> result = new ArrayList<UmlAssociationEndPair>();
		for (UmlAssociation assoc : _associationsAsTarget) {
			result.add(assoc.getEndsAsSource(false));
		}
		for (UmlAssociation assoc : _associationsAsSourceAndTarget) {
			result.add(assoc.getEndsAsSource(false));
		}
		return result;
	}

	/**
	 * Returns pairs of association ends, from the perspective of this class (this end vs. other
	 * end) - convenient for documentation generation.
	 *
	 * @see UmlAssociationEndPair
	 */
	public List<UmlAssociationEndPair> getAssociationEndPairs() {
		List<UmlAssociationEndPair> result = new ArrayList<UmlAssociationEndPair>(
				getAssociationEndPairsAsSource());
		result.addAll(getAssociationEndPairsAsTarget());
		return result;
	}

	/**
	 * Returns inherited pairs of association ends, from the perspective of this class (this end vs.
	 * other end) - convenient for documentation generation.
	 *
	 * @see UmlAssociationEndPair
	 */
	public List<UmlAssociationEndPair> getInheritedAssociationEndPairs() {
		List<UmlAssociationEndPair> result = new ArrayList<UmlAssociationEndPair>();
		for (UmlClass sup : getSuperclasses()) {
			result.addAll(sup.getAssociationEndPairs());
			result.addAll(sup.getInheritedAssociationEndPairs());
		}
		return result;
	}

	/**
	 * Returns association ends with other classes.
	 */
	public List<UmlAssociationEnd> getOtherSideAssociationEnds() {
		List<UmlAssociationEnd> result = new ArrayList<UmlAssociationEnd>();
		List<UmlAssociationEndPair> pairs = getAssociationEndPairs();
		for (UmlAssociationEndPair pair : pairs) {
			result.add(pair.getOtherEnd());
		}
		return result;
	}

	/**
	 * Returns inherited association ends with other classes.
	 */
	public List<UmlAssociationEnd> getInheritedOtherSideAssociationEnds() {
		List<UmlAssociationEnd> result = new ArrayList<UmlAssociationEnd>();
		for (UmlClass sup : getSuperclasses()) {
			result.addAll(sup.getOtherSideAssociationEnds());
			result.addAll(sup.getInheritedOtherSideAssociationEnds());
		}
		return result;
	}

	// =========== org.tanjakostic.jcleancim.model.UmlStructure ============

	@Override
	public UmlModel getModel() {
		return getContainingPackage().getModel();
	}

	@Override
	public UmlStructure getContainer() {
		return getContainingPackage();
	}

	// =========== org.tanjakostic.jcleancim.model.UmlObject ============

	@Override
	public OwningWg getOwner() {
		return getContainingPackage().getOwner();
	}

	/**
	 * {@inheritDoc}
	 * <p>
	 * Returns own namespace initialised from tagged values if not empty. Otherwise, returns the
	 * namespace of the containing package.
	 */
	@Override
	public Namespace getNamespace() {
		Namespace ns = initFromTags();
		if (ns != Namespace.EMPTY) {
			return ns;
		}

		return getContainingPackage().getNamespace();
	}

	@Override
	public Nature getNature() {
		return getContainingPackage().getNature();
	}

	@Override
	public boolean isInformative() {
		return super.isInformative() || getContainingPackage().isInformative();
	}

	/**
	 * {@inheritDoc}
	 * <p>
	 * This implementation returns either the CIM of the IEC61850 kind, depending on the nature of
	 * the class.
	 */
	@Override
	public UmlKind getKind() {
		return (getNature() == Nature.CIM) ? getCimKind() : getIec61850Kind();
	}

	@Override
	public String getQualifiedName() {
		return getContainingPackage().getName() + PACKAGE_SEPARATOR + getName();
	}

	@Override
	public Set<String> getPredefinedTagNames() {
		List<String> resultList = (getNature() == Nature.CIM) ? CIM_TAGS : IEC61850_TAGS;
		return new LinkedHashSet<String>(resultList);
	}

	// =========== java.lang.Object ============

	@Override
	public String toString() {
		String qualifier = (isAbstract() && !isInterface()) ? "abstract class" : "";
		StringBuilder sb = new StringBuilder(toShortString(true, qualifier, true));

		if (_superclasses.size() > 0) {
			sb.append(", ").append(_superclasses.size()).append(" superclasses=");
			sb.append(collectNames(_superclasses));
		}
		if (_subclasses.size() > 0) {
			sb.append(", ").append(_subclasses.size()).append(" subclasses=");
			sb.append(collectNames(_subclasses));
		}
		if (_attributes.size() > 0) {
			String attr = (isEnumeration()) ? "enum literals" : "attributes";
			sb.append(", " + getAttributes().size() + " " + attr);
			if (isEnumeration()) {
				Set<String> codes = findInitialValuesOrdered();
				if (!codes.isEmpty()) {
					sb.append(" (codes used=").append(codes.toString()).append(")");
				}
			}
		}
		if (_operations.size() > 0) {
			sb.append(", " + _operations.size() + " operations");
		}
		if (getDiagrams().size() > 0) {
			sb.append(", " + getDiagrams().size() + " diagrams");
		}
		int assocCount = _associationsAsSource.size() + _associationsAsTarget.size()
				+ _associationsAsSourceAndTarget.size();
		if (assocCount > 0) {
			sb.append(", " + assocCount + " associations");
		}
		Collection<UmlStructure> depAfferentClasses = collectDependencyAfferentStructures();
		Collection<UmlClass> attrAfferentClasses = getAttributeAfferentClasses();
		Collection<UmlClass> opAfferentClasses = getOperationAfferentClasses();
		if ((depAfferentClasses.size() + attrAfferentClasses.size()
				+ opAfferentClasses.size()) > 0) {
			sb.append("; afferent classes:");
			if (depAfferentClasses.size() > 0) {
				sb.append(" ").append("byDep=").append(collectNames(depAfferentClasses));
			}
			if (attrAfferentClasses.size() > 0) {
				sb.append(" ").append("byAttr=").append(collectNames(attrAfferentClasses));
			}
			if (opAfferentClasses.size() > 0) {
				sb.append(" ").append("byOp=").append(collectNames(opAfferentClasses));
			}
			sb.append(";");
		}
		Collection<UmlStructure> depEfferentClasses = collectDependencyEfferentStructures();
		Collection<UmlClass> attrEfferentClasses = getAttributeEfferentClasses();
		Collection<UmlClass> opEfferentClasses = getOperationEfferentClasses();
		if ((depEfferentClasses.size() + attrEfferentClasses.size()
				+ opEfferentClasses.size()) > 0) {
			sb.append("; efferent classes:");
			if (depEfferentClasses.size() > 0) {
				sb.append(" ").append("byDep=").append(collectNames(depEfferentClasses));
			}
			if (attrEfferentClasses.size() > 0) {
				sb.append(" ").append("byAttr=").append(collectNames(attrEfferentClasses));
			}
			if (opEfferentClasses.size() > 0) {
				sb.append(" ").append("byOp=").append(collectNames(opEfferentClasses));
			}
		}

		List<String> assocForAsTargetClasses = collectQNames(getAssociationSourceEndClasses(),
				false);
		List<String> assocForAsSourceClasses = collectQNames(getAssociationTargetEndClasses(),
				false);
		if (assocForAsTargetClasses.size() != 0 || assocForAsSourceClasses.size() != 0) {
			sb.append("; associated classes (bi-directional):");
			if (assocForAsTargetClasses.size() > 0) {
				sb.append(" ").append("asTarget=").append(assocForAsTargetClasses.toString());
			}
			if (assocForAsSourceClasses.size() > 0) {
				sb.append(" ").append("asSource=").append(assocForAsSourceClasses.toString());
			}
		}

		// FIXME: remove after writing tests
		// if (getName().equals("ConductingEquipment") || getName().equals("XCBR")) {
		// _logger.info(" inherited attributes:");
		// for (UmlAttribute attr : getInheritedAttributes()) {
		// _logger.info(attr.toString());
		// }
		// _logger.info(" inherited associations:");
		// for (UmlAssociation assoc : getInheritedAssociations()) {
		// _logger.info(assoc.toString());
		// }
		// _logger.info(" inherited assoc. ends:");
		// for (UmlAssociationEndPair pair : getInheritedAssociationEnds()) {
		// _logger.info(pair.toString());
		// }
		// _logger.info(" inherited operations:");
		// for (UmlOperation op : getInheritedOperations()) {
		// _logger.info(op.toString());
		// }
		// }

		if (getConstraints().size() != 0) {
			sb.append(",").append(getConstraints().size()).append(" constraints: ");
			for (UmlConstraint c : getConstraints().values()) {
				sb.append(c.toString());
			}
		}

		if (getTaggedValues().size() != 0) {
			sb.append(", tags=").append(getTaggedValues().toString());
		}
		return sb.toString();
	}
}
