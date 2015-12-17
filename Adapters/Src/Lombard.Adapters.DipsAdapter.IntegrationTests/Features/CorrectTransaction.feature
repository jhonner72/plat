Feature: Correct Transaction
	Given a valid transaction
	When a transaction validation request is issued to DIPS
	The transaction validation request batch should be viewable in DIPS

@CorrectTransaction
Scenario: Auto correct a good transaction
	Given there are no CorrectTransaction rows for batch number 58300013
	And a CorrectBatchTransactionRequest with batch number 58300013 is added to the queue for the following CorrectTransaction vouchers:
	| auxDom | extraAuxDom | bsbNumber | accountNumber | transactionCode | documentReferenceNumber | amount  | documentType | unprocessable | processingDate |
	| 001193 |             | 013812    | 256902729     | 50              | 000111222            | 45.67   | CRT          | false         | 2015/03/17     |
	| 001193 |             | 092002    | 814649        | 50              | 000111223            | 2341.45 | DBT          | false         | 2015/03/17     |
	And a CorrectBatchTransactionRequest with batch number 58300013 contains this voucher batch: 
	| Field              | Value      |
	| workType           | NABCHQ_POD |
	| processingState    | SA         |
	| scannedBatchNumber | 58300013   |
	| captureBSB         | 085384     |
	| collectingBank     | 123456     |
	| unitID             | 123        |
	When the message is published to the queue and CorrectBatchTransactionRequest process the message with this information:
	| Field                 | Value                                     |
	| CorrelationId         | NSBD-6e5bc63b-be84-4053-a4ce-191abbd69f27 |
	| RoutingKey            | NSBD                                      |
	| PublishTimeOutSeconds | 3                                         |
	Then a DipsQueue table row will exist with the following CorrectTransaction values
	| Field      | Value         |
	| S_LOCATION | ExpertBalance |
	| S_LOCK     | 0             |
	| S_CLIENT   | NabChq        |
	| S_JOB_ID   | NabChqPod     |
	| S_MODIFIED | 0             |
	| S_COMPLETE | 0             |
	| S_BATCH    | 58300013      |
	| S_TRACE    | 000111222     |
	| S_PRIORITY | 5             |
	| S_VERSION  | 4.0.2.152     |
	| S_SDATE    | 17/03/15      |
	| RoutingKey | NSBD          |
	And DipsNabChq table rows will exist with the following CorrectTransaction values
	| S_BATCH  | S_TRACE   | S_LENGTH | batch    | trace     | proc_date | ead | ser_num | bsb_num | acc_num   | trancode | amount  | job_id    | micr_unproc_flag | doc_type | processing_state | captureBSB | collecting_bank | unit_id |
	| 58300013 | 000111222 | 01025    | 58300013 | 000111222 | 20150317  |     | 001193  | 013812  | 256902729 | 50       | 45.67   | NabChqPod | 0                | CRT      | SA               | 085384     | 123456          | 123     |
	| 58300013 | 000111223 | 01025    | 58300013 | 000111223 | 20150317  |     | 001193  | 092002  | 814649    | 50       | 2341.45 | NabChqPod | 0                | DBT      | SA               | 085384     | 123456          | 123     |
	And DipsDbIndex table rows will exist with the following CorrectTransaction values
	| BATCH    | TRACE        | TABLE_NO |
	| 58300013 | 000111222    | 0        |
	| 58300013 | 000111223    | 0        |

@CorrectTransaction
Scenario: Auto correct another good transaction
	Given there are no CorrectTransaction rows for batch number 96384477
	And a CorrectBatchTransactionRequest with batch number 96384477 is added to the queue for the following CorrectTransaction vouchers:
	| auxDom | extraAuxDom | bsbNumber | accountNumber | transactionCode | documentReferenceNumber | imageRotation | capturedAmount | amountConfidenceLevel | amountRegionOfInterest | reasonCode | processingDate | isGeneratedVoucher | thirdPartyCheckRequired | thirdPartyMixedDepositReturnFlag |
	| 333    | 666         | 333666    | 12345678      | 222             | 000111222            |               |                |                       |                        |            | 2015/03/17     | true               | true                    | true                             |
	And a CorrectBatchTransactionRequest with batch number 96384477 contains this voucher batch: 
	| Field              | Value      |
	| workType           | NABCHQ_POD |
	| processingState    | SA         |
	| scannedBatchNumber | 96384477   |
	| captureBSB         | 085384     |
	| collectingBank     | 123456     |
	| unitID             | 123        |
	When the message is published to the queue and CorrectBatchTransactionRequest process the message with this information:
	| Field                 | Value                                     |
	| CorrelationId         | NSBD-6e5bc63b-be84-4053-a4ce-191abbd69f27 |
	| RoutingKey            | NSBD                                      |
	| PublishTimeOutSeconds | 3                                         |
	Then a DipsQueue table row will exist with the following CorrectTransaction values
	| Field      | Value         |
	| S_LOCATION | ExpertBalance |
	| S_LOCK     | 0             |
	| S_CLIENT   | NabChq        |
	| S_JOB_ID   | NabChqPod     |
	| S_MODIFIED | 0             |
	| S_COMPLETE | 0             |
	| S_BATCH    | 96384477      |
	| S_TRACE    | 000111222     |
	| S_PRIORITY | 5             |
	| S_VERSION  | 4.0.2.152     |
	| S_SDATE    | 17/03/15      |
	And DipsNabChq table rows will exist with the following CorrectTransaction values
	| S_BATCH  | S_TRACE   | S_LENGTH | batch    | trace     | proc_date | ead | ser_num | bsb_num | acc_num  | trancode | amount | job_id    | balanceReason | processing_state | captureBSB | collecting_bank | unit_id | isGeneratedVoucher | tpcRequired | tpcMixedDepRet |
	| 96384477 | 000111222 | 01025    | 96384477 | 000111222 | 20150317  | 666 | 333     | 333666  | 12345678 | 222      |        | NabChqPod |               | SA               | 085384     | 123456          | 123     | 1                  | Y           | 1              |
	And DipsDbIndex table rows will exist with the following CorrectTransaction values																				 						   
	| BATCH    | TRACE        | TABLE_NO |
	| 96384477 | 000111222    | 0        |
