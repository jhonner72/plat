using Lombard.Vif.Service.Domain;
using Microsoft.VisualStudio.TestTools.UnitTesting;

namespace Lombard.Vif.UnitTests
{
    [TestClass]
    public class VifHeaderTests
    {
        [TestMethod]
        public void ToString_GivenValidObject_ShouldProduceFormattedString()
        {
            var vifHeader = GetVifHeader();
            
            var actual = vifHeader.ToString();

            Assert.AreEqual("Z31206NAB201410210833400830291                                                                                                                        ", actual);

        }
        
        private VifHeader GetVifHeader()
        {
            return new VifHeader
            {
                RECORD_TYPE_CODE = "Z",
                STATE_NUMBER = "3",
                RUN_NUMBER = "1206",
                BANK_CODE = "NAB",
                PROCESS_DATE = "20141021",
                CAPTURE_BSB = "083340",
                COLLECTING_BSB = "083029",
                BUNDLE_TYPE = "1",
                EMPTY_SPACE_FILLER = string.Empty
            };
        }
    }
}
