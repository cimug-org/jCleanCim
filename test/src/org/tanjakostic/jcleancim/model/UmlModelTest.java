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
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.Collection;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.junit.Ignore;
import org.junit.Test;
import org.tanjakostic.jcleancim.common.Nature;
import org.tanjakostic.jcleancim.common.OwningWg;
import org.tanjakostic.jcleancim.util.ApplicationException;

/**
 * Here we test both public and package-private methods - for these laters, it is to ensure that
 * building of the model proceeds correctly. In particular, various add* methods should be used
 * according to restrictions given in their javadoc.
 * <p>
 * For "regular" usage of public API for building the model, see {@link SampleModelFixture}.
 *
 * @author tatjana.kostic@ieee.org
 * @version $Id: UmlModelTest.java 25 2019-11-02 17:21:28Z dev978 $
 */
public class UmlModelTest extends CommonUmlTestBase {

	// ============= Tests ===============

	@Test(expected = NullPointerException.class)
	public final void testCtor() {
		new UmlModel(null);
	}

	@Test
	public final void testGetCfg() {
		assertSame(emptyCfg, emptyModel.getCfg());
	}

	// ----------------------------------------------------------

	@Test
	public final void testGetNullModelPackagesAlwaysAvailable() {
		assertEquals(Nature.values().length, emptyModel.getNullModelPackages().size());
	}

	@Test
	public final void testGetNullModelPackagesOnePerNature() {
		for (Nature nature : Nature.values()) {
			UmlPackage nullMp = emptyModel.getNullModelPackages().get(nature);
			assertNotNull(nullMp);
			assertSame(nature, nullMp.getNature());
		}
	}

	@Test
	public final void testGetNullModelPackagesNotAccessibleThroughRegularAccessors() {
		assertEquals("dedicated getter", Nature.values().length,
				emptyModel.getNullModelPackages().size());
		assertEquals("not accessible", 0, emptyModel.getModelPackages().size());
		assertEquals("not accessible", 0, emptyModel.getPackages().size());
	}

	@Test
	public final void testGetNullClassesAlwaysAvailable() {
		assertEquals(Nature.values().length, emptyModel.getNullClasses().size());
	}

	@Test
	public final void testGetNullModelClassesOnePerNatureAndNullModelPackage() {
		for (Nature nature : Nature.values()) {
			UmlPackage nullMp = emptyModel.getNullModelPackages().get(nature);
			UmlClass nullC = emptyModel.getNullClasses().get(nature);
			assertNotNull(nullC);
			assertSame(nature, nullC.getNature());
			assertSame(nullMp, nullC.getContainingPackage());
		}
	}

	@Test
	public final void testGetNullClassesNotAccessibleThroughRegularAccessors() {
		assertEquals("dedicated getter", Nature.values().length,
				emptyModel.getNullClasses().size());
		assertEquals("not accessible", 0, emptyModel.getClasses().size());
	}

	// ---------------------------

	@Test
	@Ignore(value = "Need to implement the functionality first.")
	public final void testCrossCheck() {
		fail("Not yet implemented"); // TODO
	}

	// ==============================================================

	@Test(expected = NullPointerException.class)
	public final void testAddPackageNull() {
		emptyModel.addPackage(null);
	}

	@Test
	public final void testAddPackageReturnsAdded() {
		UmlPackage mp = UmlPackage.basic(emptyModel, "mp");

		assertSame(mp, emptyModel.addPackage(mp));
	}

	@Test
	public final void testAddPackageAddOneModelPackage() {
		assertEquals(0, emptyModel.getPackages().size());
		assertEquals(0, emptyModel.getModelPackages().size());

		UmlPackage.basic(emptyModel, "mp");

		assertEquals("package ctor added package to model", 1, emptyModel.getPackages().size());
		assertEquals("model packages in sync with all packages", 1,
				emptyModel.getModelPackages().size());
	}

	@Test
	public final void testAddPackageTwoModelPackages() {
		assertEquals(0, emptyModel.getPackages().size());
		assertEquals(0, emptyModel.getModelPackages().size());

		UmlPackage.basic(emptyModel, "mp1");
		UmlPackage.basic(emptyModel, "mp2");

		assertEquals("package ctor added package to model", 2, emptyModel.getPackages().size());
		assertEquals("model packages in sync with all packages", 2,
				emptyModel.getModelPackages().size());
	}

	@Test
	public final void testAddPackageAddOneModelAndOneTopPackage() {
		UmlPackage mp = UmlPackage.basic(emptyModel, "mp");
		UmlPackage.basic(mp, "tp");

		assertEquals("package ctor added package to model", 2, emptyModel.getPackages().size());
		assertEquals("model packages in sync with all packages", 1,
				emptyModel.getModelPackages().size());
	}

	// ---------------------------

	/**
	 * Creates mp -> tp -> {p1, p2}.
	 *
	 * @throws ApplicationException
	 */
	private UmlModel createModelWithFourPackages() throws ApplicationException {
		UmlModel m = emptyModel;
		UmlPackage.basic(m, "mp");
		UmlPackage tp = UmlPackage.basic(m, "tp");
		UmlPackage.basic(tp, "p1");
		UmlPackage.basic(tp, "p2");

		return m;
	}

	@Test(expected = NullPointerException.class)
	public final void testFindPackagesNamesNull() throws ApplicationException {
		UmlModel model4p = createModelWithFourPackages();

		model4p.findPackages(null);
	}

	@Test
	public final void testFindPackagesNamesEmpty() throws ApplicationException {
		UmlModel model4p = createModelWithFourPackages();

		ArrayList<String> names = new ArrayList<String>();
		assertTrue("list should be empty", model4p.findPackages(names).isEmpty());
	}

	@Test
	public final void testFindPackagesNamesMultipleExisting() throws ApplicationException {
		UmlModel model4p = createModelWithFourPackages();

		List<String> names = AbstractUmlObject.collectNames(model4p.getPackages());
		List<String> existingNames = new ArrayList<String>();
		existingNames.add(names.get(0));
		existingNames.add(names.get(3));

		Collection<UmlPackage> result = model4p.findPackages(existingNames);

		List<String> returnedNames = AbstractUmlObject.collectNames(result);

		assertEquals(2, result.size());
		assertEquals(existingNames, returnedNames);
	}

	@Test
	public final void testFindPackagesNamesMultipleExistingAndInexisting()
			throws ApplicationException {
		UmlModel model4p = createModelWithFourPackages();

		List<String> names = AbstractUmlObject.collectNames(model4p.getPackages());
		List<String> argNames = new ArrayList<String>();
		argNames.add(names.get(0));
		String inexisting = "dummy";
		assertFalse(argNames.contains(inexisting));
		argNames.add(inexisting);
		argNames.add(names.get(3));

		Collection<UmlPackage> result = model4p.findPackages(argNames);

		List<String> returnedNames = AbstractUmlObject.collectNames(result);

		assertEquals(2, result.size());
		assertFalse("should not contain inexisting", returnedNames.contains(inexisting));
	}

	// ==============================================================

	@Test(expected = NullPointerException.class)
	public final void testAddClasNull() {
		emptyModel.addClass(null);
	}

	@Test
	public final void testAddClassReturnsAdded() {
		UmlClass c = UmlClass.basic(UmlPackage.basic(emptyModel, "mp"), "c", "");

		assertSame(c, emptyModel.addClass(c));
	}

	@Test
	public final void testAddClassAddOne() {
		UmlClass.basic(UmlPackage.basic(emptyModel, "mp"), "c", "");

		assertEquals("class ctor added class to model", 1, emptyModel.getClasses().size());
	}

	// ---------------------------

	@Test(expected = NullPointerException.class)
	public final void testFindClassesNameNull() {
		// add at least one class, to be sure exception thrown from argument being null:
		emptyModel.addClass(UmlClass.basic(UmlPackage.basic(emptyModel, "mp"), "c", ""));

		emptyModel.findClasses(null);
	}

	@Test
	public final void testFindClassesNameDuplicateReturnsList() {
		// 3 packages mp -> tp -> p, each with one class, last with duplicate name
		UmlPackage mp = UmlPackage.basic(emptyModel, "mp");
		UmlClass mc = emptyModel.addClass(UmlClass.basic(mp, "cName", ""));
		UmlPackage tp = emptyModel.addPackage(UmlPackage.basic(mp, "tp"));
		emptyModel.addClass(UmlClass.basic(tp, "tc", ""));
		UmlPackage p = emptyModel.addPackage(UmlPackage.basic(tp, "p"));
		UmlClass c = emptyModel.addClass(UmlClass.basic(p, "cName", ""));

		Set<UmlClass> expecteds = new HashSet<UmlClass>();
		expecteds.add(mc);
		expecteds.add(c);

		assertEquals(expecteds, emptyModel.findClasses("cName"));
	}

	// ---------------------------

	@Test
	@Ignore(value = "Currently used for logging - prio 3.")
	public final void testFindClassesFull() {
		fail("Not yet implemented"); // TODO
	}

	// ---------------------------

	@Test
	@Ignore(value = "Currently used for logging - prio 3.")
	public final void testFindClassesWithConstraints() {
		fail("Not yet implemented"); // TODO
	}

	// ==============================================================

	@Test(expected = NullPointerException.class)
	public final void testAddAttributeNull() {
		emptyModel.addAttribute(null);
	}

	@Test
	public final void testAddAttributeReturnsAdded() {
		UmlPackage mp = UmlPackage.basic(emptyModel, "mp");
		UmlClass attrType = UmlClass.basic(mp, "attrType", "");
		UmlClass containing = UmlClass.basic(mp, "containing", "");
		UmlAttribute a = UmlAttribute.basic(containing, attrType, "attr");

		assertSame(a, emptyModel.addAttribute(a));
	}

	@Test
	public final void testAddAttributeNotCalledFromAttributeCtor() {
		UmlPackage mp = UmlPackage.basic(emptyModel, "mp");
		UmlClass attrType = UmlClass.basic(mp, "attrType", "");
		UmlClass containing = UmlClass.basic(mp, "containing", "");

		UmlAttribute.basic(containing, attrType, "attr");

		assertTrue("attr ctor has no side effects", emptyModel.getAttributes().isEmpty());
	}

	@Test
	public final void testAddAttributeCalledFromContainerAddAttribute() {
		UmlPackage mp = UmlPackage.basic(emptyModel, "mp");
		UmlClass attrType = UmlClass.basic(mp, "attrType", "");
		UmlClass containing = UmlClass.basic(mp, "containing", "");

		containing.addAttribute(attrType, "attr", "");

		assertEquals("container added attribute to model", 1, emptyModel.getAttributes().size());
	}

	// ---------------------------

	@Test
	@Ignore(value = "Need to test package, class and attribute first - prio 1.")
	public final void testFindAttributesRetListPresenceConditionLiterals() {
		fail("Not yet implemented"); // TODO
	}

	// ---------------------------

	@Test
	@Ignore(value = "Need to test package, class and attribute first - prio 1.")
	public final void testFindAttributesRetList() {
		fail("Not yet implemented"); // TODO
	}

	// ---------------------------

	@Test
	@Ignore(value = "Need to test package, class and attribute first - prio 1.")
	public final void testFindAttributesRetMap() {
		fail("Not yet implemented"); // TODO
	}

	// ---------------------------

	@Test
	@Ignore(value = "Need to test attribute and constraint first - prio 1.")
	public final void testFindAttributesWithConstraints() {
		fail("Not yet implemented"); // TODO
	}

	// ---------------------------

	@Test
	@Ignore(value = "Need to test attribute first - prio 1.")
	public final void testFindMultivaluedAttributes() {
		fail("Not yet implemented"); // TODO
	}

	// ==============================================================

	@Test(expected = NullPointerException.class)
	public final void testAddOperationNull() {
		emptyModel.addOperation(null);
	}

	@Test
	public final void testAddOperationReturnsAdded() {
		UmlPackage mp = UmlPackage.basic(emptyModel, "mp");
		UmlClass containing = UmlClass.basic(mp, "containing", "");
		UmlOperation op = UmlOperation.basic(containing, null, "op");

		assertSame(op, emptyModel.addOperation(op));
	}

	@Test
	public final void testAddOperationNotCalledFromOperationCtor() {
		UmlPackage mp = UmlPackage.basic(emptyModel, "mp");
		UmlClass containing = UmlClass.basic(mp, "containing", "");

		UmlOperation.basic(containing, null, "op");

		assertTrue("operation ctor has no side effects", emptyModel.getOperations().isEmpty());
	}

	@Test
	public final void testAddOperationCalledFromContainerAddOperation() {
		UmlPackage mp = UmlPackage.basic(emptyModel, "mp");
		UmlClass containing = UmlClass.basic(mp, "containing", "");

		containing.addOperation(null, "op");

		assertEquals("container added operation to model", 1, emptyModel.getOperations().size());
	}

	// ==============================================================

	@Test(expected = NullPointerException.class)
	public final void testAddAssociationNull() {
		emptyModel.addAssociation(null);
	}

	@Test
	public final void testAddAssociationReturnsAdded() {
		UmlPackage mp = UmlPackage.basic(emptyModel, "mp");
		UmlClass c1 = UmlClass.basic(mp, "c1", "");
		UmlAssociationEnd end1 = UmlAssociationEnd.basic(c1, "End1");
		UmlAssociationEnd end2 = UmlAssociationEnd.basic(c1, "End2");

		UmlAssociation a = UmlAssociation.basic(end1, end2);

		assertSame(a, emptyModel.addAssociation(a));
	}

	@Test
	public final void testAddAssociationNotCalledFromAssociationCtor() {
		UmlPackage mp = UmlPackage.basic(emptyModel, "mp");
		UmlClass c1 = UmlClass.basic(mp, "c1", "");
		UmlAssociationEnd end1 = UmlAssociationEnd.basic(c1, "End1");
		UmlAssociationEnd end2 = UmlAssociationEnd.basic(c1, "End2");

		UmlAssociation.basic(end1, end2);

		assertTrue("association ctor has no side effects", emptyModel.getAssociations().isEmpty());
	}

	@Test
	public final void testAddAssociationCalledFromSourceAddAssociation() {
		UmlPackage mp = UmlPackage.basic(emptyModel, "mp");
		UmlClass c1 = UmlClass.basic(mp, "c1", "");
		UmlClass c2 = UmlClass.basic(mp, "c2", "");
		addAssociation(c1, c2);

		assertEquals("source added association to model", 1, emptyModel.getAssociations().size());
	}

	// ---------------------------

	@Test
	@Ignore(value = "Currently used for logging - prio 3.")
	public final void testFindAssociations() {
		fail("Not yet implemented"); // TODO
	}

	// ---------------------------

	@Test
	@Ignore(value = "Currently used for logging - prio 3.")
	public final void testFindCimNoncimAssociations() {
		fail("Not yet implemented"); // TODO
	}

	// ==============================================================

	@Test(expected = NullPointerException.class)
	public final void testAddDependencyNull() {
		emptyModel.addDependency(null);
	}

	@Test
	public final void testAddDependencyReturnsAdded() {
		UmlPackage mp = UmlPackage.basic(emptyModel, "mp");
		UmlPackage mp2 = UmlPackage.basic(emptyModel, "mp2");

		UmlDependency d = UmlDependency.basic(mp, mp2);

		assertSame(d, emptyModel.addDependency(d));
	}

	@Test
	public final void testAddDependencyNotCalledFromDependencyCtor() {
		UmlPackage mp = UmlPackage.basic(emptyModel, "mp");
		UmlPackage mp2 = UmlPackage.basic(emptyModel, "mp2");

		UmlDependency.basic(mp, mp2);

		assertTrue("dependency ctor has no side effects", emptyModel.getDependencies().isEmpty());
	}

	@Test
	public final void testAddDependencyCalledFromSourceAddDependency() {
		UmlPackage mp = UmlPackage.basic(emptyModel, "mp");
		UmlClass c = UmlClass.basic(mp, "c", "");
		UmlPackage mp2 = UmlPackage.basic(emptyModel, "mp2");
		UmlClass c2 = UmlClass.basic(mp, "c2", "");

		mp.addDependency(mp2);
		c.addDependency(c2);

		assertEquals("source added dependencies to model", 2, emptyModel.getDependencies().size());
	}

	// ==============================================================

	@Test(expected = NullPointerException.class)
	public final void testAddDiagramNull() {
		emptyModel.addDiagram(null);
	}

	@Test
	public final void testAddDiagramReturnsAdded() {
		UmlPackage mp = UmlPackage.basic(emptyModel, "mp");
		UmlDiagram md = UmlDiagram.basic(mp, "md");

		assertSame(md, emptyModel.addDiagram(md));
	}

	@Test
	public final void testAddDiagramNotCalledFromDiagramCtor() {
		UmlPackage mp = UmlPackage.basic(emptyModel, "mp");
		UmlClass c = UmlClass.basic(mp, "c", "");

		UmlDiagram.basic(mp, "md");
		UmlDiagram.basic(c, "cd");

		assertTrue("diagram ctor has no side effects", emptyModel.getDiagrams().isEmpty());
	}

	@Test
	public final void testAddDiagramCalledFromContainerAddDiagram() {
		UmlPackage mp = UmlPackage.basic(emptyModel, "mp");
		UmlClass c = UmlClass.basic(mp, "c", "");

		mp.addDiagram("mpd");
		c.addDiagram("cd");

		assertEquals("container added dependencies to model", 2, emptyModel.getDiagrams().size());
	}

	// ---------------------------

	/** Adds mp -> {tp -> {tpd, c1-> c1d, c2 -> c2d}}, {tp2 -> {tp2d, c3-> c3d, c4 -> c4d}}. */
	private static UmlModel populateWithTwoPackageAndFourClassDiagrams(UmlModel model) {
		UmlPackage mp = UmlPackage.basic(model, "mp");

		UmlPackage tp = UmlPackage.basic(mp, "tp");
		UmlClass c1 = UmlClass.basic(tp, "c1", "");
		UmlClass c2 = UmlClass.basic(tp, "c2", "");
		tp.addDiagram("tpd");
		c1.addDiagram("c1d");
		c2.addDiagram("c2d");

		UmlPackage tp2 = UmlPackage.basic(mp, "tp2");
		UmlClass c3 = UmlClass.basic(tp2, "c3", "");
		UmlClass c4 = UmlClass.basic(tp2, "c4", "");
		tp2.addDiagram("tp2d");
		c3.addDiagram("c3d");
		c4.addDiagram("c4d");

		return model;
	}

	@Test
	public final void testFindDiagramsExistingOnOnePackage() {
		populateWithTwoPackageAndFourClassDiagrams(emptyModel);
		assertEquals(6, emptyModel.getDiagrams().size());

		List<UmlPackage> packages = new ArrayList<UmlPackage>(emptyModel.getPackages());
		Set<UmlDiagram> expected = new LinkedHashSet<UmlDiagram>(packages.get(1).getDiagrams());

		assertEquals(expected, emptyModel.findDiagrams("tp", "tpd", true, false));
		assertTrue("no class diagram on package",
				emptyModel.findDiagrams("tp", "tpd", false, true).isEmpty());
	}

	@Test
	public final void testFindDiagramsExistingOnOneClass() {
		populateWithTwoPackageAndFourClassDiagrams(emptyModel);
		assertEquals(6, emptyModel.getDiagrams().size());

		List<UmlClass> classes = new ArrayList<UmlClass>(emptyModel.getClasses());
		Set<UmlDiagram> expected = new LinkedHashSet<UmlDiagram>(classes.get(0).getDiagrams());

		assertEquals(expected, emptyModel.findDiagrams("c1", "c1d", false, true));
		assertTrue("no package diagram on class",
				emptyModel.findDiagrams("c1", "c1d", true, false).isEmpty());
	}

	@Test
	public final void testFindDiagramsExistingOnTwoPackages() {
		populateWithTwoPackageAndFourClassDiagrams(emptyModel);
		assertEquals(6, emptyModel.getDiagrams().size());

		// add a package with an existing name, and the diagram with existing container.name
		List<UmlPackage> packages = new ArrayList<UmlPackage>(emptyModel.getPackages());
		UmlPackage toMimicNameAndDiaPackage = packages.get(1);
		UmlDiagram toMimicDiagram = toMimicNameAndDiaPackage.getDiagrams().iterator().next();
		UmlPackage toHostNewPackage = packages.get(2);
		UmlPackage stinkerPackage = UmlPackage.basic(toHostNewPackage,
				toMimicNameAndDiaPackage.getName());
		UmlDiagram stinkerDiagram = stinkerPackage.addDiagram(toMimicDiagram.getName());

		Set<UmlDiagram> expected = new LinkedHashSet<UmlDiagram>();
		expected.add(toMimicDiagram);
		expected.add(stinkerDiagram);

		assertEquals(expected, emptyModel.findDiagrams("tp", "tpd", true, true));
	}

	@Test
	public final void testFindDiagramsExistingOnOnePackageAndOneClass() {
		populateWithTwoPackageAndFourClassDiagrams(emptyModel);
		assertEquals(6, emptyModel.getDiagrams().size());

		// add a class with the name of existing package, and the diagram with existing
		// container.name
		List<UmlPackage> packages = new ArrayList<UmlPackage>(emptyModel.getPackages());
		UmlPackage toMimicNameAndDiaPackage = packages.get(1);
		UmlDiagram toMimicDiagram = toMimicNameAndDiaPackage.getDiagrams().iterator().next();
		UmlPackage toHostNewPackage = packages.get(2);
		UmlClass stinkerClass = UmlClass.basic(toHostNewPackage, toMimicNameAndDiaPackage.getName(),
				"");
		UmlDiagram stinkerDiagram = stinkerClass.addDiagram(toMimicDiagram.getName());

		Set<UmlDiagram> expected = new LinkedHashSet<UmlDiagram>();
		expected.add(toMimicDiagram);
		expected.add(stinkerDiagram);

		assertEquals(expected, emptyModel.findDiagrams("tp", "tpd", true, true));
	}

	// ==============================================================

	@Test
	public final void testGetModelNamesWithNature() throws ApplicationException {
		UmlModel m = SampleModelFixture.create();

		Collection<UmlPackage> mPackages = m.getModelPackages();
		assertEquals("we have also extensions", 4, mPackages.size());

		String result = m.getModelNamesWithNature();
		assertTrue("includes CIM nature", result.contains("TC57CIM "));
		assertTrue("includes CIM nature", result.contains(Nature.CIM.toString()));
		assertTrue("includes IEC61850 nature", result.contains("IEC61850Domain"));
		assertTrue("includes IEC61850 nature", result.contains(Nature.IEC61850.toString()));
		assertFalse("last comma trimmed", result.trim().endsWith(","));
	}

	// ---------------------------

	@Test(expected = NullPointerException.class)
	public final void testGetModelPackagesNullNature() throws ApplicationException {
		UmlModel m = SampleModelFixture.create();

		m.getModelPackages(null);
	}

	@Test
	public final void testGetModelPackagesEmptyNatures() throws ApplicationException {
		UmlModel m = SampleModelFixture.create();

		Collection<UmlPackage> mPackages = m.getModelPackages(EnumSet.noneOf(Nature.class));
		assertTrue("should be empty", mPackages.isEmpty());
	}

	@Test
	public final void testGetModelPackagesOneNature() throws ApplicationException {
		UmlModel m = SampleModelFixture.create();

		Collection<UmlPackage> mPackages = m.getModelPackages(EnumSet.of(Nature.CIM));
		assertEquals(2, mPackages.size());
	}

	@Test
	public final void testGetModelPackagesTwoNatures() throws ApplicationException {
		UmlModel m = SampleModelFixture.create();

		Collection<UmlPackage> mPackages = m
				.getModelPackages(EnumSet.of(Nature.CIM, Nature.IEC61850));
		assertEquals(4, mPackages.size());
	}

	// ---------------------------

	@Test(expected = NullPointerException.class)
	public final void testGetTopPackagesNullOwner() throws ApplicationException {
		UmlModel m = SampleModelFixture.create();
		m.getTopPackages(null);
	}

	@Test
	public final void testGetTopPackagesEmptyOwners() throws ApplicationException {
		UmlModel m = SampleModelFixture.create();

		Collection<UmlPackage> tPackages = m.getTopPackages(EnumSet.noneOf(OwningWg.class));
		assertTrue("should be empty", tPackages.isEmpty());
	}

	@Test
	public final void testGetTopPackagesOneOwner() throws ApplicationException {
		UmlModel m = SampleModelFixture.create();

		Collection<UmlPackage> pPackages = m.getTopPackages(EnumSet.of(OwningWg.WG14));
		assertEquals(1, pPackages.size());
	}

	@Test
	public final void testGetTopPackagesTwoOwners() throws ApplicationException {
		UmlModel m = SampleModelFixture.create();

		System.out.println(m.toString());
		Collection<UmlPackage> pPackages = m
				.getTopPackages(EnumSet.of(OwningWg.WG14, OwningWg.OTHER_IEC61850));
		assertEquals(2, pPackages.size());
	}

	// ---------------------------

	@Test(expected = NullPointerException.class)
	public final void testGetVersionInfosNullOwner() throws ApplicationException {
		UmlModel m = SampleModelFixture.create();
		m.getVersionInfos(null);
	}

	@Test
	public final void testGetVersionInfosEmptyOwners() throws ApplicationException {
		UmlModel m = SampleModelFixture.create();

		Collection<VersionInfo> vInfos = m.getVersionInfos(EnumSet.noneOf(OwningWg.class));
		assertTrue("should be empty", vInfos.isEmpty());
	}

	@Test
	public final void testGetVersionInfosOneOwner() throws ApplicationException {
		UmlModel m = SampleModelFixture.create();

		Collection<VersionInfo> vInfos = m.getVersionInfos(EnumSet.of(OwningWg.WG14));
		assertEquals(1, vInfos.size());
	}

	@Test
	public final void testGetVersionInfosTwoOwners() throws ApplicationException {
		UmlModel m = SampleModelFixture.create();

		Collection<VersionInfo> vInfos = m
				.getVersionInfos(EnumSet.of(OwningWg.WG14, OwningWg.WG13));
		assertEquals(2, vInfos.size());
	}
}
