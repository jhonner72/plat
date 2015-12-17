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
namespace Lombard.Adapters.A2iaAdapter.IntegrationTests.Features
{
    using TechTalk.SpecFlow;
    
    
    [System.CodeDom.Compiler.GeneratedCodeAttribute("TechTalk.SpecFlow", "1.9.0.77")]
    [System.Runtime.CompilerServices.CompilerGeneratedAttribute()]
    [Microsoft.VisualStudio.TestTools.UnitTesting.TestClassAttribute()]
    public partial class ProcessSingleBatchUsingA2IaFeature
    {
        
        private static TechTalk.SpecFlow.ITestRunner testRunner;
        
#line 1 "ProcessChequeImageUsingA2Ia.feature"
#line hidden
        
        [Microsoft.VisualStudio.TestTools.UnitTesting.ClassInitializeAttribute()]
        public static void FeatureSetup(Microsoft.VisualStudio.TestTools.UnitTesting.TestContext testContext)
        {
            testRunner = TechTalk.SpecFlow.TestRunnerManager.GetTestRunner();
            TechTalk.SpecFlow.FeatureInfo featureInfo = new TechTalk.SpecFlow.FeatureInfo(new System.Globalization.CultureInfo("en-US"), "ProcessSingleBatchUsingA2Ia", "In order to read the courtesy amount from a cheque image\r\nAs FXA\r\nI want to be us" +
                    "e A2IA adapter to process the requested images", ProgrammingLanguage.CSharp, ((string[])(null)));
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
                        && (TechTalk.SpecFlow.FeatureContext.Current.FeatureInfo.Title != "ProcessSingleBatchUsingA2Ia")))
            {
                Lombard.Adapters.A2iaAdapter.IntegrationTests.Features.ProcessSingleBatchUsingA2IaFeature.FeatureSetup(null);
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
        [Microsoft.VisualStudio.TestTools.UnitTesting.DescriptionAttribute("Auto read the courtesy amount from a batch")]
        [Microsoft.VisualStudio.TestTools.UnitTesting.TestPropertyAttribute("FeatureTitle", "ProcessSingleBatchUsingA2Ia")]
        [Microsoft.VisualStudio.TestTools.UnitTesting.TestCategoryAttribute("AutoReadCar")]
        public virtual void AutoReadTheCourtesyAmountFromABatch()
        {
            TechTalk.SpecFlow.ScenarioInfo scenarioInfo = new TechTalk.SpecFlow.ScenarioInfo("Auto read the courtesy amount from a batch", new string[] {
                        "AutoReadCar"});
#line 7
this.ScenarioSetup(scenarioInfo);
#line 8
 testRunner.Given("the ICR engine adapter service is running in a well setup environment", ((string)(null)), ((TechTalk.SpecFlow.Table)(null)), "Given ");
#line hidden
            TechTalk.SpecFlow.Table table1 = new TechTalk.SpecFlow.Table(new string[] {
                        "documentReferenceNumber",
                        "processingDate",
                        "transactionCode"});
            table1.AddRow(new string[] {
                        "000111222",
                        "2015/03/17",
                        "9"});
            table1.AddRow(new string[] {
                        "000111223",
                        "2015/03/17",
                        "95"});
            table1.AddRow(new string[] {
                        "000111224",
                        "2015/03/17",
                        "95"});
            table1.AddRow(new string[] {
                        "000111225",
                        "2015/03/17",
                        "95"});
            table1.AddRow(new string[] {
                        "000111226",
                        "2015/03/17",
                        "9"});
            table1.AddRow(new string[] {
                        "185000030",
                        "2015/07/13",
                        "95"});
            table1.AddRow(new string[] {
                        "185000031",
                        "2015/07/13",
                        "95"});
            table1.AddRow(new string[] {
                        "185000032",
                        "2015/07/13",
                        "95"});
            table1.AddRow(new string[] {
                        "185000033",
                        "2015/07/13",
                        "95"});
            table1.AddRow(new string[] {
                        "185000034",
                        "2015/07/13",
                        "95"});
            table1.AddRow(new string[] {
                        "185000035",
                        "2015/07/13",
                        "95"});
            table1.AddRow(new string[] {
                        "185000036",
                        "2015/07/13",
                        "95"});
            table1.AddRow(new string[] {
                        "185000037",
                        "2015/07/13",
                        "95"});
            table1.AddRow(new string[] {
                        "185000038",
                        "2015/07/13",
                        "95"});
            table1.AddRow(new string[] {
                        "185000039",
                        "2015/07/13",
                        "95"});
            table1.AddRow(new string[] {
                        "185000040",
                        "2015/07/13",
                        "95"});
            table1.AddRow(new string[] {
                        "185000041",
                        "2015/07/13",
                        "95"});
            table1.AddRow(new string[] {
                        "185000042",
                        "2015/07/13",
                        "95"});
            table1.AddRow(new string[] {
                        "185000043",
                        "2015/07/13",
                        "95"});
            table1.AddRow(new string[] {
                        "185000044",
                        "2015/07/13",
                        "95"});
            table1.AddRow(new string[] {
                        "185000045",
                        "2015/07/13",
                        "95"});
            table1.AddRow(new string[] {
                        "185000046",
                        "2015/07/13",
                        "95"});
            table1.AddRow(new string[] {
                        "185000047",
                        "2015/07/13",
                        "95"});
            table1.AddRow(new string[] {
                        "185000048",
                        "2015/07/13",
                        "95"});
            table1.AddRow(new string[] {
                        "185000049",
                        "2015/07/13",
                        "95"});
            table1.AddRow(new string[] {
                        "185000050",
                        "2015/07/13",
                        "95"});
            table1.AddRow(new string[] {
                        "185000051",
                        "2015/07/13",
                        "95"});
            table1.AddRow(new string[] {
                        "185000052",
                        "2015/07/13",
                        "95"});
            table1.AddRow(new string[] {
                        "185000053",
                        "2015/07/13",
                        "95"});
            table1.AddRow(new string[] {
                        "185000054",
                        "2015/07/13",
                        "95"});
            table1.AddRow(new string[] {
                        "185000055",
                        "2015/07/13",
                        "95"});
            table1.AddRow(new string[] {
                        "185000056",
                        "2015/07/13",
                        "95"});
            table1.AddRow(new string[] {
                        "185000057",
                        "2015/07/13",
                        "95"});
            table1.AddRow(new string[] {
                        "185000058",
                        "2015/07/13",
                        "95"});
            table1.AddRow(new string[] {
                        "185000059",
                        "2015/07/13",
                        "95"});
            table1.AddRow(new string[] {
                        "142000757",
                        "2015/06/16",
                        "9"});
            table1.AddRow(new string[] {
                        "142000758",
                        "2015/06/16",
                        "9"});
            table1.AddRow(new string[] {
                        "142000759",
                        "2015/06/16",
                        "9"});
            table1.AddRow(new string[] {
                        "142000760",
                        "2015/06/16",
                        "9"});
            table1.AddRow(new string[] {
                        "142000761",
                        "2015/06/16",
                        "9"});
            table1.AddRow(new string[] {
                        "142000762",
                        "2015/06/16",
                        "9"});
            table1.AddRow(new string[] {
                        "142000763",
                        "2015/06/16",
                        "9"});
            table1.AddRow(new string[] {
                        "142000764",
                        "2015/06/16",
                        "9"});
            table1.AddRow(new string[] {
                        "142000765",
                        "2015/06/16",
                        "9"});
            table1.AddRow(new string[] {
                        "142000766",
                        "2015/06/16",
                        "9"});
            table1.AddRow(new string[] {
                        "142000767",
                        "2015/06/16",
                        "9"});
            table1.AddRow(new string[] {
                        "142000768",
                        "2015/06/16",
                        "9"});
            table1.AddRow(new string[] {
                        "142000769",
                        "2015/06/16",
                        "9"});
            table1.AddRow(new string[] {
                        "142000770",
                        "2015/06/16",
                        "9"});
            table1.AddRow(new string[] {
                        "142000771",
                        "2015/06/16",
                        "9"});
            table1.AddRow(new string[] {
                        "142000772",
                        "2015/06/16",
                        "9"});
            table1.AddRow(new string[] {
                        "142000773",
                        "2015/06/16",
                        "9"});
            table1.AddRow(new string[] {
                        "142000774",
                        "2015/06/16",
                        "NO"});
            table1.AddRow(new string[] {
                        "142000775",
                        "2015/06/16",
                        "NO"});
            table1.AddRow(new string[] {
                        "142000776",
                        "2015/06/16",
                        "?"});
            table1.AddRow(new string[] {
                        "142000777",
                        "2015/06/16",
                        "?"});
#line 9
 testRunner.When("a request is received for job identifier NSBD-765fe520-fdfc-4cd0-ae6c-63196ddf63b" +
                    "b with the following vouchers:", ((string)(null)), table1, "When ");
#line hidden
            TechTalk.SpecFlow.Table table2 = new TechTalk.SpecFlow.Table(new string[] {
                        "documentReferenceNumber",
                        "imageRotation",
                        "capturedAmount",
                        "amountConfidenceLevel"});
            table2.AddRow(new string[] {
                        "000111222",
                        "0",
                        "1000",
                        "996"});
            table2.AddRow(new string[] {
                        "000111223",
                        "0",
                        "25000",
                        "956"});
            table2.AddRow(new string[] {
                        "000111224",
                        "0",
                        "3500",
                        "541"});
            table2.AddRow(new string[] {
                        "000111225",
                        "0",
                        "786773",
                        "996"});
            table2.AddRow(new string[] {
                        "000111226",
                        "0",
                        "30359",
                        "992"});
            table2.AddRow(new string[] {
                        "185000030",
                        "0",
                        "1318",
                        "25"});
            table2.AddRow(new string[] {
                        "185000031",
                        "0",
                        "10000",
                        "552"});
            table2.AddRow(new string[] {
                        "185000032",
                        "0",
                        "125000",
                        "786"});
            table2.AddRow(new string[] {
                        "185000033",
                        "0",
                        "6000",
                        "16"});
            table2.AddRow(new string[] {
                        "185000034",
                        "0",
                        "91700",
                        "41"});
            table2.AddRow(new string[] {
                        "185000035",
                        "0",
                        "1400",
                        "929"});
            table2.AddRow(new string[] {
                        "185000036",
                        "0",
                        "376315",
                        "963"});
            table2.AddRow(new string[] {
                        "185000037",
                        "0",
                        "1115500",
                        "995"});
            table2.AddRow(new string[] {
                        "185000038",
                        "0",
                        "74595",
                        "785"});
            table2.AddRow(new string[] {
                        "185000039",
                        "0",
                        "37700",
                        "477"});
            table2.AddRow(new string[] {
                        "185000040",
                        "0",
                        "7815",
                        "924"});
            table2.AddRow(new string[] {
                        "185000041",
                        "0",
                        "10000",
                        "300"});
            table2.AddRow(new string[] {
                        "185000042",
                        "0",
                        "11190",
                        "700"});
            table2.AddRow(new string[] {
                        "185000043",
                        "0",
                        "759860",
                        "136"});
            table2.AddRow(new string[] {
                        "185000044",
                        "0",
                        "57450",
                        "988"});
            table2.AddRow(new string[] {
                        "185000045",
                        "0",
                        "10530",
                        "673"});
            table2.AddRow(new string[] {
                        "185000046",
                        "0",
                        "495",
                        "981"});
            table2.AddRow(new string[] {
                        "185000047",
                        "0",
                        "10219",
                        "978"});
            table2.AddRow(new string[] {
                        "185000048",
                        "0",
                        "491590",
                        "973"});
            table2.AddRow(new string[] {
                        "185000049",
                        "0",
                        "1205000",
                        "968"});
            table2.AddRow(new string[] {
                        "185000050",
                        "0",
                        "194565",
                        "154"});
            table2.AddRow(new string[] {
                        "185000051",
                        "0",
                        "614000",
                        "997"});
            table2.AddRow(new string[] {
                        "185000052",
                        "0",
                        "1398780",
                        "488"});
            table2.AddRow(new string[] {
                        "185000053",
                        "0",
                        "12000000",
                        "442"});
            table2.AddRow(new string[] {
                        "185000054",
                        "0",
                        "742500",
                        "969"});
            table2.AddRow(new string[] {
                        "185000055",
                        "0",
                        "12810",
                        "981"});
            table2.AddRow(new string[] {
                        "185000056",
                        "0",
                        "17480",
                        "981"});
            table2.AddRow(new string[] {
                        "185000057",
                        "0",
                        "3094521",
                        "971"});
            table2.AddRow(new string[] {
                        "185000058",
                        "0",
                        "4777",
                        "935"});
            table2.AddRow(new string[] {
                        "185000059",
                        "0",
                        "250000",
                        "976"});
            table2.AddRow(new string[] {
                        "142000757",
                        "0",
                        "2065100",
                        "0"});
            table2.AddRow(new string[] {
                        "142000758",
                        "0",
                        "20000",
                        "950"});
            table2.AddRow(new string[] {
                        "142000759",
                        "0",
                        "17000",
                        "996"});
            table2.AddRow(new string[] {
                        "142000760",
                        "0",
                        "5100",
                        "974"});
            table2.AddRow(new string[] {
                        "142000761",
                        "0",
                        "15200",
                        "830"});
            table2.AddRow(new string[] {
                        "142000762",
                        "0",
                        "14122",
                        "793"});
            table2.AddRow(new string[] {
                        "142000763",
                        "0",
                        "7300",
                        "993"});
            table2.AddRow(new string[] {
                        "142000764",
                        "0",
                        "261960",
                        "879"});
            table2.AddRow(new string[] {
                        "142000765",
                        "0",
                        "44000",
                        "991"});
            table2.AddRow(new string[] {
                        "142000766",
                        "0",
                        "350000",
                        "992"});
            table2.AddRow(new string[] {
                        "142000767",
                        "0",
                        "76500",
                        "981"});
            table2.AddRow(new string[] {
                        "142000768",
                        "0",
                        "15000",
                        "987"});
            table2.AddRow(new string[] {
                        "142000769",
                        "0",
                        "17562",
                        "931"});
            table2.AddRow(new string[] {
                        "142000770",
                        "0",
                        "3709",
                        "619"});
            table2.AddRow(new string[] {
                        "142000771",
                        "0",
                        "48991",
                        "46"});
            table2.AddRow(new string[] {
                        "142000772",
                        "0",
                        "165000",
                        "47"});
            table2.AddRow(new string[] {
                        "142000773",
                        "0",
                        "22400",
                        "307"});
            table2.AddRow(new string[] {
                        "142000774",
                        "0",
                        "30180",
                        "997"});
            table2.AddRow(new string[] {
                        "142000775",
                        "0",
                        "18300",
                        "997"});
            table2.AddRow(new string[] {
                        "142000776",
                        "0",
                        "8900",
                        "998"});
            table2.AddRow(new string[] {
                        "142000777",
                        "0",
                        "22900",
                        "989"});
#line 67
 testRunner.Then("a CAR result for job identifier NSBD-765fe520-fdfc-4cd0-ae6c-63196ddf63bb with th" +
                    "e following values is returned:", ((string)(null)), table2, "Then ");
#line hidden
            this.ScenarioCleanup();
        }
    }
}
#pragma warning restore
#endregion