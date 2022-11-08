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
import java.util.Collections;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;
import org.tanjakostic.jcleancim.common.Nature;
import org.tanjakostic.jcleancim.common.OwningWg;
import org.tanjakostic.jcleancim.util.Util;

/**
 * Explicit (hand-drawn) UML dependency between either two structures (packages or classes).
 * <p>
 * Design note: We could have had two subclasses, but it would have been an overkill at this point
 * in time.
 *
 * @author tatjana.kostic@ieee.org
 * @version $Id: UmlDependency.java 34 2019-12-20 18:37:17Z dev978 $
 */
public class UmlDependency extends AbstractUmlObject {
	private static final Logger _logger = Logger.getLogger(UmlDependency.class.getName());

	/**
	 * Kinds of dependencies.
	 *
	 * @author tatjana.kostic@ieee.org
	 * @version $Id: UmlDependency.java 34 2019-12-20 18:37:17Z dev978 $
	 */
	public enum Kind implements UmlKind {
		PACKAGE("interPackage", "interPackage dependency", "Dependency", "interPackage dependency"),

		CLASS("interClass", "interClass dependency", "Dependency", "interClass dependency");

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
	 * Returns all available classifications (kinds) for dependencies.
	 *
	 * @param nature
	 *            ignored in this method
	 */
	public static List<UmlKind> getKinds(Nature nature) {
		List<UmlKind> result = new ArrayList<UmlKind>();
		for (UmlKind kind : Kind.values()) {
			result.add(kind);
		}
		return result;
	}

	/**
	 * Data from the UML model repository specific to {@link UmlDependency}.
	 *
	 * @author tatjana.kostic@ieee.org
	 * @version $Id: UmlDependency.java 34 2019-12-20 18:37:17Z dev978 $
	 */
	public static class Data {
		private static final Data DEFAULT = new Data();

		/** Returns an empty instance. */
		public static Data empty() {
			return DEFAULT;
		}

		/**
		 * Constructor.
		 */
		public Data() {
			// currently, no data
		}
	}

	private final UmlStructure _source;
	private final UmlStructure _target;
	private final Data _data;
	private final Kind _kind;

	/** Constructs minimal instance - useful for testing. */
	static UmlDependency basic(UmlStructure source, UmlStructure target) {
		return new UmlDependency(source, target, new UmlObjectData(""), Data.empty());
	}

	/**
	 * Intended to be called by {@link UmlStructure} and tests only.
	 *
	 * @throws IllegalArgumentException
	 *             if <code>source</code> and <code>target</code> are from different models, or if
	 *             <code>source</code> and <code>target</code> are the same object, or if the types
	 *             of <code>source</code> and <code>target</code> differ, or if the type of
	 *             <code>source</code> and <code>target</code> is not either {@link UmlPackage} or
	 *             {@link UmlClass}.
	 */
	UmlDependency(UmlStructure source, UmlStructure target, UmlObjectData objData, Data data) {
		super(objData);

		Util.ensureNotNull(source, "source");
		Util.ensureNotNull(target, "target");
		if (source == target) {
			throw new IllegalArgumentException(
					String.format("Source (%s) and target (%s)" + " cannot be same.",
							source.getQualifiedName(), target.getQualifiedName()));
		}
		if (source.getModel() != target.getModel()) {
			throw new IllegalArgumentException(
					String.format("Source model (%s) and target model (%s)" + " must be same.",
							source.getModel().getUuid(), target.getModel().getUuid()));
		}
		if (source.getClass() != target.getClass()) {
			throw new IllegalArgumentException(
					String.format("Source (%s) and target (%s)" + " must be of the same type.",
							source.getClass().getName(), target.getClass().getName()));
		}
		if (source instanceof UmlPackage) {
			_kind = Kind.PACKAGE;
		} else if (source instanceof UmlClass) {
			_kind = Kind.CLASS;
		} else {
			throw new IllegalArgumentException(String.format(
					"Cannot add dependency for %s; supported types are UmlClass and UmlPackage.",
					source.getClass().getName()));
		}
		Util.ensureNotNull(data, "data");

		_data = data;
		_source = source;
		_target = target;

		_logger.trace(String.format("created %s", toString()));
	}

	/** Returns source. */
	public UmlStructure getSource() {
		return _source;
	}

	/** Returns target. */
	public UmlStructure getTarget() {
		return _target;
	}

	/** Returns true if this is dependency between packages, false if it is between classes. */
	public boolean isForPackage() {
		return getKind() == Kind.PACKAGE;
	}

	/** Returns whether source and target have the same owner. */
	public boolean isWithinSameWg() {
		return getSource().getOwner() == getTarget().getOwner();
	}

	// ===== Impl. of org.tanjakostic.jcleancim.model.UmlObject methods =====

	@Override
	public OwningWg getOwner() {
		return getSource().getOwner();
	}

	@Override
	public Namespace getNamespace() {
		return getSource().getNamespace();
	}

	@Override
	public Nature getNature() {
		return getSource().getNature();
	}

	/**
	 * {@inheritDoc}
	 * <p>
	 * Dependency is never informative, and this method always returns false.
	 */
	@Override
	public boolean isInformative() {
		return false;
	}

	@Override
	public UmlKind getKind() {
		return _kind;
	}

	@Override
	public String getQualifiedName() {
		return String.format("%s -> %s", getSource().getQualifiedName(),
				getTarget().getQualifiedName());
	}

	/**
	 * {@inheritDoc}
	 * <p>
	 * Always returns empty set.
	 */
	@Override
	public Set<String> getPredefinedTagNames() {
		return Collections.emptySet();
	}

	// =========== java.lang.Object ============

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder(toShortString(true, false));
		sb.append("= ").append(getQualifiedName());
		if (getTaggedValues().size() != 0) {
			sb.append(", tags=").append(getTaggedValues().toString());
		}
		return sb.toString();
	}
}
