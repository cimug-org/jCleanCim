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
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.xml.XMLConstants;
import javax.xml.namespace.NamespaceContext;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

/**
 * Adapted from
 * <a href="http://www.ibm.com/developerworks/xml/library/x-nmspccontext/index.html?ca=drs-">Read
 * the namespaces from the document and cache them</a>
 *
 * @author tatjana.kostic@ieee.org
 * @version $Id: NamespaceCache.java 21 2019-08-12 15:44:50Z dev978 $
 */
public class NamespaceCache implements NamespaceContext {
	private static final Logger _logger = Logger.getLogger(NamespaceCache.class.getName());

	private static final String DEFAULT_NS = "DEFAULT";
	private final Map<String, String> _prefix2Uri = new HashMap<String, String>();
	private final Map<String, Set<String>> _uri2Prefix = new HashMap<String, Set<String>>();

	/**
	 * Constructor parses the document and stores all namespaces it can find.
	 *
	 * @param document
	 *            source document
	 * @param rootOnly
	 *            restriction of the search to enhance performance; only namespaces in the root are
	 *            stored.
	 */
	public NamespaceCache(Document document, boolean rootOnly) {
		examineNode(document.getDocumentElement(), rootOnly);
		log(Level.DEBUG);
	}

	/** Constructor initialises the cache from non-null, non-empty <code>nsMappings</code>. */
	public NamespaceCache(XmlNs... nsMappings) {
		for (XmlNs ns : nsMappings) {
			cache(ns.getPrefix(), ns.getUri());
		}
		log(Level.DEBUG);
	}

	private void log(Level level) {
		_logger.log(level, "The list of the cached namespaces:");
		for (Entry<String, String> entry : _prefix2Uri.entrySet()) {
			_logger.log(level, "prefix " + entry.getKey() + ": uri " + entry.getValue());
		}
	}

	/**
	 * A single node is read, the namespace attributes are extracted and stored.
	 *
	 * @param node
	 *            to examine
	 * @param shallow
	 *            if true, no recursion happens
	 */
	private void examineNode(Node node, boolean shallow) {
		List<Attr> attributes = JaxpHelper.getAttributes((Element) node);
		for (Attr attr : attributes) {
			storeAttribute(attr);
		}

		if (!shallow) {
			List<Element> children = JaxpHelper.getSubElements((Element) node);
			for (Element child : children) {
				examineNode(child, false);
			}
		}
	}

	/** Looks at an attribute and stores it, if it is a namespace attribute. */
	private void storeAttribute(Attr xa) {
		// examine the attributes in namespace xmlns
		String attributeNsUri = xa.getNamespaceURI();
		if (attributeNsUri != null && attributeNsUri.equals(XMLConstants.XMLNS_ATTRIBUTE_NS_URI)) {
			if (xa.getNodeName().equals(XMLConstants.XMLNS_ATTRIBUTE)) {
				cache(DEFAULT_NS, xa.getNodeValue()); // default namespace xmlns="uri"
			} else {
				cache(xa.getLocalName(), xa.getNodeValue()); // defined prefixes
			}
		}

	}

	/** Adds mapping for <code>prefix</code> and <code>uri</code> to the cache. */
	public void addMapping(String prefix, String uri) {
		cache(prefix, uri);
	}

	/**
	 * Returns namespace instance if <code>uri</code> has been bound to a prefix, null otherwise.
	 */
	public XmlNs getXmlNs(String uri) {
		String pref = getPrefix(uri);
		if (pref == null) {
			return null;
		}
		return new XmlNs(pref, uri);
	}

	public List<XmlNs> getAllXmlNs() {
		List<XmlNs> result = new ArrayList<>();
		for (Entry<String, String> entry : _prefix2Uri.entrySet()) {
			result.add(new XmlNs(entry.getKey(), entry.getValue()));
		}
		return result;
	}

	private void cache(String prefix, String uri) {
		_prefix2Uri.put(prefix, uri);

		if (!_uri2Prefix.containsKey(uri)) {
			_uri2Prefix.put(uri, new LinkedHashSet<String>());
		}
		_uri2Prefix.get(uri).add(prefix);
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		for (Entry<String, Set<String>> entry : _uri2Prefix.entrySet()) {
			sb.append(entry.getKey()).append(":").append(entry.getValue().toString()).append("; ");
		}
		return sb.toString();
	}

	// ===== Impl. of javax.xml.namespace.NamespaceContext methods =====

	/**
	 * {@inheritDoc}
	 * <p>
	 * Method called by XPath; returns the default namespace, if the prefix is null or "".
	 */
	@Override
	public String getNamespaceURI(String prefix) {
		if (prefix == null || prefix.equals(XMLConstants.DEFAULT_NS_PREFIX)) {
			return _prefix2Uri.get(DEFAULT_NS);
		}
		return _prefix2Uri.get(prefix);
	}

	@Override
	public String getPrefix(String namespaceURI) {
		if (namespaceURI == null) {
			throw new IllegalArgumentException("Null namespaceURI is illegal.");
		}
		if (_uri2Prefix.containsKey(namespaceURI)) {
			return _uri2Prefix.get(namespaceURI).iterator().next();
		}
		return null;
	}

	@SuppressWarnings("rawtypes")
	@Override
	public Iterator getPrefixes(String namespaceURI) {
		if (_uri2Prefix.containsKey(namespaceURI)) {
			return _uri2Prefix.get(namespaceURI).iterator();
		}
		return Collections.emptySet().iterator();
	}
}
