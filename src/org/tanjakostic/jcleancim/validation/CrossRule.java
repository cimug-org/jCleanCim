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

package org.tanjakostic.jcleancim.validation;

import java.util.Collection;
import java.util.List;

import org.tanjakostic.jcleancim.model.UmlObject;

/**
 * Rule that applies to a collection of {@link UmlObject}-s against a collection of
 * {@link UmlObject}-s potentially of different type.
 *
 * @param <T>
 *            rule applies to collection of these {@link UmlObject}-s
 * @author tatjana.kostic@ieee.org
 * @version $Id: CrossRule.java 21 2019-08-12 15:44:50Z dev978 $
 */
public interface CrossRule<T extends UmlObject> extends Rule {

	/**
	 * Applies the validation criteria to a <b>non-empty</b> list of <code>objs</code>, and creates
	 * problems for invalid ones and adds them to <code>toCollect</code>.
	 */
	public void validate(List<T> objs, ModelIssues toCollect);

	/** Returns list of objects against which {@link #validate(List, ModelIssues)} works. */
	public Collection<? extends UmlObject> getObjsToTestAgainst();
}
