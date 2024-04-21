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
import java.util.Collections;
import java.util.List;

import org.apache.log4j.Logger;
import org.tanjakostic.jcleancim.docgen.collector.EntryDoc;
import org.tanjakostic.jcleancim.util.Util;

/**
 * Collects the description of items to reorder, and performs reordering of a list with
 * {@link #reorder(List)}.
 *
 * @author tatjana.kostic@ieee.org
 * @version $Id: Reordering.java 21 2019-08-12 15:44:50Z dev978 $
 */
class Reordering {
	private static final Logger _logger = Logger.getLogger(Reordering.class.getName());

	private final List<ReorderingItem> _reorderingItems = new ArrayList<ReorderingItem>();

	public boolean addReorderingItem(EntryDoc toMoveDoc, EntryDoc afterWhichDoc) {
		ReorderingItem spec = new ReorderingItem(toMoveDoc, afterWhichDoc);
		_logger.info("        adding reordering spec: " + spec.toString());
		return _reorderingItems.add(spec);
	}

	/**
	 * Returns reordering specification.
	 */
	public List<ReorderingItem> getReorderingItems() {
		return Collections.unmodifiableList(_reorderingItems);
	}

	/**
	 * Performs reordering of <code>entryDocs</code> and returns reordered entry docs, or empty list
	 * if there are no reordering items ({@link #getReorderingItems() returns empty list}).
	 */
	public List<EntryDoc> reorder(List<EntryDoc> entryDocs) {
		List<EntryDoc> reorderedEntryDocs = new ArrayList<EntryDoc>(entryDocs);
		for (ReorderingItem spec : getReorderingItems()) {
			int toMoveIdx = reorderedEntryDocs.indexOf(spec.getToMoveDoc());
			int afterWhichIdx = reorderedEntryDocs.indexOf(spec.getAfterWhichDoc());

			for (int k = toMoveIdx; k < afterWhichIdx; ++k) {
				Collections.swap(reorderedEntryDocs, k, k + 1);
			}
		}
		return reorderedEntryDocs;
	}

	/**
	 * @author tatjana.kostic@ieee.org
	 * @version $Id: Reordering.java 21 2019-08-12 15:44:50Z dev978 $
	 */
	static class ReorderingItem {
		private final EntryDoc _toMoveDoc;
		private final EntryDoc _afterWhichDoc;

		ReorderingItem(EntryDoc toMoveDoc, EntryDoc afterWhichDoc) {
			Util.ensureNotNull(toMoveDoc, "toMoveDoc");
			Util.ensureNotNull(afterWhichDoc, "afterWhichDoc");
			_toMoveDoc = toMoveDoc;
			_afterWhichDoc = afterWhichDoc;
		}

		public EntryDoc getToMoveDoc() {
			return _toMoveDoc;
		}

		public EntryDoc getAfterWhichDoc() {
			return _afterWhichDoc;
		}

		/**
		 * {@inheritDoc}
		 * <p>
		 * This implementation displays for both to-move and after-which entries the first value
		 * from their respective {@link EntryDoc#getValues()}.
		 */
		@Override
		public String toString() {
			return String.format("MOVE \"%s\" AFTER \"%s\"", getToMoveDoc().getValues()[0],
					getAfterWhichDoc().getValues()[0]);
		}
	}
}
