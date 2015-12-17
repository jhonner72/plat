package com.fujixerox.aus.outclearings;

import cucumber.api.CucumberOptions;
import cucumber.api.junit.Cucumber;
import org.junit.runner.RunWith;

@RunWith(Cucumber.class)
@CucumberOptions(
        plugin = {"pretty"},
        features = "features"
)
public class ExternalTest {
    public ExternalTest() {
    }
}
