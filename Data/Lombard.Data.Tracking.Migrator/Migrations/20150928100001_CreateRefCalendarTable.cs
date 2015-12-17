using FluentMigrator;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace Lombard.Data.Tracking.Migrator.Migrations
{
    [Migration(20150928100001)]
    public class _20150928100001_CreateRefCalendarTable : Migration
    {
        public override void Up()
        {
            if (Schema.Table("ref_calendar").Exists())
            {
                Delete.Table("ref_calendar");
            }

            Create.Table("ref_calendar")
                    .WithColumn("holiday_date").AsDate().NotNullable().PrimaryKey("PK_fxa_ref_bank_calendar")
                    .WithColumn("holiday_name").AsString(50).NotNullable();

            SeedData();
        }

        public override void Down()
        {
            Delete.Table("ref_calendar");
        }

        private void SeedData()
        {
            Execute.Sql("insert into dbo.ref_calendar (holiday_date, holiday_name) values ('20151225','Christmas Day');");
            Execute.Sql("insert into dbo.ref_calendar (holiday_date, holiday_name) values ('20151228','Boxing Day');");
            Execute.Sql("insert into dbo.ref_calendar (holiday_date, holiday_name) values ('20160101','New Years Day');");
            Execute.Sql("insert into dbo.ref_calendar (holiday_date, holiday_name) values ('20160126','Australia Day');");
            Execute.Sql("insert into dbo.ref_calendar (holiday_date, holiday_name) values ('20160325','Good Friday');");
            Execute.Sql("insert into dbo.ref_calendar (holiday_date, holiday_name) values ('20160326','Easter Saturday');");
            Execute.Sql("insert into dbo.ref_calendar (holiday_date, holiday_name) values ('20160328','Easter Monday');");
            Execute.Sql("insert into dbo.ref_calendar (holiday_date, holiday_name) values ('20160425','Anzac Day');");
            Execute.Sql("insert into dbo.ref_calendar (holiday_date, holiday_name) values ('20161227','Christmas Day');");
            Execute.Sql("insert into dbo.ref_calendar (holiday_date, holiday_name) values ('20161226','Boxing Day');");
        }
    }
}
