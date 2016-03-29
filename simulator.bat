@echo off

REM Regardless from where the script has been launched,
REM run setclasspath.sh, which in the same directory
set PRGDIR=%~dp0

REM Set classpath
call %PRGDIR%setclasspath.bat

REM Run the program
cd ..
call "%_RUNJAVA%" -classpath "%CLASSPATH%" org.yamcs.simulator.Main
