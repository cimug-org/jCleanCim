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

/**
 * @author tatjana.kostic@ieee.org
 * @version $Id: RdfsDifference.java 21 2019-08-12 15:44:50Z dev978 $
 */
class RdfsDifference {

	enum Kind {
		missingInOne("defined only in one"),

		nameSet("name collections differ"),

		field("field values differ");

		Kind(String msg) {
			_msg = msg;
		}

		private final String _msg;

		public String msg() {
			return _msg;
		}

		@Override
		public String toString() {
			return msg();
		}
	}

	private final String _package;
	private final String _elemKind;
	private final String _elemName;
	private final RdfsDifference.Kind _diffKind;
	private final String _diffElem;
	private final String _val1;
	private final String _val2;
	private final String _diffDetail;

	/**
	 * Constructor.
	 *
	 * @param package_
	 * @param elemKind
	 * @param elemName
	 * @param diffKind
	 * @param diffElem
	 * @param val1
	 * @param val2
	 * @param diffDetail
	 */
	public RdfsDifference(String package_, String elemKind, String elemName, Kind diffKind,
			String diffElem, String val1, String val2, String diffDetail) {
		super();
		_package = package_;
		_elemKind = elemKind;
		_elemName = elemName;
		_diffKind = diffKind;
		_diffElem = diffElem;
		_val1 = val1;
		_val2 = val2;
		_diffDetail = diffDetail;
	}

	public static final String CSV_HEADING = "elemPackage,elemKind,elemName,diffKind,diffElem,val1, val2,diffDetail";

	public String toCSV() {
		StringBuilder s = new StringBuilder();
		String comma = ",";
		s.append(_package).append(comma);
		s.append(_elemKind).append(comma);
		s.append(_elemName).append(comma);
		s.append(_diffKind.msg()).append(comma);
		s.append(_diffElem).append(comma);
		s.append(_val1).append(comma);
		s.append(_val2).append(comma);
		s.append(_diffDetail).append(comma);
		return s.toString();
	}

	@Override
	public String toString() {
		StringBuilder s = new StringBuilder();

		s.append(_elemKind).append(" '");
		if (!_package.trim().isEmpty()) {
			s.append(_package).append("::");
		}
		s.append(_elemName).append("' ");
		if (!_diffElem.isEmpty()) {
			s.append("'").append(_diffElem).append("' ");
		}
		s.append(_diffKind.msg()).append(": ");
		if (_val1.isEmpty()) {
			s.append("").append(_val2).append("");
		} else if (_val2.isEmpty()) {
			s.append("").append(_val1).append("");
		} else {
			s.append("'").append(_val1).append("' vs. ");
			s.append("'").append(_val2).append("'");
		}
		if (!_diffDetail.trim().isEmpty()) {
			s.append(" - ").append(_diffDetail);
		}

		return s.toString();
	}
}
