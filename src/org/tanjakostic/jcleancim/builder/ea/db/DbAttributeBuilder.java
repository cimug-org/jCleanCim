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

package org.tanjakostic.jcleancim.builder.ea.db;

import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.tanjakostic.jcleancim.builder.ea.AttributeBuilder;
import org.tanjakostic.jcleancim.builder.ea.ClassBuilder;
import org.tanjakostic.jcleancim.builder.ea.EA;
import org.tanjakostic.jcleancim.builder.ea.EaHelper;
import org.tanjakostic.jcleancim.builder.ea.EaModelBuilder;
import org.tanjakostic.jcleancim.model.UmlStereotype;
import org.tanjakostic.jcleancim.util.Util;

/**
 * @author tatjana.kostic@ieee.org
 * @version $Id: DbAttributeBuilder.java 21 2019-08-12 15:44:50Z dev978 $
 */
class DbAttributeBuilder extends AttributeBuilder<Map<String, String>, EaModelBuilder<?, ?>> {
	private static final Logger _logger = Logger.getLogger(DbAttributeBuilder.class.getName());

	/**
	 * Constructor.
	 *
	 * @param inData
	 * @param containingClass
	 * @param eaHelper
	 */
	public DbAttributeBuilder(Map<String, String> inData,
			ClassBuilder<?, ?, ?, ?, ?, ?> containingClass, EaHelper eaHelper) {
		super(inData, containingClass.getContainingPackage().getModel(), containingClass, eaHelper);
	}

	@Override
	protected Integer getAttributeID(Map<String, String> inData) {
		return Util.parseInt(inData.get(EA.ATTR_ID));
	}

	@Override
	protected String getAttributeGUID(Map<String, String> inData) {
		return inData.get(EA.EA_GUID);
	}

	@Override
	protected String getAttributeName(Map<String, String> inData) {
		return inData.get(EA.ATTR_NAME);
	}

	@Override
	protected String getAttributeAlias(Map<String, String> inData) {
		return inData.get(EA.ATTR_STYLE);
	}

	@Override
	protected String getAttributeStereotypes(Map<String, String> inData) {
		return inData.get(EA.DEDUCED_STEREOS);
	}

	@Override
	protected String getAttributeVisibility(Map<String, String> inData) {
		return inData.get(EA.ATTR_SCOPE);
	}

	@Override
	protected String getAttributeNotes(Map<String, String> inData) {
		return inData.get(EA.ATTR_NOTE);
	}

	@Override
	protected boolean getAttributeIsConst(Map<String, String> inData) {
		return "1".equals(inData.get(EA.ATTR_CONST)); // 0/1 -> false/true
	}

	@Override
	protected boolean getAttributeIsStatic(Map<String, String> inData) {
		return "1".equals(inData.get(EA.ATTR_STATIC)); // 0/1 -> false/true
	}

	/**
	 * {@inheritDoc}
	 * <p>
	 * Depends on stereotype being initialised. For SOME attributes with {@link UmlStereotype#ENUM},
	 * there is mismatch in values obtained through/seen in the (EA GUI + API) vs. (SQL/XML query
	 * result or cell value in the table):
	 * <ul>
	 * <li>GUI displays/API returns bounds [1..1] (as when you create any attribute on a class, by
	 * default)
	 * <li>DB does not show/store multiplicities which means the XML elements are absent (null) or
	 * the cell value returned is empty
	 * </ul>
	 * For these conrner cases, we fix the bound to 1 and log a warning.
	 */
	@Override
	protected String getAttributeLowerBound(Map<String, String> inData) {
		String lowerBound = inData.get(EA.ATTR_LOBOUND);
		return fixEmptyMultiplicityBound(lowerBound, "lower", "1");
	}

	/**
	 * {@inheritDoc}
	 * <p>
	 * Depends on stereotype being initialised; see {@link #getAttributeLowerBound(Map)}.
	 */
	@Override
	protected String getAttributeUpperBound(Map<String, String> inData) {
		String upperBound = inData.get(EA.ATTR_UPBOUND);
		return fixEmptyMultiplicityBound(upperBound, "upper", "1");
	}

	private String fixEmptyMultiplicityBound(String boundValue, String which, String newBoundValue) {
		if (!boundValue.isEmpty()) {
			return boundValue;
		}

		String kind = getObjData().getStereotype().contains(UmlStereotype.ENUM) ? "enum" : "attr";
		_logger.debug("   fixing " + which + " bound '" + boundValue + "' to 1 for " + kind + ":"
				+ getObjData());
		return newBoundValue;
	}

	@Override
	protected String getAttributeDefaultValue(Map<String, String> inData) {
		return inData.get(EA.ATTR_DEFAULT);
	}

	@Override
	protected int getAttributeClassifierID(Map<String, String> inData) {
		String typeIdString = inData.get(EA.ATTR_CLASSIF);
		return Util.parseIntZero(typeIdString).intValue();
	}

	@Override
	protected String getAttributeType(Map<String, String> inData) {
		return Util.null2empty(inData.get(EA.ATTR_TYPE));
	}

	@Override
	protected int getAttributePosition(Map<String, String> inData) {
		return Util.parseInt(inData.get(EA.ATTR_POSITION)).intValue();
	}

	@Override
	protected List<Map<String, String>> fetchTaggedValues(EaModelBuilder<?, ?> tagsSrc) {
		return tagsSrc.getTables().findAttributeTags(getObjData().getId());
	}

	@Override
	protected List<Map<String, String>> fetchAttrConstraints(EaModelBuilder<?, ?> tagsSrc) {
		return tagsSrc.getTables().findAttributeConstraints(getObjData().getId());
	}
}
