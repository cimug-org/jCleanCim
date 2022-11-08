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

import javax.xml.parsers.DocumentBuilderFactory;

import org.tanjakostic.jcleancim.util.ProgrammerErrorException;

/**
 * SAX reader configured to validate against the internal schema (specified in the instance file
 * through schema location).
 *
 * @author tatjana.kostic@ieee.org
 * @version $Id: InternalXsdValidatingDOMBuilder.java 1940 2011-08-21 23:16:38Z
 *          tatjana.kostic@ieee.org $
 */
public class InternalXsdValidatingDOMBuilder extends AbstractXsdValidatingDOMBuilder {
	private static final String JAXP_SCHEMA_LANGUAGE = "http://java.sun.com/xml/jaxp/properties/schemaLanguage";

	public InternalXsdValidatingDOMBuilder() {
		super(null, true);
	}

	@Override
	protected void configureBuilderFactoryWithSchema(DocumentBuilderFactory builderFactory) {
		if (getExternalSchema() != null) {
			throw new ProgrammerErrorException("Should not have external XSD set.");
		}
		builderFactory.setAttribute(InternalXsdValidatingDOMBuilder.JAXP_SCHEMA_LANGUAGE,
				W3C_XML_SCHEMA);
	}
}
