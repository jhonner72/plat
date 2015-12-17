using System;
using System.Collections.Generic;

namespace Lombard.ImageExchange.Nab.OutboundService.Domain
{
    public class OutboundVoucherFile
    {
        public string FileName { get; set; }

        public string FileLocation { get; set; }

        public DateTime ProcessingDate { get; set; }

        public string EndpointShortName { get; set; }

        public string EndpointLongName { get; set; }

        public string OperationType { get; set; }

        public long BatchNumber { get; set; }

        public string SequenceNumber { get; set; }

        public string ZipFileName { get; set; }

        public string ZipPassword { get; set; }

        public IEnumerable<OutboundVoucher> Vouchers { get; set; } 
    }
}