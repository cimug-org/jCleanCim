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
import org.tanjakostic.jcleancim.util.Util;

/**
 * Operation parameter.
 *
 * @author tatjana.kostic@ieee.org
 * @version $Id: UmlParameter.java 34 2019-12-20 18:37:17Z dev978 $
 */
public class UmlParameter extends AbstractUmlObject {
	private static final Logger _logger = Logger.getLogger(UmlParameter.class.getName());

	/**
	 * @author tatjana.kostic@ieee.org
	 * @version $Id: UmlParameter.java 34 2019-12-20 18:37:17Z dev978 $
	 */
	public enum Kind implements UmlKind {
		ARRAY("[]", "array", "Array", "array parameter"),

		SIMPLE("", "simple", "Parameter", "simple parameter");

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

	/** Allowed tags for any operation parameters. */
	private static final List<String> ANY_TAGS = Arrays.asList(UML.TVN_nsuri, UML.TVN_nsprefix);

	/** Allowed tags for IEC 61850 operation parameters. */
	private static final List<String> IEC61850_TAGS = ANY_TAGS;

	/** CIM should have no operation parameters, but in case it does, these tags are ok. */
	private static final List<String> CIM_TAGS = ANY_TAGS;

	/**
	 * Data from the UML model repository specific to {@link UmlParameter}.
	 *
	 * @author tatjana.kostic@ieee.org
	 * @version $Id: UmlParameter.java 34 2019-12-20 18:37:17Z dev978 $
	 */
	public static class Data {
		private final UmlKind _kind;
		private final String _eaTypeInfo;

		/**
		 * Constructor.
		 *
		 * @param kind
		 * @param eaTypeInfo
		 */
		public Data(UmlKind kind, String eaTypeInfo) {
			_kind = kind;
			_eaTypeInfo = eaTypeInfo;
		}

		public UmlKind getKind() {
			return _kind;
		}

		public String getEaTypeInfo() {
			return _eaTypeInfo;
		}
	}

	private final Data _data;
	private final UmlClass _type;

	private UmlOperation _containingOperation;

	/**
	 * Constructor. After creating this object, you may want to add tagged values. In every case,
	 * the operation that will get an instances of this, has to use
	 * {@link #setContainingOperation(UmlOperation)} to correctly set reference.
	 *
	 * @param type
	 * @param objData
	 * @param data
	 * @throws NullPointerException
	 *             if any argument is null.
	 */
	public UmlParameter(UmlClass type, UmlObjectData objData, Data data) {
		super(objData);

		Util.ensureNotNull(type, "type");
		Util.ensureNotNull(data, "data");

		_data = data;
		_type = type;

		_logger.trace(String.format("created %s", toString()));
	}

	/**
	 * Sets containing class for this attribute.
	 *
	 * @param containingOperation
	 *            non-null containing operation.
	 * @throws RuntimeException
	 *             if containing operation already set.
	 */
	void setContainingOperation(UmlOperation containingOperation) {
		if (getContainingOperation() != null) {
			throw new RuntimeException("Containing operation already set.");
		}
		Util.ensureNotNull(containingOperation, "containingOperation");

		_containingOperation = containingOperation;
	}

	public UmlOperation getContainingOperation() {
		return _containingOperation;
	}

	/**
	 * Returns known (string) info from EA; useful to display in case the type of this parameter in
	 * EA model is not a valid UML class, so the model can be corrected.
	 */
	public String getEaTypeInfo() {
		return _data.getEaTypeInfo();
	}

	public boolean isArray() {
		return getKind() == Kind.ARRAY;
	}

	public UmlClass getType() {
		return _type;
	}

	/** E.g., "MyClass[] myArg". */
	public String getSignature() {
		StringBuilder sb = new StringBuilder();
		String type = (getType() == null) ? "null" : getType().getName();
		sb.append(type);
		if (isArray()) {
			sb.append("[]");
		}
		sb.append(" ").append(getName());
		return sb.toString();
	}

	private boolean isInitialised() {
		return getContainingOperation() != null;
	}

	// ===== Impl. of org.tanjakostic.jcleancim.model.AbstractUmlObject methods =====

	/**
	 * {@inheritDoc}
	 * <p>
	 * Returns owner, null if parameter has not yet been added to its operation.
	 */
	@Override
	public OwningWg getOwner() {
		return (isInitialised()) ? getContainingOperation().getOwner() : null;
	}

	/**
	 * {@inheritDoc}
	 * <p>
	 * Returns own namespace initialised from tagged values if not empty. Otherwise, returns null if
	 * parameter has not yet been added to its operation, or operation's namespace.
	 */
	@Override
	public Namespace getNamespace() {
		Namespace ns = initFromTags();
		if (ns != Namespace.EMPTY) {
			return ns;
		}

		return (isInitialised()) ? getContainingOperation().getNamespace() : null;
	}

	/**
	 * {@inheritDoc}
	 * <p>
	 * Returns nature, null if parameter has not yet been added to its operation.
	 */
	@Override
	public Nature getNature() {
		return (isInitialised()) ? getContainingOperation().getNature() : null;
	}

	/**
	 * {@inheritDoc}
	 * <p>
	 * Returns whether this is informative, false if parameter has not yet been added to its
	 * operation.
	 */
	@Override
	public boolean isInformative() {
		if (!isInitialised()) {
			return false;
		}
		return super.isInformative() || getContainingOperation().isInformative();
	}

	@Override
	public UmlKind getKind() {
		return _data.getKind();
	}

	@Override
	public String getQualifiedName() {
		return getQualifiedName(false);
	}

	private String getQualifiedName(boolean withStereotype) {
		String qName = (isInitialised()) ? getContainingOperation().getQualifiedName()
				: AbstractUmlObject.NULL_OBJ_NAME;

		if (withStereotype) {
			return String.format("%s(...%s %s...)", qName, getStereotype(), getSignature());
		}
		return String.format("%s(...%s...)", qName, getSignature());
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
		if (!getStereotype().isEmpty()) {
			sb.append(getStereotype()).append(" ");
		}
		sb.append(getQualifiedName(true));
		return sb.toString();
	}
}
