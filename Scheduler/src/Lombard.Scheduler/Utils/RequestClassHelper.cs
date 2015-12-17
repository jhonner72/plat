using System;
using Newtonsoft.Json;

namespace Lombard.Scheduler.Utils
{
    public class RequestClassHelper
    {
        private string iType = string.Empty;
        private int iMaxQuerySize = 0;
        private string iTargetEndPoint = string.Empty;

        public RequestClassHelper(string oType, int oMaxQuerySize, string oTargetEndPoint)
        {
            this.iType = oType;
            this.iMaxQuerySize = oMaxQuerySize;
            this.iTargetEndPoint = oTargetEndPoint;
        }
        [JsonProperty("@type")]
        public string type { get { return this.iType; } }
        public int maxQuerySize { get { return this.iMaxQuerySize; } }
        public string targetEndPoint { get { return this.iTargetEndPoint; } }

    }
}