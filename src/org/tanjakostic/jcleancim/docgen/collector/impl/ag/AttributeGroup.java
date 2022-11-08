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

package org.tanjakostic.jcleancim.docgen.collector.impl.ag;

import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;
import org.tanjakostic.jcleancim.docgen.collector.AGSpec;
import org.tanjakostic.jcleancim.model.UmlAttribute;
import org.tanjakostic.jcleancim.model.UmlClass;

/**
 * Helper class, used to group attributes of a class. It is handy for doc generation of fancy
 * IEC61850 tables for LNs and CDCs.
 * <p>
 * The group lists first native then inherited attributes, and may have a name. In case of a CIM
 * class, it will return a single attribute group with null name, while the class representing
 * IEC61850 LN or CDC will return multiple attribute groups, in the order suitable for creating the
 * doc.
 * <p>
 * FIXME: consolidate with AGSpec !
 *
 * @author tatjana.kostic@ieee.org
 * @author laurent.guise@art-et-histoire.com
 * @version $Id: AttributeGroup.java 27 2019-11-23 16:29:38Z dev978 $
 */
public class AttributeGroup {
	private static final Logger _logger = Logger.getLogger(AttributeGroup.class.getName());

	public static Collection<AttributeGroup> initDaGroups(UmlClass c) {
		Set<AttributeGroup> result = new LinkedHashSet<>();
		Collection<UmlAttribute> attributes = c.getAttributes();
		Set<UmlAttribute> inheritedAttributes = c.getInheritedAttributes();
		if (!attributes.isEmpty() || !inheritedAttributes.isEmpty()) {
			result.add(new AttributeGroup(attributes, inheritedAttributes));
		}
		return result;
	}

	public static Collection<AttributeGroup> initCdcGroups(UmlClass c) {
		return initLnCdcGroups(CDCAttributeGroupKind.values(), c);
	}

	public static Collection<AttributeGroup> initLnGroups(UmlClass c) {
		return initLnCdcGroups(LNDataObjectGroupKind.values(), c);
	}

	private static Collection<AttributeGroup> initLnCdcGroups(CategoryKind[] kinds, UmlClass c) {
		Collection<AttributeGroup> result = new LinkedHashSet<>();

		for (CategoryKind kind : kinds) {
			Collection<UmlAttribute> natives = selectAttributes(kind, c.getAttributes());
			Collection<UmlAttribute> inherited = selectAttributes(kind, c.getInheritedAttributes());
			if (!(natives.isEmpty() && inherited.isEmpty())) {
				Map<String, AGSpec> predefinedAGSpecs = AGSpec.getPredefinedAGSpecs();
				AGSpec spec = predefinedAGSpecs.get(kind.getKindTag());
				if (kind.getKindTag().contains("Null")) {
					_logger.error(
							"undefined category " + spec.toString() + " for class " + c.getName());
				}
				result.add(new AttributeGroup(spec, natives, inherited));
			}
		}
		return result;
	}

	private static Collection<UmlAttribute> selectAttributes(CategoryKind category,
			Collection<UmlAttribute> attributes) {
		Set<UmlAttribute> result = new LinkedHashSet<>();
		for (UmlAttribute a : attributes) {
			Set<String> superclassNames = category.getTypesSuperclassNames();
			for (String supName : superclassNames) {
				if (a.getType().hasSuperclass(supName)) {
					result.add(a);
				}
			}
			Set<String> packageNames = category.getTypesPackageNames();
			for (String pckName : packageNames) {
				if (a.getType().getContainingPackage().isInOrUnderPackage(pckName)) {
					result.add(a);
				}
			}
		}
		return result;
	}

	// -------------------------

	private final AGSpec _agSpec;
	private final Collection<UmlAttribute> _nativeAttributes;
	private final Collection<UmlAttribute> _inheritedAttributes;

	public AttributeGroup(Collection<UmlAttribute> nativeAttributes,
			Collection<UmlAttribute> inheritedAttributes) {
		this(null, nativeAttributes, inheritedAttributes);
	}

	public AttributeGroup(AGSpec agSpec, Collection<UmlAttribute> nativeAttributes,
			Collection<UmlAttribute> inheritedAttributes) {
		_agSpec = agSpec;
		_nativeAttributes = nativeAttributes;
		_inheritedAttributes = inheritedAttributes;
	}

	public AGSpec getAgSpec() {
		return _agSpec;
	}

	public Collection<UmlAttribute> getNativeAttributes() {
		return _nativeAttributes;
	}

	public Collection<UmlAttribute> getInheritedAttributes() {
		return _inheritedAttributes;
	}
}
