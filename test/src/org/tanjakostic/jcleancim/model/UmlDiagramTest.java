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

import java.io.File;

import org.junit.Test;
import org.tanjakostic.jcleancim.util.ApplicationException;

/**
 * @author tatjana.kostic@ieee.org
 * @version $Id: UmlDiagramTest.java 25 2019-11-02 17:21:28Z dev978 $
 */
public class UmlDiagramTest extends CommonUmlTestBase {

	public static final String PIC_FILENAME = "pic.png";

	private File getPicFile() {
		return new File(PIC_FILENAME);
	}

	// ============= Tests ===============

	// null conditions tested with UmlStructureTest

	@Test
	public final void testCtorDataAccessors() {
		UmlPackage mp = UmlPackage.basic(emptyModel, "mp");
		UmlObjectData objData = new UmlObjectData(Integer.valueOf(23), "3027", "dia", "", null,
				null, null, null);
		File picFile = getPicFile();
		UmlDiagram.Data data = new UmlDiagram.Data(UmlDiagram.Kind.COMPONENT, true, true);
		UmlDiagram dia = new UmlDiagram(mp, picFile, objData, data);

		assertSame("should have the real pic", picFile, dia.getPic());
		assertFalse("should have the real pic", dia.isBlankPic());
		assertSame("should be component diagram", UmlDiagram.Kind.COMPONENT, dia.getKind());
		assertTrue("should be portrait", dia.isPortrait());
		assertTrue("should support tags", dia.isSupportsTags());

		assertSame(mp, dia.getContainer());
		assertTrue("container should be package", dia.isForPackage());
	}

	@Test
	public final void testAccessorsAfterInitialisation() {
		UmlPackage mp = UmlPackage.basic(emptyModel, "mp");
		UmlClass c = UmlClass.basic(mp, "Class", "");
		UmlDiagram dia = UmlDiagram.basic(c, "dia for class");

		assertSame(c, dia.getContainer());
		assertFalse("container should be class", dia.isForPackage());
	}

	// ----------------------------------

	@Test(expected = InvalidTagException.class)
	public final void testValidateTag() {
		boolean supportsTags = false;

		UmlPackage mp = UmlPackage.basic(emptyModel, "mp");
		UmlDiagram.Data data = new UmlDiagram.Data(UmlDiagram.Kind.COMPONENT, true, supportsTags);
		UmlDiagram dia = new UmlDiagram(mp, getPicFile(), new UmlObjectData("dia"), data);
		assertFalse("should not support tags", dia.isSupportsTags());

		dia.validateTag("name", "value");
	}

	// ----------------------------------

	@Test
	public final void testIsBlankPic() {
		File realPic = null;

		UmlPackage mp = UmlPackage.basic(emptyModel, "mp");
		UmlDiagram.Data data = new UmlDiagram.Data(UmlDiagram.Kind.COMPONENT, true, false);
		UmlDiagram dia = new UmlDiagram(mp, realPic, new UmlObjectData("dia"), data);

		assertTrue("should use default blank pic", dia.isBlankPic());
		assertFalse("blank pic should not be null", null == dia.getPic());
	}

	// ==================================

	@Test
	public final void testGetKinds() {
		assertFalse("should not be empty", UmlDiagram.getKinds(null).isEmpty());
	}

	@Test
	public final void testGetQualifiedName() {
		UmlPackage mp = UmlPackage.basic(emptyModel, "mp");
		UmlDiagram dia = UmlDiagram.basic(mp, "dia");

		assertTrue("contains container name",
				dia.getQualifiedName().contains(dia.getContainer().getName()));
		assertTrue("contains diagram name", dia.getQualifiedName().contains(dia.getName()));
	}

	// ==================================

	@Test
	public final void testGetOwner() {
		UmlPackage mp = UmlPackage.basic(emptyModel, "mp");
		UmlDiagram dia = UmlDiagram.basic(mp, "dia");

		assertEquals(mp.getOwner(), dia.getOwner());
	}

	@Test
	public final void testGetNature() throws ApplicationException {
		// using non-default nature to test correct initialisation
		UmlPackage p6 = create61850Package("p6");
		UmlDiagram dia = UmlDiagram.basic(p6, "dia");

		assertEquals(p6.getNature(), dia.getNature());
	}

	@Test
	public final void testIsInformative() {
		UmlPackage mp = UmlPackage.basic(emptyModel, "mp");
		UmlPackage top = UmlPackage.basic(mp, "tp");
		UmlPackage infPackage = UmlPackage.basic(top, "InfPackage");
		assertTrue("container should be INF", infPackage.isInformative());

		UmlDiagram dia = UmlDiagram.basic(infPackage, "dia for class");

		assertTrue("should be INF", dia.isInformative());
	}
}
