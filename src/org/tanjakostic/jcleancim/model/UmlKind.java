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

package org.tanjakostic.jcleancim.model;

/**
 * Interface intended to be implemented by various *Kind enumerations, to allow for uniform
 * processing for any {@link UmlObject}.
 * <p>
 * In some instances, we have to store predefined names (like EA stereotypes), so we define custom
 * constructors that take that value for an additional field. In other instances, we cannot define
 * an enumeration literal with the name of java reserved words (like interface, package or class),
 * but would like to print things a bit differently. For some UML objects, we also need to print
 * machine-processable tokens as XML tags. The methods below allow for customised strings per
 * enumeration literal for different purposes.
 * <p>
 * Use the toString() method to obtain the name of enumeration literal itself.
 *
 * @author tatjana.kostic@ieee.org
 * @version $Id: UmlKind.java 21 2019-08-12 15:44:50Z dev978 $
 */
public interface UmlKind {

	/**
	 * Returns the value set by the custom constructor. E.g., for ENUM("enumaration", "enumaration"
	 * "Enumeration"), returns first argument "enumaration".
	 */
	public String getValue();

	/**
	 * Returns the label to be used for human-readable documentation. E.g., ENUM("enumaration",
	 * "enumaration" "Enumeration"), returns second argument "enumaration".
	 */
	public String getLabel();

	/**
	 * Returns the tag to be used for machine-processable documentation, typically used for
	 * statistics printing. E.g., ENUM("enumaration", "enumaration" "Enumeration") returns third
	 * argument "Enumeration".
	 */
	public String getTag();

	/**
	 * Returns the description, typically used for statistics printing. PRIM("Primitive",
	 * "primitive" "primitive class"), returns "primitive class".
	 */
	public String getDesc();
}
