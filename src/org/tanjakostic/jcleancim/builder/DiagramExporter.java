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

/**
 * @author tatjana.kostic@ieee.org
 * @version $Id: DiagramExporter.java 21 2019-08-12 15:44:50Z dev978 $
 */
public interface DiagramExporter {

	/** Returns the configuration, containing also diagram export options. */
	public Config getCfg();

	/**
	 * Copies diagram <code>dia</code> to a file with <code>format</code>, in the pics output
	 * directory (per configuration) and returns the created file.
	 *
	 * @param dia
	 *            diagram representation
	 * @param format
	 *            image format
	 * @param throughClipboard
	 *            if true, image will be copied to clipboard and then saved as bitmap file (i.e.,
	 *            <code>format</code> will be ignored).
	 * @return created file with the diagram; null if file creation failed.
	 * @throws IOException
	 *             if file creation failed.
	 */
	public File saveToFile(UmlObjectBuilder<?> dia, ImageFormat format, boolean throughClipboard)
			throws IOException;
}
