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

import java.util.List;

/**
 * @author tatjana.kostic@ieee.org
 * @version $Id: PackageScl.java 21 2019-08-12 15:44:50Z dev978 $
 */
public interface PackageScl {

	/** simple, without explicit package name (like for a single dedicated annex) */
	public String SCL_ENUM_HEADING_DEFAULT = "SCL enumerations";

	/** with explicit package name (in case you chain a couple of packages) */
	public String SCL_ENUM_HEADING_FORMAT_WITH_PCK_NAME = SCL_ENUM_HEADING_DEFAULT + " (from %s)";

	/** Use this one to see whether to create XML for this package at all. */
	public boolean notEmpty();

	/** Returns text that can be used as heading of a chapter. */
	public String getHeadingText();

	/** Class as XML strings. */
	public List<ClassScl> getClassScls();

	public abstract String toXml(boolean prettyPrint);
}
