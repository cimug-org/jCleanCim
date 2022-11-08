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

package org.tanjakostic.jcleancim.experimental.builder.xsd;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.tanjakostic.jcleancim.builder.AbstractModelBuilder;
import org.tanjakostic.jcleancim.builder.DiagramExporter;
import org.tanjakostic.jcleancim.builder.EmptyDiagramExporter;
import org.tanjakostic.jcleancim.builder.EmptyXMIExporter;
import org.tanjakostic.jcleancim.builder.XMIExporter;
import org.tanjakostic.jcleancim.common.Config;
import org.tanjakostic.jcleancim.common.OwningWg;
import org.tanjakostic.jcleancim.model.UmlModel;
import org.tanjakostic.jcleancim.model.UmlPackage;
import org.tanjakostic.jcleancim.util.ProgrammerErrorException;
import org.tanjakostic.jcleancim.util.Util;

/**
 * Builds in-memory model from profile files, as specified by configuration. Each profile is read
 * from one profile file and represented in the in-memory model as one model package.
 *
 * @author tatjana.kostic@ieee.org
 * @version $Id: ModelBuilderFromProfiles.java 21 2019-08-12 15:44:50Z dev978 $
 */
public class ModelBuilderFromProfiles extends AbstractModelBuilder {
	private static final Logger _logger = Logger
			.getLogger(ModelBuilderFromProfiles.class.getName());

	public static final String MODEL_PACKAGE_NAME = "TC57CIMProfiles";

	private final Map<OwningWg, List<Profile>> _profiles = new LinkedHashMap<OwningWg, List<Profile>>();

	public ModelBuilderFromProfiles(Config cfg) {
		super(cfg);
	}

	private void parseProfiles() {
		for (Map.Entry<OwningWg, List<File>> entry : getCfg().getProfileFiles().entrySet()) {
			OwningWg owner = entry.getKey();
			List<Profile> wgProfiles = new ArrayList<Profile>();
			_profiles.put(owner, wgProfiles);
			for (File f : entry.getValue()) {
				Profile profile = new Profile(getCfg(), f);
				if (!owner.name().equals(profile.getSubdirNames().get(0))) {
					throw new ProgrammerErrorException(String.format("Owner should"
							+ " be %s, profile says it is %s.", owner.name(), profile
							.getSubdirNames().get(0)));
				}
				wgProfiles.add(profile);
			}
		}
	}

	private void correlateElements() {
		// TODO Auto-generated method stub

	}

	/**
	 * Creates in-memory model from builders.
	 */
	UmlModel createInMemoryModel(Config cfg) {
		Util.logSubtitle(Level.INFO, "creating in-memory model...");
		long start = System.currentTimeMillis();

		UmlModel resultModel = new UmlModel(cfg);

		_logger.info("  creating in-memory package structure ...");
		UmlPackage mp = UmlPackage.basic(resultModel, MODEL_PACKAGE_NAME);
		Map<Profile, UmlPackage> profilePackages = buildSubPackages(mp);

		_logger.info("  creating in-memory root classes and enum literals ...");
		// Set<ClassBuilder> roots = new LinkedHashSet<ClassBuilder>();
		// Set<ClassBuilder> nonRoots = new LinkedHashSet<ClassBuilder>();
		// for (ClassBuilder cb : _classes.values()) {
		// if (cb.getSuperclasses().isEmpty()) {
		// cb.build();
		// roots.add(cb);
		// } else {
		// nonRoots.add(cb);
		// }
		// }
		// _logger.info("  creating in-memory sub-classes recursively ...");
		// for (ClassBuilder cb : roots) {
		// buildSubclasses(cb, nonRoots);
		// }
		// if (!nonRoots.isEmpty()) {
		// throw new RuntimeException("  " + nonRoots.size() + " classes not built!");
		// }
		// _logger.info("  reordering in-memory classes in packages ...");
		// for (PackageBuilder pb : _packages.values()) {
		// UmlPackage p = pb.getResult();
		// p.orderClasses(pb.getClassUuids());
		// }
		// _logger.info("  creating in-memory class' attributes, operations and constraints ...");
		// for (ClassBuilder cb : _classes.values()) {
		// for (AttributeBuilder ab : cb.getAttributes()) {
		// if (!ab.isLiteral()) {
		// ab.build();
		// }
		// }
		// for (ConstraintBuilder constrb : cb.getConstraints().values()) {
		// constrb.build();
		// }
		// for (OperationBuilder ob : cb.getOperations()) {
		// ob.build();
		// }
		// }
		// _logger.info("  creating in-memory associations ...");
		// for (AssociationBuilder ab : _associations.values()) {
		// ab.build();
		// }
		// _logger.info("  creating in-memory dependencies ...");
		// for (DependencyBuilder db : _dependencies.values()) {
		// db.build();
		// }

		Util.logCompletion(Level.INFO, "created in-memory model", start, getCfg().isAppSkipTiming());
		return resultModel;
	}

	private Map<Profile, UmlPackage> buildSubPackages(UmlPackage mp) {
		Map<Profile, UmlPackage> result = new LinkedHashMap<Profile, UmlPackage>();
		for (Entry<OwningWg, List<Profile>> entry : getProfiles().entrySet()) {
			OwningWg owner = entry.getKey();
			List<Profile> profiles = entry.getValue();

			UmlPackage tp = UmlPackage.basic(mp, owner.getTopPackageName());

			for (Profile profile : profiles) {
				List<String> subdirNames = profile.getSubdirNames();
				UmlPackage parent = tp;
				// starting from 1, because the first already handled as the owning WG:
				for (int level = 1; level < subdirNames.size(); ++level) {
					String subdirName = subdirNames.get(level);
					Set<UmlPackage> childPackages = parent.getChildPackages(subdirName);
					UmlPackage p = !childPackages.isEmpty() ? childPackages.iterator().next()
							: UmlPackage.basic(parent, subdirName);
					parent = p;
				}
				result.put(profile, parent);
			}
		}
		return result;
	}

	// private void buildSubclasses(ClassBuilder cb, Set<ClassBuilder> nonRoots) {
	// for (ClassBuilder sub : cb.getSubclasses()) {
	// sub.build();
	// nonRoots.remove(sub);
	// buildSubclasses(sub, nonRoots);
	// }
	// }

	// ------------------------------- API ------------------------------------

	/** Returns profiles per owner. */
	public Map<OwningWg, List<Profile>> getProfiles() {
		return Collections.unmodifiableMap(_profiles);
	}

	// ===== Impl. of org.tanjakostic.jcleancim.builder.AbstractModelBuilder methods =====

	@Override
	public UmlModel build() {
		parseProfiles();
		correlateElements();

		return createInMemoryModel(getCfg());
	}

	@Override
	protected DiagramExporter createDiagramExporter() {
		return new EmptyDiagramExporter(getCfg());
	}

	@Override
	protected XMIExporter createXMIExporter() {
		return new EmptyXMIExporter(getCfg());
	}
}
