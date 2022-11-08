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

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.log4j.Logger;
import org.tanjakostic.jcleancim.model.UmlAssociationEnd;
import org.tanjakostic.jcleancim.model.UmlClass;
import org.tanjakostic.jcleancim.model.UmlMultiplicity;
import org.tanjakostic.jcleancim.model.UmlObjectData;
import org.tanjakostic.jcleancim.model.UmlStereotype;
import org.tanjakostic.jcleancim.model.UmlAssociationEnd.Data;
import org.tanjakostic.jcleancim.model.UmlAssociationEnd.Kind;
import org.tanjakostic.jcleancim.model.UmlAssociationEnd.Navigable;
import org.tanjakostic.jcleancim.util.ProgrammerErrorException;
import org.tanjakostic.jcleancim.util.Util;

/**
 * @param <O>
 *            Source data for association end
 * @param <T>
 *            Source data for association end tagged values
 * @author tatjana.kostic@ieee.org
 * @version $Id: AssociationEndBuilder.java 21 2019-08-12 15:44:50Z dev978 $
 */
abstract public class AssociationEndBuilder<O, T> extends
AbstractObjectBuilderFromEA<UmlAssociationEnd> {
	private static final Logger _logger = Logger.getLogger(AssociationEndBuilder.class.getName());

	private final AssociationBuilder<?, ?> _containingAssociation;
	private final boolean _isSource;
	private UmlObjectData _objData;
	private ClassBuilder<?, ?, ?, ?, ?, ?> _type;

	private Kind _kind;
	private UmlMultiplicity _multiplicity;
	private Navigable _navigable;

	private final Map<String, String> _taggedValues = new LinkedHashMap<String, String>();

	/**
	 * Constructs association end from EA object. Sets id to 0 and randomly generated uuid (these
	 * are not defined in EA).
	 * <p>
	 * If multiplicity for a composite end is empty, sets it to {@link UmlMultiplicity#OPT_ONE},
	 * otherwise just uses whatever is found in the model.
	 *
	 * @param inData
	 * @param tagsSrc
	 * @param containingAssociation
	 * @param type
	 * @param eaHelper
	 *            required for getting formatted doc
	 * @throws NullPointerException
	 *             if any argument is null.
	 */
	protected AssociationEndBuilder(O inData, T tagsSrc,
			AssociationBuilder<?, ?> containingAssociation, boolean isSource,
			ClassBuilder<?, ?, ?, ?, ?, ?> type, EaHelper eaHelper) {
		Util.ensureNotNull(inData, "inData");
		Util.ensureNotNull(containingAssociation, "model");
		Util.ensureNotNull(eaHelper, "helper");

		_containingAssociation = containingAssociation;
		_isSource = isSource;
		setType(type);

		String name = getRoleName(inData);
		String alias = getRoleAlias(inData);
		String stereotypes = getRoleStereotypes(inData);
		String visibility = getRoleVisibility(inData);
		String notes = getRoleNotes(inData);
		initObjData(name, alias, stereotypes, visibility, notes, eaHelper);

		String kind = Integer.valueOf(getRoleAggregation(inData)).toString();
		String cardinality = getRoleCardinality(inData);
		String direction = getRoleNavigable(inData);
		initOwnData(kind, cardinality, direction);

		List<Map<String, String>> myTaggedValues = fetchTaggedValues(tagsSrc);
		initTaggedValues(myTaggedValues);
	}

	abstract protected String getRoleName(O inData);

	abstract protected String getRoleAlias(O inData);

	abstract protected String getRoleStereotypes(O inData);

	abstract protected String getRoleVisibility(O inData);

	abstract protected String getRoleNotes(O inData);

	protected final void initObjData(String name, String alias, String stereotype,
			String visibility, String notes, EaHelper eaHelper) {
		_objData = new UmlObjectData(null, null, name, alias, new UmlStereotype(stereotype),
				visibility, eaHelper.getRawText(notes), eaHelper.getHtmlText(notes));
	}

	abstract protected String getRoleAggregation(O inData);

	abstract protected String getRoleCardinality(O inData);

	abstract protected String getRoleNavigable(O inData);

	protected final void initOwnData(String kind, String cardinality, String direction) {
		if ("2".equals(kind)) {
			_kind = Kind.COMPOS;
		} else if ("1".equals(kind)) {
			_kind = Kind.AGGREG;
		} else if ("0".equals(kind)) {
			_kind = Kind.ASSOC;
		} else {
			_kind = Kind.OTHER;
		}

		UmlMultiplicity multiplicity = UmlMultiplicity.parseFromString(cardinality);
		if (getKind() == Kind.COMPOS && multiplicity == UmlMultiplicity.EMPTY) {
			_multiplicity = UmlMultiplicity.OPT_ONE;
		} else {
			_multiplicity = multiplicity;
		}

		if ("Navigable".equals(direction)) {
			_navigable = Navigable.yes;
		} else if ("Non-Navigable".equals(direction)) {
			_navigable = Navigable.no;
		} else {
			_navigable = Navigable.unspecified;
		}
	}

	abstract protected List<Map<String, String>> fetchTaggedValues(T srcTags);

	private void initTaggedValues(List<Map<String, String>> myTaggedValuesFields) {
		for (Map<String, String> m : myTaggedValuesFields) {
			String name = m.get(EA.ROLE_TGVAL_NAME);
			String value = m.get(EA.ROLE_TGVAL_VALUE);
			getTaggedValues().put(name, Util.null2empty(value));
		}
	}

	public final Map<String, String> getTaggedValues() {
		return _taggedValues;
	}

	// ----------------------------------

	public final boolean isSource() {
		return _isSource;
	}

	public final AssociationBuilder<?, ?> getContainingAssociation() {
		return _containingAssociation;
	}

	public final Kind getKind() {
		return _kind;
	}

	public final UmlMultiplicity getMultiplicity() {
		return _multiplicity;
	}

	public final Navigable getNavigable() {
		return _navigable;
	}

	public final void setType(ClassBuilder<?, ?, ?, ?, ?, ?> type) {
		if (getType() != null) {
			throw new ProgrammerErrorException("Assoc. end type already set.");
		}
		_type = type;
		if (type != null) {
			String which = isSource() ? "source" : "target";
			_logger.debug("Updated " + which + " type to " + type.getObjData().getName());
		}
	}

	public final ClassBuilder<?, ?, ?, ?, ?, ?> getType() {
		return _type;
	}

	public final boolean isAssociation() {
		return !isAggregation() && !isComposition() && !isOther();
	}

	public final boolean isAggregation() {
		return getKind() == Kind.AGGREG;
	}

	public final boolean isComposition() {
		return getKind() == Kind.COMPOS;
	}

	public final boolean isOther() {
		return getKind() == Kind.OTHER;
	}

	// =====================================================

	@Override
	public String toString() {
		String result = "AssociationEndBuilder [_kind=" + _kind;
		result += ", _isSource=" + _isSource;
		result += ", _objData=" + _objData;
		result += ", _type=" + (_type != null ? _type.getObjData().getName() : "null");
		result += ", _multiplicity=" + _multiplicity;
		result += ", _navigable=" + _navigable;
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
		UmlClass type = getType().getResult();
		Data data = new Data(getKind(), getMultiplicity(), getNavigable());
		setResult(new UmlAssociationEnd(type, getObjData(), data));

		for (Entry<String, String> entry : getTaggedValues().entrySet()) {
			getResult().addTaggedValue(entry.getKey(), entry.getValue());
		}
	}
}
