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

import java.util.Map;

import org.tanjakostic.jcleancim.builder.ea.EA;
import org.tanjakostic.jcleancim.builder.ea.EaHelper;
import org.tanjakostic.jcleancim.builder.ea.OperationBuilder;
import org.tanjakostic.jcleancim.builder.ea.ParameterBuilder;
import org.tanjakostic.jcleancim.util.Util;

/**
 * @author tatjana.kostic@ieee.org
 * @version $Id: DbParameterBuilder.java 21 2019-08-12 15:44:50Z dev978 $
 */
class DbParameterBuilder extends ParameterBuilder<Map<String, String>> {

	/**
	 * Constructor.
	 *
	 * @param inData
	 * @param containingOperation
	 * @param eaHelper
	 */
	public DbParameterBuilder(Map<String, String> inData,
			OperationBuilder<?, ?> containingOperation, EaHelper eaHelper) {
		super(inData, containingOperation, eaHelper);
	}

	@Override
	protected String getParameterGUID(Map<String, String> inData) {
		return inData.get(EA.EA_GUID);
	}

	@Override
	protected String getParameterName(Map<String, String> inData) {
		return inData.get(EA.PAR_NAME);
	}

	@Override
	protected String getParameterAlias(Map<String, String> inData) {
		return inData.get(EA.PAR_ALIAS);
	}

	@Override
	protected String getParameterStereotypes(Map<String, String> inData) {
		return inData.get(EA.DEDUCED_STEREOS);
	}

	@Override
	protected String getParameterNotes(Map<String, String> inData) {
		return inData.get(EA.PAR_NOTE);
	}

	@Override
	protected String getParameterType(Map<String, String> inData) {
		return inData.get(EA.PAR_TYPE);
	}

	@Override
	protected String getParameterClassifierID(Map<String, String> inData) {
		return Util.parseIntZero(inData.get(EA.PAR_CLASSIF)).toString();
	}

	@Override
	protected int getParameterPosition(Map<String, String> inData) {
		return Util.parseIntZero(inData.get(EA.PAR_POS)).intValue();
	}
}
