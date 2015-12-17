﻿Feature: CreateBatchPayloads
	In order to read the courtesy amount from a cheque image
	As FXA
	I want to be use A2IA adapter to process the requested images

@AutoReadCar
Scenario: Create batch payloads
	Given the ICR engine adapter service is running in a well setup environment
	When a request is received for job identifier NSBD-765fe520-fdfc-4cd0-ae6c-63196ddf63aa with the following vouchers:
	| documentReferenceNumber | processingDate | transactionCode |
	| 000111222               | 2015/03/17     | 9               |
	| 000111223               | 2015/03/17     | 95              |
	| 000111224               | 2015/03/17     | 95              |
	| 000111225               | 2015/03/17     | 95              |
	| 000111226               | 2015/03/17     | 9               |
	And a request is received for job identifier NSBD-765fe520-fdfc-4cd0-ae6c-63196ddf63ab with the following vouchers:
	| documentReferenceNumber | processingDate | transactionCode |
	| 185000030               | 2015/07/13     | 95              |
	| 185000031               | 2015/07/13     | 95              |
	| 185000032               | 2015/07/13     | 95              |
	| 185000033               | 2015/07/13     | 95              |
	| 185000034               | 2015/07/13     | 95              |
	And a request is received for job identifier NSBD-765fe520-fdfc-4cd0-ae6c-63196ddf63ac with the following vouchers:
	| documentReferenceNumber | processingDate | transactionCode |
	| 185000035               | 2015/07/13     | 95              |
	| 185000036               | 2015/07/13     | 95              |
	| 185000037               | 2015/07/13     | 95              |
	| 185000038               | 2015/07/13     | 95              |
	| 185000039               | 2015/07/13     | 95              |
	And a request is received for job identifier NSBD-765fe520-fdfc-4cd0-ae6c-63196ddf63ad with the following vouchers:
	| documentReferenceNumber | processingDate | transactionCode |
	| 185000040               | 2015/07/13     | 95              |
	| 185000041               | 2015/07/13     | 95              |
	| 185000042               | 2015/07/13     | 95              |
	| 185000043               | 2015/07/13     | 95              |
	| 185000044               | 2015/07/13     | 95              |
	And a request is received for job identifier NSBD-765fe520-fdfc-4cd0-ae6c-63196ddf63ae with the following vouchers:
	| documentReferenceNumber | processingDate | transactionCode |
	| 185000045               | 2015/07/13     | 95              |
	| 185000046               | 2015/07/13     | 95              |
	| 185000047               | 2015/07/13     | 95              |
	| 185000048               | 2015/07/13     | 95              |
	| 185000049               | 2015/07/13     | 95              |
	And a request is received for job identifier NSBD-765fe520-fdfc-4cd0-ae6c-63196ddf63af with the following vouchers:
	| documentReferenceNumber | processingDate | transactionCode |
	| 185000050               | 2015/07/13     | 95              |
	| 185000051               | 2015/07/13     | 95              |
	| 185000052               | 2015/07/13     | 95              |
	| 185000053               | 2015/07/13     | 95              |
	| 185000054               | 2015/07/13     | 95              |
	And a request is received for job identifier NSBD-765fe520-fdfc-4cd0-ae6c-63196ddf63ba with the following vouchers:
	| documentReferenceNumber | processingDate | transactionCode |
	| 185000055               | 2015/07/13     | 95              |
	| 185000056               | 2015/07/13     | 95              |
	| 185000057               | 2015/07/13     | 95              |
	| 185000058               | 2015/07/13     | 95              |
	| 185000059               | 2015/07/13     | 95              |
	And a request is received for job identifier NSBD-765fe520-fdfc-4cd0-ae6c-63196ddf63bb with the following vouchers:
	| documentReferenceNumber | processingDate | transactionCode |
	| 142000757               | 2015/06/16     | 9               |
	| 142000758               | 2015/06/16     | 9               |
	| 142000759               | 2015/06/16     | 9               |
	| 142000760               | 2015/06/16     | 9               |
	| 142000761               | 2015/06/16     | 9               |
	And a request is received for job identifier NSBD-765fe520-fdfc-4cd0-ae6c-63196ddf63bc with the following vouchers:
	| documentReferenceNumber | processingDate | transactionCode |
	| 142000762               | 2015/06/16     | 9               |
	| 142000763               | 2015/06/16     | 9               |
	| 142000764               | 2015/06/16     | 9               |
	| 142000765               | 2015/06/16     | 9               |
	| 142000766               | 2015/06/16     | 9               |
	And a request is received for job identifier NSBD-765fe520-fdfc-4cd0-ae6c-63196ddf63bd with the following vouchers:
	| documentReferenceNumber | processingDate | transactionCode |
	| 142000767               | 2015/06/16     | 9               |
	| 142000768               | 2015/06/16     | 9               |
	| 142000769               | 2015/06/16     | 9               |
	| 142000770               | 2015/06/16     | 9               |
	| 142000771               | 2015/06/16     | 9               |
	And a request is received for job identifier NSBD-765fe520-fdfc-4cd0-ae6c-63196ddf63be with the following vouchers:
	| documentReferenceNumber | processingDate | transactionCode |
	| 142000772               | 2015/06/16     | 9               |
	| 142000773               | 2015/06/16     | 9               |
	| 142000774               | 2015/06/16     | NO              |
	| 142000775               | 2015/06/16     | NO              |
	| 142000776               | 2015/06/16     | ?               |
	| 142000777               | 2015/06/16     | ?               |
	Then a CAR result for job identifier NSBD-765fe520-fdfc-4cd0-ae6c-63196ddf63aa with the following values is returned:
	| documentReferenceNumber | imageRotation | capturedAmount | amountConfidenceLevel |
	| 000111222               | 0             | 1000           | 996                   |
	| 000111223               | 0             | 25000          | 956                   |
	| 000111224               | 0             | 3500           | 541                   |
	| 000111225               | 0             | 786773         | 996                   |
	| 000111226               | 0             | 30359          | 992                   |
	And a CAR result for job identifier NSBD-765fe520-fdfc-4cd0-ae6c-63196ddf63ab with the following values is returned:
	| documentReferenceNumber | imageRotation | capturedAmount | amountConfidenceLevel |
	| 185000030               | 0             | 1318           | 25                    |
	| 185000031               | 0             | 10000          | 552                   |
	| 185000032               | 0             | 125000         | 786                   |
	| 185000033               | 0             | 6000           | 16                    |
	| 185000034               | 0             | 91700          | 41                    |
	And a CAR result for job identifier NSBD-765fe520-fdfc-4cd0-ae6c-63196ddf63ac with the following values is returned:
	| documentReferenceNumber | imageRotation | capturedAmount | amountConfidenceLevel |
	| 185000035               | 0             | 1400           | 929                   |
	| 185000036               | 0             | 376315         | 963                   |
	| 185000037               | 0             | 1115500        | 995                   |
	| 185000038               | 0             | 74595          | 785                   |
	| 185000039               | 0             | 37700          | 477                   |
	And a CAR result for job identifier NSBD-765fe520-fdfc-4cd0-ae6c-63196ddf63ad with the following values is returned:
	| documentReferenceNumber | imageRotation | capturedAmount | amountConfidenceLevel |
	| 185000040               | 0             | 7815           | 924                   |
	| 185000041               | 0             | 10000          | 300                   |
	| 185000042               | 0             | 11190          | 700                   |
	| 185000043               | 0             | 759860         | 136                   |
	| 185000044               | 0             | 57450          | 988                   |
	And a CAR result for job identifier NSBD-765fe520-fdfc-4cd0-ae6c-63196ddf63ae with the following values is returned:
	| documentReferenceNumber | imageRotation | capturedAmount | amountConfidenceLevel |
	| 185000045               | 0             | 10530          | 673                   |
	| 185000046               | 0             | 495            | 981                   |
	| 185000047               | 0             | 10219          | 978                   |
	| 185000048               | 0             | 491590         | 973                   |
	| 185000049               | 0             | 1205000        | 968                   |
	And a CAR result for job identifier NSBD-765fe520-fdfc-4cd0-ae6c-63196ddf63af with the following values is returned:
	| documentReferenceNumber | imageRotation | capturedAmount | amountConfidenceLevel |
	| 185000050               | 0             | 194565         | 154                   |
	| 185000051               | 0             | 614000         | 997                   |
	| 185000052               | 0             | 1398780        | 488                   |
	| 185000053               | 0             | 12000000       | 442                   |
	| 185000054               | 0             | 742500         | 969                   |
	And a CAR result for job identifier NSBD-765fe520-fdfc-4cd0-ae6c-63196ddf63ba with the following values is returned:
	| documentReferenceNumber | imageRotation | capturedAmount | amountConfidenceLevel |
	| 185000055               | 0             | 12810          | 981                   |
	| 185000056               | 0             | 17480          | 981                   |
	| 185000057               | 0             | 3094521        | 971                   |
	| 185000058               | 0             | 4777           | 935                   |
	| 185000059               | 0             | 250000         | 976                   |
	And a CAR result for job identifier NSBD-765fe520-fdfc-4cd0-ae6c-63196ddf63bb with the following values is returned:
	| documentReferenceNumber | imageRotation | capturedAmount | amountConfidenceLevel |
	| 142000757               | 0             | 2065100        | 0                     |
	| 142000758               | 0             | 20000          | 950                   |
	| 142000759               | 0             | 17000          | 996                   |
	| 142000760               | 0             | 5100           | 974                   |
	| 142000761               | 0             | 15200          | 830                   |
	And a CAR result for job identifier NSBD-765fe520-fdfc-4cd0-ae6c-63196ddf63bc with the following values is returned:
	| documentReferenceNumber | imageRotation | capturedAmount | amountConfidenceLevel |
	| 142000762               | 0             | 14122          | 793                   |
	| 142000763               | 0             | 7300           | 993                   |
	| 142000764               | 0             | 261960         | 879                   |
	| 142000765               | 0             | 44000          | 991                   |
	| 142000766               | 0             | 350000         | 992                   |
	And a CAR result for job identifier NSBD-765fe520-fdfc-4cd0-ae6c-63196ddf63bd with the following values is returned:
	| documentReferenceNumber | imageRotation | capturedAmount | amountConfidenceLevel |
	| 142000767               | 0             | 76500          | 981                   |
	| 142000768               | 0             | 15000          | 987                   |
	| 142000769               | 0             | 17562          | 931                   |
	| 142000770               | 0             | 3709           | 619                   |
	| 142000771               | 0             | 48991          | 46                    |
	And a CAR result for job identifier NSBD-765fe520-fdfc-4cd0-ae6c-63196ddf63be with the following values is returned:
	| documentReferenceNumber | imageRotation | capturedAmount | amountConfidenceLevel |
	| 142000772               | 0             | 165000         | 47                    |
	| 142000773               | 0             | 22400          | 307                   |
	| 142000774               | 0             | 30180          | 997                   |
	| 142000775               | 0             | 18300          | 997                   |
	| 142000776               | 0             | 8900           | 998                   |
	| 142000777               | 0             | 22900          | 989                   |