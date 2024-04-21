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

package org.tanjakostic.jcleancim.builder.ea;

import java.util.List;

import org.tanjakostic.jcleancim.model.TextDescription;
import org.tanjakostic.jcleancim.model.TextDescription.TextKind;
import org.tanjakostic.jcleancim.util.Util;

/**
 * @author tatjana.kostic@ieee.org
 * @version $Id: EaHelper.java 31 2019-12-08 01:19:54Z dev978 $
 */
public class EaHelper {

	private final EaNotesCleaner _cleaner = new EaNotesCleaner();

	public TextDescription getRawText(String text) {
		List<String> lines = _cleaner.cleanAndCompactText(text);

		TextKind kind = (lines.size() > 1) ? TextKind.textWithNL : TextKind.textNoNL;
		String result = Util.concatCharSeparatedTokens(Util.NL, lines);

		return new TextDescription(result, kind);
	}

	public TextDescription getHtmlText(String text) {
		String result = _cleaner.cleanAndCompactHtml(text);
		return new TextDescription(result, TextKind.htmlSnippet);
	}
}
