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

import java.util.List;

import org.apache.log4j.Logger;
import org.tanjakostic.jcleancim.docgen.collector.BookmarkRegistry;
import org.tanjakostic.jcleancim.docgen.collector.DocgenConfig;
import org.tanjakostic.jcleancim.docgen.collector.PropertiesDoc;
import org.tanjakostic.jcleancim.docgen.collector.TableSpec;
import org.tanjakostic.jcleancim.docgen.collector.WAX;
import org.tanjakostic.jcleancim.model.UmlAttribute;
import org.tanjakostic.jcleancim.model.UmlPackage;

/**
 * Data required for documentation of abbreviations for IEC61850-7-4.
 * <p>
 * Here the layout you may use for
 * {@link org.tanjakostic.jcleancim.docgen.collector.PlaceholderSpec.Kind#ABBREVIATIONS}:
 *
 * <pre>
 *     this (table, like in {@link PropertiesDoc})
 * </pre>
 *
 * @author tatjana.kostic@ieee.org
 * @version $Id: AbbrPackageDoc.java 21 2019-08-12 15:44:50Z dev978 $
 */
class AbbrPackageDoc extends AbstractPropertiesDoc {
	private static final Logger _logger = Logger.getLogger(AbbrPackageDoc.class.getName());

	public static final String INTRO = " shows normative terms that are combined to create"
			+ " data object names.";
	public static final String CAPTION = "Normative abbreviations for data object names";

	/**
	 * Constructor.
	 */
	AbbrPackageDoc(DocgenConfig docgenCfg, List<UmlAttribute> attributes, UmlPackage p,
			BookmarkRegistry bmRegistry) {
		super(docgenCfg, p, "abbr", p.getDescription(), p.getHtmlDescription(), false, p.getAlias(),
				INTRO, CAPTION, TableSpec.ABBREVS, null, bmRegistry);
		log(_logger, "---- collecting doc for abbreviations in " + p.getName() + " ...");

		String bookmarkID = null; // we don't point from hyperlinks to any attribute here

		// for xml:
		putCell(WAX.A_kind, p.getName());

		for (UmlAttribute a : attributes) {
			CellText desc = deduceCellText(null, a);
			EntryDocImpl entry = EntryDocImpl.createData(bookmarkID, desc.formatInfo, a.getName(),
					desc.text);
			addEntry(entry);

			// for xml:
			entry.putCell(WAX.LOC_tag, "Term");
			entry.putCell(WAX.A_name, a.getName());
			entry.putCell(WAX.A_descID, createDocId(a, "desc"));
			entry.putCell(WAX.A_desc, desc.text);
		}

		log(_logger, ">>>>>> " + toString());
	}
}
