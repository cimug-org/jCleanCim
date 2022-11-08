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

package org.tanjakostic.jcleancim.builder.ea;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.tanjakostic.jcleancim.builder.ea.EA;

/**
 * @author tatjana.kostic@ieee.org
 * @version $Id: EATest.java 15 2016-07-12 15:11:42Z dev978 $
 */
public class EATest {

	// ============= Tests ===============

	String inputNoStereos = "@PROP=@NAME=isActive@ENDNAME;@TYPE=Boolean@ENDTYPE;@VALU=@ENDVALU;@PRMT=@ENDPRMT;@ENDPROP;";
	String input1stereo = "@STEREO;Name=enumeration;GUID={B0C27888-E359-4da4-961C-753F93F655C1};@ENDSTEREO;";
	String input2stereos = "@STEREO;Name=enumeration;GUID={B0C27888-E359-4da4-961C-753F93F655C1};@ENDSTEREO;@STEREO;Name=packed;GUID={1561FAE5-6E85-4f4a-B220-7981301AB78E};@ENDSTEREO;";

	String inputAssocStereo = "@STEREO;Name=builds;@ENDSTEREO;";

	String inputAssocEnd2Stereos = "@STEREO;Name=deprecated;@ENDSTEREO;@STEREO;Name=endStereo2;GUID={99F14557-A437-41b6-9A1A-7DDE5FDFEF06};@ENDSTEREO;";
	String inputAssocEnd2StereosSecond = "@STEREO;Name=endStereo1;GUID={8C6CD04B-024C-46ad-BD1E-779D27F9C988};@ENDSTEREO;@STEREO;Name=endStereo1,deprecated;GUID={17115745-E716-4a24-B579-56DDFE7E3572};@ENDSTEREO;@STEREO;Name=deprecated;GUID={1EEE5099-7E8E-42af-A7B9-2459953303FD};@ENDSTEREO;";

	@Test
	public final void testExtractStereotypesEmptyInput() {
		assertEquals("", EA.extractStereotypes(""));
	}

	@Test
	public final void testExtractStereotypesInputWithoutStereotypes() {
		assertEquals("", EA.extractStereotypes(inputNoStereos));
	}

	@Test
	public final void testExtractStereotypesInput1stereo() {
		assertEquals("enumeration", EA.extractStereotypes(input1stereo));
	}

	@Test
	public final void testExtractStereotypesInput2stereos() {
		assertEquals("enumeration,packed", EA.extractStereotypes(input2stereos));
	}

	@Test
	public final void testExtractStereotypesInputAssocStereo() {
		assertEquals("builds", EA.extractStereotypes(inputAssocStereo));
	}

	@Test
	public final void testExtractStereotypesInputAssocEnd2Stereos() {
		assertEquals("deprecated,endStereo2", EA.extractStereotypes(inputAssocEnd2Stereos));
	}

	@Test
	public final void testExtractStereotypesInputAssocEnd2StereosSecond() {
		assertEquals("endStereo1,deprecated", EA.extractStereotypes(inputAssocEnd2StereosSecond));
	}

	// ------------------------------------

	static final String inputNoAlias = "Derived=0;AllowDuplicates=0;";
	static final String inputWithAlias = "Derived=0;AllowDuplicates=0;alias=terminal alias;";
	static final String inputNoNavigable = "Owned=0;Union=0";
	static final String inputWithNavigable = "Owned=0;Navigable=Unspecified;Union=0";
	static final String inputParWithAlias = "alias=par alias;";

	@Test
	public final void testExtractAliasNullInput() {
		assertEquals("", EA.extractAlias(null));
	}

	@Test
	public final void testExtractAliasEmptyInput() {
		assertEquals("", EA.extractAlias(""));
	}

	@Test
	public final void testExtractAliasInputWithoutAlias() {
		assertEquals("", EA.extractAlias(inputNoAlias));
	}

	@Test
	public final void testExtractAlias() {
		assertEquals("terminal alias", EA.extractAlias(inputWithAlias));
	}

	@Test
	public final void testExtracNavigability() {
		assertEquals("", EA.extractNavigability(inputNoNavigable));
	}

	@Test
	public final void testExtractNavigability() {
		assertEquals("Unspecified", EA.extractNavigability(inputWithNavigable));
	}

	@Test
	public final void testExtractAliasFromSingle() {
		assertEquals("par alias", EA.extractAlias(inputParWithAlias));
	}
}
