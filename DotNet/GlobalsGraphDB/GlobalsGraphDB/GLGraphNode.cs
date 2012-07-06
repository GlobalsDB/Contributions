using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using InterSystems.Globals; 

namespace GlobalsGraphDB
{
    public class GlGraphNode : GlGraphComponent 
    {
        private Guid _NodeUID = Guid.Empty;

        // all outgoing edges, keyed by destination (target) node 
        internal Dictionary<GlGraphNode, GlGraphEdge> OutgoingEdges = new Dictionary<GlGraphNode, GlGraphEdge>();

        // all incoming edges, keyed by source node
        internal Dictionary<GlGraphNode, GlGraphEdge> IncomingEdges = new Dictionary<GlGraphNode, GlGraphEdge>();

        // create a new node on a graph
        internal GlGraphNode(GlGraph parent_graph)
        {
            _NodeUID = Guid.NewGuid();
            _ParentGraph = parent_graph;
            _ParentGraph.GlNodeRef.Set(GlobalsGraphAdmin.GL_NODE_FLAG, GlobalsGraphAdmin.GL_NODES_SUBSCRIPT, _NodeUID.ToString());
        }
         

        // build a graphnode class around an existing graph node in the DB; used during initialization
        internal GlGraphNode(GlGraph parent_graph, Guid node_guid)
        {
            _NodeUID = node_guid; 
            _ParentGraph = parent_graph; 
        }

        // create edge objects during node object setup
        internal void InitializeEdges()
        {
            string loop_node_guid_str = _ParentGraph.GlNodeRef.NextSubscript(GlobalsGraphAdmin.GL_EDGES_SUBSCRIPT, _NodeUID.ToString(), "");
            while (loop_node_guid_str != "")
            {
                Guid loop_node_guid = Guid.Empty;
                bool is_valid = false; 
                if (Guid.TryParse(loop_node_guid_str, out loop_node_guid))
                {
                    if (_ParentGraph.AllNodesByGuid.ContainsKey(loop_node_guid))
                    {
                        is_valid = true; 
                        GlGraphEdge new_edge = new GlGraphEdge(_ParentGraph, this, _ParentGraph.AllNodesByGuid[loop_node_guid], false);
                        // the GlGraphEdge object automatically registers itself as a member of this node's OutgoingEdges collection
                        // as well as the TARGET node's IncomingEdges collection 
                    }
                }

                if (!is_valid)
                {
                    // clean up bogus data
                    _ParentGraph.GlNodeRef.Kill(GlobalsGraphAdmin.GL_EDGES_SUBSCRIPT, _NodeUID.ToString(), loop_node_guid_str);
                }

                loop_node_guid_str = _ParentGraph.GlNodeRef.NextSubscript(GlobalsGraphAdmin.GL_EDGES_SUBSCRIPT, _NodeUID.ToString(), loop_node_guid_str);
            }
        }

        // deletes the node If the parameter "safe_mode" is true, it first checks whether 
        // there are any incoming OR outgoing edges and, if there are, it quits and returns false. 
        // Otherwise it deletes all such edges before deleting the node. 
        public bool Delete(bool safe_mode)
        {

            if (safe_mode && (IncomingEdges.Count > 0 || OutgoingEdges.Count > 0))
                return false;

            foreach (GlGraphEdge incoming_edge in IncomingEdges.Values)
                incoming_edge.Delete(); // deletes from the DB; removes it from this node's incoming list AND the source node's outgoing list

            foreach (GlGraphEdge outgoing_edge in OutgoingEdges.Values)
                outgoing_edge.Delete(); // ditto 

            _ParentGraph.AllNodesByGuid.Remove(this.NodeUID);
            _ParentGraph.GlNodeRef.Kill(GlobalsGraphAdmin.GL_NODES_SUBSCRIPT, _NodeUID.ToString()); 

            return true; 
        }

        public override string GetCustomString(string property_name)
        {
            try
            {
                return _ParentGraph.GlNodeRef.GetString(GlobalsGraphAdmin.GL_NODES_SUBSCRIPT, _NodeUID.ToString(), property_name); 
            }
            catch
            {
                return null; 
            }         
        }

        public override void SetCustomString(string property_name, string value)
        {
            _ParentGraph.GlNodeRef.Set(value, GlobalsGraphAdmin.GL_NODES_SUBSCRIPT, _NodeUID.ToString(), property_name); 
        }

        public override void DeleteCustomString(string property_name)
        {
            _ParentGraph.GlNodeRef.Kill(GlobalsGraphAdmin.GL_NODES_SUBSCRIPT, _NodeUID.ToString(), property_name); 
        }

        // list of all the property names with values
        public override List<string> NonemptyPropertyNames()
        {
            List<string> working_list = new List<string>();

            string loop_prop = _ParentGraph.GlNodeRef.NextSubscript(GlobalsGraphAdmin.GL_NODES_SUBSCRIPT, _NodeUID.ToString(), "");
            while (loop_prop != "")
            {
                working_list.Add(loop_prop);
                loop_prop = _ParentGraph.GlNodeRef.NextSubscript(GlobalsGraphAdmin.GL_NODES_SUBSCRIPT, _NodeUID.ToString(), loop_prop);
            }
            return working_list; 
        }

        internal Guid NodeUID
        {
            get
            {
                return _NodeUID;
            }
        }

        // creates a connection (edge) to the specified target node. If there 
        // is already a connection, does nothing and returns null. 
        public GlGraphEdge ConnectTo(GlGraphNode target_node)
        { 
            if (FindConnectionTo(target_node) != null) 
                return null;
            else 
                return new GlGraphEdge(_ParentGraph, this, target_node, true);             
        }

        public int ReferenceCount()
        {
            return IncomingEdges.Count;
        }

        // all OUTGOING edges from this node
        public List<GlGraphEdge> AllEdges()
        {
            return new List<GlGraphEdge>(OutgoingEdges.Values); 
        }

        // returns an edge pointing from the current node to the specified
        // node, IF ONE EXISTS. 
        public GlGraphEdge FindConnectionTo(GlGraphNode target_node)
        {
            if (OutgoingEdges.ContainsKey(target_node))
                return OutgoingEdges[target_node];
            else
                return null;
        }
    }
}
