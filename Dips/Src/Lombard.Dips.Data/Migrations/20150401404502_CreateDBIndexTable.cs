using FluentMigrator;

namespace Lombard.Dips.Data.Migrations
{
    [Migration(20150401404502)]
    public class CreateDBIndexTable : Migration
    {
        private string dbName = "DB_INDEX";
        public override void Up()
        {
            Create.Table(dbName)
                .WithColumn("DEL_IND ")
                    .AsString(5)
                    .Nullable()
                .WithColumn("BATCH ")
                    .AsString(8)
                    .Nullable()
                .WithColumn("TRACE ")
                    .AsString(9)
                    .Nullable()
                .WithColumn("SEQUENCE ")
                    .AsString(5)
                    .Nullable()
                .WithColumn("TABLE_NO ")
                    .AsString(5)
                    .Nullable()
                .WithColumn("REC_NO ")
                    .AsString(10)
                    .Nullable();

        }
        public override void Down()
        {
            Delete.Table(dbName);
        }
    }
    
}
