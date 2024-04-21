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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * @author tatjana.kostic@ieee.org
 * @version $Id: IDHelperTest.java 21 2019-08-12 15:44:50Z dev978 $
 */
public class IDHelperTest {

	IDHelper helper;

	@Before
	public void setUp() {
		helper = IDHelper.instance();
	}

	@After
	public void tearDown() {
		helper.reset();
	}

	// ============= Tests ===============

	@Test
	public final void testCreateDocID_AcceptsNullArguments() {
		helper.createDocID(null, null);
	}

	@Test
	public final void testCreateDocID_AcceptsEmptyArguments() {
		assertEquals(".1.", helper.createDocID("  ", "\t  \n"));
	}

	@Test
	public final void testCreateDocID_UniqueEvenWithSameArguments() {
		String id1 = helper.createDocID(null, null);
		String id2 = helper.createDocID(null, null);
		assertFalse("id-s should differ", id1.equals(id2));
	}

	@Test
	public final void testGetCounter() {
		assertEquals(0, helper.getDocIDCounter());
		helper.createDocID(null, null);
		assertEquals(1, helper.getDocIDCounter());
		helper.createDocID(null, null);
		assertEquals(2, helper.getDocIDCounter());
	}
}
