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

package org.tanjakostic.jcleancim.docgen.writer;

import java.util.AbstractList;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.tanjakostic.jcleancim.util.Util;

/**
 * List of cursors.
 *
 * @author tatjana.kostic@ieee.org
 * @version $Id: CursorList.java 31 2019-12-08 01:19:54Z dev978 $
 */
public class CursorList<O> extends AbstractList<Cursor<O>> {

	private final List<Cursor<O>> _cursors = new ArrayList<Cursor<O>>();
	private final Map<Placeholder, Integer> _indexedPlaceholders = new LinkedHashMap<Placeholder, Integer>();

	public CursorList() {
		// prevents default constructor
	}

	public CursorList<O> updateRanges(CursorList<O> cursors, Range<O> range) {
		for (int i = 0; i < cursors.size(); ++i) {
			range.getEnd();
		}
		return cursors;
	}

	final static class MyEntry<K, V> implements Map.Entry<K, V> {
		private final K key;
		private V value;

		public MyEntry(K key, V value) {
			this.key = key;
			this.value = value;
		}

		@Override
		public K getKey() {
			return key;
		}

		@Override
		public V getValue() {
			return value;
		}

		@Override
		public V setValue(V value) {
			V old = this.value;
			this.value = value;
			return old;
		}
	}

	public List<Map.Entry<Integer, Integer>> snapshotIndexes() {
		List<Map.Entry<Integer, Integer>> result = new ArrayList<Map.Entry<Integer, Integer>>();
		for (Cursor<O> cursor : _cursors) {
			Integer start = Integer.valueOf(cursor.getRange().getStart());
			Integer end = Integer.valueOf(cursor.getRange().getEnd());
			result.add(new MyEntry<Integer, Integer>(start, end));
		}
		return result;
	}

	public List<String> getReplacementFailures() {
		List<String> result = new ArrayList<String>();
		for (Cursor<O> phRange : _cursors) {
			Placeholder ph = phRange.getPlaceholder();
			String errorText = ph.getPlaceholderSpec().getErrorText();
			if (errorText != null) {
				result.add(errorText);
			}
		}
		return result;
	}

	/**
	 * Returns the number to be used in caption for the <code>cursor</code>; must be called.
	 */
	public int captionAdded(CaptionKind kind, Cursor<O> cursor) {
		Placeholder ph = cursor.getPlaceholder();
		int capNumber = (kind == CaptionKind.Figure) ? ph.addFigure() : ph.addTable();

		Integer index = _indexedPlaceholders.get(ph);
		for (int i = index.intValue() + 1; i < size(); ++i) {
			Placeholder subsequentPh = get(i).getPlaceholder();
			if (kind == CaptionKind.Figure) {
				subsequentPh.incrementFigureBefore();
			} else {
				subsequentPh.incrementTableBefore();
			}
		}
		return capNumber;
	}

	// ===== Impl. of java.util.AbstractCollection methods =====

	@Override
	public int size() {
		return _cursors.size();
	}

	@Override
	public Cursor<O> get(int index) {
		return _cursors.get(index);
	}

	@Override
	public boolean add(Cursor<O> cursor) {
		Util.ensureNotNull(cursor, "cursor");
		_indexedPlaceholders.put(cursor.getPlaceholder(), Integer.valueOf(size()));
		return _cursors.add(cursor);
	}

	@Override
	public Cursor<O> set(int index, Cursor<O> cursor) {
		_indexedPlaceholders.put(cursor.getPlaceholder(), Integer.valueOf(index));
		return _cursors.set(index, cursor);
	}

	@Override
	public void clear() {
		_indexedPlaceholders.clear();
	}

	@Override
	public String toString() {
		List<String> result = new ArrayList<String>();
		for (Cursor<O> phRange : _cursors) {
			result.add(phRange.toString());
		}
		return Util.NL + Util.concatCharSeparatedTokens(Util.NL, result);
	}
}
