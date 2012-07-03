using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using GlobalsGraphDB; 

/* This class is an example of a wrapper that a client application might need to 
 * write around the API's graph node class. The specific reason for this in the 
 * current example is that we want to display a list of graph node objects in a 
 * ListBox control, which uses a property name to determine the text to display. 
 * In a given client application this might be anything, based on some complex 
 * business logic. The client might use this kind of wrapper class to enforce the 
 * creation of required properties, uniqueness, etc. etc. as appropriate. 
 * 
 * This logic could arguably be put into the graph db api, but that's currently 
 * intended as a generic graph db api, so this class is more specific to a business 
 * logic example. 
 */
namespace GraphSampleGUI
{
    public class NodeWrapper
    {
        private GlGraphNode _api_graph_node;
        public const string NODE_NAME_PROPERTY = "name";

        public GlGraphNode GraphNode
        { get { return _api_graph_node; } }

        public static string GetNodeName(GlGraphNode any_node)
        {
            return any_node.GetCustomString(NODE_NAME_PROPERTY); 
        }

        public static void AddGraphNode(GlGraph parent_graph, string new_node_name)
        { 
            // biz rule
            parent_graph.CreateNewNode().SetCustomString(NODE_NAME_PROPERTY, new_node_name);
        }


        // constructors 

        public NodeWrapper(GlGraphNode existing_node_from_db)
        {
            _api_graph_node = existing_node_from_db;
        }
         

        // name property for display in list box
        public string NodeName
        {
            get
            {
                return GetNodeName(_api_graph_node); 
            }
        }

        public bool DeleteNodeFromGraphSafely()
        {
            return _api_graph_node.Delete(true); 
        }

        public void DeleteNodeFromGraphRegardless()
        {
            _api_graph_node.Delete(false); 
        }

        public List<EdgeWrapper> AllEdges()
        {
            List<EdgeWrapper> working_list = new List<EdgeWrapper>(); 
            foreach (GlGraphEdge loop_edge in _api_graph_node.AllEdges())
            {
                working_list.Add(new EdgeWrapper(loop_edge)); 
            }
            return working_list; 
        }

    }
}
