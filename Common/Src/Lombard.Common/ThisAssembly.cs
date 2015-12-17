using System;
using System.IO;
using System.Linq;
using System.Reflection;

namespace Lombard.Common
{
    public static class ThisAssembly
    {
        public static Stream ReadFile(string filename)
        {
            Guard.IsNotNull(filename, "filename");

            var assembly = Assembly.GetCallingAssembly();
            return assembly.ReadFile(filename);
        }

        public static Stream ReadFile(this Assembly assembly, string filename)
        {
            Predicate<string> endsWith = i =>
                i.EndsWith("." + filename, StringComparison.InvariantCultureIgnoreCase);

            var file = assembly.GetManifestResourceNames()
                .FirstOrDefault(x => endsWith(x));

            return string.IsNullOrWhiteSpace(file)
                ? null : assembly.GetManifestResourceStream(file);
        }
    }
}