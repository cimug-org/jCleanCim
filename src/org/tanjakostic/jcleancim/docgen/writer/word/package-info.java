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
 * Classes specific to writing MS documents.
 * <p>
 * Main classes are:
 * <ul>
 * <li>{@link org.tanjakostic.jcleancim.docgen.writer.word.WordWriter} - interface for writing
 * free-form documentation content by replacing placeholders found in input MS Word file, to produce
 * the output MS Word file.
 * <li>{@link org.tanjakostic.jcleancim.docgen.writer.word.WordHelper} - interface for formatting and
 * inserting text, tables, figures, etc. into a MS Word file.
 * <li>{@link org.tanjakostic.jcleancim.docgen.writer.word.AbstractWordWriter} - implementation of the
 * above two for template methods common to binary COM API and text Open XML API.
 * <li>{@link org.tanjakostic.jcleancim.docgen.writer.word.WordWriterInput} - input arguments common to
 * binary COM API and text Open XML API.
 * </ul>
 */
package org.tanjakostic.jcleancim.docgen.writer.word;