@echo off
REM Run YunTech Campus Activity Registration Platform

set BASE_DIR=%~dp0
set JAVAFX_LIB=%BASE_DIR%javafx-sdk-24.0.1\lib
set BIN_DIR=%BASE_DIR%java_frontFX\JAVAFX\bin
set LIB_DIR=%BASE_DIR%java_frontFX\lib

REM Build classpath
set CLASSPATH=%BIN_DIR%;%JAVAFX_LIB%\*;%LIB_DIR%\*

echo Starting YunTech Campus Activity Registration Platform...
echo.

java --module-path "%JAVAFX_LIB%" --add-modules javafx.controls,javafx.fxml -cp "%CLASSPATH%" Launcher

pause
