using System.Data.Entity.ModelConfiguration;
using Lombard.Adapters.Data.Domain;

namespace Lombard.Adapters.Data.Maps
{
    public class DipsNabChqPodMap : EntityTypeConfiguration<DipsNabChq>
    {
        public DipsNabChqPodMap()
        {
            ToTable("NabChq")
                .HasKey(x => new { x.sys_date, x.S_BATCH, x.S_TRACE, x.S_SEQUENCE });
        }
    }
}
