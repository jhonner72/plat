namespace Lombard.Reporting.AdapterService.Utils
{
    using System;
    using System.Collections.Generic;
    using System.IO;
    using System.IO.Abstractions;
    using System.Linq;
    using Lombard.Reporting.AdapterService.Extensions;
    using Lombard.Reporting.Data.Domain;

    public class AllItemsReportHelper
    {
        private const int STATE_LEN = 10;
        private const int RUN_NUMBER_LEN = 4;
        private const int BATCH_NUMBER_LEN = 10;
        private const int TRANSACTION_NUMBER_LEN = 10;
        private const int DRN_LEN = 13;
        private const int COLLECTING_BSB_LEN = 10;
        private const int EXTRA_AUX_DOM_LEN = 15;
        private const int AUX_DOM_LEN = 12;
        private const int BSB_LEN = 10;
        private const int ACCOUNT_NUMBER_LEN = 18;
        private const int TRAN_CODE_LEN = 6;
        private const int CLASSIFICATION_LEN = 9;

        private const int MAX_ROW_COUNT_PER_PAGE = 18;

        private readonly IFileSystem fileSystem;
        private readonly string reportHeaderFormat;
        private readonly string reportHeaderEmptyFormat;
        private readonly string reportBodyFormat;
        private readonly string reportFooterFormat;

        public AllItemsReportHelper(IFileSystem fileSystem)
        {
            this.fileSystem = fileSystem;

            this.reportHeaderFormat = fileSystem.File.ReadAllText(@"./Templates/AllItemsReportHeader.txt");
            this.reportHeaderEmptyFormat = fileSystem.File.ReadAllText(@"./Templates/AllItemsReportHeaderEmpty.txt");
            this.reportBodyFormat = fileSystem.File.ReadAllText(@"./Templates/AllItemsReportBody.txt");
            this.reportFooterFormat = fileSystem.File.ReadAllText(@"./Templates/AllItemsReportFooter.txt");
        }

        public virtual void PrintEmptyReport(StreamWriter streamWriter, DateTime processDate, string shortName)
        {
            streamWriter.Write(string.Format(this.reportHeaderEmptyFormat, shortName, processDate));
        }

        public virtual void PrintReportFooter(StreamWriter streamWriter, IList<AllItem> allItems)
        {
            int drCount = allItems.Count(ai => ai.Classification == "Dr");
            int crCount = allItems.Count(ai => ai.Classification == "Cr");

            decimal drAmount = allItems.Where(ai => ai.Classification == "Dr").Sum(ai => ai.Amount);
            decimal crAmount = allItems.Where(ai => ai.Classification == "Cr").Sum(ai => ai.Amount);

            streamWriter.WriteLine(string.Format(this.reportFooterFormat, drCount, drAmount, crCount, crAmount));
        }

        public virtual void PrintReportRow(StreamWriter streamWriter, AllItem item)
        {
            string runNumber = string.Empty;
            int parsedRunNumber;
            if (int.TryParse(item.RunNumber, out parsedRunNumber) && parsedRunNumber >= 8000 && parsedRunNumber <= 9999)
            {
                runNumber = item.RunNumber;
            }

            streamWriter.WriteLine(string.Format(
                this.reportBodyFormat,
                runNumber.Truncate(RUN_NUMBER_LEN),
                item.BatchNumber.Truncate(BATCH_NUMBER_LEN),
                item.TransactionNumber.Truncate(TRANSACTION_NUMBER_LEN),
                item.Drn.Truncate(DRN_LEN),
                item.CollectingBSB.Truncate(COLLECTING_BSB_LEN),
                item.ExtraAuxDom.Truncate(EXTRA_AUX_DOM_LEN),
                item.AuxDom.Truncate(AUX_DOM_LEN),
                item.BSB.Truncate(BSB_LEN),
                item.AccountNumber.Truncate(ACCOUNT_NUMBER_LEN),
                item.TranCode.Truncate(TRAN_CODE_LEN),
                item.Classification.Truncate(CLASSIFICATION_LEN),
                item.Amount));
        }

        public virtual bool IsNewBatchNumberGroup(string previousBatchNumber, string currentBatchNumber)
        {
            return !previousBatchNumber.Equals(currentBatchNumber, StringComparison.OrdinalIgnoreCase);
        }

        public virtual bool IsNewTransactionGroup(string previousTransactionNumber, string currentTransactionNumber)
        {
            return !previousTransactionNumber.Equals(currentTransactionNumber, StringComparison.OrdinalIgnoreCase);
        }

        public virtual bool IsStartNewPage(int pageRowCount)
        {
            return pageRowCount % MAX_ROW_COUNT_PER_PAGE == 0;
        }

        public virtual void PrintPageHeader(StreamWriter streamWriter, DateTime processDate, DateTime runDate, string shortName, string state, int pageNumber)
        {
            if (pageNumber > 1)
            {
                streamWriter.WriteLine();
            }

            streamWriter.WriteLine(string.Format(
                this.reportHeaderFormat,
                1,
                shortName,
                runDate,
                state.Truncate(STATE_LEN),
                processDate,
                pageNumber));
        }
    }
}
