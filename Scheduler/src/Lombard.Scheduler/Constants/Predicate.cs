using System;
using System.Collections.Generic;
using System.Linq;
using System.Web;

namespace Lombard.Scheduler.Constants
{
    public static class Predicate
    {
        public static string EndOfDay = "shutdown";
        public static string Vif = "send";
        public static string ImageExchange = "send";
        public static string InwardsFV = "repost";
        public static string AgencyBanks = "send";
        public static string LockedBox = "send";
        public static string AusPostECL = "reconcile";
    }
}