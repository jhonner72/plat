namespace Lombard.Reporting.AdapterService.Utils
{
    using System;
    using System.Linq;
    using Lombard.Reporting.AdapterService.Messages.XsdImports;

    public static class ParameterExtensions
    {
        public static Parameter GetValue(this Parameter[] parameters, string name, bool isRequired = true)
        {
            var param = parameters.SingleOrDefault(p => p.name.Equals(name, StringComparison.OrdinalIgnoreCase));

            if (param == null && isRequired)
            {
                throw new ArgumentException("Parameter not found.", name);
            }

            if (param == null)
            {
                param = new Parameter();
            }

            return param;
        }
    }
}
