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

package org.tanjakostic.jcleancim.builder.ea;

import org.apache.log4j.Level;
import org.tanjakostic.jcleancim.builder.UmlObjectBuilder;
import org.tanjakostic.jcleancim.model.UmlModel;
import org.tanjakostic.jcleancim.model.UmlObject;

/**
 * @author tatjana.kostic@ieee.org
 * @version $Id: AbstractObjectBuilderFromEA.java 2238 2012-08-25 20:46:33Z tatjana.kostic@ieee.org
 *          $
 */
abstract class AbstractObjectBuilderFromEA<T extends UmlObject> implements UmlObjectBuilder<T> {

	protected final Level CTOR_LOG_LEVEL = Level.TRACE;

	private T _result;

	/**
	 * {@inheritDoc}
	 * <p>
	 * This default implementatation ; package builder should override it by throwing exception.
	 */
	@Override
	public T build() {
		if (getResult() == null) {
			doBuild();
		}
		return getResult();
	}

	/**
	 *
	 */
	abstract protected void doBuild();

	/**
	 * {@inheritDoc}
	 * <p>
	 * This default implementatation throws only exception; package builder should override it.
	 */
	@Override
	public T build(UmlModel model) throws UnsupportedOperationException {
		throw new UnsupportedOperationException();
	}

	/**
	 * This default implementatation throws only exception; package builder should override it.
	 *
	 * @param model
	 *            not used here.
	 */
	protected void doBuild(UmlModel model) {
		throw new UnsupportedOperationException();
	}

	public final T getResult() {
		return _result;
	}

	protected final void setResult(T result) {
		_result = result;
	}
}
