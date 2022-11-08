/**
 * Copyright (C) 2015-2017 Gian Luigi (Gigi) Pugni
 * <p>
 * This file belongs to jCleanCim, a tool supporting tasks of UML model managers for IEC TC57 CIM
 * and 61850 models.
 * <p>
 * This program is free software: you can redistribute it and/or modify it under the terms of the
 * GNU Lesser General Public License version 3.
 * <p>
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without
 * even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details. You should have received a copy of the GNU Lesser
 * General Public License along with this program. If not, see <http://www.gnu.org/licenses/>.
 */

package org.gigipugni.jcleancim.mibgen;

import java.io.FileWriter;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Stack;

import org.tanjakostic.jcleancim.common.Config;
import org.tanjakostic.jcleancim.docgen.collector.DocgenConfig;
import org.tanjakostic.jcleancim.docgen.collector.ModelFinder;
import org.tanjakostic.jcleancim.docgen.collector.PackageDoc;
import org.tanjakostic.jcleancim.docgen.collector.impl.ModelFinderImpl;
import org.tanjakostic.jcleancim.model.UmlAttribute;
import org.tanjakostic.jcleancim.model.UmlClass;
import org.tanjakostic.jcleancim.model.UmlModel;
import org.tanjakostic.jcleancim.model.UmlPackage;

/**
 * @author Gian Luigi (Gigi) Pugni
 * @author tatjana.kostic@ieee.org
 * @version $Id$
 */
public class MibGen {

	static final String SP15 = "               ";

	private final Config _cfg;
	private final DocgenConfig _docgenCfg;
	private final ModelFinder _modelFinder;
	private final boolean _fromUml;
	private MibWriter _mibWriter;
	private UmlPackage _package;
	private FileWriter _outFile;
	private String _mibPrefix;
	private String _objectIdentity;
	private String _mibName;
	private String _mibIdentity;
	private boolean _isAgent;
	private boolean _isEntry;
	private boolean _isEnumeration;

	// package stack control
	private int _currentPackageLevel;
	private String _enumeratedTypesList;

	private Stack<UmlPackage> _packageTree;
	private Stack<Integer> _packageBranchIdCounter;
	private Stack<String> _packageName;

	private int _agentBranchIdCounter;

	/**
	 * Model package docs are indexed, the individual package docs contain their children
	 * recursively; we use this map for logging only. The retained docs, including packages with the
	 * same name, are in {@link #getNonSkippedPackageDocs()}.
	 */
	private final Map<String, PackageDoc> _modelPackageDocs = new LinkedHashMap<String, PackageDoc>();

	/**
	 * Constructs the collector from the UML model. After construction, call TODO to obtain the
	 * input for document generation.
	 *
	 * @param model
	 */
	public MibGen(UmlModel model) {
		this(model.getCfg(), new ModelFinderImpl(model), true);
	}

	private MibGen(Config cfg, ModelFinder modelFinder, boolean fromUml) {
		_docgenCfg = new DocgenConfig(cfg);
		_modelFinder = modelFinder;
		_fromUml = fromUml;
		_cfg = cfg;
	}

	public boolean isFromUml() {
		return _fromUml;
	}

	public DocgenConfig getDocgenCfg() {
		return _docgenCfg;
	}

	public void collectMib(UmlModel model, boolean liteMib) {
		if (!isFromUml()) {
			throw new UnsupportedOperationException("This collector has been created without"
					+ "underlying UML model, so you have to add packages from your code by"
					+ " using other methods than this one (collect()).");
		}

		if (liteMib) {
			System.out.print("writing to: " + _cfg.getMibgenOutDirLightAbsPath());
		} else {
			System.out.print("writing to: " + _cfg.getMibgenOutDirFullAbsPath());
		}

		_mibWriter = new MibWriter(_cfg, liteMib, new MibTemplate());

		_currentPackageLevel = -2; // current package not present
		_packageTree = new Stack<>();
		_packageName = new Stack<>();
		_packageBranchIdCounter = new Stack<>();
		_enumeratedTypesList = "";

		for (UmlPackage p : model.getPackages()) {
			collectMibPackage(p);
		}

		if (_mibWriter != null) {
			_mibWriter.closeMib(_agentBranchIdCounter);
			_mibWriter.closeMibFile();
		}
	}

	private void collectMibPackage(UmlPackage p) {
		_package = p;
		_mibWriter.resetBranchId();

		System.out.println("Package: " + p.getName() + ": " + p.getDepth());

		if (p.getTaggedValues().get("objectIdentity") != null) {
			// first cleanup the upper stack if we are going down in hierarchy
			while (_currentPackageLevel > p.getDepth()) {

				if ((_mibWriter.getCurrentMibIdentity() != null)
						&& _mibWriter.getCurrentMibIdentity().equals(_packageName.peek())) {
					// close previous MIB

					_mibWriter.closeMib(_agentBranchIdCounter);
				}
				System.out.println("Package removal: " + _packageName.peek());
				_packageName.pop();
				_packageTree.pop();
				_packageBranchIdCounter.pop();
				_currentPackageLevel = p.getDepth();

			}
			// if we are going at inner level then add new package to the stack
			if (_currentPackageLevel < p.getDepth()) {
				_currentPackageLevel = p.getDepth();
				_packageName.push(p.getTaggedValues().get("objectIdentity"));
				_packageTree.push(p);
				if (p.getTaggedValues().get("objectBranchId") != null) {
					// specified branch
					int j = Integer.valueOf(p.getTaggedValues().get("objectBranchId"));
					_packageBranchIdCounter.push(j);
					_currentPackageLevel = p.getDepth();
					System.out.println("Package inner: " + p.getName() + " branchid: " + j);
				} else {
					// automatic branch
					_packageBranchIdCounter.push(1);
					_currentPackageLevel = p.getDepth();
					System.out.println("Package inner: " + p.getName() + " branchid: " + "1");
				}
			} else if (_currentPackageLevel == p.getDepth()) {
				// same level of hierarchy
				if (_mibWriter != null) {
					if (_mibWriter.getCurrentMibIdentity() != null) {
						if (_mibWriter.getCurrentMibIdentity().equals(_packageName.peek())) {
							// close previous MIB
							_mibWriter.closeMib(_agentBranchIdCounter);
						}
					}
				}
				_packageName.pop();
				_packageTree.pop();
				_packageName.push(p.getTaggedValues().get("objectIdentity"));
				_packageTree.push(p);
				int i = _packageBranchIdCounter.pop();
				if (p.getTaggedValues().get("objectBranchId") == null) {
					_packageBranchIdCounter.push(++i);
					System.out.println("Package samelevel: " + p.getName() + " branchid++: " + i);
				} else {
					_packageBranchIdCounter
							.push(Integer.valueOf(p.getTaggedValues().get("objectBranchId")));
					System.out.println(
							"Package samelevel: " + p.getName() + " branchid_form objectBranchId: "
									+ p.getTaggedValues().get("objectBranchId"));
				}
			}

		}

		if (p.getTaggedValues().get("mibName") != null) {
			// close previous MIB
			// if (_mibWriter.GetCurrentMibIdentity() != null)
			// _mibWriter.CloseMib(_packageBranchIdCounter.peek());
			// get new mib name
			_mibName = p.getTaggedValues().get("mibName");
			if (p.getTaggedValues().get("mibIdentity") != null) {
				_mibIdentity = p.getTaggedValues().get("mibIdentity");
				// open new mib
			}

			// for testing purposes:

			_mibWriter.resetRootPackages();
			Iterator<String> itr = _packageName.iterator();
			Iterator<Integer> itr2 = _packageBranchIdCounter.iterator();
			Iterator<UmlPackage> itr3 = _packageTree.iterator();
			while (itr.hasNext()) {
				String pname = itr.next();
				if (!pname.equals(_mibIdentity)) {
					_mibWriter.writeObjectIdentifier(pname, itr3.next().getContainingPackage()
							.getTaggedValues().get("objectIdentity"), itr2.next().toString());
				}

			}

			_mibWriter.writeModuleHeader(_mibName, _mibIdentity, p.getDescription().toString(),
					p.getContainingPackage().getTaggedValues().get("objectIdentity"),
					Integer.toString(_packageBranchIdCounter.peek()), _enumeratedTypesList);
			_agentBranchIdCounter = 1;
		}

		if (p.getTaggedValues().get("isAgent") != null) {
			_isAgent = p.getTaggedValues().get("isAgent").equals("yes");
		} else {
			_isAgent = false;
		}

		if (p.getTaggedValues().get("isEnumeration") != null) {
			_isEnumeration = p.getTaggedValues().get("isEnumeration").equals("yes");
		} else {
			_isEnumeration = false;
		}

		if (p.getTaggedValues().get("mibPrefix") != null) {
			_mibPrefix = p.getTaggedValues().get("mibPrefix");
		}

		if (p.getTaggedValues().get("objectIdentity") != null) {
			_objectIdentity = p.getTaggedValues().get("objectIdentity");

			if ((p.getContainingPackage() != null)
					&& (p.getTaggedValues().get("mibName") == null)) {
				if (p.getContainingPackage().getTaggedValues().get("objectIdentity") != null) {
					_mibWriter.writePackageObjectIdentity(_objectIdentity, p,
							Integer.toString(_agentBranchIdCounter));
				}
			}
		}

		for (UmlClass c : p.getClasses()) {
			_mibWriter.resetBranchId();

			if (_isEnumeration) {
				if (c.isEnumeratedType()) {
					String enumerations = prepareEnumeration(c);
					_mibWriter.writeType(c.getName(), "INTEGER", "current",
							c.getDescription().toString(), enumerations);
					if (_enumeratedTypesList.length() > 0) {
						_enumeratedTypesList += ", ";
					}
					_enumeratedTypesList += c.getName();
				}
			}
			collectMibAttribute(c);
		}

		if (_isEnumeration) {
			_enumeratedTypesList += " FROM " + p.getTaggedValues().get("mibName").toString()
					+ System.lineSeparator();
		}
	}

	private void collectMibAttribute(UmlClass c) {
		boolean _isAbstractObject = false;

		if (c.getTaggedValues().get("isAbstractObject") != null) {
			_isAbstractObject = c.getTaggedValues().get("isAbstractObject").equals("yes");
		}

		if (c.getTaggedValues().get("isEnumeration") != null) {
			_isEnumeration = c.getTaggedValues().get("isEnumeration").equals("yes");
		}

		if (c.getTaggedValues().get("mibPrefix") != null) {
			_mibPrefix = c.getTaggedValues().get("mibPrefix");
		}

		if (c.getStereotype().value() != null) {
			_isAgent = (c.getStereotype().value().equals("nsmAgent"));
		} else {
			_isAgent = false;
		}

		if (c.getStereotype().value() != null) {
			_isEntry = (c.getStereotype().value().equals("nsmEntry"));
		} else {
			_isEntry = false;
		}

		if (c.getTaggedValues().get("objectIdentity") != null) {
			_objectIdentity = c.getTaggedValues().get("objectIdentity");
		}

		if (_isAgent) {
			_mibWriter.writeClassObjectIdentity(_objectIdentity, c);
		}

		if (_isAgent || _isEntry) {
			int version = 0; // start with lower version attributes
			boolean found = false;

			do {
				found = false;

				for (UmlAttribute a : c.getAttributes()) {
					if (findAttribute(a, version)) {
						found = true;
					}
				}

				for (UmlAttribute a : c.getInheritedAttributes()) {
					if (findAttribute(a, version)) {
						found = true;
					}
				}
				version++;
			} while (found);
		}
	}

	private boolean findAttribute(UmlAttribute a, int version) {
		boolean found = false;
		if (a.getType() != null) {
			if (a.getTaggedValues().get("Version") != null) {
				if (a.getTaggedValues().get("Version").equals(String.valueOf(version))) {
					found = true;
					if (a.getStereotype().value().equals("trap")) {
						prepareTrap(a.getType());
					} else if (a.getType().getAttributes() != null) {
						if (a.getMultiplicity().getUpper().equals("1")) {
							prepareObject(a);
						} else {
							prepareTable(a);
						}
					}

				}
			}
		}
		return found;
	}

	private void prepareObject(UmlAttribute a) {
		_mibWriter.writeObject(_mibPrefix, _objectIdentity, a);
	}

	private void prepareTrap(UmlClass c) {
		String enumerations = "";

		for (UmlAttribute a : c.getAttributes()) {
			for (UmlAttribute aa : a.getType().getAttributes()) {
				if (aa.getName().equals("Value")) {
					if (enumerations.length() > 0) {
						enumerations += ",";
					}
					enumerations += System.lineSeparator() + MibGen.SP15;
					enumerations += _mibPrefix + a.getName();
				}
				if (aa.getName().equals("TimeStamp")) {
					if (enumerations.length() > 0) {
						enumerations += ",";
					}
					enumerations += System.lineSeparator() + MibGen.SP15;
					enumerations += _mibPrefix + a.getName() + "Ts";
				}
			}
		}

		for (UmlAttribute a : c.getInheritedAttributes()) {
			for (UmlAttribute aa : a.getType().getAttributes()) {
				if (aa.getName().equals("Value")) {
					if (enumerations.length() > 0) {
						enumerations += ",";
					}
					enumerations += System.lineSeparator() + MibGen.SP15;
					enumerations += _mibPrefix + a.getName();
				}
				if (aa.getName().equals("TimeStamp")) {
					if (enumerations.length() > 0) {
						enumerations += ",";
					}
					enumerations += System.lineSeparator() + MibGen.SP15;
					enumerations += _mibPrefix + a.getName() + "Ts";
				}
			}
		}

		_mibWriter.writeTrap(_mibPrefix + c.getName(), "current", c.getDescription().toString(),
				_objectIdentity, enumerations);
	}

	private String prepareEnumeration(UmlClass c) {
		String enumerations = "";

		for (UmlAttribute a : c.getAttributes()) {
			if (enumerations.length() > 0) {
				enumerations += ",";
			}
			enumerations += System.lineSeparator() + MibGen.SP15;
			enumerations += a.getName() + "( " + a.getInitValue() + " )";
		}
		return enumerations;
	}

	private void prepareTable(UmlAttribute a) {
		String tableMibPrefix = _mibPrefix;
		String tableEntrySequence = "";
		String tableIndex = "";

		// first get attributes

		for (UmlAttribute aa : a.getType().getAttributes()) {
			if (aa.getStereotype() != null) {
				if (aa.getStereotype().value().equals("index")) {
					if (tableIndex.length() > 0) {
						tableIndex += ",";
					} else {
						tableIndex += MibGen.SP15;
					}
					tableIndex += a.getType().getTaggedValues().get("mibPrefix") + aa.getName()
							+ System.lineSeparator();
				}
			}

			for (UmlAttribute aaa : aa.getType().getAttributes()) {
				if (aaa.getName().equals("Value")) {
					if (tableEntrySequence.length() == 0) {
						tableEntrySequence = MibGen.SP15;
					} else {
						tableEntrySequence += "," + System.lineSeparator() + MibGen.SP15;
					}
					tableEntrySequence = tableEntrySequence
							+ a.getType().getTaggedValues().get("mibPrefix") + aa.getName() + " "
							+ aaa.getEaTypeName();
				}
				if (aaa.getName().equals("TimeStamp")) {
					if (tableEntrySequence.length() == 0) {
						tableEntrySequence = MibGen.SP15;
					} else {
						tableEntrySequence += "," + System.lineSeparator() + MibGen.SP15;
					}
					tableEntrySequence = tableEntrySequence
							+ a.getType().getTaggedValues().get("mibPrefix") + aa.getName() + "Ts"
							+ " " + aaa.getEaTypeName();
				}
			}
		}

		// then get inherited attributes

		for (UmlAttribute aa : a.getType().getInheritedAttributes()) {
			if (aa.getStereotype() != null) {
				if (aa.getStereotype().value().equals("index")) {
					if (tableIndex.length() > 0) {
						tableIndex += ",";
					} else {
						tableIndex += MibGen.SP15;
					}
					tableIndex += a.getType().getTaggedValues().get("mibPrefix") + aa.getName()
							+ System.lineSeparator() + MibGen.SP15;
				}
			}

			for (Iterator<UmlAttribute> ii = aa.getType().getAttributes().iterator(); ii
					.hasNext();) {
				UmlAttribute aaa = ii.next();
				if (aaa.getName().equals("Value")) {
					if (tableEntrySequence.length() == 0) {
						tableEntrySequence = MibGen.SP15;
					} else {
						tableEntrySequence += "," + System.lineSeparator() + MibGen.SP15;
					}
					tableEntrySequence = tableEntrySequence
							+ a.getType().getTaggedValues().get("mibPrefix") + aa.getName() + " "
							+ aaa.getEaTypeName();
				}
				if (aaa.getName().equals("TimeStamp")) {
					if (tableEntrySequence.length() == 0) {
						tableEntrySequence = MibGen.SP15;
					} else {
						tableEntrySequence += "," + System.lineSeparator() + MibGen.SP15;
					}
					tableEntrySequence = tableEntrySequence
							+ a.getType().getTaggedValues().get("mibPrefix") + aa.getName() + "Ts"
							+ " " + aaa.getEaTypeName();
				}
			}
		}

		_mibWriter.writeTable(tableMibPrefix + a.getName(), a.getEaTypeName(), "read-only",
				"current", a.getQualifiedName().toString(), _objectIdentity, tableIndex,
				tableEntrySequence);
	}
}
