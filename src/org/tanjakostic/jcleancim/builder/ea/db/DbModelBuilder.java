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

package org.tanjakostic.jcleancim.builder.ea.db;

import java.util.List;
import java.util.Map;

import org.tanjakostic.jcleancim.builder.DiagramExporter;
import org.tanjakostic.jcleancim.builder.EmptyDiagramExporter;
import org.tanjakostic.jcleancim.builder.EmptyXMIExporter;
import org.tanjakostic.jcleancim.builder.XMIExporter;
import org.tanjakostic.jcleancim.builder.ea.EA;
import org.tanjakostic.jcleancim.builder.ea.EaHelper;
import org.tanjakostic.jcleancim.builder.ea.EaModelBuilder;
import org.tanjakostic.jcleancim.builder.ea.EaSelector;
import org.tanjakostic.jcleancim.builder.ea.EaTables;
import org.tanjakostic.jcleancim.builder.ea.PackageBuilder;
import org.tanjakostic.jcleancim.common.Config;
import org.tanjakostic.jcleancim.util.ApplicationException;
import org.tanjakostic.jcleancim.util.Util;

/**
 * The fastest builder of our in-memory model from EA. It is the new implementation based on
 * Jackcess library that allows reading MS Access file in an OS-independent way, and independently
 * of EA API.
 * <p>
 * <b>Limitation:</b> Note that with this implementation we don't have access to the EA repository
 * (API) methods, so we cannot export diagrams or XMI - although we do provide "empty" exporters, so
 * that this implementation can hook into the existing framework.
 * <p>
 * This implementation should be used for very fast {edit UML - validate} cycles. When you need to
 * produce a UML release (with XMI) and/or generate any kind of documentation with diagrams, ensure
 * you swap this implementation with the one that can export XMI and diagrams.
 *
 * @author tatjana.kostic@ieee.org
 * @version $Id: DbModelBuilder.java 21 2019-08-12 15:44:50Z dev978 $
 */
public class DbModelBuilder extends EaModelBuilder<Map<String, String>, EaModelBuilder<?, ?>> {

	private DbRepo _repo;
	private EaTables _tables;

	/**
	 * Constructor.
	 *
	 * @param cfg
	 */
	public DbModelBuilder(Config cfg) {
		super(cfg);
	}

	@Override
	protected String initRepoAndGetVersion(String modelFileAbsPath) {
		_repo = DbRepo.create(modelFileAbsPath);
		return _repo.getVersion();
	}

	@Override
	protected void openRepo(String modelFileAbsPath) throws ApplicationException {
		_repo.open();
	}

	@Override
	protected void closeRepo() throws ApplicationException {
		_repo.close();
	}

	@Override
	protected void bulkLoad() throws ApplicationException {
		EaSelector selector = new DbSelector(_repo);
		_tables = new EaTables(selector, getCfg().isAppSkipTiming());
	}

	// ---------------

	@Override
	protected Map<String, String> getFirstRoot() throws ApplicationException {
		List<Map<String, String>> rootsPckFields = _tables.findPackageSubpackages(Util.ZERO, "");
		assertModelNotEmptyWarnIfMultipleRoots(rootsPckFields.size());
		return rootsPckFields.get(0);
	}

	@Override
	protected List<Map<String, String>> getModels(Map<String, String> rootPckRow) {
		Integer rootId = Util.parseInt(rootPckRow.get(EA.PACKAGE_ID));
		String rootName = rootPckRow.get(EA.PACKAGE_NAME);
		return _tables.findPackageSubpackages(rootId, rootName);
	}

	@Override
	protected String getLogSubtitleStartPopulateBuilders() {
		return "building model from EA tables (as " + _repo.getDbType() + " DB)...";
	}

	@Override
	protected String getLogSubtitleEndPopulateBuilders() {
		return "built model from EA tables (as " + _repo.getDbType() + " DB)";
	}

	@Override
	protected PackageBuilder<?, ?, ?, ?, ?, ?> createModelPackage(Map<String, String> inData) {
		return DbPackageBuilder.createModelPackageBuilder(inData, this, new EaHelper());
	}

	@Override
	public String findElementType(Integer id) {
		return _tables.findElementType(id);
	}

	@Override
	public String findElementTypeAndName(Integer id) {
		return _tables.findElementTypeAndName(id);
	}

	@Override
	protected String fetchPackageGuid(Map<String, String> inData) {
		return inData.get(EA.EA_GUID);
	}

	// ---------------

	@Override
	protected final DiagramExporter createDiagramExporter() {
		return new EmptyDiagramExporter(getCfg());
	}

	@Override
	protected final XMIExporter createXMIExporter() {
		return new EmptyXMIExporter(getCfg());
	}

	// ------------------------

	@Override
	public EaTables getTables() {
		return _tables;
	}
}
