using System;
using System.Threading;
using System.Threading.Tasks;
using Lombard.Common.Jobs;

namespace Lombard.Common.UnitTests.Jobs
{
    public class JobStub : IJob<string>
    {
        public string Data {get;set;}
        public bool ProcessThrows { get; set; }

        public virtual async Task ProcessAsync(CancellationToken cancellationToken)
        {
            if (ProcessThrows)
                throw new Exception("testException");

            await Task.Delay(0, cancellationToken);
        }
    }
}