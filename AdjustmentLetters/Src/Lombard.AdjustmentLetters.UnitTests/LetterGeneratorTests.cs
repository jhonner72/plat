using System;
using System.IO;
using System.Drawing.Imaging;
using Microsoft.VisualStudio.TestTools.UnitTesting;
using Aspose.Pdf;
using Aspose.Pdf.InteractiveFeatures.Forms;
using Lombard.AdjustmentLetters.Configuration;
using Lombard.AdjustmentLetters.Domain;
using Lombard.AdjustmentLetters.Helper;
using Lombard.Vif.Service.Messages.XsdImports;
using Moq;

namespace Lombard.AdjustmentLetters.UnitTests
{
    [TestClass]
    public class LetterGeneratorTests
    {
        private Mock<IAdjustmentLettersConfiguration> config;
        private Mock<IAsposeWrapper> aspose;
        private Mock<IFileReader> fileReader;

        [TestInitialize]
        public void TestInitialize()
        {
            config = new Mock<IAdjustmentLettersConfiguration>();
            config.Setup(x => x.BitLockerLocation).Returns("C:\\FakeBitLocker");
            config.Setup(x => x.PdfLetterTemplate).Returns("Template1.pdf");

            aspose = new Mock<IAsposeWrapper>();
            aspose.Setup(x => x.GetPdfFormTemplate(It.IsAny<string>())).Returns(TestData_PdfForm());

            using (var image = new System.Drawing.Bitmap(50, 50))
            {
                var stream = new MemoryStream();
                image.Save(stream, ImageFormat.Bmp);

                fileReader = new Mock<IFileReader>();
                fileReader.Setup(x => x.LoadImage(It.IsAny<string>(), It.IsAny<DateTime>(), It.IsAny<string>())).Returns(stream);
            }
        }


        [TestMethod]
        public void WhenAddImageCalled_ThenCreatePdfPage()
        {
            var letterGenerator = new LetterGenerator(config.Object, aspose.Object);
            var voucher = TestData_VoucherInfo();

            var letter = new AdjustmentLetter { AdjustedVoucher = voucher };
            letter.Vouchers.Add(voucher);

            var jobIdLocation = config.Object.BitLockerLocation;

            var pdf = new Document();

            var count = pdf.Pages.Count;

            letterGenerator.AddVoucherImage(jobIdLocation, pdf, letter, fileReader.Object);

            Assert.AreEqual(count + 1, pdf.Pages.Count);
        }

        [TestMethod]
        public void WhenGenerateLetterCalled_ThenCreateDocument()
        {
            var letterGenerator = new LetterGenerator(config.Object, aspose.Object);
            var voucher = TestData_VoucherInfo();
            const string testBranch = "Test";

            var pdf = letterGenerator.GeneratePdfFromTemplate(voucher, testBranch);

            Assert.IsInstanceOfType(pdf, typeof(Document));
            aspose.Verify(x => x.GetPdfFormTemplate(It.IsAny<string>()), Times.Once);
            aspose.Verify(x => x.FillAndFlattenField(It.IsAny<Aspose.Pdf.Facades.Form>(), It.IsAny<string>(), It.IsAny<string>()), Times.AtLeastOnce);
            aspose.Verify(x => x.FillField(It.IsAny<Aspose.Pdf.Facades.Form>(), It.IsAny<string>(), It.IsAny<string>()), Times.AtLeastOnce);

        }

        [TestMethod]
        public void WhenGenerateReportCalled_ThenCreateReportPage()
        {
            var letterGenerator = new LetterGenerator(config.Object, aspose.Object);
            var voucher = TestData_VoucherInfo();

            var letter = new AdjustmentLetter { AdjustedVoucher = voucher };
            letter.Vouchers.Add(voucher);

            var pdf = new Document();

            var count = pdf.Pages.Count;

            letterGenerator.AddTransactionReport(pdf, letter);

            Assert.AreEqual(count + 1, pdf.Pages.Count);
        }

        private VoucherInformation TestData_VoucherInfo()
        {
            var voucher = new Voucher
            {
                documentReferenceNumber = "04114",
                bsbNumber = "082401",
                auxDom = string.Empty,
                accountNumber = "813208132",
                amount = "419.38",
                documentType = DocumentTypeEnum.Dr,
                transactionCode = "01",
                processingDate = new DateTime(2015, 01, 01)
            };

            var voucherProcess = new VoucherProcess
            {
                adjustedFlag = true,
                adjustmentDescription = "test description",
                adjustmentReasonCode = 00,
                transactionLinkNumber = "9000001"
            };

            var voucherBatch = new VoucherBatch
            {
                scannedBatchNumber = "67500125",
                collectingBank = "082401"
            };

            var voucherInfo = new VoucherInformation
            {
                voucher = voucher,
                voucherProcess = voucherProcess,
                voucherBatch = voucherBatch
            };

            return voucherInfo;
        }

        public Aspose.Pdf.Facades.Form TestData_PdfForm()
        {
            var pdf = new Document();
            var page = pdf.Pages.Add();

            pdf.Form.Add(CreateTextBoxField(page, "Date"));
            pdf.Form.Add(CreateTextBoxField(page, "NameAddress"), page.Number);
            pdf.Form.Add(CreateTextBoxField(page, "LetterBody"), page.Number);
            pdf.Form.Add(CreateTextBoxField(page, "AccountNo"), page.Number);

            Aspose.Pdf.Facades.Form pdfForm = new Aspose.Pdf.Facades.Form();
            pdfForm.BindPdf(pdf);

            return pdfForm;
        }

        private static TextBoxField CreateTextBoxField(Page page, string name)
        {
            var textBoxField = new TextBoxField(page, new Rectangle(100, 200, 300, 400))
            {
                Name = name,
                PartialName = name,
                MappingName = name,
                AlternateName = name + 1
            };
            var border = new Aspose.Pdf.InteractiveFeatures.Annotations.Border(textBoxField)
            {
                Width = 5,
                Dash = new Aspose.Pdf.InteractiveFeatures.Annotations.Dash(1, 1)
            };
            textBoxField.Border = border;
            textBoxField.Value = "";

            return textBoxField;
        }

        //Below test data might be useful once tests are needed.
        /*
        private Report GetDataForReport()
        {
            try
            {
                var transactions = new List<TableRow>();
                var report = new Report
                {
                    HeaderInfo = new HeaderRow
                    {
                        BusinessDate = DateTime.Today.ToShortDateString(),
                        ReportOn = DateTime.Today.ToShortDateString(),
                        WorkstationID = "ALL"
                    },
                    FooterInfo = new FooterRow
                    {
                        CreditCount = "2",
                        DebitCount = "2",
                        //ItemCount = 27,
                        TotalCreditAmount = "0.00",
                        TotalDebitAmount = "0.00"
                    },
                    VoucherRows = transactions
                };

                for (int i = 0; i < 27; i++)
                {
                    var newTransactionLine = new TableRow
                    {
                        Din = "04114",
                        NegBSB = "082401",
                        AD = string.Empty,
                        BSB = string.Empty,
                        AccountNo = "813208132",
                        Amount = "419.38",
                        DRCR = "DR",
                        TransCode = null
                    };

                    newTransactionLine.Din = newTransactionLine.Din + i;

                    // For the dummy data, I will randomise it a bit to simuate a report
                    var random = new Random();
                    var newAd = random.Next(003941, 17092299);
                    var decimalLength = newAd.ToString("D").Length + 5;
                    newTransactionLine.AD = newAd.ToString("D" + decimalLength.ToString());

                    var newBsb = random.Next(0, 99999);
                    newTransactionLine.BSB = string.Format("0{0}", newBsb);
                    var newAccountNo = random.Next(121374, 813208132);
                    newTransactionLine.AccountNo = newAccountNo.ToString();
                    if (i == 10 || i == 11)
                    {
                        newTransactionLine.TransCode = "9";
                    }
                    if (i == 25 || i == 26)
                    {
                        newTransactionLine.TransCode = "95";
                        newTransactionLine.DRCR = "CR";
                    }

                    var newAmount = random.NextDouble() + 1 * (i * 100);
                    newTransactionLine.Amount = newAmount.ToString("#,##0.00");

                    transactions.Add(newTransactionLine);
                }
                report.VoucherRows = transactions;
                var creditTotal =
                    report.VoucherRows.Where(drcr => drcr.DRCR == "CR").Sum(a => Convert.ToDecimal(a.Amount));
                var debitTotal = report.VoucherRows.Where(drcr => drcr.DRCR == "DR").Sum(a => Convert.ToDecimal(a.Amount));
                report.FooterInfo.TotalCreditAmount = creditTotal.ToString();
                report.FooterInfo.TotalDebitAmount = debitTotal.ToString();

                return report;
            }
            catch (Exception exception)
            {


                return new Report
                {
                    HeaderInfo = new HeaderRow
                    {
                        BusinessDate = DateTime.Today.ToShortDateString(),
                        ReportOn = DateTime.Today.ToShortDateString(),
                        WorkstationID = "ALL"
                    },
                    FooterInfo = new FooterRow(),
                    VoucherRows = new List<TableRow>()
                };
            }
         
        }
        */
    }
}
