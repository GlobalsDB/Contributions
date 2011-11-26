@echo off

rem ... Latest Build
rem ... Not running tests at present
call mvn clean install -DskipTests=true

rem ... Output
copy /Y target\extreme-academy-maven-1.0-SNAPSHOT.jar build\extreme-academy-maven.jar
