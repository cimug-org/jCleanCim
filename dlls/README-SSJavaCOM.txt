The particular versions of SSJavaCOM.dll and SSJavaCOM64.dll hosted in the dlls folder is the one delivered in the 
32-bit release of EA17.1

This choice is based on the fact that the 32-bit release is able to directly read both .eap(x) and .qea(x) project files 
whereas the 64-bit releases of EA16.x and EA17.x must convert .eap(x) files to .qea(x) files before reading and working 
with the earlier .eap(x) format.
