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

import java.io.File;
import java.io.IOException;

import org.tanjakostic.jcleancim.common.Config;
import org.tanjakostic.jcleancim.util.Util.ImageFormat;

// import org.tanjakostic.jcleancim.builder.ea.DiagramBuilder;

/**
 * Common default implementation, as for "empty exporter.
 *
 * @author tatjana.kostic@ieee.org
 * @version $Id: AbstractDiagramExporter.java 21 2019-08-12 15:44:50Z dev978 $
 */
abstract public class AbstractDiagramExporter implements DiagramExporter {

	private final Config _cfg;

	/** Constructor. */
	public AbstractDiagramExporter(Config cfg) {
		_cfg = cfg;
	}

	@Override
	public final Config getCfg() {
		return _cfg;
	}

	/**
	 * {@inheritDoc}
	 * <p>
	 * This default implementation always returns null, without actually exporting anything; ensure
	 * to override if you can export diagrams.
	 */
	@SuppressWarnings("unused")
	@Override
	public File saveToFile(UmlObjectBuilder<?> dia, ImageFormat format, boolean throughClipboard)
			throws IOException {
		return null;
	}
}
