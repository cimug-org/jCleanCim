/*
 * Copyright (C) 2009-2019 Tatjana (Tanja) Kostic <p> This file belongs to jCleanCim, a tool
 * supporting tasks of UML model managers for IEC TC57 CIM and 61850 models. <p> This program is
 * free software: you can redistribute it and/or modify it under the terms of the GNU Lesser General
 * Public License version 3. <p> This program is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A
 * PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details. You should have
 * received a copy of the GNU Lesser General Public License along with this program. If not, see
 * <http://www.gnu.org/licenses/>.
 */

/**
 * Classes being created by a builder or with the API (code) to hold the in-memory UML model.
 * <p>
 * Note that the EA API is terribly slow, and that is why we do heavy caching of everything that we
 * read from the EA file. Afterwards, except for updating and pasting diagrams to clipboard (for doc
 * generation), we are completely detached from EA and work with these classes in-memory.
 * <p>
 * Important classes and interfaces are:
 * <ul>
 * <li>{@link org.tanjakostic.jcleancim.model.UmlObject} - interface defining methods all UML elements
 * in our model should implement.
 * <li>{@link org.tanjakostic.jcleancim.model.UmlKind} - interface implemented by various *Kind
 * enumerations, to allow for consistent displaying of kind/category/type information. Note that we
 * could have designed and implemented subclasses of basic UML elements to reflect the
 * categorisation, but it would have lead to proliferation of classes here for not a big deal of
 * required functionality. Also, the resulting API would have likely been more complex to use, so we
 * just sticked to this simple solution for the moment.
 * <li>{@link org.tanjakostic.jcleancim.model.AbstractUmlObject} - abstract class implementing some of
 * the methods of {@link org.tanjakostic.jcleancim.model.UmlObject}, and from which most of UML
 * elements in our model inherit. It also provides a couple of utility static methods that handle
 * collections of {@link org.tanjakostic.jcleancim.model.UmlObject}.
 * <li>{@link org.tanjakostic.jcleancim.model.UmlObjectData} - value object holding attributes common
 * to all {@link org.tanjakostic.jcleancim.model.UmlObject}-s, used as instance variable in
 * {@link org.tanjakostic.jcleancim.model.AbstractUmlObject}. This makes it easier to populate the
 * instances on creation, by avoiding a big number of parameters to constructors of concrete
 * {@link org.tanjakostic.jcleancim.model.UmlObject}-s.
 * <li>{@link org.tanjakostic.jcleancim.model.UmlModel} - class that holds the configuration
 * {@link org.tanjakostic.jcleancim.common.Config} and all the concrete elements of the model. An
 * instance of {@link org.tanjakostic.jcleancim.model.UmlModel} can be populated by a builder or simply
 * through the API (with the explicit code, like in tests). Elements of the UML model are arranged
 * in hierarchies (package, subpackage...) starting from model packages (
 * {@link org.tanjakostic.jcleancim.model.UmlPackage.Kind#MODEL}). This class also internaly caches the
 * major UML elements in hash maps (per UUID as string), to allow for fast searches without using
 * <code>instanceof</code> operator.
 * <li>various Uml* classes, most of them inheriting from
 * {@link org.tanjakostic.jcleancim.model.AbstractUmlObject} and implementing
 * {@link org.tanjakostic.jcleancim.model.UmlObject}.
 * <li>{@link org.tanjakostic.jcleancim.model.VersionInfo} - version information, as read from version
 * classes expected to be found in top packages.
 * </ul>
 * <p>
 * <b>TODO:</b>
 * <ul>
 * <li>
 * </ul>
 */
package org.tanjakostic.jcleancim.model;