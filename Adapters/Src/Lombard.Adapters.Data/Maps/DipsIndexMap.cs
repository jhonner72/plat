using System.Data.Entity.ModelConfiguration;
using Lombard.Adapters.Data.Domain;

namespace Lombard.Adapters.Data.Maps
{
    public class DipsIndexMap : EntityTypeConfiguration<DipsDbIndex>
    {
        public DipsIndexMap()
        {
            ToTable("DB_INDEX")
                .HasKey(x => new { x.BATCH, x.TRACE });

        }
    }
}
