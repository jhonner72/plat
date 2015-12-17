using System;
using System.Linq;
using System.Threading;
using Lombard.Documentum.IntegrationTests.Hooks;
using Lombard.Documentum.Service.Messages;
using Microsoft.VisualStudio.TestTools.UnitTesting;
using TechTalk.SpecFlow;

namespace Lombard.Documentum.IntegrationTests.Steps
{
    [Binding]
    public class DocumentumMessageQueueSteps
    {
        [When(@"a (.*) message is sent to the documentum input queue")]
        public void WhenDocumentumMessageWithSpecifiedContentIsSentToQueue(string content)
        {
            var documentumMessage = new DocumentumRequestMessage(content);

            // synchronous Publish will wait until confirmed publish or timeout exception thrown
            MessageBus.Instance.Publish(documentumMessage);
        }

        [Then(@"the (.*) message is processed and a response is sent to the documentum output queue")]
        public void ThenDocumentumMessageWithSpecifiedContentIsConsumedFromQueue(string content)
        {
            const int numberOfSecondsToWaitForMessage = 5;

            bool foundMessage = false;

            // TODO:
            var expectedResponseContent = string.Format("Response to '{0}'", content);

            for (var i = 0; i < numberOfSecondsToWaitForMessage; i++)
            {
                foundMessage = DocumentumScenario.ResponseMessages.Any(msg => msg.Content.Equals(expectedResponseContent));

                if (foundMessage)
                {
                    // Later, we may also want to remove it from the received messages
                    Console.WriteLine("Received documentum message with content = '{0}'.", content);
                    break;
                }

                Console.WriteLine("Documentum Message with content = '{0}' has not yet been received. Will try again in 1 second...", content);

                Thread.Sleep(1000); // wait another second for message to arrive before trying again
            }

            Assert.IsTrue(foundMessage);
        }
    }
}
