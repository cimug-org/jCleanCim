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

package org.tanjakostic.jcleancim.docgen.writer.word.doc;

import org.apache.log4j.Logger;

import com.jacob.com.Dispatch;

/**
 * When generating content in Word document using automation/COM API, we want to speed operations by
 * disabling certain options, then restoring them back.
 * <p>
 * Implementation note: This was simply a hell; The documentation of the Word API is in some cases
 * wrong, by telling that things apply to document, while they apply to application, and vice versa.
 * Our complication is that we are closing/reopening document in the middle of writing, so it would
 * have been important to clearly know what changes for a document instance (so, saved with the
 * document), as compared to the global Word application option (not sure where this is stored...).
 * In this implementation, I just ran things with different options/combinations and found what
 * works. If you want to change anything, do on your own risk :-)
 *
 * @author tatjana.kostic@ieee.org
 * @version $Id: OptimOptions.java 26 2019-11-12 18:50:35Z dev978 $
 */
class OptimOptions {
	private static final Logger _logger = Logger.getLogger(OptimOptions.class.getName());

	private static final int wdNormalView = 1;

	private int _viewId;
	private boolean _paginate; // reset on save/close/open
	private boolean _enableFieldUpdate; // reset on save/close/open
	private boolean _updateScreen; // reset on save/close/open
	private boolean _fastSave;
	private boolean _spellCheck;
	private boolean _grammarCheck;

	/** Constructor. */
	public OptimOptions(Dispatch doc, Dispatch app) {
		_viewId = fetchDocumentViewId(fetchView(doc));
		_paginate = fetchEnablePagination(app);
		_enableFieldUpdate = fetchEnableFieldUpdate(doc);
		_updateScreen = fetchEnableScreenUpdating(app);
		_fastSave = fetchEnableFastSave(app);
		_spellCheck = fetchEnableSpellCheck(app);
		_grammarCheck = fetchEnableGrammarCheck(app);
	}

	/** Copy constructor. */
	public OptimOptions(OptimOptions from) {
		_viewId = from._viewId;
		_paginate = from._paginate;
		_enableFieldUpdate = from._enableFieldUpdate;
		_updateScreen = from._updateScreen;
		_fastSave = from._fastSave;
		_spellCheck = from._spellCheck;
		_grammarCheck = from._grammarCheck;
	}

	public boolean isNormalView() {
		return (_viewId == wdNormalView);
	}

	public void setForOptimisedExecution(Dispatch doc, Dispatch app) {
		if (!isNormalView()) {
			_logger.info("  setting view to normal");
			Dispatch view = fetchView(doc);
			_viewId = wdNormalView;
			changeDocumentViewId(view, _viewId);
		}
		if (_paginate) {
			_logger.info("  disabling pagination");
			_paginate = false;
			changeEnablePagination(app, _paginate);
		}
		if (_enableFieldUpdate) {
			_logger.info("  disabling field update");
			_enableFieldUpdate = false;
			changeEnableFieldUpdate(doc, _enableFieldUpdate);
		}
		if (_updateScreen) {
			_logger.info("  disabling screen updating");
			_updateScreen = false;
			changeEnableScreenUpdating(app, _updateScreen);
		}
		if (_fastSave) {
			_logger.info("  disabling fast save");
			_fastSave = false;
			changeEnableFastSave(app, _fastSave);
		}
		if (_spellCheck) {
			_logger.info("  disabling spell checking");
			_spellCheck = false;
			changeEnableSpellCheck(app, _spellCheck);
		}
		if (_grammarCheck) {
			_logger.info("  disabling grammar checking");
			_grammarCheck = false;
			changeEnableGrammarCheck(app, _grammarCheck);
		}
	}

	public void restoreFrom(OptimOptions initial, Dispatch doc, Dispatch app) {
		if (_viewId != initial._viewId) {
			_logger.info("  bringing back original view");
			Dispatch view = fetchView(doc);
			_viewId = initial._viewId;
			changeDocumentViewId(view, _viewId);
		}
		if (_paginate != initial._paginate) {
			_logger.info("  enabling pagination");
			_paginate = initial._paginate;
			changeEnablePagination(app, _paginate);
		}
		if (_enableFieldUpdate != initial._enableFieldUpdate) {
			_logger.info("  enabling field updates");
			_enableFieldUpdate = initial._enableFieldUpdate;
			changeEnableFieldUpdate(doc, _enableFieldUpdate);
		}
		if (_updateScreen != initial._updateScreen) {
			_logger.info("  enabling screen updating");
			_updateScreen = initial._updateScreen;
			changeEnableScreenUpdating(app, _updateScreen);
		}
		if (_fastSave != initial._fastSave) {
			_logger.info("  enabling fast save");
			_fastSave = initial._fastSave;
			changeEnableFastSave(app, _fastSave);
		}
		if (_spellCheck != initial._spellCheck) {
			_logger.info("  enabling spell checking");
			_spellCheck = initial._spellCheck;
			changeEnableSpellCheck(app, _spellCheck);
		}
		if (_grammarCheck != initial._grammarCheck) {
			_logger.info("  enabling grammar checking");
			_grammarCheck = initial._grammarCheck;
			changeEnableGrammarCheck(app, _grammarCheck);
		}
	}

	private void changeDocumentViewId(Dispatch view, int viewId) {
		Dispatch.put(view, "Type", DocWordHelper.vInt(viewId));
	}

	private int fetchDocumentViewId(Dispatch view) {
		return Dispatch.get(view, "Type").getInt();
	}

	private void changeEnablePagination(Dispatch app, boolean b) {
		Dispatch.put(fetchOptions(app), "Pagination", DocWordHelper.getVariant(b));
	}

	private boolean fetchEnablePagination(Dispatch app) {
		return Dispatch.get(fetchOptions(app), "Pagination").getBoolean();
	}

	private void changeEnableFieldUpdate(Dispatch doc, boolean b) {
		// if enabling (true) update, locked is false; if disabling update (false), locked is true:
		boolean flip = !b;

		Dispatch docFields = Dispatch.get(doc, "Fields").toDispatch();
		Dispatch.put(docFields, "Locked", DocWordHelper.getVariantIntForBool(flip));
	}

	private boolean fetchEnableFieldUpdate(Dispatch doc) {
		Dispatch docFields = Dispatch.get(doc, "Fields").toDispatch();
		// if locked (true), update is disabled; if not locked (false), update is enabled:
		return !DocWordHelper.getBoolForVariantInt(Dispatch.get(docFields, "Locked").getInt());
	}

	private void changeEnableScreenUpdating(Dispatch app, boolean b) {
		Dispatch.put(app, "ScreenUpdating", DocWordHelper.getVariant(b));
	}

	private boolean fetchEnableScreenUpdating(Dispatch app) {
		return Dispatch.get(app, "ScreenUpdating").getBoolean();
	}

	private void changeEnableFastSave(Dispatch app, boolean b) {
		Dispatch.put(fetchOptions(app), "AllowFastSave", DocWordHelper.getVariant(b));
	}

	private boolean fetchEnableFastSave(Dispatch app) {
		return Dispatch.get(fetchOptions(app), "AllowFastSave").getBoolean();
	}

	private void changeEnableSpellCheck(Dispatch app, boolean b) {
		Dispatch.put(fetchOptions(app), "CheckSpellingAsYouType", DocWordHelper.getVariant(b));
	}

	private boolean fetchEnableSpellCheck(Dispatch app) {
		return Dispatch.get(fetchOptions(app), "CheckSpellingAsYouType").getBoolean();
	}

	private void changeEnableGrammarCheck(Dispatch app, boolean b) {
		Dispatch.put(fetchOptions(app), "CheckGrammarAsYouType", DocWordHelper.getVariant(b));
	}

	private boolean fetchEnableGrammarCheck(Dispatch app) {
		return Dispatch.get(fetchOptions(app), "CheckGrammarAsYouType").getBoolean();
	}

	private static Dispatch fetchView(Dispatch doc) {
		Dispatch windows = Dispatch.get(doc, "Windows").toDispatch();
		Dispatch window = DocWordHelper.getItem(windows, 1);
		return Dispatch.get(window, "View").toDispatch();
	}

	private Dispatch fetchOptions(Dispatch app) {
		return Dispatch.get(app, "Options").toDispatch();
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("OptimOptions [viewId=");
		builder.append(_viewId);
		builder.append(", normView=");
		builder.append(isNormalView());
		builder.append(", paginate=");
		builder.append(_paginate);
		builder.append(", updFld=");
		builder.append(_enableFieldUpdate);
		builder.append(", updScr=");
		builder.append(_updateScreen);
		builder.append(", fastSave=");
		builder.append(_fastSave);
		builder.append(", spelling=");
		builder.append(_spellCheck);
		builder.append(", grammar=");
		builder.append(_grammarCheck);
		builder.append("]");
		return builder.toString();
	}
}
