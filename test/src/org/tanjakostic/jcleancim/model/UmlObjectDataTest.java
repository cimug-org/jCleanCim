package org.tanjakostic.jcleancim.model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;

import org.junit.Test;
import org.tanjakostic.jcleancim.model.TextDescription;
import org.tanjakostic.jcleancim.model.UmlObjectData;
import org.tanjakostic.jcleancim.model.UmlStereotype;
import org.tanjakostic.jcleancim.model.UmlVisibility;
import org.tanjakostic.jcleancim.model.TextDescription.TextKind;
import org.tanjakostic.jcleancim.util.Util;

public class UmlObjectDataTest {

	private final Integer _id = Integer.valueOf(7);
	private final String _uuid = "157FF307-0C5D-4c09-A480-5EBB085B0E02";
	private final String _name = "name";
	private final String _alias = "alias";
	private final UmlStereotype _stereotype = new UmlStereotype("st", "xy");
	private final String _eaVisibility = "protected";
	private final TextDescription _txtDoc = new TextDescription("text" + Util.NL + " doc",
			TextKind.textWithNL);
	private final TextDescription _htmlDoc = new TextDescription("<p>Some HTML doc.</p>",
			TextKind.htmlSnippet);

	// ============= Tests ===============

	@Test
	public final void testUmlObjectDataTransformsNullToSomething() {
		UmlObjectData data = new UmlObjectData(null, null, null, null, null, null, null, null);

		assertNotNull("id assigned from counter", data.getId());
		assertFalse("UUID generated", data.getUuid().isEmpty());
		assertEquals("null -> empty string", "", data.getName());
		assertEquals("null -> empty string", "", data.getAlias());
		assertEquals("null -> empty string", "", data.getStereotype().value());
		assertEquals("null -> default", UmlVisibility.PUBLIC, data.getVisibility());
		assertEquals("null -> empty string", "", data.getTxtDescription().text);
		assertEquals("null -> empty string", "", data.getHtmlDescription().text);
	}

	// --------------------------------------------

	@Test
	public final void testGetters() {
		UmlObjectData data = new UmlObjectData(_id, _uuid, _name, _alias, _stereotype,
				_eaVisibility.toString(), _txtDoc, _htmlDoc);

		assertEquals(_id, data.getId());
		assertFalse(_uuid, data.getUuid().isEmpty());
		assertEquals(_name, data.getName());
		assertEquals(_alias, data.getAlias());
		assertEquals(_stereotype, data.getStereotype());
		assertEquals(UmlVisibility.PROTECTED, data.getVisibility());
		assertEquals(_txtDoc, data.getTxtDescription());
		assertEquals(_htmlDoc, data.getHtmlDescription());
	}
}
