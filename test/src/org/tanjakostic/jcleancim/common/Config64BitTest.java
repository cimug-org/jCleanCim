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
package org.tanjakostic.jcleancim.common;

/**
 * @author tviegut@ucaiug.org
 * @version $Id: ConfigTest64Bit.java 34 2026-08-10 18:37:17Z dev978 $
 */
public class Config64BitTest extends AbstractConfigTest {

	private static final String _PROPS_FILENAME = "testConfig-x64.properties";
	private static final String _MODEL_CMDLINE_FILENAME = "testOverridenFromCmdLine.qea";
	private static final String _MODEL_FILENAME = "testCombined.qea";

	@Override
	protected String getPropsFileName() {
		return _PROPS_FILENAME;
	}

	@Override
	protected String getModelCmdLineFileName() {
		return _MODEL_CMDLINE_FILENAME;
	}

	@Override
	protected String getModelFileName() {
		return _MODEL_FILENAME;
	}

	@Override
	protected String getInExistingProperties() {
		return "inexisting.qea";
	}

}
