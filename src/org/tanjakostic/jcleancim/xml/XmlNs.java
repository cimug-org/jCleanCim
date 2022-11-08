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

import javax.xml.XMLConstants;

import org.tanjakostic.jcleancim.util.Util;

/**
 * Namespace mappings for known and unknown namespaces.
 *
 * @author tatjana.kostic@ieee.org
 * @version $Id: XmlNs.java 21 2019-08-12 15:44:50Z dev978 $
 */
public class XmlNs {
	public static final XmlNs xsi = new XmlNs("xsi", XMLConstants.W3C_XML_SCHEMA_INSTANCE_NS_URI);

	public static final String FRAG_SEP = "#";

	private final String _prefix;
	private final String _uri;

	public XmlNs(String prefix, String uri) {
		this(prefix, uri, null);
	}

	public XmlNs(String prefix, String uri, NamespaceCache cache) {
		Util.ensureNotEmpty(uri, "uri");

		_prefix = prefix == null ? "" : prefix.trim();
		_uri = uri.trim();
		if (cache != null) {
			cache.addMapping(prefix, uri);
		}
	}

	// ------------------ API --------------------

	/** Returns the prefix used for this namespace (e.g., "rdf"). */
	public final String getPrefix() {
		return _prefix;
	}

	/**
	 * Returns qualified name for <code>name</code> with this instance's prefix as qualifier (e.g.,
	 * "rdf:name" or "name" if namespace prefix null or empty).
	 *
	 * @param name
	 *            name to qualify.
	 */
	public String qName(String name) {
		return asPrefix() + name;
	}

	/**
	 * Returns this namespace as prefix, ready to concatenate (e.g., "rdf:", or "" if prefix is null
	 * or empty).
	 */
	final String asPrefix() {
		return !Util.hasContent(getPrefix()) ? "" : (getPrefix() + ":");
	}

	/** Returns URI of this namespace, as found in the root element (e.g., "http://...#"). */
	public final String getUri() {
		return _uri;
	}

	/** Returns {@link #getUri()} with fragment separator trimmed out. */
	public final String getUriWithoutFragmentSeparator() {
		String uri = getUri();
		if (uri.endsWith(FRAG_SEP)) {
			int idx = uri.length() - 1;
			return uri.substring(0, idx);
		}
		return uri;
	}

	@Override
	public String toString() {
		return "{" + getPrefix() + " : " + getUri() + "}";
	}
}
