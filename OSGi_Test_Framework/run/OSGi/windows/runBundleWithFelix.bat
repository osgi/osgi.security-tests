setlocal enableDelayedExpansion
echo off
cls

set DIR=%CD%
set ROOT=%DIR%\..\..\..
cd %ROOT%
set ROOT=%CD%
set BUNDLES=tests\bundles
set OPTIONS=
set SYSATT187=tests\bundles\sysatt187

goto main

:usage
echo ------------------------------------------------------
echo  __   __   ___         ___       ___     __   __      
echo /  \ ^|__) ^|__  ^|\ ^|     ^|  ^|__^| ^|__     ^|__) /  \ \_/ 
echo \__/ ^|    ^|___ ^| \^|     ^|  ^|  ^| ^|___    ^|__) \__/ / \ 
echo. 
echo ------------------------------------------------------
echo                                       SOGETI HIGH TECH
echo                                 www.sogeti-hightech.fr
echo.
echo NAME
echo       Framework de tests orientés sécurité OSGi
echo.
echo USAGE
echo       runBundleWithFelix.bat [bundleNumber]
echo.
echo DESCRIPTION
echo       Ce script permet de lancer les tests unitaires 
echo       développé par Sogeti High Tech.
echo       Merci de vous référer au livrable \"L4.3.5.b :
echo       Exigence techniques sur le framework de tests d
echo       intrusion\" pour connaître le bundleNumber.
echo.
echo ERREUR
echo       %~1
goto:eof

:main
rem check if the framework is installed
if not exist felix call :usage "Le framework felix n'est pas installé. Merci d'exécuter le script installFelix.bat" & goto exiterror

rem check if the parameter is defined
if [%1]==[] call :usage "[bundleNumber] is missing" & goto exiterror

rem check if the length of the parameter equal 3
set #=%1
set length=0
:loop
if defined # (
    rem shorten string by one character
    set #=%#:~1%
    rem increment the string count variable %length%
    set /A length += 1
    rem repeat until string is null
    goto loop
)
if not %length% EQU 3 call :usage "%1 is not a correct [bundleNumber]" & goto exiterror

if %1 == 000 goto skip3
if %1 == 187 goto skip3
if %1 == 150 goto cond
if %1 == 171 goto cond
if %1 == 175 goto cond
if %1 == 205 goto cond
if %1 == 210 goto cond
goto skip
:cond
if not exist "%BUNDLES%\com.sogetiht.otb.sysatt%1.bundle2.jar" goto notFound
goto skip3
:skip
if %1 == 230 goto cond1
goto skip1
:cond1
if not exist "%BUNDLES%\com.sogetiht.otb.sysatt230.maliciousBundle.jar" goto notFound
goto skip3
:skip1
if %1 == 206 goto cond2
goto skip2
:cond2
if not exist "%BUNDLES%\com.sogetiht.otb.sysatt206.bundle.jar" goto notFound
goto skip3
:skip2
if not exist "%BUNDLES%\com.sogetiht.otb.sysatt%1*" goto notFound
goto skip3
:notFound
call :usage "Bundle SYS-ATT-%1 not found"
goto exiterror
:skip3
if %1 == 135 set "OPTIONS=file:..\%BUNDLES%\jna-4.0.0.jar file:..\%BUNDLES%\org.osgi.core-1.4.0.jar"
if %1 == 100
    set "OPTIONS=file:..\%BUNDLES%\bndlib-2.1.0.jar file:..\%BUNDLES%\ops4j-base-io-1.4.0.jar file:..\%BUNDLES%\ops4j-base-lang-1.4.0.jar file:..\%BUNDLES%\ops4j-base-monitors-1.4.0.jar file:..\%BUNDLES%\ops4j-base-store-1.4.0.jar file:..\%BUNDLES%\org.osgi.core-1.4.0.jar file:..\%BUNDLES%\org.osgi.core-4.2.0.jar file:..\%BUNDLES%\tinybundles-2.0.0.jar"
    set "FRAGMENTS=file:..\%BUNDLES%\slf4j-api-1.6.1.jar file:..\%BUNDLES%\slf4j-simple-1.6.1.jar"
if %1 == 165
    set "OPTIONS=file:..\%BUNDLES%\bndlib-2.1.0.jar file:..\%BUNDLES%\ops4j-base-io-1.4.0.jar file:..\%BUNDLES%\ops4j-base-lang-1.4.0.jar file:..\%BUNDLES%\ops4j-base-monitors-1.4.0.jar file:..\%BUNDLES%\ops4j-base-store-1.4.0.jar file:..\%BUNDLES%\org.osgi.core-1.4.0.jar file:..\%BUNDLES%\org.osgi.core-4.2.0.jar file:..\%BUNDLES%\tinybundles-2.0.0.jar"
    set "FRAGMENTS=file:..\%BUNDLES%\slf4j-api-1.6.1.jar file:..\%BUNDLES%\slf4j-simple-1.6.1.jar"
if %1 == 207
    set "OPTIONS=file:..\%BUNDLES%\bndlib-2.1.0.jar file:..\%BUNDLES%\ops4j-base-io-1.4.0.jar file:..\%BUNDLES%\ops4j-base-lang-1.4.0.jar file:..\%BUNDLES%\ops4j-base-monitors-1.4.0.jar file:..\%BUNDLES%\ops4j-base-store-1.4.0.jar file:..\%BUNDLES%\org.osgi.core-1.4.0.jar file:..\%BUNDLES%\org.osgi.core-4.2.0.jar file:..\%BUNDLES%\tinybundles-2.0.0.jar"
    set "FRAGMENTS=file:..\%BUNDLES%\slf4j-api-1.6.1.jar file:..\%BUNDLES%\slf4j-simple-1.6.1.jar"
if %1 == 208
    set "OPTIONS=file:..\%BUNDLES%\bndlib-2.1.0.jar file:..\%BUNDLES%\ops4j-base-io-1.4.0.jar file:..\%BUNDLES%\ops4j-base-lang-1.4.0.jar file:..\%BUNDLES%\ops4j-base-monitors-1.4.0.jar file:..\%BUNDLES%\ops4j-base-store-1.4.0.jar file:..\%BUNDLES%\org.osgi.core-1.4.0.jar file:..\%BUNDLES%\org.osgi.core-4.2.0.jar file:..\%BUNDLES%\tinybundles-2.0.0.jar"
    set "FRAGMENTS=file:..\%BUNDLES%\slf4j-api-1.6.1.jar file:..\%BUNDLES%\slf4j-simple-1.6.1.jar"
if %1 == 174 set "OPTIONS=file:..\%BUNDLES%\org.osgi.compendium-1.4.0.jar"
if %1 == 176 set "OPTIONS=file:..\%BUNDLES%\bndlib-2.1.0.jar file:..\%BUNDLES%\ops4j-base-io-1.4.0.jar file:..\%BUNDLES%\ops4j-base-lang-1.4.0.jar file:..\%BUNDLES%\ops4j-base-monitors-1.4.0.jar file:..\%BUNDLES%\ops4j-base-store-1.4.0.jar file:..\%BUNDLES%\org.osgi.core-1.4.0.jar file:..\%BUNDLES%\org.osgi.core-4.2.0.jar file:..\%BUNDLES%\slf4j-api-1.6.1.jar file:..\%BUNDLES%\slf4j-simple-1.6.1.jar file:..\%BUNDLES%\tinybundles-2.0.0.jar"
if %1 == 203 set "OPTIONS=file:..\%BUNDLES%\jna-4.0.0.jar file:..\%BUNDLES%\org.osgi.core-1.4.0.jar"
if %1 == 206 set "OPTIONS=file:..\%BUNDLES%\org.osgi.compendium-1.4.0.jar file:$BUNDLES/servlet-2.3.jar"
if %1 == 207 set "OPTIONS=file:..\%BUNDLES%\bndlib-2.1.0.jar file:..\%BUNDLES%\ops4j-base-io-1.4.0.jar file:..\%BUNDLES%\ops4j-base-lang-1.4.0.jar file:..\%BUNDLES%\ops4j-base-monitors-1.4.0.jar file:..\%BUNDLES%\ops4j-base-store-1.4.0.jar file:..\%BUNDLES%\org.osgi.core-1.4.0.jar file:..\%BUNDLES%\org.osgi.core-4.2.0.jar file:..\%BUNDLES%\slf4j-api-1.6.1.jar file:..\%BUNDLES%\slf4j-simple-1.6.1.jar file:..\%BUNDLES%\tinybundles-2.0.0.jar"
if %1 == 208 set "OPTIONS=file:..\%BUNDLES%\bndlib-2.1.0.jar file:..\%BUNDLES%\ops4j-base-io-1.4.0.jar file:..\%BUNDLES%\ops4j-base-lang-1.4.0.jar file:..\%BUNDLES%\ops4j-base-monitors-1.4.0.jar file:..\%BUNDLES%\ops4j-base-store-1.4.0.jar file:..\%BUNDLES%\org.osgi.core-1.4.0.jar file:..\%BUNDLES%\org.osgi.core-4.2.0.jar file:..\%BUNDLES%\slf4j-api-1.6.1.jar file:..\%BUNDLES%\slf4j-simple-1.6.1.jar file:..\%BUNDLES%\tinybundles-2.0.0.jar"
if %1 == 230 set "OPTIONS=file:..\%BUNDLES%\org.apache.felix.log-1.0.1.jar"
if %1 == 235 set "OPTIONS=file:..\%BUNDLES%\org.apache.felix.http.jetty-2.2.0.jar file:..\%BUNDLES%\org.apache.felix.log-1.0.1.jar"
if %1 == 302 set "OPTIONS=file:..\%BUNDLES%\jna-4.0.0.jar file:..\%BUNDLES%\org.osgi.core-1.4.0.jar"

if not exist tmp mkdir tmp & @xcopy "%DIR%\..\com.sogetiht.otb.properties.cfg" tmp\ > nul
cd tmp

for /f "delims="  %%a in ('dir "..\%BUNDLES%\com.sogetiht.otb.sysatt%1.*" /B') Do (
  if not defined OPTIONS (
    set "OPTIONS=file:..\%BUNDLES%\%%a"
  ) else (
    set "OPTIONS=!OPTIONS! file:..\%BUNDLES%\%%a"
  )
)

if %1 == 000 goto utilRun
if %1 == 100 goto tinyRun
if %1 == 165 goto tinyRun
if %1 == 187 goto 187Run
if %1 == 207 goto tinyRun
if %1 == 208 goto tinyRun
if %1 == 225 goto 225Run
if %1 == 303 goto unsafeRun
if %1 == 304 goto buggedRun

java -Dorg.osgi.framework.storage.clean=onFirstInit -Dfelix.auto.deploy.action= -Dorg.osgi.framework.startlevel.beginning=4 -Dfelix.auto.start.1="file:..\%BUNDLES%\com.sogetiht.otb.util.jar" -Dfelix.auto.start.4="%OPTIONS%" -jar "%ROOT%\felix\bin\felix.jar"
goto exit

:utilRun
java -Dorg.osgi.framework.storage.clean=onFirstInit -Dfelix.auto.deploy.action= -Dorg.osgi.framework.startlevel.beginning=4 -Dfelix.auto.start.1="file:..\%BUNDLES%\com.sogetiht.otb.util.jar" -jar "%ROOT%\felix\bin\felix.jar"
goto exit

:225Run
java -Dorg.osgi.framework.storage.clean=onFirstInit -Dfelix.auto.deploy.action= -Dorg.osgi.framework.startlevel.beginning=4 -Dfelix.auto.start.1="file:..\%BUNDLES%\com.sogetiht.otb.util.jar" -Dfelix.auto.start.4="%OPTIONS% file:..\%BUNDLES%\com.sogetiht.otb.sysatt165.helloService.jar" -jar "%ROOT%\felix\bin\felix.jar"
goto exit

:187Run
java -Dorg.osgi.framework.storage.clean=onFirstInit -Dfelix.auto.deploy.dir=%SYSATT187% -Dfelix.auto.deploy.action=install,start -Dorg.osgi.framework.startlevel.beginning=4 -Dfelix.auto.start.1="file:..\%BUNDLES%\com.sogetiht.otb.util.jar" -jar "%ROOT%\felix\bin\felix.jar"
goto exit

:tinyRun
java -Dorg.osgi.framework.storage.clean=onFirstInit -Dfelix.auto.deploy.action=install,start -Dfelix.auto.install.1="%FRAGMENTS%" -Dorg.osgi.framework.startlevel.beginning=4 -Dfelix.auto.start.1="file:..\%BUNDLES%\com.sogetiht.otb.util.jar" -Dfelix.auto.start.4="%OPTIONS% -jar "%ROOT%\felix\bin\felix.jar"
goto exit

:buggedRun
java -Dorg.osgi.framework.system.packages.extra=sun.dc.pr,sun.dc.path -Dorg.osgi.framework.storage.clean=onFirstInit -Dfelix.auto.deploy.action= -Dorg.osgi.framework.startlevel.beginning=4 -Dfelix.auto.start.1="file:..\%BUNDLES%\com.sogetiht.otb.util.jar" -Dfelix.auto.start.4="%OPTIONS%" -jar "%ROOT%\felix\bin\felix.jar"
goto exit

:unsafeRun
java -Dorg.osgi.framework.system.packages.extra=sun.misc -Dorg.osgi.framework.storage.clean=onFirstInit -Dfelix.auto.deploy.action= -Dorg.osgi.framework.startlevel.beginning=4 -Dfelix.auto.start.1="file:..\%BUNDLES%\com.sogetiht.otb.util.jar" -Dfelix.auto.start.4="%OPTIONS%" -jar "%ROOT%\felix\bin\felix.jar"
goto exit

:exit
cd %DIR%
exit /b 0

:exiterror
cd %DIR%
exit /b 1