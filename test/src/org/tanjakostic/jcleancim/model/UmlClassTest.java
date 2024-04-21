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
import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import org.junit.Ignore;
import org.junit.Test;
import org.tanjakostic.jcleancim.common.Nature;
import org.tanjakostic.jcleancim.model.UmlPackage.Data;
import org.tanjakostic.jcleancim.util.ApplicationException;

/**
 * @author tatjana.kostic@ieee.org
 * @version $Id: UmlClassTest.java 27 2019-11-23 16:29:38Z dev978 $
 */
public class UmlClassTest extends UmlStructureTestBase {

	// ===== org.tanjakostic.jcleancim.model.UmlStructureTestBase =====

	@Override
	protected UmlStructure createInstance(UmlObjectData objData, UmlStructure.Data data)
			throws Exception {
		return createInstance(emptyModel, objData, new Data(data));
	}

	@Override
	protected UmlStructure createInstance(UmlModel model, UmlObjectData objData,
			UmlStructure.Data data) {
		UmlPackage mp = UmlPackage.basic(model, "mp");
		UmlClass.Data cData = new UmlClass.Data(data, false, false, false, false, false, false,
				false, false);
		return new UmlClass(mp, objData, cData);
	}

	@Override
	protected UmlStructure createInstanceOfOtherType(UmlModel model) throws Exception {
		return (model != null) ? UmlPackage.basic(model, "mp") : UmlPackage.basic(emptyModel, "mp");
	}

	// ============= Tests ===============

	@Ignore(value = "until implemented basic tests for others")
	@Test
	public final void testCollectDependencyEfferentClasses() {
		fail("Not yet implemented"); // TODO
	}

	// ----------------------------------

	@SuppressWarnings("unused")
	@Test(expected = NullPointerException.class)
	public final void testCtorWithStereotypePackageNull() {
		new UmlClass(null,
				new UmlObjectData("ActivePower", new UmlStereotype(UmlStereotype.CIMDATATYPE)),
				UmlClass.Data.empty());
	}

	@SuppressWarnings("unused")
	@Test(expected = NullPointerException.class)
	public final void testCtorWithStereotypeODataNull() throws ApplicationException {
		UmlPackage mp = UmlPackage.basic(emptyModel, "mp");
		new UmlClass(mp, null, UmlClass.Data.empty());
	}

	@SuppressWarnings("unused")
	@Test(expected = NullPointerException.class)
	public final void testCtorWithStereotypeDataNull() throws ApplicationException {
		UmlPackage mp = UmlPackage.basic(emptyModel, "mp");
		new UmlClass(mp,
				new UmlObjectData("ActivePower", new UmlStereotype(UmlStereotype.CIMDATATYPE)),
				null);
	}

	@Test
	public final void testUmlClassDataAccessors() {
		UmlPackage mp = UmlPackage.basic(emptyModel, "mp");

		UmlClass.Data data = new UmlClass.Data(true, true, true, true, true, true, false, true,
				false);
		UmlClass c = new UmlClass(mp,
				new UmlObjectData("c", new UmlStereotype(UmlStereotype.OLD_DATATYPE)), data);

		assertTrue("should be self-dependent", c.isSelfDependent());
		assertTrue("should be abstract", c.isAbstract());
		assertTrue("should be with EA persistent property set", c.isEaPersistentPropSet());
		assertTrue("should be with EA leaf property set", c.isEaLeafPropSet());
		assertTrue("should be with EA root property set", c.isEaRootPropSet());
		assertTrue("should be EA interface", c.isEaInterface());
		assertFalse("should not be association class", c.isAssociationClass());
		assertTrue("should be self-inherited", c.isSelfInherited());
		assertFalse("should not be EA enumeration", c.isEnumeratedType());

		assertTrue("should have been with old CIM datatype stereotype",
				c.isWithOldDatatypeStereotype());
	}

	// ----------------------------------

	@Test(expected = NullPointerException.class)
	public final void testCtorWithSupersPackageNull() {
		new UmlClass(null, new ArrayList<UmlClass>(),
				new UmlObjectData("ActivePower", new UmlStereotype(UmlStereotype.CIMDATATYPE)),
				UmlClass.Data.empty());
	}

	@Test(expected = NullPointerException.class)
	public final void testCtorWithSupersSupersNull() throws ApplicationException {
		UmlPackage mp = UmlPackage.basic(emptyModel, "mp");
		new UmlClass(mp, null,
				new UmlObjectData("ActivePower", new UmlStereotype(UmlStereotype.CIMDATATYPE)),
				UmlClass.Data.empty());
	}

	@Test(expected = NullPointerException.class)
	public final void testCtorWithSupersODataNull() throws ApplicationException {
		UmlPackage mp = UmlPackage.basic(emptyModel, "mp");
		new UmlClass(mp, new ArrayList<UmlClass>(), null, UmlClass.Data.empty());
	}

	@Test(expected = NullPointerException.class)
	public final void testCtorWithSupersDataNull() throws ApplicationException {
		UmlPackage mp = UmlPackage.basic(emptyModel, "mp");
		new UmlClass(mp, new ArrayList<UmlClass>(),
				new UmlObjectData("ActivePower", new UmlStereotype(UmlStereotype.CIMDATATYPE)),
				null);
	}

	/**
	 * rootClass<--tpChi<--{[INF]tpGrandChi<--pGrandGrandChi, pGrandChi}
	 * <p>
	 * rootClass2<-pGrandChi
	 */
	private UmlModel createModelWithInheritanceHierarchy() {
		UmlPackage mp = UmlPackage.basic(emptyModel, "mp");
		UmlClass rootClass = UmlClass.basic(mp, "rootClass", "");

		UmlPackage tp = UmlPackage.basic(mp, "tp");
		UmlClass tpChi = UmlClass.basic(tp, rootClass, "tpChi");
		UmlClass tpGrandChi = UmlClass.basic(tp, tpChi, "tpGrandChi", UmlStereotype.INFORMATIVE);
		UmlClass pGrandGrandChi = UmlClass.basic(tp, tpGrandChi, "pGrandGrandChi");

		UmlPackage p = UmlPackage.basic(tp, "p");
		UmlClass rootClass2 = UmlClass.basic(p, "rootClass2", "");

		// multiple inheritance for pGrandChi
		Collection<UmlClass> supers = new LinkedHashSet<UmlClass>();
		supers.add(tpChi);
		supers.add(rootClass2);
		UmlClass pGrandChi = UmlClass.basic(p, supers, "pGrandChi");

		return emptyModel;
	}

	@Test
	public final void testCtorWithSupersChildrenLinked() {
		UmlModel model = createModelWithInheritanceHierarchy();
		UmlClass rootClass = new ArrayList<UmlClass>(model.findClasses("rootClass")).get(0);
		UmlClass rootClass2 = new ArrayList<UmlClass>(model.findClasses("rootClass2")).get(0);
		UmlClass tpChi = new ArrayList<UmlClass>(model.findClasses("tpChi")).get(0);
		UmlClass tpGrandChi = new ArrayList<UmlClass>(model.findClasses("tpGrandChi")).get(0);
		UmlClass pGrandChi = new ArrayList<UmlClass>(model.findClasses("pGrandChi")).get(0);
		UmlClass pGrandGrandChi = new ArrayList<UmlClass>(model.findClasses("pGrandGrandChi"))
				.get(0);

		// rootClass
		Set<UmlClass> expSupers = new HashSet<UmlClass>();
		Set<UmlClass> expSubs = new HashSet<UmlClass>(Arrays.asList(new UmlClass[] { tpChi }));
		assertEquals(expSupers, new HashSet<UmlClass>(rootClass.getSuperclasses()));
		assertEquals(expSubs, new HashSet<UmlClass>(rootClass.getSubclasses()));

		// tpChi
		expSupers = new HashSet<UmlClass>(Arrays.asList(new UmlClass[] { rootClass }));
		expSubs = new HashSet<UmlClass>(Arrays.asList(new UmlClass[] { tpGrandChi, pGrandChi }));
		assertEquals(expSupers, new HashSet<UmlClass>(tpChi.getSuperclasses()));
		assertEquals(expSubs, new HashSet<UmlClass>(tpChi.getSubclasses()));

		// tpGrandChi
		expSupers = new HashSet<UmlClass>(Arrays.asList(new UmlClass[] { tpChi }));
		expSubs = new HashSet<UmlClass>(Arrays.asList(new UmlClass[] { pGrandGrandChi }));
		assertEquals(expSupers, new HashSet<UmlClass>(tpGrandChi.getSuperclasses()));
		assertEquals(expSubs, new HashSet<UmlClass>(tpGrandChi.getSubclasses()));

		// pGrandChi
		expSupers = new HashSet<UmlClass>(Arrays.asList(new UmlClass[] { tpChi, rootClass2 }));
		expSubs = new HashSet<UmlClass>();
		assertEquals(expSupers, new HashSet<UmlClass>(pGrandChi.getSuperclasses()));
		assertEquals(expSubs, new HashSet<UmlClass>(pGrandChi.getSubclasses()));

		// pGrandGrandChi
		expSupers = new HashSet<UmlClass>(Arrays.asList(new UmlClass[] { tpGrandChi }));
		expSubs = new HashSet<UmlClass>();
		assertEquals(expSupers, new HashSet<UmlClass>(pGrandGrandChi.getSuperclasses()));
		assertEquals(expSubs, new HashSet<UmlClass>(pGrandGrandChi.getSubclasses()));
	}

	// ----------------------------------
	// we don't test constructor for null-classes (model calls them internally),
	// but we get it from the model

	@Test
	public final void testNullClassAccessorsJustAfterCreation() {
		Nature nature = Nature.IEC61850;
		UmlClass nullClass = emptyModel.getNullClasses().get(nature);

		assertSame(emptyModel.getNullModelPackages().get(nature), nullClass.getContainingPackage());
		assertEquals(UmlClass.Iec61850Kind.NULL_61850, nullClass.getKind());
		assertEquals(0, nullClass.getSubclasses().size());
		assertEquals(0, nullClass.getSuperclasses().size());
	}

	@Test
	public final void testNullClassAccessorsAfterCompleteModelBuilt() throws ApplicationException {
		UmlModel sampleModel = SampleModelFixture.create();

		for (UmlClass nullClass : sampleModel.getNullClasses().values()) {
			assertFalse("should not be version class", nullClass.isVersionClass());
			assertTrue("should have no skipped items", nullClass.getSkippedUmlItems().isEmpty());
			assertTrue("should have no dependencies as source",
					nullClass.getDependenciesAsSource().isEmpty());
			assertTrue("should have no dependencies as target",
					nullClass.getDependenciesAsTarget().isEmpty());
			assertTrue("should have no diagrams", nullClass.getDiagrams().isEmpty());
			assertTrue("should have no attributes", nullClass.getAttributes().isEmpty());
			assertTrue("should have no associations", nullClass.getAssociations().isEmpty());
			assertTrue("should have no constraints", nullClass.getConstraints().isEmpty());
			assertTrue("should have no operations", nullClass.getOperations().isEmpty());
		}
	}

	// ----------------------------------

	@Test
	public final void testGetAllSuperclassesFlattened() {
		UmlModel model = createModelWithInheritanceHierarchy();
		UmlClass rootClass = new ArrayList<UmlClass>(model.findClasses("rootClass")).get(0);
		UmlClass tpChi = new ArrayList<UmlClass>(model.findClasses("tpChi")).get(0);
		UmlClass tpGrandChi = new ArrayList<UmlClass>(model.findClasses("tpGrandChi")).get(0);
		UmlClass pGrandGrandChi = new ArrayList<UmlClass>(model.findClasses("pGrandGrandChi"))
				.get(0);

		/**
		 * rootClass<--tpChi<--{[INF]tpGrandChi<--pGrandGrandChi, pGrandChi}
		 * <p>
		 * rootClass2<-pGrandChi
		 */

		assertTrue("tpGrandChi should be INF", tpGrandChi.isInformative());

		// rootClass
		List<UmlClass> expSupersChain = new ArrayList<UmlClass>();
		assertEquals(expSupersChain,
				new ArrayList<UmlClass>(rootClass.getAllSuperclassesFlattened(false)));

		// middle-man
		expSupersChain = Arrays.asList(new UmlClass[] { tpChi, rootClass });
		assertEquals(expSupersChain,
				new ArrayList<UmlClass>(tpGrandChi.getAllSuperclassesFlattened(false)));

		// leaf
		expSupersChain = Arrays.asList(new UmlClass[] { tpGrandChi, tpChi, rootClass });
		assertEquals(expSupersChain,
				new ArrayList<UmlClass>(pGrandGrandChi.getAllSuperclassesFlattened(false)));

		// leaf with intermediate INF superclass tpGrandChi - should be excluded
		expSupersChain = Arrays.asList(new UmlClass[] { tpChi, rootClass });
		assertEquals(expSupersChain,
				new ArrayList<UmlClass>(pGrandGrandChi.getAllSuperclassesFlattened(true)));
	}

	// ----------------------------------

	@Test
	public final void testHasSuperclassAndIsOrHasSuperclass() {
		UmlModel model = createModelWithInheritanceHierarchy();
		UmlClass rootClass = new ArrayList<UmlClass>(model.findClasses("rootClass")).get(0);
		UmlClass tpChi = new ArrayList<UmlClass>(model.findClasses("tpChi")).get(0);
		UmlClass tpGrandChi = new ArrayList<UmlClass>(model.findClasses("tpGrandChi")).get(0);

		assertTrue("includes first super...", tpGrandChi.hasSuperclass(tpChi.getName()));
		assertTrue("and super's super...", tpGrandChi.hasSuperclass(rootClass.getName()));

		assertTrue("includes self...", tpGrandChi.isOrHasSuperclass(tpGrandChi.getName()));
		assertTrue("and first super...", tpGrandChi.isOrHasSuperclass(tpChi.getName()));
		assertTrue("and super's super...", tpGrandChi.isOrHasSuperclass(rootClass.getName()));
	}

	// ----------------------------------

	@Ignore(value = "until implemented basic tests for others")
	@Test
	public final void testGetContainingPackage() {
		fail("Not yet implemented"); // TODO
	}

	@Ignore(value = "until implemented basic tests for others")
	@Test
	public final void testGetOwner() {
		fail("Not yet implemented"); // TODO
	}

	@Ignore(value = "until implemented basic tests for others")
	@Test
	public final void testGetNature() {
		fail("Not yet implemented"); // TODO
	}

	@Ignore(value = "until implemented basic tests for others")
	@Test
	public final void testIsInformative() {
		fail("Not yet implemented"); // TODO
	}

	@Ignore(value = "until implemented basic tests for others")
	@Test
	public final void testGetQualifiedName() {
		fail("Not yet implemented"); // TODO
	}

	@Test(expected = NullPointerException.class)
	public final void testGetKindsNatureNull() {
		UmlClass.getKinds(null);
	}

	@Test
	public final void testGetKinds() {
		assertFalse("should not be empty", UmlClass.getKinds(Nature.CIM).isEmpty());
	}

	// ==================================

	@Ignore(value = "until implemented basic tests for others")
	@Test
	public final void testGetKnownTagNames() {
		fail("Not yet implemented"); // TODO
	}

	@Ignore(value = "until implemented basic tests for others")
	@Test
	public final void testGetLnMappingTags() {
		fail("Not yet implemented"); // TODO
	}

	// ==================================

	@Ignore(value = "until implemented basic tests for others")
	@Test
	public final void testIsVersionClass() {
		fail("Not yet implemented"); // TODO
	}

	@Ignore(value = "until implemented basic tests for others")
	@Test
	public final void testGetRsName() {
		fail("Not yet implemented"); // TODO
	}

	@Ignore(value = "until implemented basic tests for others")
	@Test
	public final void testGetIeeeRef() {
		fail("Not yet implemented"); // TODO
	}

	@Ignore(value = "until implemented basic tests for others")
	@Test
	public final void testGetIecRef() {
		fail("Not yet implemented"); // TODO
	}

	@Ignore(value = "until implemented basic tests for others")
	@Test
	public final void testNeedsAlias() {
		fail("Not yet implemented"); // TODO
	}

	@Ignore(value = "until implemented basic tests for others")
	@Test
	public final void testNeedsTags() {
		fail("Not yet implemented"); // TODO
	}

	// ==================================

	@Ignore(value = "until implemented basic tests for others")
	@Test
	public final void testGetKind() {
		fail("Not yet implemented"); // TODO
	}

	@Ignore(value = "until implemented basic tests for others")
	@Test
	public final void testIsPrimitive() {
		fail("Not yet implemented"); // TODO
	}

	@Test
	public final void testIsEnumerationReturnsFalseWhenEnumStereotypeNotAlone()
			throws ApplicationException {
		UmlPackage mp = create61850Package("mp6");
		UmlClass presCondEnum = UmlClass.basic(mp, "", UmlClass.Iec61850Kind.COND_ENUM.getValue());
		assertFalse("should not be 'simple' enumeration, because has 2 stereotypes",
				presCondEnum.isEnumeration());
	}

	@Ignore(value = "until implemented basic tests for others")
	@Test
	public final void testIsDatatype() {
		fail("Not yet implemented"); // TODO
	}

	@Ignore(value = "until implemented basic tests for others")
	@Test
	public final void testIsCompound() {
		fail("Not yet implemented"); // TODO
	}

	@Ignore(value = "until implemented basic tests for others")
	@Test
	public final void testIsClass() {
		fail("Not yet implemented"); // TODO
	}

	@Ignore(value = "until implemented basic tests for others")
	@Test
	public final void testIsUsedAsTypeForCimAttributes() {
		fail("Not yet implemented"); // TODO
	}

	@Ignore(value = "until implemented basic tests for others")
	@Test
	public final void testIsInterface() {
		fail("Not yet implemented"); // TODO
	}

	@Ignore(value = "until implemented basic tests for others")
	@Test
	public final void testIsPackedEnumeration() {
		fail("Not yet implemented"); // TODO
	}

	@Ignore(value = "until implemented basic tests for others")
	@Test
	public final void testIsAbbreviationEnumeration() {
		fail("Not yet implemented"); // TODO
	}

	@Test
	public final void testIsConditionEnumeration() throws ApplicationException {
		UmlPackage mp = create61850Package("mp6");
		UmlClass presCondEnum = UmlClass.basic(mp, "", UmlClass.Iec61850Kind.COND_ENUM.getValue());
		assertTrue("should be presence condition, according to stereotype",
				presCondEnum.isConditionEnumeration());
	}

	@Ignore(value = "until implemented basic tests for others")
	@Test
	public final void testIsPacked() {
		fail("Not yet implemented"); // TODO
	}

	@Ignore(value = "until implemented basic tests for others")
	@Test
	public final void testIsBasic() {
		fail("Not yet implemented"); // TODO
	}

	@Ignore(value = "until implemented basic tests for others")
	@Test
	public final void testIsPackedEnumDA() {
		fail("Not yet implemented"); // TODO
	}

	@Ignore(value = "until implemented basic tests for others")
	@Test
	public final void testIsEnumDA() {
		fail("Not yet implemented"); // TODO
	}

	@Ignore(value = "until implemented basic tests for others")
	@Test
	public final void testIsPackedPrimitiveDA() {
		fail("Not yet implemented"); // TODO
	}

	@Ignore(value = "until implemented basic tests for others")
	@Test
	public final void testIsPrimitiveDA() {
		fail("Not yet implemented"); // TODO
	}

	@Ignore(value = "until implemented basic tests for others")
	@Test
	public final void testIsComposedDA() {
		fail("Not yet implemented"); // TODO
	}

	@Ignore(value = "until implemented basic tests for others")
	@Test
	public final void testIsPackedEnumFCDA() {
		fail("Not yet implemented"); // TODO
	}

	@Ignore(value = "until implemented basic tests for others")
	@Test
	public final void testIsEnumFCDA() {
		fail("Not yet implemented"); // TODO
	}

	@Ignore(value = "until implemented basic tests for others")
	@Test
	public final void testIsPrimitiveCDC() {
		fail("Not yet implemented"); // TODO
	}

	@Ignore(value = "until implemented basic tests for others")
	@Test
	public final void testIsEnumCDC() {
		fail("Not yet implemented"); // TODO
	}

	@Ignore(value = "until implemented basic tests for others")
	@Test
	public final void testIsComposedCDC() {
		fail("Not yet implemented"); // TODO
	}

	@Ignore(value = "until implemented basic tests for others")
	@Test
	public final void testIsTransientCDC() {
		fail("Not yet implemented"); // TODO
	}

	@Ignore(value = "until implemented basic tests for others")
	@Test
	public final void testIsAnyDA() {
		fail("Not yet implemented"); // TODO
	}

	@Ignore(value = "until implemented basic tests for others")
	@Test
	public final void testIsAnyFCDA() {
		fail("Not yet implemented"); // TODO
	}

	@Ignore(value = "until implemented basic tests for others")
	@Test
	public final void testIsAnyCDC() {
		fail("Not yet implemented"); // TODO
	}

	@Ignore(value = "until implemented basic tests for others")
	@Test
	public final void testIsLN() {
		fail("Not yet implemented"); // TODO
	}

	@Ignore(value = "until implemented basic tests for others")
	@Test
	public final void testIsFunction() {
		fail("Not yet implemented"); // TODO
	}

	@Ignore(value = "until implemented basic tests for others")
	@Test
	public final void testIsOther() {
		fail("Not yet implemented"); // TODO
	}

	@Ignore(value = "until implemented basic tests for others")
	@Test
	public final void testIsUnknown() {
		fail("Not yet implemented"); // TODO
	}

	@Ignore(value = "until implemented basic tests for others")
	@Test
	public final void testIsUsedAsTypeForIec61850Attributes() {
		fail("Not yet implemented"); // TODO
	}

	@Ignore(value = "until implemented basic tests for others")
	@Test
	public final void testIsEnumeratedType() {
		fail("Not yet implemented"); // TODO
	}

	@Ignore(value = "until implemented basic tests for others")
	@Test
	public final void testIsFrom72() {
		fail("Not yet implemented"); // TODO
	}

	@Ignore(value = "until implemented basic tests for others")
	@Test
	public final void testIsFromMetaModel() {
		fail("Not yet implemented"); // TODO
	}

	// ==================================

	@Ignore(value = "until implemented basic tests for others")
	@Test
	public final void testAddOperationUmlClassUmlObjectDataData() {
		fail("Not yet implemented"); // TODO
	}

	@Ignore(value = "until implemented basic tests for others")
	@Test
	public final void testGetOperations() {
		fail("Not yet implemented"); // TODO
	}

	@Ignore(value = "until implemented basic tests for others")
	@Test
	public final void testGetInheritedOperations() {
		fail("Not yet implemented"); // TODO
	}

	@Ignore(value = "until implemented basic tests for others")
	@Test
	public final void testGetOperationAfferentClasses() {
		fail("Not yet implemented"); // TODO
	}

	@Ignore(value = "until implemented basic tests for others")
	@Test
	public final void testGetOperationEfferentClasses() {
		fail("Not yet implemented"); // TODO
	}

	// ==================================

	@Ignore(value = "until implemented basic tests for others")
	@Test
	public final void testAddConstraint() {
		fail("Not yet implemented"); // TODO
	}

	@Ignore(value = "until implemented basic tests for others")
	@Test
	public final void testFindConstraintsForAttribute() {
		fail("Not yet implemented"); // TODO
	}

	@Ignore(value = "until implemented basic tests for others")
	@Test
	public final void testGetConstraints() {
		fail("Not yet implemented"); // TODO
	}

	// ==================================

	@Ignore(value = "until implemented basic tests for others")
	@Test
	public final void testAddAttribute() {
		fail("Not yet implemented"); // TODO
		// FIXME: ensure to prevent adding two attributes with the same name !
	}

	@Ignore(value = "until implemented basic tests for others")
	@Test
	public final void testGetAttributes() {
		fail("Not yet implemented"); // TODO
	}

	@Ignore(value = "until implemented basic tests for others")
	@Test
	public final void testGetInheritedAttributes() {
		fail("Not yet implemented"); // TODO
	}

	@Ignore(value = "until implemented basic tests for others")
	@Test
	public final void testGetAttributeAfferentClasses() {
		fail("Not yet implemented"); // TODO
	}

	@Ignore(value = "until implemented basic tests for others")
	@Test
	public final void testGetAttributeEfferentClasses() {
		fail("Not yet implemented"); // TODO
	}

	@Ignore(value = "until implemented basic tests for others")
	@Test
	public final void testFindAttributesForName() {
		fail("Not yet implemented"); // TODO
	}

	@Ignore(value = "until implemented basic tests for others")
	@Test
	public final void testFindAttributesForType() {
		fail("Not yet implemented"); // TODO
	}

	@Ignore(value = "until implemented basic tests for others")
	@Test
	public final void testFindAttributesForNameInh() {
		fail("Not yet implemented"); // TODO
		// c.findAttributes(attrName, inh);
	}

	@Ignore(value = "until implemented basic tests for others")
	@Test
	public final void testFindAttributesForTypeInh() {
		fail("Not yet implemented"); // TODO
		// c.findAttributes(attrType, inh);
	}

	@Ignore(value = "until implemented basic tests for others")
	@Test
	public final void testGetAttributeGroups() {
		fail("Not yet implemented"); // TODO
	}

	// ---------------------------

	/** Returns an empty enum class. */
	static public UmlClass createEnumClass(UmlModel model) {
		UmlPackage mp = UmlPackage.basic(model, "mp");
		return UmlClass.basic(mp, "EnumClass", UmlStereotype.ENUMERATION);
	}

	@Test
	public final void testFindAttributesPerInitialValueSomeWithoutInitVal() {
		UmlClass enumClass = createEnumClass(emptyModel);
		UmlAttribute lit1 = enumClass.addLiteral("lit1", "7");
		UmlAttribute lit2 = enumClass.addLiteral("lit2");
		UmlAttribute lit3 = enumClass.addLiteral("lit3", "15");
		UmlAttribute lit4 = enumClass.addLiteral("lit4");

		Map<String, List<UmlAttribute>> actual = enumClass.findAttributesPerInitialValue();
		assertEquals("3 keys for 4 attributes", 3, actual.size());

		List<UmlAttribute> with7 = actual.get("7");
		assertEquals(1, with7.size());
		assertTrue("should contain lit1", with7.contains(lit1));

		List<UmlAttribute> withNone = actual.get("");
		assertEquals(2, withNone.size());
		assertTrue("should contain lit2", withNone.contains(lit2));
		assertTrue("should contain lit4", withNone.contains(lit4));

		List<UmlAttribute> with5 = actual.get("15");
		assertEquals(1, with5.size());
		assertTrue("should contain lit3", with5.contains(lit3));
	}

	@Test
	public final void testFindAttributesPerInitialValue() {
		UmlClass enumClass = createEnumClass(emptyModel);
		UmlAttribute lit1 = enumClass.addLiteral("lit1", "7");
		UmlAttribute lit2 = enumClass.addLiteral("lit2", "7");
		UmlAttribute lit3 = enumClass.addLiteral("lit3", "5");

		Map<String, List<UmlAttribute>> actual = enumClass.findAttributesPerInitialValue();
		assertEquals("2 keys for 3 attributes", 2, actual.size());

		List<UmlAttribute> with7 = actual.get("7");
		assertEquals(2, with7.size());
		assertTrue("should contain lit1", with7.contains(lit1));
		assertTrue("should contain lit2", with7.contains(lit2));

		List<UmlAttribute> with5 = actual.get("5");
		assertEquals(1, with5.size());
		assertTrue("should contain lit3", with5.contains(lit3));
	}

	@Test
	public final void testFindInitialValuesOrderedNone() {
		UmlClass enumClass = createEnumClass(emptyModel);
		enumClass.addLiteral("lit1");
		enumClass.addLiteral("lit2");
		enumClass.addLiteral("lit3");
		enumClass.addLiteral("lit4");

		assertTrue("should be empty", enumClass.findInitialValuesOrdered().isEmpty());
	}

	@Test
	public final void testFindInitialValuesOrdered() {
		UmlClass enumClass = createEnumClass(emptyModel);
		enumClass.addLiteral("lit1", "7");
		enumClass.addLiteral("lit2");
		enumClass.addLiteral("lit3", "15");
		enumClass.addLiteral("lit4");

		Set<String> expected = new TreeSet<>();
		expected.add("");
		expected.add("15");
		expected.add("7");
		assertEquals(expected, enumClass.findInitialValuesOrdered());
	}

	// ==================================

	@Test(expected = NullPointerException.class)
	public final void testAddAssociationSourceNull() {
		UmlPackage mp = UmlPackage.basic(emptyModel, "mp");
		UmlClass source = UmlClass.basic(mp, "Source", "");
		UmlClass target = UmlClass.basic(mp, "Target", "");
		UmlAssociationEnd targetEnd = UmlAssociationEnd.basic(target, "TargetEnd");

		source.addAssociation(null, targetEnd, new UmlObjectData(""), UmlAssociation.Data.empty());
	}

	@Test(expected = NullPointerException.class)
	public final void testAddAssociationTargetNull() {
		UmlPackage mp = UmlPackage.basic(emptyModel, "mp");
		UmlClass source = UmlClass.basic(mp, "Source", "");
		UmlAssociationEnd sourceEnd = UmlAssociationEnd.basic(source, "SourceEnd");

		source.addAssociation(sourceEnd, null, new UmlObjectData(""), UmlAssociation.Data.empty());
	}

	@Test(expected = NullPointerException.class)
	public final void testAddAssociationODataNull() {
		UmlPackage mp = UmlPackage.basic(emptyModel, "mp");
		UmlClass source = UmlClass.basic(mp, "Source", "");
		UmlClass target = UmlClass.basic(mp, "Target", "");
		UmlAssociationEnd sourceEnd = UmlAssociationEnd.basic(source, "SourceEnd");
		UmlAssociationEnd targetEnd = UmlAssociationEnd.basic(target, "TargetEnd");

		source.addAssociation(sourceEnd, targetEnd, null, UmlAssociation.Data.empty());
	}

	@Test(expected = NullPointerException.class)
	public final void testAddAssociationDataNull() {
		UmlPackage mp = UmlPackage.basic(emptyModel, "mp");
		UmlClass source = UmlClass.basic(mp, "Source", "");
		UmlClass target = UmlClass.basic(mp, "Source", "");
		UmlAssociationEnd sourceEnd = UmlAssociationEnd.basic(source, "SourceEnd");
		UmlAssociationEnd targetEnd = UmlAssociationEnd.basic(target, "TargetEnd");

		source.addAssociation(sourceEnd, targetEnd, new UmlObjectData(""), null);
	}

	@Test(expected = IllegalArgumentException.class)
	public final void testAddAssociationCalledOnTarget() {
		UmlPackage mp = UmlPackage.basic(emptyModel, "mp");
		UmlClass source = UmlClass.basic(mp, "Source", "");
		UmlClass target = UmlClass.basic(mp, "Target", "");
		UmlAssociationEnd sourceEnd = UmlAssociationEnd.basic(source, "SourceEnd");
		UmlAssociationEnd targetEnd = UmlAssociationEnd.basic(target, "TargetEnd");

		target.addAssociation(sourceEnd, targetEnd, new UmlObjectData(""),
				UmlAssociation.Data.empty());
	}

	@Test(expected = IllegalArgumentException.class)
	public final void testAddAssociationSourceAndTargetFromDifferentModels() {
		UmlPackage mp = UmlPackage.basic(emptyModel, "mp");
		UmlClass source = UmlClass.basic(mp, "Source", "");
		UmlAssociationEnd sourceEnd = UmlAssociationEnd.basic(source, "SourceEnd");

		UmlPackage mpOther = UmlPackage.basic(new UmlModel(emptyCfg), "mpOther");
		UmlClass target = UmlClass.basic(mpOther, "Target", "");
		UmlAssociationEnd targetEnd = UmlAssociationEnd.basic(target, "TargetEnd");

		source.addAssociation(sourceEnd, targetEnd, new UmlObjectData(""),
				UmlAssociation.Data.empty());
	}

	@Test
	public final void testAddAssociationReturnsAdded() {
		UmlPackage mp = UmlPackage.basic(emptyModel, "mp");
		UmlClass source = UmlClass.basic(mp, "Source", "");
		UmlClass target = UmlClass.basic(mp, "Target", "");
		UmlAssociationEnd sourceEnd = UmlAssociationEnd.basic(source, "SourceEnd");
		UmlAssociationEnd targetEnd = UmlAssociationEnd.basic(target, "TargetEnd");

		String assocUuid = "01234";
		UmlObjectData objData = new UmlObjectData(assocUuid, "", null);
		UmlAssociation assoc = source.addAssociation(sourceEnd, targetEnd, objData,
				UmlAssociation.Data.empty());

		assertEquals(assocUuid, assoc.getUuid());
	}

	@Test
	public final void testAddAssociationReturnsSameIfExistingUuid() {
		UmlPackage mp = UmlPackage.basic(emptyModel, "mp");
		UmlClass source = UmlClass.basic(mp, "Source", "");
		UmlClass target = UmlClass.basic(mp, "Target", "");
		UmlAssociationEnd sourceEnd = UmlAssociationEnd.basic(source, "SourceEnd");
		UmlAssociationEnd targetEnd = UmlAssociationEnd.basic(target, "TargetEnd");

		source.addAssociation(sourceEnd, targetEnd, new UmlObjectData(""),
				UmlAssociation.Data.empty());
	}

	@Test
	public final void testAddAssociationAddWithExistingUuid() {
		UmlPackage mp = UmlPackage.basic(emptyModel, "mp");
		UmlClass source = UmlClass.basic(mp, "Source", "");
		UmlClass target = UmlClass.basic(mp, "Target", "");
		UmlAssociationEnd sourceEnd = UmlAssociationEnd.basic(source, "SourceEnd");
		UmlAssociationEnd targetEnd = UmlAssociationEnd.basic(target, "TargetEnd");

		UmlAssociation existing = source.addAssociation(sourceEnd, targetEnd, new UmlObjectData(""),
				UmlAssociation.Data.empty());

		assertEquals("one assoc added as source", 1, source.getAssociationsAsSource().size());
		assertEquals("one assoc added as target", 1, target.getAssociationsAsTarget().size());
		assertEquals("no recursive assoc", 0, target.getAssociationsAsSourceAndTarget().size());

		UmlObjectData duplicateOData = new UmlObjectData(existing.getUuid(), "anyName", null);
		UmlAssociationEnd sourceEnd2 = UmlAssociationEnd.basic(source, "SourceEnd");
		UmlAssociationEnd targetEnd2 = UmlAssociationEnd.basic(target, "TargetEnd");
		UmlAssociation duplicate = source.addAssociation(sourceEnd2, targetEnd2, duplicateOData,
				UmlAssociation.Data.empty());

		assertEquals("new assoc not added as source", 1, source.getAssociationsAsSource().size());
		assertEquals("new assoc not added as target", 1, target.getAssociationsAsTarget().size());
		assertEquals("no recursive assoc", 0, target.getAssociationsAsSourceAndTarget().size());
		assertEquals("new assoc not added to model", 1, target.getModel().getAssociations().size());
		assertSame("returns the existing assoc", existing, duplicate);
	}

	@Test
	public final void testAddAssociationAddedToSourceTargetAndModel() {
		UmlPackage mp = UmlPackage.basic(emptyModel, "mp");
		UmlClass source = UmlClass.basic(mp, "Source", "");
		UmlClass target = UmlClass.basic(mp, "Target", "");
		UmlAssociationEnd sourceEnd = UmlAssociationEnd.basic(source, "SourceEnd");
		UmlAssociationEnd targetEnd = UmlAssociationEnd.basic(target, "TargetEnd");

		source.addAssociation(sourceEnd, targetEnd, new UmlObjectData(""),
				UmlAssociation.Data.empty());

		assertEquals("one assoc added as source", 1, source.getAssociationsAsSource().size());
		assertEquals("one assoc added as target", 1, target.getAssociationsAsTarget().size());
		assertEquals("no recursive assoc", 0, target.getAssociationsAsSourceAndTarget().size());
		assertEquals("one assoc added to model", 1, target.getModel().getAssociations().size());
	}

	@Test
	public final void testAddAssociationRecursive() {
		UmlPackage mp = UmlPackage.basic(emptyModel, "mp");
		UmlClass source = UmlClass.basic(mp, "Source", "");
		UmlAssociationEnd sourceEnd = UmlAssociationEnd.basic(source, "SelfSourceEnd");
		UmlAssociationEnd targetEnd = UmlAssociationEnd.basic(source, "SelfTargetEnd");

		source.addAssociation(sourceEnd, targetEnd, new UmlObjectData(""),
				UmlAssociation.Data.empty());

		assertEquals("no assoc added as source", 0, source.getAssociationsAsSource().size());
		assertEquals("no assoc added as target", 0, source.getAssociationsAsTarget().size());
		assertEquals("one recursive assoc", 1, source.getAssociationsAsSourceAndTarget().size());
		assertEquals("one assoc added to model", 1, source.getModel().getAssociations().size());
	}

	@Test
	public final void testAddAssociationTwoBetweenSameSourceAndTarget() {
		UmlPackage mp = UmlPackage.basic(emptyModel, "mp");
		UmlClass source = UmlClass.basic(mp, "Source", "");
		UmlClass target = UmlClass.basic(mp, "Target", "");
		UmlAssociationEnd sourceEnd = UmlAssociationEnd.basic(source, "SourceEnd");
		UmlAssociationEnd targetEnd = UmlAssociationEnd.basic(target, "TargetEnd");
		UmlAssociationEnd sourceEnd2 = UmlAssociationEnd.basic(source, "SecondSourceEnd");
		UmlAssociationEnd targetEnd2 = UmlAssociationEnd.basic(target, "SecondTargetEnd");

		source.addAssociation(sourceEnd, targetEnd, new UmlObjectData(""),
				UmlAssociation.Data.empty());
		source.addAssociation(sourceEnd2, targetEnd2, new UmlObjectData(""),
				UmlAssociation.Data.empty());

		assertEquals("2 assoc added as source", 2, source.getAssociationsAsSource().size());
		assertEquals("2 assoc added as target", 2, target.getAssociationsAsTarget().size());
		assertEquals("no recursive assoc on source", 0,
				source.getAssociationsAsSourceAndTarget().size());
		assertEquals("no recursive assoc on target", 0,
				target.getAssociationsAsSourceAndTarget().size());
		assertEquals("2 assoc added to model", 2, source.getModel().getAssociations().size());
	}

	// ----------------------------------

	@Test
	public final void testGetAssociations() {
		UmlPackage mp = UmlPackage.basic(emptyModel, "mp");
		UmlClass source = UmlClass.basic(mp, "Source", "");
		UmlClass target = UmlClass.basic(mp, "Target", "");
		UmlAssociationEnd sourceEnd = UmlAssociationEnd.basic(source, "SourceEnd");
		UmlAssociationEnd targetEnd = UmlAssociationEnd.basic(target, "TargetEnd");
		UmlAssociationEnd sourceEnd2 = UmlAssociationEnd.basic(source, "SecondSourceEnd");
		UmlAssociationEnd targetEnd2 = UmlAssociationEnd.basic(target, "SecondTargetEnd");
		UmlAssociationEnd selfSourceEnd = UmlAssociationEnd.basic(source, "SelfSourceEnd");
		UmlAssociationEnd selfTargetEnd = UmlAssociationEnd.basic(source, "SelfTargetEnd");

		UmlAssociation first = source.addAssociation(sourceEnd, targetEnd, new UmlObjectData(""),
				UmlAssociation.Data.empty());
		UmlAssociation second = source.addAssociation(sourceEnd2, targetEnd2, new UmlObjectData(""),
				UmlAssociation.Data.empty());
		UmlAssociation self = source.addAssociation(selfSourceEnd, selfTargetEnd,
				new UmlObjectData(""), UmlAssociation.Data.empty());

		assertEquals("3 assoc added to model", 3, source.getModel().getAssociations().size());

		assertEquals("2 assoc added as source to source", 2,
				source.getAssociationsAsSource().size());
		assertEquals("0 assoc added as target to source", 0,
				source.getAssociationsAsTarget().size());
		assertEquals("1 recursive assoc on source", 1,
				source.getAssociationsAsSourceAndTarget().size());

		Collection<UmlAssociation> associationsWithSource = new HashSet<UmlAssociation>();
		associationsWithSource.add(first);
		associationsWithSource.add(second);
		associationsWithSource.add(self);
		assertEquals(associationsWithSource, new HashSet<UmlAssociation>(source.getAssociations()));

		assertEquals("0 assoc added as source to target", 0,
				target.getAssociationsAsSource().size());
		assertEquals("2 assoc added as target to target", 2,
				target.getAssociationsAsTarget().size());
		assertEquals("no recursive assoc on target", 0,
				target.getAssociationsAsSourceAndTarget().size());

		Collection<UmlAssociation> associationsWithTarget = new HashSet<UmlAssociation>();
		associationsWithTarget.add(first);
		associationsWithTarget.add(second);
		assertEquals(associationsWithTarget, new HashSet<UmlAssociation>(target.getAssociations()));
	}

	@Ignore(value = "until implemented basic tests for others")
	@Test
	public final void testGetInheritedAssociations() {
		fail("Not yet implemented"); // TODO
	}

	@Ignore(value = "until implemented basic tests for others")
	@Test
	public final void testGetAssociationSourceEndClasses() {
		fail("Not yet implemented"); // TODO
	}

	@Ignore(value = "until implemented basic tests for others")
	@Test
	public final void testGetAssociationTargetEndClasses() {
		fail("Not yet implemented"); // TODO
	}

	@Ignore(value = "until implemented basic tests for others")
	@Test
	public final void testGetAssociationEndPairs() {
		fail("Not yet implemented"); // TODO
	}

	@Ignore(value = "until implemented basic tests for others")
	@Test
	public final void testGetInheritedAssociationEndPairs() {
		fail("Not yet implemented"); // TODO
	}

	@Ignore(value = "until implemented basic tests for others")
	@Test
	public final void testGetOtherSideAssociationEnds() {
		fail("Not yet implemented"); // TODO
	}

	@Ignore(value = "until implemented basic tests for others")
	@Test
	public final void testGetInheritedOtherSideAssociationEnds() {
		fail("Not yet implemented"); // TODO
	}
}
