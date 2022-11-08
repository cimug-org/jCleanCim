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
import java.util.List;
import java.util.Set;

import org.tanjakostic.jcleancim.docgen.collector.BookmarkRegistry;
import org.tanjakostic.jcleancim.docgen.collector.DocgenConfig;
import org.tanjakostic.jcleancim.docgen.collector.RawData;
import org.tanjakostic.jcleancim.docgen.collector.TableSpec;
import org.tanjakostic.jcleancim.docgen.collector.WAX;
import org.tanjakostic.jcleancim.docgen.collector.impl.ag.AttributeGroup;
import org.tanjakostic.jcleancim.model.PresenceCondition;
import org.tanjakostic.jcleancim.model.UML;
import org.tanjakostic.jcleancim.model.UmlAttribute;
import org.tanjakostic.jcleancim.model.UmlClass;
import org.tanjakostic.jcleancim.model.UmlModel;
import org.tanjakostic.jcleancim.model.UmlObject;
import org.tanjakostic.jcleancim.util.Util;

/**
 * Holds the utility method to filter groups which may stay with no members after applying
 * configuration filters (e.g., a class actually inherits from another class, but another class is
 * informative, and we don't want informative stuff printed), and some common formatting.
 *
 * @author tatjana.kostic@ieee.org
 * @author laurent.guise@art-et-histoire.com
 * @version $Id: Attributes61850Doc.java 31 2019-12-08 01:19:54Z dev978 $
 */
public abstract class Attributes61850Doc extends AbstractPropertiesDoc {

	/** String to print in 61860-7-4 when data object is transient. */
	protected static final String TRANSIENT = "T";

	public static final String INTRO_FMT = " shows all attributes of %s.";
	public static final String CAPTION_FMT = "Attributes of %s";

	/**
	 * Constructor.
	 */
	protected Attributes61850Doc(DocgenConfig docgenCfg, UmlObject object, String what,
			String headingText, String introText, String captionText, TableSpec colSpec,
			String tableName, BookmarkRegistry bmRegistry) {
		super(docgenCfg, object, what, null, null, true, headingText, introText, captionText,
				colSpec, tableName, bmRegistry);
	}

	/**
	 * Returns non-empty groups, retained from <code>groups</code> after applying the filters set in
	 * configuration.
	 */
	protected final Collection<AttributeGroup> filterGroups(Collection<AttributeGroup> groups) {
		List<AttributeGroup> result = new ArrayList<AttributeGroup>();
		for (AttributeGroup group : groups) {
			List<UmlAttribute> filteredNatives = new ArrayList<UmlAttribute>();
			for (UmlAttribute a : group.getNativeAttributes()) {
				if (!toSkip(a)) {
					filteredNatives.add(a);
				}
			}
			List<UmlAttribute> filteredInheriteds = new ArrayList<UmlAttribute>();
			for (UmlAttribute a : group.getInheritedAttributes()) {
				if (!toSkipInherited(a)) {
					filteredInheriteds.add(a);
				}
			}
			if (!filteredNatives.isEmpty() || !filteredInheriteds.isEmpty()) {
				result.add(
						new AttributeGroup(group.getAgSpec(), filteredNatives, filteredInheriteds));
			}
		}
		return result;
	}

	/**
	 * Fills <code>outRawData</code> with value for the key
	 * {@value org.tanjakostic.jcleancim.docgen.collector.WAX#A_deducedTypeText} and, if enabled (
	 * <code>all=true</code>) and where they exist, values for keys
	 * {@value org.tanjakostic.jcleancim.docgen.collector.WAX#A_type} and
	 * {@value org.tanjakostic.jcleancim.docgen.collector.WAX#A_typeKind}.
	 *
	 * @param mmType
	 * @param outRawData
	 *            in/out argument, filled here.
	 * @param all
	 *            if true, will set all potential fields; otherwise, will set only
	 *            {@value org.tanjakostic.jcleancim.docgen.collector.WAX#A_deducedTypeText}.
	 */
	protected final void deduceTypeText(UmlClass mmType, RawData outRawData, boolean all) {
		deduceTypeText(mmType, outRawData, all, false);
	}

	/**
	 * Similar to {@link #deduceTypeText(UmlClass, RawData, boolean, boolean)}, with third argument
	 * set to <code>false</code>, and taking care of the transient CDC attributes when writing data
	 * index for LNs.
	 */
	protected void deduceTypeTextForDataIndex(UmlClass mmType, RawData outRawData) {
		deduceTypeText(mmType, outRawData, false, mmType.isTransientCDC());
	}

	/**
	 * Similar to {@link #deduceTypeText(UmlClass, RawData, boolean)}, with additional parameter
	 * <code>showTransient</code>; set that one to true when writing data index for LNs and the
	 * attribute is a transient CDC.
	 */
	private void deduceTypeText(UmlClass mmType, RawData outRawData, boolean all,
			boolean showTransient) {
		if (mmType.isCodedEnumFCDA() || mmType.isCodedEnumDA() || mmType.isCodedEnum()) {
			UmlClass dedType = deduceUnderlyingType(mmType, mmType.isCodedEnumFCDA());
			String dedTypeName = dedType.getName();
			String hphText = prepareForHyperlink(dedType);
			if (all) {
				outRawData.putCell(WAX.A_type, dedTypeName);
				outRawData.putCell(WAX.A_typeKind, WAX.V_typeKind_CODED_ENUM);
			}
			outRawData.putCell(WAX.A_deducedTypeText, hphText);
		} else if (mmType.isEnumFCDA() || mmType.isEnumDA() || mmType.isEnumeration()) {
			UmlClass dedType = deduceUnderlyingType(mmType, mmType.isEnumFCDA());
			String dedTypeName = dedType.getName();
			String hphText = prepareForHyperlink(dedType);
			if (all) {
				outRawData.putCell(WAX.A_type, dedTypeName);
				outRawData.putCell(WAX.A_typeKind, WAX.V_typeKind_ENUM);
			}
			outRawData.putCell(WAX.A_deducedTypeText, hphText);
		} else if (mmType.isPackedListFCDA() || mmType.isPackedListDA() || mmType.isPackedList()) {
			UmlClass dedType = deduceUnderlyingType(mmType, mmType.isPackedListFCDA());
			String dedTypeName = trimPrimitivePrefix(dedType.getName());
			String hphText = prepareForHyperlinkAdjustedName(dedType, dedTypeName);
			if (all) {
				outRawData.putCell(WAX.A_type, dedTypeName);
				outRawData.putCell(WAX.A_typeKind, WAX.V_typeKind_PACKED_LIST);
			}
			outRawData.putCell(WAX.A_deducedTypeText, hphText);
		} else if (mmType.isComposedFCDA() || mmType.isComposedDA() || mmType.isStructured()) {
			UmlClass dedType = deduceUnderlyingType(mmType, mmType.isComposedFCDA());
			String dedTypeName = trimPrimitivePrefix(dedType.getName());
			String hphText = prepareForHyperlinkAdjustedName(dedType, dedTypeName);
			if (all) {
				outRawData.putCell(WAX.A_type, dedTypeName);
				outRawData.putCell(WAX.A_typeKind, WAX.V_typeKind_CONSTRUCTED);
			}
			outRawData.putCell(WAX.A_deducedTypeText, hphText);
		} else if (mmType.isAnyFCDA() || mmType.isPrimitiveDA() || mmType.isBasic()) {
			UmlClass dedType = deduceUnderlyingType(mmType, mmType.isAnyFCDA());
			String dedTypeName = trimPrimitivePrefix(dedType.getName());
			String hphText = prepareForHyperlinkAdjustedName(dedType, dedTypeName);
			if (all) {
				outRawData.putCell(WAX.A_type, dedTypeName);
				outRawData.putCell(WAX.A_typeKind, WAX.V_typeKind_BASIC);
			}
			outRawData.putCell(WAX.A_deducedTypeText, hphText);
		} else if (mmType.isTransientCDC()) {
			String trans = (showTransient) ? (" (" + TRANSIENT + ")") : "";
			UmlClass dedType = mmType.getSuperclasses().iterator().next();
			String dedTypeName = dedType.getName();
			String hphText = prepareForHyperlinkAdjustedName(dedType, dedTypeName + trans);
			if (all) {
				outRawData.putCell(WAX.A_type, dedTypeName);
			}
			outRawData.putCell(WAX.A_deducedTypeText, hphText);
		} else if (mmType.isEnumCDC() || mmType.isTrackingDerivedCDC()) {
			UmlClass dedSuperType = mmType.getSuperclasses().iterator().next();
			String superName = dedSuperType.getName();
			String hphTextSuper = prepareForHyperlink(dedSuperType);

			UmlClass dedCDCType = deduceUnderlyingTypeForDerivedCdc(mmType, false);
			UmlClass dedType = deduceUnderlyingType(dedCDCType, false);
			String dedTypeName = trimPrimitivePrefix(dedType.getName());
			String hphText = prepareForHyperlinkAdjustedName(dedType, dedTypeName);

			if (all) {
				outRawData.putCell(WAX.A_type, superName);
				outRawData.putCell(WAX.A_underlyingType, dedTypeName);
			}
			outRawData.putCell(WAX.A_deducedTypeText,
					String.format("%s (%s)", hphTextSuper, hphText));

			if (mmType.isEnumCDC() && mmType.isOrHasSuperclass(UML.ENC)) {
				UmlClass dedControlCDCType = deduceUnderlyingTypeForDerivedCdc(mmType, true);
				UmlClass dedControlType = deduceUnderlyingType(dedControlCDCType, false, true);
				if (dedControlType != null) {
					String dedCtlTypeName = trimPrimitivePrefix(dedControlType.getName());
					String hphControlText = prepareForHyperlinkAdjustedName(dedControlType,
							dedCtlTypeName);
					outRawData.putCell(WAX.A_underlyingControlType, dedCtlTypeName);
					outRawData.putCell(WAX.A_deducedTypeText,
							String.format("%s (%s, %s)", hphTextSuper, hphText, hphControlText));
				}
			}
		} else {
			UmlClass dedType = deduceUnderlyingType(mmType, false);
			String dedTypeName = trimPrimitivePrefix(dedType.getName());
			String hphText = prepareForHyperlink(dedType);
			if (all) {
				outRawData.putCell(WAX.A_type, dedTypeName);
			}
			outRawData.putCell(WAX.A_deducedTypeText, hphText);
		}
	}

	/**
	 * Returns {@link #deduceUnderlyingType(UmlClass, boolean, boolean)} with
	 * <code>useCtlVal=false</code>.
	 */
	private static UmlClass deduceUnderlyingType(UmlClass inType, boolean isFcda) {
		return deduceUnderlyingType(inType, isFcda, false);
	}

	/**
	 * "Descends" the UML type's private attributes to come to the actual primitive or enumerated
	 * type to display (those ending with "Kind" or starting with {@value UML#PREF_P_} or
	 * {@value UML#PREF_S_}). Note that the returned type is a real UML type and you may want to
	 * trim the prefixes for printing ( {@link #trimPrimitivePrefix(String)}).
	 *
	 * @param inType
	 *            type of attribute to analyse - could be (Packed)Enumeration, (Packed)EnumDA or
	 *            (Packed)EnumFCDA.
	 * @param isFcda
	 *            whether the passed type is an FCDA.
	 * @param useCtlVal
	 *            whether to use {@link UML#ATTR_ctlVal} instead of {@link UML#ATTR_val}.
	 * @return UML type (can be null if nothing found with <code>useCtlVal=true</code>).
	 */
	private static UmlClass deduceUnderlyingType(UmlClass inType, boolean isFcda,
			boolean useCtlVal) {
		UmlClass type = inType;
		if (isFcda) {
			Set<UmlAttribute> attributes = type.findAttributes(UML.ATTR_attr);
			if (!attributes.isEmpty()) {
				UmlAttribute first = attributes.iterator().next();
				if (!first.isPublic()) {
					type = first.getType();
				}
			}
		}
		String privateAttrName = (useCtlVal) ? UML.ATTR_ctlVal : UML.ATTR_val;
		Set<UmlAttribute> attributes = type.findAttributes(privateAttrName);
		if (useCtlVal) {
			type = null; // resets the type here, forces to either find one or return null
		}
		if (!attributes.isEmpty()) {
			UmlAttribute first = attributes.iterator().next();
			if (!first.isPublic()) {
				type = first.getType();
			}
		}
		return type;
	}

	/**
	 * Use this method when dealing with an FCDA as type, for both CDC tables and CDC attributes
	 * index, as well as for derived tracking CDC attributes in LN tables - because we search for
	 * actual underlying types for e.g. enums and composed DAs, for primitive/structured DAs it's
	 * the "real" primitive type that gets picked (with {@value UML#PREF_P_}/{@value UML#PREF_S_}),
	 * and we don't want to display that one in 7-3 or in LTRK in 7-4.
	 */
	private static String trimPrimitivePrefix(String realName) {
		if (realName.startsWith(UML.PREF_P_)) {
			return realName.replaceFirst(UML.PREF_P_, "");
		}
		if (realName.startsWith(UML.PREF_S_)) {
			return realName.replaceFirst(UML.PREF_S_, "");
		}
		return realName;
	}

	/**
	 * Deduces underlying (enum) type for enumerated CDC <code>inType</code> by skipping the first
	 * characters that are normally the CDC name.
	 * <p>
	 * FIXME: Implementation note: this is a brittle implementation and will break for the case of a
	 * derived CDC for which the base CDC has name longer than 3... At present, if nothing found,
	 * returning <code>inType</code>.
	 *
	 * @param inType
	 * @param withCtlSuffix
	 *            if true, will search for derived DA classes ending with {@link UML#SUFF_CONTROL}.
	 */
	private static UmlClass deduceUnderlyingTypeForDerivedCdc(UmlClass inType,
			boolean withCtlSuffix) {
		int cdcNameLen = 3;
		UmlClass type = inType;

		UmlModel model = type.getModel();
		// we don't have attributes of the type derivedDA, so searching from name:
		String derivedDAName = type.getName().substring(cdcNameLen);
		if (withCtlSuffix) {
			derivedDAName += UML.SUFF_CONTROL;
		}
		Set<UmlClass> derivedDAs = model.findClasses(derivedDAName);
		if (!derivedDAs.isEmpty()) {
			type = derivedDAs.iterator().next();
		}
		return type;
	}

	// ============= presence conditions ======================

	protected String prepareForHyperlink(PresenceCondition pc) {
		UmlAttribute pcLiteral = pc.getDefinitionLiteral();
		String pcText = pc.getStemAndArgs();
		return (pcLiteral == null) ? pcText : prepareForHyperlinkAdjustedName(pcLiteral, pcText);
	}

	/**
	 * Fills appropriately raw data {@link WAX#A_presCond}, {@link WAX#A_presCondArgs},
	 * {@link WAX#A_presCondArgsID} and {@link WAX#A_cond}.
	 */
	protected static void fillPresenceConditionAndArgs(RawData entry, PresenceCondition pc,
			boolean isInherited, String context) {
		fillPresenceConditionAndArgs(entry, pc, isInherited, context, false);
	}

	/**
	 * Fills appropriately raw data {@link WAX#A_presCond}, {@link WAX#A_presCondArgs},
	 * {@link WAX#A_presCondArgsID} and {@link WAX#A_cond} if <code>isDerivedStats=false</code>,
	 * otherwise {@link WAX#A_dsPresCond}, {@link WAX#A_dsPresCondArgs},
	 * {@link WAX#A_dsPresCondArgsID} and {@link WAX#A_dsCond}.
	 */
	protected static void fillPresenceConditionAndArgs(RawData entry, PresenceCondition pc,
			boolean isInherited, String context, boolean isDerivedStats) {
		String pcTag = isDerivedStats ? WAX.A_dsPresCond : WAX.A_presCond;
		String pcArgTag = isDerivedStats ? WAX.A_dsPresCondArgs : WAX.A_presCondArgs;
		String condTag = isDerivedStats ? WAX.A_dsCond : WAX.A_cond;
		String pcArgIDTag = isDerivedStats ? WAX.A_dsPresCondArgsID : WAX.A_presCondArgsID;

		entry.putCellNonEmpty(pcTag, pc.getStem());
		entry.putCellNonEmpty(pcArgTag, pc.getArgs());
		String cond = pc.getText();
		if (Util.hasContent(cond)) {
			if (!isInherited) {
				entry.putCellNonEmpty(condTag, cond);
				String docId = (pc.getConstraint() == null) ? ""
						: (createDocId(pc.getConstraint(), condTag + "." + context));
				entry.putCellNonEmpty(pcArgIDTag, docId);
			} else {
				entry.putCellNonEmpty(condTag, "");
			}
		}
	}
}
