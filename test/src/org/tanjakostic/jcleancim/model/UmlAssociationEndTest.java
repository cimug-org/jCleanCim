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

import org.apache.log4j.Logger;
import org.junit.Test;
import org.tanjakostic.jcleancim.model.UmlAssociation.Direction;
import org.tanjakostic.jcleancim.model.UmlAssociationEnd.Kind;
import org.tanjakostic.jcleancim.model.UmlAssociationEnd.Navigable;
import org.tanjakostic.jcleancim.util.ApplicationException;

/**
 * Note: Because the tested class (for association ends) is more of a helper class and the full
 * initialisation of an instance happens only after an association gets created from 2 such ends,
 * the setup is somewhat cumbersome, so we have unusually bulky methods for the case
 * "half-initialised" and fully initialised. Things are properly tested for associations.
 *
 * @author tatjana.kostic@ieee.org
 * @version $Id: UmlAssociationEndTest.java 27 2019-11-23 16:29:38Z dev978 $
 */
public class UmlAssociationEndTest extends CommonUmlTestBase {
	private static final Logger _logger = Logger.getLogger(UmlAssociationEndTest.class.getName());

	// ============= Tests ===============

	@SuppressWarnings("unused")
	@Test(expected = NullPointerException.class)
	public final void testCtorTypeNull() {
		new UmlAssociationEnd(null, new UmlObjectData("Source"), UmlAssociationEnd.Data.empty());
	}

	@SuppressWarnings("unused")
	@Test(expected = NullPointerException.class)
	public final void testCtorObjDataNull() {
		UmlPackage mp = UmlPackage.basic(emptyModel, "mp");
		UmlClass type = UmlClass.basic(mp, "Type", "");

		new UmlAssociationEnd(type, null, UmlAssociationEnd.Data.empty());
	}

	@SuppressWarnings("unused")
	@Test(expected = NullPointerException.class)
	public final void testCtorDataNull() {
		UmlPackage mp = UmlPackage.basic(emptyModel, "mp");
		UmlClass type = UmlClass.basic(mp, "Type", "");

		new UmlAssociationEnd(type, new UmlObjectData("Source"), null);
	}

	@Test
	public final void testCtorDataAccessorsBeforeAddedToAssociation() {
		UmlPackage mp = UmlPackage.basic(emptyModel, "mp");
		UmlClass type = UmlClass.basic(mp, "Type", "");
		UmlAssociationEnd.Data data = new UmlAssociationEnd.Data(Kind.AGGREG, UmlMultiplicity.ONE,
				Navigable.yes);
		UmlObjectData objData = new UmlObjectData(Integer.valueOf(22), "1359", "oneEnd", "alias",
				null, UmlVisibility.PROTECTED.toString(), null, null);

		UmlAssociationEnd end = new UmlAssociationEnd(type, objData, data);

		assertEquals(Kind.AGGREG, end.getKind());
		assertEquals(UmlMultiplicity.ONE, end.getMultiplicity());
		assertEquals(Navigable.yes, end.getNavigable());

		assertSame(type, end.getType());
		assertNull("before added to assoc", end.getContainingAssociation());
		assertNull("before added to assoc", end.getOwner());
		assertSame(type.getNature(), end.getNature());
		assertFalse("before added to assoc", end.isInformative());
		assertTrue(end.isAggregation());
		assertFalse(end.isAssociation());
		assertFalse(end.isComposition());
		assertFalse(end.isOther());
		assertFalse("before added to assoc", end.isSource());
		assertFalse("before added to assoc", end.isTarget());

		assertTrue("before added to assoc, contains at least own name",
				end.getQualifiedName().contains(end.getName()));
		end.toString(); // should not throw NPE
	}

	@Test
	public final void testCtorDataAccessorsAfterAddedToAssociation() throws ApplicationException {
		UmlPackage mp = UmlPackage.basic(emptyModel, "mp"); // model package can't be informative...
		UmlPackage tp = UmlPackage.basic(mp, "Informative"); // ...this one is informative.
		UmlClass source = UmlClass.basic(tp, "Source", "");
		UmlAssociationEnd.Data seData = new UmlAssociationEnd.Data(Kind.AGGREG,
				UmlMultiplicity.EMPTY, Navigable.yes);
		String seName = "seName";
		UmlObjectData seObjData = new UmlObjectData(Integer.valueOf(22), "1359", seName, "alias",
				null, UmlVisibility.PUBLIC.toString(), null, null);
		UmlAssociationEnd sEnd = new UmlAssociationEnd(source, seObjData, seData);

		UmlPackage mp2 = UmlPackage.basic(mp.getModel(), "mp2");
		UmlClass target = UmlClass.basic(mp2, "Target", "");
		UmlAssociationEnd.Data teData = new UmlAssociationEnd.Data(Kind.AGGREG, UmlMultiplicity.ONE,
				Navigable.no);
		String teName = "teName";
		UmlObjectData teObjData = new UmlObjectData(Integer.valueOf(23), "9753", teName, "alias",
				null, UmlVisibility.PROTECTED.toString(), null, null);
		UmlAssociationEnd tEnd = new UmlAssociationEnd(target, teObjData, teData);

		// create association from two ends...
		UmlAssociation assoc = UmlAssociation.basic(sEnd, tEnd);

		// ... and test only for one end (source):
		assertSame(source, sEnd.getType());
		assertSame(assoc, sEnd.getContainingAssociation());
		assertSame("owner of other end", target.getOwner(), sEnd.getOwner());
		assertSame("nature of type of this end", source.getNature(), sEnd.getNature());
		assertTrue(tp.isInformative());
		assertTrue("any end informative", sEnd.isInformative());
		assertTrue("is source", sEnd.isSource());
		assertFalse("is not target", sEnd.isTarget());

		assertTrue("contains own name", sEnd.getQualifiedName().contains(sEnd.getName()));
		assertTrue("contains other end's type name",
				sEnd.getQualifiedName().contains(target.getName()));
		_logger.info("sEnd = " + sEnd.toString());
		_logger.info("tEnd = " + tEnd.toString());
		_logger.info("getEndsFor(source) = " + assoc.getEndsAsSource(true));
		_logger.info("getEndsFor(target) = " + assoc.getEndsAsSource(false));
		// sEnd = OTHER_CIM [?..?] Target.seName (navigable)
		// tEnd = OTHER_CIM (protected) [1..1] Source.teName (non-navigable)
		// getEndsFor(source) = myEnd: OTHER_CIM [?..?] Target.seName (navigable),
		// otherEnd: OTHER_CIM (protected) [1..1] Source.teName (non-navigable)
		// getEndsFor(target) = myEnd: OTHER_CIM (protected) [1..1] Source.teName (non-navigable),
		// otherEnd: OTHER_CIM [?..?] Target.seName (navigable)
	}

	// ======================================

	@Test(expected = RuntimeException.class)
	public final void testSetContainingAssociationAlreadySet() {
		UmlPackage mp = UmlPackage.basic(emptyModel, "mp");
		UmlClass source = UmlClass.basic(mp, "Source", "");
		UmlAssociationEnd end = UmlAssociationEnd.basic(source, "SEnd");

		UmlAssociation.basic(end, end);
	}

	@Test
	public final void testSetContainingAssociation() {
		UmlPackage mp = UmlPackage.basic(emptyModel, "mp");
		UmlClass source = UmlClass.basic(mp, "Source", "");
		UmlAssociationEnd sEnd = UmlAssociationEnd.basic(source, "SEnd");
		UmlAssociationEnd tEnd = UmlAssociationEnd.basic(source, "tEnd");

		assertNull("before added to assoc", sEnd.getContainingAssociation());
		assertNull("before added to assoc", tEnd.getContainingAssociation());

		UmlAssociation assoc = UmlAssociation.basic(sEnd, tEnd);

		assertSame("added to assoc", assoc, sEnd.getContainingAssociation());
		assertSame("added to assoc", assoc, tEnd.getContainingAssociation());
	}

	// =============================

	/**
	 * This kind of inconsistency should in theory not happen, but the repository from which we
	 * create models (EA) allows for this condition to happen, so the in-memory UML model must be
	 * able to reflect it. Validation has rules that check for this condition and produce
	 * appropriate validation report entry.
	 */
	@Test
	public final void testIsDirectionMismatchForEnds() {
		UmlPackage mp = UmlPackage.basic(emptyModel, "mp");
		UmlClass source = UmlClass.basic(mp, "Source", "");
		UmlAssociationEnd sEnd = UmlAssociationEnd.basic(source, "SEnd", Navigable.yes);
		assertEquals("source should be navigable", Navigable.yes, sEnd.getNavigable());

		UmlAssociationEnd tEnd = UmlAssociationEnd.basic(source, "tEnd");
		assertEquals("target navigability should be unspecified", Navigable.yes,
				sEnd.getNavigable());

		UmlAssociation assoc = UmlAssociation.basic(sEnd, tEnd);
		assertEquals(Direction.unspecified, assoc.getNavigability());

		assertTrue("association has unspecified navigabilities, despite one navigable end",
				assoc.isDirectionMismatchForEnds());
	}

	// =============================

	@Test
	public final void testIsDeprecatedStereotypeOnSelf() {
		UmlPackage mp = UmlPackage.basic(emptyModel, "mp");
		UmlClass source = UmlClass.basic(mp, "Source", "");
		UmlAssociationEnd sEnd = UmlAssociationEnd.basic(source, "SEnd",
				new UmlStereotype(UmlStereotype.DEPRECATED));
		assertTrue("source should be depr. even before added to assoc.", sEnd.isDeprecated());

		UmlAssociationEnd tEnd = UmlAssociationEnd.basic(source, "tEnd");
		UmlAssociation assoc = UmlAssociation.basic(sEnd, tEnd);

		assertFalse("association is not deprecated", assoc.isDeprecated());
		assertTrue("source stays deprecated (even when assoc. is not)", sEnd.isDeprecated());
		assertFalse("target picks non-deprecated from its association", tEnd.isDeprecated());
	}

	@Test
	public final void testIsDeprecatedWithDeprecatedAssociation() {
		UmlPackage mp = UmlPackage.basic(emptyModel, "mp");
		UmlClass source = UmlClass.basic(mp, "Source", "");
		UmlAssociationEnd sEnd = UmlAssociationEnd.basic(source, "SEnd");
		assertFalse("source should not be depr. before added to depr. assoc.", sEnd.isDeprecated());

		UmlAssociationEnd tEnd = UmlAssociationEnd.basic(source, "tEnd");
		UmlAssociation assoc = UmlAssociation.basic(sEnd, tEnd,
				new UmlStereotype("any", UmlStereotype.DEPRECATED));
		assertTrue("association is deprecated", assoc.isDeprecated());
		assertTrue("source end now picks deprecated from its association", sEnd.isDeprecated());
		assertTrue("target end now picks deprecated from its association", tEnd.isDeprecated());
	}

	@Test
	public final void testIsDeprecatedWithNonDeprecatedAssociation() {
		UmlPackage mp = UmlPackage.basic(emptyModel, "mp");
		UmlClass source = UmlClass.basic(mp, "Source", "");
		UmlAssociationEnd sEnd = UmlAssociationEnd.basic(source, "SEnd");
		assertFalse("source should not be depr. before added to depr. assoc.", sEnd.isDeprecated());

		UmlAssociationEnd tEnd = UmlAssociationEnd.basic(source, "tEnd");
		UmlAssociation assoc = UmlAssociation.basic(sEnd, tEnd);
		assertFalse("association is not deprecated", assoc.isDeprecated());
		assertFalse("source end now picks non-deprecated from its association",
				sEnd.isDeprecated());
		assertFalse("target end now picks non-deprecated from its association",
				tEnd.isDeprecated());
	}

	// =============================

	@Test
	public final void testIsNamedWithoutMultiplicity() {
		UmlPackage mp = UmlPackage.basic(emptyModel, "mp");
		UmlClass type = UmlClass.basic(mp, "Type", "");

		String name = "End";
		UmlAssociationEnd end = new UmlAssociationEnd(type, new UmlObjectData(name),
				new UmlAssociationEnd.Data(Kind.AGGREG, UmlMultiplicity.EMPTY,
						Navigable.unspecified));
		assertEquals(UmlMultiplicity.EMPTY, end.getMultiplicity());
		assertEquals(name, end.getName());

		assertTrue("named without multiplicity", end.isNamedWithoutMultiplicity());
	}

	// =============================

	@Test
	public final void testGetKinds() {
		assertFalse("should not be empty", UmlAssociationEnd.getKinds(null).isEmpty());
	}
}
