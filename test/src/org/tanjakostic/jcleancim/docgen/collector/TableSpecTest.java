/**
 * Redistribution and use in source and binary forms, with or without modification, are permitted.
 * <p>
 * THIS SOFTWARE IS PROVIDED BY THE AUTHORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES,
 * INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A
 * PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE AUTHORS BE LIABLE FOR ANY DIRECT,
 * INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR ONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT
 * LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package org.tanjakostic.jcleancim.docgen.collector;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.Test;
import org.tanjakostic.jcleancim.common.Nature;

/**
 * @author tatjana.kostic@ieee.org
 * @version $Id: TableSpecTest.java 17 2016-07-23 20:38:15Z dev978 $
 */
public class TableSpecTest {

	List<ColumnSpec> colsWith1Fmted = Arrays
			.asList(new ColumnSpec[] { ColumnSpec.createUnfmted(3, "attr", "docID", "label"),
					ColumnSpec.createFmted(6, "attr2", "docID2", "label2") });

	List<ColumnSpec> colsWithoutFmted = Arrays
			.asList(new ColumnSpec[] { ColumnSpec.createUnfmted(3, "attr", "docID", "label"),
					ColumnSpec.createUnfmted(6, "attr2", "docID2", "label2") });

	List<ColumnSpec> colsWith2Fmted = Arrays
			.asList(new ColumnSpec[] { ColumnSpec.createFmted(3, "attr", "docID", "label"),
					ColumnSpec.createFmted(6, "attr2", "docID2", "label2") });

	// ============= Tests ===============

	@SuppressWarnings("unused")
	@Test(expected = NullPointerException.class)
	public final void testCtorNameNull() {
		new TableSpec(null, Nature.CIM, colsWith1Fmted);
	}

	@SuppressWarnings("unused")
	@Test(expected = IllegalArgumentException.class)
	public final void testCtorNameEmpty() {
		new TableSpec(" \t \n ", Nature.CIM, colsWith1Fmted);
	}

	@SuppressWarnings("unused")
	@Test(expected = NullPointerException.class)
	public final void testCtorNatureNull() {
		new TableSpec("table", null, colsWith1Fmted);
	}

	@SuppressWarnings("unused")
	@Test(expected = NullPointerException.class)
	public final void testCtorColumnsNull() {
		new TableSpec("table", Nature.CIM, null);
	}

	@SuppressWarnings("unused")
	@Test(expected = NullPointerException.class)
	public final void testCtorColumnsHasNull() {
		List<ColumnSpec> myColumns = new ArrayList<ColumnSpec>();
		myColumns.add(null);
		myColumns.addAll(colsWith1Fmted);

		new TableSpec("table", Nature.CIM, myColumns);
	}

	@SuppressWarnings("unused")
	@Test(expected = IllegalArgumentException.class)
	public final void testCtorColumnsEmpty() {
		new TableSpec("table", Nature.CIM, new ArrayList<ColumnSpec>());
	}

	@SuppressWarnings("unused")
	@Test(expected = IllegalArgumentException.class)
	public final void testCtorColumnsWithMoreThanOneFormatted() {
		new TableSpec("table", Nature.CIM, colsWith2Fmted);
	}

	@Test
	public final void testCtorNoFormattedColumns() {
		TableSpec ts = new TableSpec("name", Nature.CIM, colsWithoutFmted);

		assertNull("no formatted columns", ts.getFmtIdx());
	}

	// -----------------------------------

	@Test
	public final void testGetters() {
		TableSpec ts = new TableSpec("name", Nature.CIM, colsWith1Fmted);

		assertEquals(Integer.valueOf(1), ts.getFmtIdx());
		assertEquals(2, ts.colCount());
		assertEquals("name", ts.getName());
		assertEquals(Nature.CIM, ts.getNature());
		assertEquals(2, ts.getColSpecs().size());
		assertArrayEquals(new String[] { "label", "label2" }, ts.getLabels());
		assertArrayEquals(new int[] { 3, 6 }, ts.getRelativeWidths());
	}

	// -----------------------------------

	@Test
	public final void testLabelsCloned() {
		TableSpec ts = new TableSpec("other", Nature.CIM, colsWith1Fmted);

		String[] labels = ts.getLabels();
		String[] expecteds = new String[] { "label", "label2" };
		assertArrayEquals(expecteds, labels);

		// modify returned:
		labels[0] = "modified";
		assertArrayEquals("modif is local", expecteds, ts.getLabels());
	}

	@Test
	public final void testWidthsCloned() {
		TableSpec ts = new TableSpec("other", Nature.CIM, colsWith1Fmted);

		int[] relativeWidths = ts.getRelativeWidths();
		int[] expecteds = new int[] { 3, 6 };
		assertArrayEquals(expecteds, relativeWidths);

		// modify returned:
		relativeWidths[0] = 22;
		assertArrayEquals("modif is local", expecteds, ts.getRelativeWidths());
	}
}
