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

package org.tanjakostic.jcleancim.validation;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;

import org.junit.Before;
import org.junit.Test;
import org.tanjakostic.jcleancim.validation.InvalidCharactersFinder;

/**
 * @author tatjana.kostic@ieee.org
 * @version $Id: InvalidCharactersFinderTest.java 15 2016-07-12 15:11:42Z dev978 $
 */
public class InvalidCharactersFinderTest {

	private InvalidCharactersFinder _strict;
	private InvalidCharactersFinder _strictAndUnderscore;
	private InvalidCharactersFinder _numUnderscoreEtc;

	@Before
	public void setUp() {
		_strict = InvalidCharactersFinder.STRICT;
		_strictAndUnderscore = InvalidCharactersFinder.STRICT_UNDERSCORE_DASH;
		_numUnderscoreEtc = InvalidCharactersFinder.NUM_UNDERSCORE_DASH_SPACE_COMMA;
	}

	// ============= Tests ===============

	@SuppressWarnings("unused")
	@Test(expected = NullPointerException.class)
	public final void testCtorNull() {
		new InvalidCharactersFinder(null);
	}

	public final void testCtor() {
		InvalidCharactersFinder myFinder = new InvalidCharactersFinder("^a-c");
		assertEquals("^a-c", myFinder.getRegexExpression());
		assertEquals("^a-c", myFinder.getCompiledRegexExpression().pattern());
	}

	// --------------------------

	@Test
	public final void testFindInvalidCharacters() {
		assertTrue("returns empty list", _strict.findInvalidCharacters(null).isEmpty());
		assertTrue("returns empty list", _strict.findInvalidCharacters("").isEmpty());
		assertTrue("returns empty list",
				_strict.findInvalidCharacters("atrhoughzAthroughz0123456789").isEmpty());
	}

	@Test
	public final void testFindInvalidCharactersStartsWithNumber() {
		assertEquals(Arrays.asList("0"), _strict.findInvalidCharacters("0"));
		assertEquals(Arrays.asList("1"), _strict.findInvalidCharacters("1abc"));
		assertEquals(Arrays.asList("2", "-"), _strict.findInvalidCharacters("2-abc"));
		assertEquals(Arrays.asList("3", "_"), _strict.findInvalidCharacters("3a_bc"));
	}

	@Test
	public final void testFindInvalidCharactersCatchesSpace() {
		assertEquals(Arrays.asList(" "), _strict.findInvalidCharacters(" abcd"));
		assertEquals(Arrays.asList(" "), _strict.findInvalidCharacters("abcd "));
		assertEquals(Arrays.asList(" "), _strict.findInvalidCharacters("ab cd"));
	}

	@Test
	public final void testFindInvalidCharactersCatchesTab() {
		assertEquals(Arrays.asList("\t"), _strict.findInvalidCharacters("\tabcd"));
		assertEquals(Arrays.asList("\t"), _strict.findInvalidCharacters("abcd\t"));
		assertEquals(Arrays.asList("\t"), _strict.findInvalidCharacters("ab\tcd"));
	}

	@Test
	public final void testFindInvalidCharactersCatchesMultipleChars() {
		assertEquals(Arrays.asList(" ", "_", "–", "&"), _strict.findInvalidCharacters(" ab_c–d&"));
	}

	@Test
	public final void testFindInvalidCharactersWithUnderscoreAllowedCatchesMultipleChars() {
		assertEquals(Arrays.asList("8", " ", "–", "&"),
				_strictAndUnderscore.findInvalidCharacters("8 ab-c–d&"));
	}

	@Test
	public final void testFindInvalidCharactersWithNumUnderscoreEtcAllowedCatchesMultipleChars() {
		assertEquals(Arrays.asList("–", "&"), _numUnderscoreEtc.findInvalidCharacters("8 ab-c–d&"));
	}
}
