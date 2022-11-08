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

package org.tanjakostic.jcleancim.model;

import org.tanjakostic.jcleancim.util.Util;

/**
 * Container for a name-value pair to hold the URI and prefix for a namespace that can be associated
 * to almost any UML object; independent of UML object nature.
 *
 * @author tatjana.kostic@ieee.org
 * @version $Id$
 */
public class Namespace {

	public static final Namespace EMPTY = new Namespace("", "");

	private final String _uri;
	private final String _prefix;

	public static Namespace create(String uri, String prefix) {
		String uriStr = Util.null2empty(uri).trim();
		String prefixStr = Util.null2empty(prefix).trim();
		if (uriStr.isEmpty() && prefixStr.isEmpty()) {
			return EMPTY;
		}
		return new Namespace(uriStr, prefixStr);
	}

	/**
	 * Constructor.
	 *
	 * @param uri
	 *            potentially null or empty uri; will be trimmed before storing.
	 * @param prefix
	 *            potentially null or empty prefix; will be trimmed before storing.
	 */
	private Namespace(String uri, String prefix) {
		_uri = Util.null2empty(uri).trim();
		_prefix = Util.null2empty(prefix).trim();
	}

	/** Potentially empty namespace URI. */
	public String getUri() {
		return _uri;
	}

	/** Potentially empty namespace prefix. */
	public String getPrefix() {
		return _prefix;
	}

	public boolean hasPrefix() {
		return !_prefix.isEmpty();
	}

	public boolean hasURI() {
		return !_uri.isEmpty();
	}

	/** Returns XML expression usable when defining this namespace with its prefix. */
	public String getAsMappedNs() {
		return "xmlns:" + _prefix + "\"" + _uri + "\"";
	}

	/** Returns XML expression usable when defining this namespace as default namespace. */
	public String getAsDefaultNs() {
		return "xmlns:\"" + _uri + "\"";
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("prefix='").append(_prefix).append("'");
		sb.append(", uri='").append(_uri).append("'");
		return sb.toString();
	}
}
