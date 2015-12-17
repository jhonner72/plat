using System.Net.Http;
using Lombard.Adapters.MftAdapter.IntegrationTests.Hooks;
using Lombard.Adapters.MftAdapter.Web.Messages;
using Microsoft.VisualStudio.TestTools.UnitTesting;
using Newtonsoft.Json;
using TechTalk.SpecFlow;

namespace Lombard.Adapters.MftAdapter.IntegrationTests.Steps
{
    [Binding]
    public class CreateCopyImageFromRequestSteps
    {
        private string fileName;

        [Given(@"a new processing ID from file name is (.*)")]
        public void GivenANewProcessingIdFromFileNameIs(string name)
        {
            fileName = name;
        }

        [When(@"a new copy image message request body is (.*) with RoutingKey (.*)")]
        public void WhenANewCopyImageMessageRequestBodyIs(string name, string routingKey)
        {
            var message = new CopyImageRequest
            {
                FileName = name,
                RoutingKey = routingKey == "empty" ? null : routingKey
            };

            var httpClient = new HttpClient();
            var url = string.Format("{0}copyimages", ConfigurationHelper.MftAdapterApiUrl);
            var task = httpClient.PostAsync(url, new StringContent(JsonConvert.SerializeObject(message), System.Text.Encoding.UTF8, "application/json"));
            task.Wait();

            var response = task.Result;

            response.EnsureSuccessStatusCode();
        }

        [Then(@"a copy image message with id (.*) is sent to the CopyImage Service Exchange with RoutingKey (.*)")]
        public void ThenACopyImageMessageWithIdIsSentToTheCopyImageServiceExchange(string name, string routingKey)
        {
            var task = CopyImageBus.GetSingleResponseAsync(5);
            task.Wait();

            var response = task.Result;

            Assert.IsNotNull(response, "No response received");
            Assert.IsFalse(string.IsNullOrEmpty(response));

            Assert.AreEqual(name, response);
        }
    }
}
