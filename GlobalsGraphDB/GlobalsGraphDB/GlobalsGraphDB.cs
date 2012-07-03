/*
 * This is a sample .NET API for the InterSystems Globals database, simulating a Graph Node database. 
 * See README.txt for more info. 
 * 
 * * */

using System;
using System.Collections.Generic;
//using System.Linq;
using System.Text;
using InterSystems.Globals;

namespace GlobalsGraphDB
{
    public static class GlobalsGraphAdmin
    {
        internal const string GL_GRAPH_FLAG = "GlGraph";
        internal const string GL_NODE_FLAG = "GlNode";
        internal const string GL_EDGE_FLAG = "GlEdge";
        internal const string GL_NODES_SUBSCRIPT = "nodes";
        internal const string GL_EDGES_SUBSCRIPT = "edges";

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
            catch //(Exception e)
            {
                return null;
            }
        }

        // compile a list of all global names in the current namespace, either 
        // just for "graph" globals, or for everything. 
        private static List<string> AllGlobals(bool just_the_graphs)
        {
            List<string> working_list = new List<string>();
            GlobalsDirectory all_node_refs = ActiveConnection().CreateGlobalsDirectory();
            all_node_refs.Refresh(); 

            string loop_name = all_node_refs.NextGlobalName();
            while (loop_name != "")
            {
                NodeReference loop_node = ActiveConnection().CreateNodeReference(loop_name);
                if (loop_node.GetString() == GL_GRAPH_FLAG || !just_the_graphs) // if we only want graphs, is it a "graph" global? 
                    working_list.Add(loop_name);

                loop_name = all_node_refs.NextGlobalName();
            }

            return working_list;
        }


        public static List<string> AllGraphs()
        {
            return AllGlobals(true); 
        }

        public static GlGraph CreateGraph(string graph_name, out string error_message)
        {
            if (AllGraphs().Contains(graph_name))
            {
                error_message = "A graph already exists with this name.";
                return null; 
            }

            // now check whether a non-graph global exists with that name
            if (AllGlobals(false).Contains(graph_name))
            {
                error_message = "Another global (not a graph) exists with this name in the current namespace.";
                return null; 
            }

            error_message = ""; 
            return new GlGraph(graph_name); 

        }

        public static GlGraph OpenGraph(string graph_name, out string error_message)
        {
            if (AllGraphs().Contains(graph_name))
            {
                error_message = "";
                return new GlGraph(graph_name); 
            }

            error_message = "Graph not found.";
            return null; 
        }
    }




}
