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
    public partial class Form1 : Form
    {
        private const string DOCSET_CHOOSE_ENTRY = "choose ...";
        private GlDocSet CurrentDocSet = null;
        
        private bool BuildingDocSetList = false; 

        public Form1()
        {
            InitializeComponent();
        }

        private void Form1_Load(object sender, EventArgs e)
        {
            RebuildDocSetList();
        }

        private void RebuildDocSetList()
        {

            BuildingDocSetList = true;
            List<string> all_docset_names = GlobalsDocDB.GlobalsDocDB.AllDocSetNames();
            all_docset_names.Add(DOCSET_CHOOSE_ENTRY);

            comboDocSets.DataSource = all_docset_names;
            BuildingDocSetList = false;

            if (comboDocSets.SelectedItem.ToString() == DOCSET_CHOOSE_ENTRY)
                HandleDocSetChoice(); 
            else 
                comboDocSets.SelectedItem = DOCSET_CHOOSE_ENTRY;

            comboDocSets.Enabled = (all_docset_names.Count > 1);

        }

        private void RebuildDocList()
        {
            treeDocs.Nodes.Clear(); 
            if (CurrentDocSet != null)
            {
                foreach (GlDoc loop_doc in CurrentDocSet.AllDocs)
                {
                    Guid doc_guid = loop_doc.DocUID;
                    DocWrapper doc_wrap = new DocWrapper(loop_doc);
                    TreeNode docnode = treeDocs.Nodes.Add(doc_guid.ToString(), doc_wrap.DocName);
                    foreach (string loop_property in doc_wrap.CustomPropertyNames())
                    {
                        docnode.Nodes.Add(doc_guid.ToString() + "-" + loop_property, loop_property + ": " + doc_wrap.PropertyDisplayStr(loop_property)); 
                    }
                }
            }
            treeDocs.Refresh(); 

        }

        private GlDoc CurrentSelectedDoc()
        {
            if (treeDocs.SelectedNode == null)
                return null;

            Guid doc_guid = Guid.Empty; 
            if (!Guid.TryParse(treeDocs.SelectedNode.Name, out doc_guid))
                return null;

            return CurrentDocSet.FindDoc(doc_guid);  
        }

        #region doc set events


        private void buttonNewDocSet_Click(object sender, EventArgs e)
        {

            InputDialog new_name_dlg = new InputDialog();
            new_name_dlg.SetInstr("Name of new document set ...");
            new_name_dlg.ShowDialog();
            if (new_name_dlg.DialogResult == System.Windows.Forms.DialogResult.OK)
            {
                string new_name = new_name_dlg.TextValue.Trim();
                if (new_name == "") return;

                string err_msg = "";
                GlDocSet new_docset = GlobalsDocDB.GlobalsDocDB.CreateDocSet(new_name, out err_msg);
                if (new_docset == null)
                {
                    MessageBox.Show(err_msg);
                    return;
                }

                RebuildDocSetList();
                comboDocSets.SelectedItem = new_name;
            }
        }

        private void buttonDeleteDocSet_Click(object sender, EventArgs e)
        {
            if (MessageBox.Show("Really delete?", "Confirm", MessageBoxButtons.YesNo) == System.Windows.Forms.DialogResult.No)
                return;

            if (CurrentDocSet != null)
                CurrentDocSet.Delete();

            RebuildDocSetList();
            RebuildDocList(); 
        }

        private void HandleDocSetChoice()
        {
            if (comboDocSets.SelectedItem.ToString() == DOCSET_CHOOSE_ENTRY)
            {
                CurrentDocSet = null;
                RebuildDocList();
            }
            else
            {
                string err_msg = "";
                CurrentDocSet = GlobalsDocDB.GlobalsDocDB.OpenDocSet(comboDocSets.SelectedItem.ToString(), out err_msg);
                if (CurrentDocSet == null)
                    MessageBox.Show(err_msg);
                else
                    RebuildDocList();
            }

            buttonDeleteDocSet.Enabled = (CurrentDocSet != null);
            buttonBrowse.Enabled = (CurrentDocSet != null); 
        }

        private void comboDocSets_SelectedIndexChanged(object sender, EventArgs e)
        {

            if (BuildingDocSetList) return;
            HandleDocSetChoice(); 

        }
        #endregion


        #region doc events

        private void buttonNewDoc_Click(object sender, EventArgs e)
        {
            if (CurrentDocSet == null) return; 
            InputDialog new_name_dlg = new InputDialog();
            new_name_dlg.SetInstr("Name of new document ..."); // in this example we're assuming that "name" will be required for nodes
            new_name_dlg.ShowDialog();
            if (new_name_dlg.DialogResult == System.Windows.Forms.DialogResult.OK)
            {
                DocWrapper.CreateDocument(CurrentDocSet, new_name_dlg.TextValue.Trim());
                RebuildDocList();
            }

        }

        private void buttonDeleteDoc_Click(object sender, EventArgs e)
        {
            GlDoc current_doc = CurrentSelectedDoc();
            GlDoc ref_doc = current_doc.IsReferenced();
            if (ref_doc != null)
            {
                MessageBox.Show("Another document (" + DocWrapper.FindDocName(ref_doc) + ") has a reference to this document.");
                return; 
            }

            current_doc.Delete();
            RebuildDocList(); 
        }

        #endregion

        private void buttonEditDoc_Click(object sender, EventArgs e)
        {
            GlDoc current_doc = CurrentSelectedDoc();
            if (current_doc == null) return;

            frmDocEditor doc_editor = new frmDocEditor();
            doc_editor.InitForDoc(current_doc);
            doc_editor.ShowDialog();
            RebuildDocList(); 
        }

        private void treeDocs_AfterSelect(object sender, TreeViewEventArgs e)
        {
            
            Guid doc_guid = Guid.Empty; 

            buttonEditDoc.Enabled = Guid.TryParse(treeDocs.SelectedNode.Name, out doc_guid);
            buttonDeleteDoc.Enabled = buttonEditDoc.Enabled; 
        }
         

        private void buttonBrowse_Click(object sender, EventArgs e)
        {
            if (CurrentDocSet == null) return; 

            GlobalsDocDB.frmBrowse browser = new frmBrowse();
            browser.InitForDocSet(CurrentDocSet);
            browser.ShowDialog();
        }
         

    }
}
