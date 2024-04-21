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

import java.io.File;

import org.tanjakostic.jcleancim.common.Config;
import org.tanjakostic.jcleancim.docgen.collector.BookmarkRegistry;
import org.tanjakostic.jcleancim.docgen.collector.DocgenConfig;
import org.tanjakostic.jcleancim.docgen.collector.FigureDoc;
import org.tanjakostic.jcleancim.docgen.collector.WAX;
import org.tanjakostic.jcleancim.model.TextDescription;
import org.tanjakostic.jcleancim.model.UmlDiagram;
import org.tanjakostic.jcleancim.util.Util;

/**
 * Data required for documentation of diagrams. For the layout, see {@link FigureDoc}.
 *
 * @author tatjana.kostic@ieee.org
 * @version $Id: FigureDocImpl.java 21 2019-08-12 15:44:50Z dev978 $
 */
public class FigureDocImpl extends AbstractObjectDoc implements FigureDoc {

	private final String _introText;
	private final String _captionText;
	private final File _pic;

	/**
	 * Constructor.
	 *
	 * @param docgenCfg
	 * @param d
	 * @param bmRegistry
	 */
	public FigureDocImpl(DocgenConfig docgenCfg, UmlDiagram d, BookmarkRegistry bmRegistry) {
		this(docgenCfg, d, d.getDescription(), d.getHtmlDescription(), d.getPic(),
				deduceShowsWhat(d), deduceCaption(d), bmRegistry);
	}

	/**
	 * Constructor.
	 *
	 * @param docgenCfg
	 *            non-null docgen configuration.
	 * @param d
	 *            (potentially null) UML diagram.
	 * @param description
	 * @param htmlDescription
	 * @param pic
	 *            file containing picture.
	 * @param showsWhat
	 *            non-null text to enclose in
	 *            {@value org.tanjakostic.jcleancim.docgen.collector.FigureDoc#INTRO_TEXT_FORMAT}.
	 * @param caption
	 *            non-null caption text.
	 * @param bmRegistry
	 */
	public FigureDocImpl(DocgenConfig docgenCfg, UmlDiagram d, TextDescription description,
			TextDescription htmlDescription, File pic, String showsWhat, String caption,
			BookmarkRegistry bmRegistry) {
		super(docgenCfg, d, "fig", description, htmlDescription, false, null, null, bmRegistry);

		_pic = pic;
		_introText = String.format(INTRO_TEXT_FORMAT, showsWhat);
		_captionText = String.format(CAPTION_TEXT_FORMAT, caption);

		// for xml:
		if (d != null) {
			putCell(WAX.A_kind, d.getKind().getLabel());
			putCell(WAX.A_img, Config.PICS_DIR_NAME + "/" + d.getPic().getName());
			putCell(WAX.A_captionID, createDocId(d, "caption"));
			putCell(WAX.A_caption, getCaptionText());
			putCell(WAX.A_introductionID, createDocId(d, "introduction"));
			putCell(WAX.A_introduction, getIntroText());
		}
	}

	private static String deduceShowsWhat(UmlDiagram d) {
		return d.getKind().getLabel() + " diagram " + d.getName();
	}

	private static String deduceCaption(UmlDiagram d) {
		if (d.getKind() == UmlDiagram.Kind.CUSTOM) {
			// for custom diagram (pic), we print just diagram's name:
			return d.getName();
		}
		// for a UML diagram, we print what it actually is:
		return Util.capitalise(d.getKind().getLabel()) + " diagram " + d.getQualifiedName();
	}

	// ===== Impl. of org.tanjakostic.jcleancim.docgen.collector.FigureDoc methods =====

	@Override
	public String getIntroText() {
		return _introText;
	}

	@Override
	public String getCaptionText() {
		return _captionText;
	}

	@Override
	public File getFigureFile() {
		return _pic;
	}
}
