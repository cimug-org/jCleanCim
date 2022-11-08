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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.tanjakostic.jcleancim.docgen.collector.PlaceholderSpec.Kind;

/**
 * @author tatjana.kostic@ieee.org
 * @version $Id: PlaceholderSpecTest.java 15 2016-07-12 15:11:42Z dev978 $
 */
public class PlaceholderSpecTest {

	static final String CLASS_NAME = "className";
	static final String DIAGRAM_NAME = "diagramName";
	static final String ATTR_NAME = "attrName";
	static final String PCKG_NAME = "pckgName";
	static final String FILE_NAME = "fileName";

	@Before
	public void setUp() {
		// nothing
	}

	@After
	public void tearDown() {
		// nothing
	}

	static PlaceholderSpec createUnsupportedInstance() {
		return new PlaceholderSpec("invalidPlaceholderSpec");
	}

	static PlaceholderSpec createFileInstance() {
		String text = PlaceholderSpec.constructFilePlaceholderText();
		return new PlaceholderSpec(text);
	}

	static PlaceholderSpec createDiagramInstance() {
		String text = PlaceholderSpec.constructDiagramPlaceholderText(PCKG_NAME, DIAGRAM_NAME);
		return new PlaceholderSpec(text);
	}

	static PlaceholderSpec createDiagNoteInstance() {
		String text = PlaceholderSpec.constructDiagNotePlaceholderText(PCKG_NAME, DIAGRAM_NAME);
		return new PlaceholderSpec(text);
	}

	static PlaceholderSpec createAttributeInstance() {
		String text = PlaceholderSpec.constructAttributePlaceholderText(CLASS_NAME, ATTR_NAME);
		return new PlaceholderSpec(text);
	}

	static PlaceholderSpec createIec61850NsNameInstance() {
		String text = PlaceholderSpec.constructIec61850NsNamePlaceholderText(CLASS_NAME);
		return new PlaceholderSpec(text);
	}

	static PlaceholderSpec createPresConditionsPackageInstance() {
		String text = PlaceholderSpec.constructPresConditionsPackagePlaceholderText(PCKG_NAME);
		return new PlaceholderSpec(text);
	}

	static PlaceholderSpec createClassInstance() {
		String text = PlaceholderSpec.constructClassPlaceholderText(PCKG_NAME, CLASS_NAME);
		return new PlaceholderSpec(text);
	}

	static PlaceholderSpec createFcPackageInstance() {
		String text = PlaceholderSpec.constructFcsPackagePlaceholderText(PCKG_NAME);
		return new PlaceholderSpec(text);
	}

	static PlaceholderSpec createTrgOpPackageInstance() {
		String text = PlaceholderSpec.constructTrgOpsPackagePlaceholderText(PCKG_NAME);
		return new PlaceholderSpec(text);
	}

	static PlaceholderSpec createAbbrPackageInstance() {
		String text = PlaceholderSpec.constructAbbrPackagePlaceholderText(PCKG_NAME);
		return new PlaceholderSpec(text);
	}

	static PlaceholderSpec createEnumPackageInstance() {
		String text = PlaceholderSpec.constructEnumPackagePlaceholderText(PCKG_NAME);
		return new PlaceholderSpec(text);
	}

	static PlaceholderSpec createPackageInstance() {
		String text = PlaceholderSpec.constructPackagePlaceholderText(PCKG_NAME);
		return new PlaceholderSpec(text);
	}

	static PlaceholderSpec createSpecialPackageInstance() {
		String text = PlaceholderSpec.constructLNMapPackagePlaceholderText(PCKG_NAME);
		return new PlaceholderSpec(text);
	}

	static PlaceholderSpec createDataIndexInstance() {
		String text = PlaceholderSpec.constructDataIndexPlaceholderText(PCKG_NAME);
		return new PlaceholderSpec(text);
	}

	// ============= Tests ===============

	@Test
	public void testCtor_unsupported() {
		PlaceholderSpec ph = createUnsupportedInstance();
		assertNull(ph.getFirstToken());
		assertNull(ph.getSecondToken());
		assertNotNull(ph.getErrorText());
		assertSame(Kind.UNSUPPORTED, ph.getKind());
	}

	@Test
	public void testCtor_file() {
		PlaceholderSpec ph = createFileInstance();
		assertNull(ph.getFirstToken());
		assertNull(ph.getSecondToken());
		assertNull(ph.getErrorText());
		assertSame(Kind.FILE, ph.getKind());
	}

	@Test
	public void testCtor_attr() {
		PlaceholderSpec ph = createAttributeInstance();
		assertEquals(CLASS_NAME, ph.getFirstToken());
		assertEquals(ATTR_NAME, ph.getSecondToken());
		assertNull(ph.getErrorText());
		assertSame(Kind.ATTRIBUTE, ph.getKind());
	}

	@Test
	public void testCtor_61850ns() {
		PlaceholderSpec ph = createIec61850NsNameInstance();
		assertEquals(CLASS_NAME, ph.getFirstToken());
		assertNull(ph.getSecondToken());
		assertNull(ph.getErrorText());
		assertSame(Kind.IEC61850_NSNAME, ph.getKind());
	}

	@Test
	public void testCtor_diagram() {
		PlaceholderSpec ph = createDiagramInstance();
		assertEquals(PCKG_NAME, ph.getFirstToken());
		assertEquals(DIAGRAM_NAME, ph.getSecondToken());
		assertNull(ph.getErrorText());
		assertSame(Kind.DIAGRAM, ph.getKind());
	}

	@Test
	public void testCtor_diagNote() {
		PlaceholderSpec ph = createDiagNoteInstance();
		assertEquals(PCKG_NAME, ph.getFirstToken());
		assertEquals(DIAGRAM_NAME, ph.getSecondToken());
		assertNull(ph.getErrorText());
		assertSame(Kind.DIAG_NOTE, ph.getKind());
	}

	@Test
	public void testCtor_presConditions() {
		PlaceholderSpec ph = createPresConditionsPackageInstance();
		assertEquals(PCKG_NAME, ph.getFirstToken());
		assertNull(ph.getSecondToken());
		assertNull(ph.getErrorText());
		assertSame(Kind.PRES_CONDITIONS, ph.getKind());
	}

	@Test
	public void testCtor_fcs() {
		PlaceholderSpec ph = createFcPackageInstance();
		assertEquals(PCKG_NAME, ph.getFirstToken());
		assertNull(ph.getSecondToken());
		assertNull(ph.getErrorText());
		assertSame(Kind.FCS, ph.getKind());
	}

	@Test
	public void testCtor_trgops() {
		PlaceholderSpec ph = createTrgOpPackageInstance();
		assertEquals(PCKG_NAME, ph.getFirstToken());
		assertNull(ph.getSecondToken());
		assertNull(ph.getErrorText());
		assertSame(Kind.TRGOPS, ph.getKind());
	}

	@Test
	public void testCtor_abbreviations() {
		PlaceholderSpec ph = createAbbrPackageInstance();
		assertEquals(PCKG_NAME, ph.getFirstToken());
		assertNull(ph.getSecondToken());
		assertNull(ph.getErrorText());
		assertSame(Kind.ABBREVIATIONS, ph.getKind());
	}

	@Test
	public void testCtor_enumerations() {
		PlaceholderSpec ph = createEnumPackageInstance();
		assertEquals(PCKG_NAME, ph.getFirstToken());
		assertNull(ph.getSecondToken());
		assertNull(ph.getErrorText());
		assertSame(Kind.SCL_ENUMS, ph.getKind());
	}

	@Test
	public void testCtor_package() {
		PlaceholderSpec ph = createPackageInstance();
		assertEquals(PCKG_NAME, ph.getFirstToken());
		assertNull(ph.getSecondToken());
		assertNull(ph.getErrorText());
		assertSame(Kind.PACKAGE, ph.getKind());
	}

	@Test
	public void testCtor_class() {
		PlaceholderSpec ph = createClassInstance();
		assertEquals(PCKG_NAME, ph.getFirstToken());
		assertEquals(CLASS_NAME, ph.getSecondToken());
		assertNull(ph.getErrorText());
		assertSame(Kind.CLASS, ph.getKind());
	}

	@Test
	public void testCtor_specialPackage() {
		PlaceholderSpec ph = createSpecialPackageInstance();
		assertEquals(PCKG_NAME, ph.getFirstToken());
		assertNull(ph.getSecondToken());
		assertNull(ph.getErrorText());
		assertSame(Kind.LNMAP_PACKAGE, ph.getKind());
	}

	@Test
	public void testCtor_dataIndex() {
		PlaceholderSpec ph = createDataIndexInstance();
		assertEquals(PCKG_NAME, ph.getFirstToken());
		assertNull(ph.getSecondToken());
		assertNull(ph.getErrorText());
		assertSame(Kind.DATA_INDEX, ph.getKind());
	}

	// -----------------------------

	@Test
	public void testGetErrorText_abbrPckTokenEmpty() {
		String text = PlaceholderSpec.constructAbbrPackagePlaceholderText("");
		PlaceholderSpec ph = new PlaceholderSpec(text);
		assertNotNull(ph.getErrorText());
	}

	@Test
	public void testGetErrorText_abbrPckTokenEmptyNull() {
		String text = PlaceholderSpec.constructAbbrPackagePlaceholderText(null);
		PlaceholderSpec ph = new PlaceholderSpec(text);
		assertNotNull(ph.getErrorText());
	}

	// -----------------------------

	@Test
	public void testGetErrorText_presCondPckTokenEmpty() {
		String text = PlaceholderSpec.constructPresConditionsPackagePlaceholderText("");
		PlaceholderSpec ph = new PlaceholderSpec(text);
		assertNotNull(ph.getErrorText());
	}

	@Test
	public void testGetErrorText_presCondPckTokenEmptyNull() {
		String text = PlaceholderSpec.constructPresConditionsPackagePlaceholderText(null);
		PlaceholderSpec ph = new PlaceholderSpec(text);
		assertNotNull(ph.getErrorText());
	}

	// -----------------------------

	@Test
	public void testGetErrorText_presFcPckTokenEmpty() {
		String text = PlaceholderSpec.constructFcsPackagePlaceholderText("");
		PlaceholderSpec ph = new PlaceholderSpec(text);
		assertNotNull(ph.getErrorText());
	}

	@Test
	public void testGetErrorText_presFcPckTokenEmptyNull() {
		String text = PlaceholderSpec.constructFcsPackagePlaceholderText(null);
		PlaceholderSpec ph = new PlaceholderSpec(text);
		assertNotNull(ph.getErrorText());
	}

	// -----------------------------

	@Test
	public void testGetErrorText_presTrgOpPckTokenEmpty() {
		String text = PlaceholderSpec.constructTrgOpsPackagePlaceholderText("");
		PlaceholderSpec ph = new PlaceholderSpec(text);
		assertNotNull(ph.getErrorText());
	}

	@Test
	public void testGetErrorText_presTrgOpPckTokenEmptyNull() {
		String text = PlaceholderSpec.constructTrgOpsPackagePlaceholderText(null);
		PlaceholderSpec ph = new PlaceholderSpec(text);
		assertNotNull(ph.getErrorText());
	}

	// -----------------------------

	@Test
	public void testGetErrorText_enumPckTokenEmpty() {
		String text = PlaceholderSpec.constructEnumPackagePlaceholderText("");
		PlaceholderSpec ph = new PlaceholderSpec(text);
		assertNotNull(ph.getErrorText());
	}

	@Test
	public void testGetErrorText_enumPckTokenEmptyNull() {
		String text = PlaceholderSpec.constructEnumPackagePlaceholderText(null);
		PlaceholderSpec ph = new PlaceholderSpec(text);
		assertNotNull(ph.getErrorText());
	}

	// -----------------------------

	@Test
	public void testGetErrorText_pckTokenEmpty() {
		String text = PlaceholderSpec.constructPackagePlaceholderText("");
		PlaceholderSpec ph = new PlaceholderSpec(text);
		assertNotNull(ph.getErrorText());
	}

	@Test
	public void testGetErrorText_pckTokenEmptyNull() {
		String text = PlaceholderSpec.constructPackagePlaceholderText(null);
		PlaceholderSpec ph = new PlaceholderSpec(text);
		assertNotNull(ph.getErrorText());
	}

	// -----------------------------

	@Test
	public void testGetErrorText_spPckTokenEmpty() {
		String text = PlaceholderSpec.constructLNMapPackagePlaceholderText("");
		PlaceholderSpec ph = new PlaceholderSpec(text);
		assertNotNull(ph.getErrorText());
	}

	@Test
	public void testGetErrorText_spPckTokenEmptyNull() {
		String text = PlaceholderSpec.constructLNMapPackagePlaceholderText(null);
		PlaceholderSpec ph = new PlaceholderSpec(text);
		assertNotNull(ph.getErrorText());
	}

	// -----------------------------

	@Test
	public void testGetErrorText_diTokenEmpty() {
		String text = PlaceholderSpec.constructDataIndexPlaceholderText("");
		PlaceholderSpec ph = new PlaceholderSpec(text);
		assertNotNull(ph.getErrorText());
	}

	@Test
	public void testGetErrorText_diTokenEmptyNull() {
		String text = PlaceholderSpec.constructDataIndexPlaceholderText(null);
		PlaceholderSpec ph = new PlaceholderSpec(text);
		assertNotNull(ph.getErrorText());
	}

	// -----------------

	@Test
	public void testGetErrorText_attrFirstTokenEmpty() {
		String text = PlaceholderSpec.constructAttributePlaceholderText("", ATTR_NAME);
		PlaceholderSpec ph = new PlaceholderSpec(text);
		assertNotNull(ph.getErrorText());
	}

	@Test
	public void testGetErrorText_attrFirstTokenNull() {
		String text = PlaceholderSpec.constructAttributePlaceholderText(null, ATTR_NAME);
		PlaceholderSpec ph = new PlaceholderSpec(text);
		assertNotNull(ph.getErrorText());
	}

	@Test
	public void testGetErrorText_attrSecTokenEmpty() {
		String text = PlaceholderSpec.constructAttributePlaceholderText(CLASS_NAME, "");
		PlaceholderSpec ph = new PlaceholderSpec(text);
		assertNotNull(ph.getErrorText());
	}

	@Test
	public void testGetErrorText_attrSecTokenNull() {
		String text = PlaceholderSpec.constructAttributePlaceholderText(CLASS_NAME, "");
		PlaceholderSpec ph = new PlaceholderSpec(text);
		assertNotNull(ph.getErrorText());
	}

	// -----------------

	@Test
	public void testGetErrorText_61850nsFirstTokenEmpty() {
		String text = PlaceholderSpec.constructIec61850NsNamePlaceholderText("");
		PlaceholderSpec ph = new PlaceholderSpec(text);
		assertNotNull(ph.getErrorText());
	}

	@Test
	public void testGetErrorText_61850nsFirstTokenNull() {
		String text = PlaceholderSpec.constructIec61850NsNamePlaceholderText(null);
		PlaceholderSpec ph = new PlaceholderSpec(text);
		assertNotNull(ph.getErrorText());
	}

	// -----------------

	@Test
	public void testGetErrorText_diagFirstTokenEmpty() {
		String text = PlaceholderSpec.constructAttributePlaceholderText("", DIAGRAM_NAME);
		PlaceholderSpec ph = new PlaceholderSpec(text);
		assertNotNull(ph.getErrorText());
	}

	@Test
	public void testGetErrorText_diagFirstTokenNull() {
		String text = PlaceholderSpec.constructAttributePlaceholderText(null, DIAGRAM_NAME);
		PlaceholderSpec ph = new PlaceholderSpec(text);
		assertNotNull(ph.getErrorText());
	}

	@Test
	public void testGetErrorText_diagSecTokenEmpty() {
		String text = PlaceholderSpec.constructAttributePlaceholderText(PCKG_NAME, "");
		PlaceholderSpec ph = new PlaceholderSpec(text);
		assertNotNull(ph.getErrorText());
	}

	@Test
	public void testGetErrorText_diagSecTokenNull() {
		String text = PlaceholderSpec.constructAttributePlaceholderText(PCKG_NAME, "");
		PlaceholderSpec ph = new PlaceholderSpec(text);
		assertNotNull(ph.getErrorText());
	}
}
