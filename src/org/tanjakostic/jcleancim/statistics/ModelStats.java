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
import java.util.EnumSet;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.tanjakostic.jcleancim.common.Nature;
import org.tanjakostic.jcleancim.common.OwningWg;
import org.tanjakostic.jcleancim.model.AbstractUmlObject;
import org.tanjakostic.jcleancim.model.NameDecomposition;
import org.tanjakostic.jcleancim.model.NamespaceInfo;
import org.tanjakostic.jcleancim.model.UmlAssociation;
import org.tanjakostic.jcleancim.model.UmlAssociationEnd;
import org.tanjakostic.jcleancim.model.UmlAttribute;
import org.tanjakostic.jcleancim.model.UmlClass;
import org.tanjakostic.jcleancim.model.UmlDependency;
import org.tanjakostic.jcleancim.model.UmlDiagram;
import org.tanjakostic.jcleancim.model.UmlModel;
import org.tanjakostic.jcleancim.model.UmlObject;
import org.tanjakostic.jcleancim.model.UmlOperation;
import org.tanjakostic.jcleancim.model.UmlPackage;
import org.tanjakostic.jcleancim.model.VersionInfo;
import org.tanjakostic.jcleancim.util.Util;

/**
 * Statistics (counts) of different kinds of elements in the model per nature and per owner.
 *
 * @author tatjana.kostic@ieee.org
 * @version $Id: ModelStats.java 31 2019-12-08 01:19:54Z dev978 $
 */
public class ModelStats {
	private static final Logger _logger = Logger.getLogger(ModelStats.class.getName());

	// FIXME: make configurable
	private static final int DO_DESC_CHAR_COUNT = 80;

	private final Counter _scopedCounter;
	private final Map<OwningWg, Collection<UmlPackage>> _scopedPackages;
	private final Map<OwningWg, Collection<UmlClass>> _scopedClasses;
	private final Map<OwningWg, Collection<UmlAttribute>> _scopedAttributes;
	private final Map<OwningWg, Collection<UmlAssociation>> _scopedAssociations;
	private final Map<OwningWg, Collection<UmlOperation>> _scopedOperations;
	private final Map<OwningWg, Collection<UmlDependency>> _scopedDependencies;
	private final Map<OwningWg, Collection<UmlDiagram>> _scopedDiagrams;
	private final Map<OwningWg, Map<String, Set<UmlObject>>> _scopedTags;

	private final Map<Nature, Collection<StatsPerOwner>> _statsPerNature;
	private final UmlModel _model;

	/**
	 * Constructor.
	 *
	 * @param model
	 */
	public ModelStats(UmlModel model) {
		_scopedCounter = new Counter();

		EnumSet<OwningWg> scopes = model.getCfg().getValidationScope();
		_scopedPackages = AbstractUmlObject.classifyPerScope(model.getPackages(), scopes);
		_scopedCounter.packageCount = AbstractUmlObject.collectForScope(model.getPackages(), scopes)
				.size();

		_scopedClasses = AbstractUmlObject.classifyPerScope(model.getClasses(), scopes);
		_scopedCounter.classCount = AbstractUmlObject.collectForScope(model.getClasses(), scopes)
				.size();

		_scopedAttributes = AbstractUmlObject.classifyPerScope(model.getAttributes(), scopes);
		_scopedCounter.attributeCount = AbstractUmlObject
				.collectForScope(model.getAttributes(), scopes).size();

		_scopedAssociations = AbstractUmlObject.classifyPerScope(model.getAssociations(), scopes);
		_scopedCounter.associationCount = AbstractUmlObject
				.collectForScope(model.getAssociations(), scopes).size();

		_scopedOperations = AbstractUmlObject.classifyPerScope(model.getOperations(), scopes);
		_scopedCounter.operationCount = AbstractUmlObject
				.collectForScope(model.getOperations(), scopes).size();

		_scopedDependencies = AbstractUmlObject.classifyPerScope(model.getDependencies(), scopes);
		_scopedCounter.dependencyCount = AbstractUmlObject
				.collectForScope(model.getDependencies(), scopes).size();

		_scopedDiagrams = AbstractUmlObject.classifyPerScope(model.getDiagrams(), scopes);
		_scopedCounter.diagramCount = AbstractUmlObject.collectForScope(model.getDiagrams(), scopes)
				.size();

		_scopedTags = AbstractUmlObject.classifyPerScopePerTag(model.getTags(), scopes);
		_scopedCounter.tagCount = AbstractUmlObject.classifyPerTag(model.getTags(), scopes).size();

		_statsPerNature = initStatsPerNature(model);
		_model = model;
	}

	private Map<Nature, Collection<StatsPerOwner>> initStatsPerNature(UmlModel model) {
		Counter modelCounter = initModelCounter(model);

		Map<Nature, Collection<StatsPerOwner>> statsPerNature = new LinkedHashMap<Nature, Collection<StatsPerOwner>>();
		for (Nature nature : Nature.values()) {
			statsPerNature.put(nature, new LinkedHashSet<StatsPerOwner>());
		}

		for (Entry<OwningWg, Collection<UmlPackage>> entry : _scopedPackages.entrySet()) {
			OwningWg owner = entry.getKey();
			Collection<UmlPackage> packages = entry.getValue();
			if (!packages.isEmpty()) {
				Nature nature = owner.getNature();
				Collection<StatsPerOwner> stats = statsPerNature.get(nature);
				if (stats == null) {
					stats = new ArrayList<StatsPerOwner>();
					statsPerNature.put(nature, stats);
				}
				stats.add(new StatsPerOwner(model, owner, nature, _scopedCounter, modelCounter));
			}
		}
		return statsPerNature;
	}

	private Counter initModelCounter(UmlModel model) {
		Counter modelCounter = new Counter();
		modelCounter.packageCount = model.getPackages().size();
		modelCounter.classCount = model.getClasses().size();
		modelCounter.attributeCount = model.getAttributes().size();
		modelCounter.associationCount = model.getAssociations().size();
		modelCounter.operationCount = model.getOperations().size();
		modelCounter.dependencyCount = model.getDependencies().size();
		modelCounter.diagramCount = model.getDiagrams().size();
		modelCounter.tagCount = model.getTags().size();
		return modelCounter;
	}

	// ------------------------------

	public Map<OwningWg, Collection<UmlPackage>> getScopedPackages() {
		return _scopedPackages;
	}

	public int getPackageCount() {
		return _scopedCounter.packageCount;
	}

	public Map<OwningWg, Collection<UmlClass>> getScopedClasses() {
		return _scopedClasses;
	}

	public int getClassCount() {
		return _scopedCounter.classCount;
	}

	public Map<OwningWg, Collection<UmlAttribute>> getScopedAttributes() {
		return _scopedAttributes;
	}

	public int getAttributeCount() {
		return _scopedCounter.attributeCount;
	}

	public Map<OwningWg, Collection<UmlAssociation>> getScopedAssociations() {
		return _scopedAssociations;
	}

	public int getAssociationCount() {
		return _scopedCounter.associationCount;
	}

	public Map<OwningWg, Collection<UmlOperation>> getScopedOperations() {
		return _scopedOperations;
	}

	public int getOperationCount() {
		return _scopedCounter.operationCount;
	}

	public Map<OwningWg, Collection<UmlDependency>> getScopedDependencies() {
		return _scopedDependencies;
	}

	public int getDependencyCount() {
		return _scopedCounter.dependencyCount;
	}

	public Map<OwningWg, Collection<UmlDiagram>> getScopedDiagrams() {
		return _scopedDiagrams;
	}

	public int getDiagramCount() {
		return _scopedCounter.diagramCount;
	}

	public Map<OwningWg, Map<String, Set<UmlObject>>> getScopedTags() {
		return _scopedTags;
	}

	public int getTagNamesCount() {
		return _scopedCounter.tagCount;
	}

	public Map<Nature, Collection<StatsPerOwner>> getStatsPerNature() {
		return _statsPerNature;
	}

	public UmlModel getModel() {
		return _model;
	}

	// ------------------------

	public void logStats() {
		_logger.info("");
		_logger.info("====== Stats per nature and per owner for " + getPackageCount() + " packages"
				+ " (of " + _model.getPackages().size() + "):");

		for (String line : formatLines()) {
			_logger.info(line);
		}
	}

	private Collection<String> formatLines() {
		List<String> result = new ArrayList<String>();
		for (Entry<Nature, Collection<StatsPerOwner>> entry : _statsPerNature.entrySet()) {
			Nature nature = entry.getKey();
			Collection<StatsPerOwner> stats = entry.getValue();
			if (!stats.isEmpty()) {
				result.add("------ " + nature.toString() + " statistics:");
				for (StatsPerOwner s : stats) {
					result.addAll(s.formatLines());
					result.add("");
				}
				result.add("");
			}
		}

		return result;
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

	// ========================================================

	public void logPackages(Level level) {
		Util.logCollection(level, getModel().getPackages(), "packages");
	}

	public void logClasses(Level level) {
		Util.logCollection(level, getModel().getClasses(), "classes");
	}

	public void logOperations(Level level) {
		Util.logCollection(level, getModel().getOperations(), "operations");
	}

	public void logNormativeClasses(Level level, EnumSet<OwningWg> wgs) {
		Collection<UmlClass> foundClasses = getModel().findClasses(wgs,
				EnumSet.allOf(UmlClass.CimKind.class), EnumSet.noneOf(UmlClass.Iec61850Kind.class),
				true, false);
		Util.logCollection(level, foundClasses, "normative " + wgs.toString() + " classes");
	}

	public void logNormativeAssociationsWithWgClasses(Level level, OwningWg wg) {
		Collection<UmlAssociation> wgAssociations = getModel().findAssociations(EnumSet.of(wg),
				EnumSet.allOf(UmlAssociationEnd.Kind.class), true, false);
		String what = String.format("normative associations with %s classes", wg.toString());
		Util.logCollection(level, wgAssociations, what);
	}

	public void logAggregationsWithWgClasses(Level level, OwningWg wg) {
		Collection<UmlAssociation> wgAggregations = getModel().findAssociations(EnumSet.of(wg),
				EnumSet.of(UmlAssociationEnd.Kind.AGGREG), true, true);
		String what = String.format("aggregations with %s classes", wg.toString());
		Util.logCollection(level, wgAggregations, what);
	}

	public void logCimNoncimAssociations(Level level) {
		Collection<UmlAssociation> mappings = getModel().findCimNoncimAssociations();
		String what = "associations between CIM and nonCIM packages";
		Util.logCollection(level, mappings, what);
	}

	public void logClassesWithAttributeConstraints(Level level) {
		Collection<UmlClass> classes = getModel().findClassesWithConstraints();
		Util.logCollection(level, classes, "classes with constraints");
	}

	/** Note: Attribute constraints can be derived from class or own. */
	public void logAttributesWithConstraints(Level level) {
		Collection<UmlAttribute> attrs = getModel().findAttributesWithConstraints();
		Util.logCollection(level, attrs, "attributes with constraints");
	}

	public void logMultivaluedAttributes(Level level) {
		Collection<UmlAttribute> attrs = getModel().findMultivaluedAttributes();
		Util.logCollection(level, attrs, "multi-valued attributes");
	}

	public void logNamespaceInfos(Level level) {
		Collection<NamespaceInfo> nsInfos = getModel().getNamespaceInfos();
		Util.logCollection(level, nsInfos, "name spaces");
	}

	public void logVersionInfos(Level level) {
		Collection<VersionInfo> verInfos = getModel()
				.getVersionInfos(EnumSet.allOf(OwningWg.class));
		Util.logCollection(level, verInfos, "versions");
	}

	/**
	 * Using tagged values as keys, logs detailed list of referencing objects, except for those tags
	 * that are configured to be ignored.
	 */
	public void logTaggedValues(Level level) {
		Map<String, Set<UmlObject>> filteredTags = new HashMap<String, Set<UmlObject>>(
				getModel().getTags());
		List<String> statisticsTagsToIgnore = new ArrayList<String>(
				getModel().getCfg().getStatisticsTagsToIgnore());

		String what = "tag names";
		if (!statisticsTagsToIgnore.isEmpty()) {
			what += " (not listed objects using "
					+ Util.concatCharSeparatedTokens(",", statisticsTagsToIgnore) + ")";
		}
		for (String tagToIgnore : statisticsTagsToIgnore) {
			filteredTags.put(tagToIgnore, new HashSet<UmlObject>());
		}
		Util.logMap(level, filteredTags, what);
	}

	/**
	 * (IEC61850) For every DO, logs abbreviations used; opposite to
	 * {@link #logAbbreviatedTermUsage(Level)}.
	 */
	public void logDONameDecomposition(Level level) {
		Collection<UmlAttribute> doAttrs = getModel().findDOAttributes();
		Map<String, String> sortedAbbreviatedTerms = getModel()
				.getAbbreviatedTermsSortedPerDecreasingLength();

		Collection<String> decomposedNames = new ArrayList<>();
		for (UmlAttribute a : doAttrs) {
			String reference = a.getOwner().toString() + " " + a.getQualifiedName();
			String desc = Util.truncateEnd(a.getDescription().text, DO_DESC_CHAR_COUNT);
			NameDecomposition nd = a.getNameDecomposition(sortedAbbreviatedTerms);
			String txt = String.format("%s (\"%s\"): %s", reference, desc, nd.toString());
			decomposedNames.add(txt);
		}
		Util.logCollection(level, decomposedNames, "DO name decompositions");
	}

	/**
	 * (IEC61850) For every abbreviated term, logs DOs using it; opposite to
	 * {@link #logDONameDecomposition(Level)}.
	 */
	public void logAbbreviatedTermUsage(Level level) {
		Collection<UmlAttribute> doAttrs = getModel().findDOAttributes();
		Map<String, String> sortedAbbreviatedTerms = getModel()
				.getAbbreviatedTermsSortedPerDecreasingLength();

		// initialise keys (abbreviated terms, sorted per decreasing length):
		Map<String, List<String>> termsWithTheirUsers = new TreeMap<>();
		for (Entry<String, String> entry : sortedAbbreviatedTerms.entrySet()) {
			termsWithTheirUsers.put(entry.getKey(), new ArrayList<String>());
		}

		for (UmlAttribute a : doAttrs) {
			String reference = a.getOwner().toString() + " " + a.getQualifiedName();
			List<Map<String, String>> nd = a.getNameDecomposition(sortedAbbreviatedTerms)
					.getDecomposedTerms();
			for (Map<String, String> decomposedTerms : nd) {
				for (Entry<String, String> entry : decomposedTerms.entrySet()) {
					String term = entry.getKey();
					if (termsWithTheirUsers.containsKey(term)) {
						termsWithTheirUsers.get(term).add(reference);
					}
				}
			}
		}
		Util.logMap(level, termsWithTheirUsers, "Abbreviated terms usage");
	}
}
