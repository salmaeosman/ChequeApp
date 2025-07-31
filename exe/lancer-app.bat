@echo off
setlocal

:: Nom du fichier JAR
set JAR_NAME=GestionCheque.jar

:: Chemin vers les modules JavaFX RELATIF au dossier contenant le .bat
set JAVAFX_LIB=%~dp0javafx-sdk-21.0.8\lib

:: Vérification JAVA_HOME
if not defined JAVA_HOME (
    echo JAVA_HOME non défini. Tentative de détection automatique...
    for /f "delims=" %%i in ('where java') do (
        set JAVA_EXE=%%i
        goto foundJava
    )
    echo Java non trouvé. Veuillez installer Java ou définir JAVA_HOME.
    pause
    exit /b 1
)

:foundJava
if not defined JAVA_EXE set JAVA_EXE=%JAVA_HOME%\bin\java.exe

:: Lancement de l'application
echo Lancement de l'application...
"%JAVA_EXE%" ^
--module-path "%JAVAFX_LIB%" ^
--add-modules javafx.controls,javafx.fxml ^
-jar "%~dp0%JAR_NAME%"

pause
