
Feature: Create Image Exchange File
	In order to send data to other banks and endpoints
	As the outbound image exchange system
	I want to create an Image Exchange file

@outboundImageExchange
Scenario: Create Outbound Image Exchange File
	Given voucher data has been extracted for jobIdentifier 333-444-555
		And the voucher metadata contains the following data

		| transactionCode | documentReferenceNumber | bsbNumber | accountNumber | documentType | auxDom | processingDate                | amount  | extraAuxDom |
		| 12              | 88888888                | 049898    | 54987754      | DR           | Test8  | 2015-05-08T00:00:00.000+10:00 | 4634567 | test        |

		And the voucherBatch metadata contains the following data

		| collectingBsb | processingState | captureBsb | unitID | scannedBatchNumber |
		| 091123        | VIC             | 123456     |        | 33333333           |

		And the voucherProcess metadata contains the following data

		| voucherDelayedIndicator | manualRepair | targetEndPoint |targetEndPoint |
		|                         | 0            | FSV            |				  |

	When a CreateImageExchangeFileRequest is added to the queue for the given job and targetEndPoint FSV and sequenceNumber 123 for today

	#Then a CreateImageExchangeFileResponse will be added to the CreateImageExchangeFileResponse queue
		#And a matching OutboundVoucherFile will exist in the database matching the response and the original request
		
	
