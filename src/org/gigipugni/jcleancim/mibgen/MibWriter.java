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
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Scanner;

import org.tanjakostic.jcleancim.common.Config;
import org.tanjakostic.jcleancim.model.UmlAttribute;
import org.tanjakostic.jcleancim.model.UmlClass;
import org.tanjakostic.jcleancim.model.UmlPackage;
import org.tanjakostic.jcleancim.util.Util;

/**
 * @author Gian Luigi (Gigi) Pugni
 * @author tatjana.kostic@ieee.org
 * @version $Id$
 */
public class MibWriter {

	private static final String SP15 = "               ";

	private String _outPath;
	private FileWriter _outFile;
	private PrintWriter _outPrint;

	private final MibTemplate _mibTemplate;
	private final String _HeaderTemplate;
	private final String _ObjectTemplate;
	private final String _ObjectIdentityTemplate;
	private final String _TableTemplate;
	private final String _TypeTemplate;
	private final String _TrapTemplate;
	private final String _ModuleComplianceTemplate;
	private final String _ObjectGroupTemplate;

	private int _branchIdCounter;
	private final List<String> _mandatoryObjects;
	private final List<String> _optionalObjects;
	private final List<String> _rootPackages;
	private final boolean _liteMib;

	private String _mibName;
	private String _mibIdentity;
	private final String _objectIdentifiers;

	public MibWriter(Config cfg, boolean lite, MibTemplate mibTemplate) {
		_liteMib = lite;
		if (lite) {
			_outPath = cfg.getMibgenOutDirLightAbsPath();
		} else {
			_outPath = cfg.getMibgenOutDirFullAbsPath();
		}

		_mibTemplate = mibTemplate;

		_HeaderTemplate = _mibTemplate.getHeader();

		_TypeTemplate = _mibTemplate.getType();
		_TrapTemplate = _mibTemplate.getTrap();
		_TableTemplate = _mibTemplate.getTable();
		_ObjectTemplate = _mibTemplate.getObject();
		_ObjectIdentityTemplate = _mibTemplate.getObjectIdentity();
		_ModuleComplianceTemplate = _mibTemplate.getModuleCompliance();
		_ObjectGroupTemplate = _mibTemplate.getObjectGroup();
		_branchIdCounter = 1;
		_objectIdentifiers = "";

		_mandatoryObjects = new ArrayList<>();
		_optionalObjects = new ArrayList<>();

		_rootPackages = new ArrayList<>();
	}

	public void writeTrap(String name, String status, String description, String branch,
			String enumerations) {
		String object = _TrapTemplate;

		object = replaceName(object, name);
		object = replaceBranch(object, branch);
		String branchid = Integer.toString(_branchIdCounter++);
		object = replaceBranchid(object, branchid);
		String branchplus = Integer.toString(_branchIdCounter++);
		object = object.replace("$branchplus$", branchplus);
		object = replaceStatus(object, status);
		object = replaceDescription(object, description);
		object = replaceEnumerations(object, enumerations);

		_outPrint.print(object);
	}

	public void writeModuleCompliance(String name, String status, String description, String branch,
			String branchid, String groups) {
		String object = _ModuleComplianceTemplate;

		object = replaceName(object, name);
		object = replaceBranch(object, branch);
		object = replaceBranchid(object, branchid);
		object = replaceStatus(object, status);
		object = replaceDescription(object, description);
		object = replaceEnumerations(object, groups);

		_outPrint.print(object);
	}

	public void writeObjectGroup(String name, String status, String description, String branch,
			String branchid, List<String> objectList) {
		String object = _ObjectGroupTemplate;

		object = replaceName(object, name);
		object = replaceBranch(object, branch);
		object = replaceBranchid(object, branchid);
		object = replaceStatus(object, status);
		object = replaceDescription(object, description);

		String enumerations = "";
		for (Iterator<String> i = objectList.iterator(); i.hasNext();) {
			String item = i.next();
			enumerations += MibWriter.SP15 + item;
			if (i.hasNext()) {
				enumerations += "," + System.lineSeparator();
			}
		}

		object = object.replace("$enumerations$", enumerations);

		_outPrint.print(object);
	}

	public void writeType(String name, String type, String status, String description,
			String enumerations) {
		String object = _TypeTemplate;

		object = replaceName(object, name);
		object = replaceType(object, type);
		object = replaceStatus(object, status);
		object = replaceDescription(object, description);
		object = replaceEnumerations(object, enumerations);

		_outPrint.print(object);
	}

	private String replaceName(String object, String name) {
		if (name != null) {
			return object.replace("$name$", name);
		}
		return object;
	}

	private String replaceType(String object, String type) {
		if (type != null) {
			return object.replace("$type$", type);
		}
		return object;
	}

	private String replaceStatus(String object, String status) {
		if (status != null) {
			return object.replace("$status$", status);
		}
		return object;
	}

	private String replaceDescription(String object, String description) {
		if (description != null) {
			return object.replace("$description$", splitToMultipleLines(description, 29));
		}
		return object;
	}

	private String replaceEnumerations(String object, String enumerations) {
		if (enumerations != null) {
			return object.replace("$enumerations$", enumerations);
		}
		return object;
	}

	private String replaceObjectName(String object, String name) {
		if (name != null) {
			return object.replace("$objectname$", name);
		}
		return object;
	}

	public void writeTable(String name, String type, String access, String status,
			String description, String branch, String index, String tableEntrySequence) {
		String object = _TableTemplate;

		if (name != null) {
			object = object.replace("$tablename$", name);
		}
		if (type != null) {
			object = object.replace("$tableentrytype$", type);
		}
		if (access != null) {
			object = object.replace("$access$", access);
		}
		object = replaceStatus(object, status);
		object = replaceDescription(object, description);
		object = replaceBranch(object, branch);
		String branchid = Integer.toString(_branchIdCounter++);
		object = replaceBranchid(object, branchid);
		if (index != null) {
			object = object.replace("$index$", index);
		}
		if (tableEntrySequence != null) {
			object = object.replace("$tableentrysequence$", tableEntrySequence);
		}

		_outPrint.print(object);
	}

	private String replaceBranch(String object, String branch) {
		if (branch != null) {
			return object.replace("$branch$", branch);
		}
		return object;
	}

	public void writeObject(String mibprefix, String branch, UmlAttribute a) {
		for (UmlAttribute aa : a.getType().getAttributes()) {
			String object = _ObjectTemplate;

			String name = "";
			String type = aa.getEaTypeName();
			String description = "";
			String status = "current";
			String access = (a.isConst()) ? "read-only" : "read-write";

			if (a.getStereotype() != null) {
				if (a.getStereotype().value().equals("index")) {
					access = "not-accessible";
				}
			}

			if (!_liteMib) {
				description = a.getDescription().toString();
			} else {
				description = a.getQualifiedName().toString();
			}

			if (aa.getName().equals("Value")) {
				name = mibprefix + a.getName();
			}
			if (aa.getName().equals("TimeStamp")) {
				name = mibprefix + a.getName() + "Ts";
				description += " - timestamp";
				access = "read-only";
			}

			object = replaceObjectName(object, name);
			if (type != null) {
				object = object.replace("$objecttype$", type);
			}
			if (access != null) {
				object = object.replace("$access$", access);
			}
			object = replaceStatus(object, status);
			object = replaceDescription(object, description);
			object = replaceBranch(object, branch);
			String branchid = Integer.toString(_branchIdCounter++);
			object = replaceBranchid(object, branchid);

			String x = a.getMultiplicity().getLower();

			if (!access.equals("not-accessible") && !(a.getMultiplicity().getLower().equals("0"))) {
				_mandatoryObjects.add(name);
			}
			if (!access.equals("not-accessible") && (a.getMultiplicity().getLower().equals("0"))) {
				_optionalObjects.add(name);
			}

			if (_outPrint != null) {
				_outPrint.print(object);
			}
		}
	}

	// FIXME: initialising _outPrint here
	// FIXME: do null test within replace() method
	public void writeModuleHeader(String mibName, String moduleName, String description,
			String branch, String branchid, String enums) {
		String object = _HeaderTemplate;
		_mibName = mibName;
		_mibIdentity = moduleName;
		String newMibName = _outPath + "\\" + mibName + ".mib";
		try {
			_outFile = new FileWriter(newMibName);
			_outPrint = new PrintWriter(_outFile);
		} catch (IOException e) {
			System.out.print("error on MIB file open");
		}

		// Write(_HeaderTemplate);

		if (mibName != null) {
			object = object.replace("$mibname$", mibName);
		}
		if (moduleName != null) {
			object = object.replace("$modulename$", moduleName);
		}
		object = replaceDescription(object, description);
		object = replaceBranch(object, branch);
		object = replaceBranchid(object, branchid);
		if (enums != null) {
			object = object.replace("$enums$", splitToMultipleLines(enums, 4));
		}

		String listString = "";
		Iterator element = _rootPackages.iterator();
		while (element.hasNext()) {
			listString += "\t" + element.next();
		}

		object = object.replace("$objectidentifiers$", listString);

		_outPrint.print(object);
	}

	public void writeClassObjectIdentity(String name, UmlClass c) {
		String object = _ObjectIdentityTemplate;

		String description = c.getDescription().toString();
		String branch = c.getContainingPackage().getTaggedValues().get("objectIdentity");
		String branchid = c.getTaggedValues().get("objectBranchId");
		String status = "current";

		object = replaceObjectName(object, name);
		object = replaceStatus(object, status);
		object = replaceDescription(object, description);
		object = replaceBranch(object, branch);
		object = replaceBranchid(object, branchid);

		if (_outPrint != null) {
			_outPrint.print(object);
		}
	}

	public void writePackageObjectIdentity(String name, UmlPackage p, String branchid) {
		String object = _ObjectIdentityTemplate;
		String description = "";

		if (!_liteMib) {
			description = p.getQualifiedName().toString();
		} else {
			description = p.getDescription().toString();
		}

		String branch = p.getContainingPackage().getTaggedValues().get("objectIdentity");
		String status = "current";

		object = replaceObjectName(object, name);
		object = replaceStatus(object, status);
		object = replaceDescription(object, description);
		object = replaceBranch(object, branch);
		object = replaceBranchid(object, branchid);

		if (_outPrint != null) {
			_outPrint.print(object);
		}
	}

	public void resetRootPackages() {
		_rootPackages.clear();
	}

	public void writeObjectIdentifier(String name, String branch, String branchid) {
		String st = "$name$  OBJECT IDENTIFIER ::= { $branch$ $branchid$ } "
				+ System.lineSeparator() + System.lineSeparator();
		st = replaceName(st, name);
		st = replaceBranch(st, branch);
		st = replaceBranchid(st, branchid);

		if (branch != null) {
			_rootPackages.add(st);
		}
	}

	private String replaceBranchid(String object, String branchid) {
		if (branchid != null) {
			return object.replace("$branchid$", branchid);
		}
		return object;
	}

	public void resetBranchId() {
		_branchIdCounter = 1;
	}

	private String splitToMultipleLines(String str, int n) {
		String s = str.replace("\"", " "); // get rid of " within the description (it does not work
											// into a MIB)

		String strings = "";
		boolean firstCycle = true;
		int index = 0;
		String outstrings = "";
		Scanner tokenize = new Scanner(s);
		while (tokenize.hasNext()) {
			String nextString = tokenize.next();
			index += nextString.length();
			if (index > 40) {
				outstrings = outstrings + strings + Util.NL + Util.fillString(n, ' ');
				index = 0;
				strings = "";
			} else if ((index != 0) && !firstCycle) {
				strings = strings + " ";
			}
			strings = strings + nextString;
			firstCycle = false;
		}

		if (strings.length() > 0) {
			outstrings = outstrings + strings;
		}
		return outstrings;
	}

	public void closeMib(int branchId) {

		String groupComplianceList = "";

		branchId++;

		if (!_mandatoryObjects.isEmpty()) {
			writeObjectGroup(_mibIdentity + "Group", "current",
					_mibIdentity + " mandatory objects group", _mibIdentity,
					Integer.toString(branchId++), _mandatoryObjects);
			_mandatoryObjects.clear();
			groupComplianceList = "MANDATORY-GROUPS" + Util.NL + "{" + _mibIdentity + "Group" + "}"
					+ Util.NL;
		}

		if (!_optionalObjects.isEmpty()) {
			writeObjectGroup(_mibIdentity + "GroupOptional", "current",
					_mibIdentity + " optional objects group", _mibIdentity,
					Integer.toString(branchId++), _optionalObjects);
			_optionalObjects.clear();
		}

		writeModuleCompliance(_mibIdentity + "Compliance", "current", _mibIdentity + " Compliance",
				_mibIdentity, Integer.toString(branchId), groupComplianceList);

		// close MIB file ... very important!
		_outPrint.print(System.lineSeparator() + "END" + System.lineSeparator());
		_outPrint.close();

		_mibIdentity = null;
		_mibName = null;
	}

	public void closeMibFile() {
		_outPrint.close();
	}

	public String getCurrentMibIdentity() {
		return _mibIdentity;
	}
}
