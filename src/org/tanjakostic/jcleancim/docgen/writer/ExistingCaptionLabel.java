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

package org.tanjakostic.jcleancim.docgen.writer;

import org.tanjakostic.jcleancim.util.ProgrammerErrorException;

/**
 * Holds basic information describing caption labels, initialised from an open application /
 * document.
 *
 * @author tatjana.kostic@ieee.org
 * @version $Id$
 */
public class ExistingCaptionLabel {

	public enum Kind {
		FIG, TAB, OTHER
	}

	/** Label text. */
	public final String name;

	/**
	 * String representation of ID describing type. Could be also a number (like in e.g. .doc
	 * document); implementation has to cast to appropriate type used for ID.
	 */
	public final String id;

	/** If true, this is a built-in caption label. */
	public final boolean isBuiltIn;

	public final Kind kind;

	/**
	 * Constructor.
	 *
	 * @param name
	 * @param id
	 *            although String, could be also number (like in e.g. .docx document);
	 *            implementation has to cast to appropriate type used for ID
	 * @param isBuiltIn
	 * @param kind
	 */
	public ExistingCaptionLabel(String name, String id, boolean isBuiltIn, Kind kind) {
		this.name = name;
		this.id = id;
		this.isBuiltIn = isBuiltIn;
		this.kind = kind;
	}

	public boolean isUsableFor(CaptionKind cKind) {
		if (cKind == CaptionKind.Figure) {
			return kind == Kind.FIG || kind == Kind.OTHER;
		} else if (cKind == CaptionKind.Table) {
			return kind == Kind.TAB || kind == Kind.OTHER;
		}
		throw new ProgrammerErrorException("Did you add new enum for CaptionKind? Then you forgot "
				+ "to add this case here.");
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		String s = (isBuiltIn) ? "builtIn" : "custom";
		sb.append(name);
		sb.append(" (").append(s).append(",");
		sb.append(" id=").append(id).append(": ");
		sb.append(kind).append(")");
		return sb.toString();
	}
}
