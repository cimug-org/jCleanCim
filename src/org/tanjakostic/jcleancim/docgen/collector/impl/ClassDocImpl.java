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
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.log4j.Logger;
import org.tanjakostic.jcleancim.common.Nature;
import org.tanjakostic.jcleancim.docgen.collector.BookmarkRegistry;
import org.tanjakostic.jcleancim.docgen.collector.ClassDoc;
import org.tanjakostic.jcleancim.docgen.collector.DocgenConfig;
import org.tanjakostic.jcleancim.docgen.collector.FigureDoc;
import org.tanjakostic.jcleancim.docgen.collector.PlaceholderSpec;
import org.tanjakostic.jcleancim.docgen.collector.PropertiesDoc;
import org.tanjakostic.jcleancim.docgen.collector.TableSpec;
import org.tanjakostic.jcleancim.docgen.collector.WAX;
import org.tanjakostic.jcleancim.model.AbstractUmlObject;
import org.tanjakostic.jcleancim.model.PresenceCondition;
import org.tanjakostic.jcleancim.model.TextDescription;
import org.tanjakostic.jcleancim.model.UML;
import org.tanjakostic.jcleancim.model.UmlClass;
import org.tanjakostic.jcleancim.model.UmlConstraint;
import org.tanjakostic.jcleancim.model.UmlDiagram;
import org.tanjakostic.jcleancim.model.UmlStereotype;
import org.tanjakostic.jcleancim.util.Util;

/**
 * Data required for documentation of classes. For the layout, see {@link ClassDoc}.
 *
 * @author tatjana.kostic@ieee.org
 * @version $Id: ClassDocImpl.java 31 2019-12-08 01:19:54Z dev978 $
 */
class ClassDocImpl extends AbstractObjectDoc implements ClassDoc {
	private static final Logger _logger = Logger.getLogger(ClassDocImpl.class.getName());

	private final String _classQName;
	private final TextDescription _inheritancePath;
	private final TextDescription _extendedDescription;
	private final PropertiesDoc _attributesDoc;
	private final PropertiesDoc _assocEndsDoc;
	private final PropertiesDoc _operationsDoc;
	private final List<FigureDoc> _diagramDocs;

	/**
	 * Constructor.
	 */
	public ClassDocImpl(DocgenConfig docgenCfg, UmlClass c, BookmarkRegistry bmRegistry,
			int headingOffset) {
		super(docgenCfg, c, "cl", c.getDescription(), c.getHtmlDescription(), false,
				deduceHeadingText(docgenCfg, c), deduceBookmark(bmRegistry, c), bmRegistry);

		log(_logger, "---- collecting doc for class   " + Util.getIndentSpaces(headingOffset)
				+ c.getName() + " ...");

		_classQName = PlaceholderSpec
				.constructClassPlaceholderText(c.getContainingPackage().getName(), c.getName());
		_inheritancePath = createInheritancePath(docgenCfg, c);

		_attributesDoc = createAttributesDoc(docgenCfg, c, bmRegistry);
		_assocEndsDoc = createAssocEndsDoc(docgenCfg, c, bmRegistry);
		_operationsDoc = createOperationsDoc(docgenCfg, c, bmRegistry);
		List<FigureDoc> diagramDocs = createFiguresDoc(docgenCfg, c, bmRegistry);
		_diagramDocs = diagramDocs == null || diagramDocs.isEmpty() ? new ArrayList<FigureDoc>()
				: diagramDocs;
		_extendedDescription = (c.getNature() == Nature.CIM) ? deduceConstraintsAdditionalText(c)
				: prependOldNameAndDeducePresenceConditionsAdditionalText(c);

		// for xml: the rest is initialised in super
		if (c.isFromMetaModel()) {
			// overwriting tag, for XML schema restrictions: e.g., an LN cannot have associations:
			putCell(WAX.LOC_tag, UmlClass.Iec61850Kind.OTHER_61850.getTag());
		}
		if (c.isAbstract()) {
			putCell(WAX.A_abstract, "true");
		}
		if (c.isUsableForStatistics()) {
			putCell(UmlStereotype.STATISTICS, "true");
		}
		if (c.isAdmin()) {
			putCell(UmlStereotype.ADMIN, "true");
		}
		if (!c.getSuperclasses().isEmpty()) {
			List<String> supers = AbstractUmlObject.collectNames(c.getSuperclasses());
			putCell(WAX.A_superClass, Util.concatCharSeparatedTokens(",", supers));
		}
		if (c.isEnumeratedType()) {
			putCell("isEnum", "true");
			if (c.isCodedEnum()) {
				putCell("isCodedEnum", "true");
			}
		}
		if (c.getCdcId() != null) {
			putCell(WAX.A_cdcId, c.getCdcId());
		}
		if (c.getOldName() != null) {
			putCell(UML.TVN_oldName, c.getOldName());
		}

		// FIXME: Remove when HTML2Word works:
		if (c.getName().equals("TestEnum")) {
			_logger.info("----------------------- in ClassDocImpl() ctor:");
			_logger.info("RAW: " + Util.NL + c.getDescription().text);
			_logger.info("-----------------------");
			_logger.info("HTML: " + Util.NL + c.getHtmlDescription().text);
			_logger.info("-----------------------");
			_logger.info("HTML unesc Java: " + Util.NL
					+ StringEscapeUtils.escapeHtml(c.getHtmlDescription().text));
		}
	}

	private static String deduceHeadingText(DocgenConfig docgenCfg, UmlClass c) {
		List<String> moreTokens = new ArrayList<>();
		if (c.isAbstract()) {
			moreTokens.add(UML.CLASS_abstract);
		}
		if (c.isUsableForStatistics()) {
			moreTokens.add(UmlStereotype.STATISTICS);
		}
		if (c.isAdmin()) {
			moreTokens.add(UmlStereotype.ADMIN);
		}

		String fmt = (c.getNature() == Nature.CIM) ? DEFAULT_PREFIX_FMT
				: CLASS_TITLE_PREFIX_FMT_IEC61850;

		String qualifiersText = deduceQualifiersPrefix(c, UmlStereotype.getClassBuiltIns(),
				moreTokens, docgenCfg.showCustomStereotypes, fmt);

		if (c.getNature() == Nature.CIM || c.getAlias().isEmpty()) {
			String labelText = (c.isClass()) ? "" : (" " + c.getKind().getLabel());
			return String.format(NO_ALIAS_HEADING_FORMAT, qualifiersText, c.getName(), labelText);
		}

		// below: all IEC61850-specific

		if (c.is74LN()) {
			return String.format(LN_HEADING_FORMAT, qualifiersText, c.getAlias(),
					Util.getNonBreakingSpaces(3), c.getName());
		}
		if (c.isPackedListDA()) {
			return String.format(PRIM_DA_HEADING_FORMAT, qualifiersText, c.getAlias(), c.getName(),
					c.getKind().getLabel());
		}

		if (c.isAnyDA() || c.isAnyCDC()) {
			return String.format(ANY_DA_OR_CDC_HEADING_FORMAT, qualifiersText, c.getAlias(),
					c.getName());
		}

		String stereotype = c.getStereotype().value();
		String stereotypeText = stereotype.isEmpty() ? "" : (" " + c.getKind().getLabel());
		return String.format(OTHER_WITH_ALIAS_HEADING_FORMAT, qualifiersText, c.getAlias(),
				c.getName(), stereotypeText);
	}

	private TextDescription createInheritancePath(DocgenConfig docgenCfg, UmlClass c) {
		String inhPath = "";
		if (!c.getAllSuperclassesFlattened(!docgenCfg.includeInf).isEmpty()) {
			inhPath += ClassDoc.INHERITANCE_PATH_PREFIX;
			List<String> supNames = new ArrayList<>();
			for (UmlClass sup : c.getAllSuperclassesFlattened(!docgenCfg.includeInf)) {
				supNames.add(prepareForHyperlink(sup));
			}
			inhPath += Util.concatCharSeparatedTokens(INHERITANCE_PATH_SEP, supNames);
		}
		return new TextDescription(inhPath);
	}

	/** This is for default class constraints. */
	private TextDescription deduceConstraintsAdditionalText(UmlClass c) {
		TextDescription augmentedDesc = super.getDescription();
		Map<String, UmlConstraint> constraints = c.getConstraints();
		if (!constraints.isEmpty()) {
			augmentedDesc = augmentedDesc.appendParagraph(CONSTRAINTS_TXT_CIM);

			for (UmlConstraint cc : constraints.values()) {
				StringBuilder constraintParagraph = new StringBuilder();
				constraintParagraph.append(cc.getName()).append(":").append(Util.NL)
						.append(cc.getDescription());
				augmentedDesc = augmentedDesc.appendParagraph(constraintParagraph.toString());
			}
		}
		return augmentedDesc;
	}

	/** (IEC61850) This is for 61850 presence conditions with non-machine-processable condID. */
	private TextDescription prependOldNameAndDeducePresenceConditionsAdditionalText(UmlClass c) {
		List<UmlConstraint> withCondIDs = new ArrayList<UmlConstraint>();
		for (UmlConstraint cc : c.getConstraints().values()) {
			PresenceCondition pc = cc.getPresenceCondition();
			if (pc != null && pc.isWithCondID()) {
				withCondIDs.add(cc);
			}
		}
		if (c.getOldName() == null && withCondIDs.isEmpty()) {
			// we don't need to augment anything; just return now
			return null;
		}

		TextDescription augmentedDesc = super.getDescription();
		if (c.getOldName() != null) {
			augmentedDesc = augmentedDesc.prepend(String.format(OLDNAME_FMT, c.getOldName()));
		}

		if (!withCondIDs.isEmpty()) {
			String ccHeading = CONSTRAINTS_TXT_IEC61850;
			augmentedDesc = augmentedDesc.appendParagraph(ccHeading);

			for (UmlConstraint cc : withCondIDs) {
				StringBuilder condParagraph = new StringBuilder();
				condParagraph.append(cc.getName()).append(": ").append(cc.getCondition());
				augmentedDesc = augmentedDesc.appendParagraph(condParagraph.toString());
			}
		}
		return augmentedDesc;
	}

	private static PropertiesDoc createAttributesDoc(DocgenConfig docgenCfg, UmlClass c,
			BookmarkRegistry bmRegistry) {
		if (c.getOwner().getNature() == Nature.IEC61850) {
			if (c.isEnumeratedType()) {
				return new CustomLiteralsDoc(docgenCfg, c, bmRegistry);
			} else if (c.is74LN()) {
				return new LnAttributesDoc(docgenCfg, c, bmRegistry);
			} else if (c.isAnyCDC()) {
				return new CdcAttributesDoc(docgenCfg, c, bmRegistry);
			} else if (c.isAnyDA()) {
				return new DaAttributesDoc(docgenCfg, TableSpec.CDA_ATTRS, c, bmRegistry);
			} else if (isNonDaWithinPackages(c, docgenCfg.daPckNames)
					|| isNonDaWithinPackages(c, docgenCfg.basicPckNames)) {
				return new DaAttributesDoc(docgenCfg, TableSpec.CTA_ATTRS, c, bmRegistry);
			} else {
				// FIXME: Still keeping groups for DA, although never used, in case we need
				// for something in 7-2; This "else" is also on hold for 7-2 specials.
				return new DaAttributesDoc(docgenCfg, TableSpec.ODA_ATTRS, c, bmRegistry);
			}
		}

		// defaults (regular UML, like for CIM):
		if (c.isEnumeratedType()) {
			return new DefaultLiteralsDoc(docgenCfg, c, bmRegistry);
		}
		return new DefaultAttributesDoc(docgenCfg, c, bmRegistry);
	}

	private static boolean isNonDaWithinPackages(UmlClass c, Collection<String> pckNames) {
		for (String daPckName : pckNames) {
			if (c.getContainingPackage().isInOrUnderPackage(daPckName)) {
				return true;
			}
		}
		return false;
	}

	private static PropertiesDoc createAssocEndsDoc(DocgenConfig docgenCfg, UmlClass c,
			BookmarkRegistry bmRegistry) {
		if (c.getNature() == Nature.IEC61850) {
			return new CustomAssocEndsDoc(docgenCfg, c, bmRegistry);
		}
		return new DefaultAssocEndsDoc(docgenCfg, c, bmRegistry);
	}

	private static OperationsDoc createOperationsDoc(DocgenConfig docgenCfg, UmlClass c,
			BookmarkRegistry bmRegistry) {
		return new OperationsDoc(docgenCfg, c, bmRegistry);
	}

	private static List<FigureDoc> createFiguresDoc(DocgenConfig docgenCfg, UmlClass c,
			BookmarkRegistry bmRegistry) {
		List<FigureDoc> result = new ArrayList<FigureDoc>(c.getDiagrams().size());
		for (UmlDiagram d : c.getDiagrams()) {
			result.add(new FigureDocImpl(docgenCfg, d, bmRegistry));
		}
		return result;
	}

	// ===== Impl. of org.tanjakostic.jcleancim.docgen.collector.ClassDoc methods =====

	@Override
	public String getClassPlaceholderName() {
		return _classQName;
	}

	@Override
	public TextDescription getInheritancePath() {
		return _inheritancePath;
	}

	@Override
	public PropertiesDoc getAttributesDoc() {
		return _attributesDoc;
	}

	@Override
	public PropertiesDoc getAssocEndsDoc() {
		return _assocEndsDoc;
	}

	@Override
	public PropertiesDoc getOperationsDoc() {
		return _operationsDoc;
	}

	@Override
	public List<FigureDoc> getDiagramDocs() {
		return Collections.unmodifiableList(_diagramDocs);
	}

	// ===== Impl. of org.tanjakostic.jcleancim.docgen.collector.ObjectDoc methods =====

	@Override
	public TextDescription getDescription() {
		return _extendedDescription != null ? _extendedDescription : super.getDescription();
	}
}
