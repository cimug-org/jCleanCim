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

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.sparx.Connector;
import org.sparx.Diagram;
import org.sparx.Element;
import org.sparx.Package;
import org.tanjakostic.jcleancim.builder.ea.ClassBuilder;
import org.tanjakostic.jcleancim.builder.ea.DependencyBuilder;
import org.tanjakostic.jcleancim.builder.ea.DiagramBuilder;
import org.tanjakostic.jcleancim.builder.ea.EA;
import org.tanjakostic.jcleancim.builder.ea.EaHelper;
import org.tanjakostic.jcleancim.builder.ea.EaModelBuilder;
import org.tanjakostic.jcleancim.builder.ea.PackageBuilder;
import org.tanjakostic.jcleancim.builder.ea.SkippedBuilder;

/**
 * @author tatjana.kostic@ieee.org
 * @version $Id: JapiPackageBuilder.java 21 2019-08-12 15:44:50Z dev978 $
 */
class JapiPackageBuilder extends
PackageBuilder<Package, Element, Package, Element, Diagram, Connector> {

	/**
	 * Creates model package from EA object; loads all the model contents recursively.
	 *
	 * @param eaPackage
	 *            EA package that is wrapped by this UML package.
	 * @param model
	 *            parent UML model (EA repository wrapper, needed for extracting diagrams for
	 *            printing to clipboard and for formatted UML docs of elements and connectors)
	 * @param eaHelper
	 *            we need this to save diagrams and formatted text.
	 * @throws NullPointerException
	 *             if any argument is null.
	 */
	public static PackageBuilder<?, ?, ?, ?, ?, ?> createModelPackageBuilder(Package eaPackage,
			EaModelBuilder<?, ?> model, EaHelper eaHelper) {
		return new JapiPackageBuilder(eaPackage, eaPackage.GetElement(), model, null,
				eaPackage.GetPackageID(), eaHelper);
	}

	/**
	 * Constructor.
	 *
	 * @param inData
	 * @param inDataE
	 * @param model
	 * @param containingPackage
	 * @param modelId
	 * @param eaHelper
	 */
	public JapiPackageBuilder(Package inData, Element inDataE, EaModelBuilder<?, ?> model,
			PackageBuilder<?, ?, ?, ?, ?, ?> containingPackage, int modelId, EaHelper eaHelper) {
		super(inData, inDataE, inData, inDataE, model, containingPackage, modelId, eaHelper);
	}

	@Override
	protected Integer getPackageID(Package inData) {
		return Integer.valueOf(inData.GetPackageID());
	}

	@Override
	protected String getPackageGUID(Package inData) {
		return inData.GetPackageGUID();
	}

	@Override
	protected String getPackageName(Package inData) {
		return inData.GetName();
	}

	@Override
	protected String getPackageNotes(Package inData) {
		return inData.GetNotes();
	}

	@Override
	protected String getPackageAlias(Package inData) {
		return inData.GetAlias();
	}

	@Override
	protected String getPackageStereotypes(Package inData) {
		return inData.GetStereotypeEx();
	}

	@Override
	protected String getPackageVisibility(Element inDataE) {
		return inDataE.GetVisibility();
	}

	@Override
	protected int getPackagePos(Package inData) {
		return inData.GetTreePos();
	}

	@Override
	protected Integer getPackageParentID(Package inData) {
		return Integer.valueOf(inData.GetParentID());
	}

	@Override
	protected Integer getPackageElementID(Element inDataE) {
		return Integer.valueOf(inDataE.GetElementID());
	}

	@Override
	protected List<Map<String, String>> collectTaggedValues(Element itemsSrc) {
		return JapiClassBuilder.eaElementTaggedValuesToFields(itemsSrc);
	}

	@Override
	protected List<Diagram> collectDiagrams(Package itemsSrc) {
		return JapiRepo.eaToJavaList(itemsSrc.GetDiagrams());
	}

	@Override
	protected DiagramBuilder<?> createDiagram(Diagram item, EaHelper eaHelper) {
		return new JapiDiagramBuilder(item, this, null, eaHelper);
	}

	@Override
	protected List<Connector> collectConnectors(Element itemsSrcE) {
		return JapiRepo.eaToJavaList(itemsSrcE.GetConnectors());
	}

	@Override
	protected String fetchConnectorType(Connector item) {
		return item.GetType();
	}

	@Override
	protected Map<String, String> eaConnectorIDsToFields(Connector item) {
		Integer clientId = Integer.valueOf(item.GetClientID());
		Integer supplierId = Integer.valueOf(item.GetSupplierID());
		Integer connId = Integer.valueOf(item.GetConnectorID());

		Map<String, String> m = new LinkedHashMap<String, String>();
		m.put(EA.CONN_FROM_ID, clientId.toString());
		m.put(EA.CONN_TO_ID, supplierId.toString());
		m.put(EA.CONN_ID, connId.toString());
		return m;
	}

	@Override
	protected SkippedBuilder<?, ?, ?, ?> createSkippedConnector(Connector item,
			EaModelBuilder<?, ?> model, EaHelper eaHelper) {
		return new JapiSkippedBuilder(null, item, this, null, model, eaHelper);
	}

	@Override
	protected DependencyBuilder<?, ?> createDependency(Connector item, EaModelBuilder<?, ?> model,
			PackageBuilder<?, ?, ?, ?, ?, ?> source, PackageBuilder<?, ?, ?, ?, ?, ?> target,
			EaHelper eaHelper) {
		return new JapiDependencyBuilder(item, model, source, target, null, null, eaHelper);
	}

	@Override
	protected List<Element> collectPackageElements(Package itemsSrcP) {
		return JapiRepo.eaToJavaList(itemsSrcP.GetElements());
	}

	@Override
	protected String fetchElementType(Element item) {
		return item.GetType();
	}

	@Override
	protected SkippedBuilder<?, ?, ?, ?> createSkippedElement(Element item,
			EaModelBuilder<?, ?> model, EaHelper eaHelper) {
		return new JapiSkippedBuilder(item, null, this, null, model, eaHelper);
	}

	@Override
	protected ClassBuilder<?, ?, ?, ?, ?, ?> createClass(Element item, EaHelper eaHelper) {
		return new JapiClassBuilder(item, this, eaHelper);
	}

	@Override
	protected List<Package> collectSubPackages(Package itemsSrcP) {
		return JapiRepo.eaToJavaList(itemsSrcP.GetPackages());
	}

	@Override
	protected PackageBuilder<?, ?, ?, ?, ?, ?> createSubPackage(Package item, EaHelper eaHelper) {
		return new JapiPackageBuilder(item, item.GetElement(), getModel(), this, getModelId(),
				eaHelper);
	}
}
