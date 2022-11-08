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

/**
 * Data required for documentation of figures.
 * <p>
 * Here the layout you may use for a diagram within the package or class:
 *
 * <pre>
 *   getFigureFile()
 *   [Figure # - ] + getCaptionText()
 *   [Figure #: ] + getDescription()
 * </pre>
 *
 * or the legacy one, with the figure introduction before the figure (when
 * {@value org.tanjakostic.jcleancim.common.Config#KEY_DOCGEN_WORD_INTRO_TO_FIGURE_BEFORE}=true):
 *
 * <pre>
 *   [Figure # ] + getIntroText()
 *   getFigureFile()
 *   [Figure # - ] + getCaptionText()
 *   getDescription()
 * </pre>
 *
 * A writer needs to supply what is enclosed in [] and for the rest you call methods of this
 * interface.
 *
 * @author tatjana.kostic@ieee.org
 * @version $Id: FigureDoc.java 21 2019-08-12 15:44:50Z dev978 $
 */
public interface FigureDoc extends ObjectDoc {

	public static final String INTRO_TEXT_FORMAT = " shows %s.";
	public static final String CAPTION_TEXT_FORMAT = "%s";

	/** Returns text that will introduce the table. E.g. "Figure 23 <b>{introText}</b>". */
	public String getIntroText();

	/** Returns file with the figure. */
	public File getFigureFile();

	/** Returns text that describes the caption. E.g., "Figure 23 - <b>{captionText}</b>". */
	public String getCaptionText();
}
