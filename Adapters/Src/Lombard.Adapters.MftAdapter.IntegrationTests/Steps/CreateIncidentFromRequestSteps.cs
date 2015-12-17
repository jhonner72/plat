using System.Net.Http;
using Lombard.Adapters.MftAdapter.IntegrationTests.Hooks;
using Lombard.Adapters.MftAdapter.Web.Messages;
using Microsoft.VisualStudio.TestTools.UnitTesting;
using Newtonsoft.Json;
using TechTalk.SpecFlow;
using TechTalk.SpecFlow.Assist;

namespace Lombard.Adapters.MftAdapter.IntegrationTests.Steps
{
    [Binding]
    public class CreateIncidentFromRequestSteps
    {
        [Given(@"a new incident request from HTTP")]
        public void GivenANewIncidentRequestFromHttp()
        {
        }

        [When(@"a new incident message request with RoutingKey (.*)")]
        public void WhenANewIncidentMessageRequestWithRoutingKeyEmpty(string routingKey, Table table)
        {
            var message = table.CreateInstance<CreateIncidentRequest>();

            var httpClient = new HttpClient();
            var url = string.Format("{0}incidents", ConfigurationHelper.MftAdapterApiUrl);
            var task = httpClient.PostAsync(url, new StringContent(JsonConvert.SerializeObject(message), System.Text.Encoding.UTF8, "application/json"));
            task.Wait();

            var response = task.Result;

            response.EnsureSuccessStatusCode();
        }

        [Then(@"an incident message is sent to the Incident Service Exchange with RoutingKey (.*)")]
        public void ThenAnIncidentMessageIsSentToTheIncidentServiceExchangeWithRoutingKeyEmpty(string routingKey, Table table)
        {
            var task = IncidentBus.GetSingleResponseAsync(5);
            task.Wait();

            var response = task.Result;

            Assert.IsNotNull(response, "No response received");

            table.CompareToInstance(response);
        }
    }
}
