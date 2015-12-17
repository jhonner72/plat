package com.fujixerox.aus.outclearings;

import com.fujixerox.aus.job.JobDetails;
import com.fujixerox.aus.job.JobTestHandler;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Stepdefs {
    JobTestHandler jobHandler = new JobTestHandler();
    JobDetails jobDetails;
    List<JobDetails> multipleJobDetails =  new ArrayList<>();

    @Given("^A zip file with (\\d+) vouchers$")
    public void setupZip(int voucherCount) throws IOException {
        this.jobDetails = this.jobHandler.create(voucherCount);
    }

    @When("^The file is delivered$")
    public void sendFile() throws IOException {
        this.jobHandler.transferFile(this.jobDetails.getZipFile());
    }

    @Then("^All (\\d+) vouchers must be visible in the repository$")
    public void assertRepository(int voucherCount) {
    }

    @Given("^(\\d+) zip files with (\\d+) vouchers$")
    public void setupMultipleZip(int zipCount, int voucherCount) throws IOException {
        for (int i = 0; i < zipCount; i++) {
            this.multipleJobDetails.add(this.jobHandler.create(voucherCount));
        }
    }

    @When("^The files are delivered every (\\d+) ms$")
    public void sendFilesAtAnInterval(int milliseconds) throws IOException {
        for (JobDetails jobDetails : multipleJobDetails) {
            System.out.print(".");
            this.jobHandler.transferFile(jobDetails.getZipFile());
            synchronized (this) {
                try {
                    wait(milliseconds);
                } catch (InterruptedException e) {
                    return;
                }
            }
        }
    }
}