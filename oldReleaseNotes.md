 jCleanCim release notes

# jCleanCim release notes

*   [02v01](#release-notes-for-jcleancim-02v01)
*   [02v00](#release-notes-for-jcleancim-02v00)
*   [01v10](#release-notes-for-jcleancim-01v10)
*   [01v09](#release-notes-for-jcleancim-01v09)
*   [01v08a](#jcleancim-01v08a)
*   [01v07](#jcleancim-01v07)
*   [01v06](#jcleancim-01v06)
*   [01v05a](#jcleancim-01v05a)
*   [01v05](#jcleancim-01v05)
*   [01v04](#jcleancim-01v04)
*   [01v03](#jcleancim-01v03)
*   [01v02](#jcleancim-01v02)
*   [01v01](#jcleancim-01v01)
*   [01v00](#jcleancim-01v00)

[Readme file](README.md)


## Release notes for jCleanCim-02v01


**Release Date: 23-Jul-2016**

### New home

From 2016 through 2022, jCleanCim was officially hosted at [Tanja's web space](http://www.tanjakostic.org/jcleancim) - which was accessible also to non-CIMug members.  In November of 2022, with Tanja Kostic's approval, jCleanCim was officially migrated to this repository as part of the CIMug's Open Source Intiatives effort.

### New features

#### Hyperlinks in the Word document

_**Credit**: Prototype by Laurent Guise (thanks!) has inspired the implementation in this release._

This (by IEC 61850 community) long awaited feature is in and it is controlled by a new configuration option, `docgen.word.useHyperlinks` . To not incure performance penalty, the default is 'disabled' (i.e., you need to set it to `true` explicitly .

The hyperlinks are introduced for those classes and enumeration literals that have been printed in the same document. For example:

*   For CIM editors, hyperlinks to IdentifiedObject will be printed only in IEC 61970-301, all the other CIM canonical model documents will not have hyperlinks to IdentifiedObject.
*   For IEC 61850 editors, hyperlinks to e.g., INT32 will be printed only in IEC 61850-7-2 (where INT32 is defined), while in IEC 61850-7-3 there will be no hyperlinks to INT32. Another example, because we print in several annexes presence conditions, DA, CDC and LN tables will all have presence conditions (except for M and O) hyperlinked.

#### New MS Word document placeholders

In this release, we have added three new placeholders for MS Word document generation:

*   `startUmlClass.{packageName.className}.endUml`: This was on request by IEC 61850 community, but is generally useful if you want to quickly print a couple of classes only. You don't need to modify the UML model (to put them into a package) only to be able to print them. However, please do not misuse the feature to end up with a proliferation of individual class placeholders (it is always wiser and better maintainable to mainly include full packages).
*   `startUmlDiagNote{packageName}.{diagramName}.endUml`: We added this for a custom document generation, where we want to have a table with two columns: diagram in one column, its description in the other. Feature is now available, so you can experiment with your own layouts if you need.
*   See below IEC 61850-specific features for the third new placeholder.

We have also added examples in the base-small-template.docx to show how to use the new placeholders.

#### Support for EA Enumeration type (that discards class with "enumeration" stereotype)

In more recent versions of EA, on XMI import, EA refuses to import the original class with stereotype "enumeration", but transforms it automatically to the new Enumeration type. We found no way to force EA to keep enumeration stereotype imported through XMI. The result was that the enumeration classes were then not recognised in jCleanCim as enumerated types and one had to manually add enumeration stereotype.

This release provides proper handling for this case, as well - i.e., the EA Enumeration type is now recognised as enumerated type, the same way as a class with "enumeration" stereotype.

### Performance changes in release 02v01

None. Note however that newer versions of Enterprise Architect seem to always be somewhat slower than the older ones (we noticed the slow down in diagram generation).

### Potential backwards compatibility breaking changes for the application user

*   Before this release, we have been outputing Web Access XML documentation for elements and diagrams by default and only as HTML (text with mark-up), while the default for MS Word was raw text, without markup. To consistently support choice for printing markup or not in both MS Word and Web Access XML, the configuration option `docgen.word.printHtml` has been renamed to `docgen.printHtml`, incidating that this choice is not anymore limited to MS Word. By default, the value remains `false`, which means no change for MS Word output, but change for Web Access XML output (now printing raw text instead of markup). **Note:** Enabling markup for MS Word still does NOT work properly (and probably never will), so it is wise to stay with the default.

### Potential forwards incompatibility for the application user: old jCleanCim with new EA

*   \-

### Other functional changes

*   builder.ea: Improved identification and signaling of the problematic entries in messages "+++ EA problem: attr count on ..." (when reading EA model); e.g. "+++ attr Adp: pos = 14 _DUPLICATE_". Moving any one of the entries ending with _DUPLICATE_ should initiate EA's renumbering and solve the problem for the class. Note that this has been applied in the previous release for everything but class attributes and operations (now we fixed those as well).
*   docgen.collector: Improved generation of locally and globally unique IDs.
*   model: Support for mutual dependencies without going into infinite recursion.
*   model: Added EA "Process" as recognised skipped element, for some custom extensions.
*   CIM-specific:
    *   \-
*   IEC61850-specific:
    *   model: Per IEC 61850-7-3 editor request, replaced "ARRAY of min...max of XYZ" with "ARRAY min...max OF XYZ".
    *   stats: Improved logging of Abbreviated terms usage section, now alphabetically ordered.
    *   collector: Enabled support for CDC ERY of IEC 61850-7-420 (it was implemented since a while but for some reason commented out... Thanks to L. Guise for investigating the issue!)
    *   collector: Added a dedicated Word placeholder `startUmlIec61850NsName.{className}.endUml` to allow correct printing of derived IEC 61850 name space name. Word API didn't behave as expected. If there are no separator between placeholders, it doesn’t recognise a placeholder, so we either miss a piece of information or need to introduce the space character between e.g. '2007' and 'B'. This is not to be acceptable for namespace.name, so this new placeholder is the clean resolution for the issue.
    *   model: To derive Namespace "Needs" for Web Access XML, we now collect all the containing package dependencies, in addition to any native one. This allows us to have, e.g. subpackages of WG17 (7-420 and 90-8) get derived the hand-drawn dependencies of the containing package (WG17) to 7-2, 7-3, 7-4.

### Validation rules:

*   util (validation): -
*   New common / general validation rules:
    *   \-
*   New CIM-specific rules:
    *   \-
*   New IEC61850-specific rules:
    *   Iec61850LNClassesWithSuperfluousConstraints (to catch constraint for inexisting DO; typical copy/paste error, or consequence of DO renaming)

### Bug fixes:

*   collector (61850): Brought back printing default value for an FCDA (attribute in CDC).
*   main: Fixed NPE when launching with arguments -help or -version (thanks to Jacob Dall for reporting).
*   builder.ea: Now catching exceptions during EA processing and properly closing .eap file.
*   builder.ea: While building dependencies from EA, now retaining only those linking directly package-2-package or class-2-class (class, interface, enum); all other dependencies are created as a skipped connector.
*   collector (xml): In XML output, now printing properly 'this' side of association end for inherited association (thanks to Laurent Guise for reporting).

### Known issues / limitations:

*   nothing new.

### Implementation and packaging:

*   collector, writer: split placeholder functionality to two classes.
*   model, collector: added some more unit tests.
*   writer: started adapting Caption class for non-English MS Word, not finished yet...
*   upgraded libraries: n/a.
*   all sources: updated copyright year.
*   moved code to a new svn, which resulted into rejuvenated svn IDs (had to discard the history due to space limitation)
*   added Apache POI jar; implementation far away from finished...

### Documentation:

*   updated the presentation.
*   added description on how to use a custom config.properties file.


## Release notes for jCleanCim-02v00


**Release Date: 19-May-2015**

### Licensing changes

From this release, the license terms have changed, see [disclaimer](readme.html#discl). There should be no big impact if you were using it the same way as I did :-)

### Distribution changes

Producing the distribution "Eclipse project archive jCleanCim-\[version\]-eclipse.zip" has been discontinued, because it's a manual and time consuming process. The same functionality is available by using the "Source distribution jCleanCim-\[version\]-src.zip" - by importing simply the eclipse project file from un unzipped directory.

### New features

This is a release with incremental changes and no "real" new feature.

### Performance improvements in release 02v00

None.

### Potential backwards compatibility breaking changes for the application user

*   validation: Renamed rule ClassesWithSuperclassesFromUnallowed**Package** to ClassesWithSuperclassesFromUnallowed**Owner** to better reflect the intent (and also to better align with the new rule applying to attributes of a class - see below).
*   all: If you have been using source code or the .jar as a library, the root package name has changed in this release.

### Potential forwards incompatibility for the application user: old jCleanCim with new EA

*   jCleanCim releases older than 02v00 may have a problem when closing .eap file with Enterprise Architect 12 and higher. The reason is that EA12 Java API changed (they removed a method that was used in the code just before closing the .eap file; and jCleanCim-02v00 replaces that method call appropriately). In our test installation (EA12 + jCleanCim-01v10), there was a pop-up window telling that EA application stopped running and clicking on "Close" application did a smooth shut down of EA - then the run continues normally.

### Other functional changes

*   builder.ea: Improved identification and signaling of the problematic entries in messages "+++ EA ordering problem for ..." (when reading EA model); e.g. "+++ class ExciterType: pos = 22 _DUPLICATE_". Moving any one of the entries ending with _DUPLICATE_ should initiate EA's renumbering and solve the problem in that given scope (e.g., package for ordering problem with its diagrams or classes, class for ordering problem with its attributes).
*   CIM-specific:
    *   \-
*   IEC61850-specific (as usual):
    *   config61850.properties: adjusted some changed package names from UML and added ACSITypesEnums.
    *   model, docgen: added support for one more stereotype (structured) and the corresponding non-meta-model type (with prefix S\_) - this is at present a kludge to obtain in the Word what the editors want to see.
    *   docgen.writer.xml: adapted schema to accept structured type in 7-2 (credit to Chris Frei).
    *   model, collector: extended usage of 'scl:emptyValue' tag not only for enum literal, but also for any IEC61850 attribute (specifically needed for Unit.multiplier), to simply override the default value ('none') with an empty string. Good luck, IEC61850 model managers!
    *   collector: on request of 7-2/7-3 editors, replaced the table column text "M/O/C" with 'PresCond".
    *   collector: on request of 7-2/7-3 editors, simplified printing of enumerated types in 7-2 and 7-3: instead of "ENUMERATED (XyzKind)" or "CODED ENUM (XyzKind)", now printing simply "XyzKind".
    *   collector: implemented support for SCL enumeration printing to use the package name in the heading (to allow printing a couple of packages one after another); in older version, we were always printing a single package in an annex; now we can print any number of enum packages (as SCL enums) anywhere.
    *   model, collector; on request of 7-3 editors, added "datId = " in the table title for all data attribute types (ConstructedDAs).
    *   collector: on request of 7-4 editors, change ‘enumeration literal’ in table headings / captions to “enumeration item‿.
    *   collector: on request of 7-4 editors, don’t output the package name in table captions.

### Validation rules:

*   util (validation): generalised CimAttributesNameShouldBeSingular to accept anything ending with 'ous' and 'ius' as singular (e.g., instantaneous, previous, anonymous, radius); with 'bias'; with 'rans' (for Trans(ient) and Subtrans(ient)).
*   New common / general validation rules:
    *   AttributesWithTypeFromUnallowedPackage (for associations, this is already checked with AssociationsWithWrongSource)
*   New CIM-specific rules:
    *   \-
*   Removed CIM-specific rules:
    *   EnumLiteralsWithInitValue, because CIM also now allows codes for literals.
*   New IEC61850-specific rules:
    *   \-

### Bug fixes:

*   stats: distinguishing "61850" enum literal from "any" enum literal.
*   model (61850): distinguishing the case where presence condition literal with argument is used as-is, without instantiating the argument with a value (misuse of e.g. AtLeastOne(n) instead of AtLeastOne(1)) - credits to Chris Frei for pointing out the bug.

### Known issues / limitations:

*   nothing new.

### Implementation and packaging:

*   upgraded IECDomain.xsd to 0.13.
*   upgraded jackcess library to 2.1.0.
*   upgraded EA dll and jar to version 12 (from EA build 1212).
*   removed docx4j and dependencies, will try to use Apache POI instead at some later date.
*   added some more unit tests.
*   included oldReleaseNotes.html for distribution in the build script.

### Documentation:

*   updated the presentation.


## Release notes for jCleanCim-01v10


**Release Date: 20-May-2014**

### New features

This release does not introduce big new features, just a couple of formatting changes (for document generation), and a couple of new validation rules. For details, see the sections below.

### Performance improvements in release 01v10

None.

### Potential backwards compatibility breaking changes for the application user

*   Every figure in an IEC document has a numbered caption. IEC editors usually request that there is a reference to the figure number somewhere within the text. Until this release, we were printing in MS Word documents so called "introduction" to the figure just before the figure; the intent was - well - to introduce the figure that follows. Some editors found this totally useless and preferred to push that "reference to the caption / figure number" somewhere else, i.e., below the figure. Now, below the figure, we print documentation for the figure (diagram note from UML). To keep that documentation independent from the dynamically calculated number (for the figure caption), we are now just prepending the "Figure #: " to the figure doc. This is, from release 01v10, the default format. If you want to preserve the old format, set the newly introduced configuration option `docgen.word.introToFigureBefore=true` . See slide #49 in the tutorial `doc/jCleanCimIntro.pptx.`
*   In this release, we support printing codes for enumeration literals in CIM as well (for IEC61850, this was always the case). So, in CIM Word documents, tables for enumerated types now have three columns (the column for code is inserted). In agreement with CIM model managers, there is no option to preserve the old behaviour. See below for additional "helpers", in the application log (statistics must be enabled: `statistics.on = true`).

### Other functional changes

*   docgen: when printing package, now creating the systematic "General" subclause for package own content (description and diagrams) only if the package has no sub-packages or classes.
*   model: now parsing the initial value in enum literals (when defined) and making it available as integer (if parsable as integer), otherwise it's just a string.
*   model: now logging with enumerated types that have codes all those codes used, in alphabetical order (generated in the \*-detailed.log); this should make it easier to spot which codes are "free" to use.
*   CIM-specific:
    *   docgen: enum type's literals can have code (like literal's value in 61850).
*   IEC61850-specific:
    *   docgen: when printing 7-3 CDC tables, renamed "CDC-ID" to use "cdcId", the same as the tagged value name in UML and added support for printing "cdcId" for abstract superclasses when tag is defined.
    *   model, validation: added definition for UML.PREF\_DOName\_Ieee and now skipping DO name validation with respect to abbreviaitons if the name starts with "Ieee"; only logging at TRACE level.
    *   updated header text for CDC tables and removed specials when printing the title of CDC groups (packages), as per decisions of the Editors meeting in Baden, January 2014.
    *   collector: for functions table in 7-4 (relation to part 5), implemented sorting on the column with the 7-4 LN name(s) for easier reference, as per decisions of the Editors meeting in Baden, January 2014.
    *   collector: for ARRAY formatting in 7-3, removed 'OF \[\]' from the printed string, as per decisions of the Editors.
    *   collector: for CDC table heading and reference in 7-3, simplified formatting to use normal text, like for any class attribute.
    *   stats: now logging also the inverse of DO decomposition, i.e., the abbreviated terms and their usage (term: "who uses me"); in the log, search for section "Abbreviated terms usage". This is useful in the context of DO abbreviations sanity check, to get a fast overview of all references to a given abbreviation.

### Validation rules:

*   New common / general validation rules:
    *   EnumClassesWithDuplicateCodes
    *   EnumClassesWithSomeCodesMissing
*   New CIM-specific rules:
    *   \-
*   New IEC61850-specific rules:
    *   Iec61850DOAbbreviationLiteralsDuplicateDescription

### Bug fixes:

*   minor: validation action description in one rule.

### Known issues / limitations:

*   nothing new.

### Implementation and packaging:

*   added some more unit tests.
*   non-eclipse source distribution now includes also the two eclipse project files, so you can simply unzip the -src distribution and "Import->Existing project" into eclipse workspace (without the hassle of specific eclipse archive). Note: advantage is that you can also have that unzipped distribution anywhere on the disk, not necessarily within an eclipse workspace.
*   updated SSJavaCOM.dll of EA with versions coming with EA11.
*   updated UmlGraph.jar to UMLGraph-5.6\_6.6-SNAPSHOT.
*   verified code architecture with the great tool [Structure101 Studio](http://structure101.com/); thanks to Headway Software Technologies for providing free license for work on jCleanCim - see the last slide in `doc/jCleanCimIntro.pptx`.

### Documentation:

*   updated the presentation.


## Release notes for jCleanCim-01v09


**Release Date: 11-May-2013**

The main focus for this release was on validation, and some formatting specials for IEC61850 document generation.

### New features

#### Validation report available as .csv

If you are keen on keeping your model clean (the initial very purpose of this application), then you'll appreciate the new validation report, now generated every time you run with validation enabled. The report gets stored in the project's `output` directory as a comma-separated file `problemsReport-[model-name].csv` . You can then open it with a spreadsheet application, format as you like and do filtering of model issues found.

#### Disabling logging of ellapsed time

From this release it is possible to control the time logging aspect of the run by means of the new configuration option `app.skipTiming` . When set to `true` , the ellapsed time currently logged upon completion of the major steps will not be logged anymore. So, instead of e.g.:

  \[main\] INFO  time=\[0:00:10.250\] completed all configured steps - exiting
  

the log will look so:

  \[main\] INFO  completed all configured steps - exiting
  

This may be useful to allow for seemless text comparison of two consecutive runs of the application, such as when you e.g. apply changes to the model and re-run validation or statistics, then want to compare the two log runs.

By default, the timing of major steps gets logged as usual.

### Performance improvements in release 01v09

There are two places with performance improvements in this release, but they are probably not very noticeable, because the task duration initially was of the order of seconds or below:

*   Generation of the XML (Web access) documentation: We have replaced the implementation based on dom4j/jaxen libraries with the standard Java's JAX-P. Thus, we have two dependencies less, and the XML doc generation for both CIM and IEC61850 models is now 2-4 times faster.
*   (IEC61850 only) Handling of abbreviations: We have fixed some logic there and decreased the number of method invocations.

### Potential backwards compatibility breaking changes for the application user

None. The only difference in configuration file that you may notice is that only a small number of rules has been kept as ignored (to decrease the noise until important problems get fixed). In every case, you should try to fix the existing reported problems, then remove the ignored rules from the configuration file one by one (and fix those problems).

Note that from this release, you will need Java 7 or higher.

### Other functional changes

*   docgen.collector: now retaining association end only if it is named.
*   validation: now logging at DEBUG level (detailed log) all the validation rules split per model nature to which they apply, along with their category and severity; search for string "Available rules". This is useful for pasting into a presentation slide, or into a UML model management document.
*   CIM-specific:
    *   none
*   IEC61850-specific:
    *   updated Web access schema to version 0.12 (thanks to Chris Frei) to support minVal/maxVal on an attribute within packed list - triggered by the resolution of \[wg10-0591 / Tissue 813\]. Contains also support for CDC-ID on CDCs, etc.
    *   model, docgen: Added support for specials with the two DOs mandatory in the derived statistics context.
    *   validation: Added more description (including WG) when logging violation of Iec61850DOAttributesWithTooLongName and Iec61850ClassesWithInvalidConstraints.
    *   statistics: Added logging for name decomposition of all the DOs including the description of the DO itself (for easy cross-check of consistency).
    *   model, docgen: Added special printing for setting CDCs with both UML name and CDC-ID.
    *   model, docgen: Added support for old name (currently used for some primitive types).

### Validation rules:

*   made uniform handling for missing doc, bad doc start/end for attributes and association ends (while excluding associations, because we never document associations), diagrams, packages, attributes, classes, operations, operation parameters and return values.
*   "smarter" filtering of allowed characters in tokens in general (e.g., to allow in some case underscore or dash, and forbid it in other cases).
*   adapted rule PackageUnexpectedElements to ignore any content in the package called 'DetailedDiagrams' (this is what we use as "parking" for content hidden from any document generation)
*   New validation rules:
    *   DependenciesWithUnallowedStereotype
    *   OperationsWithUnallowedTagNames
    *   OperationParametersWithUnallowedTagNames
    *   OperationParametersWithUnallowedStereotype
*   New CIM-specific rules:
    *   CimClassesNameStartingWithLowerCase
    *   CimAttributesNameStartingWithUpperCase
    *   CimAssociationEndsNameStartingWithLowerCase
    *   CimAssociationEndsNameShouldBePlural
    *   CimAssociationEndsNameShouldBeSingular
    *   CimClassesNameShouldBeSingular
    *   CimAttributesWithBadCharacterInName
    *   CimAttributesNameShouldBeSingular
    *   CimAttributesNameShouldNotStartWithClassName
*   New IEC61850-specific rules:
    *   Iec61850LNClassesInWrongGroup
    *   Iec61850LNClassesMalformedName
    *   Iec61850AbbreviationLiteralsNameStartingWithLowerCase
    *   Iec61850AttributesWithBadCharacterInName
    *   Iec61850DOAttributesNameStartingWithLowerCase
    *   Iec61850DOAttributesWithNameMissingAbbreviation (replaced Iec61850DOAttributesWithInvalidName)
    *   Iec61850DOAbbreviationLiteralsDuplicateName (replaced Iec61850DOAbbreviationLiteralsDuplicateDefinition)
    *   Iec61850ConditionLiteralsNeverUsedAsConstraints (replaced Iec61850ConditionEnumsWithLiteralsNeverUsedAsConstraints)

### Bug fixes:

*   builder.ea.db/sqlxml: Fixed NPE crash in the method `initRoleTagsPerConnectorUuid`, reported by Pat Brown to appear when reading the ENTSO-E profile model [ENTSOE\_v2\_4\_2](http://cimug.ucaiug.org/Groups/ENTSO-E_IOP/SDO_2013/Shared%20Documents/Profile/v2.4.2%20-%205%20March%202013/ENTSOE_v2_4_2_iec61970cim16v18_iec61968cim12v06a_iec62325cim02v07.zip), (generated by CimContextor) with `model.builder = sqlxml/db`. Reason for NPE: EA seems to store in the connector tags table tags not only for association ends, but also for unknown and impossible-to-deduce-or-find connector(s)... Now we can handle that.
*   collector, writer: Fixed order of columns and width for custom association ends printing (this may happen when option to print inheritance from meta-model is enabled). Also, now printing simply multiplicity, like for default association (and not M/O/C - this is not really defined for relationships, such as in meta-model on 61850-7-2).
*   validation: (for IEC61850 only) Improved performance for validation related to abbreviations: now doing sorting of all abbreviated terms only once, before the loop on all abbreviations (instead within that loop).
*   model: (for IEC61850 only) For case of same abbreviated term defined in multiple places, and with different description, now correctly concatenating all the descriptions (instead of overwriting the older ones).
*   model: Fixed logic in some static methods in AbstractUmlObject related to duplicate name search and reporting; also improved performance (more compact implementation).
*   builder: Now logging "n/a" when model built with 'db' option (without EA API).
*   validation: Fixed abbreviation processing when DO name contains a number.

### Known issues / limitations:

*   MS Word and XML doc generation are mutually exclusive. In other words, you have to provide _either_ the MS Word file names _or_ XML file names through configuration options. If required, in some future release, we may support generation of it all. However, the time required for MS Word doc generation is huge (minutes or tenths of minutes) as compared to XML doc generation (seconds).

### Implementation and packaging:

*   added some more unit tests.
*   (IEC61850) fully reimplemented abbreviations handling and validation.
*   replaced use of dom4j/jaxen with the standard jvm's JAX-P. What a hell this API - this was certainly design by committee, not for developers! But it's standard java and we could remove two dependencies; and our XML doc generation for both CIM and IEC61850 models is now 2-4 times faster.
*   fully adapted all validation rules to handle structured validation errors.
*   moved non-completed builders' code into `experimental` package.
*   verified code architecture with the great tool [Structure101](http://structure101.com/resources/videos/introductory.php); thanks to Headway Software Technologies for providing free license for work on jCleanCim.

### Documentation:

*   pushed old release notes into separate file
*   added here section on dependencies
*   updated the presentation


## jCleanCim-01v08a


**Release Date: 10-Feb-2013**

Note: Version 01v08 should have been called -beta and it has not been released, but was used to generate auto-docs of various 61850 documents in last 2 weeks (plans were to release that one, but with lack of time, the deadline has been missed...). Below, I use 01v08 to refer to this 01v08a as well, and 01v08a only for the fixes applied the day of this official release.

#### New features

##### Selective XMI format export

We have added the means to configure which XMI formats to export, by setting the new configuration option `xmiexport.dialects` to one or more comma-separated values. If nothing is specified, we keep the (almost) default behaviour, to export XMI files in all supported formats, except for `cimtool` ; see config.properties for recognised values.

_Note: The reason to not export `cimtool` by default along with others is that CIMTool latest releases (1.9.6+) simply don't require .xmi file anymore, and can work directly with the .eap model file. Also, the CIM model exported as .xmi has more than 80MB, so we can be more network and disk friendly. If you however need to export it, just specify it in the new configuration option `xmiexport.dialects`._

##### Fast loading of .eap file (if no need to export diagrams or XMI)

We have added as an option, an extremely fast implementation for reading the .eap model file (thanks to Arnold deVos for the hint on a pure java library for reading M$ Access files: [Jackcess](http://jackcess.sourceforge.net/)). Our big standard UML models can now load in a couple of seconds, and this functionality is completely independent of the Enterprise Architect libraries. Consequently, this is totally platform independent and can run on any OS.

The limitation of this new implementation is that we _cannot_ export diagrams or XMI, because those exports are performed by the EA repository API (not used with this implementation). Still, most of the time, you'll be doing model edit-validate-fix cycles, and this new implementation supports perfectly that use case: you can validate very quickly what you just edited.

In order to support the switch among now three possible implementations for the .eap model loading, we decided to replace the boolean configuration option `model.useSql` from 01v07 with the new option: `model.builder` , which can take one of three values: `db, sqlxml or japi` . Table below gives a couple of characteristics to facilitate the choice of appropriate builder for your actual task.

`model.builder=`

`db`

`sqlxml`

`japi`

how it reads .eap model file

as Access DB

as .eap repository, queries (SQL) + resultset (XML)

EA Java API

speed

as fast as it gets

opening .eap very slow, iterating model very fast

opening .eap very slow, iterating model very slow

needs ea.jar + ea.dll

no

yes

yes

bound to M$ Windows

no

yes

yes

can export UML diagrams

never

yes (if `docgen.on=true`)

yes (if `docgen.on=true`)

can export XMI

never

yes (if `xmiexport.on=true`)

yes (if `xmiexport.on=true`)

#### Performance improvements in release 01v08

**B-I-G !**

The following numbers have been traced from runs on Lenovo ThinkPad T410 (i5 CPU, 3GB memory), 64-bit Windows 7 Enterprise Edition, Office 2010, Enterprise Architect 9, 32-bit Java 1.7.0\_13.

The first improvement is related to reading data from .eap UML repository:

*   `model.builder=db`: This is the rocket fast option.
*   `model.builder=sqlxml` (equivalent of `model.useSql=true` of 01v07): Speed of bulk queries here has also been improved (e,g, 4 sec in 01v08 vs. 14 sec in 01v07). However, this is not so significant in the light of the time it takes to complete everything else through the EA API: open file, initialise repository, diagram export when `docgen.on=true` .
*   `model.builder=japi`. Finally, for the original EA API-based implementation, there is no improvement possible on our side, until Sparx decides to do something about it for the sake of its users.

Note that for diagram export ( `docgen.on=true` ), it takes the same time with both `model.builder=sqlxml` and `model.builder=japi` , i.e., some 100-300ms per normative diagram exported.

The UML models used, referred to in the table below:

*   Full CIM model (iec61970cim16v17\_iec61968cim12v06\_iec62325cim02v07.eap): ~1520 classes, ~7680 attributes, ~1050 associations, ~90 dependencies, ~570 diagrams.
*   Full IEC61850 model (wg10uml02v12-wg18uml02v10c-wg17uml02v09a-jwg25uml02v02a.eap): ~1670 classes, ~6250 attributes, ~85 associations, ~260 operations, ~380 dependencies, ~230 diagrams; plus tonnes of class and attribute constraints, tagged values, and markup in the documentation of elements.
*   Small test model (base-small.eap): ~360 classes, ~700 attributes, ~95 associations, ~15 operations, ~70 dependencies, ~95 diagrams.

EA-dependent operation

01v07

useSql=false / useSql=true / -

01v08

japi / sqlxml / db

Open .eap file of any size (SSD hard disk)

5-10 sec / 5-10 sec / -

5-10 sec / 5-10 sec / **0.14-0.25** sec

Read CIM.eap  
(docgen.on=true: ~355 exported diagrams)

3 min / 29 sec / -  
(+50 sec)

3 min / **20** sec / **7.5** sec  
(+50 sec)

Read IEC61850.eap  
(docgen.on=true: ~200 exported diagrams)

2.8 min / 26 sec / -  
(+32 sec)

2.8 min / **17** sec / **6.4** sec  
(+32 sec)

Read base-small.eap  
(docgen.on=true: ~93 exported diagrams)

34 sec / 10 sec / -  
(+12 sec)

34 sec / **8** sec / **1.3** sec  
(+12 sec)

Performance for MS Word document generation has been significantly improved for very large documents with non-formatted tables (thanks to Andre Maizener's tip on ctrl-F11 !). This will work only on the template that has been saved as native Office 2007/2010 document (.docx), without compatibility enabled, because in this case only, it is possible to programmatically disable field updates.

However, for documents with fancy table formatting (and very large tables), there is very little performance improvement (beauty costs :-).

Note that the "magic number" 12 is not anymore magic. We have found best numbers for our running environment, but you may want to try other numbers that may be better for your local running environment (until we complete the new implementation, based on working with simple XML instead of COM API).

MS Word doc generation

01v07  
(docgen.saveReopenEvery) duration

01v08  
(docgen.saveReopenEvery) duration

speed improvement

IEC61970-301 Base  
(637 tables, 67 figures)

(12) 43.3 min

(27) 9.5 min

4.6 x

IEC61968-11  
(378 tables, 37 figures)

(16) 12.1 min

(24) 4 min

3.2 x

IEC62325-301  
(637 tables, 40 figures)

(16) 36.5 min

(24) 7.9 min

4.6 x

IEC61970-302 Dynamics  
(387 tables, 178 figures)

(12) 18.5 min

(27) 6 min

3.1 x

IEC61850-7-3 including a subset of IEC61850-7-2, with special table formatting  
(134 tables, 31 figures)

(12) 4.4\* min

(5) 3.9 min

1.1 x\*

IEC61850-7-4, with special table formatting  
(244 **huge** tables, 40 figures)

(12) 41.4 min

(27) 27.5 min

1.5 x

base-small  
(102 tables, 27 figures)

(12) 0.8 min

(5) 0.7 min

1.1 x\*\*

\* jCleanCim-01v07 does not support printing tracking CDC's attributes (special FC), so there are some 15 tables that are much smaller when printing with 01v07 than with 01v08.

\*\* This document is relatively small, so the cumulative effect of appending tables does now have time to show up.

#### Potential backwards compatibility breaking changes for the application user

*   With the new model [loading implementation](#db), we discarded the boolean option `model.useSql` in favour of choice option `model.builder`:
    *   `model.builder=db` (or empty, or null) is the new default, because fastest, and because it's suitable for quick validation and statistics printing, as well as for diagram-less MS Word or XML document generation.
    *   `model.builder=sqlxml` is equivalent of `model.useSql=true` from 01v07. Use it when packing a release (with XMI exports) or generating documentation (with full diagrams export).
    *   `model.builder=japi` is the equivalent of the old default; you will most likely never anymore need to use this one (but we keep it in case Sparx changes one day their underlying database schema - so we can still work before supporting new shema; or Sparx fulfils the expectation of their many customers and provides a rocket fast implementation of the API)
*   With 01v08, we have started, but not finished, the new MS Word document generation implementation, in order to reduce time required for this task. This new implementation will be documented when fully implemented. In the meantime, ensure you use a new onfiguration option `docgen.word.useDocFormat` set to `true`. The intent for setting (the-still-to-come) new feature as default is because we anticipate a huge performance gain, and in the future, there should be no reason to fall back to the extremely slow COM API-based implementation.

#### Other functional changes

*   docgen.word: now uniformly printing deprecated as part of description for all UML elements.
*   docgen.word: now logging detailed table-generation times at debug level (detailed log).
*   builder.ea: now catching and logging to screen the duplicate EA GUID when present in the model (base-small.eap contains the example of the base CIM16v14 to exercise the logging). Log starts like this: `ERROR +++ EA consistency error - duplicate EA GUID:`. In this release, it is just logged with model building (later on, we may provide a validator).
*   CIM-specific:
    *   \-
*   IEC61850-specific:
    *   docgen.xml: Added support for web-access schema version=0.11, for printing empty literals where required by SCL, and the (previously) forgotten identifier and text for those presence conditions that have (condID) argument.
    *   docgen.word: Implemented the logic for new presence condition (na) in the derived-statistic context, for every DO in an LN not-inheriting from StatisticsLN (if you don't understand this sentence, don't worry, you're not the only one; the statistics model is just inherently not understandable at all and we hope to be able to fix it in Ed.3).
    *   docgen.\*: Now printing empty string for document generation in Word, as well as XML (01v08a) in case of a literal with `scl:emptyValue` tagged value (email exchange with 7-3 editors on October 8, 2012).
    *   docgen.xml: Now printing in XML doc the text of condID (presence conditions argument).
    *   docgen.word, docgen.xml: Added support for printing tracking data attributes (FC=SR) just before description attributes.
    *   docgen.word: Removed printing of the text (description) for all presence conditions every time they are used; kept only the table where they are first defined, as well as the condID text for machine non-processable presence conditions only.

#### New validation rules:

*   ClassesWithRootPropSet; works only with `model.builder` other than `japi`, because the EA API does not provide a method to query whether root UML property is set.
*   CIM-specific:
    *   \-
*   IEC61850-specific:
    *   Iec61850ClassesWithMissingCondIDTextInConstraints
    *   Iec61850FCDAAttributesWithMissingConstraint: This is the maxId/minId constraint on multi-valued attributes within CDCs

#### Bug fixes:

*   builder: now correctly reading connector alias if `model.builder` different from `japi`.
*   builder: now correctly reading multiple stereotypes on association ends if `model.builder` different from `japi`.
*   docgen.xml: now correctly printing package subtitle in XML documentation.
*   docgen.collector: (bug submitted by Christoph Fleischer - thanx !) fixed NPE if constraint missing/null from multi-valued attribute.
*   model, statistics: now classifying correctly CIM attributes whose type is enumeration.
*   (01v08a) builder, validation: (bug submitted by Pat Brown - thanx !) when a class embedded into another class has an association, the builder was throwing NPE on the association with that embedded class; this has now been fixed, and the old validation rule about embedded class consequently works properly (base-small.eap, class Strawberry :-).

#### Known issues / limitations:

*   MS Word and XML doc generation are mutually exclusive. In other words, you have to provide _either_ the MS Word file names _or_ XML file names through configuration options. If required, in some future release, we may support generation of it all. However, the time required for MS Word doc generation is huge (minutes or tenths of minutes) as compared to XML doc generation (seconds).

#### Implementation and packaging:

*   added jars for jackcess and docx4j (and their respective dependencies) and removed dependency on apache commons-lang3 (using simply commons-lang, requred by both jackcess and docx4j).
*   started replacing use of dom4j/jaxen with the standard jvm's JAX-P.
*   updated jars for newer versions of log4j and UMLGraph (distribution build)
*   updated IECDomain.xsd (web-access schema) to version=0.11.
*   builder.ea: heavilly refactored the package and its sub-packages to clearly split EA API dependant code from pure platform-independent Java code; and to use literally the same implementation of SQL result-set based initialisation of the model with either EA repository (SQL+XML) or without it (Access DB).
*   docgen.collector: centralised constructor logging level for implementations of `AbstractObjectDoc` and by default disabled it (instead of the previous trace level = was very chatty and was generating tonnes of detailed log traces).
*   common, util: moved some classes into new package `util`.

#### Documentation:

*   updated the presentation


## jCleanCim-01v07


**Release Date: 26-Aug-2012**

#### New features

##### Fast EA model reading

In this release, we tried again to improve performance, because our standard UML models grow and the COM APIs (EA and MS Word) are terribly slow.

On EA side, we introduced a new configuration option `**model.useSql**` that is false/empty by default, so it does not change the previous behaviour. When set to true, we use SQL bulk queries and process the returned XML dataset with XPath, instead of calling the EA API methods with their extremely slow collections and chatty SQL queries. This improves the performance of reading the model from EA repository _for an order of magnitude_ - see numbers in the performance section below. The speed up is a bit less significant when document generation is enabled, because this new implementation does not concern diagram export (which is inherently slow: 300 - 500 ms per diagram).

##### Exporting XMI files

This is a new top level feature (sibling to validation, statistics and doc generation), and is introduced with the new configuration property `xmiexport.on` that is false/empty by default, so it does not change the previous behaviour (and because this takes time: EA is very slow at exporting to XMI in every set-up).

As a CIM model manager, you can set `xmiexport.on=true` once you're ready to publish the release (i.e., you've done editing of the model and other files, fixed validation warnings and errors, etc.). Instead of the error-prone manual exporting 3 XMI files (in different XMI formats) from within an open instance of Enterprise Architect, this option will result in an automatic export of those XMI files. The principle for the output files is similar to what we do for document generation: if the XMI files already exist in the ./output directory, they are backed up and the .xmi is saved in a new file. When done, you just need to copy these 3 .xmi files into your release directory.

For IEC61850 UML model managers, at present there is no need to do any XMI export, because we don't have profiling like in CIM, and those who want to see some UML seem to be happy with Enterprise Architect. Still, this option will generate the same 3 XMI exports as for CIM models (i.e., there is no option to select only one format, or one name space, or similar).

#### Performance improvements in release 01v07

Release 01v07 does contain a full new implementation of alternative way of initialising in-memory model from Enterprise Architect: instead of using simply the API, we now do the bulk SQL queries, and initialise the model from the returned XML data by using XPath. Performance improvement is huge, at a cost of potential need to trick the EA model: see [EA ordering errors](#eaErrorOrdering).

The following numbers have been traced from runs on Lenovo ThinkPad T410 (i5 CPU, 3GB memory), **64-bit** Windows 7 Enterprise Edition, Office 2010, Enterprise Architect 9, 32-bit Java **1.7.0\_05**. Performance has been significantly improved for reading from EA file; there are no changes in timing for MS Word document generation (yet):

EA-dependent operation

01v07 API vs. SQL+XML  
  
duration - no diagram export

01v07 API vs. SQL+XML  
  
duration - normative+doc diagram export (docgen.on)

Open .eap file of any size

~5 sec (SSD hard disk)

~5 sec (SSD hard disk)

Read full CIM model from .eap file

(~1500 classes, ~5900 attributes, ~1080 associations, ~80 dependencies, ~490 diagrams, ~250 diagrams exported when docgen.on=true)

2.9 min vs. 26 sec

3.5 min vs. 1 min

(both have +35 sec, for diagrams export)

Read full IEC61850 model from .eap file

(~1420 classes, ~5150 attributes, ~90 associations, ~300 operations, ~370 dependencies, ~200 diagrams)

2.1 min vs. 15 sec

3.4 min vs. 45 sec

MS Word doc generation

01v06  
  
docgen.saveReopenEvery / duration

01v07  
  
docgen.saveReopenEvery / duration

IEC61970-301, without Dynamics  
  
(597 tables, 84 figures)

12 / 38.5 min

12 / 38.5 min

IEC61968-11  
  
(267 tables, 62 figures)

16 / 5.5 min

16 / 5.5 min

IEC62325-301  
  
(623 tables, 55 figures)

16 / ? min

16 / ? min

IEC61850-7-3, with special table formatting  
  
(84 tables, 30 figures)

12 / 2.2 min

(without semantic table)  
  
12 / 2.1 min  
  
8 / 2 min

IEC61850-7-4, with special table formatting  
  
(234 tables, 32 figures)

12 / 41.2 min

12 / 41.2 min  
  
16 / 38 min  
  
20 / 35.5 min

#### Potential backwards compatibility breaking changes for the application user

*   model.builder.ea: Previously, we were exporting literally all the diagrams when `docgen.on=true`. From this release, we export only normative and documentation diagrams, and not informative ones (unless the corresponding configuration option is set, for debugging purposes). This makes it faster to read the EA model and export only those diagrams that ever may be used in the documents.
*   config, model.builder.ea: We have removed configuration option `model.picsRelpath` that allowed to specify an arbitrary relative path under which to save diagrams exported from UML, in case document generation is enabled. This flexibility for paths made it hard to process relative paths for correct inclusion of relative URL into XML output documents, while preserving the knowledge of the absolute path (as required for saving exported diagrams and for their pasting into MS Word document). So, now the place for diagram export has been fixed to the previous default location - project's ./output/pics/ directory. All the other behaviour is as before, i.e:
    *   if doc generation is with an .xml output file, pics are not removed after exiting the application, because we need them to accompany the generated XML documents; for MS Word output file, we remove diagrams, because they are already included in the generated document.
    *   ensure you remove from time to time the backup copies of the ./output/pics directory (the same way you do with other backup copies of output files).

#### Other functional changes

*   application: we log the time for EA initialisation, and the total application time
*   common: timing logs start with the string "time\[" so you can search for checkpoints
*   builder.ea: we log the time that EA methods take to export UML diagrams.
*   builder.ea: finally found the way to fetch the type and name of connector ends.
*   stats: we now log on the console all tagged values with all the UML objects defining it; this used be logged at DEBUG level in 01v06, now it's INFO (and visible on the console).
*   model: added support to recognise Object diagram.
*   model: now logging also owner when validating/printing association ends.
*   CIM-specific:
    *   validation: we now allow CIM class as well to contain boundary, because IEC61970::Dynamics package has many diagrams with boundaries (hard to beleive, but EA seams to store diagram element boundary within a class...)
*   IEC61850-specific:
    *   model, collector, docgen, IECDomain.xsd: as resolution for wg10-0491, added support for new stereotypes (statistics for CDCs, admin for LNs) and additional ds presence conditions for DOs.
    *   collector: decreased width for 7-3 CDC tables' columns TrgOp and M/O/C for wg10-0440.

#### New validation rules:

*   \-
*   CIM-specific:
    *   \-
*   IEC61850-specific:
    *   \-

#### Bug fixes:

*   builder.ea: now adding the attribute/operation that has duplicate position (before, they were overwritten).
*   docgen.writer.xml: now correctly including internal ID as a part of fabricated reference for the -doc.xml file (instead of just '0'); this ensures uniqueness of entries in that file.
*   docgen.writer.xml: now correctly writing only native properties' alias and aliasID and not inherited ones (same as for desc and descID; otherwise, we have duplicate -doc.xml entries).
*   model: (IEC 61850) now correctly determining type for some abstract IEC61850 classes of the sample base-small.eap file (instead of deducing 'unknown' type); consequently, former -spec.xml errors are not applicable anymore when file generated.
*   builder.ea: now correctly dealing with all stereotypes, while ignoring duplicate ones.
*   docgen.writer.xml: fixed relative path for diagrams in the generated -spec.xml file.

#### Known issues / limitations:

*   MS Word and XML doc generation are mutually exclusive. In other words, you have to provide _either_ the MS Word file names _or_ XML file names through configuration options. If required, in some future release, we may support generation of it all. However, the time required for MS Word doc generation is huge (minutes or tenths of minutes) as compared to XML doc generation (seconds).

#### Implementation and packaging:

*   builder, common: On the forum, somebody wrote that apparently EA goes through the clipboard internally even when exporting diagram to file, so I thought clipboard only may be faster and implemented the possiblity to use EA's export of a .bmp file to clipboard then saving that .bmp for pasting into Word/link for XML. However, performance-wise, it's a bit slower than the original EA's export to .png file: for ~100 diagrams of the base-small.eap, it takes ~3 seconds more. Therefore, I just kept the original export to .png file as faster.
*   updated jars and dlls for newer versions of EA (934) and jacob (jacob-1.17-M2-x86)
*   builder: Full new implementation of reading from EA by using SQL bulk query on tables of interest, and processing returned XML with XPath.
*   build.xml ant file: added explicit target compatibility for binaries to Java 1.6 (because we develop on Java 1.7), to ensure the binary distribution will work on both 1.6 and 1.7.

#### Documentation:

*   updated the presentation


## jCleanCim-01v06


**Release Date: 25-Mar-2012**

#### New features

This release has a fully functional new feature: XML documentation generation for both IEC 61850 and CIM name spaces. This feature was driven by IEC 61850 needs, and we provided an implementation for CIM as well (this part was trivial :-). The XML schema is included in the distribution, in the ./input directory. One schema (IECDomain.xsd) has been defined, to generate 2 XML instance files:

*   IECDomainDoc.xml contains all the translatable documentation content, such as class/attribute/diagram documentation, titles, captions and similar. It is extremely simple, with just one kind of element per name space: `Doc`. That element has an `id` attribute, and the mixed content text.
*   IECDomainSpec.xml contains "pure" specification; it never gets translated and contains normative strings, as used for generation of various implementation artefacts. In addition, it contains a lot of meta-data relevant for document generation, including references (`Doc.id`) to the above-mentioned `Doc` elements from the pure documentation file.

This split makes it possible to have a relatively lean specification exported to one XML file, with the separate pure documentation content available in one or more XML files, one per language. We obviously produce the default documentation in English, from the UML model, but the translators can produce any number of translated replicas that would all go in-line with a single specification XML file.

The schema provides support for both IEC 61850 name spaces (the way they are defined in IEC 61850-7-1) and their CIM counterparts (simply deduced from existing CIM Version classes), and by default the XML files are generated for anything found in the UML model, both IEC 61850 and CIM (as demonstrated with the mixed sample buggy model in ./input/base-small.eap). However, you can filter per IEC working group which parts you actually want auto-generated from the model. This is supported by the new configuration option `docgen.xml.scope` , which works exactly the same way as the other scope options - this scope applies to XML doc generation only.

Similar to MS Word document generation, XML document generation requires configuration for output files (schema name does not need be configured, it is found automatically), with the two new options: `docgen.xml.outSpec` holds the name of the instance file for the normative content (specification), and `docgen.xml.outDoc` is the name of the instance file with translatable content.

_**Important:**_ In this initial release, MS Word and XML doc generation are mutually exclusive. In other words, you have to provide _either_ the MS Word file names _or_ XML file names through configuration options. If required, in some future release, we may support generation of it all. However, the time required for MS Word doc generation is huge (minutes or tenths of minutes) as compared to XML doc generation (seconds).

_**Known issue:**_ Generated path for diagrams is wrong; this will be fixed in the next release.

#### Potential backwards compatibility breaking changes for the application user

In this release, these are mainly related to configuration options. We had to do heavy refactoring of existing code to support XML document generation.

*   config, model.builder.ea: added configuration option `model.picsRelpath` to specify relative path under which to save diagrams exported from UML, in case document generation is enabled. Since 01v06, default has changed from the user temporary directory to the projects output/pics/ directory; you can specify relative path for the directory of your choice. If doc generation is with an .xml output file, pics are not removed after exiting the application.
*   config: renamed 5 MS Word-specific docgen options, by inserting ".word". This exercise is to clearly split what is specific to MS Word docgen as opposed to XML docgen, or common docgen options. New names are: `docgen**_.word_** .analysePlaceholders` , `docgen**.word.useFormatting**` , `docgen**_.word_** .saveReopenEvery` , `docgen**_.word_** .inTemplate` and `docgen**_.word_** .outDocument` .
*   config: moved IEC 61850-specific configuration options to separate file (config/config61850.properties), to remove clutter of almost static options from the main configuration file.
*   IEC 61850-specific:
    *   config, docgen.collector: renamed configuration option `validation.iec61850.packageDoAbbr` to plural form `validation.iec61850.package**s**DoAbbr` and added support for multiple DO abbreviation packages; this is to support IEC 61850-7-410 and IEC 61850-7-420 which also have DO abbreviations.
    *   config, docgen.collector: replaced configuration option `validation.iec61850.packagesExtTitle` with two options: `validation.iec61850.packagesLn` and `validation.iec61850.packagesCdc`, to eliminate hard-coded package names for LN and CDC packages, that require special formatting for titles.
    *   config, docgen.collector: removed configuration option `docgen.orderCaseInsensitive`. The original IEC 61850-7-4 abbreviations, and data semantic tables in both IEC 61850-7-4 and IEC 61850-7-3 were using case-insensitive ordering. However, with the standard string processing by software, case-insensitive ordering will give one entry for "A" and "a" (so, one will be "swallowed", because considered as duplicate).
    *   docgen.writer: renamed placeholder `startUmlEnumerations` to `startUmlSclEnums`.
    *   config, docgen.collector: replaced configuration option `validation.iec61850.packageAndClassFC` with two options: `validation.iec61850.packageFC` and `validation.iec61850.packageTrgOp`, to allow for special formatting of content from IEC 61850-7-2. The corresponding placeholders can now be placed into a heading.
    *   config, model: on UML model managers tutorial, we agreed to rename top level packages (per WG). Version classes in UML have also been renamed to reflect the new containing package name. jCleanCim now will correctly identify those NEW version classes and top level package names (and older models may not produce outputs you expect.)

### Performance deterioration in release 01v06 with Office 2010 and EA 9

Release 01v06 does not contain any code modifications relevant to interaction with MS Word or Enterprise Architect. However, more recent versions of these applications seem to deteriorate performance (unless it is influenced by some PC configuration change, applied automatically by the corporate IT, which I cannot influence).

The following numbers have been traced from runs on Lenovo ThinkPad T410 (i5 CPU, 3GB memory), 32-bit Windows 7 Enterprise Edition, Office **2010**, Enterprise Architect **9**, Java **1.6.0\_26**; numbers \[+##\] are increase of counts or durations in the current model version as compared to the previous:

EA-dependent operation

Duration - no diagram export  
  
01v06 API

Duration - normative diagram export (docgen.on)  
  
01v06 API

Open .eap file of any size

7.2 \[+11%\] sec

7.2 \[+11%\] sec

Read full CIM model from .eap file

(~1350 classes, ~5500 \[+200\] attributes, ~1000 associations, ~80 \[+20\] dependencies, ~400 \[+50\] diagrams; \[+3% of items\])

2.5 min \[+25% of time\]

4 min \[+21% of time\]

Read full IEC61850 model from .eap file

(~1300 classes, ~4500 attributes, ~90 associations, ~300 operations, ~370 dependencies, ~200 diagrams)

1.9 min \[+27% of time\]

3.4 min \[+62% of time\]

MS Word doc generation

01v06  
  
docgen.saveReopenEvery / duration

IEC61970-301, without Dynamics  
  
(597 tables, 84 figures)

12 / 38.5 min \[+7%\]

IEC61968-11  
  
(267 tables, 62 figures)

16 / 5.5 min \[+10%\]

IEC62325-301  
  
(623 tables, 55 figures)

16 / ? min

IEC61850-7-3, with special table formatting  
  
(84 tables, 30 figures)

12 / 2.2 \[+50%\] min

IEC61850-7-4, with special table formatting  
  
(234 tables, 32 figures; half of tables have 12 more rows than previous models)

12 / 41.2 min \[+400%\]  
  

#### Functional changes

*   config/log4j.xml: logs are now stored in 2 files: jCleanCim-detailed.log for debugging purposes (this is what used to be the only log file before) and the jCleanCim.log for tracing a _single program execution_, that you as the application user can analyse; this one contains exactly the same logging output as the console. For model managers, this is also what you want to include in the UML model release.
*   model, stats: added better support for tagged values. Model now returns map per tag name, each name being associated with the set of all the UML objects using it. Statistics now includes the tag names per nature/per owner, and we log (at DEBUG level) the details per tag name for all elements in the model.
*   model, validation, docgen.collector: added support for new stereotype "deprecated".
*   model: initialised all the implementations of UmlKind with machine-processable token to be used for XML doc generation.
*   CIM-specific:
    *   docgen.collector: slightly changed table layouts for attributes and association ends, as agreed with the CIM model managers board; same layouts will be used for both information model and profiles documentation.
    *   docgen.collector: added support for printing attribute initial value in case it is a default, for the needs of dynamics package.
*   IEC61850-specific:
    *   config, docgen.collector: added new configuration options `validation.iec61850.packagesDa`, to avoid hard-coded package name.
    *   config, docgen.collector: added new configuration option `validation.iec61850.packagesBasic`, to allow for printing basic (core) types from IEC 61850-7-2.
    *   docgen.collector: added new table layout specific to 61850 association ends.
    *   model: added support to recognise arguments for class constraints (presence conditions).
    *   model, docgen.collector: added support for attributes 'tissuesApplied' and 'umlVersion' for namespace classes.
    *   config61850.properties: added static configuration parameters to support doc generation for IEC 61400-25-2, assuming the relevant packages will end with "\_25\_2".
    *   config.properties: added (commented) proposed configuration parameters to support doc generation for IEC 61400-25-2.

#### New validation rules:

*   \-
*   CIM-specific:
    *   \-
*   IEC61850-specific:
    *   Iec61850DOAbbreviationLiteralsDuplicateDefinition
    *   Iec61850DOAttributesWithTooLongName

#### Bug fixes:

*   \-

#### Implementation and packaging:

*   validation: moved more common implementation of rules to abstract class.

#### Documentation:

*   updated the presentation


## jCleanCim-01v05a


**Release Date: 31-Jul-2011**

*   bug fixes:
    *   docgen.word: WG14/WG16 association ends not anymore (wrongly) selected for printing in 61970-301.
*   implementation and packaging:
    *   refactored docgen packages to collector and writer, and moved implementation-specific stuff under them
    *   totally removed all circular dependencies


## jCleanCim-01v05


**Release Date: 17-Jul-2011**

#### Performance improvements in release 01v05

In release 01v05, we have changed the implementation in many places to optimise for performance. **_Most significant improvements can be achieved when using the new configuration option: `docgen.saveReopenEvery`_** . This option sets the number of tables we generate before we close and reopen the MS Word auto-generated document. The action of closing and reopening the document seems to reset something in the "counting memory" of MS Word, so that we can obtain significant speed improvement. Time to save and reopen file takes in our environment between 1.2-7.7 minutes (maximum is for IEC61970-301 that is the largest document generated). If `docgen.saveReopenEvery` is too low, it will slow down the generation of the first ~200 tables, when inserting captions is fast. If it is too high, the MS Word "counting memory" when inserting captions grows exponentially and destroys performance.

The config.properties file contains values for `docgen.saveReopenEvery` which have been proven to be close to the optimum per file tested in our environment, but you can try to slightly increase/decrease that number to see the impact.

The following numbers have been traced from runs on Lenovo ThinkPad T410 (i5 CPU, 3GB memory), 32-bit Windows 7 Enterprise Edition, Office **2007**, Enterprise Architect **7.5**, Java **1.6.0\_24**:

Operation

Duration  
  
(01v03)

Duration  
  
(01v04, docgen.on)

Duration  
  
(01v05)

Duration  
  
(01v05, docgen.on)

Open .eap file of any size

6.5 sec

6.5 sec

6.5 sec

6.5 sec

Read full CIM model from .eap file

(~1350 classes, ~5300 attributes, ~1000 associations, ~60 dependencies, ~350 diagrams)

4.7 min

5.9 min

2.0 min (no diagram export)

3.3 min (with diagram export)

Read full IEC61850 model from .eap file

(~1000 classes, ~3500 attributes, ~70 associations, ~300 operations, ~330 dependencies, ~130 diagrams)

2.5 min

3.3 min

1.5 min (no diagram export)

2.1 min (with diagram export)

MS Word doc generation of IEC61970-301, with Dynamics

(850 tables, 98 figures, docgen.saveReopenEvery = 12)

\-

18 hours!

(n/a)

50 min

MS Word doc generation of IEC61970-301, without Dynamics

(597 tables, 84 figures, docgen.saveReopenEvery = 12)

\-

\-

(n/a)

36 min

MS Word doc generation of IEC61968-11

(267 tables, 62 figures, docgen.saveReopenEvery = 16)

27 min

29 min

(n/a)

5 min

MS Word doc generation of IEC62325-301

(623 tables, 55 figures, docgen.saveReopenEvery = 16)

\-

6 hours!

(n/a)

31 min

MS Word doc generation of IEC61850-7-3, with special table formatting

(84 tables, 30 figures), docgen.saveReopenEvery = 12)

\-

6.2 min

(n/a)

1.5 min

MS Word doc generation of IEC61850-7-4, with special table formatting

(234 tables, 32 figures, docgen.saveReopenEvery = 12)

70 min

84 min

(n/a)

10.3 min

#### Changes in release 01v05

*   potential backwards compatibility breaking changes for the application user:
    *   running/developping with eclipse: now using eclipse-3.7 (indigo), but we have noticed no problems at all
    *   building with ant (in case you changed the original build.xml): now using ant-1.8.2 (also bundled with eclipse-3.7); we have noticed no problems at all
    *   config: renamed IEC61850-specific docgen option `docgen.includeInheritanceFromMetamodel` to `docgen.iec61850.includeMetamodelInheritance`
    *   model, docgen: EA does not export anymore any diagram that is under any package with name "DetailedDiagrams", because these diagrams are never meant to be used placeholders and are never printed (speed gain when reading model!)
    *   model, docgen: as agreed with CIM model managers, we reserve the prefix "Doc" for a package Doc\[AnyName\] under package \[AnyName\] to hold diagrams used in examples in the MS Word template, but that should not be printed in the actual UML model description in Clause 6 of CIM-based standards. If you have a model that contains such a package, it will not be printed when generating documentation. However, you can use its name to put in a placeholder anywhere in the MS Word template.
*   non-functional changes (improvements):
    *   builder.ea: replaced usage of clipboard for diagrams with file system. This means that the clipboard is "busy" only during model loading when docgen is enabled, instead of during the lengthy doc generation. Currently, the diagrams are saved as temporary files in the default temporary directory (differs between WinXP and Win7), and deleted upon jCleanCim exit; in a future release, the diagram files may be saved under output directory, and kept there for XML/HTML doc generation.
    *   docgen: decreased memory usage during docgen about 3-4 times (depending on what gets printed)
    *   builder.ea: significant improvement of speed, especially for models with lots of documentation, and when docgen is disabled (due to skipping diagrams export)
    *   docgen: significant improvement of speed, especially for large documents
*   functional changes:
    *   application: added command line option `-version` to only print the application and JRE version information and exit
    *   config, docgen: added configuration option `docgen.saveReopenEvery` to speed up MS Word generation process
    *   builder: added support to retain diagrams from elements in a package that are otherwise skipped (such as statecharts) \[wg10-0241\]
    *   CIM-specific:
        *   config: renamed model configuration option `profiles.dirname` to `profiles.dirname**s**` to allow for filtering/combination of profiles sub-directories (similar to what we already do with `validation.scope`)
        *   config: added property `profiles.relpath`, as relative path for profiles directories from `profiles.dirnames`
        *   builder.xsd: now packaging sample profiles under ./input/profiles directory
        *   builder.xsd: _just started_ adding support for building in-memory model from multiple XSD profiles
    *   IEC61850-specific:
        *   docgen: added new configuration option `docgen.iec61850.writeUmlTypes`, to allow for printing real types from UML, as they are, without processing required for official documents; this is useful for debugging only
        *   docgen: for all ENUMERATED and CODED ENUM attributes, and for ENS/ENC/ENG data objects, now printing actual (primitive) enumerated type name - the one ending with "Kind"
        *   docgen: removed printing "1" for multi-instance DOs, as this is not applicable anymore, with the new presence conditions \[wg10-0178\]
        *   docgen: temporarily printing presence conditions description before LN/CDC/DA tables to facilitate moving from old to new presence condition names
        *   docgen: started adding support for WebAccess XML generation
*   new validation rules:
    *   \-
    *   CIM-specific:
        *   \-
    *   IEC61850-specific:
        *   \-
*   bug fixes:
    *   docgen: fixed regression bug from 01v04 where output directory in a fresh installation directory was wrongly created under ./null/output instead of ./output
    *   application, config: fixed NPE when running jCleanCim with option -help
    *   application, run.bat: added possibility to specify command line arguments to `run`
*   implementation and packaging:
    *   builder: isolated classes specific to EA and XSD builders into sub-packages `ea` and `xsd`, respectively
    *   builder.ea: replaced all EA for-each iterations with GetAt(), and added check for collection size, to avoid round-trips to underlying RDBMS
    *   builder.ea: replaced EA calls that return raw and HTML doc by EaNotesCleaner class, and significantly improved performance for model building from EA (in particular, for HTML reading)
    *   model: renamed package `eamodel` to `model`, as it is independent of EA (still, some names in enums and such are those specific to EA, but this will be refactored in some future release)
    *   model, statistics: moved some logging methods from model to statistics
    *   xml: added new package `xml` containing facade to dom4j - this is in preparation for XML processing: input from XSD profiles and output for XML documentation
    *   docgen: added sub-package `xml61850` for creation of IEC61850 namespace description as XML
    *   docgen: Made explicit separation of doc collecting (independent of the format) from doc generation through Writer interface and WriterFactory class and arranged other classes into two new sub-packages: `collector` and `writer`. Note that doc collecting and writing were always separate steps by design, but generation called explicitly MSWordWriter constructor; this is now isolated through WriterFactory, which calls corresponding Writer implementation, based on output file format, specified in configuration.
    *   docgen: refactored to interfaces (Cursor, Range, etc.) the doc writing, with MSWord-specific implementation
    *   docgen.word: completely reimplemented table generation (for speed)
    *   docgen.word: now inserting diagram from file instead of from clipboard - it's faster
    *   updated UmlGraph.jar to version 5.4
    *   updated Jacob libraries to 1.15-M4
    *   updated commons-cli library to 1.2
    *   updated junit library to 8.2
    *   added dependency apache-lang3 library - used for HTML escaping and for time tracking
    *   added two more dependencies - dom4j and jaxen (only if XPath used through dom4j); planned for use for CIM profile-related features to come, and for 61850-to-XML for WebAccess
*   documentation:
    *   added more hints related to performance
    *   added hyperlinks with TOC
    *   updated the presentation


## jCleanCim-01v04


**Release Date: 13-Mar-2011**

*   backwards compatibility breaking changes for the application user (note: those for potential application developer have not been tracked, because of massive refactorings in almost all packages). They involve changes in some property names (keys) defined in configuration file. _If you want to reuse your existing configuration properties file from previous releases of jCleanCim, ensure you rename the properties as indicated here:_
    *   config: configuration property names have changed for `model` (now `model**.filename**` ) and `nonCimNature` (now `**model.nature.iec61850**` ). These two properties have always been at the very top of the default config.properties file provided with any jCleanCim distribution. _Rationale_: Very short proprety name (key) is not a good key, and difficult to distinguish in the code (e.g., searching for word "model" gives hundreds of hits, while search for "model.filename" gives what is actually needed).
    *   config: configuration property names have changed for all 7 `validation.[].on` properties (now `validation.[].**off**` ). _Rationale_: By adding functionality to filter individual rules for validation (see below), it is more obvious to have the defaults that do full validation of everything and let the user describe "exceptions", i.e., what to skip. With this change, the full validation is enabled by default (even if properties are not defined in the configuration file at all), and you need to specify only what you want to disable.
*   functional changes:
    *   main, builder, docgen: now logging versions of software used: EA, MS Word, jCleanCim and jvm/OS
    *   all: logging preserves order (=deterministic, for easier comparison)
    *   model, docgen: added support for HTML (formatted) descriptions for all UML elements; however, HTML-based formatting still does not work properly in Word
    *   config, docgen: added configuration option `docgen.useFormatting` to use or ignore HTML when writing to MS Word (default: false)
    *   statistics: now showing also the number of informative elements (if any) per scope - thanks to Eric Lambert (ERDF) for this idea, the feature shows extremely useful for model management
    *   docgen: added support to add custom properties to the auto-generated MS Word document: uml=model-file-name, jCleanCim=version-number \[wg10-0004\]
    *   config: all the description of configuration options has been moved from config.properties file into the code (class `org.tanjakostic.jcleancim.common.Config`), and the documentation of those options is now available as hyperlinked javadoc in `doc/api/index.html` for all distributions, and also in `doc/jCleanCim-[version].pdf` file in the binary distribution
    *   config: added configuration option `validation.rules.off` to individually disable validation rules
    *   CIM-specific:
        *   docgen: removed placeholder "startUmlDocPackage"; that was used in older versions of IEC 61970-301 to display package doc only, but was considered as redundant and is not required anymore (approved by model managers)
        *   model: removed support for stereotype ExtensionPoint (was temporarily used in IEC61968)
        *   config: added configuration option for new functionality: `profiles.crosscheck.on`; _however, the functionality has still not been implemented_
        *   config: added configuration option for new functionality: `profiles.docgen.on`; _however, the functionality has still not been implemented_
        *   config: added model configuration option `profiles.dirname` for telling where the profiles are (from which to cross-check with UML or to generate Word doc); _however, the functionality has still not been implemented_
    *   IEC61850-specific:
        *   docgen: added configuration option to ignore case for alphabetic sorting (used for data semantic tables in 7-4/7-3)
        *   docgen: added support for table name for LNs, CDCs, DAs and enums \[wg10-0031\]
        *   config: added configuration options to distinguish -7-2 part of the model and the meta-model
        *   docgen: for DA headings (7-3), added indication for enum or coded enum
        *   docgen: display conditions immediately before the LN table \[wg10-0044\]
        *   docgen: added type column to DataSemantic table \[wg10-0103\]
        *   model, docgen: added support for `moveAfter` tag for attributes to allow for reordering of tagged CDC attributes
        *   model, docgen: added support for `scl:emptyValue` tag for literals to allow for printing empty value in case the literal is "none" \[wg10-0167\]
        *   docgen: produce one row for FCDAs with fc= SG and SE, remove "\_" from name
        *   docgen: do not print FCDAs with fc= SG and SE in data semantic table
*   new validation rules:
    *   PackagesWithSameName
    *   PackagesMissingDoc
    *   PackagesWithBadDocStart
    *   PackagesWithBadDocEnd
    *   PackagesWithBadCharacterInName
    *   ClassesThatShouldNotBeAssociationClass
    *   EnumClassesWithSingleLiteral
    *   EnumClassesWithTwoLiterals
    *   EnumClassesWithBadName
    *   ClassesMissingDoc
    *   ClassesWithBadDocStart
    *   ClassesWithBadDocEnd
    *   ClassesWithBadCharacterInName
    *   AttributesWithTypeIdMismatch - thanks to Kendall Demaree (Alstom), who uncovered and reported EA inconsistency
    *   AttributesThatAreEnumsInNonEnumeratedClass
    *   AttributesWhoseTypeIsInformative
    *   CimAttributesWithFlagInName
    *   AttributesMissingDoc
    *   AttributesWithBadDocStart
    *   AttributesWithBadDocEnd
    *   AttributesWithBadCharacterInName
    *   OperationsMissingDoc
    *   OperationParametersMissingDoc
    *   OperationReturnTypeParametersMissingDoc
    *   OperationsWithBadDocStart
    *   OperationParametersWithBadDocStart
    *   OperationReturnTypesWithBadDocStart
    *   OperationsWithBadDocEnd
    *   OperationParametersWithBadDocEnd
    *   OperationReturnTypesWithBadDocEnd
    *   OperationsWithBadCharacterInName
    *   OperationParametersWithBadCharacterInName
    *   AssociationsWithSameDocOnBothEnds
    *   AssociationEndsWithUnallowedTags
    *   AssociationEndsWithUnallowedStereotype
    *   AssociationEndsMissingDoc
    *   AssociationEndsWithBadDocStart
    *   AssociationEndsWithBadDocEnd
    *   AssociationEndsWithBadCharacterInName
    *   DiagramsWithBadDocStart
    *   DiagramsWithBadDocEnd
    *   DiagramsWithBadCharacterInName
    *   CIM-specific:
        *   CimClassesWithOldDatatypeStereotype
        *   CimPrimitiveClassesWithIllegalOwner
        *   CimDatatypeClassesWithInvalidAttributes
        *   CimCompoundClassesWithNoAttributes
        *   CimAttributesThatShouldBeReplacedWithAssociation
    *   IEC61850-specific:
        *   Iec61850ClassesWithInvalidConstraints
        *   Iec61850ConditionEnumsWithLiteralsNeverUsedAsConstraints
        *   Iec61850AttributesWithInexistingSibling
*   bug fixes:
    *   app: jCleanCim now can be run from within a directory with the space in its name - thanks to Dianne Heath (Siemens) for reporting the problem and helping to identify its source
    *   (IEC61850) docgen: coded enums now get printed as "CODED ENUM" instead of "ENUMERATION"
    *   (IEC61850) docgen: added 3 NBSP in LN group titles \[wg10-0008\]
    *   (IEC61850) docgen: added 3 NBSP in LN titles \[wg10-0009\]
    *   (IEC61850) model: fixed wrong grouping of transient ACT LN attributes under Control category, instead of SPC transient LN attributes \[wg10-0027\]
    *   (IEC61850) model: fixed order of LN attribute groups to fit the last-minute changes in IEC61850-7-4 \[wg10-0028\]
    *   (IEC61850) docgen: fixed NPE when config option validation.iec61850.packageAndClassFC absent
    *   (IEC61850) docgen: changed column name for LN tables from "Description" to "Explanation" \[wg10-0032\]
*   implementation and packaging:
    *   common: consolidated app flow logging and duration printing
    *   common: added support to execute with no .eap model (to allow for fast testing by building the tiny in-memory models by hand, and in anticipation of support for profile MS Word templates into which to include the doc read from profiles generated by CIMTool); the program flow is ok (no NPE), it's just that there is nothing to validate, collect statistics on, or to write to MS Word
    *   model: added support for UUID and alias to UmlObject
    *   model: added new data structure (UmlObjectData) and refactored all ctors of concrete UmlObject-s to use it
    *   model: added support for multiple stereotypes
    *   model: refactored to builder + model pattern, to allow for creation of in-memory model in different ways (to prepare for creation of the profile models) and for fast testing without EA file (finally!)
    *   model: added lots of tests
    *   validation: heavilly refactored validators and rules; simplified generics and reduced lot of duplication by providing abstract generic implementation for rules common to several subtypes of UmlObject-s (like for tagged values, documentation, names, stereotypes)
    *   docgen: extracted interfaces for figure (diagram) and containers (package and class) docs
    *   docgen: refactored a lot, to allow for formatting
    *   model, docgen: replaced usage of clipboard for diagrams with saving to tmp files (only when docgen.on=true)
    *   ant, run.bat: removed version information from the name of jCleanCim jar, to avoid the need to update the script every time; jar is now always called jCleanCim.jar
    *   ant: externalised application version into config/build.properties for ease of maintenance and to allow application to fetch its version information even when built from within eclipse and without ant build script
*   documentation:
    *   updated performance measurements without anti-virus scan-on-access
    *   updated the presentation


## jCleanCim-01v03


**Release Date: 03-Oct-2010**

*   functional (impacting official IEC documents):
    *   docgen: added border to tables in generated doc (with Word 2007, rendering on the screen made it obvious that some tables were without any border; with Word 2003, this was not obvious)
    *   (CIM) model: added support for CIMDatatype stereotype; still keeping support for Datatype for doc generation (backwards compatibility), but validation reports it as error
*   functional (other):
    *   common: fixed calculation of allowed dependencies among top-package owners
    *   common: added configuration option to not log on console validation steps without errors
    *   common: improved names of configuration options - you must adapt an existing config.properties file!
    *   model: added support in model to retreive HTML and RTF from EA doc in addition to raw text
    *   model: added support to preserve order of attributes/operations, as they are defined within a class
    *   statistics: added filtered statistics, depending on scope and model nature
    *   model: added support for multiple stereotypes
    *   docgen: added support to print custom diagrams (caption = title)
    *   docgen: added support to print in the docgen default vs. const vs. range initial value
    *   (61850) common: added support for other WG owners than WG10
    *   (61850) model: added support for naming convention for non-CIM version class (ends with UMLVersion)
    *   (61850) model: added support for initial value for 61850 enum literals and attributes
    *   (61850) docgen: added docgen support for 61850-7-4 DO abbreviations table
    *   (61850) docgen: added docgen support for 61850-7-4/61850-7-3 enumerations XML
    *   (61850) docgen: added docgen support for 61850-7-3 presence conditions table
    *   (61850) docgen: added docgen support for 61850-7-3 FC table
    *   (61850) model, docgen: added full support for Ed.2. of 61850-7-4/61850-7-3
    *   (61850) model: added support for retaining diagrams from state machine under a class
    *   (61850) statistics: added support to log classes with constraints
    *   (61850) docgen: added config option to generate or not inherited attributes/operations/association ends from the UML meta-model
    *   (61850) validation: added validation rules:
        *   Iec61850DOAttributesThatShouldBeMultivalued
        *   Iec61850DOAttributesWithInvalidName
        *   Iec61850DOAbbreviationLiteralsNeverUsedInDOName
        *   Iec61850DOAttributesWithSameNameDifferentType
*   implementation:
    *   all: added @Override to several implementation methods - stricter compiler settings
    *   test: removed test/AllTests.java classes - eclipse Helios now supports search for all tests in a directory
    *   common: removed test/input/config/\*.properties files, implemented in memory prop creation in tests and added several tests
    *   update: removed update package (that has been deprecated)
    *   validation: removed deprecated TagsCollector class (was used for updates only)
    *   model: moved some more implementation into AbstractUmlObject
    *   model: refactored all \*Kind enums to implement interface UmlKind, so UmlObject now returns UmlKind instead of String
    *   model, docgen: moved attribute grouping logic from docgen to model
    *   validation: refactored validators and sub-validators (removed duplication); renamed sub-validators to rules, made it easier to add a new one
    *   docgen: refactored docgen to interface/abstract-class/impl for everything that prints as table and implemented toString() methods that print to log with TRACE level; it is now relatively straightforward to add an implementation for a new table format.
    *   lib: packaged more recent versions of log4j and junit
    *   (61850) model: made UmlConstraint implement UmlObject, and allowed multiple constraints for a class
    *   (61850) model: added support for IEC61850 kinds of classes and attributes
    *   (61850) validation: allowed boundaries in 61850 classes - this is for pasted pics
    *   (61850) validation: allowed associations with one public end (meta-model)
*   documentation:
    *   moved performance indicators from config.properties file into this one
    *   updated URL to new MTEI site in the doc and in the presentation
    *   updated the presentation


## jCleanCim-01v02


**Release Date: 15-May-2010**

*   functional:
    *   for docgen, removed dots "." from caption ends, and replaced the regular dash "-" in captions with EN DASH "–", as required by IEC editor
    *   added support for WG16 as owner for IEC62325 package
    *   added capability to define that an extension model package has CIM nature (or not)
    *   added sub-validators:
        *   ClassesWithSuperclassesFromUnallowedPackage
        *   DependenciesWithUnallowedDirection
    *   (implementation) moved association ownership calculation from UmlAssociation to OwningWg, and added method that returns "legal" dependencies among WGs top-level packages (so this kind of calculation is all in one place)
    *   improved NPE handling in several places
*   documentation/build flexibility:
    *   replaced simple text documentation with the html documentation, and added copyright.html
    *   elcipse-specific zip distribution (-eclipse.zip) is now exported as eclipse project archive and can be imported as project form this archive; it now also contains default launch configuration (reading model name and properties from default config.properties file)
    *   added ant build file to automate creating binary (-bin.zip) and source (-src.zip) distributions independent of eclipse
    *   added (in ant build) automatic creation of UML within javadoc
    *   added (in ant build) automatic creation of pdf javadoc for -bin.zip distribution
    *   added (in ant build) automatic creation of pdf page documenting ant targets dependencies for -src.zip distribution
*   makeup:
    *   rebranded name to start with lower case letter


## JCleanCim-01v01


**Release Date: 21-Feb-2010**

*   significantly improved docgen speed: for IEC61968-11, docgen time decreased from 62min to 21min
*   TOC and TOF update now works with IEC template as well
*   updated Jacob libraries to 1.15-M3 and EA libraries to build 850 (latest of version 7.5)
*   improved some logging
*   refactored all validators to facilitate adding new ones
*   added chapter in javadoc (in org.tanjakostic.jcleancim.validation) on how to add a new validator
*   added sub-validators:
    *   PackagesWithSameName
    *   EnumClassesWithNoLitterals
    *   ClassesWithSameName
    *   PrimitiveClassesWithAttributes
    *   ClassesWithDuplicateInheritedAttributeNames
    *   ClassesWithDuplicateInheritedAssociationEndNames
    *   ClassesNeverUsedInRelationships
    *   ClassesNeverUsedAsTypeForCIMAttribute
    *   AttributesWithInexistingEnumLiteralAsInitValue
*   fixed bug where association ends were incorrectly set for recursive associations
*   fixed problem with State (element) and Statechart (diagram) having both package and class as parent: see [forum topic](http://www.sparxsystems.com/cgi-bin/yabb/YaBB.cgi?num=1265719248/1#1).
*   did some refactoring to remove circular dependency statistics-model
*   in statistics package, started implementing owner filter
*   added basic calculation and logging of cross-package dependencies
*   eliminated all FindBugs max level warnings (so JCleanCim is a bit cleaner now :-)


## JCleanCim-01v00


**Release Date: 11-Nov-2009**

*   Initial release.

***

Built on 2019-12-20T22:05:24

[Copyright](copyright.md) [License](LICENSE)

[Feedback](mailto:tatjana-dot-kostic-atNoSpam-ieee-dot-org)

[![Valid XHTML 1.0 Strict](http://www.w3.org/Icons/valid-xhtml10)](http://validator.w3.org/check?uri=referer)
