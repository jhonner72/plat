using Emc.Documentum.FS.DataModel.Core;
using Emc.Documentum.FS.DataModel.Core.Properties;
using Lombard.Documentum.Data.Exceptions;

namespace Lombard.Documentum.Data.Extensions
{
    public static class DataObjectExt
    {
        public static Property GetProperty(this DataObject dataObject, string propertyName)
        {
            var result = dataObject.Properties.Get(propertyName);
            if (result == null)
                throw new PropertyNotFoundException(dataObject, propertyName);
            return result;
        }
    }
}
