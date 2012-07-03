using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Data;
using System.Drawing;
using System.Linq;
using System.Text;
using System.Windows.Forms;
using InterSystems.Globals;

namespace GlobalsDocDB
{
    public partial class frmBrowse : Form
    {
        public frmBrowse()
        {
            InitializeComponent();
        }

        private NodeReference working_noderef; 

        private void frmBrowse_Load(object sender, EventArgs e)
        {
            ResizeText(); 
        }

        public void InitForGlobalName(string name)
        {
            working_noderef = GlobalsDocDB.ActiveConnection().CreateNodeReference(name);
            DisplayForSubscripts(); 
        }

        public void InitForDocSet(GlDocSet working_docset)
        {
            working_noderef = working_docset.GlNodeRef;
            DisplayForSubscripts(); 
 
        }

        public void DisplayForSubscripts(params object[] working_subscripts)
        {
            string prefix = Environment.NewLine + working_noderef.GetName();
            if (working_subscripts.Length > 0)
            {
                for (int ix = 0; ix < working_subscripts.Length; ix++)
                {
                    prefix += "[" + working_subscripts[ix].ToString() + "]"; 
                }
            }
            prefix += " = "; 
            
            
            try
            {
                string current_val = working_noderef.GetString(working_subscripts); 
                if (current_val != null)
                    if (current_val != "")
                        textDocContents.AppendText(prefix + current_val); 

            }
            catch
            { }

            try
            {
                if (working_noderef.HasSubnodes(working_subscripts))
                {
                    int count = 0;
                    object[] sub_params = new object[working_subscripts.Length + 1];
                    for (int loop_index = 0; loop_index < working_subscripts.Length; loop_index++)
                    {
                        sub_params[loop_index] = working_subscripts[loop_index]; 
                    }

                    sub_params[working_subscripts.Length] = "";

                    string loop_sub = working_noderef.NextSubscript(sub_params);

                    while (loop_sub != "")
                    {
                        count++;
                        if (count > 10)
                        {
                            textDocContents.AppendText(Environment.NewLine + "..."); 
                            break; 
                        }
                        sub_params[working_subscripts.Length] = loop_sub;
                        DisplayForSubscripts(sub_params);

                        loop_sub = working_noderef.NextSubscript(sub_params);
                    }
                }
            }
            catch //(Exception e)
            {
                MessageBox.Show("Error displaying data");
                return; 
            }
        }

        private void ResizeText()
        {
            textDocContents.Width = this.DisplayRectangle.Width - (textDocContents.Left * 2);
            textDocContents.Height = this.DisplayRectangle.Height - (textDocContents.Top * 2); 
        }

        private void frmBrowse_Resize(object sender, EventArgs e)
        {
            ResizeText(); 
        }

    }
}
