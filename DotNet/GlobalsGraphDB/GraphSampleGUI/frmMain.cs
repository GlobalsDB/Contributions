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
    public partial class frmMain : Form
    {
        private const string GRAPH_CHOOSE_ENTRY = "choose ...";
        private GlGraph CurrentGraph = null;
        //private List<NodeWrapper> AllNodeWrappers = null; 

        private bool BuildingGraphList = false; 

        public frmMain()
        {
            InitializeComponent();
        }

        private void Form1_Load(object sender, EventArgs e)
        {
            RebuildGraphList(); 
        }

        private void RebuildGraphList()
        {
            
            BuildingGraphList = true; 
            List<string> all_graph_names = GlobalsGraphDB.GlobalsGraphAdmin.AllGraphs();
            all_graph_names.Add(GRAPH_CHOOSE_ENTRY); 

            comboGraphs.DataSource = all_graph_names;
            BuildingGraphList = false; 

            comboGraphs.SelectedItem = GRAPH_CHOOSE_ENTRY;

            comboGraphs.Enabled = (all_graph_names.Count > 1); 

        }

        private void RebuildNodeList()
        {
            if (CurrentGraph == null)
            {
                listNodes.DataSource = null;
            }
            else
            {
                listNodes.DisplayMember = "NodeName";
                
                List<NodeWrapper> working_list = new List<NodeWrapper>();
                foreach (GlGraphNode loop_node in CurrentGraph.AllNodes)
                {
                    working_list.Add(new NodeWrapper(loop_node));
                }
                                
                listNodes.DataSource = new BindingList<NodeWrapper>(working_list);
            }

            listNodes.Refresh(); 
        }

        
        private void RebuildEdgeList()
        {
            if (CurrentSelectedNodeWrapper() == null)
            {
                listEdges.DataSource = null;
            }
            else
            {
                listEdges.DisplayMember = "EdgeName";
                listEdges.DataSource = new BindingList<EdgeWrapper>(CurrentSelectedNodeWrapper().AllEdges()); 
            }

            listEdges.Refresh(); 
        }


        private NodeWrapper CurrentSelectedNodeWrapper()
        {
            if (listNodes.SelectedItem == null)
                return null;

            return (NodeWrapper)listNodes.SelectedItem; 
        }

        private EdgeWrapper CurrentSelectedEdgeWrapper()
        {
            if (listEdges.SelectedItem == null)
                return null;

            return (EdgeWrapper)listEdges.SelectedItem; 
        }

        #region graph events

        private void buttonNewGraph_Click(object sender, EventArgs e)
        {
            InputDialog new_name_dlg = new InputDialog();
            new_name_dlg.SetInstr("Name of new graph ...");
            new_name_dlg.ShowDialog();
            if (new_name_dlg.DialogResult == System.Windows.Forms.DialogResult.OK)
            {
                string new_name = new_name_dlg.TextValue.Trim();
                if (new_name == "") return;

                string err_msg = "";
                GlGraph new_graph = GlobalsGraphAdmin.CreateGraph(new_name, out err_msg);
                if (new_graph == null)
                {
                    MessageBox.Show(err_msg);
                    return; 
                }

                RebuildGraphList();
                comboGraphs.SelectedItem = new_name;
            }

        }


        private void buttonDeleteGraph_Click(object sender, EventArgs e)
        {
            if (MessageBox.Show("Really delete?", "Confirm", MessageBoxButtons.YesNo) == System.Windows.Forms.DialogResult.No)
                return; 

            if (CurrentGraph != null)
                CurrentGraph.Delete();

            RebuildGraphList();
        }

        private void comboGraphs_SelectedIndexChanged(object sender, EventArgs e)
        {
            if (BuildingGraphList) return; 

            if (comboGraphs.SelectedItem.ToString() == GRAPH_CHOOSE_ENTRY)
            {
                CurrentGraph = null;
                RebuildNodeList(); 
            }
            else
            {
                string err_msg = "";
                CurrentGraph = GlobalsGraphAdmin.OpenGraph(comboGraphs.SelectedItem.ToString(), out err_msg);
                if (CurrentGraph == null)
                    MessageBox.Show(err_msg);
                else
                    RebuildNodeList(); 
            }

            buttonDeleteGraph.Enabled = (CurrentGraph != null); 
        }
        #endregion

        #region node events
        private void buttonNewNode_Click(object sender, EventArgs e)
        {
            
            InputDialog new_name_dlg = new InputDialog();
            new_name_dlg.SetInstr("Name of new node ..."); // in this example we're assuming that "name" will be required for nodes
            new_name_dlg.ShowDialog();
            if (new_name_dlg.DialogResult == System.Windows.Forms.DialogResult.OK)
            { 
                NodeWrapper.AddGraphNode(CurrentGraph, new_name_dlg.TextValue.Trim());                
                RebuildNodeList(); 
            }
        }


        private void listNodes_SelectedIndexChanged(object sender, EventArgs e)
        {
            RebuildEdgeList(); 
        }

        private void buttonDeleteNode_Click(object sender, EventArgs e)
        {
            NodeWrapper node_to_delete = CurrentSelectedNodeWrapper();

            if (node_to_delete == null)
                return;

            if (!node_to_delete.DeleteNodeFromGraphSafely())                 
            {
                string confirm_msg = "That node has edges either incoming or outgoing. Delete anyway, along with all related edges?";
                if (MessageBox.Show(confirm_msg, "Confirm", MessageBoxButtons.OKCancel) == System.Windows.Forms.DialogResult.Cancel)
                    return;
                else
                {
                    node_to_delete.DeleteNodeFromGraphRegardless();
                }
            }

            RebuildNodeList();

        }

        private void buttonNodeProperties_Click(object sender, EventArgs e)
        {
            NodeWrapper node_to_edit = CurrentSelectedNodeWrapper();
            if (node_to_edit == null) return; 

            PropertiesEditor node_dialog = new PropertiesEditor();
            node_dialog.InitForNode(node_to_edit.GraphNode);
            node_dialog.ShowDialog();


        }


        private void buttonFindPath_Click(object sender, EventArgs e)
        {

            NodeWrapper current_node = CurrentSelectedNodeWrapper();
            if (current_node == null) return;
            
            TargetChooser destination_dlg = new TargetChooser();
            destination_dlg.OfferAllNodes(CurrentGraph); //, current_node.GraphNode);
            destination_dlg.ShowDialog();
            if (destination_dlg.DialogResult == System.Windows.Forms.DialogResult.OK)
            {
                if (destination_dlg.ChosenNode == current_node.GraphNode)
                    MessageBox.Show("That's the same node");
                else
                {
                    List<GlGraphEdge> short_path = CurrentGraph.ShortestPath(current_node.GraphNode, destination_dlg.ChosenNode);
                    if (short_path == null)
                        MessageBox.Show("No path found");
                    else
                    {
                        string path_desc = NodeWrapper.GetNodeName(current_node.GraphNode);
                        foreach (GlGraphEdge one_step in short_path)
                        {
                            path_desc += "->" + NodeWrapper.GetNodeName(one_step.TargetNode);                             
                        }
                        MessageBox.Show(path_desc); 
                    }
                }
            }

        }

        #endregion
        
        #region edge events

        private void buttonNewEdge_Click(object sender, EventArgs e)
        {
            NodeWrapper current_node = CurrentSelectedNodeWrapper(); 
            if (current_node == null) return; 

            TargetChooser new_edge_dlg = new TargetChooser();
            new_edge_dlg.InitForNewEdge(CurrentGraph, current_node.GraphNode);
            new_edge_dlg.ShowDialog();
            if (new_edge_dlg.DialogResult == System.Windows.Forms.DialogResult.OK)
            {
                current_node.GraphNode.ConnectTo(new_edge_dlg.ChosenNode);
                RebuildEdgeList();
            }
        }

        private void buttonDeleteEdge_Click(object sender, EventArgs e)
        {
            EdgeWrapper current_edge = CurrentSelectedEdgeWrapper();
            if (current_edge == null) return;

            current_edge.DeleteFromDB(); 
            RebuildEdgeList(); 
        }


        private void buttonEdgeProperties_Click(object sender, EventArgs e)
        {
            EdgeWrapper node_to_edit = CurrentSelectedEdgeWrapper();
            if (node_to_edit == null) return;

            PropertiesEditor node_dialog = new PropertiesEditor();
            node_dialog.InitForEdge(node_to_edit);
            node_dialog.ShowDialog();

        }
        #endregion

        
    }
}
