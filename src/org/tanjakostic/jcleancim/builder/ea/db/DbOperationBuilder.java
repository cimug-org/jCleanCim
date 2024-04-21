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

package org.tanjakostic.jcleancim.builder.ea.db;

import java.util.List;
import java.util.Map;

import org.tanjakostic.jcleancim.builder.ea.ClassBuilder;
import org.tanjakostic.jcleancim.builder.ea.EA;
import org.tanjakostic.jcleancim.builder.ea.EaHelper;
import org.tanjakostic.jcleancim.builder.ea.EaModelBuilder;
import org.tanjakostic.jcleancim.builder.ea.OperationBuilder;
import org.tanjakostic.jcleancim.util.Util;

/**
 * @author tatjana.kostic@ieee.org
 * @version $Id: DbOperationBuilder.java 21 2019-08-12 15:44:50Z dev978 $
 */
class DbOperationBuilder extends OperationBuilder<Map<String, String>, EaModelBuilder<?, ?>> {

	/**
	 * Constructor.
	 *
	 * @param inData
	 * @param containingClass
	 * @param eaHelper
	 */
	public DbOperationBuilder(Map<String, String> inData,
			ClassBuilder<?, ?, ?, ?, ?, ?> containingClass, EaHelper eaHelper) {
		super(inData, containingClass.getContainingPackage().getModel(), containingClass, eaHelper);
	}

	@Override
	protected Integer getOperationID(Map<String, String> inData) {
		return Util.parseInt(inData.get(EA.OP_ID));
	}

	@Override
	protected String getOperationGUID(Map<String, String> inData) {
		return inData.get(EA.EA_GUID);
	}

	@Override
	protected String getOperationName(Map<String, String> inData) {
		return inData.get(EA.OP_NAME);
	}

	@Override
	protected String getOperationAlias(Map<String, String> inData) {
		return inData.get(EA.OP_ALIAS);
	}

	@Override
	protected String getOperationStereotypes(Map<String, String> inData) {
		return inData.get(EA.DEDUCED_STEREOS);
	}

	@Override
	protected String getOperationVisibility(Map<String, String> inData) {
		return inData.get(EA.OP_SCOPE);
	}

	@Override
	protected String getOperationNotes(Map<String, String> inData) {
		return inData.get(EA.OP_NOTE);
	}

	@Override
	protected boolean getOperationIsAbstract(Map<String, String> inData) {
		return "1".equals(inData.get(EA.OP_ABSTRACT));
	}

	@Override
	protected boolean getOperationIsStatic(Map<String, String> inData) {
		return "1".equals(inData.get(EA.OP_STATIC));
	}

	@Override
	protected boolean getOperationIsLeaf(Map<String, String> inData) {
		return Boolean.parseBoolean(inData.get(EA.OP_FINAL).toLowerCase());
	}

	@Override
	protected boolean getOperationIsReturnArray(Map<String, String> inData) {
		return "1".equals(inData.get(EA.OP_RET_ARRAY));
	}

	@Override
	protected String getOperationReturnType(Map<String, String> inData) {
		return inData.get(EA.OP_RET_TYPE_NAME);
	}

	@Override
	protected String getOperationClassifierID(Map<String, String> inData) {
		String classifierId = inData.get(EA.OP_RET_TYPE_ID);
		if (classifierId == null || classifierId.trim().isEmpty()) {
			classifierId = "0";
		}
		return classifierId;
	}

	@Override
	protected int getOperationPosition(Map<String, String> inData) {
		return Util.parseIntZero(inData.get(EA.OP_POS)).intValue();
	}

	@Override
	protected List<Map<String, String>> fetchTaggedValues(EaModelBuilder<?, ?> tagsSrc) {
		return tagsSrc.getTables().findOperationTags(getObjData().getId());
	}

	@Override
	protected void createParams(EaModelBuilder<?, ?> parsSrc, EaHelper eaHelper) {
		List<Map<String, String>> parFields = parsSrc.getTables().findOrderedParameters(
				getObjData().getId());
		for (Map<String, String> m : parFields) {
			getParameters().add(new DbParameterBuilder(m, this, eaHelper));
		}
	}
}
