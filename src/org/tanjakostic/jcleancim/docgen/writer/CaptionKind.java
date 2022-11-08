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
 * We support 2 kinds of caption labels: for figures and for tables (and ignore those for equations,
 * as we don't print any numbered equations).
 *
 * @author tatjana.kostic@ieee.org
 * @version $Id: Caption.java 21 2019-08-12 15:44:50Z dev978 $
 */
public enum CaptionKind {
	Figure(Style.figcapt), Table(Style.tabcapt);

	private final Style _style;

	CaptionKind(Style style) {
		_style = style;
	}

	public Style getStyle() {
		return _style;
	}

	public boolean looksLikeCaption(String styleName, String text) {
		return text.startsWith(getLabel()) && (getStyle().getUsableStyles().get(styleName) != null);
	}

	public String getLabel() {
		return name();
	}
}
