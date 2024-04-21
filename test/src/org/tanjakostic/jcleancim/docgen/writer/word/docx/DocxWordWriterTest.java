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

package org.tanjakostic.jcleancim.docgen.writer.word.docx;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.junit.Ignore;
import org.tanjakostic.jcleancim.docgen.UnsupportedInputFormatException;
import org.tanjakostic.jcleancim.docgen.UnsupportedOutputFormatException;
import org.tanjakostic.jcleancim.docgen.collector.BookmarkRegistry;
import org.tanjakostic.jcleancim.docgen.collector.ClassDoc;
import org.tanjakostic.jcleancim.docgen.collector.ModelFinder;
import org.tanjakostic.jcleancim.docgen.collector.PackageDoc;
import org.tanjakostic.jcleancim.docgen.writer.word.AbstractWordWriter;
import org.tanjakostic.jcleancim.docgen.writer.word.AbstractWordWriterTestCase;
import org.tanjakostic.jcleancim.docgen.writer.word.WordWriterInput;

/**
 * @author tatjana.kostic@ieee.org
 * @version $Id: DocxWordWriterTest.java 34 2019-12-20 18:37:17Z dev978 $
 */
@Ignore
public class DocxWordWriterTest extends AbstractWordWriterTestCase<Object> {

	@Override
	protected AbstractWordWriter<Object> createWordWriter(String eapFileName, boolean skipTiming,
			Map<String, PackageDoc> packageDocs, Map<String, ClassDoc> classDocs, ModelFinder mf,
			String inFile, String outFile, int closeReopenEvery, List<String> tocStylePrefixes,
			List<String> headingStylePrefixes, List<String> paraStyles, List<String> figStyles,
			List<String> tabheadStyles, List<String> tabcellStyles, List<String> figcaptStyles,
			List<String> tabcaptStyles)
			throws IOException, UnsupportedInputFormatException, UnsupportedOutputFormatException {
		WordWriterInput input = new WordWriterInput(null, eapFileName, skipTiming, packageDocs,
				classDocs, mf, inFile, outFile, false, true, closeReopenEvery, true, false,
				tocStylePrefixes, headingStylePrefixes, paraStyles, figStyles, tabheadStyles,
				tabcellStyles, figcaptStyles, tabcaptStyles, new BookmarkRegistry());
		return new DocxWordWriter(input);
	}
}
