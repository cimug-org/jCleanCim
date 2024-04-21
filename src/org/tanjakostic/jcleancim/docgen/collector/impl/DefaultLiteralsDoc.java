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
import org.tanjakostic.jcleancim.model.UmlAttribute;
import org.tanjakostic.jcleancim.model.UmlClass;
import org.tanjakostic.jcleancim.model.UmlStereotype;
import org.tanjakostic.jcleancim.util.Util;

/**
 * Data required for documentation of enumeration literals with the middle column for codes, as used
 * for CIM. For the format, see {@link PropertiesDoc}.
 * <p>
 * FIXME: Should extract the commons with 61850 enum type into a superclass.
 *
 * @author tatjana.kostic@ieee.org
 * @version $Id: DefaultLiteralsDoc.java 31 2019-12-08 01:19:54Z dev978 $
 */
class DefaultLiteralsDoc extends AbstractPropertiesDoc {
	private static final Logger _logger = Logger.getLogger(DefaultLiteralsDoc.class.getName());

	public static final String INTRO_FMT = " shows all literals of %s.";
	public static final String CAPTION_FMT = "Literals of %s";

	public DefaultLiteralsDoc(DocgenConfig docgenCfg, UmlClass c, BookmarkRegistry bmRegistry) {
		super(docgenCfg, c, "lit", String.format(INTRO_FMT, c.getName()),
				String.format(CAPTION_FMT, c.getQualifiedName()), TableSpec.LITERALS, null,
				bmRegistry);

		log(_logger, "---- collecting doc for literals of " + c.getQualifiedName() + " ...");

		Collection<UmlAttribute> retainedNatives = new ArrayList<UmlAttribute>();
		// for inherited enum literals, we shouldn't care, but some may define inheritance...
		Collection<UmlAttribute> retainedInheriteds = new ArrayList<UmlAttribute>();
		super.filterAttributes(c, retainedNatives, retainedInheriteds);

		for (UmlAttribute a : retainedNatives) {
			String name = a.getName();
			String qualifiersText = deduceQualifiersPrefix(a, UmlStereotype.getAttributeBuiltIns(),
					docgenCfg.showCustomStereotypes, DEFAULT_PREFIX_FMT);
			String descPrefix = qualifiersText;
			CellText desc = deduceCellText(descPrefix, a);
			String bookmarkID = deduceBookmark(bmRegistry, a);

			EntryDocImpl entry = EntryDocImpl.createData(bookmarkID, desc.formatInfo, name,
					a.getInitValue().trim(), desc.text);
			addEntry(entry);

			// for xml:
			initRawData(entry, a);
			entry.putCell(WAX.A_literalVal, a.getInitValue());

			if (Util.hasContent(descPrefix)) {
				entry.putCell(WAX.A_desc, desc.text);
			}
		}

		log(_logger, ">>>>>> " + toString());
	}
}
