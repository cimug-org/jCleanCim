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

package org.tanjakostic.jcleancim.builder.ea.sqlxml;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;
import org.tanjakostic.jcleancim.builder.ea.EaSelector;
import org.tanjakostic.jcleancim.builder.ea.EaSql2Xml;
import org.tanjakostic.jcleancim.util.Util;
import org.tanjakostic.jcleancim.xml.JaxpHelper;
import org.tanjakostic.jcleancim.xml.XmlString;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * EA repository supports a method to perform an SQL query and return the result set as XML. This
 * class is a wrapper to that EA functionality without dependency on EA.
 *
 * @author tatjana.kostic@ieee.org
 * @version $Id: SqlXmlSelector.java 31 2019-12-08 01:19:54Z dev978 $
 */
public class SqlXmlSelector implements EaSelector {
	private static final Logger _logger = Logger.getLogger(SqlXmlSelector.class.getName());

	private final EaSql2Xml _queror;

	public SqlXmlSelector(EaSql2Xml queror) {
		_queror = queror;
	}

	private String sqlSelect(String what, String tableName, boolean skipTiming) {
		long start = System.currentTimeMillis();

		String queryStmt = String.format("SELECT %s FROM %s", what, tableName);
		String xmlResult = _queror.sqlResultAsXml(queryStmt);

		String time = skipTiming ? "" : ((System.currentTimeMillis() - start) + " ms: ");
		_logger.info(time + "completed query: " + queryStmt);
		return xmlResult;
	}

	private Document loadXmlResult(String xmlResult) {
		long start = System.currentTimeMillis();

		Document doc = JaxpHelper.parseAsDocument(new XmlString(xmlResult));

		_logger.info((System.currentTimeMillis() - start) + " ms : parsed XML result");
		return doc;
	}

	private List<Map<String, String>> xmlToMap(String[] columnNames, Document doc) {
		long start = System.currentTimeMillis();

		List<Map<String, String>> result = new ArrayList<Map<String, String>>();
		Set<String> tags = new HashSet<String>(Arrays.asList(columnNames));

		walkNodes(doc.getDocumentElement(), tags, result);

		_logger.info((System.currentTimeMillis() - start) + " ms: populated " + result.size()
				+ " items with tags: " + tags);
		_logger.info("..........");
		return result;
	}

	/** Recursive: Loops on child nodes of <code>element</code>; if "Row", go further down */
	private void walkNodes(Element element, Set<String> tags, List<Map<String, String>> result) {
		NodeList nodes = element.getChildNodes();
		for (int i = 0; i < nodes.getLength(); i++) {
			Node node = nodes.item(i);
			if (node.getNodeType() == Node.ELEMENT_NODE) {
				if ("Row".equals(node.getNodeName())) {
					Map<String, String> fields = selectKeyValueTexts((Element) node, tags);
					result.add(fields);
				} else {
					walkNodes((Element) node, tags, result);
				}
			}
		}
	}

	/**
	 * Loops on child elements of <code>row</code> and puts into a map the values for
	 * <code>tags</code>; if there is no element for a <code>tag</code>, adds the key with an empty
	 * value (to ensure we never get nulls for expected keys).
	 */
	private Map<String, String> selectKeyValueTexts(Element rowEl, Set<String> tags) {
		Map<String, String> row = new HashMap<String, String>();
		Set<String> presentTags = new HashSet<String>();
		NodeList underRowList = rowEl.getChildNodes();
		for (int k = 0; k < underRowList.getLength(); k++) {
			Node cellNode = underRowList.item(k);
			if (cellNode.getNodeType() == Node.ELEMENT_NODE) {
				if (tags.contains(cellNode.getNodeName())) {
					row.put(cellNode.getNodeName(), cellNode.getTextContent());
					presentTags.add(cellNode.getNodeName());
				}
			}
		}
		// By here, presentTags contains all those that have been found as elements.
		Set<String> absentTags = new HashSet<String>(tags);
		absentTags.removeAll(presentTags);
		for (String tag : absentTags) {
			row.put(tag, "");
		}

		return row;
	}

	// ===== Impl. of org.tanjakostic.jcleancim.builder.ea.EaSelector methods =====

	@Override
	public List<Map<String, String>> select(String tableName, String[] columnNames,
			boolean logTime) {
		String what = Util.concatCharSeparatedTokens(",", Arrays.asList(columnNames));
		String xmlResult = sqlSelect(what, tableName, logTime);

		Document doc = loadXmlResult(xmlResult);

		// since 01v08, new implementation based on recursion instead of XPath = much faster!
		return xmlToMap(columnNames, doc);
	}
}
