using System;
using System.Diagnostics;
using System.Diagnostics.CodeAnalysis;
using System.Linq;
using Serilog;
using Serilog.Configuration;
using Serilog.Core;
using Serilog.Events;

namespace Lombard.LogExtensions
{
    public class CallerEnricher : ILogEventEnricher
    {
        public void Enrich(LogEvent logEvent, ILogEventPropertyFactory propertyFactory)
        {
            var frames = new StackTrace().GetFrames();
            Func<StackFrame, bool> serilogFrames =
                stack =>
                    stack.GetMethod()
                        .DeclaringType
                        .Namespace
                        .StartsWith("serilog.core", StringComparison.InvariantCultureIgnoreCase);
            var serilogFrameCount = frames.Count(serilogFrames);
            var callerFrame = frames.Skip(serilogFrameCount + 1).First();

            var method = callerFrame.GetMethod().Name;
            var caller = callerFrame.GetMethod().DeclaringType.FullName;

            var property = propertyFactory.CreateProperty("LogCaller",
                new LogCaller {ClassName = caller, Method = method});
            logEvent.AddOrUpdateProperty(property);
        }
    }

    [SuppressMessage("StyleCop.CSharp.MaintainabilityRules", "SA1402:FileMayOnlyContainASingleClass", Justification = "Reviewed.")]
    public static class CallerEnricherConfigurationExt
    {
        public static LoggerConfiguration WithCallerInformation(this LoggerEnrichmentConfiguration enrichment)
        {
            return enrichment.With(new CallerEnricher());
        }
    }

    public class LogCaller
    {
        public string Method { get; set; }
        public string ClassName { get; set; }

        public override string ToString()
        {
            return string.Format("[{0}]::{1}]", ClassName, Method);
        }
    }
}