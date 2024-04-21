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

package org.tanjakostic.jcleancim.docgen.writer.word;

import org.tanjakostic.jcleancim.docgen.collector.ClassDoc;
import org.tanjakostic.jcleancim.docgen.collector.FigureDoc;
import org.tanjakostic.jcleancim.docgen.collector.PackageDoc;
import org.tanjakostic.jcleancim.docgen.collector.PropertiesDoc;
import org.tanjakostic.jcleancim.docgen.writer.Cursor;
import org.tanjakostic.jcleancim.docgen.writer.Style;
import org.tanjakostic.jcleancim.docgen.writer.Writer;

/**
 * @author tatjana.kostic@ieee.org
 * @version $Id: WordWriter.java 21 2019-08-12 15:44:50Z dev978 $
 */
public interface WordWriter<O> extends Writer {

	/**
	 * Writes all related to a package. For root package, starts from the range in the
	 * <code>initCursor</code>.
	 */
	public Cursor<O> writePackage(Cursor<O> initCursor, PackageDoc doc, boolean isRoot);

	public Cursor<O> writeDataIndex(Cursor<O> initCursor, PackageDoc packageDoc);

	public Cursor<O> writeLnMapPackage(Cursor<O> initCursor, PackageDoc packageDoc);

	public Cursor<O> writePresCondTable(Cursor<O> initCursor, PackageDoc packageDoc);

	public Cursor<O> writeFcTable(Cursor<O> initCursor, PackageDoc packageDoc);

	public Cursor<O> writeTrgOpTable(Cursor<O> initCursor, PackageDoc packageDoc);

	public Cursor<O> writeAbbrTable(Cursor<O> initCursor, PackageDoc packageDoc);

	public Cursor<O> writeSclEnum(Cursor<O> cursor, PackageDoc packageDoc);

	/**
	 * Writes all related to a class, including the title, at the start of range in
	 * <code>cursor</code>.
	 */
	public Cursor<O> writeExplicitClass(Cursor<O> cursor, ClassDoc doc);

	/** Writes all related to a class at the end of range in <code>initCursor</code>. */
	public Cursor<O> writeClassFromPackage(Cursor<O> cursor, ClassDoc doc, Style headStyle);

	/**
	 * Writes a set of properties as a table at the end of range in <code>cursor</code>. Used to
	 * write all related to a set of class properties (attributes/literals, or association ends, or
	 * operations), or for a collection of one type of properties from one or more packages or
	 * classes that need to be put in a table format.
	 */
	public Cursor<O> writeProperties(Cursor<O> initCursor, PropertiesDoc doc);

	/** Writes all related to a diagram at the end of range in <code>cursor</code>. */
	public Cursor<O> writeDiagram(Cursor<O> cursor, FigureDoc doc);

	/** Returns whether to apply close/reopen hack (may be needed for performance reasons). */
	public boolean applyCloseReopen();
}
