using System.Collections.Generic;
using System.Text;
using Lombard.Vif.Service.Domain;
using Microsoft.VisualStudio.TestTools.UnitTesting;
using TechTalk.SpecFlow;

namespace Lombard.Vif.IntegrationTests.Steps
{
    [Binding]
    public class GenerateVifSteps
    {
        private VifHeader header;
        private List<VifDetail> details;
        private VifTrailer trailer;

        private string output;

        [Given(@"header string added to the output")]
        public void GivenHeaderStringAddedToTheOutput()
        {
            header = new VifHeader
            {
                RECORD_TYPE_CODE = "A",
                STATE_NUMBER = "3",
                RUN_NUMBER = "1206",
                BANK_CODE = "NAB",
                PROCESS_DATE = "20141021",
                CAPTURE_BSB = "083340",
                COLLECTING_BSB = "083029",
                BUNDLE_TYPE = "1",
                EMPTY_SPACE_FILLER = string.Empty
            };
        }

        [Given(@"detail string added to the output")]
        public void GivenDetailStringAddedToTheOutput()
        {
            details = new List<VifDetail>
            {
                new VifDetail
                {
                    RECORD_TYPE_CODE = "D",
                    LEDGER_BSB = "182200",
                    DEPOSIT_ACCOUNT_BSB = "082604",
                    NEGOTIATING_BSB = "083340",
                    BATCH_NUMBER = "0001",
                    TRANSACTION_ID = "0001",
                    TRANSACTION_CODE = string.Empty,
                    UNIQUE_TRACE_ID = "120532753",
                    TRANSACTION_AMOUNT = "000000144400",
                    DEBIT_CREDIT_CODE = "D",
                    MULTIPLE_CREDIT_FLAG = string.Empty,
                    DRAWER_ACCOUNT_NUMBER = "10000194",
                    DEPOSIT_ACCOUNT_NUMBER = "235286605",
                    AUX_DOM_1_SHARED = "000000001",
                    EX_AUX_DOM_1_SHARED = string.Empty,
                    AUX_DOM_2_SELF = "000000013",
                    EX_AUX_DOM_2_SELF = string.Empty,
                    DEPOSIT_CHEQUE_ITEM_COUNT = "00001",
                    DELAY_VOUCHER_INDICATOR = string.Empty,
                    MANUAL_REPAIR_FLAG = "0",
                    PRESENTATION_MODE = "E",
                    POCKET_CUT = "44",
                    BATCH_HEADER_REFERENCE = "4056",
                    CHANNEL_ID = "12",
                    EMPTY_SPACE_FILLER = string.Empty
                },
                new VifDetail
                {
                    RECORD_TYPE_CODE = "D",
                    LEDGER_BSB = "182200",
                    DEPOSIT_ACCOUNT_BSB = "082604",
                    NEGOTIATING_BSB = "083340",
                    BATCH_NUMBER = "0001",
                    TRANSACTION_ID = "0001",
                    TRANSACTION_CODE = "095",
                    UNIQUE_TRACE_ID = "120532754",
                    TRANSACTION_AMOUNT = "000000144400",
                    DEBIT_CREDIT_CODE = "C",
                    MULTIPLE_CREDIT_FLAG = string.Empty,
                    DRAWER_ACCOUNT_NUMBER = "235286605",
                    DEPOSIT_ACCOUNT_NUMBER = "235286605",
                    AUX_DOM_1_SHARED = "000000001",
                    EX_AUX_DOM_1_SHARED = string.Empty,
                    AUX_DOM_2_SELF = "000000001",
                    EX_AUX_DOM_2_SELF = string.Empty,
                    DEPOSIT_CHEQUE_ITEM_COUNT = "00001",
                    DELAY_VOUCHER_INDICATOR = string.Empty,
                    MANUAL_REPAIR_FLAG = "0",
                    PRESENTATION_MODE = "E",
                    POCKET_CUT = "44",
                    BATCH_HEADER_REFERENCE = "4056",
                    CHANNEL_ID = "12",
                    EMPTY_SPACE_FILLER = string.Empty
                }
            };
        }

        [Given(@"trailer string added to the output")]
        public void GivenTrailerStringAddedToTheOutput()
        {
            trailer = new VifTrailer
            {
                RECORD_TYPE_CODE = "Z",
                PROCESS_DATE = "20141021",
                TOTAL_CREDIT_AMOUNT = "0000000020505",
                TOTAL_DEBIT_AMOUNT = "0000000000205",
                TOTAL_NUMBER_OF_DEBITS = "05000",
                TOTAL_NUMBER_OF_CREDITS = "00250",
                FILLER = string.Empty
            };
        }

        [When(@"string from object called")]
        public void WhenStringFromObjectCalled()
        {
            var generator = new VifGenerator(header, details, trailer);
            output = generator.GenerateVif();
        }

        [Then(@"the result should be expected string format")]
        public void ThenTheResultShouldBeExpectedStringFormat()
        {
            var expected = new StringBuilder();
            expected.AppendLine("A31206NAB201410210833400830291" + GetEmptyString(120, " ")); //Header
            expected.AppendLine("D18220008260408334000010001      120532753000000144400D   10000194 23528660500000000100000000000000000000001300000000000000000001 0E44405612" + GetEmptyString(10, " ")); //Detail
            expected.AppendLine("D18220008260408334000010001095   120532754000000144400C  235286605 23528660500000000100000000000000000000000100000000000000000001 0E44405612" + GetEmptyString(10, " ")); //Detail
            expected.Append("Z2014102100000000002050500000000000020505000002500000000000000000000000000000000000000000"); //Trailer

            Assert.AreEqual(expected.ToString(), output);
        }

        private string GetEmptyString(int width, string filler)
        {
            string s = string.Empty;
            for (int i = 0; i < width; i++)
                s += filler;

            return s;
        }
    }
}
