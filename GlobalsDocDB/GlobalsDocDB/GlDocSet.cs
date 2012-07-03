using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using InterSystems.Globals;

namespace GlobalsDocDB
{
    public class GlDocSet
    {

        private NodeReference _GlNodeRef;

        internal NodeReference GlNodeRef
        {
            get { return _GlNodeRef; }
        }

        // doc nodes are keyed by a GUID
        internal Dictionary<Guid, GlDoc> AllDocsByGuid = new Dictionary<Guid, GlDoc>(); 

        // constructor is marked INTERNAL because we want the client to use the 
        // static class factory method CreateDocSet(), which redirects here after doing checks. 
        // For opening existing doc sets, 
        // we provide another method called OpenDocSet() which redirects here also. 

        internal GlDocSet(string docset_name)
        {
            bool creating_new = !GlobalsDocDB.AllDocSetNames().Contains(docset_name);

            _GlNodeRef = GlobalsDocDB.ActiveConnection().CreateNodeReference(docset_name);

            if (creating_new)
                _GlNodeRef.Set(GlobalsDocDB.GL_DOCS_FLAG); // format identifier, causes persistence
            else
            {
                // opening an existing doc set. Start by initializing the existing nodes
                string loop_node_guid = _GlNodeRef.NextSubscript("");
                while (loop_node_guid != "")
                {
                    Guid new_guid = Guid.Empty;

                    if (Guid.TryParse(loop_node_guid, out new_guid))
                        AllDocsByGuid.Add(new_guid, new GlDoc(this, new_guid));
                    else
                        _GlNodeRef.Kill(loop_node_guid); // clean up bad data

                    loop_node_guid = _GlNodeRef.NextSubscript(loop_node_guid);
                }
                 
            }   
        }


        public GlDoc CreateNewDoc()
        {
            GlDoc new_doc = new GlDoc(this);
            AllDocsByGuid.Add(new_doc.DocUID, new_doc);
            return new_doc;
        }

        internal void MarkDocAsDeleted(GlDoc doomed_doc)
        {
            AllDocsByGuid.Remove(doomed_doc.DocUID); 
        }


        public string Name
        {
            get { return _GlNodeRef.GetName(); }
        }

        public GlDoc FindDoc(Guid doc_guid)
        {
            try
            {
                return AllDocsByGuid[doc_guid]; 
            }
            catch
            {
                return null;
            }
        }

        public List<GlDoc> AllDocs
        {
            get
            {
                return new List<GlDoc>(AllDocsByGuid.Values);
            }
        }

        public void Delete()
        {
            _GlNodeRef.Kill();
            AllDocsByGuid.Clear();
            _GlNodeRef = null; 
        }


    }
}
