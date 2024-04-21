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
 * FIXME: This is experimental and absolutely not tested - don't use!
 * <p>
 * The package contains the {@link org.tanjakostic.jcleancim.experimental.builder.rdfs.RdfsParser},
 * which can parse the CIM profiles in {@value org.tanjakostic.jcleancim.common.Config#XSD_EXT} format,
 * as generated with CIMTool.
 * <p>
 * The result of parsing is contained in
 * {@link org.tanjakostic.jcleancim.experimental.builder.rdfs.RdfsModel} and its related classes.
 * <p>
 * <i>Implementation note</i>: An option was to use jena libraries, but it is pretty big with all of
 * its dependencies. We therefore opted to adapt an old parser that was parsing RDFS generated with
 * XPetal from Rose long time ago.
 *
 * @author tatjana.kostic@ieee.org
 * @version $Id: package-info.java 21 2019-08-12 15:44:50Z dev978 $
 */
package org.tanjakostic.jcleancim.experimental.builder.rdfs;

