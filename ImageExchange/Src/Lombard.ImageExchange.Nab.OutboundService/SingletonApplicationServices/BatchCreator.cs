using Lombard.ImageExchange.Nab.OutboundService.Domain;
using Lombard.ImageExchange.Nab.OutboundService.Mappers;
using Lombard.ImageExchange.Nab.OutboundService.Messages.XsdImports;
using Serilog;

namespace Lombard.ImageExchange.Nab.OutboundService.SingletonApplicationServices
{
    public interface IBatchCreator
    {
        OutboundVoucherFile Execute(Batch batch);
    }

    public class BatchCreator : IBatchCreator
    {
        private readonly IFileNameCreator fileNameCreator;

        public BatchCreator(IFileNameCreator vFileNameCreator)
        {
            this.fileNameCreator = vFileNameCreator;
        }

        public OutboundVoucherFile Execute(Batch batch)
        {
            var outboundVoucherFile = new OutboundVoucherFile
            {
                EndpointShortName = batch.ShortTargetEndpoint,
                EndpointLongName = batch.LongTargetEndPoint,
                ProcessingDate = batch.ProcessingDate,
                BatchNumber = batch.BatchNumber,
                FileLocation = batch.FileLocation,
                SequenceNumber = batch.SequenceNumber,
                OperationType = batch.OperationType,
                 ZipPassword = batch.ZipPassword
                
            };

            var fileName = fileNameCreator.Execute(outboundVoucherFile);
            var zipFilename = string.Empty;
            if (outboundVoucherFile.OperationType == ImageExchangeType.Cuscal.ToString())
            {
                var filenameArray = fileName.Split(';');
                if (filenameArray.Length != 2)
                    Log.Warning("{BatchCreator:Execute} Filename may be incorrect. An error may be expected in the next execution.");
                else
                {
                    fileName = filenameArray[0];
                    zipFilename = filenameArray[1];
                }
                
            }
            else if (outboundVoucherFile.OperationType == ImageExchangeType.ImageExchange.ToString())
            {
                zipFilename = fileName.Replace(".xml", ".zip");
            }
         

            outboundVoucherFile.FileName = fileName;
            outboundVoucherFile.ZipFileName = zipFilename;
   
            return outboundVoucherFile;
        }
    }
}
