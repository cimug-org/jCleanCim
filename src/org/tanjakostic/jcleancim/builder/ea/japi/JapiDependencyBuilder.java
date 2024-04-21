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
import org.sparx.ConnectorTag;
import org.tanjakostic.jcleancim.builder.ea.ClassBuilder;
import org.tanjakostic.jcleancim.builder.ea.DependencyBuilder;
import org.tanjakostic.jcleancim.builder.ea.EA;
import org.tanjakostic.jcleancim.builder.ea.EaHelper;
import org.tanjakostic.jcleancim.builder.ea.EaModelBuilder;
import org.tanjakostic.jcleancim.builder.ea.PackageBuilder;

/**
 * @author tatjana.kostic@ieee.org
 * @version $Id: JapiDependencyBuilder.java 21 2019-08-12 15:44:50Z dev978 $
 */
class JapiDependencyBuilder extends DependencyBuilder<Connector, Connector> {

	/**
	 * Constructor.
	 *
	 * @param inData
	 * @param model
	 * @param sourcePackage
	 * @param targetPackage
	 * @param sourceClass
	 * @param targetClass
	 * @param eaHelper
	 */
	public JapiDependencyBuilder(Connector inData, EaModelBuilder<?, ?> model,
			PackageBuilder<?, ?, ?, ?, ?, ?> sourcePackage,
			PackageBuilder<?, ?, ?, ?, ?, ?> targetPackage,
			ClassBuilder<?, ?, ?, ?, ?, ?> sourceClass, ClassBuilder<?, ?, ?, ?, ?, ?> targetClass,
			EaHelper eaHelper) {
		super(inData, model, inData, sourcePackage, targetPackage, sourceClass, targetClass,
				eaHelper);
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
	protected List<Map<String, String>> fetchTaggedValues(Connector eaDep) {
		List<Map<String, String>> result = new ArrayList<Map<String, String>>();

		Collection<ConnectorTag> taggedValues = eaDep.GetTaggedValues();
		int count = taggedValues.GetCount();
		for (short i = 0; i < count; ++i) {
			ConnectorTag tag = taggedValues.GetAt(i);
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
