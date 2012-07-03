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
	this.superPath; //an array of the subscripts leading to this node
	this.subNodes=[]; //an array of the documents and data below this one
	this.globalName; //the name of the global
	this.isADocument=true; //used to check if a piece of data is a document

	function addDocument(var1, var2) 
	{
		/*
		 * 	decides to call addDocument1 (for a document)
		 *  or addDocument2 (for a key-value pair)
		 *  should be given one argument for a doc, and 
		 *  two for a key-value pair
		 * 
		 * 
		 */
		if (isADocument(var1))
		{
			this.addDocument1(var1);
		}
		else 
		{
			this.addDocument2(var1, var2);
		}
	}
	
	function addDocument1 (document) //adds a document to a document
	{
			document.global=this.global;
			document.superPath=this.superPath.concat(document.id);
			document.globalName=this.globalName;
			this.addDocument2(document.id, document);
	}
	
	function addDocument2(key, value) //adds non-document data to a document
	{
		this.global.set({
			global:this.globalName,
			subscripts: this.superPath.concat(key),
			data: value
		});
		if (isADocument(value))
		{
			this.subNodes.push(value);
		}
		else
		{
			this.subNodes.push(key);
		}
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
	
	function dumpInfo(a) //public method which calls dumpInfo1
	{
		this.dumpInfo1(a, '');
	}
	
	function dumpInfo1(a, startString) 
	/* 
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
				var s=startString+"key: "+this.subNodes[i]+"   value: ";
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
	
	function clearDocument() //"resets" a document
	{
		this.subNodes=[];
		this.global.kill({
			global: this.globalName,
			subscripts: this.superPath
		});
	}
	
		
	//these are important, do not erase them
	this.addDocument=addDocument;
	this.addDocument1=addDocument1;
	this.addDocument2=addDocument2;
	this.promoteToCollection=promoteToCollection;
	this.dumpInfo=dumpInfo;
	this.dumpInfo1=dumpInfo1;
	this.clearDocument=clearDocument;
}

//end document class

function isADocument(candidate) //returns true if candidate is a document and false otherwise
{
	return(!(candidate.isADocument===undefined)&& candidate.isADocument);
	
}


function testMethod() //a method used to test the features of Main
{
	console.log("Creating a collection and intiallizing it with data...\n"); 
	var a= new document("fridgeContents");
	a.promoteToCollection();
	b=new document("beverages");
	c=new document("leftovers");
	a.addDocument(b);
	a.addDocument(c);
	a.addDocument("shelves", "need cleaning");
	b.addDocument("milk", "spoiled");
	b.addDocument("water", "filtered");
	c.addDocument("soup", "chicken noodle");
	c.addDocument("lasagna", "burnt");
	a.dumpInfo(4); 
	a.clearDocument();
	console.log("Clearing data from the collection...");
	a.dumpInfo(4);
	a.global.close();  
}



