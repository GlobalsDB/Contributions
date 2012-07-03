using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using InterSystems.Globals;

namespace GlobalsGraphDB
{
    public class GlGraph
    {
        private NodeReference _GlNodeRef;

        internal NodeReference GlNodeRef
        {
            get { return _GlNodeRef; }
        }

        // graph nodes are keyed by a GUID which is not exposed outside of this API. 
        internal Dictionary<Guid, GlGraphNode> AllNodesByGuid = new Dictionary<Guid, GlGraphNode>(); 

        // graph constructor is marked INTERNAL because we want the client to use the 
        // static class factory method CreateGraph(), which redirects here after doing checks. 
        // For opening existing graphs, 
        // we provide another method called OpenGraph() which redirects here also. 
        internal GlGraph(string graph_name)
        {

            bool creating_new = !GlobalsGraphAdmin.AllGraphs().Contains(graph_name);

            _GlNodeRef = GlobalsGraphAdmin.ActiveConnection().CreateNodeReference(graph_name);

            if (creating_new)
                _GlNodeRef.Set(GlobalsGraphAdmin.GL_GRAPH_FLAG); // format identifier, causes persistence
            else
            {
                // opening an existing graph. Start by initializing the existing nodes
                string loop_node_guid = _GlNodeRef.NextSubscript(GlobalsGraphAdmin.GL_NODES_SUBSCRIPT, "");
                while (loop_node_guid != "")
                {
                    Guid new_guid = Guid.Empty;

                    if (Guid.TryParse(loop_node_guid, out new_guid))
                        AllNodesByGuid.Add(new_guid, new GlGraphNode(this, new_guid));
                    else
                        _GlNodeRef.Kill(GlobalsGraphAdmin.GL_NODES_SUBSCRIPT, loop_node_guid); // clean up bad data

                    loop_node_guid = _GlNodeRef.NextSubscript(GlobalsGraphAdmin.GL_NODES_SUBSCRIPT, loop_node_guid);
                } 

                // now loop again and load the edges
                foreach (GlGraphNode loop_node in AllNodes)
                {
                    loop_node.InitializeEdges(); 
                }
            }   
        }

        public GlGraphNode CreateNewNode()
        {
            GlGraphNode new_node = new GlGraphNode(this);
            AllNodesByGuid.Add(new_node.NodeUID, new_node); 
            return new_node; 
        }
         

        public string Name
        {
            get { return _GlNodeRef.GetName(); }
        }
         

        public List<GlGraphNode> AllNodes
        {
            get
            {
                return new List<GlGraphNode>(AllNodesByGuid.Values); 
            }
        }

        public void Delete()
        {
            _GlNodeRef.Kill();
        }

        // simple breadth-first search to find shortest path between two given nodes. 
        // The parameter "allow_trivial_path" instructs the routine as to whether every node should be considered 
        // to have a trivial 0-length path from itself to itself. If not, then the code will attempt to find a non-empty 
        // one in such cases (either a 1-length path represented by an a->a loop edge, or else a larger one).
        public List<GlGraphEdge> ShortestPath(GlGraphNode start_node, GlGraphNode end_node, bool allow_trivial_path = true)
        {
            if (start_node == end_node && allow_trivial_path)
                return new List<GlGraphEdge>(); 

            Dictionary<GlGraphNode, List<GlGraphEdge>> paths_by_node = new Dictionary<GlGraphNode, List<GlGraphEdge>>(); 
             
            List<GlGraphNode> current_generation = new List<GlGraphNode>();
            current_generation.Add(start_node);
            
            do
            {
                List<GlGraphNode> next_generation = new List<GlGraphNode>();
                foreach (GlGraphNode current_node in current_generation)
                {
                    foreach (GlGraphEdge next_edge in current_node.OutgoingEdges.Values)
                    {
                        GlGraphNode next_node = next_edge.TargetNode;
                        if (!paths_by_node.ContainsKey(next_node))
                        {
                            if (current_node == start_node)
                            {
                                // no path object yet
                                paths_by_node[next_node] = new List<GlGraphEdge>();
                            }
                            else
                            {
                                // base new node's path on previous steps
                                paths_by_node[next_node] = new List<GlGraphEdge>(paths_by_node[current_node]);
                            }
                            paths_by_node[next_node].Add(next_edge);

                            if (next_node == end_node)
                                return paths_by_node[next_node]; 

                            next_generation.Add(next_node); 
                        }
                    }
                }
                current_generation = next_generation; 
            }
            while (current_generation.Count > 0);

            return null; // none found
        }

        public int Distance(GlGraphNode start_node, GlGraphNode end_node)
        {
            try
            {
                return ShortestPath(start_node, end_node).Count;  
            }
            catch
            {
                return -1; 
            }
            
        }

    }
}
