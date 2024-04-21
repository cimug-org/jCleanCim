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
import java.util.EnumSet;
import java.util.List;

import org.tanjakostic.jcleancim.common.OwningWg;
import org.tanjakostic.jcleancim.docgen.collector.BookmarkRegistry;
import org.tanjakostic.jcleancim.docgen.collector.DocgenConfig;
import org.tanjakostic.jcleancim.docgen.collector.EntryDoc;
import org.tanjakostic.jcleancim.docgen.collector.EntryDoc.Kind;
import org.tanjakostic.jcleancim.docgen.collector.FormatInfo;
import org.tanjakostic.jcleancim.docgen.collector.PropertiesDoc;
import org.tanjakostic.jcleancim.docgen.collector.TableSpec;
import org.tanjakostic.jcleancim.docgen.collector.WAX;
import org.tanjakostic.jcleancim.model.TextDescription;
import org.tanjakostic.jcleancim.model.TextDescription.TextKind;
import org.tanjakostic.jcleancim.model.UmlAssociation;
import org.tanjakostic.jcleancim.model.UmlAssociationEnd;
import org.tanjakostic.jcleancim.model.UmlAssociationEnd.Navigable;
import org.tanjakostic.jcleancim.model.UmlAssociationEndPair;
import org.tanjakostic.jcleancim.model.UmlAttribute;
import org.tanjakostic.jcleancim.model.UmlClass;
import org.tanjakostic.jcleancim.model.UmlObject;
import org.tanjakostic.jcleancim.model.UmlOperation;
import org.tanjakostic.jcleancim.model.UmlStereotype;
import org.tanjakostic.jcleancim.util.ProgrammerErrorException;
import org.tanjakostic.jcleancim.util.Util;

/**
 * Common implementation for properties documentation (table).
 *
 * @author tatjana.kostic@ieee.org
 * @version $Id: AbstractPropertiesDoc.java 31 2019-12-08 01:19:54Z dev978 $
 */
public abstract class AbstractPropertiesDoc extends AbstractObjectDoc implements PropertiesDoc {
	private final String _introText;
	private final String _captionText;
	private final TableSpec _colSpec;
	private final String _tableName;
	private final List<EntryDoc> _entryDocs = new ArrayList<EntryDoc>();

	private int _headingEntriesCount = 0;

	/**
	 * Creates an instance with a
	 * {@link org.tanjakostic.jcleancim.docgen.collector.EntryDoc.Kind#tableName} entry (if
	 * <code>tableName</code> is not null) and
	 * {@link org.tanjakostic.jcleancim.docgen.collector.EntryDoc.Kind#columnLabels} entry (from
	 * <code>colSpec</code>) in the list of entries; use when you want to add
	 * {@link org.tanjakostic.jcleancim.docgen.collector.EntryDoc.Kind#data} (and optionally,
	 * {@link org.tanjakostic.jcleancim.docgen.collector.EntryDoc.Kind#groupSubhead}) entries one by
	 * one, after some complex processing.
	 *
	 * @param docgenCfg
	 * @param object
	 * @param what
	 * @param introText
	 * @param captionText
	 * @param colSpec
	 * @param tableName
	 * @param bmRegistry
	 */
	protected AbstractPropertiesDoc(DocgenConfig docgenCfg, UmlObject object, String what,
			String introText, String captionText, TableSpec colSpec, String tableName,
			BookmarkRegistry bmRegistry) {
		this(docgenCfg, object, what, null, null, true, null, introText, captionText, colSpec,
				tableName, bmRegistry);
	}

	/**
	 * Same as
	 * {@link #AbstractPropertiesDoc(DocgenConfig, UmlObject, String, String, String, TableSpec, String, BookmarkRegistry)}
	 * , but with explicit text and html documentation parameters; this is to support chained
	 * construction for the needs of testing where we don't have UML objects but want to print
	 * descriptions.
	 *
	 * @param docgenCfg
	 * @param object
	 * @param what
	 * @param description
	 * @param htmlDescription
	 * @param ignoreDesc
	 * @param headingText
	 * @param introText
	 * @param captionText
	 * @param colSpec
	 * @param tableName
	 * @param bmRegistry
	 */
	protected AbstractPropertiesDoc(DocgenConfig docgenCfg, UmlObject object, String what,
			TextDescription description, TextDescription htmlDescription, boolean ignoreDesc,
			String headingText, String introText, String captionText, TableSpec colSpec,
			String tableName, BookmarkRegistry bmRegistry) {
		super(docgenCfg, object, what, description, htmlDescription, ignoreDesc, headingText, null,
				bmRegistry);

		Util.ensureNotNull(introText, "introText");
		Util.ensureNotNull(captionText, "captionText");
		if (tableName != null) {
			Util.ensureNotEmpty(tableName.trim(), "tableName");
		}
		Util.ensureNotNull(colSpec, "colSpec");

		_introText = introText;
		_captionText = captionText;
		_tableName = tableName;
		_colSpec = colSpec;

		String whatPref = (what != null) ? (what.trim() + ".") : "";
		if (object != null) {
			putCell(WAX.A_introductionID, createDocId(object, whatPref + "introduction"));
			putCell(WAX.A_introduction, introText);
			putCell(WAX.A_captionID, createDocId(object, whatPref + "caption"));
			putCell(WAX.A_caption, captionText);
		}

		if (tableName != null) {
			addEntry(EntryDocImpl.createTableName(tableName, colSpec.colCount()));
		}
		addEntry(EntryDocImpl.createColumnLabels(colSpec.getLabels()));
	}

	/**
	 * Initialises raw data for a native property (tag, name, alias+ID, desc+ID, informative,
	 * deprecated, inheritedFrom).
	 *
	 * @param entry
	 * @param property
	 */
	protected void initRawData(EntryDoc entry, UmlObject property) {
		initRawData(entry, property, false, null);
	}

	/**
	 * Initialises raw data for an inherited property (tag, name, alias+ID, desc+ID, informative,
	 * deprecated, inheritedFrom).
	 *
	 * @param entry
	 * @param property
	 * @param baseTypeName
	 *            name of base type from which this property gets inherited.
	 */
	protected void initRawData(EntryDoc entry, UmlObject property, String baseTypeName) {
		initRawData(entry, property, true, baseTypeName);
	}

	/**
	 * Initialises raw data (tag, {@value org.tanjakostic.jcleancim.docgen.collector.WAX#A_name},
	 * alias+ID, desc+ID, informative, deprecated, inheritedFrom).
	 *
	 * @param entry
	 * @param prop
	 * @param isInherited
	 *            if true, desc+ID are not set, because this would violate identity constraint in
	 *            the doc schema for an existing property description; the stylesheet simply must
	 *            deduce it.
	 * @param baseTypeName
	 *            name of base type (from which this property gets inherited); meaningful if
	 *            <code>isInherited = true</code>.
	 */
	private void initRawData(EntryDoc entry, UmlObject prop, boolean isInherited,
			String baseTypeName) {
		entry.putCell(WAX.LOC_tag, prop.getKind().getTag());
		entry.putCell(WAX.A_name, prop.getName());
		if (prop.isInformative()) {
			entry.putCell(WAX.A_informative, "true");
		}
		if (prop.isDeprecated()) {
			entry.putCell(WAX.A_deprecated, "true");
		}
		if (!isInherited) {
			entry.putCell(WAX.A_aliasID, createDocId(prop, "alias"));
			entry.putCell(WAX.A_alias, prop.getAlias());
			entry.putCell(WAX.A_descID, createDocId(prop, "desc"));
			entry.putCell(WAX.A_desc, deduceCellText("", prop).text);
		} else {
			entry.putCell(WAX.A_alias, "");
			entry.putCell(WAX.A_desc, "");
			entry.putCell(WAX.A_inheritedFrom, baseTypeName);
		}
		if (entry.getBookmarkID() != null) {
			entry.putCell(WAX.A_bookmarkID, entry.getBookmarkID());
		}
	}

	/**
	 * @author tatjana.kostic@ieee.org
	 * @version $Id: AbstractPropertiesDoc.java 31 2019-12-08 01:19:54Z dev978 $
	 */
	protected static class CellText extends TextDescription {
		public final FormatInfo formatInfo;

		/**
		 * Creates an instance with null formatting info (all formatting will be ignored).
		 */
		protected CellText(TextDescription text) {
			this(null, text);
		}

		/**
		 * Constructor.
		 */
		protected CellText(Integer fmtIdx, TextDescription textDesc) {
			super(deduceText(textDesc), deduceKind(fmtIdx, textDesc));

			this.formatInfo = (fmtIdx == null || textDesc.kind == TextDescription.DEFAULT_KIND)
					? null
					: new FormatInfo(textDesc.kind, fmtIdx);
		}

		private static TextKind deduceKind(Integer fmtIdx, TextDescription textDesc) {
			if (fmtIdx == null || textDesc == null) {
				return TextDescription.DEFAULT_KIND;
			}
			return textDesc.kind;
		}

		private static String deduceText(TextDescription text) {
			return (text == null) ? null : text.text;
		}
	}

	/** Selects the description to retain, according to configuration. */
	protected final CellText deduceCellText(String prefix, UmlObject o) {
		return deduceCellText(prefix, o.getDescription(), o.getHtmlDescription(), o);
	}

	protected final CellText deduceCellText(String prefix, TextDescription raw,
			TextDescription html, UmlObject o) {
		if (useHtml(html)) {
			return new CellText(_colSpec.getFmtIdx(), html.prepend(prefix, o));
		}
		TextDescription rawWithPrefix = raw.prepend(prefix, o);
		return (raw.kind == TextKind.textWithNL) ? new CellText(_colSpec.getFmtIdx(), rawWithPrefix)
				: new CellText(rawWithPrefix);
	}

	/**
	 * Returns whether <code>entryDoc</code> has been successfully added.
	 */
	protected final boolean addEntry(EntryDoc entryDoc) {
		EntryDoc.Kind kind = entryDoc.getKind();

		ensureValid(kind);

		if (EnumSet.of(EntryDoc.Kind.tableName, EntryDoc.Kind.columnLabels).contains(kind)) {
			++_headingEntriesCount;
		}

		return _entryDocs.add(entryDoc);
	}

	private void ensureValid(EntryDoc.Kind kind) {
		boolean isFirst = _entryDocs.isEmpty();
		switch (kind) {
			case tableName: {
				if (!isFirst) {
					throw new ProgrammerErrorException(
							"First entry must be table name or column labels.");
				}
				break;
			}
			case columnLabels: {
				boolean isSecondAfterTableName = (_entryDocs.size() == 1)
						&& (_entryDocs.get(0).getKind() == EntryDoc.Kind.tableName);
				if (!(isFirst || isSecondAfterTableName)) {
					throw new ProgrammerErrorException(
							"Column labels can be only first, or second after table name.");
				}
				break;
			}
			default:
				break;
		}
	}

	// ------------ criteria to exclude items from document generation -------------

	protected final void filterAttributes(UmlClass c, Collection<UmlAttribute> retainedNatives,
			Collection<UmlAttribute> retainedInheriteds) {
		Util.ensureNotNull(retainedNatives, "retainedNatives");
		Util.ensureNotNull(retainedInheriteds, "retainedInheriteds");
		for (UmlAttribute a : c.getAttributes()) {
			if (!toSkip(a)) {
				retainedNatives.add(a);
			}
		}
		for (UmlAttribute a : c.getInheritedAttributes()) {
			if (!toSkipInherited(a)) {
				retainedInheriteds.add(a);
			}
		}
	}

	protected final void filterAssociationEnds(UmlClass c,
			Collection<UmlAssociationEndPair> retainedNatives,
			Collection<UmlAssociationEndPair> retainedInheriteds) {
		for (UmlAssociationEndPair pair : c.getAssociationEndPairs()) {
			UmlAssociationEnd otherEnd = pair.getOtherEnd();
			UmlAssociationEnd myEnd = pair.getMyEnd();
			if (!toSkip(otherEnd) && !toSkip(otherEnd, myEnd.getType().getOwner())) {
				retainedNatives.add(pair);
			}
		}
		for (UmlAssociationEndPair pair : c.getInheritedAssociationEndPairs()) {
			UmlAssociationEnd otherEnd = pair.getOtherEnd();
			UmlAssociationEnd myEnd = pair.getMyEnd();
			if (!toSkipInherited(otherEnd) && !toSkip(otherEnd, myEnd.getType().getOwner())) {
				retainedInheriteds.add(pair);
			}
		}
	}

	private final boolean toSkip(UmlAssociationEnd otherEnd, OwningWg classOwner) {
		UmlAssociation containingAssociation = otherEnd.getContainingAssociation();

		// in case association has a custom stereotype that is to be skipped:
		if (toSkip(containingAssociation)) {
			return true;
		}
		// this one prevents printing content of e.g. WG14 model in WG13 documents
		if (!classOwner.getAllowedOtherEndOwners().contains(otherEnd.getOwner())) {
			return true;
		}
		// this is for profile documentation, to print only navigable direction
		if (containingAssociation.hasANavigableEnd() && otherEnd.getNavigable() != Navigable.yes) {
			return true;
		}
		return false;
	}

	protected final void filterOperations(UmlClass c, Collection<UmlOperation> retainedNatives,
			Collection<UmlOperation> retainedInheriteds) {
		for (UmlOperation op : c.getOperations()) {
			if (!toSkip(op)) {
				retainedNatives.add(op);
			}
		}
		for (UmlOperation op : c.getInheritedOperations()) {
			if (!toSkipInherited(op)) {
				retainedInheriteds.add(op);
			}
		}
	}

	/**
	 * Returns whether inherited object <code>o</code> needs to be skipped, according to
	 * configuration.
	 */
	protected final boolean toSkipInherited(UmlObject o) {
		if (super.toSkip(o)) {
			return true;
		}

		if (!getDocgenCfg().includeInhFromMetamodel) {
			if (o instanceof UmlClass && ((UmlClass) o).isFrom72()) {
				return true;
			}
			if (o instanceof UmlAttribute && ((UmlAttribute) o).getContainingClass().isFrom72()) {
				return true;
			}
			if (o instanceof UmlOperation && ((UmlOperation) o).getContainingClass().isFrom72()) {
				return true;
			}
			if (o instanceof UmlAssociationEnd && ((UmlAssociationEnd) o).getType().isFrom72()) {
				return true;
			}
		}
		return false;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder(super.toString());
		sb.append(_introText).append(Util.NL);
		sb.append(_captionText).append(Util.NL);

		List<String> rows = new ArrayList<String>();
		for (EntryDoc entryDoc : _entryDocs) {
			rows.add(entryDoc.toString());
		}
		String concatEntries = Util.concatCharSeparatedTokens(Util.NL, rows);
		sb.append(concatEntries);

		return sb.toString();
	}

	// ------------- common strings to include in most of properties' documentation ---------

	/**
	 * Returns empty string if property is not deprecated, otherwise formatted text ending with
	 * white space.
	 */
	protected static final String getDeprecatedTextAsPrefix(UmlObject o) {
		return (!o.isDeprecated()) ? "" : "(" + UmlStereotype.DEPRECATED + ") ";
	}

	/** Returns the formatted initial value detail (const/initial/range) to be used as suffix. */
	protected static String getInitValAsSuffix(UmlAttribute attr) {
		if (attr.hasConstValue()) {
			return "=" + attr.getInitValue() + " (const)";
		} else if (attr.hasDefaultValue()) {
			return " (initial: " + attr.getInitValue() + ")";
		} else if (attr.hasValueRange()) {
			return "= [" + attr.getInitValue() + "]";
		} else {
			return "";
		}
	}

	/** Returns the formatted initial value detail (const/default/range) to be used as prefix. */
	protected static final String getInitValAsPrefix(UmlAttribute attr) {
		if (attr.hasConstValue()) {
			return "(const=" + attr.getInitValue() + ") ";
		} else if (attr.hasDefaultValue()) {
			return "(default=" + attr.getInitValueWithPotentialOverrideForSCL() + ") ";
		} else if (attr.hasValueRange()) {
			return "(range=[" + attr.getInitValue() + "]) ";
		} else {
			return "";
		}
	}

	// ===== org.tanjakostic.jcleancim.docgen.collector.PropertyDoc =====

	@Override
	public final boolean notEmpty() {
		return getRowCount() > getHeadingEntriesCount();
	}

	@Override
	public final String getIntroText() {
		return _introText;
	}

	@Override
	public final String getCaptionText() {
		return _captionText;
	}

	@Override
	public final int getHeadingEntriesCount() {
		return _headingEntriesCount;
	}

	@Override
	public final List<? extends EntryDoc> getEntryDocs() {
		return Collections.unmodifiableList(_entryDocs);
	}

	@Override
	public List<? extends EntryDoc> getDataEntryDocs() {
		List<EntryDoc> result = new ArrayList<EntryDoc>();
		for (EntryDoc eDoc : _entryDocs) {
			if (eDoc.getKind() == Kind.data) {
				result.add(eDoc);
			}
		}
		return result;
	}

	@Override
	public final String getTableName() {
		return _tableName;
	}

	@Override
	public final TableSpec getTableSpec() {
		return _colSpec;
	}

	@Override
	public final int getRowCount() {
		return _entryDocs.size();
	}

	@Override
	public final int getColumnCount() {
		return _colSpec.colCount();
	}

	@Override
	public final String[][] getCellValues() {
		int iCount = getRowCount();
		String result[][] = new String[iCount][];
		for (int i = 0; i < iCount; ++i) {
			result[i] = _entryDocs.get(i).getValues();
		}
		return result;
	}

	@Override
	public final EntryDoc.Kind[] getRowKinds() {
		EntryDoc.Kind[] result = new EntryDoc.Kind[_entryDocs.size()];
		for (int i = 0; i < result.length; ++i) {
			result[i] = _entryDocs.get(i).getKind();
		}
		return result;
	}

	@Override
	public TextKind[] getFormats() {
		int iCount = getRowCount();
		TextKind result[] = new TextKind[iCount];
		boolean hasFormatting = false;
		for (int i = 0; i < iCount; ++i) {
			FormatInfo fmt = _entryDocs.get(i).getFormatInfo();
			if (fmt != null) {
				result[i] = fmt.getKind();
				hasFormatting = true;
			}
		}
		return (hasFormatting) ? result : null;
	}

	@Override
	public String[] getBookmarkIDs() {
		String[] result = new String[_entryDocs.size()];
		for (int i = 0; i < result.length; ++i) {
			result[i] = _entryDocs.get(i).getBookmarkID();
		}
		return result;
	}
}
