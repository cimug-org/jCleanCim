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

import java.util.Collections;
import java.util.Set;

import org.apache.log4j.Logger;
import org.tanjakostic.jcleancim.common.Nature;
import org.tanjakostic.jcleancim.common.OwningWg;
import org.tanjakostic.jcleancim.util.Util;

/**
 * UML element or connector that we ignore, but track for validation purposes.
 * <p>
 * Design note: We could have had four subclasses, but it would have been an overkill at this point
 * in time.
 *
 * @author tatjana.kostic@ieee.org
 * @version $Id: UmlSkipped.java 34 2019-12-20 18:37:17Z dev978 $
 */
public class UmlSkipped extends AbstractUmlObject {
	private static final Logger _logger = Logger.getLogger(UmlSkipped.class.getName());

	public static final String EA_STATE_MACHINE = "StateMachine";
	public static final String EA_NOTE = "Note";
	public static final String EA_TEXT = "Text";
	public static final String EA_BOUNDARY = "Boundary";
	public static final String EA_STATE = "State";
	public static final String EA_STATE_NODE = "StateNode";
	public static final String EA_PROCESS = "Process";

	public static final String EA_NOTE_LINK = "NoteLink";

	/**
	 * Kind of EA elements and connectors that may be found in the model, but are just skipped.
	 *
	 * @author tatjana.kostic@ieee.org
	 * @version $Id: UmlSkipped.java 34 2019-12-20 18:37:17Z dev978 $
	 */
	public enum Kind implements UmlKind {

		STATE_MACHINE(EA_STATE_MACHINE, "state machine", "StateMachine", "state machine element"),

		NOTE(EA_NOTE, "note", "Note", "note element"),

		TEXT(EA_TEXT, "text", "Text", "text element"),

		BOUNDARY(EA_BOUNDARY, "boundary", "Boundary", "boundary element"),

		STATE(EA_STATE, "state", "State", "state element"),

		STATE_NODE(EA_STATE_NODE, "state node", "StateNode", "state node element"),

		NOTE_LINK(EA_NOTE_LINK, "note link", "NoteLink", "note link connector"),

		PROCESS(EA_PROCESS, "process", "Process", "process element"),

		OTHER("other", "other", "Other", "other element or connector");

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
	}

	/**
	 * Data from the UML model repository specific to {@link UmlSkipped}.
	 *
	 * @author tatjana.kostic@ieee.org
	 * @version $Id: UmlSkipped.java 34 2019-12-20 18:37:17Z dev978 $
	 */
	public static class Data {
		private static final Data DEFAULT_CONN = new Data(Kind.NOTE_LINK, true, "otherEndName");
		private static final Data DEFAULT_ELEM = new Data(Kind.NOTE, false, null);

		/**
		 * Returns empty instance; for connector, sets default kind to {@link Kind#NOTE_LINK}, and
		 * for element to {@link Kind#NOTE}.
		 */
		public static Data empty(boolean isConnector) {
			return (isConnector) ? DEFAULT_CONN : DEFAULT_ELEM;
		}

		private final Kind _kind;
		private final boolean _isConnector;
		private final String _otherEndName;

		/**
		 * Constructor.
		 *
		 * @param kind
		 * @param isConnector
		 * @param otherEndName
		 *            ignored if <code>isConnector</code> is false.
		 */
		public Data(Kind kind, boolean isConnector, String otherEndName) {
			_kind = kind;
			_isConnector = isConnector;
			_otherEndName = (isConnector) ? otherEndName : null;
		}

		public boolean isConnector() {
			return _isConnector;
		}

		public Kind getKind() {
			return _kind;
		}

		public String getOtherEndName() {
			return _otherEndName;
		}
	}

	private final UmlStructure _container;
	private final boolean _forPackage;

	private final Data _data;

	/** Constructs minimal instance - useful for testing. */
	static UmlSkipped basicConnector(UmlStructure source) {
		return new UmlSkipped(source, new UmlObjectData(null, "otherEndName", null),
				Data.empty(true));
	}

	/** Constructs minimal instance - useful for testing. */
	static UmlSkipped basicElement(UmlStructure container) {
		return new UmlSkipped(container, new UmlObjectData(null, "skippedElement", null),
				Data.empty(false));
	}

	/** Intended to be called by {@link UmlStructure} and tests only. */
	UmlSkipped(UmlStructure container, UmlObjectData objData, Data data) {
		super(objData);
		Util.ensureNotNull(container, "container");
		Util.ensureNotNull(data, "data");

		_container = container;
		_forPackage = (container instanceof UmlPackage);
		_data = data;

		_logger.trace(String.format("created %s", toString()));
	}

	/** Returns the container of this skipped element, or the source side if this is a connector. */
	public UmlStructure getContainer() {
		return _container;
	}

	/** Returns true if this skipped item is related to package, false if related to class. */
	public boolean isForPackage() {
		return _forPackage;
	}

	/**
	 * Returns true if this skipped element is some kind of connector, otherwise it's an element.
	 */
	public boolean isConnector() {
		return _data.isConnector();
	}

	/** Returns name of the other end if this skipped element is a connector, null otherwise. */
	public String getOtherEndName() {
		return _data.getOtherEndName();
	}

	// =========== org.tanjakostic.jcleancim. extends AbstractUmlObject ============

	@Override
	public OwningWg getOwner() {
		return _container.getOwner();
	}

	@Override
	public Namespace getNamespace() {
		return _container.getNamespace();
	}

	@Override
	public Nature getNature() {
		return _container.getNature();
	}

	@Override
	public boolean isInformative() {
		return super.isInformative() || _container.isInformative();
	}

	@Override
	public UmlKind getKind() {
		return _data.getKind();
	}

	@Override
	public String getQualifiedName() {
		String separator = (_forPackage) ? PACKAGE_SEPARATOR : CLASS_SEPARATOR;
		String result = _container.getName() + separator + getName();
		if (isConnector()) {
			result += " - " + getOtherEndName();
		}
		return result;
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
		StringBuilder sb = new StringBuilder(toShortString(true, true));
		if (isConnector()) {
			sb.append(" - ").append(getOtherEndName());
		}
		if (getTaggedValues().size() != 0) {
			sb.append("; tags=").append(getTaggedValues().toString());
		}
		return sb.toString();
	}
}
