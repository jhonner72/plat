using System.Linq;
using System.Net.Http;
using Lombard.Adapters.MftAdapter.IntegrationTests.Hooks;
using Lombard.Adapters.MftAdapter.Messages;
using Lombard.Adapters.MftAdapter.Web.Messages;
using Microsoft.VisualStudio.TestTools.UnitTesting;
using Newtonsoft.Json;
using TechTalk.SpecFlow;

namespace Lombard.Adapters.MftAdapter.IntegrationTests.Steps
{
    [Binding]
    public class CreateJobFromFileSteps
    {
        private string jobIdentifier;

        [Given(@"a new JScape processing ID generated is (.*)")]
        public void WhenANewJScapeProcessingIdGeneratedIs(string jobId)
        {
            jobIdentifier = jobId;
        }

        [When(@"a new (\S*) (\S*) package with file name (\S*) is received from JScape with parameter (\S*)")]
        public void WhenCodelineValidationRequest(string subject, string predicate, string fileName, string parameters)
        {
            var message = new CreateJobFromFileRequest
            {
                FileName = fileName,
                JobSubject = subject,
                JobPredicate = predicate,
                JobIdentifier = jobIdentifier,
                Parameters = parameters == "empty" ? null : parameters
            };

            var httpClient = new HttpClient();
            var url = string.Format("{0}jobs", ConfigurationHelper.MftAdapterApiUrl);
            var task = httpClient.PostAsync(url, new StringContent(JsonConvert.SerializeObject(message), System.Text.Encoding.UTF8, "application/json"));
            task.Wait();

            var response = task.Result;

            response.EnsureSuccessStatusCode();
        }

        [Then(@"a (\S*) (\S*) job message with id (\S*) is sent to the Job Service Exchange with parameter (\S*)")]
        public void ThenDocumentumMessageWithSpecifiedContentIsConsumedFromQueue(string subject, string predicate, string jobId, string parameters)
        {
            var task = JobBus.GetSingleResponseAsync(5);
            task.Wait();

            var response = task.Result;

            Assert.IsNotNull(response, "No response received");
            Assert.IsFalse(string.IsNullOrEmpty(response.jobIdentifier));

            var request = JsonConvert.DeserializeObject<FileReceivedActivityRequest>(response.activity.First().request.ToString());

            Assert.AreEqual(subject, response.subject);
            Assert.AreEqual(predicate, response.predicate);
            Assert.AreEqual(jobId, response.jobIdentifier);
            if (parameters != "empty") { Assert.AreEqual(parameters, JsonConvert.SerializeObject(response.parameters)); }

        }
    }
}
