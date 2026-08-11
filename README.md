# jCleanCim

![jCleanCim](readme-icons/image-header-1.png)

**Documentation: [jcleancim.ucaiug.io](https://jcleancim.ucaiug.io/)**

**jCleanCim** is an open source tool for validation and documentation generation from
[Enterprise Architect](https://sparxsystems.com/products/ea) UML models of IEC TC57 CIM
and IEC 61850. It is a non-GUI Java application; the Java code is fully platform
independent, but it must be run on MS Windows because of the Enterprise Architect and
MS Word automation libraries (.dlls) it depends on.

## Download

The current release is **2.4.0**, published on the
[Releases](https://github.com/cimug-org/jCleanCim/releases) page as a ZIP file. Two
distributions are available:

| Distribution | Archive | For |
|---|---|---|
| Binary | `jCleanCim-[version]-bin.zip` | Running jCleanCim from the console |
| Source | `jCleanCim-[version]-src.zip` | Developing and building jCleanCim, with Apache Ant or Eclipse |

There is no separate 32-bit or 64-bit download — bitness is a property of the Java
installation you use, and is determined by your Enterprise Architect project file
format.

## Requirements

- Java 17 or later — a JRE for the binary distribution, a JDK for the source
  distribution. A 64-bit installation is recommended: it works with both `.eap(x)` and
  `.qea(x)` project files.
- Enterprise Architect, at the version matching your project file format.
- MS Word (optional, English installation) — required only for document generation.

Full prerequisites, the project-file-to-Java compatibility matrix, installation steps,
bundled dependencies and configuration reference are on the
[documentation site](https://jcleancim.ucaiug.io/).

## Documentation

- [Full documentation](https://jcleancim.ucaiug.io/)
- [Release notes](https://jcleancim.ucaiug.io/ReleaseNotes.html)
- [Introduction to jCleanCim](https://jcleancim.ucaiug.io/doc/jCleanCimIntro.pdf) (presentation)
- Each distribution also ships its own Javadoc, slides and test report under `doc/`.

## Support

Please read the performance notes and troubleshooting hints on the
[documentation site](https://jcleancim.ucaiug.io/) before reporting a problem — several
of the most commonly reported issues are configuration or MS Word environment issues
covered there. Questions and bug reports go to
[Issues](https://github.com/cimug-org/jCleanCim/issues);
[Discussions](https://github.com/cimug-org/jCleanCim/discussions) is the place for
general conversation.

## History and credits

jCleanCim was created by Tatjana (Tanja) Kostic. Until the end of 2015 it was hosted by
the CIM Methods & Tools for Enterprise Integration group on the CIM Users Group web
site, with access limited to CIM and IEC 61850 community members. In 2016 it was
transitioned to a fully open source tool and hosted on Tanja's own web space. In
November 2022, with Tanja Kostic's approval, it was migrated here as part of the CIMug's
Open Source Initiatives effort.

## License

Since release 2.0.0, jCleanCim has been licensed under the GNU
[LGPLv3](license.txt). See [copyright](copyright.md) for the copyright notice, which is
also included in every distribution and in every Java source file.
