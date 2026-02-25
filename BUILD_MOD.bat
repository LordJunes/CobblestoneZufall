@echo off
setlocal enabledelayedexpansion
title Minecraft_CEO Mod Builder - Full Auto Edition
echo ==================================================
echo       COBBLESTONEZUFALL MOD - FULL AUTO
echo ==================================================
echo.

:: --- CONFIGURATION ---
set "MOD_VERSION=1.2.5"
set "JAR_NAME=cobblestonezufall-!MOD_VERSION!.jar"
set "TARGET_DIR=server"
set "TARGET_JAR=%TARGET_DIR%\HytaleServer.jar"

:: Paths using system variables for universal compatibility
set "HYTALE_INSTALL_PATH=%APPDATA%\Hytale\install\release\package\game\latest\Server\HytaleServer.jar"
set "HYTALE_MODS_FOLDER=%APPDATA%\Hytale\UserData\Mods"

:: 1. SEARCH FOR HYTALE SERVER JAR
if exist "%TARGET_JAR%" (
    echo [INFO] HytaleServer.jar already in project.
) else (
    echo [INFO] Searching for Hytale installation...
    if exist "%HYTALE_INSTALL_PATH%" (
        echo [SUCCESS] Found Hytale at: %HYTALE_INSTALL_PATH%
        if not exist "%TARGET_DIR%" mkdir "%TARGET_DIR%"
        copy /Y "%HYTALE_INSTALL_PATH%" "%TARGET_JAR%" >nul
    ) else (
        echo [ERROR] Could not find HytaleServer.jar! 
        echo Please put it into a folder named 'server' manually.
        pause
        exit
    )
)

:: 2. START THE BUILD PROCESS
echo.
echo [INFO] Starting build process. This may take a minute...
echo.
call gradlew.bat shadowJar

if %ERRORLEVEL% NEQ 0 (
    echo.
    echo [ERROR] Build failed! Check if Java 21 is installed.
    pause
    exit
)

:: 3. AUTOMATIC INSTALLATION
echo.
echo [INFO] Build successful! Installing mod...

if not exist "%HYTALE_MODS_FOLDER%" mkdir "%HYTALE_MODS_FOLDER%"

:: Copy the built JAR to the Hytale Mods folder
copy /Y "build\libs\%JAR_NAME%" "%HYTALE_MODS_FOLDER%\" >nul

if %ERRORLEVEL% EQU 0 (
    echo.
    echo ==================================================
    echo ✅ SUCCESS! Mod is ready to play.
    echo.
    echo Extracted to: %CD%
    echo Installed to: %HYTALE_MODS_FOLDER%\%JAR_NAME%
    echo.
    echo Just start Hytale and enjoy!
    echo ==================================================
) else (
    echo [ERROR] Could not copy the mod to the Hytale folder.
)

echo.
pause
