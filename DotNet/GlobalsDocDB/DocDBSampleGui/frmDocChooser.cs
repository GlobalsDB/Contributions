using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Data;
using System.Drawing;
using System.Linq;
using System.Text;
using System.Windows.Forms;
using GlobalsDocDB; 

namespace DocDBSampleGui
{
    public partial class frmDocChooser : Form
    {
        public frmDocChooser()
        {
            InitializeComponent();
        }

        public void InitForDocSet(GlDocSet working_set)
        {
            List<DocWrapper> wrapper_set = new List<DocWrapper>(); 
            foreach (GlDoc loop_doc in working_set.AllDocs)
            {
                wrapper_set.Add(new DocWrapper(loop_doc)); 
            }

            comboDocs.DisplayMember = "DocName";
            comboDocs.DataSource = new BindingList<DocWrapper>(wrapper_set); 
        }

        public GlDoc ChosenDoc()
        {
            if (comboDocs.SelectedItem == null)
                return null;

            DocWrapper chosen_wrapper = (DocWrapper)comboDocs.SelectedItem;
            return chosen_wrapper._api_doc; 
        }

        private void frmDocChooser_Load(object sender, EventArgs e)
        {

        }
    }
}
