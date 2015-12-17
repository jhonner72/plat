using System;
using System.Collections.Generic;

namespace Lombard.AdjustmentLetters.Domain
{
    public class AdjLetterBatch
    {
        public string PdfZipFilename { get; set; }

        public string JobIdentifier { get; set; }

        public DateTime ProcessingDate { get; set; }

        public string JobFolderLocation { get; set; }

        public List<AdjustmentLetter> Letters { get; set; }

        public AdjLetterBatch()
        {
            Letters = new List<AdjustmentLetter>();
        }
    }
}
