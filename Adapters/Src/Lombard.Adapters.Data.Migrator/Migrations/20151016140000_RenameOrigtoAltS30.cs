using FluentMigrator;


namespace Lombard.Adapters.Data.Migrator.Migrations
{
    [Migration(20151016140000)]
    public class RenameOrigtoAltS30 : Migration
    {
        public override void Up()
        {
            Rename.Column("orig_ead")
                .OnTable("NabChq")
                .To("alt_ead");

            Rename.Column("orig_ser_num")
                .OnTable("NabChq")
                .To("alt_ser_num");

            Rename.Column("orig_bsb_num")
                .OnTable("NabChq")
                .To("alt_bsb_num");

            Rename.Column("orig_acc_num")
                .OnTable("NabChq")
                .To("alt_acc_num");

            Rename.Column("orig_trancode")
                .OnTable("NabChq")
                .To("alt_trancode");

        }

        public override void Down()
        {
            Rename.Column("alt_ead")
                .OnTable("NabChq")
                .To("orig_ead");

            Rename.Column("alt_ser_num")
                .OnTable("NabChq")
                .To("orig_ser_num");

            Rename.Column("alt_bsb_num")
                .OnTable("NabChq")
                .To("orig_bsb_num");

            Rename.Column("alt_acc_num")
                .OnTable("NabChq")
                .To("orig_acc_num");

            Rename.Column("alt_trancode")
                .OnTable("NabChq")
                .To("orig_trancode");

        }
    }
}