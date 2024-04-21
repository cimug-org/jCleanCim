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
import java.util.List;

import org.apache.log4j.Logger;
import org.tanjakostic.jcleancim.model.UmlClass;
import org.tanjakostic.jcleancim.model.UmlObjectData;
import org.tanjakostic.jcleancim.model.UmlPackage;
import org.tanjakostic.jcleancim.model.UmlSkipped;
import org.tanjakostic.jcleancim.model.UmlSkipped.Data;
import org.tanjakostic.jcleancim.model.UmlStereotype;
import org.tanjakostic.jcleancim.model.UmlVisibility;
import org.tanjakostic.jcleancim.util.ProgrammerErrorException;
import org.tanjakostic.jcleancim.util.Util;

/**
 * @param <E>
 *            Source data for skipped element
 * @param <S>
 *            Source data for skipped element's diagrams
 * @param <C>
 *            Source data for skipped connector
 * @param <D>
 *            Source data for diagram
 * @author tatjana.kostic@ieee.org
 * @version $Id: SkippedBuilder.java 21 2019-08-12 15:44:50Z dev978 $
 */
abstract public class SkippedBuilder<E, S, C, D> extends AbstractObjectBuilderFromEA<UmlSkipped> {
	private static final Logger _logger = Logger.getLogger(SkippedBuilder.class.getName());

	private UmlObjectData _objData;

	private PackageBuilder<?, ?, ?, ?, ?, ?> _containingPackage;
	private ClassBuilder<?, ?, ?, ?, ?, ?> _containingClass;
	private final boolean _isConnector;

	private UmlSkipped.Kind _kind;
	private String _otherEndName;
	private final List<DiagramBuilder<?>> _diagrams = new ArrayList<DiagramBuilder<?>>();

	/**
	 * Constructor. Creates skipped relationship or element for class or package. Visibility is
	 * always set to {@link UmlVisibility#PUBLIC}.
	 *
	 * @param inDataE
	 * @param itemsSrc
	 * @param inDataC
	 * @param p
	 * @param c
	 * @param model
	 * @param eaHelper
	 */
	protected SkippedBuilder(E inDataE, S itemsSrc, C inDataC, PackageBuilder<?, ?, ?, ?, ?, ?> p,
			ClassBuilder<?, ?, ?, ?, ?, ?> c, EaModelBuilder<?, ?> model, EaHelper eaHelper) {
		if (inDataE == null && inDataC == null) {
			throw new ProgrammerErrorException("Both inDataE and inDataC null.");
		} else if (inDataE != null && inDataC != null) {
			throw new ProgrammerErrorException("Both inDataE and inDataC non-null.");
		}
		if (inDataE != null) {
			Util.ensureNotNull(itemsSrc, "itemsSrc");
		}
		if (p == null && c == null) {
			throw new ProgrammerErrorException("Both p and c null.");
		} else if (p != null && c != null) {
			throw new ProgrammerErrorException("Both p and c non-null.");
		}
		Util.ensureNotNull(model, "model");
		Util.ensureNotNull(eaHelper, "helper");

		_isConnector = (inDataC != null);

		if (c != null && p == null) {
			_containingClass = c;
			_containingPackage = null;
		} else if (p != null && c == null) {
			_containingClass = null;
			_containingPackage = p;
		}

		if (!_isConnector) {
			assert (inDataE != null);
			Integer id = getElementID(inDataE);
			String guid = getElementGUID(inDataE);
			String name = getElementName(inDataE);
			String alias = getElementAlias(inDataE);
			String stereotype = getElementStereotypes(inDataE);
			String visibility = null; // we don't care
			String notes = getElementNotes(inDataE);
			initObjData(id, guid, name, alias, stereotype, visibility, notes, eaHelper);

			String type = getElementType(inDataE);
			String otherEndName = "";
			initOwnDataElem(type, otherEndName);

			createAndAddDiagrams(itemsSrc, eaHelper);
		} else {
			assert (inDataC != null);
			Integer id = getConnectorID(inDataC);
			String guid = getConnectorGUID(inDataC);
			String name = getConnectorName(inDataC);
			String alias = getConnectorAlias(inDataC);
			String stereotype = getConnectorStereotypes(inDataC);
			String visibility = null; // EA does not have it
			String notes = getConnectorNotes(inDataC);
			initObjData(id, guid, name, alias, stereotype, visibility, notes, eaHelper);

			String type = getConnectorType(inDataC);
			Integer clientId = getConnectorClientID(inDataC);
			Integer supplierId = getConnectorSupplierID(inDataC);
			String clientInfo = model.findElementTypeAndName(clientId);
			String supplierInfo = model.findElementTypeAndName(supplierId);
			initOwnDataConn(type, clientId, supplierId, clientInfo, supplierInfo);
		}
		_logger.log(CTOR_LOG_LEVEL, "read from EA: " + toString());
	}

	abstract protected Integer getElementID(E inDataE);

	abstract protected String getElementGUID(E inDataE);

	abstract protected String getElementName(E inDataE);

	abstract protected String getElementAlias(E inDataE);

	abstract protected String getElementStereotypes(E inDataE);

	abstract protected String getElementNotes(E inDataE);

	abstract protected String getElementType(E inDataE);

	abstract protected Integer getConnectorID(C inDataC);

	abstract protected String getConnectorGUID(C inDataC);

	abstract protected String getConnectorName(C inDataC);

	abstract protected String getConnectorAlias(C inDataC);

	abstract protected String getConnectorStereotypes(C inDataC);

	abstract protected String getConnectorNotes(C inDataC);

	abstract protected String getConnectorType(C inDataC);

	abstract protected Integer getConnectorClientID(C inDataC);

	abstract protected Integer getConnectorSupplierID(C inDataC);

	private void initObjData(Integer id, String guid, String name, String alias, String stereotype,
			String visibility, String notes, EaHelper eaHelper) {
		_objData = new UmlObjectData(id, guid, name, alias, new UmlStereotype(stereotype),
				visibility, eaHelper.getRawText(notes), eaHelper.getHtmlText(notes));
	}

	private void initOwnDataElem(String type, String otherEndName) {
		_kind = UmlSkipped.Kind.findForValue(type);
		_otherEndName = otherEndName;
	}

	private void initOwnDataConn(String type, Integer clientId, Integer supplierId,
			String clientInfo, String supplierInfo) {
		_kind = UmlSkipped.Kind.findForValue(type);

		Integer myId = (getContainingClass() != null) ? getObjData().getId()
				: getContainingPackage().getEaElementID();
		if (clientId.equals(myId)) {
			_otherEndName = supplierInfo;
		} else if (supplierId.equals(myId)) {
			_otherEndName = clientInfo;
		} else {
			_otherEndName = "?";
		}
	}

	private void createAndAddDiagrams(S itemsSrc, EaHelper eaHelper) {
		List<D> dias = collectDiagrams(itemsSrc);
		for (D dia : dias) {
			getDiagrams().add(createDiagram(dia, eaHelper));
		}
	}

	abstract protected List<D> collectDiagrams(S itemsSrc);

	abstract protected DiagramBuilder<?> createDiagram(D item, EaHelper eaHelper);

	// ------------------

	public final PackageBuilder<?, ?, ?, ?, ?, ?> getContainingPackage() {
		return _containingPackage;
	}

	public final ClassBuilder<?, ?, ?, ?, ?, ?> getContainingClass() {
		return _containingClass;
	}

	public final boolean isConnector() {
		return _isConnector;
	}

	public final UmlSkipped.Kind getKind() {
		return _kind;
	}

	public final String getOtherEndName() {
		return _otherEndName;
	}

	public final List<DiagramBuilder<?>> getDiagrams() {
		return _diagrams;
	}

	// ---------------------

	@Override
	public String toString() {
		String result = "SkippedBuilder [, _kind=" + _kind;
		result += ", _isConnector=" + _isConnector;
		result += ", _objData=" + _objData;
		if (_containingPackage != null) {
			result += ", _containingPackage=" + _containingPackage.getObjData().getName();
		}
		if (_containingClass != null) {
			result += ", _containingClass=" + _containingClass.getObjData().getName();
		}
		result += ", _otherEndName=" + _otherEndName + "]";
		return result;
	}

	// ---------------------

	@Override
	public final UmlObjectData getObjData() {
		return _objData;
	}

	@Override
	protected final void doBuild() {
		UmlPackage p = (getContainingPackage() != null) ? getContainingPackage().getResult() : null;
		UmlClass c = (getContainingClass() != null) ? getContainingClass().getResult() : null;
		if (p == null && c == null) {
			throw new ProgrammerErrorException(String.format(
					"Container for skipped should have been built: %s.", getObjData().toString()));
		}

		Data data = new Data(getKind(), isConnector(), getOtherEndName());
		if (p != null) {
			setResult(p.addSkippedUmlItem(getObjData(), data));
		} else if (c != null) {
			setResult(c.addSkippedUmlItem(getObjData(), data));
		}
	}
}
