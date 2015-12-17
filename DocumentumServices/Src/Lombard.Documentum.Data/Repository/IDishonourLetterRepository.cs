using System;
using System.Threading.Tasks;
using Lombard.Common.Domain;

namespace Lombard.Documentum.Data.Repository
{
    public interface IDishonourLetterRepository
    {
        Task<DishonourLetter> CreateLetterForVoucher(Voucher voucher, DateTime processinGdate);
        Task<DishonourLetter> Get(string fileName, DateTime processingDate);
        Task Update(DishonourLetter letter, bool updateFile = false);
    }
}
