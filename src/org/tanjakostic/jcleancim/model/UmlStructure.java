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

import java.io.File;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.tanjakostic.jcleancim.common.Nature;
import org.tanjakostic.jcleancim.common.OwningWg;
import org.tanjakostic.jcleancim.util.Util;

/**
 * Common implementation for collections contained by packages and classes.
 *
 * @author tatjana.kostic@ieee.org
 * @version $Id: UmlStructure.java 27 2019-11-23 16:29:38Z dev978 $
 */
public abstract class UmlStructure extends AbstractUmlObject {
	private static final Logger _logger = Logger.getLogger(UmlStructure.class.getName());

	private final Data _data;

	private final Set<UmlSkipped> _skippedUmlItems = new LinkedHashSet<UmlSkipped>();
	private final Set<UmlDependency> _dependenciesAsSource = new LinkedHashSet<UmlDependency>();
	private final Set<UmlDependency> _dependenciesAsTarget = new LinkedHashSet<UmlDependency>();
	private final Set<UmlDiagram> _diagrams = new LinkedHashSet<UmlDiagram>();

	/**
	 * Data from the UML model repository specific to {@link UmlStructure}.
	 *
	 * @author tatjana.kostic@ieee.org
	 * @version $Id: UmlStructure.java 27 2019-11-23 16:29:38Z dev978 $
	 */
	public static class Data {
		private static final Data DEFAULT = new Data();

		private final boolean _selfDependent;

		/** Returns an empty instance. */
		public static Data empty() {
			return DEFAULT;
		}

		private Data() {
			this(false);
		}

		/** Constructor. */
		public Data(boolean selfDependent) {
			_selfDependent = selfDependent;
		}

		/**
		 * Returns whether the repository contains an explicit (hand-drawn) UML self-dependency;
		 * these are not included in the in-memory model, but only reported through validation.
		 */
		public final boolean isSelfDependent() {
			return _selfDependent;
		}
	}

	/**
	 * Creates common parts of structures. After creating an instance of a concrete subtype, you may
	 * want to add skipped items, explicit (hand-drawn) UML dependencies and diagrams.
	 *
	 * @param data
	 */
	protected UmlStructure(UmlObjectData objData, Data data) {
		super(objData);

		Util.ensureNotNull(data, "data");

		_data = data;
	}

	// --------------------- skipped items ----------------------

	/** For testing only: Adds skipped UML item with default data. */
	final UmlSkipped addSkippedUmlItem(String name, boolean isConnector) {
		return addSkippedUmlItem(new UmlObjectData(name), UmlSkipped.Data.empty(isConnector));
	}

	/**
	 * Creates from arguments a skipped UML item, adds it to itself, and returns the newly created
	 * object. In case the item with the same UUID has already been added, returns the existing item
	 * immediately.
	 */
	public final UmlSkipped addSkippedUmlItem(UmlObjectData objData, UmlSkipped.Data data) {
		UmlSkipped existing = AbstractUmlObject.findWithSameUuidAndLog(Level.WARN, this,
				_skippedUmlItems, objData.getUuid());
		if (existing != null) {
			return existing;
		}

		UmlSkipped skipped = new UmlSkipped(this, objData, data);
		_skippedUmlItems.add(skipped);

		return skipped;
	}

	/** Returns all skipped UML items: elements within or connectors with this structure. */
	public final Set<UmlSkipped> getSkippedUmlItems() {
		return Collections.unmodifiableSet(_skippedUmlItems);
	}

	// ------------------ explicit (hand-drawn) dependencies ------------------

	/** For testing only: Adds dependency with default data. */
	final UmlDependency addDependency(UmlStructure target) {
		return addDependency(target, new UmlObjectData(""), UmlDependency.Data.empty());
	}

	/**
	 * Creates from arguments an explicit (hand-drawn) UML dependency, adds it to itself as
	 * <code>source</code>, to <code>target</code> as target, and to the model, and returns the
	 * newly created object. In case the dependency with the same UUID has already been added,
	 * returns the existing dependency immediately. It is the responsibility of the caller to call
	 * this method on the source structure, otherwise the behaviour is undefined.
	 *
	 * @param target
	 *            must be of the same type as this.
	 * @param objData
	 *            common UML data for the new dependency.
	 * @param data
	 *            data proper to new dependency.
	 * @throws IllegalArgumentException
	 *             if this and <code>target</code> are from different models, or if this and
	 *             <code>target</code> are the same object, or if the types of this and
	 *             <code>target</code> differ.
	 */
	public final UmlDependency addDependency(UmlStructure target, UmlObjectData objData,
			UmlDependency.Data data) {
		Util.ensureNotNull(target, "target");
		Util.ensureNotNull(objData, "objData");

		UmlDependency existing = AbstractUmlObject.findWithSameUuidAndLog(Level.WARN, this,
				_dependenciesAsSource, objData.getUuid());
		if (existing != null) {
			return existing;
		}

		existing = AbstractUmlObject.findWithSameUuidAndLog(Level.WARN, this,
				target._dependenciesAsTarget, objData.getUuid());
		if (existing != null) {
			return existing;
		}

		UmlDependency dep = new UmlDependency(this, target, objData, data);
		_dependenciesAsSource.add(dep);
		target._dependenciesAsTarget.add(dep);
		getModel().addDependency(dep);

		return dep;
	}

	/** Returns all explicit (hand-drawn) UML dependencies where I am source. */
	public final Set<UmlDependency> getDependenciesAsSource() {
		return Collections.unmodifiableSet(_dependenciesAsSource);
	}

	/** Returns all explicit (hand-drawn) UML dependencies where I am target. */
	public final Set<UmlDependency> getDependenciesAsTarget() {
		return Collections.unmodifiableSet(_dependenciesAsTarget);
	}

	/** @see Data#isSelfDependent() */
	public final boolean isSelfDependent() {
		return _data.isSelfDependent();
	}

	/**
	 * Returns all structures that I <em>directly</em> depend on through an explicit UML dependency
	 * in the model. For exhaustive list, use
	 * {@link UmlStructure#collectMyAndParentsDependencyEfferentStructures()}.
	 */
	public final Collection<UmlStructure> collectDependencyEfferentStructures() {
		Collection<UmlStructure> result = new LinkedHashSet<UmlStructure>();
		for (UmlDependency dep : _dependenciesAsSource) {
			result.add(dep.getTarget());
		}
		return result;
	}

	/**
	 * Returns all structures that I and my containers <em>recursively</em> depend on through an
	 * explicit UML dependency in the model; starting from my direct dependencies, then following my
	 * container's dependencies and so on). We stop as soon as a cycle is detected.
	 * <p>
	 * For simple list of my direct dependencies, use
	 * {@link UmlStructure#collectDependencyEfferentStructures()}.
	 */
	public final Collection<UmlStructure> collectMyAndParentsDependencyEfferentStructures() {
		Collection<UmlStructure> result = new LinkedHashSet<UmlStructure>();
		for (UmlDependency dep : _dependenciesAsSource) {
			if (isSelfDependent()) {
				continue;
			}
			UmlStructure target = dep.getTarget();
			if (result.contains(target)) {
				continue;
			}
			result.add(target);
		}
		UmlStructure container = getContainer();
		if (container != null) {
			result.addAll(container.collectMyAndParentsDependencyEfferentStructures());
		}
		return result;
	}

	/** Returns all structures that depend on me through an explicit UML dependency in the model. */
	public final Collection<UmlStructure> collectDependencyAfferentStructures() {
		Collection<UmlStructure> result = new LinkedHashSet<UmlStructure>();
		for (UmlDependency dep : _dependenciesAsTarget) {
			result.add(dep.getSource());
		}
		return result;
	}

	// ------------------------- diagrams --------------------------

	/** For testing only: Adds diagram with default data. */
	final UmlDiagram addDiagram(String name) {
		return addDiagram(null, new UmlObjectData(name), UmlDiagram.Data.empty());
	}

	/**
	 * Creates from arguments a diagram, adds it to itself and to the model, and returns the newly
	 * created object. In case the diagram with the same UUID has already been added, returns the
	 * existing diagram immediately.
	 */
	public final UmlDiagram addDiagram(File pic, UmlObjectData objData, UmlDiagram.Data data) {
		Util.ensureNotNull(objData, "objData");

		UmlDiagram existing = AbstractUmlObject.findWithSameUuidAndLog(Level.WARN, this, _diagrams,
				objData.getUuid());
		if (existing != null) {
			return existing;
		}

		UmlDiagram diagram = new UmlDiagram(this, pic, objData, data);

		_diagrams.add(diagram);
		getModel().addDiagram(diagram);

		return diagram;
	}

	/** Returns all diagrams in this structure. */
	public final Set<UmlDiagram> getDiagrams() {
		return Collections.unmodifiableSet(_diagrams);
	}

	// ------------------------- abstract methods --------------------------

	/** Returns the model this structure belongs to. */
	abstract public UmlModel getModel();

	/** Returns containing structure, null in case this is the model package. */
	abstract public UmlStructure getContainer();

	// =========== org.tanjakostic.jcleancim.model.UmlObject ============

	@Override
	abstract public OwningWg getOwner();

	@Override
	abstract public Nature getNature();

	@Override
	abstract public UmlKind getKind();

	@Override
	abstract public String getQualifiedName();
}
