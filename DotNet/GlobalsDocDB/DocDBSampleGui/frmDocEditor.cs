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
    public partial class frmDocEditor : Form
    {
        private GlDoc CurrentDoc; 

        public frmDocEditor()
        {
            InitializeComponent();
        }

        public void InitForDoc(GlDoc working_doc)
        {
            CurrentDoc = working_doc; 
        }

        private void frmDocEditor_Load(object sender, EventArgs e)
        {
            comboProperty.Items.Add("Address"); // simple string
            comboProperty.Items.Add("Favorite color"); // simple string
            comboProperty.Items.Add("Spouse");  //simple doc 
            comboProperty.Items.Add("Children"); // multiple docs
            comboProperty.Items.Add("Favorite foods");  // multiple strings
        }

        private string ChosenPropStr()
        {
            return comboProperty.SelectedItem.ToString(); 
        }

        private void DisplayValues()
        {
            labelPropVal.Visible = false;
            panelMulti.Visible = false;

            string chosen_prop = ChosenPropStr();

            if (chosen_prop == "") return;

            switch (chosen_prop)
            {
                case "Address":
                case "Favorite color":
                    labelPropVal.Visible = true;
                    labelPropVal.Text = CurrentDoc.GetString(chosen_prop);
                    break;
                case "Spouse":
                    labelPropVal.Visible = true;
                    GlDoc target_doc = CurrentDoc.GetDoc(chosen_prop);
                    if (target_doc == null)
                        labelPropVal.Text = "";
                    else
                        labelPropVal.Text = DocWrapper.FindDocName(target_doc);
                    break;
                case "Children":
                    panelMulti.Visible = true;
                    List<DocWrapper> children = new List<DocWrapper>();
                    foreach (GlDoc child_doc in CurrentDoc.GetDocs(chosen_prop))
                    {
                        children.Add(new DocWrapper(child_doc));
                    }
                    listPropVals.DisplayMember = "DocName";
                    listPropVals.DataSource = new BindingList<DocWrapper>(children);
                    break;
                case "Favorite foods":
                    panelMulti.Visible = true;
                    List<string> yum = CurrentDoc.GetStrings(chosen_prop);
                    listPropVals.DisplayMember = "";
                    try
                    {
                        listPropVals.DataSource = new BindingList<string>(yum);  // ? 
                    }
                    catch { }
                    break;
                default:
                    MessageBox.Show("oops");
                    break;
            }

            buttonEdit.Visible = labelPropVal.Visible;
            //buttonClear.Visible = labelPropVal.Visible; 
        }

        private void comboProperty_SelectedIndexChanged(object sender, EventArgs e)
        {
            DisplayValues(); 
        }

        private void buttonCancel_Click(object sender, EventArgs e)
        {
            this.Close(); 
        }

        private void buttonClear_Click(object sender, EventArgs e)
        {
            CurrentDoc.DeleteProperty(ChosenPropStr());
            labelPropVal.Text = ""; 
        }

        private void buttonEdit_Click(object sender, EventArgs e)
        {

            switch (ChosenPropStr())
            {
                case "Address":
                case "Favorite color":                    
                    InputDialog new_name_dlg = new InputDialog();
                    new_name_dlg.SetInstr("Value ...");
                    new_name_dlg.ShowDialog();
                    if (new_name_dlg.DialogResult == System.Windows.Forms.DialogResult.OK)
                    {
                        string new_val = new_name_dlg.TextValue.Trim();
                        CurrentDoc.SetString(ChosenPropStr(), new_val);
                    }
                    break;

                case "Spouse":
                    frmDocChooser doc_chooser = new frmDocChooser();
                    doc_chooser.InitForDocSet(CurrentDoc.DocSet);
                    doc_chooser.ShowDialog();
                    if (doc_chooser.DialogResult == System.Windows.Forms.DialogResult.OK)
                    {
                        GlDoc doc_choice = doc_chooser.ChosenDoc();
                        if (doc_choice != null)
                        {
                            CurrentDoc.SetDoc(ChosenPropStr(), doc_choice);
                        }
                    }
                    break;
            }

            DisplayValues(); 
        }

        private void buttonAdd_Click(object sender, EventArgs e)
        {
            switch (ChosenPropStr())
            {
                case "Children":
                    frmDocChooser doc_chooser = new frmDocChooser();
                    doc_chooser.InitForDocSet(CurrentDoc.DocSet);
                    doc_chooser.ShowDialog();
                    if (doc_chooser.DialogResult == System.Windows.Forms.DialogResult.OK)
                    {
                        GlDoc doc_choice = doc_chooser.ChosenDoc();
                        if (doc_choice != null)
                        {
                            CurrentDoc.AppendDoc(ChosenPropStr(), doc_choice);
                            DisplayValues(); 
                        }
                    }
                    break;
                case "Favorite foods":
                    InputDialog new_name_dlg = new InputDialog();
                    
                    new_name_dlg.SetInstr("Value ...");
                    new_name_dlg.ShowDialog();
                    if (new_name_dlg.DialogResult == System.Windows.Forms.DialogResult.OK)
                    {
                        string new_val = new_name_dlg.TextValue.Trim();
                        if (new_val != "")
                        {
                            CurrentDoc.AppendString(ChosenPropStr(), new_val);
                            DisplayValues(); 
                        }
                    }
                    break;
            }
        }
    }
}
