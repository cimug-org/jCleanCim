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

import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;

/**
 * SAX reader configured to check wellformed-ness only.
 *
 * @author tatjana.kostic@ieee.org
 * @version $Id: WellformedDOMBuilder.java 21 2019-08-12 15:44:50Z dev978 $
 */
public class WellformedDOMBuilder extends AbstractConfiguredDOMBuilder {

	public WellformedDOMBuilder() {
		this(false);
	}

	protected WellformedDOMBuilder(boolean respectDtd) {
		super(respectDtd);
	}

	public Document emptyDocument() {
		try {
			return getDOMBuilder().newDocument();
		} catch (ParserConfigurationException e) {
			throw new XmlException("Failed to create empty document", e);
		}
	}
}
