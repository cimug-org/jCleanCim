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

package org.tanjakostic.jcleancim.util;

/**
 * Class producing string for the console that prints always on the same line _progress of
 * calculation done in a loop, which the user has to wait for to complete.
 * <p>
 * Adapted from <a href=
 * "https://masterex.github.io/archive/2011/10/23/java-cli-_progress-bar.html">c00kiemon5ter</a>.
 *
 * @author tatjana.kostic@ieee.org
 * @version $Id$
 */
public class ProgressBar {

	private static final int MAX_CHAR_COUNT = 60;
	private static final char[] WORKCHARS = { '|', '/', '-', '\\' };
	private static final String FMT = "%3d%% %s %c";

	private StringBuilder _progress;
	private int _total;
	private int _updateStep;

	/**
	 * Constructor.
	 *
	 * @param total
	 *            the measure of the total work
	 */
	public ProgressBar(int total, int updatePercentStep) {
		init(total, updatePercentStep);
	}

	private void reset() {
		_progress = new StringBuilder(MAX_CHAR_COUNT);
	}

	private void init(int total, int updatePercentStep) {
		if (total < 0) {
			throw new IllegalArgumentException("total (" + total + ") < 0");
		}
		if (updatePercentStep > 100) {
			throw new IllegalArgumentException(
					"updatePercentStep (" + updatePercentStep + ") > 100");
		}
		reset();
		_total = total;
		_updateStep = (int) Math.round(total * (updatePercentStep * 0.01));
	}

	/**
	 * Returns content of the progress bar, without the initial form-feed character (to be able to
	 * test the content without actually printing).
	 */
	String getProgress() {
		return _progress.toString();
	}

	int getTotal() {
		return _total;
	}

	int getUpdateStep() {
		return _updateStep;
	}

	boolean notYet(int doneInput) {
		return (doneInput % _updateStep) != 0;
	}

	/**
	 * Call this whenever progress bar needs to be updated.
	 *
	 * @param doneInput
	 *            the measure of the work done so far
	 */
	public void update(int doneInput) {
		if (notYet(doneInput)) {
			return;
		}
		int done = doneInput;
		int percent = (++done * 100) / _total;
		int extrachars = (percent / 2) - _progress.length();
		while (extrachars-- > 0) {
			_progress.append('=');
		}

		int workcharIdx = done % WORKCHARS.length;
		String fmtWithFormFeed = "\r" + FMT;
		System.out.printf(fmtWithFormFeed, percent, _progress, WORKCHARS[workcharIdx]);

		if (done == _total) {
			System.out.flush();
			System.out.println();
			reset();
		}
	}
}
