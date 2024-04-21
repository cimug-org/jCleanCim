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

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;
import org.w3c.dom.Element;

/**
 * CIM RDF Schema element representing the UML class.
 *
 * @author tatjana.kostic@ieee.org
 * @version $Id: RdfsClass.java 21 2019-08-12 15:44:50Z dev978 $
 */
public final class RdfsClass extends RdfsElem {
	private static final Logger _logger = Logger.getLogger(RdfsClass.class.getName());

	private final List<String> _superclassNames;

	// derived
	private final boolean _isSubclass;

	// derived
	private final Map<String, RdfsClass> _subclasses;

	// derived
	private final List<RdfsEnumLiteral> _enumLiterals;

	// derived
	private final boolean _isPrimitive;

	// derived
	private boolean _isDatatype;

	// derived
	private final boolean _isEnum;

	// derived
	private final boolean _isCompound;

	// FIXME: see how to get these?
	private final Map<String, String> _attrInitValues;

	// --------------------- creation --------------------------

	/**
	 * Creates instance from DOM Element. Categorises the class as either first level class or a
	 * sub-class. Among the first level classes are those that will later on be further categorised
	 * as primitive, enum, data type, compound and other first level classes.
	 *
	 * @param model
	 * @param elem
	 * @throws CimSchemaException
	 *             if about attribute contains an invalid URI.
	 */
	RdfsClass(RdfsModel model, Element elem) throws CimSchemaException {
		super(model, elem);

		_superclassNames = XmlChildElement.getResourceNames(elem, XmlChildElement.subClassOf);
		_isSubclass = determineWhetherSubclass();

		_subclasses = new HashMap<String, RdfsClass>();
		_enumLiterals = new ArrayList<RdfsEnumLiteral>();
		_isPrimitive = XmlElement.isPrimitiveClass(elem);
		_isEnum = XmlElement.isEnumClass(elem);
		_isDatatype = XmlElement.isDatatypeClass(elem);
		_isCompound = XmlElement.isCompoundClass(elem);
		_attrInitValues = new HashMap<String, String>();
	}

	private boolean determineWhetherSubclass() {
		// trim superclasses down to one or none, also logging inconsistencies
		if (_superclassNames.size() >= 1) {
			for (Iterator<String> it = _superclassNames.iterator(); it.hasNext();) {
				String supName = it.next();
				if (supName.trim().isEmpty()) {
					it.remove();
				}
			}
		}

		return _superclassNames.size() > 0;
	}

	// --------------------- package API --------------------------

	void linkWithSuperclass() {
		for (String supName : _superclassNames) {
			RdfsClass supClass = getModel().findClass(supName);
			if (supClass == null) {
				_logger.error(String.format("Inexisting superclass %s for %s", supName,
						this.getName()));
				continue;
			}

			supClass._subclasses.put(this.getName(), this);
		}
	}

	// --------------------- API --------------------------

	public boolean isSubclass() {
		return _isSubclass;
	}

	public Map<String, RdfsClass> getSubclasses() {
		return Collections.unmodifiableMap(_subclasses);
	}

	public List<RdfsEnumLiteral> getEnumLiterals() {
		return Collections.unmodifiableList(_enumLiterals);
	}

	public boolean isPrimitiveClass() {
		return _isPrimitive;
	}

	public boolean isDatatypeClass() {
		return _isDatatype;
	}

	public boolean isEnumClass() {
		return _isEnum;
	}

	public boolean isCompoundClass() {
		return _isCompound;
	}

	void addEnumLiteral(RdfsEnumLiteral el) {
		_enumLiterals.add(el);
	}

	/**
	 * RDF only
	 *
	 * @param val
	 */
	void setIsDatatypeClass(boolean val) {
		if (_isDatatype == val) {
			return;
		}
		if (val == true) {
			if (_isEnum) {
				throw new IllegalStateException("Enum class '" + getName()
						+ "' cannot be datatype.");
			}
			if (isSubclass()) {
				throw new IllegalStateException("Subclass '" + getName() + "' cannot be datatype.");
			}
		}
		_isDatatype = val;
	}

	/**
	 * Returns whether this class is valid to be used as association domain or range.
	 *
	 * @return whether this class is valid to be used as association domain or range.
	 */
	boolean isValidForAssociation() {
		return !isValidForAttribute();
	}

	/**
	 * Returns whether this class is valid to be used as attribute dataType or range.
	 *
	 * @return whether this class is valid to be used as attribute dataType or range.
	 */
	boolean isValidForAttribute() {
		return isDatatypeClass() || isEnumClass() || isPrimitiveClass();
	}

	/**
	 * Here the kinds of classes:
	 *
	 * <pre>
	 * First level classes:  non-sub (root)   Enum, Datatype, Compound, Primitive (leaf) Other (leaf)
	 * Sub-class:            sub
	 * Deepest sub-class:    sub (leaf)
	 * </pre>
	 *
	 * @return string describing the kind of this class.
	 */
	@Override
	public String getKind() {
		if (isDatatypeClass()) {
			return "dt class"; // first level
		} else if (isEnumClass()) {
			return "enum class"; // first level
		} else if (isCompoundClass()) {
			return "compound class"; // first level
		} else if (isPrimitiveClass()) {
			return "prim class"; // first level
		} else if (isSubclass()) {
			if (_subclasses.size() > 0) {
				return "mid subclass";
			}
			return "leaf subclass";
		} else { // first level
			if (_subclasses.size() > 0) {
				return "root class";
			}
			return "leaf-nonDtEnumCompPrim class";
		}
	}

	/**
	 * Returns cached initial value for the attribute (deduced by reflection).
	 *
	 * @param attrName
	 * @return cached initial value for the attribute (deduced by reflection).
	 */
	public String getAttrInitValue(String attrName) {
		return _attrInitValues.get(attrName);
	}

	@Override
	public String toString() {
		StringBuilder s = new StringBuilder(super.toString());
		if (_superclassNames.size() > 0) {
			s.append(" supers:").append(_superclassNames.toString());
		}
		if (_subclasses.size() > 0) {
			s.append(" subs:").append(_subclasses.keySet()).append("");
		}
		if (_enumLiterals.size() > 0) {
			s.append(" enumLiterals:[").append(getEnumLiteralNames().toString()).append("]");
		}
		return s.toString();
	}

	@Override
	public String toStringLong() {
		StringBuilder s = new StringBuilder(super.toStringLong());
		if (_superclassNames.size() > 0) {
			s.append("  supers = ").append(_superclassNames.toString()).append("\n");
		}
		if (_subclasses.size() > 0) {
			s.append("  subs = ").append(_subclasses.keySet()).append("\n");
		}
		if (_enumLiterals.size() > 0) {
			s.append(" enumLiterals = [").append(getEnumLiteralNames().toString()).append("]");
		}
		return s.toString();
	}

	private List<String> getEnumLiteralNames() {
		List<String> result = new ArrayList<String>();
		for (RdfsEnumLiteral c : _enumLiterals) {
			result.add(c.getName());
		}
		return result;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + (_isDatatype ? 1231 : 1237);
		result = prime * result + (_isEnum ? 1231 : 1237);
		result = prime * result + (_isSubclass ? 1231 : 1237);
		result = prime * result + (isPrimitiveClass() ? 1231 : 1237);
		result = prime * result + _subclasses.keySet().hashCode();
		result = prime * result + _superclassNames.hashCode();
		result = prime * result + _enumLiterals.hashCode();
		result = prime * result + _attrInitValues.hashCode();
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (!super.equals(obj)) {
			return false;
		}
		if (!(obj instanceof RdfsClass)) {
			return false;
		}
		return doEquals(true, obj, null);
	}

	private boolean doEquals(boolean isForEquals, Object obj, List<RdfsDifference> diffs) {
		assert (!isForEquals ? diffs != null : true) : "diffs must be non-null for collecting";

		RdfsClass other = (RdfsClass) obj;

		if (_isDatatype != other._isDatatype) {
			if (isForEquals) {
				return false;
			}
			diffs.add(formatDiff("isDatatype", Boolean.toString(_isDatatype),
					Boolean.toString(other._isDatatype), other));
		}

		if (_isEnum != other._isEnum) {
			if (isForEquals) {
				return false;
			}
			diffs.add(formatDiff("isEnum", Boolean.toString(_isEnum),
					Boolean.toString(other._isEnum), other));
		}

		if (_isSubclass != other._isSubclass) {
			if (isForEquals) {
				return false;
			}
			diffs.add(formatDiff("isSubclass", Boolean.toString(_isSubclass),
					Boolean.toString(other._isSubclass), other));
		}

		if (isPrimitiveClass() != other.isPrimitiveClass()) {
			if (isForEquals) {
				return false;
			}
			diffs.add(formatDiff("isPrimitive", Boolean.toString(isPrimitiveClass()),
					Boolean.toString(other.isPrimitiveClass()), other));
		}

		if (!_subclasses.keySet().equals(other._subclasses.keySet())) {
			if (isForEquals) {
				return false;
			}
			diffs.add(createNameSetDiff("subclasses", _subclasses.keySet(),
					other._subclasses.keySet()));
		}

		if (!_superclassNames.equals(other._superclassNames)) {
			if (isForEquals) {
				return false;
			}
			diffs.add(createNameSetDiff("superclassNames", new HashSet<String>(_superclassNames),
					new HashSet<String>(other._superclassNames)));
		}

		if (!_enumLiterals.equals(other._enumLiterals)) {
			if (isForEquals) {
				return false;
			}
			diffs.add(createNameSetDiff("enumLiterals", new HashSet<String>(getEnumLiteralNames()),
					new HashSet<String>(other.getEnumLiteralNames())));
		}

		if (!_attrInitValues.equals(other._attrInitValues)) {
			if (isForEquals) {
				return false;
			}
			diffs.add(formatDiff("attrInitValues", _attrInitValues.toString(),
					other._attrInitValues.toString(), other));
		}

		return true;
	}

	private RdfsDifference createNameSetDiff(String field, Set<String> thisNames,
			Set<String> otherNames) {
		Set<String> missingFromThis = RdfsModel.getMissingFrom(thisNames, otherNames);
		Set<String> missingFromOther = RdfsModel.getMissingFrom(otherNames, thisNames);
		String val1 = RdfsModel.formatNameSetDiffDetail(missingFromThis, missingFromOther);
		String detail = RdfsModel.formatNameSetDiffSummary(missingFromThis, missingFromOther);
		RdfsDifference diff = new RdfsDifference(getPackage(), getKind(), getName(),
				RdfsDifference.Kind.nameSet, field, val1, "", detail);
		return diff;
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
