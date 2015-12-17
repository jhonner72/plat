using System;
using System.Data.Entity;
using System.Globalization;
using System.Linq;
using Lombard.Adapters.Data;
using Serilog;

namespace FujiXerox.Adapters.DipsAdapter.Helpers
{
    public class RequestHelper
    {

        //this is a temporary fix to trim the incoming DRN to 9 characters
        //and catch a potential leading 0 in a 9 character DRN
        //both of which is not yet supported by DIPS
        public static string ResolveDocumentReferenceNumber(string s)
        {
            char[] chars = s.Substring(s.Length - (Math.Min(9, s.Length))).ToCharArray();
            if (chars[0] == '0')
                chars[0] = '9';

            return new String(chars);
        }

        public static string ConvertBoolToIntString(bool b)
        {
            return Convert.ToInt32(b).ToString(CultureInfo.InvariantCulture);
        }

        public static string ConvertNullValueToEmptyString(string s)
        {
            return string.IsNullOrEmpty(s) ? " " : s;
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
    }
}
