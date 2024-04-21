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

import java.io.File;
import java.io.IOException;

import org.apache.log4j.Logger;
import org.tanjakostic.jcleancim.model.UmlClass;
import org.tanjakostic.jcleancim.model.UmlDiagram;
import org.tanjakostic.jcleancim.model.UmlObjectData;
import org.tanjakostic.jcleancim.model.UmlPackage;
import org.tanjakostic.jcleancim.model.UmlStereotype;
import org.tanjakostic.jcleancim.model.UmlStructure;
import org.tanjakostic.jcleancim.model.UmlVisibility;
import org.tanjakostic.jcleancim.model.UmlDiagram.Data;
import org.tanjakostic.jcleancim.model.UmlDiagram.Kind;
import org.tanjakostic.jcleancim.util.ProgrammerErrorException;
import org.tanjakostic.jcleancim.util.Util;
import org.tanjakostic.jcleancim.util.Util.ImageFormat;

/**
 * @param <O>
 *            Source data for diagram
 * @author tatjana.kostic@ieee.org
 * @version $Id: DiagramBuilder.java 21 2019-08-12 15:44:50Z dev978 $
 */
abstract public class DiagramBuilder<O> extends AbstractObjectBuilderFromEA<UmlDiagram> {
	private static final Logger _logger = Logger.getLogger(DiagramBuilder.class.getName());

	public static final ImageFormat DEFAULT_FILE_FORMAT = Util.ImageFormat.PNG;

	// EA-specific sign for portrait (diagram) page layout
	private static final String EA_PORTRAIT = "P";

	private final PackageBuilder<?, ?, ?, ?, ?, ?> _containingPackage;
	private final ClassBuilder<?, ?, ?, ?, ?, ?> _containingClass;
	private UmlObjectData _objData;

	private boolean _portrait;
	private UmlDiagram.Kind _kind;

	/**
	 * Creates diagram that belongs to a package or a class. Visibility is always set to
	 * {@link UmlVisibility#PUBLIC} and alias to empty string (these are not defined in EA).
	 *
	 * @param inData
	 * @param containingPackage
	 * @param containingClass
	 * @param eaHelper
	 * @throws NullPointerException
	 *             if both containingPackage and containingClass null, or if eaDiagram is null, or
	 *             if helper is null.
	 */
	protected DiagramBuilder(O inData, PackageBuilder<?, ?, ?, ?, ?, ?> containingPackage,
			ClassBuilder<?, ?, ?, ?, ?, ?> containingClass, EaHelper eaHelper) {
		Util.ensureNotNull(inData, "inData");
		if (containingPackage == null && containingClass == null) {
			throw new ProgrammerErrorException("Both source and target null.");
		}
		_containingPackage = containingPackage;
		_containingClass = containingClass;
		EaModelBuilder<?, ?> model = getModel();

		Integer id = getDiagramID(inData);
		String guid = getDiagramGUID(inData);
		String name = getDiagramName(inData);
		String alias = null; // EA does not have it
		String stereotype = getDiagramStereotypes(inData);
		String visibility = null; // EA does not have it
		String notes = getDiagramNotes(inData);
		initObjData(id, guid, name, alias, stereotype, visibility, notes, eaHelper);
		model.addDiagram(this);

		String orientation = getDiagramOrientation(inData);
		String type = getDiagramType(inData);
		initOwnData(orientation, type);

		// will set pic only after we have UML containers, to exclude INF from export

		_logger.log(CTOR_LOG_LEVEL, "read from EA: " + toString());
	}

	abstract protected Integer getDiagramID(O inData);

	abstract protected String getDiagramGUID(O inData);

	abstract protected String getDiagramName(O inData);

	abstract protected String getDiagramStereotypes(O inData);

	abstract protected String getDiagramNotes(O inData);

	private void initObjData(Integer id, String guid, String name, String alias, String stereotype,
			String visibility, String notes, EaHelper eaHelper) {
		_objData = new UmlObjectData(id, guid, name, alias, new UmlStereotype(stereotype),
				visibility, eaHelper.getRawText(notes), eaHelper.getHtmlText(notes));
	}

	abstract protected String getDiagramOrientation(O inData);

	abstract protected String getDiagramType(O inData);

	private void initOwnData(String orientation, String type) {
		_portrait = EA_PORTRAIT.equals(orientation);
		_kind = Kind.findForValue(type);
		if (getKind() == Kind.OTHER) {
			_logger.warn("##### unknown kind of diagram " + getObjData().getName() + ": " + type
					+ " - add this EA type to implementation");
		}
	}

	/** Ensure container (class or package) is initialised before calling this one. */
	private EaModelBuilder<?, ?> getModel() {
		return _containingPackage != null ? _containingPackage.getModel() : _containingClass
				.getContainingPackage().getModel();
	}

	// ===============================

	public final PackageBuilder<?, ?, ?, ?, ?, ?> getContainingPackage() {
		return _containingPackage;
	}

	public final ClassBuilder<?, ?, ?, ?, ?, ?> getContainingClass() {
		return _containingClass;
	}

	public final boolean isPortrait() {
		return _portrait;
	}

	public final UmlDiagram.Kind getKind() {
		return _kind;
	}

	// ====================

	@Override
	public String toString() {
		String result = "DiagramBuilder [";
		if (_containingPackage != null) {
			result += "_containingPackage=" + _containingPackage.getObjData().getName();
		}
		if (_containingClass != null) {
			result += ", _containingClass=" + _containingClass.getObjData().getName();
		}
		result += ", _objData=" + _objData;
		result += ", _portrait=" + _portrait;
		result += ", _kind=" + _kind + "]";
		return result;
	}

	// ====================

	@Override
	public final UmlObjectData getObjData() {
		return _objData;
	}

	@Override
	protected final void doBuild() {
		Data data = new Data(getKind(), isPortrait(), false);

		UmlPackage p = getContainingPackage() != null ? getContainingPackage().getResult() : null;
		UmlClass c = getContainingClass() != null ? getContainingClass().getResult() : null;
		UmlStructure container = null;
		boolean exportDiagrams = false;
		if (p != null) {
			container = p;
			exportDiagrams = p.shouldExportDiagrams();
		} else if (c != null) {
			container = c;
			exportDiagrams = c.getContainingPackage().shouldExportDiagrams();
		} else {
			throw new RuntimeException(String.format(
					"Programmer error: Container for diagram should have been built: %s.",
					getObjData().toString()));
		}

		File pic = null;
		if (exportDiagrams) {
			try {
				boolean throughClipboard = false;

				// this is expensive call, because EA is slow here:
				pic = getModel().getDiagramExporter().saveToFile(this,
						DiagramBuilder.DEFAULT_FILE_FORMAT, throughClipboard);

				if (pic != null && !pic.exists()) {
					throw new ProgrammerErrorException(String.format("Pic %s does not exist.",
							pic.getAbsoluteFile()));
				}
			} catch (IOException e) {
				_logger.warn(e.getMessage());
				_logger.warn(" - continuing without image - ");
			}
		}

		setResult(container.addDiagram(pic, getObjData(), data));
	}
}
