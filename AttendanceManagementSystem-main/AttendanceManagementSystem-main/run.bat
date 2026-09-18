@echo off
echo ========================================
echo   Attendance Management System
echo ========================================

:: Go to the directory where this script lives
cd /d "%~dp0"

:: Clean and recreate output folder
if exist out rmdir /s /q out
mkdir out

:: Collect all .java files into a list
echo Collecting source files...
dir /s /b src\*.java > sources.txt

:: Compile
echo Compiling...
javac -d out @sources.txt
if %errorlevel% neq 0 (
    echo.
    echo Compilation FAILED. Check the errors above.
    del sources.txt
    pause
    exit /b 1
)

del sources.txt
echo Compilation successful!
echo.

:: Create data directory if needed
if not exist data mkdir data

:: Run from project root so data/ resolves correctly
echo Starting application...
java -cp out attendance.Main

pause
