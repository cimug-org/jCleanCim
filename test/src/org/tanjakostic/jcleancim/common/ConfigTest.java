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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.List;
import java.util.Properties;

import org.junit.Test;
import org.tanjakostic.jcleancim.util.ApplicationException;

/**
 * @author tatjana.kostic@ieee.org
 * @version $Id: ConfigTest.java 34 2019-12-20 18:37:17Z dev978 $
 */
public class ConfigTest {
	private static final String _PROPS_FILENAME = "testConfig.properties";

	private static final String _MODEL_CMDLINE_FILENAME = "testOverridenFromCmdLine.eap";
	private static final String _MODEL_FILENAME = "testCombined.eap";

	// ============= Tests ===============

	@SuppressWarnings("unused")
	@Test(expected = IllegalArgumentException.class)
	public final void testCtorPropsFileNameEmpty() throws ApplicationException {
		new Config("  ", _MODEL_CMDLINE_FILENAME);
	}

	@Test
	public final void testCtorPropsFileNameNull() throws ApplicationException {
		Config cfg = new Config((String) null, _MODEL_CMDLINE_FILENAME);
		assertEquals("uses default file name", Config.DEFAULT_PROPS_FILE_NAME,
				cfg.getPropsFileName());
	}

	@Test
	public final void testCtorPropsFileNameInexisting() throws ApplicationException {
		Config cfg = new Config("inexisting.properties", _MODEL_CMDLINE_FILENAME);
		String expectedSingleProperty = "project.version";
		assertEquals("returns properties with a single item", 20, cfg.getProperties().size());
		assertTrue("application (non-user) config always present",
				cfg.getProperties().containsKey(expectedSingleProperty));
	}

	@Test
	public final void testCtorOk() throws ApplicationException {
		Config cfg = new Config(_PROPS_FILENAME, null);
		assertTrue("contains a property from file",
				cfg.getModelFileAbsPath().endsWith("testCombined.eap"));
	}

	// ---------------------

	@SuppressWarnings("unused")
	@Test(expected = IllegalArgumentException.class)
	public final void testCtorModelFileNameEmpty() throws ApplicationException {
		new Config(_PROPS_FILENAME, "  ");
	}

	@SuppressWarnings("unused")
	@Test(expected = ApplicationException.class)
	public final void testCtorModelFileNameInexisting() throws ApplicationException {
		new Config(_PROPS_FILENAME, "inexisting.eap");
	}

	@SuppressWarnings("unused")
	@Test
	public final void testCtorModelFileNameNullAndModelPropEmptyAllowed()
			throws ApplicationException {
		Properties props = new Properties();
		props.put(Config.KEY_MODEL_FILENAME, "");
		props.put(Config.KEY_VALIDATION_SCOPE, "WG14");
		new Config(props, null);
	}

	@SuppressWarnings("unused")
	@Test(expected = ApplicationException.class)
	public final void testCtorModelFileNameNullAndModelPropInexistingFile()
			throws ApplicationException {
		Properties props = new Properties();
		props.put(Config.KEY_MODEL_FILENAME, "inexisting.eap");
		props.put(Config.KEY_VALIDATION_SCOPE, "WG14");
		new Config(props, null);
	}

	@Test
	public final void testCtorModelFileNameNullModelPropUsed() throws ApplicationException {
		Config cfg = new Config(_PROPS_FILENAME, null);
		String modelProp = cfg.value(Config.KEY_MODEL_FILENAME);
		assertFalse("model prop specified", modelProp.isEmpty());
		assertTrue("uses that one", cfg.getModelFileAbsPath().endsWith(modelProp));
	}

	@Test
	public final void testCtorModelFileNameOverridesModelProp() throws ApplicationException {
		Config cfg = new Config(_PROPS_FILENAME, _MODEL_CMDLINE_FILENAME);
		String modelProp = cfg.value(Config.KEY_MODEL_FILENAME);
		assertFalse("cmd line arg and property differ", _MODEL_CMDLINE_FILENAME.equals(modelProp));
		assertTrue("uses cmd line arg",
				cfg.getModelFileAbsPath().endsWith(_MODEL_CMDLINE_FILENAME));
	}

	// ---------------------

	@Test
	public final void testWhatIsDisabled() throws ApplicationException {
		Properties props = new Properties();
		props.put(Config.KEY_MODEL_FILENAME, _MODEL_FILENAME);
		props.put(Config.KEY_VALIDATION_ON, "");
		props.put(Config.KEY_STATISTICS_ON, "123");

		Config cfg = new Config(props, null);
		assertFalse("property with empty value", cfg.isValidationOn());
		assertFalse("property with value different than 'true'", cfg.isStatisticsOn());
		assertFalse("property not defined at all", cfg.isDocgenOn());
	}

	@Test
	public final void testWhatIsEnabledForTestsByDefault() throws ApplicationException {
		Config cfg = new Config(_PROPS_FILENAME, null);
		assertTrue(cfg.isValidationOn());
		assertTrue(cfg.isStatisticsOn());
		assertFalse(cfg.isDocgenOn());
	}

	// ---------------------

	@Test
	public final void testDocgenEna() throws ApplicationException {
		Properties props = new Properties();
		props.put(Config.KEY_MODEL_FILENAME, _MODEL_FILENAME);
		props.put(Config.KEY_DOCGEN_ON, "true");
		props.put(Config.KEY_DOCGEN_WORD_IN_TEMPLATE, "testTemplate.doc");
		props.put(Config.KEY_DOCGEN_WORD_OUT_DOCUMENT, "testIecDoc.doc");

		Config cfg = new Config(props, null);
		assertTrue(cfg.isDocgenOn());
	}

	@Test
	public final void testDocgenEnaWithoutFilenameProperties() throws ApplicationException {
		Properties props = new Properties();
		props.put(Config.KEY_MODEL_FILENAME, _MODEL_FILENAME);
		props.put(Config.KEY_DOCGEN_ON, "true");

		Config cfg = new Config(props, null);
		assertTrue(cfg.isDocgenOn());
		assertTrue("uses default file name", cfg.getDocgenWordInTemplateFileAbsPath()
				.endsWith(Config.DEFAULT_WORD_IN_TEMPLATE_FILENAME));
		assertTrue("uses default file name, will backup file if it exists by renaming it",
				cfg.getDocgenWordOutDocumentFileAbsPath()
						.endsWith(Config.DEFAULT_WORD_OUT_DOCUMENT_FILENAME));
	}

	@Test
	public final void testDocgenProfilesEna() throws ApplicationException {
		Properties props = new Properties();
		props.put(Config.KEY_DOCGEN_ON, "true");
		props.put(Config.KEY_PROFILES_DOCGEN_ON, "true");
		props.put(Config.KEY_PROFILES_RELPATH, "test/input/profiles");

		Config cfg = new Config(props, null);
		assertFalse(cfg.isDocgenModelOn());
		assertTrue(cfg.isProfilesDocgenOn());
	}

	@Test
	public final void testDocgenModelEna() throws ApplicationException {
		Properties props = new Properties();
		props.put(Config.KEY_DOCGEN_ON, "true");
		props.put(Config.KEY_PROFILES_DOCGEN_ON, "false");
		props.put(Config.KEY_PROFILES_RELPATH, "test/input/profiles");

		Config cfg = new Config(props, null);
		assertTrue(cfg.isDocgenModelOn());
		assertFalse(cfg.isProfilesDocgenOn());
	}

	@Test
	public final void testDocgenDisabled() throws ApplicationException {
		Properties props = new Properties();
		props.put(Config.KEY_DOCGEN_ON, "false");
		props.put(Config.KEY_PROFILES_DOCGEN_ON, "true");
		props.put(Config.KEY_PROFILES_RELPATH, "test/input/profiles");

		Config cfg = new Config(props, null);
		assertFalse(cfg.isDocgenModelOn());
		assertFalse(cfg.isProfilesDocgenOn());
	}

	// ---------------------

	@Test
	public final void testStatsIgnoreTagsList() throws ApplicationException {
		Properties props = new Properties();
		props.put(Config.KEY_MODEL_FILENAME, _MODEL_FILENAME);
		props.put(Config.KEY_STATISTICS_TAGS_TO_IGNORE, "t1, T2");

		Config cfg = new Config(props, null);
		assertEquals(new HashSet<>(Arrays.asList("T2", "t1")),
				new HashSet<>(cfg.getStatisticsTagsToIgnore()));
	}

	// ---------------------

	@Test
	public final void testGetProperties() throws ApplicationException {
		Config cfg = new Config(_PROPS_FILENAME, _MODEL_FILENAME);
		assertFalse(cfg.getProperties().isEmpty());
	}

	// ---------------------

	@Test
	public final void testValueInexisting() throws ApplicationException {
		Config cfg = new Config(_PROPS_FILENAME, _MODEL_FILENAME);
		assertNull(cfg.value("dummy"));
	}

	@Test
	public final void testValueExisting() throws ApplicationException {
		Config cfg = new Config(_PROPS_FILENAME, _MODEL_FILENAME);
		assertEquals("", cfg.value(Config.KEY_VALIDATION_SCOPE));
	}

	@Test
	public final void testValuePackageDoAbbr() throws ApplicationException {
		Properties props = new Properties();
		props.put(Config.KEY_MODEL_FILENAME, _MODEL_FILENAME);
		props.put(Config.KEY_VALIDATION_IEC61850_PACKAGES_DO_ABBR, "Abbreviations");

		Config cfg = new Config(props, null);
		String abbr = cfg.value(Config.KEY_VALIDATION_IEC61850_PACKAGES_DO_ABBR);
		assertTrue(cfg.getValidationIec61850PackagesDoAbbr().contains(abbr));
	}

	// ---------------------

	@Test
	public final void testValueModelBuilderEmptySetsDefault() throws ApplicationException {
		Properties props = new Properties();
		props.put(Config.KEY_MODEL_FILENAME, _MODEL_FILENAME);
		props.put(Config.KEY_MODEL_BUILDER, "");

		Config cfg = new Config(props, null);
		assertEquals("empty property sets default: db", ModelBuilderKind.db, cfg.getModelBuilder());
	}

	@Test
	public final void testValueModelBuilderAbsentSetsDefault() throws ApplicationException {
		Properties props = new Properties();
		props.put(Config.KEY_MODEL_FILENAME, _MODEL_FILENAME);

		Config cfg = new Config(props, null);
		assertEquals("absent property sets default: db", ModelBuilderKind.db,
				cfg.getModelBuilder());
	}

	@Test
	public final void testValueModelBuilderUnknownSetsDefault() throws ApplicationException {
		Properties props = new Properties();
		props.put(Config.KEY_MODEL_FILENAME, _MODEL_FILENAME);
		props.put(Config.KEY_MODEL_BUILDER, "dummy");

		Config cfg = new Config(props, null);
		assertEquals("unknown property sets default: db", ModelBuilderKind.db,
				cfg.getModelBuilder());
	}

	@Test
	public final void testValueModelBuilderOtherThanDefault() throws ApplicationException {
		Properties props = new Properties();
		props.put(Config.KEY_MODEL_FILENAME, _MODEL_FILENAME);
		props.put(Config.KEY_MODEL_BUILDER, "sqlxml");

		Config cfg = new Config(props, null);
		assertEquals(ModelBuilderKind.sqlxml, cfg.getModelBuilder());
	}

	// ---------------------

	@Test
	public final void testIsXmiexportOn() throws ApplicationException {
		Properties props = new Properties();
		props.put(Config.KEY_MODEL_FILENAME, _MODEL_FILENAME);
		props.put(Config.KEY_XMIEXPORT_ON, "true");

		Config cfg = new Config(props, null);
		assertTrue("xmi export should be enabled", cfg.isXmiexportOn());
	}

	@Test
	public final void testValueXmiDialectsSingle() throws ApplicationException {
		Properties props = new Properties();
		props.put(Config.KEY_MODEL_FILENAME, _MODEL_FILENAME);
		props.put(Config.KEY_XMIEXPORT_ON, "true");
		props.put(Config.KEY_XMIEXPORT_DIALECTS, "ea_xmi11");

		Config cfg = new Config(props, null);
		String dialects = cfg.value(Config.KEY_XMIEXPORT_DIALECTS);
		assertEquals("ea_xmi11", dialects);
		assertEquals(EnumSet.of(XMIDialect.ea_xmi11), cfg.getXmiexportDialects());
	}

	@Test
	public final void testValueXmiDialectsEmptyPicksThemAllExceptCimtool()
			throws ApplicationException {
		Properties props = new Properties();
		props.put(Config.KEY_MODEL_FILENAME, _MODEL_FILENAME);
		props.put(Config.KEY_XMIEXPORT_DIALECTS, "");

		Config cfg = new Config(props, null);
		EnumSet<XMIDialect> withoutCimtool = EnumSet.allOf(XMIDialect.class);
		withoutCimtool.remove(XMIDialect.cimtool);
		assertEquals("empty XMI export dialects takes them all", withoutCimtool,
				cfg.getXmiexportDialects());
	}

	// ---------------------

	@Test
	public final void testValueScopeSingle() throws ApplicationException {
		Properties props = new Properties();
		props.put(Config.KEY_MODEL_FILENAME, _MODEL_FILENAME);
		props.put(Config.KEY_VALIDATION_SCOPE, "WG14");

		Config cfg = new Config(props, null);
		String scope = cfg.value(Config.KEY_VALIDATION_SCOPE);
		assertEquals("WG14", scope);
		assertEquals(EnumSet.of(OwningWg.WG14), cfg.getValidationScope());
	}

	@Test
	public final void testValueScopeList() throws ApplicationException {
		Properties props = new Properties();
		props.put(Config.KEY_MODEL_FILENAME, _MODEL_FILENAME);
		props.put(Config.KEY_VALIDATION_SCOPE, "WG13, WG14");

		Config cfg = new Config(props, null);
		assertEquals(EnumSet.of(OwningWg.WG14, OwningWg.WG13), cfg.getValidationScope());
	}

	@Test
	public final void testValueScopeListWithIllegalMember() throws ApplicationException {
		Properties props = new Properties();
		props.put(Config.KEY_MODEL_FILENAME, _MODEL_FILENAME);
		props.put(Config.KEY_VALIDATION_SCOPE, "WG13, dummy");

		Config cfg = new Config(props, null);
		assertEquals(EnumSet.of(OwningWg.WG13), cfg.getValidationScope());
	}

	@Test
	public final void testValueScopeEmptyPicksThemAll() throws ApplicationException {
		Properties props = new Properties();
		props.put(Config.KEY_MODEL_FILENAME, _MODEL_FILENAME);
		props.put(Config.KEY_VALIDATION_SCOPE, "");

		Config cfg = new Config(props, null);
		assertEquals("empty scope takes them all", EnumSet.allOf(OwningWg.class),
				cfg.getValidationScope());
	}

	@Test
	public final void testValueScopeSingleUnknownPicksThemAll() throws ApplicationException {
		Properties props = new Properties();
		props.put(Config.KEY_MODEL_FILENAME, _MODEL_FILENAME);
		props.put(Config.KEY_VALIDATION_SCOPE, "dummy");

		Config cfg = new Config(props, null);
		assertEquals("empty scope takes them all", EnumSet.allOf(OwningWg.class),
				cfg.getValidationScope());
	}

	// ---------------------

	@Test
	public final void testGetBlankPngFileAbsPath() throws ApplicationException {
		Config cfg = new Config(new Properties(), null);
		File f = new File(cfg.getBlankPngFileAbsPath());
		assertTrue("file found", f.exists());
	}

	// ---------------------

	@Test
	public final void testValueProfileFilesSingleSubdir() throws ApplicationException {
		Properties props = new Properties();
		props.put(Config.KEY_DOCGEN_ON, "true");
		props.put(Config.KEY_PROFILES_DOCGEN_ON, "true");
		props.put(Config.KEY_PROFILES_RELPATH, "test/input/profiles");
		props.put(Config.KEY_PROFILES_DIRNAMES, "WG14");

		Config cfg = new Config(props, null);

		assertEquals(EnumSet.of(OwningWg.WG14), cfg.getProfileFiles().keySet());
	}

	@Test
	public final void testValueProfileFilesList() throws ApplicationException {
		Properties props = new Properties();
		props.put(Config.KEY_DOCGEN_ON, "true");
		props.put(Config.KEY_PROFILES_DOCGEN_ON, "true");
		props.put(Config.KEY_PROFILES_RELPATH, "test/input/profiles");
		props.put(Config.KEY_PROFILES_DIRNAMES, "WG13, WG14");

		Config cfg = new Config(props, null);
		assertEquals("WG13 is empty", EnumSet.of(OwningWg.WG14), cfg.getProfileFiles().keySet());
		assertEquals(4, cfg.getProfileFiles().get(OwningWg.WG14).size());
	}

	@Test
	public final void testValueProfileFilesListWithIllegalMember() throws ApplicationException {
		Properties props = new Properties();
		props.put(Config.KEY_DOCGEN_ON, "true");
		props.put(Config.KEY_PROFILES_DOCGEN_ON, "true");
		props.put(Config.KEY_PROFILES_RELPATH, "test/input/profiles");
		props.put(Config.KEY_PROFILES_DIRNAMES, "WG14, dummy, OTHER_CIM");

		Config cfg = new Config(props, null);
		assertEquals(EnumSet.of(OwningWg.WG14, OwningWg.OTHER_CIM), cfg.getProfileFiles().keySet());
	}

	@Test
	public final void testValueProfileFilesEmptyPicksThemAll() throws ApplicationException {
		Properties props = new Properties();
		props.put(Config.KEY_DOCGEN_ON, "true");
		props.put(Config.KEY_PROFILES_DOCGEN_ON, "true");
		props.put(Config.KEY_PROFILES_RELPATH, "test/input/profiles");
		props.put(Config.KEY_PROFILES_DIRNAMES, "");

		Config cfg = new Config(props, null);
		assertEquals("empty scope takes them all (which have some profiles)",
				EnumSet.of(OwningWg.WG14, OwningWg.OTHER_CIM), cfg.getProfileFiles().keySet());
	}

	@Test
	public final void testValueProfileFilesSingleUnknownPicksThemAll() throws ApplicationException {
		Properties props = new Properties();
		props.put(Config.KEY_DOCGEN_ON, "true");
		props.put(Config.KEY_PROFILES_DOCGEN_ON, "true");
		props.put(Config.KEY_PROFILES_RELPATH, "test/input/profiles");
		props.put(Config.KEY_PROFILES_DIRNAMES, "dummy");

		Config cfg = new Config(props, null);
		assertEquals("empty scope takes them all (which have some profiles)",
				EnumSet.of(OwningWg.WG14, OwningWg.OTHER_CIM), cfg.getProfileFiles().keySet());
	}

	// ---------------------

	@Test
	public final void testValueSaveReopenEveryEmpty() throws ApplicationException {
		Properties props = new Properties();
		props.put(Config.KEY_DOCGEN_WORD_SAVE_REOPEN_EVERY, "");

		Config cfg = new Config(props, null);

		assertEquals(-1, cfg.getDocgenWordSaveReopenEvery());
	}

	@Test
	public final void testValueSaveReopenEveryNonInt() throws ApplicationException {
		Properties props = new Properties();
		props.put(Config.KEY_DOCGEN_WORD_SAVE_REOPEN_EVERY, "toto");

		Config cfg = new Config(props, null);

		assertEquals(-1, cfg.getDocgenWordSaveReopenEvery());
	}

	@Test
	public final void testValueSaveReopenEveryZero() throws ApplicationException {
		Properties props = new Properties();
		props.put(Config.KEY_DOCGEN_WORD_SAVE_REOPEN_EVERY, "0");

		Config cfg = new Config(props, null);

		assertEquals(0, cfg.getDocgenWordSaveReopenEvery());
	}

	@Test
	public final void testValueSaveReopenEveryNegative() throws ApplicationException {
		Properties props = new Properties();
		props.put(Config.KEY_DOCGEN_WORD_SAVE_REOPEN_EVERY, "-1");

		Config cfg = new Config(props, null);

		assertEquals(1, cfg.getDocgenWordSaveReopenEvery());
	}

	@Test
	public final void testValueSaveReopenEvery() throws ApplicationException {
		Properties props = new Properties();
		props.put(Config.KEY_DOCGEN_WORD_SAVE_REOPEN_EVERY, "3");

		Config cfg = new Config(props, null);

		assertEquals(3, cfg.getDocgenWordSaveReopenEvery());
	}

	// ---------------------

	@Test
	public final void testGetDocgenWordCaptionLabelsAndStylesNeverEmpty()
			throws ApplicationException {
		Properties props = new Properties();
		Config cfg = new Config(props, null);

		assertGetDocgenWordStylesNeverEmpty(cfg.getDocgenWordStylesPrefixToc(),
				Config.DEFAULT_STYLES_PREFIX_TOC);
		assertGetDocgenWordStylesNeverEmpty(cfg.getDocgenWordStylesPrefixHead(),
				Config.DEFAULT_STYLES_PREFIX_HEAD);
		assertGetDocgenWordStylesNeverEmpty(cfg.getDocgenWordStylesPara(),
				Config.DEFAULT_STYLES_PARA);
		assertGetDocgenWordStylesNeverEmpty(cfg.getDocgenWordStylesFig(),
				Config.DEFAULT_STYLES_FIG);
		assertGetDocgenWordStylesNeverEmpty(cfg.getDocgenWordStylesTabhead(),
				Config.DEFAULT_STYLES_TABHEAD);
		assertGetDocgenWordStylesNeverEmpty(cfg.getDocgenWordStylesTabcell(),
				Config.DEFAULT_STYLES_TABCELL);
		assertGetDocgenWordStylesNeverEmpty(cfg.getDocgenWordStylesFigcapt(),
				Config.DEFAULT_STYLES_FIGCAPT);
		assertGetDocgenWordStylesNeverEmpty(cfg.getDocgenWordStylesTabcapt(),
				Config.DEFAULT_STYLES_TABCAPT);
	}

	public void assertGetDocgenWordStylesNeverEmpty(List<String> cfgList, String defaultValue) {
		assertEquals(1, cfgList.size());
		assertEquals(defaultValue, cfgList.get(0));
	}
}
