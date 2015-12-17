using System;
using System.Collections.Generic;
using Lombard.ImageExchange.Nab.OutboundService.Domain;

namespace Lombard.ImageExchange.Nab.OutboundService.Mappers
{
    public class Batch
    {
        public string ShortTargetEndpoint { get; set; }

        public string LongTargetEndPoint { get; set; }

        public DateTime ProcessingDate { get; set; }

        public long BatchNumber { get; set; }

        public string FileLocation { get; set; }

        public string SequenceNumber { get; set; }

        public string OperationType { get; set; }

        public string ZipPassword { get; set; }

        public IEnumerable<OutboundVoucher> OutboundVouchers { get; set; }
    }
}
