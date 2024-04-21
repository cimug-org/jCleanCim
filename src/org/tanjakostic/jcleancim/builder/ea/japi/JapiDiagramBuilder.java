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

import org.sparx.Diagram;
import org.tanjakostic.jcleancim.builder.ea.ClassBuilder;
import org.tanjakostic.jcleancim.builder.ea.DiagramBuilder;
import org.tanjakostic.jcleancim.builder.ea.EaHelper;
import org.tanjakostic.jcleancim.builder.ea.PackageBuilder;

/**
 * @author tatjana.kostic@ieee.org
 * @version $Id: JapiDiagramBuilder.java 21 2019-08-12 15:44:50Z dev978 $
 */
class JapiDiagramBuilder extends DiagramBuilder<Diagram> {

	/**
	 * Constructor.
	 *
	 * @param inData
	 * @param containingPackage
	 * @param containingClass
	 * @param eaHelper
	 */
	public JapiDiagramBuilder(Diagram inData, PackageBuilder<?, ?, ?, ?, ?, ?> containingPackage,
			ClassBuilder<?, ?, ?, ?, ?, ?> containingClass, EaHelper eaHelper) {
		super(inData, containingPackage, containingClass, eaHelper);
	}

	@Override
	protected Integer getDiagramID(Diagram inData) {
		return Integer.valueOf(inData.GetDiagramID());
	}

	@Override
	protected String getDiagramGUID(Diagram inData) {
		return inData.GetDiagramGUID();
	}

	@Override
	protected String getDiagramName(Diagram inData) {
		return inData.GetName();
	}

	@Override
	protected String getDiagramStereotypes(Diagram inData) {
		return inData.GetStereotype();
	}

	@Override
	protected String getDiagramNotes(Diagram inData) {
		return inData.GetNotes();
	}

	@Override
	protected String getDiagramOrientation(Diagram inData) {
		return inData.GetOrientation();
	}

	@Override
	protected String getDiagramType(Diagram inData) {
		return inData.GetType();
	}
}
