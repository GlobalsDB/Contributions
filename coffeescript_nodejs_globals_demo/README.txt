
Instructions for running the coffeescript+node.js+globals demo
==========
Author: Olivier Caudron, InterSystems Benelux June 20, 2012.

CAUTION: these examples come "as is" and with no guarantee whatsoever! They are examples and are not meant to be production software. You shall not hold the author or InterSystems corp. liable for any consequences of using these examples.

IMPORTANT NOTE: this demo package contains demographic files used to populate the database with random records based on real demographic elements. To the best of our knowledge these files are in the public domain. If you have reason to believe this is not the case, please discard this demo and let us know about the issue. 

Please note that this demo is not only a node.js demo, but it is also an experiment in using CoffeeScript instead of Javascript. CoffeeScript is a little programming language that aims at improving the development paradigm of Javascript. CoffeeScript compiles to Javascript, so it can be used instead of Javascript in node.js.

To install:

Have a recent version of Caché (should be 2012.2 or above) or GlobalDB.

Install node.js. IMPORTANT: make sure the node.js executable is of the same bit depth (32 or 64) as your globals library. As 64 bit support in node.js is fairly recent, it is not always entirely obvious that you have a 64 bit executable at this time. If you experience basic errors running the example, this is the first thing you should check.

Usually, node.js comes with npm, a package manager for node. Verify that it is installed, and if not, install it. It will simplify things greatly.

Install coffeescript: npm install -g coffee-script.

The example shows asynchronous data inserts. To synchronize the asynchronous calls, I used a node.js library called "async". You must also install it: npm install async.

The CoffeScript example must be compiled into Javascript before it is run (I also provide the compiled Javascript in this package so you can run it without going through the CoffeScript steps if you wish). I provide a shell script for *nix/Linux platforms that will compile, set environment variables, and run the example. The shell script is named "comprun.sh". You MUST edit it to correctly set the environment variables for your system. Generally speaking, you MUST set the environment variables GLOBALS_HOME and, if relevant, NODE_PATH before running the example. On Windows, I recommend you create a batch file to do the same, it makes things much easier.

BEFORE YOU RUN THE EXAMPLE: the code uses data coming from data files to populate the persistent records. If you unpacked the zip file with the same directory structure, you're all set: the script refers to the data using a relative path that is consistent with the way directories are stored in the zip archive. Otherwise, you can either hard-code the path to those files on your system in the coffeescript code (the variable is clearly marked in the code) or pass the path as the second parameter when invoking the script from node.js (the first parameter is the number of records to create as explained below). 

PLEASE NOTE that the first step executed by the script loads the populate data from file into memory. This can take several seconds, so be patient. Of course, this time is not taken into account in the performance measurements.

By default the example creates 10 records, it is sufficient for a functional test but not for a performance test, of course. For a performance test, provide the number of records as a command-line parameter. Note that the test will create twice that many records because we are testing 2 different persistence approaches. I have demoed this with 100,000 records.

Enjoy!
