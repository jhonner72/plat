using FluentMigrator;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace Lombard.Data.Tracking.Migrator.Migrations
{
    [Migration(20150924100001)]
    public class _20150924100001_CreateRefNonBankFiTable : Migration
    {
        public override void Up()
        {
            if (Schema.Table("ref_non_bank_fi").Exists())
            {
                Delete.Table("ref_non_bank_fi");
            }

            Create.Table("ref_non_bank_fi")
                    .WithColumn("ref_id").AsInt32().NotNullable().Identity().PrimaryKey("PK_ref_non_bank_fi")
                    .WithColumn("bsb").AsString(6).NotNullable();

            SeedData();
        }

        public override void Down()
        {
            Delete.Table("ref_non_bank_fi");
        }

        private void SeedData()
        {
            Insert.IntoTable("ref_non_bank_fi").Row(new { bsb = "082034" });
            Insert.IntoTable("ref_non_bank_fi").Row(new { bsb = "082045" });
            Insert.IntoTable("ref_non_bank_fi").Row(new { bsb = "082047" });
            Insert.IntoTable("ref_non_bank_fi").Row(new { bsb = "082049" });
            Insert.IntoTable("ref_non_bank_fi").Row(new { bsb = "082105" });
            Insert.IntoTable("ref_non_bank_fi").Row(new { bsb = "082142" });
            Insert.IntoTable("ref_non_bank_fi").Row(new { bsb = "082154" });
            Insert.IntoTable("ref_non_bank_fi").Row(new { bsb = "082170" });
            Insert.IntoTable("ref_non_bank_fi").Row(new { bsb = "082190" });
            Insert.IntoTable("ref_non_bank_fi").Row(new { bsb = "082339" });
            Insert.IntoTable("ref_non_bank_fi").Row(new { bsb = "082363" });
            Insert.IntoTable("ref_non_bank_fi").Row(new { bsb = "082382" });
            Insert.IntoTable("ref_non_bank_fi").Row(new { bsb = "082395" });
            Insert.IntoTable("ref_non_bank_fi").Row(new { bsb = "082396" });
            Insert.IntoTable("ref_non_bank_fi").Row(new { bsb = "082456" });
            Insert.IntoTable("ref_non_bank_fi").Row(new { bsb = "082462" });
            Insert.IntoTable("ref_non_bank_fi").Row(new { bsb = "082463" });
            Insert.IntoTable("ref_non_bank_fi").Row(new { bsb = "083973" });
            Insert.IntoTable("ref_non_bank_fi").Row(new { bsb = "082685" });
            Insert.IntoTable("ref_non_bank_fi").Row(new { bsb = "082925" });
            Insert.IntoTable("ref_non_bank_fi").Row(new { bsb = "082940" });
            Insert.IntoTable("ref_non_bank_fi").Row(new { bsb = "082941" });
            Insert.IntoTable("ref_non_bank_fi").Row(new { bsb = "083052" });
            Insert.IntoTable("ref_non_bank_fi").Row(new { bsb = "083126" });
            Insert.IntoTable("ref_non_bank_fi").Row(new { bsb = "083133" });
            Insert.IntoTable("ref_non_bank_fi").Row(new { bsb = "083188" });
            Insert.IntoTable("ref_non_bank_fi").Row(new { bsb = "083422" });
            Insert.IntoTable("ref_non_bank_fi").Row(new { bsb = "083428" });
            Insert.IntoTable("ref_non_bank_fi").Row(new { bsb = "083452" });
            Insert.IntoTable("ref_non_bank_fi").Row(new { bsb = "083455" });
            Insert.IntoTable("ref_non_bank_fi").Row(new { bsb = "083545" });
            Insert.IntoTable("ref_non_bank_fi").Row(new { bsb = "083613" });
            Insert.IntoTable("ref_non_bank_fi").Row(new { bsb = "083728" });
            Insert.IntoTable("ref_non_bank_fi").Row(new { bsb = "083847" });
            Insert.IntoTable("ref_non_bank_fi").Row(new { bsb = "084045" });
            Insert.IntoTable("ref_non_bank_fi").Row(new { bsb = "084058" });
            Insert.IntoTable("ref_non_bank_fi").Row(new { bsb = "085229" });
            Insert.IntoTable("ref_non_bank_fi").Row(new { bsb = "084179" });
            Insert.IntoTable("ref_non_bank_fi").Row(new { bsb = "084352" });
            Insert.IntoTable("ref_non_bank_fi").Row(new { bsb = "085037" });
            Insert.IntoTable("ref_non_bank_fi").Row(new { bsb = "085040" });
            Insert.IntoTable("ref_non_bank_fi").Row(new { bsb = "086509" });
            Insert.IntoTable("ref_non_bank_fi").Row(new { bsb = "085249" });
            Insert.IntoTable("ref_non_bank_fi").Row(new { bsb = "086044" });
            Insert.IntoTable("ref_non_bank_fi").Row(new { bsb = "086047" });
            Insert.IntoTable("ref_non_bank_fi").Row(new { bsb = "086505" });
            Insert.IntoTable("ref_non_bank_fi").Row(new { bsb = "085109" });
            Insert.IntoTable("ref_non_bank_fi").Row(new { bsb = "085118" });
        }
    }
}
