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

/**
 * Holds basic information describing styles, initialised from an open application / document.
 * <p>
 * Implementation note: This is the "buffer" between implementation-specifics (such as Word styles)
 * and the style enumeration convenient for fluent use in the code.
 *
 * @author tatjana.kostic@ieee.org
 * @version $Id$
 */
public class ExistingStyle {

	public enum Kind {
		PARA, FIG, TABHEAD, TABCELL, FIGCAPT, TABCAPT, HEAD, TOC, NORM, CAPT, OTHER
	}

	/** Style name. */
	public final String name;

	/**
	 * String representation of ID describing type. Could be also a number (like in e.g. .doc
	 * document); implementation has to cast to appropriate type used for ID.
	 */
	public final String id;

	/** If true, this is a built-in style. */
	public final boolean isBuiltIn;

	/** Number between 1 and 9, for TOC and Heading styles. */
	public final int outline;

	private Kind _kind;

	/**
	 * Constructor.
	 *
	 * @param name
	 * @param id
	 *            although String, could be also number (like in e.g. .docx document);
	 *            implementation has to cast to appropriate type used for ID
	 * @param isBuiltIn
	 * @param outline
	 *            outline of the numbered style; relevant for TOC and heading styles, ignored for
	 *            others
	 * @param kind
	 *            potentially null; you may want to set this later on, when you can determine the
	 *            kind (such as for custom, i.e., non-built-in styles)
	 */
	public ExistingStyle(String name, String id, boolean isBuiltIn, int outline, Kind kind) {
		this.name = name;
		this.id = id;
		this.isBuiltIn = isBuiltIn;
		this.outline = outline;
		setKind(kind);
	}

	public void setCustomKindFrom(Style s) {
		if (s == Style.para) {
			_kind = Kind.PARA;
		} else if (s == Style.fig) {
			_kind = Kind.FIG;
		} else if (s == Style.tabhead) {
			_kind = Kind.TABHEAD;
		} else if (s == Style.tabcell) {
			_kind = Kind.TABCELL;
		} else if (s == Style.figcapt) {
			_kind = Kind.FIGCAPT;
		} else if (s == Style.tabcapt) {
			_kind = Kind.TABCAPT;
		} else if (s.isTOC()) {
			_kind = Kind.TOC;
		} else if (s.isHeading()) {
			_kind = Kind.HEAD;
		}
	}

	/**
	 * Returns true for exact style (e.g. para) and for default (e.g., norm).
	 */
	public boolean isUsableFor(Style s) {
		if (s == Style.para) {
			return _kind == Kind.PARA || _kind == Kind.NORM;
		} else if (s == Style.fig) {
			return _kind == Kind.FIG || _kind == Kind.NORM;
		} else if (s == Style.tabhead) {
			return _kind == Kind.TABHEAD || _kind == Kind.NORM;
		} else if (s == Style.tabcell) {
			return _kind == Kind.TABCELL || _kind == Kind.NORM;
		} else if (s == Style.figcapt) {
			return _kind == Kind.FIGCAPT || _kind == Kind.CAPT;
		} else if (s == Style.tabcapt) {
			return _kind == Kind.TABCAPT || _kind == Kind.CAPT;
		} else if (_kind == Kind.TOC) {
			return s == Style.getTOCStyle(this.outline);
		} else if (_kind == Kind.HEAD) {
			return s == Style.getHeadingStyle(this.outline);
		} else {
			return false;
		}
	}

	public Kind getKind() {
		return _kind;
	}

	public void setKind(Kind kind) {
		_kind = kind;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		String s = (isBuiltIn) ? "builtIn" : "custom";
		sb.append(name);
		sb.append(" (").append(s).append(",").append(" id=").append(id).append(": ");
		sb.append(_kind).append(")");
		return sb.toString();
	}
}
