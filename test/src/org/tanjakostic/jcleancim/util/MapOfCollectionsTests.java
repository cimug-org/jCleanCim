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

package org.tanjakostic.jcleancim.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.junit.Test;
import org.tanjakostic.jcleancim.util.MapOfCollections;

/**
 * @author tatjana.kostic@ieee.org
 * @version $Id: MapOfCollectionsTests.java 15 2016-07-12 15:11:42Z dev978 $
 */
abstract public class MapOfCollectionsTests {

	protected abstract MapOfCollections<String, String> createInstance();

	// ============= Tests ===============

	@Test
	public final void testAddValue() {
		MapOfCollections<String, String> moc = createInstance();
		assertTrue("should be empty", moc.isEmpty());

		Set<String> expKeys = new HashSet<>(Arrays.asList("first"));
		moc.addValue("first", "1");

		assertEquals(1, moc.size());
		assertEquals(1, moc.toStringLines().size());
		assertEquals(1, moc.calcValueSize());

		assertEquals(expKeys, moc.keys());
		assertTrue("has key", moc.containsKey("first"));

		assertTrue("has sub-value", moc.subCollection("first").contains("1"));
		assertNotNull(moc.subCollection("inexistingKey"));
	}

	@Test
	public final void testAddValueMulti() {
		MapOfCollections<String, String> moc = createInstance();

		Set<String> expKeys = new HashSet<>(Arrays.asList("first"));
		moc.addValue("first", "1", "11");

		assertEquals(1, moc.size());
		assertEquals(1, moc.toStringLines().size());
		assertEquals(2, moc.calcValueSize());
		assertEquals(2, moc.subCollection("first").size());

		assertEquals(expKeys, moc.keys());
		assertTrue("has key", moc.containsKey("first"));

		assertTrue("has sub-value", moc.subCollection("first").contains("11"));
	}

	@Test
	public final void testAddValueNull() {
		MapOfCollections<String, String> moc = createInstance();

		Set<String> expKeys = new HashSet<>(Arrays.asList("first"));
		moc.addValue("first", "1", null, "3");

		assertEquals(1, moc.size());
		assertEquals(1, moc.toStringLines().size());
		assertEquals(3, moc.calcValueSize());
		assertEquals(3, moc.subCollection("first").size());

		assertEquals(expKeys, moc.keys());
		assertTrue("has key", moc.containsKey("first"));

		assertTrue("has sub-value", moc.subCollection("first").contains("3"));
		assertTrue("has sub-value", moc.subCollection("first").contains(null));
	}

	@Test
	public final void testSizes() {
		MapOfCollections<String, String> moc = createInstance();

		moc.addValue("first", "1", null, "3");
		moc.addValue("second", "x", "y", "z", null);

		assertEquals(2, moc.size());
		assertEquals(2, moc.toStringLines().size());
		assertEquals(7, moc.calcValueSize());

		assertEquals(3, moc.subCollection("first").size());
		assertEquals(4, moc.subCollection("second").size());
		assertTrue("should contain a null value", moc.subCollection("second").contains(null));
	}
}
