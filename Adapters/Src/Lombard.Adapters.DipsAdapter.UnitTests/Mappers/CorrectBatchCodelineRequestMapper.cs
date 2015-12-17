using System;
using System.Collections.Generic;
using System.Linq;
using Lombard.Adapters.Data.Domain;
using Lombard.Adapters.DipsAdapter.Configuration;
using Lombard.Adapters.DipsAdapter.Helpers;
using Lombard.Adapters.DipsAdapter.Helpers.Interfaces;
using Lombard.Adapters.DipsAdapter.Mappers;
using Lombard.Adapters.DipsAdapter.Messages;
using Lombard.Common.DateAndTime;
using Microsoft.VisualStudio.TestTools.UnitTesting;
using Moq;

namespace Lombard.Adapters.DipsAdapter.UnitTests.Mappers
{
    [TestClass]
    public class CorrectBatchCodelineRequestMapper
    {
        private Mock<IAdapterConfiguration> adapterConfiguration;
        private Mock<IDateTimeProvider> dateTimeProvider;

        private IBatchCodelineRequestMapHelper dipsBatchMapHelper;
        private CorrectBatchCodelineRequest sampleCorrectBatchCodelineRequest;
        private DipsQueue sampleDipsQueue;
        private IEnumerable<DipsNabChq> sampleDipsNabChqs;
        private IEnumerable<DipsDbIndex> sampleDipsDbIndexes;
        private VoucherBatch voucherBatch;

        [TestInitialize]
        public void TestInitialize()
        {
            adapterConfiguration = new Mock<IAdapterConfiguration>();
            dateTimeProvider = new Mock<IDateTimeProvider>();

            dipsBatchMapHelper = new BatchCodelineRequestMapHelper(dateTimeProvider.Object,
                adapterConfiguration.Object);

            voucherBatch = new VoucherBatch
            {
                scannedBatchNumber = "58300013",
                workType = WorkTypeEnum.NABCHQ_POD,
                processingState = StateEnum.VIC,
                unitID = "123",
                batchType = "testing",
                subBatchType = "tst123",
            };

            sampleCorrectBatchCodelineRequest = new CorrectBatchCodelineRequest
            {
                voucherBatch = voucherBatch,
                voucher = new[]
                {
                    new CorrectCodelineRequest
                    {
                        auxDom = "001193",
                        bsbNumber ="013812",
                        accountNumber="256902729",
                        transactionCode ="50",
                        documentReferenceNumber = "583000026",
                        capturedAmount = "45.67",
                        documentType = DocumentTypeEnum.DBT,
                        accountNumberStatus = true,
                        extraAuxDomStatus = true,
                        auxDomStatus = true,
                        bsbNumberStatus = true,
                        amountStatus = true,
                        transactionCodeStatus = true,
                        processingDate = new DateTime(2015, 4, 16, 14, 14, 14),
                        repostFromProcessingDate = new DateTime(2015, 8, 13, 20, 00, 00),
                        repostFromDRN="",
                        collectingBank="123456",
                    },
                    new CorrectCodelineRequest
                    {
                        auxDom = "001193",
                        bsbNumber ="092002",
                        accountNumber="814649",
                        transactionCode ="50",
                        documentReferenceNumber = "583000027",
                        capturedAmount = "2341.45",
                        documentType = DocumentTypeEnum.CRT,
                        accountNumberStatus = true,
                        extraAuxDomStatus = true,
                        auxDomStatus = false,
                        bsbNumberStatus = false,
                        amountStatus = true,
                        transactionCodeStatus = true,
                        processingDate = new DateTime(2015, 4, 16, 14, 14, 14),
                        repostFromProcessingDate = new DateTime(2015, 8, 13, 20, 00, 00),
                        repostFromDRN="",
                        collectingBank="123456",
                    } 
                }
            };

            sampleDipsQueue = new DipsQueue
            {
                ConcurrencyToken = null,
                CorrelationId = null,
                ResponseCompleted = false,
                S_BATCH = "58300013",
                S_CLIENT = "NabChq                                                                          ",
                S_COMPLETE = "    0",
                S_IMG_PATH = "C:\\Lombard\\Data\\ClientImages\\58300                                              ",
                S_JOB_ID = "NabChqPod                                                                                                                       ",
                S_LOCATION = "CodelineCorrect                  ",
                S_LOCK = "         0",
                S_LOCKMACHINENAME = "                 ",
                S_LOCKMODULENAME = "                 ",
                S_LOCKTIME = "          ",
                S_LOCKUNITID = "          ",
                S_LOCKUSER = "                 ",
                S_MODIFIED = "    0",
                S_PINDEX = "05-1504161414140",
                S_PRIORITY = "    5",
                S_PROCDATE = "         ",
                S_REPORTED = "     ",
                S_SDATE = "16/04/15",
                S_SELNSTRING = "16/04/15 58300013 NabChqPod                                                                                                     ",
                S_STIME = "14:14:14",
                S_TRACE = "583000026",
                S_USERNAME = null,
                S_UTIME = "          ",
                S_VERSION = "4.0.2.152                       "
            };

            sampleDipsNabChqs = new List<DipsNabChq>
            {
                new DipsNabChq
                {			
                    acc_num=	"256902729"	,
		            adj_code=	"  "	,
		            adj_desc=	"                              "	,
                    adjustmentReasonCode = "  ",
                    adjustmentDescription = "                                                            ",
                    adjustedBy = "               ",
                    adjustedFlag = " ",
                    adjustmentLetterRequired = " ",
                    adjustmentType = "         ",
		            amount=	"45.67"	,
		            batch=	"58300013"	,
		            batch_type	="testing"	,
		            bsb_num	="013812"	,
		            collecting_bank	="123456"	,
                    creditNoteFlag="0",
		            delay_ind=	" "	,
		            doc_ref_num	="583000026"	,
		            doc_type	="DBT"	,
		            ead	=" "	,
		            fv_exchange	=" "	,
		            fv_ind	=" "	,
		            held_ind=	" "	,
		            host_trans_no=	"   "	,
		            ie_transaction_id=	"            "	,
		            img_front=	"        "	,
		            img_location=	"                                                                                "	,
		            img_rear	="        "	,
		            job_id	="NabChqPod      "	,
		            man_rep_ind	=""	,
		            manual_repair	="     "	,
		            micr_flag	=" "	,
		            micr_suspect_fraud_flag	=" "	,
		            micr_unproc_flag	=" "	,
		            op_id	="               "	,
		            @override=	"     "	,
		            payee_name=	"                                                                                                                                                                                                                                                "	,
		            pocket=	" 0"	,
                    presentationMode = " ",
		            proc_date=	"20150416"	,
		            proc_time=	"    "	,
		            processing_state=	"VIC"	,
		            proof_seq=	"            "	,
		            raw_micr=	"                                                                "	,
		            raw_ocr=	"                                                                "	,
		            rec_type_id=	"    "	,
		            receiving_bank=	"   "	,
                    repostFromDRN="",
                    repostFromProcessingDate="20150813",
		            S_BALANCE=	"     0"	,
		            S_BATCH=	"58300013"	,
		            S_COMMITTED	="    0"	,
		            S_COMPLETE	="    0"	,
		            S_DEL_IND=	"    0"	,
		            S_IMG1_LEN	="        0"	,
		            S_IMG1_OFF	="        0"	,
		            S_IMG1_TYP	="    0"	,
		            S_IMG2_LEN	="        0"	,
		            S_IMG2_OFF	="        0"	,
		            S_IMG2_TYP	="    0"	,
		            S_LENGTH	="01025"	,
		            S_MODIFIED	="    0"	,
		            S_REPORTED	="    0"	,
		            S_REPROCESS	="    0"	,
		            S_SEQUENCE	="0000 "	,
		            S_STATUS1	="        0"	,
		            S_STATUS2	="        0"	,
		            S_STATUS3	="        0"	,
		            S_STATUS4	="        0"	,
		            S_TRACE=	"583000026"	,
		            S_TYPE	="    0"	,
		            ser_num	="001193"	,
		            sub_batch_type	= "tst123"	,
		            sys_date=	"20150416"	,
		            trace=	"583000026"	,
		            trancode	="50"	,
		            trans_seq=	"     "	,
		            unit_id	="123"	,
		            volume=	"        "
                },
                new DipsNabChq
                {
		            acc_num	="814649"	,
		            adj_code	="  "	,
		            adj_desc=	"                              "	,
                    adjustmentReasonCode = "  ",
                    adjustmentDescription = "                                                            ",
                    adjustedBy = "               ",
                    adjustedFlag = " ",
                    adjustmentLetterRequired = " ",
                    adjustmentType = "         ",
		            amount	="2341.45"	,
		            batch	="58300013"	,
		            batch_type	="testing"	,
		            bsb_num=	"092002"	,
		            collecting_bank	="123456"	,
                    creditNoteFlag="0",
		            delay_ind=	" "	,
		            doc_ref_num	="583000027"	,
		            doc_type=	"CRT"	,
		            ead	=" "	,
		            fv_exchange	=" "	,
		            fv_ind=	" "	,
		            held_ind=	" "	,
		            host_trans_no=	"   "	,
		            ie_transaction_id=	"            "	,
		            img_front=	"        "	,
		            img_location=	"                                                                                "	,
		            img_rear=	"        "	,
		            job_id=	"NabChqPod      "	,
		            man_rep_ind=	""	,
		            manual_repair=	"     "	,
		            micr_flag=	" "	,
		            micr_suspect_fraud_flag	=" "	,
		            micr_unproc_flag=	" "	,
		            op_id=	"               "	,
		            @override	="     "	,
		            payee_name=	"                                                                                                                                                                                                                                                "	,
		            pocket	=" 0"	,
                    presentationMode = " ",
		            proc_date	="20150416"	,
		            proc_time	="    "	,
		            processing_state=	"VIC"	,
		            proof_seq=	"            "	,
		            raw_micr=	"                                                                "	,
		            raw_ocr	="                                                                "	,
		            rec_type_id	="    "	,
		            receiving_bank=	"   "	,
                    repostFromDRN="",
                    repostFromProcessingDate="20150813",
		            S_BALANCE	="     0"	,
		            S_BATCH=	"58300013"	,
		            S_COMMITTED	="    0"	,
		            S_COMPLETE	="    0"	,
		            S_DEL_IND	="    0"	,
		            S_IMG1_LEN	="        0"	,
		            S_IMG1_OFF	="        0"	,
		            S_IMG1_TYP	="    0"	,
		            S_IMG2_LEN	="        0"	,
		            S_IMG2_OFF	="        0"	,
		            S_IMG2_TYP	="    0"	,
		            S_LENGTH	="01025"	,
		            S_MODIFIED	="    0"	,
		            S_REPORTED	="    0"	,
		            S_REPROCESS	="    0"	,
		            S_SEQUENCE	="0000 "	,
		            S_STATUS1	="       96"	,
		            S_STATUS2	="        0"	,
		            S_STATUS3	="        0"	,
		            S_STATUS4	="        0"	,
		            S_TRACE=	"583000027"	,
		            S_TYPE	="    0"	,
		            ser_num	="001193"	,
		            sub_batch_type = "tst123"	,
		            sys_date	="20150416"	,
		            trace=	"583000027"	,
		            trancode=	"50"	,
		            trans_seq=	"     "	,
		            unit_id	="123"	,
		            volume	="        "
                }
            };

            sampleDipsDbIndexes = new List<DipsDbIndex>
            {
                new DipsDbIndex
                {
		            BATCH=	"58300013"	,
		            DEL_IND=	"    0"	,
		            REC_NO	="0         "	,
		            SEQUENCE	="0000 "	,
		            TABLE_NO=	"    0"	,
		            TRACE=	"583000026"
                },
                new DipsDbIndex
                {
		            BATCH =	"58300013"	,
		            DEL_IND=	"    0"	,
		            REC_NO	="0         "	,
		            SEQUENCE	="0000 "	,
		            TABLE_NO=	"    0"	,
		            TRACE=	"583000027"
                }
            };

            ExpectAdapterConfigurationToReturnValues("5", "4.0.2.152", @"C:\Lombard\Data\ClientImages");
            ExpectDateTimeProviderToReturnCurrentTimeInAustralianEasternTimeZone(new DateTime(2015, 4, 16, 14, 14, 14));
        }

        [TestMethod]
        public void WhenMapQueue_ThenReturnList()
        {
            var sut = CreateDipsQueueMapper();
            var actual = sut.Map(sampleCorrectBatchCodelineRequest);

            foreach (var prop in sampleDipsQueue.GetType().GetProperties())
            {
                Assert.AreEqual(prop.GetValue(sampleDipsQueue, null), prop.GetValue(actual, null));
            }
        }

        [TestMethod]
        public void WhenMapVouchers_ThenReturnList()
        {
            var sut = CreateDipsNabChqScanPodMapper();
            var actual = sut.Map(sampleCorrectBatchCodelineRequest);

            foreach (var sampleDipsNabChq in sampleDipsNabChqs)
            {
                // ReSharper disable once PossibleMultipleEnumeration
                var actualObject = actual.SingleOrDefault(x => x.trace.Equals(sampleDipsNabChq.trace));
                var expectedObject = sampleDipsNabChqs.SingleOrDefault(x => x.trace.Equals(sampleDipsNabChq.trace));

                foreach (var prop in sampleDipsNabChq.GetType().GetProperties())
                {
                    Assert.AreEqual(prop.GetValue(expectedObject, null), prop.GetValue(actualObject, null));
                }
            }
        }

        [TestMethod]
        public void WhenMapDbIndexes_ThenReturnList()
        {
            var sut = CreateDipsDbIndexMapper();
            var actual = sut.Map(sampleCorrectBatchCodelineRequest);

            foreach (var sampleDipsDbIndex in sampleDipsDbIndexes)
            {
                // ReSharper disable once PossibleMultipleEnumeration
                var actualObject = actual.SingleOrDefault(x => x.TRACE.Equals(sampleDipsDbIndex.TRACE));
                var expectedObject = sampleDipsDbIndexes.SingleOrDefault(x => x.TRACE.Equals(sampleDipsDbIndex.TRACE));

                foreach (var prop in sampleDipsDbIndex.GetType().GetProperties())
                {
                    Assert.AreEqual(prop.GetValue(expectedObject, null), prop.GetValue(actualObject, null));
                }
            }
        }

        private void ExpectAdapterConfigurationToReturnValues(string dipsPriority, string dbioff32Version, string imagePath)
        {
            adapterConfiguration
                .Setup(x => x.DipsPriority)
                .Returns(dipsPriority);

            adapterConfiguration
                .Setup(x => x.Dbioff32Version)
                .Returns(dbioff32Version);

            adapterConfiguration
                .Setup(x => x.ImagePath)
                .Returns(imagePath);
        }

        private void ExpectDateTimeProviderToReturnCurrentTimeInAustralianEasternTimeZone(DateTime currentTimeInAustralianEasternTimeZone)
        {
            dateTimeProvider
                .Setup(x => x.CurrentTimeInAustralianEasternTimeZone())
                .Returns(currentTimeInAustralianEasternTimeZone);
        }

        private CorrectBatchCodelineRequestToDipsQueueMapper CreateDipsQueueMapper()
        {
            return new CorrectBatchCodelineRequestToDipsQueueMapper(dipsBatchMapHelper);
        }

        private CorrectBatchCodelineRequestToDipsDbIndexMapper CreateDipsDbIndexMapper()
        {
            return new CorrectBatchCodelineRequestToDipsDbIndexMapper(dipsBatchMapHelper);
        }

        private CorrectBatchCodelineRequestToDipsNabChqScanPodMapper CreateDipsNabChqScanPodMapper()
        {
            return new CorrectBatchCodelineRequestToDipsNabChqScanPodMapper(dipsBatchMapHelper);
        }
    }
}