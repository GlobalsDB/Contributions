@echo off

set CLASSPATH=hospital-monitor.jar
set CLASSPATH=%CLASSPATH%;globalsdb.jar
set CLASSPATH=%CLASSPATH%;log4j.jar
set CLASSPATH=%CLASSPATH%;slf4j-api.jar
set CLASSPATH=%CLASSPATH%;slf4j-log4j.jar
set CLASSPATH=%CLASSPATH%;jetty-server.jar
set CLASSPATH=%CLASSPATH%;jetty-continuation.jar
set CLASSPATH=%CLASSPATH%;jetty-http.jar
set CLASSPATH=%CLASSPATH%;jetty-io.jar
set CLASSPATH=%CLASSPATH%;jetty-util.jar
set CLASSPATH=%CLASSPATH%;jetty-servlet.jar
set CLASSPATH=%CLASSPATH%;jetty-security.jar
set CLASSPATH=%CLASSPATH%;servlet-api.jar

echo Hospital Monitor is about to run on port 4080 (unless there are errors below!)
echo To access the injector page, point your browser at:
echo   http://localhost:4080/hospmon
echo To access the statistics page, point your browser at:
echo   http://localhost:4080/statistics
echo +

%JAVA_HOME%/bin/java com.intersystems.globals.hospmon.webapp.HospitalMonitor
