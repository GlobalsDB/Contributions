namespace GlobalsDocDB
{
    partial class frmBrowse
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
            this.textDocContents = new System.Windows.Forms.TextBox();
            this.SuspendLayout();
            // 
            // textDocContents
            // 
            this.textDocContents.Location = new System.Drawing.Point(12, 12);
            this.textDocContents.Multiline = true;
            this.textDocContents.Name = "textDocContents";
            this.textDocContents.ReadOnly = true;
            this.textDocContents.Size = new System.Drawing.Size(366, 332);
            this.textDocContents.TabIndex = 0;
            // 
            // frmBrowse
            // 
            this.AutoScaleDimensions = new System.Drawing.SizeF(6F, 13F);
            this.AutoScaleMode = System.Windows.Forms.AutoScaleMode.Font;
            this.ClientSize = new System.Drawing.Size(457, 412);
            this.Controls.Add(this.textDocContents);
            this.Name = "frmBrowse";
            this.StartPosition = System.Windows.Forms.FormStartPosition.CenterParent;
            this.Text = "frmBrowse";
            this.Load += new System.EventHandler(this.frmBrowse_Load);
            this.Resize += new System.EventHandler(this.frmBrowse_Resize);
            this.ResumeLayout(false);
            this.PerformLayout();

        }

        #endregion

        private System.Windows.Forms.TextBox textDocContents;
    }
}