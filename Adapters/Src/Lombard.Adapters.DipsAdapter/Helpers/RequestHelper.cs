using System;
using System.Data.Entity;
using System.Globalization;
using System.Linq;
using Lombard.Adapters.Data;
using Serilog;

namespace Lombard.Adapters.DipsAdapter.Helpers
{
    public class RequestHelper
    {
        public static string ConvertBoolToIntString(bool b)
        {
            return Convert.ToInt32(b).ToString(CultureInfo.InvariantCulture);
        }

        public static string ConvertNullValueToEmptyString(string s)
        {
            return string.IsNullOrEmpty(s) ? " " : s;
        }

        public static string ConvertNullableDateToString(DateTime? d)
        {
            if (d == null)
                return string.Empty;

            return ((DateTime)d).ToString("yyyyMMdd");
        }

        public static string ConvertintvalueToString(int s)
        {
            return s.ToString();
        }

        // ReSharper disable once InconsistentNaming
        public static string ConvertBoolToYNString(bool b)
        {
            return b ? "Y" : "N";
        }

        public static string ResolveCollectingBank(string cbVoucher, string cbBatch)
        {
            if (string.IsNullOrEmpty(cbVoucher))
            {
                return cbBatch;
            }
            return cbVoucher;
        }

        public static string ResolveAmountConfidenceLevel(string s)
        {
            if (string.IsNullOrEmpty(s))
            {
                return s;
            }
            if (s.Length > 3)
            {
                return "999";
            }
            
            return s;
        }

        public static void CleanupRequestData(string guidName, IDipsDbContext dbContext)
        {
            try
            {
                Log.Debug("Deleting database entries for request {@guidName}", guidName);

                var requests = dbContext.DipsRequest.Where(b => b.guid_name == guidName).ToList();

                foreach (var request in requests.Select(b => dbContext.Entry(b)))
                {
                    request.Reload();
                    request.State = EntityState.Deleted;
                }

                dbContext.SaveChanges();

                Log.Debug("Deleted database entries for request {@guidName}", guidName);
            }
            catch (Exception ex)
            {
                Log.Debug(ex, "Could not delete database entries for request {@guidName}", guidName);

                throw;
            }
        }

        public static string DocumentumToDipsDocumentTypeConversion(string documentType)
        {
            if (!(string.IsNullOrEmpty(documentType)))
            {
                switch (documentType.ToUpper().Trim())
                {
                    case "CR":
                        return "CRT";
                    case "DR":
                        return "DBT";
                    case "SP":
                        return "SP";
                    case "BH":
                        return "HDR";
                    default:
                        return documentType;
                }
            }

            return documentType;
        }

        public static string InsertedCreditTypeToDipsConversion(string insertedCreditType)
        {
            if (string.IsNullOrEmpty(insertedCreditType))
                return "0";

            switch (insertedCreditType.Trim())
            {
                case "MISSING_CUSTOMER_CREDIT":
                    return "1";
                case "POSTED_SUSPENSE_CREDIT":
                    return "2";
                case "ADJUSTMENT_CREDIT":
                    return "3";
                default:
                    return "0";
            }
        }
    }
}