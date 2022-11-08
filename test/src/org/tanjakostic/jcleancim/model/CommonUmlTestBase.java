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

import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import java.util.Properties;

import org.junit.After;
import org.junit.Before;
import org.tanjakostic.jcleancim.common.Config;
import org.tanjakostic.jcleancim.common.Nature;
import org.tanjakostic.jcleancim.util.ApplicationException;

/**
 * Common fixture and utility methods.
 *
 * @author tatjana.kostic@ieee.org
 * @version $Id: CommonUmlTestBase.java 25 2019-11-02 17:21:28Z dev978 $
 */
abstract public class CommonUmlTestBase {

	/** Empty in-memory configuration. */
	protected Config emptyCfg;

	/** Empty model (with its default null-packages and null-classes). */
	protected UmlModel emptyModel;

	@Before
	public void setUp() throws Exception {
		emptyModel = createEmptyModel();
		emptyCfg = emptyModel.getCfg();
	}

	@After
	public void tearDown() {
		emptyModel.clear();
	}

	/**
	 * Returns an empty model with empty configuration, i.e., it cannot distinguish model natures
	 * other than default (CIM). Useful for tests where you want to check that the elements belong
	 * to the same model, so you can use {@link #emptyModel} as usual, and create another empty
	 * model with this method.
	 * <p>
	 * For model configured to recognise 61850 nature, use
	 * {@link #createEmptyModelRecognising61850Nature(String)}.
	 *
	 * @throws ApplicationException
	 */
	private UmlModel createEmptyModel() throws ApplicationException {
		Properties props = new Properties();
		// props.put(Config.KEY_MODEL_FILENAME, "inexisting.eap");
		// props.put(Config.KEY_VALIDATION_SCOPE, "WG14");
		return new UmlModel(new Config(props, null));
	}

	/**
	 * Returns an empty model configured to recognise 61850 nature, but does NOT create the package.
	 * <code>p61850Name</code> is the name you have to use subsequently once to create the package
	 * of 61850; everything below that one is of 61850 nature.
	 * <p>
	 * Where you don't need special configurations for natures, just use {@link #emptyModel}.
	 * <p>
	 * To do both model and 61850-package creation in one go, use
	 * {@link #create61850Package(String)}; this first created package is 61850 nature, you can add
	 * others of default CIM nature.
	 *
	 * @param p61850Name
	 *            name for package under which everything is 61850 nature.
	 * @throws ApplicationException
	 */
	protected static UmlModel createEmptyModelRecognising61850Nature(String p61850Name)
			throws ApplicationException {
		Properties props = new Properties();
		props.put(Config.KEY_MODEL_NATURE_IEC61850, p61850Name);
		props.put(Config.KEY_VALIDATION_IEC61850_PACKAGE72_TOP, "IEC61850_7_2");
		UmlModel model = new UmlModel(new Config(props, null));
		return model;
	}

	/**
	 * Returns package of 61850 nature, within the model that recognises both 61850 and default
	 * nature packages.
	 *
	 * @throws ApplicationException
	 */
	protected static UmlPackage create61850Package(String p61850Name) throws ApplicationException {
		UmlModel model = createEmptyModelRecognising61850Nature(p61850Name);

		UmlPackage mp = UmlPackage.basic(model, p61850Name);
		assertSame(Nature.IEC61850, mp.getNature());
		return mp;
	}

	static void createPresenceConditionsEnumType(UmlPackage mp, String... literals) {
		UmlClass pcEnum = UmlClass.basic(mp, "PresenceConditions", "enumeration", "cond");
		assertTrue("is presence condition class", pcEnum.isConditionEnumeration());
		for (String lit : literals) {
			pcEnum.addAttribute(null, lit, "enum");
		}
	}

	/** For testing only: Adds association with default data. */
	static UmlAssociation addAssociation(UmlClass clazz1, UmlClass clazz2) {
		UmlAssociationEnd source = UmlAssociationEnd.basic(clazz1, "C1");
		UmlAssociationEnd target = UmlAssociationEnd.basic(clazz2, "C2");
		return clazz1.addAssociation(source, target, new UmlObjectData(""),
				UmlAssociation.Data.empty());
	}

	/** For testing only: Adds informative association with default data. */
	static UmlAssociation addInfAssociation(UmlClass clazz1, UmlClass clazz2) {
		UmlAssociationEnd source = UmlAssociationEnd.basic(clazz1, "C1");
		UmlAssociationEnd target = UmlAssociationEnd.basic(clazz2, "C2");
		return clazz1.addAssociation(source, target,
				new UmlObjectData(null, "", new UmlStereotype(UmlStereotype.INFORMATIVE)),
				UmlAssociation.Data.empty());
	}

	static UmlClass createClassWithAttributes(UmlPackage mp, String className, String... attr) {
		UmlClass c = UmlClass.basic(mp, className, "");
		for (String a : attr) {
			UmlClass type = UmlClass.basic(mp, String.format("%sType", a), "");
			c.addAttribute(type, a, "");
		}
		return c;
	}

	/**
	 * Note: for 61850 model one should create a model with non-CIM nature; in tests for
	 * dependencies and derived namespace infos, we don't care about that, but rather test the
	 * common structure.
	 */
	static class FixtureForDependenciesAndNamespaces {
		final UmlPackage mp;
		final UmlPackage tp61850;
		final UmlPackage wg10;
		final UmlPackage p72;
		final UmlPackage p73;
		final UmlPackage p74;
		final UmlPackage wg17;
		final UmlPackage p7420;

		FixtureForDependenciesAndNamespaces(UmlModel m) {
			mp = UmlPackage.basic(m, "mp");
			tp61850 = UmlPackage.basic(mp, "tp61850");
			wg10 = UmlPackage.basic(tp61850, "wg10");
			p72 = UmlPackage.basic(wg10, "72");
			p73 = UmlPackage.basic(wg10, "73");
			p74 = UmlPackage.basic(wg10, "74");
			wg17 = UmlPackage.basic(tp61850, "wg17");
			p7420 = UmlPackage.basic(wg17, "7420");
		}
	}

}
