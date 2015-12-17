
Feature: Upload Object to Documentum
	In order to persist data 
	As a developer
	I want to send a message to upload an object to Documentum

@documentum
Scenario: Send message to upload to Documentum
	When a HelloWorld message is sent to the documentum input queue
	Then the HelloWorld message is processed and a response is sent to the documentum output queue
