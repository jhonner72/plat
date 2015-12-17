Feature: Correct Codeline Response Polling Job
	Given a valid record in queue database
	And valid vouchers in database
	When wait and a CorrectCodelineResponsePollingJob executed
	Then a CorrectBatchCodelineResponse including vouchers is added to the exchange

@CorrectCodelineResponsePollingJob
Scenario: Send correct codeline response
	Given there are Codeline Correction database rows for batch number 58300013 in DipsQueue database
	| Field      | Value               |
	| S_LOCATION | CodelineCorrectDone |
	| S_LOCK     | 0                   |
	| S_CLIENT   | NabChq              |
	| S_JOB_ID   | NabChqPod           |
	| S_MODIFIED | 0                   |
	| S_COMPLETE | 0                   |
	| S_BATCH    | 58300013            |
	| S_TRACE    | 583000026           |
	| S_PRIORITY | 5                   |
	| S_VERSION  | 4.0.2.152           |
	| S_SDATE    | 31/03/15            |
	| S_STIME    | 12:30:00            |
	| RoutingKey | NSBD                |
	And there are Codeline Correction database rows for batch number 58300013 in DipsNabChq database
	| S_BATCH  | S_TRACE   | S_LENGTH | batch    | trace     | proc_date | ead | ser_num | bsb_num | acc_num   | trancode | amount  | job_id    | ie_endpoint | unproc_flag | doc_ref_num | S_SEQUENCE | sys_date | captureBSB | processing_state | collecting_bank | unit_id | isGeneratedVoucher |
	| 58300013 | 583000026 | 01025    | 58300013 | 583000026 | 20150331  |     | 001193  | 013812  | 256902729 | 50       | 45.67   | NabChqPod | NAB         | 1           | 583000026   | 0000       | 20150624 | 085384     | SA               | 123456          | 123     | 0                  |
	| 58300013 | 583000027 | 01025    | 58300013 | 583000027 | 20150331  |     | 001193  | 092002  | 814649    | 50       | 2341.45 | NabChqPod | NAB         | 0           | 583000027   | 0001       | 20150625 | 085384     | SA               | 789101          | 123     | 1                  |
	When wait for 10 seconds until CorrectCodelineResponsePollingJob executed
	Then a CorrectBatchCodelineResponse with batch number 58300013 is added to the exchange and contains these vouchers:
	| documentReferenceNumber | bsbNumber | accountNumber | auxDom | extraAuxDom | transactionCode | amount  | targetEndPoint | unprocessable | collectingBank |
	| 583000026               | 013812    | 256902729     | 001193 |             | 50              | 45.67   | NAB            | True          | 123456         |
	| 583000027               | 092002    | 814649        | 001193 |             | 50              | 2341.45 | NAB            | False         | 789101         |
	And a CorrectBatchCodelineResponse with batch number 58300013 contains this voucher batch:
	| Field              | Value      |
	| workType           | NABCHQ_POD |
	| processingState    | SA         |
	| scannedBatchNumber | 58300013   |
	| captureBSB         | 085384     |
	| collectingBank     | 123456     |
	| unitID             | 123        |
	
@CorrectCodelineResponsePollingJob
Scenario: Send correct codeline response with exclude flag
	Given there are Codeline Correction database rows for batch number 58300019 in DipsQueue database
	| Field      | Value               |
	| S_LOCATION | CodelineCorrectDone |
	| S_LOCK     | 0                   |
	| S_CLIENT   | NabChq              |
	| S_JOB_ID   | NabChqPod           |
	| S_MODIFIED | 0                   |
	| S_COMPLETE | 0                   |
	| S_BATCH    | 58300019            |
	| S_TRACE    | 583000028           |
	| S_PRIORITY | 5                   |
	| S_VERSION  | 4.0.2.152           |
	| S_SDATE    | 31/03/15            |
	| S_STIME    | 12:30:00            |
	| RoutingKey | NSBD                |
	And there are Codeline Correction database rows for batch number 58300019 in DipsNabChq database
	| S_BATCH  | S_TRACE   | S_LENGTH | batch    | trace     | proc_date | ead | ser_num | bsb_num | acc_num   | trancode | amount  | job_id    | ie_endpoint | unproc_flag | export_exclude_flag | doc_ref_num | S_SEQUENCE | sys_date | captureBSB | processing_state | collecting_bank | unit_id |
	| 58300019 | 583000028 | 01025    | 58300019 | 583000028 | 20150331  |     | 001193  | 013812  | 256902729 | 50       | 45.67   | NabChqPod | NAB         | 1           | 0                   | 583000028   | 0000       | 20150624 | 085384     | SA               | 123456          | 123     |
	| 58300019 | 583000029 | 01025    | 58300019 | 583000029 | 20150331  |     | 001193  | 092002  | 814649    | 50       | 2341.45 | NabChqPod | NAB         | 0           | 0                   | 583000029   | 0001       | 20150624 | 085384     | SA               | 123456          | 123     |
	| 58300019 | 583000030 | 01025    | 58300019 | 583000030 | 20150331  |     | 001193  | 065002  | 814149    | 50       | 921.45  | NabChqPod | NAB         | 0           | 1                   | 583000030   | 0002       | 20150624 | 085384     | SA               | 123456          | 123     |
	When wait for 10 seconds until CorrectCodelineResponsePollingJob executed
	Then a CorrectBatchCodelineResponse with batch number 58300019 is added to the exchange and contains these vouchers:
	| documentReferenceNumber | bsbNumber | accountNumber | auxDom | extraAuxDom | transactionCode | amount  | targetEndPoint | unprocessable |
	| 583000028               | 013812    | 256902729     | 001193 |             | 50              | 45.67   | NAB            | True          |
	| 583000029               | 092002    | 814649        | 001193 |             | 50              | 2341.45 | NAB            | False         |
	And a CorrectBatchCodelineResponse with batch number 58300013 contains this voucher batch:
	| Field              | Value      |
	| workType           | NABCHQ_POD |
	| processingState    | SA         |
	| scannedBatchNumber | 58300019   |
	| captureBSB         | 085384     |
	| collectingBank     | 123456     |
	| unitID             | 123        |
	
@CorrectCodelineResponsePollingJob
Scenario: Send correct codeline response with repost
	Given there are Codeline Correction database rows for batch number 58300021 in DipsQueue database
	| Field      | Value               |
	| S_LOCATION | CodelineCorrectDone |
	| S_LOCK     | 0                   |
	| S_CLIENT   | NabChq              |
	| S_JOB_ID   | NabChqPod           |
	| S_MODIFIED | 0                   |
	| S_COMPLETE | 0                   |
	| S_BATCH    | 58300021            |
	| S_TRACE    | 583000026           |
	| S_PRIORITY | 5                   |
	| S_VERSION  | 4.0.2.152           |
	| S_SDATE    | 31/03/15            |
	| S_STIME    | 12:30:00            |
	| RoutingKey | NSBD                |
	And there are Codeline Correction database rows for batch number 58300013 in DipsNabChq database
	| S_BATCH  | S_TRACE   | S_LENGTH | batch    | trace     | proc_date | ead | ser_num | bsb_num | acc_num   | trancode | amount  | job_id    | ie_endpoint | unproc_flag | doc_ref_num | S_SEQUENCE | sys_date | captureBSB | processing_state | collecting_bank | unit_id | repostFromDRN | repostFromProcessingDate |
	| 58300021 | 583000026 | 01025    | 58300021 | 583000026 | 20150331  |     | 001193  | 013812  | 256902729 | 50       | 45.67   | NabChqPod | NAB         | 1           | 583000026   | 0000       | 20150624 | 085384     | SA               | 123456          | 123     | 583000026     |                          |
	| 58300021 | 583000027 | 01025    | 58300021 | 583000027 | 20150331  |     | 001193  | 092002  | 814649    | 50       | 2341.45 | NabChqPod | NAB         | 0           | 583000027   | 0001       | 20150625 | 085384     | SA               | 123456          | 123     | 583000027     | 20150625                 |
	When wait for 10 seconds until CorrectCodelineResponsePollingJob executed
	Then a CorrectBatchCodelineResponse with batch number 58300021 is added to the exchange and contains these vouchers:
	| documentReferenceNumber | bsbNumber | accountNumber | auxDom | extraAuxDom | transactionCode | amount  | targetEndPoint | unprocessable | repostFromDRN | repostFromProcessingDate |
	| 583000026               | 013812    | 256902729     | 001193 |             | 50              | 45.67   | NAB            | True          | 583000026     | 19500101                 |
	| 583000027               | 092002    | 814649        | 001193 |             | 50              | 2341.45 | NAB            | False         | 583000027     | 20150625                 |
	And a CorrectBatchCodelineResponse with batch number 58300013 contains this voucher batch:
	| Field              | Value      |
	| workType           | NABCHQ_POD |
	| processingState    | SA         |
	| scannedBatchNumber | 58300021   |
	| captureBSB         | 085384     |
	| collectingBank     | 123456     |
	| unitID             | 123        |
	