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

package org.tanjakostic.jcleancim.docgen.collector;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Collections;

import org.junit.Test;

/**
 * @author tatjana.kostic@ieee.org
 * @version $Id: DocgenConfigTest.java 34 2019-12-20 18:37:17Z dev978 $
 */
public class DocgenConfigTest {

	// ============= Tests ===============

	@Test
	public final void testCtorFourBooleansRetainHtml() {
		DocgenConfig docgenCfg = new DocgenConfig(false, false, false, false,
				Collections.<String> emptySet(), Collections.<String> emptySet(), false);
		assertFalse("'printHtml' should be false", docgenCfg.keepHtml);
	}

	@Test
	public final void testCtorFourBooleansIgnoreHtml() {
		DocgenConfig docgenCfg = new DocgenConfig(false, false, true, false,
				Collections.<String> emptySet(), Collections.<String> emptySet(), false);
		assertTrue("'printHtml' should be true", docgenCfg.keepHtml);
	}
}
