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
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;
import org.w3c.dom.Element;

/**
 * The model content is, at the start, reflecting the content of the parsed RDF/OWL schema, and at
 * the end, after a number of validations and consistency checks, it contains the representation of
 * the corrected UML CIM.
 *
 * @author tatjana.kostic@ieee.org
 * @version $Id: RdfsModel.java 21 2019-08-12 15:44:50Z dev978 $
 */
public final class RdfsModel {
	private static final Logger _logger = Logger.getLogger(RdfsModel.class.getName());

	private String _cimSchemaLabel;

	private final String _name;
	private final Map<String, RdfsPackage> _packages;
	private final Map<String, RdfsClass> _classes;
	private final Map<String, RdfsProperty> _props;
	private final Map<String, RdfsEnumLiteral> _literals;

	/**
	 * Constructor.
	 */
	public RdfsModel(String name) {
		_name = name;
		_packages = new HashMap<String, RdfsPackage>();
		_classes = new HashMap<String, RdfsClass>();
		_props = new HashMap<String, RdfsProperty>();
		_literals = new HashMap<String, RdfsEnumLiteral>();
	}

	/**
	 * Traverses the loaded <code>rdfSchema</code> and stores all of its elements. We first create
	 * individual elements, and on the way, their respective constructors try to fix those problems
	 * that are inherent to the kind of element itself (i.e., where there are no mutual dependencies
	 * between different element types).
	 *
	 * @param rdfSchema
	 * @throws CimSchemaException
	 *             if no schema, or more than one schema found.
	 */
	public void build(XmlDocument rdfSchema) throws CimSchemaException {
		Set<String> cimSchemaLabelsCollector = new HashSet<String>();
		cacheElements(rdfSchema, cimSchemaLabelsCollector);

		if (cimSchemaLabelsCollector.size() > 1) {
			String msg = "Multiple (" + cimSchemaLabelsCollector.size() + ") schema labels found: "
					+ cimSchemaLabelsCollector.toString();
			throw new CimSchemaException(msg);
		}
		if (cimSchemaLabelsCollector.size() < 1) {
			throw new CimSchemaException("No schema label found.");
		}
		_cimSchemaLabel = cimSchemaLabelsCollector.iterator().next();

		// Order of processing is important:

		linkClassesToSuperclass();
		validateAndFixProperties(); // sets dt class from dataType/range value
	}

	private void cacheElements(XmlDocument rdfSchema, Set<String> cimSchemaLabelsCollector)
			throws CimSchemaException {

		for (Element elem : rdfSchema.getPackages().values()) {
			RdfsPackage pack = new RdfsPackage(this, elem);
			_packages.put(pack.getName(), pack);
			cimSchemaLabelsCollector.add(pack.getSchemaLabel());
		}

		for (Element elem : rdfSchema.getClasses().values()) {
			RdfsClass clazz = new RdfsClass(this, elem);
			_classes.put(clazz.getName(), clazz);
			cimSchemaLabelsCollector.add(clazz.getSchemaLabel());
		}

		for (Element elem : rdfSchema.getProperties().values()) {
			RdfsProperty prop = new RdfsProperty(this, elem);
			_props.put(prop.getName(), prop);
			cimSchemaLabelsCollector.add(prop.getSchemaLabel());
		}

		for (Element elem : rdfSchema.getEnumLiterals().values()) {
			RdfsEnumLiteral el = new RdfsEnumLiteral(this, elem);
			_literals.put(el.getName(), el);
			cimSchemaLabelsCollector.add(el.getSchemaLabel());
		}
	}

	private void linkClassesToSuperclass() {
		for (RdfsClass c : _classes.values()) {
			if (c.isSubclass()) {
				c.linkWithSuperclass();
			}
		}
	}

	private void validateAndFixProperties() {
		for (RdfsProperty prop : _props.values()) {
			if (prop.isSimpleAttr() || prop.isEnumAttr() || prop.isAssocEnd()) {
				// nothing to do
			} else if (prop.isDatatypeAttr()) {
				prop.setDtClassAndCrossCheckDtAttribute();
			} else {
				_logger.error("unclassified property '" + prop.getName() + "'");
			}
		}
	}

	String getName() {
		return _name;
	}

	/**
	 * Returns the class that is not a sub-classes (i.e., that is direct sub-element of the RDF
	 * Schema), null if not found.
	 *
	 * @param className
	 * @return all the classes that are not sub-classes.
	 */
	RdfsClass findFirstLevelClass(String className) {
		RdfsClass c = findClass(className);
		if (c != null && !c.isSubclass()) {
			return c;
		}
		return null;
	}

	/**
	 * Returns the cached class for the given name, null if not found.
	 *
	 * @param className
	 * @return the cached class for the given name.
	 */
	RdfsClass findClass(String className) {
		return _classes.get(className);
	}

	/**
	 * Returns whether the enumliteral name has already been cached.
	 *
	 * @param elName
	 * @return whether this description has already been cached.
	 */
	boolean hasLiteral(String elName) {
		return _literals.containsKey(elName);
	}

	/**
	 * Returns all the cached properties for the given class, empty set if none.
	 *
	 * @param className
	 * @return all the cached properties for the given class.
	 */
	Set<String> findPropsForClass(String className) {
		Set<String> result = new HashSet<String>();
		for (String prop : _props.keySet()) {
			if (prop.startsWith(className + ".")) {
				result.add(prop);
			}
		}
		return result;
	}

	/**
	 * Returns the cached property for the given name, null if not found.
	 *
	 * @param prop
	 * @return cached property for the given name, null if not found.
	 */
	public RdfsProperty findProperty(String prop) {
		return _props.get(prop);
	}

	// -------------- API -------------------

	public List<RdfsDifference> calcDiffs(RdfsModel other) {
		List<RdfsDifference> result = new ArrayList<RdfsDifference>();
		result.addAll(calcDiffPackages(other, true));
		result.addAll(calcDiffEnumLiterals(other, true));
		result.addAll(calcDiffClasses(other, true));
		result.addAll(calcDiffProps(other, true));
		return result;
	}

	/**
	 * Returns string containing all the differences in CSV format.
	 *
	 * @return string containing all the differences in CSV format.
	 */
	public String getDiffsAsCSV(RdfsModel other) {
		StringBuilder s = new StringBuilder();
		s.append(RdfsDifference.CSV_HEADING).append("\n");
		for (RdfsDifference diff : calcDiffs(other)) {
			s.append(diff.toCSV()).append("\n");
		}
		return s.toString();
	}

	public List<RdfsDifference> calcDiffPackages(RdfsModel other, boolean isDeep) {
		return calcSetDiff("packages", _packages, other._packages, other, isDeep);
	}

	public List<RdfsDifference> calcDiffClasses(RdfsModel other, boolean isDeep) {
		return calcSetDiff("classes", _classes, other._classes, other, isDeep);
	}

	public List<RdfsDifference> calcDiffProps(RdfsModel other, boolean isDeep) {
		return calcSetDiff("props", _props, other._props, other, isDeep);
	}

	public List<RdfsDifference> calcDiffEnumLiterals(RdfsModel other, boolean isDeep) {
		return calcSetDiff("enumLiterals", _literals, other._literals, other, isDeep);
	}

	// for isDeep, compares also elements existing in both sets
	private List<RdfsDifference> calcSetDiff(String field,
			Map<String, ? extends RdfsElem> thisWhat, Map<String, ? extends RdfsElem> otherWhat,
			RdfsModel other, boolean isDeep) {
		Set<String> thisNames = thisWhat.keySet();
		Set<String> otherNames = otherWhat.keySet();
		Set<String> missingFromThis = getMissingFrom(thisNames, otherNames);
		Set<String> missingFromOther = getMissingFrom(otherNames, thisNames);

		List<RdfsDifference> result = new ArrayList<RdfsDifference>();
		if (!missingFromThis.isEmpty() || !missingFromOther.isEmpty()) {
			String val1 = formatNameSetDiffDetail(missingFromThis, missingFromOther);
			String detail = formatNameSetDiffSummary(missingFromThis, missingFromOther);
			RdfsDifference diff = new RdfsDifference("", "model", getName(),
					RdfsDifference.Kind.nameSet, field, val1, "", detail);
			result.add(diff);
		}
		if (!isDeep) {
			return result;
		}

		for (RdfsElem thisElem : thisWhat.values()) {
			RdfsElem otherElem = otherWhat.get(thisElem.getName());
			if (otherElem == null) {
				result.add(thisElem.formatMissingAndAdded(true));
			} else {
				List<RdfsDifference> diffs = thisElem.getDiffs(otherElem);
				if (!diffs.isEmpty()) {
					result.addAll(diffs);
				}
			}
		}
		for (String otherName : missingFromThis) {
			RdfsElem otherElem = otherWhat.get(otherName);
			result.add(otherElem.formatMissingAndAdded(false));
		}
		return result;
	}

	static Set<String> getMissingFrom(Set<String> examined, Set<String> reference) {
		Set<String> inReferenceOnly = new HashSet<String>(reference);
		inReferenceOnly.removeAll(examined);
		return inReferenceOnly;
	}

	static String formatNameSetDiffSummary(Set<String> missingFromThis, Set<String> missingFromOther) {
		if (missingFromThis.isEmpty() && missingFromOther.isEmpty()) {
			return "";
		}
		StringBuilder s = new StringBuilder();
		s.append("diff = "); // this must not be something spreadsheet would take as a formula
		s.append("-").append(missingFromThis.size());
		s.append(" +").append(missingFromOther.size());
		return s.toString();
	}

	static String formatNameSetDiffDetail(Set<String> missingFromThis, Set<String> missingFromOther) {
		StringBuilder s = new StringBuilder();
		if (!missingFromThis.isEmpty()) {
			s.append(formatSortedSet(missingFromThis, "missing"));
		}
		if (!missingFromOther.isEmpty()) {
			s.append(formatSortedSet(missingFromOther, " added"));
		}
		return s.toString();
	}

	private static String formatSortedSet(Set<String> missingFromThis, String how) {
		String[] sorted = missingFromThis.toArray(new String[missingFromThis.size()]);
		Arrays.sort(sorted);

		StringBuilder sb = new StringBuilder();
		sb.append(how).append(" ").append(sorted.length).append(" =")
		.append(Arrays.toString(sorted)).append(";");
		return sb.toString().replace(",", "");
	}

	// --------------

	/**
	 * Returns the label of the schema for this hierarchy.
	 *
	 * @return the label of the schema for this hierarchy.
	 */
	public String getSchemaLabel() {
		return _cimSchemaLabel;
	}

	/**
	 * Returns all parsed packages.
	 *
	 * @return all parsed packages.
	 */
	public Map<String, RdfsPackage> getPackages() {
		return Collections.unmodifiableMap(_packages);
	}

	/**
	 * Returns all parsed classes.
	 *
	 * @return all parsed classes.
	 */
	public Map<String, RdfsClass> getClasses() {
		return Collections.unmodifiableMap(_classes);
	}

	public Map<String, RdfsClass> getEnumClasses() {
		Map<String, RdfsClass> result = new HashMap<String, RdfsClass>();
		for (RdfsClass c : _classes.values()) {
			if (c.isEnumClass()) {
				result.put(c.getName(), c);
			}
		}
		return result;
	}

	public Map<String, RdfsClass> getDatatypeClasses() {
		Map<String, RdfsClass> result = new HashMap<String, RdfsClass>();
		for (RdfsClass c : _classes.values()) {
			if (c.isDatatypeClass()) {
				result.put(c.getName(), c);
			}
		}
		return result;
	}

	public Map<String, RdfsClass> getPrimitiveClasses() {
		Map<String, RdfsClass> result = new HashMap<String, RdfsClass>();
		for (RdfsClass c : _classes.values()) {
			if (c.isPrimitiveClass()) {
				result.put(c.getName(), c);
			}
		}
		return result;
	}

	public Map<String, RdfsClass> getSubClasses() {
		Map<String, RdfsClass> result = new HashMap<String, RdfsClass>();
		for (RdfsClass c : _classes.values()) {
			if (c.isSubclass()) {
				result.put(c.getName(), c);
			}
		}
		return result;
	}

	public Map<String, RdfsClass> getFirstLevelClasses() {
		Map<String, RdfsClass> result = new HashMap<String, RdfsClass>();
		for (RdfsClass c : _classes.values()) {
			if (!(c.isDatatypeClass() || c.isEnumClass() || c.isPrimitiveClass() || c.isSubclass())) {
				result.put(c.getName(), c);
			}
		}
		return result;
	}

	/**
	 * Returns all parsed properties.
	 *
	 * @return all parsed properties.
	 */
	public Map<String, RdfsProperty> getProps() {
		return Collections.unmodifiableMap(_props);
	}

	/**
	 * Returns all parsed enumeration literals.
	 *
	 * @return all parsed enumeration literals.
	 */
	public Map<String, RdfsEnumLiteral> getEnumLiterals() {
		return Collections.unmodifiableMap(_literals);
	}

	/**
	 * Clears the cache. Use this method after you've got the class hierarchy, to release some
	 * memory that is not used anymore.
	 */
	public void clear() {
		_packages.clear();
		_classes.clear();
		_props.clear();
		_literals.clear();
	}
}
