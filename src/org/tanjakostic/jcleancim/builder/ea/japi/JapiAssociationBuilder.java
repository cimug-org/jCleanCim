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
import org.sparx.Connector;
import org.sparx.ConnectorEnd;
import org.sparx.ConnectorTag;
import org.tanjakostic.jcleancim.builder.ea.AssociationBuilder;
import org.tanjakostic.jcleancim.builder.ea.ClassBuilder;
import org.tanjakostic.jcleancim.builder.ea.EA;
import org.tanjakostic.jcleancim.builder.ea.EaHelper;
import org.tanjakostic.jcleancim.builder.ea.EaModelBuilder;
import org.tanjakostic.jcleancim.model.UmlVisibility;

/**
 * @author tatjana.kostic@ieee.org
 * @version $Id: JapiAssociationBuilder.java 21 2019-08-12 15:44:50Z dev978 $
 */
class JapiAssociationBuilder extends AssociationBuilder<Connector, Connector> {

	/**
	 * Constructor.
	 * <p>
	 * Visibility is always set to {@link UmlVisibility#PUBLIC}.
	 *
	 * @param inData
	 * @param source
	 * @param target
	 * @param eaHelper
	 * @param model
	 */
	public JapiAssociationBuilder(Connector inData, ClassBuilder<?, ?, ?, ?, ?, ?> source,
			ClassBuilder<?, ?, ?, ?, ?, ?> target, EaHelper eaHelper, EaModelBuilder<?, ?> model) {
		super(inData, inData, source, target, model, eaHelper);
	}

	@Override
	protected Integer getConnectorID(Connector inData) {
		return Integer.valueOf(inData.GetConnectorID());
	}

	@Override
	protected String getConnectorGUID(Connector inData) {
		return inData.GetConnectorGUID();
	}

	@Override
	protected String getConnectorName(Connector inData) {
		return inData.GetName();
	}

	@Override
	protected String getConnectorAlias(Connector inData) {
		return inData.GetAlias();
	}

	@Override
	protected String getConnectorStereotypes(Connector inData) {
		return inData.GetStereotypeEx();
	}

	@Override
	protected String getConnectorNotes(Connector inData) {
		return inData.GetNotes();
	}

	@Override
	protected String getConnectorDirection(Connector inData) {
		return inData.GetDirection();
	}

	@Override
	protected JapiAssociationEndBuilder createAssociationEnd(Connector inData, Connector tagsSrc,
			boolean isSource, ClassBuilder<?, ?, ?, ?, ?, ?> type, EaHelper eaHelper) {
		ConnectorEnd eaEnd = (isSource ? inData.GetClientEnd() : inData.GetSupplierEnd());
		return new JapiAssociationEndBuilder(eaEnd, this, isSource, type, eaHelper);
	}

	@Override
	protected List<Map<String, String>> fetchTaggedValues(Connector inDataTags) {
		List<Map<String, String>> result = new ArrayList<Map<String, String>>();

		Collection<ConnectorTag> eaTaggedValues = inDataTags.GetTaggedValues();
		int count = eaTaggedValues.GetCount();
		for (short i = 0; i < count; ++i) {
			ConnectorTag tag = eaTaggedValues.GetAt(i);
			String name = tag.GetName();
			String value = tag.GetValue();

			Map<String, String> fields = new LinkedHashMap<String, String>();
			fields.put(EA.CONN_TGVAL_NAME, name);
			fields.put(EA.CONN_TGVAL_VALUE, value);
			result.add(fields);
		}
		return result;
	}
}
