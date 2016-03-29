@echo off

REM NOTE: If you run this script and encounter the following error:
REM "You do not have sufficient privilege to perform this operation."
REM The open the Command Prompt (cmd.exe) with administrator privilege, i.e. right-click + "Run as administrator"

REM Creates a runnable environment that allow to run yamcs from the command line using links to the development tree
REM the configuration files and the runnable shell files from the bin directory are not overwritten

REM The script has to be run each time the version number is changed, and has to run when everything is properly compiled from the command line



REM Set this to where you want the live "installation" to be performed
set TARGET=%cd%\live
REM Allow overriding default from the command line
if not %1.==. SET TARGET=%1

set YAMCS_HOME=%cd%

set FOLDER_ETC=%TARGET%\etc
set FOLDER_BIN=%TARGET%\bin
set FOLDER_MDB=%TARGET%\mdb
set FOLDER_WEB=%TARGET%\web
if not exist %FOLDER_ETC% md %FOLDER_ETC%
if not exist %FOLDER_BIN% md %FOLDER_BIN%
if not exist %FOLDER_MDB% md %FOLDER_MDB%
if not exist %FOLDER_WEB% md %FOLDER_WEB%

rm -rf %TARGET%/lib

set FOLDER_EXT=%TARGET%\lib\ext
if not exist %FOLDER_EXT% md %FOLDER_EXT%

cp -R %YAMCS_HOME%/yamcs-core/bin %TARGET%

REM Create symbolic links
cd %YAMCS_HOME%/yamcs-core/target
for /f %%i in ('dir *.jar /b') do (
	mklink "%TARGET%/lib/%%i" "%YAMCS_HOME%/yamcs-core/target/%%i"
)
cd %YAMCS_HOME%

cd %YAMCS_HOME%/yamcs-core/lib
for /f %%i in ('dir *.jar /b') do (
	mklink "%TARGET%/lib/%%i" "%YAMCS_HOME%/yamcs-core/lib/%%i"
)
cd %YAMCS_HOME%

cd %YAMCS_HOME%/yamcs-core/mdb
for /f %%i in ('dir *.* /b') do (
	mklink "%TARGET%/mdb/%%i" "%YAMCS_HOME%/yamcs-core/mdb/%%i"
)
cd %YAMCS_HOME%

REM cd %YAMCS_HOME%/yamcs-web/build
REM for /f %%i in ('dir *.* /b') do (
	REM mklink "%TARGET%/web/base/%%i" "%YAMCS_HOME%/yamcs-web/build/%%i"
REM )
REM cd %YAMCS_HOME%


REM if exist make-live-devel-local.bat (
	REM call make-live-devel-local.bat
REM ) else (
	cp -R %YAMCS_HOME%/yamcs-simulation/etc %TARGET%
	cp -R %YAMCS_HOME%/yamcs-simulation/bin %TARGET%
	
	cd %YAMCS_HOME%/yamcs-simulation/target
	for /f %%i in ('dir *.jar /b') do (
		mklink "%TARGET%/lib/%%i" "%YAMCS_HOME%/yamcs-simulation/target/%%i"
	)
	cd %YAMCS_HOME%
	
	cd %YAMCS_HOME%/yamcs-simulation/mdb
	for /f %%i in ('dir *.* /b') do (
		mklink "%TARGET%/mdb/%%i" "%YAMCS_HOME%/yamcs-simulation/mdb/%%i"
	)
	cd %YAMCS_HOME%
	
	mklink "%TARGET%/web/yss" "%YAMCS_HOME%/yamcs-simulation/web"
	
	cp -R %YAMCS_HOME%/yamcs-simulation/bin/simulator.bat %TARGET%/bin
	mklink "%TARGET%/test_data" "%YAMCS_HOME%/yamcs-simulation/test_data"
	set YAMCS_DATA=%YAMCS_HOME%\storage\simulator\profiles
	if not exist %YAMCS_DATA% md %YAMCS_DATA%
	cp -R %YAMCS_HOME%/yamcs-simulation/profiles/*.* %YAMCS_DATA%
REM )

REM Add sample config (if not already present)
setlocal EnableDelayedExpansion
cd %YAMCS_HOME%/yamcs-core/etc
for /f %%i in ('dir /b') do (
	set x=%%i
	set x=!x:.sample=!
	if not !x!==%%i (
		cp -R %%i %TARGET%/etc/!x!
	) else (
		cp -R %%i %TARGET%/etc/%%i
	)
)
cd %YAMCS_HOME%

