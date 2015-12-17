namespace Lombard.ImageExchange.Nab.OutboundService.Messages.Internal
{
    public class BatchReadyToProcess
    {
        public readonly long BatchId;

        public BatchReadyToProcess(long batchId)
        {
            BatchId = batchId;
        }
    }
}