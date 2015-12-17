using FluentMigrator;

namespace Lombard.NABD2UserLoad.Migrator
{
    [Profile("AlwaysRun")]
    public class InitCreate : Migration
    {
        public override void Up()
        {
            Execute.Script(@"Scripts\initNabUserSynchSchema.sql");
        }

        public override void Down()
        {
        }
    }
}
