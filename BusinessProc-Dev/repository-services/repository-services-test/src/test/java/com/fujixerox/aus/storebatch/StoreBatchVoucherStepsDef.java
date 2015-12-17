package com.fujixerox.aus.storebatch;

import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.context.ContextConfiguration;

import com.fujixerox.aus.job.StoreBatchTestJobDetails;
import com.fujixerox.aus.job.StoreBatchTestJobHandler;

import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;

@ContextConfiguration("classpath:cucumber.xml")
public class StoreBatchVoucherStepsDef {

	@Autowired
	@Qualifier("storeBatchTestJobHandler")
	StoreBatchTestJobHandler jobHandler;

	StoreBatchTestJobDetails jobDetails;
	List<StoreBatchTestJobDetails> multipleJobDetails = new ArrayList<>();

	@Given("^A batch of (\\d+) vouchers$")
	public void createRequests(int voucherCount) throws IOException,
			ParseException {
		this.jobDetails = this.jobHandler.createRequests(voucherCount);
	}

	@When("^The store batch voucher request is sent$")
	public void publishRequest() throws IOException {
		this.jobHandler.publishMessages(jobDetails);
	}

	@Then("^All (\\d+) vouchers must be visible in the repository$")
	public void assertRepository(int voucherCount) {
	}

	@Given("^(\\d+) batches of (\\d+) vouchers$")
	public void setupMultipleBatches(int batchCount, int voucherCount)
			throws IOException, ParseException {
		for (int i = 0; i < batchCount; i++) {
			this.multipleJobDetails.add(this.jobHandler
					.createRequests(voucherCount));
		}
	}

	@When("^Then (\\d+) store batch voucher requests are sent after (\\d+) ms$")
	public void publishRequests(int batchCount, int milliseconds)
			throws IOException {
		for (StoreBatchTestJobDetails jobDetails : multipleJobDetails) {
			this.jobHandler.publishMessages(jobDetails);
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
