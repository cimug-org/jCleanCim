# jCleanCim Overview

jCleanCim is an open source tool for validation and documentation generation from Enterprise Architect CIM and IEC61850 UML models.

Quick intro to jCleanCim
========================

**Note: The content below is hard to keep up to date. Please refer to the jCleanCim presentation, available in the documentation directory.**

This is jCleanCim open source application, initially developed to perform validation of CIM EA model file, then extended to do clean-up of left-overs from Rose and to show some basic statistics (thus addressing combined CIM issue #1103). Finally, it has been extended to allow for IEC (and custom) document generation, mainly driven by special needs of generating IEC 61850-7-3 and IEC 61850-7-4 from UML model developed by ABB and handed over to IEC TC57 WG19 in October 2009. On the fly, the support for CIM-based IEC documents (IEC 61970-301 and IEC 61968-11) has been added - after all, this was an easy part :-).

Since February 2010, jCleanCim has been used by CIM editors of IEC TC57 WG13, WG14 and recently WG16 to generate IEC 61970-301, IEC 61968-11 and IEC 62325-301 documents, respectively, as well as for the documentation describing CIM extensions developed in the European FP7 project [ADDRESS](http://www.addressfp7.org/).

Since September 2010, it has also been used by 61850 UML taskforce of WG10 to validate IEC 61850 UML and to automatically generate IEC 61850-7-4 and IEC 61850-7-3. As other IEC working groups start moving their IEC 61850-based specifications to UML as master model, jCleanCim will allow for automatic document generation for these, as well. Furthermore, since 01v06, the tool supports serialisation of the UML model of IEC 61850 to an XML format, that would be suitable for publishing on the web, and in support of the WebAccess taskforce of WG10. As a by-product, the same feature is available for CIM UML model.

NOTE: From this point on, the documentation is likely obsolete; we try to maintain up to date the jCleanCim presentation, so have a look there.

Main functions
--------------

jCleanCim has four main functions:

1.  validation of a UML model provided in an .eap file, bulk or per IEC TC57 WG,
2.  calculation and printing of statistics of the UML model,
3.  generation of MS Word documentation from the UML model,
4.  generation of XML documentation from the UML model.

In addition to support for the standard IEC TC57 CIM and custom CIM extensions compliant with CIM desing rules, jCleanCim supports the same functionality for the UML model of IEC 61850 being developed by the 61850 UML taskforce. Having both families of standards in UML is expected to facilitate one day harmonisation efforts between CIM and 61850, lead by IEC TC57 WG19. Since the IEC 61850 _UML model_ is still not a standard, in the following we refer to CIM only, although the descriptions apply to the UML model of IEC 61850 as well.

Intended users
--------------

(TODO: needs update)

Intended users are primarily those who edit CIM UML and publish its documentation, thus:

*   official IEC CIM model editors, responsible for maintaining the CIM information model (UML) and for generating official IEC documents, and,
*   those who define custom non-standard CIM extensions who want to ensure they have followed standard CIM rules and who want to generate documentation for those extensions.

If you are already a user of the excellent [CIMTool](https://github.com/cimug-org/CIMTool), you may wonder where within the process of CIM development and maintenance the jCleanCim fits. The answer is:

1.  You would first use jCleanCim to validate correctness of the CIM information model (UML), and if required, to generate the information model documentation in MS Word format, as required by the IEC process.
2.  You would then use CIMTool to create CIM profiles (XSD, RDF, OWL) and their documentation (HTML) from the imported CIM UML model, and to validate instance files created based on those profiles.

Available distributions
-----------------------

See [readme]({@docRoot}/../../README.md) file.

Prerequisites
-------------

See [readme]({@docRoot}/../../README.md) file.

Installation
------------

See [readme]({@docRoot}/../../README.md) file.

### Sample files

Project's [input]({@docRoot}/../../input) directory contains a very small model file (you need at least one model file to run jCleanCim at all), and a sample template file (required for doc generation only).

base-small.eap is a tiny subset of IEC 61970 UML plus home-made dumb extensions, with intentionally-introduced several modelling bugs and constructs never supposed to be found in CIM - this is for testing purposes. You can use base-small-template.doc and tailor it for your needs to produce the MS Word documentation.

Tailoring the template for your needs means modifying, adding or removing different kinds of {@link org.tanjakostic.jcleancim.docgen.writer.Placeholder}s.

If you want to run jCleanCim with your own models (and potentially template files), you need to copy them to the [input]({@docRoot}/../../input) directory.

This directory also contains a blank image file that must be available when running jCleanCim for certain scenarios (e.g., if you don't generate documentation, but only run validation, this file will be used in place of real UML diagrams).

Running jCleanCim out of the box
--------------------------------

### Binary distribution

After you have unzipped the **eclipse-independent binary** distribution (jCleanCim-\[version\]-bin.zip), you will be using the run script `run.bat` from console.

1.  In the Windows Start menu, select "Run..." and type `cmd`. This will open a console window.
2.  In the console window, type `cd` followed by a space. From the file explorer, just drag and drop the directory where you have unzipped the binary distribution onto the console window (so you don't have to type the whole path), then press enter. This will change the directory to where your jCleanCim has been installed.
3.  In the console window, type `run` and press enter. This will run the jCleanCim jar with default configuration (validation and statistics) and the provided example files.

### Source distribution

After you have unzipped the **eclipse-independent source** distribution (jCleanCim-\[version\]-src.zip) and installed [Apache ant](http://ant.apache.org/bindownload.cgi), you will be using the Apache ant script `build.xml` from console.

1.  In the Windows Start menu, select "Run..." and type `cmd`. This will open a console window.
2.  In the console window, type `cd` followed by a space. From the file explorer, just drag and drop the directory where you have unzipped the source distribution onto the console window (so you don't have to type the whole path), then press enter. This will change the directory to where your jCleanCim has been installed.
3.  In the console window, type `ant jCleanCim` and press enter. This will build the jCleanCim jar from sources and run it with the default configuration (validation and statistics) and the provided example files. To see all available ant targets contained in the [ant build file]({@docRoot}/../../build.xml), type `ant -p` (or `ant -projecthelp`) and have a look at the [graph of targets and their dependencies]({@docRoot}/../../build-graph.jpg).

Alternatively, you may want to unzip the directory and import the project into your eclipse installation. _Note: If you already have an earlier version of jCleanCim in your eclipse workspace, you will first have to rename old jCleanCim project before importing new one. For example, if you want to import new version 01v04, rename first your existing jCleanCim project to jCleanCim-01v03, then import the new one. If you want to keep the old project jCleanCim-01v03 and use it, you will also have to update the build path for the directory where dll-s reside, otherwise eclipse shows you classpath error (eclipse does not use relative paths for dlls directories!)._

After you have imported the **eclipse existing project** from unzipped source distribution (jCleanCim-\[version\]-src.zip), you should create the default eclipse launch configuration as follows:

*   Navigate to `src` directory
*   locate the source file {@link org.tanjakostic.jcleancim.JCleanCim org.tanjakostic.jcleancim.JCleanCim}
*   right click on the class and select "Run as / Java Application".

This will launch the application and also create the run configuration, that you can later on copy to create custom configurations. Cached launch configurations are available from the eclipse "Run" icon (green icon with white arrow).

Note that you can also open the [ant build file]({@docRoot}/../../build.xml) from within eclipse (Window / Show Views / ant) and run any of its tasks from the eclipse Outline window, the same way as from the console.

Configuring jCleanCim
---------------------

### Application configuration

To configure any run of jCleanCim application, you use the standard Java properties file available in the project's config directory. Default name for that properties file is [config.properties]({@docRoot}/../../config/config.properties) and you can override this default with a command line argument if you want to use different stable configurations for different jCleanCim runs - see documentation in the application class {@link org.tanjakostic.jcleancim.JCleanCim} and the configuration class {@link org.tanjakostic.jcleancim.common.Config}.

The supplied default properties file contains reasonable defaults, and several tested configurations are commented. By default (out of the box), jCleanCim will run validation and statistics on base-small.eap model file, and will _not_ generate any documentation.

### Logging configuration

Project's config directory contains also the logging configuration file [log4j.xml]({@docRoot}/../../config/log4j.xml). We have set up the console output level to INFO (within the element `appender name="CONSOLE"...` ), and the level for everything else to the most verbose, TRACE (within the element `logger name="org.tanjakostic.jcleancim"` ). The first time you run jCleanCim application, the project's [log]({@docRoot}/../../log) directory gets created automatically.

If you get too noisy log files, you can dicrease the jCleanCim logger level in the [log4j.xml]({@docRoot}/../../config/log4j.xml) file, from TRACE to DEBUG. If you want to post the jCleanCim log file with a model release, you can either:

*   Copy the console output to a jCleanCim-\[version\]-\[UMLpackageVersions\].log file - this is easy when running jCleanCim from within eclipse, but may be impossible if running from console window.
*   First dicrease the log level for the jCleanCim logger in [log4j.xml]({@docRoot}/../../config/log4j.xml) file, from TRACE to INFO, then remove old log files, run jCleanCim and save the produced jCleanCim.log file as jCleanCim-\[version\]-\[UMLpackageVersions\].log; then revert the log level change in the [log4j.xml]({@docRoot}/../../config/log4j.xml) file.

Typical usage patterns
----------------------

### UML model validation and statistics

To select what functions of jCleanCim to run, you need to set one or more of the main properties in the [config/config.properties]({@docRoot}/../../config/config.properties) file: `validation.on` , `statistics.on` and `docgen.on` . In every case, you have to provide a valid EA model file name to work with, in the property `model.filename` . That file is expected to be on the jCleanCim classpath, so the best is to put the file in the [input]({@docRoot}/../../input) directory which is already set to be on the classpath.

Typical usage will be to first enable validation and statistics mode after you have edited the model, then address the problems in the model, and revalidate before releasing. Here is an example of a minimum config.properties file to do that:

model.filename    = base-small.eap
validation.on     = true
statistics.on     = true

validation.scope  =

If you are validating IEC 61850 UML models, there are several other properties; see documentation in {@link org.tanjakostic.jcleancim.common.Config}.

If you have a big model, with potentially parts that are informative/buggy, you may want to set a filter and perform initial validation of your changes for only some top-level packages. For instance, to validate only standard CIM packages IEC61970 and IEC61968, you would set the `validation.scope` property so:

validation.scope = WG13, WG14

and to validate only custom (non-IEC) extensions:

validation.scope = OTHER\_CIM, OTHER\_IEC61850

It is recommended to _validate the full content of the EA model (by leaving the value of `validation.scope` property empty) at least before releasing the model_ , to ensure there are no cross-package issues. See classes in the package {@link org.tanjakostic.jcleancim.validation} for available validators and rules they fire - the names of classes should be descriptive enough.

### MS Word documentation generation

If you want to generate IEC (or custom) MS Word documentation from the UML model, in addition to the model file name in [config/config.properties]({@docRoot}/../../config/config.properties) you must provide the names for template (input) file, the resulting (output) file, and enable document generation by setting the property `docgen.on = true` .

The template file is a regular MS Word document (_not_ Word template with .dot extension), in which you put placeholders to control what jCleanCim should pick from the UML model and print into MS Word document. Detailed description of available placeholders and their usage is provided in the {@link org.tanjakostic.jcleancim.docgen.writer.Placeholder} class, and the templates distributed with jCleanCim in the project's [input]({@docRoot}/../../input) directory should serve you as examples (of what is correct and what is not). If using your own template, you should put it into that directory before running jCleanCim for document generation.

When generating documentation, jCleanCim does the following:

*   copies your template file into the projects `output` directory, created automatically the first time you run the document generation,
*   renames the copied file to the name given in the properties file, and
*   fills that copy with the contents from the EA model in place of the placeholders.

You can safely run document generation several times with the same name of the output file, without overwriting existing output files - if the output file exists, jCleanCim will rename it by appending a system nanosecond time. The disadvantage is that you will need to delete those discarded files from the `output` directory from time to time, but at least nothing gets lost without your control.

You may want to disable validation and statistics when enabling document generation to have the console log focused on document generation only.

Because document generation takes pretty long, you will first want to ensure that the placeholders in your template are correct, without generating the full package content. Here the minimum configuration to do this for a CIM model (IEC61850 model needs more properties; see [config/config.properties]({@docRoot}/../../config/config.properties) file):

model.filename = base-small.eap

docgen.on = true
profiles.docgen.on =
docgen.inTemplate = base-small-template.doc
docgen.outDocument = base-small.doc

docgen.analysePlaceholders = true

Running only placeholder analysis ( `docgen.analysePlaceholders=true` ) will still produce the output document, but without UML package contents (classes, attributes, etc.). More importantly, that half-baked output document will contain placeholder errors, if any - do text search for string "$ERROR".

After you have fixed the placeholders in the template, you can reset `docgen.analysePlaceholders` to empty string to generate the full documentation.

There are further options documented in {@link org.tanjakostic.jcleancim.common.Config} and in properties file [config/config.properties]({@docRoot}/../../config/config.properties). Playing with the provided sample small model and template files will hopefully get you started.

#### Other considerations

When generating official IEC documentation, the template should contain the IEC styles (this is probably already the case with CDV or FDIS documents that you as editor already have). To prevent MS Word exceptions when generating non-IEC documentation for extensions, jCleanCim defines default MS Word styles as replacement for the IEC styles. So, for example, 'Caption' is used if 'FIGURE-title' and 'TABLE-title' are not present in the template, or 'Normal' is used if 'PARAGRAPH' is not present. Below is the code snippet of the static initialiser for {@link org.tanjakostic.jcleancim.docgen.writer.Style} for the full list of default mappings: first argument is IEC style name and the second is the MS Word default style:

  para("PARAGRAPH", "Normal"),
  fig("Picture", "Normal"),
  figcapt("FIGURE-title", "Caption"),
  tabcapt("TABLE-title", "Caption"),
  tabhead("TABLE-col-heading", "Normal"),
  tabcell("TABLE-cell", "Normal"),
  h1("Heading 1", "Heading 1"),
  h2("Heading 2", "Heading 2"),
  h3("Heading 3", "Heading 3"),
  h4("Heading 4", "Heading 4"),
  h5("Heading 5", "Heading 5"),
  h6("Heading 6", "Heading 6"),
  h7("Heading 7", "Heading 7"),
  h8("Heading 8", "Heading 8"),
  h9("Heading 9", "Heading 9");

**It is essential to use correct styles for paragraphs containing figure and table captions in the template,** because jCleanCim must deduce the number of figures and tables already existing in the template to calculate on the fly the correct numbering for new figures and tables (when inserting/appending the documentation for the UML model elements and diagrams). If jCleanCim throws an exception during document generation, it is very likely that the MS Word threw exception due to wrong/inexisting/negative number for the figure or table caption. _Note: We cannot check those numbers from within the code, because the MS Word automation API does not provide reliable access to them. In the worst case, when we catch an exception from MS Word, we gracefully close the MS Word document and exit the MS Word application, before exiting jCleanCim._

Document generation may take pretty long, depending on how many classes the UML model has. The reason is that MS Word updates its fields evey time there is a table or any numbered paragraph (heading or figure/table caption) added to the document. To make that time somewhat shorter, consider the following when editing the template:

*   disable automatic spell checking in the styles 'PARAGRAPH', 'TABLE-cell' and 'Normal'
*   disable overall change tracking

Since version 01v03, jCleanCim has the MS Word application run in background by default (which is faster than having the window visible and updating all the time).

#### Known issues

Doc generation obviously relies on MS Word automation API, accessed from within Java through Java-COM bridge ([Jacob](http://sourceforge.net/projects/jacob-project/files/)). With certain MS Word files (used as jCleanCim template), we encounter from time to time issues when invoking COM objects for unknown reason, and with undetermined patterns (= an absolute horror for a programmer!). Therefore, the Java implementation of the writer catches those COM exceptions, prints the stack trace and attempts to continue, so you get at least some of the desired output.

Here some known issues related to MS Word automation API:

*   When you print a relatively large part of the UML model, you may get the Word pop-up window "memory insufficient. Do you want to continue?" several times. To prevent this, jCleanCim regularly invokes the COM method (`UndoClear`) in attempt to clear cache of the running Word instance, but this call sometimes fails for an unknown reason. Disabling change tracking, and spell checking in styles 'PARAGRAPH', 'TABLE-cell' and 'Normal' in the template document may help here.
*   In most cases, despite the above COM exceptions (and those that follow), your generated Word document will be complete, even if it stays open. Just save it and see what is in it. In cases it does not work, try to create a fresh Word document as a jCleanCim template, and copy only necessary styles from the original template, disable change tracking and spell checking.
*   We also suspect issues with localised versions of MS Word related to style definition. Michael Specht (OFFIS, Germany) reported COM exceptions with base-small-template.doc bundled in the [input]({@docRoot}/../../input) with JCleanCim-01v01. He also reported that installing English language pack for MS Office solves the problem.

If the problems persist, feel free to help fixing jCleanCim/Jacob/MS Word stuff.

If despite that we cannot find a workaround, we should convince IEC to allow us to generate our documents at least as HTML (or still better, as XML), to get rid of binary dependencies on MS Word.

#### Success stories

All that said, we have sucessfully generated:

*   IEC 61970-301 since Ed.4 (base CIM14)
*   IEC 61968-11 since Ed.1 (DCIM10), and
*   IEC 62325-301 since CDV (market CIM01), and various EU profile documents

We have also demonstrated the generation of Ed.2 IEC 61850-7-4 and IEC 61850-7-3, but these auto-generated documents from UML are not the official IEC documents yet.

The requirements for generation of the above documents (and what will be needed for IEC 61850-7-2 and other IEC CIM and 61850 family of standards) are the highest priority ones at this moment.

Checklist for the developer that produces jCleanCim distributions
-----------------------------------------------------------------

Once after you've fixed bugs or added new features to jCleanCim, follow these steps to build and publish the three jCleanCim distributions:

1.  update release notes (including date and version) in the [readme]({@docRoot}/../../readme.html) file.
2.  in the [version properties file]({@docRoot}/../../config/build.properties), update the property `project.version`.
3.  if you provide new libraries or upgraded versions, ensure you update appropriate ant properties.
4.  clean-up local config.properties file (keep only public defaults).
5.  run `ant clean, unzip-all` and verify that the content unzipped under the build directory is ok.
6.  run jCleanCim from within both unzipped directories (to verify it actually runs as described)
7.  (copy locally the content of dist directory into jCleanCim releases directory)
8.  on CIMug SharePoint, create new sub-directory within jCleanCim directory under [CIM Methods & Tools for Enterprise Integration group Shared Documents](http://cimug.ucaiug.org/MTEI/Shared Documents); call it `jCleanCim-[version]`
9.  upload into that new sub-directory all artefacts from dist directory
10.  notify CIM model managers, IEC61850 UML task force and known users
