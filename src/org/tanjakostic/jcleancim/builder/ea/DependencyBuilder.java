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

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.tanjakostic.jcleancim.model.UmlDependency;
import org.tanjakostic.jcleancim.model.UmlDependency.Data;
import org.tanjakostic.jcleancim.model.UmlDependency.Kind;
import org.tanjakostic.jcleancim.model.UmlObjectData;
import org.tanjakostic.jcleancim.model.UmlStereotype;
import org.tanjakostic.jcleancim.model.UmlStructure;
import org.tanjakostic.jcleancim.model.UmlVisibility;
import org.tanjakostic.jcleancim.util.ProgrammerErrorException;
import org.tanjakostic.jcleancim.util.Util;

/**
 * @param <O>
 *            Source data for dependency
 * @param <T>
 *            Source data for dependency tagged values
 * @author tatjana.kostic@ieee.org
 * @version $Id: DependencyBuilder.java 21 2019-08-12 15:44:50Z dev978 $
 */
abstract public class DependencyBuilder<O, T> extends AbstractObjectBuilderFromEA<UmlDependency> {
	private static final Logger _logger = Logger.getLogger(DependencyBuilder.class.getName());

	public static boolean isDependency(String type) {
		return "Dependency".equals(type);
	}

	static final List<String> TYPE_NAMES = Arrays.asList("Dependency");

	private PackageBuilder<?, ?, ?, ?, ?, ?> _sourcePackage;
	private PackageBuilder<?, ?, ?, ?, ?, ?> _targetPackage;
	private ClassBuilder<?, ?, ?, ?, ?, ?> _sourceClass;
	private ClassBuilder<?, ?, ?, ?, ?, ?> _targetClass;
	private UmlObjectData _objData;

	private Kind _kind;
	private final Map<String, String> _taggedValues = new LinkedHashMap<String, String>();

	/**
	 * Creates dependency between two packages or two classes. Visibility is always set to
	 * {@link UmlVisibility#PUBLIC}. At least one of source*, target* must be non-null.
	 * <p>
	 * Note that because at present we don't care about characteristics of dependency ends other
	 * than the elements they connect, we store tagged values on the ends into the tagged values of
	 * the dependency itself.
	 */
	protected DependencyBuilder(O inData, EaModelBuilder<?, ?> model, T tagsSrc,
			PackageBuilder<?, ?, ?, ?, ?, ?> sourcePackage,
			PackageBuilder<?, ?, ?, ?, ?, ?> targetPackage,
			ClassBuilder<?, ?, ?, ?, ?, ?> sourceClass, ClassBuilder<?, ?, ?, ?, ?, ?> targetClass,
			EaHelper eaHelper) {

		Util.ensureNotNull(inData, "inData");
		Util.ensureNotNull(model, "model");
		Util.ensureNotNull(tagsSrc, "tagsSrc");
		if ((sourcePackage == null && targetPackage == null)
				&& ((sourceClass == null && targetClass == null))) {
			throw new ProgrammerErrorException("All sources and targets null.");
		}

		setSourcePackage(sourcePackage);
		setTargetPackage(targetPackage);
		setSourceClass(sourceClass);
		setTargetClass(targetClass);

		Integer id = getConnectorID(inData);
		String guid = getConnectorGUID(inData);
		String name = getConnectorName(inData);
		String alias = getConnectorAlias(inData);
		String stereotype = getConnectorStereotypes(inData);
		String visibility = null; // EA does not have it
		String notes = getConnectorNotes(inData);
		initObjData(id, guid, name, alias, stereotype, visibility, notes, eaHelper);
		model.addDependency(this);

		initOwnData();

		List<Map<String, String>> myTaggedValues = fetchTaggedValues(tagsSrc);
		initTaggedValues(myTaggedValues);

		_logger.log(CTOR_LOG_LEVEL, "read from EA " + toString());
	}

	abstract protected Integer getConnectorID(O inData);

	abstract protected String getConnectorGUID(O inData);

	abstract protected String getConnectorName(O inData);

	abstract protected String getConnectorAlias(O inData);

	abstract protected String getConnectorStereotypes(O inData);

	abstract protected String getConnectorNotes(O inData);

	private void initObjData(Integer id, String guid, String name, String alias, String stereotype,
			String visibility, String notes, EaHelper eaHelper) {
		_objData = new UmlObjectData(id, guid, name, alias, new UmlStereotype(stereotype),
				visibility, eaHelper.getRawText(notes), eaHelper.getHtmlText(notes));
	}

	void initOwnData() {
		if (getSourcePackage() != null || getTargetPackage() != null) {
			_kind = Kind.PACKAGE;
		} else if (getSourceClass() != null || getTargetClass() != null) {
			_kind = Kind.CLASS;
		} else {
			throw new ProgrammerErrorException("Cannot determine kind.");
		}
	}

	// ---------------------- tagged values -----------------

	abstract protected List<Map<String, String>> fetchTaggedValues(T taggedValues);

	private void initTaggedValues(List<Map<String, String>> myTaggedValuesFields) {
		for (Map<String, String> m : myTaggedValuesFields) {
			String name = m.get(EA.CONN_TGVAL_NAME);
			String value = m.get(EA.CONN_TGVAL_VALUE);
			getTaggedValues().put(name, Util.null2empty(value));
		}
	}

	// ---------------------------

	public final PackageBuilder<?, ?, ?, ?, ?, ?> getSourcePackage() {
		return _sourcePackage;
	}

	public final void setSourcePackage(PackageBuilder<?, ?, ?, ?, ?, ?> sourcePackage) {
		_sourcePackage = sourcePackage;
	}

	public final PackageBuilder<?, ?, ?, ?, ?, ?> getTargetPackage() {
		return _targetPackage;
	}

	public final void setTargetPackage(PackageBuilder<?, ?, ?, ?, ?, ?> targetPackage) {
		_targetPackage = targetPackage;
	}

	public final ClassBuilder<?, ?, ?, ?, ?, ?> getSourceClass() {
		return _sourceClass;
	}

	public final void setSourceClass(ClassBuilder<?, ?, ?, ?, ?, ?> sourceClass) {
		_sourceClass = sourceClass;
	}

	public final ClassBuilder<?, ?, ?, ?, ?, ?> getTargetClass() {
		return _targetClass;
	}

	public final void setTargetClass(ClassBuilder<?, ?, ?, ?, ?, ?> targetClass) {
		_targetClass = targetClass;
	}

	public final Kind getKind() {
		return _kind;
	}

	public final Map<String, String> getTaggedValues() {
		return _taggedValues;
	}

	public final String getQualifiedName() {
		String source = "null";
		String target = "null";
		if (getKind() == Kind.PACKAGE) {
			if (getSourcePackage() != null) {
				source = getSourcePackage().getQualifiedName();
			}
			if (getTargetPackage() != null) {
				target = getTargetPackage().getQualifiedName();
			}
		} else {
			if (getSourceClass() != null) {
				source = getSourceClass().getQualifiedName();
			}
			if (getTargetClass() != null) {
				target = getTargetClass().getQualifiedName();
			}
		}
		return (source + "->" + target);
	}

	/**
	 * Model builder may want to call this method to cross-check initialisation is correct.
	 */
	public final void ensureClass2ClassOrPackage2PackageDependenciesEndsInitialised() {
		String msg = getKind().toString() + "dep " + getObjData().getName() + " (id="
				+ getObjData().getId() + ") ";

		if (getKind() == Kind.PACKAGE) {
			if (getSourcePackage() == null && getTargetPackage() == null) {
				throw new RuntimeException((msg + "has both source and target null."));
			} else if (getSourcePackage() == null) {
				throw new RuntimeException((msg + "has null source."));
			} else if (getTargetPackage() == null) {
				throw new RuntimeException((msg + "has null target."));
			}
			if (!getSourcePackage().collectEfferentPackages().contains(getTargetPackage())) {
				throw new ProgrammerErrorException("Source package missing" + toString());
			}
			if (!getTargetPackage().collectAfferentPackages().contains(getSourcePackage())) {
				throw new ProgrammerErrorException("Target package missing " + toString());
			}
		} else if (getKind() == Kind.CLASS) {
			if (getSourceClass() == null && getTargetClass() == null) {
				throw new RuntimeException((msg + "has both source and target null."));
			} else if (getSourceClass() == null) {
				throw new RuntimeException((msg + "has null source."));
			} else if (getTargetClass() == null) {
				throw new RuntimeException((msg + "has null target."));
			}
			if (!getSourceClass().getDependencyEfferentClasses().contains(getTargetClass())) {
				throw new ProgrammerErrorException("Source class missing " + toString());
			}
			if (!getTargetClass().getDependencyAfferentClasses().contains(getSourceClass())) {
				throw new ProgrammerErrorException("Target class missing " + toString());
			}
		} else {
			String excMsg = "This dependency should have not been created as "
					+ "dependency, but as skipped connector because it does not connect 2 packages"
					+ " or two classes (any other combination should be skipped)." + toString();
			throw new ProgrammerErrorException(excMsg);
		}
	}

	// =====================================================

	@Override
	public String toString() {
		String result = "DependencyBuilder [_kind=" + _kind;
		result += ", qName = " + getQualifiedName();
		result += ", _objData=" + _objData;
		if (!_taggedValues.isEmpty()) {
			result += ", " + _taggedValues.size() + "_taggedValues" + _taggedValues;
		}
		result += "]";
		return result;
	}

	// =====================================================

	@Override
	public final UmlObjectData getObjData() {
		return _objData;
	}

	@Override
	protected final void doBuild() {
		if (getKind() != UmlDependency.Kind.PACKAGE && getKind() != UmlDependency.Kind.CLASS) {
			throw new RuntimeException("Unknown dependency kind: " + getKind() + ".");
		}
		UmlStructure source = (getKind() == UmlDependency.Kind.PACKAGE)
				? getSourcePackage().getResult() : getSourceClass().getResult();
		UmlStructure target = (getKind() == UmlDependency.Kind.PACKAGE)
				? getTargetPackage().getResult() : getTargetClass().getResult();
		if (source == null || target == null) {
			String container = (getKind() == UmlDependency.Kind.PACKAGE) ? "packages" : "classes";
			throw new RuntimeException(
					"Source and target " + container + " should have been built");
		}

		Data data = new Data();
		setResult(source.addDependency(target, getObjData(), data));
	}
}
