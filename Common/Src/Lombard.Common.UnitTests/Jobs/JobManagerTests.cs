using System;
using System.Threading;
using System.Threading.Tasks;
using Lombard.Common.Jobs;
using Microsoft.VisualStudio.TestTools.UnitTesting;
using Moq;

namespace Lombard.Common.UnitTests.Jobs
{
 
    [TestClass]
    public class JobManagerTests
    {
        private Mock<IJobFactory<string>> jobFactory;
        private Mock<JobStub> job;

        [TestInitialize]
        public void TestInitialize()
        {
            job = new Mock<JobStub>();

            jobFactory = new Mock<IJobFactory<string>>();
        }

        [TestCategory("NotForResharper")]
        [TestMethod]
        public async Task WhenQueueJob_ThenProcessJob()
        {
            var data = "test";
            job.Object.ProcessThrows = false;

            ExpectJobFactoryToCreateJob(data);

            var sut = new JobManager<string>(jobFactory.Object);

            sut.Start();

            sut.QueueJob(data);

            Thread.Sleep(100);

            await sut.CompleteAsync();

            jobFactory.VerifyAll();
            job.Verify(x => x.ProcessAsync(It.IsAny<CancellationToken>()), Times.Once);
            
        }
        
        [TestMethod]
        [ExpectedException(typeof(InvalidOperationException))]
        public async Task GivenCompleted_WhenQueueJob_ThenThrow()
        {
            var data = "test";
            job.Object.ProcessThrows = false;

            ExpectJobFactoryToCreateJob(data);

            var sut = new JobManager<string>(jobFactory.Object);

            sut.Start();

            await sut.CompleteAsync();

            sut.QueueJob(data);

        }

        [TestMethod]
        public async Task GivenNotStarted_WhenQueue_ThenIgnore()
        {
            var data = "test";
            job.Object.ProcessThrows = false;

            ExpectJobFactoryToCreateJob(data);

            var sut = new JobManager<string>(jobFactory.Object);

            sut.QueueJob(data);

            await sut.CompleteAsync();

            job.Verify(x => x.ProcessAsync(It.IsAny<CancellationToken>()), Times.Never);
        }


        private void ExpectJobFactoryToCreateJob(string data)
        {
            job.Object.Data = data;

            jobFactory
                .Setup(x => x.CreateJob(data))
                .Returns(job.Object);
        }
    }
}
