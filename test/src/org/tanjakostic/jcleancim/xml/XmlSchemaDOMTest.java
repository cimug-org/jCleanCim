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

package org.tanjakostic.jcleancim.xml;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.io.File;

import org.junit.Test;
import org.tanjakostic.jcleancim.util.ProgrammerErrorException;
import org.tanjakostic.jcleancim.xml.XmlException;
import org.tanjakostic.jcleancim.xml.XmlSchemaDOM;
import org.tanjakostic.jcleancim.xml.XmlString;

// import javax.management.modelmbean.XMLParseException;

/**
 * @author tatjana.kostic@ieee.org
 * @version $Id: XmlSchemaDOMTest.java 15 2016-07-12 15:11:42Z dev978 $
 */
public class XmlSchemaDOMTest {

	static final String PATH = "test/input/xml/testSchema.xsd";

	static final String CONTENT = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
			+ "<xs:schema xmlns:iecDoc=\"http://iec.ch/TC57/UML/2011/IECDomainDoc#\" xmlns:xs=\"http://www.w3.org/2001/XMLSchema\" targetNamespace=\"http://iec.ch/TC57/UML/2011/IECDomainDoc#\" elementFormDefault=\"qualified\" attributeFormDefault=\"unqualified\" version=\"0.3\" xml:lang=\"en-GB\">"
			+ "  <xs:simpleType name=\"tDocID\">"
			+ "    <xs:restriction base=\"xs:normalizedString\">"
			+ "      <xs:minLength value=\"1\"/>"
			+ "    </xs:restriction>"
			+ "  </xs:simpleType>"
			+ "  <xs:complexType name=\"tDoc\" mixed=\"true\">"
			+ "    <xs:sequence minOccurs=\"0\" maxOccurs=\"unbounded\">"
			+ "      <xs:any namespace=\"##any\" processContents=\"lax\"/>"
			+ "    </xs:sequence>"
			+ "    <xs:attribute name=\"id\" type=\"iecDoc:tDocID\" use=\"required\"/>"
			+ "  </xs:complexType>"
			+ "  <xs:complexType name=\"tSubDomainDoc\">"
			+ "		<xs:annotation>"
			+ "      <xs:documentation>?????????</xs:documentation>"
			+ "    </xs:annotation>"
			+ "    <xs:sequence>"
			+ "      <xs:element name=\"Doc\" type=\"iecDoc:tDoc\" maxOccurs=\"unbounded\"/>"
			+ "    </xs:sequence>"
			+ "    <xs:attribute name=\"lang\" type=\"xs:language\" use=\"optional\" default=\"en-GB\">"
			+ "      <xs:annotation>"
			+ "        <xs:documentation>Language of the documentation strings, by default GB English.</xs:documentation>"
			+ "      </xs:annotation>"
			+ "    </xs:attribute>"
			+ "  </xs:complexType>"
			+ "  <xs:complexType name=\"tIECDomainDoc\">"
			+ "    <xs:annotation>"
			+ "      <xs:documentation>Documentation for the domains in scope of IEC.</xs:documentation>"
			+ "    </xs:annotation>"
			+ "    <xs:sequence>"
			+ "      <xs:element name=\"IEC61850Domain\" type=\"iecDoc:tSubDomainDoc\" minOccurs=\"0\" maxOccurs=\"1\"/>"
			+ "      <xs:element name=\"TC57CIM\" type=\"iecDoc:tSubDomainDoc\" minOccurs=\"0\" maxOccurs=\"1\"/>"
			+ "    </xs:sequence>"
			+ "  </xs:complexType>"
			+ "  <!-- ============================ ELEMENTS ======================================= -->"
			+ "  <xs:element name=\"IECDomainDoc\" type=\"iecDoc:tIECDomainDoc\">"
			+ "    <xs:unique name=\"uniqueID\">" + "      <xs:selector xpath=\"iecDoc:Doc\"/>"
			+ "      <xs:field xpath=\"@id\"/>" + "    </xs:unique>" + "  </xs:element>"
			+ "</xs:schema>";

	// ============= Tests ===============

	@SuppressWarnings("unused")
	@Test(expected = ProgrammerErrorException.class)
	public final void testCtorPathNull() {
		new XmlSchemaDOM((String) null);
	}

	@SuppressWarnings("unused")
	@Test(expected = IllegalArgumentException.class)
	public final void testCtorPathEmpty() {
		new XmlSchemaDOM("");
	}

	@SuppressWarnings("unused")
	@Test(expected = XmlException.class)
	public final void testCtorPathInexisting() {
		new XmlSchemaDOM("dummy.xsd");
	}

	@Test
	public final void testCtorPathAndAccessors() {
		File expected = new File(PATH);
		assertTrue("test schema should exist", expected.exists());
		XmlSchemaDOM schema = new XmlSchemaDOM(PATH);

		assertEquals(expected.getPath(), schema.getFile().getPath());
		assertAccessors(schema);
	}

	private void assertAccessors(XmlSchemaDOM schema) {
		assertEquals("IECDomainDoc", schema.getRootTag());
		assertEquals("http://iec.ch/TC57/UML/2011/IECDomainDoc#", schema.getTargetNs().getUri());
		assertEquals("iecDoc", schema.getTargetNs().getPrefix());

		String xml = schema.asXmlString().toString();
		assertTrue("should start with",
				xml.startsWith("<?xml version=\"1.0\" encoding=\"UTF-8\"?>"));
		assertTrue("should contain",
				xml.contains("<xs:attribute name=\"id\" type=\"iecDoc:tDocID\" use=\"required\"/>"));
		assertTrue("should end with", xml.endsWith("</xs:schema>"));

		assertNotNull("should not be null", schema.asInputStream());
	}

	// -----------------------

	@SuppressWarnings("unused")
	@Test(expected = ProgrammerErrorException.class)
	public final void testCtorTextNull() {
		new XmlSchemaDOM((XmlString) null);
	}

	@SuppressWarnings("unused")
	@Test(expected = IllegalArgumentException.class)
	public final void testCtorTextEmpty() {
		new XmlSchemaDOM(new XmlString(""));
	}

	@Test
	public final void testCtorTextAndAccessors() {
		XmlSchemaDOM schema = new XmlSchemaDOM(new XmlString(CONTENT));

		assertNull("file should be null", schema.getFile());
		assertAccessors(schema);
	}
}
