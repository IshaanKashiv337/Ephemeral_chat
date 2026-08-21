#!/bin/bash

# Create a bin directory for compiled class files if it doesn't exist
mkdir -p bin

# Compile all Java files from the src directory into the bin directory
echo "Compiling backend..."
javac -d bin src/*.java

# Run the Main class (assuming a lib folder if you add a JDBC driver jar like mysql-connector.jar)
echo "Starting server on http://localhost:8080..."
java -cp "bin:lib/*" Main