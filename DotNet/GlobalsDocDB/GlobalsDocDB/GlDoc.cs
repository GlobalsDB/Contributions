using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using InterSystems.Globals; 

namespace GlobalsDocDB
{
    public class GlDoc
    {
        private Guid _DocUID = Guid.Empty;
        private GlDocSet _ParentDocSet;
        private NodeReference _DocNodeRef; 

        private const string DATA_TYPE_SUBSCRIPT = "type";
        private const string VALUE_SUBSCRIPT = "value";

        public enum ValueType
        {
            STRING,
            DOCUMENT, 
            UNKNOWN
        }

        private const string STRING_DATATYPE_FLAG = "string";
        private const string DOC_DATATYPE_FLAG = "doc";
         

        private static string DataTypeFlag(ValueType desired_type)
        {
            switch (desired_type)
            {
                case ValueType.DOCUMENT:
                    return DOC_DATATYPE_FLAG; 
                case ValueType.STRING:
                    return STRING_DATATYPE_FLAG; 
                default:
                    return ""; 
            }
        }

        // CONSTRUCTORS (internal). Note that GlDoc objects for existing documents
        // are created by the GlDocSet constructor. GlDoc objects for docs being 
        // created by the user are created by GlDocSet.CreateNewDoc(). These two 
        // GlDocSet methods call these internal constructors for GlDoc. 

        // create a doc object for a brand-new doc (does not exist in DB yet)
        internal GlDoc(GlDocSet parent_docset)
        {
            _DocUID = Guid.NewGuid();
            _ParentDocSet = parent_docset;
            _DocNodeRef = GlobalsDocDB.ActiveConnection().CreateNodeReference(_ParentDocSet.Name);
            _DocNodeRef.AppendSubscript(_DocUID.ToString());
            _DocNodeRef.Set("GlDoc"); // dummy
        }

        // create a new doc object around an existing doc in the db
        internal GlDoc(GlDocSet parent_docset, Guid doc_guid)
        {
            _DocUID = doc_guid;
            _ParentDocSet = parent_docset;
            _DocNodeRef = GlobalsDocDB.ActiveConnection().CreateNodeReference(_ParentDocSet.Name);
            _DocNodeRef.AppendSubscript(_DocUID.ToString());
        }

        public bool Delete()
        {
            if (IsReferenced() != null) return false; // some other doc refers to this doc
            _DocNodeRef.Kill();
            _ParentDocSet.MarkDocAsDeleted(this); 
            return true; 
        }

        public Guid DocUID
        {
            get
            {
                return _DocUID;
            }
        }

        public GlDocSet DocSet
        {
            get { return _ParentDocSet; }
        }

        // indicates whether any OTHER documents contain a reference to this 
        // document in a property value (either simple or complex)
        public GlDoc IsReferenced()
        {
            foreach (GlDoc loop_doc in _ParentDocSet.AllDocs)
            {
                if (loop_doc == this) continue; // don't bother with self references
                foreach (string propname in loop_doc.PropertyNames())
                {
                    if (loop_doc.PropertyType(propname) != ValueType.DOCUMENT) continue; 

                    List<GlDoc> docrefs = loop_doc.GetDocs(propname);
                    if (docrefs == null)
                    {
                        GlDoc docval = loop_doc.GetDoc(propname);
                        if (docval == this) return loop_doc; 
                    }
                    else
                    {
                        foreach (GlDoc refdoc in loop_doc.GetDocs(propname))
                        {
                            if (refdoc == this) return loop_doc;
                        } 
                    }                    
                }
            }

            return null; 
        }

        public void DeleteProperty(string property_name)
        {
            _DocNodeRef.Kill(property_name);
        }

        public ValueType PropertyType(string property_name)
        {

            if (_DocNodeRef.GetString() != "GlDoc") return ValueType.UNKNOWN;  
            switch (_DocNodeRef.GetString(property_name, DATA_TYPE_SUBSCRIPT))
            {
                case STRING_DATATYPE_FLAG:
                    return ValueType.STRING;
                case DOC_DATATYPE_FLAG:
                    return ValueType.DOCUMENT; 
                default:
                    return ValueType.UNKNOWN; 
            }
        }
         
         
        private bool EnsureDataType(string property_name, ValueType desired_type)
        {
            ValueType current_type = PropertyType(property_name);

            if (current_type == desired_type)
                return true; // all's well

            if (current_type == ValueType.UNKNOWN) // set it
            {
                _DocNodeRef.Set(DataTypeFlag(desired_type), property_name, DATA_TYPE_SUBSCRIPT);
                return true; // all set
            }

            // only other possibility is that it's currently set to some other type; fail
            return false;
        }

        public List<string> PropertyNames()
        {
            List<string> working_list = new List<string>();
            string loop_subscript = _DocNodeRef.NextSubscript("");
            while (loop_subscript != "")
            {
                try
                {
                    if (_DocNodeRef.HasSubnodes(loop_subscript, VALUE_SUBSCRIPT))
                        continue;

                    working_list.Add(loop_subscript);
                }
                catch
                { }
                loop_subscript = _DocNodeRef.NextSubscript(loop_subscript);
            }
            return working_list;
        }

        // simple (single-value) property access methods
        #region simple properties
        private string GetActualString(string property_name)
        {
            try
            {
                return _DocNodeRef.GetString(property_name, VALUE_SUBSCRIPT);
            }
            catch
            {
                return null; 
            }
        }

        public string GetString(string property_name)
        {
            if (PropertyType(property_name) == ValueType.STRING)
                return GetActualString(property_name);
            else
                return null;  
        }

        public GlDoc GetDoc(string property_name)
        {
            if (PropertyType(property_name) == ValueType.DOCUMENT)
            {
                Guid doc_guid = Guid.Empty;

                try
                {
                    if (Guid.TryParse(GetActualString(property_name), out doc_guid))
                        return _ParentDocSet.AllDocsByGuid[doc_guid]; 
                }
                catch
                {
                    return null; 
                }
            }

            return null; 
        }


        public void SetString(string property_name, string new_value)
        {
            if (!EnsureDataType(property_name, ValueType.STRING)) return; 
            _DocNodeRef.Set(new_value, property_name, VALUE_SUBSCRIPT);
        }

        public void SetDoc(string property_name, GlDoc new_value)
        {
            if (!EnsureDataType(property_name, ValueType.DOCUMENT)) return; 
            _DocNodeRef.Set(new_value.DocUID.ToString(), property_name, VALUE_SUBSCRIPT);
        }
        #endregion

        // complex (multi-value) property access methods
        #region complex properties

        public int ValueCount(string property_name)
        {
            ValueList working_vl = _DocNodeRef.GetList(property_name, VALUE_SUBSCRIPT);
            int result = working_vl.Length; 
            working_vl.Close();
            return result; 

            // we can't just return ...GetList(...).Length because we need to close the VL object explicitly 
        }

        private List<string> GetActualStringList(string property_name)
        {
            List<string> working_list = new List<string>();
            try
            {
                ValueList string_vl = _DocNodeRef.GetList(property_name, VALUE_SUBSCRIPT);

                for (int loop_ix = 1; loop_ix <= string_vl.Length; loop_ix++)
                {
                    string loop_str = string_vl.GetNextString();
                    working_list.Add(loop_str);

                }
                string_vl.Close();
                return working_list;
            }
            catch 
            { 
                return null;
            }
        }

        public List<string> GetStrings(string property_name)
        {
            if (PropertyType(property_name) != ValueType.STRING) return new List<string>(); 
            
            return GetActualStringList(property_name);
            
        }

        public List<GlDoc> GetDocs(string property_name)
        {
            List<GlDoc> working_list = new List<GlDoc>();
            if (PropertyType(property_name) != ValueType.DOCUMENT) return working_list; 
            try
            {
                foreach (string guid_str in GetActualStringList(property_name))
                {
                    Guid doc_guid = Guid.Empty;
                    if (Guid.TryParse(guid_str, out doc_guid))
                    {
                        working_list.Add(_ParentDocSet.AllDocsByGuid[doc_guid]);
                    }
                } 

                return working_list;
            }
            catch
            {
                return null;
            }
        }


        private void AppendActualString(string property_name, string new_value)
        {
            ValueList current_vl = _DocNodeRef.GetList(property_name, VALUE_SUBSCRIPT);
            if (current_vl == null)
                current_vl = GlobalsDocDB.ActiveConnection().CreateList(); 

            current_vl.Append(new_value);
            _DocNodeRef.Set(current_vl, property_name, VALUE_SUBSCRIPT);
            current_vl.Close();
        }


        public void AppendString(string property_name, string new_value)
        {
            if (!EnsureDataType(property_name, ValueType.STRING)) return;  
            AppendActualString(property_name, new_value);
        }

        public void AppendDoc(string property_name, GlDoc new_value)
        {
            if (!EnsureDataType(property_name, ValueType.DOCUMENT)) return;  
            AppendActualString(property_name, new_value.DocUID.ToString()); 
        }


        private void RemoveActualString(string property_name, string value_to_remove)
        {
            ValueList current_vl = _DocNodeRef.GetList(property_name, VALUE_SUBSCRIPT);
            ValueList new_vl = GlobalsDocDB.ActiveConnection().CreateList();

            string loop_str = current_vl.GetNextString();
            while (loop_str != null)
            {
                if (loop_str != value_to_remove)
                    new_vl.Append(loop_str);

                loop_str = current_vl.GetNextString();
            }

            _DocNodeRef.Set(new_vl, property_name, VALUE_SUBSCRIPT);

            current_vl.Close();
            new_vl.Close();

        }

        public void RemoveString(string property_name, string value_to_remove)
        {
            if (PropertyType(property_name) != ValueType.STRING) return;  
            RemoveActualString(property_name, value_to_remove);
        }


        public void RemoveDoc(string property_name, GlDoc doc_to_remove)
        {
            if (PropertyType(property_name) != ValueType.DOCUMENT) return;  
            RemoveActualString(property_name, doc_to_remove.DocUID.ToString()); 
        }
        #endregion

        
    }

}
