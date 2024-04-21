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
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.tanjakostic.jcleancim.docgen.collector.BookmarkRegistry;
import org.tanjakostic.jcleancim.docgen.collector.DocgenConfig;
import org.tanjakostic.jcleancim.docgen.collector.EntryDoc;
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
 * Data required for documentation of common data class attributes, for IEC61850-7-3. For the
 * layout, see {@link PropertiesDoc}.
 *
 * @author tatjana.kostic@ieee.org
 * @author laurent.guise@art-et-histoire.com
 * @version $Id: CdcAttributesDoc.java 27 2019-11-23 16:29:38Z dev978 $
 */
class CdcAttributesDoc extends Attributes61850Doc {
	private static final Logger _logger = Logger.getLogger(CdcAttributesDoc.class.getName());

	public static final String TAB_TIT_FMT = "UML class name = %s";
	public static final String TAB_TIT_CDCID_FMT = UML.TVN_cdcId + " = %s, " + TAB_TIT_FMT;

	public static final String ARRAY_OF_FMT = "ARRAY %s OF %s";

	/**
	 * Constructor.
	 */
	public CdcAttributesDoc(DocgenConfig docgenCfg, UmlClass c, BookmarkRegistry bmRegistry) {
		super(docgenCfg, c, "da", null, String.format(INTRO_FMT, c.getName()),
				String.format(CAPTION_FMT, c.getName()), TableSpec.CDC_ATTRS, deduceTableName(c),
				bmRegistry);

		log(_logger, "---- collecting doc for attributes of " + c.getQualifiedName() + " ...");

		Collection<AttributeGroup> groups = AttributeGroup.initCdcGroups(c);
		Collection<AttributeGroup> retainedGroups = filterGroups(groups);

		LinkedHashMap<EntryDoc, UmlAttribute> entryDocs = collectEntryDocsInDefaultOrder(
				retainedGroups);
		Reordering r = specifyReordering(entryDocs);

		List<EntryDoc> reorderedEntryDocs = r.reorder(new ArrayList<EntryDoc>(entryDocs.keySet()));
		for (EntryDoc entryDoc : reorderedEntryDocs) {
			addEntry(entryDoc);
		}

		log(_logger, ">>>>>> " + toString());
	}

	private static String deduceTableName(UmlClass c) {
		String cdcId = c.getCdcId();
		if (cdcId == null) {
			if (c.isAbstract()) {
				// abstract classes without explicit definition for 'cdcId' ("modelling" CDCs):
				return String.format(TAB_TIT_FMT, c.getName());
			}
			// concrete classes without tag (most of CDCs):
			cdcId = c.getName();
		}
		return String.format(TAB_TIT_CDCID_FMT, cdcId, c.getName());
	}

	private LinkedHashMap<EntryDoc, UmlAttribute> collectEntryDocsInDefaultOrder(
			Collection<AttributeGroup> retainedGroups) {
		LinkedHashMap<EntryDoc, UmlAttribute> result = new LinkedHashMap<EntryDoc, UmlAttribute>();

		for (AttributeGroup group : retainedGroups) {
			if (group.getAgSpec() != null) {
				EntryDoc entryDoc = EntryDocImpl.createGroupSubhead(group.getAgSpec(),
						getTableSpec().colCount());
				result.put(entryDoc, null);
			}
			for (UmlAttribute a : group.getNativeAttributes()) {
				createEntry(a, true, result);
			}
			for (UmlAttribute a : group.getInheritedAttributes()) {
				createEntry(a, false, result);
			}
		}
		return result;
	}

	private EntryDocImpl createEntry(UmlAttribute a, boolean isNative,
			Map<EntryDoc, UmlAttribute> collector) {
		RawData values = new RawDataImpl();
		initValuesFromSplitTypeName(values, a);

		String name = a.getName();
		PresenceCondition pc = a.getPresConditions().get(0);
		String presCondition = determinePresCondText(a, pc);
		// values.putCell("mult", a.getMultiplicity().getBounds());

		String bookmarkID = null; // we don't point from hyperlinks to any attribute here

		CellText desc = null;
		if (isNative) {
			String descPrefix = getDeprecatedTextAsPrefix(a) + getInitValAsPrefix(a);
			desc = deduceCellText(descPrefix, a);
		} else {
			String descPrefix = getDeprecatedTextAsPrefix(a);
			TextDescription docInh = new TextDescription(
					INHERITED_FROM + prepareForHyperlink(a.getContainingClass()));
			desc = deduceCellText(descPrefix, docInh, null, a);
		}

		EntryDocImpl entry = EntryDocImpl.createData(bookmarkID, desc.formatInfo, name,
				values.getCell("typeText"), values.getCell(WAX.A_fc),
				values.getCell(TableSpec.KEY_trgOp), desc.text, presCondition);
		collector.put(entry, a);

		// for xml:
		if (isNative) {
			initRawData(entry, a);
			initFcdaRawData(entry, a, false, values, pc);
			entry.putCell(WAX.A_name, name); // can be changed from original
		} else {
			initRawData(entry, a, a.getContainingClass().getName());
			initFcdaRawData(entry, a, true, values, pc);
			entry.putCell(WAX.A_name, name); // can be changed from original
		}
		return entry;
	}

	/**
	 * Determines the following fields for CDC attributes: "typeText",
	 * {@value org.tanjakostic.jcleancim.docgen.collector.WAX#A_fc},
	 * {@value org.tanjakostic.jcleancim.docgen.collector.TableSpec#KEY_trgOp}, "minIndex",
	 * "maxIndex", "isArray", "isServicePar".
	 */
	private void initValuesFromSplitTypeName(RawData values, UmlAttribute a) {
		values.putCell("typeText", ""); // formatted
		values.putCell(WAX.A_fc, "");
		values.putCell(TableSpec.KEY_trgOp, "");
		values.putCell("minIndex", "");
		values.putCell("maxIndex", "");
		values.putCell("isArray", "");
		values.putCell("isServicePar", "");

		// here (the pain of) splitting type to DA + FC + trgOp...
		UmlClass type = a.getType();
		List<String> splitTypeNames = Util.splitCharSeparatedTokens(type.getName(), '_');
		int tokenCount = splitTypeNames.size();

		// ... starting by fixing various "*_STRING*" which get cut due to "_":
		if (tokenCount > 1 && splitTypeNames.get(1).startsWith("STRING")) {
			String repairedString = splitTypeNames.get(0) + "_" + splitTypeNames.get(1);
			splitTypeNames.set(0, repairedString);
			splitTypeNames.remove(1);
			tokenCount = splitTypeNames.size();
		}

		// type text, FC and trgOp:
		if (tokenCount > 0) {
			deduceTypeText(type, values, true);
			String typeText = (getDocgenCfg().writeUmlTypes) ? typeText = type.getName()
					: values.getCell(WAX.A_deducedTypeText);
			if (a.isMultivalued()) {
				values.putCell("typeText",
						String.format(ARRAY_OF_FMT, a.getArrayBounds(), typeText));
				values.putCellNonEmpty("minIndex",
						Util.null2empty(a.getConstraintValues().get(UML.CONSTR_TXT_minIdx)));
				values.putCellNonEmpty("maxIndex",
						Util.null2empty(a.getConstraintValues().get(UML.CONSTR_TXT_maxIdx)));
				values.putCell("isArray", "true");
			} else {
				values.putCell("typeText", typeText);
			}
		}
		if (tokenCount == 1 && !type.isAnyCDC()) {
			values.putCell("isServicePar", "true");
		}
		if (tokenCount > 1) {
			values.putCell(WAX.A_fc, splitTypeNames.get(1));
		}
		if (tokenCount > 2) {
			String firstTrgOp = splitTypeNames.get(2);
			values.putCell(TableSpec.KEY_trgOp, firstTrgOp); // for Word
			values.putCell(firstTrgOp, "true"); // for XML
		}
		if (tokenCount > 3) {
			String trgOp = values.getCell(TableSpec.KEY_trgOp);
			String secondTrgOp = splitTypeNames.get(3);
			trgOp += ", " + secondTrgOp;
			values.putCell(TableSpec.KEY_trgOp, trgOp); // for Word
			values.putCell(secondTrgOp, "true"); // for XML
		}
		if (0 == tokenCount || tokenCount > 4) {
			_logger.warn(String.format("Name of %s type (%s) has unexpected number of tokens (%d)",
					type.getKind(), type.getName(), Integer.valueOf(tokenCount)));
		}
	}

	private String determinePresCondText(UmlAttribute a, PresenceCondition pc) {
		return (a.getType().isAnyFCDA() || a.getType().isAnyCDC()) ? prepareForHyperlink(pc) : "";
	}

	private Reordering specifyReordering(LinkedHashMap<EntryDoc, UmlAttribute> entryDocs) {
		Reordering result = new Reordering();
		for (Map.Entry<EntryDoc, UmlAttribute> item : entryDocs.entrySet()) {
			EntryDoc toMoveEntryDoc = item.getKey();
			UmlAttribute toMoveAttr = item.getValue();
			if (toMoveAttr != null) {
				UmlAttribute afterWhichAttr = toMoveAttr.getSiblingToMoveAfter();
				if (afterWhichAttr != null) {
					EntryDoc afterWhichDoc = Util.getKeyByValue(entryDocs, afterWhichAttr);
					result.addReorderingItem(toMoveEntryDoc, afterWhichDoc);

				}
			}
		}
		return result;
	}

	private static void initFcdaRawData(RawData entry, UmlAttribute a, boolean isInherited,
			RawData src, PresenceCondition pc) {
		entry.copyCell(src, WAX.A_type);
		if (!a.getContainingClass().isFromMetaModel()) {
			entry.copyNonEmptyCell(src, WAX.A_typeKind);
		}
		// entry.copyCell(src, "mult");
		if (a.hasDefaultValue()) {
			entry.putCell(WAX.A_defaultValue, a.getInitValue());
		} else if (a.hasValueRange()) {
			if (a.getValueRange().min() != null && !a.getValueRange().min().isEmpty()) {
				entry.putCell(WAX.A_minValue, a.getValueRange().min());
			}
			if (a.getValueRange().max() != null && !a.getValueRange().max().isEmpty()) {
				entry.putCell(WAX.A_maxValue, a.getValueRange().max());
			}
		}

		entry.copyNonEmptyCell(src, "isServicePar");
		boolean isServicePar = Boolean.parseBoolean(entry.getCell("isServicePar"));
		if (isServicePar) {
			entry.putCell(WAX.LOC_tag, "ServiceParameter"); // overwrite
		} else {
			fillPresenceConditionAndArgs(entry, pc, isInherited, a.getQualifiedName());
			entry.copyNonEmptyCell(src, WAX.A_fc);
			entry.copyNonEmptyCell(src, TableSpec.KEY_trgOp);
			entry.copyNonEmptyCell(src, "dchg");
			entry.copyNonEmptyCell(src, "qchg");
			entry.copyNonEmptyCell(src, "dupd");
			entry.copyNonEmptyCell(src, "isArray");
			entry.copyNonEmptyCell(src, "minIndex");
			entry.copyNonEmptyCell(src, "maxIndex");
		}
	}
}
