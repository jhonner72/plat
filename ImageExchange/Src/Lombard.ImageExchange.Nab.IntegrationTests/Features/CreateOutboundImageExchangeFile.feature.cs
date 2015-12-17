﻿// ------------------------------------------------------------------------------
//  <auto-generated>
//      This code was generated by SpecFlow (http://www.specflow.org/).
//      SpecFlow Version:1.9.0.77
//      SpecFlow Generator Version:1.9.0.0
//      Runtime Version:4.0.30319.34209
// 
//      Changes to this file may cause incorrect behavior and will be lost if
//      the code is regenerated.
//  </auto-generated>
// ------------------------------------------------------------------------------
#region Designer generated code
#pragma warning disable
namespace Lombard.ImageExchange.Nab.IntegrationTests.Features
{
    using TechTalk.SpecFlow;
    
    
    [System.CodeDom.Compiler.GeneratedCodeAttribute("TechTalk.SpecFlow", "1.9.0.77")]
    [System.Runtime.CompilerServices.CompilerGeneratedAttribute()]
    [Microsoft.VisualStudio.TestTools.UnitTesting.TestClassAttribute()]
    public partial class CreateImageExchangeFileFeature
    {
        
        private static TechTalk.SpecFlow.ITestRunner testRunner;
        
#line 1 "CreateOutboundImageExchangeFile.feature"
#line hidden
        
        [Microsoft.VisualStudio.TestTools.UnitTesting.ClassInitializeAttribute()]
        public static void FeatureSetup(Microsoft.VisualStudio.TestTools.UnitTesting.TestContext testContext)
        {
            testRunner = TechTalk.SpecFlow.TestRunnerManager.GetTestRunner();
            TechTalk.SpecFlow.FeatureInfo featureInfo = new TechTalk.SpecFlow.FeatureInfo(new System.Globalization.CultureInfo("en-US"), "Create Image Exchange File", "In order to send data to other banks and endpoints\r\nAs the outbound image exchang" +
                    "e system\r\nI want to create an Image Exchange file", ProgrammingLanguage.CSharp, ((string[])(null)));
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
                        && (TechTalk.SpecFlow.FeatureContext.Current.FeatureInfo.Title != "Create Image Exchange File")))
            {
                Lombard.ImageExchange.Nab.IntegrationTests.Features.CreateImageExchangeFileFeature.FeatureSetup(null);
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
        [Microsoft.VisualStudio.TestTools.UnitTesting.DescriptionAttribute("Create Outbound Image Exchange File")]
        [Microsoft.VisualStudio.TestTools.UnitTesting.TestPropertyAttribute("FeatureTitle", "Create Image Exchange File")]
        [Microsoft.VisualStudio.TestTools.UnitTesting.TestCategoryAttribute("outboundImageExchange")]
        public virtual void CreateOutboundImageExchangeFile()
        {
            TechTalk.SpecFlow.ScenarioInfo scenarioInfo = new TechTalk.SpecFlow.ScenarioInfo("Create Outbound Image Exchange File", new string[] {
                        "outboundImageExchange"});
#line 8
this.ScenarioSetup(scenarioInfo);
#line 9
 testRunner.Given("voucher data has been extracted for jobIdentifier 333-444-555", ((string)(null)), ((TechTalk.SpecFlow.Table)(null)), "Given ");
#line hidden
            TechTalk.SpecFlow.Table table1 = new TechTalk.SpecFlow.Table(new string[] {
                        "transactionCode",
                        "documentReferenceNumber",
                        "bsbNumber",
                        "accountNumber",
                        "documentType",
                        "auxDom",
                        "processingDate",
                        "amount",
                        "extraAuxDom"});
            table1.AddRow(new string[] {
                        "12",
                        "88888888",
                        "049898",
                        "54987754",
                        "DR",
                        "Test8",
                        "2015-05-08T00:00:00.000+10:00",
                        "4634567",
                        "test"});
#line 10
  testRunner.And("the voucher metadata contains the following data", ((string)(null)), table1, "And ");
#line hidden
            TechTalk.SpecFlow.Table table2 = new TechTalk.SpecFlow.Table(new string[] {
                        "collectingBsb",
                        "processingState",
                        "captureBsb",
                        "unitID",
                        "scannedBatchNumber"});
            table2.AddRow(new string[] {
                        "091123",
                        "VIC",
                        "123456",
                        "",
                        "33333333"});
#line 15
  testRunner.And("the voucherBatch metadata contains the following data", ((string)(null)), table2, "And ");
#line hidden
            TechTalk.SpecFlow.Table table3 = new TechTalk.SpecFlow.Table(new string[] {
                        "voucherDelayedIndicator",
                        "manualRepair",
                        "targetEndPoint",
                        "targetEndPoint"});
            table3.AddRow(new string[] {
                        "",
                        "0",
                        "FSV",
                        ""});
#line 20
  testRunner.And("the voucherProcess metadata contains the following data", ((string)(null)), table3, "And ");
#line 25
 testRunner.When("a CreateImageExchangeFileRequest is added to the queue for the given job and targ" +
                    "etEndPoint FSV and sequenceNumber 123 for today", ((string)(null)), ((TechTalk.SpecFlow.Table)(null)), "When ");
#line hidden
            this.ScenarioCleanup();
        }
    }
}
#pragma warning restore
#endregion
