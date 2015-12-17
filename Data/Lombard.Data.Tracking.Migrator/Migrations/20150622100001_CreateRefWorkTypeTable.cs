using FluentMigrator;

namespace Lombard.Data.Tracking.Migrator.Migrations
{
    [Migration(20150622100071)]
    public class _20150622100001_CreateRefWorkTypeTable : Migration
    {
        public override void Up()
        {
            Create.Table("ref_work_type")
                .WithColumn("work_type_code").AsString(50).NotNullable().PrimaryKey("PK_fxa_ref_work_type")
                .WithColumn("work_type_name").AsString(100);
            SeedData();
        }

        public override void Down()
        {
            Delete.Table("ref_work_type");
        }

        private void SeedData()
        {
            Execute.Sql("insert into dbo.ref_work_type (work_type_code, work_type_name) values ('NABCHQ_POD','NABCHQ_POD');");
            Execute.Sql("insert into dbo.ref_work_type (work_type_code, work_type_name) values ('NABCHQ_LBOX','NABCHQ_LBOX');");
            Execute.Sql("insert into dbo.ref_work_type (work_type_code, work_type_name) values ('NABCHQ_LISTINGS','NABCHQ_LISTINGS');");
            Execute.Sql("insert into dbo.ref_work_type (work_type_code, work_type_name) values ('NABCHQ_APOST','NABCHQ_APOST');");
            Execute.Sql("insert into dbo.ref_work_type (work_type_code, work_type_name) values ('NABCHQ_SURPLUS','NABCHQ_SURPLUS');");
            Execute.Sql("insert into dbo.ref_work_type (work_type_code, work_type_name) values ('BQL_POD','BQL_POD');");
            Execute.Sql("insert into dbo.ref_work_type (work_type_code, work_type_name) values ('NABCHQ_INWARDFV','NABCHQ_INWARDFV');");
            Execute.Sql("insert into dbo.ref_work_type (work_type_code, work_type_name) values ('NABCHQ_INWARDNFV','NABCHQ_INWARDNFV');");

        }
    }
}
