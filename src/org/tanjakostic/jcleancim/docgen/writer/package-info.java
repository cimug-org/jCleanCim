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
 * Classes and interfaces responsible for document generation out of the UML model.
 * <p>
 * Interfaces are currently implemented for MS Word document generation only; we are working on
 * implementing serialisation in an XML format for documentation. Implementations are residing in
 * subpackages <code>word</code> and <code>xml</code>.
 * <p>
 * Main classes and interfaces are:
 * <ul>
 * <li>{@link org.tanjakostic.jcleancim.docgen.writer.Writer} - interface to implement in order to
 * write the collected documentation.
 * <li>{@link org.tanjakostic.jcleancim.docgen.writer.WriterInput} - arguments to initialise any
 * writer.
 * <li>{@link org.tanjakostic.jcleancim.docgen.writer.Placeholder} - contains points in template
 * document that need to be replaced with the actual documentation content, or errors for invalid
 * formats or inexistant model elements.
 * </ul>
 * <p>
 * <b>TODO:</b>
 * <ul>
 * <li>Add warning for those placeholders that should be specified in a heading in case they are
 * found in text.
 * </ul>
 */
package org.tanjakostic.jcleancim.docgen.writer;