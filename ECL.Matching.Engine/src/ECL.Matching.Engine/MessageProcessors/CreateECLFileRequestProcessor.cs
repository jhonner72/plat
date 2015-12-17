namespace Lombard.ECLMatchingEngine.Service.MessageProcessors
{
    using Lombard.Common.Queues;
    using Lombard.ECLMatchingEngine.Service.Domain;
    using Lombard.ECLMatchingEngine.Service.Mappers;
    using Lombard.ECLMatchingEngine.Service.Utils;
    using Lombard.Vif.Service.Messages.XsdImports;
    using Serilog;
    using Serilog.Context;
    using System;
    using System.Collections.Generic;
    using System.ComponentModel.DataAnnotations;
    using System.Linq;
    using System.Threading.Tasks;

    public class CreateECLFileRequestProcessor : IMessageProcessor<MatchVoucherRequest>
    {
        private readonly IExchangePublisher<MatchVoucherResponse> publisher;
        private readonly IMatchVoucherRequestToECLRecordBatch ECLFiles;
        private readonly IMatchVoucherRequestToVoucherInformationBatch voucherInformationBatch;
        private readonly IMatchedVoucherInformationToECLFileInfo ECLFileInfo;
        private readonly IECLRecordFileSystem fileWriter;


        public CreateECLFileRequestProcessor(
            IExchangePublisher<MatchVoucherResponse> publisher,
            IMatchVoucherRequestToECLRecordBatch ECLFiles,
            IMatchVoucherRequestToVoucherInformationBatch VoucherBatch,
            IMatchedVoucherInformationToECLFileInfo MatchedVouchers,
            IECLRecordFileSystem fileWriter)
        {
            this.publisher = publisher;
            this.ECLFiles = ECLFiles;
            this.voucherInformationBatch = VoucherBatch;
            this.ECLFileInfo = MatchedVouchers;
            this.fileWriter = fileWriter;
        }

        public MatchVoucherRequest Message { get; set; }

        public async Task ProcessAsync(System.Threading.CancellationToken cancellationToken, string correlationId, string routingKey)
        {
            var request = Message;

            using (LogContext.PushProperty("BusinessKey", request.jobIdentifier))
            {
                try
                {
                    if (String.IsNullOrEmpty(request.jobIdentifier))
                    {
                        throw new InvalidOperationException("JobID was invalid or empty.");
                    }
                    var voucherInfoBatch = voucherInformationBatch.Map(request);

                    if (!voucherInfoBatch.IsSuccessful)
                    {
                        ExitWithError(voucherInfoBatch.ValidationResults);
                    }

                    var voucherInfoBatchResult = voucherInfoBatch.Result;

                    var ECLItems = ECLFiles.Map(request);

                    if (!ECLItems.IsSuccessful)
                    {
                        ExitWithError(ECLItems.ValidationResults);
                    }

                    var ECLItemsResult = ECLItems.Result;
                    var MatchedVouchers = Get.MatchVouchers(voucherInfoBatchResult, ECLItemsResult.ToArray<IECLRecord>());
                    var UnMatchedVouchers = new VoucherInfoBatch();

                    if (MatchedVouchers.VoucherInformation != null)
                    {
                        UnMatchedVouchers = Get.UnMatchVouchers(voucherInfoBatchResult, MatchedVouchers.VoucherInformation.Select(a => a.Voucher).ToArray<VoucherInformation>());
                    }
                    else
                    {
                        Log.Warning("{CreateECLFileRequestProcessor}:{ProcessAsync} A response with null values for filename, matchedVoucher and unMatchedvoucer is expected.");
                    }

                    var eclFileInfo = ECLFileInfo.Map(MatchedVouchers);

                    if (!eclFileInfo.IsSuccessful)
                    {
                        ExitWithError(ECLItems.ValidationResults);
                    }

                    var eclFileResult = eclFileInfo.Result;

                    var eclFileResponse = new MatchVoucherResponse();
                    eclFileResponse.eclResponseFile =  (eclFileResult != null) ? eclFileResult.Select(a => fileWriter.WriteToFile(a)).ToArray() : null;
                    eclFileResponse.matchedVoucher = (MatchedVouchers.VoucherInformation != null) ? MatchedVouchers.VoucherInformation.Where(voucher => voucher.Voucher.voucher != null).Select(a => a.Voucher).ToArray<VoucherInformation>() : null;
                    eclFileResponse.unmatchedVoucher = (UnMatchedVouchers.VoucherInformation != null) ? UnMatchedVouchers.VoucherInformation.Select(a => a.Voucher).ToArray<VoucherInformation>() : null;

                    await publisher.PublishAsync(eclFileResponse, correlationId, routingKey);

                    Log.Debug("Responded with {@response} to the response queue", eclFileResponse);

                    Log.Information("Processed request {@request}", request);
                }
                catch (Exception ex)
                {
                    Log.Error("ProcessAsync: Error occurred within ECL Mathcing Vouchers. \n The error is {0}", ex.ToString());
                    throw;
                }
            }
        }

        private void ExitWithError(IEnumerable<ValidationResult> validationResults)
        {
            foreach (var validationResult in validationResults)
            {
                Log.Error(validationResult.ErrorMessage);
            }

            throw new Exception(validationResults.AsString());
        }
    }
}
