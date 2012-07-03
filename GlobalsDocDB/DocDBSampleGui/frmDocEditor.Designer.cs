namespace DocDBSampleGui
{
    partial class frmDocEditor
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
            this.comboProperty = new System.Windows.Forms.ComboBox();
            this.listPropVals = new System.Windows.Forms.ListBox();
            this.labelPropVal = new System.Windows.Forms.Label();
            this.buttonEdit = new System.Windows.Forms.Button();
            this.buttonClear = new System.Windows.Forms.Button();
            this.buttonAdd = new System.Windows.Forms.Button();
            this.buttonRemove = new System.Windows.Forms.Button();
            this.panelMulti = new System.Windows.Forms.Panel();
            this.panelMulti.SuspendLayout();
            this.SuspendLayout();
            // 
            // comboProperty
            // 
            this.comboProperty.DropDownStyle = System.Windows.Forms.ComboBoxStyle.DropDownList;
            this.comboProperty.FormattingEnabled = true;
            this.comboProperty.Location = new System.Drawing.Point(30, 25);
            this.comboProperty.Name = "comboProperty";
            this.comboProperty.Size = new System.Drawing.Size(233, 21);
            this.comboProperty.TabIndex = 0;
            this.comboProperty.SelectedIndexChanged += new System.EventHandler(this.comboProperty_SelectedIndexChanged);
            // 
            // listPropVals
            // 
            this.listPropVals.FormattingEnabled = true;
            this.listPropVals.Location = new System.Drawing.Point(12, 17);
            this.listPropVals.Name = "listPropVals";
            this.listPropVals.Size = new System.Drawing.Size(122, 121);
            this.listPropVals.TabIndex = 2;
            // 
            // labelPropVal
            // 
            this.labelPropVal.AutoSize = true;
            this.labelPropVal.Location = new System.Drawing.Point(39, 53);
            this.labelPropVal.Name = "labelPropVal";
            this.labelPropVal.Size = new System.Drawing.Size(35, 13);
            this.labelPropVal.TabIndex = 3;
            this.labelPropVal.Text = "label1";
            this.labelPropVal.Visible = false;
            // 
            // buttonEdit
            // 
            this.buttonEdit.Location = new System.Drawing.Point(30, 80);
            this.buttonEdit.Name = "buttonEdit";
            this.buttonEdit.Size = new System.Drawing.Size(75, 23);
            this.buttonEdit.TabIndex = 4;
            this.buttonEdit.Text = "Edit";
            this.buttonEdit.UseVisualStyleBackColor = true;
            this.buttonEdit.Click += new System.EventHandler(this.buttonEdit_Click);
            // 
            // buttonClear
            // 
            this.buttonClear.Location = new System.Drawing.Point(111, 80);
            this.buttonClear.Name = "buttonClear";
            this.buttonClear.Size = new System.Drawing.Size(75, 23);
            this.buttonClear.TabIndex = 5;
            this.buttonClear.Text = "Clear";
            this.buttonClear.UseVisualStyleBackColor = true;
            this.buttonClear.Click += new System.EventHandler(this.buttonClear_Click);
            // 
            // buttonAdd
            // 
            this.buttonAdd.Location = new System.Drawing.Point(140, 48);
            this.buttonAdd.Name = "buttonAdd";
            this.buttonAdd.Size = new System.Drawing.Size(75, 23);
            this.buttonAdd.TabIndex = 6;
            this.buttonAdd.Text = "Add";
            this.buttonAdd.UseVisualStyleBackColor = true;
            this.buttonAdd.Click += new System.EventHandler(this.buttonAdd_Click);
            // 
            // buttonRemove
            // 
            this.buttonRemove.Location = new System.Drawing.Point(140, 96);
            this.buttonRemove.Name = "buttonRemove";
            this.buttonRemove.Size = new System.Drawing.Size(75, 23);
            this.buttonRemove.TabIndex = 7;
            this.buttonRemove.Text = "Remove";
            this.buttonRemove.UseVisualStyleBackColor = true;
            // 
            // panelMulti
            // 
            this.panelMulti.Controls.Add(this.listPropVals);
            this.panelMulti.Controls.Add(this.buttonRemove);
            this.panelMulti.Controls.Add(this.buttonAdd);
            this.panelMulti.Location = new System.Drawing.Point(30, 126);
            this.panelMulti.Name = "panelMulti";
            this.panelMulti.Size = new System.Drawing.Size(242, 179);
            this.panelMulti.TabIndex = 8;
            this.panelMulti.Visible = false;
            // 
            // frmDocEditor
            // 
            this.AutoScaleDimensions = new System.Drawing.SizeF(6F, 13F);
            this.AutoScaleMode = System.Windows.Forms.AutoScaleMode.Font;
            this.ClientSize = new System.Drawing.Size(309, 327);
            this.Controls.Add(this.panelMulti);
            this.Controls.Add(this.buttonClear);
            this.Controls.Add(this.buttonEdit);
            this.Controls.Add(this.labelPropVal);
            this.Controls.Add(this.comboProperty);
            this.Name = "frmDocEditor";
            this.StartPosition = System.Windows.Forms.FormStartPosition.CenterParent;
            this.Text = "frmDocEditor";
            this.Load += new System.EventHandler(this.frmDocEditor_Load);
            this.panelMulti.ResumeLayout(false);
            this.ResumeLayout(false);
            this.PerformLayout();

        }

        #endregion

        private System.Windows.Forms.ComboBox comboProperty;
        private System.Windows.Forms.ListBox listPropVals;
        private System.Windows.Forms.Label labelPropVal;
        private System.Windows.Forms.Button buttonEdit;
        private System.Windows.Forms.Button buttonClear;
        private System.Windows.Forms.Button buttonAdd;
        private System.Windows.Forms.Button buttonRemove;
        private System.Windows.Forms.Panel panelMulti;
    }
}