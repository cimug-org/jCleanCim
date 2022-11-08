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

/**
 * Wrapper for a java string; allows us to use consistent approach for instantiation from XML string
 * in case there are classes that require a single non-XML string argument in constructor.
 *
 * @author tatjana.kostic@ieee.org
 * @version $Id: XmlString.java 21 2019-08-12 15:44:50Z dev978 $
 */
public class XmlString {

	private final String _s;

	public XmlString(String s) {
		_s = s;
	}

	@Override
	public String toString() {
		return _s;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (_s == null ? 0 : _s.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		XmlString other = (XmlString) obj;
		if (_s == null) {
			if (other._s != null) {
				return false;
			}
		} else if (!_s.equals(other._s)) {
			return false;
		}
		return true;
	}
}
