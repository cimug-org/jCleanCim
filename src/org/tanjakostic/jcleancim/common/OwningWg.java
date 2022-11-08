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

package org.tanjakostic.jcleancim.common;

import java.util.Collection;
import java.util.EnumSet;
import java.util.LinkedHashSet;

import org.apache.log4j.Logger;

/**
 * IEC TC57 working group owning the top level package in the combined CIM/IEC61850 model.
 * <p>
 * We assume that the current UML model has three standard model packages (one that contains
 * canonical CIM top-level packages, one with CIM profiles, and one that contains IEC61850 top-level
 * packages) and any number of custom model packages (both CIM and non-CIM extensions).
 * <p>
 * TODO: We may want to replace this enum with a class to allow for more flexibility for
 * non-standard packages that could be specified in the
 * {@value org.tanjakostic.jcleancim.common.Config#DEFAULT_PROPS_FILE_NAME} file.
 * <p>
 * Implementation note: We keep this class in the common package instead of model package because
 * {@link Config} depends on it.
 *
 * @author tatjana.kostic@ieee.org
 * @version $Id: OwningWg.java 26 2019-11-12 18:50:35Z dev978 $
 */
public enum OwningWg {

	WG13("IEC61970", Nature.CIM, "Base CIM"),

	WG14("IEC61968", Nature.CIM, "DCIM"),

	WG16("IEC62325", Nature.CIM, "CME"),

	OTHER_CIM("", Nature.CIM, "CIM extensions"),

	WG10("WG10", Nature.IEC61850, "Substation automation"),

	WG17("WG17", Nature.IEC61850, "DER"),

	WG18("WG18", Nature.IEC61850, "Hydro power plants"),

	JWG25("JWG25", Nature.IEC61850, "Wind turbines"),

	WG19("Mappings", Nature.IEC61850, "Mappings CIM-IEC61850"),

	OTHER_IEC61850("", Nature.IEC61850, "IEC61850 extensions");

	private static final Logger _logger = Logger.getLogger(OwningWg.class.getName());

	private OwningWg(String topPackageName, Nature nature, String appDomain) {
		_topPackageName = topPackageName;
		_nature = nature;
		_appDomain = appDomain;
	}

	private final String _topPackageName;
	private final Nature _nature;
	private final String _appDomain;

	public String getTopPackageName() {
		return _topPackageName;
	}

	public Nature getNature() {
		return _nature;
	}

	public String getAppDomain() {
		return _appDomain;
	}

	/** Returns reserved (standard) top package names. */
	public static Collection<String> getReservedTopPackageNames() {
		Collection<String> result = new LinkedHashSet<String>();
		for (OwningWg owner : values()) {
			if (!owner.getTopPackageName().isEmpty()) {
				result.add(owner.getTopPackageName());
			}
		}
		return result;
	}

	/**
	 * Utility method: returns the owner for the given name of top package, null if there is no such
	 * a name assigned to an owner. This latter is the case of extensions.
	 */
	public static OwningWg getOwnerForTopPackage(String topPackageName) {
		if (topPackageName.equals(OwningWg.WG13.getTopPackageName())) {
			return OwningWg.WG13;
		} else if (topPackageName.equals(OwningWg.WG14.getTopPackageName())) {
			return OwningWg.WG14;
		} else if (topPackageName.equals(OwningWg.WG16.getTopPackageName())) {
			return OwningWg.WG16;
		} else if (topPackageName.equals(OwningWg.WG10.getTopPackageName())) {
			return OwningWg.WG10;
		} else if (topPackageName.equals(OwningWg.WG17.getTopPackageName())) {
			return OwningWg.WG17;
		} else if (topPackageName.equals(OwningWg.WG18.getTopPackageName())) {
			return OwningWg.WG18;
		} else if (topPackageName.equals(OwningWg.JWG25.getTopPackageName())) {
			return OwningWg.JWG25;
		} else if (topPackageName.equals(OwningWg.WG19.getTopPackageName())) {
			return OwningWg.WG19;
		} else {
			return null;
		}
	}

	/**
	 * Returns the allowed dependencies of this owner, as per IEC TC57 top-level package
	 * dependencies rules.
	 */
	public EnumSet<OwningWg> getAllowedOtherEndOwners() {
		switch (this) {
			case WG13:
				return EnumSet.of(WG13);
			case WG14:
				return EnumSet.of(WG13, WG14);
			case WG16:
				return EnumSet.of(WG13, WG14, WG16);
			case OTHER_CIM: {
				return EnumSet.of(WG13, WG14, WG16, OTHER_CIM);
			}

			case WG10:
				return EnumSet.of(WG10);
			case WG17:
				return EnumSet.of(WG10, WG17);
			case WG18:
				return EnumSet.of(WG10, WG18);
			case JWG25:
				return EnumSet.of(WG10, JWG25);
			case WG19:
				return EnumSet.of(WG10, WG17, WG18, JWG25, WG19, WG13, WG14);
			case OTHER_IEC61850: {
				return EnumSet.of(WG10, WG17, WG18, JWG25, WG19, OTHER_IEC61850);
			}
			default:
				return EnumSet.allOf(OwningWg.class);
		}
	}

	/**
	 * Returns the owner of an association if both ends have been initialised, null otherwise.
	 * <p>
	 * UML generalisation (inheritance) and UML dependency (hand-drawn in the model, among elements)
	 * are relationships that have natural dependency direction, i.e., explicitly from source end to
	 * target end. However, for associations in CIM, we unfortunately cannot rely on natural
	 * dependencies, because associations are agreed to be bi-directional (we always have
	 * association end names on both sides of an association). So, in this method, it does not
	 * matter what is source and what is target - <code>oneEndOwner</code> and
	 * <code>otherEndOwner</code>. Owner is assigned for the fact that there is an association
	 * between two classes, potentially from different top-level packages (i.e., with potentially
	 * different owner).
	 * <p>
	 * The owner returned is in the reverse direction of IEC TC57 agreed dependencies. For instance,
	 * for an association involving classes between {@link #WG13} and {@link #WG14}, owner is
	 * {@link #WG14}, because the class of {@link #WG14} depends on the class of {@link #WG13}, and
	 * this latter need not know about who links to it. Therefore, we calculate the actual owner
	 * according to the IEC TC57 top-level package dependencies reverse order, as follows:
	 * <p>
	 * <code>
	 * {@link #WG13} -> {@link #WG14}<p>
	 * {@link #WG13} -> {@link #WG14} -> {@link #WG16}<p>
	 * {@link #WG13} -> {@link #WG14} -> {@link #WG16} -> {@link #OTHER_CIM}<p>
	 * {@link #WG10} -> {@link #WG17}<p>
	 * {@link #WG10} -> {@link #WG18}<p>
	 * {@link #WG10} -> {@link #JWG25}<p>
	 * [{@link #WG13} -> {@link #WG14} | {@link #WG10} -> {@link #WG17} | {@link #WG10} -> {@link #WG18} | {@link #WG10} -> {@link #JWG25}] -> {@link #WG19}<p>
	 * any -> {@link #OTHER_IEC61850}.
	 * </code>
	 * <p>
	 * Model validators have the job of detecting inconsistencies in dependencies by using
	 * {@link #getAllowedOtherEndOwners()}.
	 *
	 * @param oneEndOwner
	 *            owner of one end of the association.
	 * @param otherEndOwner
	 *            owner of the other end of the association.
	 * @return calculated owner of the association.
	 */
	public static OwningWg determineAssociationOwner(OwningWg oneEndOwner, OwningWg otherEndOwner) {
		if (oneEndOwner == null || otherEndOwner == null) {
			_logger.debug("getOwner() called before both ends initialised - returning null.");
			return null;
		}

		// base CIM, IEC61850, and any associations within a top-level package:
		if (oneEndOwner == otherEndOwner) {
			return oneEndOwner;
		}
		// DCIM:
		if (WG14.involvedIn(oneEndOwner, otherEndOwner)
				&& WG13.involvedIn(oneEndOwner, otherEndOwner)) {
			return WG14;
		}
		// CME:
		if (WG16.involvedIn(oneEndOwner, otherEndOwner)
				&& (WG13.involvedIn(oneEndOwner, otherEndOwner)
						|| WG14.involvedIn(oneEndOwner, otherEndOwner))) {
			return WG16;
		}
		// CIM ext:
		if (OTHER_CIM.involvedIn(oneEndOwner, otherEndOwner)
				&& (WG13.involvedIn(oneEndOwner, otherEndOwner)
						|| WG14.involvedIn(oneEndOwner, otherEndOwner))
				|| WG16.involvedIn(oneEndOwner, otherEndOwner)) {
			return OTHER_CIM;
		}

		// DER:
		if (WG17.involvedIn(oneEndOwner, otherEndOwner)
				&& WG10.involvedIn(oneEndOwner, otherEndOwner)) {
			return WG17;
		}
		// Hydro:
		if (WG18.involvedIn(oneEndOwner, otherEndOwner)
				&& WG10.involvedIn(oneEndOwner, otherEndOwner)) {
			return WG18;
		}
		// Wind:
		if (JWG25.involvedIn(oneEndOwner, otherEndOwner)
				&& WG10.involvedIn(oneEndOwner, otherEndOwner)) {
			return JWG25;
		}
		// harmonisation - mappings
		if (WG19.involvedIn(oneEndOwner, otherEndOwner)
				&& (WG13.involvedIn(oneEndOwner, otherEndOwner)
						|| WG14.involvedIn(oneEndOwner, otherEndOwner)
						|| WG10.involvedIn(oneEndOwner, otherEndOwner)
						|| WG17.involvedIn(oneEndOwner, otherEndOwner)
						|| WG18.involvedIn(oneEndOwner, otherEndOwner)
						|| JWG25.involvedIn(oneEndOwner, otherEndOwner))) {
			return WG19;
		}
		// IEC61850 ext:
		return OTHER_IEC61850;
	}

	/**
	 * Returns whether one of the arguments has this owner.
	 */
	public boolean involvedIn(OwningWg oneEnd, OwningWg otherEnd) {
		return this == oneEnd || this == otherEnd;
	}
}
