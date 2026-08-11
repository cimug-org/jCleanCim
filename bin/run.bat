@echo off
rem On Unix, assuming libraries are in "unixLibs", it would be -D-Djava.library.path="unixLibs:LD_LIBRARY_PATH"

rem jCleanCim 2.4.0 requires Java 17 or later. A 64-bit Java is recommended, because it
rem reads both EA project file formats. Which bitness you need depends on that format:
rem   .eap / .eapx  (EA 15.x and earlier)  - 32-bit or 64-bit Java 17+
rem   .qea / .qeax  (EA 16.0 and later)    - 64-bit Java 17+ only
rem If you must run a 32-bit Java for the legacy .eap(x) path, install it, then uncomment
rem the line below and adapt the path to your installation (thanx to Pat Brown for the fix):
rem set PATH=C:\Program Files (x86)\Java\jdk-17\bin;%PATH%

java -Djava.library.path="dlls" -cp ".;jCleanCim.jar;lib/*;config;input" org.tanjakostic.jcleancim.JCleanCim %*

rem If you want to specify command line arguments, append them so:
rem java -Djava.library.path="dlls;%PATH%" -cp ".;jCleanCim.jar;lib/*;config;input" org.tanjakostic.jcleancim.JCleanCim -modelFile myModel.eap -propFile myConfig.properties

rem NOTE: In Windows 7, for some reason there is a problem when using PATH variable explicitly:
rem:      -Djava.library.path="dlls;%PATH%"
rem:      Removing it solves the problem.
