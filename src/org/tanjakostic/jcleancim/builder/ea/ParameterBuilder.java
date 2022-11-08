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

import org.apache.log4j.Logger;
import org.tanjakostic.jcleancim.common.Nature;
import org.tanjakostic.jcleancim.model.UmlClass;
import org.tanjakostic.jcleancim.model.UmlKind;
import org.tanjakostic.jcleancim.model.UmlObjectData;
import org.tanjakostic.jcleancim.model.UmlParameter;
import org.tanjakostic.jcleancim.model.UmlStereotype;
import org.tanjakostic.jcleancim.model.UmlParameter.Data;
import org.tanjakostic.jcleancim.model.UmlParameter.Kind;
import org.tanjakostic.jcleancim.util.ProgrammerErrorException;
import org.tanjakostic.jcleancim.util.Util;

/**
 * Does not have tagged values.
 *
 * @param <O>
 *            Source data for operation parameter
 * @author tatjana.kostic@ieee.org
 * @version $Id: ParameterBuilder.java 21 2019-08-12 15:44:50Z dev978 $
 */
public abstract class ParameterBuilder<O> extends AbstractObjectBuilderFromEA<UmlParameter> {
	private static final Logger _logger = Logger.getLogger(ParameterBuilder.class.getName());

	private final OperationBuilder<?, ?> _containingOperation;
	private UmlObjectData _objData;

	private String _eaTypeName;
	private String _eaTypeIdAsString;
	private int _position;
	private UmlKind _kind;

	private ClassBuilder<?, ?, ?, ?, ?, ?> _type;

	protected ParameterBuilder(O inData, OperationBuilder<?, ?> containingOperation,
			EaHelper eaHelper) {
		Util.ensureNotNull(inData, "inData");
		Util.ensureNotNull(containingOperation, "containingOperation");
		Util.ensureNotNull(eaHelper, "helper");

		_containingOperation = containingOperation;

		String guid = getParameterGUID(inData);
		String name = getParameterName(inData);
		String alias = getParameterAlias(inData);
		String stereotypes = getParameterStereotypes(inData);
		String notes = getParameterNotes(inData);
		initObjData(guid, name, alias, stereotypes, notes, eaHelper);

		String typeName = getParameterType(inData);
		String typeIdAsString = getParameterClassifierID(inData);
		int position = getParameterPosition(inData);
		initOwnData(typeName, typeIdAsString, position);
	}

	abstract protected String getParameterGUID(O inData);

	abstract protected String getParameterName(O inData);

	abstract protected String getParameterAlias(O inData);

	abstract protected String getParameterStereotypes(O inData);

	abstract protected String getParameterNotes(O inData);

	private void initObjData(String guid, String name, String alias, String stereotype,
			String notes, EaHelper helper) {
		_objData = new UmlObjectData(null, guid, name, alias, new UmlStereotype(stereotype), null,
				helper.getRawText(notes), helper.getHtmlText(notes));
	}

	abstract protected String getParameterType(O inData);

	abstract protected String getParameterClassifierID(O inData);

	abstract protected int getParameterPosition(O inData);

	private void initOwnData(String typeName, String typeIdAsString, int position) {
		_eaTypeName = typeName;
		_eaTypeIdAsString = typeIdAsString;
		_position = position;
		_kind = typeName.endsWith(Kind.ARRAY.getValue()) ? UmlParameter.Kind.ARRAY
				: UmlParameter.Kind.SIMPLE;

		setType(null); // deduced after all UML classes have been created
	}

	private void setType(ClassBuilder<?, ?, ?, ?, ?, ?> type) {
		if (_type != null) {
			throw new ProgrammerErrorException("Type already set for " + toString());
		}
		if (type != null) {
			_type = type;
			_logger.trace("Type set for parameter " + toString() + ".");
		}
	}

	/**
	 * It is the responsibility of the model builder or its delegate to call this method after all
	 * the classes in the model have been initialised.
	 */
	public final ClassBuilder<?, ?, ?, ?, ?, ?> assignType(String opFullyQualifiedName,
			EaModelBuilder<?, ?> model) {
		Integer typeId = Integer.valueOf(getEaTypeIdAsString());
		ClassBuilder<?, ?, ?, ?, ?, ?> type = model.findClass(typeId);
		if (getKind() == UmlParameter.Kind.ARRAY) {
			assert (type == null) : "EA does not hold correct type ID for array params.";

			String typeName = getEaTypeName();

			// remove trailing "[]" and try to find class with that name:
			String trimmedTypeName = typeName.substring(0, typeName.length() - 2);
			type = model.findClass(trimmedTypeName);
			if (type != null) {
				_logger.trace("Found type '" + type.getObjData().getName()
						+ "' for array param of operation " + opFullyQualifiedName + ".");
			}
		}
		setType(type);
		return type;
	}

	public final OperationBuilder<?, ?> getContainingOperation() {
		return _containingOperation;
	}

	public final String getEaTypeName() {
		return _eaTypeName;
	}

	public final String getEaTypeIdAsString() {
		return _eaTypeIdAsString;
	}

	public final int getPosition() {
		return _position;
	}

	public final UmlKind getKind() {
		return _kind;
	}

	public final ClassBuilder<?, ?, ?, ?, ?, ?> getType() {
		return _type;
	}

	public final String getEaTypeInfo() {
		return String.format("'%s' (id=%s) used as type in parameter '%s' (%d)", getEaTypeName(),
				getEaTypeIdAsString(), getObjData().getName(), Integer.valueOf(getPosition()));
	}

	// ===============================

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("ParameterBuilder [");
		sb.append("_containingOperation=").append(_containingOperation.getObjData().getName());
		sb.append(", _position=").append(_position);
		sb.append(", _kind=").append(_kind);
		sb.append(", _objData=").append(_objData);
		sb.append(", _type=").append(_type);
		sb.append(", _eaTypeName=").append(_eaTypeName);
		sb.append(", _eaTypeIdAsString=").append(_eaTypeIdAsString);
		sb.append("]");
		return sb.toString();
	}

	// ===============================

	@Override
	public final UmlObjectData getObjData() {
		return _objData;
	}

	@Override
	protected final void doBuild() {
		UmlClass type = null;

		if (getType() == null) {
			UmlClass containingClass = getContainingOperation().getContainingClass().getResult();
			Nature nature = containingClass.getNature();
			type = containingClass.getModel().getNullClasses().get(nature);
		} else {
			type = getType().getResult();
		}
		Data data = new Data(getKind(), getEaTypeInfo());

		setResult(new UmlParameter(type, getObjData(), data));
		// tagged values ignored
	}
}
