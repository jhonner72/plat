using System.Collections.Generic;
using Lombard.Adapters.MftAdapter.Messages;
using Lombard.Adapters.MftAdapter.Web.Messages;
using Lombard.Common.DateAndTime;
using Newtonsoft.Json;

namespace Lombard.Adapters.MftAdapter.Mappers
{
    public class CreateJobFromFileMapper : IMapper<CreateJobFromFileRequest, JobRequest>
    {
        private readonly IDateTimeProvider dateTimeProvider;

        public CreateJobFromFileMapper(IDateTimeProvider dateTimeProvider)
        {
            this.dateTimeProvider = dateTimeProvider;
        }

        public JobRequest Map(CreateJobFromFileRequest input)
        {
            return new JobRequest
            {
                jobIdentifier = input.JobIdentifier,
                subject = input.JobSubject,
                predicate = input.JobPredicate,
                activity = new[] { GetActivityFromFileName(input.FileName) },
                parameters = string.IsNullOrEmpty(input.Parameters) ? null : JsonConvert.DeserializeObject<List<Parameter>>(input.Parameters).ToArray()
            };
        }

        private JobActivity GetActivityFromFileName(string fileName)
        {
            return new JobActivity
            {
                request = new FileReceivedActivityRequest
                {
                    fileIdentifier = fileName,
                    receivedDateTime = dateTimeProvider.CurrentTimeInAustralianEasternTimeZone()
                },
                requestDateTime = dateTimeProvider.CurrentTimeInAustralianEasternTimeZone()
            };
        }
    }
}
