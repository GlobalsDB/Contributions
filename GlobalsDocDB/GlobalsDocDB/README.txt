This document describes the GlobalsDocDB API. 
Copyright InterSystems 2012

The GlobalsDocDB API is a simple .NET wrapper API for the Intersystems GlobalsDB API, 
which implements a simple document-oriented database using the GlobalsDB data storage structures. 

It is currently provided is a 2-project solution consisting of 1) the API project and 2) a sample 
GUI project that illustrates how to use it. 

A .NET application referencing GlobalsDocDB can create and manage document objects without needing 
any references to the GlobalsDB API, and does not need to be written with any knowledge of the 
Globals API or Globals concepts. Of course, Globals needs to be installed and the GlobalsDocDB 
API references the GlobalsDB API. 

GlobalsDocDB documents are organized into "document sets", and each document set corresponds to a 
single "global" in the Globals DB. There is no document schema and no indexing; it is completely 
free-form. 

Each document set can have any number of documents, which in turn have arbitrary properties. The API
currently supports properties of two data types (strings and other documents), and properties can 
be either single-valued or multi-valued (lists). Adding more data types would be mostly routine. 

There are no reserved property names; each document is uniquely identified by a GUID key which is 
stored at the document level and does not take up a property slot. 

It currently does not support any query language. If an application needs to find a list of documents 
that match certain criteria, it currently must do this by examining document objects individually. 
If there is any interest in a real query facility, we can pursue this. 

CLASSES: 
- GlobalsDocDB - a static admin class
- GlDocSet  - a set of document stored in a single global
- GlDoc     - a document object, which can contain arbitrary single- 
              multi-valued properties, which can be strings or 
              other documents 
- frmBrowse - GUI form that displays the contents of a global in a multiline text 
			  box for debugging etc. Displays no more than 10 subscripts at any level. 

The client application can create multiple document sets. 


Global storage format: 

A document set is stored as a single global with a top level string 
value of "GlDocSet" to distinguish them from other globals in the 
same namespace. The following examples use the name "mydocset" as 
the name of the global. 

Each property of the doc is keyed by a name, and is identified by type
so that we can identify when a value should be interpreted as the GUID
of another document. 
Properties which can contain multiple values are identified by a flag. 

Documents are keyed by a GUID. 

Examples: 

mydocset = "GlDocSet"

-- DOCS: 
mydocset[<guid>] = "GlDoc"
mydocset[<guid>, "name", "type"] = "string"
mydocset[<guid>, "name", "value"] = "Homer Simpson"

mydocset[<guid>, "address", "type"] = "string"
mydocset[<guid>, "address"] = "Springfield"

mydocset[<guid>, "favorites", "type"] = "string"
mydocset[<guid>, "favorites", "value"] = ValueList{"Duff beer", "Forbidden donut"}

mydocset[<guid>, "spouse", "type"] = "doc"
mydocset[<guid>, "spouse", "value"] = <guid> (marge)

mydocset[<guid>, "children", "type"] = "doc"
mydocset[<guid>, "children", "value"] = ValueList{<bart guid>, <lisa guid>, <maggie guid>}

possible future schema for storing indexes: 
mydocset["index", "name", "Homer Simpson"] = <guid> 
 for unique values
mydocset["index", "address", "Springfield", <homer guid>] = ""
mydocset["index", "address", "Springfield", <marge guid>] = ""
 for multiples ... client app would have to designate which is which, and which ones need indexing, etc., 
  or else we need to enforce common cardinality (that is, if "children" is a multivalue for one document, 
  it must also be multivalue for all other uses) or else use the multi approach for everything. 

USAGE: 

Assuming that the Globals database is running, any client application can start by 
using static methods in GlobalsDocDb (a static class) to browse, open and create document 
sets. Once an application has a GlDocSet object it can its methods to open, 
manipulate and create documents, which are embodied by the GlDoc class. 

The GlDoc class exposes a variety of simple methods for managing documents. Most should be 
obvious. Of special note are: 

- public Guid DocUID - the GUID of the document (read-only)
- public GlDocSet DocSet - reference to the document set that contains the document

- public void Delete() - deletes the doc from the DB as well as removing references to that document 
from the containing document set object (GlDocSet)

- public void DeleteProperty(string property_name) - removes a property from the document
- public ValueType PropertyType(string property_name) - returns an enum identifying the data type of a property
- public bool EnsureType(string property_name, ValueType data_type) - checks whether the named property 
	is defined to be of the provided data type. If the property is not defined, EnsureType defines it. 
	Otherwise it returns true/false depending on whether the existing property matches the requested data type. 

- public List<string> PropertyNames() - all the defined property names

- string property values are then managed by GetString, SetString, GetStrings (for lists), AppendString, RemoveString. 
Parallel methods do the same for document-type properties. 

FUTURE FEATURES: 

- querying. Not sure whether it's worth it to try to write a flexible SQL parser to 
allow free form SQL text, like "select a, b from x join y on x.c = y.d ". 
 Since this API does not enforce any kind of schema on the documents, things are 
very ad-hoc and there is no indexing, but that would be the first step, but the client 
app might have to decide what properties need to be indexed. 

- transaction support, using Globals API methods like StartTransaction, Commit, Rollback, etc.

- connection/namespace management

