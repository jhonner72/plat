using System.Data.Entity.ModelConfiguration;
using Lombard.Adapters.Data.Domain;

namespace Lombard.Adapters.Data.Maps
{
    public class DipsQueueMap : EntityTypeConfiguration<DipsQueue>
    {
        public DipsQueueMap()
        {
            ToTable("queue")
                .HasKey(x => x.S_BATCH);

            Property(x => x.ConcurrencyToken)
                .IsConcurrencyToken();
        }
    }
}
