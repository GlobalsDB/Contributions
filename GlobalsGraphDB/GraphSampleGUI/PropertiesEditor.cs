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
    public partial class PropertiesEditor : Form
    {
        private GlGraphNode working_node;
        private GlGraphEdge working_edge;

        private GlGraphComponent working_component
        {
            get
            {
                if (working_node != null)
                    return working_node;
                else
                    return working_edge;  
            }
        }

        public PropertiesEditor()
        {
            InitializeComponent();
        }

        private void RebuildList()
        {
            List<string> propnames = null;

            propnames = working_component.NonemptyPropertyNames();
            if (working_node != null)
            {
                propnames.Remove(NodeWrapper.NODE_NAME_PROPERTY);
            } 

            comboProps.DataSource = propnames;
            labelPropValue.Text = ""; 
            comboProps.Refresh();  
        }

        public void InitForNode(GlGraphNode _working_node)
        {
            working_node = _working_node;
            lblName.Text = "Node name: " + working_node.GetCustomString(NodeWrapper.NODE_NAME_PROPERTY);
            RebuildList(); 
        }

        public void InitForEdge(EdgeWrapper current_edge_wrapper)
        {
            working_edge = current_edge_wrapper.GraphEdge;
            lblName.Text = "Edge: " + current_edge_wrapper.EdgeName;
            RebuildList(); 
        }

        private void buttonNew_Click(object sender, EventArgs e)
        {
            InputDialog new_prop_stuff = new InputDialog(); 
            new_prop_stuff.SetInstr("Name of new property ...");
            new_prop_stuff.ShowDialog();
            if (new_prop_stuff.DialogResult != System.Windows.Forms.DialogResult.OK)
                return;

            string new_prop_name = new_prop_stuff.TextValue;
            if (working_component.NonemptyPropertyNames().Contains(new_prop_name))
            {
                MessageBox.Show("There's already a property with that name.");
                return; 
            }

            new_prop_stuff = new InputDialog();
            new_prop_stuff.SetInstr("Value of new property ...");
            new_prop_stuff.ShowDialog();
            if (new_prop_stuff.DialogResult != System.Windows.Forms.DialogResult.OK)
                return;

            working_component.SetCustomString(new_prop_name, new_prop_stuff.TextValue);
            RebuildList(); 

        }

        private void DisplayPropVal()
        {
            string propname = comboProps.SelectedItem.ToString();
            labelPropValue.Text = working_component.GetCustomString(propname); 
        }

        private void comboProps_SelectedIndexChanged(object sender, EventArgs e)
        {
            DisplayPropVal(); 
        }

        private void buttonEdit_Click(object sender, EventArgs e)
        {
            string propname = comboProps.SelectedItem.ToString();
            InputDialog new_prop_val_dlg = new InputDialog();
            new_prop_val_dlg.SetInstr("New value property ...");
            new_prop_val_dlg.ShowDialog();
            if (new_prop_val_dlg.DialogResult != System.Windows.Forms.DialogResult.OK)
                return;

            working_component.SetCustomString(propname, new_prop_val_dlg.TextValue);
            DisplayPropVal(); 

        }

        private void buttonDelete_Click(object sender, EventArgs e)
        {
            string propname = comboProps.SelectedItem.ToString();
            working_component.DeleteCustomString(propname);
            RebuildList(); 
        }
    }
}
