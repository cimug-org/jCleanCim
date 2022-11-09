# jCleanCim

**jCleanCim** is an open source tool for validation and documentation generation from [Enterprise Architect](http://www.sparxsystems.com/products/ea) UML models of IEC TC57 CIM and IEC61850 UML models.

Up until end of 2015 it has been hosted by the [CIM Methods & Tools for Enterprise Integration group](http://cimug.ucaiug.org/MTEI/Shared%20Documents/jCleanCim) on the CIM Users Group web site, with access limited to the CIM and IEC61850 users community members only.

To make it accessible also to non-CIMug members, since 2016, I decided to share it as a full open source tool and host it at my [web space](http://www.tanjakostic.org/jcleancim).

This is a non-GUI Java application and the Java code is fully platform independent. However, it unfortunately must be run on MS Windows machine due to the usage of Enterprise Architect and MS Word automation libraries (.dlls).

*   [Documentation](#documentation)
*   [Which distribution should I download?](#which-distribution-should-i-download)
*   [Dependencies](#dependencies)
*   [Performance indicators](#performance-indicators)
*   [Hints - please read carefully before reporting problems!](#hints)
*   [Configuration documentation](#configuration-documentation)
*   [Disclaimer](#disclaimer)
*   [What's new in this release](#what-s-new-in-this-release)

([Old release notes](oldReleaseNotes.md))

## Documentation
Once you unzip a jCleanCim distribution, `doc` directory contains the full documentation. The important parts of the source code are documented and that documentation is generated as so-called _javadoc_ - namely, a set of web pages that allow for easy navigation.

*   Each distribution contains an up to date set of slides `doc/jCleanCimIntro.pptx` - you may want to start from there.
*   Each distribution contains the javadoc in `doc/api/index.html`. The documentation of the root package `org.tanjakostic.jcleancim` (Description section) is a good starting point.
*   Binary distribution contains also the PDF version of that same javadoc, in a file `doc/jCleanCim-[_version_].pdf` (source distribution contains the Apache ant build script to produce this document, so it is not packaged in source distributions).
*   Source distribution contains the PDF version of the depency graph of the Apache ant build targets (this is useful for a developer only).
*   Finally, each distribution contains the test report, run during the build of the distribution, under `doc/testReport/index.html`. For most users this is not really of interest (but keeps our developer spirit in peace :-).

## Which distribution should I download?

jCleanCim is available in two distributions, depending on how you want to use it.

_Even if you have a 64-bit Windows OS,_ ensure you install a 32-bit Java (JRE: runtime) or SDK (software development kit) and have it appear on your PATH before the potentially already installed 64-bit Java, because Enterprise Architect is still a 32-bit application and requires a 32-bit Java. See the commented text in the run.bat script.

| Distribution | User Kind | Prerequisites | Installing |
|------------------|------------------|------------------|------------------|
| Binary distribution jCleanCim-[version]-bin.zip | Run jCleanCim from the console (cmd.exe). | <ul><li>[32-bit Java 7+](http://java.sun.com/javase/downloads/index.jsp) runtime (JRE). To verify whether you already have it installed, open the console window and type `java -version`<li>Enterprise Architect build 834+ (version 7.1+)<li>(optional: to run doc generation) MS Word 2003+</ul> | Unzip the distribution anywhere on your file system; it will uncompress in its own directory tagged with the version so there is no danger of overwriting an older installation. For example, using WinZip, select "Extract to here" command. |
| Source distribution jCleanCim-[version]-bin.zip | Run jCleanCim from the console (cmd.exe) or from within eclipse.<br/><br/>Develop and build it with Apache ant or with eclipse. | <ul><li>[32-bit Java 7+](http://java.sun.com/javase/downloads/index.jsp) software development kit (SDK). To verify whether you already have it installed, open the console window and type `javac -version`<li>Something to compile the code and create executable, e.g.:<ul><li>Apache [ant 1.7.1+](http://archive.apache.org/dist/ant/binaries/apache-ant-1.7.1-bin.zip). To verify whether you already have it installed, open the console window and type `ant -version`.<li>or an IDE if you are already a Java developer.</ul><li>Enterprise Architect build 834+ (version 7.1+)<li>(optional, runtime: to run doc generation) MS Word 2003+<li>(optional, during ant build: to build javadoc with UML for any distribution, and to document ant build targets dependencies graph) [GraphViz application](http://www.graphviz.org/). To verify whether you already have it installed, open the console window and type `dot -version`</ul> | Same as for binary distribution (jCleanCim-[version]-bin.zip). <br/><br/> If used with eclipse, start eclipse SDK and after unzipping, use Import ->Existing project and browse to the unzipped directory. <br/><br/> Note: This is the most flexible option if you are developing, as you can have the eclipse project anywhere on your disk (not necessarily in an eclipse workspace). |


_Note for source distribution (and if you need to create the distribution yourself)_: Ant build file contains targets that invoke GraphViz application for creation of graphical elements for the documentation (UML-enhanced javadoc, and javadoc in pdf format). If you do not have GraphViz installed on your local system, these targets will be just skipped during the build (even if you get exceptions from the doclet, the javadoc gets generated, just without the UML diagrams). However, if you want to produce jCleanCim distributions, you should install GraphViz in order to have nicer documentation.

## Dependencies
To be self-contained, jCleanCim distributions bundle relevant third party open source/distributable libraries. Java jars are in the project's `lib` directory, and MS Windows dlls are in the `dlls` directory.

The following libraries are packaged with all the distributions of jCleanCim:

*   For access to EA model file:
    *   When we need to export images or XMI, we use EA Java API, which in turn depends on a MS Windows dll. By working directly with EA model file, jCleanCim tries to identify problems at the source (.eap file), before any XMI or other artefact generation takes place.
    *   When we don't need to export images or XMI, we use [Jackcess](http://sourceforge.net/projects/jackcess/files/) library that enables light-fast access from Java to MS Access database (EA model file is exactly that), so this is totally OS independent.
*   For document generation functionality, we use [Jacob](http://sourceforge.net/projects/jacob-project/files/) library that enables access from Java to MS Word automation interface, wrapped into a dll for 32bit MS Windows. The MS Word/Office used is the one on your local machine, and not distributed here.
*   For logging, we use Apache's [log4j](http://logging.apache.org/log4j/).
*   For command line processing, we use Apache's [Commons CLI](http://commons.apache.org/cli/).
*   For string markup processing and some stopwatch functionality, we use Apache's [Commons Lang](http://commons.apache.org/lang/).

The following libraries are packaged only with source distribution of jCleanCim:

*   For unit testing, we use [JUnit 4](http://www.junit.org/). You will need this only if you run or develop tests, or if you are producing distributions (that include running tests and producing rest reports).
*   For generation of ant target dependencies graph, we use [Grand](http://www.ggtools.net/grand/#download) library; it will be ignored if you do not have [GraphViz](http://www.graphviz.org/Download..php) installed. These are not used from the jCleanCim source code.
*   For enhancement of regular javadoc with the UML class diagrams, we use [UmlGraph](http://www.umlgraph.org/) library; it will be ignored if you do not have [GraphViz](http://www.graphviz.org/Download..php) installed. These are not used from the jCleanCim source code.
*   For pdf generation from the javadoc, we use [PDFDoclet](http://sourceforge.net/projects/pdfdoclet/) application. You will need this only if you generate the pdf documentation for binary distribution (from an ant target). The application is otherwise not used from the jCleanCim source code.

Performance indicators
----------------------

Since we talk to EA and to MS Word through their automation APIs, the model building (as a first step in the application) and the MS Word document generation (if enabled) take time:

*   EA automation API implementation unfortunately does not know of bulk CRUD operation, so for every _single_ item to be returned through the API, they perform an SQL query on the underlying Access RDBMS (even for items in a collection!). Determinant factor for performance here are: number of elements (classes, attributes, ...) and the number of diagrams that need to be saved to file. In release 01v07, we had provided a fully new implementation for reading the UML model from EA (with option `model.useSql = true`, in that release only). **Since release 01v08**, we added one more implementation (see [Fast loading of .eap file](oldReleaseNotes.md)) and replaced the `model.useSql` boolean option with the one taking one of three pre-defined string values. **_In short, if you need to export XMI for a model release, or diagrams for document generation, ensure you use `model.builder=sqlxml`, otherwise leave the option empty or set it to `model.builder=db`. See also [hint on fixing ordering errors](#eaErrorOrdering)_** .
*   MS Word is extremely slow at inserting captions for figures, and in particular for tables, as well as in populating and formatting tables. As the number of figures/tables grows, MS Word takes more and more time to insert their captions - similar would happen if you insert captions by hand in an open Word document: higher the number of captions in the document, more time MS Word takes to calculate the number for the caption (and it is impossible to disable this automatic calculation if we want to create tables of figures/tables). **Since release 01v05, we provide a configuration option `docgen.saveReopenEvery` that you should definitely use to speed up MS Word document generation** . Default value is 12, but you should play with your document to find out whether higher value would make it faster. See also [discussion on this option](oldReleaseNotes.md) and its resulting performance improvement.

Java processing - for validation, statistics calculation and documentation collection from in-memory model to pass to the actual writer (s), as well as XML document generation for web-access - takes a couple of seconds for all the models and scenarios tested.

## Hints
Here items thay may be considered as issues (but will likely not be addressed soon) and performance-related advice, so please take them into account when running jCleanCim:

*   **If you run only to validate the model or generated XML or Word doc without diagrams, you should use `model.builder=db` as the fastest way; notice that no digrams can ever get exported with this builder.** With any other builder option, and when `docgen.on=true`, diagrams get exported from the EA model (in order to be used in the generated doc), while if `docgen.on=false` (or empty, or absent), document generation is disabled and we know that we don't need diagrams, so they don't get exported at all. Not exporting diagrams saves a lot of time: between 300-500 ms per normative diagram - for 100 normative diagrams, you save at least half a minute to read the model.
*   EA ordering errors. (since 01v08) In the initial, model building phase, you may see logged ERROR (+++ EA ordering error) for several UML elements (diagrams, packages, classes, etc.). This comes from the fact that EA internal storage for some reason does not always keep up to date the order of items in a container. When using API (very slow), we just follow the order of elements as returned by the EA API. However, when processing SQL query results (faster) or reading the tables directly (the fastest),those position indices may be uninitialised (that's how they are stored in the repository). To ensure the order gets preserved, just open the model and manually move an item from the indicated list up and down - this should trigger the EA internal update mechanism - and then you're set as the error should disappear on the next run.
*   If for some reason you're still using `model.builder=japi` for reading from EA file:
    *   Remove any baselines. They grow the size of data in the underlying RDBMS and every SQL query that EA does (unfortunately: 1 for each single attribute/association/class/package/tagged value/constraint/dependency) will take longer on a larger dataset.
    *   Compact EAP file. From within EA, run regularly Tools - Manage .EAP file - Compact .EAP file. There is no effect if this is executed from EA that you've opened with your own model (that is why this is not feasible programmatically either), so you must open EA with an arbitrary, another model (or just launch EA alone), then select your file to compact.
*   When running MS Word document generation:
    *   (since 01v08) If you can, save your template as native Office 2007+ document (.docx), without compatibility enabled. This will run much faster, because in this case only, it is possible to programmatically disable field updates.
    *   This should run correctly only on an English version of MS Office. _Reason_: We create MS Word documents primarily for IEC, and these need to be in English. We have tried hard, but it is literally impossible to have implementation portable among different language versions of MS Word.

        Note that caption labels and styles are notoriously non-portable among installations of MS Word (same user - 2 computers; different users on the same computer; different installation languages). Caption labels are said to be contained in the user's Normal.dotm template, see [here](https://answers.microsoft.com/en-us/office/forum/office_2007-word/new-caption-label-doesnt-display-in-new-version-of/18b9408d-148f-4c76-8d92-917c875406d8), but not really so, see [here](https://www.msofficeforums.com/word/15715-captions-self-defined.html). Additionally, it seems that MS Word sometimes decides and changes things on its own, which affects the global template and/or registry in a way nobody sees it (and every document you'll edit). For example, if you happen to somehow get language for captions changed, see for example [Word is in English but �Figure?, �Equation? and �Table? appear in Spanish](https://superuser.com/questions/55311/microsoft-word-is-in-english-but-figure-equation-and-table-appear-in-span#55326>Microsoft). jCleanCim does NOT want to modify any user-specific installation of MS Word ! You may need to add by yourself into your Word installation what is missing and what you would like to see printed (e.g., caption label in English if you have German MS Word).

        **If somebody finds a portable way without modifying user's registry, please provide the code with test cases and I will integrate into baseline! Until then, if you are running non-English installation of MS Word, ensure you add custom caption labels in English (Figure, Table). If jCleanCim crashes and you are running a non-English installation of MS Word, it is most likely that you forgot to define for-your-language-non-native caption labels Figure and Table.**

    *   Ensure to update all fields in the MS Word template before starting document generation **_with change tracking turned OFF_** , in particular if you have added/removed tables/figures and their captions. MS Word is known to screw up caption numbers (read: identifiers) when you edit them with change tracking enabled.
    *   Disable automatic spell checking in the styles 'PARAGRAPH', 'TABLE-cell' and 'Normal'.
    *   \[old\] On our developer Windows 7 machine, we have noticed that for some special formatting of diagrams (with resizing or such), the process PrintSpooler (the Windows printing service) kicks in immediately after EA repository gets opened. This manifests at building Domain package from the sample EA repository, when it seems like never-ending. When stopping the PrintSpoolerService (Computer/Manage/Services and Applications/Services/PrintSpooler/Stop) at that point, there are about 20 popups from EA process reporting on some font problem (that is how we concluded it is about some interaction of EA with the OS). If this manifests on your machine, the easiest is to temporarily stop PrintSpooler service while running jCleanCim. Note that this has never happened on Windows XP, only on Windows 7.

## Configuration documentation
For an overview of all configuration options, once you have unpacked the release into your local directory, this link to the javadoc documentation should work: [Config](./doc/api/org/tanjakostic/jcleancim/common/Config.html). You can access the same page from your installation subdirectory [doc/api/index.html](./doc/api/index.html), by selecting from the class list the class `Config` .

## Disclaimer
Starting with release 02v00, jCleanCim has been licensed under the terms of GNU LGPLv3 [license](license.txt) and includes a modified [copyright](copyright.md). The copyright as well as a reference to the license for this software is available at the download site, and is included in every distribution and in every java source file.

Have a look [here](http://choosealicense.com/licenses/#lgpl-v3) for a relatively accessible comparison of licenses.

This software has been developed in my free time. The contained IP is not related in any way to my previous or current employer.


* * *

### Release notes for jCleanCim-02v03 (=jCleanCim-02v02.beta-3), 2019-12-20

* * *

Note that there is no jCleanCim-02v02, only 2 beta milestones (02v02.beta-2 and 02v02.beta-3) that were made available for local needs of WG13 and ENTSO-E CGMES SG work - used to validate CIM canonical and profile models and produce various WG13 documents before the end of the year. As these have been developed, debugged, tested, feature implementation reverted back after failed tests (MS Word caption labels), and over all incorporated new features on the fly - I decided to simply declare the 02v02.beta-3 as 02v03, eventhough the time was too short for a proper update of the .pptx tutorial. This is my first task for next release, I promise :-).

In the meantime, I hope you can enjoy the following new features.

### New features

In this release, main focus was on generic namespace support, stereotypes, document generation options (especially on generating documents from UML profile models), and tireless attempt at supporting English document generation with non-English versions of MS Word. The release also features the contribution used for generation of MIBs for IEC62351-7 (thanks Gigi!) as well as several smaller changes to better support IEC61850 modelling and document generation (more coming in the next release - thanks Laurent!). And as usual, there are a number of new tests and bug fixes.

#### Stereotype `informative`

From this release, you can add stereotype `informative` to _any_ UML model element and it will also be picked for determining whether something is informative, along with the other existing and new logic. The only ignored `informative` stereotype is for dependency as it does not really make sense to tag a dependency as informative (until proven otherwise :-)).

Along with this change, filtering on printing informative elements (controlled by configuration option `docgen.includeInformative` ) has been largely improved, to ensure to really exclude informative content of any kind by default, i.e., when option is set to ("false", "", null). You may want to set that option to `true` to print the informative content for debuging purposes, or during extensions development, but never for official standard documents.

#### New configuration option `docgen.showCustomStereotypes` (TODO - add to .ppt:docgen.showCustomStereotypes=true)

If set "true", it allows to show in the generated document custom UML stereotypes on UML model elements, in addition to built-in stereotypes already handled per model nature. Default is ("false", "", null) to preserve old behaviour, in which all custom stereotypes (non-built-in for a model nature) are not explicitly shown in generated documents. UML model managers in standardisation bodies want to keep this empty/false for main standard documents, and set it to true for these use cases:

1.  peer reviews during standard model development (through use of custom stereotypes such as changed, new, old, delete, discuss, etc.);
2.  review of proposed extensions marked with a given stereotype;
3.  standard document generation from standard extensions model (e.g., European extensions);
4.  users of the tool with models which are neither CIM nor IEC61850 may want to generate UML documentation (MS Word or XML) with their own stereotypes. Default model nature is CIM (unless explicitly specified as IEC61850 with configuration property `model.nature.iec61850`), so the built-in CIM stereotypes are "reserved" and cannot be modified at present.

Note: This option does not filter elements for document generation, it merely allows you to "hide" and show the custom stereotypes on UML elements in the generated document. For actual filtered printing based on custom stereotypes, see the next section.

#### New configuration option `docgen.skipForCustomStereotypes` (TODO - add to .ppt e.g.: docgen.skipForCustomStereotypes=European,new)

This new option allows for selective document generation from UML models of a given nature that contain both standard model parts and extensions tagged with a custom stereotype for that nature. You can use this option now to explicitly skip during document generation all UML elements that have any of the custom stereotypes in the list specified here. This is useful in at least these use cases:

1.  a WG develops extensions which are mixed with the normative parts of the model and tags those extensions with a stereotype (e.g., `new`), and wants to be able to generate documentation both with and without these extensions, e.g. for peer review
2.  an organisation or a project develops extensions tagged with a custom stereotype which are either mixed with the normative parts of the model or are in extension package, but the inheritance and associations result in stereotyped extensions being mixed with the normative parts of the model. The standardisation WG then wants to be able to skip these when generating documents, without manually removing anything from the model (they add the custom stereotype(s) in this option), and the organisation or project owning extensions want to leave this option empty (to see their extensions printed).

This list is matched against the built-in, standard stereotypes according to modelling rules and any built-in stereotype (per model nature) from this list is removed (i.e., there is no overriding of built-in stereotype consideration for document generation). Empty list is valid and reflects default behaviour, i.e., everything found in the model according to other specified filtering options is printed. Not having this option or having it with an empty value preserves the default behaviour: to _not_ exclude anything based on stereotype only, i.e., to include everything not disabled by other options (e.g., informative or private model elements).

Important: The list given here ignores any built-in stereotype per nature in order to not modify the behaviour established and defined for built-in stereotypes. So, any built-in stereotype you specify here will simply be ignored. Examples of built-in stereotypes:

*   `enumeration` for classes; `informative`, `deprecated` for any UML element in model of any nature - you cannot modify built-in behaviour for these in any case (at present);
*   `Primitive`, `CIMDatatype` for CIM classes - you cannot modify built-in behaviour for these in _CIM models_, but these are custom if ever used in non-CIM models; and inversly,
*   `cond` for IEC61850 presence condition enumeration - you cannot modify built-in behaviour for these in _IEC61850 models_, but these are custom if ever used in non-IEC61850 models.

#### New configuration option `docgen.showNamespacePackages` (TODO - add to .ppt model.showNamespacePackages = Base, Dynamics, Part303, IEC61968, IEC62325, ExtEuBase)

According to CIM modelling guidelines, we can use tagged values `nsuri` and `nsprefix` to support namespace definition within the UML model itself, as well as for extensions and for generation of various implementation afterfacts. We typically specify these two tagged values at the top level (e.g., TC57CIM) and then everything below that package will have the same namespace, unless an element of UML model overwrites it. Almost every element of UML that supports tagged values can use this mechanism. So, from this release, you can control document generation inclusion or not of this information, by specifying for which UML packages to output in the MS Word document a line of text with the namespace information. Examples include Base and ExtEuBase (for 61970-301 main part and Annex A, respectively), Dynamics (for 61970-302), etc. If you don't specify any package name, or use an older config.properties file, no namespace information gets printed (default).

Note that this mechanism of namespace (the usual URI and optional prefix) is supported for any UML model nature, so in theory, for IEC61850 models as well.

However, IEC61850 has a special definition for document namespace and dedicated UML modelling for this. The IEC61850Namespace mechanism will be supported in the next jCleanCim release.

#### New configuration option `docgen.word.includeInheritancePath` (TODO - add to .ppt:docgen.word.includeInheritancePath = true)

This options allows to include the list of classes along the inheritance path for a class. Default is false, as well as when the option is missing from the configuration, to preserve the default behaviour. In the supplied configuration file we have enabled this option as it is rather useful and you can see with the test run how the result looks like and whether to keep it.

Important: jCleanCim does not support multiple inheritance, but it should not crash if it finds it in the model. The behaviour for multiple inheritance is undefined.

#### Support for custom styles (TODO - update .ppt)

_Disclaimer: The MS Word and its API are for certain features unpredictable, as are all the people who edited in the past 25 years the document you use as template. This is the best I can give at the moment. Please pay attention to the WARN or ERROR log entries (what you see on the screen and what gets saved in the ./log/jCleanCim.log file). And please, be careful about style names in MS Word (see [how MS Word can smart you out](https://word.tips.net/T007601_Style_Names_Can_Affect_Style_Definitions.html))._

You can now try using the following new configuration options:

*   `docgen.word.styles.prefix.toc` and `docgen.word.styles.prefix.head`: for TOC and heading styles prefix, and,
*   `docgen.word.styles.para`, `docgen.word.styles.fig`, `docgen.word.styles.tabhead`, `docgen.word.styles.tabcell`, `docgen.word.styles.figcapt` and `docgen.word.styles.tabcapt`: for styles names for paragraphs text, figure, table heading, table cell, figure caption and table captions,

to specify a comma-separated list of style names, in order of your preference. To accommodate for non-portable style handling accross some application locales (English vs. non-English installation of MS Word), we expect the user to configure preferred styles in order of preference, and the first one found in the _existing document_ will be used all allong. If none of configured styles exists in the document, the built-in one from the document will be used. jCleanCim never modifies your PC registry and/or global application templates (such as Normal.dot for MS Word), so if you are unhappy with the fall-back solution, please go ahead and add in the document generation application (such as MS Word) the desired styles as custom and rerun document generation. On that next run, the document should contain your desired style name(s).

#### MIBs generation (for IEC62351-7) - contribution by Gigi Pugni (TODO - add to .ppt)

This is the new feature developed by WG15 for their IEC62351-7. This jCleanCim release incorporates that new feature into the baseline with the code provided by Gigi Pugni, and it can be used by means of three new configuration options:

*   `mibgen.on`: set it to true to enable MIBs generation.
*   `mibgen.outDirFull`: directory where to save full MIBs (including complete description); default is `mibs`, under the ./output/.
*   `mibgen.outDirLight`: directory where to save light MIBs (UML elements without descriptions); default value is `mibslight`, under the ./output/.

MIBs generation can be enabled simultaneously with the MS Word document generation if you need both outputs.

Some small test model and template to exercise this function should be available in the next release. In the meantime, WG15 can use the feature now integrated into the baseline.

#### New configuration option `statistics.tagsToIgnore` (TODO - add to .ppt statistics.tagsToIgnore = GUIDBasedOn)

This option allows you to add which tags to NOT export into statistics log (to shorten log). You may want to use this in case you discover that the custom tool / add-on that you use heavily marks your model with tagged values and you don't want to end up with (tens of) thousands of lines of log listing every single item in your model. In the supplied configuration file we have already added one such tagged value, known to be present in CIM profiles created and maintained with [CimConteXtor](https://www.cimcontextor.net): `GUIDBasedOn` .

#### Credits

Gigi Pugni has contributed java code package with fully functional generation of MIB syntax, based on the modelling as used to support IEC62351-7. He also provided documentation describing in detail how WG15 does modelling and generates MIBs.

Laurent Guise has provided a prototype that inspired the implementation of most of IEC61850-specific features in this release. Thanks for beeing the sparring partner for non-English MS Word tests and discussions :-).

### Performance changes in release 02v03

None. Note however that newer versions of Enterprise Architect seem to always be somewhat slower than the older ones (we noticed the slow down in diagram generation for factor 3-4).

### Deprecated API

*   Method `UmlAssociation.getDirection()`: use UmlAssociation.getNavigable() instead.

### Potential backwards compatibility breaking changes for the application user

*   If you have used jCleanCim previously to generate documentation from UML profiles, both association ends used to be printed without distinction on navigability, which was semantically wrong. This has been fixed in this release, which means that your previously generated profile documents will have only one half of association ends: those that are navigable only. This makes the documents shorter and profiles more precise.

### Potential forwards incompatibility for the application user: old jCleanCim with new EA

*   \-

### Other user-visible changes

*   model.build logging: Unclassified classes, associations and attributes: log as ERROR, not as WARN or INFO
*   improved logging and logging level (less chatty for happy path)
*   docgen.writer - added FIGURE to preferred styles
*   CIM-specific:
    *   model, validation: added new dependency stereotypes as built-in: IsBasedOn and import.
    *   docgen.collector: added basic support for printing additional paragraph after class that has constraints, starting with "Constraint" and listing constraints as \[name\]:\[new line\]\[description\].
    *   docgen.collector: added support for seggregating navigability of association ends for CIM profiles and printing only the navigable end (as has always been done with [CIMTool](http://cimtool.org), used to generate profiles up to now)
*   IEC61850-specific:
    *   docgen.collector, docgen.writer.xml, IECDomain.xsd schema: Added support for two underlying enumeration types for ENC CDC. Note that this breaks the underlying meta-model by a) assigning two DA types to one ENC, and b) by creating totally unrelated second DA type (this has been done on request by IEC61850 model managers and applies to IEC61850 model only).
    *   docgen.collector: Added support for enumeration inheritance. Note that this is against the semantic of enumerated types (this has been done on request by IEC61850 model managers and applies to IEC61850 model only).
*   Other

*   \-

### Validation rules:

*   util (validation): -
*   New common / general validation rules:
    *   \-
*   New CIM-specific rules:
    *   \-
*   New IEC61850-specific rules:
    *   \-

### Bug fixes:

*   docgen.writer.word: Now also figure reference when below figure gets inserted as field (can be updated).
*   model: Corrected kind assignment to IEC61850 classes through stereotypes, so as to ignore stereotypes `informative` and `deprecated`, as these are applicable to any UML model element (thanks to Laurent Guise for reporting).
*   Fixed several potential NPEs (thanks to Jacob Dall and Laurent Guise for reporting) and added some tests:
    *   model: In two methods on package containment.
    *   docgen.collector.impl: Returning empty list instead of null.
    *   docgen.collector.impl: When IEC61850 enumerated type inherits literals.
    *   statistics: When due to missing config61850.properties file there is mismatch between model class kind and nature.
*   model: IEC61850 unclassified classes now logged as ERROR, not as INFO.
*   validation: ensured that Iec61850ClassesWithInvalidConstraints and Iec61850LNClassesWithSuperfluousConstraints actually do checks only on classes with IEC61850 nature.
*   docgen.word: now rethrowing exception when a post-processor provided (from tests; in real life it is important to finish smoothly, for tests we want tests to fail on exception).

### Known issues / limitations:

*   nothing new.

### Implementation and packaging:

*   added new tests.
*   implemented manual insert caption for figures with hope it would be faster, but it is not.
*   upgraded libraries: Sparx Enterprise Architect 14 (jar and dll).
*   TODO: include in build the new doc, input and output files (mibs).
*   all sources: updated copyright year.

### Documentation:

*   TODO update the presentation.

* * *

Built on 2019-12-20T22:05:24

[Copyright](copyright.md) [License](license.txt)

[Feedback](mailto:tatjana-dot-kostic-atNoSpam-ieee-dot-org)

[![Valid XHTML 1.0 Strict](http://www.w3.org/Icons/valid-xhtml10)](http://validator.w3.org/check?uri=referer)
