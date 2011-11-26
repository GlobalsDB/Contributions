#!/bin/sh

# run.sh
# Runs hospital monitor

export CLASSPATH="hospital-monitor.jar;globalsdb.jar;log4j.jar;slf4j-api.jar;slf4j-log4j.jar;jetty-server.jar;jetty-continuation.jar;jetty-http.jar;jetty-io.jar;jetty-util.jar;jetty-servlet.jar;jetty-security.jar;servlet-api.jar"

echo "Hospital Monitor is about to run on port 4080 (unless there are errors below!)"
echo "To access the injector page, point your browser at:"
echo "  http://localhost:4080/hospmon"
echo "To access the statistics page, point your browser at:"
echo "  http://localhost:4080/statistics"
echo "..."
echo "..."

echo $CLASSPATH

$JAVA_HOME/bin/java -cp $CLASSPATH com.intersystems.globals.hospmon.webapp.HospitalMonitor
