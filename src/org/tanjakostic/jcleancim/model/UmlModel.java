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
import java.util.Collection;
import java.util.Collections;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;
import java.util.UUID;

import org.apache.log4j.Logger;
import org.tanjakostic.jcleancim.common.Config;
import org.tanjakostic.jcleancim.common.Nature;
import org.tanjakostic.jcleancim.common.OwningWg;
import org.tanjakostic.jcleancim.model.UmlPackage.Kind;
import org.tanjakostic.jcleancim.util.Util;

/**
 * TODO: Add link to test model built with the API.
 * <p>
 * Under the root in a repository, there may be several models. This class stores them all as
 * {@link Kind#MODEL} packages - they are the entry point into the full UML content. In the standard
 * IEC TC57 UML we have three such models:
 * <ul>
 * <li>TC57CIM, with {@link Nature#CIM} (default),</li>
 * <li>TC57CIMProfiles, with {@link Nature#CIM_PROFILE} (must be specified in configuration),
 * and,</li>
 * <li>IEC61850Domain, with {@link Nature#IEC61850} (must be specified in configuration).</li>
 * </ul>
 * A model package contains one or more {@link Kind#TOP} packages, each of them assigned to an owner
 * {@link OwningWg}. Owner is typically an IEC TC57 working group that manages the part of the model
 * under the {@link Kind#TOP} package.
 * <p>
 * If a model package is found that contains top-level packages of unknown names, that model will
 * get the owner {@link OwningWg#OTHER_CIM} (default) or {@link OwningWg#OTHER_IEC61850} (if model
 * name specified in configuration as {@link Nature#IEC61850}). This allows to combine the custom
 * extensions with the standard models.
 * <p>
 * The current implementation of {@link OwningWg} defines the "known" top-package names and IEC TC57
 * working groups and holds the rules about allowed dependencies. That class does not care about
 * models, but rather about top-level packages (each owned by a WG). This also provides for
 * flexibility when you develop non-standard extensions.
 * <p>
 * Implementation note 1: The internal maps in this class are used purely to store model elements
 * present in the UML repository, for quick access to elements per UML type where bulk data is
 * needed. The package-private modifiers, such as {@link #addClass(UmlClass)}, perform no
 * consistency checks at all and should not be used - respect the restrictions given in their doc!
 * In contrast, the in-memory objects, once after they get inter-linked with their own accessors
 * (e.g., {@link UmlPackage#addClass(UmlClass)}), can be navigated "naturally" with their own
 * getters ((e.g., {@link UmlPackage#getClasses()})). The entry point for that navigation are model
 * packages, obtained through {@link #getModelPackages()}.
 * <p>
 * Implementation note 2: This class also creates internal "null" model packages and classes (one
 * per nature) to provide for valid in-memory objects for types of attributes and operations that
 * may be invalid in the repository (and for which we don't have any information, but need to create
 * an instance of {@link UmlClass}. The treatment of these "null" elements is different on purpose:
 * we don't want to, e.g., validate them or to print their documentation - but we have to be able to
 * do this with the in-memory objects whose type {@link UmlClass} may be invalid in the original UML
 * repository - so, they are never included in maps/collections that contain the in-memory contect
 * from the real UML model repository.
 *
 * @author tatjana.kostic@ieee.org
 * @version $Id: UmlModel.java 26 2019-11-12 18:50:35Z dev978 $
 */
public class UmlModel {
	private static final Logger _logger = Logger.getLogger(UmlModel.class.getName());

	private final Config _cfg;
	private final String _uuid;
	private final Collection<UmlPackage> _modelPackages = new LinkedHashSet<>();
	private final Map<Nature, UmlPackage> _nullModelPackages = new LinkedHashMap<>();
	private final Map<Nature, UmlClass> _nullClasses = new LinkedHashMap<>();

	private final Map<String, UmlPackage> _packages = new LinkedHashMap<>();
	private final Map<String, UmlClass> _classes = new LinkedHashMap<>();
	private final Map<String, UmlAttribute> _attributes = new LinkedHashMap<>();
	private final Map<String, UmlAssociation> _associations = new LinkedHashMap<>();
	private final Map<String, UmlDependency> _dependencies = new LinkedHashMap<>();
	private final Map<String, UmlOperation> _operations = new LinkedHashMap<>();
	private final Map<String, UmlDiagram> _diagrams = new LinkedHashMap<>();

	private final Map<String, List<UmlAttribute>> _abbrTerms = new LinkedHashMap<>();
	private Map<String, String> _sortedAbbrTerms; // lazy-loaded if required

	private Map<String, Set<UmlObject>> _tags; // lazy-loaded: tags are added after construction

	private Map<String, UmlAttribute> _presenceConditions; // lazy-loaded if required

	void clear() {
		_modelPackages.clear();
		_packages.clear();
		_classes.clear();
		_attributes.clear();
		_associations.clear();
		_dependencies.clear();
		_operations.clear();
		_diagrams.clear();

		_abbrTerms.clear();

		if (_tags != null) {
			_tags.clear();
		}
	}

	/**
	 * Constructor.
	 */
	public UmlModel(Config cfg) {
		Util.ensureNotNull(cfg, "cfg");

		_cfg = cfg;
		_uuid = UUID.randomUUID().toString();

		for (Nature nature : Nature.values()) {
			UmlPackage mp = new UmlPackage(this, nature);
			UmlClass c = new UmlClass(mp);

			_nullModelPackages.put(nature, mp);
			_nullClasses.put(nature, c);
		}
	}

	/** Returns configuration. */
	public Config getCfg() {
		return _cfg;
	}

	/** Returns model UUID. */
	public String getUuid() {
		return _uuid;
	}

	/** Returns all model packages. */
	public Collection<UmlPackage> getModelPackages() {
		return Collections.unmodifiableCollection(_modelPackages);
	}

	/**
	 * Returns special, "null" model packages per nature; they are created by default to hold
	 * special, "null" classes that may be needed when the model repository allows for invalid or
	 * bad definition of types for attributes and operation parameters. These "null" model packages
	 * are not included in the model through regular packages accessors, but through this method
	 * only.
	 */
	public Map<Nature, UmlPackage> getNullModelPackages() {
		return Collections.unmodifiableMap(_nullModelPackages);
	}

	/**
	 * Returns special, "null" classes per nature.
	 *
	 * @see #getNullModelPackages()
	 */
	public Map<Nature, UmlClass> getNullClasses() {
		return Collections.unmodifiableMap(_nullClasses);
	}

	// ===========================================

	/**
	 * @param profilesModel
	 */
	public void crossCheck(UmlModel profilesModel) {
		_logger.info("NOT YET IMPLEMENTED");
		// FIXME See whether to keep this here or to create a new package/class.
	}

	// ===========================================

	// ----------------- packages --------------------

	private UmlPackage addModelPackage(UmlPackage modelPackage) {
		Util.ensureNotNull(modelPackage, "modelPackage");

		_modelPackages.add(modelPackage); // roots

		return modelPackage;
	}

	/** Intended to be called by {@link UmlPackage} (and tests) only. */
	UmlPackage addPackage(UmlPackage p) {
		Util.ensureNotNull(p, "p");
		_packages.put(p.getUuid(), p);

		if (p.getKind() == Kind.MODEL) {
			addModelPackage(p);
		}
		return p;
	}

	/** Returns all packages in this model. */
	public Collection<UmlPackage> getPackages() {
		return Collections.unmodifiableCollection(_packages.values());
	}

	/**
	 * TODO: Refactor to use AbstractUmlObject.findAllForName().
	 * <p>
	 * Returns potentially empty list of all packages whose name matches one of <code>names</code>.
	 */
	public Collection<UmlPackage> findPackages(List<String> names) {
		Util.ensureNotNull(names, "names");
		Collection<UmlPackage> result = new LinkedHashSet<UmlPackage>(names.size());
		for (UmlPackage p : _packages.values()) {
			if (names.contains(p.getName())) {
				result.add(p);
			}
		}
		return result;
	}

	// ----------------- classes --------------------

	/** Intended to be called by {@link UmlPackage#addClass(UmlClass)} and tests only. */
	UmlClass addClass(UmlClass c) {
		Util.ensureNotNull(c, "c");
		_classes.put(c.getUuid(), c);
		return c;
	}

	/** Returns all classes in this model. */
	public Collection<UmlClass> getClasses() {
		return Collections.unmodifiableCollection(_classes.values());
	}

	/** Returns all classes with name matching <code>name</code>. */
	public Set<UmlClass> findClasses(String name) {
		return AbstractUmlObject.findAllForName(getClasses(), name);
	}

	/**
	 * Returns all classes from owners <code>wg</code>.
	 *
	 * @param wgs
	 *            one or more owners.
	 * @param cimKinds
	 *            one or more CIM class kinds.
	 * @param iec61850Kinds
	 *            one or more IEC61850 class kinds.
	 * @param includeNormative
	 *            whether to include normative classes.
	 * @param includeInformative
	 *            whether to include informative classes.
	 */
	public Set<UmlClass> findClasses(EnumSet<OwningWg> wgs, EnumSet<UmlClass.CimKind> cimKinds,
			EnumSet<UmlClass.Iec61850Kind> iec61850Kinds, boolean includeNormative,
			boolean includeInformative) {
		Set<UmlClass> result = new HashSet<UmlClass>();
		for (UmlClass c : _classes.values()) {
			if (wgs.contains(c.getOwner())
					&& (cimKinds.contains(c.getKind()) || iec61850Kinds.contains(c.getKind()))
					&& (includeNormative && !c.isInformative()
							|| includeInformative && c.isInformative())) {
				result.add(c);
			}
		}
		return result;
	}

	/** Returns all classes that have constraints. */
	public Collection<UmlClass> findClassesWithConstraints() {
		Collection<UmlClass> result = new LinkedHashSet<UmlClass>();
		for (UmlClass c : _classes.values()) {
			if (!c.getConstraints().isEmpty()) {
				result.add(c);
			}
		}
		return result;
	}

	// ----------------- attributes --------------------

	/**
	 * Intended to be called by {@link UmlClass} and tests only; if <code>a</code> is abbreviation
	 * literal, stores is also in the abbreviation literal's map.
	 * <p>
	 * FIXME: Test that abbreviation gets stored.
	 */
	UmlAttribute addAttribute(UmlAttribute a) {
		Util.ensureNotNull(a, "a");
		_attributes.put(a.getUuid(), a);

		if (a.isLiteral() && a.getContainingClass().isAbbreviationEnumeration()) {
			if (!_abbrTerms.containsKey(a.getName())) {
				_abbrTerms.put(a.getName(), new ArrayList<UmlAttribute>());
			}
			_abbrTerms.get(a.getName()).add(a);
		}
		return a;
	}

	/**
	 * (IEC61850) Returns all abbreviated terms sorted by decreasing length; handles duplicate
	 * definitions by appending all of them per term.
	 */
	public Map<String, String> getAbbreviatedTermsSortedPerDecreasingLength() {
		if (_sortedAbbrTerms == null) {
			_sortedAbbrTerms = Util.sortByDecreasingLength(abbrLiteralsToTerms());
		}
		return _sortedAbbrTerms;
	}

	private Map<String, String> abbrLiteralsToTerms() {
		Map<String, String> result = new LinkedHashMap<>();
		for (Entry<String, List<UmlAttribute>> entry : _abbrTerms.entrySet()) {
			String term = entry.getKey();
			List<UmlAttribute> abbrLits = entry.getValue();

			StringBuilder desc = new StringBuilder(32);
			for (int i = 0; i < abbrLits.size(); ++i) {
				UmlAttribute abbrLit = abbrLits.get(i);
				desc.append(abbrLit.getDescription().text);
				if (i < abbrLits.size() - 1) {
					desc.append("/");
				}
			}
			result.put(term, desc.toString());
		}
		return result;
	}

	/** Returns all attributes in this model. */
	public Collection<UmlAttribute> getAttributes() {
		return Collections.unmodifiableCollection(_attributes.values());
	}

	/** (IEC61850) Returns all abbreviation literal indexed by name. */
	public Map<String, List<UmlAttribute>> getAbbreviationLiterals() {
		return Collections.unmodifiableMap(_abbrTerms);
	}

	/** (IEC61850) Returns all literals that represent presence conditions. */
	public Map<String, UmlAttribute> findPresenceConditionLiterals() {
		if (_presenceConditions == null) {
			_presenceConditions = new LinkedHashMap<String, UmlAttribute>();
			for (UmlClass c : _classes.values()) {
				if (c.isConditionEnumeration()) {
					for (UmlAttribute literal : c.getAttributes()) {
						_presenceConditions.put(literal.getName(), literal);
					}
				}
			}
		}
		return _presenceConditions;
	}

	/**
	 * Returns all attributes in and under the package <code>packageName</code> according to the
	 * given filters (note: setting both <code>includeLiterals</code> and
	 * <code>includeNonLiterals</code> to true returns all attributes).
	 *
	 * @param packageName
	 *            name of package to start from.
	 * @param includeLiterals
	 *            whether to include enumeration literals.
	 * @param includeNonLiterals
	 *            whether to include non-literals.
	 */
	public List<UmlAttribute> findAttributes(String packageName, boolean includeLiterals,
			boolean includeNonLiterals) {
		List<UmlAttribute> result = new ArrayList<UmlAttribute>();
		for (UmlClass clazz : _classes.values()) {
			if (clazz.getContainingPackage().isInOrUnderPackage(packageName)) {
				if (clazz.isEnumeratedType()) {
					if (includeLiterals) {
						result.addAll(clazz.getAttributes());
					}
				} else {
					if (includeNonLiterals) {
						result.addAll(clazz.getAttributes());
					}
				}
			}
		}
		return result;
	}

	/**
	 * Returns all attributes per name in and under the package <code>packageName</code>, grouped by
	 * their name and according to the given filters (note: setting both
	 * <code>includeLiterals</code> and <code>includeNonLiterals</code> to true returns all
	 * attributes). The names may be ordered.
	 *
	 * @param packageName
	 *            name of package to start from.
	 * @param includeLiterals
	 *            whether to include enumeration literals.
	 * @param includeNonLiterals
	 *            whether to include non-literals.
	 * @param namesOrdered
	 *            whether to perform name ordering.
	 */
	public Map<String, List<UmlAttribute>> findAttributesWithDuplicates(String packageName,
			boolean includeLiterals, boolean includeNonLiterals, boolean namesOrdered) {
		Map<String, List<UmlAttribute>> result = null;

		if (namesOrdered) {
			result = new TreeMap<String, List<UmlAttribute>>();
		} else {
			result = new LinkedHashMap<String, List<UmlAttribute>>();
		}

		List<UmlAttribute> attrs = findAttributes(packageName, includeLiterals, includeNonLiterals);
		for (UmlAttribute attr : attrs) {
			String key = attr.getName();
			if (!result.containsKey(key)) {
				List<UmlAttribute> values = new ArrayList<UmlAttribute>();
				values.add(attr);
				result.put(key, values);
			} else {
				result.get(key).add(attr);
			}
		}
		return Collections.unmodifiableMap(result);
	}

	/**
	 * Same as {@link #findAttributes(String, boolean, boolean)}, but allows for ordering per name
	 * to be specified.
	 *
	 * @param packageName
	 *            name of package to start from.
	 * @param includeLiterals
	 *            whether to include enumeration literals.
	 * @param includeNonLiterals
	 *            whether to include non-literals.
	 * @param namesOrdered
	 *            whether to perform name ordering.
	 */
	public List<UmlAttribute> findAttributes(String packageName, boolean includeLiterals,
			boolean includeNonLiterals, boolean namesOrdered) {

		Map<String, UmlAttribute> result = null;

		result = (namesOrdered) ? new TreeMap<String, UmlAttribute>()
				: new LinkedHashMap<String, UmlAttribute>();

		List<UmlAttribute> attrs = findAttributes(packageName, includeLiterals, includeNonLiterals);
		for (UmlAttribute attr : attrs) {
			String key = attr.getName();
			if (!result.containsKey(key)) {
				result.put(key, attr);
			} else {
				_logger.warn(String.format(
						"Another attribute with name %s already" + " found (%s) - skipping (%s)",
						attr.getName(), result.get(attr.getName()).getQualifiedName(),
						attr.getQualifiedName()));
			}
		}
		return new ArrayList<UmlAttribute>(result.values());
	}

	/** Returns attributes that have any kind of constraint (own and by class). */
	public Collection<UmlAttribute> findAttributesWithConstraints() {
		Collection<UmlAttribute> result = new LinkedHashSet<UmlAttribute>();
		for (UmlAttribute a : _attributes.values()) {
			if (!a.getConstraintsFromClass().isEmpty() || !a.getOwnConstraints().isEmpty()) {
				result.add(a);
			}
		}
		return result;
	}

	/** Returns multi-valued attributes. */
	public Collection<UmlAttribute> findMultivaluedAttributes() {
		Collection<UmlAttribute> result = new LinkedHashSet<UmlAttribute>();
		for (UmlAttribute a : _attributes.values()) {
			if (a.isMultivalued()) {
				result.add(a);
			}
		}
		return result;
	}

	/** Returns attributes on logical nodes (not in meta-model). */
	public Collection<UmlAttribute> findDOAttributes() {
		Collection<UmlAttribute> result = new LinkedHashSet<UmlAttribute>();
		for (UmlAttribute a : _attributes.values()) {
			if (a.isDO()) {
				result.add(a);
			}
		}
		return result;
	}

	// -----------------

	/** Intended to be called by {@link UmlClass} and tests only. */
	UmlOperation addOperation(UmlOperation op) {
		Util.ensureNotNull(op, "op");
		_operations.put(op.getUuid(), op);
		return op;
	}

	/** Returns all operations in this model. */
	public Collection<UmlOperation> getOperations() {
		return Collections.unmodifiableCollection(_operations.values());
	}

	// ----------------- associations --------------------

	/** Intended to be called by {@link UmlClass} and tests only. */
	UmlAssociation addAssociation(UmlAssociation a) {
		Util.ensureNotNull(a, "a");
		_associations.put(a.getUuid(), a);
		return a;
	}

	/** Returns all associations in this model. */
	public Collection<UmlAssociation> getAssociations() {
		return Collections.unmodifiableCollection(_associations.values());
	}

	/**
	 * Returns all associations involving classes from owners <code>wg</code>.
	 *
	 * @param wgs
	 *            one or more owners.
	 * @param kinds
	 *            one or more association kinds.
	 * @param includeNormative
	 *            whether to include normative associations.
	 * @param includeInformative
	 *            whether to include informative associations.
	 */
	public Collection<UmlAssociation> findAssociations(EnumSet<OwningWg> wgs,
			EnumSet<UmlAssociationEnd.Kind> kinds, boolean includeNormative,
			boolean includeInformative) {
		Collection<UmlAssociation> result = new LinkedHashSet<UmlAssociation>();
		for (UmlAssociation assoc : _associations.values()) {
			for (OwningWg wg : wgs) {
				if (assoc.involvesWg(wg) && kinds.contains(assoc.getKind())) {
					if (includeNormative && !assoc.isInformative()
							|| includeInformative && assoc.isInformative()) {
						result.add(assoc);
					}
				}
			}
		}
		return result;
	}

	/** Returns all associations that are mappings between models of different natures. */
	public Collection<UmlAssociation> findCimNoncimAssociations() {
		Collection<UmlAssociation> result = new LinkedHashSet<UmlAssociation>();
		for (UmlAssociation assoc : _associations.values()) {
			if (assoc.isMapping()) {
				result.add(assoc);
			}
		}
		return result;
	}

	// ----------------- explicit (hand-drawn) dependencies --------------------

	/** Intended to be called by {@link UmlStructure} and tests only. */
	UmlDependency addDependency(UmlDependency dep) {
		Util.ensureNotNull(dep, "d");
		_dependencies.put(dep.getUuid(), dep);
		return dep;
	}

	/** Returns all explicit (hand-drawn) dependencies in this model. */
	public Collection<UmlDependency> getDependencies() {
		return Collections.unmodifiableCollection(_dependencies.values());
	}

	// ----------------- diagrams --------------------

	/** Intended to be called by {@link UmlStructure} and tests only. */
	UmlDiagram addDiagram(UmlDiagram dia) {
		Util.ensureNotNull(dia, "dia");
		_diagrams.put(dia.getUuid(), dia);
		return dia;
	}

	/** Returns all diagrams in this model. */
	public Collection<UmlDiagram> getDiagrams() {
		return Collections.unmodifiableCollection(_diagrams.values());
	}

	/**
	 * Returns all diagrams whose container name matches <code>containerName</code> and with name
	 * <code>name</code>, with the specified options applied.
	 *
	 * @param containerName
	 *            name of the diagram's container.
	 * @param name
	 *            name of the diagram.
	 * @param includeOnPackage
	 *            includes diagrams defined on packages.
	 * @param includeOnClass
	 *            includes diagrams defined on classes.
	 */
	public Collection<UmlDiagram> findDiagrams(String containerName, String name,
			boolean includeOnPackage, boolean includeOnClass) {
		Collection<UmlDiagram> result = new LinkedHashSet<UmlDiagram>();
		for (UmlDiagram d : _diagrams.values()) {
			UmlObject container = d.getContainer();
			if (name.equals(d.getName()) && containerName.equals(container.getName())) {
				if ((container instanceof UmlClass && includeOnClass)
						|| (container instanceof UmlPackage && includeOnPackage)) {
					result.add(d);
				}
			}
		}
		return result;
	}

	// ----------------- tags --------------------

	private void saveTags(Collection<? extends UmlObject> objects) {
		for (UmlObject o : objects) {
			AbstractUmlObject.saveTags(o, _tags);
		}
	}

	private void saveAssocEndsTags(Collection<UmlAssociation> objects) {
		for (UmlAssociation o : objects) {
			AbstractUmlObject.saveTags(o.getSourceEnd(), _tags);
			AbstractUmlObject.saveTags(o.getTargetEnd(), _tags);
		}
	}

	public Map<String, Set<UmlObject>> getTags() {
		if (_tags == null) {
			_tags = new LinkedHashMap<String, Set<UmlObject>>();
			saveTags(_packages.values());
			saveTags(_classes.values());
			saveTags(_attributes.values());
			saveTags(_associations.values());
			saveAssocEndsTags(_associations.values());
			saveTags(_dependencies.values());
			saveTags(_operations.values());
			saveTags(_diagrams.values());
		}
		return Collections.unmodifiableMap(_tags);
	}

	// ========================================================================

	/** Returns comma-separated pairs {modelPackageName nature}. */
	public String getModelNamesWithNature() {
		StringBuilder result = new StringBuilder();
		List<UmlPackage> mps = new ArrayList<UmlPackage>(_modelPackages);
		for (int i = 0; i < mps.size(); ++i) {
			UmlPackage mp = mps.get(i);
			result.append(mp.getName()).append(" (").append(mp.getNature()).append(")");
			if (i < mps.size() - 1) {
				result.append(", ");
			}
		}
		return result.toString();
	}

	/** Returns model packages of specified nature. */
	public Collection<UmlPackage> getModelPackages(EnumSet<Nature> natures) {
		Util.ensureNotNull(natures, "natures");

		Collection<UmlPackage> result = new LinkedHashSet<UmlPackage>();
		for (UmlPackage m : _modelPackages) {
			if (natures.contains(m.getNature())) {
				result.add(m);
			}
		}
		return result;
	}

	/** Returns top packages for specified owners. */
	public Collection<UmlPackage> getTopPackages(EnumSet<OwningWg> wgs) {
		Util.ensureNotNull(wgs, "wgs");

		Collection<UmlPackage> result = new LinkedHashSet<UmlPackage>();
		for (UmlPackage m : _modelPackages) {
			for (UmlPackage top : m.getChildPackages()) {
				if (wgs.contains(top.getOwner())) {
					result.add(top);
				}
			}
		}
		return result;
	}

	/** Returns packages that have namespace info for specified owners. TODO: tests */
	public Collection<UmlPackage> getNamespacePackages(EnumSet<OwningWg> wgs) {
		Util.ensureNotNull(wgs, "wgs");

		Collection<UmlPackage> result = new LinkedHashSet<UmlPackage>();
		for (UmlPackage p : _packages.values()) {
			if (wgs.contains(p.getOwner()) && p.getNamespaceInfo() != null) {
				result.add(p);
			}
		}
		return result;
	}

	/** Returns version informations for top-level packages of specified owners. */
	public Collection<VersionInfo> getVersionInfos(EnumSet<OwningWg> wgs) {
		Util.ensureNotNull(wgs, "wgs");

		Collection<VersionInfo> result = new LinkedHashSet<VersionInfo>();
		Collection<UmlPackage> topPackages = getTopPackages(wgs);
		for (UmlPackage m : topPackages) {
			if (m.getVersionInfo() != null) {
				result.add(m.getVersionInfo());
			}
		}
		return result;
	}

	/** Returns non-null namespace informations for all packages. FIXME: tests */
	public Collection<NamespaceInfo> getNamespaceInfos() {
		Collection<NamespaceInfo> result = new LinkedHashSet<NamespaceInfo>();
		Collection<UmlPackage> packages = getNamespacePackages(EnumSet.allOf(OwningWg.class));
		for (UmlPackage p : packages) {
			result.add(p.getNamespaceInfo());
		}
		return result;
	}

	@Override
	public String toString() {
		List<String> result = new ArrayList<String>();
		result.add(String.format("%d models:%s", Integer.valueOf(_modelPackages.size()), Util.NL));
		for (UmlPackage p : _modelPackages) {
			result.add(String.format("%s%s", p.toString(), Util.NL));
			result.add(String.format("   %d top packages:%s",
					Integer.valueOf(p.getChildPackages().size()), Util.NL));
			for (UmlPackage cp : p.getChildPackages()) {
				result.add(String.format("   %s%s", cp.toString(), Util.NL));
			}
		}
		return result.toString();
	}
}
