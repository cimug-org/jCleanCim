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
 * Classes responsible for tracking and reporting model statistics.
 * <p>
 * Main classes are:
 * <ul>
 * <li>{@link org.tanjakostic.jcleancim.statistics.ModelStats} - restricts the scope as given in
 * properties and calculates statistics (counts) of different kinds of elements in the model.
 * <li>{@link org.tanjakostic.jcleancim.statistics.CrossPackageStats} - collects and logs actual
 * dependencies on two levels: among top level packages (i.e. between different package owners), and
 * among packages within the same top level package (i.e., within the same owner).
 * </ul>
 * <p>
 * <b>TODO:</b>
 * <ul>
 * <li>Design of {@link org.tanjakostic.jcleancim.statistics.CrossPackageStats} is currently very
 * quick&dirty, but at least allows to log dependencies in general. Please, propose how would you
 * like to better structure the output, or how to make it shorter and easier to scan.
 * <li>Do we need to save this in some structured format? Which?
 * </ul>
 */
package org.tanjakostic.jcleancim.statistics;