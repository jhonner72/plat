using System.Xml.Linq;
using Lombard.Common;
using Lombard.ImageExchange.Nab.OutboundService.Constants;
using Lombard.ImageExchange.Nab.OutboundService.Domain;

namespace Lombard.ImageExchange.Nab.OutboundService.Mappers
{
    public class CoinImageToXmlMapper : IMapper<CoinImage, XElement>
    {
        private readonly IMapper<byte[], string> imageMapper;

        public CoinImageToXmlMapper(IMapper<byte[], string> imageMapper)
        {
            this.imageMapper = imageMapper;
        }

        public XElement Map(CoinImage input)
        {
            Guard.IsNotNull(input, "input");

            var imageElement = new XElement(CoinElementConstants.ImageTag);

            if (input.FrontImage == null || input.RearImage == null || input.FrontImage.Length == 0 || input.RearImage.Length == 0)
            {
                return imageElement;
            }
            
            var rearImage = imageMapper.Map(input.RearImage);
            var frontImage = imageMapper.Map(input.FrontImage);

            imageElement.Add(new XElement(CoinElementConstants.FrontImage, frontImage));
            imageElement.Add(new XElement(CoinElementConstants.RearImage, rearImage));

            return imageElement;
        }
    }
}