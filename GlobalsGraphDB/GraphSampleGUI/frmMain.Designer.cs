namespace GraphSampleGUI
{
    partial class frmMain
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
            this.comboGraphs = new System.Windows.Forms.ComboBox();
            this.buttonNewGraph = new System.Windows.Forms.Button();
            this.buttonDeleteGraph = new System.Windows.Forms.Button();
            this.groupBox1 = new System.Windows.Forms.GroupBox();
            this.listNodes = new System.Windows.Forms.ListBox();
            this.label1 = new System.Windows.Forms.Label();
            this.buttonNewNode = new System.Windows.Forms.Button();
            this.buttonDeleteNode = new System.Windows.Forms.Button();
            this.buttonNodeProperties = new System.Windows.Forms.Button();
            this.labelEdges = new System.Windows.Forms.Label();
            this.listEdges = new System.Windows.Forms.ListBox();
            this.buttonEdgeProperties = new System.Windows.Forms.Button();
            this.buttonDeleteEdge = new System.Windows.Forms.Button();
            this.buttonNewEdge = new System.Windows.Forms.Button();
            this.buttonFindPath = new System.Windows.Forms.Button();
            this.groupBox1.SuspendLayout();
            this.SuspendLayout();
            // 
            // comboGraphs
            // 
            this.comboGraphs.DropDownStyle = System.Windows.Forms.ComboBoxStyle.DropDownList;
            this.comboGraphs.FormattingEnabled = true;
            this.comboGraphs.Location = new System.Drawing.Point(32, 30);
            this.comboGraphs.Name = "comboGraphs";
            this.comboGraphs.Size = new System.Drawing.Size(165, 21);
            this.comboGraphs.TabIndex = 0;
            this.comboGraphs.SelectedIndexChanged += new System.EventHandler(this.comboGraphs_SelectedIndexChanged);
            // 
            // buttonNewGraph
            // 
            this.buttonNewGraph.Location = new System.Drawing.Point(231, 30);
            this.buttonNewGraph.Name = "buttonNewGraph";
            this.buttonNewGraph.Size = new System.Drawing.Size(75, 23);
            this.buttonNewGraph.TabIndex = 2;
            this.buttonNewGraph.Text = "New ...";
            this.buttonNewGraph.UseVisualStyleBackColor = true;
            this.buttonNewGraph.Click += new System.EventHandler(this.buttonNewGraph_Click);
            // 
            // buttonDeleteGraph
            // 
            this.buttonDeleteGraph.Location = new System.Drawing.Point(331, 30);
            this.buttonDeleteGraph.Name = "buttonDeleteGraph";
            this.buttonDeleteGraph.Size = new System.Drawing.Size(75, 23);
            this.buttonDeleteGraph.TabIndex = 3;
            this.buttonDeleteGraph.Text = "Delete";
            this.buttonDeleteGraph.UseVisualStyleBackColor = true;
            this.buttonDeleteGraph.Click += new System.EventHandler(this.buttonDeleteGraph_Click);
            // 
            // groupBox1
            // 
            this.groupBox1.Controls.Add(this.buttonDeleteGraph);
            this.groupBox1.Controls.Add(this.comboGraphs);
            this.groupBox1.Controls.Add(this.buttonNewGraph);
            this.groupBox1.Location = new System.Drawing.Point(12, 12);
            this.groupBox1.Name = "groupBox1";
            this.groupBox1.Size = new System.Drawing.Size(439, 72);
            this.groupBox1.TabIndex = 4;
            this.groupBox1.TabStop = false;
            this.groupBox1.Text = "Graph";
            // 
            // listNodes
            // 
            this.listNodes.DisplayMember = "NodeName";
            this.listNodes.FormattingEnabled = true;
            this.listNodes.Location = new System.Drawing.Point(28, 112);
            this.listNodes.Name = "listNodes";
            this.listNodes.Size = new System.Drawing.Size(181, 199);
            this.listNodes.TabIndex = 5;
            this.listNodes.SelectedIndexChanged += new System.EventHandler(this.listNodes_SelectedIndexChanged);
            // 
            // label1
            // 
            this.label1.AutoSize = true;
            this.label1.Location = new System.Drawing.Point(25, 96);
            this.label1.Name = "label1";
            this.label1.Size = new System.Drawing.Size(38, 13);
            this.label1.TabIndex = 6;
            this.label1.Text = "Nodes";
            // 
            // buttonNewNode
            // 
            this.buttonNewNode.Location = new System.Drawing.Point(28, 317);
            this.buttonNewNode.Name = "buttonNewNode";
            this.buttonNewNode.Size = new System.Drawing.Size(75, 23);
            this.buttonNewNode.TabIndex = 7;
            this.buttonNewNode.Text = "New node";
            this.buttonNewNode.UseVisualStyleBackColor = true;
            this.buttonNewNode.Click += new System.EventHandler(this.buttonNewNode_Click);
            // 
            // buttonDeleteNode
            // 
            this.buttonDeleteNode.Location = new System.Drawing.Point(109, 317);
            this.buttonDeleteNode.Name = "buttonDeleteNode";
            this.buttonDeleteNode.Size = new System.Drawing.Size(75, 23);
            this.buttonDeleteNode.TabIndex = 8;
            this.buttonDeleteNode.Text = "Delete node";
            this.buttonDeleteNode.UseVisualStyleBackColor = true;
            this.buttonDeleteNode.Click += new System.EventHandler(this.buttonDeleteNode_Click);
            // 
            // buttonNodeProperties
            // 
            this.buttonNodeProperties.Location = new System.Drawing.Point(44, 346);
            this.buttonNodeProperties.Name = "buttonNodeProperties";
            this.buttonNodeProperties.Size = new System.Drawing.Size(114, 23);
            this.buttonNodeProperties.TabIndex = 10;
            this.buttonNodeProperties.Text = "Edit node properties";
            this.buttonNodeProperties.UseVisualStyleBackColor = true;
            this.buttonNodeProperties.Click += new System.EventHandler(this.buttonNodeProperties_Click);
            // 
            // labelEdges
            // 
            this.labelEdges.AutoSize = true;
            this.labelEdges.Location = new System.Drawing.Point(340, 96);
            this.labelEdges.Name = "labelEdges";
            this.labelEdges.Size = new System.Drawing.Size(61, 13);
            this.labelEdges.TabIndex = 11;
            this.labelEdges.Text = "Edges to ...";
            // 
            // listEdges
            // 
            this.listEdges.FormattingEnabled = true;
            this.listEdges.Location = new System.Drawing.Point(333, 112);
            this.listEdges.Name = "listEdges";
            this.listEdges.Size = new System.Drawing.Size(174, 199);
            this.listEdges.TabIndex = 12;
            // 
            // buttonEdgeProperties
            // 
            this.buttonEdgeProperties.Location = new System.Drawing.Point(356, 346);
            this.buttonEdgeProperties.Name = "buttonEdgeProperties";
            this.buttonEdgeProperties.Size = new System.Drawing.Size(123, 23);
            this.buttonEdgeProperties.TabIndex = 15;
            this.buttonEdgeProperties.Text = "Edit edge properties";
            this.buttonEdgeProperties.UseVisualStyleBackColor = true;
            this.buttonEdgeProperties.Click += new System.EventHandler(this.buttonEdgeProperties_Click);
            // 
            // buttonDeleteEdge
            // 
            this.buttonDeleteEdge.Location = new System.Drawing.Point(423, 317);
            this.buttonDeleteEdge.Name = "buttonDeleteEdge";
            this.buttonDeleteEdge.Size = new System.Drawing.Size(75, 23);
            this.buttonDeleteEdge.TabIndex = 14;
            this.buttonDeleteEdge.Text = "Delete edge";
            this.buttonDeleteEdge.UseVisualStyleBackColor = true;
            this.buttonDeleteEdge.Click += new System.EventHandler(this.buttonDeleteEdge_Click);
            // 
            // buttonNewEdge
            // 
            this.buttonNewEdge.Location = new System.Drawing.Point(333, 317);
            this.buttonNewEdge.Name = "buttonNewEdge";
            this.buttonNewEdge.Size = new System.Drawing.Size(75, 23);
            this.buttonNewEdge.TabIndex = 13;
            this.buttonNewEdge.Text = "New edge";
            this.buttonNewEdge.UseVisualStyleBackColor = true;
            this.buttonNewEdge.Click += new System.EventHandler(this.buttonNewEdge_Click);
            // 
            // buttonFindPath
            // 
            this.buttonFindPath.Location = new System.Drawing.Point(44, 375);
            this.buttonFindPath.Name = "buttonFindPath";
            this.buttonFindPath.Size = new System.Drawing.Size(114, 23);
            this.buttonFindPath.TabIndex = 16;
            this.buttonFindPath.Text = "Shortest Path";
            this.buttonFindPath.UseVisualStyleBackColor = true;
            this.buttonFindPath.Click += new System.EventHandler(this.buttonFindPath_Click);
            // 
            // frmMain
            // 
            this.AutoScaleDimensions = new System.Drawing.SizeF(6F, 13F);
            this.AutoScaleMode = System.Windows.Forms.AutoScaleMode.Font;
            this.ClientSize = new System.Drawing.Size(588, 404);
            this.Controls.Add(this.buttonFindPath);
            this.Controls.Add(this.buttonEdgeProperties);
            this.Controls.Add(this.buttonDeleteEdge);
            this.Controls.Add(this.buttonNewEdge);
            this.Controls.Add(this.listEdges);
            this.Controls.Add(this.labelEdges);
            this.Controls.Add(this.buttonNodeProperties);
            this.Controls.Add(this.buttonDeleteNode);
            this.Controls.Add(this.buttonNewNode);
            this.Controls.Add(this.label1);
            this.Controls.Add(this.listNodes);
            this.Controls.Add(this.groupBox1);
            this.Name = "frmMain";
            this.StartPosition = System.Windows.Forms.FormStartPosition.CenterScreen;
            this.Text = "Graphing sample";
            this.Load += new System.EventHandler(this.Form1_Load);
            this.groupBox1.ResumeLayout(false);
            this.ResumeLayout(false);
            this.PerformLayout();

        }

        #endregion

        private System.Windows.Forms.ComboBox comboGraphs;
        private System.Windows.Forms.Button buttonNewGraph;
        private System.Windows.Forms.Button buttonDeleteGraph;
        private System.Windows.Forms.GroupBox groupBox1;
        private System.Windows.Forms.ListBox listNodes;
        private System.Windows.Forms.Label label1;
        private System.Windows.Forms.Button buttonNewNode;
        private System.Windows.Forms.Button buttonDeleteNode;
        private System.Windows.Forms.Button buttonNodeProperties;
        private System.Windows.Forms.Label labelEdges;
        private System.Windows.Forms.ListBox listEdges;
        private System.Windows.Forms.Button buttonEdgeProperties;
        private System.Windows.Forms.Button buttonDeleteEdge;
        private System.Windows.Forms.Button buttonNewEdge;
        private System.Windows.Forms.Button buttonFindPath;
    }
}

