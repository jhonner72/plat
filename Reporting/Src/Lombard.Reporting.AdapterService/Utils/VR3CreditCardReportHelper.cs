namespace Lombard.Reporting.AdapterService.Utils
{
    using System;
    using System.IO;
    using System.IO.Abstractions;
    using Lombard.Reporting.Data.Domain;

    public class VR3CreditCardReportHelper
    {
        public const string Header = "Header";
        public const string Credit = "Credit";
        public const string Debit = "Debit";

        private readonly IFileSystem fileSystem;
        private readonly string pageHeaderFormat;
        private readonly string reportBodyFormat;
        private readonly string pageFooterFormat;
        private readonly string creditCardPageHeaderFormat;
        private readonly string creditCardReportBodyFormat;
        private readonly string reportFooterFormat;

        public VR3CreditCardReportHelper(IFileSystem fileSystem)
        {
            this.fileSystem = fileSystem;

            this.pageHeaderFormat = fileSystem.File.ReadAllText(@"./Templates/VR3PageHeader.txt");
            this.reportBodyFormat = fileSystem.File.ReadAllText(@"./Templates/VR3ReportBody.txt");
            this.pageFooterFormat = fileSystem.File.ReadAllText(@"./Templates/VR3PageFooter.txt");
            this.reportFooterFormat = fileSystem.File.ReadAllText(@"./Templates/VR3ReportFooter.txt");
            this.creditCardPageHeaderFormat = fileSystem.File.ReadAllText(@"./Templates/VR3CreditCardPageHeader.txt");
            this.creditCardReportBodyFormat = fileSystem.File.ReadAllText(@"./Templates/VR3CreditCardReportBody.txt");
        }

        public virtual void PrintPageHeader(StreamWriter streamWriter, DateTime processDate, DateTime runDate, string batchNumber, string workSourceCode, string workSourceName, string paymentType, string dpNumber, int pageNumber, bool isCreditCard)
        {
            if (isCreditCard)
            {
                streamWriter.WriteLine(string.Format(
                                        this.creditCardPageHeaderFormat,
                                        runDate,
                                        pageNumber,
                                        processDate,
                                        workSourceCode,
                                        workSourceName,
                                        batchNumber,
                                        dpNumber));
            }
            else
            {
                streamWriter.WriteLine(string.Format(
                                        this.pageHeaderFormat,
                                        runDate,
                                        pageNumber,
                                        processDate,
                                        workSourceCode,
                                        workSourceName,
                                        batchNumber,
                                        paymentType,
                                        dpNumber));
            }
        }

        public virtual void PrintReportRow(StreamWriter streamWriter, VR3CreditCardItem item, bool isCreditCard)
        {
            string transactionNumber = item.TransactionNumber;
            if (item.TransactionType.Equals(VR3CreditCardReportHelper.Header, StringComparison.OrdinalIgnoreCase))
            {
                transactionNumber = "0";
            }

            if (isCreditCard)
            {
                streamWriter.WriteLine(
                    string.Format(
                        this.creditCardReportBodyFormat,
                        item.SequenceNumber,
                        item.TransactionType,
                        transactionNumber,
                        item.AuxDom,
                        item.BSB,
                        item.AccountNumber,
                        item.Amount,
                        item.Difference,
                        item.ExtraAuxDom));
            }
            else
            {
                streamWriter.WriteLine(
                    string.Format(
                        this.reportBodyFormat,
                        item.SequenceNumber,
                        item.TransactionType,
                        transactionNumber,
                        item.AuxDom,
                        item.BSB,
                        item.TransactionType.Equals(VR3CreditCardReportHelper.Credit, StringComparison.OrdinalIgnoreCase) ? item.ExtraAuxDom : item.AccountNumber,
                        item.Amount,
                        item.TransactionType.Equals(VR3CreditCardReportHelper.Debit, StringComparison.OrdinalIgnoreCase) ? (decimal?)0m : null));
            }
        }

        public void PrintPageFooter(StreamWriter streamWriter, string batchNumber, int debitCount, int creditCount, decimal batchHeaderAmount, decimal debitAmount, decimal creditAmount, int debitRejectCount, int creditRejectCount)
        {
            streamWriter.Write(string.Format(
                            this.pageFooterFormat, 
                            batchNumber, 
                            debitCount, 
                            batchHeaderAmount, 
                            debitRejectCount,
                            creditCount, 
                            debitAmount, 
                            creditRejectCount,
                            creditAmount,
                            debitAmount - creditAmount));
        }

        public void PrintReportFooter(StreamWriter streamWriter)
        {
            streamWriter.Write(this.reportFooterFormat);
        }

        public virtual bool IsNewBatchNumberGroup(string previousBatchNumber, string currentBatchNumber)
        {
            return !previousBatchNumber.Equals(currentBatchNumber, StringComparison.OrdinalIgnoreCase);
        }

        public void CalculateReportSummary(VR3CreditCardItem item, ref int debitCount, ref int creditCount, ref decimal batchHeaderAmount, ref decimal debitAmount, ref decimal creditAmount)
        {
            if (item.TransactionType.Equals(VR3CreditCardReportHelper.Header, StringComparison.OrdinalIgnoreCase))
            {
                batchHeaderAmount += item.Amount;
            }
            else if (item.TransactionType.Equals(VR3CreditCardReportHelper.Credit, StringComparison.OrdinalIgnoreCase))
            {
                creditCount++;
                creditAmount += item.Amount;
            }
            else if (item.TransactionType.Equals(VR3CreditCardReportHelper.Debit, StringComparison.OrdinalIgnoreCase))
            {
                debitCount++;
                debitAmount += item.Amount;
            }
        }
    }
}
