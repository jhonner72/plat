using System;
using Emc.Documentum.FS.DataModel.Core;

namespace Lombard.Documentum.Data.Exceptions
{
    public class PropertyNotFoundException : Exception
    {
        public PropertyNotFoundException(DataObject dataObject, string propertyName, Exception innerException = null)
            : base (string.Format("Could not find property '{0}' for object type '{1}'", propertyName, dataObject.Type ), innerException)
        {
            
        }
    }
}
