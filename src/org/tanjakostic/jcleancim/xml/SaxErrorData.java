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

package org.tanjakostic.jcleancim.xml;

import java.util.ArrayList;
import java.util.List;

import org.tanjakostic.jcleancim.util.Util;

/**
 * Simple storage for XML validation warnings and errors.
 *
 * @author tatjana.kostic@ieee.org
 * @version $Id: SaxErrorData.java 21 2019-08-12 15:44:50Z dev978 $
 */
public class SaxErrorData {
	private final List<String> _warns = new ArrayList<String>();
	private final List<String> _errors = new ArrayList<String>();
	private final List<String> _fatals = new ArrayList<String>();

	public SaxErrorData() {
		// nothing to initialise
	}

	public void addWarn(String warn) {
		_warns.add(warn);
	}

	public List<String> getWarns() {
		return _warns;
	}

	public void addError(String error) {
		_errors.add(error);
	}

	public List<String> getErrors() {
		return _errors;
	}

	public void addFatal(String fatal) {
		_fatals.add(fatal);
	}

	public List<String> getFatals() {
		return _fatals;
	}

	/** Returns true if neither of warning, error or fatal has been stored. */
	public boolean isEmpty() {
		return getWarns().isEmpty() && getErrors().isEmpty() && getFatals().isEmpty();
	}

	public boolean hasErrorOrFatal() {
		return !getErrors().isEmpty() || !getFatals().isEmpty();
	}

	public List<String> getAll() {
		List<String> result = new ArrayList<String>(getWarns());
		result.addAll(getErrors());
		result.addAll(getFatals());
		return result;
	}

	/** If you are using this instance multiple times, ensure you always first reset it. */
	public void reset() {
		_fatals.clear();
		_errors.clear();
		_warns.clear();
	}

	@Override
	public String toString() {
		StringBuffer sb = new StringBuffer();
		for (String item : getAll()) {
			sb.append(item).append(Util.NL);
		}
		return sb.toString();
	}
}
