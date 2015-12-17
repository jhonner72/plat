Feature: GenerateCorrespondingVoucher
	Given a valid batch
	When a Generate Corresponding Voucher request is issued to DIPS
	The codeline correction request batch should be viewable in DIPS

@GenerateCorrespondingVoucher
Scenario: Generate Corresponding Voucher
	Given there are no GenerateCorrespondingVoucher rows for trace number 000111222 or 000111223
	And a GenerateCorrespondingVoucherRequest with batch number 58800018 is added to the queue for the following GenerateCorrespondingVoucher vouchers:
	| auxDom | extraAuxDom | bsbNumber | accountNumber | transactionCode | documentReferenceNumber | imageRotation | capturedAmount | amountConfidenceLevel | amountRegionOfInterest | workType   | processingDate | preAdjustmentAmount | adjustedFlag | thirdPartyCheckFailed | thirdPartyPoolFlag | highValueFlag | voucherDelayedIndicator | thirdPartyMixedDepositReturnFlag |
	| 001193 |             | 013812    | 256902729     | 50              | 000111222            |               | 45.67          |                       |                        | NABCHQ_POD | 2015/03/17     | 55                  | true         | true                  | true               | true          | 1                       | true                             |
	| 001193 |             | 092002    | 814649        | 50              | 000111223            |               | 2341.45        |                       |                        | NABCHQ_POD | 2015/03/17     | 55                  | false        | false                 | false              | false         | 0                       | false                            |
	And a GenerateCorrespondingVoucherRequest with batch number 58800018 contains this voucher batch: 
	| Field              | Value      |
	| workType           | NABCHQ_POD |
	| processingState    | SA         |
	| scannedBatchNumber | 58800018   |
	| captureBSB         | 085384     |
	| collectingBank     | 123456     |
	| unitID             | 123        |
	When the message is published to the queue and GenerateCorrespondingVoucherRequest process the message with this information:
	| Field                 | Value                                     |
	| CorrelationId         | NCST-6e5bc63b-be84-4053-a4ce-191abbd69f27 |
	| RoutingKey            | NCST                                      |
	| PublishTimeOutSeconds | 3                                         |
	Then a DipsQueue table row will exist with the following GenerateCorrespondingVoucher values
	| Field      | Value                        |
	| S_LOCATION | GenerateCorrespondingVoucher |
	| S_LOCK     | 0                            |
	| S_CLIENT   | NabChq                       |
	| S_JOB_ID   | NabChqPod                    |
	| S_MODIFIED | 0                            |
	| S_COMPLETE | 0                            |
	| S_TRACE    | 000111222                    |
	| S_PRIORITY | 5                            |
	| S_VERSION  | 4.0.2.152                    |
	| S_SDATE    | 17/03/15                     |
	| RoutingKey | NCST                         |
	And DipsNabChq table rows will exist with the following GenerateCorrespondingVoucher values
	| S_TRACE   | S_LENGTH | trace     | proc_date | ead | ser_num | bsb_num | acc_num   | trancode | amount | job_id    | processing_state | captureBSB | collecting_bank | unit_id | tpcMixedDepRet | fxa_tpc_suspense_pool_flag | highValueFlag |
	| 000111222 | 01025    | 000111222 | 20150317  |     | 001193  | 013812  | 256902729 | 50       |        | NabChqPod | SA               | 085384     | 123456          | 123     | 1              | 1                          | 1             |
	| 000111223 | 01025    | 000111223 | 20150317  |     | 001193  | 092002  | 814649    | 50       |        | NabChqPod | SA               | 085384     | 123456          | 123     | 0              | 0                          | 0             |
	And DipsDbIndex table rows will exist with the following GenerateCorrespondingVoucher values
	| TRACE     | TABLE_NO |
	| 000111222 | 0        |
	| 000111223 | 0        |