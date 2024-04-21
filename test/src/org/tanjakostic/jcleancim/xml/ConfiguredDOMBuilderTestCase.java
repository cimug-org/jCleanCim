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
import static org.junit.Assert.fail;

import org.junit.Test;
import org.tanjakostic.jcleancim.xml.ConfiguredDOMBuilder;
import org.tanjakostic.jcleancim.xml.XmlParsingException;

/**
 * @author tatjana.kostic@ieee.org
 * @version $Id: ConfiguredDOMBuilderTestCase.java 1969 2011-09-04 00:59:46Z tatjana.kostic@ieee.org
 *          $
 */
abstract public class ConfiguredDOMBuilderTestCase {

	abstract protected ConfiguredDOMBuilder create();

	abstract protected void readFromValidFile();

	abstract protected void readFromInvalidFile();

	// ============= Tests ===============

	@Test
	public final void testGetSAXReader() {
		ConfiguredDOMBuilder reader = create();
		assertNotNull(reader);
	}

	// --------------------

	@Test
	public final void testGetParsingErrors() {
		ConfiguredDOMBuilder reader = create();
		assertTrue("should have no errors, nothing read yet", reader.getParsingErrors().isEmpty());
	}

	// --------------------

	@Test
	public final void testReadFileValid() {
		ConfiguredDOMBuilder reader = create();
		readFromValidFile();
		assertTrue("should have no errors", reader.getParsingErrors().isEmpty());
	}

	@Test
	public final void testReadFileInvalid() {
		ConfiguredDOMBuilder reader = create();
		try {
			readFromInvalidFile();
		} catch (XmlParsingException e) {
			assertFalse("should not be empty", e.getErrorData().isEmpty());
		} finally {
			assertFalse("should not be empty", reader.getParsingErrors().isEmpty());
		}
	}

	// --------------------

	@Test
	public final void testReadXmlStringValid() {
		fail("Not yet implemented"); // TODO
	}

	@Test
	public final void testReadXmlStringInvalid() {
		fail("Not yet implemented"); // TODO
	}

	// --------------------

	@Test
	public final void testReadInputSourceValid() {
		fail("Not yet implemented"); // TODO
	}

	@Test
	public final void testReadInputSourceInvalid() {
		fail("Not yet implemented"); // TODO
	}
}
