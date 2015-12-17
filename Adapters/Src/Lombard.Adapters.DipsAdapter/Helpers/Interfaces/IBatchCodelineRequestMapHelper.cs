using System;
using Lombard.Adapters.Data.Domain;

namespace Lombard.Adapters.DipsAdapter.Helpers.Interfaces
{
    public interface IBatchCodelineRequestMapHelper
    {
        DipsQueue CreateNewDipsQueue(
            DipsLocationType locationType,
            string batchNumber,
            string documentReferenceNumber,
            DateTime processingDate,
            string jobIdentifier,
            string jobId);

        DipsDbIndex CreateNewDipsDbIndex(
            string batchNumber,
            string documentReferenceNumber);

        DipsNabChq CreateNewDipsNabChq(
            string batchNumber,
            string documentReferenceNumber,
            DateTime processingDate,
            string extraAuxDom,
            bool extraAuxDomStatus,
            string auxDom,
            bool auxDomStatus,
            string bsbNumber,
            bool bsbNumberStatus,
            string accountNumber,
            bool accountNumberStatus,
            string transactionCode,
            bool transactionCodeStatus,
            string capturedAmount,
            string amountConfidenceLevel,
            string documentType,
            string jobId,
            string manualRepair,
            string amount,
            bool amountStatus,
            string captureBsb,
            string batchAccountNumber,
            string processingState,
            string collectingBank,
            string unitId,
            string batchType,
            string repostFromDRN,
            DateTime? repostFromProcessingDate,
            string batchCollectingBank,
            string subBatchType,
            bool creditNoteFlag);
    }
}