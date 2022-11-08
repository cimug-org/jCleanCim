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

package org.tanjakostic.jcleancim.docgen.collector.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;

import org.junit.Test;
import org.tanjakostic.jcleancim.docgen.collector.AGSpec;
import org.tanjakostic.jcleancim.docgen.collector.EntryDoc;
import org.tanjakostic.jcleancim.docgen.collector.WAX;

/**
 * @author tatjana.kostic@ieee.org
 * @version $Id: ReorderingTest.java 15 2016-07-12 15:11:42Z dev978 $
 */
public class ReorderingTest {

	public EntryDoc createSubhead() {
		AGSpec agSpec = AGSpec.create(WAX.LOC_instTag, WAX.CAT_daDesc, "subhead");
		return EntryDocImpl.createGroupSubhead(agSpec, 2);
	}

	public EntryDoc createData(String firstValue) {
		return EntryDocImpl.createData(null, null, firstValue, "doc for " + firstValue);
	}

	// ============= Tests ===============

	@Test
	public final void testAddReorderingItem() {
		List<EntryDoc> entryDocs = new ArrayList<EntryDoc>();
		entryDocs.add(createSubhead());
		entryDocs.add(createData("1"));
		entryDocs.add(createData("2"));
		entryDocs.add(createSubhead());
		entryDocs.add(createData("4"));
		entryDocs.add(createData("5"));

		Reordering r = new Reordering();
		r.addReorderingItem(entryDocs.get(2), entryDocs.get(5));

		assertEquals(1, r.getReorderingItems().size());
		assertSame(entryDocs.get(2), r.getReorderingItems().get(0).getToMoveDoc());
		assertSame(entryDocs.get(5), r.getReorderingItems().get(0).getAfterWhichDoc());
	}

	// --------------------------------

	@Test
	public final void testReorderOneToMoveAfterLast() {
		List<EntryDoc> entryDocs = new ArrayList<EntryDoc>();
		entryDocs.add(createSubhead());
		entryDocs.add(createData("1"));
		entryDocs.add(createData("2"));
		entryDocs.add(createSubhead());
		entryDocs.add(createData("4"));
		entryDocs.add(createData("5"));

		Reordering r = new Reordering();
		r.addReorderingItem(entryDocs.get(2), entryDocs.get(5));

		List<EntryDoc> actual = r.reorder(entryDocs);

		List<EntryDoc> expected = new ArrayList<EntryDoc>();
		expected.add(entryDocs.get(0));
		expected.add(entryDocs.get(1));
		expected.add(entryDocs.get(3));
		expected.add(entryDocs.get(4));
		expected.add(entryDocs.get(5));
		expected.add(entryDocs.get(2));

		assertEquals(new LinkedHashSet<EntryDoc>(expected), new LinkedHashSet<EntryDoc>(actual));
	}

	@Test
	public final void testReorderTwoOneToMoveAfterLastOtherToMoveAfterTheFirst() {
		List<EntryDoc> entryDocs = new ArrayList<EntryDoc>();
		entryDocs.add(createSubhead());
		entryDocs.add(createData("1"));
		entryDocs.add(createData("2"));
		entryDocs.add(createSubhead());
		entryDocs.add(createData("4"));
		entryDocs.add(createData("5"));

		Reordering r = new Reordering();
		r.addReorderingItem(entryDocs.get(2), entryDocs.get(5));
		r.addReorderingItem(entryDocs.get(4), entryDocs.get(2));

		List<EntryDoc> actual = r.reorder(entryDocs);

		List<EntryDoc> expected = new ArrayList<EntryDoc>();
		expected.add(entryDocs.get(0));
		expected.add(entryDocs.get(1));
		expected.add(entryDocs.get(3));
		expected.add(entryDocs.get(5));
		expected.add(entryDocs.get(2));
		expected.add(entryDocs.get(4));

		assertEquals(new LinkedHashSet<EntryDoc>(expected), new LinkedHashSet<EntryDoc>(actual));
	}

	@Test
	public final void testReorderTwoCircular() {
		List<EntryDoc> entryDocs = new ArrayList<EntryDoc>();
		entryDocs.add(createSubhead());
		entryDocs.add(createData("1"));
		entryDocs.add(createData("2"));
		entryDocs.add(createSubhead());
		entryDocs.add(createData("4"));
		entryDocs.add(createData("5"));

		Reordering r = new Reordering();
		r.addReorderingItem(entryDocs.get(2), entryDocs.get(5));
		r.addReorderingItem(entryDocs.get(5), entryDocs.get(2));

		List<EntryDoc> actual = r.reorder(entryDocs);

		List<EntryDoc> expected = new ArrayList<EntryDoc>();
		expected.add(entryDocs.get(0));
		expected.add(entryDocs.get(1));
		expected.add(entryDocs.get(3));
		expected.add(entryDocs.get(4));
		expected.add(entryDocs.get(2));
		expected.add(entryDocs.get(5));

		assertEquals(new LinkedHashSet<EntryDoc>(expected), new LinkedHashSet<EntryDoc>(actual));
	}
}
