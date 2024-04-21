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

import static org.junit.Assert.assertEquals;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.tanjakostic.jcleancim.docgen.collector.PlaceholderSpec;

/**
 * @author tatjana.kostic@ieee.org
 * @version $Id: PlaceholderTest.java 21 2019-08-12 15:44:50Z dev978 $
 */
public class PlaceholderTest {

	static final String PCKG_NAME = "pckgName";

	@Before
	public void setUp() {
		// nothing
	}

	@After
	public void tearDown() {
		// nothing
	}

	static Placeholder createPackageInstance(int figCount, int tabCount) {
		String text = PlaceholderSpec.constructPackagePlaceholderText(PCKG_NAME);
		return new Placeholder(new PlaceholderSpec(text), figCount, tabCount);
	}

	// ============= Tests ===============

	@Test
	public void testCtor_packageWithFigsTabs() {
		int figCount = 2;
		int tabCount = 1;
		Placeholder ph = createPackageInstance(figCount, tabCount);
		assertEquals(figCount, ph.getFigureCountBefore());
		assertEquals(tabCount, ph.getTableCountBefore());
	}

	@Test
	public void testAddFigureAddTable() {
		int figCount = 2;
		int tabCount = 1;
		Placeholder ph = createPackageInstance(figCount, tabCount);
		assertEquals(figCount, ph.getFigureCountBefore());
		assertEquals(tabCount, ph.getTableCountBefore());

		assertEquals(figCount + 1, ph.addFigure());
		assertEquals(tabCount + 1, ph.addTable());
	}

	@Test
	public void testGetFigureAndTableCount() {
		int figCount = 2;
		int tabCount = 1;
		Placeholder ph = createPackageInstance(figCount, tabCount);
		assertEquals(figCount, ph.getFigureCount());
		assertEquals(tabCount, ph.getTableCount());

		ph.addFigure();
		ph.addTable();

		assertEquals(figCount + 1, ph.getFigureCount());
		assertEquals(tabCount + 1, ph.getTableCount());
	}

	@Test
	public void testIncrementFigureAndTableBefore() {
		Placeholder ph = createPackageInstance(2, 1);
		assertEquals(2, ph.getFigureCount());
		assertEquals(1, ph.getTableCount());

		ph.addFigure();
		ph.addTable();

		assertEquals(3, ph.getFigureCount());
		assertEquals(2, ph.getTableCount());

		ph.incrementFigureBefore();
		ph.incrementTableBefore();

		assertEquals(3, ph.getFigureCountBefore());
		assertEquals(2, ph.getTableCountBefore());
		assertEquals(4, ph.getFigureCount());
		assertEquals(3, ph.getTableCount());
	}
}
