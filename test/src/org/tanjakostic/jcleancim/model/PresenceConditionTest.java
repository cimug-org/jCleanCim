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

import java.util.Set;

import org.junit.Test;
import org.tanjakostic.jcleancim.util.ApplicationException;

/**
 * @author tatjana.kostic@ieee.org
 * @version $Id: PresenceConditionTest.java 25 2019-11-02 17:21:28Z dev978 $
 */
public class PresenceConditionTest extends CommonUmlTestBase {

	private UmlClass createClassWithAttributes(String... attributes) throws ApplicationException {
		UmlPackage mp6 = create61850Package("mp6");
		return createClassWithAttributes(mp6, "LLN0", attributes);
	}

	// ============= Tests ===============

	@Test
	public final void testGetNamesOfImplicits() {
		Set<String> namesOfImplicits = PresenceCondition.getNamesOfImplicits();
		assertTrue(UML.PC_M + " should be implicit", namesOfImplicits.contains(UML.PC_M));
		assertTrue(UML.PC_O + " should be implicit", namesOfImplicits.contains(UML.PC_O));
		assertTrue(UML.PC_na + " should be implicit", namesOfImplicits.contains(UML.PC_na));
	}

	// --------------

	@Test(expected = NullPointerException.class)
	public final void testCreateNull() {
		PresenceCondition.create(null);
	}

	@Test
	public final void testCreateAndGettersSimple() throws ApplicationException {
		UmlClass c = createClassWithAttributes("a1", "a2", "a3");

		UmlClass presCond = UmlClass.basic(c.getContainingPackage(), "PresenceConditions",
				UmlClass.Iec61850Kind.COND_ENUM.getValue());
		UmlAttribute pcLiteral = presCond.addAttribute(null, "MF", UmlStereotype.ENUM);
		presCond.addAttribute(null, "XYZ", UmlStereotype.ENUM); // dummy
		assertEquals(2, presCond.getAttributes().size());

		String constraintName = "MF";
		UmlConstraint cc = UmlConstraint.basic(c, constraintName, null, "a1", "a3");

		PresenceCondition pc1 = cc.getPresenceCondition();
		assertSame("literal from presence condition found", pcLiteral, pc1.getDefinitionLiteral());
		assertEquals("name is literal", constraintName, pc1.getStem());
		assertTrue("constraint without argument", pc1.getArgs().isEmpty());
		assertNotNull(pc1.getConstraint());
		assertFalse("should not have condID", pc1.isWithCondID());
		assertTrue("should have no text", pc1.getText().isEmpty());
		assertEquals(constraintName, pc1.getStemAndArgs());
	}

	@Test
	public final void testCreateAndGettersWithCondIDArgName() throws ApplicationException {
		UmlClass c = createClassWithAttributes("a1", "a2");

		UmlClass presCond = UmlClass.basic(c.getContainingPackage(), "PresenceConditions",
				UmlClass.Iec61850Kind.COND_ENUM.getValue());
		UmlAttribute pcWithCondIDArgLiteral = presCond.addAttribute(null,
				"MFcond" + PresenceCondition.ARG_CONDID, UmlStereotype.ENUM);
		presCond.addAttribute(null, "XYZ", UmlStereotype.ENUM); // dummy
		assertEquals(2, presCond.getAttributes().size());

		String nameWithCondIDArg = "MFcond(25)";
		String textForCondID = "txt for condID";
		UmlConstraint cc = UmlConstraint.basic(c, nameWithCondIDArg, textForCondID, "a2");

		PresenceCondition pc = cc.getPresenceCondition();
		assertSame("literal from presence condition (with condID) found", pcWithCondIDArgLiteral,
				pc.getDefinitionLiteral());
		assertEquals("name correctly deduced", "MFcond", pc.getStem());
		assertEquals("argument correctly recognised", "25", pc.getArgs());
		assertNotNull(pc.getConstraint());
		assertTrue("should have condID", pc.isWithCondID());
		assertEquals(textForCondID, pc.getText());
		assertEquals(nameWithCondIDArg, pc.getStemAndArgs());
	}

	@Test
	public final void testCreateAndGettersWithInvalidArgName() throws ApplicationException {
		UmlClass c = createClassWithAttributes("a1", "a2", "a3", "a4");

		UmlClass presCond = UmlClass.basic(c.getContainingPackage(), "PresenceConditions",
				UmlClass.Iec61850Kind.COND_ENUM.getValue());
		presCond.addAttribute(null, "MF", UmlStereotype.ENUM);
		presCond.addAttribute(null, "XYZ", UmlStereotype.ENUM); // dummy
		assertEquals(2, presCond.getAttributes().size());

		String constraintWithInvalidArgName = "MF(25";
		UmlConstraint cc = UmlConstraint.basic(c, constraintWithInvalidArgName, "", "a1");
		PresenceCondition pc = cc.getPresenceCondition();

		assertNull("invalid format", pc.getDefinitionLiteral());
		assertEquals("invalid format - full name is stem", cc.getName(), pc.getStem());
		assertTrue("inexisting", pc.getArgs().isEmpty());
		assertNotNull(pc.getConstraint());
		assertFalse("should not have condID", pc.isWithCondID());
		assertTrue("should have no text", pc.getText().isEmpty());
		assertEquals("invalud format - full name is stem", pc.getStem(), pc.getStemAndArgs());
	}

	@Test
	public final void testCreateAndGettersSiblingArgName() throws ApplicationException {
		UmlClass c = createClassWithAttributes("a3", "a4");

		UmlClass presCond = UmlClass.basic(c.getContainingPackage(), "PresenceConditions",
				UmlClass.Iec61850Kind.COND_ENUM.getValue());
		UmlAttribute pcWithSiblingArgLiteral = presCond.addAttribute(null,
				"MF" + PresenceCondition.ARG_SIBLING, UmlStereotype.ENUM);
		presCond.addAttribute(null, "XYZ", UmlStereotype.ENUM); // dummy
		assertEquals(2, presCond.getAttributes().size());

		String constraintWithSiblingArgName = "MF(a3)";
		UmlConstraint cc = UmlConstraint.basic(c, constraintWithSiblingArgName, null, "a4");

		PresenceCondition pc = cc.getPresenceCondition();
		assertSame("literal from presence condition (with sibling) found", pcWithSiblingArgLiteral,
				pc.getDefinitionLiteral());
		assertEquals("name correctly deduced", "MF", pc.getStem());
		assertEquals("argument correctly recognised", "a3", pc.getArgs());
		assertNotNull(pc.getConstraint());
		assertFalse("should not have condID", pc.isWithCondID());
		assertTrue("should have not text", pc.getText().isEmpty());
		assertEquals(constraintWithSiblingArgName, pc.getStemAndArgs());
	}
}
