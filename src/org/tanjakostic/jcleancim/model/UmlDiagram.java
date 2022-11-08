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

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;
import org.tanjakostic.jcleancim.common.Config;
import org.tanjakostic.jcleancim.common.Nature;
import org.tanjakostic.jcleancim.common.OwningWg;
import org.tanjakostic.jcleancim.util.Util;

/**
 * Diagram from the UML model, assigned to either a class or a package.
 * <p>
 * Design note: We could have had two subclasses, but it would have been an overkill at this point
 * in time.
 *
 * @author tatjana.kostic@ieee.org
 * @version $Id: UmlDiagram.java 34 2019-12-20 18:37:17Z dev978 $
 */
public class UmlDiagram extends AbstractUmlObject {
	private static final Logger _logger = Logger.getLogger(UmlDiagram.class.getName());

	/**
	 * Kind of diagram, as given by EA.
	 *
	 * @author tatjana.kostic@ieee.org
	 * @version $Id: UmlDiagram.java 34 2019-12-20 18:37:17Z dev978 $
	 */
	public enum Kind implements UmlKind {
		ACTIVITY("Activity", "activity", "Diagram", "activity diagram"),
		ANALYSIS("Analysis", "analysis", "Diagram", "analysis diagram"),
		COMPONENT("Component", "component", "Diagram", "component diagram"),
		CUSTOM("Custom", "custom", "Diagram", "custom diagram"),
		DEPLOYMENT("Deployment", "deployment", "Diagram", "deployment diagram"),
		LOGICAL("Logical", "class", "Diagram", "class diagram"),
		SEQUENCE("Sequence", "sequence", "Diagram", "sequence diagram"),
		STATECHART("Statechart", "statechart", "Diagram", "statechart diagram"),
		USE_CASE("Use Case", "use case", "Diagram", "use case diagram"),
		PACKAGE("Package", "package", "Diagram", "package diagram"),
		OBJECT("Object", "object", "Diagram", "object diagram"),
		OTHER("other", "other", "Diagram", "other diagram");

		/**
		 * Returns literal with <code>value</code> if found, {@link #OTHER} instance otherwise.
		 */
		public static Kind findForValue(String value) {
			for (Kind k : values()) {
				if (k.getValue().equals(value)) {
					return k;
				}
			}
			return OTHER;
		}

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
	 * Returns all available classifications (kinds) for diagrams.
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

	/**
	 * Data from the UML model repository specific to {@link UmlDiagram}.
	 *
	 * @author tatjana.kostic@ieee.org
	 * @version $Id: UmlDiagram.java 34 2019-12-20 18:37:17Z dev978 $
	 */
	public static class Data {
		private static final Data DEFAULT = new Data();

		/** Returns an empty instance; sets default kind to {@link Kind#CUSTOM}. */
		public static Data empty() {
			return DEFAULT;
		}

		private final Kind _kind;
		private final boolean _portrait;
		private final boolean _supportsTags;

		private Data() {
			this(Kind.CUSTOM, false, false);
		}

		/**
		 * Constructor.
		 *
		 * @param kind
		 * @param portrait
		 * @param supportsTags
		 */
		public Data(Kind kind, boolean portrait, boolean supportsTags) {
			Util.ensureNotNull(kind, "kind");
			_kind = kind;
			_portrait = portrait;
			_supportsTags = supportsTags;
		}

		public Kind getKind() {
			return _kind;
		}

		public boolean isPortrait() {
			return _portrait;
		}

		public boolean isSupportsTags() {
			return _supportsTags;
		}
	}

	private final Data _data;
	private final UmlStructure _container;
	private final File _pic;
	private final boolean _isBlankPic;
	private final boolean _forPackage;

	/** Constructs minimal instance - useful for testing. */
	static UmlDiagram basic(UmlStructure container, String name) {
		return new UmlDiagram(container, null, new UmlObjectData(name), Data.empty());
	}

	/** Intended to be called by {@link UmlStructure} and tests only. */
	UmlDiagram(UmlStructure container, File pic, UmlObjectData objData, Data data) {
		super(objData);

		Util.ensureNotNull(container, "container");
		if (container instanceof UmlPackage) {
			_forPackage = true;
		} else if (container instanceof UmlClass) {
			_forPackage = false;
		} else {
			throw new IllegalArgumentException(String.format(
					"Cannot add diagram to %s; supported types are UmlClass and UmlPackage.",
					container.getClass().getName()));
		}
		Util.ensureNotNull(data, "data");

		_container = container;
		Config cfg = container.getModel().getCfg();
		_data = data;
		if (pic == null) {
			_isBlankPic = true;
			_pic = new File(cfg.getBlankPngFileAbsPath());
		} else {
			_isBlankPic = false;
			_pic = pic;
		}

		_logger.trace(String.format("created %s", toString()));
	}

	/** Returns the containing structure (class or package). */
	public UmlStructure getContainer() {
		return _container;
	}

	/** Returns whether the containing structure for this diagram is a package. */
	public boolean isForPackage() {
		return _forPackage;
	}

	/**
	 * Returns the file where the image has been stored; in case the application failed or did not
	 * need to store the real image, the file is the default and {@link #isBlankPic()} returns true.
	 */
	public File getPic() {
		return _pic;
	}

	/**
	 * Returns true in case the application failed or did not need to store the real image. We allow
	 * for this condition in order to be able to run the rest of application, even without the
	 * diagrams.
	 */
	public boolean isBlankPic() {
		return _isBlankPic;
	}

	/** Returns whether the page format is portrait. */
	public boolean isPortrait() {
		return _data.isPortrait();
	}

	/** Returns whether tagged values are allowed. */
	public boolean isSupportsTags() {
		return _data.isSupportsTags();
	}

	// =========== org.tanjakostic.jcleancim.model.UmlObject ============

	@Override
	public OwningWg getOwner() {
		return getContainer().getOwner();
	}

	@Override
	public Namespace getNamespace() {
		return _container.getNamespace();
	}

	@Override
	public boolean isInformative() {
		return super.isInformative() || getContainer().isInformative();
	}

	@Override
	public Nature getNature() {
		return getContainer().getNature();
	}

	@Override
	public UmlKind getKind() {
		return _data.getKind();
	}

	@Override
	public String getQualifiedName() {
		String separator = (isForPackage()) ? PACKAGE_SEPARATOR : CLASS_SEPARATOR;
		return getContainer().getName() + separator + getName();
	}

	@Override
	protected void validateTag(String name, String value) {
		if (!isSupportsTags()) {
			throw new InvalidTagException("Tagged values not supported for diagrams.");
		}
	}

	/**
	 * {@inheritDoc}
	 * <p>
	 * Always returns empty set.
	 */
	@Override
	public Set<String> getPredefinedTagNames() {
		return Collections.emptySet();
	}

	// =========== java.lang.Object ============

	@Override
	public String toString() {
		return toShortString(true, true);
	}
}
