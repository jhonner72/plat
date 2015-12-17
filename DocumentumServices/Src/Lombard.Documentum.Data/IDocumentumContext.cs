using Lombard.Documentum.Data.Repository;

namespace Lombard.Documentum.Data
{
    public interface IDocumentumContext
    {
        IDataObjectRepository DataObjects { get; }
        IVoucherRepository Vouchers { get; }
        IDishonourLetterRepository DishonourLetters { get; }
    }
}
