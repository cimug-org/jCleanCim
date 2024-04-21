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

package org.tanjakostic.jcleancim.experimental.builder.rdfs;

import java.util.List;

import org.w3c.dom.Element;

/**
 * CIM RDF Schema element representing the UML attribute defined on an enum class.
 *
 * @author tatjana.kostic@ieee.org
 * @version $Id: RdfsEnumLiteral.java 21 2019-08-12 15:44:50Z dev978 $
 */
public final class RdfsEnumLiteral extends RdfsElem {

	private final String _type;

	/**
	 * Creates instance from DOM Element.
	 *
	 * @param model
	 * @param elem
	 * @throws CimSchemaException
	 *             if about attribute contains an invalid URI.
	 */
	RdfsEnumLiteral(RdfsModel model, Element elem) throws CimSchemaException {
		super(model, elem);
		_type = XmlChildElement.getResourceName(elem, XmlChildElement.type);
	}

	/**
	 * Creates instance independently of DOM Element.
	 *
	 * @param model
	 * @param about
	 *            (=schemaLabel#name)
	 * @param label
	 * @param comment
	 * @param pckage
	 * @param type
	 * @throws CimSchemaException
	 *             if about attribute contains an invalid URI.
	 */
	RdfsEnumLiteral(RdfsModel model, String about, String label, String comment, String pckage,
			String type) throws CimSchemaException {
		super(model, about, label, comment, pckage, true);
		_type = XmlChildElement.getValidatedUri(type).toString();
	}

	// --------------------- API --------------------------

	@Override
	public String getKind() {
		return "enumLiteral";
	}

	@Override
	public String toString() {
		return super.toString();
	}

	@Override
	public String toStringLong() {
		StringBuilder s = new StringBuilder(super.toStringLong());
		s.append("  type = '").append(_type).append("'\n");
		return s.toString();
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((_type == null) ? 0 : _type.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (!super.equals(obj)) {
			return false;
		}
		if (!(obj instanceof RdfsEnumLiteral)) {
			return false;
		}
		return doEquals(true, obj, null);
	}

	/**
	 * @param isForEquals
	 * @param obj
	 * @param diffs
	 *            collecting parameter
	 */
	private boolean doEquals(boolean isForEquals, Object obj, List<RdfsDifference> diffs) {
		assert (!isForEquals ? diffs != null : true) : "diffs must be non-null for collecting";

		RdfsEnumLiteral other = (RdfsEnumLiteral) obj;

		if (!_type.equals(other._type)) {
			if (isForEquals) {
				return false;
			}
			diffs.add(formatDiff("type", _type, other._type, other));
		}
		return true;
	}

	@Override
	public final List<RdfsDifference> getDiffs(RdfsElem other) {
		List<RdfsDifference> diffsCollector = super.getDiffs(other);
		if (this == other || other == null) {
			return diffsCollector;
		}

		doEquals(false, other, diffsCollector);
		return diffsCollector;
	}
}
