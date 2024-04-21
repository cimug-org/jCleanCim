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

import org.sparx.Attribute;
import org.sparx.Connector;
import org.sparx.Constraint;
import org.sparx.Diagram;
import org.sparx.Element;
import org.sparx.Method;
import org.sparx.TaggedValue;
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

/**
 * @author tatjana.kostic@ieee.org
 * @version $Id: JapiClassBuilder.java 21 2019-08-12 15:44:50Z dev978 $
 */
class JapiClassBuilder extends
ClassBuilder<Element, Element, Diagram, Attribute, Method, Connector> {

	/**
	 * Constructor.
	 *
	 * @param inData
	 * @param containingPackage
	 * @param eaHelper
	 */
	public JapiClassBuilder(Element inData, PackageBuilder<?, ?, ?, ?, ?, ?> containingPackage,
			EaHelper eaHelper) {
		super(inData, inData, containingPackage, eaHelper);
	}

	@Override
	protected Integer getElementID(Element inData) {
		return Integer.valueOf(inData.GetElementID());
	}

	@Override
	protected String getElementGUID(Element inData) {
		return inData.GetElementGUID();
	}

	@Override
	protected String getElementName(Element inData) {
		return inData.GetName();
	}

	@Override
	protected String getElementAlias(Element inData) {
		return inData.GetAlias();
	}

	@Override
	protected String getElementStereotypes(Element inData) {
		return inData.GetStereotypeEx();
	}

	@Override
	protected String getElementVisibility(Element inData) {
		return inData.GetVisibility();
	}

	@Override
	protected String getElementNotes(Element inData) {
		return inData.GetNotes();
	}

	@Override
	protected int getElementPosition(Element inData) {
		return inData.GetTreePos();
	}

	@Override
	protected String getElementType(Element inData) {
		return inData.GetType();
	}

	@Override
	protected String getElementAbstract(Element inData) {
		return inData.GetAbstract();
	}

	@Override
	protected boolean getElementIsLeaf(Element inData) {
		return inData.GetIsLeaf();
	}

	@Override
	protected boolean getElementIsRoot(Element inData) {
		return false; // not available through API (they forgot the method...)
	}

	@Override
	protected String getElementPersistence(Element inData) {
		return inData.GetPersistence();
	}

	@Override
	protected int getElementSubtypeVal(Element inData) {
		return inData.GetSubtype();
	}

	// -----------

	@Override
	protected List<Map<String, String>> collectClassConstraints(Element itemsSrc) {
		List<Constraint> eas = JapiRepo.eaToJavaList(itemsSrc.GetConstraints());
		List<Map<String, String>> result = new ArrayList<Map<String, String>>();
		for (Constraint item : eas) {
			Map<String, String> m = eaClassConstraintToFields(item);
			result.add(m);
		}
		return result;
	}

	public static Map<String, String> eaClassConstraintToFields(Constraint item) {
		Map<String, String> m = new LinkedHashMap<String, String>();
		m.put(EA.CLASS_CONSTR_NAME, item.GetName());
		m.put(EA.CLASS_CONSTR_NOTE, item.GetNotes());
		return m;
	}

	@Override
	protected List<Map<String, String>> collectTaggedValues(Element itemsSrc) {
		return eaElementTaggedValuesToFields(itemsSrc);
	}

	public static List<Map<String, String>> eaElementTaggedValuesToFields(Element itemsSrc) {
		List<TaggedValue> eas = JapiRepo.eaToJavaList(itemsSrc.GetTaggedValues());
		List<Map<String, String>> result = new ArrayList<Map<String, String>>();
		for (TaggedValue ea : eas) {
			Map<String, String> m = eaElementTaggedValueToFields(ea);
			result.add(m);
		}
		return result;
	}

	public static Map<String, String> eaElementTaggedValueToFields(TaggedValue item) {
		Map<String, String> fields = new LinkedHashMap<String, String>();
		fields.put(EA.ELEM_TGVAL_NAME, item.GetName());
		fields.put(EA.ELEM_TGVAL_VALUE, item.GetValue());
		return fields;
	}

	@Override
	protected List<Diagram> collectDiagrams(Element itemsSrc) {
		return JapiRepo.eaToJavaList(itemsSrc.GetDiagrams());
	}

	@Override
	protected DiagramBuilder<?> createDiagram(Diagram item, EaHelper eaHelper) {
		return new JapiDiagramBuilder(item, null, this, eaHelper);
	}

	@Override
	protected List<Element> collectContainedElements(Element itemsSrc) {
		return JapiRepo.eaToJavaList(itemsSrc.GetElements());
	}

	@Override
	protected ClassBuilder<?, ?, ?, ?, ?, ?> createEmbeddedClass(Element item, EaHelper eaHelper) {
		return new JapiClassBuilder(item, getContainingPackage(), eaHelper);
	}

	@Override
	protected SkippedBuilder<?, ?, ?, ?> createSkippedElement(Element item,
			EaModelBuilder<?, ?> model, EaHelper eaHelper) {
		return new JapiSkippedBuilder(item, null, null, this, model, eaHelper);
	}

	@Override
	protected List<Attribute> collectAttributes(Element itemsSrc) {
		return JapiRepo.eaToJavaList(itemsSrc.GetAttributes());
	}

	@Override
	protected AttributeBuilder<?, ?> createAttribute(Attribute item, EaHelper eaHelper) {
		return new JapiAttributeBuilder(item, this, eaHelper);
	}

	@Override
	protected List<Method> collectOperations(Element itemsSrc) {
		return JapiRepo.eaToJavaList(itemsSrc.GetMethods());
	}

	@Override
	protected OperationBuilder<?, ?> createOperation(Method item, EaHelper eaHelper) {
		return new JapiOperationBuilder(item, this, eaHelper);
	}

	@Override
	protected List<Connector> collectConnectors(Element itemsSrc) {
		return JapiRepo.eaToJavaList(itemsSrc.GetConnectors());
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
	protected AssociationBuilder<?, ?> createAssociation(Connector item,
			ClassBuilder<?, ?, ?, ?, ?, ?> source, ClassBuilder<?, ?, ?, ?, ?, ?> target,
			EaModelBuilder<?, ?> model, EaHelper eaHelper) {
		return new JapiAssociationBuilder(item, source, target, eaHelper, model);
	}

	@Override
	protected DependencyBuilder<?, ?> createDependency(Connector item, EaModelBuilder<?, ?> model,
			ClassBuilder<?, ?, ?, ?, ?, ?> source, ClassBuilder<?, ?, ?, ?, ?, ?> target,
			EaHelper eaHelper) {
		return new JapiDependencyBuilder(item, model, null, null, source, target, eaHelper);
	}

	@Override
	protected SkippedBuilder<?, ?, ?, ?> createSkippedConnector(Connector item,
			EaModelBuilder<?, ?> model, EaHelper eaHelper) {
		return new JapiSkippedBuilder(null, item, null, this, model, eaHelper);
	}
}
