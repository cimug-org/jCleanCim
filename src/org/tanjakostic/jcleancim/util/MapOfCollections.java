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

package org.tanjakostic.jcleancim.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;

/**
 * @param <K>
 *            type for key
 * @param <V>
 *            type for collection elements
 * @author tatjana.kostic@ieee.org
 * @version $Id: MapOfCollections.java 21 2019-08-12 15:44:50Z dev978 $
 */
public abstract class MapOfCollections<K, V> {

	private final LinkedHashMap<K, Collection<V>> _map;

	public MapOfCollections() {
		_map = new LinkedHashMap<K, Collection<V>>();
	}

	@SafeVarargs
	public final void addValue(K key, V value, V... furtherValues) {
		if (!_map.containsKey(key)) {
			_map.put(key, createCollection());
		}
		Collection<V> collection = _map.get(key);
		collection.add(value);
		if (furtherValues != null) {
			for (V v : furtherValues) {
				collection.add(v);
			}
		}
	}

	/** Returns sub-value for <code>key</code> if found, empty collection otherwise. */
	public Collection<V> subCollection(K key) {
		Collection<V> collection = _map.get(key);
		if (collection == null) {
			return Collections.emptySet();
		}
		return collection;
	}

	/** "Descends" <code>key</code> - <code>idx</code>; returns null if no such value. */
	public abstract V value(K key, int idx) throws UnsupportedOperationException;

	protected abstract Collection<V> createCollection();

	// --------------

	/** Returns size of this map (i.e., number of keys). */
	public int size() {
		return _map.size();
	}

	/**
	 * Returns cumulative size of all the values for all the keys in this map; note that this is
	 * caclulated at every invocation.
	 */
	public int calcValueSize() {
		int result = 0;
		for (Entry<K, Collection<V>> entry : _map.entrySet()) {
			result += entry.getValue().size();
		}
		return result;
	}

	public Set<K> keys() {
		return _map.keySet();
	}

	public boolean isEmpty() {
		return _map.isEmpty();
	}

	public boolean containsKey(K key) {
		return _map.containsKey(key);
	}

	public List<String> toStringLines() {
		List<String> result = new ArrayList<>();
		for (Entry<K, Collection<V>> entry : _map.entrySet()) {
			result.add(entry.toString());
		}
		return result;
	}

	@Override
	public String toString() {
		return _map.toString();
	}
}
