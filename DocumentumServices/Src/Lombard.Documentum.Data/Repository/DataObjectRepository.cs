using System;
using System.Collections.Generic;
using System.IO;
using System.IO.Abstractions;
using System.Linq;
using System.ServiceModel;
using Emc.Documentum.FS.DataModel.Core;
using Emc.Documentum.FS.DataModel.Core.Content;
using Emc.Documentum.FS.DataModel.Core.Profiles;
using Emc.Documentum.FS.DataModel.Core.Query;
using Emc.Documentum.FS.Runtime;
using Lombard.Common.Configuration;
using Lombard.Documentum.Data.Constants;
using Serilog;

namespace Lombard.Documentum.Data.Repository
{
    public class DataObjectRepository : IDataObjectRepository
    {
        private readonly IDfsContext dfsContext;
        private readonly IDfsConfiguration dfsConfiguration;
        private readonly IFileSystem fileSystem;

        public DataObjectRepository(IDfsContext dfsContext, IDfsConfiguration dfsConfiguration, IFileSystem fileSystem)
        {
            this.dfsContext = dfsContext;
            this.dfsConfiguration = dfsConfiguration;
            this.fileSystem = fileSystem;
        }

        public DataObject Checkout(string objectId)
        {
            Log.Verbose("DataObjectRepository Checkout DataObject '{@objectId}'", objectId);

            var objectIdSet = GetObjectIdentitySetByObjectId(objectId);
            var dataPackage = dfsContext.VersionControlService.Checkout(objectIdSet, null); // operationOptions = null

            Log.Debug("Checkout successful '{@objectId}'", objectId);
            
            return dataPackage.DataObjects[0];
        }

        public void CancelCheckout(string objectId)
        {
            Log.Verbose("DataObjectRepository Cancel Checkout of DataObject '{@objectId}'", objectId);

            var objectIdSet = GetObjectIdentitySetByObjectId(objectId);
            dfsContext.VersionControlService.CancelCheckout(objectIdSet);
            Log.Debug("Checkout successfully cancelled for objectId = '{0}'", objectId);
        }

        public void Checkin(DataObject dataObject)
        {
            Log.Verbose("DataObjectRepository Checkin DataObject '{@objectId}'", dataObject.Identity.GetValueAsString());

            var dataPackage = new DataPackage(dataObject);

            dfsContext.VersionControlService.Checkin(
                dataPackage, 
                VersionStrategy.NEXT_MINOR,
                false, // retainLock
                new List<string> {"CURRENT"},
                null); // operationOptions
        }

        public DataPackage Update(DataObject dataObject)
        {
            Log.Verbose("DataObjectRepository Update DataObject '{@objectId}'", dataObject.Identity.GetValueAsString());

            return dfsContext.ObjectService.Update(new DataPackage(dataObject), null);
        }

        public FileInfo GetFileInfo(string resourceLocation)
        {
            Log.Verbose("DataObjectRepository GetFileInfo {@resouceLocation}", resourceLocation);

            var dataObject = GetDataObjectWithContents(resourceLocation);

            if (dataObject == null || dataObject.Contents == null || !dataObject.Contents.Any())
            {
                return null;
            }

            var resultContent = dataObject.Contents[0];
            if (resultContent != null && resultContent.CanGetAsFile())
            {
                return resultContent.GetAsFile();
            }

            return null;
        }

        public string GetResourceContents(string resourceLocation)
        {
            Log.Verbose("DataObjectRepository GetResourceContents {@resouceLocation}", resourceLocation);

            var fileInfo = GetFileInfo(resourceLocation);
            
            if (fileInfo == null)
            {
                return null;
            }

            using (var streamReader = new StreamReader(fileInfo.OpenRead()))
            {
                return streamReader.ReadToEnd();
            }
        }

        public DataObject GetDataObjectByObjectId(string objectId)
        {
            Log.Verbose("DataObjectRepository GetDataObjectByObjectId {@objectId}", objectId);

            var objectIdSet = GetObjectIdentitySetByObjectId(objectId);

            var dataPackage = GetSingle(objectIdSet, null);

            return dataPackage.DataObjects[0];
        }

        public DataObject GetDataObject(string resourceLocation)
        {
            Log.Verbose("DataObjectRepository GetDataObject {@resourceLocation}", resourceLocation);

            var objectIdSet = GetObjectIdentitySet(resourceLocation);

            var dataPackage = GetSingle(objectIdSet, null);

            return dataPackage.DataObjects[0];
        }

        public DataObject GetDataObjectWithContents(string resourceLocation)
        {
            Log.Verbose("DataObjectRepository GetDataObjectWithContents {@resourceLocation}", resourceLocation);

            var objectIdSet = GetObjectIdentitySet(resourceLocation);

            var operationOptions = GetOperationOptionsForRetrievingContent();

            var dataPackage = GetSingle(objectIdSet, operationOptions);

            return dataPackage.DataObjects[0];
        }

        public DataObject GetDataObjectWithContentsObjectId(string objectId)
        {
            Log.Verbose("DataObjectRepository GetDataObjectWithContentsObjectId {@objectId}", objectId);

            var objectIdSet = GetObjectIdentitySetByObjectId(objectId);

            var operationOptions = GetOperationOptionsForRetrievingContent();

            var dataPackage = GetSingle(objectIdSet, operationOptions);

            return dataPackage.DataObjects[0];
        }

        public IList<DataObject> FindDataObjects(string objectType, List<PropertyExpression> searchCriteria, bool getContent)
        {
            Log.Verbose("DataObjectRepository FindDataObjects criteria {@searchCriteria}", searchCriteria);

            var propertyProfile = new PropertyProfile {FilterMode = PropertyFilterMode.IMPLIED};
            var operationOptions = new OperationOptions();
            operationOptions.Profiles.Add(propertyProfile);

            // Create query
            var query = new StructuredQuery();
            query.AddRepository(dfsConfiguration.Repository);
            query.ObjectType = objectType;
            query.IsIncludeHidden = true;
            query.IsDatabaseSearch = true;

            if (searchCriteria != null && searchCriteria.Count > 0)
            {
                query.RootExpressionSet = new ExpressionSet();
                foreach (var expression in searchCriteria)
                {
                    query.RootExpressionSet.AddExpression(expression);
                }
            }

            // Execute Query 
            const int StartingIndex = 0;
            int maxResults = dfsConfiguration.MaxQueryResults;
            var queryExec = new QueryExecution(StartingIndex, maxResults, maxResults);
            var queryResult = dfsContext.SearchService.Execute(query, queryExec, operationOptions);

            var queryStatus = queryResult.QueryStatus;
            var repStatusInfo = queryStatus.RepositoryStatusInfos[0];
            if (repStatusInfo.Status == Status.FAILURE)
            {
                Log.Debug("FindDataObjects failed {@searchCriteria}", searchCriteria);
                return null;
            }

            Log.Debug("FindDataObjects found {0} objects", queryResult.DataObjects.Count);

            //TODO see if there is a better way to get the contents from a search
            return queryResult.DataObjects
                .Select(dataObject => getContent 
                    ? GetDataObjectWithContentsObjectId(dataObject.Identity.GetValueAsString()) 
                    : GetDataObjectByObjectId(dataObject.Identity.GetValueAsString()))
                .ToList();
        }

        public DataObject CreateDataObject(
            string destinationFolder,
            string filename,
            string objectType,
            string format,
            IEnumerable<KeyValuePair<string, string>> properties = null,
            bool hasContent = true)
        {
            Log.Verbose("DataObjectRepository CreateDataObject File: '{0}', Type : {1}", filename, objectType);

            Guard.IsNotNull(destinationFolder, "destinationFolder");
            Guard.IsNotNull(objectType, "objectType");

            if (hasContent)
            {
                Guard.IsNotNull(filename, "fileName");
                if (!fileSystem.File.Exists(filename))
                {
                    throw new FileNotFoundException(string.Format("Could not find file {0}", filename), filename);
                }
            }

            var objectIdentity = new ObjectIdentity(dfsConfiguration.Repository);
            var dataObject = new DataObject(objectIdentity, objectType);

            if (properties != null)
            {
                foreach (var property in properties)
                {
                    dataObject.Properties.Set(property.Key, property.Value);
                }
            }

            if (hasContent)
            {
                dataObject.Contents.Add(new FileContent(filename, format));
            }

            LinkDataObjectToFolder(dataObject, destinationFolder);

            // create a new document linked into parent folder
            var dataPackage = new DataPackage(dataObject);

            dfsContext.ObjectService.Create(dataPackage, null);

            Log.Information("Created document {0} in {1}", dataObject.Properties.Get(DocumentumAttributes.ObjectName).GetValueAsString(), destinationFolder);

            return dataObject;
        }

        public void CreateParentChildRelationship(DataObject parentObject, DataObject childObject, string relationshipType)
        {
            Guard.IsNotNull(parentObject, "parentObject");
            Guard.IsNotNull(childObject, "childObject");
            Guard.IsNotNull(relationshipType, "relationType");

            var relationId = new ObjectIdentity(dfsConfiguration.Repository);
            var relationObject = new DataObject(relationId, DocumentumTypes.DocumentumRelationship);

            relationObject.Properties.Set("relation_name", relationshipType);
            relationObject.Properties.Set("parent_id", parentObject.Identity.GetValueAsString() );
            relationObject.Properties.Set("child_id", childObject.Identity.GetValueAsString() );

            var dataPackage = new DataPackage(relationObject);
            dfsContext.ObjectService.Create(dataPackage, null);           
        }

        private DataPackage GetSingle(ObjectIdentitySet objectIdSet, OperationOptions operationOptions)
        {
            try
            {
                return dfsContext.ObjectService.Get(objectIdSet, operationOptions);
            }
            catch (FaultException<SerializableException> ex)
            {
                if (ex.Message.Contains("There is no object in repository with id"))
                {
                    throw new InvalidOperationException("No matching objects found.", ex);
                }
                if (ex.Message.Contains("multiple objects qualify"))
                {
                    throw new InvalidOperationException("Found more than one matching object", ex);
                }

                throw;
            }
        }

        private void LinkDataObjectToFolder(DataObject dataObject, string folder)
        {
            // add the folder to link to as a ReferenceRelationship
            var objectPath = new ObjectPath(folder);
            var folderIdentity = new ObjectIdentity(objectPath, dfsConfiguration.Repository);

            var folderRelationship = new ReferenceRelationship
            {
                Name = Relationship.RELATIONSHIP_FOLDER,
                Target = folderIdentity,
                TargetRole = Relationship.ROLE_PARENT
            };

            dataObject.Relationships.Add(folderRelationship);
        }

        private ExpressionSet SetSearchExpressionSet(IEnumerable<KeyValuePair<string, string>> searchCriteria)
        {
            var expressionSet = new ExpressionSet();

            foreach (var criteria in searchCriteria)
            {
                expressionSet.AddExpression(new PropertyExpression(criteria.Key, Condition.EQUAL, criteria.Value));
            }

            return expressionSet;
        }

        private ObjectIdentitySet GetObjectIdentitySetByObjectId(string id)
        {
            var objectId = new ObjectId(id);

            return CreateObjectIdentitySet(objectId);
        }

        private ObjectIdentitySet GetObjectIdentitySet(string resourceLocation)
        {
            var objectPath = new ObjectPath(resourceLocation);

            return CreateObjectIdentitySet(objectPath);
        }

        private ObjectIdentitySet CreateObjectIdentitySet(object objectForIdentity)
        {
            var objIdentity = new ObjectIdentity(objectForIdentity, dfsConfiguration.Repository);
            var objectIdSet = new ObjectIdentitySet();
            objectIdSet.Identities.Add(objIdentity);
            return objectIdSet;
        }

        private OperationOptions GetOperationOptionsForRetrievingContent()
        {
            var contentProfile = new ContentProfile
                                 {
                                     FormatFilter = FormatFilter.ANY,
                                     UrlReturnPolicy = UrlReturnPolicy.PREFER
                                 };

            var operationOptions = new OperationOptions { ContentProfile = contentProfile };
            operationOptions.SetProfile(contentProfile);

            return operationOptions;
        }
    }
}