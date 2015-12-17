using Castle.Components.DictionaryAdapter;
using Lombard.Common.Configuration;

namespace Lombard.ImageExchange.Nab.OutboundService.Configuration
{
    [KeyPrefix("outbound:")]
    [AppSettingWrapper]
    public interface IOutboundConfiguration
    {
        int VoucherItemsPerOutputFile { get; set; }
        string CoinSendingOrganisation { get; set; }
        string BitLockerLocation { get; set; }
        string ZipPassword { get; set; }
        string Environment { get; set; }
        
    }
}
