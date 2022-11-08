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
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;
import org.tanjakostic.jcleancim.common.Nature;
import org.tanjakostic.jcleancim.common.OwningWg;
import org.tanjakostic.jcleancim.util.Util;

/**
 * UML package and its sub-packages hold the content of the model. In addition to UML features
 * specific to packages, it inherits implementation for features common with UML classes from
 * {@link UmlStructure} (so we avoid code duplication).
 * <p>
 * Implementation note: We distinguish among hierarchy levels of packages by their {@link Kind}, to
 * ensure we can manage the actual UML model repositories in a loosely coupled way. This also allows
 * us to filter (opt in/out) parts of the in-memory model for e.g. validation, statistics or
 * document generation.
 * <p>
 * A cleaner design would be to effectively create subclasses instead of using the above kinds, but
 * it would be overkill for minor differences in functionality per kind.
 *
 * @author tatjana.kostic@ieee.org
 * @version $Id: UmlPackage.java 34 2019-12-20 18:37:17Z dev978 $
 */
public class UmlPackage extends UmlStructure {
	private static final Logger _logger = Logger.getLogger(UmlPackage.class.getName());

	/**
	 * Kind of UML package, reflecting hierarchical package containment and common properties for
	 * the model content below a given level.
	 *
	 * @author tatjana.kostic@ieee.org
	 * @version $Id: UmlPackage.java 34 2019-12-20 18:37:17Z dev978 $
	 */
	public enum Kind implements UmlKind {
		/**
		 * Direct child of a root in UML repository (level 2); we have CIM and IEC 61850 model(s).
		 */
		MODEL("model", "model package", "Package", "model package (with nature)"),

		/** This is like {@link #MODEL} (level 2), but reserved for use by {@link UmlModel}. */
		NULL_MODEL("null model", "null model package", "Package",
				"model package (for invalid classes)"),

		/** Direct child of the {@link #MODEL} (level 3). We have WGs as owners of these. */
		TOP("top", "top package", "Package", "top package (per WG)"),

		/** Any direct or deep child of the {@link #TOP} (level 4+). */
		PACKAGE("package", "package", "Package", "sub-package (any below top)");

		private Kind(String value, String label, String tag, String desc) {
			_value = value;
			_label = label;
			_tag = tag;
			_desc = desc;
		}

		private final String _value;
		private final String _label;
		private final String _tag;
		private final String _desc;

		@Override
		public String getValue() {
			return _value;
		}

		@Override
		public String getLabel() {
			return _label;
		}

		@Override
		public String getTag() {
			return _tag;
		}

		@Override
		public String getDesc() {
			return _desc;
		}
	}

	/**
	 * Returns all available classifications (kinds) for packages.
	 *
	 * @param nature
	 *            ignored in this method
	 */
	public static List<UmlKind> getKinds(Nature nature) {
		List<UmlKind> result = new ArrayList<UmlKind>();
		for (UmlKind kind : Kind.values()) {
			result.add(kind);
		}
		return result;
	}

	/** Allowed tags for any package. */
	private static final List<String> ANY_TAGS = Arrays.asList(UML.TVN_nsuri, UML.TVN_nsprefix);

	/** Allowed tags for IEC 61850 packages. */
	private static final List<String> IEC61850_TAGS = ANY_TAGS;

	/** Allowed tags for CIM packages. */
	private static final List<String> CIM_TAGS = ANY_TAGS;

	/**
	 * Data from the UML model repository specific to {@link UmlPackage}.
	 *
	 * @author tatjana.kostic@ieee.org
	 * @version $Id: UmlPackage.java 34 2019-12-20 18:37:17Z dev978 $
	 */
	public static class Data extends UmlStructure.Data {
		private static final Data DEFAULT = new Data();

		/** Returns an empty instance. */
		public static Data empty() {
			return DEFAULT;
		}

		/** Constructor. */
		private Data() {
			this(UmlStructure.Data.empty());
		}

		/** Constructor. */
		public Data(UmlStructure.Data data) {
			super(data.isSelfDependent());
		}
	}

	private final UmlPackage _containingPackage;
	private final UmlModel _model;
	@SuppressWarnings("unused")
	private final Data _data;

	private final Kind _kind;
	private final int _depth;
	private final OwningWg _owner;
	private final Nature _nature;
	private final boolean _informative;
	private VersionInfo _versionInfo; // deduced
	private NamespaceInfo _namespaceInfo; // deduced; need instance variable for cross referencing

	private final Set<UmlClass> _classes = new LinkedHashSet<UmlClass>();
	private final Set<UmlPackage> _childPackages = new LinkedHashSet<UmlPackage>();

	/** Constructs minimal model package - useful for creation from profiles and testing. */
	public static UmlPackage basic(UmlModel model, String name) {
		return new UmlPackage(model, new UmlObjectData(name), Data.empty());
	}

	/** Constructs minimal non-model package - useful for creation from profiles and testing. */
	public static UmlPackage basic(UmlPackage containingPackage, String name) {
		return new UmlPackage(containingPackage, new UmlObjectData(name), Data.empty());
	}

	/**
	 * Constructs minimal non-model package with stereotype(s) - useful for creation from profiles
	 * and testing.
	 */
	public static UmlPackage basic(UmlPackage containingPackage, String name, String stereotype) {
		return new UmlPackage(containingPackage,
				new UmlObjectData(name, new UmlStereotype(stereotype)), Data.empty());
	}

	/**
	 * Creates a model package (e.g. TC57CIM, TC57CIMProfiles and IEC61850Domain), and adds itself
	 * to <code>model</code>. After creating this object, you may want to add tagged values, classes
	 * and child packages (as well as other objects - see
	 * {@link UmlStructure#UmlStructure(UmlObjectData, UmlStructure.Data)}).
	 *
	 * @param model
	 *            parent UML model
	 * @param objData
	 *            common data for every {@link UmlObject}s.
	 * @param data
	 *            data proper to {@link UmlPackage}.
	 */
	public UmlPackage(UmlModel model, UmlObjectData objData, Data data) {
		this(model, null, true, objData, data);
	}

	/**
	 * Creates a top-level or a regular package, and adds itself to <code>containingPackage</code>.
	 * After creating this object, you may want to add tagged values, classes and child packages (as
	 * well as other objects - see
	 * {@link UmlStructure#UmlStructure(UmlObjectData, UmlStructure.Data)}).
	 *
	 * @param containingPackage
	 *            parent UML package
	 * @param objData
	 *            common data for every {@link UmlObject}.
	 * @param data
	 *            data proper to {@link UmlPackage}.
	 */
	public UmlPackage(UmlPackage containingPackage, UmlObjectData objData, Data data) {
		this(null, containingPackage, false, objData, data);
	}

	/**
	 * Reserved for use by {@link UmlModel} only: Creates the "null" model package of
	 * <code>nature</code>; it is the model's responsibility to add this instance where it wants to
	 * (i.e., this constructor has no side effects on <code>model</code>).
	 *
	 * @param model
	 *            parent UML model
	 * @param nature
	 *            nature for this model package.
	 */
	UmlPackage(UmlModel model, Nature nature) {
		super(new UmlObjectData("Null" + nature), Data.empty());

		Util.ensureNotNull(model, "model");
		Util.ensureNotNull(nature, "nature");

		_model = model;
		_containingPackage = null;
		_data = Data.empty();

		_kind = Kind.NULL_MODEL;
		_depth = -1;
		_nature = nature;
		_owner = nature == Nature.CIM ? OwningWg.OTHER_CIM : OwningWg.OTHER_IEC61850;
		_informative = false;

		_logger.trace("---- created " + getKind().getLabel() + " "
				+ Util.getIndentSpaces(getDepth()) + getName());
	}

	private UmlPackage(UmlModel model, UmlPackage containingPackage, boolean isModelPackage,
			UmlObjectData objData, Data data) {
		super(objData, data);

		if (isModelPackage) {
			Util.ensureNotNull(model, "model");
		} else {
			Util.ensureNotNull(containingPackage, "containingPackage");
		}
		Util.ensureNotNull(data, "data");

		_model = (model != null) ? model : containingPackage.getModel();
		_containingPackage = containingPackage;
		_data = data;

		if (model != null) {
			_kind = Kind.MODEL;
			_depth = -1;
			_nature = _model.getCfg().getIec61850NaturePackages().contains(getName())
					? Nature.IEC61850
					: Nature.CIM;
			_owner = (getNature() == Nature.CIM) ? OwningWg.OTHER_CIM : OwningWg.OTHER_IEC61850;

			_model.addPackage(this);
		} else if (containingPackage.getKind() == Kind.MODEL) {
			_kind = Kind.TOP;
			_depth = 0;
			_nature = containingPackage.getNature();
			OwningWg ownerForTopPackage = OwningWg.getOwnerForTopPackage(getName());
			if (ownerForTopPackage == null) {
				_owner = (getNature() == Nature.CIM) ? OwningWg.OTHER_CIM : OwningWg.OTHER_IEC61850;
			} else {
				_owner = ownerForTopPackage;
			}

			_containingPackage.addChildPackage(this);
		} else {
			_kind = Kind.PACKAGE;
			_depth = containingPackage.getDepth() + 1;
			_nature = containingPackage.getNature();
			_owner = containingPackage.getOwner();

			_containingPackage.addChildPackage(this);
		}

		_informative = determineIsInformative(this);

		_logger.trace("---- created " + getKind().getLabel() + " "
				+ Util.getIndentSpaces(getDepth()) + getName());
	}

	private boolean determineIsInformative(UmlPackage p) {
		String name = p.getName();
		UmlPackage container = p.getContainingPackage();
		if (container == null) {
			return false;
		}
		if (super.isInformative() || name.startsWith(UML.INF_PREFIX)
				|| name.equals(UML.DetailedDiagrams)) {
			return true;
		}
		if (container.isInformative()) {
			return true;
		}
		return isDocDiagramPackage();
	}

	private boolean isDocDiagramPackage() {
		return getName()
				.equals(String.format(UML.DOC_FORMAT_STRING, getContainingPackage().getName()));
	}

	public boolean shouldExportDiagrams() {
		if (UML.DetailedDiagrams.equals(getName())) {
			return false; // never export detailed diagrams
		}
		if (getModel().getCfg().isDocgenModelOn()) {
			if (!isInformative() || isDocDiagramPackage()) {
				return true; // if generating doc, always export normative and docum. diagrams
			}
			if (isInformative() && getModel().getCfg().isDocgenIncludeInformative()) {
				return true; // if generating doc and configured so, export informative as well
			}
		}
		return false;
	}

	/** Returns the containing package, null for {@link Kind#MODEL}. */
	public UmlPackage getContainingPackage() {
		return _containingPackage;
	}

	/** Returns whether this package is a top package (owned by a WG). */
	public boolean isTop() {
		return getKind() == Kind.TOP;
	}

	/**
	 * Returns the depth of this package, relative to the top-level package (i.e., for model package
	 * returns -1, for top-level package returns 0, and for all other packages returns positive
	 * offset relative to top-level package). Used for having proper headings depth for document
	 * generation (and optionally, for indentation in logs). For model package returns -1, for
	 * top-level package 0, and for all other packages
	 */
	public int getDepth() {
		return _depth;
	}

	// ------------------------ classes -----------------------------

	/**
	 * Adds the non-null class to this package and to the model (if the class' model is not
	 * {@link Kind#NULL_MODEL} ), and returns the same object. No-op in case <code>clazz</code> has
	 * already been added. Reserved for use by {@link UmlClass} on its creation, and potentially by
	 * tests.
	 */
	UmlClass addClass(UmlClass clazz) {
		Util.ensureNotNull(clazz, "clazz");
		if (_classes.contains(clazz)) {
			_logger.warn(String.format("Class %s already in %s.", clazz.getQualifiedName(),
					getQualifiedName()));
			return clazz;
		}

		_classes.add(clazz);
		if (getKind() != Kind.NULL_MODEL) {
			getModel().addClass(clazz);
		}

		return clazz;
	}

	/** Returns all classes in this package. */
	public Set<UmlClass> getClasses() {
		return Collections.unmodifiableSet(_classes);
	}

	/** Orders classes in the order given in <code>uuids</code>. **/
	public void orderClasses(List<String> uuids) {
		Map<String, UmlClass> currentClasses = new LinkedHashMap<String, UmlClass>();
		for (UmlClass c : _classes) {
			currentClasses.put(c.getUuid(), c);
		}
		_classes.clear();
		for (String uuid : uuids) {
			_classes.add(currentClasses.get(uuid));
		}
	}

	// --------------------- (hand-drawn) dependencies - downcasting ----------------------

	/** Returns all classes that I depend on through an explicit UML dependency in the model. */
	public Collection<UmlPackage> collectDependencyEfferentPackages() {
		return downcast(collectDependencyEfferentStructures());
	}

	private static Collection<UmlPackage> downcast(Collection<UmlStructure> items) {
		Collection<UmlPackage> result = new LinkedHashSet<>();
		for (UmlStructure struct : items) {
			result.add((UmlPackage) struct);
		}
		return result;
	}

	// --------------------- version info --------------------

	/**
	 * (lazy loaded) Returns the version information in case the relevant version class is defined
	 * in the package, null otherwise. Logs error if more than one version class found and retains
	 * only one.
	 */
	public VersionInfo getVersionInfo() {
		if (_versionInfo == null) {
			UmlClass clazz = findVersionClass();
			if (clazz != null) {
				_versionInfo = new VersionInfo(clazz);
			}
		}
		return _versionInfo;
	}

	private UmlClass findVersionClass() {
		String expectedName = VersionInfo.getExpectedVersionClassName(getNature(), getName());
		Set<UmlClass> classes = AbstractUmlObject.findAllForName(getClasses(), expectedName);
		if (classes.isEmpty()) {
			return null;
		}
		if (classes.size() > 1) {
			_logger.error("Found multiple version classes '" + expectedName + "'; keeping one.");
		}
		return classes.iterator().next();
	}

	// --------------------- namespace info ---------------

	/**
	 * (lazy loaded) Returns the namespace information in case the relevant namespace class (for
	 * IEC61850) or version class (for CIM) is defined in the package, null otherwise. Logs error if
	 * more than one namespace class found and retains only one.
	 */
	public NamespaceInfo getNamespaceInfo() {
		if (_namespaceInfo == null) {
			if (getNature() == Nature.CIM) {
				VersionInfo versionInfo = getVersionInfo();
				if (versionInfo != null) {
					_namespaceInfo = NamespaceInfo.createCimInstance(versionInfo);
				}
			} else {
				UmlClass clazz = findNamespaceClass();
				if (clazz != null) {
					_namespaceInfo = NamespaceInfo.createIec61850Instance(clazz);
				}
			}
			// by here, we have potentially created an instance, but now need to add dependencies:
			if (_namespaceInfo != null) {
				deduceAndFillNamespaceInfoDependencies();
			}
		}
		return _namespaceInfo;
	}

	private UmlClass findNamespaceClass() {
		String expectedName = NamespaceInfo.getExpectedNamespaceClassName(getNature(), getName());
		Set<UmlClass> classes = AbstractUmlObject.findAllForName(getClasses(), expectedName);
		if (classes.isEmpty()) {
			return null;
		}
		if (classes.size() > 1) {
			_logger.error("Found multiple namespace classes '" + expectedName + "'; keeping one.");
		}
		return classes.iterator().next();
	}

	/**
	 * Relies on collected own and parent's dependencies: Calculates and returns namespace
	 * information for this package, null if package does not contain a namespace class.
	 */
	private void deduceAndFillNamespaceInfoDependencies() {
		Collection<UmlStructure> targetPackages = collectMyAndParentsDependencyEfferentStructures();
		_logger.debug("deduceAndFillNamespaceInfoDependencies for " + this.getQualifiedName()
				+ ", targetPackages = " + AbstractUmlObject.collectNames(targetPackages));

		for (UmlStructure targetPackage : targetPackages) {
			if (targetPackage == this) {
				throw new RuntimeException(
						"+++ programmer error: target==this '" + this.getQualifiedName() + "'.");
			}
			NamespaceInfo otherNs = ((UmlPackage) targetPackage).getNamespaceInfo();
			if (otherNs != null) {
				_namespaceInfo.addDependency(otherNs);
			}
		}
	}

	// --------------------- packages -------------------

	/**
	 * Adds the non-null child package to this package and to the model, and returns the same
	 * object. No-op in case <code>child</code> has already been added.
	 */
	private UmlPackage addChildPackage(UmlPackage child) {
		Util.ensureNotNull(child, "child");
		if (_childPackages.contains(child)) {
			_logger.warn(String.format("Package %s already in %s.", child.getQualifiedName(),
					getQualifiedName()));
			return child;
		}

		_childPackages.add(child);
		getModel().addPackage(child);

		return child;
	}

	/** Returns all direct sub-packages. */
	public Set<UmlPackage> getChildPackages() {
		return Collections.unmodifiableSet(_childPackages);
	}

	/**
	 * Returns sub-packages with <code>name</code>. Normally, sub-packages should have name unique
	 * within the containing package. However some tools allow this anomaly and we need to support
	 * that kind of result (with a set returned).
	 */
	public Set<UmlPackage> getChildPackages(String name) {
		return AbstractUmlObject.findAllForName(_childPackages, name);
	}

	/**
	 * Returns whether anywhere below this package (recursively) there is a package called
	 * <code>packageName</code>. To include this package in the search, use
	 * {@link #isInOrUnderPackage(String)}.
	 */
	public boolean isUnderPackage(String packageName) {
		if (packageName == null) {
			return false;
		}
		if (getContainingPackage() == null) {
			return false;
		}
		if (packageName.equals(this.getName())) {
			return false;
		}
		if (packageName.equals(getContainingPackage().getName())) {
			return true;
		}
		return getContainingPackage().isUnderPackage(packageName);
	}

	/**
	 * Returns whether this package or anywhere below it (recursively) there is a package called
	 * <code>packageName</code>. To exclude this package from the search, use
	 * {@link #isUnderPackage(String)}.
	 */
	public boolean isInOrUnderPackage(String packageName) {
		if (packageName == null) {
			return false;
		}
		if (packageName.equals(getName())) {
			return true;
		}
		return isUnderPackage(packageName);
	}

	// =========== org.tanjakostic.jcleancim.model.UmlStructure ============

	/**
	 * {@inheritDoc}
	 * <p>
	 * Returns the model this package and all its recursive contents belongs to.
	 */
	@Override
	public UmlModel getModel() {
		return _model;
	}

	/**
	 * {@inheritDoc}
	 * <p>
	 * See {@link UmlPackage#getContainingPackage()}.
	 */
	@Override
	public UmlStructure getContainer() {
		return getContainingPackage();
	}

	// =========== org.tanjakostic.jcleancim.model.UmlObject ============

	@Override
	public OwningWg getOwner() {
		return _owner;
	}

	/**
	 * {@inheritDoc}
	 * <p>
	 * Returns own namespace initialised from tagged values if not empty. Otherwise, returns the
	 * namespace of the containing package, or empty namespace otherwise.
	 */
	@Override
	public Namespace getNamespace() {
		Namespace ns = initFromTags();
		if (ns != Namespace.EMPTY) {
			return ns;
		}
		if (getContainingPackage() == null) {
			return Namespace.EMPTY;
		}
		return getContainingPackage().getNamespace();
	}

	@Override
	public Nature getNature() {
		return _nature;
	}

	/**
	 * {@inheritDoc}
	 * <p>
	 * Model package is never informative.
	 * <p>
	 * Any other package is considered as informative if any of the following is true:
	 * <ul>
	 * <li>package stereotype includes {@value UmlStereotype#INFORMATIVE},
	 * <li>package name starts with {@link UML#INF_PREFIX},
	 * <li>package name is {@link UML#DetailedDiagrams},
	 * <li>any parent package (in the chain) is informative.
	 * </ul>
	 */
	@Override
	public boolean isInformative() {
		return _informative;
	}

	@Override
	public UmlKind getKind() {
		return _kind;
	}

	@Override
	public String getQualifiedName() {
		if (getKind() == Kind.MODEL) {
			return "/" + getName();
		}
		if (getKind() == Kind.NULL_MODEL) {
			return "~/" + getName();
		}
		return getContainingPackage().getName() + PACKAGE_SEPARATOR + getName();
	}

	@Override
	public Set<String> getPredefinedTagNames() {
		List<String> resultList = (getNature() == Nature.CIM) ? CIM_TAGS : IEC61850_TAGS;
		return new LinkedHashSet<String>(resultList);
	}

	// =========== java.lang.Object ============

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder(toShortString(true, true));
		if (getVersionInfo() != null) {
			sb.append(" (").append(getVersionInfo().toString()).append(")");
		}
		if (getNamespaceInfo() != null) {
			sb.append(" (").append(getNamespaceInfo().toString()).append(")");
		}
		sb.append(": ").append(getDiagrams().size()).append(" ").append("diagrams,");
		sb.append(" ").append(getClasses().size()).append(" ").append("classes,");
		sb.append(" ").append(getChildPackages().size()).append(" ").append("packages");
		List<String> efferentPackages = collectNames(collectDependencyEfferentStructures());
		if (efferentPackages.size() > 0) {
			sb.append(", ").append("efferent=" + efferentPackages);
		}
		List<String> afferentPackages = collectNames(collectDependencyEfferentStructures());
		if (afferentPackages.size() > 0) {
			sb.append(", ").append("afferent=" + afferentPackages);
		}
		if (getTaggedValues().size() != 0) {
			sb.append(", ").append("; tags=").append(getTaggedValues().toString());
		}
		return sb.toString();
	}
}
