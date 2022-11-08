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

package org.tanjakostic.jcleancim.docgen.writer.xml;

import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.log4j.Logger;
import org.tanjakostic.jcleancim.common.Nature;
import org.tanjakostic.jcleancim.docgen.collector.ClassDoc;
import org.tanjakostic.jcleancim.docgen.collector.EntryDoc;
import org.tanjakostic.jcleancim.docgen.collector.GroupsSpec;
import org.tanjakostic.jcleancim.docgen.collector.PackageDoc;
import org.tanjakostic.jcleancim.docgen.collector.PropertiesDoc;
import org.tanjakostic.jcleancim.docgen.collector.RawData;
import org.tanjakostic.jcleancim.docgen.collector.TableSpec;
import org.tanjakostic.jcleancim.docgen.collector.WAX;
import org.tanjakostic.jcleancim.docgen.writer.AbstractWriter;
import org.tanjakostic.jcleancim.model.NamespaceInfo;
import org.tanjakostic.jcleancim.util.Util;
import org.tanjakostic.jcleancim.xml.XmlInstanceDOM;
import org.tanjakostic.jcleancim.xml.XmlParsingException;
import org.tanjakostic.jcleancim.xml.XmlSchemaDOM;
import org.w3c.dom.Element;

/**
 * Writes UML model content in XML format for Web access.
 *
 * @author tatjana.kostic@ieee.org
 * @version $Id: WAXWriter.java 31 2019-12-08 01:19:54Z dev978 $
 */
public class WAXWriter extends AbstractWriter {
	private static final Logger _logger = Logger.getLogger(WAXWriter.class.getName());

	public static final List<String> FILE_EXTENSIONS = Arrays.asList(".xml");

	private final WAXWriterInput _input;

	private WAXDocument _spec;
	private WAXDocument _doc;

	/**
	 * Constructs this instance and copies schemas into output directory.
	 *
	 * @throws IOException
	 *             if fails to copy schemas into output directory.
	 */
	public WAXWriter(WAXWriterInput input) throws IOException {
		super(input);

		_input = input;

		copySchemaToOutput();
	}

	private void copySchemaToOutput() throws IOException {
		File src = new File(getInput().getInXsdWebaccessPath());
		File dst = new File(getInput().getOutXsdWebaccessPath());
		Util.copy(src, dst);
	}

	@Override
	public WAXWriterInput getInput() {
		return _input;
	}

	@Override
	public String getInputFileNames() {
		return getInput().getInXsdWebaccessPath();
	}

	@Override
	public String getOutputFileNames() {
		return getInput().getOutXmlSpecPath() + ", " + getInput().getOutXmlDocPath();
	}

	@Override
	public Set<String> getSupportedFormats() {
		return new HashSet<String>(FILE_EXTENSIONS);
	}

	@Override
	public void write() {
		try {
			createDocuments();

			Map<Nature, Map<NamespaceInfo, Map<String, PackageDoc>>> nsPackageDocs = getInput()
					.getFixedFormDocumentation().getNsPackageDocs();

			addIec61850Namespaces(nsPackageDocs.get(Nature.IEC61850));
			addCimNamespaces(nsPackageDocs.get(Nature.CIM));
		} catch (Exception e) {
			_logger.error(
					"##### Prematurely closing documents due to exception: " + e.getMessage());
			e.printStackTrace(System.err);
		} finally {
			validateDocuments();
			closeDocuments();
		}
	}

	// ----------------- document lifecycle -----------------

	private void createDocuments() {
		String comment = createComment();
		XmlSchemaDOM schema = new XmlSchemaDOM(getInput().getOutXsdWebaccessPath());
		_spec = new WAXDocument(comment, getInput().getOutXmlSpecPath(), schema,
				WAX.E_IECDomainSpec);
		_doc = new WAXDocument(comment, getInput().getOutXmlDocPath(), schema, WAX.E_IECDomainDoc);
	}

	private String createComment() {
		StringBuilder sb = new StringBuilder();
		sb.append("Generated automatically from UML model file ")
				.append(getInput().getModelFileName());
		sb.append(" with jCleanCim-").append(getInput().getAppVersion());
		sb.append(" (").append(DateFormat.getDateTimeInstance().format(new Date())).append(")");
		return sb.toString();
	}

	private void validateDocuments() {
		if (_spec != null) {
			validateDocument(_spec);
		}
		if (_doc != null) {
			validateDocument(_doc);
		}
	}

	private void validateDocument(XmlInstanceDOM instance) {
		Util.ensureNotNull(instance, "instance");
		try {
			instance.validate();
		} catch (XmlParsingException e) {
			_logger.error("Caught XmlParsingException for " + instance.getInstanceFile().getName()
					+ ": " + e.getMessage());
		}
	}

	private void closeDocuments() {
		try {
			if (_spec != null) {
				_spec.save();
			}
			if (_doc != null) {
				_doc.save();
			}
		} catch (Exception e) {
			_logger.error(
					"##### Prematurely closing documents due to exception: " + e.getMessage());
			e.printStackTrace(System.err);
		}
	}

	// ===========================================================================
	// ======================= IEC 61850 name spaces =============================
	// ===========================================================================

	private void addIec61850Namespaces(Map<NamespaceInfo, Map<String, PackageDoc>> namespaces) {
		if (namespaces == null) {
			return;
		}

		String domainTag = WAX.E_IEC61850Domain;
		_logger.info("   writing " + namespaces.size() + " " + domainTag + " namespaces...");
		Element dmSpec = _spec.createSubElementUnderRoot(domainTag);
		Element dmDoc = _doc.createSubElementUnderRoot(domainTag);

		Element ps = addPrettyStrings(dmSpec, dmDoc, TableSpec.getTableSpecs(Nature.IEC61850));

		List<GroupsSpec> groups = GroupsSpec.getGroups();
		for (GroupsSpec gs : groups) {
			addCategories(ps, dmDoc, gs);
		}

		for (Entry<NamespaceInfo, Map<String, PackageDoc>> nsEntry : namespaces.entrySet()) {
			Element nsSpec = addNamespaceCommon(dmSpec, nsEntry.getKey());

			Map<String, PackageDoc> pDocs = nsEntry.getValue();
			for (Entry<String, PackageDoc> pEntry : pDocs.entrySet()) {
				PackageDoc pDoc = pEntry.getValue();

				String pName = pDoc.getPackageName();
				if (pDoc.getFcPackageDoc() != null) {
					add61850SpecialPackageAsTable(nsSpec, dmDoc, pName, pDoc.getFcPackageDoc());
				} else if (pDoc.getTrgOpPackageDoc() != null) {
					add61850SpecialPackageAsTable(nsSpec, dmDoc, pName, pDoc.getTrgOpPackageDoc());
				} else if (pDoc.getPresCondPackageDoc() != null) {
					add61850SpecialPackageAsTable(nsSpec, dmDoc, pName,
							pDoc.getPresCondPackageDoc());
				} else if (pDoc.getAbbrPackageDoc() != null) {
					add61850SpecialPackageAsTable(nsSpec, dmDoc, "Abbreviations",
							pDoc.getAbbrPackageDoc());
				} else if (pDoc.getLnMapPackageDoc() != null) {
					add61850SpecialPackageAsTable(nsSpec, dmDoc, pName, pDoc.getLnMapPackageDoc());
				} else {
					addPackage61850(nsSpec, dmDoc, pDoc);
				}
			}
		}
	}

	// --------------------- 61850 specials -------------------------

	private Element addCategories(Element sel, Element del, GroupsSpec groupsSpec) {
		Element groups = addElem(sel, groupsSpec.getName());

		for (RawData agSpec : groupsSpec.getAgSpecs()) {
			boolean isSpecial = Boolean.parseBoolean(agSpec.getCell(WAX.LOC_isSpecial));
			if (!isSpecial) {
				Element ag = addElem(groups, agSpec.getCell(WAX.LOC_tag));
				addAttr(ag, WAX.A_kind, agSpec.getCell(WAX.A_kind));
				addAttrWithElemInDoc(ag, WAX.A_textID, agSpec, del, WAX.A_text);
			}
		}
		return groups;
	}

	/**
	 * Adds package-as-properties table to <code>par</code> and returns the new element; we have 4
	 * of them with the same structure, only the tag names differ.
	 */
	private Element add61850SpecialPackageAsTable(Element par, Element del, String tagName,
			PropertiesDoc ppDoc) {
		Element result = addElem(par, tagName);
		addAttrWithElemInDoc(result, "introductionID", ppDoc, del, "introduction");
		addAttrWithElemInDoc(result, "captionID", ppDoc, del, "caption");
		addAttrWithElemInDocOpt(result, WAX.A_titleID, ppDoc, del, WAX.A_title);
		addAttrOpt(result, WAX.A_name, ppDoc);
		addAttrWithElemInDocOpt(result, WAX.A_aliasID, ppDoc, del, WAX.A_alias);
		addAttrWithElemInDocOpt(result, WAX.A_descID, ppDoc, del, WAX.A_desc);

		for (RawData eDoc : ppDoc.getDataEntryDocs()) {
			Element el = addElem(result, eDoc.getCell(WAX.LOC_tag));
			addAttr(el, WAX.A_name, eDoc);
			addAttrWithElemInDocOpt(el, WAX.A_aliasID, eDoc, del, WAX.A_alias);
			addAttrWithElemInDocOpt(el, WAX.A_descID, eDoc, del, WAX.A_desc);
			addAttrOpt(el, WAX.A_ieeeRef, eDoc);
			addAttrOpt(el, WAX.A_iecRef, eDoc);
			addAttrOpt(el, WAX.A_rsName, eDoc);
			addAttrOpt(el, WAX.A_lns, eDoc);
		}
		return result;
	}

	/** Adds 61850-specific package element to <code>sel</code> and returns the new element. */
	private Element addPackage61850(Element sel, Element del, PackageDoc pDoc) {
		Element p = addPackageCommon(sel, del, pDoc);

		String pckKind = pDoc.getCell(WAX.A_kind); // TODO: remove?

		for (ClassDoc cDoc : pDoc.getClassDocs()) {
			addClass61850(p, del, cDoc, pckKind);
		}

		for (PackageDoc childpDoc : pDoc.getChildPackageDocs()) {
			addPackage61850(p, del, childpDoc);
		}
		return p;
	}

	private Element addClass61850(Element p, Element del, ClassDoc cDoc, String pckageKind) {
		Element c = addClassCommon(p, del, cDoc);
		addXAttrIfTrue(c, "statistics", cDoc);
		addXAttrIfTrue(c, "admin", cDoc);

		PropertiesDoc aDocs = cDoc.getAttributesDoc();
		if (aDocs.notEmpty()) {
			String isEnum = cDoc.getCell("isEnum");
			if (isEnum != null && "true".equals(isEnum)) {
				addLiterals(c, del, cDoc.getAttributesDoc());
			} else {
				addGroupsOrFeatures61850(c, del, cDoc, pckageKind); // TODO: remove pckageKind?
			}
		}
		if (cDoc.getAssocEndsDoc().notEmpty()) {
			addAssociationEnds61850(c, del, cDoc.getAssocEndsDoc());
		}
		return c;
	}

	private Element addGroupsOrFeatures61850(Element sel, Element del, ClassDoc cDoc,
			String pckageKind) { // TODO: remove pckageKind?
		PropertiesDoc ppDoc = cDoc.getAttributesDoc();

		Element result = addTable(sel, del, ppDoc, "Features");

		Element parent = result;
		for (EntryDoc eDoc : ppDoc.getEntryDocs()) {
			if (eDoc.getKind() == EntryDoc.Kind.groupSubhead) {
				RawData agSpec = eDoc.getAttrGroupSpec();

				boolean isSpecial = Boolean.parseBoolean(agSpec.getCell(WAX.LOC_isSpecial));
				Element group = null;
				if (isSpecial) {
					// internally, we store
					group = addElem(result, agSpec.getCell(WAX.A_kind));
				} else {
					group = addElem(result, agSpec.getCell(WAX.LOC_instTag));
					addAttrOpt(group, WAX.A_kind, agSpec.getCell(WAX.A_kind));
				}
				parent = group;
			}
			if (eDoc.getKind() == EntryDoc.Kind.data) {
				boolean isServicePar = Boolean.parseBoolean(eDoc.getCell("isServicePar"));
				Element el = isServicePar ? addServiceParameter(parent, del, eDoc)
						: addAttribute(parent, del, eDoc);
				addAttrOpt(el, WAX.A_presCond, eDoc);
				addAttrOpt(el, WAX.A_presCondArgs, eDoc);
				addAttrWithElemInDocOpt(el, WAX.A_presCondArgsID, eDoc, del, WAX.A_cond);

				if ("LogicalNodes".equals(pckageKind)) {
					addAttrOpt(el, WAX.A_transient, eDoc);
					addAttrOpt(el, WAX.A_underlyingType, eDoc);
					addAttrOpt(el, WAX.A_underlyingControlType, eDoc);
					addAttrOpt(el, WAX.A_dsPresCond, eDoc);
					addAttrOpt(el, WAX.A_dsPresCondArgs, eDoc);
					addAttrWithElemInDocOpt(el, WAX.A_dsPresCondArgsID, eDoc, del, WAX.A_dsCond);
				} else if ("CommonDataClasses".equals(pckageKind)) {
					addAttrOpt(el, WAX.A_fc, eDoc);
					addAttrOpt(el, "dchg", eDoc);
					addAttrOpt(el, "qchg", eDoc);
					addAttrOpt(el, "dupd", eDoc);
					addAttrOpt(el, "minIndex", eDoc);
					addAttrOpt(el, "maxIndex", eDoc);
					addAttrOpt(el, "isArray", eDoc);
					addAttrOpt(el, WAX.A_typeKind, eDoc);
				} else {
					addAttrOpt(el, WAX.A_typeKind, eDoc);
					addAttrOpt(el, "minValue", eDoc);
					addAttrOpt(el, "maxValue", eDoc);
				}
			}
		}
		return result;
	}

	private Element addServiceParameter(Element sel, Element del, EntryDoc eDoc) {
		Element result = addUmlObject(sel, del, eDoc);
		addAttr(result, WAX.A_type, eDoc);
		addAttrOpt(result, WAX.A_typeKind, eDoc);
		return result;
	}

	private Element addAssociationEnds61850(Element sel, Element del, PropertiesDoc ppDoc) {
		Element result = addTable(sel, del, ppDoc, "AssociationEnds");

		for (RawData eDoc : ppDoc.getDataEntryDocs()) {
			Element entry = addAssociationEndCommon(result, del, eDoc);
			addAttrOpt(entry, WAX.A_presCond, eDoc);
			addAttrOpt(entry, "presCondArgs", eDoc);
		}
		return result;
	}

	// ===========================================================================
	// ============================= CIM name spaces =============================
	// ===========================================================================

	private void addCimNamespaces(Map<NamespaceInfo, Map<String, PackageDoc>> namespaces) {
		if (namespaces == null) {
			return;
		}

		String domainTag = "TC57CIM";
		_logger.info("   writing " + namespaces.size() + " " + domainTag + " namespaces...");
		Element dmSpec = _spec.createSubElementUnderRoot(domainTag);
		Element dmDoc = _doc.createSubElementUnderRoot(domainTag);

		addPrettyStrings(dmSpec, dmDoc, TableSpec.getTableSpecs(Nature.CIM));

		for (Entry<NamespaceInfo, Map<String, PackageDoc>> nsEntry : namespaces.entrySet()) {
			Element nsSpec = addNamespaceCommon(dmSpec, nsEntry.getKey());

			Map<String, PackageDoc> pDocs = nsEntry.getValue();
			for (Entry<String, PackageDoc> pEntry : pDocs.entrySet()) {
				PackageDoc pDoc = pEntry.getValue();
				addPackage(nsSpec, dmDoc, pDoc);
			}
		}
	}

	/**
	 * Adds package element with all its contents, recursively, to <code>sel</code> and returns the
	 * new element.
	 */
	Element addPackage(Element sel, Element del, PackageDoc pDoc) {
		Element p = addPackageCommon(sel, del, pDoc);

		for (ClassDoc cDoc : pDoc.getClassDocs()) {
			addClass(p, del, cDoc);
		}

		for (PackageDoc childpDoc : pDoc.getChildPackageDocs()) {
			addPackage(p, del, childpDoc);
		}
		return p;
	}

	private Element addClass(Element p, Element del, ClassDoc cDoc) {
		Element c = addClassCommon(p, del, cDoc);
		if (cDoc.getAttributesDoc().notEmpty()) {
			String isEnum = cDoc.getCell("isEnum");
			if (isEnum != null && "true".equals(isEnum)) {
				addLiterals(c, del, cDoc.getAttributesDoc());
			} else {
				addAttributes(c, del, cDoc.getAttributesDoc());
			}
		}
		if (cDoc.getAssocEndsDoc().notEmpty()) {
			addAssociationEnds(c, del, cDoc.getAssocEndsDoc());
		}
		return c;
	}

	private Element addAttributes(Element sel, Element del, PropertiesDoc ppDoc) {
		Element result = addTable(sel, del, ppDoc, "Attributes");

		for (RawData e : ppDoc.getDataEntryDocs()) {
			addAttribute(result, del, e);
		}
		return result;
	}

	private Element addAssociationEnds(Element sel, Element del, PropertiesDoc ppDoc) {
		Element result = addTable(sel, del, ppDoc, "AssociationEnds");
		for (RawData eDoc : ppDoc.getDataEntryDocs()) {
			Element el = addAssociationEndCommon(result, del, eDoc);
			addAttr(el, WAX.A_myMult, eDoc);
		}
		return result;
	}

	// --------- Regular/default pretty strings and UML elements ------------

	private Element addPrettyStrings(Element par, Element del, List<TableSpec> tableSpecs) {
		Element result = addElem(par, "PrettyStrings");

		for (TableSpec ts : tableSpecs) {
			if (ts == TableSpec.ATTR_INDEX) {
				// the only table spec we don't export to XML
				continue;
			}

			Element table = addElem(result, ts.getName());

			for (RawData colSpec : ts.getColSpecs()) {
				Element col = addElem(table, colSpec.getCell(WAX.LOC_tag));

				addAttr(col, "attributeName", colSpec);
				addAttrWithElemInDoc(col, WAX.A_textID, colSpec, del, WAX.A_text);
			}
		}
		return result;
	}

	/** Adds new name space common stuff to <code>domainSpec</code> and returns new name space. */
	Element addNamespaceCommon(Element domainSpec, NamespaceInfo ns) {
		Element result = addNamespaceElement(domainSpec, ns, "NameSpace");
		for (NamespaceInfo dep : ns.getDependencies()) {
			addNamespaceElement(result, dep, "Needs");
		}
		return result;
	}

	/** Adds new name space element to <code>parent</code> and returns new element. */
	Element addNamespaceElement(Element parent, NamespaceInfo ns, String tag) {
		Element result = addElem(parent, tag);

		addAttr(result, "id", ns.getId());
		addAttr(result, "version", ns.getVersion());
		addAttrOpt(result, "revision", ns.getRevision());
		addAttr(result, "umlVersion", ns.getUmlVersion());
		addAttrOpt(result, "tissues", ns.getTissuesApplied());
		return result;
	}

	/** Adds UML package commons element to <code>sel</code> and returns the package element. */
	private Element addPackageCommon(Element sel, Element del, PackageDoc pDoc) {
		Element p = addUmlObject(sel, del, pDoc);

		addAttrWithElemInDoc(p, WAX.A_titleID, pDoc, del, WAX.A_title);
		addAttrWithElemInDoc(p, WAX.A_subtitleID, pDoc, del, WAX.A_subtitle);

		for (RawData d : pDoc.getFigureDocs()) {
			addDiagram(p, del, d);
		}

		return p;
	}

	private Element addClassCommon(Element sel, Element del, ClassDoc cDoc) {
		Element c = addUmlObject(sel, del, cDoc);

		addAttrWithElemInDoc(c, WAX.A_titleID, cDoc, del, WAX.A_title);
		addXAttrIfTrue(c, WAX.A_abstract, cDoc);
		addAttrOpt(c, "superClass", cDoc);
		addAttrOpt(c, WAX.A_cdcId, cDoc);

		for (RawData d : cDoc.getDiagramDocs()) {
			addDiagram(c, del, d);
		}
		if (cDoc.getOperationsDoc().notEmpty()) {
			addOperations(c, del, cDoc.getOperationsDoc());
		}
		return c;
	}

	private Element addOperations(Element sel, Element del, PropertiesDoc ppDoc) {
		Element result = addTable(sel, del, ppDoc, "Operations");
		addAttrOpt(result, WAX.A_kind, ppDoc);

		for (RawData eDoc : ppDoc.getDataEntryDocs()) {
			Element entry = addUmlObject(result, del, eDoc);
			addAttr(entry, WAX.A_signature, eDoc);
			addAttrOpt(entry, "inheritedFrom", eDoc);
		}
		return result;
	}

	private Element addLiterals(Element sel, Element del, PropertiesDoc ppDoc) {
		Element result = addTable(sel, del, ppDoc, "Literals");

		for (RawData eDoc : ppDoc.getDataEntryDocs()) {
			Element entry = addUmlObject(result, del, eDoc);
			addAttrOpt(entry, WAX.A_literalVal, eDoc);
		}
		return result;
	}

	private Element addAttribute(Element sel, Element del, RawData eDoc) {
		Element result = addUmlObject(sel, del, eDoc);
		addAttr(result, WAX.A_type, eDoc);
		addAttrOpt(result, WAX.A_mult, eDoc);
		addAttrOpt(result, "inheritedFrom", eDoc);
		addAttrOpt(result, "defaultValue", eDoc);
		return result;
	}

	private Element addAssociationEndCommon(Element sel, Element del, RawData eDoc) {
		Element result = addUmlObject(sel, del, eDoc);
		addAttr(result, WAX.A_type, eDoc);
		addAttr(result, WAX.A_mult, eDoc);
		addAttrOpt(result, "inheritedFrom", eDoc);
		return result;
	}

	private Element addTable(Element sel, Element del, RawData ppDoc, String tagName) {
		Element result = addElem(sel, tagName);
		addAttrWithElemInDoc(result, "introductionID", ppDoc, del, "introduction");
		addAttrWithElemInDoc(result, "captionID", ppDoc, del, "caption");
		return result;
	}

	/** Returns new diagram element. */
	Element addDiagram(Element sel, Element del, RawData dDoc) {
		Element result = addUmlObject(sel, del, dDoc);
		addAttr(result, WAX.A_kind, dDoc);
		addAttr(result, "img", dDoc);
		addAttrWithElemInDoc(result, "captionID", dDoc, del, "caption");
		addAttrWithElemInDoc(result, "introductionID", dDoc, del, "introduction");
		return result;
	}

	Element addUmlObject(Element par, Element del, RawData oDoc) {
		Element result = addElem(par, oDoc.getCell(WAX.LOC_tag));
		addAttr(result, WAX.A_name, oDoc);
		addAttrWithElemInDocOpt(result, WAX.A_aliasID, oDoc, del, "alias");
		addAttrWithElemInDocOpt(result, WAX.A_descID, oDoc, del, "desc");
		addAttrOpt(result, "informative", oDoc);
		addAttrOpt(result, "deprecated", oDoc);
		return result;
	}

	// --------------

	/**
	 * If the value in <code>data</code> for key <code>docValueKey</code> has content (translatable
	 * documentation), calls
	 * {@link #addAttrWithElemInDoc(Element, String, RawData, Element, String)}, otherwise no-op.
	 */
	Element addAttrWithElemInDocOpt(Element par, String tag, RawData data, Element del,
			String docValueKey) {
		if (Util.hasContent(data.getCell(docValueKey))) {
			return addAttrWithElemInDoc(par, tag, data, del, docValueKey);
		}
		return par;
	}

	/**
	 * Adds to <code>par</code> a new attribute with tag and its value from <code>data</code>, and
	 * to <code>del</code> the corresponding doc element (value is the translatable doc, obtained
	 * from <code>data</code> with <code>docValueKey</code> as key). Returns the modified
	 * <code>par</code>.
	 */
	Element addAttrWithElemInDoc(Element par, String tag, RawData data, Element del,
			String docValueKey) {
		String id = data.getCell(tag);

		Element docEl = _doc.createSubElement(del, WAX.E_Doc);
		addAttr(docEl, "id", id);
		_doc.addCDATA(docEl, data.getCell(docValueKey));

		return addAttr(par, tag, id);
	}

	Element addElem(Element par, String tag) {
		return _spec.createSubElement(par, tag);
	}

	/**
	 * Calls {@link #addAttr(Element, String, RawData)} if value obtained from <code>data</code>
	 * with <code>tag</code> as key is <code>true</code>, otherwise no-op.
	 */
	static Element addXAttrIfTrue(Element par, String tag, RawData data) {
		if (Boolean.parseBoolean(data.getCell(tag))) {
			return addAttr(par, tag, "true");
		}
		return par;
	}

	/**
	 * Same as {@link #addAttrOpt(Element, String, String)} with value obtained from
	 * <code>data</code> for key <code>tag</code>.
	 */
	static Element addAttrOpt(Element par, String tag, RawData data) {
		return addAttrOpt(par, tag, data.getCell(tag));
	}

	/**
	 * If <code>value</code> not null and not empty, calls {@link #addAttr(Element, String, String)}
	 * , otherwise no-op.
	 */
	static Element addAttrOpt(Element par, String tag, String value) {
		if (Util.hasContent(value)) {
			return addAttr(par, tag, value);
		}
		return par;
	}

	/**
	 * Adds to <code>el</code> new attribute with tag used as key for value in <code>data</code>.
	 * Returs modified <code>el</code>.
	 */
	static Element addAttr(Element par, String tag, RawData data) {
		return addAttr(par, tag, data.getCell(tag));
	}

	/**
	 * Adds to <code>el</code> new attribute with tag and value. Returs modified <code>el</code>.
	 */
	static Element addAttr(Element par, String tag, String value) {
		par.setAttribute(tag, value);
		return par;
	}
}
