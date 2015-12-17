namespace Lombard.ECLMatchingEngine.UnitTests.Mappers
{
    using System;
    using System.ComponentModel.DataAnnotations;
    using System.IO;
    using System.Collections.Generic;
    using System.IO.Abstractions;
    using Lombard.Common.FileProcessors;
    using Lombard.Common.Queues;
    using Lombard.ECLMatchingEngine.Service.Configuration;
    using Lombard.ECLMatchingEngine.Service.Domain;
    using Lombard.ECLMatchingEngine.Service.MessageProcessors;
    using Lombard.ECLMatchingEngine.Service.Mappers;
    using Lombard.ECLMatchingEngine.Service.Utils;
    using Lombard.ECLMatchingEngine.Service.Constants;
    using Lombard.Vif.Service.Messages.XsdImports;
    using Microsoft.VisualStudio.TestTools.UnitTesting;
    using Moq;

    [TestClass]
    public class UT_MatchedVoucherInformationToECLFileInfo
    {
        private Mock<IECLRecordConfiguration> eclConfiguration;
        private Mock<IFileSystem> eclFileSystem;
        private Mock<iVoucherInfoBatch> voucherInfoBatch;
        private readonly MatchVoucherRequest message;

        public UT_MatchedVoucherInformationToECLFileInfo()
        {
            message = new MatchVoucherRequest
            {
                jobIdentifier = "AnyJobID"
            };
            
            eclConfiguration = new Mock<IECLRecordConfiguration>();
            eclFileSystem = new Mock<IFileSystem>();
            voucherInfoBatch = new Mock<iVoucherInfoBatch>();
        }

        [TestMethod]
        public void MatchedVoucherInformationToECLFileInfo()
        {
            var matchVoucherProcessor = this.GetMatchVoucherInfoToECLFileInfo();
            var mockedData = this.GenerateMockData();

            var actualObject = matchVoucherProcessor.Map(mockedData);

            Assert.AreEqual(actualObject.IsSuccessful, true);
        }

        private MatchedVoucherInformationToECLFileInfo GetMatchVoucherInfoToECLFileInfo()
        {
            return new MatchedVoucherInformationToECLFileInfo(
                eclConfiguration.Object,
                eclFileSystem.Object,
                voucherInfoBatch.Object);
        }

        private VoucherInfoBatch GenerateMockData()
        {
            return new VoucherInfoBatch
            {
                 JobIdentifier = "AnyJobID" ,
                 VoucherInformation = new List<IECLRecordVoucherInfo>() 
                 { 
                     new ECLRecordVoucherInfo
                     {
                          Voucher = new VoucherInformation
                            {
                                voucherBatch = new VoucherBatch{processingState = StateEnum.VIC},
                                voucher = new Voucher
                                {
                                    bsbNumber = "123456",
                                    auxDom = "1234567890",
                                    accountNumber ="1234567890",
                                    extraAuxDom = "",
                                    transactionCode = "E",
                                    amount = "1000",
                                }
                            },
                           MatchedECLRecord = new ECLRecord("D0809991234567890   000233531000000000000000010000041M19/08/15 083029 083309 020211200 41    Z  083894 999999999")
                           {
                                Amount = "1000",
                                ChequeSerialNumber  = "1234567890",
                                DrawerAccountNumber ="1234567890",
                                ECLInput = "AnyECLStringInput",
                                ExchangeModeCode ="",
                                LedgerBSBCode = "123456",
                                DistributionPocketId ="12"
                           }
                     },
                     new ECLRecordVoucherInfo
                     {
                          Voucher = new VoucherInformation
                            {
                                    voucherBatch = new VoucherBatch
                                    {
                                        processingState = StateEnum.NSW
                                    },
                                    voucher = new Voucher
                                    {
                                        bsbNumber = "654321",
                                        auxDom = "0987654321",
                                        accountNumber ="0987654321",
                                        extraAuxDom = "1234567890",
                                        transactionCode = "M",
                                        amount = "2000",
                                    }
                            },
                           MatchedECLRecord = new ECLRecord("D0332250987654321   000475651000000000000000001572806M19/08/15 083029 083309 020191750 06    X  083879 999999999")
                           {
                                Amount = "1000",
                                ChequeSerialNumber  = "0987654321",
                                DrawerAccountNumber ="0987654321",
                                ECLInput = "AnyECLStringInput2",
                                ExchangeModeCode ="",
                                LedgerBSBCode = "654321",
                                DistributionPocketId ="12"
                           }
                     }
                 }
            };
        }
    }
}
