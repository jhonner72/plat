Feature: GetVouchersInformationRequestPollingJob
	Given a valid record in request database
	And valid record in database
	When wait and a GetVouchersInformationRequestPollingJob executed
	Then a GetVouchersInformationRequest is added to the exchange

@GetVouchersInformationRequestPollingJob
Scenario: Get Vouchers Information request
	Given there are GetVouchersInformationRequest records in the request table
	| guid_name | payload                                                                                                                                         | request_time          | RequestCompleted |
	| 12345     | [{"name":"documentReferenceNumber","value":"583000026"},{"name":"processingDate","value":"14-09-2015"},{"name":"surplusItemFlag","value":true}] | 9/15/2015 12:00:00 AM | false            |
	| 67890     | [{"name":"documentReferenceNumber","value":"583000027"},{"name":"processingDate","value":"14-09-2015"},{"name":"surplusItemFlag","value":true}] | 9/15/2015 12:00:00 AM | false            |
	When wait for 10 seconds until GetVouchersInformationRequestPollingJob executed
	Then a GetVouchersInformationRequest 12345 is added to the exchange and contains these information:
	| name                    | value      |
	| documentReferenceNumber | 583000026  |
	| processingDate          | 14-09-2015 |
	| surplusItemFlag         | True       |
	And a GetVouchersInformationRequest 67890 is added to the exchange and contains these information:
	| name                    | value      |
	| documentReferenceNumber | 583000027  |
	| processingDate          | 14-09-2015 |
	| surplusItemFlag         | True       |
