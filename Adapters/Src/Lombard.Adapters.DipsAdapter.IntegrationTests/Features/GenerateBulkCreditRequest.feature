Feature: Generate Bulk Credit Request
	Given a valid request
	When a generate bulk credit request is issued to DIPS
	The correct bulk credit vouchers should be viewable in DIPS

@GenerateBulkCreditRequest
Scenario: Generate Bulk Credit Requests
	Given there are no GenerateBatchBulkCreditRequest rows for batch number 96384477
	And a GenerateBatchBulkCreditRequest is added to the queue for the following BulkCreditRequest vouchers:
	| captureBsb | documentReferenceNumber | processingDate |
	| 085384     | 895214001            | 2015/10/20     | 
	| 085385     | 895214003            | 2015/10/20     | 
	When the message is published to the queue and GenerateBatchBulkCreditRequest process the message with this information:
	| Field                 | Value                                                                               |
	| CorrelationId         | NECL-47887b18-e731-4839-b77a-d48c00003328/NGBC-04d91de6-5051-4a17-ab70-b296bb1c4cc4 |
	| RoutingKey            | NECL                                                                                |
	| PublishTimeOutSeconds | 3                                                                                   |