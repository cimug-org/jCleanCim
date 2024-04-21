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
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;

import java.io.File;
import java.io.InputStream;

import org.junit.Test;
import org.tanjakostic.jcleancim.util.ResourceNotOnClasspathException;
import org.tanjakostic.jcleancim.xml.XmlException;
import org.tanjakostic.jcleancim.xml.XmlSchemaDOM;
import org.tanjakostic.jcleancim.xml.XmlString;
import org.tanjakostic.jcleancim.xml.XmlUtil;
import org.xml.sax.InputSource;

/**
 * @author tatjana.kostic@ieee.org
 * @version $Id: XmlUtilTest.java 15 2016-07-12 15:11:42Z dev978 $
 */
public class XmlUtilTest {

	static final String PATH = "test/input/xml/";
	static final String TEST_XML_FILE = PATH + "test.xml";
	static final String TEST_XML_INVALID_FILE = PATH + "testInvalid.xml";

	// if searching as resource on classpath, we use simply the name:
	static final String TEST_CP_XSD = "testCpSchema.xsd";
	static final String TEST_XSD = "testSchema.xsd";
	// if not searching for a resource, we use relative path:
	static final String TEST_XSD_PATH = PATH + "testSchema.xsd";
	static final String TEST_XML_WITH_SCHEMA_FILE = PATH + "testWithTestSchema.xml";
	static final String TEST_XML_INVALID_WITH_SCHEMA_FILE = PATH + "testInvalidWithTestSchema.xml";

	static final XmlSchemaDOM SCHEMA = new XmlSchemaDOM(TEST_XSD_PATH);

	// ============= Tests ===============

	@Test(expected = NullPointerException.class)
	public final void testXmlAsInputStreamFileNull() {
		XmlUtil.xmlAsInputStream((File) null, false);
	}

	@Test(expected = XmlException.class)
	public final void testXmlAsInputStreamFileInexisting() {
		File f = new File("dummy");
		assertFalse("should not exist", f.exists());
		XmlUtil.xmlAsInputStream(f, false);
	}

	@Test(expected = ResourceNotOnClasspathException.class)
	public final void testXmlAsInputStreamFileNotOnCpCpTrue() {
		File f = new File(TEST_XSD);
		XmlUtil.xmlAsInputStream(f, true);
	}

	@Test
	public final void testXmlAsInputStreamFileCpFalse() {
		File f = new File(TEST_XSD_PATH);
		InputStream is = XmlUtil.xmlAsInputStream(f, false);
		assertNotNull(is);
	}

	@Test
	public final void testXmlAsInputStreamFileCpTrue() {
		File f = new File(TEST_CP_XSD);
		InputStream is = XmlUtil.xmlAsInputStream(f, true);
		assertNotNull(is);
	}

	// ---------------

	@Test(expected = NullPointerException.class)
	public final void testXmlAsInputStreamUriNull() {
		XmlUtil.xmlAsInputStream((String) null, false);
	}

	@Test(expected = IllegalArgumentException.class)
	public final void testXmlAsInputStreamUriEmpty() {
		XmlUtil.xmlAsInputStream("", false);
	}

	@Test(expected = XmlException.class)
	public final void testXmlAsInputStreamUriInexisting() {
		File f = new File("dummy");
		assertFalse("should not exist", f.exists());
		XmlUtil.xmlAsInputStream("dummy", false);
	}

	@Test(expected = ResourceNotOnClasspathException.class)
	public final void testXmlAsInputStreamUriCpTrueNotOnCp() {
		XmlUtil.xmlAsInputStream(TEST_XSD, true);
	}

	@Test
	public final void testXmlAsInputStreamUriCpFalse() {
		InputStream is = XmlUtil.xmlAsInputStream(TEST_XSD_PATH, false);
		assertNotNull(is);
	}

	@Test
	public final void testXmlAsInputStreamUriCpOnCp() {
		InputStream is = XmlUtil.xmlAsInputStream(TEST_CP_XSD, true);
		assertNotNull(is);
	}

	// ---------------

	@Test(expected = NullPointerException.class)
	public final void testXmlAsInputStreamXmlTextNull() {
		XmlUtil.xmlAsInputStream((XmlString) null);
	}

	@Test(expected = IllegalArgumentException.class)
	public final void testXmlAsInputStreamXmlTextEmpty() {
		XmlUtil.xmlAsInputStream(new XmlString("   "));
	}

	@Test
	public final void testXmlAsInputStreamXmlText() {
		InputStream is = XmlUtil.xmlAsInputStream(SCHEMA.asXmlString());
		assertNotNull(is);
	}

	// ---------------

	@Test(expected = NullPointerException.class)
	public final void testXmlAsInputSourceXmlTextNull() {
		XmlUtil.xmlAsInputSource((XmlString) null);
	}

	@Test(expected = IllegalArgumentException.class)
	public final void testXmlAsInputSourceXmlTextEmpty() {
		XmlUtil.xmlAsInputSource(new XmlString("   "));
	}

	@Test
	public final void testXmlAsInputSourceXmlText() {
		InputSource is = XmlUtil.xmlAsInputSource(SCHEMA.asXmlString());
		assertNotNull(is);
		assertEquals(XmlUtil.ENCODING, is.getEncoding());
	}
}
