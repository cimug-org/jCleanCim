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
import org.sparx.ConnectorEnd;
import org.sparx.RoleTag;
import org.tanjakostic.jcleancim.builder.ea.AssociationBuilder;
import org.tanjakostic.jcleancim.builder.ea.AssociationEndBuilder;
import org.tanjakostic.jcleancim.builder.ea.ClassBuilder;
import org.tanjakostic.jcleancim.builder.ea.EA;
import org.tanjakostic.jcleancim.builder.ea.EaHelper;

/**
 * @author tatjana.kostic@ieee.org
 * @version $Id: JapiAssociationEndBuilder.java 21 2019-08-12 15:44:50Z dev978 $
 */
class JapiAssociationEndBuilder extends AssociationEndBuilder<ConnectorEnd, ConnectorEnd> {

	/**
	 * Constructor.
	 *
	 * @param eaEnd
	 * @param containingAssociation
	 * @param isSource
	 * @param type
	 * @param eaHelper
	 */
	protected JapiAssociationEndBuilder(ConnectorEnd eaEnd,
			AssociationBuilder<?, ?> containingAssociation, boolean isSource,
			ClassBuilder<?, ?, ?, ?, ?, ?> type, EaHelper eaHelper) {
		super(eaEnd, eaEnd, containingAssociation, isSource, type, eaHelper);
	}

	@Override
	protected String getRoleName(ConnectorEnd inData) {
		return inData.GetRole();
	}

	@Override
	protected String getRoleAlias(ConnectorEnd inData) {
		return inData.GetAlias();
	}

	@Override
	protected String getRoleStereotypes(ConnectorEnd inData) {
		return inData.GetStereotypeEx();
	}

	@Override
	protected String getRoleVisibility(ConnectorEnd inData) {
		return inData.GetVisibility();
	}

	@Override
	protected String getRoleNotes(ConnectorEnd inData) {
		return inData.GetRoleNote();
	}

	@Override
	protected String getRoleAggregation(ConnectorEnd inData) {
		return Integer.toString(inData.GetAggregation());
	}

	@Override
	protected String getRoleCardinality(ConnectorEnd inData) {
		return inData.GetCardinality();
	}

	@Override
	protected String getRoleNavigable(ConnectorEnd inData) {
		return inData.GetNavigable();
	}

	@Override
	protected final List<Map<String, String>> fetchTaggedValues(ConnectorEnd tagsSrc) {
		List<Map<String, String>> result = new ArrayList<Map<String, String>>();

		Collection<RoleTag> eaTaggedValues = tagsSrc.GetTaggedValues();
		int count = eaTaggedValues.GetCount();
		for (short i = 0; i < count; ++i) {
			RoleTag tag = eaTaggedValues.GetAt(i);
			String name = tag.GetTag();
			String value = tag.GetValue();

			Map<String, String> fields = new LinkedHashMap<String, String>();
			fields.put(EA.ROLE_TGVAL_NAME, name);
			fields.put(EA.ROLE_TGVAL_VALUE, value);
			result.add(fields);
		}
		return result;
	}
}
