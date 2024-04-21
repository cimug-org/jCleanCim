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
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

/**
 * Data structure to hold two levels of keys.
 *
 * @param <K>
 *            type for key
 * @param <SK>
 *            type for sub-key
 * @param <SV>
 *            type for value elements
 * @author tatjana.kostic@ieee.org
 * @version $Id: MapOfMaps.java 21 2019-08-12 15:44:50Z dev978 $
 */
public class MapOfMaps<K, SK, SV> {
	private final Map<K, Map<SK, SV>> _map;

	public MapOfMaps() {
		_map = new LinkedHashMap<K, Map<SK, SV>>();
	}

	public void putValue(K key, SK subkey, SV value) {
		if (!_map.containsKey(key)) {
			_map.put(key, createSubMap());
		}
		_map.get(key).put(subkey, value);
	}

	/** Returns sub-values for <code>key</code> if found, empty map otherwise. */
	public Map<SK, SV> subMap(K key) {
		Map<SK, SV> subMap = _map.get(key);
		if (subMap == null) {
			return Collections.emptyMap();
		}
		return subMap;
	}

	/** "Descends" <code>key</code> - <code>subkey</code>; returns null if no such value. */
	public SV value(K key, SK subkey) {
		Map<SK, SV> subMap = subMap(key);
		return subMap.get(subkey);
	}

	protected Map<SK, SV> createSubMap() {
		return new LinkedHashMap<>();
	}

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
		for (Entry<K, Map<SK, SV>> entry : _map.entrySet()) {
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
		for (Entry<K, Map<SK, SV>> entry : _map.entrySet()) {
			result.add(entry.toString());
		}
		return result;
	}

	@Override
	public String toString() {
		return _map.toString();
	}
}
