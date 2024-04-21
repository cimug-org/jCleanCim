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

import java.util.LinkedHashSet;
import java.util.Set;

/**
 * @param <K>
 *            type for key
 * @param <V>
 *            type for set elements
 * @author tatjana.kostic@ieee.org
 * @version $Id: MapOfSets.java 21 2019-08-12 15:44:50Z dev978 $
 */
public class MapOfSets<K, V> extends MapOfCollections<K, V> {
	@Override
	protected Set<V> createCollection() {
		return new LinkedHashSet<V>();
	}

	@Override
	public V value(K key, int idx) {
		throw new UnsupportedOperationException();
	}
}
