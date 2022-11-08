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
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Ignore;
import org.junit.Test;
import org.tanjakostic.jcleancim.model.UmlAttribute.Data;
import org.tanjakostic.jcleancim.model.UmlClass.InheritedKind;
import org.tanjakostic.jcleancim.util.ApplicationException;
import org.tanjakostic.jcleancim.util.ProgrammerErrorException;
import org.tanjakostic.jcleancim.util.Util;

/**
 * @author tatjana.kostic@ieee.org
 * @version $Id: UmlAttributeTest.java 31 2019-12-08 01:19:54Z dev978 $
 */
public class UmlAttributeTest extends CommonUmlTestBase {

	// ============= Tests ===============

	@SuppressWarnings("unused")
	@Test(expected = NullPointerException.class)
	public final void testCtorContainerNull() {
		UmlPackage mp = UmlPackage.basic(emptyModel, "mp");
		UmlClass type = UmlClass.basic(mp, "Int", "");

		new UmlAttribute(null, type, new UmlObjectData("attr"), Data.empty());
	}

	@SuppressWarnings("unused")
	@Test(expected = ProgrammerErrorException.class)
	public final void testCtorTypeNullForNonLiteral() {
		UmlPackage mp = UmlPackage.basic(emptyModel, "mp");
		UmlClass containingClass = UmlClass.basic(mp, "containingClass", "");

		assertFalse("containing class is not an enum type", containingClass.isEnumeratedType());

		new UmlAttribute(containingClass, null, new UmlObjectData("attr"), Data.empty());
	}

	@SuppressWarnings("unused")
	@Test(expected = NullPointerException.class)
	public final void testCtorODataNull() {
		UmlPackage mp = UmlPackage.basic(emptyModel, "mp");
		UmlClass containingClass = UmlClass.basic(mp, "containingClass", "");
		UmlClass type = UmlClass.basic(mp, "Int", "");

		new UmlAttribute(containingClass, type, null, Data.empty());
	}

	@SuppressWarnings("unused")
	@Test(expected = NullPointerException.class)
	public final void testCtorDataNull() {
		UmlPackage mp = UmlPackage.basic(emptyModel, "mp");
		UmlClass containingClass = UmlClass.basic(mp, "containingClass", "");
		UmlClass type = UmlClass.basic(mp, "Int", "");

		new UmlAttribute(containingClass, type, new UmlObjectData("attr"), null);
	}

	@SuppressWarnings("unused")
	@Test(expected = IllegalArgumentException.class)
	public final void testCtorContainerAndTypeFromDifferentModel() {
		UmlPackage mp = UmlPackage.basic(emptyModel, "mp");
		UmlClass containingClass = UmlClass.basic(mp, "containingClass", "");

		UmlModel otherModel = new UmlModel(emptyCfg);
		UmlClass type = UmlClass.basic(UmlPackage.basic(otherModel, "otherModel"), "Int", "");

		new UmlAttribute(containingClass, type, new UmlObjectData("attr"), Data.empty());
	}

	@Test
	public final void testCtorDataAccessors() {
		UmlPackage mp = UmlPackage.basic(emptyModel, "mp");
		UmlClass containingClass = UmlClass.basic(mp, "containingClass", "");
		UmlClass type = UmlClass.basic(mp, "Int", "");

		Data data = new Data(true, true, UmlMultiplicity.OPT_ONE, "23", 234, "EaType", false);
		UmlObjectData objData = new UmlObjectData(Integer.valueOf(33), "2345", "alias", "attr",
				null, UmlVisibility.PACKAGE.toString(), null, null);
		UmlAttribute attr = new UmlAttribute(containingClass, type, objData, data);

		assertTrue("should be constant", attr.isConst());
		assertTrue("should be static", attr.isStatic());
		assertSame(UmlMultiplicity.OPT_ONE, attr.getMultiplicity());
		assertEquals("23", attr.getInitValue());
		assertEquals(234, attr.getEaTypeId());
		assertEquals("EaType", attr.getEaTypeName());
		assertFalse("type should not be superfluous for non-literal", attr.hasSuperfluousType());
		assertTrue("should contain EA Id",
				attr.getEaTypeInfo().indexOf(Integer.toString(attr.getEaTypeId())) != -1);
		assertTrue("should contain EA Type",
				attr.getEaTypeInfo().indexOf(attr.getEaTypeName()) != -1);
		assertTrue("should be optional", attr.isOptional());
		assertFalse("should not be multi-valued", attr.isMultivalued());
		assertFalse("should not be public", attr.isPublic());

		assertSame(containingClass, attr.getContainingClass());
		assertSame(type, attr.getType());
	}

	// ==================================

	@Test
	public final void testGetTypeForLiteral() {
		UmlPackage mp = UmlPackage.basic(emptyModel, "mp");
		UmlClass containingEnumClass = UmlClass.basic(mp, "containingEnumClass",
				UmlStereotype.ENUMERATION);

		UmlAttribute attr = new UmlAttribute(containingEnumClass, null, new UmlObjectData("attr"),
				UmlAttribute.Data.empty());

		assertTrue("should be literal", attr.isLiteral());
		assertNull("literal should have no type", attr.getType());
	}

	// ==================================

	@Test
	public final void testGetOwner() throws ApplicationException {
		String mp6Name = "mp6";
		UmlPackage nonCimMp = create61850Package(mp6Name);
		UmlClass type = UmlClass.basic(nonCimMp, "Int", "");

		UmlPackage mp = UmlPackage.basic(nonCimMp.getModel(), "mp");
		UmlClass containingClass = UmlClass.basic(mp, "containingClass", "");

		UmlAttribute attr = UmlAttribute.basic(containingClass, type, "attr");
		assertFalse("natures of containingClass and type should differ",
				containingClass.getNature() == type.getNature());

		assertEquals("owner always the one from containing class", containingClass.getOwner(),
				attr.getOwner());
	}

	// ---------------------------------------

	@Test
	public final void testGetNatureNonLiteral() throws ApplicationException {
		String mp6Name = "mp6";
		UmlPackage mp6 = create61850Package(mp6Name);
		UmlClass type = UmlClass.basic(mp6, "Int", "");

		UmlPackage mp = UmlPackage.basic(mp6.getModel(), "mp");
		UmlClass containingClass = UmlClass.basic(mp, "containingClass", "");

		UmlAttribute attr = UmlAttribute.basic(containingClass, type, "attr");
		assertFalse("natures of containingClass and type should differ",
				containingClass.getNature() == type.getNature());

		assertEquals("nature for non-literal should be that of its container",
				containingClass.getNature(), attr.getNature());
	}

	@Test
	public final void testGetNatureLiteral() {
		UmlPackage mp = UmlPackage.basic(emptyModel, "mp");
		UmlClass containingEnumClass = UmlClass.basic(mp, "containingEnumClass",
				UmlStereotype.ENUMERATION);

		UmlAttribute literal = UmlAttribute.basicLiteral(containingEnumClass, "literal");

		assertEquals("nature for literal should be that of its containing class",
				containingEnumClass.getNature(), literal.getNature());
	}

	// ---------------------------------------

	@Test
	public final void testIsInformative() {
		UmlPackage mp = UmlPackage.basic(emptyModel, "Model");
		UmlPackage tp = UmlPackage.basic(mp, "InfTopPackage");
		UmlClass containingEnumClassInf = UmlClass.basic(tp, "containingEnumClassInf",
				UmlStereotype.ENUMERATION);
		assertTrue("class INF if containing package INF", containingEnumClassInf.isInformative());

		UmlPackage mpNorm = UmlPackage.basic(emptyModel, "IEC61968");
		UmlClass containingEnumClassNorm = UmlClass.basic(mpNorm, "containingEnumClassNorm",
				UmlStereotype.ENUMERATION);

		UmlAttribute literal = UmlAttribute.basicLiteral(containingEnumClassInf, "literal");
		UmlAttribute literalNorm = UmlAttribute.basicLiteral(containingEnumClassNorm,
				"literalNorm");

		assertTrue("literal (or attribute) inf if container inf", literal.isInformative());
		assertFalse("literal (or attribute) inf if container inf", literalNorm.isInformative());
	}

	@Test
	public final void testIsInformativeFromType() {
		UmlPackage mp = UmlPackage.basic(emptyModel, "mp");
		UmlPackage tp = UmlPackage.basic(mp, "TopPackage");
		UmlClass containingClass = UmlClass.basic(tp, "containingClass");
		UmlClass type = UmlClass.basic(tp, "type", UmlStereotype.INFORMATIVE);

		UmlAttribute attr = UmlAttribute.basic(containingClass, type, "attr");

		assertTrue("attr should be INF because its type is INF", attr.isInformative());
	}

	// ---------------------------------------

	@Test
	public final void testGetQualifiedName() {
		UmlPackage mp = UmlPackage.basic(emptyModel, "mp");
		UmlClass containingClass = UmlClass.basic(mp, "containingClass", "");
		UmlClass type = UmlClass.basic(mp, "Int", "");

		UmlAttribute attr = UmlAttribute.basic(containingClass, type, "attr");
		assertTrue("contains container name",
				attr.getQualifiedName().contains(attr.getContainingClass().getName()));
		assertTrue("contains attr name", attr.getQualifiedName().contains(attr.getName()));
	}

	// ==================================

	@Test
	public final void testGetKinds() {
		assertFalse("should not be empty", UmlAttribute.getKinds(null).isEmpty());
	}

	@Ignore(value = "until implemented basic tests for others")
	@Test
	public final void testGetKind() {
		// assertNotNull("should not be null", ); // TODO
	}

	@Ignore(value = "until implemented basic tests for others")
	@Test
	public final void testIsLiteral() {
		fail("Not yet implemented"); // TODO
	}

	// ==================================

	@Test
	public final void testIsConditional() throws ApplicationException {
		UmlPackage mp = create61850Package("mp6");
		UmlClass containingClass = UmlClass.basic(mp, "containingClass", "");
		UmlClass type = UmlClass.basic(mp, "Int", "");

		String nameCond = "COND";
		createPresenceConditionsEnumType(mp, nameCond);

		UmlAttribute attr = UmlAttribute.basic(containingClass, type, "attr");
		UmlAttribute attrWithClassConstraint = UmlAttribute.basic(containingClass, type,
				"attrWithClassConstraint");
		UmlAttribute attrWithOwnConstraint = UmlAttribute.basic(containingClass, type,
				"attrWithOwnConstraint");

		containingClass.addConstraint(new UmlObjectData(nameCond), UmlConstraint.Data
				.createClassConstraintData("mandatory if...", "attrWithClassConstraint"));

		attrWithOwnConstraint.addOwnConstraint(new UmlObjectData("maxId"),
				UmlConstraint.Data.createAttrConstraintData("<255"));

		assertFalse("no class constraint added, so should not be conditional",
				attr.isConditional());
		assertTrue("class constraint added, so should be conditional",
				attrWithClassConstraint.isConditional());
		assertFalse("attribute constraint should not matter for condition",
				attrWithOwnConstraint.isConditional());
	}

	// ---------------------------------------

	@Test
	public final void testGetConstraintsFromClass() throws ApplicationException {
		UmlPackage mp = create61850Package("mp6");
		UmlClass containingClass = UmlClass.basic(mp, "containingClass", "");
		UmlClass type = UmlClass.basic(mp, "Int", "");

		String nameM1 = "M1";
		String nameO2 = "O2";
		createPresenceConditionsEnumType(mp, nameM1, nameO2);

		UmlAttribute a1 = UmlAttribute.basic(containingClass, type, "a1");
		UmlAttribute a2 = UmlAttribute.basic(containingClass, type, "a2");
		UmlAttribute a3 = UmlAttribute.basic(containingClass, type, "a3");

		UmlConstraint m1 = containingClass.addConstraint(new UmlObjectData(nameM1),
				UmlConstraint.Data.createClassConstraintData("mandatory if...", "a1", "a2"));
		UmlConstraint o2 = containingClass.addConstraint(new UmlObjectData(nameO2),
				UmlConstraint.Data.createClassConstraintData("optional if...", "a3", "a1"));

		List<UmlConstraint> both = new ArrayList<UmlConstraint>();
		both.add(m1);
		both.add(o2);
		assertTrue("two constraints should have been added",
				a1.getConstraintsFromClass().containsAll(both));
		assertTrue("should contain M1", a2.getConstraintsFromClass().contains(m1));
		assertTrue("should contain O2", a3.getConstraintsFromClass().contains(o2));
	}

	// ---------------------------------------

	@Test
	public final void testGetConditionNames() throws ApplicationException {
		UmlPackage mp = create61850Package("mp6");
		UmlClass containingClass = UmlClass.basic(mp, "containingClass", "");
		UmlClass type = UmlClass.basic(mp, "Int", "");

		String nameM1 = "M1";
		String nameO2 = "O2";
		createPresenceConditionsEnumType(mp, nameM1, nameO2);

		UmlAttribute a1 = UmlAttribute.basic(containingClass, type, "a1");
		UmlAttribute a2 = UmlAttribute.basic(containingClass, type, "a2");
		UmlAttribute a3 = UmlAttribute.basic(containingClass, type, "a3");

		containingClass.addConstraint(new UmlObjectData(nameM1),
				UmlConstraint.Data.createClassConstraintData("mandatory if...", "a1", "a2"));
		containingClass.addConstraint(new UmlObjectData(nameO2),
				UmlConstraint.Data.createClassConstraintData("optional if...", "a3", "a1"));

		assertEquals("M1,O2", collectNamesCsv(a1.getConstraintsFromClass()));
		assertEquals(nameM1, collectNamesCsv(a2.getConstraintsFromClass()));
		assertEquals(nameO2, collectNamesCsv(a3.getConstraintsFromClass()));
	}

	private static <T extends UmlObject> String collectNamesCsv(Collection<T> objects) {
		return Util.concatCharSeparatedTokens(",", AbstractUmlObject.collectNames(objects));
	}

	// ---------------------------------------

	@Test
	public final void testGetPresConditions() throws ApplicationException {
		UmlPackage mp = create61850Package("mp6");
		UmlClass containingClass = UmlClass.basic(mp, "containingClass", "");
		UmlClass type = UmlClass.basic(mp, "Int", "");

		String literalNameC3 = "MO(arg1, arg2)";
		String literalNameC4 = "simpleCond";
		createPresenceConditionsEnumType(mp, literalNameC3, literalNameC4);

		UmlAttribute a1 = UmlAttribute.basic(containingClass, type, "a1", UmlMultiplicity.ONE);
		UmlAttribute a2 = UmlAttribute.basic(containingClass, type, "a2", UmlMultiplicity.OPT_ONE);
		UmlAttribute a3 = UmlAttribute.basic(containingClass, type, "a3");
		UmlAttribute a4 = UmlAttribute.basic(containingClass, type, "a4");
		UmlAttribute a5 = UmlAttribute.basic(containingClass, type, "a5", UmlMultiplicity.EMPTY);
		UmlAttribute a6 = UmlAttribute.basic(containingClass, type, "a6");

		containingClass.addConstraint(new UmlObjectData("MO(3, 11)"),
				UmlConstraint.Data.createClassConstraintData("", "a3"));
		containingClass.addConstraint(new UmlObjectData("simpleCond"),
				UmlConstraint.Data.createClassConstraintData("", "a4"));
		containingClass.addConstraint(new UmlObjectData("unrecognisedCond(x,y)"),
				UmlConstraint.Data.createClassConstraintData("", "a6"));

		assertConstraintSplitOk("deduced from multiplicity", "M", "", a1);
		assertConstraintSplitOk("deduced from multiplicity", "O", "", a2);
		assertConstraintSplitOk("", "MO", "3, 11", a3);
		assertConstraintSplitOk("", "simpleCond", "", a4);
		assertConstraintSplitOk("deduced from (empty) multiplicity", "O", "", a5);
		assertConstraintSplitOk("not added to enum", "unrecognisedCond(x,y)", "", a6);
	}

	private static void assertConstraintSplitOk(String txt, String expKey, String expValue,
			UmlAttribute a) {
		Map<String, String> expected = new HashMap<String, String>();
		expected.put(expKey, expValue);

		PresenceCondition presCond = a.getPresConditions().get(0);
		Map<String, String> actual = new HashMap<String, String>();
		actual.put(presCond.getStem(), presCond.getArgs());
		assertEquals(txt, expected, actual);
	}

	@Ignore(value = "until implemented basic tests for others; needs meta model fixture...")
	@Test
	public final void testGetSplitDsPresCondAndArgs() {
		fail("Not yet implemented"); // TODO
	}

	// ==================================

	@Ignore(value = "until implemented basic tests for others; needs meta model fixture...")
	@Test
	public final void testGetNameDecompositionNull() {
		fail("Not yet implemented"); // TODO
	}

	@Ignore(value = "until implemented basic tests for others; needs meta model fixture...")
	@Test
	public final void testGetNameDecomposition() {
		fail("Not yet implemented"); // TODO
	}

	// ==================================

	@Test
	public final void testAddAndGetOwnConstraint() throws ApplicationException {
		UmlAttribute a1 = create61850Attribute("a1");
		assertTrue("should have no own constraints", a1.getOwnConstraints().isEmpty());

		a1.addOwnConstraint(new UmlObjectData(UML.CONSTR_TXT_minIdx),
				UmlConstraint.Data.createAttrConstraintData("-5"));
		assertEquals(1, a1.getOwnConstraints().size());

		UmlConstraint cmin = a1.getOwnConstraints().get(0);
		assertEquals(UML.CONSTR_TXT_minIdx, cmin.getName());
	}

	static UmlAttribute create61850Attribute(String name) throws ApplicationException {
		UmlPackage mp = create61850Package("mp6");
		UmlClass containingClass = UmlClass.basic(mp, "containingClass", "");
		UmlClass type = UmlClass.basic(mp, "Int", "");
		return UmlAttribute.basic(containingClass, type, name);
	}

	@Test
	public final void testGetArrayBoundsNone() throws ApplicationException {
		UmlAttribute a1 = create61850Attribute("a1");
		assertTrue("should have no own constraints", a1.getOwnConstraints().isEmpty());

		assertTrue("should be empty", a1.getArrayBounds().isEmpty());
	}

	@Test
	public final void testGetArrayBoundsMinOnly() throws ApplicationException {
		UmlAttribute a1 = create61850Attribute("a1");
		a1.addOwnConstraint(new UmlObjectData(UML.CONSTR_TXT_minIdx),
				UmlConstraint.Data.createAttrConstraintData("-5"));

		assertEquals("-5...", a1.getArrayBounds());
	}

	@Test
	public final void testGetArrayBoundsMaxOnly() throws ApplicationException {
		UmlAttribute a1 = create61850Attribute("a1");
		a1.addOwnConstraint(new UmlObjectData(UML.CONSTR_TXT_maxIdx),
				UmlConstraint.Data.createAttrConstraintData("top"));

		assertEquals("...top", a1.getArrayBounds());
	}

	@Test
	public final void testGetArrayBoundsBoth() throws ApplicationException {
		UmlAttribute a1 = create61850Attribute("a1");
		a1.addOwnConstraint(new UmlObjectData(UML.CONSTR_TXT_minIdx),
				UmlConstraint.Data.createAttrConstraintData("-5"));
		a1.addOwnConstraint(new UmlObjectData(UML.CONSTR_TXT_maxIdx),
				UmlConstraint.Data.createAttrConstraintData("top"));

		assertEquals("-5...top", a1.getArrayBounds());
	}

	// ==================================

	public static UmlAttribute createAttributeWithInitValue(UmlModel model, String initValue) {
		UmlPackage mp = UmlPackage.basic(model, "mp");
		UmlClass containingClass = UmlClass.basic(mp, "containingClass", "");
		UmlClass type = UmlClass.basic(mp, "Int", "");

		return UmlAttribute.basic(containingClass, type, "a1", initValue);
	}

	UmlAttribute createAttributeWithInitValue(String initValue) {
		return createAttributeWithInitValue(emptyModel, initValue);
	}

	@Test
	public final void testHasInitialValueTrue() {
		UmlAttribute a = createAttributeWithInitValue("some value");
		assertTrue("Should have initial value", a.hasInitialValue());
	}

	@Test
	public final void testHasInitialValueFalse() {
		UmlAttribute a = createAttributeWithInitValue("   \t  ");
		assertFalse("Should have trimmed white space in initial value", a.hasInitialValue());
	}

	// ---------------------------------------

	@Test
	public final void testHasValueRangeFalseDueToNoValues() {
		String initValue = "...";
		UmlAttribute a = createAttributeWithInitValue(initValue);
		assertEquals(initValue, a.getInitValue());
		assertFalse("should return false: only range delimiter present", a.hasValueRange());
	}

	@Test
	public final void testHasValueRangeFalseDueToNoInitialValue() {
		String initValue = "";
		UmlAttribute a = createAttributeWithInitValue(initValue);
		assertEquals(initValue, a.getInitValue());
		assertFalse("should return false: empty init value", a.hasValueRange());
	}

	@Test
	public final void testGetValueRangeOnlyMin() {
		String initValue = "-10...";
		UmlAttribute a = createAttributeWithInitValue(initValue);
		assertTrue("should have recognised range", a.hasValueRange());
		assertEquals("-10", a.getValueRange().min());
		assertNull("max not set", a.getValueRange().max());
	}

	@Test
	public final void testGetValueRangeOnlyMax() {
		String initValue = "...33";
		UmlAttribute a = createAttributeWithInitValue(initValue);
		assertTrue("should have recognised range", a.hasValueRange());
		assertNull("min not set", a.getValueRange().min());
		assertEquals("33", a.getValueRange().max());
	}

	@Test
	public final void testGetValueRangeBoth() {
		String initValue = "-10...33";
		UmlAttribute a = createAttributeWithInitValue(initValue);
		assertTrue("should have recognised range", a.hasValueRange());
		assertEquals("-10", a.getValueRange().min());
		assertEquals("33", a.getValueRange().max());
	}

	// ---------------------------------------

	@Ignore(value = "until implemented basic tests for others")
	@Test
	public final void testHasDefaultValue() {
		fail("Not yet implemented"); // TODO
	}

	// ---------------------------------------

	@Ignore(value = "until implemented basic tests for others")
	@Test
	public final void testHasConstValue() {
		fail("Not yet implemented"); // TODO
	}

	// -------------------------

	@Test(expected = NullPointerException.class)
	public final void testGetInitialValueAsIntegerNull() {
		UmlAttribute a = createAttributeWithInitValue(null);
		assertNull(a.getInitialValueAsInteger());
	}

	@Test(expected = NullPointerException.class)
	public final void testHasInitialValueAsIntegerNull() {
		UmlAttribute a = createAttributeWithInitValue(null);
		assertFalse("Should have not recognised null", a.hasInitialValueAsInteger());
		assertNull(a.getInitialValueAsInteger());
	}

	@Test
	public final void testInitialValueAsIntegerEmpty() {
		UmlAttribute a = createAttributeWithInitValue("   \t  ");
		assertFalse("Should have not recognised empty", a.hasInitialValueAsInteger());
		assertNull(a.getInitialValueAsInteger());
	}

	@Test
	public final void testInitialValueAsIntegerString() {
		UmlAttribute a = createAttributeWithInitValue("some string");
		assertFalse("Should have not recognised a string", a.hasInitialValueAsInteger());
		assertNull(a.getInitialValueAsInteger());
	}

	@Test
	public final void testInitialValueAsIntegerDouble() {
		UmlAttribute a = createAttributeWithInitValue(" \t 44.7  \t");
		assertFalse("Should have not recognised a double", a.hasInitialValueAsInteger());
		assertNull(a.getInitialValueAsInteger());
	}

	@Test
	public final void testInitialValueAsIntegerTrimmedMultipleIntegers() {
		UmlAttribute a = createAttributeWithInitValue(" \t44  \n33 7");
		assertFalse("Should have not recognised multiple integers", a.hasInitialValueAsInteger());
		assertNull(a.getInitialValueAsInteger());
	}

	@Test
	public final void testInitialValueAsIntegerInteger() {
		UmlAttribute a = createAttributeWithInitValue(" \t44  \n");
		assertTrue("Should have recognised an integer", a.hasInitialValueAsInteger());
		assertEquals(44, a.getInitialValueAsInteger().intValue());
	}

	// -----------------------------

	@Test
	public final void testGetSiblingToMoveAfterNoTag() {
		UmlPackage mp = UmlPackage.basic(emptyModel, "mp");
		UmlClass containingClass = UmlClass.basic(mp, "containingClass", "");
		UmlClass type = UmlClass.basic(mp, "Int", "");
		UmlAttribute attr = UmlAttribute.basic(containingClass, type, "a");

		assertNull("no tag defined", attr.getTaggedValues().get(UML.TAG_moveAfter));
		assertNull(attr.getSiblingToMoveAfter());
	}

	@Test
	public final void testGetSiblingToMoveAfterTagWithInexisting() {
		UmlPackage mp = UmlPackage.basic(emptyModel, "mp");
		UmlClass containingClass = UmlClass.basic(mp, "containingClass", "");
		UmlClass type = UmlClass.basic(mp, "Int", "");
		UmlAttribute attr = UmlAttribute.basic(containingClass, type, "a");
		attr.addTaggedValue(UML.TAG_moveAfter, "inexistingAttr");

		assertEquals(0, attr.getContainingClass()
				.findAttributes("inexistingAttr", InheritedKind.all).size());
		assertNull(attr.getSiblingToMoveAfter());
	}

	@Test
	public final void testGetSiblingToMoveAfterTagWithExisting() {
		UmlPackage mp = UmlPackage.basic(emptyModel, "mp");
		UmlClass containingClass = UmlClass.basic(mp, "containingClass", "");
		UmlClass type = UmlClass.basic(mp, "Int", "");
		UmlAttribute a = containingClass.addAttribute(type, "a");
		UmlAttribute b = containingClass.addAttribute(type, "b");
		a.addTaggedValue(UML.TAG_moveAfter, "b");

		assertSame(b, a.getSiblingToMoveAfter());
	}

	@Test
	public final void testGetSiblingToMoveAfterTagWithMoreThanOneExisting() {
		UmlPackage mp = UmlPackage.basic(emptyModel, "mp");
		UmlClass containingClass = UmlClass.basic(mp, "containingClass", "");
		UmlClass type = UmlClass.basic(mp, "Int", "");
		UmlAttribute a = containingClass.addAttribute(type, "a");
		UmlAttribute b = containingClass.addAttribute(type, "b");
		containingClass.addAttribute(type, "c");
		containingClass.addAttribute(type, "b"); // second "b"

		a.addTaggedValue(UML.TAG_moveAfter, "b");
		assertSame(b, a.getSiblingToMoveAfter());
	}

	// -----------------------------

	@Test
	public final void testDisplayEmptyValue() {
		UmlPackage mp = UmlPackage.basic(emptyModel, "mp");
		UmlClass enumClass = UmlClass.basic(mp, "EnumClass", UmlStereotype.ENUMERATION);
		UmlAttribute literal = UmlAttribute.basicLiteral(enumClass, "literal");
		literal.addTaggedValue(UML.TAG_SCL_emptyValue, null);

		assertTrue("should display empty value", literal.displayEmptyValue());
	}
}
