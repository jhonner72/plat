

namespace Lombard.ECLMatchingEngine.UnitTests.Domain
{
    using System;
    using System.Linq;
    using System.Collections.Generic;
    using Microsoft.VisualStudio.TestTools.UnitTesting;
    using Lombard.ECLMatchingEngine.Service.Domain;
    using Lombard.Vif.Service.Messages.XsdImports;

    [TestClass]
    public class UT_Get
    {
        [TestMethod]
        public void Get_GivenAValidVoucherInfoAndECLItemsResult_ShouldReturnOneMatchedVoucher()
        {
            var ECLRecords = MockECLItemsResult();
            var VoucherInfo = MockVoucherInformation();

            var actualObject = Get.MatchVouchers(VoucherInfo, ECLRecords);

            Assert.AreEqual(actualObject.VoucherInformation[0].Voucher.voucher.amount, "100000");
            Assert.AreEqual(actualObject.VoucherInformation[0].Voucher.voucher.auxDom, "233531");
            Assert.AreEqual(actualObject.VoucherInformation[0].Voucher.voucher.accountNumber, "461087460");
            Assert.AreEqual(actualObject.VoucherInformation[0].Voucher.voucher.bsbNumber, "080999");
            Assert.AreEqual(actualObject.VoucherInformation[0].Voucher.voucherProcess.apPresentmentType, APPresentmentTypeEnum.M);
        }

        [TestMethod]
        public void Get_GivenAValidVoucherInfoAndECLItemsResult_ShouldReturnOneUnMatchedVoucher()
        {
            var ECLRecords = MockECLItemsResult();
            var VoucherInfo = MockVoucherInformation();

            var matchedVouchers = Get.MatchVouchers(VoucherInfo, ECLRecords).VoucherInformation.Select(a => a.Voucher).ToArray<VoucherInformation>();
            var actualObject = Get.UnMatchVouchers(VoucherInfo, matchedVouchers);

            Assert.AreEqual(actualObject.VoucherInformation[0].Voucher.voucher.amount, "15728");
            Assert.AreEqual(actualObject.VoucherInformation[0].Voucher.voucher.auxDom, "475651");
            Assert.AreEqual(actualObject.VoucherInformation[0].Voucher.voucher.accountNumber, "NotValid");
            Assert.AreEqual(actualObject.VoucherInformation[0].Voucher.voucher.bsbNumber, "033225");
        }

        private IEnumerable<IECLRecord> MockECLItemsResult()
        {
            return new List<IECLRecord>
            {
                new ECLRecord("D080999 461087460   000233531000000000000000010000041M19/08/15 083029 083309 020211200 41    Z  083894 999999999"),
                new ECLRecord("D033225    XXXXXX   000475651000000000000000001572806M19/08/15 083029 083309 020191750 06    X  083879 999999999")
            };
        }

        private VoucherInfoBatch MockVoucherInformation()
        {
            return new VoucherInfoBatch
            {
                VoucherInformation = new List<IECLRecordVoucherInfo>()
                {
                    new ECLRecordVoucherInfo
                    {
                         Voucher = new VoucherInformation
                                {
                                      voucher =  new Voucher
                                      { 
                                         amount = "100000",
                                         auxDom = "233531",
                                         accountNumber = "461087460",
                                         bsbNumber ="080999"
                                      },
                                       voucherProcess = new VoucherProcess
                                       {
                                            apPresentmentType = APPresentmentTypeEnum.M
                                       }
                                }
                    },
                    new ECLRecordVoucherInfo
                    {
                         Voucher = new VoucherInformation
                            {
                                 voucher = new Voucher
                                 {
                                    amount = "15728",
                                    auxDom = "475651",
                                    accountNumber = "NotValid",
                                    bsbNumber ="033225"
                                 },
                                 voucherProcess = new VoucherProcess
                                   {
                                        apPresentmentType = APPresentmentTypeEnum.M
                                   }
                            }
                    }
                }

            };

        }
    }
}
