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

import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;
import org.tanjakostic.jcleancim.common.Nature;
import org.tanjakostic.jcleancim.common.OwningWg;
import org.tanjakostic.jcleancim.model.UmlAssociationEnd.Kind;
import org.tanjakostic.jcleancim.model.UmlAssociationEnd.Navigable;
import org.tanjakostic.jcleancim.util.Util;

/**
 * UML association, with its two ends (UML classes).
 * <p>
 * All the information related to the types/UML classes ({@link #getSource()} and
 * {@link #getTarget()}) gets derived from the contained association ends ({@link #getSourceEnd()}
 * and {@link #getTargetEnd()}). Whether association is informative, in contrast, is based on the
 * stereotype (this allows us to identify and report when the due stereotype is missing).
 * <p>
 * Ownership of association is defined in {@link OwningWg}, according to TC57 rules.
 * <p>
 * Associations with their two ends are a bit tricky. To define owner (owning top-level package and
 * its WG), we use the classes at both ends, i.e., methods {@link #getSource()} and
 * {@link #getTarget()}.
 * <p>
 * Consider example association from combined CIM model, between Location and PowerSystemResource.
 * Location (from IEC61968) is the source and PowerSystemResource (from IEC61970) is the target.
 * Qualified association ends, to display association, are shown so:
 * <p>
 * [0..*] Location.PowerSystemResources - [0..1] PowerSystemResource.Location
 * <p>
 *
 * @author tatjana.kostic@ieee.org
 * @version $Id: UmlAssociation.java 34 2019-12-20 18:37:17Z dev978 $
 */
public class UmlAssociation extends AbstractUmlObject {
	private static final Logger _logger = Logger.getLogger(UmlAssociation.class.getName());

	/**
	 * Returns all available classifications (kinds) for associations.
	 *
	 * @param nature
	 *            ignored in this method
	 */
	public static List<UmlKind> getKinds(Nature nature) {
		return UmlAssociationEnd.getKinds(nature);
	}

	/**
	 * Direction (navigability) of association.
	 *
	 * @author tatjana.kostic@ieee.org
	 * @version $Id: UmlAssociation.java 34 2019-12-20 18:37:17Z dev978 $
	 */
	public enum Direction {
		biDirectional, directed, unspecified
	}

	/** Allowed tags for IEC 61850 associations. */
	private static final List<String> IEC61850_TAGS = Arrays.asList(UML.TVN_nsuri,
			UML.TVN_nsprefix);

	/** Allowed tags for CIM associations. */
	private static final List<String> CIM_TAGS = Arrays.asList(UML.TVN_nsuri, UML.TVN_nsprefix);

	/** Allowed tags for CIM profile associations. */
	private static final List<String> CIM_PROFILE_TAGS = Arrays.asList(UML.TVN_nsuri,
			UML.TVN_nsprefix, UML.TVN_GUIDBasedOn);

	/**
	 * Data from the UML model repository specific to {@link UmlAssociation}.
	 *
	 * @author tatjana.kostic@ieee.org
	 * @version $Id: UmlAssociation.java 34 2019-12-20 18:37:17Z dev978 $
	 */
	public static class Data {
		private static final Data DEFAULT = new Data();

		/**
		 * Returns empty instance; sets default direction to {@link Direction#unspecified}.
		 */
		public static Data empty() {
			return DEFAULT;
		}

		private final Direction _direction;

		private Data() {
			this(Direction.unspecified);
		}

		/**
		 * Constructor.
		 *
		 * @param direction
		 */
		public Data(Direction direction) {
			_direction = direction;
		}

		public Direction getDirection() {
			return _direction;
		}
	}

	private final UmlAssociationEnd _sourceEnd;
	private final UmlAssociationEnd _targetEnd;
	private final Data _data;
	private final Kind _kind;

	/** For testing only: Constructs minimal association. */
	static UmlAssociation basic(UmlAssociationEnd sourceEnd, UmlAssociationEnd targetEnd) {
		return new UmlAssociation(sourceEnd, targetEnd, new UmlObjectData(""), Data.empty());
	}

	/** For testing only: Constructs minimal association with stereotype. */
	static UmlAssociation basic(UmlAssociationEnd sourceEnd, UmlAssociationEnd targetEnd,
			UmlStereotype stereotype) {
		return new UmlAssociation(sourceEnd, targetEnd, new UmlObjectData("", stereotype),
				Data.empty());
	}

	/**
	 * Intended to be called by {@link UmlClass} and tests only: Creates an instance and sets itself
	 * as containing association of both <code>sourceEnd</code> and <code>targetEnd</code>. After
	 * creating this object, you may want to add tagged values.
	 *
	 * @param sourceEnd
	 * @param targetEnd
	 * @param objData
	 * @param data
	 * @throws IllegalArgumentException
	 *             if <code>sourceEnd</code> and non-null <code>targetEnd</code> are from different
	 *             models.
	 */
	UmlAssociation(UmlAssociationEnd sourceEnd, UmlAssociationEnd targetEnd, UmlObjectData objData,
			Data data) {
		super(objData);

		Util.ensureNotNull(sourceEnd, "sourceEnd");
		Util.ensureNotNull(targetEnd, "targetEnd");
		Util.ensureNotNull(data, "data");

		if (sourceEnd.getType().getModel() != targetEnd.getType().getModel()) {
			throw new IllegalArgumentException(String.format(
					"Source end's model (%s) and target end's model (%s)" + " must be same.",
					sourceEnd.getType().getModel().getUuid(), targetEnd.getType().getUuid()));
		}

		_sourceEnd = sourceEnd;
		_targetEnd = targetEnd;
		_data = data;

		sourceEnd.setContainingAssociation(this);
		targetEnd.setContainingAssociation(this);

		if (sourceEnd.isAggregation() || targetEnd.isAggregation()) {
			_kind = Kind.AGGREG;
		} else if (sourceEnd.isComposition() || targetEnd.isComposition()) {
			_kind = Kind.COMPOS;
		} else if (sourceEnd.isAssociation() && targetEnd.isAssociation()) {
			_kind = Kind.ASSOC;
		} else {
			_kind = Kind.OTHER;
			_logger.error("unclassified association: " + getQualifiedName());
		}

		_logger.trace(String.format("created %s", toString()));
	}

	/** Returns source end. */
	public UmlAssociationEnd getSourceEnd() {
		return _sourceEnd;
	}

	/** Returns target end. */
	public UmlAssociationEnd getTargetEnd() {
		return _targetEnd;
	}

	/**
	 * Returns the pair of association ends from the perspective of source if <code>asSource</code>
	 * true (this is what we need when printing model documentation for associations of a
	 * <code>type</code>, or when reading RDF/OWL properties), from the perspective of target
	 * otherwise.
	 */
	public UmlAssociationEndPair getEndsAsSource(boolean asSource) {
		if (asSource) {
			return new UmlAssociationEndPair(getSourceEnd(), getTargetEnd());
		}
		return new UmlAssociationEndPair(getTargetEnd(), getSourceEnd());
	}

	/** Returns {@link UmlClass} used as type for the source end. */
	public UmlClass getSource() {
		return getSourceEnd().getType();
	}

	/** Returns {@link UmlClass} used as type for the target end. */
	public UmlClass getTarget() {
		return getTargetEnd().getType();
	}

	/** Returns whether any end is not private. */
	public boolean isNonPrivate() {
		return getSourceEnd().getVisibility() != UmlVisibility.PRIVATE
				|| getTargetEnd().getVisibility() != UmlVisibility.PRIVATE;
	}

	/** Returns whether neither end is public. */
	public boolean isNonPublic() {
		return getSourceEnd().getVisibility() != UmlVisibility.PUBLIC
				&& getTargetEnd().getVisibility() != UmlVisibility.PUBLIC;
	}

	/** Returns whether at least one end is public. */
	public boolean isAtLeastOneEndPublic() {
		return getSourceEnd().getVisibility() == UmlVisibility.PUBLIC
				|| getTargetEnd().getVisibility() == UmlVisibility.PUBLIC;
	}

	/** Returns whether both ends have the same visibility. */
	public boolean areEndVisibilitiesSame() {
		return getSourceEnd().getVisibility() == getTargetEnd().getVisibility();
	}

	/**
	 * Returns the direction (navigability).
	 *
	 * @deprecated use {@link #getNavigability()} instead.
	 */
	@Deprecated
	public Direction getDirection() {
		return getNavigability();
	}

	/**
	 * Returns the nature of navigability (whether navigable); for direction, use
	 * {@link UmlAssociationEnd#getNavigable()} for association ends.
	 */
	public Direction getNavigability() {
		return _data.getDirection();
	}

	/** Returns whether this association has at least one navigable end. */
	public boolean hasANavigableEnd() {
		return getSourceEnd().getNavigable() == Navigable.yes
				|| getTargetEnd().getNavigable() == Navigable.yes;
	}

	/**
	 * Returns whether an association with unspecified navigability has a navigable end. This may
	 * happen in EA when you draw an association (which gets created as navigable, according to your
	 * local EA settings) and then you make it of unspecified direction: EA does not correctly
	 * update the formerly navigable end to unspecified.
	 */
	public boolean isDirectionMismatchForEnds() {
		return getNavigability() == Direction.unspecified
				&& (getSourceEnd().getNavigable() != Navigable.unspecified
						|| getTargetEnd().getNavigable() != Navigable.unspecified);
	}

	/** Returns whether this association is accross model domains. */
	public boolean isMapping() {
		return (getSource().getNature() != getTarget().getNature());
	}

	/** Returns whether this association is between the classes with the same owner. */
	public boolean isWithinSameWg() {
		return getSource().getOwner() == getTarget().getOwner();
	}

	/** Returns whether any of two classes of this association involve owner <code>wg</code>. */
	public boolean involvesWg(OwningWg wg) {
		return wg.involvedIn(getSource().getOwner(), getTarget().getOwner());
	}

	// =========== org.tanjakostic.jcleancim.model.UmlObject ============

	@Override
	public OwningWg getOwner() {
		return OwningWg.determineAssociationOwner(getSource().getOwner(), getTarget().getOwner());
	}

	/**
	 * {@inheritDoc}
	 * <p>
	 * Returns own namespace initialised from tagged values if not empty. Otherwise, returns the
	 * namespace of the source end.
	 */
	@Override
	public Namespace getNamespace() {
		Namespace ns = initFromTags();
		if (ns != Namespace.EMPTY) {
			return ns;
		}
		return getSource().getNamespace();
	}

	@Override
	public Nature getNature() {
		return isMapping() ? Nature.IEC61850 : getSourceEnd().getNature();
	}

	@Override
	public UmlKind getKind() {
		return _kind;
	}

	@Override
	public String getQualifiedName() {
		return getTargetEnd().getQualifiedName() + " - " + getSourceEnd().getQualifiedName();
	}

	@Override
	public Set<String> getPredefinedTagNames() {
		List<String> resultList = (getNature() == Nature.CIM) ? CIM_TAGS : IEC61850_TAGS;
		return new LinkedHashSet<String>(resultList);
	}

	// =========== java.lang.Object ============

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder(toShortString(true, false));
		// ... and then append the details for this association:
		sb.append(", qname='").append(getQualifiedName()).append("'");
		if (getTaggedValues().size() != 0) {
			sb.append(", tags=").append(getTaggedValues().toString());
		}
		sb.append(", endAsSrc: ").append(getEndsAsSource(true));
		sb.append(", endAsTgt: ").append(getEndsAsSource(false));
		return sb.toString();
	}
}
