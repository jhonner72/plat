using System;
using System.IO.Abstractions;
using Serilog.Extras.Attributed;

namespace Lombard.Common.Domain
{
    public class DishonourLetter
    {
        public string Name { get; private set; }
        public DateTime ProcessingDate { get; private set; }
        public DishonourLetterStatus Status { get; set; }

        [NotLogged]
        public FileInfoBase FileInfo { get; set; }

        public DishonourLetter(string fileName, DishonourLetterStatus status, DateTime processingDate)
        {
            Name = fileName;
            Status = status;
            ProcessingDate = processingDate;
        }

        public static string GetFileName(string auxiliaryDomestic, string bsb, string accountNumber, string amount, DateTime processingDate)
        {
            var fileName = string.Format("{0}_{1}_{2}_{3}_{4}.pdf",
                auxiliaryDomestic,
                bsb,
                accountNumber,
                amount.Replace(",", string.Empty).Replace(".", string.Empty), //for now
                processingDate.ToString("yyyyMMdd"));
            return fileName;
        }
    }
}
