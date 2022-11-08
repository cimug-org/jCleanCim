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

package org.tanjakostic.jcleancim.util;

import static org.junit.Assert.assertEquals;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * @author tatjana.kostic@ieee.org
 * @version $Id$
 */
public class ProgressBarTest {

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	// ============= Tests ===============

	@Test(expected = IllegalArgumentException.class)
	public final void testProgressBarPercOver100() {
		ProgressBar pb = new ProgressBar(500, 101);
		assertEquals("/", pb.getProgress());
	}

	@Test(expected = IllegalArgumentException.class)
	public final void testProgressBarPercNegative() {
		ProgressBar pb = new ProgressBar(-1, 20);
	}

	@Test
	public final void testCorrectlyInitialised() {
		int perc = 3;
		int chunksOf = (int) Math.round(500 * (perc * 0.01));

		ProgressBar pb = new ProgressBar(500, perc);
		assertEquals(500, pb.getTotal());
		assertEquals(chunksOf, pb.getUpdateStep());
		assertEquals("", pb.getProgress());
	}

	// ===================================

	@Test
	public final void testUpdate() {
		ProgressBar pb = new ProgressBar(100, 1);
		pb.update(0);
		pb.update(5);
		pb.update(10);
		pb.update(20);
	}

}
