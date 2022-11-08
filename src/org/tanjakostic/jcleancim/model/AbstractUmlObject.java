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
import java.util.Collection;
import java.util.Collections;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.tanjakostic.jcleancim.common.Nature;
import org.tanjakostic.jcleancim.common.OwningWg;
import org.tanjakostic.jcleancim.util.MapOfCollections;
import org.tanjakostic.jcleancim.util.MapOfSets;
import org.tanjakostic.jcleancim.util.Util;

/**
 * Common implementation of several methods and static utility methods for manipulating collections
 * of {@link UmlObject}-s.
 * <p>
 * <i>Implementation note:</i> Uses composition with {@link UmlObjectData} for all the fields that
 * are initialised in the constructor, and in {@link #toShortString(boolean, String, boolean)}
 * relies on abstract methods, to be implemented by concrete subtypes.
 *
 * @author tatjana.kostic@ieee.org
 * @version $Id: AbstractUmlObject.java 34 2019-12-20 18:37:17Z dev978 $
 */
abstract public class AbstractUmlObject implements UmlObject {
	private static final Logger _logger = Logger.getLogger(AbstractUmlObject.class.getName());

	public static final String NULL_OBJ_NAME = "null";

	/** Package separator, for qualified names. */
	public static final String PACKAGE_SEPARATOR = "::";

	/** Class separator, for qualified names. */
	public static final String CLASS_SEPARATOR = ".";

	// ================== utility static methods on collections ====================

	/**
	 * Returns list of names. For a null object in <code>objects</code>, returns the name
	 * {@link #NULL_OBJ_NAME}.
	 */
	public static <T extends UmlObject> List<String> collectNames(Collection<T> objects) {
		List<String> result = new ArrayList<String>(objects.size());
		for (T o : objects) {
			String name = (o == null) ? NULL_OBJ_NAME : o.getName();
			result.add(name);
		}
		return result;
	}

	/**
	 * Returns list of qualified names, with prepended owner if <code>includeOwner=true</code>. For
	 * a null object in <code>objects</code>, returns the name {@link #NULL_OBJ_NAME}.
	 */
	public static <T extends UmlObject> List<String> collectQNames(Collection<T> objects,
			boolean includeOwner) {
		List<String> result = new ArrayList<String>(objects.size());
		for (T o : objects) {
			String objName = (o == null) ? NULL_OBJ_NAME : o.getQualifiedName();
			String ownerName = "";
			if (includeOwner && o != null) {
				ownerName = o.getOwner().toString() + " ";
			}
			result.add(ownerName + objName);
		}
		return result;
	}

	/**
	 * Returns those <code>objects</code> that have the same name, indexed by name; skips null
	 * objects.
	 */
	public static <T extends UmlObject> MapOfCollections<String, T> collectDuplicateNames(
			Collection<T> objects) {
		MapOfCollections<String, T> result = new MapOfSets<>();
		Map<String, T> visitedNames = new HashMap<>();
		for (T o : objects) {
			if (o == null || o.getName().trim().isEmpty()) {
				continue;
			}
			String name = o.getName();
			boolean duplicate = visitedNames.containsKey(name);
			if (duplicate) {
				if (!result.containsKey(name)) {
					result.addValue(name, visitedNames.get(name));
				}
				result.addValue(name, o);
			}
			visitedNames.put(name, o);
		}
		return result;
	}

	/**
	 * Returns set of objects with given name; skips null objects.
	 */
	public static <T extends UmlObject> Set<T> findAllForName(Collection<T> objects, String name) {
		Util.ensureNotNull(objects, "objects");
		Util.ensureNotNull(name, "name");
		Set<T> result = new LinkedHashSet<>();
		for (T o : objects) {
			if (o == null) {
				continue;
			}
			if (name.equals(o.getName())) {
				result.add(o);
			}
		}
		return result;
	}

	/**
	 * Returns those <code>objects</code> that have the same description (trimmed), indexed by that
	 * description; skips null objects.
	 */
	public static <T extends UmlObject> MapOfCollections<String, T> collectDuplicateDescriptions(
			Collection<T> objects) {
		MapOfCollections<String, T> result = new MapOfSets<>();
		Map<String, T> visitedDescriptions = new HashMap<>();
		for (T o : objects) {
			if (o == null || o.getDescription().text.trim().isEmpty()) {
				continue;
			}
			String description = o.getDescription().text.trim();
			boolean duplicate = visitedDescriptions.containsKey(description);
			if (duplicate) {
				if (!result.containsKey(description)) {
					result.addValue(description, visitedDescriptions.get(description));
				}
				result.addValue(description, o);
			}
			visitedDescriptions.put(description, o);
		}
		return result;
	}

	/**
	 * Returns map of objects indexed per <code>scope</code>; skips null objects. For a simple list,
	 * use {@link #collectForScope(Collection, EnumSet)}.
	 */
	public static <T extends UmlObject> Map<OwningWg, Collection<T>> classifyPerScope(
			Collection<T> objects, EnumSet<OwningWg> scope) {
		Map<OwningWg, Collection<T>> result = new LinkedHashMap<>();
		for (OwningWg owner : scope) {
			result.put(owner, new ArrayList<T>());
		}
		for (T o : objects) {
			if (o != null && scope.contains(o.getOwner())) {
				result.get(o.getOwner()).add(o);
			}
		}
		return result;
	}

	/**
	 * Returns list of objects that belong to a <code>scope</code>; skips null objects. For a map
	 * indexed per scope, use {@link #classifyPerScope(Collection, EnumSet)}.
	 */
	public static <T extends UmlObject> List<T> collectForScope(Collection<T> objects,
			EnumSet<OwningWg> scope) {
		List<T> result = new ArrayList<>();
		for (T o : objects) {
			if (o != null && scope.contains(o.getOwner())) {
				result.add(o);
			}
		}
		return result;
	}

	/**
	 * Returns map of objects indexed per <code>scope</code>, then per tag name <code>tags</code>;
	 * skips null objects. For a simple map indexed per tag name only, use
	 * {@link #classifyPerTag(Map, EnumSet)}.
	 */
	public static <T extends UmlObject> Map<OwningWg, Map<String, Set<T>>> classifyPerScopePerTag(
			Map<String, Set<T>> tags, EnumSet<OwningWg> scope) {
		Map<OwningWg, Map<String, Set<T>>> result = new LinkedHashMap<>();
		for (OwningWg owner : scope) {
			result.put(owner, new LinkedHashMap<String, Set<T>>());
		}
		for (Entry<String, Set<T>> entry : tags.entrySet()) {
			String tagName = entry.getKey();
			Set<T> objects = entry.getValue();
			for (T o : objects) {
				if (o == null) {
					continue;
				}
				Map<String, Set<T>> ownerResult = result.get(o.getOwner());
				if (scope.contains(o.getOwner())) {
					if (!ownerResult.containsKey(tagName)) {
						ownerResult.put(tagName, new LinkedHashSet<T>());
					}
					ownerResult.get(tagName).add(o);
				}
			}
		}

		return result;
	}

	/** Returns restricted map with objects that have given scope. */
	public static <T extends UmlObject> Map<String, Set<T>> classifyPerTag(Map<String, Set<T>> tags,
			EnumSet<OwningWg> scope) {
		Map<String, Set<T>> result = new LinkedHashMap<>();
		for (Entry<String, Set<T>> entry : tags.entrySet()) {
			String tagName = entry.getKey();
			Set<T> objects = entry.getValue();
			if (!result.containsKey(tagName)) {
				result.put(tagName, new LinkedHashSet<T>());
			}
			for (T o : objects) {
				if (o != null && scope.contains(o.getOwner())) {
					result.get(tagName).add(o);
				}
			}
		}
		return result;
	}

	/** Stores object indexed by all of its tag names. */
	public static void saveTags(UmlObject o, Map<String, Set<UmlObject>> destination) {
		for (Entry<String, String> entry : o.getTaggedValues().entrySet()) {
			String tagName = entry.getKey();
			if (!destination.containsKey(tagName)) {
				destination.put(tagName, new LinkedHashSet<UmlObject>());
			}
			destination.get(tagName).add(o);
		}
	}

	/**
	 * Returns the object with <code>uuid</code> found in <code>objects</code> and logs the message
	 * with <code>level</code>; returns null otherwise.
	 */
	public static <T extends UmlObject> T findWithSameUuidAndLog(Level level, UmlObject asker,
			Collection<T> objects, String uuid) {
		for (T o : objects) {
			if (uuid.equals(o.getUuid())) {
				_logger.log(level,
						String.format("%s (%s) %s already in %s.", o.getKind().getLabel(),
								o.getUuid(), o.getQualifiedName(), asker.getQualifiedName()));
				return o;
			}
		}
		return null;
	}

	// ----------------- helpers for stereotypes --------------------------

	/**
	 * Returns potentially empty list of deprecation and informative qualifiers for <code>o</code>
	 * from its deprecation and informative status, not from its stereotypes. Namely, although we do
	 * have and here use these stereotypes as tokens, the deprecation and informative status for an
	 * object are not exclusively determined from object's stereotype, but are often derived (e.g.,
	 * content of deprecated class or package gets also deprecated, even without every attribute and
	 * relation having deprecated stereotype).
	 */
	public static List<String> addDeprecAndInf(UmlObject o) {
		List<String> result = new ArrayList<String>();
		if (o.isDeprecated()) {
			result.add(UmlStereotype.DEPRECATED);
		}
		if (o.isInformative()) {
			result.add(UmlStereotype.INFORMATIVE);
		}
		return result;
	}

	/**
	 * Extends <code>tokens</tokens> with custom (=non-built-in) stereotypes that are not already
	 * contained in <code>tokens</code>. This is useful not only to collect custom stereotype
	 * tokens, but also when you have a stereotype token be built-in for one object nature, and
	 * custom for another.
	 */
	public static void appendRemainingCustomStereotypes(List<String> tokens, UmlObject o,
			Map<Nature, Set<String>> builtins) {
		Set<String> collected = Collections.unmodifiableSet(new LinkedHashSet<>(tokens));

		Set<String> remaining = o.getStereotype().getTokensOtherThan(collected);
		remaining.removeAll(builtins.get(o.getNature()));
		tokens.addAll(remaining);
	}

	// ================== instance variables and methods ====================

	private final UmlObjectData _objData;
	private final Map<String, String> _taggedValues = new LinkedHashMap<String, String>();

	/**
	 * Constructor.
	 *
	 * @param objData
	 */
	protected AbstractUmlObject(UmlObjectData objData) {
		Util.ensureNotNull(objData, "objData");

		_objData = objData;
	}

	// =========== org.tanjakostic.jcleancim.model.UmlObject ============

	@Override
	public final Integer getId() {
		return _objData.getId();
	}

	@Override
	public String getUuid() {
		return _objData.getUuid();
	}

	@Override
	public String getSince() {
		return _objData.getSince();
	}

	@Override
	abstract public OwningWg getOwner();

	protected Namespace initFromTags() {
		return Namespace.create(getTaggedValues().get(UML.TVN_nsuri),
				getTaggedValues().get(UML.TVN_nsprefix));
	}

	@Override
	abstract public Nature getNature();

	/**
	 * {@inheritDoc}
	 * <p>
	 * This default implementation returns true if objects steretypes include
	 * {@value UmlStereotype#INFORMATIVE}. If there are additional criteria for deriving informative
	 * status, ensure to invoke this default implementation first.
	 *
	 * @see org.tanjakostic.jcleancim.model.UmlObject#isInformative()
	 */
	@Override
	public boolean isInformative() {
		return _objData.getStereotype().contains(UmlStereotype.INFORMATIVE);
	}

	@Override
	public final UmlVisibility getVisibility() {
		return _objData.getVisibility();
	}

	@Override
	abstract public UmlKind getKind();

	@Override
	public final String getName() {
		return _objData.getName();
	}

	@Override
	public String getAlias() {
		return _objData.getAlias();
	}

	@Override
	abstract public String getQualifiedName();

	@Override
	public String toShortString(boolean includeId, boolean isNameQualified) {
		return toShortString(includeId, null, isNameQualified);
	}

	/**
	 * Similar to {@link #toShortString(boolean, boolean)}, but allows to specify a qualifier.
	 *
	 * @param qualifier
	 *            optional qualifier, specific to subtype
	 */
	protected String toShortString(boolean includeId, String qualifier, boolean isNameQualified) {
		StringBuilder sb = new StringBuilder();
		if (includeId) {
			sb.append("(").append(getId()).append(") ");
		}
		sb.append(wrapToStringIfNull(getOwner()));
		sb.append(" ").append(wrapToStringIfNull(getNature()));
		if (isInformative()) {
			sb.append(" INF");
		}
		if (getVisibility() != UmlVisibility.PUBLIC) {
			sb.append(" ").append(getVisibility().toString());
		}
		if (Util.hasContent(qualifier)) {
			sb.append(" ").append(qualifier);
		}
		String kindLabel = (getKind() == null) ? null : getKind().getLabel();
		sb.append(" ").append(wrapToStringIfNull(kindLabel));
		if (!getStereotype().isEmpty()) {
			sb.append(" ").append(getStereotype().toString());
		}
		String name = (isNameQualified) ? getQualifiedName() : getName();
		if (!name.isEmpty()) {
			sb.append(" ").append(name);
		}
		return sb.toString();
	}

	private static String wrapToStringIfNull(Object o) {
		return (o == null) ? AbstractUmlObject.NULL_OBJ_NAME : o.toString();
	}

	@Override
	public final TextDescription getDescription() {
		return _objData.getTxtDescription();
	}

	@Override
	public final TextDescription getHtmlDescription() {
		return _objData.getHtmlDescription();
	}

	@Override
	public final UmlStereotype getStereotype() {
		return _objData.getStereotype();
	}

	/**
	 * {@inheritDoc}
	 * <p>
	 * This default implementation returns whether the stereotype string of this object contains the
	 * string {@link UmlStereotype#DEPRECATED}.
	 */
	@Override
	public boolean isDeprecated() {
		return getStereotype().contains(UmlStereotype.DEPRECATED);
	}

	@Override
	public final Set<String> getUnallowedTagNames() {
		Set<String> result = new LinkedHashSet<String>(getTaggedValues().keySet());
		result.removeAll(new LinkedHashSet<String>(getPredefinedTagNames()));
		return result;
	}

	@Override
	public final String addTaggedValue(String name, String value) throws InvalidTagException {
		validateTag(name, value);
		return _taggedValues.put(name, value);
	}

	/**
	 * Subclasses should overwride this method in case some validation about the tagged value is
	 * needed before adding it. This default implementation is a no-op.
	 *
	 * @param name
	 * @param value
	 */
	protected void validateTag(String name, String value) {
		// no-op
	}

	@Override
	public final Map<String, String> getTaggedValues() {
		return Collections.unmodifiableMap(_taggedValues);
	}
}
