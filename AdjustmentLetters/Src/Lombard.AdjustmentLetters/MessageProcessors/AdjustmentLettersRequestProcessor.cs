using System;
using System.Collections.Generic;
using System.IO;
using System.Linq;
using System.Threading.Tasks;
using Autofac;
using Lombard.AdjustmentLetters.Data;
using Lombard.AdjustmentLetters.Domain;
using Lombard.AdjustmentLetters.Helper;
using Lombard.AdjustmentLetters.Mappers;
using Lombard.AdjustmentLetters.Utils;
using Lombard.Common.Queues;
using Lombard.Vif.Service.Messages.XsdImports;
using Serilog;
using Serilog.Context;

namespace Lombard.AdjustmentLetters.MessageProcessors
{
    public class AdjustmentLettersRequestProcessor : IMessageProcessor<CreateBatchAdjustmentLettersRequest>
    {
        private readonly ILifetimeScope component;
        private readonly IExchangePublisher<CreateBatchAdjustmentLettersResponse> publisher;
        private readonly IMessageToBatchConverter messageConverter;
        private readonly ILetterGenerator letterGenerator;
        private readonly IFileWriter fileWriter;
        private readonly IFileReader fileReader;
        private readonly IPathHelper pathHelper;
        private readonly IReferenceDbContext dbContext;
        private readonly IAsposeWrapper asposeWrapper;

        public AdjustmentLettersRequestProcessor(
            ILifetimeScope component,
            IExchangePublisher<CreateBatchAdjustmentLettersResponse> publisher,
            IMessageToBatchConverter requestSplitter,
            ILetterGenerator letterGenerator,
            IFileWriter fileWriter,
            IPathHelper pathHelper,
            IFileReader fileReader,
            IAsposeWrapper asposeWrapper,
            IReferenceDbContext dbContext = null)
        {
            this.component = component;
            this.publisher = publisher;
            this.messageConverter = requestSplitter;
            this.letterGenerator = letterGenerator;
            this.fileWriter = fileWriter;
            this.pathHelper = pathHelper;
            this.fileReader = fileReader;
            this.asposeWrapper = asposeWrapper;
            this.dbContext = dbContext;
        }

        public CreateBatchAdjustmentLettersRequest Message { get; set; }

        public async Task ProcessAsync(System.Threading.CancellationToken cancellationToken, string correlationId, string routingKey)
        {
            var request = this.Message;
            var responses = new List<CreateAdjustmentLettersResponse>();

            using (LogContext.PushProperty("BusinessKey", request.jobIdentifier))
            {
                Log.Information("Processing request {@request}", request);

                try
                {
                    var requestSplitterResponse = this.messageConverter.Map(request);

                    if (requestSplitterResponse.IsSuccessful)
                    {
                        var result = requestSplitterResponse.Result;

                        Log.Debug("AdjustmentLettersRequestProcessor:ProcessAsync, Message has been converted successfully.");

                        using (var lifetimeScope = this.component.BeginLifetimeScope())
                        {
                            using (var trackingDbContext = this.dbContext ?? lifetimeScope.Resolve<IReferenceDbContext>())
                            {
                                var branches = trackingDbContext.GetAllBranches();

                                foreach (var letter in result.Letters)
                                {
                                    var branch =
                                        branches.SingleOrDefault(
                                            _ =>
                                                _.branch_bsb.Equals(
                                                    letter.AdjustedVoucher.voucherBatch.collectingBank));

                                    string branchName = string.Empty;
                                    if (branch == null)
                                    {
                                        Log.Error("Branch with bsb number [" +
                                                            letter.AdjustedVoucher.voucherBatch.collectingBank +
                                                            "] does not exists.");
                                    }
                                    else
                                    {
                                        branchName = branch.branch_name;
                                    }

                                    // Create a new PDF
                                    var pdf = this.letterGenerator.GeneratePdfFromTemplate(letter.AdjustedVoucher, branchName);

                                    this.letterGenerator.AddVoucherImage(result.JobFolderLocation, pdf, letter, this.fileReader);

                                    this.asposeWrapper.SaveDocument(pdf, Path.Combine(result.JobFolderLocation, letter.PdfFilename));

                                    this.asposeWrapper.AddPageNumbers(Path.Combine(result.JobFolderLocation, letter.PdfFilename));

                                    var response = new CreateAdjustmentLettersResponse
                                    {
                                        documentReferenceNumber = letter.AdjustedVoucher.voucher.documentReferenceNumber,
                                        filename = letter.PdfFilename,
                                        processingDate = letter.ProcessingDate,
                                        scannedBatchNumber = letter.AdjustedVoucher.voucherBatch.scannedBatchNumber,
                                        transactionLinkNumber = letter.AdjustedVoucher.voucherProcess.transactionLinkNumber
                                    };

                                    // Add to the response batch
                                    responses.Add(response);
                                }
                            }
                        }

                        this.fileWriter.SaveZipFile(result.JobFolderLocation, result.PdfZipFilename);

                        var batchResponse = new CreateBatchAdjustmentLettersResponse
                        {
                            adjustmentLetters = responses.ToArray(),
                            zipFilename = result.PdfZipFilename
                        };

                        await this.publisher.PublishAsync(batchResponse, correlationId, routingKey);

                        Log.Debug("Responded with {@response} to the response queue", batchResponse);

                        Log.Information("Processed request {@request}", request);
                    }
                    else
                    {
                        throw new Exception(string.Format("AdjustmentLettersRequestProcessor:ProcessAsync, Error mapping request. {0}",
                            string.Join(Environment.NewLine, requestSplitterResponse.ValidationResults)));
                    }
                }
                catch (Exception ex)
                {
                    Log.Error(ex, "AdjustmentLettersRequestProcessor:ProcessAsync: " + ex.Message);
                    throw;
                }
            }
        }
    }
}
