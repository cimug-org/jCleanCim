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

package org.tanjakostic.jcleancim.docgen.collector;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;
import org.tanjakostic.jcleancim.model.UmlObject;

/**
 * Registry for bookmarks.
 * <p>
 * Use {@link #getOrCreateBookmarkID(UmlObject)} for UmlObjects that you potentially want to be able
 * to refer to from others. At present, we do only classes (they are types and super-types) and
 * enumeration literals (they are potentially used as initial values).
 * <p>
 * Use {@link #markAsAvailableInDocument(String)} in {@link ObjectDoc} subclasses that actually
 * denote places that you would like to reference (by printing documentation for classes and
 * enumeration literals). These are like bookmarks.
 *
 * @author laurent.guise@art-et-histoire.com
 * @author tatjana.kostic@ieee.org
 */

public class BookmarkRegistry {
	private static final Logger _logger = Logger.getLogger(BookmarkRegistry.class.getName());

	private static final String BOOKMARK_PREFIX = "UML"; // ... because it's for a UmlObject

	private long _counter = 0;
	private final Map<UmlObject, String> _idsForUmlObject = new HashMap<>();
	private final Set<String> _availableIDs = new HashSet<>();

	/** Constructor. */
	public BookmarkRegistry() {
	}

	/**
	 * Returns the existing or the newly created bookmark ID for key <code>o</code>. Use this for
	 * every object whose documentation you may want to refer to.
	 */
	public String getOrCreateBookmarkID(UmlObject o) {
		String result = null;
		result = findID(o);
		if (result == null) {
			++_counter;
			if (_counter == Long.MAX_VALUE) {
				throw new RuntimeException("Bookmark counter reached max value: " + Long.MAX_VALUE);
			}
			result = BOOKMARK_PREFIX + _counter;
			_idsForUmlObject.put(o, result);
			_logger.trace(
					"BR: added new bookmark ID for '" + o.getQualifiedName() + "': " + result);
		} else {
			_logger.trace(
					"BR: returning bookmark ID for '" + o.getQualifiedName() + "': " + result);
		}
		return result;
	}

	public String findID(UmlObject o) {
		return _idsForUmlObject.get(o);
	}

	/**
	 * Called from writer for ObjectDocs that have bookmark ID when actual bookmark is added to the
	 * document; these are then available in the last pass, to insert hyperlinks pointing to those
	 * bookmarks. At present, we create bookmarks for classes and enumeration literals only.
	 */
	public void markAsAvailableInDocument(String bookmarkID) {
		_availableIDs.add(bookmarkID);
	}

	/**
	 * Returns whether the bookmark has been added to the document during writing; if so, it can be
	 * used to create hyperlink.
	 */
	public boolean isAvailableInDocument(String bookmarkID) {
		return _availableIDs.contains(bookmarkID);
	}
}
