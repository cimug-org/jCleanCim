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

import java.util.List;

import org.tanjakostic.jcleancim.model.UmlAttribute;
import org.tanjakostic.jcleancim.model.UmlClass;
import org.tanjakostic.jcleancim.model.UmlConstraint;
import org.tanjakostic.jcleancim.model.UmlKind;
import org.tanjakostic.jcleancim.model.UmlObjectData;
import org.tanjakostic.jcleancim.model.UmlConstraint.Data;
import org.tanjakostic.jcleancim.util.Util;

/**
 * @author tatjana.kostic@ieee.org
 * @version $Id: ConstraintBuilder.java 21 2019-08-12 15:44:50Z dev978 $
 */
public class ConstraintBuilder extends AbstractObjectBuilderFromEA<UmlConstraint> {
	private UmlObjectData _objData;

	private ClassBuilder<?, ?, ?, ?, ?, ?> _containingClass;
	private AttributeBuilder<?, ?> _containingAttribute;

	private UmlKind _kind;
	private List<String> _attrNames;
	private String _condition;

	/**
	 * Constructor for testing only.
	 */
	ConstraintBuilder() {
		// no-op
	}

	/**
	 * Constructor for class constraint from EA object. Sets id to 0, and uuid, visibility, alias
	 * and stereotype to their default values - they are not defined for constraints in EA.
	 *
	 * @throws NullPointerException
	 *             if <code>containingClass</code> or <code>helper</code> is null.
	 */
	public ConstraintBuilder(ClassBuilder<?, ?, ?, ?, ?, ?> containingClass, String name,
			String notes, EaHelper helper) {
		Util.ensureNotNull(containingClass, "containingClass");
		Util.ensureNotNull(helper, "helper");

		_objData = new UmlObjectData(null, null, name, null, null, null, helper.getRawText(notes),
				helper.getHtmlText(notes));
		_kind = UmlConstraint.Kind.CLASS;
		_containingClass = containingClass;
		_attrNames = deduceAttrNamesAndInitCondition(getObjData().getTxtDescription().text);
		_containingAttribute = null;
	}

	/**
	 * Constructor for attribute constraint from EA object. Sets id to 0, and uuid, visibility,
	 * alias and stereotype to their default values - they are not defined for constraints in EA.
	 *
	 * @throws NullPointerException
	 *             if <code>containingAttribute</code> or <code>helper</code> is null.
	 */
	ConstraintBuilder(AttributeBuilder<?, ?> containingAttribute, String name, String notes,
			EaHelper eaHelper) {
		Util.ensureNotNull(containingAttribute, "containingAttribute");
		Util.ensureNotNull(eaHelper, "helper");

		_objData = new UmlObjectData(null, null, name, null, null, null,
				eaHelper.getRawText(notes), eaHelper.getHtmlText(notes));
		_kind = UmlConstraint.Kind.ATTR_MIN_MAX;
		_containingClass = null;
		_attrNames = null;
		_condition = getObjData().getTxtDescription().text;
		_containingAttribute = containingAttribute;
	}

	private List<String> deduceAttrNamesAndInitCondition(String description) {
		int sepIdx = description.indexOf(UmlConstraint.SEPARATOR);
		boolean hasCondition = sepIdx != -1;

		if (hasCondition) {
			_condition = description.substring(sepIdx + 1).trim();
			return Util.splitCommaSeparatedTokens(description.substring(0, sepIdx));
		}

		_condition = "";
		return Util.splitCommaSeparatedTokens(description.trim());
	}

	public final ClassBuilder<?, ?, ?, ?, ?, ?> getContainingClass() {
		return _containingClass;
	}

	public final AttributeBuilder<?, ?> getContainingAttribute() {
		return _containingAttribute;
	}

	public final UmlKind getKind() {
		return _kind;
	}

	public final List<String> getAttrNames() {
		return _attrNames;
	}

	public final String getCondition() {
		return _condition;
	}

	// =============================================

	@Override
	public String toString() {
		String result = "ConstraintBuilder [_objData=" + _objData;
		if (_containingClass != null) {
			result += ", _containingClass=" + _containingClass.getObjData().getName();
		}
		if (_containingAttribute != null) {
			result += ", _containingAttribute=" + _containingAttribute.getObjData().getName();
		}
		result += ", _kind=" + _kind;
		result += ", _attrNames=" + _attrNames;
		result += ", _condition=" + _condition + "]";
		return result;
	}

	// =============================================

	@Override
	public final UmlObjectData getObjData() {
		return _objData;
	}

	@Override
	protected void doBuild() {
		UmlClass c = getContainingClass() != null ? getContainingClass().getResult() : null;
		UmlAttribute a = getContainingAttribute() != null ? getContainingAttribute().getResult()
				: null;
		if (c == null && a == null) {
			throw new RuntimeException(
					"either containing class or attribute should have been built");
		}

		Data data = new Data(getAttrNames(), getCondition(), false);
		if (c != null) {
			setResult(c.addConstraint(getObjData(), data));
		} else if (a != null) {
			setResult(a.addOwnConstraint(getObjData(), data));
		}
	}
}
