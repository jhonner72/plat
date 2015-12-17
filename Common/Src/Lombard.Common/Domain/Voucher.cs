using System;

using Serilog.Extras.Attributed;

namespace Lombard.Common.Domain
{
    public class Voucher
    {
        //TODO check what belongs here
        public string Id { get; set; }

        
        public string Bsb { get; set; }
        public string AccountNumber { get; set; }
        public string AuxiliaryDomestic { get; set; }
        public string Amount { get; set; }
        public bool DelayedImage { get; set; }
        public bool Dishonoured { get; set; }

        public DateTime ProcessingDate { get; set; }
        
        [NotLogged]
        public byte[] File { get; set; }
        public string Name { get; set; }


    }
}
