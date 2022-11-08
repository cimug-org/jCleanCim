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
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import org.junit.Ignore;
import org.junit.Test;
import org.tanjakostic.jcleancim.xml.JaxpHelper;
import org.tanjakostic.jcleancim.xml.XmlNs;
import org.tanjakostic.jcleancim.xml.XmlString;
import org.w3c.dom.Document;
import org.w3c.dom.DocumentFragment;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

/**
 * @author tatjana.kostic@ieee.org
 * @version $Id: JaxpHelperTest.java 15 2016-07-12 15:11:42Z dev978 $
 */
public class JaxpHelperTest {

	private static final XmlNs P_URI = new XmlNs("p", "uri");

	private static String asXml(Node n) {
		return JaxpHelper.asXml(n, null).toString();
	}

	// ============= Tests ===============

	@Test(expected = NullPointerException.class)
	public final void testCreateDocumentWithRootRootNull() {
		JaxpHelper.createDocumentWithRoot("comment", null, P_URI);
	}

	@Test(expected = IllegalArgumentException.class)
	public final void testCreateDocumentWithRootRootEmpty() {
		JaxpHelper.createDocumentWithRoot("comment", "   ", P_URI);
	}

	@Test
	public final void testCreateDocumentWithRootCommentNull() {
		Document d = JaxpHelper.createDocumentWithRoot(null, "root", P_URI);
		assertFalse("has no comment", asXml(d).contains("<!--"));
	}

	@Test
	public final void testCreateDocumentWithRootCommentEmpty() {
		Document d = JaxpHelper.createDocumentWithRoot("   ", "root", P_URI);
		assertFalse("has no comment", asXml(d).contains("<!--"));
	}

	@Test
	public final void testCreateDocumentWithRootWithComment() {
		Document d = JaxpHelper.createDocumentWithRoot("comment", "root", P_URI);
		assertTrue("has comment", asXml(d).contains("<!--comment"));
	}

	@Test
	public final void testCreateDocumentWithRootNsNull() {
		Element el = JaxpHelper.createDocumentWithRoot(null, "root", null).getDocumentElement();
		assertTrue("not qualified", asXml(el).contains("<root"));
	}

	@Test
	public final void testCreateDocumentWithRoot() {
		Element el = JaxpHelper.createDocumentWithRoot(null, "root", P_URI).getDocumentElement();
		assertTrue("has comment", asXml(el).contains("<p:root xmlns:p=\"uri\""));
	}

	// ---------------

	@Test(expected = NullPointerException.class)
	public final void testAddNamespaceElemNull() {
		JaxpHelper.addNamespace(null, P_URI);
	}

	@Test(expected = NullPointerException.class)
	public final void testAddNamespaceNsNull() {
		Document d = JaxpHelper.createDocumentWithRoot(null, "root", P_URI);
		JaxpHelper.addNamespace(d, null);
	}

	@Test
	public final void testAddNamespace() {
		Document d = JaxpHelper.createDocumentWithRoot(null, "root", P_URI);
		XmlNs ns2 = new XmlNs("q", "iru");

		JaxpHelper.addNamespace(d, ns2);

		assertEquals(ns2.getUri(), d.getDocumentElement().lookupNamespaceURI(ns2.getPrefix()));
		assertEquals(ns2.getPrefix(), d.getDocumentElement().lookupPrefix(ns2.getUri()));
	}

	// ---------------

	@Test(expected = NullPointerException.class)
	public final void testEmbedIntoRootSnippetNull() {
		JaxpHelper.embedIntoRoot(null, "root", new XmlNs("", "http://uri"));
	}

	@Test(expected = NullPointerException.class)
	public final void testEmbedIntoRootRootNull() {
		JaxpHelper.embedIntoRoot(new XmlString("snippet"), null, new XmlNs("", "http://uri"));
	}

	@Test(expected = IllegalArgumentException.class)
	public final void testEmbedIntoRootRootEmpty() {
		JaxpHelper.embedIntoRoot(new XmlString("snippet"), "  ", new XmlNs("", "http://uri"));
	}

	@Test
	public final void testEmbedIntoRootNsPrefixNull() {
		XmlString result = JaxpHelper.embedIntoRoot(new XmlString("snippet"), "root", new XmlNs(
				null, "http://uri"));
		assertEquals("<root xmlns=\"http://uri\">snippet</root>", result.toString());
	}

	@Test
	public final void testEmbedIntoRootNsPrefixEmpty() {
		XmlString result = JaxpHelper.embedIntoRoot(new XmlString("snippet"), "root", new XmlNs(
				"  ", "http://uri"));
		assertEquals("<root xmlns=\"http://uri\">snippet</root>", result.toString());
	}

	@Test
	public final void testEmbedIntoRootNoNs() {
		XmlString result = JaxpHelper.embedIntoRoot(new XmlString("snippet"), "root", null);
		assertEquals("<root>snippet</root>", result.toString());
	}

	@Test
	public final void testEmbedIntoRoot() {
		XmlString result = JaxpHelper.embedIntoRoot(new XmlString("snippet"), "root", new XmlNs(
				"p", "http://uri"));
		assertEquals("<p:root xmlns:p=\"http://uri\">snippet</p:root>", result.toString());
	}

	// ---------------

	@Test
	public final void testCreateXpathNsNullOk() {
		assertNotNull(JaxpHelper.createXpath((XmlNs[]) null));
	}

	@Test(expected = NullPointerException.class)
	public final void testCreateXpathFirstNsNull() {
		assertNotNull(JaxpHelper.createXpath(null, P_URI));
	}

	@Test
	public final void testCreateXpath() {
		assertNotNull(JaxpHelper.createXpath(P_URI));
	}

	@Test
	public final void testCreateXpathTwoNs() {
		assertNotNull(JaxpHelper.createXpath(P_URI, XmlNs.xsi));
	}

	// ---------------

	@Test(expected = NullPointerException.class)
	public final void testCompileXpathExprNull() {
		assertNotNull(JaxpHelper.compileXpath(null, P_URI));
	}

	@Test(expected = IllegalArgumentException.class)
	public final void testCompileXpathExprEmpty() {
		assertNotNull(JaxpHelper.compileXpath("  ", P_URI));
	}

	@Test
	public final void testCompileXpathExpr() {
		assertNotNull(JaxpHelper.compileXpath("/", P_URI));
	}

	@Test
	public final void testCompileXpathExprTwoNs() {
		assertNotNull(JaxpHelper.compileXpath("/", P_URI, XmlNs.xsi));
	}

	// ------------------

	@Test
	public final void testParseAsDocumentArgNull() {
		assertNull(JaxpHelper.parseAsDocument(null));
	}

	@Test
	public final void testParseAsDocumentArgMalformedEnd() {
		assertNull(JaxpHelper.parseAsDocument(new XmlString("malformed</p>")));
	}

	@Test
	public final void testParseAsDocumentArgMalformedNoClosing() {
		assertNull(JaxpHelper.parseAsDocument(new XmlString("<p>malformed")));
	}

	// ----------------------

	@Test
	public final void testParseAsElement() {
		assertNull(JaxpHelper.parseAsFragment(null));
	}

	@Test
	public final void testParseAsElementArgMalformedEnd() {
		assertNull(JaxpHelper.parseAsFragment(new XmlString("malformed</p>")));
	}

	@Test
	public final void testParseAsElementArgMalformedNoClosing() {
		assertNull(JaxpHelper.parseAsFragment(new XmlString("<p>malformed")));
	}

	@Ignore("TODO")
	@Test
	public final void testParseAsElementMixedContent() {
		// String s1 = "<p>&nbsp;</p>";
		String s1 = "<p>anything</p>";
		// List<String> expecteds = new ArrayList<String>();
		// expecteds.add(s1);
		// expecteds.add(EaNotesCleaner.P_START
		// + "(paraStart)Some intro (&amp; &lt; &gt;) - next line is empty.(paraEnd)"
		// + EaNotesCleaner.P_END + Util.NL);
		// expecteds.add("<ul>" + Util.NL);
		// expecteds.add("	<li>list test</li>" + Util.NL);
		// expecteds.add("</ul>" + Util.NL);
		// expecteds.add("<ol>" + Util.NL);
		// expecteds.add("	<li>this one is numbered</li>" + Util.NL);
		// expecteds.add("	<li>&nbsp;</li>" + Util.NL);
		// expecteds.add("</ol>" + Util.NL);
		// expecteds.add(EaNotesCleaner.P_START + "\t\tPara with white space around\t\t"
		// + EaNotesCleaner.P_END);

		DocumentFragment result = JaxpHelper.parseAsFragment(new XmlString(s1));
		// System.out.println(result.asXML());
		assertNull(result);
	}

	// ----------------------

	@Ignore("TODO")
	@Test
	public final void testGetPrettyPrintElement() {
		// fail("Not yet implemented"); // TODO
	}

	@Ignore("TODO")
	@Test
	public final void testGetPrettyPrintDocument() {
		// fail("Not yet implemented"); // TODO
	}

	@Ignore("TODO")
	@Test
	public final void testSave() {
		// fail("Not yet implemented"); // TODO
	}
}
