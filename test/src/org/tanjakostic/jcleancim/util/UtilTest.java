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

/**
 * @author tatjana.kostic@ieee.org
 * @version $Id: UtilTest.java 31 2019-12-08 01:19:54Z dev978 $
 */
public class UtilTest extends AbstractUtilTest {

	private static final String MODEL_FILE = "testCombined.eap";

	private static final String TEST_CONFIG = "testConfig.properties";

	private static final String IN_EXISTING = "inexisting.properties";

	@Override
	protected String getModelFile() {
		return MODEL_FILE;
	}

	@Override
	protected String getTestConfigProperties() {
		return TEST_CONFIG;
	}

	@Override
	protected String getInExistingProperties() {
		return IN_EXISTING;
	}

}
