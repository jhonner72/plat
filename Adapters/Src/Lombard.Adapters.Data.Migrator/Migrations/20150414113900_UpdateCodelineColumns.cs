using FluentMigrator;

namespace Lombard.Adapters.Data.Migrator.Migrations
{
    [Migration(20150414113900)]
    public class UpdateCodelineColumns : Migration
    {
        public override void Up()
        {
            Alter.Table("queue")
                .AlterColumn("CorrelationId")
                .AsString()
                .Nullable();

        }

        public override void Down()
        {
            Execute.Script("UPDATE [queue] SET [CorrelationId] = NULL");

            Alter.Table("queue")
                .AlterColumn("CorrelationId")
                .AsFixedLengthAnsiString(5)
                .Nullable();
        }
    }
}
