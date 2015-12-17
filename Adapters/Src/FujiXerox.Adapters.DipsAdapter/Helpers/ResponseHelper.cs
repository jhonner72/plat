using System;
using System.Data.Entity;
using System.Globalization;
using System.Linq;
using Lombard.Adapters.Data;
using Lombard.Adapters.Data.Domain;
using Lombard.Adapters.DipsAdapter.Messages;
using Serilog;

namespace FujiXerox.Adapters.DipsAdapter.Helpers
{
    public class ResponseHelper
    {
        public static void CleanupBatchData(string batchNumber, IDipsDbContext dbContext)
        {
            try
            {
                Log.Debug("Deleting database entries for batch {@batch}", batchNumber);

                var batches = dbContext.Queues.Where(b => b.S_BATCH == batchNumber).ToList();
                var vouchers = dbContext.NabChqPods.Where(v => v.S_BATCH == batchNumber);
                var indexes = dbContext.DbIndexes.Where(i => i.BATCH == batchNumber);

                foreach (var entry in batches.Select(b => dbContext.Entry(b)))
                {
                    entry.Reload();
                    entry.State = EntityState.Deleted;
                }
                foreach (var v in vouchers)
                {
                    dbContext.Entry(v).State = EntityState.Deleted;
                }
                foreach (var i in indexes)
                {
                    dbContext.Entry(i).State = EntityState.Deleted;
                }

                dbContext.SaveChanges();

                Log.Debug("Deleted database entries for batch {@batch}", batchNumber);
            }
            catch (Exception ex)
            {
                Log.Debug(ex, "Could not delete database entries for batch {@batch}", batchNumber);

                throw;
            }
        }

        public static bool ParseStringAsBool(string boolString)
        {
            int boolInt;
            return Int32.TryParse(boolString, out boolInt) && Convert.ToBoolean(boolInt);
        }

        public static int ParseStringAsInt(string intString)
        {
            int outInt;
            return int.TryParse(intString, out outInt) ? outInt : 0;
        }

        public static ExpertBalanceReason GetExpertBalanceReason(string balanceReason)
        {
            ExpertBalanceReason balanceReasonEnum;
            return Enum.TryParse(balanceReason, out balanceReasonEnum) ? balanceReasonEnum : ExpertBalanceReason.None;
        }

        public static DocumentTypeEnum ParseDocumentType(string documentType)
        {

            if (!(string.IsNullOrEmpty(documentType)))
            {
                switch (documentType.Trim())
                {
                    case "CRT":
                        documentType = "CRT";
                        break;
                    case "SUP":
                        documentType = "SUP";
                        break;
                    case "DBT":
                        documentType = "DBT";
                        break;
                    case "HDR":
                        documentType = "HDR";
                        break;
                    case "BH":
                        documentType = "Bh";
                        break;
                    case "CR":
                        documentType = "Cr";
                        break;
                    case "DR":
                        documentType = "Dr";
                        break;
                    case "SP":
                        documentType = "Sp";
                        break;
                    default:
                        documentType = "HDR";
                        break;
                }
            }

            DocumentTypeEnum documentTypeEnum;
            return Enum.TryParse(documentType, out documentTypeEnum) ? documentTypeEnum : DocumentTypeEnum.HDR;
        }

        public static WorkTypeEnum ParseWorkType(string workType)
        {
            if (!(string.IsNullOrEmpty(workType)))
            {
                if (workType.Equals(DipsJobIdType.NabChqPod.ToString()))
                {
                    return WorkTypeEnum.NABCHQ_POD;
                }
            }
            WorkTypeEnum workTypeEnum;
            return Enum.TryParse(workType, out workTypeEnum) ? workTypeEnum : WorkTypeEnum.NABCHQ_POD;
        }

        public static StateEnum ParseState(string processingState)
        {
            StateEnum processingStateEnum;
            return Enum.TryParse(processingState, out processingStateEnum) ? processingStateEnum : StateEnum.VIC;
        }

        public static string TrimString(string s)
        {
            if (!string.IsNullOrEmpty(s))
            {
                return s.Trim();
            }
            else
            {
                return string.Empty;
            }
        }

        //this is a temporary fix to revert a potential leading 9
        //that was rewritten by the DIPS adapter back to 0
        public static string ResolveDocumentReferenceNumber(string s)
        {
            char[] chars = s.ToCharArray();
            if (chars[0] == '9')
            {
                chars[0] = '0';
            }
            return new String(chars);
        }

        public static DateTime ParseDateField(string d)
        {
            DateTime dateField = new DateTime();

            if ((!string.IsNullOrEmpty(d)))
            {
                if (!string.IsNullOrEmpty(d.Trim()))
                {
                    dateField = DateTime.ParseExact(string.Format("{0}", d), "yyyyMMdd", CultureInfo.InvariantCulture);
                }
            }
            return dateField;
        }

        public static bool ParseTpcResult(string s)
        {
            switch (s.Trim())
            {
                case "F":
                case "f":
                    return true;
                default: //"P"
                    return false;
            }
        }

        // ReSharper disable once InconsistentNaming
        public static bool ParseYNStringAsBool(string s)
        {
            if (!string.IsNullOrEmpty(s))
            {
                switch (s.Trim())
                {
                    case "Y":
                    case "y":
                        return true;
                    default: //"N"
                        return false;
                }
            }
            return false;
        }

        public static string ParseAmountField(string s)
        {
            if (string.IsNullOrEmpty(s))
            {
                return ("0");
            }

            if (string.IsNullOrEmpty(s.Trim()))
            {
                return ("0");
            }

            if (s.Trim().Equals("000"))
            {
                return ("0");
            }

            return s.Trim();
        }

        public static bool ParseOverloadedSuspectFraudFlag(string s)
        {
            if (!string.IsNullOrEmpty(s))
            {
                switch (s.Trim())
                {
                    case "2":
                        return true;
                    case "1":
                    case "0":
                        return false;
                    default:
                        return false;
                }
            }
            return false;
        }
    }
}
