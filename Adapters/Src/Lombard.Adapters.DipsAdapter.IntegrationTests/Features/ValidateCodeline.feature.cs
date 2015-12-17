﻿// ------------------------------------------------------------------------------
//  <auto-generated>
//      This code was generated by SpecFlow (http://www.specflow.org/).
//      SpecFlow Version:1.9.0.77
//      SpecFlow Generator Version:1.9.0.0
//      Runtime Version:4.0.30319.18408
// 
//      Changes to this file may cause incorrect behavior and will be lost if
//      the code is regenerated.
//  </auto-generated>
// ------------------------------------------------------------------------------
#region Designer generated code
#pragma warning disable
namespace Lombard.Adapters.DipsAdapter.IntegrationTests.Features
{
    using TechTalk.SpecFlow;
    
    
    [System.CodeDom.Compiler.GeneratedCodeAttribute("TechTalk.SpecFlow", "1.9.0.77")]
    [System.Runtime.CompilerServices.CompilerGeneratedAttribute()]
    [Microsoft.VisualStudio.TestTools.UnitTesting.TestClassAttribute()]
    public partial class ValidateCodelineFeature
    {
        
        private static TechTalk.SpecFlow.ITestRunner testRunner;
        
#line 1 "ValidateCodeline.feature"
#line hidden
        
        [Microsoft.VisualStudio.TestTools.UnitTesting.ClassInitializeAttribute()]
        public static void FeatureSetup(Microsoft.VisualStudio.TestTools.UnitTesting.TestContext testContext)
        {
            testRunner = TechTalk.SpecFlow.TestRunnerManager.GetTestRunner();
            TechTalk.SpecFlow.FeatureInfo featureInfo = new TechTalk.SpecFlow.FeatureInfo(new System.Globalization.CultureInfo("en-US"), "Validate Codeline", "Given a valid codeline\r\nWhen a codeline validation request is issued to DIPS\r\nThe" +
                    " codeline validation request batch should be viewable in DIPS", ProgrammingLanguage.CSharp, ((string[])(null)));
            testRunner.OnFeatureStart(featureInfo);
        }
        
        [Microsoft.VisualStudio.TestTools.UnitTesting.ClassCleanupAttribute()]
        public static void FeatureTearDown()
        {
            testRunner.OnFeatureEnd();
            testRunner = null;
        }
        
        [Microsoft.VisualStudio.TestTools.UnitTesting.TestInitializeAttribute()]
        public virtual void TestInitialize()
        {
            if (((TechTalk.SpecFlow.FeatureContext.Current != null) 
                        && (TechTalk.SpecFlow.FeatureContext.Current.FeatureInfo.Title != "Validate Codeline")))
            {
                Lombard.Adapters.DipsAdapter.IntegrationTests.Features.ValidateCodelineFeature.FeatureSetup(null);
            }
        }
        
        [Microsoft.VisualStudio.TestTools.UnitTesting.TestCleanupAttribute()]
        public virtual void ScenarioTearDown()
        {
            testRunner.OnScenarioEnd();
        }
        
        public virtual void ScenarioSetup(TechTalk.SpecFlow.ScenarioInfo scenarioInfo)
        {
            testRunner.OnScenarioStart(scenarioInfo);
        }
        
        public virtual void ScenarioCleanup()
        {
            testRunner.CollectScenarioErrors();
        }
        
        [Microsoft.VisualStudio.TestTools.UnitTesting.TestMethodAttribute()]
        [Microsoft.VisualStudio.TestTools.UnitTesting.DescriptionAttribute("Auto validate a good codeline")]
        [Microsoft.VisualStudio.TestTools.UnitTesting.TestPropertyAttribute("FeatureTitle", "Validate Codeline")]
        [Microsoft.VisualStudio.TestTools.UnitTesting.TestCategoryAttribute("ValidateCodeline")]
        public virtual void AutoValidateAGoodCodeline()
        {
            TechTalk.SpecFlow.ScenarioInfo scenarioInfo = new TechTalk.SpecFlow.ScenarioInfo("Auto validate a good codeline", new string[] {
                        "ValidateCodeline"});
#line 7
this.ScenarioSetup(scenarioInfo);
#line 8
 testRunner.Given("there are no ValidateCodeline rows for batch number 58300013", ((string)(null)), ((TechTalk.SpecFlow.Table)(null)), "Given ");
#line hidden
            TechTalk.SpecFlow.Table table1 = new TechTalk.SpecFlow.Table(new string[] {
                        "auxDom",
                        "extraAuxDom",
                        "bsbNumber",
                        "accountNumber",
                        "transactionCode",
                        "documentReferenceNumber",
                        "imageRotation",
                        "capturedAmount",
                        "amountConfidenceLevel",
                        "amountRegionOfInterest",
                        "processingDate"});
            table1.AddRow(new string[] {
                        "001193",
                        "",
                        "013812",
                        "256902729",
                        "50",
                        "583000026",
                        "",
                        "45.67",
                        "",
                        "",
                        "2015/03/31"});
            table1.AddRow(new string[] {
                        "001193",
                        "",
                        "092002",
                        "814649",
                        "50",
                        "583000027",
                        "",
                        "2341.45",
                        "",
                        "",
                        "2015/03/31"});
#line 9
 testRunner.And("a ValidateBatchCodelineRequest with batch number 58300013 is added to the queue f" +
                    "or the following ValidateCodeline vouchers:", ((string)(null)), table1, "And ");
#line hidden
            TechTalk.SpecFlow.Table table2 = new TechTalk.SpecFlow.Table(new string[] {
                        "Field",
                        "Value"});
            table2.AddRow(new string[] {
                        "workType",
                        "NABCHQ_POD"});
            table2.AddRow(new string[] {
                        "processingState",
                        "SA"});
            table2.AddRow(new string[] {
                        "scannedBatchNumber",
                        "58300013"});
            table2.AddRow(new string[] {
                        "captureBSB",
                        "085384"});
            table2.AddRow(new string[] {
                        "collectingBank",
                        "123456"});
            table2.AddRow(new string[] {
                        "unitID",
                        "123"});
#line 13
 testRunner.And("a ValidateBatchCodelineRequest with batch number 58300013 contains this voucher b" +
                    "atch:", ((string)(null)), table2, "And ");
#line hidden
            TechTalk.SpecFlow.Table table3 = new TechTalk.SpecFlow.Table(new string[] {
                        "Field",
                        "Value"});
            table3.AddRow(new string[] {
                        "CorrelationId",
                        "NSBD-6e5bc63b-be84-4053-a4ce-191abbd69f27"});
            table3.AddRow(new string[] {
                        "RoutingKey",
                        "NSBD"});
            table3.AddRow(new string[] {
                        "PublishTimeOutSeconds",
                        "3"});
#line 21
 testRunner.When("the message is published to the queue and ValidateBatchCodelineRequest process th" +
                    "e message with this information:", ((string)(null)), table3, "When ");
#line hidden
            TechTalk.SpecFlow.Table table4 = new TechTalk.SpecFlow.Table(new string[] {
                        "Field",
                        "Value"});
            table4.AddRow(new string[] {
                        "S_LOCATION",
                        "CodelineValidation"});
            table4.AddRow(new string[] {
                        "S_LOCK",
                        "0"});
            table4.AddRow(new string[] {
                        "S_CLIENT",
                        "NabChq"});
            table4.AddRow(new string[] {
                        "S_JOB_ID",
                        "NabChqPod"});
            table4.AddRow(new string[] {
                        "S_MODIFIED",
                        "0"});
            table4.AddRow(new string[] {
                        "S_COMPLETE",
                        "0"});
            table4.AddRow(new string[] {
                        "S_BATCH",
                        "58300013"});
            table4.AddRow(new string[] {
                        "S_TRACE",
                        "583000026"});
            table4.AddRow(new string[] {
                        "S_PRIORITY",
                        "5"});
            table4.AddRow(new string[] {
                        "S_VERSION",
                        "4.0.2.152"});
            table4.AddRow(new string[] {
                        "S_SDATE",
                        "31/03/15"});
            table4.AddRow(new string[] {
                        "RoutingKey",
                        "NSBD"});
#line 26
 testRunner.Then("a DipsQueue table row will exist with the following ValidateCodeline values", ((string)(null)), table4, "Then ");
#line hidden
            TechTalk.SpecFlow.Table table5 = new TechTalk.SpecFlow.Table(new string[] {
                        "S_BATCH",
                        "S_TRACE",
                        "S_LENGTH",
                        "batch",
                        "trace",
                        "proc_date",
                        "ead",
                        "ser_num",
                        "bsb_num",
                        "acc_num",
                        "trancode",
                        "amount",
                        "job_id",
                        "processing_state",
                        "captureBSB",
                        "collecting_bank",
                        "unit_id"});
            table5.AddRow(new string[] {
                        "58300013",
                        "583000026",
                        "01025",
                        "58300013",
                        "583000026",
                        "20150331",
                        "",
                        "001193",
                        "013812",
                        "256902729",
                        "50",
                        "45.67",
                        "NabChqPod",
                        "SA",
                        "085384",
                        "123456",
                        "123"});
            table5.AddRow(new string[] {
                        "58300013",
                        "583000027",
                        "01025",
                        "58300013",
                        "583000027",
                        "20150331",
                        "",
                        "001193",
                        "092002",
                        "814649",
                        "50",
                        "2341.45",
                        "NabChqPod",
                        "SA",
                        "085384",
                        "123456",
                        "123"});
#line 40
 testRunner.And("DipsNabChq table rows will exist with the following ValidateCodeline values", ((string)(null)), table5, "And ");
#line hidden
            TechTalk.SpecFlow.Table table6 = new TechTalk.SpecFlow.Table(new string[] {
                        "BATCH",
                        "TRACE",
                        "TABLE_NO"});
            table6.AddRow(new string[] {
                        "58300013",
                        "583000026",
                        "0"});
            table6.AddRow(new string[] {
                        "58300013",
                        "583000027",
                        "0"});
#line 44
 testRunner.And("DipsDbIndex table rows will exist with the following ValidateCodeline values", ((string)(null)), table6, "And ");
#line hidden
            this.ScenarioCleanup();
        }
        
        [Microsoft.VisualStudio.TestTools.UnitTesting.TestMethodAttribute()]
        [Microsoft.VisualStudio.TestTools.UnitTesting.DescriptionAttribute("Auto validate another good codeline")]
        [Microsoft.VisualStudio.TestTools.UnitTesting.TestPropertyAttribute("FeatureTitle", "Validate Codeline")]
        [Microsoft.VisualStudio.TestTools.UnitTesting.TestCategoryAttribute("ValidateCodeline")]
        public virtual void AutoValidateAnotherGoodCodeline()
        {
            TechTalk.SpecFlow.ScenarioInfo scenarioInfo = new TechTalk.SpecFlow.ScenarioInfo("Auto validate another good codeline", new string[] {
                        "ValidateCodeline"});
#line 50
this.ScenarioSetup(scenarioInfo);
#line 51
 testRunner.Given("there are no ValidateCodeline rows for batch number 96384477", ((string)(null)), ((TechTalk.SpecFlow.Table)(null)), "Given ");
#line hidden
            TechTalk.SpecFlow.Table table7 = new TechTalk.SpecFlow.Table(new string[] {
                        "auxDom",
                        "extraAuxDom",
                        "bsbNumber",
                        "accountNumber",
                        "transactionCode",
                        "documentReferenceNumber",
                        "imageRotation",
                        "capturedAmount",
                        "amountConfidenceLevel",
                        "amountRegionOfInterest",
                        "processingDate"});
            table7.AddRow(new string[] {
                        "333",
                        "666",
                        "333666",
                        "12345678",
                        "222",
                        "000111222",
                        "",
                        "",
                        "",
                        "",
                        "2015-04-14"});
#line 52
 testRunner.And("a ValidateBatchCodelineRequest with batch number 96384477 is added to the queue f" +
                    "or the following ValidateCodeline vouchers:", ((string)(null)), table7, "And ");
#line hidden
            TechTalk.SpecFlow.Table table8 = new TechTalk.SpecFlow.Table(new string[] {
                        "Field",
                        "Value"});
            table8.AddRow(new string[] {
                        "workType",
                        "NABCHQ_POD"});
            table8.AddRow(new string[] {
                        "processingState",
                        "SA"});
            table8.AddRow(new string[] {
                        "scannedBatchNumber",
                        "96384477"});
            table8.AddRow(new string[] {
                        "captureBSB",
                        "085384"});
            table8.AddRow(new string[] {
                        "collectingBank",
                        "123456"});
            table8.AddRow(new string[] {
                        "unitID",
                        "123"});
#line 55
 testRunner.And("a ValidateBatchCodelineRequest with batch number 58300013 contains this voucher b" +
                    "atch:", ((string)(null)), table8, "And ");
#line hidden
            TechTalk.SpecFlow.Table table9 = new TechTalk.SpecFlow.Table(new string[] {
                        "Field",
                        "Value"});
            table9.AddRow(new string[] {
                        "CorrelationId",
                        "NSBD-6e5bc63b-be84-4053-a4ce-191abbd69f27"});
            table9.AddRow(new string[] {
                        "RoutingKey",
                        "NSBD"});
            table9.AddRow(new string[] {
                        "PublishTimeOutSeconds",
                        "3"});
#line 63
 testRunner.When("the message is published to the queue and ValidateBatchCodelineRequest process th" +
                    "e message with this information:", ((string)(null)), table9, "When ");
#line hidden
            TechTalk.SpecFlow.Table table10 = new TechTalk.SpecFlow.Table(new string[] {
                        "Field",
                        "Value"});
            table10.AddRow(new string[] {
                        "S_LOCATION",
                        "CodelineValidation"});
            table10.AddRow(new string[] {
                        "S_LOCK",
                        "0"});
            table10.AddRow(new string[] {
                        "S_CLIENT",
                        "NabChq"});
            table10.AddRow(new string[] {
                        "S_JOB_ID",
                        "NabChqPod"});
            table10.AddRow(new string[] {
                        "S_MODIFIED",
                        "0"});
            table10.AddRow(new string[] {
                        "S_COMPLETE",
                        "0"});
            table10.AddRow(new string[] {
                        "S_BATCH",
                        "96384477"});
            table10.AddRow(new string[] {
                        "S_TRACE",
                        "000111222"});
            table10.AddRow(new string[] {
                        "S_PRIORITY",
                        "5"});
            table10.AddRow(new string[] {
                        "S_VERSION",
                        "4.0.2.152"});
            table10.AddRow(new string[] {
                        "S_SDATE",
                        "14/04/15"});
#line 68
 testRunner.Then("a DipsQueue table row will exist with the following ValidateCodeline values", ((string)(null)), table10, "Then ");
#line hidden
            TechTalk.SpecFlow.Table table11 = new TechTalk.SpecFlow.Table(new string[] {
                        "S_BATCH",
                        "S_TRACE",
                        "S_LENGTH",
                        "batch",
                        "trace",
                        "proc_date",
                        "ead",
                        "ser_num",
                        "bsb_num",
                        "acc_num",
                        "trancode",
                        "amount",
                        "job_id",
                        "processing_state",
                        "captureBSB",
                        "collecting_bank",
                        "unit_id"});
            table11.AddRow(new string[] {
                        "96384477",
                        "000111222",
                        "01025",
                        "96384477",
                        "000111222",
                        "20150414",
                        "666",
                        "333",
                        "333666",
                        "12345678",
                        "222",
                        "",
                        "NabChqPod",
                        "SA",
                        "085384",
                        "123456",
                        "123"});
#line 81
 testRunner.And("DipsNabChq table rows will exist with the following ValidateCodeline values", ((string)(null)), table11, "And ");
#line hidden
            TechTalk.SpecFlow.Table table12 = new TechTalk.SpecFlow.Table(new string[] {
                        "BATCH",
                        "TRACE",
                        "TABLE_NO"});
            table12.AddRow(new string[] {
                        "96384477",
                        "000111222",
                        "0"});
#line 84
 testRunner.And("DipsDbIndex table rows will exist with the following ValidateCodeline values", ((string)(null)), table12, "And ");
#line hidden
            this.ScenarioCleanup();
        }
    }
}
#pragma warning restore
#endregion
