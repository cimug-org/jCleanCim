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

import org.apache.log4j.Logger;
import org.tanjakostic.jcleancim.common.Nature;
import org.tanjakostic.jcleancim.docgen.collector.BookmarkRegistry;
import org.tanjakostic.jcleancim.docgen.collector.ClassDoc;
import org.tanjakostic.jcleancim.docgen.collector.DocCollector;
import org.tanjakostic.jcleancim.docgen.collector.DocgenConfig;
import org.tanjakostic.jcleancim.docgen.collector.FigureDoc;
import org.tanjakostic.jcleancim.docgen.collector.PackageDoc;
import org.tanjakostic.jcleancim.docgen.collector.PackageScl;
import org.tanjakostic.jcleancim.docgen.collector.PropertiesDoc;
import org.tanjakostic.jcleancim.docgen.collector.WAX;
import org.tanjakostic.jcleancim.model.Namespace;
import org.tanjakostic.jcleancim.model.NamespaceInfo;
import org.tanjakostic.jcleancim.model.TextDescription;
import org.tanjakostic.jcleancim.model.UML;
import org.tanjakostic.jcleancim.model.UmlAttribute;
import org.tanjakostic.jcleancim.model.UmlClass;
import org.tanjakostic.jcleancim.model.UmlDiagram;
import org.tanjakostic.jcleancim.model.UmlPackage;
import org.tanjakostic.jcleancim.model.UmlStereotype;
import org.tanjakostic.jcleancim.util.Util;

/**
 * Data required for documentation of packages. For the layout, see {@link PackageDoc}.
 *
 * @author tatjana.kostic@ieee.org
 * @version $Id: PackageDocImpl.java 34 2019-12-20 18:37:17Z dev978 $
 */
class PackageDocImpl extends AbstractObjectDoc implements PackageDoc {
	private static final Logger _logger = Logger.getLogger(PackageDocImpl.class.getName());

	private final String _packageName;
	private final NamespaceInfo _nsInfo;
	private final String _modelName;
	private final Nature _nature;
	private final String _genHeadingText;
	private final TextDescription _nsUriAndPrefix; // FIXME: move to _nsInfo;
	private final List<FigureDoc> _figureDocs = new ArrayList<FigureDoc>();
	private final List<ClassDoc> _classDocs = new ArrayList<ClassDoc>();
	private final List<PackageDoc> _childPackageDocs = new ArrayList<PackageDoc>();

	private final PropertiesDoc _dataIndexDoc;
	private final PropertiesDoc _lnMapPackageDoc;
	private final PropertiesDoc _presCondPackageDoc;
	private final PropertiesDoc _fcPackageDoc;
	private final PropertiesDoc _trgOpPackageDoc;
	private final PropertiesDoc _abbrPackageDoc;
	private final PackageScl _enumsPackageScl;

	/**
	 * Constructor, to be called on model package.
	 *
	 * @param docgenCfg
	 * @param pckage
	 * @param collector
	 */
	PackageDocImpl(DocgenConfig docgenCfg, UmlPackage pckage, DocCollector collector) {
		this(docgenCfg, pckage, collector, pckage.getName(), 0);
	}

	private PackageDocImpl(DocgenConfig docgenCfg, UmlPackage pckage, DocCollector collector,
			String modelName, int headingOffset) {
		super(docgenCfg, pckage, "pck", pckage.getDescription(), pckage.getHtmlDescription(), false,
				deduceHeadingText(docgenCfg, pckage), null, collector.getBmRegistry());

		_packageName = pckage.getName();
		_modelName = modelName;
		_nature = pckage.getNature();
		_nsInfo = initNamespace(docgenCfg, pckage);
		_logger.info("    collecting doc for package " + Util.getIndentSpaces(headingOffset)
				+ _packageName + " ...");

		// this must be called after the above has been initialised:
		collector.addToFlattened(this);
		if (_nsInfo != null) {
			collector.addToScoped(this);
		}

		_genHeadingText = HANGING_PARA_TITLE;
		_nsUriAndPrefix = createNsUriAndPrefixText(pckage);

		putCell(WAX.A_subtitleID, createDocId(pckage, "subtitleID"));
		putCell(WAX.A_subtitle, _genHeadingText);
		if (_nature == Nature.IEC61850) {
			putCell(WAX.A_kind, deduce61850PackageKind(docgenCfg, pckage));
		}

		for (UmlDiagram d : pckage.getDiagrams()) {
			_figureDocs.add(new FigureDocImpl(docgenCfg, d, collector.getBmRegistry()));
		}

		for (UmlClass c : pckage.getClasses()) {
			if (toSkip(c)) {
				continue;
			}
			ClassDocImpl classDoc = new ClassDocImpl(docgenCfg, c, collector.getBmRegistry(),
					headingOffset + 1);
			_classDocs.add(classDoc);
			collector.addToFlattened(classDoc);
		}

		for (UmlPackage p : pckage.getChildPackages()) {
			if (toSkip(p)) {
				collector.addSkippedInformativePackage(p.getQualifiedName());
				continue;
			}
			_childPackageDocs.add(
					new PackageDocImpl(docgenCfg, p, collector, modelName, (headingOffset + 1)));
		}

		// these are IEC61850 specials, all applicable to a package:
		_dataIndexDoc = createDataIndexDoc(pckage, collector.getBmRegistry());
		_lnMapPackageDoc = createLnMapDoc(pckage);
		_presCondPackageDoc = createPresCondPackageDoc(pckage, collector.getBmRegistry());
		_fcPackageDoc = createFcPackageDoc(pckage, collector.getBmRegistry());
		_trgOpPackageDoc = createTrgOpPackageDoc(pckage, collector.getBmRegistry());
		_abbrPackageDoc = createAbbrPackageDoc(pckage);
		_enumsPackageScl = createEnumsPackageScl(pckage);
	}

	private NamespaceInfo initNamespace(DocgenConfig docgenCfg, UmlPackage pckage) {
		if (getDocgenCfg().owners.contains(pckage.getOwner())) {
			if (Nature.CIM == _nature && pckage.getKind() == UmlPackage.Kind.TOP
					&& pckage.getNamespaceInfo() != null) {
				return pckage.getNamespaceInfo();
			} else if (Nature.IEC61850 == _nature
					&& docgenCfg.docgenPckNames.contains(pckage.getName())
					&& pckage.getContainingPackage() != null
					&& pckage.getContainingPackage().getNamespaceInfo() != null) {
				return pckage.getContainingPackage().getNamespaceInfo();
			}
		}
		return null;
	}

	private static String deduce61850PackageKind(DocgenConfig docgenCfg, UmlPackage pckage) {
		for (String pName : docgenCfg.lnPckNames) {
			if (pckage.isInOrUnderPackage(pName)) {
				return "LogicalNodes";
			}
		}
		for (String pName : docgenCfg.cdcPckNames) {
			if (pckage.isInOrUnderPackage(pName)) {
				return "CommonDataClasses";
			}
		}
		for (String pName : docgenCfg.basicPckNames) {
			if (pckage.isInOrUnderPackage(pName)) {
				return "CoreTypes";
			}
		}
		for (String pName : docgenCfg.daPckNames) {
			if (pckage.isInOrUnderPackage(pName)) {
				return "ConstructedDAs";
			}
		}
		for (String pName : docgenCfg.enumsXmlPckNames) {
			if (pckage.isInOrUnderPackage(pName)) {
				return "Enums";
			}
		}
		return "Other";
	}

	private TextDescription createNsUriAndPrefixText(UmlPackage p) {
		String ns = "";
		if (p.getNamespace() != Namespace.EMPTY) {
			ns = String.format(NAMESPACE_FORMAT, UML.TVN_nsuri, p.getNamespace().getUri(),
					UML.TVN_nsprefix, p.getNamespace().getPrefix());
		}
		return new TextDescription(ns);
	}

	public static String deduceHeadingText(DocgenConfig docgenCfg, UmlPackage p) {
		String fmt = (p.getNature() == Nature.CIM) ? DEFAULT_PREFIX_FMT
				: PKG_TITLE_PREFIX_FMT_IEC61850;

		String qualifiersText = deduceQualifiersPrefix(p, UmlStereotype.getPackageBuiltIns(),
				docgenCfg.showCustomStereotypes, fmt);

		String label = p.getAlias().isEmpty() ? (Util.capitalise(p.getKind().getLabel()) + " ")
				: "";
		String mainTitle = p.getAlias().isEmpty() ? p.getName() : p.getAlias();

		if (p.getNature() == Nature.IEC61850 && !p.getAlias().isEmpty()) {
			for (String lnPckName : docgenCfg.lnPckNames) {
				if (p.isUnderPackage(lnPckName)) {
					return String.format(LNPKG_HEADING_FORMAT, p.getAlias(), p.getName());
				}
			}
		}

		return String.format(HEADING_FORMAT, qualifiersText, label, mainTitle);
	}

	public PropertiesDoc createDataIndexDoc(UmlPackage pckage, BookmarkRegistry bmRegistry) {
		PropertiesDoc result = null;
		Collection<String> pNames = getDocgenCfg().dataIndexPckNames;
		String pName = pckage.getName();
		if (pNames != null && pNames.contains(pName)) {
			Map<String, List<UmlAttribute>> orderedAttributes = pckage.getModel()
					.findAttributesWithDuplicates(pName, false, true, true);
			result = new DataIndexDoc(getDocgenCfg(), orderedAttributes, pName, bmRegistry);
			_logger.info("    collected data index for " + pName + " ...");
			_logger.trace(result.toString() + Util.NL);
		}
		return result;
	}

	public PropertiesDoc createFcPackageDoc(UmlPackage pckage, BookmarkRegistry bmRegistry) {
		PropertiesDoc result = null;
		String pName = getDocgenCfg().fcPckName;
		if (pName != null && pName.equals(pckage.getName())) {
			boolean includeLiterals = true;
			boolean includeNonLiterals = false;
			List<UmlAttribute> literals = pckage.getModel().findAttributes(pckage.getName(),
					includeLiterals, includeNonLiterals);
			result = new FcPackageDoc(getDocgenCfg(), literals, pckage, bmRegistry);
			_logger.info("    collected functional constraints from " + pckage.getName() + " ...");
			_logger.trace(result.toString() + Util.NL);
		}
		return result;
	}

	public PropertiesDoc createTrgOpPackageDoc(UmlPackage pckage, BookmarkRegistry bmRegistry) {
		PropertiesDoc result = null;
		String pName = getDocgenCfg().trgOpPckName;
		if (pName != null && pName.equals(pckage.getName())) {
			boolean includeLiterals = true;
			boolean includeNonLiterals = false;
			List<UmlAttribute> literals = pckage.getModel().findAttributes(pckage.getName(),
					includeLiterals, includeNonLiterals);
			result = new TrgOpPackageDoc(getDocgenCfg(), literals, pckage, bmRegistry);
			_logger.info("    collected trigger options from " + pckage.getName() + " ...");
			_logger.trace(result.toString() + Util.NL);
		}
		return result;
	}

	public PropertiesDoc createPresCondPackageDoc(UmlPackage pckage, BookmarkRegistry bmRegistry) {
		PropertiesDoc result = null;
		String pName = getDocgenCfg().presCondPckName;
		if (pName != null && pName.equals(pckage.getName())) {
			boolean includeLiterals = true;
			boolean includeNonLiterals = false;
			List<UmlAttribute> literals = pckage.getModel().findAttributes(pckage.getName(),
					includeLiterals, includeNonLiterals);
			result = new PresCondPackageDoc(getDocgenCfg(), literals, pckage, bmRegistry);
			_logger.info("    collected presence conditions from " + pckage.getName() + " ...");
			_logger.trace(result.toString() + Util.NL);
		}
		return result;
	}

	public PropertiesDoc createAbbrPackageDoc(UmlPackage pckage) {
		PropertiesDoc result = null;
		Collection<String> pNames = getDocgenCfg().doAbbrPckNames;
		String pName = pckage.getName();
		if (pNames != null && pNames.contains(pName)) {
			List<UmlAttribute> orderedLiterals = pckage.getModel().findAttributes(pckage.getName(),
					true, false, true);
			result = new AbbrPackageDoc(getDocgenCfg(), orderedLiterals, pckage, getBmRegistry());
			_logger.info("    collected abbreviations from " + pckage.getName() + " ...");
			_logger.trace(result.toString() + Util.NL);
		}
		return result;
	}

	public PropertiesDoc createLnMapDoc(UmlPackage pckage) {
		PropertiesDoc result = null;
		String pName = getDocgenCfg().lnMapPckName;
		if (pName != null && pName.contains(pckage.getName())) {
			List<UmlClass> classes = new ArrayList<UmlClass>();
			for (UmlPackage childPackage : pckage.getChildPackages()) {
				classes.addAll(childPackage.getClasses());
			}
			result = new LnMapPackageDoc(getDocgenCfg(), classes, pckage, getBmRegistry());
			_logger.info("    collected LN mappings for " + pckage.getName() + " ...");
			_logger.trace(result.toString() + Util.NL);
		}
		return result;
	}

	public PackageScl createEnumsPackageScl(UmlPackage pckage) {
		PackageScl result = null;
		Collection<String> pName = getDocgenCfg().enumsXmlPckNames;
		if (pName != null && pName.contains(pckage.getName())) {
			Collection<UmlClass> retaineds = new ArrayList<UmlClass>();
			super.filterClasses(pckage, retaineds);

			result = new EnumsScl(retaineds, pckage.getName());
			_logger.info("    collected SCL from " + pckage.getName() + " ...");
			_logger.trace(result.toString() + Util.NL);
		}
		return result;
	}

	// ===== Impl. of org.tanjakostic.jcleancim.docgen.collector.PackageDoc methods =====

	@Override
	public String getPackageName() {
		return _packageName;
	}

	@Override
	public NamespaceInfo getNamespaceInfo() {
		return _nsInfo;
	}

	@Override
	public String getModelName() {
		return _modelName;
	}

	@Override
	public Nature getNature() {
		return _nature;
	}

	@Override
	public String getGenHeadingText() {
		return _genHeadingText;
	}

	@Override
	public TextDescription getNsUriAndPrefix() {
		return _nsUriAndPrefix;
	}

	@Override
	public List<FigureDoc> getFigureDocs() {
		return Collections.unmodifiableList(_figureDocs);
	}

	@Override
	public List<ClassDoc> getClassDocs() {
		return Collections.unmodifiableList(_classDocs);
	}

	@Override
	public List<PackageDoc> getChildPackageDocs() {
		return Collections.unmodifiableList(_childPackageDocs);
	}

	@Override
	public PropertiesDoc getDataIndexDoc() {
		return _dataIndexDoc;
	}

	@Override
	public PropertiesDoc getLnMapPackageDoc() {
		return _lnMapPackageDoc;
	}

	@Override
	public PropertiesDoc getPresCondPackageDoc() {
		return _presCondPackageDoc;
	}

	@Override
	public PropertiesDoc getFcPackageDoc() {
		return _fcPackageDoc;
	}

	@Override
	public PropertiesDoc getTrgOpPackageDoc() {
		return _trgOpPackageDoc;
	}

	@Override
	public PropertiesDoc getAbbrPackageDoc() {
		return _abbrPackageDoc;
	}

	@Override
	public PackageScl getEnumsPackageScl() {
		return _enumsPackageScl;
	}
}
