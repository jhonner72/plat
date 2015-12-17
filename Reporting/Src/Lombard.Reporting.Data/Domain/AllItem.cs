using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace Lombard.Reporting.Data.Domain
{
    public class AllItem
    {
        public string BatchNumber { get; set; }
        public string TransactionNumber { get; set; }
        public string Drn { get; set; }
        public string CollectingBSB { get; set; }
        public string ExtraAuxDom { get; set; }
        public string AuxDom { get; set; }
        public string BSB { get; set; }
        public string AccountNumber { get; set; }
        public string TranCode { get; set; }
        public string Classification { get; set; }
        public decimal Amount { get; set; }
        public string State { get; set; }
        public string RunNumber { get; set; }
    }
}
