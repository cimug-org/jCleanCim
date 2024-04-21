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
import java.util.Map.Entry;

import org.apache.log4j.Logger;
import org.tanjakostic.jcleancim.model.UmlAssociation;
import org.tanjakostic.jcleancim.model.UmlAssociationEnd;
import org.tanjakostic.jcleancim.model.UmlObjectData;
import org.tanjakostic.jcleancim.model.UmlStereotype;
import org.tanjakostic.jcleancim.model.UmlAssociation.Data;
import org.tanjakostic.jcleancim.model.UmlAssociation.Direction;
import org.tanjakostic.jcleancim.util.ProgrammerErrorException;
import org.tanjakostic.jcleancim.util.Util;

/**
 * @param <O>
 *            Source data for association
 * @param <T>
 *            Source data for association tagged values
 * @author tatjana.kostic@ieee.org
 * @version $Id: AssociationBuilder.java 21 2019-08-12 15:44:50Z dev978 $
 */
abstract public class AssociationBuilder<O, T> extends AbstractObjectBuilderFromEA<UmlAssociation> {
	private static final Logger _logger = Logger.getLogger(AssociationBuilder.class.getName());

	// EA-specific strings for aggregation and association. Note that you may have aggregation
	// even when EA uses "Association", for which aggregation on one of two ends is other than
	// 'none'. So, to determine aggregation, we check both conditions.
	private static final String EA_AGGREGATION = "Aggregation";
	private static final String EA_ASSOCIATION = "Association";
	static final List<String> TYPE_NAMES = Arrays.asList(EA_AGGREGATION, EA_ASSOCIATION);

	// EA-specific strings for navigability of association and of its ends. Note that when you
	// draw an association, EA may create directed one (according to you your local EA application
	// settings), then you have to set by hand direction to 'Unspecified' for BOTH association and
	// one of its ends...
	private static final String EA_DIR_BI_DIRECTIONAL = "Bi-Directional";
	private static final String EA_DIR_UNSPECIFIED = "Unspecified";

	/**
	 * Returns whether the EA connector is either an association or an aggregation (and thus needs
	 * to be retained for processing).
	 */
	public static boolean isAssociationOrAggregation(String type) {
		return EA_ASSOCIATION.equals(type) || EA_AGGREGATION.equals(type);
	}

	// ----------------- instance members ----------

	private UmlObjectData _objData;

	private final AssociationEndBuilder<?, ?> _sourceEnd;
	private final AssociationEndBuilder<?, ?> _targetEnd;

	private String _direction;
	private boolean _directionUnspecified;
	private boolean _biDirectional;
	private final Map<String, String> _taggedValues = new LinkedHashMap<String, String>();

	/**
	 * Constructor.
	 *
	 * @param inData
	 * @param tagsSrc
	 * @param source
	 * @param target
	 * @param model
	 * @param eaHelper
	 * @throws NullPointerException
	 *             if both source and target are null.
	 */
	protected AssociationBuilder(O inData, T tagsSrc, ClassBuilder<?, ?, ?, ?, ?, ?> source,
			ClassBuilder<?, ?, ?, ?, ?, ?> target, EaModelBuilder<?, ?> model, EaHelper eaHelper) {
		Util.ensureNotNull(inData, "inData");
		Util.ensureNotNull(eaHelper, "helper");
		if (source == null && target == null) {
			throw new ProgrammerErrorException("Both source and target null.");
		}

		Integer id = getConnectorID(inData);
		String guid = getConnectorGUID(inData);
		String name = getConnectorName(inData);
		String alias = getConnectorAlias(inData);
		String stereotype = getConnectorStereotypes(inData);
		String visibility = null; // EA does not have it
		String notes = getConnectorNotes(inData);
		initObjData(id, guid, name, alias, stereotype, visibility, notes, eaHelper);
		model.addAssociation(this);

		String direction = getConnectorDirection(inData);
		initOwnData(direction);

		_sourceEnd = createAssociationEnd(inData, tagsSrc, true, source, eaHelper);
		_targetEnd = createAssociationEnd(inData, tagsSrc, false, target, eaHelper);

		List<Map<String, String>> myTaggedValues = fetchTaggedValues(tagsSrc);
		initTaggedValues(myTaggedValues);

		_logger.log(CTOR_LOG_LEVEL, "read from EA: " + toString());
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

	abstract protected String getConnectorDirection(O inData);

	private void initOwnData(String direction) {
		_direction = direction;
		_directionUnspecified = EA_DIR_UNSPECIFIED.equals(_direction);
		_biDirectional = EA_DIR_BI_DIRECTIONAL.equals(_direction);
	}

	abstract protected AssociationEndBuilder<?, ?> createAssociationEnd(O inData, T tagsSrc,
			boolean isSource, ClassBuilder<?, ?, ?, ?, ?, ?> type, EaHelper eaHelper);

	// ------------------------------

	public final AssociationEndBuilder<?, ?> getSourceEnd() {
		return _sourceEnd;
	}

	public final AssociationEndBuilder<?, ?> getTargetEnd() {
		return _targetEnd;
	}

	public final String getDirection() {
		return _direction;
	}

	public final boolean isDirectionUnspecified() {
		return _directionUnspecified;
	}

	public final boolean isBiDirectional() {
		return _biDirectional;
	}

	// -------------------- tagged values ---------------------

	abstract protected List<Map<String, String>> fetchTaggedValues(T inDataTags);

	protected final void initTaggedValues(List<Map<String, String>> myTaggedValuesFields) {
		for (Map<String, String> m : myTaggedValuesFields) {
			String name = m.get(EA.CONN_TGVAL_NAME);
			String value = m.get(EA.CONN_TGVAL_VALUE);
			getTaggedValues().put(name, Util.null2empty(value));
		}
	}

	public final Map<String, String> getTaggedValues() {
		return _taggedValues;
	}

	// ---------------------------

	/** Model builder should call this method to cross-check initialisation is correct. */
	public final void ensureAssociationsOfEndClassesInitialised() {
		String msg = "Association " + getObjData().getName() + " (id=" + getObjData().getId()
				+ ") has";
		if (getSourceEnd().getType() == null && getTargetEnd().getType() == null) {
			throw new RuntimeException((msg + " both source and target null: " + toString()));
		} else if (getSourceEnd().getType() == null) {
			throw new RuntimeException((msg + " null source: " + toString()));
		} else if (getTargetEnd().getType() == null) {
			throw new RuntimeException((msg + " null target: " + toString()));
		}
		if (!getSourceEnd().getType().getAssociationTargetEndClasses()
				.contains(getTargetEnd().getType())) {
			throw new ProgrammerErrorException("Source missing" + toString());
		}
		if (!getTargetEnd().getType().getAssociationSourceEndClasses()
				.contains(getSourceEnd().getType())) {
			throw new ProgrammerErrorException("Target missing" + toString());
		}
	}

	// =====================================================

	@Override
	public String toString() {
		String result = "AssociationBuilder [_sourceEnd=" + _sourceEnd;
		result += ", _targetEnd=" + _targetEnd;
		result += ", _objData=" + _objData;
		result += ", _direction=" + _direction;
		result += ", _directionUnspecified=" + _directionUnspecified;
		result += ", _biDirectional=" + _biDirectional;
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
	public final void doBuild() {
		UmlAssociationEnd sourceEnd = getSourceEnd().build();
		UmlAssociationEnd targetEnd = getTargetEnd().build();
		Direction dir = isBiDirectional() ? Direction.biDirectional
				: (isDirectionUnspecified() ? Direction.unspecified : Direction.directed);
		Data data = new Data(dir);

		setResult(sourceEnd.getType().addAssociation(sourceEnd, targetEnd, getObjData(), data));

		for (Entry<String, String> entry : getTaggedValues().entrySet()) {
			getResult().addTaggedValue(entry.getKey(), entry.getValue());
		}
	}
}
