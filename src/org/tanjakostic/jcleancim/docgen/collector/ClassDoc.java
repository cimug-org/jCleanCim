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

package org.tanjakostic.jcleancim.docgen.collector;

import java.util.List;

import org.tanjakostic.jcleancim.model.TextDescription;

/**
 * Data required for documentation of classes.
 * <p>
 * Here the layout you may use:
 *
 * <pre>
 * getHeadingText()
 *     getInheritancePath()		// if not empty and if enabled by configuration, you can print
 *     getDescription()			// (includes constraints, if enabled by configuration)
 *     getDiagramDocs()        	// loop and create figures
 *     getAttributesDoc()      	// if getAttributesDoc().notEmpty() then create table
 *     getAssociationEndsDoc() 	// if getAssociationEndsDoc().notEmpty() then create table
 *     getOperationsDoc()      	// if getOperationsDoc().notEmpty() then create table
 * </pre>
 *
 * @author tatjana.kostic@ieee.org
 * @version $Id: ClassDoc.java 34 2019-12-20 18:37:17Z dev978 $
 */
public interface ClassDoc extends ObjectDoc {
	public static final String CLASS_TITLE_PREFIX_FMT_IEC61850 = "<<%s>> ";

	/** (deprecated, abstract, informative, custom), name, (kindLabel) */
	public static final String NO_ALIAS_HEADING_FORMAT = "%s%s%s";

	/** (deprecated, abstract, informative, custom), alias, nbsp, name */
	public static final String LN_HEADING_FORMAT = "%sLN: %s%sName: %s";

	/** (deprecated, abstract, informative, custom), alias, name, kindLabel */
	public static final String PRIM_DA_HEADING_FORMAT = "%s%s (%s %s)";

	/** (deprecated, abstract, informative, custom), alias, name */
	public static final String ANY_DA_OR_CDC_HEADING_FORMAT = "%s%s (%s)";

	/** (deprecated, abstract, informative, custom), alias, name, (stereotype) */
	public static final String OTHER_WITH_ALIAS_HEADING_FORMAT = "%s%s (%s%s)";

	/** Prefix when introducing inheritance path. */
	public static final String INHERITANCE_PATH_PREFIX = "Inheritance path = ";

	/** Separator when printing inheritance path. */
	public static final String INHERITANCE_PATH_SEP = " : ";

	/** At present, used for some 61850 classes only (based on tagged value). */
	public static final String OLDNAME_FMT = "(old name = %s) ";

	/** (IEC61850) Text to append to description if constraints need to be explicitly printed. */
	public static final String CONSTRAINTS_TXT_IEC61850 = "Conditions:";

	/** (CIM) Text to append to description if constraints need to be explicitly printed. */
	public static final String CONSTRAINTS_TXT_CIM = "Constraints:";

	/**
	 * Returns the <i>qualified</i> name of the class, with the separator appropriate for
	 * placeholder. This gives the writer the name of the class present as placeholder in the
	 * template, and to avoid search through the model.
	 */
	public String getClassPlaceholderName();

	/**
	 * Returns all superclasses formatted as inheritance path, suitable to be printed in a single
	 * paragraph. Consider printing only if the returned value is not empty.
	 */
	public TextDescription getInheritancePath();

	/**
	 * Returns the documentation for all the attributes of this class, suitable to be printed as a
	 * table. Use {@link PropertiesDoc#notEmpty()} to see whether there is anything to print.
	 */
	public PropertiesDoc getAttributesDoc();

	/**
	 * Returns the documentation for all the association ends on the 'other' side of this class,
	 * suitable to be printed as a table. Use {@link PropertiesDoc#notEmpty()} to see whether there
	 * is anything to print.
	 */
	public PropertiesDoc getAssocEndsDoc();

	/**
	 * Returns the documentation for all the operations of this class, suitable to be printed as a
	 * table. Use {@link PropertiesDoc#notEmpty()} to see whether there is anything to print.
	 */
	public PropertiesDoc getOperationsDoc();

	/**
	 * Returns the documentation for all the diagrams of this class.
	 */
	public List<FigureDoc> getDiagramDocs();
}
