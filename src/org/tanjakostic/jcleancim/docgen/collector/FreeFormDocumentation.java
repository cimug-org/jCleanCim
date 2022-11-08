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

package org.tanjakostic.jcleancim.docgen.collector;

import java.util.Map;

/**
 * Documentation in the free form, such as for printing content simply per package or class name
 * (like for MS Word documentation). The scope of retained packages is limitted according to
 * configuration.
 *
 * @author tatjana.kostic@ieee.org
 * @version $Id: FreeFormDocumentation.java 21 2019-08-12 15:44:50Z dev978 $
 */
public class FreeFormDocumentation {

	private final ModelFinder _modelFinder;
	private final BookmarkRegistry _bmRegistry;
	private final Map<String, PackageDoc> _packageDocs;
	private final Map<String, ClassDoc> _classDocs;

	/**
	 * Constructor.
	 *
	 * @param modelFinder
	 *            model facade; if null, most of placeholders will have error.
	 * @param bmRegistry
	 *            registry of bookmarks, i.e., contains maps between bookmark IDs (unique strings)
	 *            and UML objects; can be used for generating hyperlinks.
	 * @param packageDocs
	 *            "flattened" map of package documentation instances, with package name as key (to
	 *            allow to quickly find the package name from what is read in the placeholder). If
	 *            null or empty, placeholders dealing with packages will all have error and empty
	 *            content.
	 * @param classDocs
	 *            "flattened" map of class documentation instances, with <i>qualified</i> class name
	 *            as key (to allow to quickly find the package name from what is read in the
	 *            placeholder). If null or empty, placeholders dealing with classes will all have
	 *            error and empty content.
	 */
	public FreeFormDocumentation(ModelFinder modelFinder, BookmarkRegistry bmRegistry,
			Map<String, PackageDoc> packageDocs, Map<String, ClassDoc> classDocs) {
		_modelFinder = modelFinder;
		_bmRegistry = bmRegistry;
		_packageDocs = packageDocs;
		_classDocs = classDocs;
	}

	public ModelFinder getModelFinder() {
		return _modelFinder;
	}

	public BookmarkRegistry getBmRegistry() {
		return _bmRegistry;
	}

	public Map<String, PackageDoc> getPackageDocs() {
		return _packageDocs;
	}

	public PackageDoc getPackageDoc(String name) {
		return _packageDocs.get(name);
	}

	public Map<String, ClassDoc> getClassDocs() {
		return _classDocs;
	}

	public ClassDoc getClassDoc(String qName) {
		return _classDocs.get(qName);
	}
}
