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
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;
import org.tanjakostic.jcleancim.common.Nature;
import org.tanjakostic.jcleancim.common.OwningWg;
import org.tanjakostic.jcleancim.util.Util;

/**
 * UML operation.
 *
 * @author tatjana.kostic@ieee.org
 * @version $Id: UmlOperation.java 34 2019-12-20 18:37:17Z dev978 $
 */
public class UmlOperation extends AbstractUmlObject {
	private static final Logger _logger = Logger.getLogger(UmlOperation.class.getName());

	/** Allowed tags for any operation. */
	private static final List<String> ANY_TAGS = Arrays.asList(UML.TVN_throws, UML.TVN_nsuri,
			UML.TVN_nsprefix);

	/** Allowed tags for IEC 61850 operations. */
	private static final List<String> IEC61850_TAGS = ANY_TAGS;

	/** CIM should have no operations, but in case it does, these tags are ok. */
	private static final List<String> CIM_TAGS = ANY_TAGS;

	/**
	 * @author tatjana.kostic@ieee.org
	 * @version $Id: UmlOperation.java 34 2019-12-20 18:37:17Z dev978 $
	 */
	public enum ReturnKind implements UmlKind {
		OP_RET_VOID("operation", "operation", "Operation", "void op()"),

		OP_RET_ARRAY("operation", "operation", "Operation", "T[] op()"),

		OP_RET_SIMPLE("operation", "operation", "Operation", "T op()");

		private ReturnKind(String value, String label, String tag, String desc) {
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
	 * Returns all available classifications (kinds) for operations.
	 *
	 * @param nature
	 *            ignored in this method
	 */
	public static List<UmlKind> getKinds(Nature nature) {
		List<UmlKind> result = new ArrayList<UmlKind>();
		for (UmlKind kind : ReturnKind.values()) {
			result.add(kind);
		}
		return result;
	}

	/**
	 * Data from the UML model repository specific to {@link UmlOperation}.
	 *
	 * @author tatjana.kostic@ieee.org
	 * @version $Id: UmlOperation.java 34 2019-12-20 18:37:17Z dev978 $
	 */
	public static class Data {
		private static final Data DEFAULT = new Data();

		/**
		 * Returns empty instance; sets default return kind to {@value ReturnKind#OP_RET_VOID}.
		 */

		public static Data empty() {
			return DEFAULT;
		}

		private final ReturnKind _kind;
		private final boolean _abstract;
		private final boolean _static;
		private final boolean _final;
		private final int _eaReturnTypeId;
		private final String _eaReturnTypeName;
		private final List<String> _eaExceptionTypeInfo;

		private Data() {
			this(ReturnKind.OP_RET_VOID, false, false, false, 0, "", new ArrayList<String>());
		}

		/**
		 * Constructor.
		 *
		 * @param kind
		 * @param isAbstract
		 * @param isStatic
		 * @param isFinal
		 * @param eaReturnTypeId
		 * @param eaReturnTypeName
		 * @param eaExceptionTypeInfo
		 */
		public Data(ReturnKind kind, boolean isAbstract, boolean isStatic, boolean isFinal,
				int eaReturnTypeId, String eaReturnTypeName, List<String> eaExceptionTypeInfo) {
			super();
			_kind = kind;
			_abstract = isAbstract;
			_static = isStatic;
			_final = isFinal;
			_eaReturnTypeId = eaReturnTypeId;
			_eaReturnTypeName = eaReturnTypeName;
			_eaExceptionTypeInfo = eaExceptionTypeInfo;
		}

		public ReturnKind getKind() {
			return _kind;
		}

		public boolean isAbstract() {
			return _abstract;
		}

		public boolean isStatic() {
			return _static;
		}

		public boolean isFinal() {
			return _final;
		}

		public int getEaReturnTypeId() {
			return _eaReturnTypeId;
		}

		public String getEaReturnTypeName() {
			return _eaReturnTypeName;
		}

		public List<String> getEaExceptionTypeInfo() {
			return _eaExceptionTypeInfo;
		}
	}

	/** Constructs minimal operation - useful for testing. */
	static UmlOperation basic(UmlClass containingClass, UmlClass returnType, String name) {
		return new UmlOperation(containingClass, returnType, new UmlObjectData(name), Data.empty());
	}

	private final UmlClass _containingClass;
	private final UmlClass _returnType;
	private final Data _data;

	private final List<UmlParameter> _parameters = new ArrayList<UmlParameter>();
	private final List<UmlClass> _exceptions = new ArrayList<UmlClass>();

	/**
	 * Intended to be called by {@link UmlClass} and tests only.
	 *
	 * @throws IllegalArgumentException
	 *             if returnType is null but data.kind says it does not return void, or if
	 *             <code>containingClass</code> and non-null <code>returnType</code> are from
	 *             different models.
	 */
	UmlOperation(UmlClass containingClass, UmlClass returnType, UmlObjectData objData, Data data) {
		super(objData);

		Util.ensureNotNull(containingClass, "containingClass");
		Util.ensureNotNull(data, "data");
		if (data.getKind() != ReturnKind.OP_RET_VOID) {
			if (returnType == null) {
				throw new IllegalArgumentException(String.format(
						"Return type (name=%s) for operation %s.%s should not be null.",
						data.getEaReturnTypeName(), containingClass.getName(), this.getName()));
			}
			if (containingClass.getModel() != returnType.getModel()) {
				throw new IllegalArgumentException(String.format(
						"Containing class model (%s) and return type model (%s)" + " must be same.",
						containingClass.getModel().getUuid(), returnType.getModel().getUuid()));
			}
		}

		_containingClass = containingClass;
		_returnType = returnType;
		_data = data;

		_logger.trace(String.format("created %s", toString()));
	}

	public UmlClass getContainingClass() {
		return _containingClass;
	}

	public boolean isAbstract() {
		return _data.isAbstract();
	}

	public boolean isStatic() {
		return _data.isStatic();
	}

	public boolean isFinal() {
		return _data.isFinal();
	}

	private boolean isArrayReturned() {
		return _data.getKind() == ReturnKind.OP_RET_ARRAY;
	}

	public boolean isVoidReturned() {
		return _data.getKind() == ReturnKind.OP_RET_VOID;
	}

	public int getEaReturnTypeId() {
		return _data.getEaReturnTypeId();
	}

	public String getEaReturnTypeName() {
		return _data.getEaReturnTypeName();
	}

	/**
	 * Returns known (string) info from EA; useful to display in case the return type of this
	 * operation in EA model is not a valid UML class, so the model can be corrected.
	 */
	public String getEaReturnTypeInfo() {
		return String.format("'%s' (id=%d)", getEaReturnTypeName(),
				Integer.valueOf(getEaReturnTypeId()));
	}

	public String getEaExceptionTypeInfo(int i) {
		return _data.getEaExceptionTypeInfo().get(i);
	}

	/** Returns return type of this operation, null if kind is {@link ReturnKind#OP_RET_VOID}. */
	public UmlClass getReturnType() {
		return _returnType;
	}

	// ----------------- parameters ---------------------

	/** Adds non-null parameter <code>par</code> to this operation, and returns the same object. */
	public UmlParameter addParameter(UmlParameter par) {
		Util.ensureNotNull(par, "par");

		_parameters.add(par);
		par.setContainingOperation(this);

		return par;
	}

	/** Returns all parameters of this operation. */
	public List<UmlParameter> getParameters() {
		return Collections.unmodifiableList(_parameters);
	}

	// --------------- exceptions ----------------------------

	/** Adds non-null class <code>exc</code> to this operation, and returns the same object. */
	public UmlClass addException(UmlClass exc) {
		Util.ensureNotNull(exc, "exc");

		_exceptions.add(exc);

		return exc;
	}

	/** Returns all exceptions declared for this operation. */
	public List<UmlClass> getExceptions() {
		return Collections.unmodifiableList(_exceptions);
	}

	/** Returns all classes that I use as type for return value, parameter or exception. */
	public Set<UmlClass> getEfferentClasses() {
		Set<UmlClass> result = new LinkedHashSet<UmlClass>();
		if (!isVoidReturned()) {
			result.add(getReturnType());
		}
		for (UmlParameter par : getParameters()) {
			result.add(par.getType());
		}
		for (UmlClass exc : getExceptions()) {
			result.add(exc);
		}
		return result;
	}

	/**
	 * Returns potentially empty string containing comma-separated list of exceptions that follow
	 * the 'throw' statement in operation signature.
	 */
	public String getExceptionsSignature() {
		String result = getTaggedValues().get(UML.TVN_throws);
		return (result == null) ? "" : result;
	}

	/**
	 * E.g. "abstract public static RetType[] foo(C1 arg1, C2[] arg2) throws SomeExc, OthExc".
	 */
	public String getSignature() {
		StringBuilder sb = new StringBuilder();
		if (isAbstract()) {
			sb.append("abstract ");
		}
		sb.append(getVisibility()).append(" ");
		if (isStatic()) {
			sb.append("static ");
		}
		String rtype = isVoidReturned() ? "void"
				: ((getReturnType() == null) ? "null" : getReturnType().getName());
		sb.append(rtype);
		if (isArrayReturned()) {
			sb.append("[]");
		}
		sb.append(" ").append(getName());
		sb.append("(");
		for (int i = 0; i < _parameters.size(); ++i) {
			UmlParameter par = _parameters.get(i);
			sb.append(par.getSignature());
			if (i < (_parameters.size() - 1)) {
				sb.append(", ");
			}
		}
		sb.append(")");
		if (!getExceptionsSignature().isEmpty()) {
			sb.append(" throws ").append(getExceptionsSignature());
		}
		return sb.toString();
	}

	// ===== Impl. of org.tanjakostic.jcleancim.model.UmlObject methods =====

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

	@Override
	public Nature getNature() {
		return getContainingClass().getNature();
	}

	@Override
	public boolean isInformative() {
		return super.isInformative() || getContainingClass().isInformative();
	}

	@Override
	public UmlKind getKind() {
		return _data.getKind();
	}

	@Override
	public String getQualifiedName() {
		return getContainingClass().getName() + CLASS_SEPARATOR + getName();
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
		sb.append(" (").append(getId()).append(")");
		sb.append(" ").append(getOwner());
		sb.append(" ").append(getNature());
		if (isInformative()) {
			sb.append(" INF");
		}
		sb.append(" ").append(getContainingClass().getQualifiedName());
		sb.append(" ").append(getKind().getLabel());
		if (!getStereotype().isEmpty()) {
			sb.append(" ").append(getStereotype().toString());
		}
		sb.append(" ").append(getSignature());
		if (!getTaggedValues().isEmpty()) {
			sb.append("; tags=").append(getTaggedValues());
		}
		return sb.toString();
	}
}
