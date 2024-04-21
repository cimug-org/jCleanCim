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

import org.apache.commons.lang.StringEscapeUtils;
import org.tanjakostic.jcleancim.model.UmlObject;
import org.tanjakostic.jcleancim.util.Util;

/**
 * Model issue found during validation.
 *
 * @author tatjana.kostic@ieee.org
 * @version $Id: ModelIssue.java 21 2019-08-12 15:44:50Z dev978 $
 */
public class ModelIssue {

	private final UmlObject _subject;
	private final Rule _rule;
	private final String _groupTag;

	private final String _subjectDescription;
	private final String _evidence;
	private String _diagnosisItem; // lazy initialised

	/**
	 * Creates an issue without the evidence part and with toShortString() subject description.
	 *
	 * @param subject
	 *            non-null subject of this issue.
	 * @param rule
	 *            non-null rule whole violation created this issue.
	 */
	public ModelIssue(UmlObject subject, Rule rule) {
		this(subject, rule, null);
	}

	/**
	 * Creates an issue without the evidence part.
	 *
	 * @param subject
	 *            non-null subject of this issue.
	 * @param rule
	 *            non-null rule whole violation created this issue.
	 * @param subjectDescription
	 *            subject description; if null, using toShortString()
	 */
	public ModelIssue(UmlObject subject, Rule rule, String subjectDescription) {
		this(subject, rule, subjectDescription, null, null);
	}

	/**
	 * Constructor.
	 *
	 * @param subject
	 *            non-null subject of this issue.
	 * @param rule
	 *            non-null rule whose violation created this issue.
	 * @param subjectDescription
	 *            subject description; if null, using toShortString().
	 * @param evidence
	 *            (potentially null) evidence of the issue for subject.
	 * @param groupTag
	 *            (potentially null) tag to indicate this issue is related to some other one;
	 *            typically used in relation to duplicate names.
	 */
	public ModelIssue(UmlObject subject, Rule rule, String subjectDescription, String evidence,
			String groupTag) {
		Util.ensureNotNull(subject, "subject");
		Util.ensureNotNull(rule, "rule");

		_subject = subject;
		_rule = rule;
		_subjectDescription = subjectDescription == null ? subject.toShortString(false, true)
				: subjectDescription;
		_evidence = evidence == null ? "" : evidence;
		_groupTag = groupTag == null ? "" : groupTag;
	}

	@Override
	public String toString() {
		return getDiagnosisItem();
	}

	public String getSubjectOwner() {
		return _subject.getOwner().toString();
	}

	public String getSubjectQName() {
		return _subject.getQualifiedName();
	}

	public String getSubjectKind() {
		return _subject.getKind().getLabel();
	}

	public String getCategory() {
		return _rule.getCategory().toString();
	}

	public String getSeverity() {
		return _rule.getSeverity().toString();
	}

	/** Returns the name of the rule that was violated, which resulted in this issue. */
	public String getRuleName() {
		return _rule.getClass().getSimpleName();
	}

	/** Returns what the rule is enforcing (and what was violated). */
	public String getHypothesis() {
		return _rule.getHypothesis();
	}

	/** Returns the suggestion on how to fix the problem. */
	public String getHowToFix() {
		return _rule.getHowToFix();
	}

	/**
	 * Returns the description of subject in this issue, sufficient to find it among all the objects
	 * in the model.
	 */
	public String getSubjectDescription() {
		return _subjectDescription;
	}

	/** Returns the "proof of guilt"; may be null if obvious. */
	public String getEvidence() {
		return _evidence;
	}

	/** Returns potentially null/empty tag indicating relation with other issues of the same type. */
	public String getGroupTag() {
		return _groupTag;
	}

	/**
	 * Returns the line of text with diagnosis as appropriate for logging; it will likely be deduced
	 * from {@link #getGroupTag()}, {@link #getEvidence()} and {@link #getSubjectDescription()}.
	 */
	public String getDiagnosisItem() {
		if (_diagnosisItem == null) {
			_diagnosisItem = "";
			if (Util.hasContent(getGroupTag())) {
				_diagnosisItem += getGroupTag() + " ";
			}
			_diagnosisItem += getSubjectDescription();
			if (Util.hasContent(getEvidence())) {
				_diagnosisItem += " : " + getEvidence();
			}
		}
		return _diagnosisItem;
	}

	private static final String COLUMNS_CSV = "SubjectOwner,Severity,GroupTag,SubjectDescription,Evidence,"
			+ "Hypothesis,HowToFix,RuleName,Category,SubjectQName,SubjectKind";

	/** Returns the string representation of columns suitable for comma-separated format. */
	public static String columnsAsCSV() {
		return ModelIssue.COLUMNS_CSV;
	}

	/** Returns the string representation suitable for comma-separated format. */
	public String asCSV() {
		StringBuilder builder = new StringBuilder();
		builder.append(getSubjectOwner()).append(",");
		builder.append(getSeverity()).append(",");
		builder.append(getGroupTag()).append(",");
		builder.append(StringEscapeUtils.escapeCsv(getSubjectDescription())).append(",");
		builder.append(StringEscapeUtils.escapeCsv(getEvidence())).append(",");
		builder.append(StringEscapeUtils.escapeCsv(getHypothesis())).append(",");
		builder.append(StringEscapeUtils.escapeCsv(getHowToFix())).append(",");
		builder.append(getRuleName()).append(",");
		builder.append(getCategory()).append(",");
		builder.append(StringEscapeUtils.escapeCsv(getSubjectQName())).append(",");
		builder.append(StringEscapeUtils.escapeCsv(getSubjectKind()));
		return builder.toString();
	}
}
