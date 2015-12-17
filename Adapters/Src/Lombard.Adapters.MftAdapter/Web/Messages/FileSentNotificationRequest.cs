using System.ComponentModel.DataAnnotations;

namespace Lombard.Adapters.MftAdapter.Web.Messages
{
    public sealed class FileSentNotificationRequest
    {
        [Required]
        public string Filename { get; set; } 
        [Required]
        public string FileType { get; set; }
    }
}
