using System;
using System.Collections.Generic;
using System.Linq;
using System.Threading.Tasks;
using A2iACheckReaderLib;
using Lombard.Adapters.A2iaAdapter.Wrapper.Configuration;
using Lombard.Adapters.A2iaAdapter.Wrapper.Domain;

namespace Lombard.Adapters.A2iaAdapter.Wrapper
{
    public class A2iAMultipleTableOcrService : IOcrProcessingService
    {
        private readonly Dictionary<VoucherType, A2iAOcrService> a2iAOcrServices;
        private readonly IA2iaConfiguration configuration;

        private struct A2iATableConfig
        {
            public string TableFilename { get; private set; }
            public int ProcessorCount { get; private set; }

            public A2iATableConfig(string tableFilename, int processorCount)
                : this()
            {
                TableFilename = tableFilename;
                ProcessorCount = processorCount;
            }
        }

        public A2iAMultipleTableOcrService(API a2IaEngine, IA2iaConfiguration configuration)
        {
            this.configuration = configuration;
            CheckProcessorConfiguration();
            a2iAOcrServices = new Dictionary<VoucherType, A2iAOcrService>();
            foreach (VoucherType voucher in Enum.GetValues(typeof(VoucherType)))
            {
                a2iAOcrServices.Add(voucher, new A2iAOcrService(a2IaEngine, configuration));
            }
        }

        private const int MinimumProcessorCount = 2;

        private void CheckProcessorConfiguration()
        {
            var typeCheck = new Dictionary<VoucherType, int>
            {
                {VoucherType.Credit, configuration.CreditMaxProcessorCount},
                {VoucherType.Debit, configuration.DebitMaxProcessorCount}
            };

            var reConfigure = new Dictionary<VoucherType, Action<int>>
            {
                {VoucherType.Credit, v => configuration.CreditMaxProcessorCount = v},
                {VoucherType.Debit, v => configuration.DebitMaxProcessorCount = v}
            };

            var result = Enum.GetValues(typeof (VoucherType)).Cast<VoucherType>().Sum(voucherType => typeCheck[voucherType]);
            if (!A2iAOcrService.ExceedsMaxProcessors(result)) return;
            // example credit uses 35 threads
            //         debit  uses  5 threads
            // on a 24 core server with 48 threads, this is fine
            // however if the server only has 4 cores with 8 threads
            // factor = 7 / 40
            // credit = 35 * factor = 6
            // debit = 5 * factor = 1
            if(A2iAOcrService.ProcessorCount < MinimumProcessorCount) throw new ApplicationException(string.Format("Processor count of {0} must be at least {1} to use multiple document configurations", A2iAOcrService.ProcessorCount, MinimumProcessorCount));
            var factor = A2iAOcrService.ProcessorCount/result;
            foreach (VoucherType voucherType in Enum.GetValues(typeof(VoucherType)))
            {
                reConfigure[voucherType].Invoke(factor*typeCheck[voucherType]);
            }
        }

        public void Initialise()
        {
            foreach (var a2iAOcrService in a2iAOcrServices)
            {
                a2iAOcrService.Value.Initialise();
            }
        }

        public Task ProcessVoucherAsync(IList<OcrVoucher> vouchers)
        {
            throw new NotImplementedException();
        }

        public virtual void ProcessBatch(OcrBatch batch)
        {
            var vouchers = batch.Vouchers;
            foreach (var a2iAOcrService in a2iAOcrServices)
            {
                PreProcessVouchers(a2iAOcrService);
                ProcessVouchers(vouchers, a2iAOcrService.Key);
            }
            OnBatchComplete(this, batch.JobIdentifier);
        }

        public virtual void Shutdown()
        {
            foreach (var a2iAOcrService in a2iAOcrServices)
            {
                a2iAOcrService.Value.Shutdown();
            }
        }

        public event BatchCompleteDelegate BatchComplete;

        protected virtual void PreProcessVouchers(KeyValuePair<VoucherType, A2iAOcrService> a2iAOcrService) { }

        protected virtual void OnBatchComplete(object sender, string batchId)
        {
            var batchComplete = BatchComplete;
            if (batchComplete != null) batchComplete(sender, batchId);
        }

        private void ProcessVouchers(IEnumerable<OcrVoucher> vouchers, VoucherType voucherType)
        {
            var voucherActions = new Dictionary<VoucherType, Func<A2iATableConfig>>
            {
                { VoucherType.Credit, () => new A2iATableConfig(configuration.CreditTablePath, configuration.CreditMaxProcessorCount) },
                { VoucherType.Debit, () => new A2iATableConfig(configuration.DebitTablePath, configuration.DebitMaxProcessorCount) }
            };

            var voucherSubset = vouchers.Where(v => v.VoucherType == voucherType);
            var tableConfig = voucherActions[voucherType].Invoke();
            a2iAOcrServices[voucherType].OpenOcrChannel(tableConfig.TableFilename, tableConfig.ProcessorCount);

            var ocrVouchers = vouchers as IList<OcrVoucher> ?? voucherSubset.ToList();
            foreach (var voucher in ocrVouchers)
            {
                a2iAOcrServices[voucherType].ProcessVoucher(voucher);
            }

            foreach (var voucher in ocrVouchers)
            {
                a2iAOcrServices[voucherType].GetIcrChannelResult(voucher);
            }
        }
    }
}
