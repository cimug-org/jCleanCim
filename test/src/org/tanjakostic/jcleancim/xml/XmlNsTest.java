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
import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.tanjakostic.jcleancim.xml.XmlNs;

/**
 * @author tatjana.kostic@ieee.org
 * @version $Id: XmlNsTest.java 15 2016-07-12 15:11:42Z dev978 $
 */
public class XmlNsTest {

	// ============= Tests ===============

	@Test
	public final void testCtorPrefixNull() {
		XmlNs ns = new XmlNs(null, "iru");
		assertTrue("Prefix should be empty.", ns.getPrefix().isEmpty());
	}

	@SuppressWarnings("unused")
	@Test(expected = NullPointerException.class)
	public final void testCtorUriNull() {
		new XmlNs("q", null);
	}

	@Test
	public final void testCtorPrefixEmpty() {
		XmlNs ns = new XmlNs(" \n ", "iru");
		assertTrue("Prefix should be empty.", ns.getPrefix().isEmpty());
	}

	@SuppressWarnings("unused")
	@Test(expected = IllegalArgumentException.class)
	public final void testCtorUriEmpty() {
		new XmlNs("q", " \n ");
	}

	@Test
	public final void testCtorUriTrimmed() {
		XmlNs ns = new XmlNs("p", " \t  iru \n  \t");
		assertEquals("iru", ns.getUri());
	}

	// ---------------

	@Test
	public final void testAsPrefixNull() {
		XmlNs ns = new XmlNs(null, "http://toto#");
		assertEquals("", ns.asPrefix());
	}

	@Test
	public final void testAsPrefixEmpty() {
		XmlNs ns = new XmlNs("\n", "http://toto#");
		assertEquals("", ns.asPrefix());
	}

	@Test
	public final void testAsPrefix() {
		XmlNs ns = new XmlNs("p", "http://toto#");
		assertEquals("p:", ns.asPrefix());
	}

	// ---------------

	@Test
	public final void testGettersUriWithFragment() {
		XmlNs ns = new XmlNs("p", "http://toto#");

		assertEquals("p", ns.getPrefix());
		assertEquals("p:", ns.asPrefix());
		assertEquals("p:name", ns.qName("name"));
		assertEquals("http://toto#", ns.getUri());
		assertEquals("http://toto", ns.getUriWithoutFragmentSeparator());
	}

	@Test
	public final void testGettersUriWithoutFragment() {
		XmlNs ns = new XmlNs("p", "http://toto");

		assertEquals("p", ns.getPrefix());
		assertEquals("p:", ns.asPrefix());
		assertEquals("p:name", ns.qName("name"));
		assertEquals("http://toto", ns.getUri());
		assertEquals("http://toto", ns.getUriWithoutFragmentSeparator());
	}
}
