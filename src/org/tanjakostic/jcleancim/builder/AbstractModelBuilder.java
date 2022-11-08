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

package org.tanjakostic.jcleancim.builder;

import org.tanjakostic.jcleancim.common.Config;
import org.tanjakostic.jcleancim.model.UmlModel;
import org.tanjakostic.jcleancim.util.ApplicationException;

/**
 * @author tatjana.kostic@ieee.org
 * @version $Id: AbstractModelBuilder.java 21 2019-08-12 15:44:50Z dev978 $
 */
abstract public class AbstractModelBuilder implements ModelBuilder {

	private final Config _cfg;
	private DiagramExporter _diagramExporter;
	private XMIExporter _xmiExporter;

	/**
	 * Constructor.
	 */
	protected AbstractModelBuilder(Config cfg) {
		_cfg = cfg;
	}

	@Override
	public final DiagramExporter getDiagramExporter() {
		if (_diagramExporter == null) {
			_diagramExporter = createDiagramExporter();
		}
		return _diagramExporter;
	}

	@Override
	public final XMIExporter getXMIExporter() {
		if (_xmiExporter == null) {
			_xmiExporter = createXMIExporter();
		}
		return _xmiExporter;
	}

	/** Creates exporter of UML diagrams where applicable (otherwise, can be just a stub). */
	abstract protected DiagramExporter createDiagramExporter();

	/** Returns exporter to XMI where applicable (otherwise, can be just a stub). */
	abstract protected XMIExporter createXMIExporter();

	// ===== Impl. of org.tanjakostic.jcleancim.builder.ModelBuilder methods =====

	@Override
	public final Config getCfg() {
		return _cfg;
	}

	@Override
	abstract public UmlModel build() throws ApplicationException;
}
