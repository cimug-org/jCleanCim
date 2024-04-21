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

import org.junit.Test;
import org.tanjakostic.jcleancim.util.ApplicationException;

/**
 * @author tatjana.kostic@ieee.org
 * @version $Id: UmlSkippedTest.java 25 2019-11-02 17:21:28Z dev978 $
 */
public class UmlSkippedTest extends CommonUmlTestBase {

	// ============= Tests ===============

	// null conditions tested with UmlStructureTest

	@Test
	public final void testCtorElement() {
		UmlPackage mp = UmlPackage.basic(emptyModel, "mp");
		UmlObjectData objData = new UmlObjectData(Integer.valueOf(98), "3456", "skipped name", "",
				null, null, null, null);
		String otherEndName = "otherEndName";
		UmlSkipped.Data data = new UmlSkipped.Data(UmlSkipped.Kind.BOUNDARY, false, otherEndName);
		UmlSkipped skippedElem = new UmlSkipped(mp, objData, data);

		assertEquals(UmlSkipped.Kind.BOUNDARY, skippedElem.getKind());
		assertFalse("should not be connector", skippedElem.isConnector());
		assertNull("provided other names should be ignored for skipped element",
				skippedElem.getOtherEndName());
		assertTrue("should be for package", skippedElem.isForPackage());
		assertSame(mp, skippedElem.getContainer());
	}

	@Test
	public final void testCtorConnector() {
		UmlPackage mp = UmlPackage.basic(emptyModel, "mp");
		UmlClass c = UmlClass.basic(mp, "C", "");
		UmlObjectData objData = new UmlObjectData(Integer.valueOf(98), "3456", "skipped name", "",
				null, null, null, null);
		String otherEndName = "noteName";
		UmlSkipped.Data data = new UmlSkipped.Data(UmlSkipped.Kind.NOTE_LINK, true, otherEndName);
		UmlSkipped skippedConn = new UmlSkipped(c, objData, data);

		assertEquals(UmlSkipped.Kind.NOTE_LINK, skippedConn.getKind());
		assertTrue("should be connector", skippedConn.isConnector());
		assertEquals(otherEndName, skippedConn.getOtherEndName());
		assertFalse("should be for class", skippedConn.isForPackage());
		assertSame(c, skippedConn.getContainer());
	}

	// ==================================

	@Test
	public final void testGetQualifiedNameElement() {
		UmlPackage mp = UmlPackage.basic(emptyModel, "mp");
		UmlSkipped skipped = UmlSkipped.basicElement(mp);

		assertTrue("contains container name",
				skipped.getQualifiedName().contains(skipped.getContainer().getName()));
	}

	@Test
	public final void testGetQualifiedNameConnector() {
		UmlPackage mp = UmlPackage.basic(emptyModel, "mp");
		UmlSkipped skipped = UmlSkipped.basicConnector(mp);

		assertTrue("contains other end name",
				skipped.getQualifiedName().contains(skipped.getOtherEndName()));
	}

	// ==================================

	@Test
	public final void testGetNature() throws ApplicationException {
		// using non-default nature to test correct initialisation
		UmlPackage mp = create61850Package("mp");
		UmlSkipped skipped = UmlSkipped.basicConnector(mp);

		assertSame(skipped.getContainer().getNature(), skipped.getNature());
	}

	@Test
	public final void testGetOwner() throws ApplicationException {
		// using non-default nature to test correct initialisation
		UmlPackage mp = create61850Package("mp");
		UmlSkipped skipped = UmlSkipped.basicConnector(mp);

		assertSame(skipped.getContainer().getOwner(), skipped.getOwner());
	}

	@Test
	public final void testIsInformative() {
		UmlPackage mp = UmlPackage.basic(emptyModel, "mp");
		UmlPackage top = UmlPackage.basic(mp, "tp");
		UmlPackage infPackage = UmlPackage.basic(top, "InfPackage");
		assertTrue("container should be INF", infPackage.isInformative());

		UmlSkipped skipped = UmlSkipped.basicConnector(infPackage);

		assertTrue("should be INF", skipped.isInformative());
	}
}
