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
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;
import org.tanjakostic.jcleancim.common.Nature;
import org.tanjakostic.jcleancim.common.OwningWg;
import org.tanjakostic.jcleancim.util.ProgrammerErrorException;
import org.tanjakostic.jcleancim.util.Util;

/**
 * UML association end.
 * <p>
 * This class is more of a helper for {@link UmlAssociation}. We make it however implement
 * {@link UmlObject} to be able to use utility methods of {@link AbstractUmlObject}, but do not
 * store any instance in {@link UmlModel} - association ends are stored in associations only. Note
 * that after creation, several methods will return null before the containinig association gets
 * created with this instance as one of its ends (
 * {@link UmlAssociation#UmlAssociation(UmlAssociationEnd, UmlAssociationEnd, UmlObjectData, UmlAssociation.Data)}
 * ).
 * <p>
 * The owner of this end is determined as the owner of the type of the other end, and the nature is
 * the nature of the type of this end. Example: For association A (bRole) --- (aRole) B, if this is
 * aRole, its owner is the owner of A (because it is needed by A), and its nature is the nature of B
 * (because its type is B).
 *
 * @author tatjana.kostic@ieee.org
 * @version $Id: UmlAssociationEnd.java 34 2019-12-20 18:37:17Z dev978 $
 */
public class UmlAssociationEnd extends AbstractUmlObject {
	private static final Logger _logger = Logger.getLogger(UmlAssociationEnd.class.getName());

	/**
	 * Kind of aggregation for association end.
	 *
	 * @author tatjana.kostic@ieee.org
	 * @version $Id: UmlAssociationEnd.java 34 2019-12-20 18:37:17Z dev978 $
	 */
	public enum Kind implements UmlKind {
		COMPOS("composition", "composition", "AssociationEnd", "composition"),

		AGGREG("aggregation", "aggregation", "AssociationEnd", "aggregation"),

		ASSOC("association", "association", "AssociationEnd", "simple association"),

		OTHER("other", "other association", "AssociationEnd", "other association");

		/**
		 * Returns literal with <code>value</code> if found, {@link #OTHER} instance otherwise.
		 */
		public static Kind findForValue(String value) {
			for (Kind k : values()) {
				if (k.getValue().equals(value)) {
					return k;
				}
			}
			return OTHER;
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
	 * Returns all available classifications (kinds) for association ends.
	 *
	 * @param nature
	 *            ignored in this method
	 */
	public static List<UmlKind> getKinds(Nature nature) {
		List<UmlKind> result = new ArrayList<UmlKind>();
		for (UmlKind kind : UmlAssociationEnd.Kind.values()) {
			result.add(kind);
		}
		return result;
	}

	/**
	 * Navigability of an association end.
	 *
	 * @author tatjana.kostic@ieee.org
	 * @version $Id: UmlAssociationEnd.java 34 2019-12-20 18:37:17Z dev978 $
	 */
	public static enum Navigable {
		yes, no, unspecified
	}

	/** Allowed tags for any association end. */
	private static final List<String> ANY_TAGS = Arrays.asList(UML.TVN_nsuri, UML.TVN_nsprefix);

	/** Allowed tags for IEC 61850 association ends. */
	private static final List<String> IEC61850_TAGS = ANY_TAGS;

	/** Allowed tags for CIM association ends. */
	private static final List<String> CIM_TAGS = ANY_TAGS;

	/**
	 * Data from the UML model repository specific to {@link UmlAssociationEnd}.
	 *
	 * @author tatjana.kostic@ieee.org
	 * @version $Id: UmlAssociationEnd.java 34 2019-12-20 18:37:17Z dev978 $
	 */
	public static class Data {
		private static final Data DEFAULT = new Data();

		/**
		 * Returns empty instance; sets default multiplicity to {@link UmlMultiplicity#ONE}, kind to
		 * {@link Kind#ASSOC}, and direction to {@link Navigable#unspecified}.
		 */
		public static Data empty() {
			return DEFAULT;
		}

		private final Kind _kind;
		private final UmlMultiplicity _multiplicity;
		private final Navigable _navigable;

		private Data() {
			this(Kind.ASSOC, UmlMultiplicity.ONE, Navigable.unspecified);
		}

		/**
		 * Constructor.
		 *
		 * @param kind
		 * @param multiplicity
		 * @param navigable
		 */
		public Data(Kind kind, UmlMultiplicity multiplicity, Navigable navigable) {
			super();
			_kind = kind;
			_multiplicity = multiplicity;
			_navigable = navigable;
		}

		public Kind getKind() {
			return _kind;
		}

		public UmlMultiplicity getMultiplicity() {
			return _multiplicity;
		}

		public Navigable getNavigable() {
			return _navigable;
		}
	}

	private final UmlClass _type;
	private final Data _data;

	private UmlAssociation _containingAssociation;

	/** For testing only: Constructs minimal association end. */
	static UmlAssociationEnd basic(UmlClass type, String name) {
		return new UmlAssociationEnd(type, new UmlObjectData(name), Data.empty());
	}

	/** For testing only: Constructs minimal association end with given stereotype. */
	static UmlAssociationEnd basic(UmlClass type, String name, UmlStereotype stereotype) {
		return new UmlAssociationEnd(type, new UmlObjectData(name, stereotype), Data.empty());
	}

	/**
	 * For testing only: Constructs minimal association end with given navigability.
	 */
	static UmlAssociationEnd basic(UmlClass type, String name, Navigable navigable) {
		return new UmlAssociationEnd(type, new UmlObjectData(name),
				new Data(Kind.ASSOC, UmlMultiplicity.ONE, navigable));
	}

	/**
	 * Constructor. After creating this object, you may want to add tagged values. In every case,
	 * the association that will be initialised from two instances of this, has to use
	 * {@link #setContainingAssociation(UmlAssociation)} to correctly set reference to itself.
	 *
	 * @param type
	 *            class used as type for this association end.
	 * @param objData
	 *            common data for any {@link UmlObject}.
	 * @param data
	 *            data proper to {@link UmlAssociationEnd}.
	 */
	public UmlAssociationEnd(UmlClass type, UmlObjectData objData, Data data) {
		super(objData);

		Util.ensureNotNull(type, "type");
		Util.ensureNotNull(data, "data");

		_type = type;
		_data = data;

		_logger.trace(String.format("created %s", toString()));
	}

	/** Returns the association to which this end belongs. */
	public UmlAssociation getContainingAssociation() {
		return _containingAssociation;
	}

	/**
	 * Reserved for use by {@link UmlAssociation}: Sets containing association for this end.
	 *
	 * @param containingAssociation
	 *            non-null containing association.
	 */
	void setContainingAssociation(UmlAssociation containingAssociation) {
		Util.ensureNotNull(containingAssociation, "containingAssociation");

		if (getContainingAssociation() != null) {
			throw new ProgrammerErrorException(
					String.format("Containing assoc. already set for end %s.", toString()));
		}

		_containingAssociation = containingAssociation;
	}

	/** Returns {@link UmlClass} used as type for this association end. */
	public UmlClass getType() {
		return _type;
	}

	public boolean isAssociation() {
		return !isAggregation() && !isComposition() && !isOther();
	}

	public boolean isAggregation() {
		return getKind() == Kind.AGGREG;
	}

	public boolean isComposition() {
		return getKind() == Kind.COMPOS;
	}

	public boolean isOther() {
		return getKind() == Kind.OTHER;
	}

	public UmlMultiplicity getMultiplicity() {
		return _data.getMultiplicity();
	}

	public Navigable getNavigable() {
		return _data.getNavigable();
	}

	public boolean isNamedWithoutMultiplicity() {
		return !getName().isEmpty() && getMultiplicity() == UmlMultiplicity.EMPTY;
	}

	private boolean otherEndInitialised() {
		return getContainingAssociation() != null;
	}

	/** Returns null if containing association not initialised. */
	private UmlAssociationEnd getOtherEnd() {
		if (!otherEndInitialised()) {
			return null;
		}
		if (isSource()) {
			return getContainingAssociation().getTargetEnd();
		}
		return getContainingAssociation().getSourceEnd();
	}

	/** Returns whether this end is the source end of the containing association. */
	public boolean isSource() {
		if (!otherEndInitialised()) {
			return false;
		}
		return getContainingAssociation().getSourceEnd() == this;
	}

	/** Returns whether this end is the target end of the containing association. */
	public boolean isTarget() {
		if (!otherEndInitialised()) {
			return false;
		}
		return !isSource();
	}

	// =========== org.tanjakostic.jcleancim.model.UmlObject ============

	/**
	 * {@inheritDoc}
	 * <p>
	 * Before two instances of this are used to create an association, returns null.
	 */
	@Override
	public OwningWg getOwner() {
		if (!otherEndInitialised()) {
			return null;
		}
		return OwningWg.determineAssociationOwner(getType().getOwner(),
				getOtherEnd().getType().getOwner());
	}

	/**
	 * {@inheritDoc}
	 * <p>
	 * Returns own namespace initialised from tagged values if not empty. Otherwise, returns null if
	 * association end has not yet been added to its association, or association's namespace.
	 */
	@Override
	public Namespace getNamespace() {
		Namespace ns = initFromTags();
		if (ns != Namespace.EMPTY) {
			return ns;
		}
		if (!otherEndInitialised()) {
			return null;
		}

		return getContainingAssociation().getNamespace();
	}

	@Override
	public Nature getNature() {
		return getType().getNature();
	}

	/**
	 * {@inheritDoc}
	 * <p>
	 * Before two instances of this are used to create an association, returns false.
	 * <p>
	 * Association end is considered as informative if any of the following is true:
	 * <ul>
	 * <li>association end stereotype includes {@value UmlStereotype#INFORMATIVE},
	 * <li>association end type is informative,
	 * <li>association end's other end type is informative,
	 * <li>containing association is informative.
	 * </ul>
	 */
	@Override
	public boolean isInformative() {
		if (!otherEndInitialised()) {
			return false;
		}
		return super.isInformative() || getOtherEnd().getType().isInformative()
				|| getType().isInformative() || getContainingAssociation().isInformative();
	}

	@Override
	public UmlKind getKind() {
		return _data.getKind();
	}

	/**
	 * {@inheritDoc}
	 * <p>
	 * Returns qualified name of this association end (i.e., the type of the other association end
	 * prepended to the name).
	 */
	@Override
	public String getQualifiedName() {
		String otherType = (!otherEndInitialised()) ? "null" : getOtherEnd().getType().getName();
		return otherType + CLASS_SEPARATOR + (getName().isEmpty() ? "<>" : getName());
	}

	/**
	 * {@inheritDoc}
	 * <p>
	 * Association end is deprecated if its stereotype contains {@value UmlStereotype#DEPRECATED},
	 * or if any of these is deprecated: its type, its other end type, or the containing
	 * association.
	 */
	@Override
	public boolean isDeprecated() {
		if (super.isDeprecated()) {
			return true;
		}
		if (getContainingAssociation() == null) {
			return false;
		}
		return getOtherEnd().getType().isDeprecated() || getType().isDeprecated()
				|| getContainingAssociation().isDeprecated();
	}

	@Override
	public Set<String> getPredefinedTagNames() {
		List<String> resultList = (getNature() == Nature.CIM) ? CIM_TAGS : IEC61850_TAGS;
		return new LinkedHashSet<String>(resultList);
	}

	// =========== java.lang.Object ============

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append(getOwner());
		if (getVisibility() != UmlVisibility.PUBLIC) {
			sb.append(" (").append(getVisibility()).append(")");
		}
		if (!getStereotype().isEmpty()) {
			sb.append(" ").append(getStereotype().toString());
		}
		sb.append(" ").append(getMultiplicity());
		sb.append(" ").append(getQualifiedName());
		if (getNavigable() != Navigable.unspecified) {
			String nav = (getNavigable() == Navigable.no) ? "non-" : "";
			sb.append(" (").append(nav).append("navigable)");
		}
		if (!getTaggedValues().isEmpty()) {
			sb.append("; tags=").append(getTaggedValues().toString());
		}
		return sb.toString();
	}

	// ---------------------------------------

}
