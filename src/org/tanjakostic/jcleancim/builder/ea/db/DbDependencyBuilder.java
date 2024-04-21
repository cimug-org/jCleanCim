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
import org.tanjakostic.jcleancim.builder.ea.DependencyBuilder;
import org.tanjakostic.jcleancim.builder.ea.EA;
import org.tanjakostic.jcleancim.builder.ea.EaHelper;
import org.tanjakostic.jcleancim.builder.ea.EaModelBuilder;
import org.tanjakostic.jcleancim.builder.ea.PackageBuilder;
import org.tanjakostic.jcleancim.util.Util;

/**
 * @author tatjana.kostic@ieee.org
 * @version $Id: DbDependencyBuilder.java 21 2019-08-12 15:44:50Z dev978 $
 */
class DbDependencyBuilder extends DependencyBuilder<Map<String, String>, EaModelBuilder<?, ?>> {

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
	public DbDependencyBuilder(Map<String, String> inData, EaModelBuilder<?, ?> model,
			PackageBuilder<?, ?, ?, ?, ?, ?> sourcePackage,
			PackageBuilder<?, ?, ?, ?, ?, ?> targetPackage,
			ClassBuilder<?, ?, ?, ?, ?, ?> sourceClass, ClassBuilder<?, ?, ?, ?, ?, ?> targetClass,
			EaHelper eaHelper) {
		super(inData, model, model, sourcePackage, targetPackage, sourceClass, targetClass,
				eaHelper);
	}

	@Override
	protected Integer getConnectorID(Map<String, String> inData) {
		return Util.parseInt(inData.get(EA.CONN_ID));
	}

	@Override
	protected String getConnectorGUID(Map<String, String> inData) {
		return inData.get(EA.EA_GUID);
	}

	@Override
	protected String getConnectorName(Map<String, String> inData) {
		return inData.get(EA.CONN_NAME);
	}

	@Override
	protected String getConnectorAlias(Map<String, String> inData) {
		return inData.get(EA.CONN_ALIAS);
	}

	@Override
	protected String getConnectorStereotypes(Map<String, String> inData) {
		return inData.get(EA.DEDUCED_STEREOS);
	}

	@Override
	protected String getConnectorNotes(Map<String, String> inData) {
		return inData.get(EA.CONN_NOTE);
	}

	@Override
	protected List<Map<String, String>> fetchTaggedValues(EaModelBuilder<?, ?> tagsSrc) {
		return tagsSrc.getTables().findConnectorTags(getObjData().getId());
	}
}
