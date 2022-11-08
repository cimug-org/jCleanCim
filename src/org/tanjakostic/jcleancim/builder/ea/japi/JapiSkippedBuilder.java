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

import java.util.List;

import org.sparx.Connector;
import org.sparx.Diagram;
import org.sparx.Element;
import org.tanjakostic.jcleancim.builder.ea.ClassBuilder;
import org.tanjakostic.jcleancim.builder.ea.DiagramBuilder;
import org.tanjakostic.jcleancim.builder.ea.EaHelper;
import org.tanjakostic.jcleancim.builder.ea.EaModelBuilder;
import org.tanjakostic.jcleancim.builder.ea.PackageBuilder;
import org.tanjakostic.jcleancim.builder.ea.SkippedBuilder;

/**
 * @author tatjana.kostic@ieee.org
 * @version $Id: JapiSkippedBuilder.java 21 2019-08-12 15:44:50Z dev978 $
 */
class JapiSkippedBuilder extends SkippedBuilder<Element, Element, Connector, Diagram> {

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
	public JapiSkippedBuilder(Element inDataE, Connector inDataC,
			PackageBuilder<?, ?, ?, ?, ?, ?> p, ClassBuilder<?, ?, ?, ?, ?, ?> c,
			EaModelBuilder<?, ?> model, EaHelper eaHelper) {
		super(inDataE, inDataE, inDataC, p, c, model, eaHelper);
	}

	@Override
	protected Integer getElementID(Element inDataE) {
		return Integer.valueOf(inDataE.GetElementID());
	}

	@Override
	protected String getElementGUID(Element inDataE) {
		return inDataE.GetElementGUID();
	}

	@Override
	protected String getElementName(Element inDataE) {
		return inDataE.GetName();
	}

	@Override
	protected String getElementAlias(Element inDataE) {
		return inDataE.GetAlias();
	}

	@Override
	protected String getElementStereotypes(Element inDataE) {
		return inDataE.GetStereotypeEx();
	}

	@Override
	protected String getElementNotes(Element inDataE) {
		return inDataE.GetNotes();
	}

	@Override
	protected String getElementType(Element inDataE) {
		return inDataE.GetType();
	}

	@Override
	protected List<Diagram> collectDiagrams(Element itemsSrc) {
		return JapiRepo.eaToJavaList(itemsSrc.GetDiagrams());
	}

	@Override
	protected DiagramBuilder<?> createDiagram(Diagram dia, EaHelper eaHelper) {
		return new JapiDiagramBuilder(dia, getContainingPackage(), getContainingClass(), eaHelper);
	}

	@Override
	protected Integer getConnectorID(Connector inDataC) {
		return Integer.valueOf(inDataC.GetConnectorID());
	}

	@Override
	protected String getConnectorGUID(Connector inDataC) {
		return inDataC.GetConnectorGUID();
	}

	@Override
	protected String getConnectorName(Connector inDataC) {
		return inDataC.GetName();
	}

	@Override
	protected String getConnectorAlias(Connector inDataC) {
		return inDataC.GetAlias();
	}

	@Override
	protected String getConnectorStereotypes(Connector inDataC) {
		return inDataC.GetStereotypeEx();
	}

	@Override
	protected String getConnectorNotes(Connector inDataC) {
		return inDataC.GetNotes();
	}

	@Override
	protected String getConnectorType(Connector inDataC) {
		return inDataC.GetType();
	}

	@Override
	protected Integer getConnectorClientID(Connector inDataC) {
		return Integer.valueOf(inDataC.GetClientID());
	}

	@Override
	protected Integer getConnectorSupplierID(Connector inDataC) {
		return Integer.valueOf(inDataC.GetSupplierID());
	}
}
