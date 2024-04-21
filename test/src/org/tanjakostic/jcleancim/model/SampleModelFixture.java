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

import java.util.Properties;

import org.tanjakostic.jcleancim.common.Config;
import org.tanjakostic.jcleancim.model.UmlAttribute;
import org.tanjakostic.jcleancim.model.UmlClass;
import org.tanjakostic.jcleancim.model.UmlModel;
import org.tanjakostic.jcleancim.model.UmlMultiplicity;
import org.tanjakostic.jcleancim.model.UmlObjectData;
import org.tanjakostic.jcleancim.model.UmlPackage;
import org.tanjakostic.jcleancim.model.UmlStereotype;
import org.tanjakostic.jcleancim.util.ApplicationException;

/**
 * @author tatjana.kostic@ieee.org
 * @version $Id: SampleModelFixture.java 15 2016-07-12 15:11:42Z dev978 $
 */
public class SampleModelFixture {

	private final Config _cfg;
	private final UmlModel _model;

	private SampleModelFixture() throws ApplicationException {
		Properties props = new Properties();
		props.put(Config.KEY_MODEL_NATURE_IEC61850, "IEC61850Domain, My61850Ext");

		_cfg = new Config(props, null);
		_model = new UmlModel(_cfg);
		UmlPackage mCim = UmlPackage.basic(_model, "TC57CIM");

		UmlPackage cim61970 = UmlPackage.basic(mCim, "IEC61970");
		/**/UmlPackage pDomain = UmlPackage.basic(cim61970, "Domain");
		/*--*/UmlClass cString = UmlClass.basic(pDomain, "String", UmlStereotype.PRIMITIVE);
		/*--*/UmlClass cFloat = UmlClass.basic(pDomain, "Float", UmlStereotype.PRIMITIVE);
		/*--*/UmlClass cADT = UmlClass.basic(pDomain, "AbsoluteDateTime",
				UmlStereotype.CIMDATATYPE);
		/*----*/UmlAttribute aADTval = UmlAttribute.basic(cADT, cString, "value");
		/*--*/UmlClass cUnitSymbol = UmlClass.basic(pDomain, "UnitSymbol",
				UmlStereotype.ENUMERATION);
		/*----*/UmlAttribute aUnitSymbW = UmlAttribute.basicLiteral(cUnitSymbol, "W");
		/*----*/UmlAttribute aUnitSymbs = UmlAttribute.basicLiteral(cUnitSymbol, "s");
		/*--*/UmlClass cUnitMultipl = UmlClass.basic(pDomain, "UnitMultiplier",
				UmlStereotype.ENUMERATION);
		/*----*/UmlAttribute aUnitMultk = UmlAttribute.basicLiteral(cUnitMultipl, "k");
		/*----*/UmlAttribute aUnitMultnone = UmlAttribute.basicLiteral(cUnitMultipl, "none");
		/*--*/UmlClass cAP = UmlClass.basic(pDomain, "ActivePower", UmlStereotype.CIMDATATYPE);
		/*----*/UmlAttribute aAPval = UmlAttribute.basic(cAP, cFloat, "value");
		/*----*/UmlAttribute aAPunit = new UmlAttribute(cAP, cUnitSymbol, new UmlObjectData(
				"unit"), new UmlAttribute.Data(true, true, UmlMultiplicity.OPT_ONE, "W", 0, "",
				false));
		/*----*/UmlAttribute aAPmult = UmlAttribute.basic(cAP, cUnitMultipl, "multiplier");
		/**/UmlPackage pCore = UmlPackage.basic(cim61970, "Core");
		/*--*/UmlClass vc61970 = UmlClass.basic(cim61970, "IEC61970CIMVersion", "");
		/*----*/UmlAttribute aVc61970version = UmlAttribute.basic(vc61970, cString, "version",
				"15v03");
		/*----*/UmlAttribute aVc61970date = UmlAttribute.basic(vc61970, cADT, "date",
				"2010-12-31");
		/*--*/UmlClass cIdObj = UmlClass.basic(pCore, "IdentifiedObject", "");
		/*----*/UmlAttribute aIdObjMrid = UmlAttribute.basic(cIdObj, cString, "mRID");
		/*----*/UmlAttribute aIdObjName = UmlAttribute.basic(cIdObj, cString, "name");
		/*--*/UmlClass cPSR = UmlClass.basic(pCore, cIdObj, "PowerSystemResource");
		/*--*/UmlClass cCnContainer = UmlClass.basic(pCore, cPSR, "ConnectivityNodeContainer");
		/*--*/UmlClass cEqContainer = UmlClass.basic(pCore, cCnContainer, "EquipmentContainer");
		/*--*/UmlClass cEquipment = UmlClass.basic(pCore, cPSR, "Equipment");
		/*--*/UmlClass cCondEquipment = UmlClass.basic(pCore, cEquipment, "ConductingEquipment");
		CommonUmlTestBase.addAssociation(cEqContainer, cEquipment);

		UmlPackage cim61968 = UmlPackage.basic(mCim, "IEC61968");
		cim61968.addDependency(cim61970);
		/*--*/UmlClass vc61968 = UmlClass.basic(cim61968, "IEC61968CIMVersion", "");
		/*----*/UmlAttribute aVc61968version = UmlAttribute.basic(vc61968, cString, "version",
				"11v04");
		/*----*/UmlAttribute aVc61968date = UmlAttribute.basic(vc61968, cADT, "date",
				"2010-12-31");
		/**/UmlPackage pCommon = UmlPackage.basic(cim61968, "Common");
		/**/UmlPackage pAssets = UmlPackage.basic(cim61968, "Assets");
		pAssets.addDependency(pCommon);
		/*--*/UmlClass cAsset = UmlClass.basic(cim61968, cIdObj, "Asset");

		CommonUmlTestBase.addAssociation(cAsset, cPSR);

		UmlPackage cim62325 = UmlPackage.basic(mCim, "IEC62325");

		// ----------------------------
		UmlPackage m61850 = UmlPackage.basic(_model, "IEC61850Domain");
		UmlPackage pwg10 = UmlPackage.basic(m61850, "WG10");
		UmlPackage pwg18 = UmlPackage.basic(m61850, "WG18");

		// ----------------------------
		UmlPackage mCimExt = UmlPackage.basic(_model, "MyCimExt");
		UmlPackage pCimExt = UmlPackage.basic(mCimExt, "ACimProject");

		// ----------------------------
		UmlPackage m61850Ext = UmlPackage.basic(_model, "My61850Ext");
		UmlPackage p61850Ext = UmlPackage.basic(m61850Ext, "AnIEC61850Project");
	}

	public static UmlModel create() throws ApplicationException {
		return new SampleModelFixture()._model;
	}
}
