using System.Data.Common;
using System.Data.Entity;

namespace Lombard.Dips.Data
{
    public class DipsContext : DbContext
    {
        public DipsContext(DbConnection database)
            : base(database, true)
        {
            Database.Log += msg => System.Diagnostics.Debug.WriteLine(msg);
        }

        protected override void OnModelCreating(DbModelBuilder model)
        {
            model.Configurations.AddFromAssembly(typeof(DipsContext).Assembly);
            base.OnModelCreating(model);
        }
    }
}
