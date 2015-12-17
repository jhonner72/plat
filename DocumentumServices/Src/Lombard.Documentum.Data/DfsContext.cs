using Emc.Documentum.FS.DataModel.Core.Content;
using Emc.Documentum.FS.DataModel.Core.Context;
using Emc.Documentum.FS.DataModel.Core.Profiles;
using Emc.Documentum.FS.Runtime.Context;
using Emc.Documentum.FS.Services.Core;
using Emc.Documentum.FS.Services.Search;
using Lombard.Common.Configuration;


namespace Lombard.Documentum.Data
{
    public interface IDfsContext
    {
        IObjectService ObjectService { get; }
        ISearchService SearchService { get; }
        IVersionControlService VersionControlService { get; }
    }


    public class DfsContext : IDfsContext
    {
        private readonly ServiceFactory serviceFactory;
        private readonly IServiceContext serviceContext;
        private readonly IDfsConfiguration dfsConfiguration;

        public DfsContext(IDfsConfiguration dfsConfiguration)
        {
            this.dfsConfiguration = dfsConfiguration;
            serviceFactory = ServiceFactory.Instance;

            var contextFactory = ContextFactory.Instance;
            serviceContext = contextFactory.NewContext();

            var repositoryIdentity = new RepositoryIdentity(dfsConfiguration.Repository, dfsConfiguration.UserName, dfsConfiguration.Password, string.Empty);
            serviceContext.AddIdentity(repositoryIdentity);

            var contentTransferProfile = new ContentTransferProfile
            {
                TransferMode = ContentTransferMode.MTOM
            };
            serviceContext.SetProfile(contentTransferProfile);

            // Setting the filter to ALL can cause errors if the DataObject
            // passed to the operation contains system properties, so to be safe 
            // set the filter to ALL_NON_SYSTEM unless you explicitly want to update
            // a system property
            var propertyProfile = new PropertyProfile
            {
                FilterMode = PropertyFilterMode.ALL_NON_SYSTEM
            };
            serviceContext.SetProfile(propertyProfile);

            serviceContext.SetRuntimeProperty("USER_TRANSACTION_HINT", "TRANSACTION_REQUIRED");
        }

        public IObjectService ObjectService
        {
            get
            {
                return serviceFactory.GetRemoteService<IObjectService>(serviceContext, "core", dfsConfiguration.ServiceUrl);
            }
        }

        public ISearchService SearchService
        {
            get
            {
                return serviceFactory.GetRemoteService<ISearchService>(serviceContext, "search", dfsConfiguration.ServiceUrl);

            }
        }

        public IVersionControlService VersionControlService
        {
            get
            {
                return serviceFactory.GetRemoteService<IVersionControlService>(serviceContext, "core", dfsConfiguration.ServiceUrl);
            }
        }
    }
}