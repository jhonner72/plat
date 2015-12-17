using System;
using System.Diagnostics.CodeAnalysis;
using Newtonsoft.Json;

// ReSharper disable InconsistentNaming
namespace Lombard.Adapters.MftAdapter.Messages
{

    [SuppressMessage("Microsoft.StyleCop.CSharp.NamingRules", "SA1300:ElementMustBeginWithUpperCaseLetter", Justification = "JSON")]
    public class FileReceivedActivityRequest
    {
        [JsonProperty("@type")]
        public string type { get { return "com.fujixerox.aus.lombard.common.receipt.ReceivedFile"; } }

        public string fileIdentifier { get; set; }

        public DateTime receivedDateTime { get; set; }
    }
}
// ReSharper restore InconsistentNaming
