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

import java.util.List;

import org.tanjakostic.jcleancim.common.Config;
import org.tanjakostic.jcleancim.common.Nature;
import org.tanjakostic.jcleancim.model.NamespaceInfo;
import org.tanjakostic.jcleancim.model.TextDescription;

/**
 * Data required for documentation of packages; documentation includes classes and sub-packages.
 * Creating this instance for a root package results in creation of the doc data for the whole
 * package contents.
 * <p>
 * Here the layout you may use for "regular" package, with
 * {@link org.tanjakostic.jcleancim.docgen.collector.PlaceholderSpec.Kind#PACKAGE}:
 *
 * <pre>
 * getHeadingText()
 *     getGenHeadingText()
 *         getNsUriAndPrefix()     // if not empty and if enabled by configuration, you can print
 *         getDescription()
 *         getDiagramDocs()        // loop
 *     getClassDocs()              // loop
 *     getChildPackageDocs()       // loop
 * </pre>
 *
 * This kind of documentation is needed for printing the full content of the relevant packages in
 * both CIM and IEC61850 domains.
 * <p>
 * If the package has been configured to print data index in place of
 * {@link org.tanjakostic.jcleancim.docgen.collector.PlaceholderSpec.Kind#DATA_INDEX} (i.e.,
 * {@link Config#getValidationPackagesDataIndex()} is not empty), then {@link #getDataIndexDoc()}
 * will be non-null and can be used. This kind of documentation is needed for data index clauses in
 * IEC61850-7-4 and IEC61850-7-3.
 * <p>
 * If the package has been configured to print enums as XML in place of
 * {@link org.tanjakostic.jcleancim.docgen.collector.PlaceholderSpec.Kind#SCL_ENUMS} (i.e.,
 * {@link Config#getValidationIec61850PackagesEnumsXml()} is not empty), then
 * {@link #getEnumsPackageScl()} will be non-null and can be used. This kind of documentation is
 * needed for annexes listing enums as XML in IEC61850-7-4 and IEC61850-7-3.
 * <p>
 * If the package has been configured to print the functional constraints table in place of
 * {@link org.tanjakostic.jcleancim.docgen.collector.PlaceholderSpec.Kind#FCS} (i.e.,
 * {@link Config#getValidationIec61850PackageFc()} is not null), then {@link #getFcPackageDoc()}
 * will be non-null and can be used. This kind of documentation is needed for a subclause in
 * IEC61850-7-2 and an annex in IEC61850-7-3.
 * <p>
 * If the package has been configured to print the trigger options table in place of
 * {@link org.tanjakostic.jcleancim.docgen.collector.PlaceholderSpec.Kind#TRGOPS} (i.e.,
 * {@link Config#getValidationIec61850PackageTrgOp()} is not null), then
 * {@link #getTrgOpPackageDoc()} will be non-null and can be used. This kind of documentation is
 * needed for a subclause in IEC61850-7-2.
 * <p>
 * If the package has been configured to print data object abbreviations in place of
 * {@link org.tanjakostic.jcleancim.docgen.collector.PlaceholderSpec.Kind#ABBREVIATIONS} (i.e.,
 * {@link Config#getValidationIec61850PackagesDoAbbr()} is not null), then
 * {@link #getAbbrPackageDoc()} will be non-null and can be used. This kind of documentation is
 * needed for Abbreviations clause in IEC61850-7-4.
 * <p>
 * If the package has been configured to print presence conditions in place of
 * {@link org.tanjakostic.jcleancim.docgen.collector.PlaceholderSpec.Kind#PRES_CONDITIONS} (i.e.,
 * {@link Config#getValidationIec61850PackagePresCond()} is not null), then
 * {@link #getPresCondPackageDoc()} will be non-null and can be used. This kind of documentation is
 * needed for Presence conditions clause in IEC61850-7-3.
 * <p>
 * If the package has been configured to print LN mapings in place of
 * {@link org.tanjakostic.jcleancim.docgen.collector.PlaceholderSpec.Kind#LNMAP_PACKAGE} (i.e.,
 * {@link Config#getValidationIec61850PackageLnMaps()} is not null), then
 * {@link #getLnMapPackageDoc()} will be non-null and can be used. This kind of documentation is
 * needed for one clause in IEC61850-7-4 (where we have to show tables of "mappings" between
 * requirements LNs of IEC61850-5, and real LNs of IEC61850-7-4).
 *
 * @author tatjana.kostic@ieee.org
 * @version $Id: PackageDoc.java 34 2019-12-20 18:37:17Z dev978 $
 */
public interface PackageDoc extends ObjectDoc {

	public static final String PKG_TITLE_PREFIX_FMT_IEC61850 = "<<%s>> ";

	/** (deprecated, informative, custom), package label, name */
	public static final String HEADING_FORMAT = "%s%s%s";

	/** (IEC 61850) Format to use for logical node packages: alias (UML name). */
	public static String LNPKG_HEADING_FORMAT = "%s (%s)";

	/** IEC does not allow hanging paragraphs, so we include a sub-clause per package. */
	public static final String HANGING_PARA_TITLE = "General";

	/** nsuri = uri, nsprefix = prefix */
	public static final String NAMESPACE_FORMAT = "[%s = %s, %s = %s]";

	/**
	 * Returns the name of the package. This gives the writer the name of the package present as
	 * placeholder in the template, and to avoid search through the model.
	 */
	public String getPackageName();

	/**
	 * Returns name space information if it is defined for the package, null otherwise. This is the
	 * means to identify content for fixed-format documentation output (such as XML).
	 */
	public NamespaceInfo getNamespaceInfo();

	/**
	 * Returns name of the model package (the one with nature) to which it belongs.
	 */
	public String getModelName();

	/**
	 * Returns nature of the package.
	 */
	public Nature getNature();

	/**
	 * To avoid hanging paragraphs (i.e., those with some text but without title), ensure to include
	 * a "general" heading and print the doc and diagrams of the package under it.
	 */
	public String getGenHeadingText();

	/**
	 * Returns formatted namespace URI and prefix, suitable to be printed in a single paragraph.
	 * Consider printing only if the returned value is not empty.
	 */
	public TextDescription getNsUriAndPrefix();

	/**
	 * Returns documentation for all the figures in this package.
	 */
	public List<FigureDoc> getFigureDocs();

	/**
	 * Returns documentation for all the classes in this package.
	 */
	public List<ClassDoc> getClassDocs();

	/**
	 * Returns documentation for all the child packages of this package.
	 */
	public List<PackageDoc> getChildPackageDocs();

	public PropertiesDoc getDataIndexDoc();

	public PropertiesDoc getLnMapPackageDoc();

	public PropertiesDoc getPresCondPackageDoc();

	public PropertiesDoc getFcPackageDoc();

	public PropertiesDoc getTrgOpPackageDoc();

	public PropertiesDoc getAbbrPackageDoc();

	public PackageScl getEnumsPackageScl();
}
