var rootOfGlobalsInstall = process.env.GLOBALS_HOME;
var rootOfNodeInstall = process.env.nodeRoot;
var globals = require(rootOfNodeInstall+'\\cache');
var pathToGlobalsMGR = rootOfGlobalsInstall + '/mgr';


testMethod();


//begin document class

function document(name)
{
	this.id=name; //the name of this node
	this.global; //the global
	this.superPath; //an array of the subscripts to this node
	this.numberOfSubNodes=0; //the number of subscripts
	this.subNodes=[]; //an array of the nodes below this one
	this.numberOfData=0; //number of non-document data
	this.dataVals=[]; //an array of non-document data
	this.globalName; //the name of the global

	
	function addDocument (document) //adds a document to a document
	{
		document.global=this.global;
		document.superPath=this.superPath.concat(document.id);
		document.globalName=this.globalName;
		this.global.set({
			global:this.globalName,
			subscripts: document.superPath,
			data: document
		});
		this.subNodes.push(document);
		this.numberOfSubNodes++;
	}
	
	function addDatum(key, value) //adds a non-document piece of data to a document
	{
		this.global.set({
			global:this.globalName,
			subscripts: this.superPath.concat(key),
			data: value
		});
		this.numberOfData++;
		this.dataVals.push(key);
	}

	function promoteToCollection()
	{
		/*
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
			namespace: name
		}
		);
		this.globalName=this.id;
		this.superPath=[];
	}
	
	function dumpInfo(a) 
	/* 
	 *  a rather complicated method that outputs information on the document
	 *  if a=0,1,2 then it prints info using JSON stringify
	 *  for other values of a, prints using my own method (which will become more descriptive later!
	 * 
	 */
	{
		if (this.numberOfSubNodes===0)
		{
			if (this.numberOfData===0)
			{
				console.log("The document \""+ this.id + "\" is empty.");
			}
			else 
			{
				for(var i=0; i<this.numberOfData;i++)
				{
					var s="key: "+this.dataVals[i]+"   value: ";
					s+=this.global.get({
						global: this.globalName,
						subscripts: this.superPath.concat(this.dataVals[i])
					}).data;
					console.log(s);
				}
			}
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
		}
		else
		{
			if (this.superPath.length===0)
			{
				console.log("In the collection "+ this.id+
						" there are the following things:");
			}
			var dataHere=this.global.get({
				global: this.globalName,
				subscripts: this.superPath
			}).data;
			console.log(dataHere);
			for (var i=0; i<this.numberOfData;i++)
			{
				var s="key: "+this.dataVals[i]+"   value: ";
				s+=this.global.get({
					global: this.globalName,
					subscripts: this.superPath.concat(this.dataVals[i])
				}).data;
				console.log(s);
			}
			for (var i=0;i<this.numberOfSubNodes;i++)
			{
				this.subNodes[i].dumpInfo (4);
			}
		}
	}
	
	function clearDocument() //"resets" a document
	{
		this.numberOfSubNodes=0;
		this.subNodes=[];
		this.global.kill({
			global: this.globalName,
			subscripts: this.superPath
		});
		this.numberOfData=0;
	}
	
	function getData () //returns a data value for a thing
	{
		return this.global.get({
			global: this.globalName,
			subscripts: this.superPath
		}).data;
	}
		
	//these are important, do not erase them
	this.addDocument=addDocument;
	this.addDatum=addDatum;
	this.promoteToCollection=promoteToCollection;
	this.dumpInfo=dumpInfo;
	this.clearDocument=clearDocument;
	this.getData=getData;
}

//end document class



function testMethod(){ //a method used to test the features of Main


	var a= new document("fridgeContents");
	a.promoteToCollection();
	b=new document("beverages");
	c=new document("leftovers");
	a.addDocument(b);
	a.addDocument(c);
	a.addDatum("shelves", "need cleaning");
	b.addDatum("milk", "spoiled");
	b.addDatum("water", "filtered");
	c.addDatum("soup", "chicken noodle");
	c.addDatum("lasagna", "burnt");
	console.log("Creating a collection and intiallizing it with data...")
	a.dumpInfo(4); 
	a.clearDocument();
	console.log("Clearing data from the collection...");
	a.dumpInfo(4);
	a.global.close();  
}



