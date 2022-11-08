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
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;

import org.junit.Test;
import org.tanjakostic.jcleancim.common.Nature;
import org.tanjakostic.jcleancim.util.ApplicationException;

/**
 * @author tatjana.kostic@ieee.org
 * @version $Id: UmlConstraintTest.java 25 2019-11-02 17:21:28Z dev978 $
 */
public class UmlConstraintTest extends CommonUmlTestBase {

	private UmlAttribute createAttribute(String containerName, String name) {
		UmlPackage mp = UmlPackage.basic(emptyModel, "mp");
		UmlClass c = UmlClass.basic(mp, containerName, "");
		UmlClass type = UmlClass.basic(mp, String.format("%sType", name), "");
		return c.addAttribute(type, name, "");
	}

	// ============= Tests ===============

	@SuppressWarnings("unused")
	@Test(expected = NullPointerException.class)
	public final void testCtorClassContainerNull() {
		UmlObjectData objData = new UmlObjectData(Integer.valueOf(56), "8304", "SCAV", "", null,
				null, null, null);
		UmlConstraint.Data data = new UmlConstraint.Data(Arrays.asList(new String[] { "a1", "a3" }),
				null, false);

		new UmlConstraint((UmlClass) null, objData, data);
	}

	@SuppressWarnings("unused")
	@Test(expected = NullPointerException.class)
	public final void testCtorClassODataNull() {
		UmlPackage mp = UmlPackage.basic(emptyModel, "mp");
		UmlClass c = createClassWithAttributes(mp, "LLN0", "a1", "a2", "a3");
		UmlConstraint.Data data = new UmlConstraint.Data(Arrays.asList(new String[] { "a1", "a3" }),
				null, false);

		new UmlConstraint(c, null, data);
	}

	@SuppressWarnings("unused")
	@Test(expected = NullPointerException.class)
	public final void testCtorClassDataNull() {
		UmlPackage mp = UmlPackage.basic(emptyModel, "mp");
		UmlClass c = createClassWithAttributes(mp, "LLN0", "a1", "a2", "a3");
		UmlObjectData objData = new UmlObjectData(Integer.valueOf(56), "8304", "SCAV", "", null,
				null, null, null);

		new UmlConstraint(c, objData, null);
	}

	@Test
	public final void testCtorClassDataAccessors() throws ApplicationException {
		UmlPackage mp = UmlPackage.basic(emptyModel, "mp");
		UmlClass c = UmlClass.basic(mp, "IdentifiedObject");
		UmlConstraint cc = UmlConstraint.basic(c, "CIM constraint", "-- comment, then some OCL");

		assertSame(UmlConstraint.Kind.CLASS, cc.getKind());
		assertEquals("CIM constraint", cc.getName());
		assertSame(c, cc.getContainingClass());
		assertNull("no containing attr (this is class constraint)", cc.getContainingAttribute());
		assertEquals(0, cc.getAttrNames().size());
		assertEquals("-- comment, then some OCL", cc.getCondition());
	}

	@Test
	public final void testCtorClassGetPresenceCondition() throws ApplicationException {
		UmlPackage mp6 = create61850Package("mp6");
		UmlClass c = createClassWithAttributes(mp6, "LLN0", "a1", "a2", "a3", "a4");
		assertEquals(Nature.IEC61850, c.getNature());
		String constraintName = "MF";
		UmlObjectData objData1 = new UmlObjectData(Integer.valueOf(56), "8304", constraintName, "",
				null, null, null, null);
		UmlConstraint.Data data1 = new UmlConstraint.Data(
				Arrays.asList(new String[] { "a1", "a3" }), null, true);

		UmlClass presCond = UmlClass.basic(c.getContainingPackage(), "PresenceConditions",
				UmlClass.Iec61850Kind.COND_ENUM.getValue());
		presCond.addAttribute(null, "XYZ", UmlStereotype.ENUM);
		assertEquals(1, presCond.getAttributes().size());

		UmlConstraint cc = new UmlConstraint(c, objData1, data1);

		assertNotNull(cc.getPresenceCondition());
	}

	@Test
	public final void testCtorClassDataAccessors61850() throws ApplicationException {
		UmlPackage mp6 = create61850Package("mp6");
		UmlClass c = createClassWithAttributes(mp6, "LLN0", "a1", "a2", "a3");
		String gc1Name = "GC_1";
		UmlObjectData objData = new UmlObjectData(Integer.valueOf(56), "8304", gc1Name, "", null,
				null, null, null);
		UmlConstraint.Data data = new UmlConstraint.Data(Arrays.asList(new String[] { "a1", "a3" }),
				"some condition", true);

		UmlClass presCond = UmlClass.basic(c.getContainingPackage(), "PresenceConditions",
				UmlClass.Iec61850Kind.COND_ENUM.getValue());
		UmlAttribute gc1Literal = presCond.addAttribute(null, gc1Name, UmlStereotype.ENUM);
		presCond.addAttribute(null, "XYZ", UmlStereotype.ENUM);

		UmlConstraint cc = new UmlConstraint(c, objData, data);

		assertSame(UmlConstraint.Kind.CLASS, cc.getKind());
		assertSame("literal from presence condition found", gc1Literal,
				cc.getPresenceCondition().getDefinitionLiteral());
		assertSame(c, cc.getContainingClass());
		assertNull("no containing attr (this is class constraint)", cc.getContainingAttribute());
		assertEquals(2, cc.getAttrNames().size());
		assertTrue("should contain attr a1", cc.getAttrNames().contains("a1"));
		assertTrue("should contain attr a3", cc.getAttrNames().contains("a3"));
		assertEquals("some condition", cc.getCondition());
		assertTrue("should support tags", cc.isSupportsTags());
	}

	@Test
	public final void testCondIDText() throws ApplicationException {
		UmlPackage mp6 = create61850Package("mp6");
		UmlClass c = createClassWithAttributes(mp6, "LLN0", "a1", "a2", "a3");

		// set-up a tiny PresenceConditions enumerated type:
		UmlClass presCond = UmlClass.basic(c.getContainingPackage(), "PresenceConditions",
				UmlClass.Iec61850Kind.COND_ENUM.getValue());
		presCond.addAttribute(null, "MO(sibling)", UmlStereotype.ENUM);
		presCond.addAttribute(null, "OFcond(condID)", UmlStereotype.ENUM);
		presCond.addAttribute(null, "FM(condID)", UmlStereotype.ENUM);

		UmlConstraint moSibling = UmlConstraint.basic(c, "MO(a2)", "cond not needed here", "a1");
		UmlConstraint ofCondGood = UmlConstraint.basic(c, "OFcond(1)", "if XYZ supported", "a2");
		UmlConstraint ofCondBad = UmlConstraint.basic(c, "FM(1)", null, "a3");

		assertEquals("cond not needed here", moSibling.getCondition());

		assertTrue(ofCondGood.getPresenceCondition().isWithCondID());
		assertEquals("if XYZ supported", ofCondGood.getCondition());

		assertFalse(ofCondBad.getPresenceCondition().isWithCondID());
		assertNull("if XYZ supported", ofCondBad.getCondition());
	}

	// --------------------------------------------

	@SuppressWarnings("unused")
	@Test(expected = NullPointerException.class)
	public final void testCtorAttrContainerNull() {
		UmlObjectData objData = new UmlObjectData(Integer.valueOf(56), "8304", "minId", "", null,
				null, null, null);
		UmlConstraint.Data data = new UmlConstraint.Data(null, "0", false);

		new UmlConstraint((UmlAttribute) null, objData, data);
	}

	@SuppressWarnings("unused")
	@Test(expected = NullPointerException.class)
	public final void testCtorAttrODataNull() {
		UmlAttribute a = createAttribute("CSD", "numPts");
		UmlConstraint.Data data = new UmlConstraint.Data(null, "5", false);

		new UmlConstraint(a, null, data);
	}

	@SuppressWarnings("unused")
	@Test(expected = NullPointerException.class)
	public final void testCtorAttrDataNull() {
		UmlAttribute a = createAttribute("CSD", "numPts");
		UmlObjectData objData = new UmlObjectData(Integer.valueOf(56), "8304", "minId", "", null,
				null, null, null);

		new UmlConstraint(a, objData, null);
	}

	@Test
	public final void testCtorAttributeGetArgumentNull() {
		UmlAttribute a = createAttribute("CSD", "numPts");
		String nameWithArgument = "minId(arg)";
		UmlObjectData objData = new UmlObjectData(Integer.valueOf(56), "8304", nameWithArgument, "",
				null, null, null, null);
		String condition = "0";
		boolean supportsTags = false;
		UmlConstraint.Data data = new UmlConstraint.Data(null, condition, supportsTags);

		UmlConstraint ac = new UmlConstraint(a, objData, data);
		assertNull("for attribute constraint, we don't care for arguments",
				ac.getPresenceCondition());
	}

	@Test
	public final void testCtorAttrDataAccessors() {
		UmlAttribute a = createAttribute("CSD", "numPts");
		UmlObjectData objData = new UmlObjectData(Integer.valueOf(56), "8304", "minId", "", null,
				null, null, null);
		String condition = "0";
		boolean supportsTags = false;
		UmlConstraint.Data data = new UmlConstraint.Data(null, condition, supportsTags);

		UmlConstraint ac = new UmlConstraint(a, objData, data);

		assertSame(UmlConstraint.Kind.ATTR_MIN_MAX, ac.getKind());
		assertNull("containing class used for class constraint", ac.getContainingClass());
		assertSame(a, ac.getContainingAttribute());
		assertTrue("attr names used for class constraint", ac.getAttrNames().isEmpty());
		assertEquals(condition, ac.getCondition());
		assertFalse("should not support tags", ac.isSupportsTags());
	}

	// --------------------------

	@Test(expected = InvalidTagException.class)
	public final void testValidateTag() {
		UmlAttribute a = createAttribute("CSD", "numPts");
		UmlObjectData objData = new UmlObjectData("minIdx");
		boolean supportsTags = false;
		UmlConstraint.Data data = new UmlConstraint.Data(null, "0", supportsTags);

		UmlConstraint ac = new UmlConstraint(a, objData, data);
		assertFalse("should not support tags", ac.isSupportsTags());

		ac.validateTag("name", "value");
	}

	// ==================================

	@Test
	public final void testGetQualifiedNameClass() {
		UmlPackage mp = UmlPackage.basic(emptyModel, "mp");
		UmlClass c = createClassWithAttributes(mp, "LLN0", "a1", "a2", "a3");
		UmlObjectData objData = new UmlObjectData(Integer.valueOf(56), "8304", "SCAV", "", null,
				null, null, null);
		UmlConstraint.Data data = new UmlConstraint.Data(Arrays.asList(new String[] { "a1", "a3" }),
				null, true);

		UmlConstraint cc = new UmlConstraint(c, objData, data);

		assertTrue("should include container", cc.getQualifiedName().contains(c.getName()));
		assertTrue("should include attr list", cc.getQualifiedName().contains("a3"));
	}

	@Test
	public final void testGetQualifiedNameAttribute() {
		UmlAttribute a = createAttribute("CSD", "numPts");
		UmlObjectData objData = new UmlObjectData(Integer.valueOf(56), "8304", "maxId", "", null,
				null, null, null);
		UmlConstraint.Data data = new UmlConstraint.Data(null, "ptsMaxNum", false);

		UmlConstraint ac = new UmlConstraint(a, objData, data);

		assertTrue("should include container", ac.getQualifiedName().contains(a.getName()));
		assertTrue("should include own name", ac.getQualifiedName().contains(ac.getName()));
		assertTrue("should include condition", ac.getQualifiedName().contains(ac.getCondition()));
	}

	// ==================================

	@Test
	public final void testGetOwner() {
		UmlAttribute a = createAttribute("CSD", "numPts");
		UmlObjectData objData = new UmlObjectData(Integer.valueOf(56), "8304", "maxId", "", null,
				null, null, null);
		UmlConstraint.Data data = new UmlConstraint.Data(null, "ptsMaxNum", false);

		UmlConstraint ac = new UmlConstraint(a, objData, data);

		assertEquals(ac.getContainingAttribute().getOwner(), ac.getOwner());
	}

	@Test
	public final void testGetNature() {
		UmlAttribute a = createAttribute("CSD", "numPts");
		UmlObjectData objData = new UmlObjectData(Integer.valueOf(56), "8304", "maxId", "", null,
				null, null, null);
		UmlConstraint.Data data = new UmlConstraint.Data(null, "ptsMaxNum", false);

		UmlConstraint ac = new UmlConstraint(a, objData, data);

		assertEquals(ac.getContainingAttribute().getNature(), ac.getNature());
	}

	@Test
	public final void testIsInformative() {
		UmlAttribute a = createAttribute("CSD", "numPts");
		UmlObjectData objData = new UmlObjectData(Integer.valueOf(56), "8304", "maxId", "", null,
				null, null, null);
		UmlConstraint.Data data = new UmlConstraint.Data(null, "ptsMaxNum", false);

		UmlConstraint ac = new UmlConstraint(a, objData, data);

		assertTrue("INF same as container",
				ac.getContainingAttribute().isInformative() == ac.isInformative());
	}
}
