Feature: Validate Codeline
	Given a valid codeline
	When a codeline validation request is issued to DIPS
	The codeline validation request batch should be viewable in DIPS

@ValidateCodeline
Scenario: Auto validate a good codeline
	Given there are no ValidateCodeline rows for batch number 58300013
	And a ValidateBatchCodelineRequest with batch number 58300013 is added to the queue for the following ValidateCodeline vouchers:
	| auxDom | extraAuxDom | bsbNumber | accountNumber | transactionCode | documentReferenceNumber | imageRotation | capturedAmount | amountConfidenceLevel | amountRegionOfInterest | processingDate |
	| 001193 |             | 013812    | 256902729     | 50              | 583000026            |               | 45.67          |                       |                        | 2015/03/31     |
	| 001193 |             | 092002    | 814649        | 50              | 583000027            |               | 2341.45        |                       |                        | 2015/03/31     |
	And a ValidateBatchCodelineRequest with batch number 58300013 contains this voucher batch: 
	| Field              | Value      |
	| workType           | NABCHQ_POD |
	| processingState    | SA         |
	| scannedBatchNumber | 58300013   |
	| captureBSB         | 085384     |
	| collectingBank     | 123456     |
	| unitID             | 123        |
	When the message is published to the queue and ValidateBatchCodelineRequest process the message with this information:
	| Field                 | Value                                     |
	| CorrelationId         | NSBD-6e5bc63b-be84-4053-a4ce-191abbd69f27 |
	| RoutingKey            | NSBD                                      |
	| PublishTimeOutSeconds | 3                                         |
	Then a DipsQueue table row will exist with the following ValidateCodeline values
	| Field      | Value              |
	| S_LOCATION | CodelineValidation |
	| S_LOCK     | 0                  |
	| S_CLIENT   | NabChq             |
	| S_JOB_ID   | NabChqPod          |
	| S_MODIFIED | 0                  |
	| S_COMPLETE | 0                  |
	| S_BATCH    | 58300013           |
	| S_TRACE    | 583000026          |
	| S_PRIORITY | 5                  |
	| S_VERSION  | 4.0.2.152          |
	| S_SDATE    | 31/03/15           |
	| RoutingKey | NSBD               |
	And DipsNabChq table rows will exist with the following ValidateCodeline values
	| S_BATCH  | S_TRACE      | S_LENGTH | batch    | trace        | proc_date | ead | ser_num | bsb_num | acc_num   | trancode | amount  | job_id    | processing_state | captureBSB | collecting_bank | unit_id |
	| 58300013 | 583000026    | 01025    | 58300013 | 583000026    | 20150331  |     | 001193  | 013812  | 256902729 | 50       | 45.67   | NabChqPod | SA               | 085384     | 123456          | 123     |
	| 58300013 | 583000027    | 01025    | 58300013 | 583000027    | 20150331  |     | 001193  | 092002  | 814649    | 50       | 2341.45 | NabChqPod | SA               | 085384     | 123456          | 123     |
	And DipsDbIndex table rows will exist with the following ValidateCodeline values
	| BATCH    | TRACE        | TABLE_NO |
	| 58300013 | 583000026 | 0        |
	| 58300013 | 583000027 | 0        |

@ValidateCodeline
Scenario: Auto validate another good codeline
	Given there are no ValidateCodeline rows for batch number 96384477
	And a ValidateBatchCodelineRequest with batch number 96384477 is added to the queue for the following ValidateCodeline vouchers:
	| auxDom | extraAuxDom | bsbNumber | accountNumber | transactionCode | documentReferenceNumber | imageRotation | capturedAmount | amountConfidenceLevel | amountRegionOfInterest | processingDate |
	| 333    | 666         | 333666    | 12345678      | 222             | 000111222            |               |                |                       |                        | 2015-04-14     |
	And a ValidateBatchCodelineRequest with batch number 58300013 contains this voucher batch: 
	| Field              | Value      |
	| workType           | NABCHQ_POD |
	| processingState    | SA         |
	| scannedBatchNumber | 96384477   |
	| captureBSB         | 085384     |
	| collectingBank     | 123456     |
	| unitID             | 123        |
	When the message is published to the queue and ValidateBatchCodelineRequest process the message with this information:
	| Field                 | Value                                     |
	| CorrelationId         | NSBD-6e5bc63b-be84-4053-a4ce-191abbd69f27 |
	| RoutingKey            | NSBD                                      |
	| PublishTimeOutSeconds | 3                                         |
	Then a DipsQueue table row will exist with the following ValidateCodeline values
	| Field      | Value              |
	| S_LOCATION | CodelineValidation |
	| S_LOCK     | 0                  |
	| S_CLIENT   | NabChq             |
	| S_JOB_ID   | NabChqPod          |
	| S_MODIFIED | 0                  |
	| S_COMPLETE | 0                  |
	| S_BATCH    | 96384477           |
	| S_TRACE    | 000111222          |
	| S_PRIORITY | 5                  |
	| S_VERSION  | 4.0.2.152          |
	| S_SDATE    | 14/04/15           |
	And DipsNabChq table rows will exist with the following ValidateCodeline values
	| S_BATCH  | S_TRACE      | S_LENGTH | batch    | trace        | proc_date | ead | ser_num | bsb_num | acc_num  | trancode | amount | job_id    | processing_state | captureBSB | collecting_bank | unit_id |
	| 96384477 | 000111222    | 01025    | 96384477 | 000111222    | 20150414  | 666 | 333     | 333666  | 12345678 | 222      |        | NabChqPod | SA               | 085384     | 123456          | 123     |
	And DipsDbIndex table rows will exist with the following ValidateCodeline values																							
	| BATCH    | TRACE        | TABLE_NO |
	| 96384477 | 000111222    | 0        |