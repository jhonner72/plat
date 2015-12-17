using System;
using System.Collections.Generic;

namespace FujiXerox.Adapters.A2iaAdapter.Extensions
{
    public static class ListExt
    {
        [ThreadStatic] private static Random random;
        private static Random Random { get { return random ?? (random = new Random(Environment.TickCount)); } }
        public static void Shuffle<T>(this IList<T> list)
        {
            var n = list.Count;
            while (n > 1)
            {
                n--;
                var k = Random.Next(n + 1);
                var value = list[k];
                list[k] = list[n];
                list[n] = value;
            }
        }
    }
}
