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

import java.io.File;

import org.tanjakostic.jcleancim.model.TextDescription;

/**
 * Thin set of methods, allowing us to do document generation tests without actually having the full
 * model loaded and built from EA file.
 *
 * @author tatjana.kostic@ieee.org
 * @version $Id: ModelFinder.java 21 2019-08-12 15:44:50Z dev978 $
 */
public interface ModelFinder {

	/**
	 * Returns value of first attribute <code>attributeName</code> of first class
	 * <code>className</code> when found, null otherwise.
	 */
	public String findAttributeValue(String className, String attributeName);

	/**
	 * Returns file containing the first <code>diagramName</code> on the first container
	 * <code>containerName</code> when found, null otherwise. Note that the diagram container could
	 * be either package or class; if there is a diagram with the same name on a package and on a
	 * class, the package diagram is returned.
	 */
	public File findDiagramFile(String containerName, String diagramName);

	/**
	 * Returns the note (description) of the first <code>diagramName</code> on the first container
	 * <code>containerName</code> when found, null otherwise. Note that the diagram container could
	 * be either package or class; if there is a diagram with the same name on a package and on a
	 * class, the package diagram is returned.
	 */
	public TextDescription findDiagramNote(String containerName, String diagramName);

	/**
	 * Returns the name of the first class <code>className</code> withing the first package
	 * <code>packageName</code> when found, null otherwise.
	 */
	public String findClassName(String packageName, String className);

	/**
	 * Returns the name space name for the IEC 61850 namespace class <code>className</code> when
	 * found, null otherwise.
	 */
	public String findIec61850NsName(String className);
}
