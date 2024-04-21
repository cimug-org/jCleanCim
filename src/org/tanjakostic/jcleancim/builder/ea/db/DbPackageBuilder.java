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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.tanjakostic.jcleancim.builder.ea.ClassBuilder;
import org.tanjakostic.jcleancim.builder.ea.DependencyBuilder;
import org.tanjakostic.jcleancim.builder.ea.DiagramBuilder;
import org.tanjakostic.jcleancim.builder.ea.EA;
import org.tanjakostic.jcleancim.builder.ea.EaHelper;
import org.tanjakostic.jcleancim.builder.ea.EaModelBuilder;
import org.tanjakostic.jcleancim.builder.ea.PackageBuilder;
import org.tanjakostic.jcleancim.builder.ea.SkippedBuilder;
import org.tanjakostic.jcleancim.util.Util;

/**
 * @author tatjana.kostic@ieee.org
 * @version $Id: DbPackageBuilder.java 21 2019-08-12 15:44:50Z dev978 $
 */
public class DbPackageBuilder
extends
PackageBuilder<Map<String, String>, Map<String, String>, EaModelBuilder<?, ?>, EaModelBuilder<?, ?>, Map<String, String>, Map<String, String>> {

	/**
	 * Creates model package from EA object; loads all the model contents recursively.
	 *
	 * @param item
	 *            EA package that is wrapped by this UML package.
	 * @param model
	 *            parent UML model (EA repository wrapper, needed for extracting diagrams for
	 *            printing to clipboard and for formatted UML docs of elements and connectors)
	 * @param eaHelper
	 *            we need this to save diagrams and formatted text.
	 * @throws NullPointerException
	 *             if any argument is null.
	 */
	public static PackageBuilder<?, ?, ?, ?, ?, ?> createModelPackageBuilder(
			Map<String, String> item, EaModelBuilder<?, ?> model, EaHelper eaHelper) {
		String pckIdStr = item.get(EA.PACKAGE_ID);
		int packageId = Util.parseIntZero(pckIdStr).intValue();
		return new DbPackageBuilder(item, model, null, packageId, eaHelper);
	}

	/**
	 * Constructor.
	 *
	 * @param inData
	 * @param model
	 * @param containingPackage
	 * @param modelId
	 * @param eaHelper
	 */
	public DbPackageBuilder(Map<String, String> inData, EaModelBuilder<?, ?> model,
			PackageBuilder<?, ?, ?, ?, ?, ?> containingPackage, int modelId, EaHelper eaHelper) {
		super(inData, inData, model, model, model, containingPackage, modelId, eaHelper);
	}

	@Override
	protected Integer getPackageID(Map<String, String> inData) {
		return Util.parseInt(inData.get(EA.PACKAGE_ID));
	}

	@Override
	protected String getPackageGUID(Map<String, String> inData) {
		return inData.get(EA.EA_GUID);
	}

	@Override
	protected String getPackageName(Map<String, String> inData) {
		return inData.get(EA.PACKAGE_NAME);
	}

	@Override
	protected String getPackageAlias(Map<String, String> inData) {
		return inData.get(EA.ELEM_ALIAS);
	}

	@Override
	protected String getPackageStereotypes(Map<String, String> inData) {
		return inData.get(EA.DEDUCED_STEREOS);
	}

	@Override
	protected String getPackageVisibility(Map<String, String> inDataE) {
		return inDataE.get(EA.ELEM_SCOPE);
	}

	@Override
	protected String getPackageNotes(Map<String, String> inData) {
		return inData.get(EA.PACKAGE_NOTE);
	}

	@Override
	protected int getPackagePos(Map<String, String> inData) {
		return Util.parseIntZero(inData.get(EA.PACKAGE_POS)).intValue();
	}

	@Override
	protected Integer getPackageParentID(Map<String, String> inData) {
		return Util.parseInt(inData.get(EA.PACKAGE_OWNER_ID));
	}

	@Override
	protected Integer getPackageElementID(Map<String, String> inDataE) {
		return Util.parseInt(inDataE.get(EA.ELEM_ID));
	}

	@Override
	protected List<Map<String, String>> collectTaggedValues(EaModelBuilder<?, ?> itemsSrc) {
		return itemsSrc.getTables().findObjectTaggedValues(getEaElementID());
	}

	@Override
	protected List<Map<String, String>> collectDiagrams(EaModelBuilder<?, ?> itemsSrc) {
		return itemsSrc.getTables().findPackageDiagrams(getObjData().getId(),
				getObjData().getName());
	}

	@Override
	protected DiagramBuilder<?> createDiagram(Map<String, String> item, EaHelper eaHelper) {
		return new DbDiagramBuilder(item, this, null, eaHelper);
	}

	@Override
	protected List<Map<String, String>> collectConnectors(EaModelBuilder<?, ?> itemsSrcE) {
		return itemsSrcE.getTables().findConnectors(getEaElementID());
	}

	@Override
	protected String fetchConnectorType(Map<String, String> item) {
		return item.get(EA.CONN_TYPE);
	}

	@Override
	protected Map<String, String> eaConnectorIDsToFields(Map<String, String> item) {
		return item;
	}

	@Override
	protected SkippedBuilder<?, ?, ?, ?> createSkippedConnector(Map<String, String> item,
			EaModelBuilder<?, ?> model, EaHelper eaHelper) {
		return new DbSkippedBuilder(null, item, this, null, model, eaHelper);
	}

	@Override
	protected DependencyBuilder<?, ?> createDependency(Map<String, String> item,
			EaModelBuilder<?, ?> model, PackageBuilder<?, ?, ?, ?, ?, ?> source,
			PackageBuilder<?, ?, ?, ?, ?, ?> target, EaHelper eaHelper) {
		return new DbDependencyBuilder(item, model, source, target, null, null, eaHelper);
	}

	@Override
	protected List<Map<String, String>> collectPackageElements(EaModelBuilder<?, ?> itemsSrcP) {
		List<Map<String, String>> items = new ArrayList<Map<String, String>>();
		List<Map<String, String>> skippeds = itemsSrcP.getTables().findPackageEmbeddedElements(
				getObjData().getId());
		// this was original implementation, with split processing for skipped elements and
		// classes; we keep it split, because we need classes in a correct order, while for
		// skipped we don't care:
		List<Map<String, String>> clFields = itemsSrcP.getTables().findPackageClasses(
				getObjData().getId(), getObjData().getName());
		items.addAll(skippeds);
		items.addAll(clFields);
		return items;
	}

	@Override
	protected String fetchElementType(Map<String, String> item) {
		return item.get(EA.ELEM_TYPE);
	}

	@Override
	protected SkippedBuilder<?, ?, ?, ?> createSkippedElement(Map<String, String> item,
			EaModelBuilder<?, ?> model, EaHelper eaHelper) {
		return new DbSkippedBuilder(item, null, this, null, model, eaHelper);
	}

	@Override
	protected ClassBuilder<?, ?, ?, ?, ?, ?> createClass(Map<String, String> item, EaHelper eaHelper) {
		return new DbClassBuilder(item, this, eaHelper);
	}

	@Override
	protected List<Map<String, String>> collectSubPackages(EaModelBuilder<?, ?> itemsSrcP) {
		return itemsSrcP.getTables().findPackageSubpackages(getObjData().getId(),
				getObjData().getName());
	}

	@Override
	protected PackageBuilder<?, ?, ?, ?, ?, ?> createSubPackage(Map<String, String> item,
			EaHelper eaHelper) {
		return new DbPackageBuilder(item, getModel(), this, getModelId(), eaHelper);
	}
}
