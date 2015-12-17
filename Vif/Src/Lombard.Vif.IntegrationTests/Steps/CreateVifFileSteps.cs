using System.Configuration;
using System.IO;
using System.Threading;
using Lombard.Vif.IntegrationTests.Hooks;
using Lombard.Vif.Service.Messages.XsdImports;
using Microsoft.VisualStudio.TestTools.UnitTesting;
using Newtonsoft.Json;
using TechTalk.SpecFlow;

namespace Lombard.Vif.IntegrationTests.Steps
{
    [Binding]
    public class CreateVifFileSteps
    {
        [Given(@"some vouchers is in ""(.*)"" job folder")]
        public void GivenSomeVouchersIsInJobFolder(string jobIdentifier)
        {
            string bitLockerLocation = ConfigurationManager.AppSettings["vif:BitLockerLocation"];

            string jobLocation = Path.Combine(bitLockerLocation, "IntegrationTest", jobIdentifier);

            if (Directory.Exists(jobLocation))
            {
                Directory.Delete(jobLocation, true);
            }

            Directory.CreateDirectory(jobLocation);

            string testDataLocation = Path.Combine("TestData", jobIdentifier);

            foreach (var file in Directory.GetFiles(testDataLocation, "VOUCHER_*.JSON"))
            {
                File.Copy(file, file.Replace(testDataLocation, jobLocation));
            }
        }

        [Given(@"a request with job id ""(.*)"" to create a vif file is submitted")]
        public void GivenARequestWithJobIdToCreateAVifFileIsSubmitted(string jobIdentifier)
        {
            string requestJson = File.ReadAllText(Path.Combine("TestData", jobIdentifier, jobIdentifier + ".JSON"));

            var request = JsonConvert.DeserializeObject<CreateValueInstructionFileRequest>(requestJson);

            OutboundServiceBus.Publish(request);
        }

        [When(@"the request is consumed by the service")]
        public void WhenTheRequestIsConsumedByTheService()
        {
            int seconds = 0;
            while(OutboundServiceBus.RequestQueueCount() == 1 && seconds < 5)
            {
                Thread.Sleep(1000);
                seconds++;
            }

            Assert.IsTrue(OutboundServiceBus.RequestQueueCount() == 0);
        }

        [Then(@"a vif file named ""(.*)"" should be generated in ""(.*)"" job folder")]
        public void ThenAVifFileNamedShouldBeGeneratedInJobFolder(string outputFilename, string jobIdentifier)
        {
            string bitLockerLocation = ConfigurationManager.AppSettings["vif:BitLockerLocation"];

            string vifFilePath = Path.Combine(bitLockerLocation, "IntegrationTest", jobIdentifier, outputFilename);

            int seconds = 0;
            while (!File.Exists(vifFilePath) && seconds < 5)
            {
                Thread.Sleep(1000);
                seconds++;
            }

            Assert.IsTrue(File.Exists(vifFilePath));
        }

        [Then(@"the generated vif file named ""(.*)"" in ""(.*)"" job folder is correct")]
        public void ThenTheGeneratedVifFileNamedInJobFolderIsCorrect(string outputFilename, string jobIdentifier)
        {
            string bitLockerLocation = ConfigurationManager.AppSettings["vif:BitLockerLocation"];

            string actualVifFile = File.ReadAllText(Path.Combine(bitLockerLocation, "IntegrationTest", jobIdentifier, outputFilename));
            string expectedVifFile = File.ReadAllText(Path.Combine("TestData", jobIdentifier, outputFilename));

            Assert.AreEqual(expectedVifFile, actualVifFile);
        }
    }
}
