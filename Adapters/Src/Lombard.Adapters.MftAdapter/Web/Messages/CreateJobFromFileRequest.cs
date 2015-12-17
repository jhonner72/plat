using System.ComponentModel.DataAnnotations;

namespace Lombard.Adapters.MftAdapter.Web.Messages
{
    public sealed class CreateJobFromFileRequest
    {
        [Required]
        [RegularExpression(@"^\S*$",
            ErrorMessage = "The file name is not in the expected format. No spaces in the file name.")]
        public string FileName { get; set; } 
        [Required]
        public string JobSubject { get; set; }
        [Required]
        public string JobPredicate { get; set; }

        public string JobIdentifier { get; set; }

        public string Parameters { get; set; }
    }
}
