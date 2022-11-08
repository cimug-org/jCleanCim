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
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;
import org.tanjakostic.jcleancim.docgen.collector.BookmarkRegistry;
import org.tanjakostic.jcleancim.docgen.collector.DocgenConfig;
import org.tanjakostic.jcleancim.docgen.collector.RawData;
import org.tanjakostic.jcleancim.docgen.collector.RawDataImpl;
import org.tanjakostic.jcleancim.docgen.collector.TableSpec;
import org.tanjakostic.jcleancim.docgen.collector.WAX;
import org.tanjakostic.jcleancim.model.AbstractUmlObject;
import org.tanjakostic.jcleancim.model.TextDescription;
import org.tanjakostic.jcleancim.model.TextDescription.TextKind;
import org.tanjakostic.jcleancim.model.UmlAttribute;
import org.tanjakostic.jcleancim.model.UmlClass;
import org.tanjakostic.jcleancim.util.Util;

/**
 * Data required for documentation of all attributes of a package as a data index (required for
 * IEC61850, but can be used for CIM as well).
 * <p>
 * Here the layout you may use for
 * {@link org.tanjakostic.jcleancim.docgen.collector.PlaceholderSpec.Kind#DATA_INDEX}:
 *
 * <pre>
 * package.getHeadingText()
 *     this (table, like in PropertiesDoc)
 * </pre>
 *
 * @author tatjana.kostic@ieee.org
 * @author laurent.guise@art-et-histoire.com
 * @version $Id: DataIndexDoc.java 31 2019-12-08 01:19:54Z dev978 $
 */
class DataIndexDoc extends Attributes61850Doc {
	private static final Logger _logger = Logger.getLogger(DataIndexDoc.class.getName());

	public static final String INTRO_FMT_DI = " shows all attributes defined on classes of %s package.";
	public static final String CAPTION_FMT_DI = "Attributes defined on classes of %s package";

	public static final String HEADING = "Data semantics";

	/**
	 * Constructor.
	 * <p>
	 * Note: To allow for special handling of IEC 61850-7-3 data semantic table, we simply skip
	 * attribute names starting with one or two underscores if not in debug mode (with
	 * {@link DocgenConfig#writeUmlTypes} set to true).
	 */
	public DataIndexDoc(DocgenConfig docgenCfg, Map<String, List<UmlAttribute>> attributesPerName,
			String packageName, BookmarkRegistry bmRegistry) {
		super(docgenCfg, null, "idx", HEADING, String.format(INTRO_FMT_DI, packageName),
				String.format(CAPTION_FMT_DI, packageName), TableSpec.ATTR_INDEX, null, bmRegistry);

		log(_logger, "---- collecting doc for attributes of " + packageName + " ...");

		String bookmarkID = null; // we don't point from hyperlinks to any attribute here

		for (Map.Entry<String, List<UmlAttribute>> entry : attributesPerName.entrySet()) {
			String name = entry.getKey();
			List<UmlAttribute> attributes = entry.getValue();
			if (attributes.size() > 1) {
				_logger.debug("Attribute '" + name + "' defined on multiple classes in and under '"
						+ packageName + "' - may be possible to avoid duplication by using "
						+ "inheritance.");
			}

			List<UmlClass> types = new ArrayList<UmlClass>();
			List<String> descriptions = new ArrayList<String>();
			TextKind txtKind = TextKind.textNoNL;
			StringBuilder sbHtml = new StringBuilder();
			boolean isSgSeFcda = false;
			for (int i = 0; i < attributes.size(); ++i) {
				UmlAttribute attr = attributes.get(i);
				if (toSkip(attr)) {
					continue;
				}
				if (toSkipSgSeFcda(attr)) {
					isSgSeFcda = true;
					continue;
				}

				if (i > 0 && attr.getDescription().kind != TextDescription.DEFAULT_KIND) {
					txtKind = TextKind.textWithNL;
				}
				types.add(attr.getType());

				String owningType = "(" + prepareForHyperlink(attr.getContainingClass()) + ") ";
				String deprec = getDeprecatedTextAsPrefix(attr);
				String prefix = owningType + deprec;
				TextDescription desc = attr.getDescription().prepend(prefix, attr);
				TextDescription descHtml = attr.getHtmlDescription().prepend(prefix, attr);

				descriptions.add(desc.text);
				sbHtml.append(descHtml.text);
			}
			if (isSgSeFcda) {
				continue;
			}

			String typeText = deduceTypeText(types);
			TextDescription raw = new TextDescription(
					Util.concatCharSeparatedTokens(Util.NL, descriptions), txtKind);
			TextDescription html = new TextDescription(sbHtml.toString(), TextKind.htmlSnippet);
			CellText desc = deduceCellText(null, raw, html, null); // no context here
			addEntry(EntryDocImpl.createData(bookmarkID, desc.formatInfo, name, typeText,
					desc.text));
		}

		log(_logger, ">>>>>> " + toString());
	}

	/** FIXME: hack to avoid printing _setVal and __setVal etc. attributes in IEC 61850-7-3... */
	private boolean toSkipSgSeFcda(UmlAttribute a) {
		if (getDocgenCfg().writeUmlTypes) {
			return false;
		}
		return (a.getType().isAnyFCDA() && a.getName().startsWith("_"));
	}

	private String deduceTypeText(List<UmlClass> types) {
		if (types.size() > 1) {
			List<String> typeNames = AbstractUmlObject.collectNames(types);
			Set<String> uniqueTypeNames = new LinkedHashSet<String>(typeNames);
			if (uniqueTypeNames.size() == 1) {
				UmlClass type = types.get(0);
				RawData outRawCells = new RawDataImpl();
				deduceTypeTextForDataIndex(type, outRawCells);

				return getDocgenCfg().writeUmlTypes ? type.getName()
						: outRawCells.getCell(WAX.A_deducedTypeText);
			}
		}

		List<String> typeNames = new ArrayList<String>();
		for (UmlClass type : types) {
			RawData outRawCells = new RawDataImpl();
			deduceTypeTextForDataIndex(type, outRawCells);

			String typeName = getDocgenCfg().writeUmlTypes ? type.getName()
					: outRawCells.getCell(WAX.A_deducedTypeText);
			typeNames.add(typeName);
		}

		return Util.concatCharSeparatedTokens(", ", typeNames);
	}
}
