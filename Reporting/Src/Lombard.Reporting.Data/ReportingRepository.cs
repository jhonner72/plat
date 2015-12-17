using Lombard.Reporting.Data.Domain;
using System;
using System.Collections.Generic;
using System.Data.SqlClient;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace Lombard.Reporting.Data
{
    public class ReportingRepository
    {
        private readonly ReportingDbContext context;

        public ReportingRepository(ReportingDbContext context)
        {
            this.context = context;
        }

        public virtual IList<AllItem> GetAllItems(DateTime processDate, string processingState, string bankCode)
        {
            return context.Database.SqlQuery<AllItem>("EXEC usp_rpt_All_Items @ProcessDate=@ProcessDate, @ProcessingState=@ProcessingState, @BankCode=@BankCode",
                new SqlParameter("@ProcessDate", processDate),
                new SqlParameter("@ProcessingState", processingState),
                new SqlParameter("@BankCode", bankCode)).ToList();
        }

        public virtual IList<VR3CreditCardItem> GetLockedBoxCreditCardItems(DateTime processDate, string customerId, bool isHideDebit)
        {
            return context.Database.SqlQuery<VR3CreditCardItem>("EXEC [usp_ext_NAB_Locked_Box_Extract_VR3] @ProcessDate=@ProcessDate, @CustomerId=@CustomerId, @HideDebit=@HideDebit",
                new SqlParameter("@ProcessDate", processDate),
                new SqlParameter("@CustomerId", customerId),
                new SqlParameter("@HideDebit", isHideDebit)).ToList();
        }
    }
}
