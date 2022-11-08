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
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.junit.Test;
import org.tanjakostic.jcleancim.util.MapOfMaps;

/**
 * @author tatjana.kostic@ieee.org
 * @version $Id: MapOfMapsTest.java 15 2016-07-12 15:11:42Z dev978 $
 */
public class MapOfMapsTest {

	protected MapOfMaps<String, String, String> createInstance() {
		return new MapOfMaps<>();
	}

	// ============= Tests ===============

	@Test
	public final void testPutValue() {
		MapOfMaps<String, String, String> moc = createInstance();
		assertTrue("should be empty", moc.isEmpty());

		Set<String> expKeys = new HashSet<>(Arrays.asList("first"));
		moc.putValue("first", "subFirst", "1");

		assertEquals(1, moc.size());
		assertEquals(1, moc.toStringLines().size());
		assertEquals(1, moc.calcValueSize());
		assertEquals(1, moc.subMap("first").size());

		assertEquals(expKeys, moc.keys());
		assertTrue("has key", moc.containsKey("first"));

		assertTrue("has sub-key", moc.subMap("first").containsKey("subFirst"));
		assertTrue("has sub-value", moc.value("first", "subFirst").contains("1"));
		assertNotNull(moc.subMap("inexistingKey"));
		assertNull(moc.value("first", "inexistingKey"));
	}

	@Test
	public final void testPutValueNull() {
		MapOfMaps<String, String, String> moc = createInstance();

		Set<String> expKeys = new HashSet<>(Arrays.asList("first"));
		moc.putValue("first", "subFirst1", "1.1");
		moc.putValue("first", "subFirst2", null);
		moc.putValue("first", "subFirst3", "1.3");

		assertEquals(1, moc.size());
		assertEquals(1, moc.toStringLines().size());
		assertEquals(3, moc.calcValueSize());
		assertEquals(3, moc.subMap("first").size());

		assertEquals(expKeys, moc.keys());
		assertTrue("has key", moc.containsKey("first"));

		assertTrue("has sub-value", moc.subMap("first").containsValue("1.3"));
		assertTrue("has sub-value", moc.subMap("first").containsValue(null));
	}

	@Test
	public final void testSizes() {
		MapOfMaps<String, String, String> moc = createInstance();

		moc.putValue("first", "subFirst1", "1.1");
		moc.putValue("first", "subFirst2", null);
		moc.putValue("first", "subFirst3", "1.2");
		moc.putValue("second", "subSecond1", "2.1");
		moc.putValue("second", "subSecond2", "2.2");
		moc.putValue("second", "subSecond3", "2.3");
		moc.putValue("second", "subSecond4", null);

		assertEquals(2, moc.size());
		assertEquals(2, moc.toStringLines().size());
		assertEquals(7, moc.calcValueSize());

		assertEquals(3, moc.subMap("first").size());
		assertEquals(4, moc.subMap("second").size());
		assertTrue("should contain a null value", moc.subMap("second").containsValue(null));
	}
}
