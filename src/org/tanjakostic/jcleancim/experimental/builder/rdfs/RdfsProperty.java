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

package org.tanjakostic.jcleancim.experimental.builder.rdfs;

import java.util.List;

import org.apache.log4j.Logger;
import org.w3c.dom.Element;

/**
 * CIM RDF Schema element representing the UML attribute or association end (role).
 *
 * @author tatjana.kostic@ieee.org
 * @version $Id: RdfsProperty.java 21 2019-08-12 15:44:50Z dev978 $
 */
public final class RdfsProperty extends RdfsElem {
	private static final Logger _logger = Logger.getLogger(RdfsProperty.class.getName());

	/**
	 * We need this one with OWL, since we cannot distinguish between dataType and range (OWL always
	 * uses range plus some other tags).
	 *
	 * @author tatjana.kostic@ieee.org
	 * @version $Id: RdfsProperty.java 21 2019-08-12 15:44:50Z dev978 $
	 */
	public enum Kind {
		simpleAttr, datatypeAttr, enumAttr, compoundAttr, assocEnd, unknown
	}

	private final String _domain;
	private String _dataType;
	private final String _range;
	private final String _invRoleName;
	private final String _multiplicity;

	/**
	 * Legacy RDF does not allow us to distinguish with certainty the kind of property at
	 * construction, except for association ends. Therefore, we can rely on kind field for OWL
	 * format only.
	 */
	private final Kind _kind;

	// --------------------- creation --------------------------

	/**
	 * Creates instance from DOM Element. Applies following fixes:
	 * <ul>
	 * <li>replaces empty _dataType with String</li>
	 * <li>replaces type for *Version.date with String</li>
	 * <li>fixes empty/wrong multiplicities.</li>
	 * </ul>
	 *
	 * @param model
	 * @param elem
	 * @throws CimSchemaException
	 *             if about attribute contains an invalid URI.
	 */
	RdfsProperty(RdfsModel model, Element elem) throws CimSchemaException {
		super(model, elem);
		_domain = XmlChildElement.getResourceName(elem, XmlChildElement.domain);

		List<String> invNames = XmlElement.deduceInverseRoleNames(elem);
		_invRoleName = (invNames.isEmpty()) ? null : invNames.get(0);

		List<String> ranges = XmlElement.deduceRangeNames(elem);
		_range = (ranges.isEmpty()) ? null : ranges.get(0);

		// association ends can be easily deduced for both RDF and OWL
		Kind kind = null;
		String mult = null;
		if (_invRoleName != null) {
			kind = Kind.assocEnd;
			mult = parseAndCheckRdfMultiplicity(elem);
		}
		_multiplicity = mult;

		// RDF has either dataType (simple and dt attrs) or range (enum attrs and roles)
		if (_range == null) {
			_dataType = XmlChildElement.getResourceName(elem, XmlChildElement.dataType);
			kind = Kind.unknown; // until we port everything to OWL, we ignore kind for RDF
		}
		validateDatatypeAndRange();

		_kind = kind;
	}

	/**
	 * Constructor.
	 *
	 * @param model
	 * @param about
	 * @param label
	 * @param comment
	 * @param pckage
	 * @param validateAbout
	 * @param domain
	 * @param kind
	 * @param range
	 * @param invRoleName
	 *            null for attribute
	 * @param multiplicity
	 *            empty string for attribute
	 * @throws CimSchemaException
	 */
	public RdfsProperty(RdfsModel model, String about, String label, String comment, String pckage,
			boolean validateAbout, String domain, Kind kind, String dataType, String range,
			String invRoleName, String multiplicity) throws CimSchemaException {

		super(model, about, label, comment, pckage, validateAbout);
		_domain = domain;
		_dataType = dataType;
		_kind = kind;
		_range = range;
		_invRoleName = invRoleName;
		_multiplicity = multiplicity;

		validateDatatypeAndRange();
	}

	private void validateDatatypeAndRange() {
		if (_dataType != null && _range != null) {
			throw new RuntimeException("Cannot have both datatype and range set:" + _dataType + " "
					+ _range);
		}
		if (_dataType == null && _range == null) {
			throw new RuntimeException("Must have either datatype or range set.");
		}
	}

	// RDF
	private String parseAndCheckRdfMultiplicity(Element elem) {
		String mult = XmlChildElement.getResourceName(elem, XmlChildElement.multiplicity);
		if (mult == null) {
			return null;
		}

		String defaultMultiplicity = "0..n";
		if (mult.endsWith("M:0..n")) {
			return defaultMultiplicity;
		} else if (mult.endsWith("M:0..1")) {
			return "0..1";
		} else if (mult.endsWith("M:1..n")) {
			return "1..n";
		} else if (mult.endsWith("M:1")) {
			return "1";
		} else if (mult.endsWith("M:1..1")) {
			String fixedMultiplicity = "1";
			return fixedMultiplicity;
		} else {
			return defaultMultiplicity;
		}
	}

	// -------------- package API ------------------

	public boolean isAssocEnd() {
		return _kind == Kind.assocEnd;
	}

	public static boolean isPrimitiveType(String className) {
		return XmlResourceValue.getCimPrimitiveClassResourceValues().contains(className);
	}

	public boolean isSimpleAttr() {
		return (getDatatype() != null) && isPrimitiveType(getDatatype());
	}

	public boolean isDatatypeAttr() {
		return (getDatatype() != null) && !isPrimitiveType(getDatatype());
	}

	public boolean isEnumAttr() {
		return (getRange() != null) && !isAssocEnd();
	}

	boolean isEnumAttr(String range) {
		return isEnumAttr() && getRange().equals(range);
	}

	public String getDomain() {
		return _domain;
	}

	public String getRange() {
		return _range;
	}

	public String getDatatype() {
		return _dataType;
	}

	public String getInvRoleName() {
		return _invRoleName;
	}

	public String getMultiplicity() {
		return _multiplicity;
	}

	public String getNameAndMultiplicity() {
		return getName() + " [" + getMultiplicity() + "]";
	}

	@Override
	public String getKind() {
		if (isSimpleAttr()) {
			return "simple attr ";
		} else if (isDatatypeAttr()) {
			return "dt attr ";
		} else if (isEnumAttr()) {
			return "enum attr ";
		} else if (isAssocEnd()) {
			return "assoc end ";
		} else {
			return "unknown kind of " + getClass().getSimpleName() + " ";
		}
	}

	/**
	 * Sets a class as datatype class based on the definition of the property (i.e., when datatype
	 * defined for this attribute). If class does not exist, tries to fix this attribute, if
	 * possible.
	 * <p>
	 * Since RDF does not give us stereotypes, we have to "search" for them. We deduce that class is
	 * a datatype if this attribute's datatype is defined by that class. The class is supposed to
	 * <i>not</i> be a subclass, i.e., it must have been found to be a first level class.
	 */
	void setDtClassAndCrossCheckDtAttribute() {
		RdfsClass firstLevelClass = getModel().findFirstLevelClass(getDatatype());
		if (firstLevelClass == null) {
			// fixDatatypeRangeAndSetDtEnumClass();
			return;
		}

		firstLevelClass.setIsDatatypeClass(true);
	}

	// -------------------- object methods -----------------

	@Override
	public String toString() {
		StringBuilder s = new StringBuilder(super.toString()).append(" : ");
		if (isSimpleAttr() || isDatatypeAttr()) {
			s.append(getDatatype());
		} else {
			s.append(getRange());
			if (isAssocEnd()) {
				s.append(" [").append(_multiplicity).append("]");
				s.append(" invRole = ").append(_invRoleName);
			}
		}
		return s.toString();
	}

	@Override
	public String toStringLong() {
		StringBuilder s = new StringBuilder(super.toStringLong());
		s.append("  domain = '").append(getDomain()).append("'\n");
		if (isSimpleAttr() || isDatatypeAttr()) {
			s.append("  dataType = '").append(getDatatype()).append("'\n");
		} else {
			s.append("  range = '").append(getRange()).append("'\n");
			if (isAssocEnd()) {
				s.append("  multiplicity = '").append(_multiplicity).append("'\n");
				s.append("  invRole = '").append(_invRoleName).append("'\n");
			}
		}
		return s.toString();
	}

	/**
	 * Uses all the instance fields, except for _kind.
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((_dataType == null) ? 0 : _dataType.hashCode());
		result = prime * result + ((_domain == null) ? 0 : _domain.hashCode());
		result = prime * result + ((_invRoleName == null) ? 0 : _invRoleName.hashCode());
		// result = prime * result + ((_kind == null) ? 0 : _kind.hashCode());
		result = prime * result + ((_multiplicity == null) ? 0 : _multiplicity.hashCode());
		result = prime * result + ((_range == null) ? 0 : _range.hashCode());
		return result;
	}

	/**
	 * Uses all the instance fields, except for _kind. Also, if dialects are different, compares
	 * only upper limit in multiplicity.
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (!super.equals(obj)) {
			return false;
		}
		if (!(obj instanceof RdfsProperty)) {
			return false;
		}
		return doEquals(true, obj, null);
	}

	private boolean doEquals(boolean isForEquals, Object obj, List<RdfsDifference> diffs) {
		assert (!isForEquals ? diffs != null : true) : "diffs must be non-null for collecting";

		RdfsProperty other = (RdfsProperty) obj;

		if (_dataType == null) {
			if (other._dataType != null) {
				if (isForEquals) {
					return false;
				}
				diffs.add(formatDiff("dataType", _dataType, other._dataType, other));
			}
		} else if (!_dataType.equals(other._dataType)) {
			if (isForEquals) {
				return false;
			}
			diffs.add(formatDiff("dataType", _dataType, other._dataType, other));
		}

		if (_domain == null) {
			if (other._domain != null) {
				if (isForEquals) {
					return false;
				}
				diffs.add(formatDiff("domain", _domain, other._domain, other));
			}
		} else if (!_domain.equals(other._domain)) {
			if (isForEquals) {
				return false;
			}
			diffs.add(formatDiff("domain", _domain, other._domain, other));
		}

		if (_range == null) {
			if (other._range != null) {
				if (isForEquals) {
					return false;
				}
				diffs.add(formatDiff("range", _range, other._range, other));
			}
		} else if (!_range.equals(other._range)) {
			if (isForEquals) {
				return false;
			}
			diffs.add(formatDiff("range", _range, other._range, other));
		}

		if (_invRoleName == null) {
			if (other._invRoleName != null) {
				if (isForEquals) {
					return false;
				}
				diffs.add(formatDiff("invRoleName", _invRoleName, other._invRoleName, other));
			}
		} else if (!_invRoleName.equals(other._invRoleName)) {
			if (isForEquals) {
				return false;
			}
			diffs.add(formatDiff("invRoleName", _invRoleName, other._invRoleName, other));
		}

		// if (_kind == null) {
		// if (other._kind != null) {
		// return false;
		// }
		// } else if (!_kind.equals(other._kind)) {
		// return false;
		// }

		if (_multiplicity == null) {
			if (other._multiplicity != null) {
				if (isForEquals) {
					return false;
				}
				diffs.add(formatDiff("multiplicity", _multiplicity, other._multiplicity, other));
			}
		} else {
			if (!_multiplicity.equals(other._multiplicity)) {
				if (isForEquals) {
					if (getLastChar(_multiplicity) != getLastChar(other._multiplicity)
							|| getFirstChar(_multiplicity) != getFirstChar(other._multiplicity)) {
						return false;
					}
				} else {
					diffs.add(formatDiff("multiplicity", _multiplicity, other._multiplicity, other));
				}
			}
		}

		return true;
	}

	private char getFirstChar(String s) {
		return s.charAt(0);
	}

	private char getLastChar(String s) {
		return s.charAt(s.length() - 1);
	}

	@Override
	public final List<RdfsDifference> getDiffs(RdfsElem other) {
		List<RdfsDifference> diffsCollector = super.getDiffs(other);
		if (this == other || other == null) {
			return diffsCollector;
		}

		doEquals(false, other, diffsCollector);
		return diffsCollector;
	}
}
