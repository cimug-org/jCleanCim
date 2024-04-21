/**
 * Copyright (C) 2009-2019 Tatjana (Tanja) Kostic
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

package org.tanjakostic.jcleancim.model;

import org.tanjakostic.jcleancim.util.Util;

/**
 * Helper class, used to hold the two association ends of an association, from the perspective of a
 * UmlClass used as type for those ends. Useful for doc generation, as it gives "this" and "other"
 * end of an association so a class can use "other" end to list its roles (with the other end class)
 * in a similar way it lists its own attributes.
 * <p>
 * Consider association between classes A and B. Their (qualified) association ends names are
 * (A.bRole, B.aRole) from the perspective of A, and (B.aRole, A.bRole) from the perspective of B.
 * <p>
 * Here example of a couple of inherited association ends for CIM ConductingEquipment:
 *
 * <pre>
 * myEnd: [0..*] EquipmentContainer.Equipments, otherEnd: [0..1] Equipment.EquipmentContainer
 * myEnd: [0..*] PSRType.PowerSystemResources, otherEnd: [0..1] PowerSystemResource.PSRType
 * myEnd: [0..1] Measurement.PowerSystemResource, otherEnd: [0..*]
 * PowerSystemResource.Measurements
 * </pre>
 *
 * @author tatjana.kostic@ieee.org
 * @version $Id: UmlAssociationEnd.java 23 2019-08-25 21:04:58Z dev978 $
 */
public class UmlAssociationEndPair {
	private final UmlAssociationEnd _myEnd;
	private final UmlAssociationEnd _otherEnd;

	UmlAssociationEndPair(UmlAssociationEnd myEnd, UmlAssociationEnd otherEnd) {
		Util.ensureNotNull(myEnd, "myEnd");
		Util.ensureNotNull(otherEnd, "otherEnd");

		_myEnd = myEnd;
		_otherEnd = otherEnd;
	}

	public UmlAssociationEnd getMyEnd() {
		return _myEnd;
	}

	public UmlAssociationEnd getOtherEnd() {
		return _otherEnd;
	}

	/**
	 * {@inheritDoc} Example for inherited association ends of ConductingEquipment:
	 *
	 * <pre>
	 * myEnd: [0..*] EquipmentContainer.Equipments, otherEnd: [0..1] Equipment.EquipmentContainer
	 * myEnd: [1..1] OperationalLimitSet.Equipment, otherEnd: [0..*] Equipment.OperationalLimitSet
	 * myEnd: [1..1] ContingencyEquipment.Equipment, otherEnd: [0..*] Equipment.ContingencyEquipment
	 * myEnd: [0..*] PSRType.PowerSystemResources, otherEnd: [0..1] PowerSystemResource.PSRType
	 * myEnd: [0..1] Measurement.PowerSystemResource, otherEnd: [0..*] PowerSystemResource.Measurements
	 * myEnd: [1..1] OperatingShare.PowerSystemResource, otherEnd: [0..*] PowerSystemResource.OperatingShare
	 * myEnd: [0..*] PsrList.PowerSystemResources, otherEnd: [0..*] PowerSystemResource.PsrLists
	 * myEnd: [1..1] OutageSchedule.PowerSystemResource, otherEnd: [0..1] PowerSystemResource.OutageSchedule
	 * myEnd: [0..*] ReportingGroup.PowerSystemResource, otherEnd: [0..*] PowerSystemResource.ReportingGroup
	 * myEnd: [1..*] ModelingAuthoritySet.IdentifiedObjects, otherEnd: [0..1] IdentifiedObject.ModelingAuthoritySet
	 * 	/*
	 * </pre>
	 *
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "myEnd: " + getMyEnd() + "; otherEnd: " + getOtherEnd();
	}
}
