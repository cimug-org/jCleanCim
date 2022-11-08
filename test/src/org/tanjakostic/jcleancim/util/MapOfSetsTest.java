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

package org.tanjakostic.jcleancim.util;

import org.junit.Test;
import org.tanjakostic.jcleancim.util.MapOfCollections;
import org.tanjakostic.jcleancim.util.MapOfSets;

/**
 * @author tatjana.kostic@ieee.org
 * @version $Id: MapOfSetsTest.java 15 2016-07-12 15:11:42Z dev978 $
 */
public class MapOfSetsTest extends MapOfCollectionsTests {

	@Override
	protected MapOfCollections<String, String> createInstance() {
		return new MapOfSets<>();
	}

	// ============= Tests ===============

	@Test(expected = UnsupportedOperationException.class)
	public final void testGetSubValue() {
		MapOfCollections<String, String> yo = createInstance();
		yo.addValue("toto", "value");
		yo.value("toto", 0);
	}
}
