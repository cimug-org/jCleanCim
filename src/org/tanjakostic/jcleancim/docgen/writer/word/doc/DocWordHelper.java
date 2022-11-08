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

package org.tanjakostic.jcleancim.docgen.writer.word.doc;

import java.util.LinkedHashMap;
import java.util.Map;

import org.tanjakostic.jcleancim.util.ProgrammerErrorException;

import com.jacob.com.Dispatch;
import com.jacob.com.Variant;

/**
 * @author tatjana.kostic@ieee.org
 * @version $Id: DocWordHelper.java 26 2019-11-12 18:50:35Z dev978 $
 */
class DocWordHelper {

	private DocWordHelper() {
		// prevents construction
	}

	// --------- variants ----------------

	// these are purely for performance purposes
	static final Variant VARIANT_EMPTY_STRING = new Variant("");
	static final Variant VARIANT_FALSE = new Variant(false);
	static final Variant VARIANT_TRUE = new Variant(true);
	static final int VARIANT_INTS_COUNT = 200;
	static final Variant[] VARIANT_INTS = new Variant[VARIANT_INTS_COUNT];
	static final Map<String, Variant> VARIANT_STRINGS = new LinkedHashMap<>();

	static {
		for (int i = 0; i < 200; ++i) {
			VARIANT_INTS[i] = new Variant(i);
		}
	}

	static final Variant getVariant(boolean arg) {
		return arg ? VARIANT_TRUE : VARIANT_FALSE;
	}

	static final Variant wdUndefined = new Variant(9999999);
	static final Variant VARIANT_INT_TRUE = new Variant(-1);
	static final Variant VARIANT_INT_FALSE = new Variant(0);

	static final Variant getVariantIntForBool(boolean b) {
		return b ? VARIANT_INT_TRUE : VARIANT_INT_FALSE;
	}

	/** Returns false if <code>b</code> is 0; true if it is -1 or 9999999; throws exc. otherwise. */
	static final boolean getBoolForVariantInt(int b) {
		if (b == VARIANT_INT_FALSE.getInt()) {
			return false;
		}
		if (b == VARIANT_INT_TRUE.getInt() || b == wdUndefined.getInt()) {
			return true;
		}
		throw new ProgrammerErrorException("Invalid range for argument " + b);
	}

	static Variant vInt(int number) {
		if (number < 0 || number >= VARIANT_INTS_COUNT) {
			return new Variant(number);
		}
		return VARIANT_INTS[number];
	}

	static Variant vString(String txt) {
		if (VARIANT_STRINGS.get(txt) == null) {
			VARIANT_STRINGS.put(txt, new Variant(txt));
		}
		return VARIANT_STRINGS.get(txt);
	}

	// --------- collections -------------

	static int getCount(Dispatch collection) {
		return Dispatch.call(collection, "Count").getInt();
	}

	static Dispatch getItem(Dispatch collection, int i) {
		return Dispatch.call(collection, "Item", vInt(i)).toDispatch();
	}
}
