echo off
cls

set DIR=%CD%
set ROOT=%DIR%\..\..
cd %ROOT%

if exist felix (
echo ERROR: Felix est d‚j… install‚. Pour relancer
echo        l'installation, supprimez le dossier
echo        felix et relancez ce script.
cd %DIR%
exit /b 1
)

set proxy=off
echo Installation de Felix
echo ---------------------
echo ^> Utilisez-vous un proxy (O/n) ?
set /P input=
if not %input% EQU O goto download
echo ^> Adresse du proxy (eg : http://proxy.myprovider.net:8080)
set /P http_proxy=
set proxy=on
:download
"%DIR%\tools\wget.exe" --proxy=%proxy% http://apache.crihan.fr/dist//felix/org.apache.felix.main.distribution-4.2.1.zip

"%DIR%\tools\7-Zip\7z.exe" x org.apache.felix.main.distribution-4.2.1.zip > nul
ren felix-framework-4.2.1 felix
del org.apache.felix.main.distribution-4.2.1.zip
cd %DIR%
exit /b 0
