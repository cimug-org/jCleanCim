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

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import org.tanjakostic.jcleancim.experimental.builder.rdfs.RdfsDifference.Kind;
import org.tanjakostic.jcleancim.util.Util;
import org.w3c.dom.Element;

/**
 * Base class with common implementation for all CIM RDF/OWL Schema elements.
 * <p>
 * This implementation delegates issue tracking and logging to its {@link #getModel() model}.
 *
 * @author tatjana.kostic@ieee.org
 * @version $Id: RdfsElem.java 21 2019-08-12 15:44:50Z dev978 $
 */
abstract public class RdfsElem {

	/** Pattern of valid characters for all the CIM tokens. */
	public static final Pattern invalidCharsPattern = Pattern.compile("[^a-zA-Z0-9]");

	private final RdfsModel _model;

	private final URI _about;
	private final String _label;
	private final String _comment;
	private final String _isDefinedBy;

	/**
	 * Initialises fields common to RDF elements from DOM Element.
	 *
	 * @param model
	 * @param elem
	 *            DOM element
	 * @throws CimSchemaException
	 *             if about attribute contains an invalid URI.
	 */
	protected RdfsElem(RdfsModel model, Element elem) throws CimSchemaException {
		this(model, XmlAttribute.about.getValue(elem), XmlChildElement.label.getText(elem),
				XmlChildElement.comment.getText(elem), XmlElement.deducePackageName(elem), false);
	}

	/**
	 * Initialises fields common to RDF elements independently of DOM Element.
	 *
	 * @param about
	 *            non-null (schemaLabel#name)
	 * @param label
	 *            non-null
	 * @param comment
	 *            if null, will be set to empty string
	 * @param pckage
	 *            if null, will be set to "?"
	 * @param validateAbout
	 *            whether to validate <code>about</code>.
	 * @throws CimSchemaException
	 *             if about attribute contains an invalid URI.
	 */
	protected RdfsElem(RdfsModel model, String about, String label, String comment, String pckage,
			boolean validateAbout) throws CimSchemaException {
		Util.ensureNotNull(model, "model");
		Util.ensureNotEmpty(about, "about");
		Util.ensureNotEmpty(label, "label");

		_model = model;
		_about = XmlChildElement.getValidatedUri(about);
		_label = label;
		_comment = (comment == null) ? "" : comment;
		_isDefinedBy = (pckage == null) ? "?" : pckage;
	}

	/**
	 * Returns the string describing the kind of this element.
	 *
	 * @return string describing the kind of this element.
	 */
	abstract public String getKind();

	/**
	 * Returns the model containing this element.
	 *
	 * @return model containing this element.
	 */
	public RdfsModel getModel() {
		return _model;
	}

	public final String getAbout() {
		return _about.toString();
	}

	public final String getName() {
		return _about.getRawFragment();
	}

	public final String getSchemaLabel() {
		return _about.getScheme() + ":" + _about.getSchemeSpecificPart();
	}

	public final String getComment() {
		return _comment;
	}

	public final String getLabel() {
		return _label;
	}

	public final String getPackage() {
		return _isDefinedBy;
	}

	/**
	 * Calculates differences between this instance and <code>other</code> and returns them in a
	 * list of Strings. While {@link #equals(Object)} method ignores some known differences between
	 * elements in different dialects, this method catches them all.
	 * <p>
	 * Implementation note: Subclasses that override this method are expected to first call
	 * super.runDiff().
	 *
	 * @param other
	 */
	public List<RdfsDifference> getDiffs(RdfsElem other) {
		List<RdfsDifference> diffsCollector = new ArrayList<RdfsDifference>();
		if (this == other || other == null) {
			return diffsCollector;
		}

		doEquals(false, other, diffsCollector);
		return diffsCollector;
	}

	protected final RdfsDifference formatDiff(String field, String thisVal, String otherVal,
			RdfsElem other) {
		return new RdfsDifference(getPackage(), getKind(), getName(), Kind.field, field, thisVal,
				otherVal, "");
	}

	final RdfsDifference formatMissingAndAdded(boolean isAdded) {
		String val1 = (isAdded) ? "<added>" : "<missing>";
		return new RdfsDifference(getPackage(), getKind(), getName(), Kind.missingInOne, "", val1,
				"", "");
	}

	// -------------------- object methods -----------------

	@Override
	public String toString() {
		StringBuilder s = new StringBuilder();
		s.append(getKind()).append(" ");
		s.append(getPackage()).append("::");
		s.append(getName());
		return s.toString();
	}

	public String toStringLong() {
		StringBuilder s = new StringBuilder();
		s.append(getKind());
		s.append("  package = '").append(getPackage()).append("'\n");
		s.append("  name = '").append(getName()).append("'\n");
		s.append("  label = '").append(getLabel()).append("'\n");
		s.append("  comment = '").append(getComment()).append("'\n");
		return s.toString();
	}

	/**
	 * Uses all instance fields except for _model.
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((_about == null) ? 0 : _about.hashCode());
		result = prime * result + ((_comment == null) ? 0 : _comment.hashCode());
		result = prime * result + ((_isDefinedBy == null) ? 0 : _isDefinedBy.hashCode());
		result = prime * result + ((_label == null) ? 0 : _label.hashCode());
		return result;
	}

	/**
	 * Uses all instance fields except for _model. We do track differences for relevant fields, but
	 * we ignore the difference (i.e., don't return false) in following two cases:
	 * <ul>
	 * <li>_package field when dialects are different and this element is not an RdfsClass (RDF has
	 * info on package for classes only, so in case of RdfsClass we always compare normally).
	 * <li>_comment field when dialects are different (with OWL, we have to generate attributes of
	 * datatype classes, as well as 4 CIM primitive classes, so we have no original comments).
	 * </ul>
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (!(obj instanceof RdfsElem)) {
			return false;
		}
		return doEquals(true, obj, null);
	}

	private boolean doEquals(boolean isForEquals, Object obj, List<RdfsDifference> diffs) {
		assert (!isForEquals ? diffs != null : true) : "diffs must be non-null for collecting";

		RdfsElem other = (RdfsElem) obj;

		if (!_about.equals(other._about)) {
			if (isForEquals) {
				return false;
			}
			diffs.add(formatDiff("about", _about.toString(), other._about.toString(), other));
		}

		if (!_label.equals(other._label)) {
			if (isForEquals) {
				return false;
			}
			diffs.add(formatDiff("label", _label, other._label, other));
		}

		if (!_comment.equals(other._comment)) {
			if (isForEquals) {
				return false;
			} else {
				diffs.add(formatDiff("comment", "\"" + _comment + "\"", "\"" + other._comment
						+ "\"", other));
			}
		}

		if (!_isDefinedBy.equals(other._isDefinedBy)) {
			if (isForEquals) {
				if (this instanceof RdfsClass && other instanceof RdfsClass) {
					return false;
				}
			} else {
				diffs.add(formatDiff("isDefinedBy", _isDefinedBy, other._isDefinedBy, other));
			}
		}

		return true;
	}
}
