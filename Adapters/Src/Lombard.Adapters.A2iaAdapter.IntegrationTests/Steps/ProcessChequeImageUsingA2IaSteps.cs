using System.Linq;
using System.Threading;
using Lombard.Adapters.A2iaAdapter.IntegrationTests.Hooks;
using Lombard.Adapters.A2iaAdapter.Messages.XsdImports;
using Microsoft.VisualStudio.TestTools.UnitTesting;
using TechTalk.SpecFlow;
using TechTalk.SpecFlow.Assist;

namespace Lombard.Adapters.A2iaAdapter.IntegrationTests.Steps
{
    [Binding]
    public class ProcessChequeImageUsingA2IaSteps
    {
        [Given(@"the ICR engine adapter service is running in a well setup environment")]
        public void GivenTheIcrEngineAdapterServiceIsRunningInAWellSetupEnvironment()
        {
            // Check adapter service is running. If not, start the service
        }

        [When(@"a request is received for job identifier (.*) with the following vouchers:")]
        public void WhenAMessageWithJobIdentifierArrivesInQueueLombard_Service_Outclearings_Recognisecourtesyamount_Request_QueueForTheFollowingVouchers(string jobId, Table table)
        {
            var vouchers = table.CreateSet<RecogniseCourtesyAmountRequest>();
            var message = new RecogniseBatchCourtesyAmountRequest
            {
                jobIdentifier = jobId,
                voucher = vouchers.ToArray()
            };

            AutoReadCarBus.Publish(message);

            Thread.Sleep(3000);
        }

        [Then(@"a CAR result for job identifier (.*) with the following values is returned:")]
        public void ThenThereWillBeAMessageInLombard_Service_Outclearings_Recognisecourtesyamount_Response_QueueWithJobIdentifierAndFollowingCARResult(string jobId, Table table)
        {
            var vouchers = table.CreateSet<RecogniseCourtesyAmountResponse>();

            var expectedMessage = new RecogniseBatchCourtesyAmountResponse
            {
                jobIdentifier = jobId,
                voucher = vouchers.ToArray()
            };

            var task = AutoReadCarBus.GetSingleResponseAsync(10, jobId);
            task.Wait();

            var response = task.Result;

            Assert.IsNotNull(response);
            Assert.AreEqual(expectedMessage.jobIdentifier, response.jobIdentifier);
            Assert.AreEqual(expectedMessage.voucher.Length, response.voucher.Length);

            table.CompareToSet(response.voucher);
        }
    }
}
