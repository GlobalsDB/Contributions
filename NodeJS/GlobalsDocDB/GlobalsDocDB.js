var rootOfGlobalsInstall = process.env.GLOBALS_HOME;
var rootOfNodeInstall = process.env.nodeRoot;
var globals = require(rootOfNodeInstall+'\\cache');
var pathToGlobalsMGR = rootOfGlobalsInstall + '/mgr';


//testMethod();
//console.log("\n\n\n");

sampleMethod();

//begin document class

function document(name, param1)  
/*
 * name should be the desired name of the document
 * 
 * param1 is an optional parameter for initializing this more succinctly
 * if you want to make this a collection, param1 should be "true"
 * if you want to immediately add this to another document, param1
 * should be that document
 * 
 */
{
	//these are important, do not erase them
	this.addInfo=addInfo;
	this.addDocument=addDocument;
	this.addData=addData;
	this.promoteToCollection=promoteToCollection;
	this.dumpInfo=dumpInfo;
	this.dumpInfo1=dumpInfo1;
	this.clearDocument=clearDocument;
	this.endCollection=endCollection;
	
	
	
	//beginning of "proper" constructor
	
	this.id=name; //the name of this node
	this.global; //the global
	this.superPath; //an array of the subscripts leading to this node
	this.subNodes=[]; //an array of the documents and data below this one
	this.globalName; //the name of the global
	this.isADocument=true; //used to check if a piece of data is a document
	if(param1===true)	
	{
		this.promoteToCollection();
	}
	if(isADocument(param1))
	{
		param1.addInfo(this);
	}

	
	
	function addInfo(var1, var2) 
	{
		/*
		 *  a "public" method
		 * 	decides to call addDocument (for a document)
		 *  or addData (for a key-value pair)
		 *  should be given one argument for a doc, and 
		 *  two for a key-value pair
		 * 
		 * 
		 */
		if (isADocument(var1))
		{
			this.addDocument(var1);
		}
		else 
		{
			this.addData(var1, var2);
		}
	}
	
	function addDocument (document) //a "private" method; adds a document to this
	{
			document.global=this.global;
			document.superPath=this.superPath.concat(document.id);
			document.globalName=this.globalName;
			this.addData(document.id, document);
	}
	
	function addData(key, value) //a "private" method; adds non-document data to this
	{
		var result=this.global.data({
			global: this.globalName,
			subscripts: this.superPath.concat(key)
		}).defined;
		/*
		 * result is used to see if there is already data
		 * 
		 * 
		 * result will equal:
		 * 0 if undefined
		 * 1 if has value
		 * 10 if further subscripted without data
		 * 11 if it has data and subscripts
		 * 
		 */
		
		this.global.set({
			global:this.globalName,
			subscripts: this.superPath.concat(key),
			data: value
		});
		if(result%2===0)
		{
			if (isADocument(value))
			{
				this.subNodes.push(value);
			}
			else
			{
				this.subNodes.push(key);
			}
		}
	}
	

	function promoteToCollection()
	{
		/*
		 * a method that may or may not deserve to be private
		 * "promotes" a document to a collection
		 * note that this MUST be done before data can be added
		 * as data can only be added to collections or documents
		 * that are in a collection(or are in a document 
		 * that is in a collection etc...)
		 * 
		 */
		this.global=new globals.Cache();
		this.global.open({
			path: pathToGlobalsMGR,
			username: "userName",
			password: "password",
			namespace: "namespace"
		}
		);
		this.globalName=this.id;
		this.superPath=[];
	}
	
	function dumpInfo(a) //a "public" method; calls dumpInfo1
	{
		this.dumpInfo1(a, '');
	}
	
	function dumpInfo1(a, startString) 
	/* 
	 * a "private" method
	 * 
	 *  a rather complicated method that outputs information on the document
	 *  if a=0,1,2 then it prints info using JSON stringify
	 *  for other values of a, prints using my own method 
	 *  which now has indentation and spacing to make things more clear
	 *  
	 *  this method should not be public; all invocations of dumpInfo1 should
	 *  come from dumpInfo (or internally)
	 * 
	 */
	{
		if (this.subNodes.length===0)
		{
			console.log("The document \""+ this.id + "\" is empty.");
			return;
		}
		
		
		if (a===0||a===1||a===2)
		{
			var b= ["list", "object", "array"]; //different types of outputs, from least to most verbose
			var result=this.global.retrieve({
				global: this.globalName,
				subscripts: this.superPath
			}, b [a]);
			console.log("Contents of the \"" + this.id +
					"\" document:"+ JSON.stringify(result, null, '\t'));
			return;
		}
		
		
		if (this.superPath.length===0)
		{
			console.log(startString+ "In the collection \""+ this.id+
			"\" there are the following things:");
		}
		else
		{
			console.log(startString+"In the document \""+ this.id+
			"\" there are the following things:");
		}
		
		for (var i=	0; i<this.subNodes.length;i++)
		{
			if (!isADocument(this.subNodes[i]))
			{
				var s=startString+this.subNodes[i]+": ";
				s+=this.global.get({
					global: this.globalName,
					subscripts: this.superPath.concat(this.subNodes[i])
				}).data;
				console.log(s);
			}
		}
		console.log();
		for (var i=0;i<this.subNodes.length;i++)
		{
			if (isADocument(this.subNodes[i]))
			{
				this.subNodes[i].dumpInfo1 (4, startString+"\t");
			}
		}
	}
	
	function clearDocument() //a "public" method; "resets" a document
	{
		this.subNodes=[];
		this.global.kill({
			global: this.globalName,
			subscripts: this.superPath
		});
	}
	
	function endCollection() //a "public" method; closes the global and clears the collection
	{
		this.clearDocument();
		this.global.close();
	}

}

//end document class

function isADocument(candidate) //returns true if candidate is a document and false otherwise
{
	if(candidate===undefined)
		{
		return false;
		}
	return(!(candidate.isADocument===undefined)&& candidate.isADocument);
	
}


function testMethod() //a method used to test the features of Main
{
	console.log("Creating a collection and intiallizing it with data...\n"); 
	var a= new document("fridgeContents", true);
	b=new document("beverages", a);
	c=new document("leftovers", a);
	a.addInfo("shelves", "need cleaning");
	b.addInfo("milk", "spoiled");
	b.addInfo("water", "filtered");
	c.addInfo("soup", "chicken noodle");
	c.addInfo("lasagna", "burnt");
	a.dumpInfo(); 
	a.clearDocument();
	console.log("Clearing data from the collection...");
	a.dumpInfo();
	a.endCollection();  
}

function sampleMethod() //another method for showing off/testing GlobalsDocDB testMethod()
{
	/*
	 * I will start with a simple example of creating a document database
	 * and giving it some values with a breakdown of each step.
	 * 
	 */
	
	
	
	var inanimates=new document("InanimateObjects", true);  
	/*
	 * This creates a document. The name of the document is the first parameter.
	 * 
	 * In this case, the document will also be made a collection because the
	 * second parameter is true. If the second parameter is ANYTHING ELSE
	 * the document will not be made a collection, but documents may be promoted to
	 * collections with promoteToCollection(). However, it is advised that you make
	 * collections this way, instead of with promoteToCollection().
	 * 
	 * Data CANNOT be stored in a document unless it is "linked" to a collection.
	 * A document is "linked" to a collection if:
	 * a) it is a collection or
	 * b) it is in a document that is "linked"
	 * 
	 * 
	 * We have created this collection so that we can store information about
	 * inanimate objects that we find.
	 * 
	 */
	gem=new document("diamond", inanimates);
	/*
	 * Oh look, we found an inanimate object!
	 * 
	 * We would like to make a document to store information on the diamond
	 * that we found. Therefore we call the constructor for documents
	 * again, using "diamond" as the name of the document. However, note that 
	 * this time the second parameter is different. Because the second parameter is not
	 * "true" this document will not be a collection. However, putting a document
	 * as your second parameter for the constructor automatically adds the new document
	 * to the second parameter. Alternatively, we could have called the constructor with
	 * only a name parameter, and then added the new document to the collection using code like this:
	 * 
	 *  gem=new document("diamond");
	 *  inanimates.addInfo(gem);
	 *  
	 *  If the second parameter of the constructor is anything besides "true" or a
	 *  document, it will be ignored.
	 * 
	 */
	gem.addInfo("description", "big beautiful diamond");
	/*
	 * We decide to add a description to our gem. Note that addInfo() is used to add
	 * both documents and non-document data. When adding non-document data, addInfo()
	 * takes two parameters, a key and a value.
	 */
	gem.addInfo("name", "Tom");
	/*
	 * Later on we name our diamond Tom and add that information to the database.
	 */
	
	inanimates.dumpInfo();
	/*
	 *  Now we look at what is in our database. Note that we could call dumpInfo() on
	 *  any document (not just a collection) and get just information for that document and its
	 *  descendants.
	 */
	gem.addInfo("description", "a rock");
	/*
	 * We realize Tom is not a diamond, but just a rock! We change our database to reflect that.
	 */
	gem.dumpInfo();
	inanimates.endCollection(); 
	/*
	 * After printing out all the information on Tom that is in our database, we decide
	 * to terminate our database with endCollection(). endCollection removes all data and also
	 * closes the global. If you just want to delete all data without closing the global, use clearDocument()
	 */
	
	console.log("\n\n");
	
	/*
	 * Now that we've done a simple example, let's make a bigger collection! We will be using arrays
	 * to set up several documents at once.
	 */
	var animals=new document("Animals", true);
	var namesArray=["cats", "pangolin", "dodo", "unicorn"];
	var dietArray= ["meat", "ants", "unknown", "rainbows"];
	var populationArray= [1000000, 1000, 0, 0];
	var someAnimals=[];
	for (var i=0;i<4;i++)
	{
		someAnimals [i]=new document(namesArray [i], animals);
		someAnimals[i].addInfo("diet", dietArray [i]);
		someAnimals[i].addInfo("population", populationArray[i]);
	}
	var catSpeciesArray= ["lion", "tiger", "jaguar"];
	var catsArray=[];
	var habitatArray= ["savannah", "mountains", "jungle"];
	var weightArray= [250, 300, 150];
	for (var i=0;i<3;i++)
	{
		catsArray[i]=new document(catSpeciesArray[i], someAnimals[0]);
		catsArray[i].addInfo("habitat",habitatArray[i]);
		catsArray[i].addInfo("weight",weightArray[i]);
	}
	
	/*
	 * This now contains a collection of animals, each of which has some information associated with it.
	 * The cats document also has more documents within it, with each document representing
	 * one specific species of cats. Notice that one can easily make nested documents.
	 */
	
	animals.dumpInfo();
	
	
	
	var plants=new document("plants", true);
	var trees=new document("tree", plants);
	trees.addInfo("height", "really tall!");
	var grass=new document("grass", plants);
	var roses=new document("rose bush", plants);
	var sunflower=new document("sunflower", plants);
	plants.dumpInfo();
	plants.clearDocument();
	animals.endCollection();
}


