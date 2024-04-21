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

import org.w3c.dom.Element;

/**
 * CIM RDF Schema element representing the UML package.
 *
 * @author tatjana.kostic@ieee.org
 * @version $Id: RdfsPackage.java 21 2019-08-12 15:44:50Z dev978 $
 */
public final class RdfsPackage extends RdfsElem {
	/**
	 * Creates instance from DOM Element.
	 *
	 * @param model
	 * @param elem
	 * @throws CimSchemaException
	 *             if about attribute contains an invalid URI.
	 */
	RdfsPackage(RdfsModel model, Element elem) throws CimSchemaException {
		super(model, elem);
	}

	/**
	 * Creates instance independently of dom4j Element.
	 *
	 * @param model
	 * @param about
	 * @param label
	 * @param comment
	 * @param pckage
	 * @param validateAbout
	 * @throws CimSchemaException
	 */
	RdfsPackage(RdfsModel model, String about, String label, String comment, String pckage,
			boolean validateAbout) throws CimSchemaException {
		super(model, about, label, comment, pckage, validateAbout);
	}

	// -------------------- API -----------------------

	@Override
	public String getKind() {
		return "package";
	}
}
