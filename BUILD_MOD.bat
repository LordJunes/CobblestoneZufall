@echo off
title Minecraft_CEO Mod Builder
echo ==================================================
echo       COBBLESTONEZUFALL MOD - EASY BUILDER
echo ==================================================
echo.

:: 1. Check if the server folder and JAR exist
if not exist "server\HytaleServer.jar" (
    echo [ERROR] HytaleServer.jar not found!
    echo.
    echo Please do the following:
    echo 1. Create a folder named "server" in this directory.
    echo 2. Copy your "HytaleServer.jar" into that folder.
    echo.
    pause
    exit
)

echo [INFO] Checking environment and building mod...
echo.

:: 2. Run Gradle build
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
