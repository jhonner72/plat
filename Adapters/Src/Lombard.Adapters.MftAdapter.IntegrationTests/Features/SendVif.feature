Feature: Send a VIF  File
	In order to send VIF files in a controlled fashion
	As a BPM
	I want to publish a Send VIF request and have the associated response

@sendVif
Scenario: Move a Vif file
	Given a file exists in the Vif pending directory
	When a request is published to send the file
	Then the file should be moved to the correct location
