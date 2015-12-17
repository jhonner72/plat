using System.Collections.Generic;
using System.Text;
using Lombard.Vif.Service.Domain;
using Microsoft.VisualStudio.TestTools.UnitTesting;

namespace Lombard.Vif.UnitTests.Domain
{
    [TestClass]
    public class VifGeneratorTest
    {
        [TestMethod]
        public void VifGeneratorGenerateVif_GivenValidInputParam_ShouldReturnValidVifStringObject()
        {
            var vifHeader = (VifHeader)GetVifHeader();
            var vifTrailer = (VifTrailer)GetVifTrailer();
            var vifDetails = GetVifDetails();
            var vifGen = new VifGenerator(vifHeader, vifDetails, vifTrailer);
            var actual = vifGen.GenerateVif();

            var expected = new StringBuilder();
            expected.AppendLine("A38356BQL201505200830290820376"+GetEmptyString(120, " ")); //Header
            expected.AppendLine("D06344406344408302983560002 50   100000003000000030000CY  21267121  2126712120000000700000020000000820000000700000000000000000002 0E00302929" + GetEmptyString(10, " ")); //Detail
            expected.AppendLine("D06333306333308302983560002 50   100000004000000020000CY  21111111  2111111120000000500000020000000620000000500000000000000000002 0E00302929" + GetEmptyString(10, " ")); //Detail
            expected.AppendLine("D06555506333308302983560002 12   100000005000000020000DY  21222222  2111111120000000500000020000000620000000700000020000000800002 0E00302929" + GetEmptyString(10, " ")); //Detail
            expected.AppendLine("D06355506333308302983560002 12   100000006000000030000DY  21555555  2111111120000000500000020000000620000000900000020000001000002 0E00302929" + GetEmptyString(10, " ")); //Detail
            expected.AppendLine("Z2015052000000000000050000000000000000500000000200002" + GetEmptyString(40, "0")); //Trailer

            Assert.AreEqual(expected.ToString().Trim(), actual);
        }

        [TestMethod]
        public void VifGeneratorGetHeader_GivenValidInputParam_ShouldReturnVifHeader()
        {
            var vifHeader = (VifHeader) GetVifHeader();
            var vifTrailer = (VifTrailer) GetVifTrailer();
            var vifDetails = GetVifDetails();
            var vifGen = new VifGenerator(vifHeader, vifDetails, vifTrailer);
            var actual = vifGen.GetHeader();

            Assert.AreEqual("A38356BQL201505200830290820376" + GetEmptyString(120, " "), actual);
        }

        [TestMethod]
        public void VifGeneratorGetDetails_GivenValidInputParam_ShouldReturnVifDetails()
        {
            var vifHeader = (VifHeader)GetVifHeader();
            var vifTrailer = (VifTrailer)GetVifTrailer();
            var vifDetails = GetVifDetails();
            var vifGen = new VifGenerator(vifHeader, vifDetails, vifTrailer);
            var actual = vifGen.GetDetails();

            var expected = new StringBuilder();
            expected.AppendLine("D06344406344408302983560002 50   100000003000000030000CY  21267121  2126712120000000700000020000000820000000700000000000000000002 0E00302929" + GetEmptyString(10, " ")); //Detail
            expected.AppendLine("D06333306333308302983560002 50   100000004000000020000CY  21111111  2111111120000000500000020000000620000000500000000000000000002 0E00302929" + GetEmptyString(10, " ")); //Detail
            expected.AppendLine("D06555506333308302983560002 12   100000005000000020000DY  21222222  2111111120000000500000020000000620000000700000020000000800002 0E00302929" + GetEmptyString(10, " ")); //Detail
            expected.AppendLine("D06355506333308302983560002 12   100000006000000030000DY  21555555  2111111120000000500000020000000620000000900000020000001000002 0E00302929" + GetEmptyString(10, " ")); //Detail

            Assert.AreEqual(expected.ToString(), actual);
        }

        [TestMethod]
        public void VifGeneratorGetTrailer_GivenValidInputParam_ShouldReturnVifTrailer()
        {
            var vifHeader = (VifHeader)GetVifHeader();
            var vifTrailer = (VifTrailer)GetVifTrailer();
            var vifDetails = GetVifDetails();
            var vifGen = new VifGenerator(vifHeader, vifDetails, vifTrailer);
            var actual = vifGen.GetTrailer();

            Assert.AreEqual("Z2015052000000000000050000000000000000500000000200002" + GetEmptyString(40, "0"), actual);
        }
        private IEnumerable<VifDetail> GetVifDetails()
        {
            return new List<VifDetail>
            {
                new VifDetail
                {
                    RECORD_TYPE_CODE = "D", LEDGER_BSB = "063444", DEPOSIT_ACCOUNT_BSB = "063444", NEGOTIATING_BSB = "083029",
                    BATCH_NUMBER = "8356", TRANSACTION_ID = "2", TRANSACTION_CODE = "50",
                    UNIQUE_TRACE_ID = "100000003", TRANSACTION_AMOUNT = "30000", DEBIT_CREDIT_CODE = "C", MULTIPLE_CREDIT_FLAG = "Y",
                    DRAWER_ACCOUNT_NUMBER = "21267121", DEPOSIT_ACCOUNT_NUMBER = "21267121", AUX_DOM_1_SHARED = "200000007", EX_AUX_DOM_1_SHARED = "200000008",
                    AUX_DOM_2_SELF = "200000007", EX_AUX_DOM_2_SELF = string.Empty, DEPOSIT_CHEQUE_ITEM_COUNT = "2", DELAY_VOUCHER_INDICATOR = string.Empty,
                    MANUAL_REPAIR_FLAG = "0", PRESENTATION_MODE = "E", POCKET_CUT = string.Empty, BATCH_HEADER_REFERENCE = "3029", CHANNEL_ID = "29",EMPTY_SPACE_FILLER = string.Empty
                },
                 new VifDetail
                {
                    RECORD_TYPE_CODE = "D", LEDGER_BSB = "063333", DEPOSIT_ACCOUNT_BSB = "063333", NEGOTIATING_BSB = "083029",
                    BATCH_NUMBER = "8356", TRANSACTION_ID = "2", TRANSACTION_CODE = "50", 
                    UNIQUE_TRACE_ID = "100000004", TRANSACTION_AMOUNT = "20000", DEBIT_CREDIT_CODE = "C", MULTIPLE_CREDIT_FLAG = "Y",
                    DRAWER_ACCOUNT_NUMBER = "21111111", DEPOSIT_ACCOUNT_NUMBER = "21111111", AUX_DOM_1_SHARED = "200000005", EX_AUX_DOM_1_SHARED = "200000006",
                    AUX_DOM_2_SELF = "200000005", EX_AUX_DOM_2_SELF = string.Empty, DEPOSIT_CHEQUE_ITEM_COUNT = "2", DELAY_VOUCHER_INDICATOR = string.Empty,
                    MANUAL_REPAIR_FLAG = "0", PRESENTATION_MODE = "E", POCKET_CUT = string.Empty, BATCH_HEADER_REFERENCE = "3029", CHANNEL_ID = "29",EMPTY_SPACE_FILLER = string.Empty
                },
                 new VifDetail
                {
                    RECORD_TYPE_CODE = "D", LEDGER_BSB = "065555", DEPOSIT_ACCOUNT_BSB = "063333", NEGOTIATING_BSB = "083029",
                    BATCH_NUMBER = "8356", TRANSACTION_ID = "2", TRANSACTION_CODE = "12", 
                    UNIQUE_TRACE_ID = "   100000005", TRANSACTION_AMOUNT = "20000", DEBIT_CREDIT_CODE = "D", MULTIPLE_CREDIT_FLAG = "Y",
                    DRAWER_ACCOUNT_NUMBER = "21222222", DEPOSIT_ACCOUNT_NUMBER = "21111111", AUX_DOM_1_SHARED = "200000005", EX_AUX_DOM_1_SHARED = "000000200000006",
                    AUX_DOM_2_SELF = "200000007", EX_AUX_DOM_2_SELF = "000000200000008", DEPOSIT_CHEQUE_ITEM_COUNT = "2", DELAY_VOUCHER_INDICATOR = string.Empty,
                    MANUAL_REPAIR_FLAG = "0", PRESENTATION_MODE = "E", POCKET_CUT = string.Empty, BATCH_HEADER_REFERENCE = "3029", CHANNEL_ID = "29",EMPTY_SPACE_FILLER = string.Empty
                },
                 new VifDetail
                {
                    RECORD_TYPE_CODE = "D", LEDGER_BSB = "063555", DEPOSIT_ACCOUNT_BSB = "063333", NEGOTIATING_BSB = "083029",
                    BATCH_NUMBER = "8356", TRANSACTION_ID = "2", TRANSACTION_CODE = "12", 
                    UNIQUE_TRACE_ID = "100000006", TRANSACTION_AMOUNT = "30000", DEBIT_CREDIT_CODE = "D", MULTIPLE_CREDIT_FLAG = "Y",
                    DRAWER_ACCOUNT_NUMBER = "21555555", DEPOSIT_ACCOUNT_NUMBER = "21111111", AUX_DOM_1_SHARED = "200000005", EX_AUX_DOM_1_SHARED = "200000006",
                    AUX_DOM_2_SELF = "200000009", EX_AUX_DOM_2_SELF = "200000010", DEPOSIT_CHEQUE_ITEM_COUNT = "2", DELAY_VOUCHER_INDICATOR = string.Empty,
                    MANUAL_REPAIR_FLAG = "0", PRESENTATION_MODE = "E", POCKET_CUT = string.Empty, BATCH_HEADER_REFERENCE = "3029", CHANNEL_ID = "29",EMPTY_SPACE_FILLER = string.Empty
                }
            };
        }

        private object GetVifTrailer()
        {
            return new VifTrailer
            {
                RECORD_TYPE_CODE = "Z",
                PROCESS_DATE = "20150520",
                TOTAL_CREDIT_AMOUNT = "00000000000050000",
                TOTAL_DEBIT_AMOUNT = "00000000000050000",
                TOTAL_NUMBER_OF_DEBITS = "00002",
                TOTAL_NUMBER_OF_CREDITS = "00002",
                 FILLER =string.Empty
            };
        }

        private object GetVifHeader()
        {
            return new VifHeader
            {
                RECORD_TYPE_CODE = "A",
                STATE_NUMBER = "3",
                RUN_NUMBER = "8356",
                BANK_CODE = "BQL",
                PROCESS_DATE = "20150520",
                CAPTURE_BSB = "083029",
                COLLECTING_BSB = "082037",
                BUNDLE_TYPE = "6",
                EMPTY_SPACE_FILLER = string.Empty
            };
        }

        private string GetEmptyString(int width, string filler)
        {
            string s = string.Empty;
            for (int i=0; i<width; i++)
                s += filler;

            return s;
        }
    }
}
