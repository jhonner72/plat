Feature: Create CopyImage From Request
	When an HTTP request for a new DIPS package transfer is received 
	Then a new copy image message will be sent to the Job Service Exchange

@CopyImage
Scenario: A valid copy image NFVX message request is received via HTTP
	Given a new processing ID from file name is NFVX-2cbabea0-43f9-4554-b118-c80c487d97c3
	When a new copy image message request body is NFVX-2cbabea0-43f9-4554-b118-c80c487d97c3 with RoutingKey NFVX
	Then a copy image message with id NFVX-2cbabea0-43f9-4554-b118-c80c487d97c3 is sent to the CopyImage Service Exchange with RoutingKey NFVX

@CopyImage
Scenario: A valid copy image NECL message request is received via HTTP
	Given a new processing ID from file name is NSBP-2cbabea0-43f9-4554-b118-c80c487d97c3
	When a new copy image message request body is NSBP-2cbabea0-43f9-4554-b118-c80c487d97c3 with RoutingKey NECL
	Then a copy image message with id NSBP-2cbabea0-43f9-4554-b118-c80c487d97c3 is sent to the CopyImage Service Exchange with RoutingKey NECL