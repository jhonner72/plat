using System.ComponentModel.DataAnnotations;

namespace Lombard.Adapters.MftAdapter.Web.Messages
{
    public sealed class CreateIncidentRequest
    {
        [Required]
        public string JobSubject { get; set; }
        [Required]
        public string JobPredicate { get; set; }

        public string JobIdentifier { get; set; }

        public string Details { get; set; }
    }
}
