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

package org.tanjakostic.jcleancim.builder.ea.japi;

import java.util.List;

import org.sparx.Element;
import org.sparx.Package;
import org.tanjakostic.jcleancim.builder.DiagramExporter;
import org.tanjakostic.jcleancim.builder.XMIExporter;
import org.tanjakostic.jcleancim.builder.ea.EaHelper;
import org.tanjakostic.jcleancim.builder.ea.EaModelBuilder;
import org.tanjakostic.jcleancim.builder.ea.EaTables;
import org.tanjakostic.jcleancim.builder.ea.PackageBuilder;
import org.tanjakostic.jcleancim.common.Config;
import org.tanjakostic.jcleancim.util.ApplicationException;

/**
 * The slowest builder of our in-memory model from EA: it uses the very slow EA API and iterates
 * over its collections. It is the refactored version of the original implementation since 01v01.
 * With this implementation we access the EA repository and can thus export diagrams and XMI if
 * required.
 * <p>
 * We intentionally keep this implementation because we hope Sparx will one day provide a fast
 * implementation...
 *
 * @author tatjana.kostic@ieee.org
 * @version $Id: JapiModelBuilder.java 21 2019-08-12 15:44:50Z dev978 $
 */
public class JapiModelBuilder extends EaModelBuilder<Package, Package> {

	private JapiRepo _repo;

	/**
	 * Constructor.
	 *
	 * @param cfg
	 */
	public JapiModelBuilder(Config cfg) {
		super(cfg);
	}

	@Override
	protected String initRepoAndGetVersion(String nodelFileAbsPath) {
		_repo = new JapiRepo();
		return _repo.getVersion();
	}

	@Override
	protected void openRepo(String modelFileAbsPath) {
		_repo.open(modelFileAbsPath);
	}

	@Override
	protected void closeRepo() throws ApplicationException {
		_repo.close();
	}

	@Override
	protected void bulkLoad() {
		// does nothing
	}

	// ---------------

	@Override
	protected Package getFirstRoot() throws ApplicationException {
		List<Package> roots = _repo.getRootPackages();
		assertModelNotEmptyWarnIfMultipleRoots(roots.size());
		return roots.get(0);
	}

	@Override
	protected List<Package> getModels(Package root) {
		return JapiRepo.eaToJavaList(root.GetPackages());
	}

	@Override
	protected String getLogSubtitleStartPopulateBuilders() {
		return "reading model from EA with iteration API (this will take a while)...";
	}

	@Override
	protected String getLogSubtitleEndPopulateBuilders() {
		return "read model from EA with iteration API";
	}

	@Override
	protected PackageBuilder<?, ?, ?, ?, ?, ?> createModelPackage(Package m) {
		return JapiPackageBuilder.createModelPackageBuilder(m, this, new EaHelper());
	}

	@Override
	public String findElementTypeAndName(Integer id) {
		// I don't find other means to fetch type for connector ends for skipped connectors...
		Element el = _repo.getElementForID(id.intValue());
		String type = el.GetType();
		String name = el.GetName();
		return type + " '" + name + "'";
	}

	@Override
	public String findElementType(Integer objId) {
		return _repo.getElementForID(objId.intValue()).GetType();
	}

	@Override
	protected String fetchPackageGuid(Package inData) {
		return inData.GetPackageGUID();
	}

	// ---------------

	@Override
	protected final DiagramExporter createDiagramExporter() {
		return _repo.getDiagramExporter(getCfg());
	}

	@Override
	protected final XMIExporter createXMIExporter() {
		return _repo.getXMIExporter(getCfg());
	}

	// ---------------

	@Override
	public EaTables getTables() throws UnsupportedOperationException {
		throw new UnsupportedOperationException("I'm working with EA API and don't have tables.");
	}
}
