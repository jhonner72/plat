Feature: Create VIF File
	In order to send data to NAB
	As the Vif service
	I want to create a VIF file

@vif
Scenario Outline: Create vif file
  Given some vouchers is in "<Request>" job folder
	And a request with job id "<Request>" to create a vif file is submitted
   When the request is consumed by the service
   Then a vif file named "<VifOutputName>" should be generated in "<Request>" job folder
   And the generated vif file named "<VifOutputName>" in "<Request>" job folder is correct

  Examples:
    | Request                  | VifOutputName                 |
    | Request1                 | MO.FXA.VIF.NAB381.D151028.R87 |
    | SingleCreditDebit        | MO.FXA.VIF.NAB380.D151030.R34 |
    | CorrectPrimeCreditChosen | MO.FXA.VIF.NAB380.D151030.R34 |
    | PrimeCreditHasNoAuxDom   | MO.FXA.VIF.NAB380.D151030.R34 |
    | NoVoucherData            | MO.FXA.VIF.NAB380.D151030.R34 |
    | HighValueCreditChosen    | MO.FXA.VIF.NAB380.D151030.R34 |
