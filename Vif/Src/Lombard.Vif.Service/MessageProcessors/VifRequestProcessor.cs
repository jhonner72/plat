using System;
using System.Collections.Generic;
using System.ComponentModel.DataAnnotations;
using System.Globalization;
using System.Threading.Tasks;
using Lombard.Common.Queues;
using Lombard.Vif.Service.Domain;
using Lombard.Vif.Service.Mappers;
using Lombard.Vif.Service.Messages.XsdImports;
using Lombard.Vif.Service.Utils;
using Serilog;
using Serilog.Context;

namespace Lombard.Vif.Service.MessageProcessors
{
    public class VifRequestProcessor : IMessageProcessor<CreateValueInstructionFileRequest>
    {
        private readonly IExchangePublisher<CreateValueInstructionFileResponse> publisher;
        private readonly IRequestSplitter requestSplitter;
        private readonly IRequestConverter requestConverter;
        private readonly IFileWriter fileWriter;
        private readonly IPathHelper pathHelper;

        public CreateValueInstructionFileRequest Message { get; set; }

        public VifRequestProcessor(
            IExchangePublisher<CreateValueInstructionFileResponse> publisher,
            IRequestSplitter requestSplitter,
            IRequestConverter requestConverter,
            IFileWriter fileWriter,
            IPathHelper pathHelper)
        {
            this.publisher = publisher;
            this.requestSplitter = requestSplitter;
            this.requestConverter = requestConverter;
            this.fileWriter = fileWriter;
            this.pathHelper = pathHelper;
        }

        public async Task ProcessAsync(System.Threading.CancellationToken cancellationToken, string correlationId, string routingKey)
        {
            var request = Message;

            try
            {
                using (LogContext.PushProperty("BusinessKey", request.jobIdentifier))
                {
                    Log.Information("Processing request {@request}", request);

                    var pathHelperResponse = pathHelper.GetVifPath(request.jobIdentifier);

                    if (!pathHelperResponse.IsSuccessful)
                    {
                        ExitWithError(pathHelperResponse.ValidationResults);
                    }

                    var vifFolderPath = pathHelperResponse.Result;

                    var requestSplitterResponse = requestSplitter.Map(request);

                    if (!requestSplitterResponse.IsSuccessful)
                    {
                        ExitWithError(requestSplitterResponse.ValidationResults);
                    }

                    var vifFileInfo = requestSplitterResponse.Result;

                    var vifItemResponse = new CreateValueInstructionFileResponse();

                    var requestConverterResponse = requestConverter.Map(vifFileInfo);

                    if (requestConverterResponse.IsSuccessful)
                    {
                        var vifGenerator = requestConverterResponse.Result;

                        var vifContents = vifGenerator.GenerateVif();

                        var filenamePrefix = "MO.FXA.VIF";
                        var entity = request.entity.ToString() + State.Parse(request.state.ToString(CultureInfo.InvariantCulture)) + request.sequenceNumber.ToString().Substring(0, 2);
                        var businessCalendar = "D" + request.businessDate.ToString("yyMMdd");
                        var runNumberPart2 = "R" + request.sequenceNumber.ToString().Substring(2, 2);
                        var fileName = string.Join(".", filenamePrefix, entity, businessCalendar, runNumberPart2);

                        var fileNameAndPath = fileWriter.WriteToFile(vifFolderPath, fileName, vifContents);

                        vifItemResponse.valueInstructionFileFilename = fileName;
                    }
                    else
                    {
                        ExitWithError(requestConverterResponse.ValidationResults);
                    }

                    await publisher.PublishAsync(vifItemResponse, correlationId, routingKey);

                    Log.Debug("Responded with {@response} to the response queue", vifItemResponse);

                    Log.Information("Request processed successfully");
                }
            }
            catch (Exception ex)
            {
                Log.Error(ex, "Error processing request");
                throw;
            }
        }

        private void ExitWithError(IEnumerable<ValidationResult> validationResults)
        {
            foreach (var validationResult in validationResults)
            {
                Log.Warning(validationResult.ErrorMessage);
            }

            throw new InvalidOperationException(validationResults.AsString());
        }
    }
}
