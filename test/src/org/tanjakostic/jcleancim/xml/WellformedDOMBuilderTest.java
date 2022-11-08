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

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.File;

import org.junit.Before;
import org.junit.Test;
import org.tanjakostic.jcleancim.xml.ConfiguredDOMBuilder;
import org.tanjakostic.jcleancim.xml.WellformedDOMBuilder;
import org.tanjakostic.jcleancim.xml.XmlParsingException;

/**
 * @author tatjana.kostic@ieee.org
 * @version $Id: WellformedDOMBuilderTest.java 15 2016-07-12 15:11:42Z dev978 $
 */
public class WellformedDOMBuilderTest {

	static final String PATH = "test/input/xml/";
	static final String TEST_XML_FILE = PATH + "contacts.xml";
	static final String TEST_MALFORMED_XML_FILE = PATH + "testMalFormed.xml";

	private ConfiguredDOMBuilder _builder;

	@Before
	public void setUp() {
		_builder = new WellformedDOMBuilder();
	}

	// ============= Tests ===============

	@Test
	public final void testCtor() {
		assertNotNull(_builder);
		assertTrue("should have no errors, nothing read yet", _builder.getParsingErrors().isEmpty());
	}

	// ------------------

	@Test
	public final void testGetParsingErrorsNone() {
		_builder.readAndValidate(new File(TEST_XML_FILE));
		assertTrue("should have no errors, because this reader checks for well-formdness only",
				_builder.getParsingErrors().isEmpty());
	}

	@Test
	public final void testGetParsingErrorsSome() {
		try {
			_builder.readAndValidate(new File(TEST_MALFORMED_XML_FILE));
		} catch (XmlParsingException e) {
			assertFalse("should not be empty", e.getErrorData().isEmpty());
		} finally {
			assertFalse("should not be empty", _builder.getParsingErrors().isEmpty());
		}
	}
}
