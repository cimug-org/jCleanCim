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
import java.util.Collections;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;
import org.tanjakostic.jcleancim.common.Nature;
import org.tanjakostic.jcleancim.common.OwningWg;
import org.tanjakostic.jcleancim.util.Util;

/**
 * UML constraint.
 * <p>
 * Initially, we have designed it to allow handling of complex IEC61850 presence conditions (through
 * class constraints; for recognised formats, see {@link Kind}) and IEC61850 array min/max index
 * (through attribute constraint).
 * <p>
 * Then we retrofitted the implementation to be able to use "vanilla" constraints on classes
 * (without any special processing). For CIM domains, just ignore specials and use normal UmlObject
 * methods on this class that you normally use for other UmlObject-s.
 * <p>
 * Design note: We could have had two subclasses, but it would have been an overkill at this point
 * in time.
 *
 * @author tatjana.kostic@ieee.org
 * @version $Id: UmlConstraint.java 34 2019-12-20 18:37:17Z dev978 $
 */
public class UmlConstraint extends AbstractUmlObject {
	private static final Logger _logger = Logger.getLogger(UmlConstraint.class.getName());

	/** (61850) Used to separate attribute names from the condition text for class constraints. */
	public static final String SEPARATOR = ":";

	/**
	 * Kind of constraint.
	 *
	 * @author tatjana.kostic@ieee.org
	 * @version $Id: UmlConstraint.java 34 2019-12-20 18:37:17Z dev978 $
	 */
	public enum Kind implements UmlKind {

		/**
		 * We currently use class constraints in IEC61850-7-4 and IEC61850-7-3 UML for presence
		 * conditions of attributes that are not simply M or O. These conditions usually involve
		 * multiple attributes of a class, so we place constraint on a class. The expected format
		 * for doc of the constraint is:
		 *
		 * <pre>
		 *   <comma separated attributes list>[: optional free text]
		 * </pre>
		 */
		CLASS("class attributes", "class attributes constraint", "Constraint",
				"class attributes constraint"),

		/**
		 * We currently use attribute constraints in IEC61850-7-3 for attributes that are arrays, to
		 * store their min and max index, to be able to print "ARRAY min...max OF XYZ".
		 */
		ATTR_MIN_MAX("attribute indexes", "attribute index constraint", "Constraint",
				"attribute constraint (minIdx/maxIdx)");

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
	 * Data from the UML model repository specific to {@link UmlConstraint}.
	 *
	 * @author tatjana.kostic@ieee.org
	 * @version $Id: UmlConstraint.java 34 2019-12-20 18:37:17Z dev978 $
	 */
	public static class Data {
		private final List<String> _attrNames = new ArrayList<String>();
		private final String _condition;
		private final boolean _supportsTags;

		/** For tests only: Constructs data for an attribute constraint. */
		static Data createAttrConstraintData(String condition) {
			return new Data(null, condition, false);
		}

		/** For tests only: Constructs data for a class constraint. */
		static Data createClassConstraintData(String condition, String... attrNames) {
			return new Data(Arrays.asList(attrNames), condition, false);
		}

		/**
		 * Constructor.
		 *
		 * @param attrNames
		 * @param condition
		 *            text in the notes
		 * @param supportsTags
		 */
		public Data(List<String> attrNames, String condition, boolean supportsTags) {
			if (attrNames != null) {
				_attrNames.addAll(attrNames);
			}
			_condition = condition;
			_supportsTags = supportsTags;
		}

		public List<String> getAttrNames() {
			return _attrNames;
		}

		public String getCondition() {
			return _condition;
		}

		public boolean isSupportsTags() {
			return _supportsTags;
		}
	}

	private final UmlClass _containingClass;
	private final PresenceCondition _pc;
	private final UmlAttribute _containingAttribute;
	private final Data _data;
	private final UmlKind _kind;

	/** For tests only: Constructs class constraint. */
	static UmlConstraint basic(UmlClass containingClass, String name, String condition,
			String... attrNames) {
		return new UmlConstraint(containingClass, new UmlObjectData(name),
				Data.createClassConstraintData(condition, attrNames));
	}

	/** For tests only: Constructs attribute constraint. */
	static UmlConstraint basic(UmlAttribute containingAttribute, String name, String condition) {
		return new UmlConstraint(containingAttribute, new UmlObjectData(name),
				Data.createAttrConstraintData(condition));
	}

	/** Intended to be called by {@link UmlClass} and tests only. */
	UmlConstraint(UmlClass containingClass, UmlObjectData objData, Data data) {
		super(objData);

		Util.ensureNotNull(containingClass, "containingClass");
		Util.ensureNotNull(data, "data");

		_containingAttribute = null;
		_data = data;
		_kind = Kind.CLASS;
		_containingClass = containingClass;
		_pc = (_containingClass.getNature() == Nature.IEC61850) ? PresenceCondition.create(this)
				: null;

		_logger.trace(String.format("created %s", toString()));
	}

	/** Intended to be called by {@link UmlAttribute} and tests only. */
	UmlConstraint(UmlAttribute containingAttribute, UmlObjectData objData, Data data) {
		super(objData);

		Util.ensureNotNull(containingAttribute, "containingAttribute");
		Util.ensureNotNull(data, "data");

		_containingAttribute = containingAttribute;
		_data = data;
		_kind = Kind.ATTR_MIN_MAX;
		_containingClass = null;
		_pc = null;

		_logger.trace(String.format("created %s", toString()));
	}

	/** Returns containing class if this is a class constraint, null otherwise. */
	public UmlClass getContainingClass() {
		return _containingClass;
	}

	/**
	 * Returns list of class attribute names if this is a class constraint, empty list otherwise.
	 */
	public List<String> getAttrNames() {
		return Collections.unmodifiableList(_data.getAttrNames());
	}

	/**
	 * Returns presence condition deduced from this IEC61850 class constraint; null for other model
	 * nature classes and for attribute constraint.
	 */
	public PresenceCondition getPresenceCondition() {
		return _pc;
	}

	/**
	 * Returns value for condition if this is an attribute constraint, or description of presence
	 * condition if this is a class constraint.
	 */
	public String getCondition() {
		return _data.getCondition();
	}

	/** Returns containing attribute if this is an attribute constraint, null otherwise. */
	public UmlAttribute getContainingAttribute() {
		return _containingAttribute;
	}

	/** Returns containing object (either class or attribute). */
	private UmlObject getContainer() {
		return getContainingClass() != null ? getContainingClass() : getContainingAttribute();
	}

	/** Returns whether tagged values are supported. */
	public boolean isSupportsTags() {
		return _data.isSupportsTags();
	}

	// =========== org.tanjakostic.jcleancim.model.UmlObject ============

	@Override
	public OwningWg getOwner() {
		return getContainer().getOwner();
	}

	@Override
	public Namespace getNamespace() {
		return getContainer().getNamespace();
	}

	@Override
	public Nature getNature() {
		return getContainer().getNature();
	}

	/**
	 * {@inheritDoc}
	 * <p>
	 * Returns whether its container is informative, ignoring any potential stereotype (these cannot
	 * be stored in the model).
	 */
	@Override
	public boolean isInformative() {
		return getContainer().isInformative();
	}

	@Override
	public UmlKind getKind() {
		return _kind;
	}

	@Override
	public String getQualifiedName() {
		String text = getKind() == Kind.CLASS ? getAttrNames().toString() : getCondition();
		return String.format("%s {%s = %s}", getContainer().getQualifiedName(), getName(), text);
	}

	@Override
	protected void validateTag(String name, String value) {
		if (!isSupportsTags()) {
			throw new InvalidTagException("Tagged values not supported for constraints.");
		}
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

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append(getKind().getLabel()).append(" ");
		sb.append(getQualifiedName()).append(" ");
		if (getPresenceCondition() != null) {
			UmlAttribute definitionLiteral = getPresenceCondition().getDefinitionLiteral();
			String pcName = (definitionLiteral == null) ? "null" : definitionLiteral.getName();
			sb.append("(").append(pcName).append(")");
		}
		return sb.toString();
	}
}
