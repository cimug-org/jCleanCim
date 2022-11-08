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

package org.tanjakostic.jcleancim.docgen.collector.impl;

import org.tanjakostic.jcleancim.docgen.collector.AGSpec;
import org.tanjakostic.jcleancim.docgen.collector.EntryDoc;
import org.tanjakostic.jcleancim.docgen.collector.EntryDocTestCase;
import org.tanjakostic.jcleancim.docgen.collector.FormatInfo;
import org.tanjakostic.jcleancim.docgen.collector.WAX;

/**
 * @author tatjana.kostic@ieee.org
 * @version $Id: EntryDocImplTest.java 15 2016-07-12 15:11:42Z dev978 $
 */
public class EntryDocImplTest extends EntryDocTestCase {

	@Override
	public EntryDoc createTableName(String name, int columnCount) {
		return EntryDocImpl.createTableName(name, columnCount);
	}

	@Override
	public EntryDoc createGroupSubhead(String subheadTxt, int columnCount) {
		return EntryDocImpl.createGroupSubhead(
				AGSpec.create(WAX.LOC_instTag, "kindTag", subheadTxt), columnCount);
	}

	@Override
	public EntryDoc createColumnLabels(String... values) {
		return EntryDocImpl.createColumnLabels(values);
	}

	@Override
	public EntryDocImpl createData(String bookmarkID, FormatInfo formatInfo, String... values) {
		return EntryDocImpl.createData(bookmarkID, formatInfo, values);
	}

	// ============= Tests ===============
	// none; all implemented by the parent test for interface
}
