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

package org.tanjakostic.jcleancim.builder.ea;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.log4j.Logger;
import org.tanjakostic.jcleancim.common.Nature;
import org.tanjakostic.jcleancim.model.AbstractUmlObject;
import org.tanjakostic.jcleancim.model.UML;
import org.tanjakostic.jcleancim.model.UmlClass;
import org.tanjakostic.jcleancim.model.UmlObjectData;
import org.tanjakostic.jcleancim.model.UmlOperation;
import org.tanjakostic.jcleancim.model.UmlOperation.Data;
import org.tanjakostic.jcleancim.model.UmlOperation.ReturnKind;
import org.tanjakostic.jcleancim.model.UmlStereotype;
import org.tanjakostic.jcleancim.util.ProgrammerErrorException;
import org.tanjakostic.jcleancim.util.Util;

/**
 * @param <O>
 *            Source data for operation
 * @param <T>
 *            Source data for operation tagged values and parameters
 * @author tatjana.kostic@ieee.org
 * @version $Id: OperationBuilder.java 34 2019-12-20 18:37:17Z dev978 $
 */
abstract public class OperationBuilder<O, T> extends AbstractObjectBuilderFromEA<UmlOperation> {

	private static final Logger _logger = Logger.getLogger(OperationBuilder.class.getName());

	private final ClassBuilder<?, ?, ?, ?, ?, ?> _containingClass;
	private UmlObjectData _objData;

	private int _pos;
	private boolean _abstract;
	private boolean _static;
	private boolean _final;
	private ReturnKind _kind;
	private int _eaReturnTypeId;
	private String _eaReturnTypeName;
	private final List<String> _exceptionNames;

	private final Map<String, String> _taggedValues = new LinkedHashMap<String, String>();
	private final List<ParameterBuilder<?>> _parameters = new ArrayList<ParameterBuilder<?>>();
	private ClassBuilder<?, ?, ?, ?, ?, ?> _returnType;
	private final List<ClassBuilder<?, ?, ?, ?, ?, ?>> _exceptions = new ArrayList<ClassBuilder<?, ?, ?, ?, ?, ?>>();

	/**
	 * Constructor for operation from EA object.
	 *
	 * @param inData
	 * @param tagsSrc
	 * @param containingClass
	 * @param eaHelper
	 * @throws NullPointerException
	 *             if any argument is null.
	 */
	protected OperationBuilder(O inData, T tagsSrc, ClassBuilder<?, ?, ?, ?, ?, ?> containingClass,
			EaHelper eaHelper) {
		Util.ensureNotNull(inData, "inData");
		Util.ensureNotNull(tagsSrc, "tagsSrc");
		Util.ensureNotNull(containingClass, "containingClass");
		Util.ensureNotNull(eaHelper, "helper");

		_containingClass = containingClass;
		EaModelBuilder<?, ?> model = containingClass.getContainingPackage().getModel();
		setReturnType(null); // deduced after all UML classes created
		// _exceptions: deduced and filled after all UML classes created

		Integer id = getOperationID(inData);
		String guid = getOperationGUID(inData);
		String name = getOperationName(inData);
		String alias = getOperationAlias(inData);
		String stereotypes = getOperationStereotypes(inData);
		String visibility = getOperationVisibility(inData);
		String notes = getOperationNotes(inData);
		initObjData(id, guid, name, alias, stereotypes, visibility, notes, eaHelper);
		model.addOperation(this);

		int pos = getOperationPosition(inData);
		boolean isAbstract = getOperationIsAbstract(inData);
		boolean isStatic = getOperationIsStatic(inData);
		boolean isFinal = getOperationIsLeaf(inData);
		boolean returnsArray = getOperationIsReturnArray(inData);
		String returnTypeName = getOperationReturnType(inData);
		String classifierId = getOperationClassifierID(inData);
		initOwnData(pos, isAbstract, isStatic, isFinal, returnsArray, returnTypeName, classifierId);

		List<Map<String, String>> myTaggedValues = fetchTaggedValues(tagsSrc);
		initTaggedValues(myTaggedValues);

		// depends on tagged values being initialised:
		_exceptionNames = Util.splitCommaSeparatedTokens(getExceptionsSignature());

		createParams(tagsSrc, eaHelper);

		_logger.log(CTOR_LOG_LEVEL, "read from EA: " + toString());
	}

	abstract protected Integer getOperationID(O inData);

	abstract protected String getOperationGUID(O inData);

	abstract protected String getOperationName(O inData);

	abstract protected String getOperationAlias(O inData);

	abstract protected String getOperationStereotypes(O inData);

	abstract protected String getOperationVisibility(O inData);

	abstract protected String getOperationNotes(O inData);

	private void initObjData(Integer id, String guid, String name, String alias, String stereotypes,
			String visibility, String notes, EaHelper eaHelper) {
		_objData = new UmlObjectData(id, guid, name, alias, new UmlStereotype(stereotypes),
				visibility, eaHelper.getRawText(notes), eaHelper.getHtmlText(notes));
	}

	abstract protected int getOperationPosition(O inData);

	abstract protected boolean getOperationIsAbstract(O inData);

	abstract protected boolean getOperationIsStatic(O inData);

	abstract protected boolean getOperationIsLeaf(O inData);

	abstract protected boolean getOperationIsReturnArray(O inData);

	abstract protected String getOperationReturnType(O inData);

	abstract protected String getOperationClassifierID(O inData);

	private void initOwnData(int pos, boolean isAbstract, boolean isStatic, boolean isFinal,
			boolean returnsArray, String returnTypeName, String classId) {
		_pos = pos;
		_abstract = isAbstract;
		_static = isStatic;
		_final = isFinal;
		if (returnsArray) {
			_kind = ReturnKind.OP_RET_ARRAY;
		} else if ("void".equals(returnTypeName)) {
			_kind = ReturnKind.OP_RET_VOID;
		} else {
			_kind = ReturnKind.OP_RET_SIMPLE;
		}
		_eaReturnTypeId = (getKind() == ReturnKind.OP_RET_VOID) ? 0 : Integer.parseInt(classId);
		_eaReturnTypeName = returnTypeName;
	}

	// ----------------- exceptions ---------------------

	/**
	 * Returns potentially empty string containing comma-separated list of exceptions that follow
	 * the 'throw' statement in operation signature.
	 */
	public final String getExceptionsSignature() {
		String result = getTaggedValues().get(UML.TVN_throws);
		return (result == null) ? "" : result;
	}

	/**
	 * Returns potentially empty list of exception names.
	 */
	public final List<String> getExceptionNames() {
		return Collections.unmodifiableList(_exceptionNames);
	}

	// --------------------- parameters ---------------------

	abstract protected void createParams(T parsSrc, EaHelper eaHelper);

	/**
	 * It is the responsibility of the model builder to call this method after all the classes in
	 * the model have been initialised.
	 */
	public final void assignTypeToParametersAndExceptions(EaModelBuilder<?, ?> model) {
		// Each method below fills the non-null classes into the set:
		Set<ClassBuilder<?, ?, ?, ?, ?, ?>> collectedTypes = new HashSet<ClassBuilder<?, ?, ?, ?, ?, ?>>();

		assignTypeToParameters(collectedTypes, model);
		if (getKind() != UmlOperation.ReturnKind.OP_RET_VOID) {
			assignTypeToReturnParameter(collectedTypes, model);
		}
		if (!getExceptionNames().isEmpty()) {
			assignTypeToExceptions(collectedTypes, model);
		}

		// Now we can "link" classes that are afferent/efferent due to operations
		for (ClassBuilder<?, ?, ?, ?, ?, ?> type : collectedTypes) {
			addOperationAfferrentAndEfferentClasses(type);
		}
	}

	private void assignTypeToParameters(Set<ClassBuilder<?, ?, ?, ?, ?, ?>> collectedTypes,
			EaModelBuilder<?, ?> model) {
		for (ParameterBuilder<?> par : getParameters()) {
			ClassBuilder<?, ?, ?, ?, ?, ?> type = par.assignType(getQualifiedName(), model);
			collectedTypes.add(type);
		}
	}

	private void assignTypeToReturnParameter(Set<ClassBuilder<?, ?, ?, ?, ?, ?>> collectedTypes,
			EaModelBuilder<?, ?> model) {
		ClassBuilder<?, ?, ?, ?, ?, ?> type = model.findClass(Integer.valueOf(getEaReturnTypeId()));
		if (type != null) {
			_logger.trace("Found type '" + type.getObjData().getName()
					+ "' for return parameter of operation " + getQualifiedName() + ".");
		}
		setReturnType(type);

		collectedTypes.add(type);
	}

	private void assignTypeToExceptions(Set<ClassBuilder<?, ?, ?, ?, ?, ?>> collectedTypes,
			EaModelBuilder<?, ?> model) {
		for (String excName : getExceptionNames()) {
			ClassBuilder<?, ?, ?, ?, ?, ?> type = model.findClass(excName);
			if (type != null) {
				_logger.trace("Found type '" + type.getObjData().getName()
						+ "' for exception of operation " + getQualifiedName() + ".");
			}
			getExceptions().add(type);
			collectedTypes.add(type);
		}
	}

	private String getQualifiedName() {
		return getContainingClass().getObjData().getName() + AbstractUmlObject.CLASS_SEPARATOR
				+ getObjData().getName();
	}

	private void addOperationAfferrentAndEfferentClasses(ClassBuilder<?, ?, ?, ?, ?, ?> type) {
		if (type != null) {
			type.addOperationAfferentClass(this.getContainingClass());
			this.getContainingClass().addOperationEfferentClass(type);
		}
	}

	public final ClassBuilder<?, ?, ?, ?, ?, ?> getContainingClass() {
		return _containingClass;
	}

	public final int getPos() {
		return _pos;
	}

	public final boolean isAbstract() {
		return _abstract;
	}

	public final boolean isStatic() {
		return _static;
	}

	public final boolean isFinal() {
		return _final;
	}

	public final ReturnKind getKind() {
		return _kind;
	}

	public final int getEaReturnTypeId() {
		return _eaReturnTypeId;
	}

	public final String getEaReturnTypeName() {
		return _eaReturnTypeName;
	}

	// ------------------------ tagged values -------------------------

	abstract protected List<Map<String, String>> fetchTaggedValues(T tagsSrc);

	private void initTaggedValues(List<Map<String, String>> myTaggedValuesFields) {
		for (Map<String, String> m : myTaggedValuesFields) {
			String name = m.get(EA.OP_TGVAL_NAME);
			String value = m.get(EA.OP_TGVAL_VALUE);
			getTaggedValues().put(name, Util.null2empty(value));
		}
	}

	public final Map<String, String> getTaggedValues() {
		return _taggedValues;
	}

	// --------------------------------------

	public final List<String> createEaExceptionTypeInfo() {
		List<String> result = new ArrayList<String>();
		for (String name : getExceptionNames()) {
			result.add("'" + name + "'");
		}
		return result;
	}

	public final List<ClassBuilder<?, ?, ?, ?, ?, ?>> getExceptions() {
		return _exceptions;
	}

	public final void setReturnType(ClassBuilder<?, ?, ?, ?, ?, ?> returnType) {
		if (_returnType != null) {
			throw new ProgrammerErrorException("Type already set for " + toString());
		}
		if (returnType != null) {
			_returnType = returnType;
			_logger.trace("Return type set for " + toString() + ".");
		}
	}

	public final ClassBuilder<?, ?, ?, ?, ?, ?> getReturnType() {
		return _returnType;
	}

	public final List<ParameterBuilder<?>> getParameters() {
		return _parameters;
	}

	// ====================

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("OperationBuilder [");
		sb.append("_containingClass=").append(_containingClass.getObjData().getName());
		sb.append(", _objData=").append(_objData);
		sb.append(", _abstract=").append(_abstract);
		sb.append(", _static=").append(_static);
		sb.append(", _final=").append(_final);
		sb.append(", _kind=").append(_kind);
		sb.append(", _returnType=").append(_returnType);
		sb.append(", _eaReturnTypeId=").append(_eaReturnTypeId);
		sb.append(", _eaReturnTypeName=").append(_eaReturnTypeName);
		if (!_exceptionNames.isEmpty()) {
			sb.append(", ").append(_exceptionNames.size());
			sb.append("_exceptionNames").append(_exceptionNames);
		}
		if (!_exceptions.isEmpty()) {
			sb.append(", ").append(_exceptions.size());
			sb.append("_exceptions").append(_exceptions);
		}
		if (!_taggedValues.isEmpty()) {
			sb.append(", ").append(_taggedValues.size());
			sb.append("_taggedValues").append(_taggedValues);
		}
		if (!_parameters.isEmpty()) {
			sb.append(", ").append(_parameters.size());
			sb.append("_parameters").append(_parameters);
		}
		sb.append("]");
		return sb.toString();
	}

	// ====================

	@Override
	public final UmlObjectData getObjData() {
		return _objData;
	}

	@Override
	protected final void doBuild() {
		Data data = new Data(getKind(), isAbstract(), isStatic(), isFinal(), getEaReturnTypeId(),
				getEaReturnTypeName(), createEaExceptionTypeInfo());

		UmlClass containingClass = getContainingClass().getResult();
		UmlClass returnType = null;
		if (getReturnType() == null) {
			if (data.getKind() != UmlOperation.ReturnKind.OP_RET_VOID) {
				Nature nature = containingClass.getNature();
				returnType = containingClass.getModel().getNullClasses().get(nature);
			}
		} else {
			returnType = getReturnType().getResult();
		}

		setResult(containingClass.addOperation(returnType, getObjData(), data));

		for (Entry<String, String> entry : getTaggedValues().entrySet()) {
			getResult().addTaggedValue(entry.getKey(), entry.getValue());
		}
		for (ParameterBuilder<?> pb : getParameters()) {
			getResult().addParameter(pb.build());
		}
		for (ClassBuilder<?, ?, ?, ?, ?, ?> cb : getExceptions()) {
			UmlClass exc = null;
			if (cb == null) {
				Nature nature = containingClass.getNature();
				exc = containingClass.getModel().getNullClasses().get(nature);
			} else {
				exc = cb.getResult();
			}
			getResult().addException(exc);
		}
	}
}
