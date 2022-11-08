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
import org.tanjakostic.jcleancim.model.UmlAttribute;
import org.tanjakostic.jcleancim.model.UmlClass;
import org.tanjakostic.jcleancim.util.Util;

/**
 * Data required for documentation of logical node attributes, for IEC61850-7-4. For the layout, see
 * {@link PropertiesDoc}.
 *
 * @author tatjana.kostic@ieee.org
 * @author laurent.guise@art-et-histoire.com
 * @version $Id: LnAttributesDoc.java 31 2019-12-08 01:19:54Z dev978 $
 */
class LnAttributesDoc extends Attributes61850Doc {
	private static final Logger _logger = Logger.getLogger(LnAttributesDoc.class.getName());

	public static final String INTRO_FMT_LN = " shows all data objects of %s.";
	public static final String CAPTION_FMT_LN = "Data objects of %s";

	/**
	 * Constructor.
	 */
	public LnAttributesDoc(DocgenConfig docgenCfg, UmlClass c, BookmarkRegistry bmRegistry) {
		super(docgenCfg, c, "do", null, String.format(INTRO_FMT_LN, c.getName()),
				String.format(CAPTION_FMT_LN, c.getName()), TableSpec.LN_ATTRS, c.getName(),
				bmRegistry);

		log(_logger, "---- collecting doc for attributes of " + c.getQualifiedName() + " ...");

		Collection<AttributeGroup> groups = AttributeGroup.initLnGroups(c);
		Collection<AttributeGroup> retainedGroups = filterGroups(groups);

		String bookmarkID = null; // we don't point from hyperlinks to any attribute here

		for (AttributeGroup group : retainedGroups) {
			if (group.getAgSpec() != null) {
				addEntry(EntryDocImpl.createGroupSubhead(group.getAgSpec(),
						getTableSpec().colCount()));
			}
			for (UmlAttribute a : group.getNativeAttributes()) {
				RawData values = new RawDataImpl();
				initRawFromSplitTypeName(values, a);

				PresenceCondition pc = a.getPresConditions().get(0);
				PresenceCondition dsPc = a.getDsPresConditions(c).get(0);
				String presCond = formatDOPresenceCondition(pc, dsPc);
				// values.putCell("mult", a.getMultiplicity().getBounds());

				String descPrefix = getDeprecatedTextAsPrefix(a);
				CellText desc = deduceCellText(descPrefix, a);

				EntryDocImpl entry = EntryDocImpl.createData(bookmarkID, desc.formatInfo,
						values.getCell(WAX.A_name), values.getCell("typeText"),
						values.getCell("trans"), desc.text, presCond);
				addEntry(entry);

				// for xml:
				initRawData(entry, a);
				initDoRawData(entry, values, pc, false, dsPc, a.getQualifiedName());

				if (Util.hasContent(descPrefix)) {
					entry.putCell(WAX.A_desc, desc.text);
				}
			}
			for (UmlAttribute a : group.getInheritedAttributes()) {
				TextDescription docInh = new TextDescription(
						INHERITED_FROM + prepareForHyperlink(a.getContainingClass()));
				CellText desc = deduceCellText(getDeprecatedTextAsPrefix(a), docInh, null, a);

				RawData values = new RawDataImpl();
				initRawFromSplitTypeName(values, a);

				PresenceCondition pc = a.getPresConditions().get(0);
				PresenceCondition dsPc = a.getDsPresConditions(c).get(0);
				String presCond = formatDOPresenceCondition(pc, dsPc);
				// values.putCell("mult", a.getMultiplicity().getBounds());

				EntryDocImpl entry = EntryDocImpl.createData(bookmarkID, null,
						values.getCell(WAX.A_name), values.getCell("typeText"),
						values.getCell("trans"), desc.text, presCond);
				addEntry(entry);

				// for xml:
				initRawData(entry, a, a.getContainingClass().getName());
				initDoRawData(entry, values, pc, true, dsPc, a.getQualifiedName());
			}
		}

		log(_logger, ">>>>>> " + toString());
	}

	/**
	 * Sets value for keys {@value org.tanjakostic.jcleancim.docgen.collector.WAX#A_name}, "trans",
	 * "typeText"; and if applicable, for
	 * {@value org.tanjakostic.jcleancim.docgen.collector.WAX#A_transient},
	 * {@value org.tanjakostic.jcleancim.docgen.collector.WAX#A_type}, and those as potentially set
	 * by {@link #deduceTypeText(UmlClass, RawData, boolean)}.
	 */
	private void initRawFromSplitTypeName(RawData values, UmlAttribute attr) {
		values.putCell(WAX.A_name, attr.getName());
		values.putCell("trans", "");

		UmlClass type = attr.getType();

		// here (the pain of) splitting type to [enum CDC parent] + CDC + [Transient]...
		if (type.isTransientCDC()) {
			values.putCell("trans", TRANSIENT); // formatted
			values.putCell(WAX.A_transient, "true"); // raw
			deduceTypeText(type, values, true);
			String typeText = getDocgenCfg().writeUmlTypes ? type.getName()
					: values.getCell(WAX.A_deducedTypeText);
			values.putCell("typeText", typeText);
		} else if (type.isEnumCDC() || type.isTrackingDerivedCDC()) {
			deduceTypeText(type, values, true);
			String typeText = getDocgenCfg().writeUmlTypes ? type.getName()
					: values.getCell(WAX.A_deducedTypeText);
			values.putCell("typeText", typeText);
		} else {
			values.putCell(WAX.A_type, type.getName());
			values.putCell("typeText", type.getName());
		}
	}

	private String formatDOPresenceCondition(PresenceCondition pc, PresenceCondition dsPc) {
		return prepareForHyperlink(pc) + " / " + prepareForHyperlink(dsPc);
	}

	private static void initDoRawData(RawData entry, RawData values, PresenceCondition pc,
			boolean isInherited, PresenceCondition dsPc, String context) {
		entry.copyCell(values, WAX.A_type);
		// entry.copyCell(values, "mult");
		entry.copyCell(values, WAX.A_underlyingType);
		entry.copyCell(values, WAX.A_underlyingControlType);
		entry.copyCell(values, WAX.A_transient);
		fillPresenceConditionAndArgs(entry, pc, isInherited, context);
		fillPresenceConditionAndArgs(entry, dsPc, isInherited, context, true);
	}
}
