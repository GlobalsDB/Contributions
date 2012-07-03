namespace GraphSampleGUI
{
    partial class PropertiesEditor
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
            this.lblName = new System.Windows.Forms.Label();
            this.lblProps = new System.Windows.Forms.Label();
            this.comboProps = new System.Windows.Forms.ComboBox();
            this.buttonNew = new System.Windows.Forms.Button();
            this.buttonEdit = new System.Windows.Forms.Button();
            this.buttonDelete = new System.Windows.Forms.Button();
            this.labelPropValue = new System.Windows.Forms.Label();
            this.SuspendLayout();
            // 
            // lblName
            // 
            this.lblName.AutoSize = true;
            this.lblName.Location = new System.Drawing.Point(25, 23);
            this.lblName.Name = "lblName";
            this.lblName.Size = new System.Drawing.Size(67, 13);
            this.lblName.TabIndex = 0;
            this.lblName.Text = "Node Name:";
            // 
            // lblProps
            // 
            this.lblProps.AutoSize = true;
            this.lblProps.Location = new System.Drawing.Point(28, 54);
            this.lblProps.Name = "lblProps";
            this.lblProps.Size = new System.Drawing.Size(94, 13);
            this.lblProps.TabIndex = 1;
            this.lblProps.Text = "Custom properties:";
            // 
            // comboProps
            // 
            this.comboProps.DropDownStyle = System.Windows.Forms.ComboBoxStyle.DropDownList;
            this.comboProps.FormattingEnabled = true;
            this.comboProps.Location = new System.Drawing.Point(31, 84);
            this.comboProps.Name = "comboProps";
            this.comboProps.Size = new System.Drawing.Size(135, 21);
            this.comboProps.TabIndex = 2;
            this.comboProps.SelectedIndexChanged += new System.EventHandler(this.comboProps_SelectedIndexChanged);
            // 
            // buttonNew
            // 
            this.buttonNew.Location = new System.Drawing.Point(31, 120);
            this.buttonNew.Name = "buttonNew";
            this.buttonNew.Size = new System.Drawing.Size(75, 23);
            this.buttonNew.TabIndex = 3;
            this.buttonNew.Text = "New";
            this.buttonNew.UseVisualStyleBackColor = true;
            this.buttonNew.Click += new System.EventHandler(this.buttonNew_Click);
            // 
            // buttonEdit
            // 
            this.buttonEdit.Location = new System.Drawing.Point(112, 120);
            this.buttonEdit.Name = "buttonEdit";
            this.buttonEdit.Size = new System.Drawing.Size(75, 23);
            this.buttonEdit.TabIndex = 4;
            this.buttonEdit.Text = "Edit";
            this.buttonEdit.UseVisualStyleBackColor = true;
            this.buttonEdit.Click += new System.EventHandler(this.buttonEdit_Click);
            // 
            // buttonDelete
            // 
            this.buttonDelete.Location = new System.Drawing.Point(193, 120);
            this.buttonDelete.Name = "buttonDelete";
            this.buttonDelete.Size = new System.Drawing.Size(75, 23);
            this.buttonDelete.TabIndex = 5;
            this.buttonDelete.Text = "Delete";
            this.buttonDelete.UseVisualStyleBackColor = true;
            this.buttonDelete.Click += new System.EventHandler(this.buttonDelete_Click);
            // 
            // labelPropValue
            // 
            this.labelPropValue.AutoSize = true;
            this.labelPropValue.Location = new System.Drawing.Point(184, 84);
            this.labelPropValue.Name = "labelPropValue";
            this.labelPropValue.Size = new System.Drawing.Size(39, 13);
            this.labelPropValue.TabIndex = 6;
            this.labelPropValue.Text = "(value)";
            // 
            // NodeProperties
            // 
            this.AutoScaleDimensions = new System.Drawing.SizeF(6F, 13F);
            this.AutoScaleMode = System.Windows.Forms.AutoScaleMode.Font;
            this.ClientSize = new System.Drawing.Size(313, 177);
            this.Controls.Add(this.labelPropValue);
            this.Controls.Add(this.buttonDelete);
            this.Controls.Add(this.buttonEdit);
            this.Controls.Add(this.buttonNew);
            this.Controls.Add(this.comboProps);
            this.Controls.Add(this.lblProps);
            this.Controls.Add(this.lblName);
            this.Name = "NodeProperties";
            this.StartPosition = System.Windows.Forms.FormStartPosition.CenterParent;
            this.Text = "NodeProperties";
            this.ResumeLayout(false);
            this.PerformLayout();

        }

        #endregion

        private System.Windows.Forms.Label lblName;
        private System.Windows.Forms.Label lblProps;
        private System.Windows.Forms.ComboBox comboProps;
        private System.Windows.Forms.Button buttonNew;
        private System.Windows.Forms.Button buttonEdit;
        private System.Windows.Forms.Button buttonDelete;
        private System.Windows.Forms.Label labelPropValue;
    }
}