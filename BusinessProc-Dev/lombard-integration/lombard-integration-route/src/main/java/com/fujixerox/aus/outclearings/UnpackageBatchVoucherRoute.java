package com.fujixerox.aus.outclearings;

import com.fujixerox.aus.lombard.outclearings.scannedvoucher.ScannedBatch;
import com.fujixerox.aus.lombard.outclearings.unpackagebatchvoucher.UnpackageBatchVoucherRequest;
import com.fujixerox.aus.lombard.outclearings.unpackagebatchvoucher.UnpackageBatchVoucherResponse;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.dataformat.JsonLibrary;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

/**
 * Created by warwick on 23/02/2015.
 */
public class UnpackageBatchVoucherRoute extends RouteBuilder
{
    private String hostname;
    private String port;
    private String options;
    private String lockerPath;
    private String filter;
    private Logger log = LoggerFactory.getLogger(getClass().getName());

    @Override
    public void configure() throws Exception
    {
        String toOptions = options.length() > 0 ? "?" + options : "";
        String fromOptions = options.length() > 0 ? "&" + options : "";

        fromF("rabbitmq://%s:%s/lombard.service.outclearings.unpackagevoucher.request?queue=lombard.service.outclearings.unpackagevoucher.request.queue%s", hostname, port, fromOptions).
                routeId("lombard-service-outclearings-unpackagevoucher-service").
                log("Service Begin: Unpackage Batch Voucher").
                setProperty("correlation", simple("${header[rabbitmq.CORRELATIONID]}")).
                unmarshal().json(JsonLibrary.Jackson, UnpackageBatchVoucherRequest.class).
                process(new Processor() {
                    @Override
                    public void process(Exchange exchange) throws Exception {
                        UnpackageBatchVoucherRequest request = exchange.getIn().getBody(UnpackageBatchVoucherRequest.class);
                        File file = determineFile(request);
                        exchange.getIn().setBody(readFile(file));
                    }
                }).
                unmarshal().jaxb(ScannedBatch.class.getPackage().getName()).
                process(new Processor() {
                    @Override
                    public void process(Exchange exchange) throws Exception {
                        UnpackageBatchVoucherResponse response = new UnpackageBatchVoucherResponse();
                        response.setBatch(exchange.getIn().getBody(ScannedBatch.class));
                        exchange.getIn().setBody(response);
                    }
                }).
                marshal().json(JsonLibrary.Jackson).
                setHeader("rabbitmq.CORRELATIONID", simple("${header[rabbitmq.CORRELATIONID]}")).
                removeHeader("rabbitmq.EXCHANGE_NAME").
                toF("rabbitmq://%s:%s/lombard.service.outclearings.unpackagevoucher.response%s", hostname, port, toOptions).
                log("Service End: Unpackage Batch Voucher").end();
    }

    public void setHostname(String hostname) {
        this.hostname = hostname;
    }

    public void setPort(String port) {
        this.port = port;
    }

    public void setOptions(String options) {
        this.options = options;
    }

    protected String readFile(File file)
    {
        try {
            FileInputStream fileInputStream = new FileInputStream(file);
            return IOUtils.toString(fileInputStream);
        } catch (FileNotFoundException e) {
            throw new RuntimeException("Metadata file does not found:" + file.getAbsolutePath(), e);
        } catch (IOException e) {
            throw new RuntimeException("Metadata file not readable:" + file.getAbsolutePath(), e);
        }
    }

    protected File determineFile(UnpackageBatchVoucherRequest request)
    {
        String jobIdentifier = request.getJobIdentifier();
        File dir = new File(lockerPath, jobIdentifier);
        if (!dir.exists())
        {
            throw new RuntimeException("Job directory does not exist:" + dir.getAbsolutePath());
        }
        String fileName = request.getReceivedFile().getFileIdentifier();
        return new File(dir, fileName.replace(".zip", ".XML"));
    }

    public void setLockerPath(String lockerPath) {
        this.lockerPath = lockerPath;
    }

    public void setFilter(String filter) {
        this.filter = filter;
    }
}
