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

package org.tanjakostic.jcleancim.builder.ea.japi;

import org.sparx.Parameter;
import org.tanjakostic.jcleancim.builder.ea.EaHelper;
import org.tanjakostic.jcleancim.builder.ea.OperationBuilder;
import org.tanjakostic.jcleancim.builder.ea.ParameterBuilder;

/**
 * @author tatjana.kostic@ieee.org
 * @version $Id: JapiParameterBuilder.java 21 2019-08-12 15:44:50Z dev978 $
 */
class JapiParameterBuilder extends ParameterBuilder<Parameter> {

	/**
	 * Constructor.
	 *
	 * @param inData
	 * @param containingOperation
	 * @param eaHelper
	 */
	public JapiParameterBuilder(Parameter inData, OperationBuilder<?, ?> containingOperation,
			EaHelper eaHelper) {
		super(inData, containingOperation, eaHelper);
	}

	@Override
	protected String getParameterGUID(Parameter inData) {
		return inData.GetParameterGUID();
	}

	@Override
	protected String getParameterName(Parameter inData) {
		return inData.GetName();
	}

	@Override
	protected String getParameterAlias(Parameter inData) {
		return inData.GetAlias();
	}

	@Override
	protected String getParameterStereotypes(Parameter inData) {
		return inData.GetStereotypeEx();
	}

	@Override
	protected String getParameterNotes(Parameter inData) {
		return inData.GetNotes();
	}

	@Override
	protected String getParameterType(Parameter inData) {
		return inData.GetType();
	}

	@Override
	protected String getParameterClassifierID(Parameter inData) {
		return inData.GetClassifierID();
	}

	@Override
	protected int getParameterPosition(Parameter inData) {
		return inData.GetPosition();
	}
}
