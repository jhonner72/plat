using System;
using Lombard.Vif.Acknowledgement.Service.Domain;

namespace Lombard.Vif.Acknowledgement.Service.Domain
{
    public interface IAcknowledgmentCode
    {
        string StatusCode { get; set; }
        string ProcessCode { get; set; }
    }
    public class AcknowledgmentCode : IAcknowledgmentCode
    {
        private string vStatusCode;
        private string vProcessCode;
        private string vMessage;

        public AcknowledgmentCode(string statusCode, string processCode)
        {
            vStatusCode = statusCode;
            vProcessCode = processCode;
        }
        public string StatusCode
        {
            get { return vStatusCode; }
            set { vStatusCode = value; }
        }
        public string ProcessCode
        {
            get { return vProcessCode; }
            set { vProcessCode = value; }
        }

        public string Message
        {
            get { return vMessage; }
            set { vMessage = value; }
        }
    }
}
