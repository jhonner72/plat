﻿// ------------------------------------------------------------------------------
//  <auto-generated>
//      This code was generated by SpecFlow (http://www.specflow.org/).
//      SpecFlow Version:1.9.0.77
//      SpecFlow Generator Version:1.9.0.0
//      Runtime Version:4.0.30319.42000
// 
//      Changes to this file may cause incorrect behavior and will be lost if
//      the code is regenerated.
//  </auto-generated>
// ------------------------------------------------------------------------------
#region Designer generated code
#pragma warning disable
namespace Lombard.AdjustmentLetters.IntegrationTests.Features
{
    using TechTalk.SpecFlow;
    
    
    [System.CodeDom.Compiler.GeneratedCodeAttribute("TechTalk.SpecFlow", "1.9.0.77")]
    [System.Runtime.CompilerServices.CompilerGeneratedAttribute()]
    [Microsoft.VisualStudio.TestTools.UnitTesting.TestClassAttribute()]
    public partial class CreateAdjustmentLettersFeature
    {
        
        private static TechTalk.SpecFlow.ITestRunner testRunner;
        
#line 1 "CreateAdjustmentLetters.feature"
#line hidden
        
        [Microsoft.VisualStudio.TestTools.UnitTesting.ClassInitializeAttribute()]
        public static void FeatureSetup(Microsoft.VisualStudio.TestTools.UnitTesting.TestContext testContext)
        {
            testRunner = TechTalk.SpecFlow.TestRunnerManager.GetTestRunner();
            TechTalk.SpecFlow.FeatureInfo featureInfo = new TechTalk.SpecFlow.FeatureInfo(new System.Globalization.CultureInfo("en-US"), "CreateAdjustmentLetters", "A valid payload is available in request queue\nAs AdjustmentLeters adapter process" +
                    "\nAdjustmentLetters file will be created", ProgrammingLanguage.CSharp, ((string[])(null)));
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
                        && (TechTalk.SpecFlow.FeatureContext.Current.FeatureInfo.Title != "CreateAdjustmentLetters")))
            {
                Lombard.AdjustmentLetters.IntegrationTests.Features.CreateAdjustmentLettersFeature.FeatureSetup(null);
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
        [Microsoft.VisualStudio.TestTools.UnitTesting.DescriptionAttribute("Create AdjustmentLetters")]
        [Microsoft.VisualStudio.TestTools.UnitTesting.TestPropertyAttribute("FeatureTitle", "CreateAdjustmentLetters")]
        [Microsoft.VisualStudio.TestTools.UnitTesting.TestCategoryAttribute("CreateAdjustmentLetters")]
        public virtual void CreateAdjustmentLetters()
        {
            TechTalk.SpecFlow.ScenarioInfo scenarioInfo = new TechTalk.SpecFlow.ScenarioInfo("Create AdjustmentLetters", new string[] {
                        "CreateAdjustmentLetters"});
#line 7
this.ScenarioSetup(scenarioInfo);
#line hidden
            TechTalk.SpecFlow.Table table1 = new TechTalk.SpecFlow.Table(new string[] {
                        "Field",
                        "Value"});
            table1.AddRow(new string[] {
                        "JobIdentifier",
                        "NEDF_26ed3b9c-158e-425b-b868/NGAL-097ede2d-2a5a-4d1b-9c64-aed5cacc7cbf"});
            table1.AddRow(new string[] {
                        "processingDate",
                        "2015-09-02"});
#line 8
 testRunner.Given("a valid AdjustmentLetters request is available in the request queue with this inf" +
                    "ormation", ((string)(null)), table1, "Given ");
#line hidden
            TechTalk.SpecFlow.Table table2 = new TechTalk.SpecFlow.Table(new string[] {
                        "ScannedBatchNumber",
                        "DocumentReferenceNumber",
                        "AdjustedFlag",
                        "TransactionLinkNumber",
                        "AccountNumber",
                        "BsbNumber",
                        "OutputFilenamePrefix",
                        "CollectingBank"});
            table2.AddRow(new string[] {
                        "67500031",
                        "175000019",
                        "true",
                        "1",
                        "899920322",
                        "081408",
                        "coles",
                        "082988"});
            table2.AddRow(new string[] {
                        "67500031",
                        "175000020",
                        "false",
                        "1",
                        "899920322",
                        "082001",
                        "coles",
                        "082991"});
            table2.AddRow(new string[] {
                        "67500032",
                        "175000022",
                        "true",
                        "1",
                        "899920322",
                        "082003",
                        "super",
                        "082996"});
            table2.AddRow(new string[] {
                        "67500032",
                        "175000023",
                        "false",
                        "1",
                        "899920322",
                        "082004",
                        "super",
                        "083001"});
            table2.AddRow(new string[] {
                        "67500033",
                        "175000025",
                        "true",
                        "1",
                        "899920322",
                        "082005",
                        "cuscal",
                        "083002"});
            table2.AddRow(new string[] {
                        "67500033",
                        "175000026",
                        "false",
                        "1",
                        "899920322",
                        "082008",
                        "cuscal",
                        "083002"});
#line 12
 testRunner.And("AdjustmentLetters payload contains these voucherProcess", ((string)(null)), table2, "And ");
#line 20
 testRunner.When("create AdjustmentLetters process run", ((string)(null)), ((TechTalk.SpecFlow.Table)(null)), "When ");
#line hidden
            TechTalk.SpecFlow.Table table3 = new TechTalk.SpecFlow.Table(new string[] {
                        "FileName"});
            table3.AddRow(new string[] {
                        "coles_0001.pdf"});
            table3.AddRow(new string[] {
                        "super_0002.pdf"});
            table3.AddRow(new string[] {
                        "cuscal_0003.pdf"});
            table3.AddRow(new string[] {
                        "DEV.NAB.RPT.LTR.ADJC.20150902.2"});
#line 21
 testRunner.Then("these AdjustmentLetters files will be created", ((string)(null)), table3, "Then ");
#line hidden
            TechTalk.SpecFlow.Table table4 = new TechTalk.SpecFlow.Table(new string[] {
                        "documentReferenceNumber",
                        "filename",
                        "processingDate",
                        "scannedBatchNumber",
                        "transactionLinkNumber"});
            table4.AddRow(new string[] {
                        "175000019",
                        "coles_0001.pdf",
                        "2015-09-02",
                        "67500031",
                        "1"});
            table4.AddRow(new string[] {
                        "175000022",
                        "super_0002.pdf",
                        "2015-09-02",
                        "67500032",
                        "1"});
            table4.AddRow(new string[] {
                        "175000025",
                        "cuscal_0003.pdf",
                        "2015-09-02",
                        "67500033",
                        "1"});
#line 27
 testRunner.And("AdjustmentLetters response will be created", ((string)(null)), table4, "And ");
#line hidden
            this.ScenarioCleanup();
        }
    }
}
#pragma warning restore
#endregion
