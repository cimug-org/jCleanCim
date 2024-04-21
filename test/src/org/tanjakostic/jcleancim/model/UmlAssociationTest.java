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

import org.apache.log4j.Logger;
import org.junit.Test;
import org.tanjakostic.jcleancim.common.Nature;
import org.tanjakostic.jcleancim.common.OwningWg;
import org.tanjakostic.jcleancim.model.TextDescription.TextKind;
import org.tanjakostic.jcleancim.model.UmlAssociation.Direction;
import org.tanjakostic.jcleancim.model.UmlAssociationEnd.Data;
import org.tanjakostic.jcleancim.model.UmlAssociationEnd.Kind;
import org.tanjakostic.jcleancim.model.UmlAssociationEnd.Navigable;
import org.tanjakostic.jcleancim.util.ApplicationException;

/**
 * @author tatjana.kostic@ieee.org
 * @version $Id: UmlAssociationTest.java 27 2019-11-23 16:29:38Z dev978 $
 */
public class UmlAssociationTest extends CommonUmlTestBase {
	private static final Logger _logger = Logger.getLogger(UmlAssociationTest.class.getName());

	private UmlAssociationEnd createAssociationEnd(UmlModel model, String typeName,
			String endName) {
		return createAssociationEnd(UmlPackage.basic(model, "mp"), typeName, endName,
				UmlVisibility.PUBLIC, UmlAssociationEnd.Data.empty());
	}

	private UmlAssociationEnd createAssociationEnd(UmlModel model, String typeName, String endName,
			Navigable navigable) {
		return createAssociationEnd(UmlPackage.basic(model, "mp"), typeName, endName,
				UmlVisibility.PUBLIC, new Data(Kind.ASSOC, UmlMultiplicity.ONE, navigable));
	}

	private UmlAssociationEnd createAssociationEnd(UmlModel model, String packageName,
			String typeName, String endName) {
		return createAssociationEnd(UmlPackage.basic(model, packageName), typeName, endName,
				UmlVisibility.PUBLIC, UmlAssociationEnd.Data.empty());
	}

	private UmlAssociationEnd createAssociationEnd(UmlPackage mp, String typeName, String endName,
			UmlVisibility visibility, UmlAssociationEnd.Data data) {
		UmlClass type = UmlClass.basic(mp, typeName, "");
		UmlObjectData objData = new UmlObjectData(Integer.valueOf(12), null, endName, "", null,
				visibility.toString(), null, null);
		return new UmlAssociationEnd(type, objData, data);
	}

	// ============= Tests ===============

	@Test(expected = NullPointerException.class)
	public final void testCtorSourceEndNull() {
		UmlObjectData objData = new UmlObjectData(Integer.valueOf(22), "1359", "", "alias", null,
				UmlVisibility.PROTECTED.toString(), null, null);
		UmlAssociation.Data data = new UmlAssociation.Data(Direction.unspecified);
		UmlAssociationEnd tEnd = createAssociationEnd(emptyModel, "Target", "TEnd");

		new UmlAssociation(null, tEnd, objData, data);
	}

	@Test(expected = NullPointerException.class)
	public final void testCtorTargetEndNull() {
		UmlObjectData objData = new UmlObjectData(Integer.valueOf(22), "1359", "", "alias", null,
				UmlVisibility.PROTECTED.toString(), null, null);
		UmlAssociation.Data data = new UmlAssociation.Data(Direction.unspecified);
		UmlAssociationEnd sEnd = createAssociationEnd(emptyModel, "Source", "SEnd");

		new UmlAssociation(sEnd, null, objData, data);
	}

	@Test(expected = NullPointerException.class)
	public final void testCtorObjDataNull() {
		UmlAssociation.Data data = new UmlAssociation.Data(Direction.unspecified);
		UmlAssociationEnd sEnd = createAssociationEnd(emptyModel, "Source", "SEnd");
		UmlAssociationEnd tEnd = createAssociationEnd(emptyModel, "Target", "TEnd");

		new UmlAssociation(sEnd, tEnd, null, data);
	}

	@Test(expected = NullPointerException.class)
	public final void testCtorDataNull() {
		UmlObjectData objData = new UmlObjectData(Integer.valueOf(22), "1359", "", "alias", null,
				UmlVisibility.PROTECTED.toString(), null, null);
		UmlAssociationEnd sEnd = createAssociationEnd(emptyModel, "Source", "SEnd");
		UmlAssociationEnd tEnd = createAssociationEnd(emptyModel, "Target", "TEnd");

		new UmlAssociation(sEnd, tEnd, objData, null);
	}

	@Test(expected = IllegalArgumentException.class)
	public final void testCtorEndTypesFromDifferentModels() throws ApplicationException {
		UmlAssociationEnd sEnd = createAssociationEnd(createEmptyModelRecognising61850Nature("mp6"),
				"Source", "SEnd");
		UmlAssociationEnd tEnd = createAssociationEnd(emptyModel, "Target", "TEnd");

		new UmlAssociation(sEnd, tEnd, new UmlObjectData(""), UmlAssociation.Data.empty());
	}

	@Test
	public final void testCtorDataAccessorsBeforeAddedToClass() {
		UmlPackage mp = UmlPackage.basic(emptyModel, "mp");
		UmlAssociationEnd sEnd = createAssociationEnd(mp, "Source", "SEnd", UmlVisibility.PRIVATE,
				new UmlAssociationEnd.Data(UmlAssociationEnd.Kind.AGGREG, UmlMultiplicity.ONE,
						Navigable.unspecified));
		UmlAssociationEnd tEnd = createAssociationEnd(mp, "Target", "TEnd", UmlVisibility.PROTECTED,
				new UmlAssociationEnd.Data(UmlAssociationEnd.Kind.ASSOC, UmlMultiplicity.OPT_MANY,
						Navigable.yes)); // this is the stinker navigability
		UmlObjectData objData = new UmlObjectData(Integer.valueOf(22), "1359", "name", "alias",
				new UmlStereotype("stereo"), UmlVisibility.PROTECTED.toString(),
				new TextDescription("doc"), new TextDescription("<p>doc", TextKind.htmlSnippet));
		UmlAssociation.Data data = new UmlAssociation.Data(Direction.unspecified);

		UmlAssociation assoc = new UmlAssociation(sEnd, tEnd, objData, data);

		assertEquals(Direction.unspecified, assoc.getNavigability());
		assertTrue("should have mismatch", assoc.isDirectionMismatchForEnds());

		assertSame(sEnd, assoc.getSourceEnd());
		assertSame(tEnd, assoc.getTargetEnd());
		assertSame(sEnd.getType(), assoc.getSource());
		assertSame(tEnd.getType(), assoc.getTarget());
		assertTrue("one end should be non-private", assoc.isNonPrivate());
		assertTrue("neither end should be public", assoc.isNonPublic());
		assertFalse("neither end is public", assoc.isAtLeastOneEndPublic());
		assertFalse("end visitibilities should be different", assoc.areEndVisibilitiesSame());
		_logger.info(assoc.toString()); // should not throw NPE
	}

	// ======================================

	@Test
	public final void testGetKinds() {
		assertFalse("should not be empty", UmlAssociation.getKinds(null).isEmpty());
	}

	// ======================================

	@Test
	public final void testGetEndsAsSource() {
		UmlAssociationEnd sEnd = createAssociationEnd(emptyModel, "Source", "SEnd", Navigable.yes);
		UmlAssociationEnd tEnd = createAssociationEnd(emptyModel, "Target", "TEnd",
				Navigable.unspecified);
		UmlAssociation assoc = UmlAssociation.basic(sEnd, tEnd);

		UmlAssociationEndPair endPairForSource = assoc.getEndsAsSource(true);
		UmlAssociationEndPair endPairForTarget = assoc.getEndsAsSource(false);

		assertSame(tEnd, endPairForSource.getOtherEnd());
		assertSame(sEnd, endPairForTarget.getOtherEnd());
	}

	@Test
	public final void testGetEndsAsSourceForSelfAssociation() {
		UmlPackage mp = UmlPackage.basic(emptyModel, "mp");
		UmlClass sourceAndTarget = UmlClass.basic(mp, "SourceAndTarget", "");
		UmlAssociationEnd sEnd = UmlAssociationEnd.basic(sourceAndTarget, "SEnd", Navigable.yes);
		UmlAssociationEnd tEnd = UmlAssociationEnd.basic(sourceAndTarget, "TEnd");
		UmlAssociation assoc = UmlAssociation.basic(sEnd, tEnd);

		UmlAssociationEndPair aePairSelfAsSource = assoc.getEndsAsSource(true);
		UmlAssociationEndPair aePairSelfAsTarget = assoc.getEndsAsSource(false);

		assertSame(tEnd, aePairSelfAsSource.getOtherEnd());
		assertSame(sEnd, aePairSelfAsTarget.getOtherEnd());
	}

	// ======================================

	@Test
	public final void testGetNature() throws ApplicationException {
		UmlPackage mp6 = create61850Package("mp6");
		UmlAssociationEnd sEnd = createAssociationEnd(mp6.getModel(), "mp", "Source", "SEnd");
		UmlAssociationEnd tEnd = createAssociationEnd(mp6.getModel(), mp6.getName(), "Target",
				"TEnd");

		UmlAssociation assoc = UmlAssociation.basic(sEnd, tEnd);
		assertFalse("natures of source and end should differ",
				sEnd.getNature() == tEnd.getNature());
		assertTrue("nature of one end should be other than CIM",
				tEnd.getNature() == Nature.IEC61850);

		assertEquals(Nature.IEC61850, assoc.getNature());
	}

	@Test
	public final void testGetOwner() throws ApplicationException {
		UmlModel model = SampleModelFixture.create();
		UmlClass psr = model.findClasses("PowerSystemResource").iterator().next();
		UmlClass asset = model.findClasses("Asset").iterator().next();
		UmlAssociation assocForPsr = psr.getAssociations().iterator().next();
		UmlAssociation assocForAsset = asset.getAssociations().iterator().next();
		assertSame("we picked one association", assocForPsr, assocForAsset);
		assertTrue("involves different owners",
				assocForPsr.involvesWg(OwningWg.WG13) && assocForPsr.involvesWg(OwningWg.WG14));

		assertEquals("more dependent is the owner", OwningWg.WG14, assocForPsr.getOwner());
	}

	// --------------------------

	@Test
	public final void testIsInformative() {
		UmlPackage mp = UmlPackage.basic(emptyModel, "mp");
		UmlClass source = UmlClass.basic(mp, "Source", "");
		UmlClass target = UmlClass.basic(mp, "Target", "");
		assertFalse("source should be normative", source.isInformative());
		assertFalse("target should be normative", target.isInformative());

		UmlAssociation assoc = addInfAssociation(source, target);
		assertTrue("only assoc stereotype used for check, not assoc ends", assoc.isInformative());
	}

	// --------------------------

	@Test
	public final void testGetQualifiedName() {
		UmlAssociationEnd sEnd = createAssociationEnd(emptyModel, "Source", "SEnd");
		UmlAssociationEnd tEnd = createAssociationEnd(emptyModel, "Target", "TEnd");
		UmlAssociation assoc = UmlAssociation.basic(sEnd, tEnd);

		assertTrue("contains source type name",
				assoc.getQualifiedName().contains(sEnd.getType().getName()));
		assertTrue("contains target type name",
				assoc.getQualifiedName().contains(tEnd.getType().getName()));
		assertTrue("contains source end name", assoc.getQualifiedName().contains(sEnd.getName()));
		assertTrue("contains target end name", assoc.getQualifiedName().contains(tEnd.getName()));
	}

	// ======================================

	@Test
	public final void testIsMapping() throws ApplicationException {
		UmlPackage mp6 = create61850Package("mp6");
		UmlAssociationEnd sEnd = createAssociationEnd(mp6.getModel(), "mp", "Source", "SEnd");
		UmlAssociationEnd tEnd = createAssociationEnd(mp6.getModel(), mp6.getName(), "Target",
				"TEnd");

		UmlAssociation assoc = UmlAssociation.basic(sEnd, tEnd);
		assertTrue("should be mapping with ends of different natures", assoc.isMapping());
	}

	// --------------------------

	@Test
	public final void testIsWithinSameWgTrue() throws ApplicationException {
		UmlModel model = SampleModelFixture.create();
		UmlClass eq = model.findClasses("Equipment").iterator().next();
		UmlClass eqCont = model.findClasses("EquipmentContainer").iterator().next();
		UmlAssociation assocForEq = eq.getAssociations().iterator().next();
		UmlAssociation assocForEqCont = eqCont.getAssociations().iterator().next();
		assertSame("we picked one association", assocForEq, assocForEqCont);

		assertTrue("ends should be with the same owner", assocForEq.isWithinSameWg());
	}

	@Test
	public final void testIsWithinSameWgFalse() throws ApplicationException {
		UmlModel model = SampleModelFixture.create();
		UmlClass psr = model.findClasses("PowerSystemResource").iterator().next();
		UmlClass asset = model.findClasses("Asset").iterator().next();
		UmlAssociation assocForPsr = psr.getAssociations().iterator().next();
		UmlAssociation assocForAsset = asset.getAssociations().iterator().next();
		assertSame("we picked one association", assocForPsr, assocForAsset);

		assertFalse("ends should be with the different owners", assocForPsr.isWithinSameWg());
	}

	// --------------------------

	@Test
	public final void testInvolvesWg() throws ApplicationException {
		UmlModel model = SampleModelFixture.create();
		UmlClass psr = model.findClasses("PowerSystemResource").iterator().next();
		UmlClass asset = model.findClasses("Asset").iterator().next();
		UmlAssociation assocForPsr = psr.getAssociations().iterator().next();
		UmlAssociation assocForAsset = asset.getAssociations().iterator().next();
		assertSame("we picked one association", assocForPsr, assocForAsset);

		assertTrue("involves one owner", assocForPsr.involvesWg(OwningWg.WG13));
		assertTrue("involves other owner", assocForPsr.involvesWg(OwningWg.WG14));
	}
}
