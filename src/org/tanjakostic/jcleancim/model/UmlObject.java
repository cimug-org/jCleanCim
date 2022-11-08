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

import java.util.Map;
import java.util.Set;

import org.tanjakostic.jcleancim.common.Nature;
import org.tanjakostic.jcleancim.common.OwningWg;

/**
 * Data common to all UML objects.
 * <p>
 * <b>Methods never return a null value</b>, but rather empty string or empty collection.
 *
 * @author tatjana.kostic@ieee.org
 * @version $Id: UmlObject.java 34 2019-12-20 18:37:17Z dev978 $
 */
public interface UmlObject {

	/**
	 * Returns the local ID of this UML object.
	 * <p>
	 * In case of EA, this is an integer, assigned per EA type of tables (i.e., it's a counter) that
	 * cannot be changed and is a real persistent identifier for the given scope, so the application
	 * should be using this number to ensure uniqueness. However, some EA objects do not have this
	 * identifier at all, and the application is free to assign any number, assuming that that kind
	 * of UML object will not be cached on its own (but rather accessed from its container).
	 */
	public Integer getId();

	/**
	 * Returns the universally unique ID of this UML object.
	 * <p>
	 * In case of EA, this is the UUID used for manipulating diagram objects (in contrast to
	 * everythin else) and when manipulating XMI, but it can be stripped off on model import/export
	 * so it cannot be considered as persistent. In case there is not UUID assigned within the
	 * model, the application should assign a valid UUID.
	 */
	public String getUuid();

	/**
	 * Returns the version of the model in which this UML object has been introduced, as defined in
	 * the UML model.
	 */
	public String getSince();

	/**
	 * Returns the IEC working group owning this UML object, as calculated by the application based
	 * on the UML model structure.
	 */
	public OwningWg getOwner();

	/**
	 * Returns the namespace of this UML object, based on tagged values in the UML model and when
	 * missing, calculated by the application.
	 */
	public Namespace getNamespace();

	/**
	 * Returns the nature of this UML object, which determines the validation rules to apply, and
	 * sometimes document generation formats.
	 */
	public Nature getNature();

	/**
	 * Returns whether this UML object is informative (and thus should be ignored when generating
	 * official IEC documents).
	 */
	public boolean isInformative();

	/**
	 * Returns visibility of this UML object, as defined in the UML model.
	 */
	public UmlVisibility getVisibility();

	/**
	 * Returns kind of this UML object, as assigned by the application.
	 */
	public UmlKind getKind();

	/**
	 * Returns the name of this UML object, as defined in the UML model.
	 */
	public String getName();

	/**
	 * Returns the alias of this UML object, as defined in the UML model, empty string if not
	 * defined. Typically used for a "pretty print" name of an UML object, as required for
	 * documentation generation.
	 */
	public String getAlias();

	/**
	 * Returns the name of this UML object combined with some container-related information (e.g.,
	 * packageName.className, or containingPackageName.packageName). This is meant to be used for
	 * displaying purposes, to facilitate locating UML objects.
	 */
	public String getQualifiedName();

	/**
	 * Returns the context as string, for logging purposes:
	 *
	 * <pre>
	 * owner nature [inf] [visibility] [qualifier] kind [stereotype] [q]name;
	 * </pre>
	 *
	 * @param includeId
	 *            whether to print ID
	 * @param isNameQualified
	 *            whether to print qualified name
	 */
	public String toShortString(boolean includeId, boolean isNameQualified);

	/**
	 * Returns the raw text description for this UML object, as defined in the UML model. For
	 * formatted description, use {@link #getHtmlDescription()}.
	 */
	public TextDescription getDescription();

	/**
	 * Returns the formatted description for this UML object, as defined in the UML model. For raw
	 * text description, use {@link #getDescription()}.
	 */
	public TextDescription getHtmlDescription();

	/**
	 * Returns the stereotype of this UML object, as defined in the UML model.
	 */
	public UmlStereotype getStereotype();

	/**
	 * Returns whether this UML object is deprecated; this may be defined directly on this object
	 * with the stereotype {@link UmlStereotype#DEPRECATED}, or derived (for instance, for
	 * association ends of an association).
	 */
	public boolean isDeprecated();

	/**
	 * Returns allowed tag names, as expected to be found in the UML model.
	 */
	public Set<String> getPredefinedTagNames();

	/**
	 * Returns actual tag names defined for this object, but not found in
	 * {@link #getPredefinedTagNames()}.
	 */
	public Set<String> getUnallowedTagNames();

	/**
	 * Adds the UML tagged value (<code>name</code>, <code>value</code> pair) to this UML object, as
	 * defined in the UML model.
	 *
	 * @param name
	 *            tag name.
	 * @param value
	 *            tag value.
	 * @return null if the <code>name</code> is a new tag, otherwise old value for <code>name</code>
	 *         thas has been overwritten with <code>value</code>.
	 * @throws InvalidTagException
	 *             if either <code>name</code> or <code>value</code> is invalid.
	 */
	public String addTaggedValue(String name, String value) throws InvalidTagException;

	/**
	 * Returns all the tagged values of this UML object, as defined in the UML model.
	 */
	public Map<String, String> getTaggedValues();
}
