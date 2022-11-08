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
import java.util.Collection;

import org.apache.log4j.Logger;
import org.tanjakostic.jcleancim.docgen.collector.BookmarkRegistry;
import org.tanjakostic.jcleancim.docgen.collector.DocgenConfig;
import org.tanjakostic.jcleancim.docgen.collector.PropertiesDoc;
import org.tanjakostic.jcleancim.docgen.collector.TableSpec;
import org.tanjakostic.jcleancim.docgen.collector.WAX;
import org.tanjakostic.jcleancim.model.TextDescription;
import org.tanjakostic.jcleancim.model.UmlAttribute;
import org.tanjakostic.jcleancim.model.UmlClass;

/**
 * Data required for documentation of enumeration literals in custom format, as used for literals
 * with integer values in tables in IEC61850-7-4 and IEC61850-7-3. For the format, see
 * {@link PropertiesDoc}.
 *
 * @author tatjana.kostic@ieee.org
 * @version $Id: CustomLiteralsDoc.java 31 2019-12-08 01:19:54Z dev978 $
 */
class CustomLiteralsDoc extends AbstractPropertiesDoc {
	private static final Logger _logger = Logger.getLogger(CustomLiteralsDoc.class.getName());

	public static final String INTRO_FMT = " shows all enumeration items of %s.";
	public static final String CAPTION_FMT = "Literals of %s";

	/**
	 * Constructor.
	 */
	public CustomLiteralsDoc(DocgenConfig docgenCfg, UmlClass c, BookmarkRegistry bmRegistry) {
		super(docgenCfg, c, "lit", String.format(INTRO_FMT, c.getName()),
				String.format(CAPTION_FMT, c.getName()), TableSpec.CUSTOM_LITERALS, c.getName(),
				bmRegistry);

		log(_logger, "---- collecting doc for literals of " + c.getQualifiedName() + " ...");

		Collection<UmlAttribute> retainedNatives = new ArrayList<UmlAttribute>();
		// for inherited enum literals, we shouldn't care, but some may define inheritance...
		Collection<UmlAttribute> retainedInheriteds = new ArrayList<UmlAttribute>();

		super.filterAttributes(c, retainedNatives, retainedInheriteds);

		for (UmlAttribute a : retainedInheriteds) {
			String descPrefix = getDeprecatedTextAsPrefix(a);
			String name = a.displayEmptyValue() ? "" : a.getName();
			TextDescription docInh = new TextDescription(
					"(" + INHERITED_FROM + prepareForHyperlink(a.getContainingClass()) + " - "
							+ a.getName() + ") " + a.getDescription());
			CellText desc = deduceCellText(descPrefix, docInh, null, a);

			EntryDocImpl entry = EntryDocImpl.createData(null, desc.formatInfo, name,
					a.getInitValue().trim(), desc.text);
			addEntry(entry);

			// for xml:
			initRawData(entry, a, a.getContainingClass().getName());
			entry.putCell(WAX.A_literalVal, a.getInitValue());
			if (a.displayEmptyValue()) { // overwrite inherited name with empty string
				entry.putCell(WAX.A_name, "");
			}
		}

		for (UmlAttribute a : retainedNatives) {
			String name = a.displayEmptyValue() ? "" : a.getName();
			CellText desc = deduceCellText(getDeprecatedTextAsPrefix(a), a);
			String bookmarkID = deduceBookmark(bmRegistry, a);

			EntryDocImpl entry = EntryDocImpl.createData(bookmarkID, desc.formatInfo, name,
					a.getInitValue().trim(), desc.text);
			addEntry(entry);

			// for xml:
			initRawData(entry, a);
			entry.putCell(WAX.A_literalVal, a.getInitValue());
			if (a.displayEmptyValue()) { // overwrite inherited name with empty string
				entry.putCell(WAX.A_name, "");
			}
		}

		log(_logger, ">>>>>> " + toString());
	}
}
