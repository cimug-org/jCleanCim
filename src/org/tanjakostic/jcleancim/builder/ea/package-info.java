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
 * Classes responsible for building in-memory UML model from EA repository (.eap model file).
 * <p>
 * The classes in this package have been factored out of the initial, simpler but less flexible
 * implementation (in which these classes were initialising themselves from the EA repository and
 * were used further by application for everything). These builder classes now are the only ones
 * that "talk" to the EA repository, through a terribly slow EA API or, since 01v07 through bulk SQL
 * queries, and they cache all the data we are interested in for a UML model. After they fetch all
 * the data and diagrams from the EA repository, and potentially export diagrams for document
 * generation, or export XMI, they create (or "build") a simple in-memory UML model that the
 * application then uses for everything else. From that moment on, the application is totally
 * independent of the EA repository, as it works with the in-memory UML model.
 * <p>
 * Important classes and interfaces are:
 * <ul>
 * <li>{@link org.tanjakostic.jcleancim.builder.ModelBuilder} - interface implemented by model builders
 * from various model sources.
 * <li>{@link org.tanjakostic.jcleancim.builder.ea.EaHelper} - interface defining methods that rely on
 * EA repository or project objects, such as copying diagrams to system clipboard or saving them to
 * files, or the formatted documentation of the UML elements in EA repository.
 * <li>All the other *Builder classes.
 * </ul>
 */
package org.tanjakostic.jcleancim.builder.ea;