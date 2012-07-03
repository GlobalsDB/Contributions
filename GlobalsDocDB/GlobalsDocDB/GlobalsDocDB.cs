
/* see README.txt for info. 
 */


using System;
using System.Collections.Generic;
//using System.Linq;
using System.Text;
using InterSystems.Globals;

namespace GlobalsDocDB
{
    public static class GlobalsDocDB
    {
        public const string GL_DOCS_FLAG = "GlDocSet";


        internal static Connection ActiveConnection()
        {
            try
            {
                // if the connection context already has a connection, this 
                // will be returned. Otherwise a new one is created. 
                // Current problem: if Globals isn't running, this HANGS. Can we 
                // check whether it's running, or do we have to create a worker thread 
                // to check ...? Let's not get involved with multithreading for 
                // simple proof-of-concept examples if possible. 
                Connection working_conn = ConnectionContext.GetConnection();

                // ensure that the connection is connected (active)
                if (!working_conn.IsConnected())
                {
                    working_conn.Connect();

                }

                return working_conn;
            }
            catch
            {
                return null;
            }
        }

        // compile a list of all global names in the current namespace, either 
        // just for "graph" globals, or for everything. 
        public static List<string> AllGlobals(bool just_the_docsets)
        {
            List<string> working_list = new List<string>();
            GlobalsDirectory all_node_refs = ActiveConnection().CreateGlobalsDirectory();
            all_node_refs.Refresh();

            string loop_name = all_node_refs.NextGlobalName();
            while (loop_name != "")
            {
                NodeReference loop_node = ActiveConnection().CreateNodeReference(loop_name);
                if (loop_node.GetString() == GL_DOCS_FLAG || !just_the_docsets) // if we only want graphs, is it a "graph" global? 
                    working_list.Add(loop_name);

                loop_name = all_node_refs.NextGlobalName();
            }

            return working_list;
        }


        public static List<string> AllDocSetNames()
        {
            return AllGlobals(true);
        }

        public static List<GlDocSet> AllDocSets()
        {
            List<GlDocSet> working_list = new List<GlDocSet>(); 
            foreach (string ds_name in AllDocSetNames())
            {
                string err_msg = "";
                GlDocSet new_ds = CreateDocSet(ds_name, out err_msg); 
                if (err_msg == "")
                    working_list.Add(new_ds); 
            }
            return working_list; 
        }

        public static GlDocSet CreateDocSet(string docset_name, out string error_message)
        {
            if (AllDocSetNames().Contains(docset_name))
            {
                error_message = "A document set already exists with this name.";
                return null;
            }

            // now check whether a non-graph global exists with that name
            if (AllGlobals(false).Contains(docset_name))
            {
                error_message = "Another global (not a document set) exists with this name in the current namespace.";
                return null;
            }

            error_message = "";
            return new GlDocSet(docset_name);

        }

        public static GlDocSet OpenDocSet(string docset_name, out string error_message)
        {
            if (AllDocSetNames().Contains(docset_name))
            {
                error_message = "";
                return new GlDocSet(docset_name);
            }

            error_message = "Document set not found.";
            return null;
        }
         
    }
}
