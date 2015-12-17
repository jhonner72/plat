using System;
using System.IO;
using System.Linq;
using System.Reflection;
using Lombard.ImageExchange.Nab.OutboundService.Helpers;
using Lombard.ImageExchange.Nab.OutboundService.Messages.XsdImports;
using Microsoft.VisualStudio.TestTools.UnitTesting;
using Newtonsoft.Json;
using TechTalk.SpecFlow;
using TechTalk.SpecFlow.Assist;

namespace Lombard.ImageExchange.Nab.IntegrationTests.Steps
{
    [Binding]
    public class GivenVoucherDataSteps
    {
        [Given(@"voucher data has been extracted for jobIdentifier (.*)")]
        public void GivenVoucherDataHasBeenExtractedForJobIdentifier(string jobIdentifier)
        {
            if (string.IsNullOrEmpty(jobIdentifier))
            {
                Assert.Fail("jobIdentifier is not set");
            }

            var bitLockerLocation = System.Configuration.ConfigurationManager.AppSettings["outbound:BitLockerLocation"];

            if (!Directory.Exists(bitLockerLocation))
            {
                Assert.Fail("Please create root directory for BitLockerLocation or check app.config settings");
            }

            var folder = Path.Combine(bitLockerLocation, jobIdentifier);

            if (Directory.Exists(folder))
            {
                foreach (var file in Directory.EnumerateFiles(folder))
                {
                    Console.WriteLine("Deleting existing file {0}", file);
                    File.Delete(file);
                }
            }
            else
            {
                Console.WriteLine("Creating folder {0}", folder);
                Directory.CreateDirectory(folder);
            }
            
            ScenarioContext.Current.Add("jobIdentifier", jobIdentifier);
            ScenarioContext.Current.Add("folder", folder);
        }

        [Given(@"the voucher metadata contains the following data")]
        public void GivenTheVoucherMetadataContainsTheFollowingData(Table table)
        {
            var vouchers = table.CreateSet<Voucher>().ToList();

            if (vouchers.Count() != 1)
            {
                Assert.Inconclusive("Currently only 1 voucher is supported");
            }

            ScenarioContext.Current.Add("voucher", vouchers.Single());
        }

        [Given(@"the voucherBatch metadata contains the following data")]
        public void GivenTheVoucherBatchMetadataContainsTheFollowingData(Table table)
        {
            var voucherBatches = table.CreateSet<VoucherBatch>().ToList();

            if (voucherBatches.Count() != 1)
            {
                Assert.Inconclusive("Currently only 1 voucherBatch is supported");
            }

            ScenarioContext.Current.Add("voucherBatch", voucherBatches.Single());
        }

        [Given(@"the voucherProcess metadata contains the following data")]
        public void GivenTheVoucherProcessMetadataContainsTheFollowingData(Table table)
        {
            var folder = ScenarioContext.Current.Get<string>("folder");
            var voucher = ScenarioContext.Current.Get<Voucher>("voucher");
            var voucherBatch = ScenarioContext.Current.Get<VoucherBatch>("voucherBatch");

            var voucherProcesses = table.CreateSet<VoucherProcess>().ToList();

            if (voucherProcesses.Count() != 1)
            {
                Assert.Inconclusive("Currently only 1 voucherProcess is supported");
            }

            var imageExchangeVoucher = new ImageExchangeVoucher
            {
                voucher = voucher,
                voucherBatch = voucherBatch,
                voucherProcess = voucherProcesses.Single()
            };

            var voucherFilePrefix = string.Format("VOUCHER_{0}_{1}", voucher.processingDate.ToString("ddMMyyyy"), voucher.documentReferenceNumber);
            var voucherFileFullPathAndPrefix = Path.Combine(folder, voucherFilePrefix);

            WriteMetaData(voucherFileFullPathAndPrefix, imageExchangeVoucher);
            WriteImage(voucherFileFullPathAndPrefix, "FRONT");
            WriteImage(voucherFileFullPathAndPrefix, "REAR");
        }

        private static void WriteMetaData(string voucherFileFullPathAndPrefix, ImageExchangeVoucher imageExchangeVoucher)
        {
            var voucherMetaDataFile = string.Format("{0}.json", voucherFileFullPathAndPrefix);

            using (var sw = new StreamWriter(voucherMetaDataFile))
            using (JsonWriter writer = new JsonTextWriter(sw))
            {
                JsonSerializerFactory.Get().Serialize(writer, imageExchangeVoucher);
            }
        }

        private void WriteImage(string voucherFileFullPathAndPrefix, string frontOrBackSuffix)
        {
            var imageFullPathFileName = string.Format("{0}_{1}.jpg", voucherFileFullPathAndPrefix, frontOrBackSuffix);

            using (var stream = Assembly.GetAssembly(typeof(CreateOutboundImageExchangeFileSteps)).GetManifestResourceStream(string.Format("Lombard.ImageExchange.Nab.IntegrationTests.TestImages.{0}.JPG", frontOrBackSuffix)))
            using (var fileStream = File.Create(imageFullPathFileName))
            {
                Assert.IsNotNull(stream);
                stream.Seek(0, SeekOrigin.Begin);
                stream.CopyTo(fileStream);
            }
        }
    }
}
