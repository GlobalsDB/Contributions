using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;

namespace GlobalsGraphDB
{
    public class GlGraphEdge : GlGraphComponent 
    {
        internal GlGraphNode source_node;
        internal GlGraphNode target_node;

        // constructor is INTERNAL because client code should generate edges by using the graph node class's "ConnectTo" method
        internal GlGraphEdge(GlGraph parent_graph, GlGraphNode _source, GlGraphNode _target, bool create_in_db)
        {
            _ParentGraph = parent_graph;

            if (create_in_db)
                parent_graph.GlNodeRef.Set(GlobalsGraphAdmin.GL_EDGE_FLAG, GlobalsGraphAdmin.GL_EDGES_SUBSCRIPT, _source.NodeUID.ToString(), _target.NodeUID.ToString());

            source_node = _source;
            target_node = _target;
            Attach();
        }

        public void Delete()
        {
            _ParentGraph.GlNodeRef.Kill(GlobalsGraphAdmin.GL_EDGES_SUBSCRIPT, SourceNode.NodeUID.ToString(), TargetNode.NodeUID.ToString());
            Detach();
            source_node = null;
            target_node = null; 
        }

        internal void Attach()
        {
            source_node.OutgoingEdges.Add(target_node, this);
            target_node.IncomingEdges.Add(source_node, this);
        }

        internal void Detach()
        {
            source_node.OutgoingEdges.Remove(target_node);
            target_node.IncomingEdges.Remove(source_node);
        }

        public void ReOrient(GlGraphNode new_target)
        {
            Detach();
            target_node = new_target;
            Attach();
        }

        public GlGraphNode SourceNode
        {
            get
            {
                return source_node; 
            }
        }

        public GlGraphNode TargetNode
        {
            get
            {
                return target_node; 
            }
        }

        public override string GetCustomString(string property_name)
        {
            try
            {
                return _ParentGraph.GlNodeRef.GetString(GlobalsGraphAdmin.GL_EDGES_SUBSCRIPT, SourceNode.NodeUID.ToString(), TargetNode.NodeUID.ToString(), property_name);
            }
            catch
            {
                return null;
            }
        }

        public override void SetCustomString(string property_name, string value)
        {
            _ParentGraph.GlNodeRef.Set(value, GlobalsGraphAdmin.GL_EDGES_SUBSCRIPT, SourceNode.NodeUID.ToString(), TargetNode.NodeUID.ToString(), property_name);
        }

        public override void DeleteCustomString(string property_name)
        {
            _ParentGraph.GlNodeRef.Kill(GlobalsGraphAdmin.GL_EDGES_SUBSCRIPT, SourceNode.NodeUID.ToString(), TargetNode.NodeUID.ToString(), property_name);
        }

        public override List<string> NonemptyPropertyNames()
        {
            List<string> working_list = new List<string>();

            string loop_prop = _ParentGraph.GlNodeRef.NextSubscript(GlobalsGraphAdmin.GL_EDGES_SUBSCRIPT, SourceNode.NodeUID.ToString(), TargetNode.NodeUID.ToString(), "");
            while (loop_prop != "")
            {
                working_list.Add(loop_prop);
                loop_prop = _ParentGraph.GlNodeRef.NextSubscript(GlobalsGraphAdmin.GL_EDGES_SUBSCRIPT, SourceNode.NodeUID.ToString(), TargetNode.NodeUID.ToString(), loop_prop);
            }
            return working_list;
        }
    }
}
