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

package org.tanjakostic.jcleancim.model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

import org.junit.Test;

/**
 * @author tatjana.kostic@ieee.org
 * @version $Id: UmlStereotypeTest.java 27 2019-11-23 16:29:38Z dev978 $
 */
public class UmlStereotypeTest {

	// ============= Tests ===============

	@SuppressWarnings("unused")
	@Test(expected = NullPointerException.class)
	public final void testCtorStringNull() {
		new UmlStereotype((String) null);
	}

	@SuppressWarnings("unused")
	@Test(expected = NullPointerException.class)
	public final void testCtorStringArrayNull() {
		new UmlStereotype((String[]) null);
	}

	public final void testCtorNoArg() {
		UmlStereotype s = new UmlStereotype();
		assertEquals("", s.value());
	}

	@SuppressWarnings("unused")
	@Test(expected = NullPointerException.class)
	public final void testCtorAnyArgumentNull() {
		new UmlStereotype("hello", null, "there");
	}

	@Test
	public final void testCtorSkipsEmptyArgument() {
		UmlStereotype s = new UmlStereotype("   ", "there");
		assertEquals("there", s.value());
	}

	@Test
	public final void testCtorArgumentContainsComma() {
		UmlStereotype s = new UmlStereotype("fir,st");
		assertEquals("fir, st", s.value());
	}

	@Test
	public final void testCtorArgumentContainsCommaAndSpace() {
		UmlStereotype s = new UmlStereotype("fir, st");
		assertEquals("fir, st", s.value());
	}

	@Test
	public final void testCtorTokenGetsTrimmed() {
		UmlStereotype s = new UmlStereotype("  first  ");
		assertEquals("first", s.value());
	}

	// -----------------------

	@Test
	public final void testValueSingleToken() {
		UmlStereotype s = new UmlStereotype("first");
		assertEquals("first", s.value());
	}

	@Test
	public final void testValueMultipleTokens() {
		UmlStereotype s = new UmlStereotype("first", "second");
		assertEquals("first, second", s.value());
	}

	// -----------------------

	@Test
	public final void testMemberOfAllContained() {
		List<String> input = Arrays.asList("first", "second", "third", "fourth");
		UmlStereotype stereo = new UmlStereotype("second", "fourth");
		assertTrue("all should be contained", stereo.memberOf(new HashSet<String>(input)));
	}

	@Test
	public final void testMemberOfSomeMissing() {
		List<String> input = Arrays.asList("first", "second", "third", "fourth");
		UmlStereotype stereo = new UmlStereotype("second", "dummy");
		assertFalse("some should be missing", stereo.memberOf(new HashSet<String>(input)));
	}

	// -----------------------

	@Test
	public final void testContainsAnyOfEmptyInput() {
		List<String> input = new ArrayList<>();
		UmlStereotype stereo = new UmlStereotype("second", "fourth");
		assertFalse("contains none", stereo.containsAnyOf(input));
	}

	@Test
	public final void testContainsAnyOf() {
		List<String> input = Arrays.asList("first", "second", "third", "fourth");
		UmlStereotype stereo = new UmlStereotype("second", "fourth");
		assertTrue("contains at least one of", stereo.containsAnyOf(input));
	}

	@Test
	public final void testContainsAnyOfFalse() {
		List<String> input = Arrays.asList("first", "third");
		UmlStereotype stereo = new UmlStereotype("second", "fourth");
		assertFalse("contains none", stereo.containsAnyOf(input));
	}

	// -----------------------

	@Test
	public final void testGetMissingTokens() {
		List<String> input = Arrays.asList("first", "second", "third", "fourth");
		UmlStereotype stereo = new UmlStereotype("second", "dummy");
		List<String> expected = Arrays.asList("dummy");
		assertEquals(new HashSet<String>(expected),
				stereo.getTokensOtherThan(new HashSet<String>(input)));
	}

	// -----------------------

	@Test
	public final void testToString() {
		UmlStereotype s = new UmlStereotype("first", "second");
		String toString = s.toString();
		assertTrue("should start with angle brackets", toString.startsWith("<<"));
		assertTrue("should end with angle brackets", toString.endsWith(">>"));
	}

	@Test
	public final void testToStringWhenEmpty() {
		UmlStereotype s = new UmlStereotype();
		assertTrue("empty string", s.toString().isEmpty());
	}
}
