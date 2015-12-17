Feature: Generate Report
	In order to send data to NAB
	As the Vif service
	I want to generate a report and 

@reporting
@bitLocker
Scenario: Generate Report

	# Assumes the given report name (TestFIs, in this case) is a valid report name on the 
	# report server where this executes
	When a request is added to the queue with jobIdentifier 111-222-333 and the following info

	| ReportName      | OutputFilename      | OutputFormatType | Parameter1Key | Parameter1Value | Parameter2Key | Parameter2Value |
	| TestFIs         | TestFIs.pdf         | PDF              |               |                 |               |                 |
	| FilesByEndpoint | FilesByEndpoint.xls | EXCEL            | endpoint      | ANZ             |               |                 |
	#| FilesByEndpoint | FilesByEndpoint.xls | TXT            | endpoint      | ANZ             |               |                 |
	#| Daily_VIF_Transmission_Report_7095 | Daily_VIF_Transmission_Report_7095.txt | CSV            | transmissiondate | 2015-04-28      | capturestate  | SA              |
			
	Then a response will be added to the response queue
		
	
