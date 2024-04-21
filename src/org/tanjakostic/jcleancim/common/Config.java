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

package org.tanjakostic.jcleancim.common;

import java.io.File;
import java.io.FilenameFilter;
import java.util.Collection;
import java.util.Collections;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.TreeMap;

import org.apache.log4j.Logger;
import org.tanjakostic.jcleancim.util.ApplicationException;
import org.tanjakostic.jcleancim.util.Util;

/**
 * Configuration is read from three configuration files.
 * <ol>
 * <li>main user configuration file, with the default name {@value #DEFAULT_PROPS_FILE_NAME},
 * contains main user-configurable definitions and options common to any UML model nature. It can be
 * replaced by another one provided as command line argument. If no main configuration file has been
 * specified or found on the classpath, default value {@value #DEFAULT_PROPS_FILE_NAME} is used.
 * Such a sample file, with the valid defaults, is provided with every distribution of jCleanCim.
 * </li>
 * <li>IEC 61850-specific configuration file, with the fixed name {@value #IEC61850_PROPS_FILE_NAME}
 * , contains mainly rarely modified definitions, and thus removes the clutter from the main
 * configuration that the user may edit frequently.</li>
 * <li>jCleanCim version configuration file, with the fixed name {@value #VERSION_PROPS_FILENAME},
 * is edited only when branching development of jCleanCim, to properly tag its current version. The
 * user is not expected to edit it.</li>
 * </ol>
 * <p>
 * <b>Files</b>
 * <p>
 * <i>Input files</i> are mainly expected to be available in any place that is on the classpath. By
 * convention, we configure the directory <code>input</code> under the project root to be on the
 * classpath (implementation note: we simply search for input files as resources that must be on the
 * classpath, and we set the classpath to include directory <code>input</code>; this eliminates the
 * need for absolute paths). The application never modifies anything under <code>input</code>
 * directory. Files searched on the classpath are for instance model file
 * {@value org.tanjakostic.jcleancim.common.Config#KEY_MODEL_FILENAME}, XML schema for document
 * generation (defined only as default: {@value #DEFAULT_WEBACCESS_SCHEMA_FILENAME}), or MS Word
 * templace file {@link #KEY_DOCGEN_WORD_IN_TEMPLATE}.
 * <p>
 * <i>Output files</i> are created in a separate directory, {@value #OUTPUT_DIR_NAME}, under the
 * current execution path obtained with the Java system property
 * {@value org.tanjakostic.jcleancim.util.Util#USER_DIR_KEY}. If such a directory is not available,
 * like in case of a fresh installation, it gets automatically created (implementation note: using
 * the Java system property {@value org.tanjakostic.jcleancim.util.Util#USER_DIR_KEY} ensures that
 * the "home" for {@value #OUTPUT_DIR_NAME} directory will be under the project root directory when
 * you run jCleanCim application, and under <code>test</code> directory when you run tests).
 * <p>
 * The constructor of this class completes all the tricks related to files, both input and output,
 * and once it completed successfully, it is sure that all the file absolute paths are valid: if
 * there is any problem related to resources, we will fail fast, before starting potentially lengthy
 * operations. Furthermore, if an output file (to be produced by the current run of jCleanCim)
 * already exists in the {@value #OUTPUT_DIR_NAME} directory, it will be renamed already in the
 * constructor to ensure that it does not get overwritten later on.
 * <p>
 * <b>Other properties</b>
 * <p>
 * Several available properties allow you to configure how to run jCleanCim: which main functions to
 * run or not (validation, statistics, document generation), and to fine tune their execution. At
 * present, this is the most comfortable way to configure a run of jCleanCim.
 * <p>
 * Because this is a java application, every property found in the {@value #DEFAULT_PROPS_FILE_NAME}
 * file can be overwritten when launching the application by providing one or more
 * <code>-D&lt;propertyName&gt;=&lt;propertyValue&gt;</code> statements immediately after the
 * <code>java</code> command, but if there are many properties to configure, it is simpler to do it
 * in the {@value #DEFAULT_PROPS_FILE_NAME} file.
 * <p>
 * <i>Implementation note</i>: The values obtained from properties have been validated and stored in
 * appropriate format in the constructor. For instance, "true" is read as string and stored as
 * boolean; comma-separated string read from file is stored as a list of strings; absolute file
 * paths are produced from the simple file names. These are then made available through methods to
 * the application.
 * <p>
 * Follows the description of individual properties.
 * <p>
 * <b>Controlling some aspect of the overall application</b>
 * <p>
 * As of release 01v09, there is one such property:
 * <ul>
 * <li>Set {@value #KEY_APP_SKIP_TIMING} = "true" when debugging overall application INFO log, to
 * allow seemless text comparison of two consecutive runs of the application. By default, the timing
 * of major steps get logged.</li>
 * </ul>
 * <p>
 * <b>Top-level properties, to select the functionality to execute</b>
 * <p>
 * You control what gets executed by enabling ("true") or disabling ("false" = value, "" = value
 * omitted, null = whole property absent) one or more of the top level options. The values are shown
 * below enclosed in "" to denote some text, but they should be typed in the properties file without
 * "":
 * <ul>
 * <li>Set {@value #KEY_XMIEXPORT_ON} = "true" to export the .eap model to the three XMI formats
 * (XMI 1.1, XMI 2.1 and CIMTool XMI 1.4/Rose); this option is independent from other top-level
 * options, but makes sense only if the .eap model file is available.</li>
 * <li>Set {@value #KEY_VALIDATION_ON} = "true" to run model validation; this option is independent
 * from other top-level options.</li>
 * <li>Set {@value #KEY_STATISTICS_ON} = "true" to run model statistics; this option is independent
 * from other top-level options.</li>
 * <li>Set {@value #KEY_PROFILES_CROSSCHECK_ON} = "true" to run crosscheck between UML model and
 * multiple profiles; this option is independent from other top-level options. Note: not yet
 * implemented.</li>
 * <li>Set {@value #KEY_MIBGEN_ON} = "true"to run MIBs generation; this option is independent from
 * other top-level options.</li>
 * <li>Set {@value #KEY_DOCGEN_ON} = "true" and {@value #KEY_PROFILES_DOCGEN_ON} = "false","", null
 * to run document generation from UML model (as required for IEC61968-11, IEC61970-301 or
 * IEC61850-7-4).</li>
 * <li>Set {@value #KEY_DOCGEN_ON} = "true" and {@value #KEY_PROFILES_DOCGEN_ON} = "true" to run MS
 * Word document generation from one or more CIM RDF/OWL profiles (as required for IEC61970-452 or
 * IEC61968-13 documents). At present, it is impossible to create multiple Word documents. Note: not
 * yet implemented.</li>
 * </ul>
 * <p>
 * <b>Model-related properties</b>
 * <p>
 * These properties specify the UML or other model to work with:
 * <ul>
 * <li>Property {@value org.tanjakostic.jcleancim.common.Config#KEY_MODEL_FILENAME} holds the name
 * of the EA file containing UML model. This file is expected to be found on the classpath. The
 * value is ignored if you invoke the application with the <code>-m &lt;myModel.eap&gt;</code>
 * command line argument. The value specified with this property is useful if you always use the
 * same configuration file, so you need not type command line argument.
 * <p>
 * A valid UML repository is required for every scenario, except:
 * <ul>
 * <li>[NOT YET IMPLEMENTED] when doing docgen for one or more profiles, the model is built from the
 * files in profiles subdirectories
 * {@value #KEY_PROFILES_RELPATH}/{@value #KEY_PROFILES_DIRNAMES}</li>
 * <li>when you populate the in-memory model through the API instead of reading from EA file (with
 * version 01v04 or higher).</li>
 * </ul>
 * </li>
 * <li>Property {@value #KEY_MODEL_BUILDER} allows you to choose the most performant loading of the
 * model .eap file given your usage requirements. For full support of diagram and XMI export use
 * {@link ModelBuilderKind#sqlxml}. This implementation is based on SQL queries for reading the
 * model .eap file (and replaces <code>model.useSql=true</code> option of 01v07). It is almost order
 * of magnitude faster than the regular API calls (option {@link ModelBuilderKind#japi}). Since
 * 01v08, we have a rocket-fast implementation, with {@link ModelBuilderKind#db} in case you don't
 * need to export diagrams or XMI. Note that both non-API options work properly for the .eap file
 * based on Acess RDBMS only.</li>
 * <li>Property {@value #KEY_MODEL_NATURE_IEC61850} allows you to specify a list of model packages
 * (directly below the root in the model repository) that are IEC61850, or derive from it. Potential
 * IEC61850-family model packages not specified in this list will simply be processed as if they
 * were CIM (and this is normally not what one wants...).</li>
 * <li>Property {@value #KEY_PROFILES_DIRNAMES} allows you to specify one or more subdirectories
 * under relative path {@value #KEY_PROFILES_RELPATH}, under which to search for profile files, of
 * format {@value #XSD_EXT}. This is required if running UML/profile cross-check or if generating MS
 * Word documentation for profiles. The value should reflect one of more IEC {@link OwningWg WGs}
 * owning the profiles. These names are important as they are the only simple means to determine
 * which WG owns the profile without requiring other configuration by the user. For all of the
 * validation of cross-referencing and document generation, we are building an in-memory model with
 * profiles as packages, and therefore we have to assign them the owning WG the same way we do when
 * building the in-memory model from a UML repository. Any number of further subdirectories is
 * allowed, as all the files below the selected subdirectory get scanned. If the value is left
 * empty, profile files of all WGs found under the default profiles directory
 * {@link #DEFAULT_PROFILES_RELPATH} supported profile extension {@value #XSD_EXT} will be picked
 * and processed.</li>
 * </ul>
 * <p>
 * <b>XMI export properties</b>
 * <p>
 * This set of properties controls XMI export functionality, and are relevant only if
 * {@value #KEY_XMIEXPORT_ON}="true".
 * <ul>
 * <li>Property {@value #KEY_XMIEXPORT_DIALECTS}, if not empty or absent, allows to select which XMI
 * {@link XMIDialect dialects} to export.</li>
 * </ul>
 * <p>
 * <b>Common validation properties</b>
 * <p>
 * This set of properties controls validation functionality, and are relevant only if
 * {@value #KEY_VALIDATION_ON}="true".
 * <ul>
 * <li>Property {@value #KEY_VALIDATION_SCOPE}, if not empty or absent, allows to filter the scope
 * for validation per IEC {@link OwningWg WG} owning the top-level package. Note that the whole of
 * the model must be read in order to determine the scope of associations and explicit UML
 * dependencies, as well as inheritance among classes from different packages. The scope is then
 * used for validation and statistics, but NOT for doc generation purposes. This filter is useful
 * for model editors who want to do validation and see statistics for their field of concern only.
 * However, before releasing any updated model, it is recommended to do the full validation, i.e.,
 * to leave this value empty.</li>
 * <li>Properties {@value #KEY_VALIDATION_PACKAGES_OFF}, {@value #KEY_VALIDATION_CLASSES_OFF},
 * {@value #KEY_VALIDATION_ATTRIBUTES_OFF}, {@value #KEY_VALIDATION_ASSOCIATIONS_OFF},
 * {@value #KEY_VALIDATION_OPERATIONS_OFF}, {@value #KEY_VALIDATION_DEPENDENCIES_OFF} and
 * {@value #KEY_VALIDATION_DIAGRAMS_OFF}, when set "true", allow to disable the whole family or
 * validaton rules, applicable to the given type of UML element (package, ..., diagram,
 * respectively). For a model editor, in case there are many validation error or warnings, it may be
 * convenient to temporarily disable the validation for all except for 1 type of elements. However,
 * before releasing any updated model, it is recommended to do the full validation, i.e., to leave
 * these values empty.</li>
 * <li>Property {@value #KEY_VALIDATION_RULES_OFF}, if not empty or absent, allows you to disable
 * individual validation rules. Since version 01v04, console log (as well as log file) contain the
 * full set of available rules, so you can just copy/paste the desired class names, separated with a
 * comma, as value for this key. Note: Line escape character is backslash "\". This may be useful if
 * there are "noisy" warnings that you cannot fix for a given release, so by disabling specific one
 * or two rules, you can temporarily reduce the noise in the output.</li>
 * <li>Property {@value #KEY_VALIDATION_LOGGING_VERBOSE}, if set "true", allows you to have on the
 * console output all the validation rules displayed, as they are fired, even if they produce no
 * error or warning. This may be handy until you get familiar with all the available rules or for
 * debugging, but typically you'll have this option disabled ("false", "" or null).</li>
 * <li>Property {@value #KEY_VALIDATION_PACKAGES_DATA_INDEX} is used for validation and
 * documentation generation of parts of IEC61850-7-4 and IEC61850-7-3, but may be handy for CIM
 * models (for debugging), and that is why it is not specified as IEC61850-specific property.
 * Namely, if your MS Word document template has one or more data index placeholders (to generate an
 * alphabetical index of all the attributes defined on classes within or below a given package), you
 * must specify the names of the packages that you want to use in placeholders for data indexes. To
 * support the primary need of IEC61850-7-3 or IEC61850-7-4 documents, the data indexes are built
 * for the given package and all its content recursively. To avoid building those indexes for
 * everything in the UML model, this option requires to specify one or more packages for which the
 * data index should be built (i.e., made available for printing). Package names provided in the
 * configuration file contain real values for IEC61850, and as an example the package Core from CIM
 * - this is then used in the sample MS Word document template to illustrate functionality. If the
 * MS Word document template does not include the data index placeholder, the data index (even if
 * built) is never printed. However, if you do have a data index placeholder in the template but
 * omit to specify the package name here, no content with data index will be printed in the output
 * document (because it will not have been built).</li>
 * </ul>
 * <p>
 * <b>IEC61850-specific validation properties</b>
 * <p>
 * While CIM UML uses UML as its meta-model, and we generate the documentation for any CIM package
 * and its elements the same way, IEC61850 has a pretty complex structure and the underlying
 * meta-model. Almost every element of the IEC61850 concrete model needs different treatment in both
 * UML and for document generation at present (in the future, still more to come for SCL
 * modelling!). Consequently, validation of IEC61850 UML model and document generation from that
 * model requires many special "hints" for the application to produce the desired format (and allow
 * us to not hard-code these in the source code). The properties relevant for IEC61850 only,
 * explicitly contain in their names "IEC61850". Strictly speaking, most of these options are mainly
 * required for document generation only. However, the document gets generated from the UML model,
 * and we want to ensure that we have performed validation of the model before generating
 * documentation. Therefore, the majority of IEC61850-specific properties are applicable to both
 * validation (if {@value #KEY_VALIDATION_ON} ="true") and document generation (if
 * {@value #KEY_DOCGEN_ON}="true"). The values provided in the default
 * {@value #DEFAULT_PROPS_FILE_NAME} file need not be modified, except if the packages get renamed
 * in the UML model of IEC61850, or if WG10 decides to generate doc differently.
 * <ul>
 * <li>Properties {@value #KEY_VALIDATION_IEC61850_PACKAGES72},
 * {@value #KEY_VALIDATION_IEC61850_PACKAGES73} and {@value #KEY_VALIDATION_IEC61850_PACKAGES74}
 * must have as value a comma-separated list of names of IEC61850 sub-packages that have some
 * special requirement for validation and doc generation of IEC61850-7-2, IEC61850-7-3 and
 * IEC61850-7-4, respectively (for instance, tables for IEC61850-7-3 and IEC61850-7-4 have different
 * format, because they document different elements of the meta-model, common data classes and
 * logical nodes, respectively).</li>
 * <li>Property {@value #KEY_VALIDATION_IEC61850_PACKAGE_META_MODEL} indicates the name of package
 * where IEC61850 meta-model is defined: UML elements from this package must not be printed as
 * inherited in the concrete definitions (tables) in IEC61850-7-3 and IEC61850-7-4.</li>
 * <li>Property {@value #KEY_VALIDATION_IEC61850_PACKAGE72_TOP} indicates the name of top package
 * where IEC6185007-2 is is defined: this is temporary thing, because we still don't have full and
 * final 7-2 in UML, and we want to be able to skip validation of types from its sub-packages.</li>
 * <li>Property {@value #KEY_VALIDATION_IEC61850_PACKAGES_ENUMS_XML} must have as value a
 * comma-separated list of names of IEC61850 sub-packages that contain enumerations that are to be
 * printed as XML (in addition to their normal printing as tables).</li>
 * <li>Property {@value #KEY_VALIDATION_IEC61850_PACKAGES_LN} should have as value a comma-separated
 * list of names of IEC61850 packages containing logical node classes (and other sub-packages). The
 * packages and the classes typically have a short name in the UML model, but need a full name for
 * the headings in the auto-generated documentation, and also some special heading formatting (e.g.,
 * heading for the XCBR class should look like "Logical node circuit breaker LNName: XCBR"). In UML,
 * these human-readable names are defined as alias for the element. If no values are specified here,
 * document generation runs normally, but those aliases will be simply ignored and the headings of
 * the packages and classes will have just short names without any special formatting.</li>
 * <li>Property {@value #KEY_VALIDATION_IEC61850_PACKAGES_CDC} is like
 * {@value #KEY_VALIDATION_IEC61850_PACKAGES_LN}, but for common data classes.</li>
 * <li>Property {@value #KEY_VALIDATION_IEC61850_PACKAGES_DA} is like
 * {@value #KEY_VALIDATION_IEC61850_PACKAGES_LN}, but for constructed data attributes.</li>
 * <li>Property {@value #KEY_VALIDATION_IEC61850_PACKAGES_BASIC} is like
 * {@value #KEY_VALIDATION_IEC61850_PACKAGES_LN}, but for basic types.</li>
 * <li>Property {@value #KEY_VALIDATION_IEC61850_PACKAGE_PRES_COND} must specify the name of the UML
 * package that contains definition for presence conditions: defined in IEC61850-7-3 (in its own
 * clause), and used to model the conditional presence of elements in both IEC61850-7-3 and
 * IEC61850-7-4 (this is modelled in UML as constraints on classes).</li>
 * <li>Property {@value #KEY_VALIDATION_IEC61850_PACKAGE_FC} must specify the name of the UML
 * package that contains definition for functional constraints: defined in IEC61850-7-3 (in its own
 * clause). This package is defined in IEC61850-7-<b><i>2</i></b>, but we must be able to generate
 * Annex B in IEC61850-7-<b><i>3</i></b> with that table.</li>
 * <li>Property {@value #KEY_VALIDATION_IEC61850_PACKAGE_TRGOP} must specify the name of the UML
 * package that contains definition for trigger options: defined in IEC61850-7-3 (in its own
 * clause).</li>
 * <li>Property {@value #KEY_VALIDATION_IEC61850_PACKAGES_DO_ABBR} should have as value a
 * comma-separated list of names of IEC61850 packages containing definitions for valid abbreviations
 * to use for names of data objects within logical nodes, and this table must be generated as clause
 * 4 in IEC61850-7-4, -7-410 and -7-420. Also, we perform validation of data objects defined for
 * logical nodes in UML (i.e. attribute names in concrete sub-classes of LN) and log errors.</li>
 * <li>Property {@value #KEY_VALIDATION_IEC61850_PACKAGE_LN_MAPS} must specify the name of the UML
 * package that contains (in its sub-packages) an extract from IEC61850-5, to be able to generate in
 * IEC61850-7-4 a "special" clause with the table showing the mappings between LNs defined in
 * requirements document (IEC61850-5) and actual normative LNs (IEC61850-7-4). In UML model, this
 * info is contained as tagged values on classes representing IEC61850-5, and through dependencies
 * between them and the actual LNs (IEC61850-7-4). If the value is left empty (or the property is
 * not present at all), the content of that "special" clause in the generated Word document will be
 * empty, even if there is a placeholder.</li>
 * </ul>
 * <p>
 * <b>Statistics properties</b>
 * <p>
 * These options control the <i>displaying</i> of statistics and are applicable only if value in
 * property {@value #KEY_STATISTICS_ON} is set to "true".
 * <p>
 * There are two boolean options, both applicable to CIM only:
 * {@value #KEY_STATISTICS_CIM_IGNORE_ID_OBJECT_INHERITANCE} and
 * {@value #KEY_STATISTICS_CIM_IGNORE_DOMAIN_CLASS_ATTRIBUTES}. In CIM, almost every class inherits
 * from IEC61970::Core::IdentifiedObject, and most of attributes have as a type some class from
 * IEC61970::Domain package. These two options allow, when set to "true", to skip displaying these
 * obvious cross-WG dependencies and avoid unnecessary noise in the output.
 * <p>
 * One more option has been added, applicable to any model family:
 * {@value #KEY_STATISTICS_TAGS_TO_IGNORE}. You may want to use this in case you discover that the
 * custom tool / add-on that you use heavily marks your model with tagged values and you don't want
 * to end up with thousands of lines of log listing every single item in your model.
 * <p>
 * <b>Document generation properties (MS Word or XML)</b>
 * <p>
 * These options specify and control the generation of MS Word or XML document when property
 * {@value #KEY_DOCGEN_ON} is set to "true". Depending on the value in
 * {@value #KEY_PROFILES_DOCGEN_ON}, the document will be generated from the UML model as default
 * (if {@value #KEY_PROFILES_DOCGEN_ON}="false", "", null), or from multiple profiles (if
 * {@value #KEY_PROFILES_DOCGEN_ON}="true"; note that at present, this functionality is not
 * implemented, but is planned for some of future releases).
 * <p>
 * Because MS Word document generation takes very long, we apply several optimisations depending on
 * the value of {@value #KEY_DOCGEN_ON}. For instance, if it not enabled, we don't export diagrams
 * from the UML model (reading model is almost twice faster without exporting diagrams).
 * <p>
 * Several document generation properties are in fact used when collecting the content from the
 * in-memory UML model, before outputing anything to files. Here are general document generation
 * properties:
 * <ul>
 * <li>Property {@value #KEY_DOCGEN_INCLUDE_INFORMATIVE}, if set "true", allows to include
 * informative elements from UML model into generated document. By default ("false", "", null),
 * these are skipped for document generation.</li>
 * <li>Property {@value #KEY_DOCGEN_INCLUDE_NON_PUBLIC}, if set "true", allows to include private,
 * package-private or protected UML elements into generated document. By default ("false", "",
 * null), these are skipped for document generation.</li>
 * <li>Property {@value #KEY_DOCGEN_PRINT_HTML}, if set "true", will allow to respect markup
 * formatting in the documentation of elements in the UML repository when generating output
 * documents. EA currently supports some simple markup (like italic, bold, underline, subscript,
 * superscript, bulleted and numbered lists) in notes for diagram, package, class, attribute,
 * operation, but not for association ends, constraints, operation parameters, tags etc. In CIM, we
 * never use formated documentation, but it is heavilly used in IEC61850 model (e.g., for formulae).
 * Consider that enabling fomatted output results in ~1.5 time longer MS Word document generation,
 * while for XML output documents there is no impact to performance. (TODO: In this release of
 * jCleanCim, this functionality is still not working properly for MS Word output documents, so you
 * will not be happy with the output; this should be fixed in some of comming releases.)</li>
 * <li>Property {@value #KEY_DOCGEN_SHOW_CUSTOM_STEREOTYPES}, if set "true", allows to show in the
 * generated document custom UML stereotypes on UML model elements (in addition to built-in
 * stereotypes already handled). By default ("false", "", null), these are skipped for document
 * generation.</li>
 * <li>Property {@value #KEY_DOCGEN_SKIP_FOR_CUSTOM_STEREOTYPES} holds a comma-separated list of
 * custom UML stereotypes that you want to exclude from document generation. This list is matched
 * against the built-in, standard stereotypes according to modelling rules and any built-in
 * stereotype from this list is removed (i.e., no overriding of built-in stereotype consideration
 * for document generation). Empty list is valid and reflects default behaviour, i.e., everything
 * found in the model according to other specified filtering options is printed.</li>
 * <li>Property {@value #KEY_DOCGEN_SHOW_NAMESPACE_PACKAGES} allows you to specify a comma-separated
 * list of UML packages for which you want to explicitly print the namespace URI and prefix
 * information, if existing.</li>
 * <li>Property {@value #KEY_DOCGEN_IEC61850_INCLUDE_METAMODEL_INHERITANCE}, if set "true", allows
 * for IEC61850 document generation, to include UML elements inherited from the IEC61850 meta-model
 * package {@value #KEY_VALIDATION_IEC61850_PACKAGE_META_MODEL}. By default ("false", "", null),
 * these are skipped for document generation.</li>
 * <li>Property {@value #KEY_DOCGEN_IEC61850_WRITE_UML_TYPES}, if set "true", allows for IEC61850
 * document generation, to write documents in debug mode, i.e., instead of special processing of
 * types for attributes of LNs, CDCs, DAs and the index tables for LNs and CDCs, this option makes
 * to write the actual type, as it is in UML. This is useful for debugging only. By default
 * ("false", "", null), document generation is for "real".</li>
 * </ul>
 * <p>
 * <b> XML document generation properties</b>
 * <p>
 * Starting with release 01v06, we started implementing support for printing UML model content to
 * two XML files: so-called XML spec and XML doc. The first one is for all the content that is
 * technically speaking specification (used for implementation), and the second one contains all the
 * strings from the UML element description, special heading, caption or other titles, i.e.,
 * everything that needs translation. This decision has been taken in order to facilitate editing
 * and translating process by IEC editors (once we move completely to web-based access and away from
 * MS Word).
 * <ul>
 * <li>Property {@value #KEY_DOCGEN_XML_SCOPE}, if not empty or absent, allows to filter the scope
 * for generating XML documentation per IEC {@link OwningWg WG} owning the top-level package, in IEC
 * 61850 lingo, so-called name spaces. In contrast to MS Word documentation, we do not have any
 * input template, so this is the means to select one or more namespaces, per WG. In CIM, we do not
 * have name spaces, but we fabricate one per top level package (from the existing version class).
 * </li>
 * </ul>
 * <p>
 * <b> MS Word document generation properties</b>
 * <ul>
 * <li>Property {@value #KEY_DOCGEN_WORD_ANALYSE_PLACEHOLDERS}, if set "true", allows you to only
 * analyse ("validate") your input template and get hint on errors. This is useful when e.g.
 * updating template with placeholders for new diagrams: if you have a typing error and specify the
 * value in the placeholder which does not exist in the UML model, an output MS Word document will
 * be generated by replacing the placeholders <i>not</i> with real content from UML, but with the
 * actual names from UML that would be used; or with ERROR description in case the placeholder value
 * is invalid. This is very handy to run if you've updated the template, but before actually
 * generating the full documentation (which takes long!) - the produced skeleton output document, if
 * not containg "ERROR" indicates that all the placeholders are OK.</li>
 * <li>Property {@value #KEY_DOCGEN_WORD_SAVE_REOPEN_EVERY} gives the number of tables (and
 * implicitly, table captions) to write before saving, closing and reopening the auto-generated
 * document. This is new option, introduced in release 01v05, to improve performance of MS Word
 * document generation for extremely large documents (i.e., those that have more than ~200 tables).
 * Ensure you set this value as indicated in the readme file when generating very large
 * documentation! MS Word's "insertCaption" method, which is the only means to automatically have
 * numbered figures, tables and their tables of contents, slows down exponentially with the number
 * of captions inserted (in particular, for tables). The only way we found to speed this up is to
 * save the document at {@value #KEY_DOCGEN_WORD_SAVE_REOPEN_EVERY} number of tables, reopen it and
 * continue printing from there. Default value is -1, which means no close/reopen will happen. If
 * you set it to 0, it will perform close/reopen just after writing the first table, then never
 * anymore. Any value greater than zero is applied to all the tables generated.</li>
 * <li>Property {@value #KEY_DOCGEN_WORD_USE_DOC_FORMAT}, when enabled, forces usage of the slow COM
 * API for MS Word. This mimics the original implementation, before we have provided much faster
 * one, assuming we can always work with the OpenXML (.docx) MS Word documents. (TODO: OpenXML
 * implementation not yet functional, so you have to set this to true for the time being...)</li>
 * <li>Property {@value #KEY_DOCGEN_WORD_INTRO_TO_FIGURE_BEFORE}, when enabled, preserves original
 * way of printing introduction to figures first in MS Word document generation, as opposed to
 * referring to the figure caption below the figure and its caption. This latter is the new default
 * behaviour.</li>
 * <li>Property {@value #KEY_DOCGEN_WORD_USE_HYPERLINKS}, if set "true", will allow to hyperlink to
 * the chapter describing the type for attributes, association ends and operation parameters.
 * Consider that enabling fomatted output results in longer MS Word document generation!</li>
 * <li>Property {@value #KEY_DOCGEN_WORD_INCLUDE_INHERITANCE_PATH}, if set "true", will include as a
 * part of class documentation, immediately after the title, the class' inheritance path, i.e., all
 * its superclasses (if applicable).</li>
 * <li>Property {@value #KEY_DOCGEN_WORD_STYLES_PREFIX_TOC} /
 * {@value #KEY_DOCGEN_WORD_STYLES_PREFIX_TOC} is a comma-separated list of single-word prefixes for
 * TOC/heading style names. If none specified, default {@value #DEFAULT_STYLES_PREFIX_TOC} /
 * {@value #DEFAULT_STYLES_PREFIX_HEAD} is used. When scanning existing TOC/heading styles, their
 * names are matched also against this list. For writing, TOC/heading style names must be read from
 * the document, but the retained style is matched if possible according to order and values in this
 * list.</li>
 * <li>Property {@value #KEY_DOCGEN_WORD_STYLES_PARA} / {@value #KEY_DOCGEN_WORD_STYLES_FIG} /
 * {@value #KEY_DOCGEN_WORD_STYLES_TABHEAD} / {@value #KEY_DOCGEN_WORD_STYLES_TABCELL} /
 * {@value #KEY_DOCGEN_WORD_STYLES_FIGCAPT} / {@value #KEY_DOCGEN_WORD_STYLES_TABCAPT} is a
 * comma-separated list of single-word prefixes for TOC/heading style names. If none specified,
 * default {@value #DEFAULT_STYLES_PARA} / {@value #DEFAULT_STYLES_FIG} /
 * {@value #DEFAULT_STYLES_TABHEAD} / {@value #DEFAULT_STYLES_TABCELL} /
 * {@value #DEFAULT_STYLES_FIGCAPT} / {@value #DEFAULT_STYLES_TABCAPT} is used. When scanning
 * existing TOC/heading styles, their names are matched also against this list. For writing,
 * TOC/heading style names must be read from the document, but the retained style is matched if
 * possible according to order and values in this list.</li>
 * </ul>
 * <p>
 * <b>MIBs generation properties (for IEC62351-7)</b>
 * <p>
 * (Since 02v03) These options specify and control the generation of MIBs when property
 * {@value #KEY_MIBGEN_ON} is set to "true". It can be enabled simultaneously with the MS Word
 * document generation ({@value #KEY_DOCGEN_ON} = true) if both MIBs and MS Word document generation
 * is wanted.
 * <p>
 * With properties {@value #KEY_MIBGEN_OUT_DIR_FULL} and {@value #KEY_MIBGEN_OUT_DIR_LIGHT} you
 * specify the directories where to put the resulting full and light MIBs, respectively. If these
 * properties are not present or empty, default directory names {@link #DEFAULT_MIBS_OUT_DIRNAME}
 * and {@link #DEFAULT_MIBSLIGHT_OUT_DIRNAME} will be used, respectively. In every case, these
 * directories will be created under the default output directory: {@link #OUTPUT_DIR_NAME}.
 * <p>
 * <b>CIM-profile document generation properties</b>
 * <p>
 * Currently, there are no properties for this use case, except for the top-property
 * {@value #KEY_PROFILES_DOCGEN_ON}; note that at present, this functionality is not implemented.
 *
 * @author tatjana.kostic@ieee.org
 * @version $Id: Config.java 35 2019-12-20 20:58:26Z dev978 $
 */
public class Config {
	private static final Logger _logger = Logger.getLogger(Config.class.getName());

	/**
	 * App configuration: Skip logging ellapsed time = "true" (default = "false", "", null).
	 */
	public static final String KEY_APP_SKIP_TIMING = "app.skipTiming";

	/**
	 * File name of a UML repository (model), expected to be found on the classpath.
	 */
	public static final String KEY_MODEL_FILENAME = "model.filename";

	/**
	 * Comma-separated list of model packages (directly below the root) that are IEC61850, or derive
	 * from it.
	 */
	public static final String KEY_MODEL_NATURE_IEC61850 = "model.nature.iec61850";

	/**
	 * One of enumeration literals in {@link ModelBuilderKind} (def = {@link ModelBuilderKind#db}).
	 */
	public static final String KEY_MODEL_BUILDER = "model.builder";

	/**
	 * Relative path of directory storing profiles; default is {@link #DEFAULT_PROFILES_RELPATH}.
	 */
	public static final String KEY_PROFILES_RELPATH = "profiles.relpath";

	/**
	 * Comma-separated list of values corresponding to literals in {@link OwningWg}; empty value
	 * (default) takes them all. These correspond to profile subdirectories, under
	 * {@value #KEY_PROFILES_RELPATH}, below which the profile files are located. The file extension
	 * that is recognised is {@value #XSD_EXT} .
	 */
	public static final String KEY_PROFILES_DIRNAMES = "profiles.dirnames";

	/**
	 * Top-level functionality: Enable XMI export = "true" (default = "false", "", null).
	 */
	public static final String KEY_XMIEXPORT_ON = "xmiexport.on";

	/**
	 * Comma-separated list of values corresponding to literals in {@link XMIDialect}; empty value
	 * (default) takes them all.
	 */
	public static final String KEY_XMIEXPORT_DIALECTS = "xmiexport.dialects";

	/**
	 * Top-level functionality: Enable validation = "true" (default = "false", "", null).
	 */
	public static final String KEY_VALIDATION_ON = "validation.on";

	/**
	 * Comma-separated list of values corresponding to literals in {@link OwningWg}; empty value
	 * (default) takes them all.
	 */
	public static final String KEY_VALIDATION_SCOPE = "validation.scope";

	/**
	 * Skip all validation rules for packages = "true" (default = "false", "", null).
	 */
	public static final String KEY_VALIDATION_PACKAGES_OFF = "validation.packages.off";

	/**
	 * Skip all validation rules for classes = "true" (default = "false", "", null).
	 */
	public static final String KEY_VALIDATION_CLASSES_OFF = "validation.classes.off";

	/**
	 * Skip all validation rules for associations and thier ends = "true" (default = "false", "",
	 * null).
	 */
	public static final String KEY_VALIDATION_ASSOCIATIONS_OFF = "validation.associations.off";

	/**
	 * Skip all validation rules for attributes = "true" (default = "false", "", null).
	 */
	public static final String KEY_VALIDATION_ATTRIBUTES_OFF = "validation.attributes.off";

	/**
	 * Skip all validation rules for operations and their parameters = "true" (default = "false",
	 * "", null).
	 */
	public static final String KEY_VALIDATION_OPERATIONS_OFF = "validation.operations.off";

	/**
	 * Skip all validation rules for (hand-drawn UML) dependencies = "true" (default = "false", "",
	 * null).
	 */
	public static final String KEY_VALIDATION_DEPENDENCIES_OFF = "validation.dependencies.off";

	/**
	 * Skip all validation rules for diagrams = "true" (default = "false", "", null).
	 */
	public static final String KEY_VALIDATION_DIAGRAMS_OFF = "validation.diagrams.off";

	/**
	 * Comma-separated list of individual rule class names to be skipped during validation.
	 */
	public static final String KEY_VALIDATION_RULES_OFF = "validation.rules.off";

	/**
	 * Log to console also validation steps with no errors = "true" (default = "false", "", null).
	 */
	public static final String KEY_VALIDATION_LOGGING_VERBOSE = "validation.logging.verbose";

	/**
	 * Comma-separated list of package names required for building data index from all the
	 * attributes on classes from the given package and below, recursively.
	 */
	public static final String KEY_VALIDATION_PACKAGES_DATA_INDEX = "validation.packagesDataIndex";

	/**
	 * Comma-separated list of package names required for generation of main clauses in
	 * IEC61850-7-2.
	 */
	public static final String KEY_VALIDATION_IEC61850_PACKAGES72 = "validation.iec61850.packages72";

	/**
	 * Comma-separated list of package names required for generation of main clauses in
	 * IEC61850-7-3.
	 */
	public static final String KEY_VALIDATION_IEC61850_PACKAGES73 = "validation.iec61850.packages73";

	/**
	 * Comma-separated list of package names required for generation of main clauses in
	 * IEC61850-7-4.
	 */
	public static final String KEY_VALIDATION_IEC61850_PACKAGES74 = "validation.iec61850.packages74";

	/**
	 * Name of the UML package where the meta-model of IEC61850 is defined.
	 */
	public static final String KEY_VALIDATION_IEC61850_PACKAGE_META_MODEL = "validation.iec61850.packageMetaModel";

	/**
	 * Name of the UML package where the meta-model of IEC61850 is defined.
	 */
	public static final String KEY_VALIDATION_IEC61850_PACKAGE72_TOP = "validation.iec61850.package72Top";

	/**
	 * Comma-separated list of package names containing enumerations that must be printed as XML (in
	 * addition to tables) in IEC61850-7-3 and IEC61850-7-4.
	 */
	public static final String KEY_VALIDATION_IEC61850_PACKAGES_ENUMS_XML = "validation.iec61850.packagesEnumsXml";

	/**
	 * Comma-separated list of package names whose all deep-child elements (both sub-packages and
	 * classes) should contain a human-readable name and apply special formatting for headings in
	 * IEC61850-7-4.
	 */
	public static final String KEY_VALIDATION_IEC61850_PACKAGES_LN = "validation.iec61850.packagesLn";

	/**
	 * Comma-separated list of package names whose all deep-child elements (both sub-packages and
	 * classes) should contain a human-readable name and apply special formatting for headings in
	 * IEC61850-7-3.
	 */
	public static final String KEY_VALIDATION_IEC61850_PACKAGES_CDC = "validation.iec61850.packagesCdc";

	/**
	 * Comma-separated list of package names whose all deep-child elements (both sub-packages and
	 * classes) should contain a human-readable name and may need special table formatting in
	 * IEC61850-7-3.
	 */
	public static final String KEY_VALIDATION_IEC61850_PACKAGES_DA = "validation.iec61850.packagesDa";

	/**
	 * Comma-separated list of package names whose all deep-child elements (both sub-packages and
	 * classes) should contain a human-readable name and may need special table formatting in
	 * IEC61850-7-2.
	 */
	public static final String KEY_VALIDATION_IEC61850_PACKAGES_BASIC = "validation.iec61850.packagesBasic";

	/**
	 * Name of the UML package where the presence conditions of IEC61850 are defined.
	 */
	public static final String KEY_VALIDATION_IEC61850_PACKAGE_PRES_COND = "validation.iec61850.packagePresCond";

	/**
	 * Name of the UML package where the functional constraints of IEC61850 are defined.
	 */
	public static final String KEY_VALIDATION_IEC61850_PACKAGE_FC = "validation.iec61850.packageFC";

	/**
	 * Name of the UML package where the trigger options of IEC61850 are defined.
	 */
	public static final String KEY_VALIDATION_IEC61850_PACKAGE_TRGOP = "validation.iec61850.packageTrgOp";

	/**
	 * Name of the UML package where the abbreviations for data object names in IEC61850 are
	 * defined.
	 */
	public static final String KEY_VALIDATION_IEC61850_PACKAGES_DO_ABBR = "validation.iec61850.packagesDoAbbr";

	/**
	 * Name of the UML package where the requirements specification for logical nodes (IEC61850-5)
	 * is defined.
	 */
	public static final String KEY_VALIDATION_IEC61850_PACKAGE_LN_MAPS = "validation.iec61850.packageLnMaps";

	/**
	 * Top-level functionality: Enable statistics = "true" (default = "false", "", null).
	 */
	public static final String KEY_STATISTICS_ON = "statistics.on";

	/**
	 * (if {@value #KEY_STATISTICS_ON}="true"): Comma-separated list of tagged values for which to
	 * skip logging to console.
	 */
	public static final String KEY_STATISTICS_TAGS_TO_IGNORE = "statistics.tagsToIgnore";

	/**
	 * (if {@value #KEY_STATISTICS_ON}="true"): Skip logging to console dependencies through
	 * inheritance from CIM IdentifiedObject = "true" (default = "false", "", null).
	 */
	public static final String KEY_STATISTICS_CIM_IGNORE_ID_OBJECT_INHERITANCE = "statistics.cim.ignoreIdObjectInheritance";

	/**
	 * (if {@value #KEY_STATISTICS_ON}="true"): Skip logging to console dependencies through usage
	 * of types from CIM Domain package in attributes = "true" (default = "false", "", null).
	 */
	public static final String KEY_STATISTICS_CIM_IGNORE_DOMAIN_CLASS_ATTRIBUTES = "statistics.cim.ignoreDomainClassAttributes";

	/**
	 * Top-level functionality: Enable crosscheck between the UML model and a set of profiles =
	 * "true" (default = "false", "", null). Two models are created in-memory: one from UML and one
	 * from profiles.
	 */
	public static final String KEY_PROFILES_CROSSCHECK_ON = "profiles.crosscheck.on";

	/**
	 * Top-level functionality: Enable document generation = "true" (default = "false", "", null).
	 * UML is source of the model if {@value #KEY_PROFILES_DOCGEN_ON}="false"), otherwise the source
	 * of the model are profiles.
	 */
	public static final String KEY_DOCGEN_ON = "docgen.on";

	/**
	 * File name of the (input) MS Word document template, expected to be found on the classpath. If
	 * not specified, default {@link #DEFAULT_WORD_IN_TEMPLATE_FILENAME} is used.
	 */
	public static final String KEY_DOCGEN_WORD_IN_TEMPLATE = "docgen.word.inTemplate";

	/**
	 * File name of the (output) MS Word generated document; will be created in
	 * {@value #OUTPUT_DIR_NAME} from template {@value #KEY_DOCGEN_WORD_IN_TEMPLATE} and the UML,
	 * profile or in-memory model. If not specified, default
	 * {@link #DEFAULT_WORD_OUT_DOCUMENT_FILENAME} is used.
	 */
	public static final String KEY_DOCGEN_WORD_OUT_DOCUMENT = "docgen.word.outDocument";

	/**
	 * Comma-separated list of values corresponding to literals in {@link OwningWg}; empty value
	 * (default) takes them all.
	 */
	public static final String KEY_DOCGEN_XML_SCOPE = "docgen.xml.scope";

	/**
	 * File name of the (output) XML generated specification document; will be created in
	 * {@value #OUTPUT_DIR_NAME} from the UML, profile or in-memory model. If not specified, default
	 * {@link #DEFAULT_OUT_XML_SPEC_FILENAME} is used.
	 */
	public static final String KEY_DOCGEN_XML_OUT_SPEC = "docgen.xml.outSpec";

	/**
	 * File name of the (output) XML generated documentation (translatable) document; will be
	 * created in {@value #OUTPUT_DIR_NAME} from the UML, profile or in-memory model. If not
	 * specified, default {@link #DEFAULT_OUT_XML_DOC_FILENAME} is used.
	 */
	public static final String KEY_DOCGEN_XML_OUT_DOC = "docgen.xml.outDoc";

	/**
	 * Top-level functionality: Enable MIBs generation = "true" (default = "false", "", null).
	 */
	public static final String KEY_MIBGEN_ON = "mibgen.on";

	/**
	 * Directory name for the (output) full MIBs; will be created in {@value #OUTPUT_DIR_NAME} from
	 * the UML or in-memory model. If not specified, default {@link #DEFAULT_MIBS_OUT_DIRNAME} is
	 * used.
	 */
	public static final String KEY_MIBGEN_OUT_DIR_FULL = "mibgen.outDirFull";

	/**
	 * Directory name for the (output) light MIBs; will be created in {@value #OUTPUT_DIR_NAME} from
	 * the UML or in-memory model. If not specified, default {@link #DEFAULT_MIBSLIGHT_OUT_DIRNAME}
	 * is used.
	 */
	public static final String KEY_MIBGEN_OUT_DIR_LIGHT = "mibgen.outDirLight";

	/**
	 * Force MS Word COM (.doc) document generation = "true" (default = "false", "", null), as
	 * opposed to the Open XML (.docx) format.
	 */
	public static final String KEY_DOCGEN_WORD_USE_DOC_FORMAT = "docgen.word.useDocFormat";

	/**
	 * Preserves original way of printing introduction to figures first in MS Word document
	 * generation = "true" (default = "false", "", null), as opposed to referring to the figure
	 * caption below the figure and its caption.
	 */
	public static final String KEY_DOCGEN_WORD_INTRO_TO_FIGURE_BEFORE = "docgen.word.introToFigureBefore";

	/**
	 * The number of tables (and table captions) to write before closing and reopening the document.
	 */
	public static final String KEY_DOCGEN_WORD_SAVE_REOPEN_EVERY = "docgen.word.saveReopenEvery";

	/**
	 * Enable dry run of MS Word document generation = "true" (default = "false", "", null).
	 */
	public static final String KEY_DOCGEN_WORD_ANALYSE_PLACEHOLDERS = "docgen.word.analysePlaceholders";

	/**
	 * Enable hyperlinking of UML model elements in the generated MS Word document = "true" (default
	 * = "false", "", null).
	 */
	public static final String KEY_DOCGEN_WORD_USE_HYPERLINKS = "docgen.word.useHyperlinks";

	/**
	 * Include inheritance paths when printing classes in the generated document in the generated MS
	 * Word document = "true" (default = "false", "", null).
	 */
	public static final String KEY_DOCGEN_WORD_INCLUDE_INHERITANCE_PATH = "docgen.word.includeInheritancePath";

	/**
	 * Comma-separated list of TOC style name prefixes (style name without number), in order of
	 * preference. When scanning existing style names, they are matched against this list. If none
	 * specified, default {@link #DEFAULT_STYLES_PREFIX_TOC} is used.
	 */
	public static final String KEY_DOCGEN_WORD_STYLES_PREFIX_TOC = "docgen.word.styles.prefix.toc";

	/**
	 * Comma-separated list of Heading style name prefixes (style name without number), in order of
	 * preference. When scanning existing style names, they are matched against this list. If none
	 * specified, default {@link #DEFAULT_STYLES_PREFIX_HEAD} is used.
	 */
	public static final String KEY_DOCGEN_WORD_STYLES_PREFIX_HEAD = "docgen.word.styles.prefix.head";

	/**
	 * Comma-separated list of single-word style names for text paragraphs, in order of preference.
	 * When scanning existing style names, they are matched against this list. If none specified,
	 * default {@link #DEFAULT_STYLES_PARA} is used.
	 */
	public static final String KEY_DOCGEN_WORD_STYLES_PARA = "docgen.word.styles.para";

	/**
	 * Comma-separated list of single-word style names for figures, in order of preference. When
	 * scanning existing style names, they are matched against this list. If none specified, default
	 * {@link #DEFAULT_STYLES_FIG} is used.
	 */
	public static final String KEY_DOCGEN_WORD_STYLES_FIG = "docgen.word.styles.fig";

	/**
	 * Comma-separated list of single-word style names for table headings, in order of preference.
	 * When scanning existing style names, they are matched against this list. If none specified,
	 * default {@link #DEFAULT_STYLES_TABHEAD} is used.
	 */
	public static final String KEY_DOCGEN_WORD_STYLES_TABHEAD = "docgen.word.styles.tabhead";

	/**
	 * Comma-separated list of single-word style names for table cells, in order of preference. When
	 * scanning existing style names, they are matched against this list. If none specified, default
	 * {@link #DEFAULT_STYLES_TABCELL} is used.
	 */
	public static final String KEY_DOCGEN_WORD_STYLES_TABCELL = "docgen.word.styles.tabcell";

	/**
	 * Comma-separated list of single-word style names for figure caption, in order of preference.
	 * When scanning existing style names, they are matched against this list. If none specified,
	 * default {@link #DEFAULT_STYLES_TABCAPT} is used.
	 */
	public static final String KEY_DOCGEN_WORD_STYLES_FIGCAPT = "docgen.word.styles.figcapt";

	/**
	 * Comma-separated list of single-word style names for table caption, in order of preference.
	 * When scanning existing style names, they are matched against this list. If none specified,
	 * default {@link #DEFAULT_STYLES_TABCAPT} is used.
	 */
	public static final String KEY_DOCGEN_WORD_STYLES_TABCAPT = "docgen.word.styles.tabcapt";

	/**
	 * Include informative UML model elements in the generated document = "true" (default = "false",
	 * "", null).
	 */
	public static final String KEY_DOCGEN_INCLUDE_INFORMATIVE = "docgen.includeInformative";

	/**
	 * Include non-public UML model elements in the generated document = "true" (default = "false",
	 * "", null).
	 */
	public static final String KEY_DOCGEN_INCLUDE_NON_PUBLIC = "docgen.includeNonPublic";

	/**
	 * Enable formatted documentation of UML model elements in the generated document = "true"
	 * (default = "false", "", null).
	 */
	public static final String KEY_DOCGEN_PRINT_HTML = "docgen.printHtml";

	/**
	 * Enable showing custom UML stereotypes on UML model elements in the generated document =
	 * "true" (default = "false", "", null).
	 */
	public static final String KEY_DOCGEN_SHOW_CUSTOM_STEREOTYPES = "docgen.showCustomStereotypes";

	/**
	 * Comma-separated list of custom stereotypes which if used, their elements are to be skipped
	 * when generating documents. This list is matched against the built-in, standard stereotypes
	 * according to modelling rules and any built-in stereotype from this list is removed (i.e., no
	 * overriding of built-in stereotype consideration for document generation). Empty list is valid
	 * and reflects default behaviour, i.e., everything found in the model according to other
	 * specified filtering options is printed.
	 */
	public static final String KEY_DOCGEN_SKIP_FOR_CUSTOM_STEREOTYPES = "docgen.skipForCustomStereotypes";

	/**
	 * Comma-separated list of packages for which to explicitly show namespace and URI (if known) in
	 * the auto-generated document.
	 */
	public static final String KEY_DOCGEN_SHOW_NAMESPACE_PACKAGES = "docgen.showNamespacePackages";

	/**
	 * Write inheritance from IEC61850 UML meta-model
	 * {@link #KEY_VALIDATION_IEC61850_PACKAGE_META_MODEL} = "true" (default = "false", "", null).
	 */
	public static final String KEY_DOCGEN_IEC61850_INCLUDE_METAMODEL_INHERITANCE = "docgen.iec61850.includeMetamodelInheritance";

	/**
	 * Write types with their real names, as they are in IEC61850 UML = "true" (default = "false",
	 * "", null).
	 */
	public static final String KEY_DOCGEN_IEC61850_WRITE_UML_TYPES = "docgen.iec61850.writeUmlTypes";

	/**
	 * Top-level functionality: Enable document generation for profiles = "true" (default = "false",
	 * "", null), if also {@value #KEY_DOCGEN_ON}="true".
	 */
	public static final String KEY_PROFILES_DOCGEN_ON = "profiles.docgen.on";

	/** Default input directory, set to be on the classpath. */
	public static final String INPUT_DIR_NAME = "input";

	/** Directory under classpath below which to search for profile files. */
	public static final String PROFILES_DIR_NAME = "profiles";

	/** Default value for */
	public static final String DEFAULT_PROFILES_RELPATH = Util.USER_DIR + Util.FILE_SEP
			+ INPUT_DIR_NAME + Util.FILE_SEP + PROFILES_DIR_NAME;

	/** Directory into which to create output files. */
	public static final String OUTPUT_DIR_NAME = "output";

	/** Directory name for exported diagram images. */
	public static final String PICS_DIR_NAME = "pics";

	/** Directory path for exported diagram images. */
	public static final String MODEL_PICS_RELPATH = OUTPUT_DIR_NAME + Util.FILE_SEP + PICS_DIR_NAME;

	/** Application configuration properties, to be edited by the user. */
	public static final String DEFAULT_PROPS_FILE_NAME = "config.properties";

	/** IEC 61850-specific application configuration properties, to be edited by the user. */
	public static final String IEC61850_PROPS_FILE_NAME = "config61850.properties";

	/** Build properties (for use by ant script), containing also the version information. */
	public static final String VERSION_PROPS_FILENAME = "build.properties";

	/** Searched on classpath. */
	public static final String DEFAULT_WEBACCESS_SCHEMA_FILENAME = "IECDomain.xsd";

	/**
	 * Project version property name (for use by ant script), and if not available through the jar
	 * manifest, available through .
	 */
	public static final String DEFAULT_VERSION_PROP_NAME = "project.version";

	/** Default file name for MS Word input template. */
	public static final String DEFAULT_WORD_IN_TEMPLATE_FILENAME = "base-small-template.docx";

	/**
	 * Default file name for MS Word output document (generated from input template and a model).
	 */
	public static final String DEFAULT_WORD_OUT_DOCUMENT_FILENAME = "base-small.docx";

	/** Default file name for XML spec output document (generated from a model). */
	public static final String DEFAULT_OUT_XML_SPEC_FILENAME = "base-small-spec.xml";

	/** Default file name for XML doc output document (generated from a model). */
	public static final String DEFAULT_OUT_XML_DOC_FILENAME = "base-small-doc.xml";

	/** Default directory name for full MIBs. */
	public static final String DEFAULT_MIBS_OUT_DIRNAME = "mibs";

	/** Default directory name for light MIBs. */
	public static final String DEFAULT_MIBSLIGHT_OUT_DIRNAME = "mibslight";

	/** Empty image, used when not storing diagrams into files. */
	public static final String DEFAULT_BLANK_PNG_FILENAME = "blank.png";

	/** Default prefix (style name without number) for TOC styles, if none provided. */
	public static final String DEFAULT_STYLES_PREFIX_TOC = "TOC";

	/** Default prefix (style name without number) for heading styles, if none provided. */
	public static final String DEFAULT_STYLES_PREFIX_HEAD = "Heading";

	/** Default style name for text paragraph, if none provided. */
	public static final String DEFAULT_STYLES_PARA = "PARAGRAPH";

	/** Default style name for figure, if none provided. */
	public static final String DEFAULT_STYLES_FIG = "FIGURE";

	/** Default style name for table heading, if none provided. */
	public static final String DEFAULT_STYLES_TABHEAD = "TABLE-col-heading";

	/** Default style name for table cell, if none provided. */
	public static final String DEFAULT_STYLES_TABCELL = "TABLE-cell";

	/** Default style name for figure caption, if none provided. */
	public static final String DEFAULT_STYLES_FIGCAPT = "FIGURE-title";

	/** Default style name for table caption, if none provided. */
	public static final String DEFAULT_STYLES_TABCAPT = "TABLE-title";

	/** Supported profile format (file extension). */
	public static final String XSD_EXT = "xsd";

	private String _propsFilename;
	private final Properties _props;

	private final String _appVersion;
	private final boolean _appSkipTiming;

	private final ModelBuilderKind _modelBuilder;
	private final String _modelFileAbsPath;
	private final String _modelPicsDirAbsPath;
	private final List<String> _iec61850NaturePackages;
	private final String _blankPngFileAbsPath;

	private final boolean _xmiexportOn;
	private final EnumSet<XMIDialect> _xmiexportDialects;

	private final boolean _validationOn;
	private final EnumSet<OwningWg> _validationScope;
	private final boolean _validationPackagesOff;
	private final boolean _validationClassesOff;
	private final boolean _validationAssociationsOff;
	private final boolean _validationAttributesOff;
	private final boolean _validationOperationsOff;
	private final boolean _validationDependenciesOff;
	private final boolean _validationDiagramsOff;
	private final Collection<String> _validationRulesOff;
	private final boolean _validationLoggingVerbose;
	private final Collection<String> _validationPackagesDataIndex;
	private final Collection<String> _validationIec61850Packages72;
	private final Collection<String> _validationIec61850Packages73;
	private final Collection<String> _validationIec61850Packages74;
	private final String _validationIec61850PackageMetaModel;
	private final String _validationIec61850Package72Top;
	private final Collection<String> _validationIec61850PackagesEnumsXml;
	private final Collection<String> _validationIec61850PackagesLn;
	private final Collection<String> _validationIec61850PackagesCdc;
	private final Collection<String> _validationIec61850PackagesDa;
	private final Collection<String> _validationIec61850PackagesBasic;
	private final String _validationIec61850PackagePresCond;
	private final String _validationIec61850PackageFc;
	private final String _validationIec61850PackageTrgOp;
	private final Collection<String> _validationIec61850PackagesDoAbbr;
	private final String _validationIec61850PackageLnMaps;

	private final boolean _statisticsOn;
	private final Collection<String> _statisticsTagsToIgnore;
	private final boolean _statisticsCimIgnoreIdObjectInheritance;
	private final boolean _statisticsCimIgnoreDomainClassAttributes;

	private final boolean _profilesCrosscheckOn;

	private final boolean _docgenOn;
	private final String _docgenWordInTemplateFileAbsPath;
	private final String _docgenWordOutDocumentFileAbsPath;
	private final boolean _docgenWordUseDocFormat;
	private final boolean _docgenWordIntroToFigureBefore;
	private final int _docgenWordSaveReopenEvery;
	private final boolean _docgenWordAnalysePlaceholders;

	private final EnumSet<OwningWg> _docgenXmlScope;
	private final String _docgenXsdInWebaccessFileAbsPath;
	private final String _docgenXmlOutSpecFileAbsPath;
	private final String _docgenXmlOutDocFileAbsPath;
	private final String _docgenXsdOutWebaccessFileAbsPath;

	private final boolean _docgenIncludeInformative;
	private final boolean _docgenIncludeNonPublic;
	private final boolean _docgenPrintHtml;
	private final boolean _docgenShowCustomStereotypes;
	private final List<String> _docgenSkipForCustomStereotypes;
	private final List<String> _docgenShowNamespacePackages;

	private final boolean _docgenWordUseHyperlinks;
	private final boolean _docgenWordIncludeInheritancePath;
	private final List<String> _docgenWordStylesPrefixToc;
	private final List<String> _docgenWordStylesPrefixHead;
	private final List<String> _docgenWordStylesPara;
	private final List<String> _docgenWordStylesFig;
	private final List<String> _docgenWordStylesTabhead;
	private final List<String> _docgenWordStylesTabcell;
	private final List<String> _docgenWordStylesFigcapt;
	private final List<String> _docgenWordStylesTabcapt;

	private final boolean _docgenIec61850IncludeMetamodelInheritance;
	private final boolean _docgenIec61850WriteUmlTypes;

	private final boolean _profilesDocgenOn;
	private final String _profilesRelpath;
	private final Map<OwningWg, List<File>> _profilesFiles;

	private final boolean _mibgenOn;
	private final String _mibgenOutDirFullAbsPath;
	private final String _mibgenOutDirLightAbsPath;

	// for testing
	void setDefaultPropsFileName(String propsFileName) {
		_propsFilename = propsFileName;
	}

	// for testing
	void resetDefaultPropsFileName() {
		_propsFilename = DEFAULT_PROPS_FILE_NAME;
	}

	/**
	 * Constructor.
	 *
	 * @param propsFilename
	 *            non-empty name of properties file; if null, default will be used. If no such a
	 *            file can be found on the classpath, empty properties set is created.
	 * @param modelFilename
	 *            non-empty name of model file, that will override the value in property
	 *            {@value #KEY_MODEL_FILENAME}; if null, the value in property
	 *            {@value #KEY_MODEL_FILENAME} is used; if that one is also null, no model file will
	 *            be read.
	 * @throws ApplicationException
	 */
	public Config(String propsFilename, String modelFilename) throws ApplicationException {
		this(propsFilename, modelFilename, null);
	}

	/**
	 * Constructor; useful for testing.
	 *
	 * @param props
	 *            properties initialised from code instead of from file.
	 * @param modelFilename
	 *            non-empty name of model file, that will override the value in property
	 *            {@value #KEY_MODEL_FILENAME}; if null, the value in property
	 *            {@value #KEY_MODEL_FILENAME} is used; if that one is also null, no model file will
	 *            be read.
	 * @throws ApplicationException
	 */
	public Config(Properties props, String modelFilename) throws ApplicationException {
		this(null, modelFilename, props);
	}

	public static String deduceAppVersion() {
		return deduceAppVersion(VERSION_PROPS_FILENAME, DEFAULT_VERSION_PROP_NAME);
	}

	private Config(String propsFilename, String modelFilename, Properties props)
			throws ApplicationException {

		if (modelFilename != null) {
			Util.ensureNotEmpty(modelFilename, "modelFilename");
		}
		if (propsFilename != null) {
			Util.ensureNotEmpty(propsFilename, "propsFilename");
			setDefaultPropsFileName(propsFilename);
			_props = Util.initPropsFromFile(_propsFilename);
		} else {
			if (props != null) {
				_props = props;
				_logger.info("loaded properties from in-memory object");
			} else {
				setDefaultPropsFileName(DEFAULT_PROPS_FILE_NAME);
				_props = Util.initPropsFromFile(_propsFilename);
			}
		}
		if (props == null) {
			_props.putAll(Util.initPropsFromFile(IEC61850_PROPS_FILE_NAME));
		}

		_appVersion = deduceAppVersion();
		_props.put(DEFAULT_VERSION_PROP_NAME, _appVersion);

		_appSkipTiming = "true".equals(value(KEY_APP_SKIP_TIMING));

		_modelBuilder = initModelBuilder(KEY_MODEL_BUILDER, "model builder");
		_modelFileAbsPath = initModelFileAbsPath(modelFilename, KEY_MODEL_FILENAME);
		_iec61850NaturePackages = Util.splitCommaSeparatedTokens(value(KEY_MODEL_NATURE_IEC61850));
		_blankPngFileAbsPath = initBlankPngFileAbsPath(DEFAULT_BLANK_PNG_FILENAME);

		_xmiexportOn = "true".equals(value(KEY_XMIEXPORT_ON));
		_xmiexportDialects = initXmiDialects(KEY_XMIEXPORT_DIALECTS, "xmi dialect");

		_validationOn = "true".equals(value(KEY_VALIDATION_ON));
		_validationScope = initWGs(KEY_VALIDATION_SCOPE, "validation scope");
		_validationPackagesOff = "true".equals(value(KEY_VALIDATION_PACKAGES_OFF));
		_validationClassesOff = "true".equals(value(KEY_VALIDATION_CLASSES_OFF));
		_validationAssociationsOff = "true".equals(value(KEY_VALIDATION_ASSOCIATIONS_OFF));
		_validationAttributesOff = "true".equals(value(KEY_VALIDATION_ATTRIBUTES_OFF));
		_validationOperationsOff = "true".equals(value(KEY_VALIDATION_OPERATIONS_OFF));
		_validationDependenciesOff = "true".equals(value(KEY_VALIDATION_DEPENDENCIES_OFF));
		_validationDiagramsOff = "true".equals(value(KEY_VALIDATION_DIAGRAMS_OFF));
		_validationRulesOff = Util.splitCommaSeparatedTokens(value(KEY_VALIDATION_RULES_OFF));
		_validationLoggingVerbose = "true".equals(value(KEY_VALIDATION_LOGGING_VERBOSE));
		_validationPackagesDataIndex = Util
				.splitCommaSeparatedTokens(value(KEY_VALIDATION_PACKAGES_DATA_INDEX));
		_validationIec61850Packages72 = Util
				.splitCommaSeparatedTokens(value(KEY_VALIDATION_IEC61850_PACKAGES72));
		_validationIec61850Packages73 = Util
				.splitCommaSeparatedTokens(value(KEY_VALIDATION_IEC61850_PACKAGES73));
		_validationIec61850Packages74 = Util
				.splitCommaSeparatedTokens(value(KEY_VALIDATION_IEC61850_PACKAGES74));
		_validationIec61850PackageMetaModel = value(KEY_VALIDATION_IEC61850_PACKAGE_META_MODEL);
		_validationIec61850Package72Top = value(KEY_VALIDATION_IEC61850_PACKAGE72_TOP);
		_validationIec61850PackagesEnumsXml = Util
				.splitCommaSeparatedTokens(value(KEY_VALIDATION_IEC61850_PACKAGES_ENUMS_XML));
		_validationIec61850PackagesLn = Util
				.splitCommaSeparatedTokens(value(KEY_VALIDATION_IEC61850_PACKAGES_LN));
		_validationIec61850PackagesCdc = Util
				.splitCommaSeparatedTokens(value(KEY_VALIDATION_IEC61850_PACKAGES_CDC));
		_validationIec61850PackagesDa = Util
				.splitCommaSeparatedTokens(value(KEY_VALIDATION_IEC61850_PACKAGES_DA));
		_validationIec61850PackagesBasic = Util
				.splitCommaSeparatedTokens(value(KEY_VALIDATION_IEC61850_PACKAGES_BASIC));
		_validationIec61850PackagePresCond = value(KEY_VALIDATION_IEC61850_PACKAGE_PRES_COND);
		_validationIec61850PackageFc = value(KEY_VALIDATION_IEC61850_PACKAGE_FC);
		_validationIec61850PackageTrgOp = value(KEY_VALIDATION_IEC61850_PACKAGE_TRGOP);
		_validationIec61850PackagesDoAbbr = Util
				.splitCommaSeparatedTokens(value(KEY_VALIDATION_IEC61850_PACKAGES_DO_ABBR));
		_validationIec61850PackageLnMaps = value(KEY_VALIDATION_IEC61850_PACKAGE_LN_MAPS);

		_statisticsOn = "true".equals(value(KEY_STATISTICS_ON));
		_statisticsTagsToIgnore = Util
				.splitCommaSeparatedTokens(value(KEY_STATISTICS_TAGS_TO_IGNORE));
		_statisticsCimIgnoreIdObjectInheritance = "true"
				.equals(value(KEY_STATISTICS_CIM_IGNORE_ID_OBJECT_INHERITANCE));
		_statisticsCimIgnoreDomainClassAttributes = "true"
				.equals(value(KEY_STATISTICS_CIM_IGNORE_DOMAIN_CLASS_ATTRIBUTES));

		_profilesCrosscheckOn = "true".equals(value(KEY_PROFILES_CROSSCHECK_ON));

		_docgenOn = "true".equals(value(KEY_DOCGEN_ON));
		_profilesDocgenOn = "true".equals(value(KEY_PROFILES_DOCGEN_ON));
		if (_docgenOn || _profilesDocgenOn) {
			_modelPicsDirAbsPath = initOutFile(MODEL_PICS_RELPATH, "", "pics output directory")
					.getPath();

			String docgenXmlOutSpec = _props.getProperty(KEY_DOCGEN_XML_OUT_SPEC);
			if (docgenXmlOutSpec != null) {
				_docgenWordOutDocumentFileAbsPath = null;
				_docgenWordInTemplateFileAbsPath = null;

				_docgenXsdInWebaccessFileAbsPath = initInFileAbsPath(
						DEFAULT_WEBACCESS_SCHEMA_FILENAME, "input web access XSD");
				String outName = _props.getProperty(KEY_DOCGEN_XML_OUT_SPEC,
						DEFAULT_OUT_XML_SPEC_FILENAME);
				_docgenXmlOutSpecFileAbsPath = initOutFile(OUTPUT_DIR_NAME, outName,
						"output XML spec file").getAbsolutePath();
				outName = _props.getProperty(KEY_DOCGEN_XML_OUT_DOC, DEFAULT_OUT_XML_DOC_FILENAME);
				_docgenXmlOutDocFileAbsPath = initOutFile(OUTPUT_DIR_NAME, outName,
						"output XML doc file").getAbsolutePath();
				_docgenXsdOutWebaccessFileAbsPath = initOutFile(OUTPUT_DIR_NAME,
						DEFAULT_WEBACCESS_SCHEMA_FILENAME, "output copy of web access XSD")
								.getAbsolutePath();
			} else {
				_docgenXsdInWebaccessFileAbsPath = null;
				_docgenXmlOutSpecFileAbsPath = null;
				_docgenXmlOutDocFileAbsPath = null;
				_docgenXsdOutWebaccessFileAbsPath = null;

				String inName = _props.getProperty(KEY_DOCGEN_WORD_IN_TEMPLATE,
						DEFAULT_WORD_IN_TEMPLATE_FILENAME);
				_docgenWordInTemplateFileAbsPath = initInFileAbsPath(inName,
						"input MS Word template");
				String outName = _props.getProperty(KEY_DOCGEN_WORD_OUT_DOCUMENT,
						DEFAULT_WORD_OUT_DOCUMENT_FILENAME);
				_docgenWordOutDocumentFileAbsPath = initOutFile(OUTPUT_DIR_NAME, outName,
						"output MS Word file").getAbsolutePath();
			}
		} else {
			_modelPicsDirAbsPath = null;

			_docgenXsdInWebaccessFileAbsPath = null;
			_docgenXmlOutSpecFileAbsPath = null;
			_docgenXmlOutDocFileAbsPath = null;
			_docgenXsdOutWebaccessFileAbsPath = null;

			_docgenWordOutDocumentFileAbsPath = null;
			_docgenWordInTemplateFileAbsPath = null;
		}

		_docgenXmlScope = initWGs(KEY_DOCGEN_XML_SCOPE, "XML docgen scope");

		// relative path must be initialised before files:
		String profileRelPath = _props.getProperty(KEY_PROFILES_RELPATH);
		if (profileRelPath == null || profileRelPath.trim().isEmpty()) {
			profileRelPath = DEFAULT_PROFILES_RELPATH;
		}
		_profilesRelpath = profileRelPath;
		if (_profilesDocgenOn || _profilesCrosscheckOn) {
			_profilesFiles = initInProfileFiles(KEY_PROFILES_DIRNAMES);
		} else {
			_profilesFiles = Collections.emptyMap();
		}

		_mibgenOn = "true".equals(value(KEY_MIBGEN_ON));
		if (_mibgenOn) {
			String subdir = OUTPUT_DIR_NAME + Util.FILE_SEP
					+ _props.getProperty(KEY_MIBGEN_OUT_DIR_FULL, DEFAULT_MIBS_OUT_DIRNAME);
			_mibgenOutDirFullAbsPath = initOutFile(subdir, "", "full MIBs output directory")
					.getPath();

			subdir = OUTPUT_DIR_NAME + Util.FILE_SEP
					+ _props.getProperty(KEY_MIBGEN_OUT_DIR_LIGHT, DEFAULT_MIBSLIGHT_OUT_DIRNAME);
			_mibgenOutDirLightAbsPath = initOutFile(subdir, "", "light MIBs output directory")
					.getPath();
		} else {
			_mibgenOutDirFullAbsPath = null;
			_mibgenOutDirLightAbsPath = null;
		}

		_docgenWordUseDocFormat = "true".equals(value(KEY_DOCGEN_WORD_USE_DOC_FORMAT));
		_docgenWordIntroToFigureBefore = "true"
				.equals(value(KEY_DOCGEN_WORD_INTRO_TO_FIGURE_BEFORE));
		_docgenWordSaveReopenEvery = initSaveReopenEvery(KEY_DOCGEN_WORD_SAVE_REOPEN_EVERY);
		_docgenWordAnalysePlaceholders = "true".equals(value(KEY_DOCGEN_WORD_ANALYSE_PLACEHOLDERS));
		_docgenWordUseHyperlinks = "true".equals(value(KEY_DOCGEN_WORD_USE_HYPERLINKS));
		_docgenWordIncludeInheritancePath = "true"
				.equals(value(KEY_DOCGEN_WORD_INCLUDE_INHERITANCE_PATH));
		_docgenWordStylesPrefixToc = initStyleNames(value(KEY_DOCGEN_WORD_STYLES_PREFIX_TOC),
				DEFAULT_STYLES_PREFIX_TOC);
		_docgenWordStylesPrefixHead = initStyleNames(value(KEY_DOCGEN_WORD_STYLES_PREFIX_HEAD),
				DEFAULT_STYLES_PREFIX_HEAD);
		_docgenWordStylesPara = initStyleNames(value(KEY_DOCGEN_WORD_STYLES_PARA),
				DEFAULT_STYLES_PARA);
		_docgenWordStylesFig = initStyleNames(value(KEY_DOCGEN_WORD_STYLES_FIG),
				DEFAULT_STYLES_FIG);
		_docgenWordStylesTabhead = initStyleNames(value(KEY_DOCGEN_WORD_STYLES_TABHEAD),
				DEFAULT_STYLES_TABHEAD);
		_docgenWordStylesTabcell = initStyleNames(value(KEY_DOCGEN_WORD_STYLES_TABCELL),
				DEFAULT_STYLES_TABCELL);
		_docgenWordStylesFigcapt = initStyleNames(value(KEY_DOCGEN_WORD_STYLES_FIGCAPT),
				DEFAULT_STYLES_FIGCAPT);
		_docgenWordStylesTabcapt = initStyleNames(value(KEY_DOCGEN_WORD_STYLES_TABCAPT),
				DEFAULT_STYLES_TABCAPT);

		_docgenIncludeInformative = "true".equals(value(KEY_DOCGEN_INCLUDE_INFORMATIVE));
		_docgenIncludeNonPublic = "true".equals(value(KEY_DOCGEN_INCLUDE_NON_PUBLIC));
		_docgenPrintHtml = "true".equals(value(KEY_DOCGEN_PRINT_HTML));
		_docgenShowCustomStereotypes = "true".equals(value(KEY_DOCGEN_SHOW_CUSTOM_STEREOTYPES));
		_docgenSkipForCustomStereotypes = Util
				.splitCommaSeparatedTokens(value(KEY_DOCGEN_SKIP_FOR_CUSTOM_STEREOTYPES));
		_docgenShowNamespacePackages = Util
				.splitCommaSeparatedTokens(value(KEY_DOCGEN_SHOW_NAMESPACE_PACKAGES));

		_docgenIec61850IncludeMetamodelInheritance = "true"
				.equals(value(KEY_DOCGEN_IEC61850_INCLUDE_METAMODEL_INHERITANCE));
		_docgenIec61850WriteUmlTypes = "true".equals(value(KEY_DOCGEN_IEC61850_WRITE_UML_TYPES));
	}

	private static String deduceAppVersion(String versionPropsFileName, String versionPropName) {
		String implementationVersion = Config.class.getPackage().getImplementationVersion();
		if (implementationVersion != null) {
			return implementationVersion;
		}
		Properties versionProps = Util.initPropsFromFile(versionPropsFileName);
		implementationVersion = versionProps.getProperty(versionPropName);
		if (implementationVersion == null) {
			_logger.warn("Property " + versionPropName + " not found - setting to null.");
			implementationVersion = "null";
		}
		return implementationVersion;
	}

	/**
	 * Initialises model file name trying first the argument <code>modelFilename</code>, then the
	 * one in <code>propertyKey</code>. If one of these is not null, returns the repository absolute
	 * path, otherwise returns null.
	 *
	 * @param modelFilename
	 *            model file name that overrides value in <code>propertyKey</code>.
	 * @throws ApplicationException
	 *             if model file name is specified either through argument or in properties, but not
	 *             found on the classpath.
	 */
	private String initModelFileAbsPath(String modelFilename, String propertyKey)
			throws ApplicationException {
		String where = null;
		String resourceName = null;
		if (modelFilename != null) {
			where = "on cmd line";
			resourceName = modelFilename;
		} else {
			String modelPropVal = value(propertyKey);
			if (modelPropVal != null && !modelPropVal.isEmpty()) {
				where = "in " + _propsFilename;
				resourceName = modelPropVal;
			}
		}
		if (resourceName == null) {
			return null;
		}

		String detail = (where != null && !where.isEmpty()) ? ("EA model specified " + where) : "";
		String path = Util.getResourceAbsPath(resourceName, detail);
		_logger.info(detail + ": '" + path + "'");
		return path;
	}

	private String initBlankPngFileAbsPath(String blankPngFileName) throws ApplicationException {
		String detail = "blank image file";
		String path = Util.getResourceAbsPath(blankPngFileName, detail);
		_logger.info(detail + ": '" + path + "'");
		return path;
	}

	private static String initInFileAbsPath(String inFileName, String detail)
			throws ApplicationException {
		String path = Util.getResourceAbsPath(inFileName, detail);
		_logger.info(detail + ": '" + path + "'");
		return path;
	}

	/** */
	private static File initOutFile(String outDirName, String outFileName, String detail)
			throws ApplicationException {
		File result = Util.getOutputFileRenameIfExists(outDirName, outFileName);
		_logger.info(detail + ": '" + result.getPath() + "'");
		return result;
	}

	private static List<String> initStyleNames(String value, String defaultValue) {
		List<String> result = Util.splitCommaSeparatedTokens(value);
		if (!result.contains(defaultValue)) {
			result.add(defaultValue);
		}
		return result;
	}

	private Map<OwningWg, List<File>> initInProfileFiles(String propName) {
		FilenameFilter filter = new FilenameFilter() {
			@Override
			public boolean accept(File dir, String name) {
				return name.endsWith(XSD_EXT);
			}
		};

		EnumSet<OwningWg> profileSubdirNames = initWGs(propName, "profile subdirectories");
		Map<OwningWg, List<File>> result = new LinkedHashMap<OwningWg, List<File>>();

		for (OwningWg owner : profileSubdirNames) {
			String profilesSubdirName = getProfilesRelpath() + Util.FILE_SEP + owner.name();
			File dirFile = new File(profilesSubdirName);
			List<File> profileFiles = Util.listFiles(dirFile, filter, true);
			if (profileFiles.isEmpty()) {
				continue;
			}
			_logger.info(String.format("found %d profile files for %s:",
					Integer.valueOf(profileFiles.size()), owner.toString()));
			result.put(owner, profileFiles);
			System.out.println("+++ result" + result.toString());
		}

		return result;
	}

	private ModelBuilderKind initModelBuilder(String propName, String what) {
		ModelBuilderKind result = ModelBuilderKind.db;
		String token = value(propName);
		try {
			result = ModelBuilderKind.valueOf(token);
		} catch (Exception e) {
			_logger.info("Unknown " + what + " value '" + token + "', using default '"
					+ result.toString() + "' (you can use one of "
					+ EnumSet.allOf(ModelBuilderKind.class).toString() + ").");
		}
		return result;
	}

	private EnumSet<XMIDialect> initXmiDialects(String propName, String what) {
		EnumSet<XMIDialect> result = EnumSet.noneOf(XMIDialect.class);
		List<String> tokens = Util.splitCommaSeparatedTokens(value(propName));
		for (String token : tokens) {
			try {
				result.add(XMIDialect.valueOf(token));
			} catch (Exception e) {
				_logger.warn("Unknown " + what + " value '" + token + "': use one or more of "
						+ EnumSet.allOf(XMIDialect.class).toString() + ".");
			}
		}
		if (result.isEmpty()) {
			result = EnumSet.allOf(XMIDialect.class);
			result.remove(XMIDialect.cimtool);
		}
		return result;
	}

	private EnumSet<OwningWg> initWGs(String propName, String what) {
		EnumSet<OwningWg> result = EnumSet.noneOf(OwningWg.class);
		List<String> tokens = Util.splitCommaSeparatedTokens(value(propName));
		for (String token : tokens) {
			try {
				result.add(OwningWg.valueOf(token));
			} catch (Exception e) {
				_logger.warn("Unknown " + what + " value '" + token + "': use one or more of "
						+ EnumSet.allOf(OwningWg.class).toString() + ".");
			}
		}
		if (result.isEmpty()) {
			return EnumSet.allOf(OwningWg.class);
		}
		return result;
	}

	private int initSaveReopenEvery(String propName) {
		int defResult = -1;
		String val = _props.getProperty(propName);
		if (val != null && !val.isEmpty()) {
			try {
				return Math.abs(Integer.parseInt(val));
			} catch (NumberFormatException e) {
				_logger.debug("Property " + propName + "=" + val
						+ " cannot be converted to an integer, returning default (" + defResult
						+ ").");
			}
		}
		return defResult;
	}

	String getPropsFileName() {
		return _propsFilename;
	}

	/**
	 * Returns all user-defined properties, read from configuration file.
	 *
	 * @return all user-defined properties, read from configuration file.
	 */
	Properties getProperties() {
		Properties result = new Properties();
		result.putAll(_props);
		return result;
	}

	/**
	 * Returns the property value when found, null otherwise.
	 *
	 * @param key
	 *            non-null property name
	 * @return property value when found, null otherwise.
	 */
	String value(String key) {
		if (key == null) {
			throw new IllegalArgumentException("key should not be null.");
		}

		String result = _props.getProperty(key);
		if (result == null) {
			_logger.debug("Property " + key + " not found in " + _propsFilename + ".");
		}
		return result;
	}

	// ==================== API ========================

	/**
	 * Returns application version deduced from manifest (if running with a jar), or read from
	 * {@link #VERSION_PROPS_FILENAME} file otherwise.
	 */
	public String getAppVersion() {
		return _appVersion;
	}

	/** Returns whether to skip logging ellapsed time. */
	public boolean isAppSkipTiming() {
		return _appSkipTiming;
	}

	/** Returns whether to use SQL to build model. */
	public ModelBuilderKind getModelBuilder() {
		return _modelBuilder;
	}

	/** Returns absolute path of the UML model file. */
	public String getModelFileAbsPath() {
		return _modelFileAbsPath;
	}

	/** Returns true when EA file is specified in configuration. */
	public boolean hasUmlModel() {
		return getModelFileAbsPath() != null;
	}

	/** Comma-separated list of names of model packages (below the root) with non-CIM nature. */
	public List<String> getIec61850NaturePackages() {
		return _iec61850NaturePackages;
	}

	/**
	 * Returns absolute path of the default image file, to be used as replacement when the "real"
	 * image is not available.
	 */
	public String getBlankPngFileAbsPath() {
		return _blankPngFileAbsPath;
	}

	/**
	 * Returns whether export to XMI is enabled; applicable only if the source of the model is an
	 * .eap file.
	 */
	public boolean isXmiexportOn() {
		return _xmiexportOn;
	}

	/**
	 * Returns the configured XMI dialects to be used for export.
	 */
	public EnumSet<XMIDialect> getXmiexportDialects() {
		return _xmiexportDialects;
	}

	public boolean isValidationOn() {
		return _validationOn;
	}

	/**
	 * Returns the owners of packages that determine the scope of validation and statistics. Note
	 * that despite these options, the full model needs to be built.
	 */
	public EnumSet<OwningWg> getValidationScope() {
		return _validationScope;
	}

	public boolean isValidationPackagesOn() {
		return !_validationPackagesOff;
	}

	public boolean isValidationClassesOn() {
		return !_validationClassesOff;
	}

	public boolean isValidationAssociationsOn() {
		return !_validationAssociationsOff;
	}

	public boolean isValidationAttributesOn() {
		return !_validationAttributesOff;
	}

	public boolean isValidationOperationsOn() {
		return !_validationOperationsOff;
	}

	public boolean isValidationDependenciesOn() {
		return !_validationDependenciesOff;
	}

	public boolean isValidationDiagramsOn() {
		return !_validationDiagramsOff;
	}

	public Collection<String> getValidationRulesOff() {
		return Collections.unmodifiableCollection(_validationRulesOff);
	}

	public boolean isValidationLoggingVerbose() {
		return _validationLoggingVerbose;
	}

	public Collection<String> getValidationPackagesDataIndex() {
		return Collections.unmodifiableCollection(_validationPackagesDataIndex);
	}

	public Collection<String> getValidationIec61850Packages72() {
		return Collections.unmodifiableCollection(_validationIec61850Packages72);
	}

	public Collection<String> getValidationIec61850Packages73() {
		return Collections.unmodifiableCollection(_validationIec61850Packages73);
	}

	public Collection<String> getValidationIec61850Packages74() {
		return Collections.unmodifiableCollection(_validationIec61850Packages74);
	}

	public String getValidationIec61850PackageMetaModel() {
		return _validationIec61850PackageMetaModel;
	}

	public String getValidationIec61850Package72Top() {
		return _validationIec61850Package72Top;
	}

	public Collection<String> getValidationIec61850PackagesEnumsXml() {
		return Collections.unmodifiableCollection(_validationIec61850PackagesEnumsXml);
	}

	public Collection<String> getValidationIec61850PackagesLn() {
		return Collections.unmodifiableCollection(_validationIec61850PackagesLn);
	}

	public Collection<String> getValidationIec61850PackagesCdc() {
		return Collections.unmodifiableCollection(_validationIec61850PackagesCdc);
	}

	public Collection<String> getValidationIec61850PackagesDa() {
		return Collections.unmodifiableCollection(_validationIec61850PackagesDa);
	}

	public Collection<String> getValidationIec61850PackagesBasic() {
		return Collections.unmodifiableCollection(_validationIec61850PackagesBasic);
	}

	/**
	 * Returns the union of {@link #getValidationIec61850PackagesLn()},
	 * {@link #getValidationIec61850PackagesCdc()} and .
	 */
	public Collection<String> getValidationIec61850PackagesExtTitle() {
		Collection<String> result = new HashSet<String>(_validationIec61850PackagesLn);
		result.addAll(_validationIec61850PackagesCdc);
		result.addAll(_validationIec61850PackagesDa);
		result.addAll(_validationIec61850PackagesBasic);
		result.add(_validationIec61850PackageFc);
		result.add(_validationIec61850PackageTrgOp);
		result.add(_validationIec61850PackagePresCond);
		return result;
	}

	/**
	 * Returns the union of {@link #getValidationIec61850Packages72()},
	 * {@link #getValidationIec61850Packages73()} and {@link #getValidationIec61850Packages74()}.
	 */
	public Collection<String> getValidationIec61850PackagesDocgen() {
		Collection<String> result = new HashSet<String>(_validationIec61850Packages72);
		result.addAll(_validationIec61850Packages73);
		result.addAll(_validationIec61850Packages74);
		return result;
	}

	public String getValidationIec61850PackagePresCond() {
		return _validationIec61850PackagePresCond;
	}

	public String getValidationIec61850PackageFc() {
		return _validationIec61850PackageFc;
	}

	public String getValidationIec61850PackageTrgOp() {
		return _validationIec61850PackageTrgOp;
	}

	public Collection<String> getValidationIec61850PackagesDoAbbr() {
		return _validationIec61850PackagesDoAbbr;
	}

	public String getValidationIec61850PackageLnMaps() {
		return _validationIec61850PackageLnMaps;
	}

	public boolean isStatisticsOn() {
		return _statisticsOn;
	}

	public Collection<String> getStatisticsTagsToIgnore() {
		return Collections.unmodifiableCollection(_statisticsTagsToIgnore);
	}

	public boolean isStatisticsCimIgnoreIdObjectInheritance() {
		return _statisticsCimIgnoreIdObjectInheritance;
	}

	public boolean isStatisticsCimIgnoreDomainClassAttributes() {
		return _statisticsCimIgnoreDomainClassAttributes;
	}

	public boolean isProfilesCrosscheckOn() {
		return _profilesCrosscheckOn;
	}

	public boolean isMibgenOn() {
		return _mibgenOn;
	}

	/**
	 * Returns absolute path of output for full MIBs, null if doc generation disabled or output
	 * files are given in another format.
	 */
	public String getMibgenOutDirFullAbsPath() {
		return _mibgenOutDirFullAbsPath;
	}

	/**
	 * Returns absolute path of output for light MIBs, null if doc generation disabled or output
	 * files are given in another format.
	 */
	public String getMibgenOutDirLightAbsPath() {
		return _mibgenOutDirLightAbsPath;
	}

	public boolean isDocgenOn() {
		return _docgenOn;
	}

	public boolean isDocgenWordUseDocFormat() {
		return _docgenWordUseDocFormat;
	}

	public boolean isDocgenWordIntroToFigureBefore() {
		return _docgenWordIntroToFigureBefore;
	}

	/**
	 * In case an integer cannot be parsed, returns -1; otherwise, an absolute value of
	 * {@value #KEY_DOCGEN_WORD_SAVE_REOPEN_EVERY}.
	 */
	public int getDocgenWordSaveReopenEvery() {
		return _docgenWordSaveReopenEvery;
	}

	/**
	 * Returns whether to only analyse placeholders in output MS Word document, without replacing
	 * them with the full content.
	 */
	public boolean isDocgenWordAnalysePlaceholders() {
		return _docgenWordAnalysePlaceholders;
	}

	/**
	 * Returns whether to use hyperlinks in output MS Word document.
	 */
	public boolean isDocgenWordUseHyperlinks() {
		return _docgenWordUseHyperlinks;
	}

	/**
	 * Returns whether to include inheritance path in output MS Word document.
	 */
	public boolean isDocgenWordIncludeInheritancePath() {
		return _docgenWordIncludeInheritancePath;
	}

	/** Returns non-empty list of user-prioritised prefixes for TOC style names. */
	public List<String> getDocgenWordStylesPrefixToc() {
		return _docgenWordStylesPrefixToc;
	}

	/** Returns non-empty list of user-prioritised prefixes for heading style names. */
	public List<String> getDocgenWordStylesPrefixHead() {
		return _docgenWordStylesPrefixHead;
	}

	/** Returns non-empty list of user-prioritised names for paragraph text styles. */
	public List<String> getDocgenWordStylesPara() {
		return _docgenWordStylesPara;
	}

	/** Returns non-empty list of user-prioritised names for figure styles. */
	public List<String> getDocgenWordStylesFig() {
		return _docgenWordStylesFig;
	}

	/** Returns non-empty list of user-prioritised names for table heading styles. */
	public List<String> getDocgenWordStylesTabhead() {
		return _docgenWordStylesTabhead;
	}

	/** Returns non-empty list of user-prioritised names for table cells styles. */
	public List<String> getDocgenWordStylesTabcell() {
		return _docgenWordStylesTabcell;
	}

	/** Returns non-empty list of user-prioritised names for figure caption styles. */
	public List<String> getDocgenWordStylesFigcapt() {
		return _docgenWordStylesFigcapt;
	}

	/** Returns non-empty list of user-prioritised names for table caption styles. */
	public List<String> getDocgenWordStylesTabcapt() {
		return _docgenWordStylesTabcapt;
	}

	/**
	 * Returns absolute path of input Word template file, null if doc generation disabled or output
	 * files are given in another format.
	 */
	public String getDocgenWordInTemplateFileAbsPath() {
		return _docgenWordInTemplateFileAbsPath;
	}

	/**
	 * Returns absolute path of output Word document file, null if doc generation disabled or output
	 * files are given in another format.
	 */
	public String getDocgenWordOutDocumentFileAbsPath() {
		return _docgenWordOutDocumentFileAbsPath;
	}

	/**
	 * Returns absolute path of XML web access schema file, null if doc generation disabled or
	 * output files are given in another format.
	 */
	public String getDocgenXsdInWebaccessFileAbsPath() {
		return _docgenXsdInWebaccessFileAbsPath;
	}

	/**
	 * Returns absolute path of output XML spec file, null if doc generation disabled or output
	 * files are given in another format.
	 */
	public String getDocgenXmlOutSpecFileAbsPath() {
		return _docgenXmlOutSpecFileAbsPath;
	}

	/**
	 * Returns absolute path of output XML doc file, null if doc generation disabled or output files
	 * are given in another format.
	 */
	public String getDocgenXmlOutDocFileAbsPath() {
		return _docgenXmlOutDocFileAbsPath;
	}

	/**
	 * Returns absolute path of where to copy the XML web access schema, null if doc generation
	 * disabled or output files are given in another format.
	 */
	public String getDocgenXsdOutWebaccessFileAbsPath() {
		return _docgenXsdOutWebaccessFileAbsPath;
	}

	/**
	 * Returns whether to remove exported diagrams at application exit (true if MS Word output is to
	 * be generated).
	 */
	public boolean isRemovePicsAfterExit() {
		return getDocgenWordOutDocumentFileAbsPath() != null;
	}

	/**
	 * Returns the owners of packages that determine the scope of XML generation. Note that despite
	 * these options, the full model needs to be built.
	 */
	public EnumSet<OwningWg> getDocgenXmlScope() {
		return _docgenXmlScope;
	}

	public boolean isDocgenIncludeInformative() {
		return _docgenIncludeInformative;
	}

	public boolean isDocgenIncludeNonPublic() {
		return _docgenIncludeNonPublic;
	}

	/** Returns whether to respect markup (present in UML descriptions) in output document. */
	public boolean isDocgenPrintHtml() {
		return _docgenPrintHtml;
	}

	public boolean isDocgenShowCustomStereotypes() {
		return _docgenShowCustomStereotypes;
	}

	public List<String> getDocgenSkipForCustomStereotypes() {
		return Collections.unmodifiableList(_docgenSkipForCustomStereotypes);
	}

	public List<String> getDocgenShowNamespacePackages() {
		return Collections.unmodifiableList(_docgenShowNamespacePackages);
	}

	public boolean isDocgenIec61850IncludeMetamodelInheritance() {
		return _docgenIec61850IncludeMetamodelInheritance;
	}

	public boolean isDocgenIec61850WriteUmlTypes() {
		return _docgenIec61850WriteUmlTypes;
	}

	/** Returns true if both general and profile docgen are enabled. */
	public boolean isProfilesDocgenOn() {
		return _docgenOn && _profilesDocgenOn;
	}

	/** Returns true if only docgen from EA is enabled (but not profile docgen). */
	public boolean isDocgenModelOn() {
		return _docgenOn && !_profilesDocgenOn;
	}

	/** Returns absolute path string for directory where to export images from the model. */
	public String getPicsDirAbsPath() {
		return _modelPicsDirAbsPath;
	}

	// FIXME: ensure this is relative and not absolute path
	public String getProfilesRelpath() {
		System.out.println("%%% _profilesRelpath = " + _profilesRelpath);
		return _profilesRelpath;
	}

	public Map<OwningWg, List<File>> getProfileFiles() {
		return Collections.unmodifiableMap(_profilesFiles);
	}

	@Override
	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append("Config:").append(Util.NL);
		final String eq = " = ";
		Map<Object, Object> sortedProps = new TreeMap<Object, Object>();
		sortedProps.putAll(_props);

		for (Entry<Object, Object> prop : sortedProps.entrySet()) {
			sb.append(prop.getKey()).append(eq).append(prop.getValue()).append(Util.NL);
		}
		return sb.toString();
	}
}
