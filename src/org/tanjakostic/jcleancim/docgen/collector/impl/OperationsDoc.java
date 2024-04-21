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
import org.tanjakostic.jcleancim.model.UmlClass;
import org.tanjakostic.jcleancim.model.UmlOperation;

/**
 * Data required for documentation of operations. For the format, see {@link PropertiesDoc}.
 * <p>
 * FIXME: handle deprecated for parameters
 * <p>
 * FIXME: 1 operation has its own table
 *
 * @author tatjana.kostic@ieee.org
 * @version $Id: OperationsDoc.java 21 2019-08-12 15:44:50Z dev978 $
 */
class OperationsDoc extends AbstractPropertiesDoc {
	private static final Logger _logger = Logger.getLogger(OperationsDoc.class.getName());

	public static final String INTRO_FMT = " shows all operations of %s.";
	public static final String CAPTION_FMT = "Operations of %s";

	/**
	 * Constructor.
	 *
	 * @param docgenCfg
	 * @param c
	 * @param bmRegistry
	 *            (note: at present not used, will be when refactoring operations)
	 */
	public OperationsDoc(DocgenConfig docgenCfg, UmlClass c, BookmarkRegistry bmRegistry) {
		super(docgenCfg, c, "op", String.format(INTRO_FMT, c.getName()), String.format(CAPTION_FMT,
				c.getName()), TableSpec.OPERATIONS, null, bmRegistry);

		log(_logger, "---- collecting doc for operations of " + c.getQualifiedName() + " ...");

		Collection<UmlOperation> retainedNatives = new ArrayList<UmlOperation>();
		Collection<UmlOperation> retainedInheriteds = new ArrayList<UmlOperation>();
		super.filterOperations(c, retainedNatives, retainedInheriteds);

		String bookmarkID = null; // we don't point from hyperlinks to any attribute here

		for (UmlOperation op : retainedNatives) {
			String signature = op.getSignature();
			CellText desc = deduceCellText(getDeprecatedTextAsPrefix(op), op);

			EntryDocImpl entry = EntryDocImpl.createData(bookmarkID, desc.formatInfo, signature,
					desc.text);
			addEntry(entry);

			// for xml:
			initRawData(entry, op);
			entry.putCell(WAX.A_signature, op.getSignature());
		}
		for (UmlOperation op : retainedInheriteds) {
			String signature = op.getSignature();
			String inhDoc = getDeprecatedTextAsPrefix(op) + INHERITED_FROM
					+ prepareForHyperlink(op.getContainingClass());

			EntryDocImpl entry = EntryDocImpl.createData(bookmarkID, null, signature, inhDoc);
			addEntry(entry);

			// for xml:
			initRawData(entry, op, op.getContainingClass().getName());
			entry.putCell(WAX.A_signature, op.getSignature());
		}

		log(_logger, ">>>>>> " + toString());
	}
}
