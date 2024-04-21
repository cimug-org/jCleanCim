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
import java.util.List;

import org.junit.Ignore;
import org.junit.Test;
import org.tanjakostic.jcleancim.model.UmlOperation.Data;
import org.tanjakostic.jcleancim.model.UmlOperation.ReturnKind;
import org.tanjakostic.jcleancim.util.ApplicationException;

/**
 * @author tatjana.kostic@ieee.org
 * @version $Id: UmlOperationTest.java 25 2019-11-02 17:21:28Z dev978 $
 */
public class UmlOperationTest extends CommonUmlTestBase {

	// ============= Tests ===============

	@Test(expected = NullPointerException.class)
	public final void testCtorContainerNull() {
		UmlPackage mp = UmlPackage.basic(emptyModel, "mp");
		UmlClass type = UmlClass.basic(mp, "Class", "");

		new UmlOperation(null, type, new UmlObjectData("oper"), Data.empty());
	}

	@Test(expected = IllegalArgumentException.class)
	public final void testCtorReturnTypeNullForNonVoid() {
		UmlPackage mp = UmlPackage.basic(emptyModel, "mp");
		UmlClass containingClass = UmlClass.basic(mp, "containingClass", "");
		Data data = new Data(ReturnKind.OP_RET_SIMPLE, false, true, false, 0, "",
				new ArrayList<String>());

		assertTrue("return type is not void", data.getKind() == ReturnKind.OP_RET_SIMPLE);

		new UmlOperation(containingClass, null, new UmlObjectData("oper"), data);
	}

	@Test(expected = NullPointerException.class)
	public final void testCtorODataNull() {
		UmlPackage mp = UmlPackage.basic(emptyModel, "mp");
		UmlClass containingClass = UmlClass.basic(mp, "containingClass", "");

		new UmlOperation(containingClass, null, null, Data.empty());
	}

	@Test(expected = NullPointerException.class)
	public final void testCtorDataNull() {
		UmlPackage mp = UmlPackage.basic(emptyModel, "mp");
		UmlClass containingClass = UmlClass.basic(mp, "containingClass", "");

		new UmlOperation(containingClass, null, new UmlObjectData("oper"), null);
	}

	@Test(expected = IllegalArgumentException.class)
	public final void testCtorContainerAndReturnTypeFromDifferentModel() {
		UmlPackage mp = UmlPackage.basic(emptyModel, "mp");
		UmlClass containingClass = UmlClass.basic(mp, "containingClass", "");

		UmlModel otherModel = new UmlModel(emptyCfg);
		UmlClass retType = UmlClass.basic(UmlPackage.basic(otherModel, "otherModel"), "Int", "");
		Data data = new Data(ReturnKind.OP_RET_SIMPLE, false, true, false, 0, "",
				new ArrayList<String>());

		new UmlOperation(containingClass, retType, new UmlObjectData("oper"), data);
	}

	@Test
	public final void testCtorDataAccessors() {
		UmlPackage mp = UmlPackage.basic(emptyModel, "mp");
		UmlClass containingClass = UmlClass.basic(mp, "containingClass", "");

		int retTypeId = 0;
		String retTypeName = "Voltage";
		List<String> eaExcNames = Arrays.asList(new String[] { "Exc1", "Exc2" });
		Data data = new Data(ReturnKind.OP_RET_ARRAY, false, true, false, retTypeId, retTypeName,
				eaExcNames);
		UmlObjectData objData = new UmlObjectData(Integer.valueOf(33), "2345", "oper", "alias",
				null, UmlVisibility.PACKAGE.toString(), null, null);
		UmlClass nullClass = emptyModel.getNullClasses().get(mp.getNature());

		UmlOperation oper = new UmlOperation(containingClass, nullClass, objData, data);

		assertFalse("should not be constant", oper.isAbstract());
		assertTrue("should be static", oper.isStatic());
		assertFalse("should not be final", oper.isFinal());
		assertFalse("should not return void", oper.isVoidReturned());
		assertEquals(ReturnKind.OP_RET_ARRAY, oper.getKind());
		assertEquals(retTypeId, oper.getEaReturnTypeId());
		assertEquals(retTypeName, oper.getEaReturnTypeName());
		assertTrue("should contain EA Id", oper.getEaReturnTypeInfo()
				.indexOf(Integer.toString(oper.getEaReturnTypeId())) != -1);
		assertTrue("should contain EA Type",
				oper.getEaReturnTypeInfo().indexOf(oper.getEaReturnTypeName()) != -1);

		assertEquals(eaExcNames.get(1), oper.getEaExceptionTypeInfo(1));

		assertSame(containingClass, oper.getContainingClass());
		assertSame(nullClass, oper.getReturnType());
	}

	// ==================================

	@Test
	public final void testGetOwner() throws ApplicationException {
		// using non-default nature to test correct initialisation
		UmlPackage p6 = create61850Package("p6");
		UmlClass containingClass = UmlClass.basic(p6, "containingClass", "");

		UmlPackage p = UmlPackage.basic(p6.getModel(), "p");
		UmlClass retType = UmlClass.basic(p, "Int", "");

		UmlOperation oper = UmlOperation.basic(containingClass, retType, "oper");
		assertFalse("natures of containingClass and return type should differ",
				containingClass.getNature() == retType.getNature());

		assertEquals("owner always the one from containing class", containingClass.getOwner(),
				oper.getOwner());
	}

	// ---------------------------------------

	@Test
	public final void testGetNature() throws ApplicationException {
		// using non-default nature to test correct initialisation
		UmlPackage p6 = create61850Package("p6");
		UmlClass containingClass = UmlClass.basic(p6, "containingClass", "");

		UmlPackage p = UmlPackage.basic(p6.getModel(), "p");
		UmlClass retType = UmlClass.basic(p, "Int", "");

		UmlOperation oper = UmlOperation.basic(containingClass, retType, "oper");
		assertFalse("natures of containingClass and return type should differ",
				containingClass.getNature() == retType.getNature());

		assertEquals("nature for operation should be that of its container",
				containingClass.getNature(), oper.getNature());
	}

	// ---------------------------------------

	@Test
	public final void testIsInformative() {
		UmlPackage mp = UmlPackage.basic(emptyModel, "Informative");
		UmlPackage tp = UmlPackage.basic(mp, "InfTopPackage");
		UmlClass containingClass = UmlClass.basic(tp, "containingClass", "");

		UmlPackage mpNorm = UmlPackage.basic(emptyModel, "IEC61968");
		UmlClass containingClassNorm = UmlClass.basic(mpNorm, "containingClassNorm", "");

		UmlOperation oper = UmlOperation.basic(containingClass, null, "oper");
		UmlOperation operNorm = UmlOperation.basic(containingClassNorm, null, "oper");

		assertTrue("operation inf if container inf", oper.isInformative());
		assertFalse("operation inf if container inf", operNorm.isInformative());
	}

	// ---------------------------------------

	@Test
	public final void testGetQualifiedName() {
		UmlPackage mp = UmlPackage.basic(emptyModel, "mp");
		UmlClass containingClass = UmlClass.basic(mp, "containingClass", "");
		UmlClass type = UmlClass.basic(mp, "Int", "");

		UmlOperation oper = UmlOperation.basic(containingClass, type, "oper");
		assertTrue("contains container name",
				oper.getQualifiedName().contains(oper.getContainingClass().getName()));
		assertTrue("contains oper name", oper.getQualifiedName().contains(oper.getName()));
	}

	@Ignore(value = "Implement later")
	@Test
	public final void testGetSignature() {
		fail("Not yet implemented"); // TODO
	}

	// ==================================

	@Ignore(value = "Implement later")
	@Test
	public final void testGetKinds() {
		assertFalse("should not be empty", UmlOperation.getKinds(null).isEmpty());
	}

	// ==================================

	@Ignore(value = "Implement later")
	@Test
	public final void testAddParameter() {
		fail("Not yet implemented"); // TODO
	}

	@Ignore(value = "Implement later")
	@Test
	public final void testGetParameters() {
		fail("Not yet implemented"); // TODO
	}

	// ==================================

	@Ignore(value = "Implement later")
	@Test
	public final void testAddException() {
		fail("Not yet implemented"); // TODO
	}

	@Ignore(value = "Implement later")
	@Test
	public final void testGetExceptions() {
		fail("Not yet implemented"); // TODO
	}

	@Ignore(value = "Implement later")
	@Test
	public final void testGetExceptionsSignature() {
		fail("Not yet implemented"); // TODO
	}

	// ==================================

	@Ignore(value = "Implement later")
	@Test
	public final void testGetEfferentClasses() {
		fail("Not yet implemented"); // TODO
	}
}
