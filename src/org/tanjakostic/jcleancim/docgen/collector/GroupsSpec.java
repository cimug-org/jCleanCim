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

package org.tanjakostic.jcleancim.docgen.collector;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.tanjakostic.jcleancim.util.Util;

/**
 * @author tatjana.kostic@ieee.org
 * @version $Id: GroupsSpec.java 21 2019-08-12 15:44:50Z dev978 $
 */
public class GroupsSpec {

	public static final GroupsSpec DACategories;
	public static final GroupsSpec DOCategories;

	private static final Map<String, GroupsSpec> GROUPS;
	static {
		GROUPS = new LinkedHashMap<String, GroupsSpec>();

		DACategories = createDAGroupsSpecs();
		DOCategories = createDOGroupsSpecs();
	}

	private static GroupsSpec createDAGroupsSpecs() {
		return createAndStoreGroupsSpec("DACategoryKindPrettyStrings",
				AGSpec.getForInstTag(AGSpec.DA_CATEGORY));
	}

	private static GroupsSpec createDOGroupsSpecs() {
		return createAndStoreGroupsSpec("DOCategoryKindPrettyStrings",
				AGSpec.getForInstTag(AGSpec.DO_CATEGORY));
	}

	static GroupsSpec createAndStoreGroupsSpec(String name, Collection<AGSpec> agSpecs) {
		GroupsSpec result = new GroupsSpec(name, agSpecs);
		putPredefined(result);
		return result;
	}

	private static void putPredefined(GroupsSpec tab) {
		GROUPS.put(tab.getName(), tab);
	}

	/** Returns all the predefined categories. */
	public static Map<String, GroupsSpec> getPredefinedGroupsSpecs() {
		return Collections.unmodifiableMap(GROUPS);
	}

	public static List<GroupsSpec> getGroups() {
		List<GroupsSpec> groups = new ArrayList<GroupsSpec>();
		groups.add(GroupsSpec.DACategories);
		groups.add(GroupsSpec.DOCategories);
		return groups;
	}

	// --------------------------

	private final String _name;
	private final Collection<AGSpec> _agSpecs;

	public GroupsSpec(String name, Collection<AGSpec> agSpecs) {
		Util.ensureNotEmpty(name, "name");
		Util.ensureContainsNoNull(agSpecs, "agSpecs");

		System.out.println("");

		_name = name;
		_agSpecs = agSpecs;
	}

	public String getName() {
		return _name;
	}

	public Collection<AGSpec> getAgSpecs() {
		return Collections.unmodifiableCollection(_agSpecs);
	}
}
