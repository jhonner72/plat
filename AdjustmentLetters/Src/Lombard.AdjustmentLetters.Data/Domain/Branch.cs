using System.ComponentModel.DataAnnotations;
// ReSharper disable InconsistentNaming

namespace Lombard.AdjustmentLetters.Data.Domain
{
    public class Branch
    {
        [Key]
        public string bank_code { get; set; }

        [Key]
        public string branch_bsb { get; set; }

        public string branch_name { get; set; }
    }
}
