namespace Lombard.Extensions
{
    public static class StringExt
    {
        public static string With(this string text, params object[] args)
        {
            return string.Format(text, args);
        }
    }
}