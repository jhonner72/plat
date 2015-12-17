using System.IO;
using System.IO.Abstractions;
using System.Linq;
using System.Net.Http;
using System.Threading;
using Lombard.Adapters.MftAdapter.IntegrationTests.Hooks;
using Lombard.Adapters.MftAdapter.Messages;
using Lombard.Adapters.MftAdapter.Messages.XsdImports;
using Lombard.Adapters.MftAdapter.Web.Messages;
using Microsoft.VisualStudio.TestTools.UnitTesting;
using Newtonsoft.Json;
using TechTalk.SpecFlow;

namespace Lombard.Adapters.MftAdapter.IntegrationTests.Steps
{
    [Binding]
    public class VifSteps
    {
        private const string PendingFilename = @"C:\Lombard\Data\Vif\test.pending";
        private const string ExpectedFilename = @"C:\Lombard\Data\Vif\Ready\test.ready";

        [Given(@"a file exists in the Vif pending directory")]
        public void GivenAFileIsExistsInTheVifPendingDirectory()
        {
            using (var sw = File.CreateText(PendingFilename))
            {
                sw.Write("blah");
            }

            File.Delete(ExpectedFilename);
        }

        [When(@"a request is published to send the file")]
        public void WhenARequestIsPublishedToSendTheFile()
        {
            var request = new SendBatchValueInstructionFileRequest
            {
                jobIdentifier = "xxx",
                valueInstructionFile = new[]
                {
                    new SendValueInstructionFileRequest
                    {
                        valueInstructionFileBatchFilename = PendingFilename,
                        valueInstructionFileState = StateEnum.VIC
                    }
                }

            };

            VifBus.Publish(request);

            Thread.Sleep(3000);
        }

        [Then(@"the file should be moved to the correct location")]
        public void ThenTheFileShouldBeMovedToTheCorrectLocation()
        {
            Assert.IsTrue(File.Exists(ExpectedFilename));
        }
    }
}
