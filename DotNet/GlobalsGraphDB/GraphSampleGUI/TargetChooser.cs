using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Data;
using System.Drawing;
using System.Linq;
using System.Text;
using System.Windows.Forms;
using GlobalsGraphDB; 

namespace GraphSampleGUI
{
    public partial class TargetChooser : Form
    { 
        public GlGraphNode ChosenNode; 

        public TargetChooser()
        {
            InitializeComponent();
        }


        public void OfferAllNodes(GlGraph working_graph)
        {
            List<NodeWrapper> valid_targets = new List<NodeWrapper>();
            foreach (GlGraphNode loop_node in working_graph.AllNodes)
            {
                valid_targets.Add(new NodeWrapper(loop_node)); 
            }

            comboTargetNodes.DisplayMember = "NodeName";
            comboTargetNodes.DataSource = new BindingList<NodeWrapper>(valid_targets);
            comboTargetNodes.Refresh();

        }

        public void InitForNewEdge(GlGraph working_graph, GlGraphNode start_node)
        {
            List<NodeWrapper> valid_targets = new List<NodeWrapper>(); 
            foreach (GlGraphNode loop_node in working_graph.AllNodes)
            {
                if (start_node.FindConnectionTo(loop_node) == null)
                {
                    valid_targets.Add(new NodeWrapper(loop_node)); 
                }
            }

            comboTargetNodes.DisplayMember = "NodeName";
            comboTargetNodes.DataSource = new BindingList<NodeWrapper>(valid_targets);
            comboTargetNodes.Refresh();
             
        }

        private void buttonOK_Click(object sender, EventArgs e)
        {
            NodeWrapper chosen_node_wrapper = (NodeWrapper)comboTargetNodes.SelectedItem;
            ChosenNode = chosen_node_wrapper.GraphNode; 
            //StartingNode.ConnectTo(chosen_node_wrapper.GraphNode);
            this.Close(); 
        }

        private void comboTargetNodes_SelectedIndexChanged(object sender, EventArgs e)
        {
            buttonOK.Enabled = true; 
        }
    }
}
