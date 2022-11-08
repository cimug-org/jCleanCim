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
 * Classes commonly used by several packages.
 * <p>
 * Important classes are:
 * <ul>
 * <li>{@link org.tanjakostic.jcleancim.common.Config} - contains information parsed from
 * {@value org.tanjakostic.jcleancim.common.Config#DEFAULT_PROPS_FILE_NAME} file, according to which
 * the whole application will run.</li>
 * <li>{@link org.tanjakostic.jcleancim.common.OwningWg} - contains definition for the ownership of
 * top-level packages by IEC working groups.</li>
 * <li>{@link org.tanjakostic.jcleancim.common.Nature} - contains two values, used to "classify" the
 * nature of the model (packages) and be able to correctly do validation, statistics and document
 * generation.</li>
 * </ul>
 * <p>
 * <b>TODO:</b>
 * <ul>
 * <li>Do we need to have more flexible ownership?
 * </ul>
 */
package org.tanjakostic.jcleancim.common;