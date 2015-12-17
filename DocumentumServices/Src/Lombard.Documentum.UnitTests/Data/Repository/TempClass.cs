using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using Lombard.Common.Domain;
using Microsoft.VisualStudio.TestTools.UnitTesting;
using Newtonsoft.Json;

namespace Lombard.Documentum.UnitTests.Data.Repository
{

    [TestClass]
    public class TempClass
    {
        [TestMethod]
        public void DoThis()
        {
            const string AuxiliaryDomestic = "000001029";
            const string Bsb = "012468";
            const string AccountNumber = "999999999";
            const string Amount = "77440";
            var processingDate = new DateTime(2015, 02, 10);

            var fileName = DishonourLetter.GetFileName(AuxiliaryDomestic, Bsb, AccountNumber, Amount, processingDate);

            var dishonourLetter = new DishonourLetter(fileName, DishonourLetterStatus.LetterIssued, processingDate);

            string output = JsonConvert.SerializeObject(dishonourLetter);

            var t = 5;
        }
    }
}
