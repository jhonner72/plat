Feature: Create Job From File
	When an HTTP request for a new DIPS package is received 
	Then a new voucher outclearings job message will be sent to the Job Service Exchange

@Job
Scenario: A valid NSBD file request is received via HTTP
	Given a new JScape processing ID generated is NSBD-2cbabea0-43f9-4554-b118-c80c487d97c3
	When a new voucher outclearings package with file name OUTCLEARINGSPKG_28052015_NSBD68200022.zip is received from JScape with parameter empty
	Then a voucher outclearings job message with id NSBD-2cbabea0-43f9-4554-b118-c80c487d97c3 is sent to the Job Service Exchange with parameter empty
	
@Job
Scenario: A valid NIEI file request is received via HTTP
	Given a new JScape processing ID generated is NIEI-2cbabea0-43f9-4554-b118-c80c487d97c3
	When a new inwardimageexchange inclearings package with file name IMGEXCH.20120704.20120705.223010.9999999999999999.ANZ.RBA.000005.zip is received from JScape with parameter empty
	Then a inwardimageexchange inclearings job message with id NIEI-2cbabea0-43f9-4554-b118-c80c487d97c3 is sent to the Job Service Exchange with parameter empty
	
@Job
Scenario: A valid NSBP file request is received via HTTP
	Given a new JScape processing ID generated is NSBP-2cbabea0-43f9-4554-b118-c80c487d97c3
	When a new voucher outclearings package with file name NONE is received from JScape with parameter [{"name":"workType","value":"NABCHQ_APOST"}]
	Then a voucher outclearings job message with id NSBP-2cbabea0-43f9-4554-b118-c80c487d97c3 is sent to the Job Service Exchange with parameter [{"name":"workType","value":"NABCHQ_APOST"}]