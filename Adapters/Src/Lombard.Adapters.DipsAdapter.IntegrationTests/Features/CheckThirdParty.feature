Feature: CheckThirdParty
	Given a valid batch
	When a check third party request is issued to DIPS
	The codeline correction request batch should be viewable in DIPS

@CheckThirdParty
Scenario: Check Third Party
	Given there are no CheckThirdParty rows for batch number 58800018
	And a CheckThirdPartyBatchRequest with batch number 58800018 is added to the queue for the following CheckThirdParty vouchers:
	| auxDom | extraAuxDom | bsbNumber | accountNumber | transactionCode | documentReferenceNumber | imageRotation | capturedAmount | amountConfidenceLevel | amountRegionOfInterest | workType   | processingDate | preAdjustmentAmount | adjustedFlag | thirdPartyCheckFailed | thirdPartyPoolFlag | highValueFlag | voucherDelayedIndicator | thirdPartyMixedDepositReturnFlag | dipsSequenceNumber |
	| 001193 |             | 013812    | 256902729     | 50              | 000111222            |               | 45.67          |                       |                        | NABCHQ_POD | 2015/03/17     | 55                  | true         | true                  | true               | true          | 1                       | true                             | 0000               |
	| 001193 |             | 092002    | 814649        | 50              | 000111223            |               | 2341.45        |                       |                        | NABCHQ_POD | 2015/03/17     | 55                  | false        | false                 | false              | false         | 0                       | false                            | 0001               |
	And a CheckThirdPartyBatchRequest with batch number 58800018 contains this voucher batch: 
	| Field              | Value      |
	| workType           | NABCHQ_POD |
	| processingState    | SA         |
	| scannedBatchNumber | 58800018   |
	| captureBSB         | 085384     |
	| collectingBank     | 123456     |
	| unitID             | 123        |
	When the message is published to the queue and CheckThirdPartyBatchRequest process the message with this information:
	| Field                 | Value                                     |
	| CorrelationId         | NSBD-6e5bc63b-be84-4053-a4ce-191abbd69f27 |
	| RoutingKey            | NSBD                                      |
	| PublishTimeOutSeconds | 3                                         |
	Then a DipsQueue table row will exist with the following CheckThirdParty values
	| Field      | Value           |
	| S_LOCATION | ThirdPartyCheck |
	| S_LOCK     | 0               |
	| S_CLIENT   | NabChq          |
	| S_JOB_ID   | NabChqPod       |
	| S_MODIFIED | 0               |
	| S_COMPLETE | 0               |
	| S_BATCH    | 58800018        |
	| S_TRACE    | 000111222       |
	| S_PRIORITY | 5               |
	| S_VERSION  | 4.0.2.152       |
	| S_SDATE    | 17/03/15        |
	| RoutingKey | NSBD            |
	And DipsNabChq table rows will exist with the following CheckThirdParty values
	| S_BATCH  | S_TRACE   | S_LENGTH | batch    | trace     | proc_date | ead | ser_num | bsb_num | acc_num   | trancode | amount | job_id    | processing_state | captureBSB | collecting_bank | unit_id | tpcMixedDepRet | fxa_tpc_suspense_pool_flag | highValueFlag |
	| 58800018 | 000111222 | 01025    | 58800018 | 000111222 | 20150317  |     | 001193  | 013812  | 256902729 | 50       |        | NabChqPod | SA               | 085384     | 123456          | 123     | 1              | 1                          | 1             |
	| 58800018 | 000111223 | 01025    | 58800018 | 000111223 | 20150317  |     | 001193  | 092002  | 814649    | 50       |        | NabChqPod | SA               | 085384     | 123456          | 123     | 0              | 0                          | 0             |
	And DipsDbIndex table rows will exist with the following CheckThirdParty values
	| BATCH    | TRACE        | TABLE_NO |
	| 58800018 | 000111222    | 0        |
	| 58800018 | 000111223    | 0        |