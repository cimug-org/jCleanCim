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
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.junit.Ignore;
import org.junit.Test;
import org.tanjakostic.jcleancim.common.Nature;
import org.tanjakostic.jcleancim.common.OwningWg;
import org.tanjakostic.jcleancim.model.UmlPackage.Data;
import org.tanjakostic.jcleancim.model.UmlPackage.Kind;
import org.tanjakostic.jcleancim.util.ApplicationException;

/**
 * @author tatjana.kostic@ieee.org
 * @version $Id: UmlPackageTest.java 27 2019-11-23 16:29:38Z dev978 $
 */
public class UmlPackageTest extends UmlStructureTestBase {

	// ===== org.tanjakostic.jcleancim.model.UmlStructureTestBase =====

	@Override
	protected UmlStructure createInstance(UmlObjectData objData, UmlStructure.Data data)
			throws Exception {
		return createInstance(emptyModel, objData, new Data(data));
	}

	@Override
	protected UmlStructure createInstance(UmlModel model, UmlObjectData objData,
			UmlStructure.Data data) {
		return new UmlPackage(model, objData, new Data(data));
	}

	@Override
	protected UmlStructure createInstanceOfOtherType(UmlModel model) throws Exception {
		UmlPackage mp = (model != null) ? UmlPackage.basic(model, "mp")
				: UmlPackage.basic(emptyModel, "mp");
		return mp.addClass(UmlClass.basic(mp, "class", ""));
	}

	// ============= Tests ===============

	@Test(expected = NullPointerException.class)
	public final void testCtorModelModelNull() {
		new UmlPackage((UmlModel) null, new UmlObjectData("mp"), Data.empty());
	}

	@Test(expected = NullPointerException.class)
	public final void testCtorModelODataNull() {
		new UmlPackage(emptyModel, null, Data.empty());
	}

	@Test(expected = NullPointerException.class)
	public final void testCtorModelDataNull() {
		new UmlPackage(emptyModel, new UmlObjectData("mp"), null);
	}

	@Test
	public final void testGetNatureModelPackageEmptyConfigCimIsDefault() {
		UmlPackage mp = new UmlPackage(emptyModel, new UmlObjectData("mp"), Data.empty());

		assertTrue("no package configured for IEC61850 nature",
				emptyModel.getCfg().getIec61850NaturePackages().isEmpty());
		assertSame("default nature is CIM", Nature.CIM, mp.getNature());
	}

	@Test
	public final void testGetNatureModelPackageNonCimNatureConfigured()
			throws ApplicationException {
		String p61850Name = "mp";
		// we first have to provide configuration, to recognise 61850 nature
		UmlModel m = createEmptyModelRecognising61850Nature(p61850Name);
		UmlPackage mp = UmlPackage.basic(m, p61850Name);

		assertFalse("should have one model package name for IEC61850 nature",
				mp.getModel().getCfg().getIec61850NaturePackages().isEmpty());
		assertSame(Nature.IEC61850, mp.getNature());
	}

	@Test
	public final void testModelPackageAccessorsJustAfterCreation() {
		UmlPackage mp = new UmlPackage(emptyModel, new UmlObjectData("mp"), Data.empty());

		assertModelPackageInitialisedProperly(mp, Kind.MODEL, Nature.CIM, OwningWg.OTHER_CIM);
	}

	public void assertModelPackageInitialisedProperly(UmlPackage mp, Kind expectedKind,
			Nature expectedNature, OwningWg expectedOwner) {
		assertSame(emptyModel, mp.getModel());
		assertNull(mp.getContainingPackage());

		assertSame(expectedKind, mp.getKind());
		assertEquals(-1, mp.getDepth());
		assertSame(expectedNature, mp.getNature());
		assertSame(expectedOwner, mp.getOwner());

		assertNull(mp.getVersionInfo());
		assertFalse("never informative", mp.isInformative());
		assertFalse("shouldn't be top", mp.isTop());
	}

	// ----------------------------------

	@Test(expected = NullPointerException.class)
	public final void testCtorPackagePackageNull() {
		new UmlPackage((UmlPackage) null, new UmlObjectData("mp"), Data.empty());
	}

	@Test(expected = NullPointerException.class)
	public final void testCtorPackageODataNull() {
		UmlPackage mp = UmlPackage.basic(emptyModel, "mp");
		new UmlPackage(mp, null, Data.empty());
	}

	@Test(expected = NullPointerException.class)
	public final void testCtorPackageDataNull() {
		UmlPackage mp = UmlPackage.basic(emptyModel, "mp");
		new UmlPackage(mp, new UmlObjectData("tp"), null);
	}

	// ----------------------------------
	// we don't test constructor for null-package (model calls them internally),
	// but we get it from the model

	@Test
	public final void testNullPackageAccessorsJustAfterCreation() {
		Nature nature = Nature.IEC61850;
		UmlPackage nullMp = emptyModel.getNullModelPackages().get(nature);

		assertModelPackageInitialisedProperly(nullMp, Kind.NULL_MODEL, nature,
				OwningWg.OTHER_IEC61850);

		assertEquals(1, nullMp.getClasses().size());
	}

	@Test
	public final void testNullPackageAccessorsAfterCompleteModelBuilt()
			throws ApplicationException {
		UmlModel sampleModel = SampleModelFixture.create();

		for (UmlPackage nullMp : sampleModel.getNullModelPackages().values()) {
			assertNull("should have no version info", nullMp.getVersionInfo());
			assertTrue("should have no skipped items", nullMp.getSkippedUmlItems().isEmpty());
			assertTrue("should have no dependencies as source",
					nullMp.getDependenciesAsSource().isEmpty());
			assertTrue("should have no dependencies as target",
					nullMp.getDependenciesAsTarget().isEmpty());
			assertTrue("should have no diagrams", nullMp.getDiagrams().isEmpty());
			assertTrue("should have no child packages", nullMp.getChildPackages().isEmpty());
			assertEquals("should have single null-class", 1, nullMp.getClasses().size());
		}
	}

	// ----------------------------------

	@Ignore(value = "until implemented basic tests for others")
	@Test
	public final void testGetQualifiedNameModelOrNullPackage() {
		fail("Not yet implemented"); // TODO
	}

	@Ignore(value = "until implemented basic tests for others")
	@Test
	public final void testGetQualifiedNameTopOrOtherPackage() {
		fail("Not yet implemented"); // TODO
	}

	// ----------------------------------

	@Test
	public final void testIsInformativeModelPackage() {
		UmlPackage mp = UmlPackage.basic(emptyModel, "Informative");

		assertFalse("model package cannot be informative", mp.isInformative());
	}

	@Test
	public final void testIsInformativeDetailedDiagrams() {
		UmlPackage mp = UmlPackage.basic(emptyModel, "mp");
		UmlPackage tp = UmlPackage.basic(mp, "tp");
		UmlPackage ddp = UmlPackage.basic(tp, UML.DetailedDiagrams);

		assertTrue("should be informative", ddp.isInformative());
	}

	@Test
	public final void testIsInformativeNamePrefixInf() {
		UmlPackage mp = UmlPackage.basic(emptyModel, "mp");
		UmlPackage tp = UmlPackage.basic(mp, "tp");
		UmlPackage ddp = UmlPackage.basic(tp, "Inf");

		assertTrue("should be informative", ddp.isInformative());
	}

	@Test
	public final void testIsInformativeSubpackageOfDetailedDiagrams() {
		UmlPackage mp = UmlPackage.basic(emptyModel, "mp");
		UmlPackage tp = UmlPackage.basic(mp, "tp");
		UmlPackage ddp = UmlPackage.basic(tp, UML.DetailedDiagrams);
		UmlPackage ddsp = UmlPackage.basic(ddp, "DDSubPackage");

		assertTrue("should be informative", ddsp.isInformative());
	}

	@Test
	public final void testIsInformativeDocABC() {
		UmlPackage mp = UmlPackage.basic(emptyModel, "mp");
		UmlPackage tp = UmlPackage.basic(mp, "Tp");
		UmlPackage docTp = UmlPackage.basic(tp, String.format(UML.DOC_FORMAT_STRING, "Tp"));

		assertTrue("should be informative", docTp.isInformative());
	}

	@Test
	public final void testIsInformativeSubpackageOfDocABC() {
		UmlPackage mp = UmlPackage.basic(emptyModel, "mp");
		UmlPackage tp = UmlPackage.basic(mp, "Tp");
		UmlPackage docTp = UmlPackage.basic(tp, String.format(UML.DOC_FORMAT_STRING, "Tp"));
		UmlPackage subDocTp = UmlPackage.basic(docTp, "SubPackageOfDocTp");

		assertTrue("should be informative", subDocTp.isInformative());
	}

	@Test
	public final void testIsInformativeArbitraryPackageStartingWithDoc() {
		UmlPackage mp = UmlPackage.basic(emptyModel, "mp");
		UmlPackage tp = UmlPackage.basic(mp, "Documentation");

		assertFalse("should not be informative", tp.isInformative());
	}

	@Test
	public final void testIsInformativeArbitraryPackageWithInformativeStereotype() {
		UmlPackage mp = UmlPackage.basic(emptyModel, "mp");
		UmlPackage tp = UmlPackage.basic(mp, "SomePckg", UmlStereotype.INFORMATIVE);
		UmlPackage p = UmlPackage.basic(tp, "SubDocumentation");

		assertTrue("top package should be inf (through stereotype)", tp.isInformative());
		assertTrue("child package should be informative (through container)", p.isInformative());
	}

	// ==============================================

	@Test
	public final void testGetKinds() {
		assertFalse("should not be empty", UmlPackage.getKinds(null).isEmpty());
	}

	// ==============================================

	@Ignore(value = "until implemented basic tests for others")
	@Test
	public final void testGetClasses() {
		fail("Not yet implemented"); // TODO
	}

	@Test
	public final void testOrderClasses() {
		UmlPackage mp = UmlPackage.basic(emptyModel, "mp");
		UmlClass c1 = UmlClass.basic(mp, "c1", "");
		UmlClass c2 = UmlClass.basic(mp, "c2", "");
		UmlClass c3 = UmlClass.basic(mp, "c3", "");
		UmlClass c4 = UmlClass.basic(mp, "c4", "");

		List<String> orders = new ArrayList<String>();
		orders.add(c4.getUuid());
		orders.add(c3.getUuid());
		orders.add(c2.getUuid());
		orders.add(c1.getUuid());
		Collection<UmlClass> expecteds = new LinkedHashSet<UmlClass>();
		expecteds.add(c4);
		expecteds.add(c3);
		expecteds.add(c2);
		expecteds.add(c1);

		mp.orderClasses(orders);
		assertEquals(expecteds, mp.getClasses());
	}

	// ==============================================

	@Ignore(value = "until implemented basic tests for others")
	@Test
	public final void testGetVersionInfo() {
		fail("Not yet implemented"); // TODO
	}

	@Ignore(value = "until implemented basic tests for others")
	@Test
	public final void testGetNamespaceInfo() {
		fail("Not yet implemented"); // TODO
	}

	// ==============================================

	@Test
	public final void testGetChildPackages() {
		FixtureForDependenciesAndNamespaces f = new FixtureForDependenciesAndNamespaces(emptyModel);

		Set<UmlPackage> expWg10Children = new HashSet<>(Arrays.asList(f.p72, f.p73, f.p74));
		assertTrue("should have 3 subpackages", f.wg10.getChildPackages().equals(expWg10Children));

		assertTrue("should have no subpackages", f.p7420.getChildPackages().isEmpty());
	}

	@Test
	public final void testGetChildPackages_argName() {
		FixtureForDependenciesAndNamespaces f = new FixtureForDependenciesAndNamespaces(emptyModel);

		UmlPackage p72bis = UmlPackage.basic(f.wg10, "72");

		Set<UmlPackage> expWg10Children = new HashSet<>(Arrays.asList(f.p72, p72bis));
		assertTrue("should have 2 matches (same name)",
				f.wg10.getChildPackages(f.p72.getName()).equals(expWg10Children));
	}

	@Test
	public final void testIsUnderPackage() {
		FixtureForDependenciesAndNamespaces f = new FixtureForDependenciesAndNamespaces(emptyModel);

		assertTrue(f.tp61850.isUnderPackage("mp"));
		assertTrue(f.wg10.isUnderPackage(f.tp61850.getName()));
		assertFalse(f.wg10.isUnderPackage(f.p72.getName()));
	}

	@Test
	public final void testIsInOrUnderPackage() {
		FixtureForDependenciesAndNamespaces f = new FixtureForDependenciesAndNamespaces(emptyModel);

		assertTrue(f.wg10.isInOrUnderPackage(f.mp.getName()));
		assertTrue(f.wg10.isInOrUnderPackage(f.tp61850.getName()));
		assertFalse(f.wg10.isInOrUnderPackage(f.p72.getName()));
	}

	// ==============================================

	@Ignore(value = "until implemented basic tests for others")
	@Test
	public final void testCollectEfferentPackages() {
		fail("Not yet implemented"); // TODO
	}

	@Ignore(value = "until implemented basic tests for others")
	@Test
	public final void testCollectAfferentPackages() {
		fail("Not yet implemented"); // TODO
	}
}
