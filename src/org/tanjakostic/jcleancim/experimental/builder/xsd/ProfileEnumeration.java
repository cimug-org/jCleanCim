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

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.w3c.dom.Element;

/**
 * @author tatjana.kostic@ieee.org
 * @version $Id: ProfileEnumeration.java 21 2019-08-12 15:44:50Z dev978 $
 */
class ProfileEnumeration extends ProfileObject {
	private static final Logger _logger = Logger.getLogger(ProfileEnumeration.class.getName());

	final List<ProfileLiteral> literals = new ArrayList<ProfileLiteral>();

	ProfileEnumeration(Element xmlElement, String nsPrefix, String nsUri, String name,
			List<String> descriptionParas) {
		super(xmlElement, nsPrefix, nsUri, name, descriptionParas);

		_logger.trace("parsed from profile: " + toString());
	}

	void addLiteral(ProfileLiteral profileLiteral) {
		literals.add(profileLiteral);
	}

	List<String> getLiteralNames() {
		List<String> result = new ArrayList<String>();
		for (ProfileLiteral literal : literals) {
			result.add(literal.name);
		}
		return result;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder(super.toString());
		sb.append("=").append(getLiteralNames());
		return sb.toString();
	}
}
