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

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.log4j.Logger;
import org.tanjakostic.jcleancim.common.Config;
import org.tanjakostic.jcleancim.common.Nature;
import org.tanjakostic.jcleancim.docgen.collector.BookmarkRegistry;
import org.tanjakostic.jcleancim.docgen.collector.ClassDoc;
import org.tanjakostic.jcleancim.docgen.collector.DocCollector;
import org.tanjakostic.jcleancim.docgen.collector.DocgenConfig;
import org.tanjakostic.jcleancim.docgen.collector.FixedFormDocumentation;
import org.tanjakostic.jcleancim.docgen.collector.FreeFormDocumentation;
import org.tanjakostic.jcleancim.docgen.collector.ModelFinder;
import org.tanjakostic.jcleancim.docgen.collector.PackageDoc;
import org.tanjakostic.jcleancim.model.NamespaceInfo;
import org.tanjakostic.jcleancim.model.UmlAttribute;
import org.tanjakostic.jcleancim.model.UmlClass;
import org.tanjakostic.jcleancim.model.UmlModel;
import org.tanjakostic.jcleancim.model.UmlPackage;
import org.tanjakostic.jcleancim.util.ProgrammerErrorException;
import org.tanjakostic.jcleancim.util.Util;

/**
 * Default implementation of {@link DocCollector},.
 *
 * @author tatjana.kostic@ieee.org
 * @version $Id: DocCollectorImpl.java 21 2019-08-12 15:44:50Z dev978 $
 */
public class DocCollectorImpl implements DocCollector {
	private static final Logger _logger = Logger.getLogger(DocCollectorImpl.class.getName());

	private final DocgenConfig _docgenCfg;
	private final ModelFinder _modelFinder;
	private final boolean _fromUml;

	private FreeFormDocumentation _freeFormDoc;
	private FixedFormDocumentation _fixedFormDoc;

	/**
	 * Model package docs are indexed, the individual package docs contain their children
	 * recursively; we use this map for logging only. The retained docs, including packages with the
	 * same name, are in {@link #getNonSkippedPackageDocs()}.
	 */
	private final Map<String, PackageDoc> _modelPackageDocs = new LinkedHashMap<String, PackageDoc>();

	/**
	 * "Flattenned" docs for all retained packages. Lists are for the case of duplicate names.
	 * Filled by {@link PackageDoc} on creation.
	 */
	private final Map<String, List<PackageDoc>> _nonSkippedPackageDocs = new LinkedHashMap<String, List<PackageDoc>>();

	/**
	 * "Flattenned" docs for all retained classes; key is <i>qualified</i> class name. Lists are for
	 * the case of duplicate names. Filled by {@link PackageDoc} on creation.
	 */
	private final Map<String, List<ClassDoc>> _nonSkippedClassDocs = new LinkedHashMap<String, List<ClassDoc>>();

	/**
	 * "Categorised" docs for all retained packages. Lists are for the case of duplicate names.
	 * Filled by {@link PackageDoc} on creation.
	 */
	private final Map<Nature, Map<NamespaceInfo, Map<String, List<PackageDoc>>>> _nonSkippedNsPackageDocs = new LinkedHashMap<Nature, Map<NamespaceInfo, Map<String, List<PackageDoc>>>>();

	private final List<String> _skippedInfPackageQNames = new ArrayList<String>();

	private final BookmarkRegistry _bmRegistry;

	/**
	 * Constructs the collector from the UML model. After construction, call
	 * {@link #collect(UmlModel)} to obtain the input for document generation.
	 *
	 * @param model
	 */
	public DocCollectorImpl(UmlModel model) {
		this(model.getCfg(), new ModelFinderImpl(model), true,
				createAndPopulateBookmarkRegistry(model));
	}

	/**
	 * Returns fully populated bookmark registry; for every class and enumeration literal in the
	 * model, we create a bookmark ID to make the element and its documentation referenceable.
	 */
	private static BookmarkRegistry createAndPopulateBookmarkRegistry(UmlModel model) {
		BookmarkRegistry result = new BookmarkRegistry();
		for (UmlClass c : model.getClasses()) {
			result.getOrCreateBookmarkID(c);
		}
		for (UmlAttribute a : model.getAttributes()) {
			if (a.isLiteral()) {
				result.getOrCreateBookmarkID(a);
			}
		}
		return result;
	}

	/**
	 * Constructs the instance to manually (through API) add documentation for package and other UML
	 * objects.
	 *
	 * @param cfg
	 * @param modelFinder
	 */
	public DocCollectorImpl(Config cfg, ModelFinder modelFinder) {
		this(cfg, modelFinder, false, new BookmarkRegistry());
	}

	private DocCollectorImpl(Config cfg, ModelFinder modelFinder, boolean fromUml,
			BookmarkRegistry bmRegistry) {
		_docgenCfg = new DocgenConfig(cfg);
		_modelFinder = modelFinder;
		_fromUml = fromUml;
		_bmRegistry = bmRegistry;
	}

	// package private for testing
	Map<String, List<PackageDoc>> getNonSkippedPackageDocs() {
		return _nonSkippedPackageDocs;
	}

	// package private for testing
	Map<String, List<ClassDoc>> getNonSkippedClassDocs() {
		return _nonSkippedClassDocs;
	}

	// package private for testing
	Map<Nature, Map<NamespaceInfo, Map<String, List<PackageDoc>>>> getNonSkippedNsPackageDocs() {
		return _nonSkippedNsPackageDocs;
	}

	// package private for testing
	ModelFinder getModelFinder() {
		return _modelFinder;
	}

	/**
	 * Returns package docs for all {@link org.tanjakostic.jcleancim.model.UmlPackage.Kind#MODEL}
	 * packages from the UML model, indexed by the
	 * {@link org.tanjakostic.jcleancim.model.UmlPackage.Kind#MODEL} package name. Each package doc
	 * contains all the non-skipped child element docs, recursively. The flattened map of all
	 * retained package docs, including packages with the same name, can be obtained with
	 * {@link #getNonSkippedPackageDocs()} and {@link #getNonSkippedClassDocs()}. The flattened maps
	 * without duplicates can be obtained with {@link #selectPackageDocsWithoutDuplicates(Map)} and
	 * {@link #selectClassDocsWithoutDuplicates(Map)}.
	 * <p>
	 * Implementation note: This map is not of much value for document generation, it is used only
	 * for logging.
	 */
	Map<String, PackageDoc> getModelPackageDocs() {
		return _modelPackageDocs;
	}

	// ===== org.tanjakostic.jcleancim.docgen.collector.DocCollector =====

	@Override
	public void collect(UmlModel model) {
		if (!isFromUml()) {
			throw new UnsupportedOperationException("This collector has been created without"
					+ "underlying UML model, so you have to add packages from your code by"
					+ " using other methods than this one (collect()).");
		}

		Map<String, PackageDoc> modelPackageDocs = buildPackageDocs(getDocgenCfg(),
				model.getModelPackages());
		_modelPackageDocs.putAll(modelPackageDocs);
	}

	@Override
	public FreeFormDocumentation getFreeFormDocumentation() {
		if (_freeFormDoc == null) {
			Map<String, List<PackageDoc>> nonSkippedPackageDocs = getNonSkippedPackageDocs();
			Map<String, List<ClassDoc>> nonSkippedClassDocs = getNonSkippedClassDocs();
			Map<String, PackageDoc> pNoDuplicates = selectPackageDocsWithoutDuplicates(
					nonSkippedPackageDocs);
			Map<String, ClassDoc> cNoDuplicates = selectClassDocsWithoutDuplicates(
					nonSkippedClassDocs);
			_freeFormDoc = new FreeFormDocumentation(getModelFinder(), _bmRegistry, pNoDuplicates,
					cNoDuplicates);
		}
		return _freeFormDoc;
	}

	@Override
	public FixedFormDocumentation getFixedFormDocumentation() {
		if (_fixedFormDoc == null) {
			_fixedFormDoc = new FixedFormDocumentation(removeNsDuplicates());
		}
		return _fixedFormDoc;
	}

	private Map<Nature, Map<NamespaceInfo, Map<String, PackageDoc>>> removeNsDuplicates() {
		Map<Nature, Map<NamespaceInfo, Map<String, PackageDoc>>> flattenedPackageDocs = new LinkedHashMap<Nature, Map<NamespaceInfo, Map<String, PackageDoc>>>();

		for (Entry<Nature, Map<NamespaceInfo, Map<String, List<PackageDoc>>>> eNature : getNonSkippedNsPackageDocs()
				.entrySet()) {
			Nature nature = eNature.getKey();
			Map<NamespaceInfo, Map<String, PackageDoc>> trgNspaces = new LinkedHashMap<NamespaceInfo, Map<String, PackageDoc>>();
			for (Entry<NamespaceInfo, Map<String, List<PackageDoc>>> eNs : eNature.getValue()
					.entrySet()) {
				NamespaceInfo ns = eNs.getKey();
				Map<String, PackageDoc> pDocs = selectPackageDocsWithoutDuplicates(eNs.getValue());
				trgNspaces.put(ns, pDocs);
			}
			flattenedPackageDocs.put(nature, trgNspaces);
		}
		return flattenedPackageDocs;
	}

	/**
	 * Builds recursively package docs for <code>modelPackages</code>; every instance will add
	 * itself with {@link #addToFlattened(PackageDoc)} method to flattened map, and with
	 * {@link #addToScoped(PackageDoc)} to scoped map of retained packages.
	 */
	private Map<String, PackageDoc> buildPackageDocs(DocgenConfig docgenCfg,
			Collection<UmlPackage> modelPackages) {
		Map<String, PackageDoc> result = new LinkedHashMap<String, PackageDoc>();
		for (UmlPackage p : modelPackages) {
			PackageDoc pDoc = new PackageDocImpl(docgenCfg, p, this);
			result.put(p.getName(), pDoc);
		}
		return result;
	}

	@Override
	public void addToFlattened(PackageDoc packageDoc) {
		String name = packageDoc.getPackageName();
		if (!_nonSkippedPackageDocs.containsKey(name)) {
			_nonSkippedPackageDocs.put(name, new ArrayList<PackageDoc>());
		}
		_nonSkippedPackageDocs.get(name).add(packageDoc);
	}

	@Override
	public void addToFlattened(ClassDoc classDoc) {
		String name = classDoc.getClassPlaceholderName();
		if (!_nonSkippedClassDocs.containsKey(name)) {
			_nonSkippedClassDocs.put(name, new ArrayList<ClassDoc>());
		}
		_nonSkippedClassDocs.get(name).add(classDoc);
	}

	@Override
	public void addToScoped(PackageDoc packageDoc) {
		Util.ensureNotNull(packageDoc, "packageDoc");

		Nature nature = packageDoc.getNature();
		NamespaceInfo nsInfo = packageDoc.getNamespaceInfo();
		String pName = packageDoc.getPackageName();
		Util.ensureNotNull(nature, "nature");
		Util.ensureNotNull(nsInfo, "nsInfo");
		Util.ensureNotNull(pName, "pName");

		if (!_nonSkippedNsPackageDocs.containsKey(nature)) {
			_nonSkippedNsPackageDocs.put(nature,
					new LinkedHashMap<NamespaceInfo, Map<String, List<PackageDoc>>>());
		}
		if (!_nonSkippedNsPackageDocs.get(nature).containsKey(nsInfo)) {
			_nonSkippedNsPackageDocs.get(nature).put(nsInfo,
					new LinkedHashMap<String, List<PackageDoc>>());
		}

		if (!_nonSkippedNsPackageDocs.get(nature).get(nsInfo).containsKey(pName)) {
			_nonSkippedNsPackageDocs.get(nature).get(nsInfo).put(pName,
					new ArrayList<PackageDoc>());
		}

		_nonSkippedNsPackageDocs.get(nature).get(nsInfo).get(pName).add(packageDoc);
		_logger.info(String.format(" >> retained scoped %s/%s/%s", nature, nsInfo.getId(), pName));
	}

	@Override
	public boolean addSkippedInformativePackage(String qName) {
		return _skippedInfPackageQNames.add(qName);
	}

	/**
	 * This is one final result for packages, where the {@link #_nonSkippedPackageDocs} is filtered
	 * by retaining only one package doc per name.
	 */
	private static Map<String, PackageDoc> selectPackageDocsWithoutDuplicates(
			Map<String, List<PackageDoc>> all) {
		Map<String, PackageDoc> flattenedPackageDocs = new LinkedHashMap<String, PackageDoc>();
		for (Entry<String, List<PackageDoc>> entry : all.entrySet()) {
			String pName = entry.getKey();
			List<PackageDoc> pDocs = entry.getValue();
			if (pDocs == null) {
				throw new ProgrammerErrorException("Name was found for null package doc.");
			}
			if (pDocs.isEmpty()) {
				throw new ProgrammerErrorException("Name was found for empty package doc.");
			}
			PackageDoc pDoc = pDocs.get(0);
			if (pDocs.size() > 1) {
				_logger.warn("Multiple packages with name '" + pDoc.getPackageName()
						+ "' - retaining first one in case you use its placeholder.");
			}
			flattenedPackageDocs.put(pName, pDoc);
		}
		return flattenedPackageDocs;
	}

	/**
	 * This is one final result for classes, where the {@link #_nonSkippedClassDocs} is filtered by
	 * retaining only one class doc per Qname.
	 */
	private static Map<String, ClassDoc> selectClassDocsWithoutDuplicates(
			Map<String, List<ClassDoc>> all) {
		Map<String, ClassDoc> flattenedClassDocs = new LinkedHashMap<String, ClassDoc>();
		for (Entry<String, List<ClassDoc>> entry : all.entrySet()) {
			String cQName = entry.getKey();
			List<ClassDoc> cDocs = entry.getValue();
			if (cDocs == null) {
				throw new ProgrammerErrorException("Name was found for null class doc.");
			}
			if (cDocs.isEmpty()) {
				throw new ProgrammerErrorException("Name was found for empty class doc.");
			}
			ClassDoc cDoc = cDocs.get(0);
			if (cDocs.size() > 1) {
				_logger.warn("Multiple classes with Qname '" + cDoc.getClassPlaceholderName()
						+ "' - retaining first one in case you use its placeholder.");
			}
			flattenedClassDocs.put(cQName, cDoc);
		}
		return flattenedClassDocs;
	}

	@Override
	public DocgenConfig getDocgenCfg() {
		return _docgenCfg;
	}

	@Override
	public BookmarkRegistry getBmRegistry() {
		return _bmRegistry;
	}

	@Override
	public boolean isFromUml() {
		return _fromUml;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("model packages=");
		sb.append(getModelPackageDocs().keySet().toString());
		sb.append("; retained=");
		sb.append(getNonSkippedPackageDocs().keySet().toString());
		sb.append("; skipped=");
		sb.append(_skippedInfPackageQNames.toString());
		return sb.toString();
	}
}
