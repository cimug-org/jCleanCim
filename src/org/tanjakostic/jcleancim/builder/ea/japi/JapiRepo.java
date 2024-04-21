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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.sparx.Collection;
import org.sparx.Element;
import org.sparx.Package;
import org.sparx.Repository;
import org.tanjakostic.jcleancim.builder.DiagramExporter;
import org.tanjakostic.jcleancim.builder.XMIExporter;
import org.tanjakostic.jcleancim.builder.ea.EaSql2Xml;
import org.tanjakostic.jcleancim.common.Config;
import org.tanjakostic.jcleancim.util.ApplicationException;

/**
 * @author tatjana.kostic@ieee.org
 * @version $Id: JapiRepo.java 21 2019-08-12 15:44:50Z dev978 $
 */
public class JapiRepo implements EaSql2Xml {

	private Repository _eaRepo;

	/** Constructor. */
	public JapiRepo() {
		_eaRepo = new Repository();
		// From the forum: Setting these when appropriate will make a big difference to speed.
		// From performance tests: these have effect only on the execution time of this very
		// method (4s without -> 3.4s with), and no effect at all on iterations or SQLQuery method.
		_eaRepo.SetEnableCache(true);
		_eaRepo.SetBatchAppend(true);
		_eaRepo.SetEnableUIUpdates(false);
	}

	public String getVersion() {
		return Integer.toString(_eaRepo.GetLibraryVersion());
	}

	public void open(String modelFileAbsPath) {
		_eaRepo.OpenFile(modelFileAbsPath);
	}

	public void close() throws ApplicationException {
		try {
			// In EA older than 12: To force Java to release the memory you need to call System.gc()
			// followed by System.runFinalization(). This has been provided as a single call in
			// Repository.Compact() (but this is not the .eap file compact operation one would
			// expect...) In EA 12, this dubous method has been removed from EA Java API!
			System.gc();
			System.runFinalization();
		} catch (Exception e) {
			throw new ApplicationException("Failed to close EA repository.", e);
		} finally {
			_eaRepo.CloseFile();
			_eaRepo.Exit();
			_eaRepo.destroy();
			_eaRepo = null;
		}
	}

	List<Package> getRootPackages() {
		return JapiRepo.eaToJavaList(_eaRepo.GetModels());
	}

	Element getElementForID(int id) {
		return _eaRepo.GetElementByID(id);
	}

	public DiagramExporter getDiagramExporter(Config cfg) {
		return new JapiDiagramExporter(cfg, _eaRepo);
	}

	public XMIExporter getXMIExporter(Config cfg) {
		return new JapiXMIExporter(cfg, _eaRepo);
	}

	// ===== Impl. of org.tanjakostic.jcleancim.builder.ea.EaSql2Xml methods =====

	@Override
	public String sqlResultAsXml(String queryStatement) {
		return _eaRepo.SQLQuery(queryStatement);
	}

	public static <T> List<T> eaToJavaList(Collection<T> eaCollection) {
		List<T> result = new ArrayList<T>();
		int size = eaCollection.GetCount();
		if (size == 0) {
			return Collections.emptyList();
		}

		for (short i = 0; i < size; ++i) {
			T dia = eaCollection.GetAt(i);
			result.add(dia);
		}
		return result;
	}
}
