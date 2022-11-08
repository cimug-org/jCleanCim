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

import org.tanjakostic.jcleancim.util.Util;

/**
 * @author Gian Luigi (Gigi) Pugni
 * @author tatjana.kostic@ieee.org
 * @version $Id$
 */
public class MibTemplate {
	private final String _headerTemplate;
	private final String _objectTemplate;
	private final String _tableTemplate;
	private final String _typeTemplate;
	private final String _trapTemplate;
	private final String _objectIdentityTemplate;
	private final String _objectGroupTemplate;
	private final String _moduleComplianceTemplate;

	public MibTemplate() {
		_headerTemplate = initHeader();
		_objectTemplate = initObject();
		_tableTemplate = initTable();
		_typeTemplate = initType();
		_trapTemplate = initTrap();
		_objectIdentityTemplate = initObjectIdentity();
		_objectGroupTemplate = initObjectGroup();
		_moduleComplianceTemplate = initModuleCompliance();
	}

	private String initHeader() {
		StringBuilder sb = new StringBuilder();
		sb.append("$mibname$ DEFINITIONS ::= BEGIN").append(Util.NL);
		sb.append("    IMPORTS").append(Util.NL);
		sb.append("    MODULE-IDENTITY,  OBJECT-IDENTITY,").append(Util.NL);
		sb.append("    OBJECT-TYPE, Counter32, Integer32, Unsigned32, ").append(Util.NL);
		sb.append("    Gauge32, TimeTicks, NOTIFICATION-TYPE ").append(Util.NL);
		sb.append("        FROM SNMPv2-SMI").append(Util.NL);
		sb.append("    TEXTUAL-CONVENTION, DisplayString, TruthValue,").append(Util.NL);
		sb.append("    MacAddress, PhysAddress, DateAndTime").append(Util.NL);
		sb.append("        FROM SNMPv2-TC").append(Util.NL);
		sb.append("    OBJECT-GROUP, NOTIFICATION-GROUP, MODULE-COMPLIANCE").append(Util.NL);
		sb.append("        FROM SNMPv2-CONF").append(Util.NL);
		sb.append("    Float32TC ").append(Util.NL);
		sb.append("		FROM FLOAT-TC-MIB").append(Util.NL);
		sb.append("	$enums$").append(Util.NL);
		sb.append("    InetAddressType, InetAddress").append(Util.NL);
		sb.append("        FROM INET-ADDRESS-MIB;").append(Util.NL);
		sb.append("		").append(Util.NL);
		sb.append("    $modulename$ MODULE-IDENTITY").append(Util.NL);
		sb.append("        LAST-UPDATED        \"201706061000Z\"").append(Util.NL);
		sb.append("        ORGANIZATION        \"IEC\"").append(Util.NL);
		sb.append("        CONTACT-INFO        \"IEC TC57 WG15\"").append(Util.NL);
		sb.append(
				"        DESCRIPTION         \"Copyright (C) IEC. This version of this MIB module is part")
				.append(Util.NL);
		sb.append("                             of IEC 57-62351-7-Ed1. ").append(Util.NL);
		sb.append(
				"                             See the IEC 57-62351-7-Ed1 for full legal notices. ")
				.append(Util.NL);
		sb.append("                             $description$\"").append(Util.NL);
		sb.append(Util.NL);
		sb.append("        REVISION            \"201706061000Z\"").append(Util.NL);
		sb.append("        DESCRIPTION         \"IEC 57-62351-7-Ed1\"").append(Util.NL);
		sb.append(Util.NL);
		sb.append("        ::= { $branch$ $branchid$ }").append(Util.NL);
		sb.append(Util.NL);
		sb.append("	standard  OBJECT IDENTIFIER ::= { iso 0 }").append(Util.NL);
		sb.append("		").append(Util.NL);
		sb.append("$objectidentifiers$	").append(Util.NL);
		sb.append("		").append(Util.NL);
		sb.append(Util.NL);
		sb.append("          ").append(Util.NL);
		sb.append("		").append(Util.NL);
		return sb.toString();
	}

	private String initObject() {
		StringBuilder sb = new StringBuilder();
		sb.append("    $objectname$ OBJECT-TYPE").append(Util.NL);
		sb.append("        SYNTAX              $objecttype$").append(Util.NL);
		sb.append("        MAX-ACCESS          $access$").append(Util.NL);
		sb.append("        STATUS              $status$").append(Util.NL);
		sb.append("        DESCRIPTION         \"$description$\"").append(Util.NL);
		sb.append("        ::= { $branch$ $branchid$ }").append(Util.NL);
		sb.append(Util.NL);
		return sb.toString();
	}

	private String initTable() {
		StringBuilder sb = new StringBuilder();
		sb.append("    $tablename$Table OBJECT-TYPE").append(Util.NL);
		sb.append("        SYNTAX  SEQUENCE OF $tableentrytype$").append(Util.NL);
		sb.append("        MAX-ACCESS  not-accessible").append(Util.NL);
		sb.append("        STATUS  $status$").append(Util.NL);
		sb.append("        DESCRIPTION").append(Util.NL);
		sb.append("                \"$description$\"").append(Util.NL);
		sb.append("        ::= { $branch$ $branchid$ }").append(Util.NL);
		sb.append(Util.NL);
		sb.append("    $tablename$Entry OBJECT-TYPE").append(Util.NL);
		sb.append("        SYNTAX  $tableentrytype$").append(Util.NL);
		sb.append("        MAX-ACCESS  not-accessible").append(Util.NL);
		sb.append("        STATUS  $status$").append(Util.NL);
		sb.append("        DESCRIPTION").append(Util.NL);
		sb.append("                \"$description$\"").append(Util.NL);
		sb.append("        INDEX   { ").append(Util.NL);
		sb.append("$index$ ").append(Util.NL);
		sb.append("	}").append(Util.NL);
		sb.append("        ::= { $tablename$Table 1 }").append(Util.NL);
		sb.append(Util.NL);
		sb.append("    $tableentrytype$ ::=").append(Util.NL);
		sb.append("        SEQUENCE {").append(Util.NL);
		sb.append("$tableentrysequence$").append(Util.NL);
		sb.append("        }").append(Util.NL);
		sb.append(Util.NL);
		return sb.toString();
	}

	private String initType() {
		StringBuilder sb = new StringBuilder();
		sb.append("    $name$ ::= TEXTUAL-CONVENTION").append(Util.NL);
		sb.append("        STATUS              $status$").append(Util.NL);
		sb.append("        DESCRIPTION         \"$description$\"").append(Util.NL);
		sb.append("        SYNTAX  $type$ ").append(Util.NL);
		sb.append("		{   $enumerations$").append(Util.NL);
		sb.append("		}").append(Util.NL);
		return sb.toString();
	}

	private String initTrap() {
		StringBuilder sb = new StringBuilder();
		sb.append(Util.NL);
		sb.append("    $name$Type NOTIFICATION-TYPE").append(Util.NL);
		sb.append("        OBJECTS {           ").append(Util.NL);
		sb.append("                        $enumerations$").append(Util.NL);
		sb.append("                }").append(Util.NL);
		sb.append("        STATUS              $status$").append(Util.NL);
		sb.append("        DESCRIPTION         \"$description$\"").append(Util.NL);
		sb.append("        ::= { $branch$ $branchid$ }").append(Util.NL);
		sb.append(Util.NL);
		sb.append("    $name$ NOTIFICATION-GROUP").append(Util.NL);
		sb.append("        NOTIFICATIONS       {").append(Util.NL);
		sb.append("                                $name$Type").append(Util.NL);
		sb.append("                            }").append(Util.NL);
		sb.append("        STATUS              $status$").append(Util.NL);
		sb.append("        DESCRIPTION         \"$description$\"").append(Util.NL);
		sb.append("        ::= { $branch$ $branchplus$ }").append(Util.NL);
		sb.append(Util.NL);
		return sb.toString();
	}

	private String initObjectIdentity() {
		StringBuilder sb = new StringBuilder();
		sb.append("    $objectname$ OBJECT-IDENTITY").append(Util.NL);
		sb.append("        STATUS              $status$").append(Util.NL);
		sb.append("        DESCRIPTION         \"$description$\"").append(Util.NL);
		sb.append("        ::= { $branch$ $branchid$ }").append(Util.NL);
		sb.append(Util.NL);
		return sb.toString();
	}

	private String initObjectGroup() {
		StringBuilder sb = new StringBuilder();
		sb.append("    $name$ OBJECT-GROUP ").append(Util.NL);
		sb.append("	OBJECTS {").append(Util.NL);
		sb.append("	$enumerations$").append(Util.NL);
		sb.append("	}").append(Util.NL);
		sb.append("        STATUS              $status$").append(Util.NL);
		sb.append("    	DESCRIPTION         \"$description$\"").append(Util.NL);
		sb.append("        ::= { $branch$ $branchid$ }").append(Util.NL);
		sb.append(Util.NL);
		return sb.toString();
	}

	private String initModuleCompliance() {
		StringBuilder sb = new StringBuilder();
		sb.append("    $name$ MODULE-COMPLIANCE	").append(Util.NL);
		sb.append("        STATUS              $status$").append(Util.NL);
		sb.append("        DESCRIPTION         \"$description$\"").append(Util.NL);
		sb.append("	MODULE").append(Util.NL);
		sb.append("		$enumerations$").append(Util.NL);
		sb.append("        ::= { $branch$ $branchid$ }").append(Util.NL);
		sb.append(Util.NL);
		return sb.toString();
	}

	public String getHeader() {
		return _headerTemplate;
	}

	public String getObject() {
		return _objectTemplate;
	}

	public String getTable() {
		return _tableTemplate;
	}

	public String getType() {
		return _typeTemplate;
	}

	public String getTrap() {
		return _trapTemplate;
	}

	public String getObjectIdentity() {
		return _objectIdentityTemplate;
	}

	public String getObjectGroup() {
		return _objectGroupTemplate;
	}

	public String getModuleCompliance() {
		return _moduleComplianceTemplate;
	}
}
