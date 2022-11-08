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

import java.io.InputStream;

/**
 * DOM builder configured to validate against both the external schema (specified programmatically,
 * by the code) and the internal DTD (specified in the instance file through DOCTYPE).
 *
 * @author tatjana.kostic@ieee.org
 * @version $Id: ExternalXsdInternalDtdValidatingDOMBuilder.java 1940 2011-08-21 23:16:38Z
 *          tatjana.kostic@ieee.org $
 */
public class ExternalXsdInternalDtdValidatingDOMBuilder extends ExternalXsdValidatingDOMBuilder {
	public ExternalXsdInternalDtdValidatingDOMBuilder(InputStream externalSchema) {
		super(externalSchema, true);
	}
}
