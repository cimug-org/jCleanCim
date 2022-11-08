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

import org.tanjakostic.jcleancim.xml.XmlNs;

/**
 * Common implementation for various tags that appear in CIM RDF Schema.
 *
 * @author tatjana.kostic@ieee.org
 * @version $Id: XmlTag.java 21 2019-08-12 15:44:50Z dev978 $
 */
public abstract class XmlTag {

	private final XmlNs _ns;
	private final String _name;

	private String _qname;
	private String _uri;

	/**
	 * Constructor.
	 *
	 * @param ns
	 * @param name
	 */
	protected XmlTag(XmlNs ns, String name) {
		_name = name;
		_ns = ns;
	}

	/** Returns the qualified name of this tag (e.g., rdf:about). */
	protected final String getQName() {
		if (_qname == null) {
			_qname = _ns.qName(getName());
		}
		return _qname;
	}

	// --------------- API ---------------

	/** Returns the name of this tag (e.g., about). */
	public final String getName() {
		return _name;
	}

	/** Returns the URI of this tag (e.g., http://...#about). */
	public final String getURI() {
		if (_uri == null) {
			_uri = _ns.getUri() + getName();
		}
		return _uri;
	}

	/** Returns qualified name of this tag as string (e.g., rdf:about). */
	@Override
	public String toString() {
		return getQName();
	}
}
