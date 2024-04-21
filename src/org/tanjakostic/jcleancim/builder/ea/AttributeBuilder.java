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
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.log4j.Logger;
import org.tanjakostic.jcleancim.common.Nature;
import org.tanjakostic.jcleancim.model.UmlAttribute;
import org.tanjakostic.jcleancim.model.UmlClass;
import org.tanjakostic.jcleancim.model.UmlMultiplicity;
import org.tanjakostic.jcleancim.model.UmlObjectData;
import org.tanjakostic.jcleancim.model.UmlStereotype;
import org.tanjakostic.jcleancim.model.UmlAttribute.Data;
import org.tanjakostic.jcleancim.util.ProgrammerErrorException;
import org.tanjakostic.jcleancim.util.Util;

/**
 * <p>
 * It is the responsibility of the model builder to call {@link #assignType} for every created
 * attribute, after all the classes have been loaded.
 *
 * @param <O>
 *            Source data for attribute
 * @param <T>
 *            Source data for attribute tagged values
 * @author tatjana.kostic@ieee.org
 * @version $Id: AttributeBuilder.java 21 2019-08-12 15:44:50Z dev978 $
 */
abstract public class AttributeBuilder<O, T> extends AbstractObjectBuilderFromEA<UmlAttribute> {
	private static final Logger _logger = Logger.getLogger(AttributeBuilder.class.getName());

	private final ClassBuilder<?, ?, ?, ?, ?, ?> _containingClass;
	private UmlObjectData _objData;

	private int _pos;
	private boolean _isConst;
	private boolean _isStatic;
	private UmlMultiplicity _multiplicity;
	private String _initValue;
	private int _eaTypeId;
	private String _eaTypeName;
	private boolean _isLiteral;
	private boolean _isTypeSuperfluous;
	private final List<ConstraintBuilder> _constraints = new ArrayList<ConstraintBuilder>();
	private final Map<String, String> _taggedValues = new LinkedHashMap<String, String>();

	private ClassBuilder<?, ?, ?, ?, ?, ?> _type;

	/**
	 * Constructor for attribute from EA object.
	 *
	 * @param inData
	 * @param tagsSrc
	 * @param containingClass
	 * @param eaHelper
	 * @throws NullPointerException
	 *             if containingClass, eaAttr or helper is null.
	 */
	protected AttributeBuilder(O inData, T tagsSrc, ClassBuilder<?, ?, ?, ?, ?, ?> containingClass,
			EaHelper eaHelper) {
		Util.ensureNotNull(inData, "inData");
		Util.ensureNotNull(containingClass, "containingClass");
		Util.ensureNotNull(eaHelper, "helper");

		_containingClass = containingClass;
		EaModelBuilder<?, ?> model = containingClass.getContainingPackage().getModel();

		Integer id = getAttributeID(inData);
		String guid = getAttributeGUID(inData);
		String name = getAttributeName(inData);
		String alias = getAttributeAlias(inData);
		String stereotype = getAttributeStereotypes(inData);
		String visibility = getAttributeVisibility(inData);
		String notes = getAttributeNotes(inData);
		initObjData(id, guid, name, alias, stereotype, visibility, notes, eaHelper);
		model.addAttribute(this);

		int pos = getAttributePosition(inData);
		boolean isConst = getAttributeIsConst(inData);
		boolean isStatic = getAttributeIsStatic(inData);
		String lowerBound = getAttributeLowerBound(inData);
		String upperBound = getAttributeUpperBound(inData);
		String defaultVal = getAttributeDefaultValue(inData);
		int classifierID = getAttributeClassifierID(inData);
		String type = getAttributeType(inData);
		initOwnData(pos, containingClass.isEnumeratedType(), isConst, isStatic, lowerBound,
				upperBound, defaultVal, classifierID, type);

		List<Map<String, String>> constraints = fetchAttrConstraints(tagsSrc);
		initAttrConstraints(constraints, eaHelper);

		List<Map<String, String>> taggedValues = fetchTaggedValues(tagsSrc);
		initTaggedValues(taggedValues);

		setType(null); // deduced after all UML classes have been created

		_logger.log(CTOR_LOG_LEVEL, "read from EA " + toString());
	}

	abstract protected Integer getAttributeID(O inData);

	abstract protected String getAttributeGUID(O inData);

	abstract protected String getAttributeName(O inData);

	abstract protected String getAttributeAlias(O inData);

	abstract protected String getAttributeStereotypes(O inData);

	abstract protected String getAttributeVisibility(O inData);

	abstract protected String getAttributeNotes(O inData);

	protected final void initObjData(Integer id, String guid, String name, String alias,
			String stereotype, String visibility, String notes, EaHelper eaHelper) {
		_objData = new UmlObjectData(id, guid, name, alias, new UmlStereotype(stereotype),
				visibility, eaHelper.getRawText(notes), eaHelper.getHtmlText(notes));
	}

	abstract protected boolean getAttributeIsConst(O inData);

	abstract protected boolean getAttributeIsStatic(O inData);

	abstract protected String getAttributeLowerBound(O inData);

	abstract protected String getAttributeUpperBound(O inData);

	abstract protected String getAttributeDefaultValue(O inData);

	abstract protected int getAttributeClassifierID(O inData);

	abstract protected String getAttributeType(O inData);

	abstract protected int getAttributePosition(O inData);

	protected final void initOwnData(int pos, boolean isEnum, boolean isConst, boolean isStatic,
			String lowerBound, String upperBound, String defaultVal, int classifierID, String type) {
		_pos = pos;
		_isConst = isConst;
		_isStatic = isStatic;
		_multiplicity = UmlMultiplicity.parseBounds(lowerBound, upperBound);
		_initValue = Util.null2empty(defaultVal);
		_eaTypeId = classifierID;
		_eaTypeName = type;
		if (isEnum) {
			_isLiteral = true;
			_isTypeSuperfluous = !type.isEmpty();
		} else {
			_isLiteral = false;
			_isTypeSuperfluous = false;
		}
	}

	private void setType(ClassBuilder<?, ?, ?, ?, ?, ?> type) {
		if (_type != null) {
			throw new ProgrammerErrorException("Type already set for " + toString());
		}
		if (type != null) {
			_type = type;
			_logger.trace("Type set for " + toString() + ".");
		}
	}

	public final ClassBuilder<?, ?, ?, ?, ?, ?> getType() {
		return _type;
	}

	/**
	 * It is the responsibility of the model builder to call this method after all the classes in
	 * the model have been initialised.
	 */
	public final void assignType(Map<Integer, ClassBuilder<?, ?, ?, ?, ?, ?>> classes) {
		if (!isLiteral()) {
			Integer typeId = Integer.valueOf(getEaTypeId());
			ClassBuilder<?, ?, ?, ?, ?, ?> type = classes.get(typeId);
			setType(type);

			if (type != null) {
				type.addAttributeAfferentClass(getContainingClass());
				getContainingClass().addAttributeEfferentClass(type);
			}
		}
	}

	public final ClassBuilder<?, ?, ?, ?, ?, ?> getContainingClass() {
		return _containingClass;
	}

	public final int getPos() {
		return _pos;
	}

	public final boolean isConst() {
		return _isConst;
	}

	public final boolean isStatic() {
		return _isStatic;
	}

	public final UmlMultiplicity getMultiplicity() {
		return _multiplicity;
	}

	public final String getInitValue() {
		return _initValue;
	}

	public final int getEaTypeId() {
		return _eaTypeId;
	}

	public final String getEaTypeName() {
		return _eaTypeName;
	}

	public final boolean isLiteral() {
		return _isLiteral;
	}

	public final boolean isTypeSuperfluous() {
		return _isTypeSuperfluous;
	}

	// --------------- constraints ------------------------

	abstract protected List<Map<String, String>> fetchAttrConstraints(T tagsSrc);

	private void initAttrConstraints(List<Map<String, String>> myConstraints, EaHelper eaHelper) {
		for (Map<String, String> m : myConstraints) {
			String name = m.get(EA.ATTR_CONSTR_NAME);
			String notes = m.get(EA.ATTR_CONSTR_NOTE);
			getConstraints().add(new ConstraintBuilder(this, name, notes, eaHelper));
		}
	}

	public final List<ConstraintBuilder> getConstraints() {
		return _constraints;
	}

	// --------------------- tagged values ---------------------

	abstract protected List<Map<String, String>> fetchTaggedValues(T tagsSrc);

	private void initTaggedValues(List<Map<String, String>> myTaggedValuesFields) {
		for (Map<String, String> m : myTaggedValuesFields) {
			String name = m.get(EA.ATTR_TGVAL_NAME);
			String value = m.get(EA.ATTR_TGVAL_VALUE);
			getTaggedValues().put(name, Util.null2empty(value));
		}
	}

	public final Map<String, String> getTaggedValues() {
		return _taggedValues;
	}

	// ====================

	// public final String getQualifiedName() {
	// return getContainingClass().getQualifiedName() + AbstractUmlObject.CLASS_SEPARATOR
	// + getObjData().getName();
	// }

	@Override
	public String toString() {
		String result = "AttributeBuilder [_containingClass=" + _containingClass.getQualifiedName();
		result += ", _objData=" + _objData;
		result += ", _isConst=" + _isConst;
		result += ", _isStatic=" + _isStatic;
		result += ", _multiplicity=" + _multiplicity;
		result += ", _initValue=" + _initValue;
		result += ", _eaTypeId=" + _eaTypeId;
		result += ", _eaTypeName=" + _eaTypeName;
		result += ", _isLiteral=" + _isLiteral;
		result += ", _isTypeSuperfluous=" + _isTypeSuperfluous;
		result += ", _type=" + (_type == null ? "null" : _type);
		if (!_constraints.isEmpty()) {
			result += ", " + _constraints.size() + " _constraints" + _constraints;
		}
		if (!_taggedValues.isEmpty()) {
			result += ", " + _taggedValues.size() + "_taggedValues" + _taggedValues;
		}
		result += "]";
		return result;
	}

	// ====================

	@Override
	public final UmlObjectData getObjData() {
		return _objData;
	}

	@Override
	protected final void doBuild() {
		UmlClass containingClass = (getContainingClass() != null) ? getContainingClass()
				.getResult() : null;
				if (containingClass == null) {
					throw new RuntimeException("containing class should have been built for attribute "
							+ getObjData());
				}

				UmlClass type;
				if (getType() == null && !isLiteral()) {
					Nature nature = containingClass.getNature();
					type = containingClass.getModel().getNullClasses().get(nature);
				} else if (isLiteral()) {
					type = null;
				} else {
					type = getType().getResult();
					if (type == null) {
						throw new RuntimeException("type should have been built for attribute "
								+ getObjData() + " of " + containingClass.getQualifiedName());
					}
				}

				Data data = new Data(isConst(), isStatic(), getMultiplicity(), getInitValue(),
						getEaTypeId(), getEaTypeName(), isTypeSuperfluous());

				UmlAttribute result = containingClass.addAttribute(type, getObjData(), data);
				setResult(result);

				for (Entry<String, String> entry : getTaggedValues().entrySet()) {
					result.addTaggedValue(entry.getKey(), entry.getValue());
				}
				for (ConstraintBuilder cb : getConstraints()) {
					cb.build();
				}
	}
}
