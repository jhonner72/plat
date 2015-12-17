Feature: Correct Codeline
	Given a valid codeline
	When a codeline correction request is issued to DIPS
	The codeline correction request batch should be viewable in DIPS

@CorrectCodeline
Scenario: Auto correct a good codeline
	Given there are no CorrectCodeline rows for batch number 58300013
	And a CorrectBatchCodelineRequest with batch number 58300013 is added to the queue for the following CorrectCodeline vouchers:
	| auxDom | extraAuxDom | bsbNumber | accountNumber | transactionCode | documentReferenceNumber | imageRotation | capturedAmount | amountConfidenceLevel | amountRegionOfInterest | workType   | processingDate | collectingBank |
	| 001193 |             | 013812    | 256902729     | 50              | 000111222			   |               | 45.67          | 1000                  |                        | NABCHQ_POD | 2015/03/17     | 123456         |
	| 001193 |             | 092002    | 814649        | 50              | 000111223               |               | 2341.45        | 785                   |                        | NABCHQ_POD | 2015/03/17     | 123456         |
	And a CorrectBatchCodelineRequest with batch number 58300013 contains this voucher batch: 
	| Field              | Value      |
	| workType           | NABCHQ_POD |
	| processingState    | SA         |
	| scannedBatchNumber | 58300013   |
	| captureBSB         | 085384     |
	| collectingBank     | 123456     |
	| unitID             | 123        |
	When the message is published to the queue and CorrectBatchCodelineRequest process the message with this information:
	| Field                 | Value                                     |
	| CorrelationId         | NSBD-6e5bc63b-be84-4053-a4ce-191abbd69f27 |
	| RoutingKey            | NSBD                                      |
	| PublishTimeOutSeconds | 3                                         |
	Then a DipsQueue table row will exist with the following CorrectCodeline values
	| Field      | Value           |
	| S_LOCATION | CodelineCorrect |
	| S_LOCK     | 0               |
	| S_CLIENT   | NabChq          |
	| S_JOB_ID   | NabChqPod       |
	| S_MODIFIED | 0               |
	| S_COMPLETE | 0               |
	| S_BATCH    | 58300013        |
	| S_TRACE    | 000111222       |
	| S_PRIORITY | 5               |
	| S_VERSION  | 4.0.2.152       |
	| S_SDATE    | 17/03/15        |
	| RoutingKey | NSBD            |
	And DipsNabChq table rows will exist with the following CorrectCodeline values
	| S_BATCH  | S_TRACE   | S_LENGTH | batch    | trace     | proc_date | ead | ser_num | bsb_num | acc_num   | trancode | amount  | job_id    | processing_state | captureBSB | collecting_bank | unit_id | amountConfidenceLevel |
	| 58300013 | 000111222 | 01025    | 58300013 | 000111222 | 20150317  |     | 001193  | 013812  | 256902729 | 50       | 45.67   | NabChqPod | SA               | 085384     | 123456          | 123     | 999                   |
	| 58300013 | 000111223 | 01025    | 58300013 | 000111223 | 20150317  |     | 001193  | 092002  | 814649    | 50       | 2341.45 | NabChqPod | SA               | 085384     | 123456          | 123     | 785                   |
	And DipsDbIndex table rows will exist with the following CorrectCodeline values - trace
	| BATCH    | TRACE        | TABLE_NO |
	| 58300013 | 000111222    | 0        |
	| 58300013 | 000111223    | 0        |

@CorrectCodeline
Scenario: Auto correct a good codeline with a long DRN
	Given there are no CorrectCodeline rows for batch number 58300015
	And a CorrectBatchCodelineRequest with batch number 58300015 is added to the queue for the following CorrectCodeline vouchers:
	| auxDom | extraAuxDom | bsbNumber | accountNumber | transactionCode | documentReferenceNumber | imageRotation | capturedAmount | amountConfidenceLevel | amountRegionOfInterest | workType   | processingDate | collectingBank | repostFromDRN |
	| 001193 |             | 013812    | 256902729     | 50              | 000000111222            |               | 45.67          |                       |                        | NABCHQ_POD | 2015/03/17     | 123456         | 000000111222  |
	| 001193 |             | 092002    | 814649        | 50              | 000000111223            |               | 2341.45        |                       |                        | NABCHQ_POD | 2015/03/18     | 123456         | 000000111223  |
	And a CorrectBatchCodelineRequest with batch number 58300015 contains this voucher batch: 
	| Field              | Value      |
	| workType           | NABCHQ_POD |
	| processingState    | SA         |
	| scannedBatchNumber | 58300015   |
	| captureBSB         | 085384     |
	| collectingBank     | 123456     |
	| unitID             | 123        |
	When the message is published to the queue and CorrectBatchCodelineRequest process the message with this information:
	| Field                 | Value                                     |
	| CorrelationId         | NSBD-6e5bc63b-be84-4053-a4ce-191abbd69f28 |
	| RoutingKey            | NSBD                                      |
	| PublishTimeOutSeconds | 3                                         |
	Then a DipsQueue table row will exist with the following CorrectCodeline values
	| Field      | Value           |
	| S_LOCATION | CodelineCorrect |
	| S_LOCK     | 0               |
	| S_CLIENT   | NabChq          |
	| S_JOB_ID   | NabChqPod       |
	| S_MODIFIED | 0               |
	| S_COMPLETE | 0               |
	| S_BATCH    | 58300015        |
	| S_PRIORITY | 5               |
	| S_VERSION  | 4.0.2.152       |
	| S_SDATE    | 17/03/15        |
	And DipsNabChq table rows will exist with the following CorrectCodeline values - long Drn
	| S_BATCH  | S_LENGTH | batch    | proc_date | ead | ser_num | bsb_num | acc_num   | trancode | amount  | job_id    | processing_state | captureBSB | collecting_bank | unit_id | repostFromDRN |
	| 58300015 | 01025    | 58300015 | 20150317  |     | 001193  | 013812  | 256902729 | 50       | 45.67   | NabChqPod | SA               | 085384     | 123456          | 123     | 000000111222  |
	| 58300015 | 01025    | 58300015 | 20150318  |     | 001193  | 092002  | 814649    | 50       | 2341.45 | NabChqPod | SA               | 085384     | 123456          | 123     | 000000111223  |
	And DipsDbIndex table rows will exist with batch number 58300015