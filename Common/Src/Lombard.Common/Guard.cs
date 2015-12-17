using System;
using System.Collections.Generic;
using System.Linq;

namespace Lombard.Common
{
    public static class Guard
    {
        public static void IsNotNull<T>(T check, string name) where T : class
        {
            if (check == null)
            {
                throw new ArgumentNullException(name);
            }
        }

        public static void IsNotNullOrEmpty<T>(IEnumerable<T> check, string name) where T : class
        {
            if (check == null)
            {
                throw new ArgumentNullException(name);
            }

            if (!check.Any())
            {
                var msg = string.Format("Collection '{0}' must contain at least 1 item.", name);
                throw new ArgumentException(msg);
            }
        }
    }
}