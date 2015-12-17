using System;
using System.Collections.Generic;
using System.ComponentModel.DataAnnotations;
using System.Globalization;
using System.IO.Abstractions;
using System.Linq;
using Lombard.ImageExchange.Nab.OutboundService.Domain;
using Lombard.ImageExchange.Nab.OutboundService.Configuration;
using Lombard.ImageExchange.Nab.OutboundService.Helpers;
using Lombard.ImageExchange.Nab.OutboundService.Messages.XsdImports;
using Lombard.Common.FileProcessors;
using Newtonsoft.Json;
using Serilog;

namespace Lombard.ImageExchange.Nab.OutboundService.Mappers
{
    public interface IMessageToBatchConverter : IMapper<CreateImageExchangeFileRequest, ValidatedResponse<Batch>>
    {
    }

    public class MessageToBatchConverter : IMessageToBatchConverter
    {
        private readonly IFileSystem fileSystem;
        private readonly IMapper<string, DebitCreditType> debitCreditTypeMapper;

        private readonly string bitLockerLocation;

        private readonly string zipPassword;

        public MessageToBatchConverter(IOutboundConfiguration outboundConfiguration, IFileSystem fileSystem, IMapper<string, DebitCreditType> debitCreditTypeMapper)
        {
            this.debitCreditTypeMapper = debitCreditTypeMapper;
            this.fileSystem = fileSystem;

            bitLockerLocation = outboundConfiguration.BitLockerLocation;
            this.zipPassword = outboundConfiguration.ZipPassword;
        }

        public ValidatedResponse<Batch> Map(CreateImageExchangeFileRequest request)
        {
            try
            {
                // TODO: use separate message validator

                if (string.IsNullOrEmpty(request.jobIdentifier))
                {
                    return Failure("Request does not contain a jobIdentifier");
                }

                var jobLocation = fileSystem.Path.Combine(bitLockerLocation, request.jobIdentifier);
                
                if (!fileSystem.Directory.Exists(jobLocation))
                {
                    return Failure(string.Format("Cannot find bitlocker job location {0}", jobLocation));
                }

                var metadataFiles = fileSystem.Directory.EnumerateFiles(jobLocation, "VOUCHER_*.json");

                var jsonFiles = metadataFiles as IList<string> ?? metadataFiles.ToList();
                if (!jsonFiles.Any())
                {
                    return Failure(string.Format("Cannot find any ImageExchangeVoucher json files in the job location {0}", jobLocation));
                }
                
                var serializer = JsonSerializerFactory.Get();

                var outboundVouchers = new List<OutboundVoucher>();

                // On advice from Adam, creating an IE "batch" number using the unique combination of Date + SequenceNumber
                var batchNumber = string.Empty;
          
                foreach (var jsonFile in jsonFiles)
                {
                    // deserialize JSON directly from a file
                    ImageExchangeVoucher imageExchangeVoucher;
                    using (var streamReader = fileSystem.File.OpenText(jsonFile))
                    {
                        // TODO: use settings from Lombard.Common
                        
                        imageExchangeVoucher = (ImageExchangeVoucher)serializer.Deserialize(streamReader, typeof (ImageExchangeVoucher));
                    }

                    if (request.fileType == ImageExchangeType.ImageExchange)
                    {
                        var batchNumberAsString = string.Format
                            ("{0}{1}",
                            request.businessDate.ToString("yyyyMMdd"),
                            request.sequenceNumber.ToString("D8"));

                        batchNumber = (long.Parse(batchNumberAsString)).ToString(CultureInfo.InvariantCulture).PadLeft(16, '0');
                    }
                    else
                        batchNumber = imageExchangeVoucher.voucherBatch.scannedBatchNumber;

                    var outboundVoucher = new OutboundVoucher
                    {
                        BsbLedgerFi = imageExchangeVoucher.voucher.bsbNumber,
                        BsbCollectingFi = imageExchangeVoucher.voucherBatch.collectingBank,
                        BsbCapturingFi = imageExchangeVoucher.voucherBatch.captureBsb,
                        TransactionCode = imageExchangeVoucher.voucher.transactionCode,
                        Amount = (long) Convert.ToDouble(imageExchangeVoucher.voucher.amount), // expected that does not contain decimals
                        DrawerAccountNumber = imageExchangeVoucher.voucher.accountNumber,
                        TransmissionDate = imageExchangeVoucher.voucher.processingDate,
                        DebitCreditType = debitCreditTypeMapper.Map(imageExchangeVoucher.voucher.transactionCode),
                        AuxiliaryDomestic = imageExchangeVoucher.voucher.auxDom,
                        ExtraAuxiliaryDomestic = imageExchangeVoucher.voucher.extraAuxDom,
                        TransactionIdentifier = imageExchangeVoucher.voucher.documentReferenceNumber, 
                        VoucherIndicator = string.IsNullOrEmpty(imageExchangeVoucher.voucherProcess.voucherDelayedIndicator) 
                                                ? VoucherIndicator.ImageIsPresent
                                                : VoucherIndicator.FromValue(imageExchangeVoucher.voucherProcess.voucherDelayedIndicator), 
                        BatchNumber = batchNumber,
                        DipsBatchNumber = imageExchangeVoucher.voucherBatch.scannedBatchNumber // Not used
                    };

                    var imageInfo = GetCoinImageInfo(jobLocation, jsonFile);

                    outboundVoucher.Image = imageInfo.CoinImage;
                    outboundVoucher.FrontImagePath = imageInfo.FrontImagePath;
                    outboundVoucher.RearImagePath = imageInfo.RearImagePath;

                    outboundVouchers.Add(outboundVoucher);
                }
                
                var batch = new Batch
                {
                    OutboundVouchers = outboundVouchers, 
                    ShortTargetEndpoint = request.targetEndPoint,
                    LongTargetEndPoint = request.fourCharactersEndPoint,
                    ProcessingDate = request.businessDate,
                    BatchNumber = long.Parse(batchNumber),
                    FileLocation = jobLocation,
                    SequenceNumber = request.sequenceNumber.ToString(),
                    OperationType = request.fileType.ToString(),
                     ZipPassword = this.zipPassword
                };

                return ValidatedResponse<Batch>.Success(batch);
            }
            catch (Exception ex)
            {
                var unhandledValidationErrors = new List<ValidationResult>
                {
                    new ValidationResult(string.Format("Unexpected exception in converting a CreateImageExchangeFileRequest message: '{0}' ", ex))
                };

                return ValidatedResponse<Batch>.Failure(unhandledValidationErrors);
            }
        }

        private ImageInfo GetCoinImageInfo(string jobLocation, string jsonFileName)
        {
            var frontImageFullPath = GetImageFullPath(jobLocation, jsonFileName, "FRONT");
            var rearImageFullPath = GetImageFullPath(jobLocation, jsonFileName, "REAR");

            var frontImageBytes = GetImageBytes(frontImageFullPath);
            var rearImageBytes = GetImageBytes(rearImageFullPath);

            return new ImageInfo
            {
                FrontImagePath = frontImageFullPath,
                RearImagePath = rearImageFullPath,
                CoinImage = new CoinImage
                {
                    FrontImage = frontImageBytes,
                    RearImage = rearImageBytes
                }
            };
        }

        private string GetImageFullPath(string jobLocation, string jsonFileName, string frontOrBackSuffix)
        {
            var baseFileName = fileSystem.Path.GetFileNameWithoutExtension(jsonFileName);
            
            //Get the number of underscore char. 
            var fileNameComponent = baseFileName.Split('_');

            if(fileNameComponent.Length != 5)
            {
                Log.Warning("JSON filename may not be following the naming convention. The process to truncate the filename will continue and throw exception when it can't construct the file accordingly.");
            }
            else if (fileNameComponent.Length <3)
            {
                Log.Error("JSON filename is too short for next processing.");
                throw new InvalidOperationException();
            }

            var truncatedFilename = String.Join("_", fileNameComponent[0], fileNameComponent[1], fileNameComponent[2]);
            var fileName = string.Format("{0}_{1}.jpg", truncatedFilename, frontOrBackSuffix);

            var imageFullPath = fileSystem.Path.Combine(jobLocation, fileName);
            
            return imageFullPath;
        }

        private byte[] GetImageBytes(string imageFullPath)
        {
            byte[] imageBytes = null;

            if (fileSystem.File.Exists(imageFullPath))
            {
                imageBytes = fileSystem.File.ReadAllBytes(imageFullPath);
            }
            else
            {
                Log.Warning("Cannot find image {0}", imageFullPath);
            }

            return imageBytes;
        }

        private ValidatedResponse<Batch> Failure(string failureMessage)
        {
            return ValidatedResponse<Batch>.Failure(new List<ValidationResult> {new ValidationResult(failureMessage)});
        }
    }
}
