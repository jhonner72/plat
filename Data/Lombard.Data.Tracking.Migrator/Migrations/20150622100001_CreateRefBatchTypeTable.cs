using FluentMigrator;


namespace Lombard.Data.Tracking.Migrator.Migrations
{
    [Migration(20150622100021)]
    public class _20150622100001_CreateRefBatchTypeTable : Migration
    {
        public override void Up()
        {
            Create.Table("ref_batch_type")
                .WithColumn("batch_type_code").AsString(50).NotNullable().PrimaryKey("PK_fxa_ref_batch_type")
                .WithColumn("work_type_code").AsString(50).NotNullable().PrimaryKey("PK_fxa_ref_batch_type")
                .WithColumn("batch_type_name").AsString(50);
            SeedData();
        }

        public override void Down()
        {
            Delete.Table("ref_batch_type");
        }

        private void SeedData()
        {
            Execute.Sql("insert into dbo.ref_batch_type (work_type_code, batch_type_code, batch_type_name) values ('NABCHQ_POD','OTC Vouchers','OTC Vouchers');");
            Execute.Sql("insert into dbo.ref_batch_type (work_type_code, batch_type_code, batch_type_name) values ('NABCHQ_POD','ECD Vouchers','ECD Vouchers');");
            Execute.Sql("insert into dbo.ref_batch_type (work_type_code, batch_type_code, batch_type_name) values ('NABCHQ_POD','EBD Vouchers','EBD Vouchers');");
            Execute.Sql("insert into dbo.ref_batch_type (work_type_code, batch_type_code, batch_type_name) values ('NABCHQ_POD','IDM Vouchers','IDM Vouchers');");
            Execute.Sql("insert into dbo.ref_batch_type (work_type_code, batch_type_code, batch_type_name) values ('NABCHQ_LBOX','Customer 1','Customer 1');");
            Execute.Sql("insert into dbo.ref_batch_type (work_type_code, batch_type_code, batch_type_name) values ('NABCHQ_LBOX','Customer 2','Customer 2');");
            Execute.Sql("insert into dbo.ref_batch_type (work_type_code, batch_type_code, batch_type_name) values ('NABCHQ_LBOX','Customer 3','Customer 3');");
            Execute.Sql("insert into dbo.ref_batch_type (work_type_code, batch_type_code, batch_type_name) values ('NABCHQ_LBOX','Customer N','Customer N');");
            Execute.Sql("insert into dbo.ref_batch_type (work_type_code, batch_type_code, batch_type_name) values ('NABCHQ_LISTINGS','OTC Listings','OTC Listings');");
            Execute.Sql("insert into dbo.ref_batch_type (work_type_code, batch_type_code, batch_type_name) values ('NABCHQ_LISTINGS','ECD Listings','ECD Listings');");
            Execute.Sql("insert into dbo.ref_batch_type (work_type_code, batch_type_code, batch_type_name) values ('NABCHQ_LISTINGS','EBD Listings','EBD Listings');");
            Execute.Sql("insert into dbo.ref_batch_type (work_type_code, batch_type_code, batch_type_name) values ('BQL_POD','OTC Vouchers','OTC Vouchers');");
            Execute.Sql("insert into dbo.ref_batch_type (work_type_code, batch_type_code, batch_type_name) values ('BQL_POD','ECD Vouchers','ECD Vouchers');");
            Execute.Sql("insert into dbo.ref_batch_type (work_type_code, batch_type_code, batch_type_name) values ('BQL_POD','EBD Vouchers','EBD Vouchers');");

        }
    }
}
