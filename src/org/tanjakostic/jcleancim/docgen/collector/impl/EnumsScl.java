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

package org.tanjakostic.jcleancim.docgen.collector.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.apache.log4j.Logger;
import org.tanjakostic.jcleancim.docgen.collector.ClassScl;
import org.tanjakostic.jcleancim.docgen.collector.PackageScl;
import org.tanjakostic.jcleancim.model.UmlClass;

/**
 * Data required for SCL enumerations.
 * <p>
 * Here the layout you may use for
 * {@link org.tanjakostic.jcleancim.docgen.collector.PlaceholderSpec.Kind#SCL_ENUMS}:
 *
 * <pre>
 * this.toString()
 * </pre>
 *
 * TODO: This is quick-and-dirty implementation; if we once generate more of SCL from the UML,
 * ensure you don't forget to implement this there.
 *
 * @author tatjana.kostic@ieee.org
 * @version $Id: EnumsScl.java 21 2019-08-12 15:44:50Z dev978 $
 */
class EnumsScl implements PackageScl {
	private static final Logger _logger = Logger.getLogger(EnumsScl.class.getName());

	public static boolean DETAILED_HEADING = true;

	private final String _headingText;
	private final List<ClassScl> _entryDocs = new ArrayList<ClassScl>();

	/**
	 * Constructor.
	 */
	EnumsScl(Collection<UmlClass> classes, String packageName) {
		_logger.trace("---- collecting XML doc for enums in " + packageName + " ...");

		_headingText = DETAILED_HEADING ? String.format(SCL_ENUM_HEADING_FORMAT_WITH_PCK_NAME,
				packageName) : SCL_ENUM_HEADING_DEFAULT;

		for (UmlClass c : classes) {
			if (!c.isEnumeratedType()) {
				_logger.warn("Class " + c.getName() + " is not an enum - skipping.");
				continue;
			}
			_entryDocs.add(new EnumClassScl(c));
		}
	}

	// ===== Impl. of org.tanjakostic.jcleancim.docgen.collector.ContainerXml methods =====

	@Override
	public boolean notEmpty() {
		return _entryDocs.size() > 0;
	}

	@Override
	public String getHeadingText() {
		return _headingText;
	}

	@Override
	public List<ClassScl> getClassScls() {
		return Collections.unmodifiableList(_entryDocs);
	}

	@Override
	public String toXml(boolean prettyPrint) {
		StringBuilder sb = new StringBuilder();
		for (ClassScl xml : getClassScls()) {
			sb.append(xml.toString());
		}
		return sb.toString();
	}

	@Override
	public String toString() {
		return toXml(false);
	}
}
