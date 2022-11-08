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

import org.junit.Test;
import org.tanjakostic.jcleancim.util.ApplicationException;

/**
 * @author tatjana.kostic@ieee.org
 * @version $Id: UmlDependencyTest.java 25 2019-11-02 17:21:28Z dev978 $
 */
public class UmlDependencyTest extends CommonUmlTestBase {

	// ============= Tests ===============

	// null conditions tested with UmlStructureTest

	@Test
	public final void testAccessorsPacakageDepAfterInitialisation() {
		UmlPackage mp = UmlPackage.basic(emptyModel, "mp");
		UmlPackage mp2 = UmlPackage.basic(emptyModel, "mp2");
		UmlObjectData objData = new UmlObjectData(Integer.valueOf(45), "08921", "", "", null, null,
				null, null);
		UmlDependency.Data data = new UmlDependency.Data();

		UmlDependency dep = new UmlDependency(mp, mp2, objData, data);

		assertSame(mp, dep.getSource());
		assertSame(mp2, dep.getTarget());
		assertTrue("should be for package", dep.isForPackage());
		assertSame(UmlDependency.Kind.PACKAGE, dep.getKind());
	}

	@Test
	public final void testAccessorsClassDepAfterInitialisation() {
		UmlClass c = UmlClass.basic(UmlPackage.basic(emptyModel, "mp"), "C", "");
		UmlClass c2 = UmlClass.basic(UmlPackage.basic(emptyModel, "mp"), "C2", "");
		UmlObjectData objData = new UmlObjectData(Integer.valueOf(45), "08921", "", "", null, null,
				null, null);
		UmlDependency.Data data = new UmlDependency.Data();

		UmlDependency dep = new UmlDependency(c, c2, objData, data);

		assertSame(c, dep.getSource());
		assertSame(c2, dep.getTarget());
		assertFalse("should be for class", dep.isForPackage());
		assertSame(UmlDependency.Kind.CLASS, dep.getKind());
	}

	// ==================================

	@Test
	public final void testGetKinds() {
		assertFalse("should not be empty", UmlDiagram.getKinds(null).isEmpty());
	}

	@Test
	public final void testGetQualifiedName() {
		UmlPackage mp = UmlPackage.basic(emptyModel, "mp");
		UmlPackage mp2 = UmlPackage.basic(emptyModel, "mp2");
		UmlDependency dep = UmlDependency.basic(mp, mp2);

		assertTrue("contains source name",
				dep.getQualifiedName().contains(dep.getSource().getName()));
		assertTrue("contains target name",
				dep.getQualifiedName().contains(dep.getTarget().getName()));
	}

	// ==================================

	@Test
	public final void testGetNature() throws ApplicationException {
		UmlPackage mp6 = create61850Package("mp6");
		UmlPackage mp = UmlPackage.basic(mp6.getModel(), "mp");

		UmlDependency dep = UmlDependency.basic(mp6, mp);
		assertTrue("natures of ends should be different", mp6.getNature() != mp.getNature());

		assertEquals("nature should be that of the source", mp6.getNature(), dep.getNature());
	}

	@Test
	public final void testGetOwnerAndIsWithinSameWg() throws ApplicationException {
		UmlPackage mp6 = create61850Package("mp6");
		UmlPackage mp = UmlPackage.basic(mp6.getModel(), "mp");

		UmlDependency dep = UmlDependency.basic(mp6, mp);
		assertTrue("owners of ends should be different", mp6.getOwner() != mp.getOwner());

		assertEquals("owner should be that of the source", mp6.getOwner(), dep.getOwner());
		assertFalse("should not be the same WG", dep.isWithinSameWg());
	}

	@Test
	public final void testIsInformative() {
		UmlPackage mp = UmlPackage.basic(emptyModel, "mp");
		UmlPackage top = UmlPackage.basic(mp, "tp");
		UmlPackage infPackage = UmlPackage.basic(top, "InfPackage");
		assertTrue("one end should be INF", infPackage.isInformative());

		UmlDependency dep = UmlDependency.basic(infPackage, top);

		assertFalse("dependency should never be INF", dep.isInformative());
	}
}
