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

package org.tanjakostic.jcleancim.docgen.collector.impl.ag;

import java.util.Set;

import org.tanjakostic.jcleancim.model.UML;

/**
 * Group of data objects of a logical node, or group of attributes of a common data class, that need
 * to be seggregated for printing tables in IEC61850-7-4 and IEC61850-7-3, respectively.
 *
 * @author tatjana.kostic@ieee.org
 * @version $Id: CategoryKind.java 21 2019-08-12 15:44:50Z dev978 $
 */
interface CategoryKind {

	// public enum Kind {
	// DACategory, DOCategory
	// }

	// public Kind getSpecKind();

	/**
	 * Returns name (useful for code generation).
	 */
	public String getKindTag();

	/**
	 * Returns the names of the CDC-containing packages, which allows to classify the attributes for
	 * logical nodes (e.g., {@link UML#CDCStatusInfo}); for common data classes, returns empty set.
	 */
	public Set<String> getTypesPackageNames();

	/**
	 * Returns the names of superclasses (from the meta-model), which allow to classify the
	 * attributes for common data classes (e.g., {@link UML#FCDA_MX} for CDCs or {@link UML#ENC} for
	 * LNs).
	 */
	public Set<String> getTypesSuperclassNames();
}
