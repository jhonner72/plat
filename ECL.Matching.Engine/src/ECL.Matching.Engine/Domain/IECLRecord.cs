namespace Lombard.ECLMatchingEngine.Service.Domain
{

    public interface IECLRecord
    {
        string ECLInput { get; set; }

        string LedgerBSBCode { get; set; }

        string DrawerAccountNumber { get; set; }

        string ChequeSerialNumber { get; set; }

        string Amount { get; set; }

        string ExchangeModeCode { get; set; }

        string DistributionPocketId { get; set; }

    }
}