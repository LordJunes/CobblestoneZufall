@echo off
setlocal enabledelayedexpansion
<<<<<<< HEAD
title Minecraft_CEO Mod Builder v2
=======
title Minecraft_CEO Mod Builder v3
>>>>>>> cd2cc2b (Prepare clean project state)
echo ==================================================
echo       COBBLESTONEZUFALL MOD - FULL AUTO
echo ==================================================
echo.

:: --- CONFIGURATION ---
<<<<<<< HEAD
set "MOD_VERSION=1.2.5"
set "JAR_NAME=cobblestonezufall-%MOD_VERSION%.jar"
set "TARGET_DIR=server"
set "TARGET_JAR=%TARGET_DIR%\HytaleServer.jar"

:: Paths (Using quotes for safety with spaces)
set "HYTALE_INSTALL_PATH=%APPDATA%\Hytale\install\release\package\game\latest\Server\HytaleServer.jar"
set "HYTALE_MODS_FOLDER=%APPDATA%\Hytale\UserData\Mods"
=======
set "TARGET_DIR=server"
set "TARGET_JAR=%TARGET_DIR%\HytaleServer.jar"

:: Paths
set "HYTALE_INSTALL_PATH=%APPDATA%\Hytale\install\release\package\game\latest\Server\HytaleServer.jar"
set "HYTALE_MODS_FOLDER=%APPDATA%\Hytale\UserData\Mods"
set "CONFIG_EXPORT_DIR=build\config_bundle"
>>>>>>> cd2cc2b (Prepare clean project state)

:: 1. CHECK FOR HYTALE SERVER JAR
if exist "%TARGET_JAR%" (
    echo [INFO] HytaleServer.jar already in project.
) else (
    echo [INFO] Searching for Hytale installation...
    if exist "%HYTALE_INSTALL_PATH%" (
        echo [SUCCESS] Found Hytale at AppData.
        if not exist "%TARGET_DIR%" mkdir "%TARGET_DIR%"
        copy /Y "%HYTALE_INSTALL_PATH%" "%TARGET_JAR%" >nul
    ) else (
        echo [ERROR] Could not find HytaleServer.jar automatically.
        echo Please put it into a folder named 'server' manually.
        pause
<<<<<<< HEAD
        exit
    )
)

:: 2. START THE BUILD PROCESS (WITH CLEAN)
echo.
echo [INFO] Starting build... (This will take a moment)
echo.

:: We use "clean" to make sure it doesn't just say "UP-TO-DATE"
=======
        exit /b 1
    )
)

:: 2. BUMP VERSION
echo.
echo [INFO] Bumping mod version...
call gradlew.bat bumpModVersion

if %ERRORLEVEL% NEQ 0 (
    echo.
    echo [ERROR] Version bump failed.
    pause
    exit /b 1
)

set "MOD_VERSION="
for /f "tokens=1,2 delims==" %%A in ('findstr /B /C:"modVersion=" gradle.properties') do (
    if /I "%%A"=="modVersion" set "MOD_VERSION=%%B"
)

if "%MOD_VERSION%"=="" (
    echo.
    echo [ERROR] Could not read modVersion from gradle.properties.
    pause
    exit /b 1
)

set "JAR_NAME=cobblestonezufall-%MOD_VERSION%.jar"

:: 3. BUILD
echo.
echo [INFO] Starting build for version %MOD_VERSION%...
echo.
>>>>>>> cd2cc2b (Prepare clean project state)
call gradlew.bat clean shadowJar

if %ERRORLEVEL% NEQ 0 (
    echo.
    echo [ERROR] Build failed! Check your Java installation.
    pause
<<<<<<< HEAD
    exit
)

:: 3. AUTOMATIC INSTALLATION
=======
    exit /b 1
)

:: 4. INSTALL
>>>>>>> cd2cc2b (Prepare clean project state)
echo.
echo [INFO] Build successful! Installing mod...

if not exist "%HYTALE_MODS_FOLDER%" mkdir "%HYTALE_MODS_FOLDER%"
<<<<<<< HEAD

:: Copy with quotes to handle spaces in paths
copy /Y "build\libs\%JAR_NAME%" "%HYTALE_MODS_FOLDER%\%JAR_NAME%" >nul

if %ERRORLEVEL% EQU 0 (
    echo.
    echo ==================================================
    echo ✅ SUCCESS! Mod is ready to play.
    echo.
    echo Installed to: %HYTALE_MODS_FOLDER%\%JAR_NAME%
=======
if not exist "%HYTALE_MODS_FOLDER%\Backup" mkdir "%HYTALE_MODS_FOLDER%\Backup"

if not exist "build\libs\%JAR_NAME%" (
    echo [ERROR] Built jar not found: build\libs\%JAR_NAME%
    pause
    exit /b 1
)

:: Move previous CobblestoneZufall jars to Backup (keep only current version in Mods root)
for %%F in ("%HYTALE_MODS_FOLDER%\cobblestonezufall-*.jar") do (
    if /I not "%%~nxF"=="%JAR_NAME%" (
        move /Y "%%~fF" "%HYTALE_MODS_FOLDER%\Backup\" >nul
    )
)

copy /Y "build\libs\%JAR_NAME%" "%HYTALE_MODS_FOLDER%\%JAR_NAME%" >nul

if %ERRORLEVEL% EQU 0 (
    if exist "%CONFIG_EXPORT_DIR%" rmdir /S /Q "%CONFIG_EXPORT_DIR%"
    mkdir "%CONFIG_EXPORT_DIR%" >nul 2>&1
    if exist "config\*.example.json" copy /Y "config\*.example.json" "%CONFIG_EXPORT_DIR%\" >nul

    echo.
    echo ==================================================
    echo SUCCESS! Mod is ready to play.
    echo.
    echo Installed to: %HYTALE_MODS_FOLDER%\%JAR_NAME%
    echo Config examples: %CONFIG_EXPORT_DIR%
>>>>>>> cd2cc2b (Prepare clean project state)
    echo ==================================================
) else (
    echo [ERROR] Could not copy the mod to the destination.
)

echo.
pause
