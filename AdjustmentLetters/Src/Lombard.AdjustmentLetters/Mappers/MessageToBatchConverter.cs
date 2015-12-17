using System;
using System.Collections.Generic;
using System.ComponentModel.DataAnnotations;
using System.Linq;
using Lombard.AdjustmentLetters.Configuration;
using Lombard.AdjustmentLetters.Constants;
using Lombard.AdjustmentLetters.Domain;
using Lombard.AdjustmentLetters.Utils;
using Lombard.Common.FileProcessors;
using Lombard.Vif.Service.Messages.XsdImports;
using Serilog;

namespace Lombard.AdjustmentLetters.Mappers
{
    public interface IMessageToBatchConverter : IMapper<CreateBatchAdjustmentLettersRequest, ValidatedResponse<AdjLetterBatch>>
    {
    }

    public class MessageToBatchConverter : IMessageToBatchConverter
    {
        private readonly IAdjustmentLettersConfiguration config;
        private readonly IPathHelper pathHelper;

        public MessageToBatchConverter(
            IAdjustmentLettersConfiguration config,
            IPathHelper pathHelper)
        {
            this.config = config;
            this.pathHelper = pathHelper;
        }

        public ValidatedResponse<AdjLetterBatch> Map(CreateBatchAdjustmentLettersRequest request)
        {
            if (string.IsNullOrEmpty(request.jobIdentifier))
            {
                Log.Error("MessageToBatchConverter:Map, Request does not contain a jobIdentifier.");
                return this.Failure("Request does not contain a jobIdentifier");
            }

            var batch = new AdjLetterBatch();
            batch.JobFolderLocation = this.pathHelper.GetJobPath(request.jobIdentifier.Replace("/", "\\")).Result;
            batch.JobIdentifier = request.jobIdentifier;
            batch.ProcessingDate = request.processingDate;
            batch.PdfZipFilename = string.Format(this.config.PdfZipFilename, this.config.Environment, request.processingDate.ToString(ReportConstants.DateTimeFormat));

            if (request.voucherInformation.Any())
            {
                var voucherGroup = request.voucherInformation.GroupBy(x => new { x.voucherProcess.transactionLinkNumber, x.voucherBatch.scannedBatchNumber });

                var index = 1;
                foreach (var group in voucherGroup)
                {
                    if (!group.Any(g => g.voucherProcess.adjustedFlag))
                    {   
                        return this.Failure(string.Format("No adjusted Voucher found for {@batchNumber}, {@TransLinkNo}",
                            group.First().voucherBatch.scannedBatchNumber,
                            group.First().voucherProcess.transactionLinkNumber));
                    }
                    else
                    {
                        foreach (var adjustedVoucher in group.Where(vi => vi.voucherProcess.adjustedFlag))
                        {
                            var matchedCustomerDetails = request.outputMetadata.FirstOrDefault(t => t.customer.Any
                                    (g => g.accountNumber == adjustedVoucher.voucher.accountNumber
                                        && g.bsb == adjustedVoucher.voucher.bsbNumber));

                            var filenamePrefix = "nbsb";

                            if (matchedCustomerDetails != null)
                            {
                                filenamePrefix = matchedCustomerDetails.outputFilenamePrefix;
                            }
                            else
                            {
                                Log.Warning("WARNING: {@bsb} and {@accountNumber} did not match with the reference data, Letter will be generated using" + filenamePrefix + " prefix.",
                                    adjustedVoucher.voucher.bsbNumber,
                                    adjustedVoucher.voucher.accountNumber);
                            }

                            var newLetter = new AdjustmentLetter
                            {
                                AdjustedVoucher = adjustedVoucher,
                                JobIdentifier = request.jobIdentifier,
                                ProcessingDate = request.processingDate,
                                PdfFilename =
                                    string.Format("{0}_{1}.pdf", filenamePrefix, index.ToString().PadLeft(4, '0'))
                            };

                            batch.Letters.Add(newLetter);

                            newLetter.Vouchers.AddRange(group.ToList());

                            index++;
                        }
                    }
                }
            }
            else
            {
                Log.Error("[MessageToBatchConverter:Map], No vouchers found.");
                return this.Failure("No vouchers found");
            }

            return ValidatedResponse<AdjLetterBatch>.Success(batch);
        }

        private ValidatedResponse<AdjLetterBatch> Failure(string failureMessage)
        {
            return ValidatedResponse<AdjLetterBatch>.Failure(new List<ValidationResult> { new ValidationResult(failureMessage) });
        }
    }
}
