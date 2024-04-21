package org.tanjakostic.jcleancim.docgen.collector.impl;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.tanjakostic.jcleancim.docgen.collector.ModelFinder;
import org.tanjakostic.jcleancim.model.SampleModelFixture;

import junit.framework.Assert;

public class ModelFinderImplTest {
	private ModelFinder _mf;

	@Before
	public void setUp() throws Exception {
		_mf = new ModelFinderImpl(SampleModelFixture.create());
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public final void testModelFinderImpl() {
		// fail("Not yet implemented"); // TODO
	}

	@Test
	public final void testFindAttributeValue() {
		// fail("Not yet implemented"); // TODO
	}

	@Test
	public final void testFindDiagramFile() {
		// fail("Not yet implemented"); // TODO
	}

	@Test
	public final void testFindDiagramNote() {
		// fail("Not yet implemented"); // TODO
	}

	@Test
	public final void testFindClassName() {
		Assert.assertEquals("Float", _mf.findClassName("Domain", "Float"));
	}

	@Test()
	public final void testFindIec61850NsName() {
		// no NPE when empty list
		_mf.findIec61850NsName("anything");
	}

}
