Feature: Validate Codeline Response Polling Job
	Given a valid record in queue database
	And valid vouchers in database
	When wait and a ValidateCodelineResponsePollingJob executed
	Then a ValidateBatchCodelineResponse including vouchers is added to the exchange

@ValidateCodelineResponsePollingJob
Scenario: ValidateCodeline response for a clean voucher
	Given there are Codeline Validation database rows for batch number 58300013 in DipsQueue database
	| Field      | Value                  |
	| S_LOCATION | CodelineValidationDone |
	| S_LOCK     | 0                      |
	| S_CLIENT   | NabChq                 |
	| S_JOB_ID   | NabChqPod              |
	| S_MODIFIED | 0                      |
	| S_COMPLETE | 0                      |
	| S_BATCH    | 58300013               |
	| S_TRACE    | 583000026              |
	| S_PRIORITY | 5                      |
	| S_VERSION  | 4.0.2.152              |
	| S_SDATE    | 31/03/15               |
	| S_STIME    | 12:30:00               |
	| RoutingKey | NSBD                   |
	And there are Codeline Validation database rows for batch number 58300013 in DipsNabChq database
	| S_BATCH  | S_TRACE   | doc_ref_num | S_LENGTH | batch    | trace     | proc_date | ead | ser_num | bsb_num | acc_num   | trancode | amount  | job_id    | S_STATUS1 | S_SEQUENCE | sys_date | captureBSB | processing_state | collecting_bank | unit_id |
	| 58300013 | 583000026 | 583000026   | 01025    | 58300013 | 583000026 | 20150331  |     | 001193  | 013812  | 256902729 | 50       | 45.67   | NabChqPod | 0         | 0000       | 20150624 | 085384     | SA               | 123456          | 123     |
	| 58300013 | 583000027 | 583000027   | 01025    | 58300013 | 583000027 | 20150331  |     | 001193  | 092002  | 814649    | 50       | 2341.45 | NabChqPod | 0         | 0001       | 20150625 | 085384     | SA               | 123456          | 123     |
	When wait for 10 seconds until ValidateCodelineResponsePollingJob executed
	Then a ValidateBatchCodelineResponse with batch number 58300013 is added to the exchange and contains these vouchers:
	| documentReferenceNumber | accountNumberStatus | auxDomStatus | bsbNumberStatus | extraAuxDomStatus | transactionCodeStatus | amountStatus |
	| 583000026               | true                | true         | true            | true              | true                  | true         |
	| 583000027               | true                | true         | true            | true              | true                  | true         |
	And a ValidateBatchCodelineResponse with batch number 58300013 contains this voucher batch:
	| Field              | Value      |
	| workType           | NABCHQ_POD |
	| processingState    | SA         |
	| scannedBatchNumber | 58300013   |
	| captureBSB         | 085384     |
	| collectingBank     | 123456     |
	| unitID             | 123        |
	  