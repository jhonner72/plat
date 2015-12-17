using Lombard.Adapters.MftAdapter.Messages;
using Lombard.Adapters.MftAdapter.Web.Messages;
using Lombard.Common.DateAndTime;

namespace Lombard.Adapters.MftAdapter.Mappers
{
    public class CreateIncidentMapper : IMapper<CreateIncidentRequest, Incident>
    {
        private readonly IDateTimeProvider dateTimeProvider;

        public CreateIncidentMapper(IDateTimeProvider dateTimeProvider)
        {
            this.dateTimeProvider = dateTimeProvider;
        }

        public Incident Map(CreateIncidentRequest input)
        {
            return new Incident
            {
                datetimeRaised = dateTimeProvider.CurrentTimeInAustralianEasternTimeZone(),
                subject = input.JobSubject,
                predicate = input.JobPredicate,
                jobIdentifier = input.JobIdentifier,
                details = input.Details
            };
        }
    }
}
