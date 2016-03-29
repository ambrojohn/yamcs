@echo off

REM # -----------------------------------------------------------------------------
REM #  Set CLASSPATH and Java options
REM # -----------------------------------------------------------------------------

REM Regardless from where the script has been launched,
REM run setclasspath.sh, which in the same directory
set PRGDIR=%~dp0
set CURRENT_DIR=%cd%

REM First clear out the user classpath
set CLASSPATH=

REM Add all necessary jars
setlocal EnableDelayedExpansion
cd %PRGDIR%..\lib
for /f %%i in ('dir /b /s *.jar') do (
	set x=%%i
	set CLASSPATH=!CLASSPATH!;!x!
)
REM Remove first ";" from the CLASSPATH string
set CLASSPATH=%CLASSPATH:~1%

set FOLDER_ETC=%PRGDIR%..\lib\ext
if exist %FOLDER_ETC% (
	for /f %%i in ('dir /b /s *.jar') do (
		set x=%%i
		set CLASSPATH=!CLASSPATH!;!x!
	)
)

set CLASSPATH=%CLASSPATH%;%PRGDIR%..;%PRGDIR%..\lib

cd %CURRENT_DIR%

endlocal && set CLASSPATH=%CLASSPATH%


REM # Set standard command for invoking Java.
set _RUNJAVA=java
