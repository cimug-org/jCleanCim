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

package org.tanjakostic.jcleancim.docgen.collector.impl;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;
import org.tanjakostic.jcleancim.docgen.collector.ModelFinder;
import org.tanjakostic.jcleancim.model.NamespaceInfo;
import org.tanjakostic.jcleancim.model.TextDescription;
import org.tanjakostic.jcleancim.model.UmlAttribute;
import org.tanjakostic.jcleancim.model.UmlClass;
import org.tanjakostic.jcleancim.model.UmlDiagram;
import org.tanjakostic.jcleancim.model.UmlModel;
import org.tanjakostic.jcleancim.model.UmlPackage;

/**
 * Provides methods for a writer to retreive UML model elements that are referenced by placeholders
 * in the input template document.
 * <p>
 * This implementation of {@link ModelFinder} relies on the full in-memory model, and is convenient
 * for document generation.
 *
 * @author tatjana.kostic@ieee.org
 * @version $Id: ModelFinderImpl.java 23 2019-08-25 21:04:58Z dev978 $
 */
public class ModelFinderImpl implements ModelFinder {
	private static final Logger _logger = Logger.getLogger(ModelFinderImpl.class.getName());

	private final UmlModel _model;

	/**
	 * Constructor.
	 */
	public ModelFinderImpl(UmlModel model) {
		_model = model;
	}

	@Override
	public String findAttributeValue(String className, String attributeName) {
		List<String> initValues = new ArrayList<String>();
		Collection<UmlClass> foundClasses = _model.findClasses(className);
		for (UmlClass c : foundClasses) {
			Set<UmlAttribute> attributes = c.findAttributes(attributeName);
			for (UmlAttribute a : attributes) {
				initValues.add(a.getInitValue());
			}
		}
		if (!initValues.isEmpty()) {
			if (initValues.size() > 1) {
				_logger.warn(String.format(
						"Multiple pairs %s.%s found - returning initial value for the first.",
						className, attributeName));
			}
			return initValues.get(0);
		}
		return null;
	}

	@Override
	public File findDiagramFile(String containerName, String diagramName) {
		UmlDiagram d = findDiagram(containerName, diagramName);
		return (d != null) ? d.getPic() : null;
	}

	@Override
	public TextDescription findDiagramNote(String containerName, String diagramName) {
		UmlDiagram d = findDiagram(containerName, diagramName);
		return (d != null) ? d.getDescription() : null;
	}

	private UmlDiagram findDiagram(String containerName, String diagramName) {
		List<UmlDiagram> diagrams = new ArrayList<UmlDiagram>(
				_model.findDiagrams(containerName, diagramName, true, true));
		if (diagrams.isEmpty()) {
			_logger.warn(String.format("No diagram %s::%s found - returning null.", containerName,
					diagramName));
			return null;
		}

		if (diagrams.size() > 1) {
			_logger.warn(String.format("Found multiple diagrams %s::%s - returning first.",
					containerName, diagramName));
		}
		for (UmlDiagram d : diagrams) {
			if (d.getContainer() instanceof UmlPackage) {
				return d;
			}
		}
		return diagrams.get(0);
	}

	@Override
	public String findClassName(String packageName, String className) {
		Collection<UmlClass> classes = findAllWithNameANDlogWhenSizeDifferentThanOne(className);

		for (UmlClass c : classes) {
			if (c.getContainingPackage().getName().equals(packageName)) {
				return c.getName();
			}
		}
		return null;
	}

	@Override
	public String findIec61850NsName(String className) {
		Collection<UmlClass> classes = findAllWithNameANDlogWhenSizeDifferentThanOne(className);

		if (!classes.isEmpty()) {
			UmlClass c = classes.iterator().next();
			NamespaceInfo nsInfo = c.getContainingPackage().getNamespaceInfo();
			if (nsInfo != null) {
				return nsInfo.getName();
			}
		}
		return null;
	}

	private Collection<UmlClass> findAllWithNameANDlogWhenSizeDifferentThanOne(String className) {
		Collection<UmlClass> classes = _model.findClasses(className);
		if (classes.isEmpty()) {
			_logger.warn(String.format("No class %s found - returning null.", className));
		}
		if (classes.size() > 1) {
			_logger.warn(String.format("Found multiple classes %s - returning first.", className));
		}
		return classes;
	}
}
