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

package org.tanjakostic.jcleancim.builder;

import org.tanjakostic.jcleancim.model.UmlModel;
import org.tanjakostic.jcleancim.model.UmlObject;
import org.tanjakostic.jcleancim.model.UmlObjectData;

/**
 * To avoid interface bloat, we follow the design pattern of Java collections API: to provide
 * "optional" methods and let implementations select which one they implement.
 * <p>
 * FIXME: doc
 *
 * @author tatjana.kostic@ieee.org
 * @version $Id: UmlObjectBuilder.java 21 2019-08-12 15:44:50Z dev978 $
 */
public interface UmlObjectBuilder<T extends UmlObject> {

	/**
	 * @return FIXME
	 */
	public T build() throws UnsupportedOperationException;

	/**
	 * @param model
	 * @return FIXME
	 */
	public T build(UmlModel model) throws UnsupportedOperationException;

	/**
	 * @return FIXME
	 */
	public UmlObjectData getObjData();
}
