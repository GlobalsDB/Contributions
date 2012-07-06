#!/bin/sh
echo compiling source...
if coffee -c test1.coffee 
then
echo DONE. Now running source...
export GLOBALS_HOME=/opt/ensemble20131
export NODE_PATH=/usr/lib/nodejs
node test1.js $1 $2
fi