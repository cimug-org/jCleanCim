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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.BeforeClass;
import org.junit.Test;
import org.tanjakostic.jcleancim.model.NameDecomposition;
import org.tanjakostic.jcleancim.util.Util;

/**
 * @author tatjana.kostic@ieee.org
 * @version $Id: NameDecompositionTest.java 15 2016-07-12 15:11:42Z dev978 $
 */
public class NameDecompositionTest {

	private static Map<String, String> _sortedAbbrTerms;

	@BeforeClass
	public static void setUp() {
		Map<String, String> terms = new HashMap<String, String>();
		terms.put("Acc", "Accuracy");
		terms.put("Abr", "Abrasion");
		terms.put("Amp", "Current non-phase-related");
		terms.put("Loc", "Local");
		terms.put("PhsA", "Phase L1");
		terms.put("A", "Current");
		terms.put("V", "Voltage");
		terms.put("Chr", "Characteristic");
		terms.put("Ch", "Channel");
		terms.put("Num", "Number");
		terms.put("Ha", "Harmonics (non-phase-related)");
		terms.put("Vol", "Voltage non-phase-related");
		terms.put("Vol3", "Voltage with number");
		terms.put("Amp", "Current non-phase-related");
		terms.put("K", "A by Chris");
		terms.put("L", "B by Chris");
		terms.put("mn", "cd by Chris");
		terms.put("KLm", "ABc by Chris");

		_sortedAbbrTerms = Util.sortByDecreasingLength(terms);
	}

	static Map<String, String> createUnknownTerm(String unk) {
		return NameDecomposition.createUnknownTerm(unk);
	}

	static Map<String, String> createTerm(String term) {
		return NameDecomposition.createTerm(term, _sortedAbbrTerms.get(term));
	}

	// ============= Tests ===============

	@Test
	public final void testCreateUnknownTerm() {
		Map<String, String> t = NameDecomposition.createUnknownTerm("term");
		assertTrue("should be unknown (no description)", NameDecomposition.isUnknown(t));
	}

	// --------------------

	@Test
	public final void testCreateTerm() {
		Map<String, String> t = NameDecomposition.createTerm("term", "some definition");
		assertFalse("should not be unknown (description is given)", NameDecomposition.isUnknown(t));
	}

	// --------------------

	@SuppressWarnings("unused")
	@Test(expected = NullPointerException.class)
	public final void testCtorInputNull() {
		new NameDecomposition(null, _sortedAbbrTerms);
	}

	@SuppressWarnings("unused")
	@Test(expected = IllegalArgumentException.class)
	public final void testCtorInputEmpty() {
		new NameDecomposition("  \n  \t ", _sortedAbbrTerms);
	}

	@SuppressWarnings("unused")
	@Test(expected = NullPointerException.class)
	public final void testCtorTermsNull() {
		new NameDecomposition("PhsA", null);
	}

	@SuppressWarnings("unused")
	@Test(expected = IllegalArgumentException.class)
	public final void testCtorTermsEmpty() {
		new NameDecomposition("PhsA", new HashMap<String, String>());
	}

	// --------------------

	@Test
	public final void testGettersWhenAllMatched() {
		NameDecomposition nd = new NameDecomposition("LocAmp", _sortedAbbrTerms);
		assertEquals("LocAmp", nd.getInputName());
		assertTrue("should be matched; all terms known", nd.isMatched());
		assertEquals(_sortedAbbrTerms.get("Loc"), nd.getDecomposedTerms().get(0).get("Loc"));
		assertEquals(_sortedAbbrTerms.get("Amp"), nd.getDecomposedTerms().get(1).get("Amp"));
	}

	@Test
	public final void testGettersWhenSomeUnmatched() {
		NameDecomposition nd = new NameDecomposition("DummyAmp", _sortedAbbrTerms);
		assertEquals("DummyAmp", nd.getInputName());
		assertFalse("should not be matched; first term not known", nd.isMatched());
		assertTrue("should be unknown", NameDecomposition.isUnknown(nd.getDecomposedTerms().get(0)));
		assertEquals(_sortedAbbrTerms.get("Amp"), nd.getDecomposedTerms().get(1).get("Amp"));
	}

	// --------------------

	@Test
	public void testMatchesAbbreviatedTerms_AccAbr_ok() {
		List<Map<String, String>> exp = new ArrayList<Map<String, String>>();
		exp.add(createTerm("Acc"));
		exp.add(createTerm("Abr"));

		assertDecomposition("AccAbr", exp, true);
	}

	@Test
	public void testMatchesAbbreviatedTerms_AccClcDev_nok() {
		List<Map<String, String>> exp = new ArrayList<Map<String, String>>();
		exp.add(createTerm("Acc"));
		exp.add(createUnknownTerm("ClcDev"));

		// List<Map<String, String>> act =
		assertDecomposition("AccClcDev", exp, false);
		// assertFalse("should be unknown", act.get(1))
	}

	@Test
	public void testMatchesAbbreviatedTerms_AmpLocPhsA_ok() {
		List<Map<String, String>> exp = new ArrayList<Map<String, String>>();
		String term = "Amp";
		exp.add(createTerm(term));
		exp.add(createTerm("Loc"));
		exp.add(createTerm("PhsA"));

		assertDecomposition("AmpLocPhsA", exp, true);
	}

	@Test
	public void testMatchesAbbreviatedTerms_AVChr33_nok() {
		List<Map<String, String>> exp = new ArrayList<Map<String, String>>();
		exp.add(createTerm("A"));
		exp.add(createTerm("V"));
		exp.add(createTerm("Chr"));
		exp.add(createUnknownTerm("33"));

		// List<Map<String, String>> act =
		assertDecomposition("AVChr33", exp, false);
	}

	@Test
	public void testMatchesAbbreviatedTerms_ChNum_ok() {
		List<Map<String, String>> exp = new ArrayList<Map<String, String>>();
		exp.add(createTerm("Ch"));
		exp.add(createTerm("Num"));

		assertDecomposition("ChNum", exp, true);
	}

	@Test
	public void testMatchesAbbreviatedTerms_Damp_nok() {
		List<Map<String, String>> exp = new ArrayList<Map<String, String>>();
		exp.add(createUnknownTerm("Damp"));

		// List<Map<String, String>> act =
		assertDecomposition("Damp", exp, false);
	}

	@Test
	public void testMatchesAbbreviatedTerms_HaVolAmp_ok() {
		List<Map<String, String>> exp = new ArrayList<Map<String, String>>();
		exp.add(createTerm("Ha"));
		exp.add(createTerm("Vol"));
		exp.add(createTerm("Amp"));

		assertDecomposition("HaVolAmp", exp, true);
	}

	@Test
	public void testMatchesAbbreviatedTerms_HaVolAmpr_nok() {
		List<Map<String, String>> exp = new ArrayList<Map<String, String>>();
		exp.add(createTerm("Ha"));
		exp.add(createTerm("Vol"));
		exp.add(createTerm("Amp"));
		exp.add(createUnknownTerm("r"));

		// List<Map<String, String>> act =
		assertDecomposition("HaVolAmpr", exp, false);
	}

	@Test
	public void testMatchesAbbreviatedTerms_MyDummy_nok() {
		List<Map<String, String>> exp = new ArrayList<Map<String, String>>();
		exp.add(createUnknownTerm("MyDummy"));

		// List<Map<String, String>> act =
		assertDecomposition("MyDummy", exp, false);
	}

	@Test
	public void testMatchesAbbreviatedTerms_MyDummyWithNmb33_nok() {
		List<Map<String, String>> exp = new ArrayList<Map<String, String>>();
		exp.add(createUnknownTerm("MyDummyWithNmb33"));

		// List<Map<String, String>> act =
		assertDecomposition("MyDummyWithNmb33", exp, false);
	}

	@Test
	public void testMatchesAbbreviatedTerms_HaXyzAmp1_nok() {
		List<Map<String, String>> exp = new ArrayList<Map<String, String>>();
		exp.add(createTerm("Ha"));
		exp.add(createUnknownTerm("Xyz"));
		exp.add(createTerm("Amp"));
		exp.add(createUnknownTerm("1"));

		// List<Map<String, String>> act =
		assertDecomposition("HaXyzAmp1", exp, false);
	}

	@Test
	public void testMatchesAbbreviatedTerms_KLm_ok() {
		List<Map<String, String>> exp = new ArrayList<Map<String, String>>();
		exp.add(createTerm("KLm"));

		assertDecomposition("KLm", exp, true);
	}

	@Test
	public void testMatchesAbbreviatedTerms_KLmn_nok() {
		List<Map<String, String>> exp = new ArrayList<Map<String, String>>();
		// KLmn should be K, L, mn; but no way to programmatically conclude this
		exp.add(createTerm("KLm"));
		exp.add(createUnknownTerm("n"));

		// List<Map<String, String>> act =
		assertDecomposition("KLmn", exp, false);
	}

	@Test
	public void testMatchesAbbreviatedTerms_Vol3Amp_ok() {
		List<Map<String, String>> exp = new ArrayList<Map<String, String>>();
		exp.add(createTerm("Vol3"));
		exp.add(createTerm("Amp"));

		assertDecomposition("Vol3Amp", exp, true);
	}

	private List<Map<String, String>> assertDecomposition(String input,
			List<Map<String, String>> expected, boolean allMatch) {
		NameDecomposition nd = new NameDecomposition(input, _sortedAbbrTerms);
		List<Map<String, String>> collected = nd.getDecomposedTerms();
		String trace = "(" + input + ")";
		assertEquals(trace, expected, collected);
		assertEquals(trace, Boolean.valueOf(allMatch), Boolean.valueOf(nd.isMatched()));
		return collected;
	}
}
