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

import org.tanjakostic.jcleancim.builder.ea.AssociationBuilder;
import org.tanjakostic.jcleancim.builder.ea.AssociationEndBuilder;
import org.tanjakostic.jcleancim.builder.ea.ClassBuilder;
import org.tanjakostic.jcleancim.builder.ea.EA;
import org.tanjakostic.jcleancim.builder.ea.EaHelper;
import org.tanjakostic.jcleancim.builder.ea.EaModelBuilder;

/**
 * @author tatjana.kostic@ieee.org
 * @version $Id: DbAssociationEndBuilder.java 21 2019-08-12 15:44:50Z dev978 $
 */
class DbAssociationEndBuilder extends
AssociationEndBuilder<Map<String, String>, EaModelBuilder<?, ?>> {

	/**
	 * Constructor.
	 *
	 * @param inData
	 * @param tagsSrc
	 * @param containingAssociation
	 * @param isSource
	 * @param type
	 * @param eaHelper
	 */
	public DbAssociationEndBuilder(Map<String, String> inData, EaModelBuilder<?, ?> tagsSrc,
			AssociationBuilder<?, ?> containingAssociation, boolean isSource,
			ClassBuilder<?, ?, ?, ?, ?, ?> type, EaHelper eaHelper) {
		super(inData, tagsSrc, containingAssociation, isSource, type, eaHelper);
	}

	@Override
	protected String getRoleName(Map<String, String> inData) {
		return inData.get(isSource() ? EA.CONN_FROM_NAME : EA.CONN_TO_NAME);
	}

	@Override
	protected String getRoleAlias(Map<String, String> inData) {
		return inData.get(isSource() ? EA.CONN_FROM_ALIAS : EA.CONN_TO_ALIAS);
	}

	@Override
	protected String getRoleStereotypes(Map<String, String> inData) {
		return inData.get(isSource() ? EA.CONN_FROM_STEREOS : EA.CONN_TO_STEREOS);
	}

	@Override
	protected String getRoleVisibility(Map<String, String> inData) {
		return inData.get(isSource() ? EA.CONN_FROM_SCOPE : EA.CONN_TO_SCOPE);
	}

	@Override
	protected String getRoleNotes(Map<String, String> inData) {
		return inData.get(isSource() ? EA.CONN_FROM_NOTE : EA.CONN_TO_NOTE);
	}

	@Override
	protected String getRoleAggregation(Map<String, String> inData) {
		return inData.get(isSource() ? EA.CONN_FROM_AGGREG : EA.CONN_TO_AGGREG);
	}

	@Override
	protected String getRoleCardinality(Map<String, String> inData) {
		return inData.get(isSource() ? EA.CONN_FROM_CARD : EA.CONN_TO_CARD);
	}

	@Override
	protected String getRoleNavigable(Map<String, String> inData) {
		return inData.get(isSource() ? EA.CONN_FROM_NAV : EA.CONN_TO_NAV);
	}

	@Override
	protected List<Map<String, String>> fetchTaggedValues(EaModelBuilder<?, ?> srcTags) {
		Integer connId = getContainingAssociation().getObjData().getId();
		return isSource() ? srcTags.getTables().findConnectorSourceEndTags(connId) : srcTags
				.getTables().findConnectorTargetEndTags(connId);
	}
}
