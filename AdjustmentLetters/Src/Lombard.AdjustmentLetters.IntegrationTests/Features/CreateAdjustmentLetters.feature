Feature: CreateAdjustmentLetters
	A valid payload is available in request queue
	As AdjustmentLeters adapter process
	AdjustmentLetters file will be created

@CreateAdjustmentLetters
Scenario: Create AdjustmentLetters
	Given a valid AdjustmentLetters request is available in the request queue with this information
	| Field          | Value                                                                  |
	| JobIdentifier  | NEDF_26ed3b9c-158e-425b-b868/NGAL-097ede2d-2a5a-4d1b-9c64-aed5cacc7cbf |
	| processingDate | 2015-09-02                                                             |
	And AdjustmentLetters payload contains these voucherProcess
	| ScannedBatchNumber | DocumentReferenceNumber | AdjustedFlag | TransactionLinkNumber | AccountNumber | BsbNumber | OutputFilenamePrefix | CollectingBank |
	| 67500031           | 175000019               | true         | 1                     | 899920322     | 081408    | coles                | 082988         |
	| 67500031           | 175000020               | false        | 1                     | 899920322     | 082001    | coles                | 082991         |
	| 67500032           | 175000022               | true         | 1                     | 899920322     | 082003    | super                | 082996         |
	| 67500032           | 175000023               | false        | 1                     | 899920322     | 082004    | super                | 083001         |
	| 67500033           | 175000025               | true         | 1                     | 899920322     | 082005    | cuscal               | 083002         |
	| 67500033           | 175000026               | false        | 1                     | 899920322     | 082008    | cuscal               | 083002         |
	When create AdjustmentLetters process run
	Then these AdjustmentLetters files will be created
	| FileName                        |
	| coles_0001.pdf                  |
	| super_0002.pdf                  |
	| cuscal_0003.pdf                 |
	| DEV.NAB.RPT.LTR.ADJC.20150902.2 |
	And AdjustmentLetters response will be created
	| documentReferenceNumber | filename        | processingDate | scannedBatchNumber | transactionLinkNumber |
	| 175000019               | coles_0001.pdf  | 2015-09-02     | 67500031           | 1                     |
	| 175000022               | super_0002.pdf  | 2015-09-02     | 67500032           | 1                     |
	| 175000025               | cuscal_0003.pdf | 2015-09-02     | 67500033           | 1                     |
