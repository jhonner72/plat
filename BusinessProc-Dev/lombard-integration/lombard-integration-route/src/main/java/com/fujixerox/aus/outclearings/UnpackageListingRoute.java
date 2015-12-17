package com.fujixerox.aus.outclearings;

import com.fujixerox.aus.lombard.outclearings.scannedlisting.ScannedListing;
import com.fujixerox.aus.lombard.outclearings.scannedlisting.ScannedListingBatchHeader;
import com.fujixerox.aus.lombard.outclearings.unpackagelisting.UnpackageListingRequest;
import com.fujixerox.aus.lombard.outclearings.unpackagelisting.UnpackageListingResponse;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.dataformat.JsonLibrary;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;

/**
 * Created by vidyavenugopal on 6/07/15.
 */
public class UnpackageListingRoute extends RouteBuilder {

    private String hostname;
    private String port;
    private String options;
    private String lockerPath;
    private String filter;
    private Logger log = LoggerFactory.getLogger(getClass().getName());


    @Override
    public void configure() throws Exception {

        String toOptions = options.length() > 0 ? "?" + options : "";
        String fromOptions = options.length() > 0 ? "&" + options : "";

        fromF("rabbitmq://%s:%s/lombard.service.outclearings.unpackagelisting.request?queue=lombard.service.outclearings.unpackagelisting.request.queue%s", hostname, port, fromOptions).
                routeId("lombard-service-outclearings-unpackagelisting-service").
                log("Service Begin: Unpackage Listings").
                setProperty("correlation", simple("${header[rabbitmq.CORRELATIONID]}")).
                unmarshal().json(JsonLibrary.Jackson, UnpackageListingRequest.class).
                process(new Processor() {
                    @Override
                    public void process(Exchange exchange) throws Exception {
                        UnpackageListingRequest request = exchange.getIn().getBody(UnpackageListingRequest.class);
                        File file = determineFile(request);
                        exchange.getIn().setBody(readFile(file));
                    }
                }).
                unmarshal().jaxb(ScannedListingBatchHeader.class.getPackage().getName()).
                process(new Processor() {
                    @Override
                    public void process(Exchange exchange) throws Exception {
                        UnpackageListingResponse response = new UnpackageListingResponse();
                        response.setScannedListing(exchange.getIn().getBody(ScannedListingBatchHeader.class));
                        exchange.getIn().setBody(response);
                    }
                }).
                marshal().json(JsonLibrary.Jackson).
                setHeader("rabbitmq.CORRELATIONID", simple("${header[rabbitmq.CORRELATIONID]}")).
                removeHeader("rabbitmq.EXCHANGE_NAME").
                toF("rabbitmq://%s:%s/lombard.service.outclearings.unpackagelisting.response%s", hostname, port, toOptions).
                log("Service End: Unpackage Listings").end();

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

    protected File determineFile(UnpackageListingRequest request)
    {
        String jobIdentifier = request.getJobIdentifier();
        File dir = new File(lockerPath, jobIdentifier);
        if (!dir.exists())
        {
            throw new RuntimeException("Job directory does not exist:" + dir.getAbsolutePath());
        }

        File[] files = dir.listFiles(new FilenameFilter() {
            @Override
            public boolean accept(File dir, String name) {
                return name.endsWith(".xml");
            }
        });
        String fileName = files[0].getName();
        return new File(dir, fileName);
    }

    public void setLockerPath(String lockerPath) {
        this.lockerPath = lockerPath;
    }

    public void setFilter(String filter) {
        this.filter = filter;
    }

}
