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

import org.tanjakostic.jcleancim.model.TextDescription;

/**
 * Interface common to most kinds of documentation for the model.
 *
 * @author tatjana.kostic@ieee.org
 * @version $Id: ObjectDoc.java 27 2019-11-23 16:29:38Z dev978 $
 */
public interface ObjectDoc extends RawData {

	public static final String DEFAULT_PREFIX_FMT = "(%s) ";

	/** Returns document generation specific configuration. */
	public DocgenConfig getDocgenCfg();

	/** Returns text to be used for chapter heading for this model element; may be empty. */
	public String getHeadingText();

	/** Returns description of this model element. */
	public TextDescription getDescription();

	/** Returns the populated bookmark registry. */
	public BookmarkRegistry getBmRegistry();

	/**
	 * Returns an ID guaranteed to be unique for the model; usable for referencing such as e.g.,
	 * hyperlinks (as anchor id in HTML or bookmark in Word) or references (as id in XML).
	 */
	public String getBookmarkID();
}
