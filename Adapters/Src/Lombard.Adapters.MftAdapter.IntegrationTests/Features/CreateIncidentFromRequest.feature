Feature: Create Incident From Request
	When an HTTP request for a new incident is received 
	Then a new incident message will be sent to the Incident Service Exchange

@CopyImage
Scenario: A valid incident message request is received via HTTP
	Given a new incident request from HTTP
	When a new incident message request with RoutingKey empty
		| Field        | Value                                                                                                                        |
		| JobSubject   | incident                                                                                                                     |
		| JobPredicate | valuefromauspost                                                                                                             |
		| Details      | Incomplete set or no AusPost ECL files have been received by 13/10/2015. No downstream AusPost ECL processing occurred today |
	Then an incident message is sent to the Incident Service Exchange with RoutingKey empty
		| Field     | Value                                                                                                                        |
		| Subject   | incident                                                                                                                     |
		| Predicate | valuefromauspost                                                                                                             |
		| details   | Incomplete set or no AusPost ECL files have been received by 13/10/2015. No downstream AusPost ECL processing occurred today |
