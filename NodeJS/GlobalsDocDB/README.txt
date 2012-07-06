Version 2.0:


This program is a document-database built on globals Cache in Node.js.

In the current build, documents and collections are the same type of object (called "documents).

The documents store their own information about the data structure (link to parent node,
 links to each subnode, the path to themselves from the parent node, and a link to the global
 itself, in particular). This allows for documents to be recursively held within each other.

This build is an improvement over the previous build because it allows docu-ception (documents
 inside documents inside documents...). Known weaknesses include not being able to move a document
 from one "branch" of the tree to another, and storing documents and non-documents separately.

NOTE: As in previous versions, the first four lines of code determine where the program "looks" for globals cache,
 and if it is unable to find it, it will encounter a bug.

NOTE 2: In this version, before setting data, you must:
	1)create a document
	2)promote that document to a collection with promoteToCollection()
	3)use addDatum() or addDocument() to add data or a document (respectively)



Version 2.1 Patch Notes:
This version now has a much more logical storage system for data and documents. It also features 100% less redundant variables!

DumpInfo() is also much improved in this version, using line breaks and indenting to make things much more readable.


Version 2.2 Patch Notes:

Now has a heavily-commented sample program which explains how the program works.

Fixed a bug where a collection name could not contain a space.

Improved the document constructor so that you can construct a document as a collection or in another document.