namespace Lombard.ECLMatchingEngine.Service.Mappers
{
    using Lombard.Common.FileProcessors;
    using Lombard.ECLMatchingEngine.Service.Configuration;
    using Lombard.ECLMatchingEngine.Service.Constants;
    using Lombard.ECLMatchingEngine.Service.Domain;
    using Lombard.ECLMatchingEngine.Service.Utils;
    using Lombard.Vif.Service.Messages.XsdImports;
    using Serilog;
    using System;
    using System.Collections.Generic;
    using System.IO.Abstractions;
    using System.Linq;

    public interface IMatchedVoucherInformationToECLFileInfo : IMapper<iVoucherInfoBatch, ValidatedResponse<IEnumerable<MatchedECLFileInfo>>>
    {

    }

    public class MatchedVoucherInformationToECLFileInfo : IMatchedVoucherInformationToECLFileInfo
    {
        private readonly IFileSystem fileSystem;
        private string jobID;
        private readonly string bitLockerLocation;
        private readonly IEnumerable<IECLRecordVoucherInfo> MatchedVouchers;

        public MatchedVoucherInformationToECLFileInfo(IECLRecordConfiguration eclConfiguration, IFileSystem fileSystem, iVoucherInfoBatch MatchedVoucherBatch)
        {
            this.fileSystem = fileSystem;
            this.bitLockerLocation = eclConfiguration.BitLockerLocation;
            this.MatchedVouchers = MatchedVoucherBatch.VoucherInformation;
            this.jobID = MatchedVoucherBatch.JobIdentifier;
        }

        public ValidatedResponse<IEnumerable<MatchedECLFileInfo>> Map (iVoucherInfoBatch MatchedVoucherBatch)
        {
            List<MatchedECLFileInfo> AllStatesECLInfo = new List<MatchedECLFileInfo>();

            try
            {
                MatchedVoucherBatch.VoucherInformation = this.createDummyVoucher(MatchedVoucherBatch.VoucherInformation);

                var eclFileInfo = new List<IGrouping<StateEnum, IECLRecordVoucherInfo>>();
                this.jobID = MatchedVoucherBatch.JobIdentifier;

                if (MatchedVoucherBatch.VoucherInformation.Count != 0)
                {
                    eclFileInfo = MatchedVoucherBatch.VoucherInformation
                            .GroupBy(g => g.Voucher.voucherBatch.processingState).ToList();

                    AllStatesECLInfo = eclFileInfo.Select(group => this.GenerateMatchedReport(group)).ToList();
                }
                else
                {
                    Log.Warning("{MatchedVoucherInformationToECLFileInfo}:{Map}: Matched Voucher Batch found with empty vouchers!");
                }
 
           }
            catch (Exception ex)
            {
                return ValidatedResponseHelper.Failure<IEnumerable<MatchedECLFileInfo>>("{MatchedVoucherInformationToECLFileInfo}:{Map}:, Error Details: {0}", ex.ToString());
            }

            return ValidatedResponse<IEnumerable<MatchedECLFileInfo>>.Success(AllStatesECLInfo);
        }

        private MatchedECLFileInfo GenerateMatchedReport(IGrouping<StateEnum, IECLRecordVoucherInfo> vouchers)
        {
            var unMatchedECL = new MatchedECLFileInfo();
            try
            {
                State processingState = null;
                State.TryParse(vouchers.FirstOrDefault().Voucher.voucherBatch.processingState.ToString(),
                    out processingState);

                if (processingState == null)
                    throw new InvalidOperationException("{MatchedVoucherInformationToECLFileInfo}:{GenerateMatchedReport}[Processing state] with null value is found!");

                unMatchedECL.Header = GenerateHeader(vouchers.FirstOrDefault().Voucher);
                unMatchedECL.Body = vouchers
                    .Where(voucher => voucher.Voucher.voucher != null)
                    .Select(voucher => this.GenerateBody(voucher)).ToList();
                unMatchedECL.Trailer = this.GenerateTrailer(vouchers.FirstOrDefault().Voucher);

                unMatchedECL.FileName = String.Join("\\", bitLockerLocation, jobID, Constants.FileName + processingState.Value);
                unMatchedECL.Trailer.TransactionNumberTotal = vouchers.Where(voucher => voucher.Voucher.voucher != null)
                    .Select(a => a.Voucher).Count().ToString();
            }
            catch (InvalidOperationException ex)
            {
                throw new InvalidOperationException(String.Format("MatchedVoucherInformationToECLFileInfo:GenerateMatchedReport: unexpected error occured. The stack trace was {0}", ex.ToString()));
            }

            return unMatchedECL;
        }

        private MatchedECLRecordHeader GenerateHeader(VoucherInformation voucher)
        {
            return new MatchedECLRecordHeader
            {
                Record_Type_Code = Constants.Header,
                Filler = string.Empty,
                StateId = State.Parse(Enum.GetName(typeof(StateEnum), voucher.voucherBatch.processingState)).Value,
                ProcessingDate = DateTime.Today.ToString(Constants.DateFormat),
            };
        }

        private MatchedECLRecordBody GenerateBody(IECLRecordVoucherInfo voucher)
        {
            return new MatchedECLRecordBody
            {
                Record_Type_Code = Constants.Body,
                LedgerBsbCode = voucher.Voucher.voucher.bsbNumber,
                ChequeSerialNumber = voucher.Voucher.voucher.auxDom,
                DrawerAccountNumber = voucher.Voucher.voucher.accountNumber,
                ExtraAuxDom = voucher.Voucher.voucher.extraAuxDom,
                TransactionTypeCode = voucher.Voucher.voucher.transactionCode,
                TransactionAmount = voucher.Voucher.voucher.amount,
                Machine_Olp_Distribution_Number = voucher.MatchedECLRecord.DistributionPocketId
            };
        }

        private MatchedECLRecordTrailer GenerateTrailer(VoucherInformation voucher)
        {
            return new MatchedECLRecordTrailer
            {
                Record_Type_Code = Constants.Trailer,
                Filler = string.Empty,
                TransactionNumberTotal = "0"
            };
        }

        private List<IECLRecordVoucherInfo> createDummyVoucher(List<IECLRecordVoucherInfo> input)
        {
            if (input == null)
            {
                input = new List<IECLRecordVoucherInfo>();
                Log.Warning("{MatchedVoucherInformationToECLFileInfo}:{createDummyVoucher} No Voucher found, initiated an empty list.");
            }

            if (input.Where(s => s.Voucher.voucherBatch.processingState == StateEnum.NSW).Count() == 0)
            {
                input.Add(new ECLRecordVoucherInfo { Voucher = new VoucherInformation { voucherBatch = new VoucherBatch { processingState = StateEnum.NSW } } });
                Log.Warning("{MatchedVoucherInformationToECLFileInfo}:{createDummyVoucher} No Voucher NSW found, added a dummy voucher information object.");
            }
            if (input.Where(s => s.Voucher.voucherBatch.processingState == StateEnum.QLD).Count() == 0)
            {
                input.Add(new ECLRecordVoucherInfo { Voucher = new VoucherInformation { voucherBatch = new VoucherBatch { processingState = StateEnum.QLD } } });
                Log.Warning("{MatchedVoucherInformationToECLFileInfo}:{createDummyVoucher} No Voucher QLD found, added a dummy voucher information object.");
            }

            if (input.Where(s => s.Voucher.voucherBatch.processingState == StateEnum.SA).Count() == 0)
            {
                input.Add(new ECLRecordVoucherInfo { Voucher = new VoucherInformation { voucherBatch = new VoucherBatch { processingState = StateEnum.SA } } });
                Log.Warning("{MatchedVoucherInformationToECLFileInfo}:{createDummyVoucher} No Voucher SA found, added a dummy voucher information object.");
            }
            if (input.Where(s => s.Voucher.voucherBatch.processingState == StateEnum.VIC).Count() == 0)
            {
                input.Add(new ECLRecordVoucherInfo { Voucher = new VoucherInformation { voucherBatch = new VoucherBatch { processingState = StateEnum.VIC } } });
                Log.Warning("{MatchedVoucherInformationToECLFileInfo}:{createDummyVoucher} No Voucher VIC found, added a dummy voucher information object.");
            }

            if (input.Where(s => s.Voucher.voucherBatch.processingState == StateEnum.WA).Count() == 0)
            {
                input.Add(new ECLRecordVoucherInfo { Voucher = new VoucherInformation { voucherBatch = new VoucherBatch { processingState = StateEnum.WA } } });
                Log.Warning("{MatchedVoucherInformationToECLFileInfo}:{createDummyVoucher} No Voucher WA found, added a dummy voucher information object.");
            }
            return input;
        }
    }
}
