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

import org.sparx.Collection;
import org.sparx.Method;
import org.sparx.MethodTag;
import org.sparx.Parameter;
import org.tanjakostic.jcleancim.builder.ea.ClassBuilder;
import org.tanjakostic.jcleancim.builder.ea.EA;
import org.tanjakostic.jcleancim.builder.ea.EaHelper;
import org.tanjakostic.jcleancim.builder.ea.OperationBuilder;

/**
 * @author tatjana.kostic@ieee.org
 * @version $Id: JapiOperationBuilder.java 21 2019-08-12 15:44:50Z dev978 $
 */
class JapiOperationBuilder extends OperationBuilder<Method, Method> {

	/**
	 * Constructor.
	 *
	 * @param inData
	 * @param containingClass
	 * @param eaHelper
	 */
	public JapiOperationBuilder(Method inData, ClassBuilder<?, ?, ?, ?, ?, ?> containingClass,
			EaHelper eaHelper) {
		super(inData, inData, containingClass, eaHelper);
	}

	@Override
	protected Integer getOperationID(Method inData) {
		return Integer.valueOf(inData.GetMethodID());
	}

	@Override
	protected String getOperationGUID(Method inData) {
		return inData.GetMethodGUID();
	}

	@Override
	protected String getOperationName(Method inData) {
		return inData.GetName();
	}

	@Override
	protected String getOperationAlias(Method inData) {
		// EA stores alias for attribute/operation in the Style property (sic!):
		// http://www.sparxsystems.com/cgi-bin/yabb/YaBB.cgi?num=1195814570/5#5
		return inData.GetStyle();
	}

	@Override
	protected String getOperationStereotypes(Method inData) {
		return inData.GetStereotypeEx();
	}

	@Override
	protected String getOperationVisibility(Method inData) {
		return inData.GetVisibility();
	}

	@Override
	protected String getOperationNotes(Method inData) {
		return inData.GetNotes();
	}

	@Override
	protected boolean getOperationIsAbstract(Method inData) {
		return inData.GetAbstract();
	}

	@Override
	protected boolean getOperationIsStatic(Method inData) {
		return inData.GetIsStatic();
	}

	@Override
	protected boolean getOperationIsLeaf(Method inData) {
		return inData.GetIsLeaf();
	}

	@Override
	protected boolean getOperationIsReturnArray(Method inData) {
		return inData.GetReturnIsArray();
	}

	@Override
	protected String getOperationReturnType(Method inData) {
		return inData.GetReturnType();
	}

	@Override
	protected String getOperationClassifierID(Method inData) {
		return inData.GetClassifierID();
	}

	@Override
	protected int getOperationPosition(Method inData) {
		return inData.GetPos();
	}

	@Override
	protected List<Map<String, String>> fetchTaggedValues(Method tagsSrc) {
		List<Map<String, String>> result = new ArrayList<Map<String, String>>();
		Collection<MethodTag> eaTaggedValues = tagsSrc.GetTaggedValues();
		int count = eaTaggedValues.GetCount();
		for (short i = 0; i < count; ++i) {
			MethodTag tag = eaTaggedValues.GetAt(i);
			String name = tag.GetName();
			String value = tag.GetValue();

			Map<String, String> fields = new LinkedHashMap<String, String>();
			fields.put(EA.OP_TGVAL_NAME, name);
			fields.put(EA.OP_TGVAL_VALUE, value);
			result.add(fields);
		}
		return result;
	}

	@Override
	protected void createParams(Method parsSrc, EaHelper eaHelper) {
		Collection<Parameter> eaParams = parsSrc.GetParameters();
		int count = eaParams.GetCount();
		/*
		 * EA sometimes screws up the order of parameters, so the order it returns in the collection
		 * does not correspond to effective position of the parameter in operation signature. We
		 * thus have to first populate our parameter list with null objects, then set the real
		 * objects at the position given by EA field in its parameter.
		 */
		for (int i = 0; i < count; ++i) {
			getParameters().add(null);
		}
		for (short i = 0; i < count; ++i) {
			Parameter par = eaParams.GetAt(i);
			getParameters().set(par.GetPosition(), new JapiParameterBuilder(par, this, eaHelper));
		}
	}

}
