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

package org.tanjakostic.jcleancim.builder.ea.japi;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.sparx.Attribute;
import org.sparx.AttributeConstraint;
import org.sparx.AttributeTag;
import org.sparx.Collection;
import org.tanjakostic.jcleancim.builder.ea.AttributeBuilder;
import org.tanjakostic.jcleancim.builder.ea.ClassBuilder;
import org.tanjakostic.jcleancim.builder.ea.EA;
import org.tanjakostic.jcleancim.builder.ea.EaHelper;

/**
 * @author tatjana.kostic@ieee.org
 * @version $Id: JapiAttributeBuilder.java 21 2019-08-12 15:44:50Z dev978 $
 */
class JapiAttributeBuilder extends AttributeBuilder<Attribute, Attribute> {

	/**
	 * Constructor.
	 *
	 * @param inData
	 * @param containingClass
	 * @param eaHelper
	 */
	public JapiAttributeBuilder(Attribute inData, ClassBuilder<?, ?, ?, ?, ?, ?> containingClass,
			EaHelper eaHelper) {
		super(inData, inData, containingClass, eaHelper);
	}

	@Override
	protected Integer getAttributeID(Attribute inData) {
		return Integer.valueOf(inData.GetAttributeID());
	}

	@Override
	protected String getAttributeGUID(Attribute inData) {
		return inData.GetAttributeGUID();
	}

	@Override
	protected String getAttributeName(Attribute inData) {
		return inData.GetName();
	}

	@Override
	protected String getAttributeAlias(Attribute inData) {
		return inData.GetStyle();
	}

	@Override
	protected String getAttributeStereotypes(Attribute inData) {
		return inData.GetStereotypeEx();
	}

	@Override
	protected String getAttributeVisibility(Attribute inData) {
		return inData.GetVisibility();
	}

	@Override
	protected String getAttributeNotes(Attribute inData) {
		return inData.GetNotes();
	}

	@Override
	protected boolean getAttributeIsConst(Attribute inData) {
		return inData.GetIsConst();
	}

	@Override
	protected boolean getAttributeIsStatic(Attribute inData) {
		return inData.GetIsStatic();
	}

	@Override
	protected String getAttributeLowerBound(Attribute inData) {
		return inData.GetLowerBound();
	}

	@Override
	protected String getAttributeUpperBound(Attribute inData) {
		return inData.GetUpperBound();
	}

	@Override
	protected String getAttributeDefaultValue(Attribute inData) {
		return inData.GetDefault();
	}

	@Override
	protected int getAttributeClassifierID(Attribute inData) {
		return inData.GetClassifierID();
	}

	@Override
	protected String getAttributeType(Attribute inData) {
		return inData.GetType();
	}

	@Override
	protected int getAttributePosition(Attribute inData) {
		return inData.GetPos();
	}

	@Override
	protected List<Map<String, String>> fetchTaggedValues(Attribute tagsSrc) {
		List<Map<String, String>> result = new ArrayList<Map<String, String>>();

		Collection<AttributeTag> eaTaggedValues = tagsSrc.GetTaggedValues();
		int count = eaTaggedValues.GetCount();
		for (short i = 0; i < count; ++i) {
			AttributeTag tag = eaTaggedValues.GetAt(i);
			String name = tag.GetName();
			String value = tag.GetValue();

			Map<String, String> fields = new LinkedHashMap<String, String>();
			fields.put(EA.ATTR_TGVAL_NAME, name);
			fields.put(EA.ATTR_TGVAL_VALUE, value);
			result.add(fields);
		}
		return result;
	}

	@Override
	protected List<Map<String, String>> fetchAttrConstraints(Attribute tagsSrc) {
		List<Map<String, String>> result = new ArrayList<Map<String, String>>();

		Collection<AttributeConstraint> eaAttrConstraints = tagsSrc.GetConstraints();
		int count = eaAttrConstraints.GetCount();
		for (short i = 0; i < count; ++i) {
			AttributeConstraint eaConstraint = eaAttrConstraints.GetAt(i);
			String name = eaConstraint.GetName();
			String notes = eaConstraint.GetNotes();

			Map<String, String> fields = new LinkedHashMap<String, String>();
			fields.put(EA.ATTR_CONSTR_NAME, name);
			fields.put(EA.ATTR_CONSTR_NOTE, notes);
			result.add(fields);
		}
		return result;
	}
}
