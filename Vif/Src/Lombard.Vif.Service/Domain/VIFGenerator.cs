using System.Collections.Generic;
using System.Text;

namespace Lombard.Vif.Service.Domain
{
    public interface IVifGenerator
    {
        string GetHeader();
        string GetDetails();
        string GetTrailer();
        string GenerateVif();
    }

    public class VifGenerator : IVifGenerator
    {
        private readonly VifHeader header;
        private readonly IEnumerable<VifDetail> details;
        private readonly VifTrailer trailer;

        public VifGenerator(VifHeader header, IEnumerable<VifDetail> details, VifTrailer trailer)
        {
            this.header = header;
            this.details = details;
            this.trailer = trailer;
        }

        public string GetHeader()
        {
            return header.ToString();
        }

        public string GetDetails()
        {
            var output = new StringBuilder();

            foreach (VifDetail detail in details)
            {
                output.AppendLine(detail.ToString());
            }

            return output.ToString();
        }

        public string GetTrailer()
        {
            return trailer.ToString();
        }

        public string GenerateVif()
        {
            var output = new StringBuilder();

            output.AppendLine(GetHeader());
            
            foreach (var detail in details)
            {
                output.AppendLine(detail.ToString());
            }
            
            output.AppendLine(GetTrailer());

            return output.ToString().Trim();
        }
    }
}
