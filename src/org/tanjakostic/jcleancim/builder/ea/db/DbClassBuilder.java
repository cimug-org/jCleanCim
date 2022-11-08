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
import org.tanjakostic.jcleancim.builder.ea.AttributeBuilder;
import org.tanjakostic.jcleancim.builder.ea.ClassBuilder;
import org.tanjakostic.jcleancim.builder.ea.DependencyBuilder;
import org.tanjakostic.jcleancim.builder.ea.DiagramBuilder;
import org.tanjakostic.jcleancim.builder.ea.EA;
import org.tanjakostic.jcleancim.builder.ea.EaHelper;
import org.tanjakostic.jcleancim.builder.ea.EaModelBuilder;
import org.tanjakostic.jcleancim.builder.ea.OperationBuilder;
import org.tanjakostic.jcleancim.builder.ea.PackageBuilder;
import org.tanjakostic.jcleancim.builder.ea.SkippedBuilder;
import org.tanjakostic.jcleancim.util.Util;

/**
 * @author tatjana.kostic@ieee.org
 * @version $Id: DbClassBuilder.java 21 2019-08-12 15:44:50Z dev978 $
 */
class DbClassBuilder
extends
ClassBuilder<Map<String, String>, EaModelBuilder<?, ?>, Map<String, String>, Map<String, String>, Map<String, String>, Map<String, String>> {

	/**
	 * Constructor.
	 *
	 * @param inData
	 * @param containingPackage
	 * @param eaHelper
	 */
	public DbClassBuilder(Map<String, String> inData,
			PackageBuilder<?, ?, ?, ?, ?, ?> containingPackage, EaHelper eaHelper) {
		super(inData, containingPackage.getModel(), containingPackage, eaHelper);
	}

	@Override
	protected Integer getElementID(Map<String, String> inData) {
		return Util.parseInt(inData.get(EA.ELEM_ID));
	}

	@Override
	protected String getElementGUID(Map<String, String> inData) {
		return inData.get(EA.EA_GUID);
	}

	@Override
	protected String getElementName(Map<String, String> inData) {
		return inData.get(EA.ELEM_NAME);
	}

	@Override
	protected String getElementAlias(Map<String, String> inData) {
		return inData.get(EA.ELEM_ALIAS);
	}

	@Override
	protected String getElementStereotypes(Map<String, String> inData) {
		return inData.get(EA.DEDUCED_STEREOS);
	}

	@Override
	protected String getElementVisibility(Map<String, String> inData) {
		return inData.get(EA.ELEM_SCOPE);
	}

	@Override
	protected String getElementNotes(Map<String, String> inData) {
		return inData.get(EA.ELEM_NOTE);
	}

	@Override
	protected int getElementPosition(Map<String, String> inData) {
		return Util.parseIntZero(inData.get(EA.ELEM_POS)).intValue();
	}

	@Override
	protected String getElementType(Map<String, String> inData) {
		return inData.get(EA.ELEM_TYPE);
	}

	@Override
	protected String getElementAbstract(Map<String, String> inData) {
		return inData.get(EA.ELEM_ABSTRACT);
	}

	@Override
	protected boolean getElementIsLeaf(Map<String, String> inData) {
		return Boolean.parseBoolean(inData.get(EA.ELEM_LEAF).toLowerCase());
	}

	@Override
	protected boolean getElementIsRoot(Map<String, String> inData) {
		return Boolean.parseBoolean(inData.get(EA.ELEM_ROOT).toLowerCase());
	}

	@Override
	protected String getElementPersistence(Map<String, String> inData) {
		String persistenceVal = inData.get(EA.ELEM_PERSIST);
		return (persistenceVal == null) ? "" : persistenceVal;
	}

	@Override
	protected int getElementSubtypeVal(Map<String, String> inData) {
		return Integer.parseInt(inData.get(EA.ELEM_SUBTYPE));
	}

	@Override
	protected List<Map<String, String>> collectClassConstraints(EaModelBuilder<?, ?> itemsSrc) {
		return itemsSrc.getTables().findObjectConstraints(getObjData().getId());
	}

	@Override
	protected List<Map<String, String>> collectTaggedValues(EaModelBuilder<?, ?> itemsSrc) {
		return itemsSrc.getTables().findObjectTaggedValues(getObjData().getId());
	}

	@Override
	protected List<Map<String, String>> collectDiagrams(EaModelBuilder<?, ?> itemsSrc) {
		return itemsSrc.getTables()
				.findObjectDiagrams(getObjData().getId(), getObjData().getName());
	}

	@Override
	protected DiagramBuilder<?> createDiagram(Map<String, String> item, EaHelper eaHelper) {
		return new DbDiagramBuilder(item, null, this, eaHelper);
	}

	@Override
	protected List<Map<String, String>> collectContainedElements(EaModelBuilder<?, ?> itemsSrc) {
		return itemsSrc.getTables().findClassEmbeddedElements(getObjData().getId());
	}

	@Override
	protected ClassBuilder<?, ?, ?, ?, ?, ?> createEmbeddedClass(Map<String, String> item,
			EaHelper eaHelper) {
		return new DbClassBuilder(item, getContainingPackage(), eaHelper);
	}

	@Override
	protected SkippedBuilder<?, ?, ?, ?> createSkippedElement(Map<String, String> item,
			EaModelBuilder<?, ?> model, EaHelper eaHelper) {
		return new DbSkippedBuilder(item, null, null, this, model, eaHelper);
	}

	@Override
	protected List<Map<String, String>> collectAttributes(EaModelBuilder<?, ?> itemsSrc) {
		return itemsSrc.getTables().findAttributes(getObjData().getId());
	}

	@Override
	protected AttributeBuilder<?, ?> createAttribute(Map<String, String> item, EaHelper eaHelper) {
		return new DbAttributeBuilder(item, this, eaHelper);
	}

	@Override
	protected List<Map<String, String>> collectOperations(EaModelBuilder<?, ?> itemsSrc) {
		return itemsSrc.getTables().findOperations(getObjData().getId());
	}

	@Override
	protected OperationBuilder<?, ?> createOperation(Map<String, String> item, EaHelper eaHelper) {
		return new DbOperationBuilder(item, this, eaHelper);
	}

	@Override
	protected List<Map<String, String>> collectConnectors(EaModelBuilder<?, ?> itemsSrc) {
		return itemsSrc.getTables().findConnectors(getObjData().getId());
	}

	@Override
	protected String fetchConnectorType(Map<String, String> item) {
		return item.get(EA.CONN_TYPE);
	}

	@Override
	protected Map<String, String> eaConnectorIDsToFields(Map<String, String> conn) {
		return conn;
	}

	@Override
	protected AssociationBuilder<?, ?> createAssociation(Map<String, String> item,
			ClassBuilder<?, ?, ?, ?, ?, ?> source, ClassBuilder<?, ?, ?, ?, ?, ?> target,
			EaModelBuilder<?, ?> model, EaHelper eaHelper) {
		return new DbAssociationBuilder(item, source, target, eaHelper, model);
	}

	@Override
	protected DependencyBuilder<?, ?> createDependency(Map<String, String> item,
			EaModelBuilder<?, ?> model, ClassBuilder<?, ?, ?, ?, ?, ?> source,
			ClassBuilder<?, ?, ?, ?, ?, ?> target, EaHelper eaHelper) {
		return new DbDependencyBuilder(item, model, null, null, source, target, eaHelper);
	}

	@Override
	protected SkippedBuilder<?, ?, ?, ?> createSkippedConnector(Map<String, String> item,
			EaModelBuilder<?, ?> model, EaHelper eaHelper) {
		return new DbSkippedBuilder(null, item, null, this, model, eaHelper);
	}
}
