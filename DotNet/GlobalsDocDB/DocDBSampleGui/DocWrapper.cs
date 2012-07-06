using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using GlobalsDocDB;

/* This class is an example of a wrapper that a client application might need to 
 * write around the API's doc class. The specific reason for this in the 
 * current example is that we want to display a list of doc objects in a 
 * ListBox control, which uses a property name to determine the text to display. 
 * In a given client application this might be anything, based on some complex 
 * business logic. The client might use this kind of wrapper class to enforce the 
 * creation of required properties, uniqueness, etc. etc. as appropriate. 
 * 
 * This logic could arguably be put into the doc db api, but that's currently 
 * intended as a generic api, so this class is more specific to a business 
 * logic example. 
 */

namespace DocDBSampleGui
{
    public class DocWrapper
    {
        public const string DOC_NAME_PROPERTY = "name";
        
        public static string FindDocName(GlDoc any_doc)
        {
            if (any_doc == null)
                return "Unknown document"; 

            return any_doc.GetString(DOC_NAME_PROPERTY); 
        }

        public static void CreateDocument(GlDocSet parent_docset, string new_doc_name)
        {
            parent_docset.CreateNewDoc().SetString(DOC_NAME_PROPERTY, new_doc_name); 
        }


        

        public GlDoc _api_doc = null;
        public DocWrapper(GlDoc start_doc)
        {
            _api_doc = start_doc; 
        }

        public string DocName
        {
            get 
            {
                return FindDocName(_api_doc); 
            }
        }

        public List<string> CustomPropertyNames()
        {
            List<string> working_list = _api_doc.PropertyNames();
            working_list.Remove(DOC_NAME_PROPERTY);
            return working_list;
        }

        public string PropertyDisplayStr(string property_name)
        {
            switch (_api_doc.PropertyType(property_name))
            {
                case GlDoc.ValueType.DOCUMENT:
                    List<GlDoc> docrefs = _api_doc.GetDocs(property_name);
                    if (docrefs == null)
                        return FindDocName(_api_doc.GetDoc(property_name));
                    else
                    {
                        switch (docrefs.Count)
                        {
                            case 0:
                                return "(empty list)";
                            case 1:
                                return DocWrapper.FindDocName(docrefs[0]);
                            default:
                                return DocWrapper.FindDocName(docrefs[0]) + ", " + DocWrapper.FindDocName(docrefs[1]) + ", ...";
                        }
                    }                        
                case GlDoc.ValueType.STRING:
                    List<string> multivals = _api_doc.GetStrings(property_name);
                    if (multivals == null)
                        return _api_doc.GetString(property_name);
                    else
                    {
                        switch (multivals.Count)
                        {
                            case 0:
                                return "empty list"; 
                            case 1:
                                return multivals[0]; 
                            default:
                                return multivals[0] + ", " + multivals[1] + ", ..."; 
                        }
                    }
                default:
                    return ""; 
                    
            }
        }
    }
}
