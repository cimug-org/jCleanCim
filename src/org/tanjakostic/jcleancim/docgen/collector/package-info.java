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
 * This package defines interfaces and classes responsible for collecting documentation and figures
 * from the UML model, or freely initialised through the API without any UML model. The result is
 * the UML model content relevant for writing documentation (independent of the UML model) that is
 * then passed to a {@link org.tanjakostic.jcleancim.docgen.writer.Writer}, to actually output some
 * documentation.
 * <p>
 * Major interfaces and classes are:
 * <ul>
 * <li>{@link org.tanjakostic.jcleancim.docgen.collector.DocCollector} - the collector of
 * documentation.
 * <li>{@link org.tanjakostic.jcleancim.docgen.collector.DocgenConfig} - configuration options specific
 * to document generation (such as what to include/exclude), whether to retain HTML documentation
 * and such.
 * <li>{@link org.tanjakostic.jcleancim.docgen.collector.DocgenConfig} - configuration options specific
 * to document generation (such as what to include/exclude), whether to retain HTML documentation
 * and such.
 * <li>{@link org.tanjakostic.jcleancim.docgen.collector.FreeFormDocumentation} - collected
 * documentation for free format printing, such as when using a template and placeholders, where you
 * can freely choose content to generate).
 * <li>{@link org.tanjakostic.jcleancim.docgen.collector.FixedFormDocumentation} - collected
 * documentation for fixed format printing, such as when printing relevant content for a name space.
 * <li>Interfaces are those that end with "Doc" or "Scl". Other are classes responsible for
 * formatting and configuration data.
 * <li>{@link org.tanjakostic.jcleancim.docgen.collector.ModelFinder} - interface defining thin set of
 * methods to do lookup into the model as required for document generation (allowed us to do
 * document generation tests without actually having the full model loaded and built from EA file).
 * </ul>
 * <p>
 * The implementation of interfaces are all available in the <code>impl</code> sub-package.
 */
package org.tanjakostic.jcleancim.docgen.collector;