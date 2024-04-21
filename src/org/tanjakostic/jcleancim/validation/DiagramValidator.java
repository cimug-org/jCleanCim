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

import java.util.Collection;
import java.util.EnumSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.tanjakostic.jcleancim.common.Config;
import org.tanjakostic.jcleancim.common.Nature;
import org.tanjakostic.jcleancim.model.AbstractUmlObject;
import org.tanjakostic.jcleancim.model.UML;
import org.tanjakostic.jcleancim.model.UmlDiagram;
import org.tanjakostic.jcleancim.model.UmlObject;
import org.tanjakostic.jcleancim.model.UmlStereotype;
import org.tanjakostic.jcleancim.validation.AbstractRule.UmlObjectsMissingDoc;
import org.tanjakostic.jcleancim.validation.AbstractRule.UmlObjectsWithBadCharacterInName;
import org.tanjakostic.jcleancim.validation.AbstractRule.UmlObjectsWithBadDocEnd;
import org.tanjakostic.jcleancim.validation.AbstractRule.UmlObjectsWithBadDocStart;
import org.tanjakostic.jcleancim.validation.AbstractRule.UmlObjectsWithUnallowedStereotype;

/**
 * Validates diagrams.
 *
 * @author tatjana.kostic@ieee.org
 * @version $Id: DiagramValidator.java 27 2019-11-23 16:29:38Z dev978 $
 */
public class DiagramValidator extends AbstractValidator<UmlDiagram> {
	private static final Logger _logger = Logger.getLogger(DiagramValidator.class.getName());

	@SuppressWarnings("serial")
	private final List<UmlDiagram> _scopedUmlObjects;

	DiagramValidator(Config cfg, Collection<UmlDiagram> allDiagrams, ModelIssues issues) {
		super(cfg, allDiagrams.size(), "diagrams", issues);

		_scopedUmlObjects = AbstractUmlObject.collectForScope(allDiagrams,
				cfg.getValidationScope());

		addSimpleRule(new DiagramsWithBadOrientation());
		addSimpleRule(new DiagramsWithUnallowedStereotype());
		addSimpleRule(new DiagramsMissingDoc());
		addSimpleRule(new DiagramsWithBadDocStart());
		addSimpleRule(new DiagramsWithBadDocEnd());
		addSimpleRule(new DiagramsWithBadCharacterInName());
	}

	@Override
	public boolean enabled() {
		return getCfg().isValidationDiagramsOn();
	}

	@Override
	public List<UmlDiagram> getScopedUmlObjects() {
		return _scopedUmlObjects;
	}

	// ==================== simple rules

	public static class DiagramsWithBadOrientation extends AbstractRule
			implements SimpleRule<UmlDiagram> {
		private static final String HYPO = "diagrams with bad orientation, content may be "
				+ "unreadable when printed on A4 page";
		private static final String HOWTO = "change orientation to potrait";

		public DiagramsWithBadOrientation() {
			super(DiagramValidator._logger, Level.WARN, Severity.low, Category.formatting, HYPO,
					HOWTO);
		}

		@Override
		public EnumSet<Nature> getApplicability() {
			return EnumSet.allOf(Nature.class);
		}

		/**
		 * {@inheritDoc}
		 * <p>
		 * Matches if diagram orientation is not portrait.
		 */
		@Override
		public void validate(UmlDiagram d, ModelIssues issues) {
			if (!d.isPortrait()) {
				issues.add(d, createIssue(d));
			}
		}
	}

	public static class DiagramsWithUnallowedStereotype
			extends UmlObjectsWithUnallowedStereotype<UmlDiagram> {
		// doing this way to allow later on adding custom
		private static final Map<Nature, Set<String>> ALLOWED_STEREOS = new TreeMap<>(
				UmlStereotype.getDiagramBuiltIns());

		public DiagramsWithUnallowedStereotype() {
			super(DiagramValidator._logger, "diagrams", ALLOWED_STEREOS);
		}

		@Override
		public EnumSet<Nature> getApplicability() {
			return EnumSet.allOf(Nature.class);
		}
	}

	public static class DiagramsMissingDoc extends UmlObjectsMissingDoc<UmlDiagram> {
		public DiagramsMissingDoc() {
			super(DiagramValidator._logger, "diagrams");
		}

		@Override
		public EnumSet<Nature> getApplicability() {
			return EnumSet.allOf(Nature.class);
		}
	}

	public static class DiagramsWithBadDocStart extends UmlObjectsWithBadDocStart<UmlDiagram> {
		public DiagramsWithBadDocStart() {
			super(DiagramValidator._logger, "diagrams");
		}

		@Override
		public EnumSet<Nature> getApplicability() {
			return EnumSet.allOf(Nature.class);
		}
	}

	public static class DiagramsWithBadDocEnd extends UmlObjectsWithBadDocEnd<UmlDiagram> {
		public DiagramsWithBadDocEnd() {
			super(DiagramValidator._logger, "diagrams");
		}

		@Override
		public EnumSet<Nature> getApplicability() {
			return EnumSet.allOf(Nature.class);
		}
	}

	public static class DiagramsWithBadCharacterInName
			extends UmlObjectsWithBadCharacterInName<UmlDiagram> {
		public DiagramsWithBadCharacterInName() {
			super(DiagramValidator._logger, "diagrams");
		}

		@Override
		public EnumSet<Nature> getApplicability() {
			return EnumSet.allOf(Nature.class);
		}

		@Override
		protected boolean skipValidation(UmlDiagram o) {
			return UML.DetailedDiagrams.equals(o.getContainer().getName());
		}

		@Override
		protected InvalidCharactersFinder getInvalidCharacterFinder(UmlObject o) {
			return o.getNature() == Nature.CIM ? InvalidCharactersFinder.STRICT
					: InvalidCharactersFinder.NUM_UNDERSCORE_DASH_SPACE_COMMA;
		}
	}
}
