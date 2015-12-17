using FluentMigrator;

namespace Lombard.Adapters.Data.Migrator.Migrations
{
    /// <summary>
    /// This migration adds fields required for repost as part of sprint 26
    /// </summary>
    [Migration(20150813153000)]
    public class RepostS26 : Migration
    {
        public override void Up()
        {
            Alter.Table("NabChq")
                .AlterColumn("repostFromDRN")
                .AsFixedLengthAnsiString(12)
                .Nullable();
        }

        public override void Down()
        {
            Alter.Table("NabChq")
                .AlterColumn("repostFromDRN")
                .AsFixedLengthAnsiString(9)
                .Nullable();
        }
    }
}
