#!/bin/bash

echo "========================================"
echo "  Attendance Management System"
echo "========================================"

# Move to the directory where this script lives
cd "$(dirname "$0")"

# Clean and recreate output folder
rm -rf out
mkdir -p out

# Collect source files
echo "Collecting source files..."
find src -name "*.java" > sources.txt

# Compile
echo "Compiling..."
javac -d out @sources.txt
if [ $? -ne 0 ]; then
    echo ""
    echo "Compilation FAILED. Check the errors above."
    rm -f sources.txt
    exit 1
fi

rm -f sources.txt
echo "Compilation successful!"
echo ""

# Create data directory if needed
mkdir -p data

# Run from project root so data/ resolves correctly
echo "Starting application..."
java -cp out attendance.Main
