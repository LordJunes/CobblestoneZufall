@echo off
setlocal enabledelayedexpansion
title Minecraft_CEO Mod Builder - AutoDetect Edition
echo ==================================================
echo       COBBLESTONEZUFALL MOD - EASY BUILDER
echo ==================================================
echo.

:: Path configuration
set "TARGET_DIR=server"
set "TARGET_JAR=%TARGET_DIR%\HytaleServer.jar"
set "HYTALE_PATH=%APPDATA%\Hytale\install\release\package\game\latest\Server\HytaleServer.jar"

:: 1. Check if JAR is already there
if exist "%TARGET_JAR%" (
    echo [INFO] HytaleServer.jar already present in project.
    goto START_BUILD
)

echo [INFO] HytaleServer.jar missing in project. Searching local Hytale installation...

:: 2. Try to auto-locate HytaleServer.jar
if exist "%HYTALE_PATH%" (
    echo [SUCCESS] Found Hytale installation at:
    echo "%HYTALE_PATH%"
    echo.
    echo [INFO] Copying HytaleServer.jar to project...
    if not exist "%TARGET_DIR%" mkdir "%TARGET_DIR%"
    copy /Y "%HYTALE_PATH%" "%TARGET_JAR%" >nul
) else (
    echo [ERROR] Could not find HytaleServer.jar automatically.
    echo.
    echo Please do the following manually:
    echo 1. Create a folder named "server" in this directory.
    echo 2. Copy your "HytaleServer.jar" into that folder.
    echo.
    pause
    exit
)

:START_BUILD
echo.
echo [INFO] Environment ready. Starting Gradle build...
echo.

:: 3. Run Gradle build
call gradlew.bat shadowJar

if %ERRORLEVEL% EQU 0 (
    echo.
    echo ==================================================
    echo ✅ SUCCESS! Your mod has been built.
    echo.
    echo You can find the playable file here:
    echo \build\libs\cobblestonezufall-1.2.5.jar
    echo ==================================================
) else (
    echo.
    echo [ERROR] Build failed. 
    echo Do you have Java 21 or newer installed?
)

echo.
pause
