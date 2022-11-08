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
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;
import org.tanjakostic.jcleancim.common.Config;
import org.tanjakostic.jcleancim.model.AbstractUmlObject;
import org.tanjakostic.jcleancim.model.UmlAssociation;
import org.tanjakostic.jcleancim.model.UmlClass;
import org.tanjakostic.jcleancim.model.UmlDependency;
import org.tanjakostic.jcleancim.model.UmlModel;
import org.tanjakostic.jcleancim.model.UmlPackage;
import org.tanjakostic.jcleancim.util.Util;

/**
 * Provides methods to get and log links among packages having different owners (WGs), as well as
 * links among packages within the same owner. Actual dependencies are calculated based on
 * inheritance, associations, dependencies (drawn by hand in the model), attribute types, and
 * arguments and exceptions in operations.
 *
 * @author tatjana.kostic@ieee.org
 * @version $Id: CrossPackageStats.java 23 2019-08-25 21:04:58Z dev978 $
 */
public class CrossPackageStats {
	private static final Logger _logger = Logger.getLogger(CrossPackageStats.class.getName());

	// TODO
	// public enum DependencyKind {
	// superclass,
	// attributeType,
	// operationParams,
	// association,
	// classUmlDependency,
	// packageUmlDependency
	// }
	//
	// // these can be deduced from efferent classes; we could then also deduce for packages
	// public static class EffectiveDependency {
	// private UmlClass _sourceClass;
	// private UmlClass _targetClass;
	// }

	private static final String CIM_DOMAIN_PACKAGE = "Domain";
	private static final String CIM_ID_OBJECT_CLASS = "IdentifiedObject";

	private final Config _cfg;
	private final int _packageCount;
	private final Collection<UmlPackage> _scopedPackages;
	private final Collection<UmlClass> _scopedClasses;
	private final Collection<UmlDependency> _scopedDeps;
	private final Collection<UmlAssociation> _scopedAssociations;

	private final CrossPackageStatsData _xownerData;
	private final CrossPackageStatsData _xpackageData;

	/**
	 * Constructor. Calculates and stores all dependencies.
	 *
	 * @param model
	 */
	public CrossPackageStats(UmlModel model) {
		_cfg = model.getCfg();
		_packageCount = model.getPackages().size();
		_scopedPackages = AbstractUmlObject.collectForScope(model.getPackages(),
				_cfg.getValidationScope());
		_scopedClasses = AbstractUmlObject.collectForScope(model.getClasses(),
				_cfg.getValidationScope());
		_scopedDeps = AbstractUmlObject.collectForScope(model.getDependencies(),
				_cfg.getValidationScope());
		_scopedAssociations = AbstractUmlObject.collectForScope(model.getAssociations(),
				_cfg.getValidationScope());

		_xownerData = new CrossPackageStatsData(_cfg);
		_xpackageData = new CrossPackageStatsData(_cfg);

		for (UmlClass c : _scopedClasses) {
			addClassPairs(c, c.getSuperclasses(), _xownerData._classInhPairs,
					_xpackageData._classInhPairs, _cfg.isStatisticsCimIgnoreIdObjectInheritance(),
					false);
			addClassPairs(c, c.getAttributeEfferentClasses(), _xownerData._classAttrPairs,
					_xpackageData._classAttrPairs, false,
					_cfg.isStatisticsCimIgnoreDomainClassAttributes());
			addClassPairs(c, c.getOperationEfferentClasses(), _xownerData._classOperPairs,
					_xpackageData._classOperPairs, false, false);
			addClassPairs(c, c.collectDependencyEfferentClasses(), _xownerData._classDeps,
					_xpackageData._classDeps, false, false);
		}
		addClassPairsFromAssocs(_scopedAssociations, _xownerData._classAssocs,
				_xpackageData._classAssocs);

		addPackagePairsFromDeps(_scopedDeps, _xownerData._packageDeps, _xpackageData._packageDeps);
	}

	private void addClassPairs(UmlClass c, Collection<UmlClass> relatedClasses,
			Collection<Collection<UmlClass>> collectedXownerClasses,
			Collection<Collection<UmlClass>> collectedXpackageClasses, boolean ignoreIdObjInh,
			boolean ignoreDomainAttr) {
		UmlPackage p = c.getContainingPackage();
		for (UmlClass otherClass : relatedClasses) {
			UmlPackage otherPackage = otherClass.getContainingPackage();
			if (ignoreDomainAttr && CIM_DOMAIN_PACKAGE.equals(otherPackage.getName())) {
				continue;
			}
			if (ignoreIdObjInh && CIM_ID_OBJECT_CLASS.equals(otherClass.getName())) {
				continue;
			}
			if (!p.getId().equals(otherPackage.getId())) {
				List<UmlClass> pair = new ArrayList<UmlClass>();
				pair.add(c);
				pair.add(otherClass);
				if (p.getOwner() != otherPackage.getOwner()) {
					collectedXownerClasses.add(pair);
				} else {
					collectedXpackageClasses.add(pair);
				}
			}
		}
	}

	private void addClassPairsFromAssocs(Collection<UmlAssociation> assocs,
			Collection<Collection<UmlClass>> xownerAssocs,
			Collection<Collection<UmlClass>> xpackageAssocs) {
		for (UmlAssociation a : assocs) {
			UmlClass source = a.getSource();
			UmlClass target = a.getTarget();
			if (!source.getContainingPackage().getId()
					.equals(target.getContainingPackage().getId())) {
				List<UmlClass> pair = new ArrayList<UmlClass>();
				pair.add(source);
				pair.add(target);
				if (source.getOwner() != target.getOwner()) {
					xownerAssocs.add(pair);
				} else {
					xpackageAssocs.add(pair);
				}
			}
		}
	}

	private void addPackagePairsFromDeps(Collection<UmlDependency> pckDeps,
			Collection<Collection<UmlPackage>> xownerPackageDeps,
			Collection<Collection<UmlPackage>> xPackagePackageDeps) {
		for (UmlDependency d : pckDeps) {
			if (!(d.getSource() instanceof UmlPackage)) {
				continue; // interClass dependency ignored
			}
			UmlPackage p1 = (UmlPackage) d.getSource();
			UmlPackage p2 = (UmlPackage) d.getTarget();
			if (!p1.getId().equals(p2.getId())) {
				Set<UmlPackage> pair = new HashSet<UmlPackage>();
				pair.add(p1);
				pair.add(p2);
				if (p1.getOwner() != p2.getOwner()) {
					xownerPackageDeps.add(pair);
				} else {
					xPackagePackageDeps.add(pair);
				}
			}
		}
	}

	/**
	 * Logs statistics on links among packages.
	 */
	public void logStats() {
		_logger.info("");
		_logger.info("====== Cross-package stats for " + _scopedPackages.size() + " packages (of "
				+ _packageCount + "):");

		for (String line : formatCrossOwnerLines()) {
			_logger.info(line);
		}
		for (String line : formatWithinOwnerLines()) {
			_logger.trace(line);
		}
	}

	private List<String> formatCrossOwnerLines() {
		List<String> result = new ArrayList<String>();
		result.add("Cross-owner links:");
		result.addAll(_xownerData.formatLines());
		return result;
	}

	private List<String> formatWithinOwnerLines() {
		List<String> result = new ArrayList<String>();
		result.add("Cross-package links (within same owner):");
		result.addAll(_xpackageData.formatLines());
		return result;
	}

	@Override
	public String toString() {
		StringBuilder s = new StringBuilder();
		for (String line : formatCrossOwnerLines()) {
			s.append(line).append(Util.NL);
		}
		for (String line : formatWithinOwnerLines()) {
			s.append(line).append(Util.NL);
		}
		return s.toString();
	}

	/**
	 * Helper class.
	 *
	 * @author tatjana.kostic@ieee.org
	 * @version $Id: CrossPackageStats.java 23 2019-08-25 21:04:58Z dev978 $
	 */
	private static class CrossPackageStatsData {
		// whether util method should include [WG] when returning names:
		private static final boolean INCLUDE_OWNER = true;
		private static final String INDENT = "  ";
		private static final String INDENT2 = "    ";

		final Config _cfg;

		final Collection<Collection<UmlClass>> _classInhPairs;
		final Collection<Collection<UmlClass>> _classAttrPairs;
		final Collection<Collection<UmlClass>> _classOperPairs;
		final Collection<Collection<UmlClass>> _classAssocs;
		final Collection<Collection<UmlClass>> _classDeps;
		final Collection<Collection<UmlPackage>> _packageDeps;

		CrossPackageStatsData(Config cfg) {
			_cfg = cfg;

			_classInhPairs = new LinkedHashSet<Collection<UmlClass>>();
			_classAttrPairs = new LinkedHashSet<Collection<UmlClass>>();
			_classOperPairs = new LinkedHashSet<Collection<UmlClass>>();
			_classAssocs = new ArrayList<Collection<UmlClass>>(); // set does not keep duplicates!
			_classDeps = new LinkedHashSet<Collection<UmlClass>>();

			_packageDeps = new LinkedHashSet<Collection<UmlPackage>>();
		}

		private List<String> formatLines() {
			List<String> result = new ArrayList<String>();

			result.add(INDENT + _classInhPairs.size() + " class inheritance:");
			if (_cfg.isStatisticsCimIgnoreIdObjectInheritance()) {
				result.add(INDENT + "(excluded inheritance from IdentifiedObject)");
			}
			for (Collection<UmlClass> pair : _classInhPairs) {
				result.add(INDENT2 + AbstractUmlObject.collectQNames(pair, INCLUDE_OWNER));
			}

			result.add(INDENT + _classAttrPairs.size() + " class's attribute types:");
			if (_cfg.isStatisticsCimIgnoreDomainClassAttributes()) {
				result.add(INDENT + "(excluded types from Domain package)");
			}
			for (Collection<UmlClass> pair : _classAttrPairs) {
				result.add(INDENT2 + AbstractUmlObject.collectQNames(pair, INCLUDE_OWNER));
			}

			result.add(INDENT + _classOperPairs.size()
					+ " class's operation parameters and exceptions:");
			for (Collection<UmlClass> pair : _classOperPairs) {
				result.add(INDENT2 + AbstractUmlObject.collectQNames(pair, INCLUDE_OWNER));
			}

			result.add(INDENT + _classDeps.size() + " class-to-class (hand-drawn) dependency:");
			for (Collection<UmlClass> pair : _classDeps) {
				result.add(INDENT2 + AbstractUmlObject.collectQNames(pair, INCLUDE_OWNER));
			}

			result.add(INDENT + _classAssocs.size() + " class-to-class associations:");
			for (Collection<UmlClass> pair : _classAssocs) {
				result.add(INDENT2 + AbstractUmlObject.collectQNames(pair, INCLUDE_OWNER));
			}

			result.add(
					INDENT + _packageDeps.size() + " package-to-package (hand-drawn) dependency:");
			for (Collection<UmlPackage> pair : _packageDeps) {
				result.add(INDENT2 + AbstractUmlObject.collectQNames(pair, INCLUDE_OWNER));
			}
			return result;
		}

		@Override
		public String toString() {
			StringBuilder s = new StringBuilder();
			for (String line : formatLines()) {
				s.append(line).append(Util.NL);
			}
			return s.toString();
		}
	}
}
