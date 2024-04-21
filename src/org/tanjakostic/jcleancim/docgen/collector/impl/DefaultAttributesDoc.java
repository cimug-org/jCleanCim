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
import org.tanjakostic.jcleancim.docgen.collector.TableSpec;
import org.tanjakostic.jcleancim.docgen.collector.WAX;
import org.tanjakostic.jcleancim.model.TextDescription;
import org.tanjakostic.jcleancim.model.UmlAttribute;
import org.tanjakostic.jcleancim.model.UmlClass;
import org.tanjakostic.jcleancim.model.UmlStereotype;
import org.tanjakostic.jcleancim.util.Util;

/**
 * Documentation for attributes in simple format.
 *
 * @author tatjana.kostic@ieee.org
 * @author laurent.guise@art-et-histoire.com
 * @version $Id: DefaultAttributesDoc.java 27 2019-11-23 16:29:38Z dev978 $
 */
class DefaultAttributesDoc extends AbstractPropertiesDoc {
	private static final Logger _logger = Logger.getLogger(DefaultAttributesDoc.class.getName());

	public static final String INTRO_FMT = " shows all attributes of %s.";
	public static final String CAPTION_FMT = "Attributes of %s";

	/**
	 * Constructor.
	 */
	public DefaultAttributesDoc(DocgenConfig docgenCfg, UmlClass c, BookmarkRegistry bmRegistry) {
		super(docgenCfg, c, "att", String.format(INTRO_FMT, c.getName()),
				String.format(CAPTION_FMT, c.getQualifiedName()), TableSpec.ATTRS, null,
				bmRegistry);
		log(_logger, "---- collecting doc for attributes of " + c.getName() + " ...");

		Collection<UmlAttribute> retainedNatives = new ArrayList<UmlAttribute>();
		Collection<UmlAttribute> retainedInheriteds = new ArrayList<UmlAttribute>();
		super.filterAttributes(c, retainedNatives, retainedInheriteds);

		String bookmarkID = null; // we don't point from hyperlinks to any attribute here

		for (UmlAttribute a : retainedNatives) {
			String qualifiersText = deduceQualifiersPrefix(a, UmlStereotype.getAttributeBuiltIns(),
					docgenCfg.showCustomStereotypes, DEFAULT_PREFIX_FMT);
			String descPrefix = qualifiersText + getInitValAsPrefix(a);
			CellText desc = deduceCellText(descPrefix, a);
			String typeName = prepareForHyperlink(a.getType());

			EntryDocImpl entry = EntryDocImpl.createData(bookmarkID, desc.formatInfo, a.getName(),
					a.getMultiplicity().getBounds(), typeName, desc.text);
			addEntry(entry);

			// for xml:
			initRawData(entry, a);
			initAttributeRawData(entry, a);

			if (Util.hasContent(descPrefix)) {
				entry.putCell(WAX.A_desc, desc.text);
			}
		}
		for (UmlAttribute a : retainedInheriteds) {
			String qualifiersText = deduceQualifiersPrefix(a, UmlStereotype.getAttributeBuiltIns(),
					docgenCfg.showCustomStereotypes, DEFAULT_PREFIX_FMT);
			String descPrefix = qualifiersText;
			String typeName = prepareForHyperlink(a.getType());
			TextDescription docInh = new TextDescription(
					INHERITED_FROM + prepareForHyperlink(a.getContainingClass()));
			CellText desc = deduceCellText(descPrefix, docInh, null, a);

			EntryDocImpl entry = EntryDocImpl.createData(bookmarkID, null, a.getName(),
					a.getMultiplicity().getBounds(), typeName, desc.text);
			addEntry(entry);

			// for xml:
			initRawData(entry, a, a.getContainingClass().getName());
			initAttributeRawData(entry, a);
		}

		log(_logger, ">>>>>> " + toString());
	}

	private static void initAttributeRawData(EntryDocImpl entry, UmlAttribute a) {
		entry.putCell("mult", a.getMultiplicity().getBounds());
		entry.putCell(WAX.A_type, a.getType().getName());
		entry.putCell("defaultValue", a.getInitValue());
	}
}
