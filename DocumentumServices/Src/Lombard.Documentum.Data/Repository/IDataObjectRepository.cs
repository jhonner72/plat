using System.Collections.Generic;
using System.IO;
using Emc.Documentum.FS.DataModel.Core;
using Emc.Documentum.FS.DataModel.Core.Query;

namespace Lombard.Documentum.Data.Repository
{
    public interface IDataObjectRepository
    {
        string GetResourceContents(string resourceLocation);
        
        FileInfo GetFileInfo(string resourceLocation);

        DataObject GetDataObject(string resourceLocation);

        DataObject GetDataObjectWithContents(string resourceLocation);

        DataObject GetDataObjectByObjectId(string objectId);

        IList<DataObject> FindDataObjects(string objectType, List<PropertyExpression> searchCriteria, bool getContent);

        DataPackage Update(DataObject dataObject);

        DataObject Checkout(string objectId);
        void Checkin(DataObject dataObject);
        void CancelCheckout(string objectId);

        DataObject CreateDataObject(
            string destinationFolder,
            string fileName,
            string objectType,
            string format,
            IEnumerable<KeyValuePair<string, string>> properties = null,
            bool hasContent = true);

        void CreateParentChildRelationship(DataObject parentObject, DataObject childObject, string relationshipType); 
    }
}
