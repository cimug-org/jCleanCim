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

import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

import org.tanjakostic.jcleancim.model.TextDescription.TextKind;
import org.tanjakostic.jcleancim.model.UmlPackage.Data;
import org.tanjakostic.jcleancim.util.Util;

/**
 * Simple data structure that allows us to instantiate a subset of data of any {@link UmlObject}
 * that can be initialised simply without any validation logic. This facilitates creation from both
 * real UML model (with builders), and from within the API (for testing without real UML model).
 * <p>
 * TODO: see whether we'll need the attribute since (for IEC 61850).
 * <p>
 * <i>Implementation note:</i> We have considered having this class inherit from
 * {@link AbstractUmlObject} and then have concrete classes (such as, e.g., {@link UmlPackage})
 * inherit form this. However, we have discarded this option because it would force us to do lots of
 * checks in the code to avoid NPEs. We also considered making the data containers (such as
 * {@link Data}) inherit from this one, but then the creation of immutable objects (i.e., using
 * purely ctor params) would become extremely cumbersome. So, we prefer using this type in
 * composition, which is better modular and better testable.
 *
 * @author tatjana.kostic@ieee.org
 * @version $Id: UmlObjectData.java 21 2019-08-12 15:44:50Z dev978 $
 */
public class UmlObjectData {
	private static final AtomicInteger COUNTER = new AtomicInteger();

	/** Helper used to give a unique integer id (to simulate EA local id). */
	private static Integer nextId() {
		return Integer.valueOf(COUNTER.incrementAndGet());
	}

	private final Integer _id;
	private final String _uuid;
	private final String _since;
	private final String _name;
	private final String _alias;
	private final UmlStereotype _stereotype;
	private final UmlVisibility _visibility;
	private final TextDescription _txtDescription;
	private final TextDescription _htmlDescription;

	/**
	 * For testing only: Creates all not provided fields to default/empty/dumb values. For id, it
	 * simply increments a number at every invocation, to emulate EA local id. Note that this
	 * constructor will never be used from the builder that instantiates the model from EA, so there
	 * is no problem for id clashes.
	 */
	UmlObjectData(String name) {
		this(nextId(), null, name, null, null, null, null, null);
	}

	/**
	 * For testing only: Creates all not provided fields to default/empty/dumb values. For id, it
	 * simply increments a number at every invocation, to emulate EA local id. Note that this
	 * constructor will never be used from the builder that instantiates the model from EA, so there
	 * is no problem for id clashes.
	 */
	UmlObjectData(String name, UmlStereotype stereotype) {
		this(nextId(), null, name, null, stereotype, null, null, null);
	}

	/** For testing only: See {@link #UmlObjectData(String, UmlStereotype)}. */
	UmlObjectData(String uuid, String name, UmlStereotype stereotype) {
		this(nextId(), uuid, name, null, stereotype, null, null, null);
	}

	/** Constructor useful when collecting model content for documentation. */
	public UmlObjectData(String name, String alias, TextDescription txtDoc, TextDescription htmlDoc) {
		this(nextId(), null, name, alias, null, null, txtDoc, htmlDoc);
	}

	/**
	 * Constructor; accepts null arguments and initialises them with default values, so that all the
	 * getters return non-null values. In case an argument is a null object, stores empty string
	 * except for <code>eaVisibility</code> (sets it to {@link UmlVisibility#PUBLIC}) and for
	 * <code>uuid</code> (generates random UUID). In such a way, all the getters return non-null
	 * values.
	 *
	 * @param id
	 *            if null, sets it to auto-generated sequence number.
	 * @param uuid
	 *            if null, sets it to an UUID generated from combination of id and name (note that
	 *            this is not guaranteed to produce a unique UUID, but we need repeatable values for
	 *            comparisons in tests and for debugging).
	 * @param name
	 *            if null, sets it to empty string.
	 * @param alias
	 *            if null, sets it to empty string.
	 * @param stereotype
	 *            if null, sets it to empty stereotype.
	 * @param eaVisibility
	 *            if null, sets it to {@link UmlVisibility#PUBLIC}.
	 * @param txtDoc
	 *            if null, sets it to empty string.
	 * @param htmlDoc
	 *            if null, sets it to empty string.
	 */
	public UmlObjectData(Integer id, String uuid, String name, String alias,
			UmlStereotype stereotype, String eaVisibility, TextDescription txtDoc,
			TextDescription htmlDoc) {
		Integer counter = nextId();
		_id = id == null ? counter : id;
		_name = Util.null2empty(name);
		if (uuid != null && !uuid.trim().isEmpty()) {
			_uuid = uuid;
		} else {
			String nameAndId = _name + _id;
			_uuid = UUID.nameUUIDFromBytes(nameAndId.getBytes()).toString();
		}
		_since = null;
		_alias = Util.null2empty(alias);
		_stereotype = stereotype != null ? stereotype : new UmlStereotype();
		_visibility = (eaVisibility != null && !eaVisibility.trim().isEmpty()) ? UmlVisibility
				.valueOf(eaVisibility.toUpperCase()) : UmlVisibility.PUBLIC;
				_txtDescription = txtDoc != null ? txtDoc : new TextDescription();
				_htmlDescription = htmlDoc != null ? htmlDoc
						: new TextDescription("", TextKind.htmlSnippet);
	}

	/** Copy constructor. */
	public UmlObjectData(UmlObject o) {
		this(o.getId(), o.getUuid(), o.getName(), o.getAlias(), o.getStereotype(), o
				.getVisibility().toString(), o.getDescription(), o.getHtmlDescription());
	}

	public Integer getId() {
		return _id;
	}

	public String getUuid() {
		return _uuid;
	}

	public String getSince() {
		return _since;
	}

	public String getName() {
		return _name;
	}

	public String getAlias() {
		return _alias;
	}

	public UmlStereotype getStereotype() {
		return _stereotype;
	}

	public UmlVisibility getVisibility() {
		return _visibility;
	}

	public TextDescription getTxtDescription() {
		return _txtDescription;
	}

	public TextDescription getHtmlDescription() {
		return _htmlDescription;
	}

	@Override
	public String toString() {
		return "UmlObjectData [id=" + getId() + ", uuid=" + getUuid() + ", since=" + getSince()
				+ ", name=" + getName() + ", alias=" + getAlias() + ", stereotype="
				+ getStereotype().value() + ", visibility=" + getVisibility()
				+ ", txtDescription='" + getTxtDescription() + "', htmlDescription='"
				+ getHtmlDescription() + "']";
	}
}
