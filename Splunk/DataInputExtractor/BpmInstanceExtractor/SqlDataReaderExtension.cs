using System;
using System.Data.SqlClient;

namespace BpmInstanceExtractor
{
    public static class SqlDataReaderExtension
    {
        public static DateTime? GetNullableDateTime(this SqlDataReader reader, int ordinal)
        {
            return reader.GetNullableValue<DateTime?>(ordinal);
        }

        public static decimal? GetNullableDecimal(this SqlDataReader reader, int ordinal)
        {
            return reader.GetNullableValue<decimal?>(ordinal);
        }

        public static T GetNullableValue<T>(this SqlDataReader reader, int ordinal)
        {
            if (reader.IsDBNull(ordinal))
                return default(T);
            else
                return (T)reader.GetValue(ordinal);
        }
    }
}
