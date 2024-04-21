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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

import org.apache.log4j.Logger;
import org.tanjakostic.jcleancim.docgen.collector.BookmarkRegistry;
import org.tanjakostic.jcleancim.docgen.collector.DocgenConfig;
import org.tanjakostic.jcleancim.docgen.collector.TableSpec;
import org.tanjakostic.jcleancim.docgen.collector.WAX;
import org.tanjakostic.jcleancim.model.AbstractUmlObject;
import org.tanjakostic.jcleancim.model.UmlClass;
import org.tanjakostic.jcleancim.model.UmlPackage;
import org.tanjakostic.jcleancim.util.Util;

/**
 * Data required for documentation of all attributes of a package as a data index (used for IEC61850
 * only).
 * <p>
 * Here the layout you may use for
 * {@link org.tanjakostic.jcleancim.docgen.collector.PlaceholderSpec.Kind#LNMAP_PACKAGE}:
 *
 * <pre>
 * getHeadingText()
 *     getDescription()
 *     this (table, like in PropertiesDoc)
 * </pre>
 *
 * @author tatjana.kostic@ieee.org
 * @version $Id: LnMapPackageDoc.java 31 2019-12-08 01:19:54Z dev978 $
 */
class LnMapPackageDoc extends AbstractPropertiesDoc {
	private static final Logger _logger = Logger.getLogger(LnMapPackageDoc.class.getName());

	public static final String INTRO = " shows mappings between IEC 61850-5 and IEC 61850-7-4"
			+ " logical nodes.";
	public static final String CAPTION = "Logical nodes mappings";

	/**
	 * Constructor.
	 */
	LnMapPackageDoc(DocgenConfig docgenCfg, List<UmlClass> classes, UmlPackage p,
			BookmarkRegistry bmRegistry) {
		super(docgenCfg, p, "func", p.getDescription(), p.getHtmlDescription(), false, p.getAlias(),
				INTRO, CAPTION, TableSpec.FUNCTIONS, null, bmRegistry);

		log(_logger, "---- collecting doc for LN mapings from " + p.getName() + " ...");

		// for xml:
		putCell(WAX.A_kind, p.getName());

		Map<String, List<UmlClass>> orderedByLn74 = orderByLn74text(classes);

		String bookmarkID = null; // we don't point from hyperlinks to any attribute here

		for (Entry<String, List<UmlClass>> sortedItem : orderedByLn74.entrySet()) {
			String lnNamesText = sortedItem.getKey();
			for (UmlClass c : sortedItem.getValue()) {
				CellText desc = deduceCellText(null, c);

				EntryDocImpl entry = EntryDocImpl.createData(bookmarkID, desc.formatInfo,
						c.getName(), c.getIeeeRef(), c.getIecRef(), c.getRsName(), lnNamesText,
						desc.text);
				addEntry(entry);

				// for xml:
				entry.putCell(WAX.LOC_tag, "Function");
				entry.putCell(WAX.A_name, c.getName());
				entry.putCell(WAX.A_aliasID, createDocId(c, "alias"));
				entry.putCell(WAX.A_alias, c.getAlias());
				entry.putCell(WAX.A_descID, createDocId(c, "desc"));
				entry.putCell(WAX.A_desc, desc.text);
				entry.putCell(WAX.A_ieeeRef, c.getIeeeRef());
				entry.putCell(WAX.A_iecRef, c.getIecRef());
				entry.putCell(WAX.A_rsName, c.getRsName());
				entry.putCell(WAX.A_lns, lnNamesText);
			}
		}

		log(_logger, ">>>>>> " + toString());
	}

	/** Does sorting on the text deduced for the 7-4 LN column. */
	private Map<String, List<UmlClass>> orderByLn74text(List<UmlClass> classes) {
		Map<String, List<UmlClass>> orderedByLn74 = new TreeMap<>();
		for (UmlClass c : classes) {
			// skips also CurrentRelay61850 abstract class (used for mappings with CIM)
			if (!(toSkip(c) || c.isAbstract())) {
				List<String> lnNames = AbstractUmlObject
						.collectNames(c.collectDependencyEfferentStructures());
				String lnNamesText = Util.concatCharSeparatedTokens(", ", lnNames);
				if (!orderedByLn74.containsKey(lnNamesText)) {
					orderedByLn74.put(lnNamesText, new ArrayList<UmlClass>());
				}
				orderedByLn74.get(lnNamesText).add(c);
			}
		}
		return orderedByLn74;
	}
}
