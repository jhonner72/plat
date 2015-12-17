using System.Collections.Generic;
using System.Linq;
using System.Threading;
using Lombard.Adapters.Data.Domain;
using Lombard.Adapters.DipsAdapter.IntegrationTests.Hooks;
using Lombard.Adapters.DipsAdapter.Messages;
using Microsoft.VisualStudio.TestTools.UnitTesting;
using TechTalk.SpecFlow;
using TechTalk.SpecFlow.Assist;

namespace Lombard.Adapters.DipsAdapter.IntegrationTests.Steps
{
    [Binding]
    public class GetVouchersInformationRequestPollingJobSteps
    {
        [Given(@"there are GetVouchersInformationRequest records in the request table")]
        public void Given1(Table table)
        {
            using (var context = GetVouchersInformationBus.CreateContext())
            {
                //Clear table
                context.Database.ExecuteSqlCommand("TRUNCATE TABLE request");

                var dbRows = table.CreateSet<DipsRequest>();

                foreach (var dbRow in dbRows)
                {
                    context.DipsRequest.Add(dbRow);
                }

                context.SaveChanges();
            }
        }

        [When(@"wait for (.*) seconds until GetVouchersInformationRequestPollingJob executed")]
        public void When1(int waitSeconds)
        {
            Thread.Sleep(waitSeconds * 1000);
        }

        private List<GetVouchersInformationRequest> request;

        [Then(@"a GetVouchersInformationRequest (.*) is added to the exchange and contains these information:")]
        public void Then1(string jobIdentifier, Table table)
        {
            if (request == null)
            {
                var task = GetVouchersInformationBus.GetRequestAsync(5);
                task.Wait();

                request = task.Result;

                Assert.IsNotNull(request, "No response received");
            }

            table.CompareToSet(request.Single(_ => _.jobIdentifier == jobIdentifier).searchCriteria);
        }
    }
}
