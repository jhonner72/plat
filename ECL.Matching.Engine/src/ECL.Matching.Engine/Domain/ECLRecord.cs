namespace Lombard.ECLMatchingEngine.Service.Domain
{
    using Serilog;
    using System;
    public class ECLRecord : IECLRecord
    {
        public ECLRecord(string target) 
        {
            if (target.Length != 112)
            {
                var errorMessage = "[IECLRecord]:[Constructor] An error occured during constructing IECLRecord object";
                Log.Warning(errorMessage);
                throw new ArgumentOutOfRangeException(errorMessage);
            }
            else
            {
                this.ECLInput = target;
                this.LedgerBSBCode = target.Substring(1, 6);
                this.Amount = target.Substring(41, 10);
                this.ChequeSerialNumber = target.Substring(20, 9);
                this.DrawerAccountNumber = target.Substring(7, 10);
                this.ExchangeModeCode = target.Substring(53, 1);
                this.DistributionPocketId = target.Substring(51,2);
            }
        }

        public string ECLInput { get; set; }

        public string ChequeSerialNumber { get; set; }

        public string LedgerBSBCode { get; set; }

        public string DrawerAccountNumber { get; set; }

        public string Amount { get; set; }

        public string ExchangeModeCode { get; set; }

        public string DistributionPocketId { get; set; }
    }
}
