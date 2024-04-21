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
import org.tanjakostic.jcleancim.builder.ea.DiagramBuilder;
import org.tanjakostic.jcleancim.builder.ea.EA;
import org.tanjakostic.jcleancim.builder.ea.EaHelper;
import org.tanjakostic.jcleancim.builder.ea.EaModelBuilder;
import org.tanjakostic.jcleancim.builder.ea.PackageBuilder;
import org.tanjakostic.jcleancim.builder.ea.SkippedBuilder;
import org.tanjakostic.jcleancim.util.Util;

/**
 * @author tatjana.kostic@ieee.org
 * @version $Id: DbSkippedBuilder.java 21 2019-08-12 15:44:50Z dev978 $
 */
class DbSkippedBuilder
extends
SkippedBuilder<Map<String, String>, EaModelBuilder<?, ?>, Map<String, String>, Map<String, String>> {

	/**
	 * Constructor.
	 *
	 * @param inDataE
	 * @param inDataC
	 * @param p
	 * @param c
	 * @param model
	 * @param eaHelper
	 */
	public DbSkippedBuilder(Map<String, String> inDataE, Map<String, String> inDataC,
			PackageBuilder<?, ?, ?, ?, ?, ?> p, ClassBuilder<?, ?, ?, ?, ?, ?> c,
			EaModelBuilder<?, ?> model, EaHelper eaHelper) {
		super(inDataE, model, inDataC, p, c, model, eaHelper);
	}

	@Override
	protected Integer getElementID(Map<String, String> inDataE) {
		return Util.parseInt(inDataE.get(EA.ELEM_ID));
	}

	@Override
	protected String getElementGUID(Map<String, String> inDataE) {
		return inDataE.get(EA.EA_GUID);
	}

	@Override
	protected String getElementName(Map<String, String> inDataE) {
		return inDataE.get(EA.ELEM_NAME);
	}

	@Override
	protected String getElementAlias(Map<String, String> inDataE) {
		return inDataE.get(EA.ELEM_ALIAS);
	}

	@Override
	protected String getElementStereotypes(Map<String, String> inDataE) {
		return inDataE.get(EA.DEDUCED_STEREOS);
	}

	@Override
	protected String getElementNotes(Map<String, String> inDataE) {
		return inDataE.get(EA.ELEM_NOTE);
	}

	@Override
	protected String getElementType(Map<String, String> inDataE) {
		return inDataE.get(EA.ELEM_TYPE);
	}

	@Override
	protected List<Map<String, String>> collectDiagrams(EaModelBuilder<?, ?> itemsSrc) {
		return itemsSrc.getTables()
				.findObjectDiagrams(getObjData().getId(), getObjData().getName());
	}

	@Override
	protected DiagramBuilder<?> createDiagram(Map<String, String> inData, EaHelper eaHelper) {
		return new DbDiagramBuilder(inData, getContainingPackage(), getContainingClass(), eaHelper);
	}

	@Override
	protected Integer getConnectorID(Map<String, String> inDataC) {
		return Util.parseInt(inDataC.get(EA.CONN_ID));
	}

	@Override
	protected String getConnectorGUID(Map<String, String> inDataC) {
		return inDataC.get(EA.EA_GUID);
	}

	@Override
	protected String getConnectorName(Map<String, String> inDataC) {
		return inDataC.get(EA.CONN_NAME);
	}

	@Override
	protected String getConnectorAlias(Map<String, String> inDataC) {
		return inDataC.get(EA.CONN_ALIAS);
	}

	@Override
	protected String getConnectorStereotypes(Map<String, String> inDataC) {
		return inDataC.get(EA.DEDUCED_STEREOS);
	}

	@Override
	protected String getConnectorNotes(Map<String, String> inDataC) {
		return inDataC.get(EA.CONN_NOTE);
	}

	@Override
	protected String getConnectorType(Map<String, String> inDataC) {
		return inDataC.get(EA.CONN_TYPE);
	}

	@Override
	protected Integer getConnectorClientID(Map<String, String> inDataC) {
		return Integer.valueOf(inDataC.get(EA.CONN_FROM_ID));
	}

	@Override
	protected Integer getConnectorSupplierID(Map<String, String> inDataC) {
		return Integer.valueOf(inDataC.get(EA.CONN_TO_ID));
	}
}
