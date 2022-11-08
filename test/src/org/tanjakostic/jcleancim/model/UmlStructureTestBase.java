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
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.util.Collection;
import java.util.HashSet;

import org.junit.Test;
import org.tanjakostic.jcleancim.model.UmlStructure.Data;

/**
 * @author tatjana.kostic@ieee.org
 * @version $Id: UmlStructureTestBase.java 15 2016-07-12 15:11:42Z dev978 $
 */
abstract public class UmlStructureTestBase extends CommonUmlTestBase {

	/** Returns an instance created with the constructor using given parameters. */
	abstract protected UmlStructure createInstance(UmlObjectData objData, Data data)
			throws Exception;

	/**
	 * Returns an instance for the <code>model</code> created with the constructor using given
	 * parameters.
	 */
	abstract protected UmlStructure createInstance(UmlModel model, UmlObjectData objData, Data data)
			throws Exception;

	/**
	 * Returns an instance of some other concrete subtype of {@link UmlStructure}; if
	 * <code>model</code> is null, creates an arbitrary model.
	 */
	abstract protected UmlStructure createInstanceOfOtherType(UmlModel model) throws Exception;

	// ============= Tests ===============

	@Test(expected = NullPointerException.class)
	public final void testCtorODataNull() throws Exception {
		createInstance(null, Data.empty());
	}

	@Test(expected = NullPointerException.class)
	public final void testCtorDataNull() throws Exception {
		createInstance(new UmlObjectData("structure"), null);
	}

	// ------------------------------

	@Test
	public final void testGetModel() throws Exception {
		UmlStructure s = createInstance(new UmlObjectData("structure"), Data.empty());

		assertNotNull(s.getModel());
	}

	// ------------------------------

	@Test(expected = NullPointerException.class)
	public final void testAddSkippedUmlItemODataNull() throws Exception {
		UmlStructure s = createInstance(new UmlObjectData("structure"), Data.empty());
		s.addSkippedUmlItem(null, UmlSkipped.Data.empty(true));
	}

	@Test(expected = NullPointerException.class)
	public final void testAddSkippedUmlItemDataNull() throws Exception {
		UmlStructure s = createInstance(new UmlObjectData("structure"), Data.empty());
		s.addSkippedUmlItem(new UmlObjectData("skippedConn"), null);
	}

	@Test
	public final void testAddSkippedUmlItemReturnsAdded() throws Exception {
		UmlStructure s = createInstance(new UmlObjectData("structure"), Data.empty());

		String skippedUuid = "01234";
		UmlObjectData objData = new UmlObjectData(skippedUuid, "skippedConn", null);
		UmlSkipped skipped = s.addSkippedUmlItem(objData, UmlSkipped.Data.empty(true));

		assertEquals(skippedUuid, skipped.getUuid());
	}

	@Test
	public final void testAddSkippedUmlItemOneConnectorAndOneElement() throws Exception {
		UmlStructure s = createInstance(new UmlObjectData("structure"), Data.empty());

		s.addSkippedUmlItem("skippedConn", true);
		s.addSkippedUmlItem("skippedElem", false);

		assertEquals("two items added to self", 2, s.getSkippedUmlItems().size());
	}

	@Test
	public final void testAddSkippedUmlItemAddWithExistingUuid() throws Exception {
		UmlStructure s = createInstance(new UmlObjectData("structure"), Data.empty());

		UmlSkipped skippedConn = s.addSkippedUmlItem("skippedConn", true);
		s.addSkippedUmlItem("skippedElem", false);

		assertEquals("two items added", 2, s.getSkippedUmlItems().size());

		UmlObjectData duplicateOData = new UmlObjectData(skippedConn.getUuid(), "anyName", null);
		UmlSkipped duplicate = s.addSkippedUmlItem(duplicateOData, UmlSkipped.Data.empty(false));

		assertEquals("new connector not added", 2, s.getSkippedUmlItems().size());
		assertSame("returns the existing connector", skippedConn, duplicate);
	}

	// ------------------------------

	@Test(expected = NullPointerException.class)
	public final void testAddDependencyTargetNull() throws Exception {
		UmlStructure source = createInstance(new UmlObjectData("source"), Data.empty());

		source.addDependency(null, new UmlObjectData("dep"), UmlDependency.Data.empty());
	}

	@Test(expected = NullPointerException.class)
	public final void testAddDependencyODataNull() throws Exception {
		UmlStructure source = createInstance(new UmlObjectData("source"), Data.empty());
		UmlStructure target = createInstance(new UmlObjectData("target"), Data.empty());

		source.addDependency(target, null, UmlDependency.Data.empty());
	}

	@Test(expected = NullPointerException.class)
	public final void testAddDependencyDataNull() throws Exception {
		UmlStructure source = createInstance(new UmlObjectData("source"), Data.empty());
		UmlStructure target = createInstance(source.getModel(), new UmlObjectData("target"),
				Data.empty());

		source.addDependency(target, new UmlObjectData("dep"), null);
	}

	@Test(expected = IllegalArgumentException.class)
	public final void testAddDependencySourceAndTargetSame() throws Exception {
		UmlStructure source = createInstance(new UmlObjectData("source"), Data.empty());

		source.addDependency(source, new UmlObjectData("dep"), UmlDependency.Data.empty());
	}

	@Test(expected = IllegalArgumentException.class)
	public final void testAddDependencySourceAndTargetOfDifferentType() throws Exception {
		UmlStructure source = createInstance(new UmlObjectData("source"), Data.empty());
		UmlStructure target = createInstanceOfOtherType(source.getModel());

		source.addDependency(target, new UmlObjectData("dep"), UmlDependency.Data.empty());
	}

	@Test(expected = IllegalArgumentException.class)
	public final void testAddDependencySourceAndTargetFromDifferentModel() throws Exception {
		UmlStructure source = createInstance(new UmlObjectData("source"), Data.empty());
		UmlStructure target = createInstanceOfOtherType(null);

		source.addDependency(target, new UmlObjectData("dep"), UmlDependency.Data.empty());
	}

	@Test
	public final void testAddDependencyReturnsAdded() throws Exception {
		UmlStructure source = createInstance(new UmlObjectData("source"), Data.empty());
		UmlStructure target = createInstance(source.getModel(), new UmlObjectData("target"),
				Data.empty());

		String depUuid = "01234";
		UmlObjectData objData = new UmlObjectData(depUuid, "dep", null);
		UmlDependency dep = source.addDependency(target, objData, UmlDependency.Data.empty());

		assertEquals(depUuid, dep.getUuid());
	}

	@Test
	public final void testAddDependencyAddWithExistingUuid() throws Exception {
		UmlStructure source = createInstance(new UmlObjectData("source"), Data.empty());
		UmlStructure target = createInstance(source.getModel(), new UmlObjectData("target"),
				Data.empty());

		UmlDependency existing = source.addDependency(target);

		assertEquals("one dependency added as source", 1, source.getDependenciesAsSource().size());
		assertEquals("one dependency added as target", 1, target.getDependenciesAsTarget().size());

		UmlObjectData duplicateOData = new UmlObjectData(existing.getUuid(), "anyName", null);
		UmlDependency duplicate = source.addDependency(target, duplicateOData,
				UmlDependency.Data.empty());

		assertEquals("new dependency not added as source", 1, source.getDependenciesAsSource()
				.size());
		assertEquals("new dependency not added as target", 1, target.getDependenciesAsTarget()
				.size());
		assertEquals("new dependency not added to model", 1, target.getModel().getDependencies()
				.size());
		assertSame("returns the existing dependency", existing, duplicate);
	}

	@Test
	public final void testAddDependencyAddedToSourceTargetAndModel() throws Exception {
		UmlStructure source = createInstance(new UmlObjectData("source"), Data.empty());
		UmlStructure target = createInstance(source.getModel(), new UmlObjectData("target"),
				Data.empty());
		source.addDependency(target);

		assertEquals("one dependency added as source", 1, source.getDependenciesAsSource().size());
		assertEquals("one dependency added as target", 1, target.getDependenciesAsTarget().size());
		assertEquals("one dependency added to model", 1, target.getModel().getDependencies().size());
	}

	// ------------------------------

	@Test
	public final void testIsSelfDependent() throws Exception {
		UmlStructure source = createInstance(new UmlObjectData("source"), new Data(true));

		assertTrue("is self-dependent", source.isSelfDependent());
	}

	// ------------------------------

	@Test
	public final void testCollectDependencyEfferentStructures() throws Exception {
		UmlStructure s1 = createInstance(new UmlObjectData("s1"), new Data(true));
		UmlStructure s2 = createInstance(s1.getModel(), new UmlObjectData("s2"), new Data(true));
		UmlStructure s3 = createInstance(s1.getModel(), new UmlObjectData("s3"), new Data(true));
		UmlStructure s4 = createInstance(s1.getModel(), new UmlObjectData("s4"), new Data(true));

		// s1 --> s4
		// s1 <--> s2 --> s3 --> s4
		// s1 <-- s3
		s1.addDependency(s2);
		s2.addDependency(s3);
		s2.addDependency(s1); // circular dependency
		s3.addDependency(s4);
		s3.addDependency(s1);
		s1.addDependency(s4);
		// s4 depends on nothing

		Collection<UmlStructure> s1ExpAfferent = new HashSet<UmlStructure>();
		s1ExpAfferent.add(s2);
		s1ExpAfferent.add(s3);
		Collection<UmlStructure> s1ExpEfferent = new HashSet<UmlStructure>();
		s1ExpEfferent.add(s2);
		s1ExpEfferent.add(s4);
		assertEquals("afferent for s1", s1ExpAfferent, s1.collectDependencyAfferentStructures());
		assertEquals("efferent for s1", s1ExpEfferent, s1.collectDependencyEfferentStructures());

		Collection<UmlStructure> s2ExpAfferent = new HashSet<UmlStructure>();
		s2ExpAfferent.add(s1);
		Collection<UmlStructure> s2ExpEfferent = new HashSet<UmlStructure>();
		s2ExpEfferent.add(s1);
		s2ExpEfferent.add(s3);
		assertEquals("afferent for s2", s2ExpAfferent, s2.collectDependencyAfferentStructures());
		assertEquals("efferent for s2", s2ExpEfferent, s2.collectDependencyEfferentStructures());

		Collection<UmlStructure> s3ExpAfferent = new HashSet<UmlStructure>();
		s3ExpAfferent.add(s2);
		Collection<UmlStructure> s3ExpEfferent = new HashSet<UmlStructure>();
		s3ExpEfferent.add(s1);
		s3ExpEfferent.add(s4);
		assertEquals("afferent for s3", s3ExpAfferent, s3.collectDependencyAfferentStructures());
		assertEquals("efferent for s3", s3ExpEfferent, s3.collectDependencyEfferentStructures());

		Collection<UmlStructure> s4ExpAfferent = new HashSet<UmlStructure>();
		s4ExpAfferent.add(s1);
		s4ExpAfferent.add(s3);
		Collection<UmlStructure> s4ExpEfferent = new HashSet<UmlStructure>();
		assertEquals("afferent for s4", s4ExpAfferent, s4.collectDependencyAfferentStructures());
		assertEquals("efferent for s4", s4ExpEfferent, s4.collectDependencyEfferentStructures());
	}

	// ------------------------------

	@Test
	public final void testCollectExhaustiveDependencyAfferentAndEfferentStructures() {
		FixtureForDependenciesAndNamespaces f = new FixtureForDependenciesAndNamespaces(emptyModel);

		f.p74.addDependency(f.p73);
		f.p73.addDependency(f.p72);
		f.p74.addDependency(f.p72);
		f.p73.addDependency(f.p74); // circular!
		f.wg17.addDependency(f.p72); // subpackages of wg17 should have "derived" this link

		assertSame("wg17 has sub-package 7-420 in the fixture", f.wg17, f.p7420.getContainer());
		assertTrue("wg17 depends on 7-2 in the fixture", f.wg17
				.collectDependencyEfferentStructures().contains(f.p72));

		Collection<UmlStructure> expExhaustiveEfferent = new HashSet<UmlStructure>();
		expExhaustiveEfferent.add(f.p72);
		assertEquals("exhaustive efferent for 7-420", expExhaustiveEfferent,
				f.p7420.collectMyAndParentsDependencyEfferentStructures());
	}

	// ======================================================

	@Test(expected = NullPointerException.class)
	public final void testAddDiagramODataNull() throws Exception {
		UmlStructure s = createInstance(new UmlObjectData("structure"), Data.empty());
		File picNullOk = null;

		s.addDiagram(picNullOk, null, UmlDiagram.Data.empty());
	}

	@Test(expected = NullPointerException.class)
	public final void testAddDiagramDataNull() throws Exception {
		UmlStructure s = createInstance(new UmlObjectData("structure"), Data.empty());
		File picNullOk = null;

		s.addDiagram(picNullOk, new UmlObjectData("dia"), null);
	}

	@Test
	public final void testAddDiagramReturnsAdded() throws Exception {
		UmlStructure s = createInstance(new UmlObjectData("structure"), Data.empty());

		String diaUuid = "01234";
		UmlObjectData objData = new UmlObjectData(diaUuid, "dia", null);
		UmlDiagram dia = s.addDiagram(null, objData, UmlDiagram.Data.empty());

		assertEquals(diaUuid, dia.getUuid());
	}

	@Test
	public final void testAddDiagramAddWithExistingUuid() throws Exception {
		UmlStructure s = createInstance(new UmlObjectData("structure"), Data.empty());

		UmlDiagram existing = s.addDiagram("dia");

		assertEquals("one diagram added", 1, s.getDiagrams().size());

		UmlObjectData duplicateOData = new UmlObjectData(existing.getUuid(), "anyName", null);
		UmlDiagram duplicate = s.addDiagram(null, duplicateOData, UmlDiagram.Data.empty());

		assertEquals("new diagram not added", 1, s.getDiagrams().size());
		assertSame("returns the existing diagram", existing, duplicate);
	}
}
