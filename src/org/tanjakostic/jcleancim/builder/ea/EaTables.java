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
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.tanjakostic.jcleancim.util.ApplicationException;
import org.tanjakostic.jcleancim.util.ProgrammerErrorException;
import org.tanjakostic.jcleancim.util.Util;

/**
 * An attempt to speed up reading the .eap model.
 * <p>
 * The constructor takes an instance of {@link EaSelector} that performs access to the underlying
 * repository and produces tables (maps) as a simple initial in-memory model. For those scenarios
 * where we don't need to export diagrams or XMI from EA (with its repository/project methods),
 * after construction of this instance we can safely close the EA repository.
 *
 * @author tatjana.kostic@ieee.org
 * @version $Id: EaTables.java 21 2019-08-12 15:44:50Z dev978 $
 */
public class EaTables {
	private static final Logger _logger = Logger.getLogger(EaTables.class.getName());

	/** Used for all but diagram's and connector end's stereotypes. */
	private final Map<String, String> _stereosPerOwnerUuid;

	/**
	 * Used for connector end stereotypes; key is connector UUID, values are src and dest stereos.
	 * This one is typically a very short map, with keys for only those connectors whose ends
	 * actually have stereotypes.
	 */
	private final Map<String, List<String>> _aeStereosPerConnUuid = new LinkedHashMap<String, List<String>>();

	private final Map<Integer, List<Map<String, String>>> _packagesPerOwnerId;

	private final Map<Integer, List<Map<String, String>>> _diagramsPerOwnerId;

	private final Map<String, Map<Integer, Map<String, String>>> _objectsPerTypePerId;
	private final Map<Integer, List<Map<String, String>>> _objectsPerObjectOwnerId;
	private final Map<Integer, List<Map<String, String>>> _constraintsPerObjectId;
	private final Map<Integer, List<Map<String, String>>> _taggedValuesPerObjectId;

	private final Map<Integer, List<Map<String, String>>> _attributesPerOwnerId;
	private final Map<Integer, List<Map<String, String>>> _constraintsPerAttributeId;
	private final Map<Integer, List<Map<String, String>>> _taggedValuesPerAttributeId;

	private final Map<Integer, List<Map<String, String>>> _operationsPerOwnerId;
	private final Map<Integer, List<Map<String, String>>> _taggedValuesPerOperationId;
	private final Map<Integer, List<Map<String, String>>> _parametersPerOwnerId;

	private final Map<String, Map<Integer, Map<String, String>>> _connectorsPerTypePerId;
	private final Map<Integer, List<Map<String, String>>> _taggedValuesPerConnectorId;
	private final Map<Integer, List<List<Map<String, String>>>> _roleTagsPerConnectorId;

	/**
	 * Constructor; loads all the relevant content from the repository into simple data structures
	 * (maps).
	 *
	 * @param selector
	 *            accesses EA data
	 * @throws ApplicationException
	 */
	public EaTables(EaSelector selector, boolean skipTiming) throws ApplicationException {
		Util.logSubtitle(Level.INFO, "running bulk queries...");
		long start = System.currentTimeMillis();

		List<Map<String, String>> rows = selector.select("t_xref", EA.XREF_TAGS, skipTiming);
		_stereosPerOwnerUuid = initStereosPerOwnerUuid(rows, _aeStereosPerConnUuid);

		rows = selector.select("t_package", EA.PACKAGE_TAGS, skipTiming);
		_packagesPerOwnerId = initPerOwnerId(rows, EA.PACKAGE_OWNER_ID);

		rows = selector.select("t_diagram", EA.DIA_TAGS, skipTiming);
		_diagramsPerOwnerId = initPerOwnerId(rows, EA.DIA_OWNER_ID);

		rows = selector.select("t_object", EA.ELEM_TAGS, skipTiming); /* EA.ELEM_TAGS_OUT */
		_objectsPerTypePerId = initPerTypePerId(rows, EA.ELEM_TYPE, EA.ELEM_ID,
				_stereosPerOwnerUuid, null);
		_objectsPerObjectOwnerId = initPerOwnerId(rows, EA.PARENT_ID, _stereosPerOwnerUuid, false);

		finishInitialisePackagesFromObjects();

		rows = selector.select("t_objectconstraint", EA.CLASS_CONSTR_TAGS, skipTiming);
		_constraintsPerObjectId = initPerOwnerId(rows, EA.ELEM_ID);

		rows = selector.select("t_objectproperties", EA.ELEM_TGVAL_TAGS, skipTiming);
		_taggedValuesPerObjectId = initPerOwnerId(rows, EA.ELEM_ID);

		rows = selector.select("t_attribute", EA.ATTR_TAGS, skipTiming); /* EA.ATTR_TAGS_OUT */
		_attributesPerOwnerId = initPerOwnerId(rows, EA.ELEM_ID, _stereosPerOwnerUuid, false);

		rows = selector.select("t_attributeconstraints", EA.ATTR_CONSTR_TAGS, skipTiming);
		_constraintsPerAttributeId = initPerOwnerId(rows, EA.ATTR_ID);

		rows = selector.select("t_attributetag", EA.ATTR_TGVAL_TAGS, skipTiming);
		_taggedValuesPerAttributeId = initPerOwnerId(rows, EA.ATTR_TGVAL_OWNER_ID);

		rows = selector.select("t_operation", EA.OP_TAGS, skipTiming); /* EA.OP_TAGS_OUT */
		_operationsPerOwnerId = initPerOwnerId(rows, EA.OP_OWNER_ID, _stereosPerOwnerUuid, false);

		rows = selector.select("t_operationtag", EA.OP_TGVAL_TAGS, skipTiming);
		_taggedValuesPerOperationId = initPerOwnerId(rows, EA.OP_TGVAL_OWNER_ID);

		rows = selector.select("t_operationparams", EA.PAR_TAGS, skipTiming); /* EA.PAR_TAGS_OUT */
		_parametersPerOwnerId = initPerOwnerId(rows, EA.PAR_OWNER_ID, _stereosPerOwnerUuid, true);

		rows = selector.select("t_connector", EA.CONN_TAGS, skipTiming); /* EA.CONN_TAGS_OUT */
		_connectorsPerTypePerId = initPerTypePerId(rows, EA.CONN_TYPE, EA.CONN_ID,
				_stereosPerOwnerUuid, _aeStereosPerConnUuid);

		rows = selector.select("t_connectortag", EA.CONN_TGVAL_TAGS, skipTiming);
		_taggedValuesPerConnectorId = initPerOwnerId(rows, EA.CONN_TGVAL_OWNER_ID);

		rows = selector.select("t_taggedvalue", EA.ROLE_TGVAL_TAGS, skipTiming);
		_roleTagsPerConnectorId = initRoleTagsPerConnectorUuid(rows);

		Util.logCompletion(Level.INFO, "done bulk queries.", start, skipTiming);
	}

	/**
	 * Returns the "regular" map of stereotypes, plus fills (if applicable)
	 * <code>aeStereosPerConnUuid</code> with the stereotypes for connector ends.
	 */
	private Map<String, String> initStereosPerOwnerUuid(List<Map<String, String>> table,
			Map<String, List<String>> aeStereosPerConnUuid) {
		if (table == null) {
			return Collections.emptyMap();
		}

		Map<String, String> result = new LinkedHashMap<String, String>();
		for (Map<String, String> row : table) {
			String name = row.get(EA.XREF_NAME);
			if (!EA.XREF_NAME_STEREOS.equals(name)) {
				continue;
			}

			String type = row.get(EA.XREF_TYPE);
			String guid = row.get(EA.XREF_CLIENT);
			String description = row.get(EA.XREF_DESCRIPTION);
			String stereos = EA.extractStereotypes(description);

			// normal case: everything except for connector ends:
			if (!(EA.XREF_TYPE_CONN_SRC.equals(type) || EA.XREF_TYPE_CONN_DEST.equals(type))) {
				result.put(guid, stereos);
			} else {
				// connector ends: first is SRC, second is DEST:
				List<String> aeGuids = aeStereosPerConnUuid.get(guid);
				if (aeGuids == null) {
					aeGuids = Arrays.asList(new String[] { "", "" });
					aeStereosPerConnUuid.put(guid, aeGuids);
				}
				if (EA.XREF_TYPE_CONN_SRC.equals(type)) {
					aeGuids.set(0, stereos);
				}
				if (EA.XREF_TYPE_CONN_DEST.equals(type)) {
					aeGuids.set(1, stereos);
				}
			}
		}
		return result;
	}

	private Map<Integer, List<List<Map<String, String>>>> initRoleTagsPerConnectorUuid(
			List<Map<String, String>> rows) {
		if (rows == null) {
			return Collections.emptyMap();
		}

		// collect GUIDs for connectors found
		Set<String> connGuids = new HashSet<>();
		for (Map<String, String> row : rows) {
			String connGuid = row.get(EA.ROLE_TGVAL_OWNER_ID);
			connGuids.add(connGuid);
		}

		// from all the connectors, pick only those required; key is GUID, value is row
		Map<String, Map<String, String>> connRowsPerGuid = findConnectorRowsPerGuid(connGuids);

		Map<Integer, List<List<Map<String, String>>>> result = new LinkedHashMap<>();
		for (Map<String, String> row : rows) {
			String connGuid = row.get(EA.ROLE_TGVAL_OWNER_ID);

			Map<String, String> connRow = connRowsPerGuid.get(connGuid);
			if (connRow == null) {
				// this may happen because EA seems to store here tags not only for association
				// ends, but also for (to me unknown and impossible-to-deduce-or-find) connector...
				_logger.debug("+++ no connRow for connGuid = " + connGuid);
				continue;
			}

			Integer id = Integer.valueOf(connRow.get(EA.CONN_ID));
			List<List<Map<String, String>>> fromTos = result.get(id);
			if (fromTos == null) {
				fromTos = new ArrayList<List<Map<String, String>>>();
				fromTos.add(new ArrayList<Map<String, String>>());
				fromTos.add(new ArrayList<Map<String, String>>());
				result.put(id, fromTos);
			}
			int fromToIdx = row.get(EA.ROLE_TGVAL_BASECLASS).equals("ASSOCIATION_TARGET") ? 1 : 0;

			Map<String, String> tvRow = new HashMap<String, String>();
			tvRow.put(EA.ROLE_TGVAL_NAME, row.get(EA.ROLE_TGVAL_NAME));
			tvRow.put(EA.ROLE_TGVAL_VALUE, row.get(EA.ROLE_TGVAL_VALUE));
			fromTos.get(fromToIdx).add(tvRow);
		}
		return result;
	}

	private Map<String, Map<String, String>> findConnectorRowsPerGuid(Set<String> connGuids) {
		Map<String, Map<String, String>> result = new HashMap<>();
		for (Map<Integer, Map<String, String>> ofTypes : _connectorsPerTypePerId.values()) {
			for (Entry<Integer, Map<String, String>> perIds : ofTypes.entrySet()) {
				Map<String, String> row = perIds.getValue();
				String guid = row.get(EA.EA_GUID);
				if (connGuids.contains(guid)) {
					result.put(guid, row);
				}
			}
		}
		return result;
	}

	// {id, listOfRows{key, value}]}
	private Map<Integer, List<Map<String, String>>> initPerOwnerId(List<Map<String, String>> table,
			String ownerIdTag) {
		return initPerOwnerId(table, ownerIdTag, null, false);
	}

	// {id, listOfRows{key, value}]}
	private Map<Integer, List<Map<String, String>>> initPerOwnerId(List<Map<String, String>> table,
			String ownerIdTag, Map<String, String> stereosPerUuid, boolean deduceParamAlias,
			String... deducedTags) {
		if (table == null) {
			return Collections.emptyMap();
		}

		Map<Integer, List<Map<String, String>>> result = new LinkedHashMap<Integer, List<Map<String, String>>>();
		for (Map<String, String> row : table) {
			if (stereosPerUuid != null) {
				row.put(EA.DEDUCED_STEREOS,
						Util.null2empty(stereosPerUuid.get(row.get(EA.EA_GUID))));
			}
			if (deduceParamAlias) {
				row.put(EA.PAR_ALIAS, EA.extractAlias(row.get(EA.PAR_STYLE)));
			}
			for (String col : deducedTags) {
				if (row.get(col) == null) {
					_logger.warn("  >>> seeting deduced tags to empty string");
					row.put(col, "");
				}
			}
			Integer ownerId = Util.parseIntZero(row.get(ownerIdTag));
			List<Map<String, String>> itemsPerObjId = result.get(ownerId);
			if (itemsPerObjId == null) {
				itemsPerObjId = new ArrayList<Map<String, String>>();
			}
			itemsPerObjId.add(row);
			result.put(ownerId, itemsPerObjId);
		}
		return result;
	}

	// {type, {id, row}}
	private Map<String, Map<Integer, Map<String, String>>> initPerTypePerId(
			List<Map<String, String>> table, String typeTag, String idTag,
			Map<String, String> stereosPerUuid, Map<String, List<String>> aeStereosPerConnUuid) {
		if (table == null) {
			return Collections.emptyMap();
		}
		Util.ensureNotNull(stereosPerUuid, "stereosPerUuid");

		Map<String, Map<Integer, Map<String, String>>> result = new LinkedHashMap<String, Map<Integer, Map<String, String>>>();
		for (Map<String, String> row : table) {
			String guid = row.get(EA.EA_GUID);

			row.put(EA.DEDUCED_STEREOS, Util.null2empty(stereosPerUuid.get(guid)));

			if (aeStereosPerConnUuid != null) {
				List<String> aeStereos = aeStereosPerConnUuid.get(guid);
				if (aeStereos != null) {
					row.put(EA.CONN_FROM_STEREOS, Util.null2empty(aeStereos.get(0)));
					row.put(EA.CONN_TO_STEREOS, Util.null2empty(aeStereos.get(1)));
				} else {
					row.put(EA.CONN_FROM_STEREOS, "");
					row.put(EA.CONN_TO_STEREOS, "");
				}

				row.put(EA.CONN_ALIAS, EA.extractAlias(row.get(EA.CONN_STYLEEX)));
				row.put(EA.CONN_FROM_ALIAS, EA.extractAlias(row.get(EA.CONN_FROM_STYLE)));
				row.put(EA.CONN_TO_ALIAS, EA.extractAlias(row.get(EA.CONN_TO_STYLE)));

				row.put(EA.CONN_FROM_NAV, EA.extractNavigability(row.get(EA.CONN_FROM_STYLE)));
				row.put(EA.CONN_TO_NAV, EA.extractNavigability(row.get(EA.CONN_TO_STYLE)));
			}
			String type = row.get(typeTag);
			Map<Integer, Map<String, String>> itemsPerType = result.get(type);
			if (itemsPerType == null) {
				itemsPerType = new LinkedHashMap<Integer, Map<String, String>>();
				result.put(type, itemsPerType);
			}
			Integer id = Integer.valueOf(row.get(idTag));
			itemsPerType.put(id, row);
		}
		return result;
	}

	/** Some data for packages is split into 2 tables - sic! */
	private void finishInitialisePackagesFromObjects() {
		for (Entry<Integer, List<Map<String, String>>> entries : _packagesPerOwnerId.entrySet()) {
			List<Map<String, String>> subPackagesRows = entries.getValue();
			for (Map<String, String> subPackageRow : subPackagesRows) {
				if (isRootPackage(subPackageRow)) {
					continue; // we don't initialise root; it's also not in t_object table...
				}
				String guid = subPackageRow.get(EA.EA_GUID);
				Map<String, String> elemRow = findPackageAsObject(guid);
				if (elemRow.isEmpty()) {
					throw new ProgrammerErrorException("Sub-package with guid=" + guid
							+ " should have been found in object/elements table.");
				}
				for (String tag : EA.PACKAGE_AS_ELEM_TAGS) {
					String val = elemRow.get(tag);
					subPackageRow.put(tag, val);
				}
			}
		}
	}

	private boolean isRootPackage(Map<String, String> elemRow) {
		return "0".equals(elemRow.get(EA.PACKAGE_OWNER_ID));
	}

	// ===========================================

	/**
	 * Returns ordered packages in <code>containingPackageId</code> if existing, empty list
	 * otherwise.
	 */
	public List<Map<String, String>> findPackageSubpackages(Integer containingPackageId,
			String name) {
		List<Map<String, String>> pObjects = findRows(containingPackageId, _packagesPerOwnerId);

		return orderItemsOrCatchScrewedOrdering("package", pObjects, name, EA.PACKAGE_POS,
				EA.PACKAGE_NAME);
	}

	private List<Map<String, String>> orderItemsOrCatchScrewedOrdering(String what,
			List<Map<String, String>> rows, String containerName, String posTag, String nameTag) {
		List<String> diagnosis = new ArrayList<String>();
		diagnosis.add(String.format(
				"+++ EA ordering problem for %d %s(s) in %s (manually move "
						+ "back/forth a %s to initiate EA internal ordering update!):%n",
				Integer.valueOf(rows.size()), what, containerName, what));
		Set<Map<String, String>> all = new LinkedHashSet<Map<String, String>>();

		Map<Integer, Map<String, String>> result = new TreeMap<Integer, Map<String, String>>();
		for (Map<String, String> row : rows) {
			Integer pos = Util.parseIntZero(row.get(posTag));
			String subName = row.get(nameTag);

			String duplicatePos = (result.containsKey(pos)) ? (" " + EaModelBuilder.DUPLICATE) : "";
			result.put(pos, row);
			all.add(row);

			String msg = String.format("   +++ %s %s: pos = %s%s%n", what, subName, pos,
					duplicatePos);
			diagnosis.add(msg);
		}

		if (all.size() != result.size()) {
			_logger.error(diagnosis.toString());
			return new ArrayList<Map<String, String>>(all);
		}
		return new ArrayList<Map<String, String>>(result.values());
	}

	/**
	 * Returns ordered diagrams under object <code>containingObjectId</code> if existing, empty list
	 * otherwise; if <code>containingObjectId</code> = 0, then the returned diagrams belong to
	 * packages and it makes no sense to order them here, because you need to further filter the
	 * items for their packageId first, then order the result (sorry, that's how EA stores
	 * diagrams...).
	 */
	public List<Map<String, String>> findObjectDiagrams(Integer containingObjectId,
			String containerName) {
		List<Map<String, String>> diagItems = findRows(containingObjectId, _diagramsPerOwnerId);

		if (Util.ZERO.equals(containingObjectId)) {
			return diagItems;
		}
		return orderItemsOrCatchScrewedOrdering("diagram", diagItems, containerName, EA.DIA_POS,
				EA.DIA_NAME);
	}

	/**
	 * Returns ordered diagrams under package <code>containingPackageId</code> if existing, empty
	 * list otherwise.
	 */
	public List<Map<String, String>> findPackageDiagrams(Integer containingPackageId,
			String containerName) {
		List<Map<String, String>> rows = findObjectDiagrams(Util.ZERO, containerName);

		List<Map<String, String>> result = new ArrayList<Map<String, String>>();
		for (Map<String, String> row : rows) {
			Integer pId = Integer.valueOf(row.get(EA.DIA_PCKG_ID));
			if (containingPackageId.equals(pId)) {
				result.add(row);
			}
		}
		return orderItemsOrCatchScrewedOrdering("diagram", result, containerName, EA.DIA_POS,
				EA.DIA_NAME);
	}

	/**
	 * Returns ordered classifiers in <code>containingPackageId</code> if existing, empty list
	 * otherwise.
	 */
	public List<Map<String, String>> findPackageClasses(Integer containingPackageId, String name) {
		List<Map<String, String>> packageAsObjectRows = findRows(Integer.valueOf(0),
				_objectsPerObjectOwnerId);

		List<Map<String, String>> result = new ArrayList<Map<String, String>>();
		for (Map<String, String> row : packageAsObjectRows) {
			String type = row.get(EA.ELEM_TYPE);
			Integer pckId = Integer.valueOf(row.get(EA.PACKAGE_ID));
			boolean isClassifierWithinPackage = containingPackageId.equals(pckId)
					&& ClassBuilder.isClassOrEaInterface(type);
			if (isClassifierWithinPackage) {
				result.add(row);
			}
		}
		return orderItemsOrCatchScrewedOrdering("class", result, name, EA.ELEM_POS, EA.ELEM_NAME);
	}

	/**
	 * Returns non-classifiers and non-packages in package <code>containingPackageId</code> if
	 * existing, empty list otherwise.
	 */
	public List<Map<String, String>> findPackageEmbeddedElements(Integer containingPackageId) {
		List<Map<String, String>> packageAsObjectRows = findRows(Integer.valueOf(0),
				_objectsPerObjectOwnerId);

		List<Map<String, String>> result = new ArrayList<Map<String, String>>();
		for (Map<String, String> row : packageAsObjectRows) {
			String type = row.get(EA.ELEM_TYPE);
			Integer pckId = Integer.valueOf(row.get(EA.PACKAGE_ID));
			if (ClassBuilder.isClassOrEaInterface(type) || PackageBuilder.isEaPackage(type)) {
				continue;
			}
			if (containingPackageId.equals(pckId)) {
				result.add(row);
			}
		}
		return result;
	}

	/**
	 * Returns connectors that include or exclude <code>typeNames</code> for <code>elementId</code>
	 * if found, empty list otherwise.
	 */
	public List<Map<String, String>> findConnectors(boolean include, List<String> typeNames,
			Integer elementId) {
		return findConnectors(true, include, typeNames, elementId);
	}

	/**
	 * Returns all connectors for <code>elementId</code> if found, empty list otherwise.
	 */
	public List<Map<String, String>> findConnectors(Integer elementId) {
		return findConnectors(false, false, null, elementId);
	}

	private List<Map<String, String>> findConnectors(boolean applyFilters, boolean include,
			List<String> typeNames, Integer elementId) {
		List<Map<String, String>> result = new ArrayList<Map<String, String>>();
		for (Entry<String, Map<Integer, Map<String, String>>> connsPerType : _connectorsPerTypePerId
				.entrySet()) {
			if (applyFilters) {
				boolean isInTypeNames = typeNames.contains(connsPerType.getKey());
				if ((include && !isInTypeNames) || !include && isInTypeNames) {
					continue;
				}
			}
			for (Map<String, String> row : connsPerType.getValue().values()) {
				Integer fromId = Integer.valueOf(row.get(EA.CONN_FROM_ID));
				Integer toId = Integer.valueOf(row.get(EA.CONN_TO_ID));
				if (elementId.equals(fromId) || elementId.equals(toId)) {
					result.add(row);
				}
			}
		}
		return result;
	}

	/** Returns type and name (as string) for <code>objectId</code> if found, null otherwise. */
	public String findElementTypeAndName(Integer objectId) {
		return doFindElementTypeAndName(objectId, true);
	}

	/** Returns type (as string) for <code>objectId</code> if found, null otherwise. */
	public String findElementType(Integer objectId) {
		return doFindElementTypeAndName(objectId, false);
	}

	private String doFindElementTypeAndName(Integer objectId, boolean withName) {
		for (Entry<String, Map<Integer, Map<String, String>>> allOfType : _objectsPerTypePerId
				.entrySet()) {
			String type = allOfType.getKey();
			for (Entry<Integer, Map<String, String>> ids : allOfType.getValue().entrySet()) {
				Integer id = ids.getKey();
				if (id.equals(objectId)) {
					if (withName) {
						return type + " '" + Util.null2empty(ids.getValue().get(EA.ELEM_NAME))
								+ "'";
					}
					return type;
				}
			}
		}
		return null;
	}

	/** Returns package as EA object (defined in object table) for package guid. */
	private Map<String, String> findPackageAsObject(String guid) {
		Map<Integer, Map<String, String>> allOfType = _objectsPerTypePerId.get("Package");
		if (allOfType != null) {
			for (Entry<Integer, Map<String, String>> entry : allOfType.entrySet()) {
				Map<String, String> row = entry.getValue();
				if (row.get(EA.EA_GUID).equals(guid)) {
					return row;
				}
			}
		}
		return Collections.emptyMap();
	}

	/**
	 * Returns elements embedded in <code>containingObjectId</code> if existing, empty list
	 * otherwise.
	 */
	public List<Map<String, String>> findClassEmbeddedElements(Integer containingObjectId) {
		return findRows(containingObjectId, _objectsPerObjectOwnerId);
	}

	/**
	 * Returns constraints for <code>containingObjectId</code> if existing, empty list otherwise.
	 */
	public List<Map<String, String>> findObjectConstraints(Integer containingObjectId) {
		return findRows(containingObjectId, _constraintsPerObjectId);
	}

	/** Returns constraints for <code>containingElemId</code> if existing, empty list otherwise. */
	public List<Map<String, String>> findObjectTaggedValues(Integer containingObjectId) {
		return findRows(containingObjectId, _taggedValuesPerObjectId);
	}

	/** Returns attributes for <code>containingClassId</code> if existing, empty list otherwise. */
	public List<Map<String, String>> findAttributes(Integer containingClassId) {
		return findRows(containingClassId, _attributesPerOwnerId);
	}

	/** Returns constraints for <code>containingAttrId</code> if existing, empty list otherwise. */
	public List<Map<String, String>> findAttributeConstraints(Integer containingAttrId) {
		return findRows(containingAttrId, _constraintsPerAttributeId);
	}

	/**
	 * Returns tagged values for <code>containingElemId</code> if existing, empty list otherwise.
	 */
	public List<Map<String, String>> findAttributeTags(Integer containingElemId) {
		return findRows(containingElemId, _taggedValuesPerAttributeId);
	}

	/**
	 * Returns tagged values for <code>containingElemId</code> if existing, empty list otherwise.
	 */
	public List<Map<String, String>> findConnectorTags(Integer containingElemId) {
		return findRows(containingElemId, _taggedValuesPerConnectorId);
	}

	/**
	 * Returns tagged values for source end of <code>containingConnId</code> if existing, empty list
	 * otherwise.
	 */
	public List<Map<String, String>> findConnectorSourceEndTags(Integer containingConnId) {
		return fincConnectorEndTags(0, containingConnId);
	}

	/**
	 * Returns tagged values for target end of <code>containingConnId</code> if existing, empty list
	 * otherwise.
	 */
	public List<Map<String, String>> findConnectorTargetEndTags(Integer containingConnId) {
		return fincConnectorEndTags(1, containingConnId);
	}

	private List<Map<String, String>> fincConnectorEndTags(int index, Integer containingConnId) {
		List<List<Map<String, String>>> perConnIds = _roleTagsPerConnectorId.get(containingConnId);
		if (perConnIds == null) {
			return Collections.emptyList();
		}
		return _roleTagsPerConnectorId.get(containingConnId).get(index);
	}

	/** Returns operations for <code>containingClassId</code> if existing, empty list otherwise. */
	public List<Map<String, String>> findOperations(Integer containingClassId) {
		return findRows(containingClassId, _operationsPerOwnerId);
	}

	/**
	 * Returns ordered parameters for <code>containingOpId</code> if existing, empty list otherwise.
	 */
	public List<Map<String, String>> findOrderedParameters(Integer containingOpId) {
		List<Map<String, String>> paramRows = findRows(containingOpId, _parametersPerOwnerId);

		Map<Integer, Map<String, String>> result = new TreeMap<Integer, Map<String, String>>();
		for (Map<String, String> row : paramRows) {
			String posStr = row.get(EA.PAR_POS);
			Integer pos = (posStr != null && !posStr.trim().isEmpty()) ? Integer.valueOf(posStr)
					: Integer.valueOf(0);
			result.put(pos, row);
		}
		return new ArrayList<Map<String, String>>(result.values());
	}

	/** Returns tagged values for <code>containingOpId</code> if existing, empty list otherwise. */
	public List<Map<String, String>> findOperationTags(Integer containingOpId) {
		return findRows(containingOpId, _taggedValuesPerOperationId);
	}

	private List<Map<String, String>> findRows(Integer containerId,
			Map<Integer, List<Map<String, String>>> rowsPerContainerId) {
		List<Map<String, String>> result = rowsPerContainerId.get(containerId);
		if (result == null) {
			return Collections.emptyList();
		}
		return result;
	}

	// ----------------------------------------
}
