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

package org.tanjakostic.jcleancim.docgen.collector;

import org.tanjakostic.jcleancim.model.UmlModel;

/**
 * Collects documentation content for the model packages available in {@link UmlModel}, according to
 * configuration, without generating any document. Results are available with
 * {@link #getFreeFormDocumentation()} and {@link #getFixedFormDocumentation()}.
 *
 * @author tatjana.kostic@ieee.org
 * @version $Id: DocCollector.java 21 2019-08-12 15:44:50Z dev978 $
 */
public interface DocCollector {

	/**
	 * Collects recursively documentation from UML model packages, their sub-packages, etc.
	 *
	 * @param model
	 *            UML model.
	 * @throws UnsupportedOperationException
	 *             if this instance has not been created with an uderlying UML model.
	 */
	public void collect(UmlModel model);

	/**
	 * Creates if not yet called and then returns documentation per package, indexed by package name
	 * for easy reference; if there were any two packages with the same name, retains only the first
	 * one collected. This presentation is convenient for free selection of packages, in any order,
	 * by any model nature.
	 */
	public FreeFormDocumentation getFreeFormDocumentation();

	/**
	 * Creates if not yet called and then returns documentation per package, indexed by nature. This
	 * presentation is convenient for nature-dependent selection of packages.
	 */
	public FixedFormDocumentation getFixedFormDocumentation();

	// ------------ these three are properly said collectors --------

	/**
	 * Must be called for every newly created {@link PackageDoc} that need not be skipped.
	 *
	 * @param packageDoc
	 *            package documentation to retain.
	 */
	public void addToFlattened(PackageDoc packageDoc);

	/**
	 * Must be called for every newly created {@link ClassDoc} that need not be skipped.
	 *
	 * @param classDoc
	 *            class documentation to retain.
	 */
	public void addToFlattened(ClassDoc classDoc);

	/**
	 * Must be called by every newly created {@link PackageDoc} that need not be skipped, if
	 * <code>owner</code> is in scope and it needs to be included in the name space: adds the
	 * <code>packageDoc</code> under appropriate nature and name space, no-op otherwise.
	 *
	 * @param packageDoc
	 */
	public void addToScoped(PackageDoc packageDoc);

	/**
	 * Must be called by every newly created {@link PackageDoc} that is to be skipped. Returns
	 * whether <code>qName</code> has been added to the collection of skipped informative package
	 * names. Because it is intended to be used for logging only, we don't need objects (strings are
	 * enough).
	 *
	 * @param qName
	 *            qualified name of the package.
	 */
	public boolean addSkippedInformativePackage(String qName);

	/** Returns bookmark registry populated from the UML model. */
	public BookmarkRegistry getBmRegistry();

	// ---------------- these are not (yet) used externally, meant for testing -------

	/**
	 * Returns configuration according to which the documentation is collected for generation.
	 */
	public DocgenConfig getDocgenCfg();

	/**
	 * Returns whether this collector has been created from a UML model (as opposed to pure API
	 * calls).
	 */
	public boolean isFromUml();
}
