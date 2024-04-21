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

import org.apache.log4j.Logger;
import org.tanjakostic.jcleancim.docgen.collector.BookmarkRegistry;
import org.tanjakostic.jcleancim.docgen.collector.DocgenConfig;
import org.tanjakostic.jcleancim.docgen.collector.PropertiesDoc;
import org.tanjakostic.jcleancim.docgen.collector.TableSpec;
import org.tanjakostic.jcleancim.docgen.collector.WAX;
import org.tanjakostic.jcleancim.model.UmlAssociationEnd;
import org.tanjakostic.jcleancim.model.UmlAssociationEndPair;
import org.tanjakostic.jcleancim.model.UmlClass;
import org.tanjakostic.jcleancim.model.UmlStereotype;

/**
 * Data required for documentation of association ends of the class with the other end class. For
 * the layout, see {@link PropertiesDoc}.
 *
 * @author tatjana.kostic@ieee.org
 * @version $Id: DefaultAssocEndsDoc.java 27 2019-11-23 16:29:38Z dev978 $
 */
class DefaultAssocEndsDoc extends AbstractPropertiesDoc {
	private static final Logger _logger = Logger.getLogger(DefaultAssocEndsDoc.class.getName());

	public static final String INTRO_FMT = " shows all association ends of %s with other classes.";
	public static final String CAPTION_FMT = "Association ends of %s with other classes";

	/**
	 * Constructor.
	 */
	public DefaultAssocEndsDoc(DocgenConfig docgenCfg, UmlClass c, BookmarkRegistry bmRegistry) {
		super(docgenCfg, c, "ae", String.format(INTRO_FMT, c.getName()),
				String.format(CAPTION_FMT, c.getQualifiedName()), TableSpec.ASSOC_ENDS, null,
				bmRegistry);

		log(_logger, "---- collecting doc for assocEnds of " + c.getQualifiedName() + " ...");

		Collection<UmlAssociationEndPair> retainedNatives = new ArrayList<UmlAssociationEndPair>();
		Collection<UmlAssociationEndPair> retainedInheriteds = new ArrayList<UmlAssociationEndPair>();
		super.filterAssociationEnds(c, retainedNatives, retainedInheriteds);

		String bookmarkID = null; // we don't point from hyperlinks to any association end here

		for (UmlAssociationEndPair aePair : retainedNatives) {
			UmlAssociationEnd otherEnd = aePair.getOtherEnd();
			UmlAssociationEnd myEnd = aePair.getMyEnd();
			String myMult = myEnd.getMultiplicity().getBounds();
			String name = otherEnd.getName();
			String mult = otherEnd.getMultiplicity().getBounds();
			String type = prepareForHyperlink(otherEnd.getType());

			String qualifiersText = deduceQualifiersPrefix(otherEnd,
					UmlStereotype.getAssociationBuiltIns(), docgenCfg.showCustomStereotypes,
					otherEnd.getContainingAssociation(), DEFAULT_PREFIX_FMT);

			/*
			 * Currently, EA actually does not allow markup in the notes for association ends; if we
			 * use html returned by EA, the format is screwed (i.e., we don't get what we see).
			 * Therefore, we use raw text always. Commented line is for when EA starts supporting
			 * html format in association end notes.
			 */
			// CellText desc = deduceCellText(qualifiersText);
			CellText desc = deduceCellText(qualifiersText, otherEnd.getDescription(), null,
					otherEnd);

			EntryDocImpl entry = EntryDocImpl.createData(bookmarkID, desc.formatInfo, myMult, name,
					mult, type, desc.text);
			addEntry(entry);

			// for xml:
			initRawData(entry, otherEnd);
			initAssocEndRawData(entry, otherEnd, myEnd);
		}
		for (UmlAssociationEndPair aePair : retainedInheriteds) {
			UmlAssociationEnd otherEnd = aePair.getOtherEnd();
			UmlAssociationEnd myEnd = aePair.getMyEnd();
			String myMult = myEnd.getMultiplicity().getBounds();
			String name = otherEnd.getName();
			String mult = otherEnd.getMultiplicity().getBounds();
			String type = prepareForHyperlink(otherEnd.getType());
			String qualifiersText = deduceQualifiersPrefix(otherEnd,
					UmlStereotype.getAssociationBuiltIns(), docgenCfg.showCustomStereotypes,
					otherEnd.getContainingAssociation(), DEFAULT_PREFIX_FMT);
			String description = qualifiersText + INHERITED_FROM
					+ prepareForHyperlink(myEnd.getType());

			EntryDocImpl entry = EntryDocImpl.createData(bookmarkID, null, myMult, name, mult, type,
					description);
			addEntry(entry);

			// for xml:
			initRawData(entry, otherEnd, myEnd.getType().getName());
			initAssocEndRawData(entry, otherEnd, myEnd);
		}

		log(_logger, ">>>>>> " + toString());
	}

	private static void initAssocEndRawData(EntryDocImpl entry, UmlAssociationEnd otherEnd,
			UmlAssociationEnd myEnd) {
		entry.putCell(WAX.A_myMult, myEnd.getMultiplicity().getBounds());
		entry.putCell(WAX.A_mult, otherEnd.getMultiplicity().getBounds());
		entry.putCell(WAX.A_name, otherEnd.getName());
		entry.putCell(WAX.A_type, otherEnd.getType().getName());
	}
}
