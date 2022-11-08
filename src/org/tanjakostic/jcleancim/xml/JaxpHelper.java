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

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;

import javax.xml.XMLConstants;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.sax.SAXSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.apache.log4j.Logger;
import org.tanjakostic.jcleancim.util.ProgrammerErrorException;
import org.tanjakostic.jcleancim.util.Util;
import org.w3c.dom.Attr;
import org.w3c.dom.CDATASection;
import org.w3c.dom.Comment;
import org.w3c.dom.Document;
import org.w3c.dom.DocumentFragment;
import org.w3c.dom.DocumentType;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * @author tatjana.kostic@ieee.org
 * @version $Id: JaxpHelper.java 21 2019-08-12 15:44:50Z dev978 $
 */
public class JaxpHelper {
	private static final Logger _logger = Logger.getLogger(JaxpHelper.class.getName());

	public static final String INDENT = "2";

	private JaxpHelper() {
		// prevents construction
	}

	// --------- document creation (with/without validation against schema) ----------------

	/**
	 * Returns a document with <code>rootName</code> element. If <code>ns</code> is not null, the
	 * root element is qualified with the namespace, and specifies the binding prefix/URI for that
	 * namespace.
	 *
	 * @param comment
	 *            (potentially null or empty) document comment.
	 * @param rootName
	 *            non-null, non-empty name of the root element.
	 * @param ns
	 *            (potentially null) namespace definition.
	 */
	public static Document createDocumentWithRoot(String comment, String rootName, XmlNs ns) {
		Util.ensureNotEmpty(rootName, "rootName");

		Document dom = new WellformedDOMBuilder().emptyDocument();
		if (Util.hasContent(comment)) {
			Comment commentEl = dom.createComment(comment);
			dom.appendChild(commentEl);
		}

		if (ns != null) {
			String qname = ns.qName(rootName);
			Element root = createQRoot(qname, dom);
			addNamespace(root.getOwnerDocument(), ns);

			// Element root = dom.createElementNS(ns.getUri(), qname);
			// dom.appendChild(root);
		} else {
			dom.appendChild(dom.createElement(rootName));
		}

		return dom;
	}

	/**
	 * Adds the namespace binding definition to DOM <code>dom</code> (to define multiple namespace
	 * bindings on the root element).
	 *
	 * @param dom
	 *            non-null element to which to add the namespace binding.
	 * @param ns
	 *            non-null namespace.
	 */
	public static void addNamespace(Document dom, XmlNs ns) {
		Util.ensureNotNull(dom, "dom");
		Util.ensureNotNull(ns, "ns");

		Element root = dom.getDocumentElement();
		root.setAttributeNS(XMLConstants.XMLNS_ATTRIBUTE_NS_URI, XMLConstants.XMLNS_ATTRIBUTE + ":"
				+ ns.getPrefix(), ns.getUri());
	}

	/**
	 * (useful for testing, to set anything as an XML string) Returns <code>xmlSnippet</code>
	 * embedded into <code>root</code> for the given namespace binding. If both
	 * <code>rootName</code> and <code>ns</code> are null or empty, does not bind any namespace.
	 */
	static XmlString embedIntoRoot(XmlString xmlSnippet, String rootName, XmlNs ns) {
		Util.ensureNotNull(xmlSnippet, "xmlSnippet");
		Util.ensureNotEmpty(rootName, "root");

		StringBuilder sb = new StringBuilder();
		String root = (ns != null) ? ns.qName(rootName) : rootName;
		sb.append("<");
		sb.append(root);
		if (ns != null) {
			String column = (ns.qName(rootName).equals(rootName)) ? "" : ":";
			sb.append(" xmlns").append(column).append(ns.getPrefix()).append("=\"")
			.append(ns.getUri()).append("\"");
		}
		sb.append(">");
		sb.append(xmlSnippet.toString());
		sb.append("</").append(root).append(">");
		return new XmlString(sb.toString());
	}

	/** Creates root element, adds it to the document and returns that new root element. */
	public static Element createQRoot(String qname, Document document) {
		Element root = document.createElement(qname);
		document.appendChild(root);
		return root;
	}

	/** Creates sub-element, adds it to <code>el</code> and returns that new sub-element. */
	public static Element createQSubElement(Element el, String qname, Document document) {
		Element child = document.createElement(qname);
		el.appendChild(child);
		return child;
	}

	/** Adds sub-element to <code>el</code> and returns modified <code>el</code>. */
	public static Element addQSubElement(Element el, String qname, Document document) {
		Element child = document.createElement(qname);
		el.appendChild(child);
		return el;
	}

	/** Adds CDATA section to <code>el</code> and returns modified <code>el</code>. */
	public static Element addCDATA(Element el, String cdata, Document document) {
		CDATASection child = document.createCDATASection(cdata);
		el.appendChild(child);
		return el;
	}

	// ------------------- XPath -------------------------

	/** Returns the xpath instance that recognises all <code>namespaces</code>. */
	public static XPath createXpath(XmlNs... namespaces) {
		XPath xpath = XPathFactory.newInstance().newXPath();
		if (namespaces != null && namespaces.length > 0) {
			NamespaceCache nsContext = new NamespaceCache(namespaces);
			xpath.setNamespaceContext(nsContext);
		}
		return xpath;
	}

	/** Returns compiled xpath expression that recognises all <code>namespaces</code>. */
	public static XPathExpression compileXpath(String xpathExpression, XmlNs... namespaces) {
		XPath xpath = createXpath(namespaces);
		try {
			return xpath.compile(xpathExpression);
		} catch (XPathExpressionException e) {
			throw new IllegalArgumentException(
					"Invalid xpathExpression: '" + xpathExpression + "'", e);
		}
	}

	public static Node selectNode(String xpathExpression, Object ctx, XmlNs[] namespaces) {
		XPath xp = JaxpHelper.createXpath(namespaces);
		try {
			Node envelopeNode = (Node) xp.evaluate(xpathExpression, ctx, XPathConstants.NODE);
			return envelopeNode;
		} catch (XPathExpressionException e) {
			throw new XmlException("Failed evaluation of '" + xpathExpression + "'; ", e);
		}
	}

	public static List<Node> selectNodes(String xpathExpression, Object ctx, XmlNs... namespaces) {
		XPathExpression cxp = JaxpHelper.compileXpath(xpathExpression, namespaces);
		NodeList modelRefNodes;
		try {
			modelRefNodes = (NodeList) cxp.evaluate(ctx, XPathConstants.NODESET);
		} catch (XPathExpressionException e) {
			throw new XmlException("Failed evaluation of '" + xpathExpression + "'; ", e);
		}

		List<Node> result = new ArrayList<Node>();
		for (int i = 0; i < modelRefNodes.getLength(); ++i) {
			result.add(modelRefNodes.item(i));
		}
		return result;
	}

	public static List<Element> selectElements(String xpathExpression, Object ctx,
			XmlNs... namespaces) {
		List<Node> nodes = selectNodes(xpathExpression, ctx, namespaces);

		List<Element> result = new ArrayList<Element>();
		for (Node n : nodes) {
			if (n.getNodeType() == Node.ELEMENT_NODE) {
				result.add((Element) n);
			}
		}
		return result;
	}

	// ---------------- input -------------------

	/**
	 * Parses <code>xmlText</code> and returns DOM document containing it on success, null on
	 * failure or if <code>xmlText</code> is null. Use this method when you require fully validated
	 * document.
	 */
	public static Document parseAsDocument(XmlString xmlText) {
		if (xmlText == null) {
			return null;
		}
		Document result = null;
		try {
			result = new WellformedDOMBuilder().readAndValidate(xmlText);
		} catch (Exception e) {
			_logger.warn("Failed to parse input xmlText, returning null document.");
		}
		return result;
	}

	/**
	 * Parses <code>xmlText</code> and returns DOM document fragment on success, null on failure or
	 * if <code>xmlText</code> is null. Use this method when you require fully validated document
	 * fragment.
	 */
	public static DocumentFragment parseAsFragment(XmlString xmlText) {
		Document d = parseAsDocument(xmlText);
		if (d != null) {
			return d.createDocumentFragment();
		}
		return null;
	}

	// ----------------------- output --------------------------

	/**
	 * Returns <code>documentOrElement</code> as pretty-print XML string and saves it to
	 * <code>xmlFile</code> if not null.
	 */
	public static XmlString asPrettyXml(Node documentOrElement, File xmlFile) {
		return getXml(documentOrElement, null, xmlFile, INDENT);
	}

	/**
	 * Returns <code>documentOrElement</code> as XML string and saves it to <code>xmlFile</code> if
	 * not null.
	 */
	public static XmlString asXml(Node documentOrElement, File xmlFile) {
		return getXml(documentOrElement, null, xmlFile, null);
	}

	/**
	 * Returns <code>xmlText</code> as pretty-print string and saves to <code>xmlFile</code> if not
	 * null.
	 */
	public static XmlString asPrettyXml(XmlString xmlText, File xmlFile) {
		return getXml(null, xmlText, xmlFile, INDENT);
	}

	private static XmlString getXml(Node documentOrElement, XmlString xmlText, File xmlFile,
			String indentAmount) {
		if (documentOrElement == null && xmlText == null) {
			throw new ProgrammerErrorException("Both documentOrElement and xmlText null.");
		}
		if (documentOrElement != null && xmlText != null) {
			throw new ProgrammerErrorException("Both documentOrElement and xmlText non-null.");
		}

		Source xmlSource = null;
		String systemValue = null;
		if (documentOrElement != null) {
			if (documentOrElement instanceof Document) {
				Document doc = (Document) documentOrElement;
				doc.setXmlStandalone(true); // before creating the DOMSource

				DocumentType docType = doc.getDoctype();
				if (docType != null) {
					systemValue = (new File(docType.getSystemId())).getName();
				}
			}
			xmlSource = new DOMSource(documentOrElement);
		} else if (xmlText != null) {
			xmlSource = new SAXSource(XmlUtil.xmlAsInputSource(xmlText));
		}

		// -----------------

		// XMLOutputFactory xmlof = XMLOutputFactory.newInstance();
		// XMLStreamWriter writer = new IndentingXMLStreamWriter(xmlof.createXMLStreamWriter(out));

		// -----------------

		Transformer t = null;
		try {
			t = getConfiguredTransformer(XmlUtil.ENCODING, indentAmount, systemValue);
		} catch (Exception e) {
			throw new XmlException("Couldn't get pretty-print transformer.", e);
		}

		OutputStream os = null;
		try {
			os = (xmlFile != null) ? new FileOutputStream(xmlFile) : new ByteArrayOutputStream();
		} catch (FileNotFoundException e) {
			throw new IllegalArgumentException(e);
		}

		try (Writer writer = new OutputStreamWriter(os, XmlUtil.ENCODING)) {
			t.transform(xmlSource, new StreamResult(writer));
			return new XmlString(os.toString());
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	private static Transformer getConfiguredTransformer(String encoding, String indentAmount,
			String systemValue) throws TransformerConfigurationException,
			TransformerFactoryConfigurationError {
		Transformer t = TransformerFactory.newInstance().newTransformer();
		t.setOutputProperty(OutputKeys.ENCODING, encoding);
		t.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "no");
		t.setOutputProperty(OutputKeys.METHOD, "xml");
		if (systemValue != null) {
			t.setOutputProperty(OutputKeys.DOCTYPE_SYSTEM, systemValue);
		}
		if (indentAmount != null) {
			t.setOutputProperty(OutputKeys.INDENT, "yes");
			t.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", indentAmount);
		}
		_logger.debug(t.getOutputProperties().toString());
		return t;
	}

	// ------------------------- iteration (for what a clumsy DOM API !) --------------------

	/** Returns all elements under <code>el</code>, potentially empty list. */
	public static List<Element> getSubElements(Element el) {
		return getSubElements(el, null, false);
	}

	/**
	 * Returns elements under <code>el</code> with the <code>name</code> if found, empty list
	 * otherwise.
	 */
	public static List<Element> getNamedSubElements(Element el, String name) {
		return getSubElements(el, name, false);
	}

	/**
	 * Returns first element under <code>el</code> with the <code>name</code> if found, null
	 * otherwise.
	 */
	public static Element getFirstNamedSubElement(Element el, String name) {
		List<Element> all = getSubElements(el, name, true);
		if (all.isEmpty()) {
			return null;
		}
		return all.get(0);
	}

	private static List<Element> getSubElements(Element el, String name, boolean single) {
		List<Element> result = new ArrayList<Element>();
		NodeList nodes = el.getChildNodes();
		for (int i = 0; i < nodes.getLength(); i++) {
			Node node = nodes.item(i);
			if (node.getNodeType() == Node.ELEMENT_NODE) {
				Element child = (Element) node;
				if (name == null || name.equals(child.getNodeName())) {
					result.add(child);
					if (single) {
						return result;
					}
				}
			}
		}
		return result;
	}

	/** Returns all attributes of <code>el</code>, potentially empty list. */
	public static List<Attr> getAttributes(Element el) {
		return getAttributes(el, null);
	}

	/** Returns attribute of <code>el</code> with the <code>name</code> if found, null otherwise. */
	public static Attr getNamedAttribute(Element el, String name) {
		Util.ensureNotNull(name, "name");
		List<Attr> all = getAttributes(el, name);
		if (all.isEmpty()) {
			return null;
		}
		return all.get(0);
	}

	private static List<Attr> getAttributes(Element el, String name) {
		List<Attr> result = new ArrayList<Attr>();
		NamedNodeMap attrs = el.getAttributes();
		for (int i = 0; i < attrs.getLength(); i++) {
			Attr child = (Attr) attrs.item(i);
			if (name == null || name.equals(child.getNodeName())) {
				result.add(child);
			}
		}
		return result;
	}
}
