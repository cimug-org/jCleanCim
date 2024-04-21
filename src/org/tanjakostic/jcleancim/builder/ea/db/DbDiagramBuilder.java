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

import java.util.Map;

import org.tanjakostic.jcleancim.builder.ea.ClassBuilder;
import org.tanjakostic.jcleancim.builder.ea.DiagramBuilder;
import org.tanjakostic.jcleancim.builder.ea.EA;
import org.tanjakostic.jcleancim.builder.ea.EaHelper;
import org.tanjakostic.jcleancim.builder.ea.PackageBuilder;
import org.tanjakostic.jcleancim.util.Util;

/**
 * @author tatjana.kostic@ieee.org
 * @version $Id: DbDiagramBuilder.java 21 2019-08-12 15:44:50Z dev978 $
 */
class DbDiagramBuilder extends DiagramBuilder<Map<String, String>> {

	/**
	 * Constructor.
	 *
	 * @param inData
	 * @param containingPackage
	 * @param containingClass
	 * @param eaHelper
	 */
	public DbDiagramBuilder(Map<String, String> inData,
			PackageBuilder<?, ?, ?, ?, ?, ?> containingPackage,
			ClassBuilder<?, ?, ?, ?, ?, ?> containingClass, EaHelper eaHelper) {
		super(inData, containingPackage, containingClass, eaHelper);
	}

	@Override
	protected Integer getDiagramID(Map<String, String> inData) {
		return Util.parseInt(inData.get(EA.DIA_ID));
	}

	@Override
	protected String getDiagramGUID(Map<String, String> inData) {
		return inData.get(EA.EA_GUID);
	}

	@Override
	protected String getDiagramName(Map<String, String> inData) {
		return inData.get(EA.DIA_NAME);
	}

	@Override
	protected String getDiagramStereotypes(Map<String, String> inData) {
		return inData.get(EA.DIA_STEREO);
	}

	@Override
	protected String getDiagramNotes(Map<String, String> inData) {
		return inData.get(EA.DIA_NOTE);
	}

	@Override
	protected String getDiagramOrientation(Map<String, String> inData) {
		return inData.get(EA.DIA_ORIENT);
	}

	@Override
	protected String getDiagramType(Map<String, String> inData) {
		return inData.get(EA.DIA_TYPE);
	}
}
