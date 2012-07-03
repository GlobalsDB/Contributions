using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using GlobalsGraphDB; 

namespace GraphSampleGUI
{
    /* This class is an example of a wrapper that a client application might need to 
     * write around the API's graph edge class. The specific reason for this in the 
     * current example is that we want to display a list of graph edge objects in a 
     * ListBox control, which uses a property name to determine the text to display. 
     * In a given client application this might be anything, based on some complex 
     * business logic. The client might use this kind of wrapper class to enforce the 
     * creation of required properties, uniqueness, etc. etc. as appropriate. 
     * 
     * This logic could arguably be put into the graph db api, but that's currently 
     * intended as a generic graph db api, so this class is more specific to a business 
     * logic example. 
     */
    public class EdgeWrapper
    {
        private GlGraphEdge _api_graph_edge;

        public GlGraphEdge GraphEdge
        { get { return _api_graph_edge; } }

        public EdgeWrapper(GlGraphEdge existing_edge_from_db)
        {
            _api_graph_edge = existing_edge_from_db; 
        }

        public string EdgeName
        {
            get
            {

                string source_node_name = _api_graph_edge.SourceNode.GetCustomString(NodeWrapper.NODE_NAME_PROPERTY);
                string target_node_name = _api_graph_edge.TargetNode.GetCustomString(NodeWrapper.NODE_NAME_PROPERTY);

                return source_node_name
                    + " to "
                    + target_node_name;
            }
        }

        public void DeleteFromDB()
        {
            _api_graph_edge.Delete(); 
        }
    }
}
