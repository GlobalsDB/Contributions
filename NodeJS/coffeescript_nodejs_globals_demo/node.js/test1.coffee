#
# Simple demo of using coffescript (see http://coffeescript.org/) and node.js (see http://nodejs.org/) 
# to access Caché throuh the Node.js (Javascript) Globals API 
# This demo generates random person records and saves them to the database (using 4 different variants). This operation is timed.
# Then the data is read (3 different variants). This operation is also timed.
#
# Author: O. Caudron, InterSystems Benelux.
# Date: 3/6/2012
#

# Get first command-line parameter (if it exists), convert it to an int, 
# and store it in the (JS) global "count"
if process.argv.length>2 
  count=parseInt process.argv[2]
else
  count=10

path=require "path"
# Indicate here the directory in which the populate data files will be found. 
# You can also pass it as the second command-line parameter (that will override this value)
dataDir ="../data/"
# If there is a second command-line argument, use it as the source for populate data 
# if it is a valid directory (note: we don't test if it contains the right files; if not,
# it will cause an exception later)
if process.argv.length>3
  tmp=path.normalize(process.argv[3])
  if path.existsSync(tmp)
    dataDir=tmp

if not path.existsSync(dataDir)
  console.log("The data directory you provided does not exist! -- ABORTING.")
  return

# Declare other (JS) globals
os=require "os"
fs=require "fs"
async=require "async"

rootOfGlobalsInstall = process.env.GLOBALS_HOME
pathToGlobalsMGR = rootOfGlobalsInstall + "/mgr"

firstnames=[]
lastnames=[]
cities=[]
countries=[]

# Get samples used in populate functions from diverse files
console.log "===================="
console.log "Preparing test, reading random populate data from files..."
lastnames=fs.readFileSync(path.join(dataDir,"BElastnames.txt"), "utf8").split("\n")
firstnames=fs.readFileSync(path.join(dataDir,"BEfirstnames2008.txt"), "utf8").split("\n")
cities=fs.readFileSync(path.join(dataDir,"cities.txt"), "utf8").split("\n")
for c in fs.readFileSync(path.join(dataDir,"countrycodes.txt"), "utf8").split("\n")
  countries[c.split("|")[0]]=c.split("|")[1]
console.log "DONE."

# Utility functions
formatMem=(mem)->
  mem=parseInt(mem)
  return "?" if mem is NaN
  units=[" bytes","KB","MB","GB","TB","PB"]
  for unit in units
    return mem.toFixed(3)+unit if mem<1024
    mem/=1024
  return mem.toFixed()+"HB"

now=()->return new Date().getTime()
dur=(starttime)->return (new Date().getTime()-starttime)+"ms."

# Populate functions
rndLastname=()->return firstnames[Math.floor(Math.random()*firstnames.length)]
rndFirstname=()->return lastnames[Math.floor(Math.random()*lastnames.length)]
rndCity=()->return cities[Math.floor(Math.random()*(cities.length-1))+1]
rndDate=()->return new Date(Math.floor(Math.random()*1261440000000))

# Test utility functions
createPersonObject=()->city=rndCity().split(","); return {firstname:rndFirstname(), lastname:rndLastname(), dob:rndDate().toString(), address:{city:city[1], country:countries[city[0]]}}

# Test1: store objects, synchronous
simpleObjectStoreSync=->
  myData.kill "nodejsdemo1"
  console.log "Starting simple *synchronous* object store with "+count+" objects"
  inter=now()
  for x in [1..count]
    newnode={node:{global:"nodejsdemo1", subscripts:[x]},object:createPersonObject()}
    myData.update newnode, "object"
  console.log "Simple synchronous object store completed in "+dur(inter)

# Test2: store objects, asynchronous
oneSimpleObjectStoreAsync=(x)->
  newnode={node:{global:"nodejsdemo1", subscripts:[x]},object:createPersonObject()}
  myData.update newnode, "object", $=(error, result)->return

simpleObjectStoreAsync=->
  myData.kill "nodejsdemo1"
  console.log "Starting simple *Asynchronous* object store with "+count+" objects"
  inter=now()
  async.parallel(oneSimpleObjectStoreAsync x for x in [1..count])
  console.log "Simple Asynchronous object store completed in "+dur(inter)

# Test 3: store objects JSON-serialized, synchronous
JSONObjectStoreSync=->
  myData.kill "nodejsJSON1"
  console.log "Starting JSON *synchronous* object store with "+count+" objects"
  inter=now()
  for x in [1..count]
    myData.set "nodejsJSON1", x, JSON.stringify(createPersonObject())
  console.log "JSON synchronous object store completed in "+dur(inter)
  
# Test 4: store objects JSON-serialized, synchronous
oneJSONObjectStoreAsync=(x)->myData.set {global:"nodejsJSON1", subscripts:[x], data:JSON.stringify(createPersonObject())}, $=(error, result)->return
JSONObjectStoreAsync=->
  myData.kill "nodejsJSON1"
  console.log "Starting JSON *Asynchronous* object store with "+count+" objects"
  inter=now()
  async.parallel(oneJSONObjectStoreAsync x for x in [1..count])
  console.log "JSON Asynchronous object store completed in "+dur(inter)  
  
# Test 5: get all person records from Belgium, Luxembourg or the Netherlands, synchronous
simpleObjectQuery=->
  console.log "Starting simple object query test"
  inter=now()
  results=[]
  key=myData.order("nodejsdemo1","")
  while key isnt ""
    results.push(myData.retrieve({global:"nodejsdemo1", subscripts:[key]}, "object").object) if myData.get("nodejsdemo1",key,"address","country") in ["Belgium", "Luxembourg", "Netherlands"]
    key=myData.order("nodejsdemo1",key)
  console.log "Simple object query completed in "+dur(inter)+" ("+results.length+" objects retrieved)"

# Test 6: get all person records from Belgium, Luxembourg or the Netherlands from JSON-encoded records, synchronous
JSONObjectQuery=->
  console.log "Starting JSON object query test"
  inter=now()
  results=[]
  key=myData.order("nodejsJSON1","")
  while key isnt ""
    obj=JSON.parse myData.get("nodejsJSON1", key)
    results.push obj if obj.address.country in ["Belgium", "Luxembourg", "Netherlands"]
    key=myData.order("nodejsdemo1",key)
  console.log "JSON object query completed in "+dur(inter)+" ("+results.length+" objects retrieved)"

# Test 7: get all records from Belgium, Luxembourg or the Netherlands, running synchronous retrieves in parallel
simpleGetObjectParallel=(key, results)->
  results.push(myData.retrieve({global:"nodejsdemo1", subscripts:[key]}, "object").object) if myData.get("nodejsdemo1",key,"address","country") in ["Belgium", "Luxembourg", "Netherlands"]
  return
simpleObjectQueryParallel=->
  console.log "Starting simple object *synchronous* *parallel* query test"
  inter=now()
  results=[]
  keys=[]
  key=""
  keys.push key while (key=myData.order("nodejsdemo1",key)) isnt ""
  async.parallel(simpleGetObjectParallel(key, results) for key in keys)
  console.log "Simple object synchronous parallel query completed in "+dur(inter)+" ("+results.length+" objects retrieved)"
  
# ### Main code ###
# First output some general information on the platform
console.log "===================="
console.log "Platform: "+os.hostname()+" "+os.platform()+" ("+os.arch()+"); kernel: "+os.release()+"; "+os.cpus().length+" CPUs; installed memory: "+formatMem(os.totalmem())+"/free: "+formatMem(os.freemem())
console.log "Version of Node.JS: "+process.version
console.log "env var GLOBALS_HOME: "+process.env.GLOBALS_HOME
console.log "env var NODE_PATH: "+process.env.NODE_PATH
console.log "===================="
# Get a reference to the Globals module and output its version
console.log "Referencing globals module"
globals=require "cache"
myData=new globals.Cache()
console.log "Version of globals: "+myData.version()
console.log "===================="
# Test
start=now()
console.log "Opening connection to Caché"
inter=now()
myData.open {path:pathToGlobalsMGR, username:"_system", password:"SYS", namespace:"samples"}, $=(error, result)->
  if not error
    console.log "Connection to Caché took "+dur(inter)
    # Tests proper
    simpleObjectStoreSync()
    console.log ""
    simpleObjectStoreAsync()
    console.log ""
    JSONObjectStoreSync()
    console.log ""
    JSONObjectStoreAsync()
    console.log ""    
    simpleObjectQuery()
    console.log ""
    JSONObjectQuery()
    console.log ""
    simpleObjectQueryParallel()
    console.log ""
    console.log "Closing connection to Caché"
    inter=now()
    myData.close()
    console.log "Close connection took "+dur(inter)
    console.log "Test completed in "+dur(start)
  else
    console.log "Error "+JSON.stringify(result, null, '\t')