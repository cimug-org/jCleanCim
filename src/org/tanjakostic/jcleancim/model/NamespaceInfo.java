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

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;

import org.apache.log4j.Logger;
import org.tanjakostic.jcleancim.common.Nature;
import org.tanjakostic.jcleancim.util.Util;

/**
 * Content of namespace class. In case of CIM, the information is deduced from version class, while
 * for IEC61850, the information is extracted from the namespace class.
 * <p>
 * Note that the constructor initialises only the "field", but not the list of dependencies; use
 * {@link #addDependency(NamespaceInfo)} to gradually add needed instances as you visit them.
 *
 * @author tatjana.kostic@ieee.org
 * @version $Id: NamespaceInfo.java 31 2019-12-08 01:19:54Z dev978 $
 */
public class NamespaceInfo {
	/**  */
	private static final String NS_FORMAT = "%s:%s%s";

	private static final Logger _logger = Logger.getLogger(NamespaceInfo.class.getName());

	private final String _id;
	private final String _version;
	private final String _revision;
	private final String _date;
	private final String _umlVersion;
	private final String _tissuesApplied;
	private final Set<NamespaceInfo> _dependencies = new LinkedHashSet<NamespaceInfo>();

	/** Returns the expected name for the namespace class, as per IEC TC57 UML models rules. */
	public static String getExpectedNamespaceClassName(Nature nature, String name) {
		String suffix = (nature == Nature.CIM) ? UML.CIM_VERSION_CLASS_SUFFIX
				: UML.IEC61850_NAMESPACE_CLASS_SUFFIX;
		return name + suffix;
	}

	/**
	 * Factory method to construct CIM namespace info from <code>versionInfo</code>; see
	 * {@link #NamespaceInfo(String, String, String)}.
	 */
	public static NamespaceInfo createCimInstance(VersionInfo versionInfo) {
		String id = versionInfo.getVersion();
		String date = versionInfo.getDate();
		String versionYear = (date.length() >= 4) ? date.substring(0, 4) : ((date.isEmpty()) ? "?"
				: date);
		return new NamespaceInfo(id, versionYear, "", versionInfo.getDate(), versionYear, "");
	}

	public static NamespaceInfo createIec61850Instance(UmlClass nsClass) {
		Util.ensureNotNull(nsClass, "nsClass");
		String umlVersion = deduceUmlVersionForNamespace(nsClass);
		if (umlVersion.isEmpty()) {
			_logger.warn("Could not deduce UML version for 61850 namespace class "
					+ nsClass.getName() + " - keeping empty attribute.");
		}

		String id = "";
		String version = "";
		String revision = "";
		String date = "";
		String tissuesApplied = "";
		for (UmlAttribute att : nsClass.getAttributes()) {
			String attName = att.getName();

			if (UML.ATTR_id.equals(attName) && id.isEmpty()) {
				id = att.getInitValue();
			} else if (UML.ATTR_version.equals(attName) && version.isEmpty()) {
				version = att.getInitValue();
			} else if (UML.ATTR_revision.equals(attName) && revision.isEmpty()) {
				revision = att.getInitValue();
			} else if (UML.ATTR_date.equals(attName) && date.isEmpty()) {
				date = att.getInitValue();
			} else if (UML.ATTR_tissuesApplied.equals(attName) && tissuesApplied.isEmpty()) {
				tissuesApplied = att.getInitValue();
			} else {
				_logger.error("Unexpected attribute '" + att.getName() + "' in namespace class '"
						+ nsClass.getName() + "'.");
			}
		}
		return new NamespaceInfo(id, version, revision, date, umlVersion, tissuesApplied);
	}

	/** (IEC61850 namespaces only) Returns empty string in bad formed models, to avoid NPE. */
	private static String deduceUmlVersionForNamespace(UmlClass nsClass) {
		// both Namespace and UML version classes in the same package:
		UmlPackage pck = nsClass.getContainingPackage();
		VersionInfo versionInfo = pck.getVersionInfo();
		if (versionInfo == null) {
			// Namespace is in this package, its parent has UML version:
			UmlPackage parentPck = pck.getContainingPackage();
			versionInfo = parentPck.getVersionInfo();
		}
		// all other combinations are mal-formed model:
		return (versionInfo != null) ? versionInfo.getVersion() : "";
	}

	// -------------------- instance methods ----------------------
	/**
	 * Constructs instance with empty revision and tissues, and with UML version same as namespace
	 * version; this is for CIM namespaces, which are deduced from UML version class. After
	 * construction, you still need to add dependencies with {@link #addDependency(NamespaceInfo)}.
	 *
	 * @param id
	 * @param version
	 * @param date
	 */
	public NamespaceInfo(String id, String version, String date) {
		this(id, version, "", date, version, "");
	}

	/**
	 * Constructor for IEC61850 namespaces; after construction, you still need to add dependencies
	 * with {@link #addDependency(NamespaceInfo)}.
	 *
	 * @param id
	 * @param version
	 * @param revision
	 * @param date
	 * @param umlVersion
	 * @param tissuesApplied
	 */
	public NamespaceInfo(String id, String version, String revision, String date,
			String umlVersion, String tissuesApplied) {
		_id = id;
		_version = version;
		_revision = revision;
		_date = date;
		_umlVersion = umlVersion;
		_tissuesApplied = tissuesApplied;
	}

	public String getId() {
		return _id;
	}

	public String getVersion() {
		return _version;
	}

	public String getRevision() {
		return _revision;
	}

	public String getDate() {
		return _date;
	}

	public String getUmlVersion() {
		return _umlVersion;
	}

	public String getTissuesApplied() {
		return _tissuesApplied;
	}

	/** Returns all dependencies of this namespace. */
	public Set<NamespaceInfo> getDependencies() {
		return Collections.unmodifiableSet(_dependencies);
	}

	/**
	 * Adds <code>namespace</code> as dependency to this namespace, if the dependency is not cirular
	 * and returns whether addition happened.
	 */
	public boolean addDependency(NamespaceInfo namespace) {
		Util.ensureNotNull(namespace, "namespace");
		if (namespace == this) {
			_logger.warn(String.format("Cannot add self (%s) as dependency !", namespace.getName()));
			return false;
		}
		if (namespace.getDependencies().contains(this)) {
			_logger.warn(String.format("Cannot add namespace (%s) with circular"
					+ " dependency to me (%s) !", namespace.getName(), getName()));
			return false;
		}
		if (getDependencies().contains(namespace)) {
			_logger.error(String.format(
					"Namespace (%s) already added to me - this is abnormal condition"
							+ "and you should fix the logic in the code !", namespace.getName()));
			return false;
		}
		return _dependencies.add(namespace);
	}

	/** Returns formatted string including id, version and revision. */
	public String getName() {
		return String.format(NamespaceInfo.NS_FORMAT, getId(), getVersion(), getRevision());
	}

	/** Returns namespace strings of all the dependencies. */
	public Set<String> getDependencyStrings() {
		Set<String> result = new LinkedHashSet<String>();
		for (NamespaceInfo ns : getDependencies()) {
			result.add(ns.getName());
		}
		return result;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("namespace ").append(getName()).append(", ").append(getDate());
		if (!getUmlVersion().isEmpty()) {
			sb.append(";  UML version ").append(getUmlVersion());
		}
		if (!getTissuesApplied().isEmpty()) {
			sb.append(";  tissues applied: ").append(getTissuesApplied());
		}
		if (!getDependencies().isEmpty()) {
			sb.append(";  requires: ").append(
					Util.concatStringSeparatedTokens(", ", true, new ArrayList<String>(
							getDependencyStrings())));
		}
		return sb.toString();
	}
}
