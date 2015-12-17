using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace Lombard.Reporting.Data.Domain
{
    public class VR3CreditCardItem
    {
        public string BatchNumber { get; set; }
        public string PaymentType { get; set; }
        public string TransactionNumber { get; set; }
        public string TransactionType { get; set; }
        public string SequenceNumber { get; set; }
        public string ExtraAuxDom { get; set; }
        public string AuxDom { get; set; }
        public string BSB { get; set; }
        public string AccountNumber { get; set; }
        public decimal Amount { get; set; }
        public decimal? Difference { get; set; }
        public string CreditCardNumber { get; set; }
    }
}
