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

package org.tanjakostic.jcleancim.experimental.builder.xsd;

import java.util.List;

import org.w3c.dom.Element;

/**
 * @author tatjana.kostic@ieee.org
 * @version $Id: ProfileObject.java 21 2019-08-12 15:44:50Z dev978 $
 */
abstract class ProfileObject {
	final Element xmlElement;
	final String nsPrefix;
	final String nsUri;
	final String name;
	final List<String> descriptionParas;

	/**
	 * Constructor.
	 *
	 * @param xmlElement
	 * @param nsPrefix
	 * @param nsUri
	 * @param name
	 * @param descriptionParas
	 */
	protected ProfileObject(Element xmlElement, String nsPrefix, String nsUri, String name,
			List<String> descriptionParas) {
		super();
		this.xmlElement = xmlElement;
		this.nsPrefix = nsPrefix;
		this.nsUri = nsUri;
		this.name = name;
		this.descriptionParas = descriptionParas;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append(getClass().getSimpleName()).append(" ").append(name);
		return sb.toString();
	}
}
