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

import java.util.Collection;

import org.apache.log4j.Logger;
import org.tanjakostic.jcleancim.docgen.collector.BookmarkRegistry;
import org.tanjakostic.jcleancim.docgen.collector.DocgenConfig;
import org.tanjakostic.jcleancim.docgen.collector.TableSpec;
import org.tanjakostic.jcleancim.docgen.collector.WAX;
import org.tanjakostic.jcleancim.model.UmlAttribute;
import org.tanjakostic.jcleancim.model.UmlPackage;

/**
 * Data required for documentation of trigger options for IEC61850-7-2.
 * <p>
 * Here the layout you may use for
 * {@link org.tanjakostic.jcleancim.docgen.collector.PlaceholderSpec.Kind#TRGOPS}:
 *
 * <pre>
 * getHeadingText()
 *     getDescription()
 *     this (table, like in PropertiesDoc)
 * </pre>
 * <p>
 * FIXME: this currently differs from FcPackageDoc in COL_SPEC only.
 *
 * @author tatjana.kostic@ieee.org
 * @version $Id: TrgOpPackageDoc.java 21 2019-08-12 15:44:50Z dev978 $
 */
class TrgOpPackageDoc extends AbstractPropertiesDoc {
	private static final Logger _logger = Logger.getLogger(TrgOpPackageDoc.class.getName());

	public static final String INTRO = "shows all trigger options.";

	/**
	 * Constructor.
	 *
	 * @param docgenCfg
	 * @param literals
	 * @param p
	 */
	public TrgOpPackageDoc(DocgenConfig docgenCfg, Collection<UmlAttribute> literals, UmlPackage p,
			BookmarkRegistry bmRegistry) {
		super(docgenCfg, p, "to", p.getDescription(), p.getHtmlDescription(), false, p.getAlias(),
				INTRO, p.getAlias(), TableSpec.TRG_OPS, p.getAlias(), bmRegistry);
		log(_logger, "---- collecting doc for literals in " + p.getName() + " ...");

		// for xml:
		putCell(WAX.A_kind, p.getName());

		for (UmlAttribute a : literals) {
			CellText desc = deduceCellText(null, a);
			String bookmarkID = deduceBookmark(bmRegistry, a);
			EntryDocImpl entry = EntryDocImpl.createData(bookmarkID, desc.formatInfo, a.getName(),
					a.getAlias(), desc.text);
			addEntry(entry);

			// for xml:
			entry.putCell(WAX.LOC_tag, "TrgOp");
			entry.putCell(WAX.A_name, a.getName());
			entry.putCell(WAX.A_aliasID, createDocId(a, "alias"));
			entry.putCell(WAX.A_alias, a.getAlias());
			entry.putCell(WAX.A_descID, createDocId(a, "desc"));
			entry.putCell(WAX.A_desc, desc.text);
			entry.putCell(WAX.A_bookmarkID, bookmarkID);
		}

		log(_logger, ">>>>>> " + toString());
	}
}
