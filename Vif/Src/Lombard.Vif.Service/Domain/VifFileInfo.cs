using System;
using System.Collections.Generic;
using Lombard.Vif.Service.Constants;
using Lombard.Vif.Service.Messages.XsdImports;

namespace Lombard.Vif.Service.Domain
{
    public class VifFileInfo
    {
        private string vQueryLinkType;
        private string vBankCode;
        private int vBundletype;
        private string vCaptureBSB;
        private string vCollectingBSB;
        private DateTime vProcessDate;
        private RecordTypeCode[] vRecordTypeCode;
        private string vSequenceNumber;
        private string vState;
        private double vTotalCreditAmount;
        private double vTotalDebitAmount;
        private int vTotalCreditCount;
        private int vTotalDebitCount;
        public IEnumerable<VoucherInformation> VoucherInfos { get; set; }
        private CreateValueInstructionFileRequest request;

        public VifFileInfo(CreateValueInstructionFileRequest requestValue, IEnumerable<VoucherInformation> voucherInfos)
        {
            request = requestValue;
            VoucherInfos = voucherInfos;

            vBankCode = request.entity;
            vBundletype = VifConstants.BundleType;
            vCaptureBSB = request.captureBsb;
            vCollectingBSB = request.collectingBsb;
            vProcessDate = request.businessDate;
            vSequenceNumber = request.sequenceNumber.ToString();
            vRecordTypeCode = request.recordTypeCode;
            vState = request.state.ToString();
            vTotalCreditAmount = 0;
            vTotalDebitAmount = 0;
            vTotalCreditCount = 0;
            vTotalDebitCount = 0;
            vQueryLinkType = requestValue.queryLinkType.ToString();
        }

        public string QueryLinkType
        {
            get { return vQueryLinkType; }
            set { vQueryLinkType = value; }
        }

        public string BankCode 
        {
            get { return vBankCode; }
            set { vBankCode = value; }
        }
        public int BundleType
        {
            get { return vBundletype; }
            set { vBundletype = value; }
        }
        public string CaptureBSB
        {
            get { return vCaptureBSB; }
            set { vCaptureBSB = value; } 
        }
        public string CollectingBSB 
        {
            get { return vCollectingBSB; }
            set { vCollectingBSB = value; } 
        }
        public DateTime ProcessDate 
        {
            get { return vProcessDate; }
            set { vProcessDate = value; } 
        }
        public RecordTypeCode[] RecordTypeCode 
        {
            get { return vRecordTypeCode; }
            set { vRecordTypeCode = value; } 
        }
        public string SequenceNumber 
        {
            get { return vSequenceNumber; }
            set { vSequenceNumber = value; } 
        }
        public string State {
            get { return vState; }
            set { vState = value; }
        }

        public double TotalCreditAmount 
        {
            get { return vTotalCreditAmount; }
            set { vTotalCreditAmount = value; } 
        }
        public double TotalDebitAmount 
        {
            get { return vTotalDebitAmount; }
            set { vTotalDebitAmount = value; } 
        }
        public int TotalCreditCount 
        {
            get { return vTotalCreditCount; }
            set { vTotalCreditCount = value; } 
        }
        public int TotalDebitCount
        {
            get { return vTotalDebitCount; }
            set { vTotalDebitCount = value; }
        }
    }
}