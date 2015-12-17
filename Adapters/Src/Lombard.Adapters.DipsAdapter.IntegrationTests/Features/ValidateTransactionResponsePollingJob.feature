Feature: Validate Transaction Response Polling Job
	Given a valid record in queue database
	And valid vouchers in database
	When wait and a ValidateTransactionResponsePollingJob executed
	Then a ValidateBatchTransactionResponse including vouchers is added to the exchange

@ValidateTransactionResponsePollingJob
Scenario: ValidateTransaction response for a clean voucher
	Given there are Transaction Validation database rows for batch number 58300013 in DipsQueue database
	| Field      | Value             |
	| S_LOCATION | AutoBalancingDone |
	| S_LOCK     | 0                 |
	| S_CLIENT   | NabChq            |
	| S_JOB_ID   | NabChqPod         |
	| S_MODIFIED | 0                 |
	| S_COMPLETE | 0                 |
	| S_BATCH    | 58300013          |
	| S_TRACE    | 583000026         |
	| S_PRIORITY | 5                 |
	| S_VERSION  | 4.0.2.152         |
	| S_SDATE    | 31/03/15          |
	| S_STIME    | 12:30:00          |
	| RoutingKey | NSBD              |
	And there are Transaction Validation database rows for batch number 58300013 in DipsNabChq database
	| S_BATCH  | S_TRACE   | doc_ref_num | S_LENGTH | batch    | trace     | proc_date | ead | ser_num | bsb_num | acc_num   | trancode | amount  | job_id    | S_STATUS1 | micr_unproc_flag | doc_type | balanceReason | manual_repair | micr_suspect_fraud_flag | op_id | S_SEQUENCE | sys_date | captureBSB | processing_state | collecting_bank | unit_id | isGeneratedVoucher | tpcRequired | fxa_unencoded_ECD_return |
	| 58300013 | 583000026 | 583000026   | 01025    | 58300013 | 583000026 | 20150331  |     | 001193  | 013812  | 256902729 | 50       | 45.67   | NabChqPod | 0         | 0                | CRT      | Unbalanced    | 0             | 0                       | abc   | 0000       | 20150624 | 085384     | SA               | 123456          | 123     | 1                  | 1           | 0                        |
	| 58300013 | 583000027 | 583000027   | 01025    | 58300013 | 583000027 | 20150331  |     | 001193  | 092002  | 814649    | 50       | 2341.45 | NabChqPod | 0         | 0                | DBT      | HighValue     | 0             | 0                       | def   | 0001       | 20150625 | 085384     | SA               | 123456          | 123     | 0                  | 0           | 1                        |
	When wait for 10 seconds until ValidateTransactionResponsePollingJob executed
	Then a ValidateBatchTransactionResponse with batch number 58300013 is added to the exchange and contains these vouchers:
	| documentReferenceNumber | reasonCode | transactionLinkNumber | unprocessable | thirdPartyCheckRequired | unencodedECDReturnFlag |
	| 583000026               | Unbalanced |                       | false         | false                   | false                  |
	| 583000027               | HighValue  |                       | false         | false                   | true                   |
	And a ValidateBatchTransactionResponse with batch number 58300013 contains this voucher batch:
	| Field              | Value      |
	| workType           | NABCHQ_POD |
	| processingState    | SA         |
	| scannedBatchNumber | 58300013   |
	| captureBSB         | 085384     |
	| collectingBank     | 123456     |
	| unitID             | 123        |
	And with these validate transaction voucher values:
	| accountNumber | amount  | auxDom | bsbNumber | documentReferenceNumber | documentType | extraAuxDom | transactionCode |
	| 256902729     | 45.67   | 001193 | 013812    | 583000026               | CRT          |             | 50              |
	| 814649        | 2341.45 | 001193 | 092002    | 583000027               | DBT          |             | 50              |



