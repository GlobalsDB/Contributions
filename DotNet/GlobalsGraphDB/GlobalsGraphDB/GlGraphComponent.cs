using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;

namespace GlobalsGraphDB
{
    // common ancestor class for graph edges & graph nodes, to organize common functionality
    public abstract class GlGraphComponent
    {
        protected GlGraph _ParentGraph = null; 

        public abstract string GetCustomString(string property_name);
        public abstract void SetCustomString(string property_name, string value);
        public abstract void DeleteCustomString(string property_name);
        public abstract List<string> NonemptyPropertyNames(); 
    }
}
