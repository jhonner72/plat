using FluentMigrator;
using FluentMigrator.Runner.Extensions;
using System;
namespace Lombard.Data.Tracking.Migrator.Migrations
{
    [Migration(2015100600101)]
    public class _2015100600001_CreateDipsOperatorStats : Migration
    {
        public override void Up()
        {
            if (Schema.Table("dips_operator_stats").Exists())
            {
                Delete.Table("dips_operator_stats");
            }

            Create.Table("dips_operator_stats")
                    .WithColumn("ID").AsInt32().Identity(1, 1).NotNullable().PrimaryKey("PK_dips_operator_stats")
                    .WithColumn("BATCH").AsString(8)
                    .WithColumn("UTIME").AsString(10)
                    .WithColumn("REC_NO").AsString(10)
                    .WithColumn("MODULE").AsString(40)
                    .WithColumn("CLIENT").AsString(80)
                    .WithColumn("JOB_ID").AsString(128)
                    .WithColumn("QUEUE").AsString(40)
                    .WithColumn("OPERATOR").AsString(17)
                    .WithColumn("DATE_OPEN").AsString(10)
                    .WithColumn("TIME_OPEN").AsString(8)
                    .WithColumn("TIME_CLOSE").AsString(8)
                    .WithColumn("TIME_ELAPD").AsString(8)
                    .WithColumn("TIME_ACTIVE").AsString(8)
                    .WithColumn("FRM_CNT").AsString(10)
                    .WithColumn("FRM_CNT_B").AsString(10)
                    .WithColumn("KEY_CNT").AsString(10)
                    .WithColumn("FRM_TOUCH").AsString(10)
                    .WithColumn("FRM_TIME").AsString(10)
                    .WithColumn("FLD_CNT").AsString(10)
                    .WithColumn("FLD_CNT_B").AsString(10)
                    .WithColumn("FLD_TIME").AsString(10)
                    .WithColumn("KEY_CNT]").AsString(10)
                    .WithColumn("KEY_CNT_B").AsString(10)
                    .WithColumn("NUM_FRMS").AsString(10)
                    .WithColumn("SITE").AsString(20)
                    .WithColumn("MACHINE_NUMBER").AsString(20)
                    .WithColumn("INSTANCE_NUMBER").AsString(2)
                    .WithColumn("FILENAME").AsString(255)
                    .WithColumn("MODIFIED_DATE").AsDate();




        }

        public override void Down()
        {
            Delete.Table("dips_operator_stats");
        }
    }
}
