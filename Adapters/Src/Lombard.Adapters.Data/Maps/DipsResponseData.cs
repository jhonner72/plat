using System.Data.Entity.ModelConfiguration;
using Lombard.Adapters.Data.Domain;

namespace Lombard.Adapters.Data.Maps
{
      
    public class DipsResponseDataMap : EntityTypeConfiguration<DipsResponseData>
    {
        public DipsResponseDataMap()
        {
            ToTable("response_data")
                .HasKey(x => x.guid_name);
        }
    }
}
