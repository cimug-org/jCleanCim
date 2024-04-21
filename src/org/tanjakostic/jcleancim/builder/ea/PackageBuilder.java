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

package org.tanjakostic.jcleancim.builder.ea;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.log4j.Logger;
import org.tanjakostic.jcleancim.model.AbstractUmlObject;
import org.tanjakostic.jcleancim.model.UmlModel;
import org.tanjakostic.jcleancim.model.UmlObjectData;
import org.tanjakostic.jcleancim.model.UmlPackage;
import org.tanjakostic.jcleancim.model.UmlPackage.Data;
import org.tanjakostic.jcleancim.model.UmlPackage.Kind;
import org.tanjakostic.jcleancim.model.UmlStereotype;
import org.tanjakostic.jcleancim.model.UmlStructure;
import org.tanjakostic.jcleancim.util.ProgrammerErrorException;
import org.tanjakostic.jcleancim.util.Util;

/**
 * @param <P>
 *            Type for package data
 * @param <E>
 *            Type for element data
 * @param <SP>
 *            Type for package as source
 * @param <SE>
 *            Type for element as source
 * @param <D>
 *            Type for diagram data
 * @param <C>
 *            Type for connector data
 * @author tatjana.kostic@ieee.org
 * @version $Id: PackageBuilder.java 34 2019-12-20 18:37:17Z dev978 $
 */
public abstract class PackageBuilder<P, E, SP, SE, D, C>
		extends AbstractObjectBuilderFromEA<UmlPackage> {
	private static final Logger _logger = Logger.getLogger(PackageBuilder.class.getName());

	public static boolean isEaPackage(String eaType) {
		return "Package".equals(eaType);
	}

	private UmlObjectData _objData;
	private final PackageBuilder<?, ?, ?, ?, ?, ?> _containingPackage;
	private int _depth;
	private final int _modelId;
	private final EaModelBuilder<?, ?> _model;

	private int _pos;
	private Integer _eaElementID;
	private UmlPackage.Kind _kind;
	private final Map<String, String> _taggedValues = new LinkedHashMap<String, String>();

	private final List<SkippedBuilder<?, ?, ?, ?>> _skippedEaItems = new ArrayList<SkippedBuilder<?, ?, ?, ?>>();
	private final List<DependencyBuilder<?, ?>> _dependenciesAsSource = new ArrayList<DependencyBuilder<?, ?>>();
	private final List<DependencyBuilder<?, ?>> _dependenciesAsTarget = new ArrayList<DependencyBuilder<?, ?>>();
	private boolean _selfDependent;
	private final List<DiagramBuilder<?>> _diagrams = new ArrayList<DiagramBuilder<?>>();
	private final List<ClassBuilder<?, ?, ?, ?, ?, ?>> _classes = new ArrayList<ClassBuilder<?, ?, ?, ?, ?, ?>>();
	private final List<PackageBuilder<?, ?, ?, ?, ?, ?>> _childPackages = new ArrayList<PackageBuilder<?, ?, ?, ?, ?, ?>>();

	/**
	 * Constructor. Package is stored in EA DB table for elements, but there is also a specific
	 * package table, based on packageId; with respect to the data we use, it contains only one item
	 * not present in elements table: parent package ID. However, when using API, EA does the chatty
	 * queries, so we use eaPackage as much as possible (that table is smaller and queries are
	 * faster).
	 *
	 * @param inData
	 * @param inDataE
	 * @param itemsSrcP
	 * @param itemsSrcE
	 * @param model
	 * @param containingPackage
	 * @param modelId
	 * @param eaHelper
	 */
	protected PackageBuilder(P inData, E inDataE, SP itemsSrcP, SE itemsSrcE,
			EaModelBuilder<?, ?> model, PackageBuilder<?, ?, ?, ?, ?, ?> containingPackage,
			int modelId, EaHelper eaHelper) {
		Util.ensureNotNull(inData, "inData");
		Util.ensureNotNull(inDataE, "inDataE");
		Util.ensureNotNull(model, "model");
		Util.ensureNotNull(eaHelper, "helper");

		_model = model;
		_containingPackage = containingPackage;
		_modelId = modelId;

		Integer id = getPackageID(inData);
		String guid = getPackageGUID(inData);
		String name = getPackageName(inData);
		String notes = getPackageNotes(inData);
		String alias = getPackageAlias(inData);
		String stereotypes = getPackageStereotypes(inData);
		String visibility = getPackageVisibility(inDataE);
		initObjData(id, guid, name, alias, stereotypes, visibility, notes, eaHelper);
		getModel().addPackage(this);

		int pos = getPackagePos(inData);
		Integer parentId = getPackageParentID(inData);
		Integer eaElementId = getPackageElementID(inDataE);
		initOwnData(pos, containingPackage, modelId, parentId, eaElementId);
		_logger.info("  processing " + getKind().getLabel() + " " + Util.getIndentSpaces(getDepth())
				+ name + " (" + getPos() + ") ...");

		createAndAddTaggedValues(itemsSrcE);
		createAndAddDiagrams(itemsSrcP, eaHelper);
		createAndAddConnectors(itemsSrcE, model, eaHelper);
		createAndAddClassesAndSkippedElementsWithTheirDiagrams(itemsSrcP, model, eaHelper);

		// recursively creates sub-packages:
		createChildPackages(itemsSrcP, eaHelper);

		_logger.log(CTOR_LOG_LEVEL, "read " + toString() /* getObjData().getName() */);
	}

	abstract protected Integer getPackageID(P inData);

	abstract protected String getPackageGUID(P inData);

	abstract protected String getPackageName(P inData);

	abstract protected String getPackageNotes(P inData);

	abstract protected String getPackageAlias(P inData);

	abstract protected String getPackageStereotypes(P inData);

	abstract protected String getPackageVisibility(E inDataE);

	private void initObjData(Integer id, String guid, String name, String alias, String stereotypes,
			String visibility, String notes, EaHelper eaHelper) {
		_objData = new UmlObjectData(id, guid, name, alias, new UmlStereotype(stereotypes),
				visibility, eaHelper.getRawText(notes), eaHelper.getHtmlText(notes));
	}

	abstract protected int getPackagePos(P inData);

	abstract protected Integer getPackageParentID(P inData);

	abstract protected Integer getPackageElementID(E inDataE);

	private void initOwnData(int pos, PackageBuilder<?, ?, ?, ?, ?, ?> containingPackage,
			int modelId, Integer parentId, Integer eaElementId) {
		_pos = pos;
		_eaElementID = eaElementId;
		if (containingPackage == null) {
			_kind = UmlPackage.Kind.MODEL;
			_depth = -1;
		} else if (parentId != null && parentId.intValue() == modelId) {
			_kind = UmlPackage.Kind.TOP;
			_depth = 0;
		} else {
			_kind = UmlPackage.Kind.PACKAGE;
			_depth = containingPackage.getDepth() + 1;
		}
	}

	// ------------------- tagged values ------------------------

	private void createAndAddTaggedValues(SE itemsSrc) {
		List<Map<String, String>> myTaggedValuesFields = collectTaggedValues(itemsSrc);
		for (Map<String, String> m : myTaggedValuesFields) {
			String name = m.get(EA.ELEM_TGVAL_NAME);
			String value = m.get(EA.ELEM_TGVAL_VALUE);

			getTaggedValues().put(name, Util.null2empty(value));
		}
	}

	abstract protected List<Map<String, String>> collectTaggedValues(SE itemsSrc);

	public final Map<String, String> getTaggedValues() {
		return _taggedValues;
	}

	// ----------------------- diagrams ------------------

	private void createAndAddDiagrams(SP itemsSrc, EaHelper eaHelper) {
		List<D> items = collectDiagrams(itemsSrc);
		for (D item : items) {
			getDiagrams().add(createDiagram(item, eaHelper));
		}
	}

	abstract protected List<D> collectDiagrams(SP itemsSrc);

	abstract protected DiagramBuilder<?> createDiagram(D item, EaHelper eaHelper);

	// ------------------- connectors (including skipped) ------------------------

	private void createAndAddConnectors(SE itemsSrcE, EaModelBuilder<?, ?> model,
			EaHelper eaHelper) {
		int selfDepCount = 0;

		List<C> items = collectConnectors(itemsSrcE);

		for (C item : items) {
			String type = fetchConnectorType(item);
			Map<String, String> ids = eaConnectorIDsToFields(item);

			if (DependencyBuilder.isDependency(type) && bothEndsArePackage(ids, model)) {
				boolean isSelfDependent = createAndAddOrUpdateExistingDependency(ids, item, model,
						eaHelper);
				if (isSelfDependent) {
					++selfDepCount;
				}
			} else {
				getSkippedEaItems().add(createSkippedConnector(item, model, eaHelper));
			}
		}
		_selfDependent = (selfDepCount > 0);
	}

	abstract protected List<C> collectConnectors(SE itemsSrc);

	abstract protected String fetchConnectorType(C item);

	abstract protected Map<String, String> eaConnectorIDsToFields(C item);

	protected boolean bothEndsArePackage(Map<String, String> connIds, EaModelBuilder<?, ?> model) {
		String type1 = model.findElementType(Integer.valueOf(connIds.get(EA.CONN_FROM_ID)));
		String type2 = model.findElementType(Integer.valueOf(connIds.get(EA.CONN_TO_ID)));
		return isEaPackage(type1) && isEaPackage(type2);
	}

	abstract protected SkippedBuilder<?, ?, ?, ?> createSkippedConnector(C item,
			EaModelBuilder<?, ?> model, EaHelper eaHelper);

	private boolean createAndAddOrUpdateExistingDependency(Map<String, String> ids, C item,
			EaModelBuilder<?, ?> model, EaHelper eaHelper) {
		boolean isSelfdependent = false;
		Integer clientId = Integer.valueOf(ids.get(EA.CONN_FROM_ID));
		Integer supplierId = Integer.valueOf(ids.get(EA.CONN_TO_ID));
		Integer connId = Integer.valueOf(ids.get(EA.CONN_ID));

		Integer elemId = getEaElementID();
		boolean amSource = (clientId.equals(elemId));
		boolean amTarget = (supplierId.equals(elemId));
		if (!amSource && !amTarget) {
			throw new ProgrammerErrorException("Package '" + getObjData().getName()
					+ "' neither source nor target in package dependency '" + connId + "'.");
		}

		DependencyBuilder<?, ?> dep = getModel().findDependency(connId);
		if (amSource && amTarget) {
			isSelfdependent = true;
		} else if (amSource) {
			if (dep != null) {
				dep.setSourcePackage(this);
				_logger.debug("Updated source of package dependency " + dep);
			} else {
				dep = createDependency(item, model, this, null, eaHelper);
			}
			getDependenciesAsSource().add(dep);
		} else if (amTarget) {
			if (dep != null) {
				dep.setTargetPackage(this);
				_logger.debug("Updated target of package dependency " + dep);
			} else {
				dep = createDependency(item, model, null, this, eaHelper);
			}
			getDependenciesAsTarget().add(dep);
		}
		return isSelfdependent;
	}

	abstract protected DependencyBuilder<?, ?> createDependency(C item, EaModelBuilder<?, ?> model,
			PackageBuilder<?, ?, ?, ?, ?, ?> source, PackageBuilder<?, ?, ?, ?, ?, ?> target,
			EaHelper eaHelper);

	// --------------- classes (including skipped elements with their diagrams) ------------------

	private void createAndAddClassesAndSkippedElementsWithTheirDiagrams(SP itemsSrcP,
			EaModelBuilder<?, ?> model, EaHelper eaHelper) {
		List<E> items = collectPackageElements(itemsSrcP);

		for (E item : items) {
			String eaType = fetchElementType(item);

			if (ClassBuilder.isClassOrEaInterface(eaType)) {
				getClasses().add(createClass(item, eaHelper));
			} else if (PackageBuilder.isEaPackage(eaType)) {
				continue;
			} else {
				SkippedBuilder<?, ?, ?, ?> skippedElement = createSkippedElement(item, model,
						eaHelper);
				getSkippedEaItems().add(skippedElement);

				List<DiagramBuilder<?>> skippedDiagrams = skippedElement.getDiagrams();
				if (!skippedDiagrams.isEmpty()) {
					getDiagrams().addAll(skippedDiagrams);
				}
			}
		}
	}

	abstract protected List<E> collectPackageElements(SP itemsSrcP);

	abstract protected String fetchElementType(E item);

	abstract protected SkippedBuilder<?, ?, ?, ?> createSkippedElement(E item,
			EaModelBuilder<?, ?> model, EaHelper eaHelper);

	abstract protected ClassBuilder<?, ?, ?, ?, ?, ?> createClass(E item, EaHelper eaHelper);

	// ---------------- sub-packages ----------

	private void createChildPackages(SP itemsSrcP, EaHelper eaHelper) {
		List<P> items = collectSubPackages(itemsSrcP);
		for (P item : items) {
			getChildPackages().add(createSubPackage(item, eaHelper));
		}
	}

	abstract protected List<P> collectSubPackages(SP itemsSrcP);

	abstract protected PackageBuilder<?, ?, ?, ?, ?, ?> createSubPackage(P item, EaHelper eaHelper);

	// ---------------

	public final EaModelBuilder<?, ?> getModel() {
		return _model;
	}

	public final PackageBuilder<?, ?, ?, ?, ?, ?> getContainingPackage() {
		return _containingPackage;
	}

	public final UmlPackage.Kind getKind() {
		return _kind;
	}

	public final int getDepth() {
		return _depth;
	}

	public final int getModelId() {
		return _modelId;
	}

	public final int getPos() {
		return _pos;
	}

	public final Integer getEaElementID() {
		return _eaElementID;
	}

	public final boolean isSelfDependent() {
		return _selfDependent;
	}

	public final List<SkippedBuilder<?, ?, ?, ?>> getSkippedEaItems() {
		return _skippedEaItems;
	}

	public final List<DependencyBuilder<?, ?>> getDependenciesAsSource() {
		return _dependenciesAsSource;
	}

	public final List<DependencyBuilder<?, ?>> getDependenciesAsTarget() {
		return _dependenciesAsTarget;
	}

	public final List<DiagramBuilder<?>> getDiagrams() {
		return _diagrams;
	}

	public final List<ClassBuilder<?, ?, ?, ?, ?, ?>> getClasses() {
		return _classes;
	}

	public final List<PackageBuilder<?, ?, ?, ?, ?, ?>> getChildPackages() {
		return _childPackages;
	}

	// -----------------------

	/** Returns all packages that I depend on through an explicit UML dependency in the model. */
	public final List<PackageBuilder<?, ?, ?, ?, ?, ?>> collectEfferentPackages() {
		List<PackageBuilder<?, ?, ?, ?, ?, ?>> result = new ArrayList<PackageBuilder<?, ?, ?, ?, ?, ?>>(
				_dependenciesAsSource.size());
		for (DependencyBuilder<?, ?> dep : _dependenciesAsSource) {
			result.add(dep.getTargetPackage());
		}
		return result;
	}

	/** Returns all packages that depend on me through an explicit UML dependency in the model. */
	public final List<PackageBuilder<?, ?, ?, ?, ?, ?>> collectAfferentPackages() {
		List<PackageBuilder<?, ?, ?, ?, ?, ?>> result = new ArrayList<PackageBuilder<?, ?, ?, ?, ?, ?>>(
				_dependenciesAsTarget.size());
		for (DependencyBuilder<?, ?> dep : _dependenciesAsTarget) {
			result.add(dep.getSourcePackage());
		}
		return result;
	}

	/** Returns UUIDs of classes in the order they are defined in the repository. */
	public final List<String> getClassUuids() {
		List<String> result = new ArrayList<String>();
		for (ClassBuilder<?, ?, ?, ?, ?, ?> c : getClasses()) {
			result.add(c.getObjData().getUuid());
		}
		return result;
	}

	public final String getQualifiedName() {
		if (getKind() == Kind.MODEL) {
			return "/" + getObjData().getName();
		}
		if (getContainingPackage() == null) {
			return "null::" + getObjData().getName();
		}
		return getContainingPackage().getObjData().getName() + AbstractUmlObject.PACKAGE_SEPARATOR
				+ getObjData().getName();
	}

	// ==================

	@Override
	public String toString() {
		String result = "PackageBuilder [_kind=" + _kind;
		if (_containingPackage != null) {
			result += ", _containingPackage=" + _containingPackage.getObjData().getName();
		}
		result += ", _depth=" + _depth;
		result += ", _eaElementID=" + _eaElementID;
		result += ", _objData=" + _objData;
		result += ", _modelId=" + _modelId;
		result += ", _selfDependent=" + _selfDependent;
		if (!_taggedValues.isEmpty()) {
			result += ", " + _taggedValues.size() + "_taggedValues" + _taggedValues;
		}
		if (!_skippedEaItems.isEmpty()) {
			result += ", " + _skippedEaItems.size() + "_skippedEaItems=" + _skippedEaItems;
		}
		if (!_diagrams.isEmpty()) {
			result += ", " + _diagrams.size() + "_diagrams=" + _diagrams;
		}
		if (!_dependenciesAsSource.isEmpty()) {
			result += ", _dependenciesAsSource=" + _dependenciesAsSource.size();
		}
		if (!_dependenciesAsTarget.isEmpty()) {
			result += ", _dependenciesAsTarget=" + _dependenciesAsTarget.size();
		}
		if (!_classes.isEmpty()) {
			result += ", _classes=" + _classes.size();
		}
		if (!_childPackages.isEmpty()) {
			result += ", _childPackages=" + _childPackages.size() + "]";
		}
		return result;
	}

	// ==================

	@Override
	public final UmlObjectData getObjData() {
		return _objData;
	}

	@Override
	public UmlPackage build() {
		throw new UnsupportedOperationException("Programmer error: use method with parameter.");
	}

	@Override
	protected void doBuild() {
		throw new UnsupportedOperationException("Programmer error: use method with parameter.");
	}

	/**
	 * {@inheritDoc}
	 * <p>
	 * Recursively builds the model skeleton with packages and classes, and all other items that do
	 * not require references to classes. The in-memory model returned from here is thus only
	 * half-built. The model builder must finish the build process ("link") by calling the builders
	 * for class features (attributes, operations, associations, dependencies) and for packages
	 * (dependencies), because they all require existing, valid classes and packages.
	 */
	@Override
	public final UmlPackage build(UmlModel model) {
		if (getResult() == null) {
			doBuild(model);
		}
		return getResult();
	}

	@Override
	protected final void doBuild(UmlModel model) {
		Data data = new Data(new UmlStructure.Data(isSelfDependent()));

		UmlPackage result = null;
		if (model != null) {
			result = new UmlPackage(model, getObjData(), data);
		} else {
			UmlPackage containingPackage = getContainingPackage().getResult();
			if (containingPackage == null) {
				throw new ProgrammerErrorException(
						String.format("Container for package %s should have been built.",
								getObjData().toString()));
			}
			result = new UmlPackage(containingPackage, getObjData(), data);
		}
		setResult(result);

		for (Entry<String, String> entry : getTaggedValues().entrySet()) {
			result.addTaggedValue(entry.getKey(), entry.getValue());
		}
		for (SkippedBuilder<?, ?, ?, ?> sb : getSkippedEaItems()) {
			sb.build();
		}
		for (DiagramBuilder<?> db : getDiagrams()) {
			db.build();
		}
		for (PackageBuilder<?, ?, ?, ?, ?, ?> childPb : getChildPackages()) {
			childPb.doBuild(null);
		}
	}
}
