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

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

import org.tanjakostic.jcleancim.util.Util;

/**
 * Default implementation.
 *
 * @author tatjana.kostic@ieee.org
 * @version $Id: RawDataImpl.java 21 2019-08-12 15:44:50Z dev978 $
 */
public class RawDataImpl implements RawData {

	private final Map<String, String> _rawFields = new LinkedHashMap<String, String>();

	@Override
	public final String putCell(String key, String value) {
		Util.ensureNotEmpty(key, "key");
		Util.ensureNotNull(value, "value");
		return _rawFields.put(key, value);
	}

	@Override
	public String copyCell(RawData src, String key) {
		if (src.hasKey(key)) {
			return putCell(key, src.getCell(key));
		}
		return null;
	}

	@Override
	public String copyNonEmptyCell(RawData src, String key) {
		if (src.hasKey(key)) {
			String value = src.getCell(key);
			if (!value.isEmpty()) {
				return putCell(key, value);
			}
		}
		return null;
	}

	@Override
	public String putCellNonEmpty(String key, String value) {
		Util.ensureNotEmpty(key, "key");
		Util.ensureNotNull(value, "value");
		if (!value.isEmpty()) {
			return _rawFields.put(key, value);
		}
		return null;
	}

	@Override
	public boolean hasKey(String key) {
		return _rawFields.containsKey(key);
	}

	@Override
	public Map<String, String> getCells() {
		return Collections.unmodifiableMap(_rawFields);
	}

	@Override
	public String getCell(String key) {
		return _rawFields.get(key);
	}
}
