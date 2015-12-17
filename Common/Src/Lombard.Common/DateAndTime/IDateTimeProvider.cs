using System;

namespace Lombard.Common.DateAndTime
{
    public interface IDateTimeProvider
    {
        DateTime ProcessingDate { get; }

        DateTime CurrentTimeInAustralianEasternTimeZone();

    }
}
