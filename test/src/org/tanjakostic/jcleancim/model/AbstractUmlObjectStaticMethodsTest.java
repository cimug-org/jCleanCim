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

package org.tanjakostic.jcleancim.model;

import static org.junit.Assert.assertEquals;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.Ignore;
import org.junit.Test;
import org.tanjakostic.jcleancim.common.Nature;
import org.tanjakostic.jcleancim.common.OwningWg;
import org.tanjakostic.jcleancim.util.MapOfCollections;

/**
 * @author tatjana.kostic@ieee.org
 * @version $Id: AbstractUmlObjectStaticMethodsTest.java 34 2019-12-20 18:37:17Z dev978 $
 */
public class AbstractUmlObjectStaticMethodsTest {

	static class NamedObject extends AbstractUmlObject {
		private String _containerName;
		private String _separator;
		private OwningWg _owner;

		NamedObject(String name) {
			super(new UmlObjectData(name));
		}

		NamedObject(String name, OwningWg owner) {
			this(name, "", "", owner);
		}

		NamedObject(String name, String containerName, String separator) {
			this(name);
			_containerName = containerName;
			_separator = separator;
		}

		NamedObject(String name, String containerName, String separator, OwningWg owner) {
			this(name, containerName, separator);
			_owner = owner;
		}

		@Override
		public OwningWg getOwner() {
			return _owner;
		}

		@Override
		public Namespace getNamespace() {
			return Namespace.EMPTY;
		}

		@Override
		public Nature getNature() {
			return null;
		}

		@Override
		public boolean isInformative() {
			return false;
		}

		@Override
		public UmlKind getKind() {
			return null;
		}

		@Override
		public String getQualifiedName() {
			return _containerName + _separator + getName();
		}

		@Override
		public Set<String> getPredefinedTagNames() {
			return Collections.emptySet();
		}

		@Override
		public String toString() {
			return getQualifiedName();
		}
	}

	// ============= Tests ===============

	@Test(expected = NullPointerException.class)
	public final void testCollectNamesNull() {
		AbstractUmlObject.collectNames(null);
	}

	@Test
	public final void testCollectNames() {
		UmlObject n0 = new NamedObject("n0");
		UmlObject n1 = new NamedObject("n1");
		UmlObject n2Null = null;
		UmlObject n3Dup1 = new NamedObject("n1");
		List<UmlObject> input = Arrays.asList(new UmlObject[] { n0, n1, n2Null, n3Dup1 });

		List<String> exp = Arrays
				.asList(new String[] { "n0", "n1", AbstractUmlObject.NULL_OBJ_NAME, "n1" });

		assertEquals(exp, AbstractUmlObject.collectNames(input));
	}

	// -------------------

	@Test(expected = NullPointerException.class)
	public final void testCollectQNamesNull() {
		AbstractUmlObject.collectQNames(null, false);
	}

	@Test
	public final void testCollectQNamesNoOwner() {
		UmlObject n0 = new NamedObject("n0", "c", ":");
		UmlObject n1 = new NamedObject("n1", "c", ":");
		UmlObject n2Null = null;
		UmlObject n3Dup1 = new NamedObject("n1", "c", ":");
		List<UmlObject> input = Arrays.asList(new UmlObject[] { n0, n1, n2Null, n3Dup1 });

		List<String> exp = Arrays
				.asList(new String[] { "c:n0", "c:n1", AbstractUmlObject.NULL_OBJ_NAME, "c:n1" });

		assertEquals(exp, AbstractUmlObject.collectQNames(input, false));
	}

	@Test
	public final void testCollectQNamesWithOwner() {
		UmlObject n0 = new NamedObject("n0", "c", ":", OwningWg.WG14);
		UmlObject n1 = new NamedObject("n1", "c", ":", OwningWg.WG14);
		UmlObject n2Null = null;
		UmlObject n3Dup1 = new NamedObject("n1", "c", ":", OwningWg.WG13);
		List<UmlObject> input = Arrays.asList(new UmlObject[] { n0, n1, n2Null, n3Dup1 });

		List<String> exp = Arrays.asList(new String[] { "WG14 c:n0", "WG14 c:n1",
				AbstractUmlObject.NULL_OBJ_NAME, "WG13 c:n1" });

		assertEquals(exp, AbstractUmlObject.collectQNames(input, true));
	}

	// -------------------

	@Test(expected = NullPointerException.class)
	public final void testCollectDuplicateNamesNull() {
		AbstractUmlObject.collectDuplicateNames(null);
	}

	@Test
	public final void testCollectDuplicateNames() {
		UmlObject n0 = new NamedObject("n0");
		UmlObject n1 = new NamedObject("n1");
		UmlObject n2Null = null;
		UmlObject n3Dup1 = new NamedObject("n1");
		UmlObject n4 = new NamedObject("n4");
		UmlObject n5Dup0 = new NamedObject("n0");
		UmlObject n6Dup1 = new NamedObject("n1");
		List<UmlObject> input = Arrays
				.asList(new UmlObject[] { n0, n1, n2Null, n3Dup1, n4, n5Dup0, n6Dup1 });

		Set<UmlObject> expDups0 = new HashSet<>(Arrays.asList(new UmlObject[] { n0, n5Dup0 }));
		Set<UmlObject> expDups1 = new HashSet<>(
				Arrays.asList(new UmlObject[] { n1, n3Dup1, n6Dup1 }));

		MapOfCollections<String, UmlObject> duplicates = AbstractUmlObject
				.collectDuplicateNames(input);

		assertEquals(expDups0, duplicates.subCollection(n0.getName()));
		assertEquals(expDups1, duplicates.subCollection(n1.getName()));
	}

	// -------------------

	@Test(expected = NullPointerException.class)
	public final void testClassifyPerScopeObjectsNull() {
		AbstractUmlObject.classifyPerScope(null, EnumSet.allOf(OwningWg.class));
	}

	@Test(expected = NullPointerException.class)
	public final void testClassifyPerScopeScopesNull() {
		UmlObject n0 = new NamedObject("n0", OwningWg.WG10);
		List<UmlObject> input = Arrays.asList(new UmlObject[] { n0 });
		AbstractUmlObject.classifyPerScope(input, null);
	}

	@Test
	public final void testClassifyPerScope() {
		UmlObject n0 = new NamedObject("n0", OwningWg.WG10);
		UmlObject n1 = new NamedObject("n1", OwningWg.WG13);
		UmlObject n2Null = null;
		UmlObject n3 = new NamedObject("n1", OwningWg.WG14);
		UmlObject n4 = new NamedObject("n4", OwningWg.WG14);
		UmlObject n5 = new NamedObject("n5", OwningWg.WG10);
		List<UmlObject> input = Arrays.asList(new UmlObject[] { n0, n1, n2Null, n3, n4, n5 });

		Map<OwningWg, Collection<UmlObject>> exp = new HashMap<>();
		exp.put(OwningWg.WG10, Arrays.asList(new UmlObject[] { n0, n5 }));
		exp.put(OwningWg.WG14, Arrays.asList(new UmlObject[] { n3, n4 }));

		Map<OwningWg, Collection<UmlObject>> actual = AbstractUmlObject.classifyPerScope(input,
				EnumSet.of(OwningWg.WG10, OwningWg.WG14));

		assertEquals(exp.size(), actual.size());
		assertEquals(exp.get(OwningWg.WG10), actual.get(OwningWg.WG10));
		assertEquals(exp.get(OwningWg.WG14), actual.get(OwningWg.WG14));
	}

	// -------------------

	@Test(expected = NullPointerException.class)
	public final void testCollectForScopeObjectsNull() {
		AbstractUmlObject.collectForScope(null, EnumSet.allOf(OwningWg.class));
	}

	@Test(expected = NullPointerException.class)
	public final void testCollectForScopeScopesNull() {
		UmlObject n0 = new NamedObject("n0", OwningWg.WG10);
		List<UmlObject> input = Arrays.asList(new UmlObject[] { n0 });
		AbstractUmlObject.collectForScope(input, null);
	}

	@Test
	public final void testCollectForScope() {
		UmlObject n0 = new NamedObject("n0", OwningWg.WG10);
		UmlObject n1 = new NamedObject("n1", OwningWg.WG13);
		UmlObject n2Null = null;
		UmlObject n3 = new NamedObject("n1", OwningWg.WG14);
		UmlObject n4 = new NamedObject("n4", OwningWg.WG14);
		UmlObject n5 = new NamedObject("n5", OwningWg.WG10);
		List<UmlObject> input = Arrays.asList(new UmlObject[] { n0, n1, n2Null, n3, n4, n5 });

		List<UmlObject> exp = Arrays.asList(new UmlObject[] { n0, n3, n4, n5 });

		List<UmlObject> actual = AbstractUmlObject.collectForScope(input,
				EnumSet.of(OwningWg.WG10, OwningWg.WG14));

		assertEquals(exp, actual);
	}

	// -------------------

	@Ignore
	@Test
	public final void testClassifyPerScopePerTag() {
		// AbstractUmlObject.classifyPerScopePerTag(tags, scope);
		// TODO
	}

	// -------------------

	@Ignore
	@Test
	public final void testClassifyPerTag() {
		// AbstractUmlObject.classifyPerTag(tags, scope);
		// TODO
	}

	// -------------------

	@Ignore
	@Test
	public final void testSaveTags() {
		// AbstractUmlObject.saveTags(o, destination);
		// TODO
	}

	// -------------------

	@Ignore
	@Test
	public final void testFindWithSameUuidAndLog() {
		// AbstractUmlObject.findWithSameUuidAndLog(level, asker, objects, uuid);
		// TODO
	}
}
