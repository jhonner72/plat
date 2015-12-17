using System;
using System.Text;
using System.Collections.Generic;
using System.Linq;
using Lombard.AdjustmentLetters.Service.Domain;
using Lombard.Common.FileProcessors;

namespace Lombard.AdjustmentLetters.Mappers
{
    public interface IAdjustmentBatchToReport : IMapper<AdjLetterBatch, ValidatedResponse<AdjLetterBatch>>
    {

    }
    public class AdjustmentBatchToReport
    {
        public AdjustmentBatchToReport()
        {

        }

        public string GetHeader()
        {
            var output = new StringBuilder();
            var header = new HeaderRow
            {
                WorkstationID = "All",
                BusinessDate = DateTime.Today.ToString(),
                ReportOn = DateTime.Today.ToString(),
                Page = "1"
            };

            return output.ToString();
        }
    }
}
