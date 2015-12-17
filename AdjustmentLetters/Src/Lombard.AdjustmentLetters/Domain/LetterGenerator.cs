using System;
using System.Data;
using System.Globalization;
using System.Linq;
using Aspose.Pdf;
using Aspose.Pdf.Text;
using Lombard.AdjustmentLetters.Configuration;
using Lombard.AdjustmentLetters.Constants;
using Lombard.AdjustmentLetters.Helper;
using Lombard.Vif.Service.Messages.XsdImports;
using Serilog;

namespace Lombard.AdjustmentLetters.Domain
{
    using Rectangle = Rectangle;
    using BorderInfo = BorderInfo;
    using BorderSide = BorderSide;
    using Color = System.Drawing.Color;
    using HorizontalAlignment = HorizontalAlignment;
    using MarginInfo = MarginInfo;
    using Table = Table;

    public interface ILetterGenerator
    {
        Document GeneratePdfFromTemplate(VoucherInformation voucher, string branchName);

        void AddTransactionReport(Document pdf, AdjustmentLetter letter);

        void AddVoucherImage(string jobIdLocation, Document pdf, AdjustmentLetter letter, IFileReader fileReader);
    }

    public class LetterGenerator : ILetterGenerator
    {
        private readonly string pdfTemplate;
        private readonly IAsposeWrapper aspose;

        public LetterGenerator(IAdjustmentLettersConfiguration config, IAsposeWrapper aspose)
        {
            this.pdfTemplate = config.PdfLetterTemplate;

            var license = new License();
            //// Instantiate license file
            license.SetLicense("Aspose.Pdf.lic");
            //// Set the value to indicate that license will be embedded in the application
            license.Embedded = true;

            this.aspose = aspose;
        }

        public Document GeneratePdfFromTemplate(VoucherInformation voucher, string branchName)
        {
            var template = aspose.GetPdfFormTemplate(pdfTemplate);

            foreach (var fieldname in template.FieldNames)
            {
                var field = template.GetFullFieldName(fieldname);
                switch (fieldname)
                {
                    case "Date":
                        {
                            aspose.FillAndFlattenField(template, field, DateTime.Today.ToString("dd MMMM yyyy"));
                            break;
                        }

                    case "NameAddress":
                        {
                            aspose.FillField(template, field, "[NameAddress - first line here]");
                            break;
                        }

                    case "CustomerName":
                        {
                            aspose.FillField(template, field, "[CustomerName]");
                            break;
                        }

                    case "LetterBody":
                        {
                            var bsbNumber = string.Join("-", voucher.voucherBatch.collectingBank.Substring(0, 3), voucher.voucherBatch.collectingBank.Substring(3, 3));
                            var body = "I'm writing to let you know that your deposit on " + voucher.voucher.processingDate.ToShortDateString() + " at " + bsbNumber + ", " + branchName + " branch was found to be out of balance when we processed it. We have now adjusted your deposit.";
                            aspose.FillAndFlattenField(template, field, body);
                            break;
                        }

                    case "BranchNo":
                        {
                            aspose.FillAndFlattenField(template, field, voucher.voucherBatch.collectingBank);
                            break;
                        }

                    case "AccountNo":
                        {
                            aspose.FillField(template, fieldname, voucher.voucher.accountNumber);
                            break;
                        }

                    case "AgentNo":
                        {
                            aspose.FillAndFlattenField(template, field, voucher.voucher.extraAuxDom);
                            break;
                        }

                    case "OriginalDepositAmount":
                        {
                            var dollarAmount = (Convert.ToDecimal(voucher.voucherProcess.preAdjustmentAmount) / 100m).ToString("C2");

                            aspose.FillAndFlattenField(template, field, dollarAmount);
                            break;
                        }

                    case "AdjustmentReason":
                        {
                            aspose.FillAndFlattenField(template, field, voucher.voucherProcess.adjustmentDescription);

                            break;
                        }

                    case "ErrorAmount":
                        {
                            var errorAmount = (Math.Abs(Convert.ToDecimal(voucher.voucher.amount) - Convert.ToDecimal(voucher.voucherProcess.preAdjustmentAmount)) / 100m).ToString("C2");

                            aspose.FillAndFlattenField(template, field, errorAmount);
                            break;
                        }

                    case "AdjustedAmount":
                        {
                            var dollarAmount = (Convert.ToDecimal(voucher.voucher.amount) / 100m).ToString("C2");

                            aspose.FillAndFlattenField(template, field, dollarAmount);

                            break;
                        }

                    case "AdviceReference":
                        {
                            var reference = voucher.voucher.documentReferenceNumber + " [" + voucher.voucherProcess.adjustmentReasonCode + "]";
                            aspose.FillAndFlattenField(template, field, reference);
                            break;
                        }
                }
            }

            return template.Document;
        }

        public void AddVoucherImage(string jobIdLocation, Document pdf, AdjustmentLetter letter, IFileReader fileReader)
        {
            var secondPage = pdf.Pages.Add();

            var drn = letter.AdjustedVoucher.voucher.documentReferenceNumber;
            var fs = fileReader.LoadImage(jobIdLocation, letter.ProcessingDate, drn);

            if (fs != null)
            {
                secondPage.Resources.Images.Add(fs);

                secondPage.Contents.Add(new Operator.GSave());

                //// Create Rectangle and Matrix objects
                var rectangle = new Rectangle(ReportConstants.LowerLeftX, ReportConstants.LowerLeftY, ReportConstants.UpperRightX, ReportConstants.UpperRightY);
                var matrix = new Aspose.Pdf.DOM.Matrix(new[] { rectangle.URX - rectangle.LLX, 0, 0, rectangle.URY - rectangle.LLY, rectangle.LLX, rectangle.LLY });

                //// Using ConcatenateMatrix (concatenate matrix) operator: defines how image must be placed
                secondPage.Contents.Add(new Operator.ConcatenateMatrix(matrix));
                var ximage = secondPage.Resources.Images[secondPage.Resources.Images.Count];

                //// Using Do operator: this operator draws image
                secondPage.Contents.Add(new Operator.Do(ximage.Name));

                //// Using GRestore operator: this operator restores graphics state
                secondPage.Contents.Add(new Operator.GRestore());
            }
            else
            {
                Log.Warning("Image not found for voucher {@voucher} to attach to letter",
                    new { jobIdentifier = letter.JobIdentifier, processingDate = letter.ProcessingDate, DRN = drn });
            }
        }

        public void AddTransactionReport(Document pdf, AdjustmentLetter letter)
        {

            var page = pdf.Pages.Add();
            page.PageInfo.Margin.Left = 35;
            page.PageInfo.Margin.Right = 10;
            page.PageInfo.Width = Aspose.Pdf.Generator.PageSize.A4Width;

            var report = MapVoucherToReport(letter);

            var dataTable = CreateTransactionLines(report);
            BuildReportFromDataTable(dataTable, page, report);
        }

        private static DataTable CreateTransactionLines(Report report)
        {
            var dataTable = new DataTable();
            dataTable.Columns.Add("DIN");
            dataTable.Columns.Add("NEGBSB");
            dataTable.Columns.Add("EAD");
            dataTable.Columns.Add("AD");
            dataTable.Columns.Add("BSB");
            dataTable.Columns.Add("ACCOUNT NO");
            dataTable.Columns.Add("TC");
            dataTable.Columns.Add("DR/CR");
            dataTable.Columns.Add("AMOUNT");

            //// Add the rows to the table
            foreach (var transactionLine in report.VoucherRows)
            {
                dataTable.Rows.Add(transactionLine.Din, transactionLine.NegBsb, transactionLine.Ean,
                                   transactionLine.Ad, transactionLine.Bsb,
                                   transactionLine.AccountNo, transactionLine.TransCode, transactionLine.Drcr,
                                   transactionLine.Amount);
            }

            return dataTable;
        }

        private static Row CreateTableRow(string text, HorizontalAlignment alignment, BorderInfo borderInfo)
        {
            //// Add the header Rows.
            var reportHeadingRow = new Row();
            reportHeadingRow.Cells.Add(text);
            reportHeadingRow.Cells[0].ColSpan = ReportConstants.ColumnSpan;
            reportHeadingRow.Cells[0].Alignment = alignment;
            reportHeadingRow.Border = borderInfo;
            return reportHeadingRow;
        }

        private static void BuildReportFromDataTable(DataTable dataTable, Page page, Report report)
        {
            //// Convert data table
            //// Initializes a new instance of the Table
            var table = new Table
            {
                ColumnWidths = "60 50 60 70 40 60 40 40 90",
                ColumnAdjustment = ColumnAdjustment.Customized,
                RepeatingRowsCount = 4,
                Border = new BorderInfo(BorderSide.Top, .5f, Aspose.Pdf.Color.FromRgb(Color.LightGray)),
                DefaultCellBorder =
                    new BorderInfo(BorderSide.None, 0.0f),
                DefaultCellPadding = new MarginInfo(1, 3, 0, 0),
                Alignment = HorizontalAlignment.Left,
                Margin = new MarginInfo(0, 3, 0, 3)
            };

            if (dataTable != null)
            {
                const int firstFilledRow = 3; ////start at row 4 to leave room for header.
                const bool isColumnNamesShown = true;
                const byte firstFilledColumn = 0;

                table.ImportDataTable(dataTable, isColumnNamesShown, firstFilledRow, firstFilledColumn);
                table.DefaultCellTextState.FontSize = 8;
                table.DefaultCellTextState.FontStyle = FontStyles.Regular;
                table.Alignment = HorizontalAlignment.Left;
                table.Margin = new MarginInfo(6, 3, 5, 3);

                ////A line under the header
                var firstFilledRowObject = table.Rows[firstFilledRow];
                firstFilledRowObject.Border = new BorderInfo(BorderSide.Bottom, 1f, Aspose.Pdf.Color.FromRgb(Color.LightGray));

                var rowCount = table.Rows.Count;
                var colCount = dataTable.Columns.Count;

                //// Align last column RIGHT
                for (var i = 0 + firstFilledRow; i < rowCount; i++)
                {
                    var rowCellsLastColumn = table.Rows[i].Cells[colCount - 1];
                    rowCellsLastColumn.Alignment = HorizontalAlignment.Right;
                }

                var reportHeadingText = "Transaction Input Report for NAB";
                var businessDateText = string.Format("Business Date: {0}", report.HeaderInfo.BusinessDate);
                var reportDateText = string.Format("Report Date: {0}", report.HeaderInfo.ReportOn);

                table.Rows[0] = CreateTableRow(reportHeadingText, HorizontalAlignment.Center, new BorderInfo(BorderSide.None));
                table.Rows[1] = CreateTableRow(businessDateText, HorizontalAlignment.Right, new BorderInfo(BorderSide.None));
                table.Rows[2] = CreateTableRow(reportDateText, HorizontalAlignment.Right, new BorderInfo(BorderSide.None));

                table.Rows[2].Border = new BorderInfo(BorderSide.Bottom, 1f, Aspose.Pdf.Color.FromRgb(Color.LightGray));
                table.Rows[table.Rows.Count - 1].Border = new BorderInfo(BorderSide.Bottom, .5f, Aspose.Pdf.Color.FromRgb(Color.LightGray));

                page.Paragraphs.Add(table);

                ////Add footer as table, was having formatting issues when adding as rows to above table.
                var footer = new Table
                {
                    ColumnWidths = "510",
                    ColumnAdjustment = ColumnAdjustment.Customized,
                    Border = new BorderInfo(BorderSide.None, .5f, Aspose.Pdf.Color.FromRgb(Color.LightGray)),
                    DefaultCellBorder =
                        new BorderInfo(BorderSide.None, 0.0f),
                    DefaultCellPadding = new MarginInfo(1, 3, 0, 0),
                    Alignment = HorizontalAlignment.Left,
                    Margin = new MarginInfo(6, 3, 5, 3),
                    DefaultCellTextState =
                    {
                        FontSize = 8,
                        FontStyle = FontStyles.Regular
                    }
                };

                var totalAmountText =
                       string.Format("Total Credit Amount: {0}             Total Debit Amount: {1}",
                                     FormatAmount(report.FooterInfo.TotalCreditAmount, "Currency"), FormatAmount(report.FooterInfo.TotalDebitAmount, "Currency"));

                var totalCountText =
                    string.Format("Credit Count: {0}               Debit Count: {1}               Total Item Count: {2}",
                                  report.FooterInfo.CreditCount, report.FooterInfo.DebitCount, report.VoucherRows.Count);

                var totalAmountRow = new Row();
                totalAmountRow.Cells.Add(totalAmountText);

                footer.Rows.Add(totalAmountRow);

                var totalCountRow = new Row();
                totalCountRow.Cells.Add(totalCountText);
                totalCountRow.Border = new BorderInfo(BorderSide.Bottom, .5f, Aspose.Pdf.Color.FromRgb(Color.LightGray));
                footer.Rows.Add(totalCountRow);

                page.Paragraphs.Add(footer);
            }

        }

        public Report MapVoucherToReport(AdjustmentLetter letter)
        {
            var voucher = letter.AdjustedVoucher;

            var headerRow = new HeaderRow
            {
                BusinessDate = voucher.voucher.processingDate.ToShortDateString(),
                ReportOn = DateTime.Today.ToShortDateString(),
                Page = "1"
            };

            var voucherList = letter.Vouchers.Select(vouch => new TableRow
            {
                AccountNo = vouch.voucher.accountNumber.ToString(),
                Ad = vouch.voucher.auxDom,
                Amount = FormatAmount(vouch.voucher.amount, "Decimal"),
                Bsb = vouch.voucher.bsbNumber,
                Bch = vouch.voucherBatch.scannedBatchNumber,
                NegBsb = vouch.voucherBatch.collectingBank,
                Drcr = vouch.voucher.documentType.ToString(),
                TransCode = vouch.voucher.transactionCode,
                Ean = vouch.voucher.extraAuxDom,
                Din = vouch.voucher.documentReferenceNumber
            }).ToList();

            var totalCredAmount = letter.Vouchers.Where(x => x.voucher.documentType.ToString().ToUpper() == "CR").Sum(x => Convert.ToDecimal(x.voucher.amount));
            var totalDebAmount = letter.Vouchers.Where(x => x.voucher.documentType.ToString().ToUpper() == "DR").Sum(x => Convert.ToDecimal(x.voucher.amount));

            var footerRow = new FooterRow
            {
                TotalCreditAmount = totalCredAmount.ToString(CultureInfo.InvariantCulture),
                TotalDebitAmount = totalDebAmount.ToString(CultureInfo.InvariantCulture),
                CreditCount = letter.Vouchers.Count(x => x.voucher.documentType.ToString().ToUpper() == "CR").ToString(),
                DebitCount = letter.Vouchers.Count(x => x.voucher.documentType.ToString().ToUpper() == "DR").ToString()
            };


            var report = new Report
            {
                HeaderInfo = headerRow,
                VoucherRows = voucherList,
                FooterInfo = footerRow
            };

            return report;

        }

        public static string FormatAmount(string amount, string formatAs)
        {
            double formattedAmount;
            var strFormattedAmount = amount;
            if (strFormattedAmount.Length < 3)
                strFormattedAmount = strFormattedAmount.PadRight(3, '0');

            var dollarAmount = strFormattedAmount.Substring(0, strFormattedAmount.Length - 2);
            var centAmount = strFormattedAmount.Substring(strFormattedAmount.Length - 2, 2);
            double.TryParse(string.Join(".", dollarAmount, centAmount), out formattedAmount);

            switch (formatAs)
            {
                case "Currency":
                    {
                        strFormattedAmount = formattedAmount.ToString("C2");
                        break;
                    }

                case "Decimal":
                    {
                        strFormattedAmount = formattedAmount.ToString("N");
                        break;
                    }
            }
            return strFormattedAmount;
        }
    }
}
