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

package org.tanjakostic.jcleancim.statistics;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.log4j.Logger;
import org.tanjakostic.jcleancim.common.Config;
import org.tanjakostic.jcleancim.common.Nature;
import org.tanjakostic.jcleancim.common.OwningWg;
import org.tanjakostic.jcleancim.model.AbstractUmlObject;
import org.tanjakostic.jcleancim.model.UmlAssociation;
import org.tanjakostic.jcleancim.model.UmlAttribute;
import org.tanjakostic.jcleancim.model.UmlClass;
import org.tanjakostic.jcleancim.model.UmlDependency;
import org.tanjakostic.jcleancim.model.UmlDiagram;
import org.tanjakostic.jcleancim.model.UmlKind;
import org.tanjakostic.jcleancim.model.UmlModel;
import org.tanjakostic.jcleancim.model.UmlObject;
import org.tanjakostic.jcleancim.model.UmlOperation;
import org.tanjakostic.jcleancim.model.UmlPackage;
import org.tanjakostic.jcleancim.util.Util;

/**
 * Statistics per owner for model of any {@link Nature}.
 *
 * @author tatjana.kostic@ieee.org
 * @version $Id: StatsPerOwner.java 31 2019-12-08 01:19:54Z dev978 $
 */
public class StatsPerOwner {
	private static final Logger _logger = Logger.getLogger(StatsPerOwner.class.getName());

	private final OwningWg _owner;
	private final Nature _nature;
	private final Counter _currentScopeCounter;
	private final Counter _currentScopeInformativeCounter;
	private final Counter _totalModelCounter;

	private final Counter _counter;
	private final Map<UmlKind, List<UmlPackage>> _packages = new LinkedHashMap<UmlKind, List<UmlPackage>>();
	private final Map<UmlKind, List<UmlClass>> _classes = new LinkedHashMap<UmlKind, List<UmlClass>>();
	private final Map<UmlKind, List<UmlAttribute>> _attributes = new LinkedHashMap<UmlKind, List<UmlAttribute>>();
	private final Map<UmlKind, List<UmlAssociation>> _associations = new LinkedHashMap<UmlKind, List<UmlAssociation>>();
	private final Map<UmlKind, List<UmlOperation>> _operations = new LinkedHashMap<UmlKind, List<UmlOperation>>();
	private final Map<UmlKind, List<UmlDependency>> _dependencies = new LinkedHashMap<UmlKind, List<UmlDependency>>();
	private final Map<UmlKind, List<UmlDiagram>> _diagrams = new LinkedHashMap<UmlKind, List<UmlDiagram>>();
	private final Map<String, Set<UmlObject>> _tags = new LinkedHashMap<String, Set<UmlObject>>();

	public StatsPerOwner(UmlModel model, OwningWg owner, Nature nature, Counter currentScopeCounter,
			Counter totalModelCounter) {
		_owner = owner;
		_nature = nature;
		_currentScopeCounter = currentScopeCounter;
		_currentScopeInformativeCounter = new Counter();
		_totalModelCounter = totalModelCounter;

		_counter = new Counter();
		_counter.packageCount = initPackages(model, owner);
		_counter.classCount = initClasses(model, owner);
		_counter.attributeCount = initAttributes(model, owner);
		_counter.associationCount = initAssociations(model, owner);
		_counter.operationCount = initOperations(model, owner);
		_counter.dependencyCount = initDependencies(model, owner);
		_counter.diagramCount = initDiagrams(model, owner);
	}

	private int initPackages(UmlModel model, OwningWg owner) {
		List<UmlPackage> packages = AbstractUmlObject.collectForScope(model.getPackages(),
				EnumSet.of(owner));
		for (UmlKind kind : UmlPackage.getKinds(_nature)) {
			_packages.put(kind, new ArrayList<UmlPackage>());
		}
		for (UmlPackage o : packages) {
			_packages.get(o.getKind()).add(o);
			if (o.isInformative()) {
				++_currentScopeInformativeCounter.packageCount;
			}
			if (!o.getTaggedValues().isEmpty()) {
				addElementWithTag(o);
			}
		}
		return packages.size();
	}

	private int initClasses(UmlModel model, OwningWg owner) {
		List<UmlClass> classes = AbstractUmlObject.collectForScope(model.getClasses(),
				EnumSet.of(owner));
		for (UmlKind kind : UmlClass.getKinds(_nature)) {
			_classes.put(kind, new ArrayList<UmlClass>());
		}
		for (UmlClass o : classes) {
			List<UmlClass> list = _classes.get(o.getKind());
			if (list != null) {
				list.add(o);
			} else {
				String propsFileName = Config.DEFAULT_PROPS_FILE_NAME;
				if (Nature.isIec61850(_nature)) {
					propsFileName = Config.IEC61850_PROPS_FILE_NAME;
				}
				_logger.warn("In statistics: Found mismatch for nature of class '"
						+ o.toShortString(false, true) + ". Are you missing nature-specific "
						+ "config file (" + propsFileName
						+ ") while your model is containing packages of that nature?");
			}
			if (o.isInformative()) {
				++_currentScopeInformativeCounter.classCount;
			}
			if (!o.getTaggedValues().isEmpty()) {
				addElementWithTag(o);
			}
		}
		return classes.size();
	}

	private int initAttributes(UmlModel model, OwningWg owner) {
		List<UmlAttribute> attributes = AbstractUmlObject.collectForScope(model.getAttributes(),
				EnumSet.of(owner));
		for (UmlKind kind : UmlAttribute.getKinds(_nature)) {
			_attributes.put(kind, new ArrayList<UmlAttribute>());
		}
		for (UmlAttribute o : attributes) {
			_attributes.get(o.getKind()).add(o);
			if (o.isInformative()) {
				++_currentScopeInformativeCounter.attributeCount;
			}
			if (!o.getTaggedValues().isEmpty()) {
				addElementWithTag(o);
			}
		}
		return attributes.size();
	}

	private int initAssociations(UmlModel model, OwningWg owner) {
		List<UmlAssociation> associations = AbstractUmlObject
				.collectForScope(model.getAssociations(), EnumSet.of(owner));
		for (UmlKind kind : UmlAssociation.getKinds(_nature)) {
			_associations.put(kind, new ArrayList<UmlAssociation>());
		}
		for (UmlAssociation o : associations) {
			_associations.get(o.getKind()).add(o);
			if (o.isInformative()) {
				++_currentScopeInformativeCounter.associationCount;
			}
			if (!o.getTaggedValues().isEmpty()) {
				addElementWithTag(o);
			}
			if (!o.getSourceEnd().getTaggedValues().isEmpty()) {
				addElementWithTag(o.getSourceEnd());
			}
			if (!o.getTargetEnd().getTaggedValues().isEmpty()) {
				addElementWithTag(o.getTargetEnd());
			}
		}
		return associations.size();
	}

	private int initOperations(UmlModel model, OwningWg owner) {
		List<UmlOperation> operations = AbstractUmlObject.collectForScope(model.getOperations(),
				EnumSet.of(owner));
		for (UmlKind kind : UmlOperation.getKinds(_nature)) {
			_operations.put(kind, new ArrayList<UmlOperation>());
		}
		for (UmlOperation o : operations) {
			_operations.get(o.getKind()).add(o);
			if (o.isInformative()) {
				++_currentScopeInformativeCounter.operationCount;
			}
			if (!o.getTaggedValues().isEmpty()) {
				addElementWithTag(o);
			}
		}
		return operations.size();
	}

	private int initDependencies(UmlModel model, OwningWg owner) {
		List<UmlDependency> dependencies = AbstractUmlObject
				.collectForScope(model.getDependencies(), EnumSet.of(owner));
		for (UmlKind kind : UmlDependency.getKinds(_nature)) {
			_dependencies.put(kind, new ArrayList<UmlDependency>());
		}
		for (UmlDependency o : dependencies) {
			_dependencies.get(o.getKind()).add(o);
			if (o.isInformative()) {
				++_currentScopeInformativeCounter.dependencyCount;
			}
			if (!o.getTaggedValues().isEmpty()) {
				addElementWithTag(o);
			}
		}
		return dependencies.size();
	}

	private int initDiagrams(UmlModel model, OwningWg owner) {
		List<UmlDiagram> diagrams = AbstractUmlObject.collectForScope(model.getDiagrams(),
				EnumSet.of(owner));
		for (UmlKind kind : UmlDiagram.getKinds(_nature)) {
			_diagrams.put(kind, new ArrayList<UmlDiagram>());
		}
		for (UmlDiagram o : diagrams) {
			_diagrams.get(o.getKind()).add(o);
			if (o.isInformative()) {
				++_currentScopeInformativeCounter.diagramCount;
			}
			if (!o.getTaggedValues().isEmpty()) {
				addElementWithTag(o);
			}
		}
		return diagrams.size();
	}

	private void addElementWithTag(UmlObject o) {
		AbstractUmlObject.saveTags(o, _tags);
		_counter.tagCount = _tags.size();
	}

	// ---------------------

	public final OwningWg getOwner() {
		return _owner;
	}

	public Nature getNature() {
		return _nature;
	}

	public Map<UmlKind, List<UmlPackage>> getPackages() {
		return _packages;
	}

	public Map<UmlKind, List<UmlClass>> getClasses() {
		return _classes;
	}

	public Map<UmlKind, List<UmlAttribute>> getAttributes() {
		return _attributes;
	}

	public Map<UmlKind, List<UmlAssociation>> getAssociations() {
		return _associations;
	}

	public Map<UmlKind, List<UmlOperation>> getOperations() {
		return _operations;
	}

	public Map<UmlKind, List<UmlDependency>> getDependencies() {
		return _dependencies;
	}

	public Map<UmlKind, List<UmlDiagram>> getDiagrams() {
		return _diagrams;
	}

	public Map<String, Set<UmlObject>> getTags() {
		return _tags;
	}

	public int getPackageCount() {
		return _counter.packageCount;
	}

	public int getClassCount() {
		return _counter.classCount;
	}

	public int getAttributeCount() {
		return _counter.attributeCount;
	}

	public int getAssociationCount() {
		return _counter.associationCount;
	}

	public int getOperationCount() {
		return _counter.operationCount;
	}

	public int getDependencyCount() {
		return _counter.dependencyCount;
	}

	public int getDiagramCount() {
		return _counter.diagramCount;
	}

	public int getTagNameCount() {
		return _counter.tagCount;
	}

	public String toHtml() {
		StringBuilder s = new StringBuilder();
		for (String line : formatLines()) {
			s.append("<p>").append(line).append("</p>");
		}
		return s.toString();
	}

	@Override
	public String toString() {
		StringBuilder s = new StringBuilder();
		for (String line : formatLines()) {
			s.append(line).append(Util.NL);
		}
		return s.toString();
	}

	List<String> formatLines() {
		List<String> result = new ArrayList<String>();

		result.add("[" + getOwner().toString() + "]");
		result.add(getPackageCount() + " packages (" + _currentScopeCounter.packageCount + "/"
				+ _totalModelCounter.packageCount + ")"
				+ deduceInf(_currentScopeInformativeCounter.packageCount) + ":");
		for (Entry<UmlKind, List<UmlPackage>> entry : _packages.entrySet()) {
			if (!entry.getValue().isEmpty()) {
				result.add("  " + entry.getValue().size() + " " + entry.getKey().getDesc());
			}
		}

		result.add(getClassCount() + " classes (" + _currentScopeCounter.classCount + "/"
				+ _totalModelCounter.classCount + ")"
				+ deduceInf(_currentScopeInformativeCounter.classCount) + ":");
		for (Entry<UmlKind, List<UmlClass>> entry : _classes.entrySet()) {
			if (!entry.getValue().isEmpty()) {
				result.add("  " + entry.getValue().size() + " " + entry.getKey().getDesc());
			}
		}
		result.add(getAttributeCount() + " attributes (" + _currentScopeCounter.attributeCount + "/"
				+ _totalModelCounter.attributeCount + ")"
				+ deduceInf(_currentScopeInformativeCounter.attributeCount) + ":");
		for (Entry<UmlKind, List<UmlAttribute>> entry : _attributes.entrySet()) {
			if (!entry.getValue().isEmpty()) {
				result.add("  " + entry.getValue().size() + " " + entry.getKey().getDesc());
			}
		}
		result.add(getAssociationCount() + " associations (" + _currentScopeCounter.associationCount
				+ "/" + _totalModelCounter.associationCount + ")"
				+ deduceInf(_currentScopeInformativeCounter.associationCount) + ":");
		for (Entry<UmlKind, List<UmlAssociation>> entry : _associations.entrySet()) {
			if (!entry.getValue().isEmpty()) {
				result.add("  " + entry.getValue().size() + " " + entry.getKey().getDesc());
			}
		}
		result.add(getOperationCount() + " operations (" + _currentScopeCounter.operationCount + "/"
				+ _totalModelCounter.operationCount + ")"
				+ deduceInf(_currentScopeInformativeCounter.operationCount) + ":");
		for (Entry<UmlKind, List<UmlOperation>> entry : _operations.entrySet()) {
			if (!entry.getValue().isEmpty()) {
				result.add("  " + entry.getValue().size() + " " + entry.getKey().getDesc());
			}
		}

		result.add(getDependencyCount() + " dependencies (" + _currentScopeCounter.dependencyCount
				+ "/" + _totalModelCounter.dependencyCount + ")"
				+ deduceInf(_currentScopeInformativeCounter.dependencyCount) + ":");
		for (Entry<UmlKind, List<UmlDependency>> entry : _dependencies.entrySet()) {
			if (!entry.getValue().isEmpty()) {
				result.add("  " + entry.getValue().size() + " " + entry.getKey().getDesc());
			}
		}

		result.add(getDiagramCount() + " diagrams (" + _currentScopeCounter.diagramCount + "/"
				+ _totalModelCounter.diagramCount + ")"
				+ deduceInf(_currentScopeInformativeCounter.diagramCount) + ":");
		for (Entry<UmlKind, List<UmlDiagram>> entry : _diagrams.entrySet()) {
			if (!entry.getValue().isEmpty()) {
				result.add("  " + entry.getValue().size() + " " + entry.getKey().getDesc());
			}
		}

		result.add(getTagNameCount() + " tag names (" + _currentScopeCounter.tagCount + "/"
				+ _totalModelCounter.tagCount + "):");
		for (Entry<String, Set<UmlObject>> entry : _tags.entrySet()) {
			if (!entry.getValue().isEmpty()) {
				result.add("  " + entry.getValue().size() + " " + entry.getKey());
			}
		}
		return result;
	}

	private String deduceInf(int infCount) {
		return (infCount > 0) ? (" - " + infCount + " informative") : "";
	}
}
