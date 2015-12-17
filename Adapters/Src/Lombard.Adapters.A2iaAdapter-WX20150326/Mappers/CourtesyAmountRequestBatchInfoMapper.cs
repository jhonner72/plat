using System.Collections.Generic;
using System.IO;
using System.Linq;
using System.Threading.Tasks;
using Lombard.Adapters.A2iaAdapter.Configuration;
using Lombard.Adapters.A2iaAdapter.Domain;
using Lombard.Adapters.A2iaAdapter.Messages;

namespace Lombard.Adapters.A2iaAdapter.Mappers
{
    public interface ICourtesyAmountRequestBatchInfoMapper : IMapper<RecogniseBatchCourtesyAmountRequest, BatchInfo>
    {

    }

    public class CourtesyAmountRequestBatchInfoMapper : ICourtesyAmountRequestBatchInfoMapper
    {
        private readonly IAdapterConfiguration adapterConfiguration;

        public CourtesyAmountRequestBatchInfoMapper(IAdapterConfiguration adapterConfiguration)
        {
            this.adapterConfiguration = adapterConfiguration;
        }

        public BatchInfo Map(RecogniseBatchCourtesyAmountRequest message)
        {
            //var body = Encoding.UTF8.GetString(message.Body);
            //Log.Debug("Message body was {0}", body); 
            //var currentMessage = JsonConvert.DeserializeObject<RecogniseBatchCourtesyAmountRequest>(body);
            //Log.Information("The batch has correlationId {0}", message.BasicProperties.CorrelationId);
            BatchInfo batchInfo = new BatchInfo();
            IList<ChequeImageInfo> imageList = new List<ChequeImageInfo>();
            // TODO: get CorrelationId
            //batchInfo.CorrelationId = message.BasicProperties.CorrelationId;
            //RecogniseCourtesyAmountRequest;
            Parallel.ForEach(message.Voucher, item =>
            {
                //Log.Debug("The batch contains image file {0} with ref {1}.", item.frontImageIdentifier, item.documentReferenceNumber);
                imageList.Add(new ChequeImageInfo()
                {
                    //CorrelationId = message.BasicProperties.CorrelationId,
                    DocumentReferenceNumber = item.DocumentReferenceNumber,
                    Urn = Path.Combine(adapterConfiguration.ImageFileFolder, item.FrontImageIdentifier),
                    //Type = (ImageFormat)Enum.Parse(typeof(ImageFormat), currentMessage.FileType, true),
                    Status = 0,
                });
            });
            batchInfo.ChequeImageInfos = imageList.ToArray();

            return batchInfo;
        }
    }
}
