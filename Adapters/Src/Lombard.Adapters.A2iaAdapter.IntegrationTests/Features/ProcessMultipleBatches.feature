Feature: ProcessMultipleBatchesUsingA2Ia
	In order to read the courtesy amount from a cheque image
	As FXA
	I want to be use A2IA adapter to process the requested images

@MultipleBatchProcessing
Scenario: Auto read the courtesy amount from multiple batches
	Given the ICR engine adapter service is running in a well setup environment
	When a request is received for job identifier NSBD-765fe520-fdfc-4cd0-ae6c-63196ddf63ba with the following vouchers:
	| documentReferenceNumber | processingDate | transactionCode |
	| 000111222               | 2015/03/17     | 9               |
	| 000111223               | 2015/03/17     | 95              |
	| 000111224               | 2015/03/17     | 95              |
	| 000111225               | 2015/03/17     | 95              |
	| 000111226               | 2015/03/17     | ?               |
	And a request is received for job identifier NSBD-765fe520-fdfc-4cd0-ae6c-63196ddf63bb with the following vouchers:
	| documentReferenceNumber | processingDate | transactionCode |
	| 000111222               | 2015/03/17     | 9               |
	| 000111223               | 2015/03/17     | 95              |
	| 000111224               | 2015/03/17     | 95              |
	| 000111225               | 2015/03/17     | 95              |
	| 000111226               | 2015/03/17     | NO              |
	Then a CAR result for job identifier NSBD-765fe520-fdfc-4cd0-ae6c-63196ddf63ba with the following values is returned:
	| documentReferenceNumber | imageRotation | capturedAmount | amountConfidenceLevel |
	| 000111222               | 0             | 1000           | 996                   |
	| 000111223               | 0             | 25000          | 956                   |
	| 000111224               | 0             | 3500           | 541                   |
	| 000111225               | 0             | 786773         | 996                   |
	| 000111226               | 0             | 30359          | 992                   |
	And a CAR result for job identifier NSBD-765fe520-fdfc-4cd0-ae6c-63196ddf63bb with the following values is returned:
	| documentReferenceNumber | imageRotation | capturedAmount | amountConfidenceLevel |
	| 000111222               | 0             | 1000           | 996                   |
	| 000111223               | 0             | 25000          | 956                   |
	| 000111224               | 0             | 3500           | 541                   |
	| 000111225               | 0             | 786773         | 996                   |
	| 000111226               | 0             | 30359          | 992                   |