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
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;

import org.apache.log4j.Logger;
import org.tanjakostic.jcleancim.model.AbstractUmlObject;
import org.tanjakostic.jcleancim.model.UmlClass;
import org.tanjakostic.jcleancim.model.UmlClass.Data;
import org.tanjakostic.jcleancim.model.UmlObjectData;
import org.tanjakostic.jcleancim.model.UmlPackage;
import org.tanjakostic.jcleancim.model.UmlStereotype;
import org.tanjakostic.jcleancim.util.ProgrammerErrorException;
import org.tanjakostic.jcleancim.util.Util;

/**
 * @param <E>
 *            Type for element data
 * @param <S>
 *            Type for element as source
 * @param <D>
 *            Type for diagrams data
 * @param <A>
 *            Type for attributes data
 * @param <O>
 *            Type for operations data
 * @param <C>
 *            Type for connectors data
 * @author tatjana.kostic@ieee.org
 * @version $Id: ClassBuilder.java 21 2019-08-12 15:44:50Z dev978 $
 */
abstract public class ClassBuilder<E, S, D, A, O, C> extends AbstractObjectBuilderFromEA<UmlClass> {
	private static final Logger _logger = Logger.getLogger(ClassBuilder.class.getName());

	/**
	 * Returns whether EA type is an EA class or an EA interface. E.g., a class with stereotype
	 * 'interface' is <b>not</b> EA interface, because you cannot show it, for example, with the
	 * circle notation; an EA interface has some kind of special treatment.
	 */
	public static boolean isClassOrEaInterface(String eaType) {
		return isEaClass(eaType) || isEaInterface(eaType) || isEaEnumeration(eaType);
	}

	private static boolean isEaClass(String eaType) {
		return "Class".equals(eaType);
	}

	private static boolean isEaInterface(String eaType) {
		return "Interface".equals(eaType);
	}

	private static boolean isEaEnumeration(String eaType) {
		return "Enumeration".equals(eaType);
	}

	// EA-specific names for connectors representing inheritance
	private static final List<String> INHERITANCE_CONNECTORS = Arrays.asList("Generalization",
			"Realisation");

	// private static List<String> getNonSkippedConnNames() {
	// List<String> result = new ArrayList<String>();
	// result.addAll(INHERITANCE_CONNECTORS);
	// result.addAll(DependencyBuilder.TYPE_NAMES);
	// result.addAll(AssociationBuilder.TYPE_NAMES);
	// return result;
	// }

	private final PackageBuilder<?, ?, ?, ?, ?, ?> _containingPackage;
	private UmlObjectData _objData;

	private int _pos;
	private boolean _isEnumeratedType;
	private boolean _isAbstract;
	private boolean _eaPersistentPropSet;
	private boolean _eaLeafPropSet;
	private boolean _eaRootPropSet; // not available through API, only through SQL+XML
	private boolean _isEaInterface;
	private boolean _associationClass;

	private final Map<String, String> _taggedValues = new LinkedHashMap<String, String>();
	private final List<SkippedBuilder<?, ?, ?, ?>> _skippedEaItems = new ArrayList<SkippedBuilder<?, ?, ?, ?>>();
	private final Map<String, ConstraintBuilder> _constraints = new LinkedHashMap<String, ConstraintBuilder>();
	private final List<DiagramBuilder<?>> _diagrams = new ArrayList<DiagramBuilder<?>>();

	private final List<AttributeBuilder<?, ?>> _attributes = new ArrayList<AttributeBuilder<?, ?>>();
	private final List<OperationBuilder<?, ?>> _operations = new ArrayList<OperationBuilder<?, ?>>();

	private final List<AssociationBuilder<?, ?>> _associationsAsSource = new ArrayList<AssociationBuilder<?, ?>>();
	private final List<AssociationBuilder<?, ?>> _associationsAsTarget = new ArrayList<AssociationBuilder<?, ?>>();
	private final List<AssociationBuilder<?, ?>> _associationsAsSourceAndTarget = new ArrayList<AssociationBuilder<?, ?>>();
	private final List<DependencyBuilder<?, ?>> _dependenciesAsSource = new ArrayList<DependencyBuilder<?, ?>>();
	private final List<DependencyBuilder<?, ?>> _dependenciesAsTarget = new ArrayList<DependencyBuilder<?, ?>>();
	private boolean _selfDependent;
	private final List<ClassBuilder<?, ?, ?, ?, ?, ?>> _superclasses = new ArrayList<ClassBuilder<?, ?, ?, ?, ?, ?>>();
	private final List<ClassBuilder<?, ?, ?, ?, ?, ?>> _subclasses = new ArrayList<ClassBuilder<?, ?, ?, ?, ?, ?>>();
	private boolean _selfInherited;
	private final Set<ClassBuilder<?, ?, ?, ?, ?, ?>> _classesUsingMeInAttributes = new HashSet<ClassBuilder<?, ?, ?, ?, ?, ?>>();
	private final Set<ClassBuilder<?, ?, ?, ?, ?, ?>> _classesIUseInAttributes = new HashSet<ClassBuilder<?, ?, ?, ?, ?, ?>>();
	private final Set<ClassBuilder<?, ?, ?, ?, ?, ?>> _classesUsingMeInOperationParams = new HashSet<ClassBuilder<?, ?, ?, ?, ?, ?>>();
	private final Set<ClassBuilder<?, ?, ?, ?, ?, ?>> _classesIUseInOperationParams = new HashSet<ClassBuilder<?, ?, ?, ?, ?, ?>>();

	/**
	 * Constructor that stores EA class data and initialises attribute and association builders.
	 *
	 * @param inData
	 * @param itemsSrc
	 * @param containingPackage
	 * @param eaHelper
	 * @throws NullPointerException
	 *             if any argument is null.
	 */
	protected ClassBuilder(E inData, S itemsSrc, PackageBuilder<?, ?, ?, ?, ?, ?> containingPackage,
			EaHelper eaHelper) {
		Util.ensureNotNull(inData, "inData");
		Util.ensureNotNull(containingPackage, "containingPackage");
		Util.ensureNotNull(eaHelper, "eaHelper");

		_containingPackage = containingPackage;
		EaModelBuilder<?, ?> model = containingPackage.getModel();

		Integer id = getElementID(inData);
		String guid = getElementGUID(inData);
		String name = getElementName(inData);
		String alias = getElementAlias(inData);
		String stereotypes = getElementStereotypes(inData);
		String visibility = getElementVisibility(inData);
		String notes = getElementNotes(inData);
		initObjData(id, guid, name, alias, stereotypes, visibility, notes, eaHelper);
		model.addClass(this);

		int pos = getElementPosition(inData);
		String eaType = getElementType(inData);
		String abstractVal = getElementAbstract(inData);
		boolean isLeaf = getElementIsLeaf(inData);
		boolean isRoot = getElementIsRoot(inData);
		String persistenceVal = getElementPersistence(inData);
		int subtypeVal = getElementSubtypeVal(inData);
		initOwnData(pos, eaType, abstractVal, isLeaf, isRoot, persistenceVal, subtypeVal);

		_logger.log(CTOR_LOG_LEVEL, "Class " + getObjData().getName() + " (" + getPos()
				+ " in package " + getContainingPackage().getObjData().getName() + ")");

		createAndAddClassConstraints(itemsSrc, eaHelper);
		createAndAddTaggedValues(itemsSrc);
		createAndAddDiagrams(itemsSrc, eaHelper);
		createSkippedElementsAndTheirDiagrams(itemsSrc, model, eaHelper);
		createAndAddAttributes(itemsSrc, eaHelper);
		createAndAddOperations(itemsSrc, eaHelper);
		createAndAddConnectors(itemsSrc, model, eaHelper);

		_logger.log(CTOR_LOG_LEVEL, "read from EA: " + getQualifiedName());
	}

	abstract protected Integer getElementID(E inData);

	abstract protected String getElementGUID(E inData);

	abstract protected String getElementName(E inData);

	abstract protected String getElementAlias(E inData);

	abstract protected String getElementStereotypes(E inData);

	abstract protected String getElementVisibility(E inData);

	abstract protected String getElementNotes(E inData);

	private void initObjData(Integer id, String guid, String name, String alias, String stereotypes,
			String visibility, String notes, EaHelper eaHelper) {
		_objData = new UmlObjectData(id, guid, name, alias, new UmlStereotype(stereotypes),
				visibility, eaHelper.getRawText(notes), eaHelper.getHtmlText(notes));
	}

	abstract protected int getElementPosition(E inData);

	abstract protected String getElementType(E inData);

	abstract protected String getElementAbstract(E inData);

	abstract protected boolean getElementIsLeaf(E inData);

	abstract protected boolean getElementIsRoot(E inData);

	abstract protected String getElementPersistence(E inData);

	abstract protected int getElementSubtypeVal(E inData);

	private void initOwnData(int pos, String eaType, String abstractVal, boolean isLeaf,
			boolean isRoot, String persistenceVal, int subtypeVal) {
		_pos = pos;
		_isEaInterface = isEaInterface(eaType);
		_isEnumeratedType = getObjData().getStereotype().contains(UmlStereotype.ENUMERATION)
				|| isEaEnumeration(eaType);
		_isAbstract = abstractVal.equals("1");
		_eaLeafPropSet = isLeaf;
		_eaRootPropSet = isRoot;
		_eaPersistentPropSet = !persistenceVal.isEmpty();
		_associationClass = subtypeVal == 17;
	}

	// ---------------------------- constraints --------------------

	private void createAndAddClassConstraints(S itemsSrc, EaHelper eaHelper) {
		List<Map<String, String>> myConstraints = collectClassConstraints(itemsSrc);
		for (Map<String, String> m : myConstraints) {
			String name = m.get(EA.CLASS_CONSTR_NAME);
			String notes = m.get(EA.CLASS_CONSTR_NOTE);
			getConstraints().put(name, new ConstraintBuilder(this, name, notes, eaHelper));
		}
	}

	abstract protected List<Map<String, String>> collectClassConstraints(S itemsSrc);

	public final Map<String, ConstraintBuilder> getConstraints() {
		return _constraints;
	}

	// ----------------------------- tagged values ---------------

	private void createAndAddTaggedValues(S itemsSrc) {
		List<Map<String, String>> myTaggedValuesFields = collectTaggedValues(itemsSrc);
		for (Map<String, String> m : myTaggedValuesFields) {
			String name = m.get(EA.ELEM_TGVAL_NAME);
			String value = m.get(EA.ELEM_TGVAL_VALUE);

			getTaggedValues().put(name, Util.null2empty(value));
		}
	}

	abstract protected List<Map<String, String>> collectTaggedValues(S itemsSrc);

	public final Map<String, String> getTaggedValues() {
		return _taggedValues;
	}

	// --------------- diagrams ------------------

	private void createAndAddDiagrams(S itemsSrc, EaHelper eaHelper) {
		List<D> dias = collectDiagrams(itemsSrc);
		for (D dia : dias) {
			getDiagrams().add(createDiagram(dia, eaHelper));
		}
	}

	abstract protected List<D> collectDiagrams(S itemsSrc);

	abstract protected DiagramBuilder<?> createDiagram(D item, EaHelper eaHelper);

	// -------------------- skipped elements (and their diagrams) ---------------------

	private void createSkippedElementsAndTheirDiagrams(S itemsSrc, EaModelBuilder<?, ?> model,
			EaHelper eaHelper) {
		List<E> items = collectContainedElements(itemsSrc);
		for (E item : items) {
			if (isClassOrEaInterface(getElementType(item))) {
				// handle class in class:
				createEmbeddedClass(item, eaHelper);
			}
			SkippedBuilder<?, ?, ?, ?> skippedElement = createSkippedElement(item, model, eaHelper);
			getSkippedEaItems().add(skippedElement);

			List<DiagramBuilder<?>> skippedDiagrams = skippedElement.getDiagrams();
			if (!skippedDiagrams.isEmpty()) {
				getDiagrams().addAll(skippedDiagrams);
			}
		}
	}

	/**
	 * Creates class embedded into this class; containing package of the new embedded class is the
	 * same as the containing package of this class.
	 */
	abstract protected ClassBuilder<?, ?, ?, ?, ?, ?> createEmbeddedClass(E item,
			EaHelper eaHelper);

	abstract protected List<E> collectContainedElements(S itemsSrc);

	abstract protected SkippedBuilder<?, ?, ?, ?> createSkippedElement(E item,
			EaModelBuilder<?, ?> model, EaHelper eaHelper);

	// ------------------- attributes -------------------

	// FIXME: Is some refactoring possible, with EaTables.orderItemsOrCatchScrewedOrdering()?
	private void createAndAddAttributes(S itemsSrc, EaHelper eaHelper) {
		// EA unfortunately does not return attributes in the order they are within the class:
		String what = "attr";
		Map<Integer, AttributeBuilder<?, ?>> result = new TreeMap<Integer, AttributeBuilder<?, ?>>(
				new Comparator<Integer>() {
					@Override
					public int compare(Integer o1, Integer o2) {
						return o1.compareTo(o2);
					}
				});
		Set<AttributeBuilder<?, ?>> all = new LinkedHashSet<AttributeBuilder<?, ?>>();

		List<A> attrItems = collectAttributes(itemsSrc);
		int count = attrItems.size();
		List<String> diagnosis = new ArrayList<String>();
		diagnosis.add(String.format("+++ EA problem: attr count on %s = %s%n",
				getObjData().getName(), Integer.valueOf(count)));

		for (int i = 0; i < count; ++i) {
			A a = attrItems.get(i);
			AttributeBuilder<?, ?> attr = createAttribute(a, eaHelper);

			Integer pos = Integer.valueOf(attr.getPos());
			String subName = attr.getObjData().getName();

			String duplicatePos = result.containsKey(pos) ? (" " + EaModelBuilder.DUPLICATE) : "";
			result.put(pos, attr); // if duplicate, will overwrite existing one
			all.add(attr);

			String msg = String.format("   +++ %s %s: pos = %s%s%n", what, subName, pos,
					duplicatePos);
			diagnosis.add(msg);
		}

		if (all.size() != result.size()) {
			_logger.error(diagnosis.toString());
			getAttributes().addAll(all);
			return;
		}
		getAttributes().addAll(result.values());
	}

	abstract protected List<A> collectAttributes(S itemsSrc);

	abstract protected AttributeBuilder<?, ?> createAttribute(A item, EaHelper eaHelper);

	// --------------- operations -----------

	// FIXME: Is some refactoring possible, with createAndAddAttributes()?
	private void createAndAddOperations(S itemsSrc, EaHelper eaHelper) {
		// EA unfortunately does not return operations in the order they are within the class:
		String what = "operation";
		Map<Integer, OperationBuilder<?, ?>> result = new TreeMap<Integer, OperationBuilder<?, ?>>();
		Set<OperationBuilder<?, ?>> all = new HashSet<OperationBuilder<?, ?>>();

		List<O> eaOperations = collectOperations(itemsSrc);
		int count = eaOperations.size();
		List<String> diagnosis = new ArrayList<String>();
		diagnosis.add(String.format("+++ EA problem: operation count on %s = %s%n",
				getObjData().getName(), Integer.valueOf(count)));

		for (int i = 0; i < count; ++i) {
			O op = eaOperations.get(i);
			OperationBuilder<?, ?> oper = createOperation(op, eaHelper);

			Integer pos = Integer.valueOf(oper.getPos());
			String name = oper.getObjData().getName();

			String duplicatePos = result.containsKey(pos) ? (" " + EaModelBuilder.DUPLICATE) : "";
			result.put(pos, oper);
			all.add(oper);

			String msg = String.format("   +++ %s %s: pos = %s%s%n", what, name, pos, duplicatePos);
			diagnosis.add(msg);
		}

		if (all.size() != result.size()) {
			_logger.error(diagnosis.toString());
			getOperations().addAll(all);
			return;
		}
		getOperations().addAll(result.values());
	}

	abstract protected List<O> collectOperations(S itemsSrc);

	abstract protected OperationBuilder<?, ?> createOperation(O item, EaHelper eaHelper);

	// ------ connectors (associations, dependencies, inheritance, skipped connectors) ---------

	private void createAndAddConnectors(S itemsSrc, EaModelBuilder<?, ?> model, EaHelper eaHelper) {
		int selfInhCount = 0;
		int selfDepCount = 0;

		List<C> connectors = collectConnectors(itemsSrc);

		for (C conn : connectors) {
			String type = fetchConnectorType(conn);
			Map<String, String> ids = eaConnectorIDsToFields(conn);

			if (AssociationBuilder.isAssociationOrAggregation(type)
					&& bothEndsAreClass(ids, model)) {
				createAndAddAssociation(ids, conn, model, eaHelper);
			} else if (DependencyBuilder.isDependency(type) && bothEndsAreClass(ids, model)) {
				boolean isSelfDependent = createAndAddOrUpdateExistingDependency(ids, conn, model,
						eaHelper);
				if (isSelfDependent) {
					++selfDepCount;
				}
			} else {
				if (INHERITANCE_CONNECTORS.contains(type)) {
					boolean isSelfInherited = initSubAndSup(ids, model);
					if (isSelfInherited) {
						++selfInhCount;
					}
				} else {
					getSkippedEaItems().add(createSkippedConnector(conn, model, eaHelper));
				}
			}
		}

		_selfInherited = (selfInhCount > 0);
		_selfDependent = (selfDepCount > 0);
	}

	abstract protected List<C> collectConnectors(S itemsSrc);

	abstract protected String fetchConnectorType(C item);

	abstract protected Map<String, String> eaConnectorIDsToFields(C item);

	protected boolean bothEndsAreClass(Map<String, String> connIds, EaModelBuilder<?, ?> model) {
		String type1 = model.findElementType(Integer.valueOf(connIds.get(EA.CONN_FROM_ID)));
		String type2 = model.findElementType(Integer.valueOf(connIds.get(EA.CONN_TO_ID)));
		return isEaClass(type1) && isEaClass(type2);
	}

	abstract protected AssociationBuilder<?, ?> createAssociation(C item,
			ClassBuilder<?, ?, ?, ?, ?, ?> source, ClassBuilder<?, ?, ?, ?, ?, ?> target,
			EaModelBuilder<?, ?> model, EaHelper eaHelper);

	abstract protected DependencyBuilder<?, ?> createDependency(C item, EaModelBuilder<?, ?> model,
			ClassBuilder<?, ?, ?, ?, ?, ?> source, ClassBuilder<?, ?, ?, ?, ?, ?> target,
			EaHelper eaHelper);

	abstract protected SkippedBuilder<?, ?, ?, ?> createSkippedConnector(C item,
			EaModelBuilder<?, ?> model, EaHelper eaHelper);

	private void createAndAddAssociation(Map<String, String> ids, C item,
			EaModelBuilder<?, ?> model, EaHelper eaHelper) {
		Integer clientId = Integer.valueOf(ids.get(EA.CONN_FROM_ID));
		Integer supplierId = Integer.valueOf(ids.get(EA.CONN_TO_ID));
		Integer connId = Integer.valueOf(ids.get(EA.CONN_ID));

		Integer id = getObjData().getId();
		boolean amSource = clientId.equals(id);
		boolean amTarget = supplierId.equals(id);
		if (!amSource && !amTarget) {
			throw new ProgrammerErrorException("Class '" + getObjData().getName()
					+ "' neither source nor target in assoc '" + model.findClass(clientId) + " - "
					+ model.findClass(supplierId) + "'.");
		}

		AssociationBuilder<?, ?> assoc = model.findAssociation(connId);
		if (amSource && amTarget) {
			if (assoc != null) {
				throw new ProgrammerErrorException("Assoc " + model.findClass(clientId) + " - "
						+ model.findClass(supplierId) + " should have not been created.");
			}
			assoc = createAssociation(item, this, this, model, eaHelper);
			_associationsAsSourceAndTarget.add(assoc);
		} else if (amSource) {
			if (assoc != null) {
				assoc.getSourceEnd().setType(this);
			} else {
				assoc = createAssociation(item, this, null, model, eaHelper);
			}
			_associationsAsSource.add(assoc);
			_logger.debug("Added " + assoc + " as source to " + getQualifiedName());
		} else if (amTarget) {
			if (assoc != null) {
				assoc.getTargetEnd().setType(this);
			} else {
				assoc = createAssociation(item, null, this, model, eaHelper);
			}
			_associationsAsTarget.add(assoc);
			_logger.debug("Added " + assoc + " as target to " + getQualifiedName());
		}
	}

	private boolean initSubAndSup(Map<String, String> ids, EaModelBuilder<?, ?> model) {
		boolean selfInherited = false;
		Integer clientId = Integer.valueOf(ids.get(EA.CONN_FROM_ID));
		Integer supplierId = Integer.valueOf(ids.get(EA.CONN_TO_ID));
		Integer connId = Integer.valueOf(ids.get(EA.CONN_ID));

		Integer id = getObjData().getId();
		boolean amSource = (clientId.equals(id));
		boolean amTarget = (supplierId.equals(id));
		if (!amSource && !amTarget) {
			throw new ProgrammerErrorException("Class '" + getObjData().getName()
					+ "' neither source nor target in generalization (" + connId + ").");
		}

		if (amSource && amTarget) {
			selfInherited = true;
		} else if (amSource) {
			ClassBuilder<?, ?, ?, ?, ?, ?> otherClass = model.findClass(supplierId);
			if (otherClass != null) {
				_logger.debug("Adding " + getObjData().getName() + " as subclass of "
						+ otherClass.getObjData().getName());
				otherClass.getSubclasses().add(this);
				getSuperclasses().add(otherClass);
			}
		} else if (amTarget) {
			ClassBuilder<?, ?, ?, ?, ?, ?> otherClass = model.findClass(clientId);
			if (otherClass != null) {
				_logger.debug("Adding " + getObjData().getName() + " as superclass of "
						+ otherClass.getObjData().getName());
				otherClass.getSuperclasses().add(this);
				getSubclasses().add(otherClass);
			}
		}
		return selfInherited;
	}

	private boolean createAndAddOrUpdateExistingDependency(Map<String, String> ids, C item,
			EaModelBuilder<?, ?> model, EaHelper eaHelper) {
		boolean selfDependent = false;
		Integer clientId = Integer.valueOf(ids.get(EA.CONN_FROM_ID));
		Integer supplierId = Integer.valueOf(ids.get(EA.CONN_TO_ID));
		Integer connId = Integer.valueOf(ids.get(EA.CONN_ID));

		Integer id = getObjData().getId();
		boolean amSource = clientId.equals(id);
		boolean amTarget = supplierId.equals(id);
		if (!amSource && !amTarget) {
			throw new ProgrammerErrorException("Class '" + getObjData().getName()
					+ "' neither source nor target in class dependency '" + connId + "'.");
		}

		DependencyBuilder<?, ?> dep = model.findDependency(connId);
		if (amSource && amTarget) {
			selfDependent = true;
		} else if (amSource) {
			if (dep != null) {
				dep.setSourceClass(this);
				_logger.debug("Updated source of class dependency " + dep);
			} else {
				dep = createDependency(item, model, this, null, eaHelper);
			}
			_dependenciesAsSource.add(dep);
		} else if (amTarget) {
			if (dep != null) {
				dep.setTargetClass(this);
				_logger.debug("Updated target of class dependency " + dep);
			} else {
				dep = createDependency(item, model, null, this, eaHelper);
			}
			_dependenciesAsTarget.add(dep);
		}
		return selfDependent;
	}

	// -----------------------------------

	public final PackageBuilder<?, ?, ?, ?, ?, ?> getContainingPackage() {
		return _containingPackage;
	}

	public final boolean isSelfDependent() {
		return _selfDependent;
	}

	public final boolean isSelfInherited() {
		return _selfInherited;
	}

	public final int getPos() {
		return _pos;
	}

	public final boolean isEnumeratedType() {
		return _isEnumeratedType;
	}

	public final boolean isAbstract() {
		return _isAbstract;
	}

	public final boolean isEaPersistentPropSet() {
		return _eaPersistentPropSet;
	}

	public final boolean isEaLeafPropSet() {
		return _eaLeafPropSet;
	}

	public final boolean isEaRootPropSet() {
		return _eaRootPropSet;
	}

	public final boolean isEaInterface() {
		return _isEaInterface;
	}

	public final boolean isAssociationClass() {
		return _associationClass;
	}

	public final List<SkippedBuilder<?, ?, ?, ?>> getSkippedEaItems() {
		return _skippedEaItems;
	}

	public final List<AttributeBuilder<?, ?>> getAttributes() {
		return _attributes;
	}

	public final List<OperationBuilder<?, ?>> getOperations() {
		return _operations;
	}

	public final List<DiagramBuilder<?>> getDiagrams() {
		return _diagrams;
	}

	public final List<ClassBuilder<?, ?, ?, ?, ?, ?>> getSuperclasses() {
		return _superclasses;
	}

	public final List<ClassBuilder<?, ?, ?, ?, ?, ?>> getSubclasses() {
		return _subclasses;
	}

	public final void addAttributeAfferentClass(ClassBuilder<?, ?, ?, ?, ?, ?> clazz) {
		_classesUsingMeInAttributes.add(clazz);
	}

	public final void addAttributeEfferentClass(ClassBuilder<?, ?, ?, ?, ?, ?> clazz) {
		_classesIUseInAttributes.add(clazz);
	}

	/**
	 * For associations where I'm on the target end, returns the list of source end classes. The
	 * result may includes this if the association is recursive (both ends of the same type).
	 * <p>
	 * Implementation note: If you call {@link #toString()} from within this method, ensure you add
	 * a condition to avoid recursion (because {@link #toString()} calls this method).
	 */
	public final List<ClassBuilder<?, ?, ?, ?, ?, ?>> getAssociationSourceEndClasses() {
		List<AssociationBuilder<?, ?>> asTargets = new ArrayList<AssociationBuilder<?, ?>>(
				_associationsAsTarget);
		asTargets.addAll(_associationsAsSourceAndTarget);
		List<ClassBuilder<?, ?, ?, ?, ?, ?>> result = new ArrayList<ClassBuilder<?, ?, ?, ?, ?, ?>>(
				asTargets.size());
		for (AssociationBuilder<?, ?> assoc : asTargets) {
			ClassBuilder<?, ?, ?, ?, ?, ?> other = assoc.getSourceEnd().getType();
			result.add(other);
		}
		return result;
	}

	/**
	 * For associations where I'm on the source end, returns the list of target end classes. The
	 * result may includes this if the association is recursive (both ends of the same type).
	 * <p>
	 * Implementation note: If you call {@link #toString()} from within this method, ensure you add
	 * a condition to avoid recursion (because {@link #toString()} calls this method).
	 */
	public final List<ClassBuilder<?, ?, ?, ?, ?, ?>> getAssociationTargetEndClasses() {
		List<AssociationBuilder<?, ?>> asSources = new ArrayList<AssociationBuilder<?, ?>>(
				_associationsAsSource);
		asSources.addAll(_associationsAsSourceAndTarget);
		List<ClassBuilder<?, ?, ?, ?, ?, ?>> result = new ArrayList<ClassBuilder<?, ?, ?, ?, ?, ?>>(
				asSources.size());
		for (AssociationBuilder<?, ?> assoc : asSources) {
			ClassBuilder<?, ?, ?, ?, ?, ?> other = assoc.getTargetEnd().getType();
			result.add(other);
		}
		return result;
	}

	/** Returns classes that depend on me through explicit UML dependency in the model. */
	public final List<ClassBuilder<?, ?, ?, ?, ?, ?>> getDependencyAfferentClasses() {
		List<ClassBuilder<?, ?, ?, ?, ?, ?>> result = new ArrayList<ClassBuilder<?, ?, ?, ?, ?, ?>>(
				_dependenciesAsTarget.size());
		for (DependencyBuilder<?, ?> dep : _dependenciesAsTarget) {
			ClassBuilder<?, ?, ?, ?, ?, ?> other = dep.getSourceClass();
			result.add(other);
		}
		return result;
	}

	/** Returns classes on which I depend through explicit UML dependency in the model. */
	public final List<ClassBuilder<?, ?, ?, ?, ?, ?>> getDependencyEfferentClasses() {
		List<ClassBuilder<?, ?, ?, ?, ?, ?>> result = new ArrayList<ClassBuilder<?, ?, ?, ?, ?, ?>>(
				_dependenciesAsSource.size());
		for (DependencyBuilder<?, ?> dep : _dependenciesAsSource) {
			ClassBuilder<?, ?, ?, ?, ?, ?> other = dep.getTargetClass();
			result.add(other);
		}
		return result;
	}

	public final void addOperationAfferentClass(ClassBuilder<?, ?, ?, ?, ?, ?> clazz) {
		_classesUsingMeInOperationParams.add(clazz);
	}

	public final void addOperationEfferentClass(ClassBuilder<?, ?, ?, ?, ?, ?> clazz) {
		_classesIUseInOperationParams.add(clazz);
	}

	public final String getQualifiedName() {
		return getContainingPackage().getObjData().getName() + AbstractUmlObject.PACKAGE_SEPARATOR
				+ getObjData().getName();
	}

	// =====================================================

	@Override
	public final UmlObjectData getObjData() {
		return _objData;
	}

	/**
	 * {@inheritDoc}
	 * <p>
	 * Returns class with its data, tagged values, skipped items, constraints, diagrams and literals
	 * (in case this is an enumerated type). Model builder must add other class features
	 * (attributes, opearations, associations and dependencies) once all the classes in the model
	 * have been created.
	 */
	@Override
	public final UmlClass build() {
		return super.build();
	}

	@Override
	protected final void doBuild() {
		UmlPackage containingPackage = getContainingPackage().getResult();
		List<UmlClass> superclasses = new ArrayList<UmlClass>();
		for (ClassBuilder<?, ?, ?, ?, ?, ?> cb : getSuperclasses()) {
			superclasses.add(cb.build());
		}

		Data data = new Data(isSelfDependent(), isAbstract(), isEaPersistentPropSet(),
				isEaLeafPropSet(), isEaRootPropSet(), isEaInterface(), isAssociationClass(),
				isSelfInherited(), isEnumeratedType());

		UmlClass result = new UmlClass(containingPackage, superclasses, getObjData(), data);
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
		if (result.isEnumeratedType()) {
			for (AttributeBuilder<?, ?> ab : getAttributes()) {
				ab.build();
			}
		}
	}
}
