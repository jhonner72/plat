using Lombard.Documentum.Data.Repository;

namespace Lombard.Documentum.Data
{
    public class DocumentumContext : IDocumentumContext
    {
        public IDataObjectRepository DataObjects { get; private set;}
        public IVoucherRepository Vouchers { get; private set; }
        public IDishonourLetterRepository DishonourLetters { get; set; }

        public DocumentumContext(IDataObjectRepository dataObjects, IVoucherRepository vouchers, IDishonourLetterRepository letters)
        {
            DataObjects = dataObjects;
            Vouchers = vouchers;
            DishonourLetters = letters;
        }
    }
}
