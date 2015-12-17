package com.fujixerox.aus.storebatch;

import cucumber.api.CucumberOptions;
import cucumber.api.junit.Cucumber;
import org.junit.runner.RunWith;

@RunWith(Cucumber.class)
@CucumberOptions(
        dryRun = false,
        glue = {"com.fujixerox.aus.storebatch", "spring"}
)
public class StoreBatchVoucherTest {
}
