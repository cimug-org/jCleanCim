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
import org.tanjakostic.jcleancim.docgen.collector.PropertiesDoc;
import org.tanjakostic.jcleancim.docgen.collector.RawData;
import org.tanjakostic.jcleancim.docgen.collector.RawDataImpl;
import org.tanjakostic.jcleancim.docgen.collector.TableSpec;
import org.tanjakostic.jcleancim.docgen.collector.WAX;
import org.tanjakostic.jcleancim.docgen.collector.impl.ag.AttributeGroup;
import org.tanjakostic.jcleancim.model.PresenceCondition;
import org.tanjakostic.jcleancim.model.TextDescription;
import org.tanjakostic.jcleancim.model.UML;
import org.tanjakostic.jcleancim.model.UmlAttribute;
import org.tanjakostic.jcleancim.model.UmlClass;
import org.tanjakostic.jcleancim.util.Util;

/**
 * Data required for documentation of constructed data attributes, for IEC61850-7-3. For the layout,
 * see {@link PropertiesDoc}.
 *
 * @author tatjana.kostic@ieee.org
 * @author laurent.guise@art-et-histoire.com
 * @version $Id: DaAttributesDoc.java 27 2019-11-23 16:29:38Z dev978 $
 */
class DaAttributesDoc extends Attributes61850Doc {
	private static final Logger _logger = Logger.getLogger(DaAttributesDoc.class.getName());

	public static final String TAB_TIT_DATID_FMT = UML.TVN_datId + " = %s";

	/**
	 * Constructor.
	 */
	public DaAttributesDoc(DocgenConfig docgenCfg, TableSpec tableSpec, UmlClass c,
			BookmarkRegistry bmRegistry) {
		super(docgenCfg, c, "att", null, String.format(INTRO_FMT, c.getName()),
				String.format(CAPTION_FMT, c.getName()), tableSpec,
				String.format(TAB_TIT_DATID_FMT, c.getName()), bmRegistry);

		log(_logger, "---- collecting doc for attributes of " + c.getQualifiedName() + " ...");

		Collection<AttributeGroup> groups = AttributeGroup.initDaGroups(c);
		Collection<AttributeGroup> retainedGroups = filterGroups(groups);

		String bookmarkID = null; // we don't point from hyperlinks to any attribute here

		for (AttributeGroup group : retainedGroups) {
			if (group.getAgSpec() != null) {
				addEntry(EntryDocImpl.createGroupSubhead(group.getAgSpec(),
						getTableSpec().colCount()));
			}

			for (UmlAttribute a : group.getNativeAttributes()) {
				String descPrefix = getDeprecatedTextAsPrefix(a) + getInitValAsPrefix(a);
				CellText desc = deduceCellText(descPrefix, a);

				RawData values = new RawDataImpl();
				deduceTypeText(a.getType(), values, true);
				String typeText = (getDocgenCfg().writeUmlTypes) ? a.getType().getName()
						: values.getCell(WAX.A_deducedTypeText);

				PresenceCondition pc = a.getPresConditions().get(0);
				String pcText = prepareForHyperlink(pc);

				EntryDocImpl entry = EntryDocImpl.createData(bookmarkID, desc.formatInfo,
						a.getName(), typeText, desc.text, pcText);
				addEntry(entry);

				// for xml:
				initRawData(entry, a);
				initBdaRawData(entry, a, false, values, pc, a.getQualifiedName());

				if (Util.hasContent(descPrefix)) {
					entry.putCell(WAX.A_desc, desc.text);
				}
			}
			for (UmlAttribute a : group.getInheritedAttributes()) {
				TextDescription docInh = new TextDescription(
						INHERITED_FROM + prepareForHyperlink(a.getContainingClass()));
				CellText desc = deduceCellText(getDeprecatedTextAsPrefix(a), docInh, null, a);

				RawData values = new RawDataImpl();
				deduceTypeText(a.getType(), values, true);
				String typeText = getDocgenCfg().writeUmlTypes ? a.getType().getName()
						: values.getCell(WAX.A_deducedTypeText);

				PresenceCondition pc = a.getPresConditions().get(0);
				String pcText = prepareForHyperlink(pc);
				EntryDocImpl entry = EntryDocImpl.createData(bookmarkID, null, a.getName(),
						typeText, desc.text, pcText);
				addEntry(entry);

				// for xml:
				initRawData(entry, a, a.getContainingClass().getName());
				initBdaRawData(entry, a, true, values, pc, a.getQualifiedName());
			}
		}

		log(_logger, ">>>>>> " + toString());
	}

	private static void initBdaRawData(EntryDocImpl entry, UmlAttribute a, boolean isInherited,
			RawData values, PresenceCondition pc, String context) {
		entry.copyCell(values, WAX.A_type);
		if (!a.getContainingClass().isFromMetaModel()) {
			entry.copyNonEmptyCell(values, WAX.A_typeKind);
		}
		// entry.addRawField("mult", a.getMultiplicity().getBounds());
		if (a.hasDefaultValue()) {
			entry.putCell("defaultValue", a.getInitValue());
		} else if (a.hasValueRange()) {
			if (a.getValueRange().min() != null && !a.getValueRange().min().isEmpty()) {
				entry.putCell("minValue", a.getValueRange().min());
			}
			if (a.getValueRange().max() != null && !a.getValueRange().max().isEmpty()) {
				entry.putCell("maxValue", a.getValueRange().max());
			}
		}
		fillPresenceConditionAndArgs(entry, pc, isInherited, context);
	}
}
