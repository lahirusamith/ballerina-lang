@echo off

REM ---------------------------------------------------------------------------
REM   Copyright (c) 2017, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
REM
REM   Licensed under the Apache License, Version 2.0 (the "License");
REM   you may not use this file except in compliance with the License.
REM   You may obtain a copy of the License at
REM
REM   http://www.apache.org/licenses/LICENSE-2.0
REM
REM   Unless required by applicable law or agreed to in writing, software
REM   distributed under the License is distributed on an "AS IS" BASIS,
REM   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
REM   See the License for the specific language governing permissions and
REM   limitations under the License.

rem ---------------------------------------------------------------------------
rem Main Script for Ballerina
rem
rem Environment Variable Prerequisites
rem
rem   BALLERINA_HOME  Home of BALLERINA installation. If not set I will  try
rem                   to figure it out.
rem
rem   JAVA_HOME       Must point at your Java Development Kit installation.
rem
rem   JAVA_OPTS       (Optional) Java runtime options used when the commands
rem                   is executed.
rem ---------------------------------------------------------------------------

rem ----- if JAVA_HOME is not set we're not happy ------------------------------

:checkJava

if "%JAVA_HOME%" == "" goto noJavaHome
if not exist "%JAVA_HOME%\bin\java.exe" goto noJavaHome
goto checkServer

:noJavaHome
echo "You must set the JAVA_HOME variable before running Ballerina."
goto end

rem ----- set BALLERINA_HOME ----------------------------
:checkServer
rem %~sdp0 is expanded pathname of the current script under NT with spaces in the path removed
set BALLERINA_HOME=%~sdp0..

goto updateClasspath

:noServerHome
echo BALLERINA_HOME is set incorrectly or BALLERINA could not be located. Please set BALLERINA_HOME.
goto end

rem ----- update classpath -----------------------------------------------------
:updateClasspath

setlocal EnableDelayedExpansion
set BALLERINA_CLASSPATH=
FOR %%C in ("%BALLERINA_HOME%\bre\lib\bootstrap\*.jar") DO set BALLERINA_CLASSPATH=!BALLERINA_CLASSPATH!;"%BALLERINA_HOME%\bre\lib\bootstrap\%%~nC%%~xC"

set BALLERINA_CLASSPATH="%JAVA_HOME%\lib\tools.jar";%BALLERINA_CLASSPATH%;

set BALLERINA_CLASSPATH=!BALLERINA_CLASSPATH!;"%BALLERINA_HOME%\bre\lib\*"

set BALLERINA_CLI_HEIGHT=
set BALLERINA_CLI_WIDTH=
for /F "tokens=2 delims=:" %%a in ('mode con') do for %%b in (%%a) do (
  if not defined BALLERINA_CLI_HEIGHT (
     set "BALLERINA_CLI_HEIGHT=%%b"
  ) else if not defined BALLERINA_CLI_WIDTH (
     set "BALLERINA_CLI_WIDTH=%%b"
  )
)
rem ----- Process the input command -------------------------------------------

rem Slurp the command line arguments. This loop allows for an unlimited number
rem of arguments (up to the command line limit, anyway).

:setupArgs
if ""%1""=="""" goto doneStart

if ""%1""==""java.debug""    goto commandDebug
if ""%1""==""-java.debug""   goto commandDebug
if ""%1""==""--java.debug""  goto commandDebug

shift
goto setupArgs


rem ----- commandDebug ---------------------------------------------------------
:commandDebug
shift
set DEBUG_PORT=%1
if "%DEBUG_PORT%"=="" goto noDebugPort
if not "%JAVA_OPTS%"=="" echo Warning !!!. User specified JAVA_OPTS will be ignored, once you give the --java.debug option.
set JAVA_OPTS=-Xdebug -Xnoagent -Djava.compiler=NONE -Xrunjdwp:transport=dt_socket,server=y,suspend=y,address=%DEBUG_PORT%
echo Please start the remote debugging client to continue...
goto runServer

:noDebugPort
echo Please specify the debug port after the --java.debug option
goto end

:doneStart
if "%OS%"=="Windows_NT" @setlocal
if "%OS%"=="WINNT" @setlocal
rem find the version of the jdk
:findJdk

set CMD=RUN %*

:checkJdk18
"%JAVA_HOME%\bin\java" -version 2>&1 | findstr /r "[1.8.|9.|10.]" >NUL
IF ERRORLEVEL 1 goto unknownJdk
"%JAVA_HOME%\bin\java" -version 2>&1 | findstr /r "[1.8.]" >NUL
IF ERRORLEVEL 0 goto jdk8AndHigher
rem In, JDK9 or above need to import 'java.corba' module
set JAVA_MODULES="--add-modules java.corba"
goto jdk8AndHigher

:unknownJdk
echo Ballerina is supported only on JDK 1.8 and above
goto end

:jdk8AndHigher
goto runServer


rem ----------------- Execute The Requested Command ----------------------------

:runServer

set CMD=%*

rem ---------- Add jars to classpath ----------------

set BALLERINA_CLASSPATH=.\bre\lib\bootstrap;%BALLERINA_CLASSPATH%

set CMD_LINE_ARGS=-Xbootclasspath/a:%BALLERINA_XBOOTCLASSPATH% -Xms256m -Xmx1024m -XX:+HeapDumpOnOutOfMemoryError -XX:HeapDumpPath="%BALLERINA_HOME%\heap-dump.hprof"  -Dcom.sun.management.jmxremote -classpath %BALLERINA_CLASSPATH% %JAVA_OPTS% -Dballerina.home="%BALLERINA_HOME%"  -Djava.command="%JAVA_HOME%\bin\java" -Djava.opts="%JAVA_OPTS%" -Denable.nonblocking=false -Dfile.encoding=UTF8 -Dballerina.version=${project.version} -Djava.util.logging.config.class="org.ballerinalang.logging.util.LogConfigReader" -Djava.util.logging.manager="org.ballerinalang.logging.BLogManager" %JAVA_MODULES%


:runJava
"%JAVA_HOME%\bin\java" %CMD_LINE_ARGS% org.ballerinalang.launcher.Main %CMD%
:end
goto endlocal

:endlocal

:END
