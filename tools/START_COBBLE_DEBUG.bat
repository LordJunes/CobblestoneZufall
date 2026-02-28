@echo off
setlocal
cd /d "%~dp0"
python cobble_debug_companion.py
if errorlevel 1 (
  echo.
  echo Fehler beim Start. Pruefe Python-Installation.
  pause
)
