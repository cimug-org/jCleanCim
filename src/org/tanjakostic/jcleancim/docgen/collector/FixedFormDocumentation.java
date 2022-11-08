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

import org.tanjakostic.jcleancim.common.Nature;
import org.tanjakostic.jcleancim.model.NamespaceInfo;
import org.tanjakostic.jcleancim.util.Util;

/**
 * Documentation in the fixed form, such as for printing content per model nature and per name space
 * (as with XML output). The scope or retained packages is limitted according to configuration.
 *
 * @author tatjana.kostic@ieee.org
 * @version $Id: FixedFormDocumentation.java 21 2019-08-12 15:44:50Z dev978 $
 */
public class FixedFormDocumentation {

	private final Map<Nature, Map<NamespaceInfo, Map<String, PackageDoc>>> _nsPackageDocs;

	/**
	 * Constructor.
	 *
	 * @param nsPackageDocs
	 */
	public FixedFormDocumentation(
			Map<Nature, Map<NamespaceInfo, Map<String, PackageDoc>>> nsPackageDocs) {
		Util.ensureNotNull(nsPackageDocs, "nsPackageDocs");

		_nsPackageDocs = nsPackageDocs;
	}

	/** Returns retained scoped package docs per nature and per name space. */
	public Map<Nature, Map<NamespaceInfo, Map<String, PackageDoc>>> getNsPackageDocs() {
		return _nsPackageDocs;
	}
}
