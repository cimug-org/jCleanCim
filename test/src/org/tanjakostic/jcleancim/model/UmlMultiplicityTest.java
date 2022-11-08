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
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.tanjakostic.jcleancim.model.UmlMultiplicity;

public class UmlMultiplicityTest {

	// ============= Tests ===============

	// --------------------

	@Test
	public void testParseBoundsPredefined() {
		assertSame(UmlMultiplicity.ONE, UmlMultiplicity.parseBounds("1", "1"));
		assertSame(UmlMultiplicity.ONE_TO_MANY, UmlMultiplicity.parseBounds("1", "*"));
		assertSame(UmlMultiplicity.OPT_ONE, UmlMultiplicity.parseBounds("0", "1"));
		assertSame(UmlMultiplicity.OPT_MANY, UmlMultiplicity.parseBounds("0", "*"));
		assertSame(UmlMultiplicity.EMPTY, UmlMultiplicity.parseBounds("", ""));
	}

	@Test
	public void testParseBoundsAcceptedAsPredefined() {
		assertSame(UmlMultiplicity.ONE, UmlMultiplicity.parseBounds("1", ""));
	}

	// --------------------

	@Test
	public void testParseFromStringPredefined() {
		assertSame(UmlMultiplicity.ONE, UmlMultiplicity.parseFromString("1..1"));
		assertSame(UmlMultiplicity.ONE_TO_MANY, UmlMultiplicity.parseFromString("1..*"));
		assertSame(UmlMultiplicity.OPT_ONE, UmlMultiplicity.parseFromString("0..1"));
		assertSame(UmlMultiplicity.OPT_MANY, UmlMultiplicity.parseFromString("0..*"));
		assertSame(UmlMultiplicity.EMPTY, UmlMultiplicity.parseFromString(""));

	}

	@Test
	public void testParseFromStringAcceptedAsPredefined() {
		assertSame(UmlMultiplicity.ONE, UmlMultiplicity.parseFromString("1"));
	}

	@Test
	public void testParseFromStringOther() {
		assertEquals("[?..4]", UmlMultiplicity.parseFromString("..4").toString());
		assertEquals("[0..*]", UmlMultiplicity.parseFromString("*").toString());
		assertEquals("[2..?]", UmlMultiplicity.parseFromString("2..").toString());
		assertEquals("[?..?]", UmlMultiplicity.parseFromString(null).toString());
	}

	// --------------------

	@Test
	public void testToStringONE() {
		assertEquals("[1..1]", UmlMultiplicity.ONE.toString());
	}

	@Test
	public void testToStringONE_TO_MANY() {
		assertEquals("[1..*]", UmlMultiplicity.ONE_TO_MANY.toString());
	}

	@Test
	public void testToStringOPT_ONE() {
		assertEquals("[0..1]", UmlMultiplicity.OPT_ONE.toString());
	}

	@Test
	public void testToStringOPT_MANY() {
		assertEquals("[0..*]", UmlMultiplicity.OPT_MANY.toString());
	}

	@Test
	public void testToStringEMPTY() {
		assertEquals("[?..?]", UmlMultiplicity.EMPTY.toString());
	}

	@Test
	public void testToStringLowerEmpty() {
		UmlMultiplicity m = UmlMultiplicity.parseBounds("", "*");
		assertEquals("[?..*]", m.toString());
	}

	@Test
	public void testToStringLowerNull() {
		UmlMultiplicity m = UmlMultiplicity.parseBounds(null, "*");
		assertEquals("[?..*]", m.toString());
	}

	@Test
	public void testToStringUpperEmptyLowerOne() {
		UmlMultiplicity m = UmlMultiplicity.parseBounds("1", "");
		assertEquals("defaults to 1", "[1..1]", m.toString());
	}

	@Test
	public void testToStringUpperEmptyLowerZero() {
		UmlMultiplicity m = UmlMultiplicity.parseBounds("0", "");
		assertEquals("[0..?]", m.toString());
	}

	@Test
	public void testToStringUpperEmptyLowerOther() {
		UmlMultiplicity m = UmlMultiplicity.parseBounds("*", "");
		assertEquals("[*..?]", m.toString());
	}

	// --------------------

	@Test
	public void testIsOptionalLowerBoundZero() {
		UmlMultiplicity m = UmlMultiplicity.parseBounds("0", "");
		assertTrue("should be optional", m.isOptional());
	}

	@Test
	public void testIsOptionalLowerBoundEmpty() {
		UmlMultiplicity m = UmlMultiplicity.parseBounds("", "");
		assertTrue("should be optional", m.isOptional());
	}

	// --------------------

	@Test
	public void testIsMultivalueUpperBoundOne() {
		UmlMultiplicity m = UmlMultiplicity.parseBounds("", " 1 ");
		assertFalse("should not be multivalue", m.isMultivalue());
	}

	@Test
	public void testIsMultivalueUpperBoundEmpty() {
		UmlMultiplicity m = UmlMultiplicity.parseBounds("", "");
		assertFalse("should not be multivalue", m.isMultivalue());
	}

	// --------------------

	@Test
	public void testIsCustomBothEmpty() {
		UmlMultiplicity m = UmlMultiplicity.parseBounds("", "");
		assertTrue(m.isCustom());
	}

	@Test
	public void testIsCustomLowerEmpty() {
		UmlMultiplicity m = UmlMultiplicity.parseBounds("", "*");
		assertTrue(m.isCustom());
	}

	@Test
	public void testIsCustomUpperAsN() {
		UmlMultiplicity m = UmlMultiplicity.parseBounds("1", "n");
		assertTrue(m.isCustom());
	}
}
