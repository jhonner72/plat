using System;
using EasyNetQ;
using Serilog;

namespace Lombard.Common.EasyNetQ
{
    public class CustomSerilogLogger : IEasyNetQLogger
    {
        public void DebugWrite(string format, params object[] args)
        {
            Log.Debug(format, args);
        }

        public void ErrorWrite(Exception exception)
        {
            Log.Error(exception, "EasyNetQ has encountered an error.");
        }

        public void ErrorWrite(string format, params object[] args)
        {
            Log.Error(format, args);
        }

        public void InfoWrite(string format, params object[] args)
        {
            Log.Information(format, args);
        }
    }
}
