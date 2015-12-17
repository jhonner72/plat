The integration tests are bundled into a war file to make the distribution simple.
The war will contain all the java classes and dependant libraries.
Transfer the war to the server where you would like to execute the tests and then expand the war. (It is just a zip file after all).

The batch file run.bat contains an example on how to start an integration test.
java -cp .;lombard-integration-web-test-0.0.0-SNAPSHOT\WEB-INF\lib\* org.junit.runner.JUnitCore com.fujixerox.aus.outclearings.ExternalTest

The file config.ini may contain properties that control how the tests are executed. A default copy of this file is located in the war.
On commencement of the test the system will look for a config.ini in the current working directory. That is the same directory as the batch file.

This is the default settings

delivery.mode=ftp
# delivery.mode=drop
# delivery.drop.path=\\temp\\files

Tests are defined as features and may be located either internally or in the features folder from the current working directory.
Depending on the test template data files may be placed into the data folder from the current working directory. Otherwise
the data files are retrieved internally.

The following class names control the location of the features
Current Working Directory: com.fujixerox.aus.outclearings.ExternalTest
Internal Integration: com.fujixerox.aus.outclearings.IntegrationTest

