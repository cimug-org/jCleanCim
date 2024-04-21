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

package org.tanjakostic.jcleancim.validation;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.tanjakostic.jcleancim.model.UmlObject;
import org.tanjakostic.jcleancim.util.MapOfLists;
import org.tanjakostic.jcleancim.util.Util;

/**
 * @author tatjana.kostic@ieee.org
 * @version $Id: ModelIssues.java 21 2019-08-12 15:44:50Z dev978 $
 */
public class ModelIssues {
	private final List<ModelIssue> _issues = new ArrayList<>();
	private final MapOfLists<UmlObject, ModelIssue> _issuesPerSubject = new MapOfLists<>();
	private final MapOfLists<String, UmlObject> _subjectsPerRulename = new MapOfLists<>();
	private final MapOfLists<String, ModelIssue> _issuesPerRulename = new MapOfLists<>();

	public List<ModelIssue> getIssues() {
		return Collections.unmodifiableList(_issues);
	}

	public Collection<ModelIssue> getIssuesFor(UmlObject subject) {
		return Collections.unmodifiableCollection(_issuesPerSubject.subCollection(subject));
	}

	public Collection<UmlObject> getSubjectsWithProblem(String ruleName) {
		return Collections.unmodifiableCollection(_subjectsPerRulename.subCollection(ruleName));
	}

	public List<String> getDiagnosisItems(String ruleName) {
		List<String> result = new ArrayList<String>();
		Collection<ModelIssue> issues = _issuesPerRulename.subCollection(ruleName);
		for (ModelIssue issue : issues) {
			result.add(Util.getIndentSpaces(1) + issue.getDiagnosisItem());
		}
		return result;
	}

	public void add(UmlObject subject, ModelIssue issue) {
		Util.ensureNotNull(subject, "subject");
		Util.ensureNotNull(issue, "issue");

		_issues.add(issue);
		_issuesPerSubject.addValue(subject, issue);
		_subjectsPerRulename.addValue(issue.getRuleName(), subject);
		_issuesPerRulename.addValue(issue.getRuleName(), issue);
	}

	public String asCSV() {
		StringBuilder sb = new StringBuilder();
		sb.append(ModelIssue.columnsAsCSV()).append(Util.NL);
		for (ModelIssue issue : _issues) {
			sb.append(issue.asCSV()).append(Util.NL);
		}
		return sb.toString();
	}
}
