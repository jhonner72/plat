using Lombard.Ingestion.Service.Models;
using Serilog;
using System;
using System.IO;

namespace Lombard.Ingestion.Service.Helpers
{
    public class EclIngestionHelper
    {
        public virtual string GetFileState(string filename)
        {
            string fileState = Path.GetExtension(filename).Replace(".", "");

            if (fileState == State.VIC || fileState == State.NSW || fileState == State.WA || fileState == State.SA || fileState == State.QLD)
            {
                return fileState;
            }

            return State.INVALID;
        }

        public virtual string GetRecordType(string record)
        {
            if (!string.IsNullOrWhiteSpace(record))
            {
                string recordType = record.Substring(0, 1);

                if (recordType == RecordType.HEADER || recordType == RecordType.DATA || recordType == RecordType.FOOTER)
                {
                    return recordType;
                }

                Log.Debug("RecordType: {0}", recordType);
            }
            return RecordType.INVALID;
        }

        public virtual DateTime? GetFileDate(string record)
        {
            if (record.Length >= 10)
            {
                string dateExtract = record.Substring(2, 8);

                DateTime fileDate;

                if (DateTime.TryParseExact(dateExtract, "yyyyMMdd", null, System.Globalization.DateTimeStyles.None, out fileDate))
                {
                    return fileDate;
                }
            }

            return null;
        }
    }
}
