namespace DocDBSampleGui
{
    partial class Form1
    {
        /// <summary>
        /// Required designer variable.
        /// </summary>
        private System.ComponentModel.IContainer components = null;

        /// <summary>
        /// Clean up any resources being used.
        /// </summary>
        /// <param name="disposing">true if managed resources should be disposed; otherwise, false.</param>
        protected override void Dispose(bool disposing)
        {
            if (disposing && (components != null))
            {
                components.Dispose();
            }
            base.Dispose(disposing);
        }

        #region Windows Form Designer generated code

        /// <summary>
        /// Required method for Designer support - do not modify
        /// the contents of this method with the code editor.
        /// </summary>
        private void InitializeComponent()
        {
            this.groupBox1 = new System.Windows.Forms.GroupBox();
            this.buttonBrowse = new System.Windows.Forms.Button();
            this.comboDocSets = new System.Windows.Forms.ComboBox();
            this.buttonNewDocSet = new System.Windows.Forms.Button();
            this.buttonNewDoc = new System.Windows.Forms.Button();
            this.buttonDeleteDoc = new System.Windows.Forms.Button();
            this.buttonEditDoc = new System.Windows.Forms.Button();
            this.treeDocs = new System.Windows.Forms.TreeView();
            this.buttonDeleteDocSet = new System.Windows.Forms.Button();
            this.groupBox1.SuspendLayout();
            this.SuspendLayout();
            // 
            // groupBox1
            // 
            this.groupBox1.Controls.Add(this.buttonBrowse);
            this.groupBox1.Controls.Add(this.buttonDeleteDocSet);
            this.groupBox1.Controls.Add(this.comboDocSets);
            this.groupBox1.Controls.Add(this.buttonNewDocSet);
            this.groupBox1.Location = new System.Drawing.Point(12, 12);
            this.groupBox1.Name = "groupBox1";
            this.groupBox1.Size = new System.Drawing.Size(422, 94);
            this.groupBox1.TabIndex = 5;
            this.groupBox1.TabStop = false;
            this.groupBox1.Text = "Document set";
            // 
            // buttonBrowse
            // 
            this.buttonBrowse.Location = new System.Drawing.Point(233, 30);
            this.buttonBrowse.Name = "buttonBrowse";
            this.buttonBrowse.Size = new System.Drawing.Size(125, 23);
            this.buttonBrowse.TabIndex = 4;
            this.buttonBrowse.Text = "Browse global";
            this.buttonBrowse.UseVisualStyleBackColor = true;
            this.buttonBrowse.Click += new System.EventHandler(this.buttonBrowse_Click);
            // 
            // comboDocSets
            // 
            this.comboDocSets.DropDownStyle = System.Windows.Forms.ComboBoxStyle.DropDownList;
            this.comboDocSets.FormattingEnabled = true;
            this.comboDocSets.Location = new System.Drawing.Point(32, 30);
            this.comboDocSets.Name = "comboDocSets";
            this.comboDocSets.Size = new System.Drawing.Size(165, 21);
            this.comboDocSets.TabIndex = 0;
            this.comboDocSets.SelectedIndexChanged += new System.EventHandler(this.comboDocSets_SelectedIndexChanged);
            // 
            // buttonNewDocSet
            // 
            this.buttonNewDocSet.Location = new System.Drawing.Point(32, 57);
            this.buttonNewDocSet.Name = "buttonNewDocSet";
            this.buttonNewDocSet.Size = new System.Drawing.Size(75, 23);
            this.buttonNewDocSet.TabIndex = 2;
            this.buttonNewDocSet.Text = "New ...";
            this.buttonNewDocSet.UseVisualStyleBackColor = true;
            this.buttonNewDocSet.Click += new System.EventHandler(this.buttonNewDocSet_Click);
            // 
            // buttonNewDoc
            // 
            this.buttonNewDoc.Location = new System.Drawing.Point(281, 160);
            this.buttonNewDoc.Name = "buttonNewDoc";
            this.buttonNewDoc.Size = new System.Drawing.Size(75, 23);
            this.buttonNewDoc.TabIndex = 7;
            this.buttonNewDoc.Text = "New";
            this.buttonNewDoc.UseVisualStyleBackColor = true;
            this.buttonNewDoc.Click += new System.EventHandler(this.buttonNewDoc_Click);
            // 
            // buttonDeleteDoc
            // 
            this.buttonDeleteDoc.Location = new System.Drawing.Point(281, 264);
            this.buttonDeleteDoc.Name = "buttonDeleteDoc";
            this.buttonDeleteDoc.Size = new System.Drawing.Size(75, 23);
            this.buttonDeleteDoc.TabIndex = 8;
            this.buttonDeleteDoc.Text = "Delete node";
            this.buttonDeleteDoc.UseVisualStyleBackColor = true;
            this.buttonDeleteDoc.Click += new System.EventHandler(this.buttonDeleteDoc_Click);
            // 
            // buttonEditDoc
            // 
            this.buttonEditDoc.Location = new System.Drawing.Point(281, 215);
            this.buttonEditDoc.Name = "buttonEditDoc";
            this.buttonEditDoc.Size = new System.Drawing.Size(75, 23);
            this.buttonEditDoc.TabIndex = 9;
            this.buttonEditDoc.Text = "Edit";
            this.buttonEditDoc.UseVisualStyleBackColor = true;
            this.buttonEditDoc.Click += new System.EventHandler(this.buttonEditDoc_Click);
            // 
            // treeDocs
            // 
            this.treeDocs.Location = new System.Drawing.Point(63, 124);
            this.treeDocs.Name = "treeDocs";
            this.treeDocs.Size = new System.Drawing.Size(196, 234);
            this.treeDocs.TabIndex = 10;
            this.treeDocs.AfterSelect += new System.Windows.Forms.TreeViewEventHandler(this.treeDocs_AfterSelect);
            // 
            // buttonDeleteDocSet
            // 
            this.buttonDeleteDocSet.Location = new System.Drawing.Point(122, 57);
            this.buttonDeleteDocSet.Name = "buttonDeleteDocSet";
            this.buttonDeleteDocSet.Size = new System.Drawing.Size(75, 23);
            this.buttonDeleteDocSet.TabIndex = 3;
            this.buttonDeleteDocSet.Text = "Delete";
            this.buttonDeleteDocSet.UseVisualStyleBackColor = true;
            this.buttonDeleteDocSet.Click += new System.EventHandler(this.buttonDeleteDocSet_Click);
            // 
            // Form1
            // 
            this.AutoScaleDimensions = new System.Drawing.SizeF(6F, 13F);
            this.AutoScaleMode = System.Windows.Forms.AutoScaleMode.Font;
            this.ClientSize = new System.Drawing.Size(459, 430);
            this.Controls.Add(this.treeDocs);
            this.Controls.Add(this.buttonEditDoc);
            this.Controls.Add(this.buttonDeleteDoc);
            this.Controls.Add(this.buttonNewDoc);
            this.Controls.Add(this.groupBox1);
            this.Name = "Form1";
            this.StartPosition = System.Windows.Forms.FormStartPosition.CenterScreen;
            this.Text = "Doc db sample gui";
            this.Load += new System.EventHandler(this.Form1_Load);
            this.groupBox1.ResumeLayout(false);
            this.ResumeLayout(false);

        }

        #endregion

        private System.Windows.Forms.GroupBox groupBox1;
        private System.Windows.Forms.ComboBox comboDocSets;
        private System.Windows.Forms.Button buttonNewDocSet;
        private System.Windows.Forms.Button buttonNewDoc;
        private System.Windows.Forms.Button buttonDeleteDoc;
        private System.Windows.Forms.Button buttonEditDoc;
        private System.Windows.Forms.TreeView treeDocs;
        private System.Windows.Forms.Button buttonBrowse;
        private System.Windows.Forms.Button buttonDeleteDocSet;
    }
}

