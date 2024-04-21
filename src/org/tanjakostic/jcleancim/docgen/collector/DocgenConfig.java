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

package org.tanjakostic.jcleancim.docgen.collector;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.EnumSet;
import java.util.LinkedHashSet;
import java.util.List;

import org.tanjakostic.jcleancim.common.Config;
import org.tanjakostic.jcleancim.common.OwningWg;

/**
 * Subset of {@link Config} relevant for documentation generation.
 *
 * @author tatjana.kostic@ieee.org
 * @version $Id: DocgenConfig.java 34 2019-12-20 18:37:17Z dev978 $
 */
public class DocgenConfig {

	private static final List<String> EMPTY_STRINGS = Collections
			.unmodifiableList(new ArrayList<String>());

	public final boolean includeInf;
	public final boolean includeNonPublic;
	public final boolean keepHtml;
	public final boolean showCustomStereotypes;
	public final Collection<String> skipForCustomStereotypes;
	public final Collection<String> showNamespacePackages;
	public final boolean useHyperlinks;
	public final boolean includeInheritancePath;
	public final EnumSet<OwningWg> owners;

	public final boolean includeInhFromMetamodel;
	public final boolean writeUmlTypes;

	public final Collection<String> docgenPckNames;
	public final Collection<String> dataIndexPckNames;
	public final String lnMapPckName;
	public final String presCondPckName;
	public final String fcPckName;
	public final String trgOpPckName;
	public final Collection<String> doAbbrPckNames;
	public final Collection<String> enumsXmlPckNames;
	public final Collection<String> lnPckNames;
	public final Collection<String> cdcPckNames;
	public final Collection<String> daPckNames;
	public final Collection<String> basicPckNames;

	/**
	 * Constructor for CIM-like simple printing; for XML all CIM owners are included.
	 *
	 * @param includeInf
	 * @param includeNonPublic
	 * @param printHtml
	 * @param includeInheritancePath
	 */
	public DocgenConfig(boolean includeInf, boolean includeNonPublic, boolean printHtml,
			boolean showCustomStereotypes, Collection<String> skipForCustomStereotypes,
			Collection<String> showNamespacePackages, boolean includeInheritancePath) {
		this(includeInf, includeInheritancePath, printHtml, showCustomStereotypes,
				skipForCustomStereotypes, showNamespacePackages, false, includeNonPublic,
				EnumSet.of(OwningWg.WG13, OwningWg.WG14, OwningWg.WG16, OwningWg.OTHER_CIM), false,
				false, EMPTY_STRINGS, EMPTY_STRINGS, "", "", "", "", EMPTY_STRINGS, EMPTY_STRINGS,
				EMPTY_STRINGS, EMPTY_STRINGS, EMPTY_STRINGS, EMPTY_STRINGS);
	}

	/**
	 * Constructor for IEC61850 printing.
	 */
	public DocgenConfig(boolean includeInf, boolean includeNonPublic, boolean printHtml,
			boolean showCustomStereotypes, Collection<String> skipForCustomStereotypes,
			Collection<String> showNamespacePackages, boolean useHyperlinks,
			boolean includeInheritancePath, EnumSet<OwningWg> owners,
			boolean includeInhFromMetamodel, boolean writeUmlTypes,
			Collection<String> docgenPckNames, Collection<String> dataIndexPckNames,
			String lnMapPckName, String presCondPckName, String fcPckName, String trgOpPckName,
			Collection<String> doAbbrPckNames, Collection<String> enumsXmlPckNames,
			Collection<String> lnPckNames, Collection<String> cdcPckNames,
			Collection<String> daPckNames, Collection<String> basicPckNames) {
		this.includeInf = includeInf;
		this.includeNonPublic = includeNonPublic;
		this.keepHtml = printHtml;
		this.showCustomStereotypes = showCustomStereotypes;
		this.skipForCustomStereotypes = new LinkedHashSet<String>(skipForCustomStereotypes);
		this.showNamespacePackages = new LinkedHashSet<String>(showNamespacePackages);
		this.useHyperlinks = useHyperlinks;
		this.includeInheritancePath = includeInheritancePath;
		this.owners = owners;

		this.includeInhFromMetamodel = includeInhFromMetamodel;
		this.writeUmlTypes = writeUmlTypes;

		this.docgenPckNames = docgenPckNames;
		this.dataIndexPckNames = new LinkedHashSet<String>(dataIndexPckNames);
		this.lnMapPckName = lnMapPckName;
		this.presCondPckName = presCondPckName;
		this.fcPckName = fcPckName;
		this.trgOpPckName = trgOpPckName;
		this.doAbbrPckNames = doAbbrPckNames;
		this.enumsXmlPckNames = new LinkedHashSet<String>(enumsXmlPckNames);
		this.lnPckNames = new LinkedHashSet<String>(lnPckNames);
		this.cdcPckNames = new LinkedHashSet<String>(cdcPckNames);
		this.daPckNames = new LinkedHashSet<String>(daPckNames);
		this.basicPckNames = new LinkedHashSet<String>(basicPckNames);
	}

	/**
	 * Constructor.
	 *
	 * @param cfg
	 */
	public DocgenConfig(Config cfg) {
		this(cfg.isDocgenIncludeInformative(), cfg.isDocgenIncludeNonPublic(),
				cfg.isDocgenPrintHtml(), cfg.isDocgenShowCustomStereotypes(),
				cfg.getDocgenSkipForCustomStereotypes(), cfg.getDocgenShowNamespacePackages(),
				cfg.isDocgenWordUseHyperlinks(), cfg.isDocgenWordIncludeInheritancePath(),
				cfg.getDocgenXmlScope(), cfg.isDocgenIec61850IncludeMetamodelInheritance(),
				cfg.isDocgenIec61850WriteUmlTypes(), cfg.getValidationIec61850PackagesDocgen(),
				cfg.getValidationPackagesDataIndex(), cfg.getValidationIec61850PackageLnMaps(),
				cfg.getValidationIec61850PackagePresCond(), cfg.getValidationIec61850PackageFc(),
				cfg.getValidationIec61850PackageTrgOp(), cfg.getValidationIec61850PackagesDoAbbr(),
				cfg.getValidationIec61850PackagesEnumsXml(), cfg.getValidationIec61850PackagesLn(),
				cfg.getValidationIec61850PackagesCdc(), cfg.getValidationIec61850PackagesDa(),
				cfg.getValidationIec61850PackagesBasic());
	}
}
