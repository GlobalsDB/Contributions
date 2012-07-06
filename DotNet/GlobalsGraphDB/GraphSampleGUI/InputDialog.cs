using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Data;
using System.Drawing;
using System.Linq;
using System.Text;
using System.Windows.Forms;

namespace GraphSampleGUI
{
    public partial class InputDialog : Form
    {
        public InputDialog()
        {
            InitializeComponent();
        }

        private void buttonCancel_Click(object sender, EventArgs e)
        {
            this.Close(); 
        }

        public void SetInstr(string Instructions)
        {
            labelInstructions.Text = Instructions; 
        }

        public string TextValue
        {
            get
            {
                return textInput.Text; 
            }
        }

        private void textInput_KeyPress(object sender, KeyPressEventArgs e)
        {
            // respond to ENTER key
            if (e.KeyChar == (char)13)
            {
                this.DialogResult = System.Windows.Forms.DialogResult.OK;
                this.Close(); 
            }
        }
    }
}
