@echo off
rem $Id: run.bat 2727 2015-05-14 19:21:55Z CHTAKOS $

rem On Unix, assuming libraries are in "unixLibs", it would be -D-Djava.library.path="unixLibs:LD_LIBRARY_PATH"

rem Even if running a 64-bit OS, you'll need 32-bit java because EA is still a 32-bit application.
rem So, install a 32-bit Java and uncomment the following line and potentially adapt the path to
rem the actual place where you installed the 32-bit Java (thanx to Pat Brown for the fix):
rem set PATH=C:\Program Files (x86)\Java\Jre7\bin;%PATH%

java -Djava.library.path="dlls" -cp ".;jCleanCim.jar;lib/*;config;input" org.tanjakostic.jcleancim.JCleanCim %1 %2 %3 %4

rem If you want to specify command line arguments, append them so:
rem java -Djava.library.path="dlls;%PATH%" -cp ".;jCleanCim.jar;lib/*;config;input" org.tanjakostic.jcleancim.JCleanCim -modelFile myModel.eap -propFile myConfig.properties

rem If you want to use java -jar <>
rem java -Djava.library.path="dlls;%PATH%" -Djava.ext.dirs=".;lib;config;input" -jar jCleanCim.jar %1 %2 %3 %4

rem NOTE: In Windows 7, for some reason there is a problem when using PATH variable explicitly:
rem:      -Djava.library.path="dlls;%PATH%"
rem:      Removing it solves the problem.
