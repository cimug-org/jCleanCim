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
 * Classes specific to writing MS documents by means of automation API through Java-COM bridge (<a
 * href="http://sourceforge.net/projects/jacob-project/files/">Jacob</a>); this implementation is
 * extremely slow (it requires MS Word application and its COM API is just slow), but it supports
 * both .doc and .docx MS Word formats.
 * <p>
 * All classes are implementations of interfaces from
 * {@link org.tanjakostic.jcleancim.docgen.writer}.
 */
package org.tanjakostic.jcleancim.docgen.writer.word.doc;