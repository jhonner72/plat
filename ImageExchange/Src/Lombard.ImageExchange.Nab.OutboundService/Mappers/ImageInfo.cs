using Lombard.ImageExchange.Nab.OutboundService.Domain;

namespace Lombard.ImageExchange.Nab.OutboundService.Mappers
{
    // This class is just a temporary container and is not an official domain entity
    public class ImageInfo
    {
        public CoinImage CoinImage { get; set; }

        public string FrontImagePath { get; set; }
        public string RearImagePath { get; set; }
    }
}
