/*
 * Copyright (C) 2009-2019 Tatjana (Tanja) Kostic <p> This file belongs to jCleanCim, a tool
 * supporting tasks of UML model managers for IEC TC57 CIM and 61850 models. <p> This program is
 * free software: you can redistribute it and/or modify it under the terms of the GNU Lesser General
 * Public License version 3. <p> This program is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A
 * PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details. You should have
 * received a copy of the GNU Lesser General Public License along with this program. If not, see
 * <http://www.gnu.org/licenses/>.
 */

/**
 * Classes responsible for validating the model and the rules to apply.
 * <p>
 * Main classes are:
 * <ul>
 * <li>{@link org.tanjakostic.jcleancim.validation.ModelValidator} class - launches validation by
 * delegating to other *Validator classes for the scope defined in the
 * {@value org.tanjakostic.jcleancim.common.Config#DEFAULT_PROPS_FILE_NAME} file. These latter all
 * inherit from {@link org.tanjakostic.jcleancim.validation.AbstractValidator} for the common
 * implementation, which allows to have very thin concrete validators.
 * <li>{@link org.tanjakostic.jcleancim.validation.Rule} interface and the interfaces extending it (
 * {@link org.tanjakostic.jcleancim.validation.SimpleRule},
 * {@link org.tanjakostic.jcleancim.validation.CrossRule}) - these allow for simplified processing
 * implemented in {@link org.tanjakostic.jcleancim.validation.AbstractValidator}. Concrete rules
 * inherit from {@link org.tanjakostic.jcleancim.validation.AbstractRule} and need to implement only
 * the necessary minimum.
 * </ul>
 * <p>
 * All concrete validators include mostly simple rules, and some include more complex (bulk and/or
 * cross) rules.
 * <p>
 * To add a new rule, there are 2 things to do in the corresponding
 * org.tanjakostic.jcleancim.validation.*Validator.java file:
 * <ul>
 * <li>add a class for the new rule (similar to existing rules), make it inherit from
 * {@link org.tanjakostic.jcleancim.validation.AbstractRule} and implement one of
 * {@link org.tanjakostic.jcleancim.validation.SimpleRule} or
 * {@link org.tanjakostic.jcleancim.validation.CrossRule} interfaces; and</li>
 * <li>in the constructor of the corresponding validator, add that new rule to appropriate
 * collection, following the same pattern as existing ones.
 * </ul>
 * <p>
 * <b>TODO:</b>
 * <p>
 * Further validation rules to add:
 * <ul>
 * <li>CMM doc, pg. 23 - Check against the naming rules and tag as warning everything that does not
 * fit (according to CIM model management document)</li>
 * <li>upperCase(CIM_WARN, "label should start with lower case"); to be applied to attributes and
 * enum labels of classes, except for the following: UnitSymbol, UnitMultiplier, Currency,
 * MonetaryAmountPerEnergyUnit, MonetaryAmountPerHeatUnit, MonetaryAmountRate; these must preserve
 * the case
 * <li>missingSforMultipleSideRole(CIM_WARN, "label should end with an 's'"); applicable to
 * association end names with multiplicity [0..n], [1..n]; one will have to add "exclusion filter"
 * <li>superfluousSforSingleSideRole(CIM_WARN, "label should not end with an 's'"): applicable to
 * association end names with multiplicity [0..n], [1..n]; one will have to add "exclusion filter"
 * (e.g., for address, status)
 * </ul>
 */
package org.tanjakostic.jcleancim.validation;