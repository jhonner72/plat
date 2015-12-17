using System;

namespace Lombard.Common.DateAndTime
{
    public class DateTimeProvider : IDateTimeProvider
    {
        public DateTime ProcessingDate
        {
            get
            {
                return DateTime.Today;//for now
            }
        }

        public DateTime CurrentTimeInAustralianEasternTimeZone()
        {
            // NOTE: This is not the final implementation.
            // We'll most likely need to handle being located in other time zones
            // we might end up using NodaTime for that http://nodatime.org/
            return DateTime.Now;
        }
    }
}