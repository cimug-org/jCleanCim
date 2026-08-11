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

package org.tanjakostic.jcleancim.builder.ea;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.tanjakostic.jcleancim.builder.AbstractModelBuilder;
import org.tanjakostic.jcleancim.builder.UmlObjectBuilder;
import org.tanjakostic.jcleancim.common.Config;
import org.tanjakostic.jcleancim.model.UmlModel;
import org.tanjakostic.jcleancim.model.UmlPackage;
import org.tanjakostic.jcleancim.util.ApplicationException;
import org.tanjakostic.jcleancim.util.ProgrammerErrorException;
import org.tanjakostic.jcleancim.util.Util;

/**
 * Class that wraps the EA repository; currently supports a single root (in the
 * EA project browser), i.e., if there are more than one roots, all but the
 * first will be ignored.
 * <p>
 *
 * @param <P> Type for package data
 * @param <S> Type for element as source
 * @author tatjana.kostic@ieee.org
 * @version $Id: EaModelBuilder.java 21 2019-08-12 15:44:50Z dev978 $
 */
abstract public class EaModelBuilder<P, S> extends AbstractModelBuilder {
	private static final Logger _logger = Logger.getLogger(EaModelBuilder.class.getName());

	static final String DUPLICATE = "DUPLICATE";

	private final List<PackageBuilder<?, ?, ?, ?, ?, ?>> _eaModels = new ArrayList<PackageBuilder<?, ?, ?, ?, ?, ?>>();

	// these are the content of the model as we build it; in the end, the full model
	private final Map<Integer, PackageBuilder<?, ?, ?, ?, ?, ?>> _packages = new LinkedHashMap<Integer, PackageBuilder<?, ?, ?, ?, ?, ?>>();
	private final Map<Integer, ClassBuilder<?, ?, ?, ?, ?, ?>> _classes = new LinkedHashMap<Integer, ClassBuilder<?, ?, ?, ?, ?, ?>>();
	private final Map<Integer, AttributeBuilder<?, ?>> _attributes = new LinkedHashMap<Integer, AttributeBuilder<?, ?>>();
	private final Map<Integer, AssociationBuilder<?, ?>> _associations = new LinkedHashMap<Integer, AssociationBuilder<?, ?>>();
	private final Map<Integer, DependencyBuilder<?, ?>> _dependencies = new LinkedHashMap<Integer, DependencyBuilder<?, ?>>();
	private final Map<Integer, OperationBuilder<?, ?>> _operations = new LinkedHashMap<Integer, OperationBuilder<?, ?>>();
	private final Map<Integer, DiagramBuilder<?>> _diagrams = new LinkedHashMap<Integer, DiagramBuilder<?>>();

	private final Map<String, UmlObjectBuilder<?>> _guidNonDuplicates = new LinkedHashMap<String, UmlObjectBuilder<?>>();
	private final Map<String, List<UmlObjectBuilder<?>>> _guidDuplicates = new LinkedHashMap<String, List<UmlObjectBuilder<?>>>();

	/**
	 * Constructor.
	 *
	 * @param cfg
	 */
	protected EaModelBuilder(Config cfg) {
		super(cfg);

		Util.logSubtitle(Level.INFO, "initialising EA builder...");
		long start = System.currentTimeMillis();

		String version = initRepoAndGetVersion(getCfg().getModelFileAbsPath());

		_logger.info(String.format("EA version: build %s", version));
		Util.logCompletion(Level.INFO, "initialised EA builder.", start, getCfg().isAppSkipTiming());
	}

	abstract protected String initRepoAndGetVersion(String modelFileAbsPath);

	// ----------------- repository lifecycle -----------------------

	private void openModel() throws ApplicationException {
		Util.logSubtitle(Level.INFO, String.format("opening EA file '%s'...", getCfg().getModelFileAbsPath()));
		long start = System.currentTimeMillis();

		openRepo(getCfg().getModelFileAbsPath());

		Util.logCompletion(Level.INFO, "opened EA file.", start, getCfg().isAppSkipTiming());
	}

	abstract protected void openRepo(String modelFileAbsPath) throws ApplicationException;

	abstract protected void bulkLoad() throws ApplicationException;

	private void closeModel() throws ApplicationException {
		Util.logSubtitle(Level.INFO, String.format("closing EA file '%s'...", getCfg().getModelFileAbsPath()));
		long start = System.currentTimeMillis();

		try {
			closeRepo();
		} finally {
			Util.logCompletion(Level.INFO, "closed EA file.", start, getCfg().isAppSkipTiming());
		}
	}

	abstract protected void closeRepo() throws ApplicationException;

	// ---------------------------------

	/**
	 * Creates the in-memory builders with the data from EA repository and returns
	 * the UUID of the model root (needed for XMI export, if enabled by
	 * configuration).
	 * <p>
	 * Ensure you call {@link #linkBuilders()} after this one.
	 *
	 * @throws ApplicationException if model file is empty, or if there is a root in
	 *                              the model but without packages.
	 */
	private String populateBuilders() throws ApplicationException {
		Util.logSubtitle(Level.INFO, getLogSubtitleStartPopulateBuilders());
		long start = System.currentTimeMillis();

		P root = getFirstRoot();
		String rootUuid = fetchPackageGuid(root);

		List<P> models = getModels(root);
		if (models.size() == 0) {
			throw new ApplicationException("Root has no models.");
		}

		for (P m : models) {
			PackageBuilder<?, ?, ?, ?, ?, ?> modelPackage = createModelPackage(m);
			_eaModels.add(modelPackage);
		}

		if (!_guidDuplicates.isEmpty()) {
			_logger.error("+++ EA consistency error - duplicate EA GUID:");
			for (Entry<String, List<UmlObjectBuilder<?>>> dups : _guidDuplicates.entrySet()) {
				_logger.error("    " + dups.getKey());
				for (UmlObjectBuilder<?> builder : dups.getValue()) {
					_logger.error("    " + builder.toString());
				}
			}
		}

		Util.logCompletion(Level.INFO, getLogSubtitleEndPopulateBuilders(), start, getCfg().isAppSkipTiming());
		return rootUuid;
	}

	abstract protected String getLogSubtitleStartPopulateBuilders();

	abstract protected String getLogSubtitleEndPopulateBuilders();

	abstract protected PackageBuilder<?, ?, ?, ?, ?, ?> createModelPackage(P m);

	abstract protected P getFirstRoot() throws ApplicationException;

	abstract protected String fetchPackageGuid(P inData);

	abstract protected List<P> getModels(P root);

	protected static void assertModelNotEmptyWarnIfMultipleRoots(int count) throws ApplicationException {
		if (count == 0) {
			throw new ApplicationException("EA repository contains no root.");
		}
		if (count > 1) {
			_logger.warn(String.format("EA repository contains %d roots - all but the first will be ignored.",
					Integer.valueOf(count)));
		}
	}

	// -------------------

	/**
	 * Below is what needs to be processed after all class and packages builders
	 * have been created.
	 */
	private void linkBuilders() {
		long start = System.currentTimeMillis();
		Util.logSubtitle(Level.INFO, "linking builders...");

		_logger.info("  assigning type to attributes ...");
		for (AttributeBuilder<?, ?> attr : _attributes.values()) {
			attr.assignType(_classes);
		}
		_logger.info("  assigning type for operations' parameters and exceptions ...");
		for (OperationBuilder<?, ?> op : _operations.values()) {
			op.assignTypeToParametersAndExceptions(this);
		}

		_logger.info("  cross-checking dependencies ...");
		for (DependencyBuilder<?, ?> dep : _dependencies.values()) {
			dep.ensureClass2ClassOrPackage2PackageDependenciesEndsInitialised();
		}
		_logger.info("  cross-checking associations ...");
		for (AssociationBuilder<?, ?> assoc : _associations.values()) {
			assoc.ensureAssociationsOfEndClassesInitialised();
		}

		Util.logCompletion(Level.INFO, "linked builders.", start, getCfg().isAppSkipTiming());
	}

	/**
	 * Creates in-memory model from builders.
	 */
	private UmlModel createInMemoryModel(Config cfg) {
		String withDiagrams = getCfg().isDocgenModelOn() ? " and exporting normative diagrams" : "";
		Util.logSubtitle(Level.INFO, String.format("creating in-memory model%s...", withDiagrams));
		long start = System.currentTimeMillis();

		UmlModel resultModel = new UmlModel(cfg);

		_logger.info("  creating in-memory package structure ...");
		for (PackageBuilder<?, ?, ?, ?, ?, ?> mb : _eaModels) {
			mb.build(resultModel);
		}
		_logger.info("  creating in-memory root classes and enum literals ...");
		Set<ClassBuilder<?, ?, ?, ?, ?, ?>> roots = new LinkedHashSet<ClassBuilder<?, ?, ?, ?, ?, ?>>();
		Set<ClassBuilder<?, ?, ?, ?, ?, ?>> nonRoots = new LinkedHashSet<ClassBuilder<?, ?, ?, ?, ?, ?>>();
		for (ClassBuilder<?, ?, ?, ?, ?, ?> cb : _classes.values()) {
			if (cb.getSuperclasses().isEmpty()) {
				cb.build();
				roots.add(cb);
			} else {
				nonRoots.add(cb);
			}
		}
		_logger.info("  creating in-memory sub-classes recursively ...");
		for (ClassBuilder<?, ?, ?, ?, ?, ?> cb : roots) {
			buildSubclasses(cb, nonRoots);
		}
		if (!nonRoots.isEmpty()) {
			throw new ProgrammerErrorException(nonRoots.size() + " classes not built!");
		}
		_logger.info("  reordering in-memory classes in packages ...");
		for (PackageBuilder<?, ?, ?, ?, ?, ?> pb : _packages.values()) {
			UmlPackage p = pb.getResult();
			p.orderClasses(pb.getClassUuids());
		}
		_logger.info("  creating in-memory class' attributes, operations and constraints ...");
		for (ClassBuilder<?, ?, ?, ?, ?, ?> cb : _classes.values()) {
			for (AttributeBuilder<?, ?> ab : cb.getAttributes()) {
				if (!ab.isLiteral()) {
					ab.build();
				}
			}
			for (ConstraintBuilder constrb : cb.getConstraints().values()) {
				constrb.build();
			}
			for (OperationBuilder<?, ?> ob : cb.getOperations()) {
				ob.build();
			}
		}
		_logger.info("  creating in-memory associations ...");
		for (AssociationBuilder<?, ?> ab : _associations.values()) {
			ab.build();
		}
		_logger.info("  creating in-memory dependencies ...");
		for (DependencyBuilder<?, ?> db : _dependencies.values()) {
			db.build();
		}

		withDiagrams = getCfg().isDocgenModelOn() ? " and exported normative diagrams" : "";
		Util.logCompletion(Level.INFO, String.format("created in-memory model%s", withDiagrams), start,
				getCfg().isAppSkipTiming());
		return resultModel;
	}

	/** Recursive. */
	private void buildSubclasses(ClassBuilder<?, ?, ?, ?, ?, ?> cb, Set<ClassBuilder<?, ?, ?, ?, ?, ?>> nonRoots) {
		for (ClassBuilder<?, ?, ?, ?, ?, ?> sub : cb.getSubclasses()) {
			sub.build();
			nonRoots.remove(sub);
			buildSubclasses(sub, nonRoots);
		}
	}

	// ----------------

	private void validateGuid(UmlObjectBuilder<?> builder) {
		String uuid = builder.getObjData().getUuid();
		if (!_guidNonDuplicates.containsKey(uuid)) {
			_guidNonDuplicates.put(uuid, builder);
			return;
		}

		// found somebody with the same GUID as the received builder; need to store both
		UmlObjectBuilder<?> previouslyStoredBuilder = _guidNonDuplicates.get(uuid);
		if (!_guidDuplicates.containsKey(uuid)) {
			_guidDuplicates.put(uuid, new ArrayList<UmlObjectBuilder<?>>());
			_guidDuplicates.get(uuid).add(previouslyStoredBuilder);
		}
		_guidDuplicates.get(uuid).add(builder);
	}

	public final void addPackage(PackageBuilder<?, ?, ?, ?, ?, ?> builder) {
		validateGuid(builder);
		_packages.put(builder.getObjData().getId(), builder);
	}

	public final void addDependency(DependencyBuilder<?, ?> builder) {
		validateGuid(builder);
		_dependencies.put(builder.getObjData().getId(), builder);
	}

	public final DependencyBuilder<?, ?> findDependency(Integer depId) {
		return _dependencies.get(depId);
	}

	public final void addClass(ClassBuilder<?, ?, ?, ?, ?, ?> builder) {
		validateGuid(builder);
		_classes.put(builder.getObjData().getId(), builder);
	}

	public final ClassBuilder<?, ?, ?, ?, ?, ?> findClass(Integer typeId) {
		return _classes.get(typeId);
	}

	/** Returns the EA type for object ID. */
	abstract public String findElementType(Integer id);

	abstract public String findElementTypeAndName(Integer id);

	public boolean isEaElementClass(Integer objId) {
		return ClassBuilder.isClassOrEaInterface(findElementType(objId));
	}

	public boolean isEaElementPackage(Integer objId) {
		return PackageBuilder.isEaPackage(findElementType(objId));
	}

	// FIXME: (used for operations and their parameters) handle multiple classes
	// found for name
	public final ClassBuilder<?, ?, ?, ?, ?, ?> findClass(String name) {
		for (ClassBuilder<?, ?, ?, ?, ?, ?> clazz : _classes.values()) {
			if (name.equals(clazz.getObjData().getName())) {
				return clazz;
			}
		}
		return null;
	}

	public final void addAssociation(AssociationBuilder<?, ?> builder) {
		validateGuid(builder);
		_associations.put(builder.getObjData().getId(), builder);
	}

	public final AssociationBuilder<?, ?> findAssociation(Integer assocId) {
		return _associations.get(assocId);
	}

	public final void addDiagram(DiagramBuilder<?> builder) {
		validateGuid(builder);
		_diagrams.put(builder.getObjData().getId(), builder);
	}

	public final void addAttribute(AttributeBuilder<?, ?> builder) {
		validateGuid(builder);
		_attributes.put(builder.getObjData().getId(), builder);
	}

	public final void addOperation(OperationBuilder<?, ?> builder) {
		validateGuid(builder);
		_operations.put(builder.getObjData().getId(), builder);
	}

	/** Returns tables resulting from the bulk initialisation (if applicable). */
	abstract public EaTables getTables() throws UnsupportedOperationException;

	// ===== Impl. of org.tanjakostic.jcleancim.builder.AbstractModelBuilder methods
	// =====

	/**
	 * {@inheritDoc}
	 * <p>
	 * This implementation is mainly working with EA model files. opens the EA model
	 * file, reads in all it needs, closes the EA model file and creates the
	 * in-memory model.
	 */
	@Override
	public UmlModel build() throws ApplicationException {
		openModel();
		bulkLoad();
		String rootGuid = populateBuilders();

		if (getCfg().isXmiexportOn()) {
			getXMIExporter().exportToXMIs(rootGuid);
		}

		// For a clean design, we were exporting diagrams in populateBuilders() above,
		// but we were
		// exporting more than necessary, because the builder does not have the logic to
		// decide
		// whether a package is informative or not. Desperate by the time it takes, we
		// broke the
		// clean design: now we postpone the diagram export, if applicable, to the
		// actual building
		// where we can use methods of already constructed UmlPackage, in
		// createInMemoryModel(), and
		// we close the EA model immediately after that.
		if (!getCfg().isDocgenOn()) {
			closeModel(); // we don't need EA repository anymore
		}
		linkBuilders();

		UmlModel inMemoryModel = createInMemoryModel(getCfg());
		if (getCfg().isDocgenOn()) {
			closeModel();
		}

		return inMemoryModel;
	}
}
