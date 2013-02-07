@echo off

set TOP=%~dp0..
set LIB=%TOP%\lib
set MAIN=SOAPClient

for /R %LIB% %%a in (*.jar) do call :AddToPath %%a

set CLASSPATH=%CLASSPATH%;%LIB%\classes

java -classpath %CLASSPATH% %MAIN% %*

goto Done

:AddToPath
set CLASSPATH=%1;%CLASSPATH%
goto :EOF

:Done
