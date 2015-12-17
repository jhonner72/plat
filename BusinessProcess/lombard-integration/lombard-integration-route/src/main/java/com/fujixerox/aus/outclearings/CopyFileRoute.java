package com.fujixerox.aus.outclearings;

import static org.camunda.bpm.camel.component.CamundaBpmConstants.CAMUNDA_BPM_BUSINESS_KEY;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.nio.file.CopyOption;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.builder.RouteBuilder;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fujixerox.aus.integration.service.ErrorHandlingProcessor;
import com.fujixerox.aus.lombard.common.copyfile.CopyFileRequest;
import com.fujixerox.aus.lombard.common.copyfile.CopyFileResponse;

/**
 * Created with IntelliJ IDEA.
 * User: Eloisa.Redubla
 * Date: 5/05/15
 * Time: 11:59 AM
 * To change this template use File | Settings | File Templates.
 */
public class CopyFileRoute extends RouteBuilder {
	final Logger log = LoggerFactory.getLogger(this.getClass());
	
    private String serviceName;
    private String subject;
    private String predicate;
    private String consumeType;
    private boolean isSkipStoreJobRequest = false;
    private boolean isSkipStoreJobResponse = false;

    @Override
    public void configure() throws Exception
    {
        fromF("direct:%s-request", serviceName.replaceAll("\\.", "-")).
                routeId(String.format("%s.request", serviceName)).
                log("${property.CamundaBpmBusinessKey} - Service Start: File Copy - "+serviceName).
                onException(Exception.class).
	                log("Exception Start").
	                useOriginalMessage().
	                process(new ErrorHandlingProcessor("integration")).
	                handled(true).
	                log("Exception End").
	            end().
                beanRef("jobStore", "findJob(${property.CamundaBpmBusinessKey})").
                setHeader("rabbitmq.CORRELATIONID").spel("#{body.jobIdentifier}").
                setHeader("rabbitmq.TYPE", constant(consumeType)).
                setProperty("job").spel("#{body}").
                beanRef(String.format("transform.%s.request", serviceName), "transform(${property.job})").
                choice().
                	when(constant(isSkipStoreJobRequest).isEqualTo(false)).	// default
                		beanRef("jobStore", String.format("addRequest(${property.job}, ${body}, %s, %s)", subject, predicate)).
                	otherwise().
                		beanRef("jobStore", String.format("addRequestOnce(${property.job}, ${body}, %s, %s)", subject, predicate)).
                end().
                process(new Processor() {
                    @Override
                    public void process(Exchange exchange) throws Exception {
                        CopyFileRequest request = exchange.getIn().getBody(CopyFileRequest.class);
                        String sourceFilename = request.getSourceFilename();
                        String targetFilename = request.getTargetFilename();
                        boolean fileCopied = copyFile(sourceFilename, targetFilename);
                        exchange.getIn().setBody(fileCopied);
                    }
                }).
                setProperty("fileCopied").simple("${body}").
                process(new Processor() {
                    @Override
                    public void process(Exchange exchange) throws Exception {
                        boolean fileCopied = (Boolean) exchange.getIn().getBody();

                        CopyFileResponse response = new CopyFileResponse();
                        if (fileCopied) {
                            String dateString = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:00.000X").format(new Date());
                            Date now = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:00.000X").parse(dateString);
                            response.setCopyDate(now);
                        }
                        exchange.getIn().setBody(response);
                    }
                }).
                setProperty("response").simple("${body}").
                choice().
	            	when(constant(isSkipStoreJobResponse).isEqualTo(false)).	// default addResponse everytime
	            		beanRef("jobStore", String.format("addResponse(${property.job}, ${property.response}, %s, %s)", subject, predicate)).
	            		log("${property.CamundaBpmBusinessKey} - Executed Job Store Response - "+serviceName).
		            when(property("fileCopied").isEqualTo(true)).	// when fileCopied true only
	            		beanRef("jobStore", String.format("addResponse(${property.job}, ${property.response}, %s, %s)", subject, predicate)).
	            		log("${property.CamundaBpmBusinessKey} - Executed Job Store Response - "+serviceName).
	            	otherwise().
	            		log("${property.CamundaBpmBusinessKey} - Skipped Job Store Response - "+serviceName).
	            end().
                setProperty(CAMUNDA_BPM_BUSINESS_KEY, simple("${header[rabbitmq.CORRELATIONID]}")).
                process(new Processor() {
                    @Override
                    public void process(Exchange exchange) throws Exception {
                        CopyFileResponse response = exchange.getIn().getBody(CopyFileResponse.class);
                        if (response.getCopyDate() == null) {
                            exchange.getIn().setBody(false, Boolean.class);
                        } else {
                            exchange.getIn().setBody(true, Boolean.class);
                        }
                    }
                }).
                setProperty("response").simple("${body}").
                log("${property.CamundaBpmBusinessKey} - Service Done: File Copy - "+serviceName).end();
    }

    private boolean copyFile(String sourceFileName, String targetFileName) {
        boolean fileCopied = false;
        File sourceFile = new File(sourceFileName);
        File targetFile = new File(targetFileName);

        if (!sourceFile.exists())
        {
            return fileCopied;
        }

        try {
            if (!targetFile.exists())
            {
                targetFile.createNewFile();
            }
        } catch (IOException ioe) {
            throw new RuntimeException("IO Error to create target file:" + targetFile.getAbsolutePath(), ioe);
        }

        try {
            log.debug("Copying the file "+ sourceFile.getAbsolutePath() +" to {}", targetFile.getAbsolutePath());
            if (sourceFile.getAbsolutePath().contains("VIFACK")) {
                Files.move(sourceFile.toPath(), targetFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
            } else if (targetFile.isDirectory()) {
                if (targetFile.getAbsolutePath().contains("AusPostECL")) {
                    File[] files = sourceFile.listFiles(getFilteredDirectory());
                    for (File file : files) {
                        if (file.isDirectory()) {
                            continue;
                        } else {
                            File targetEclFile = new File(targetFile, file.getName());
                            Files.copy(file.toPath(), targetEclFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
                        }
                    }

                } else {
                    FileUtils.copyDirectory(sourceFile, targetFile);

                    if (targetFileName.indexOf(".part") != -1) {
                        String jobFolderName = targetFileName.replace(".part", "");
                        targetFile.renameTo(new File(jobFolderName));
                    }
                }
            } else {
                Files.copy(sourceFile.toPath(), targetFile.toPath(), StandardCopyOption.REPLACE_EXISTING);

                if (targetFileName.indexOf(".part") != -1) {
                    String tempFileName = targetFileName.replace(".part", "");
                    targetFile.renameTo(new File(tempFileName));
                }
            }
            fileCopied = true;
        } catch (IOException e) {
            throw new RuntimeException("Failed to copy file:" + sourceFile.getAbsolutePath(), e);
        }
        return fileCopied;
    }

    private FilenameFilter getFilteredDirectory() {
        FilenameFilter textFilter = new FilenameFilter() {
            public boolean accept(File dir, String name) {
                String uppercaseName = name.toUpperCase();
                if (uppercaseName.contains("ECL.FILE")) {
                    return true;
                } else {
                    return false;
                }
            }
        };
        return  textFilter;
    }

    public static void copyFolder(Path source,Path target) throws IOException{
        CopyOption[] options = new CopyOption[]{StandardCopyOption.REPLACE_EXISTING,StandardCopyOption.COPY_ATTRIBUTES};
        Files.copy(source, target, options);
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    public void setConsumeType(String consumeType) {
        this.consumeType = consumeType;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public void setPredicate(String predicate) {
        this.predicate = predicate;
    }
    
    public boolean isSkipStoreJobRequest() {
		return isSkipStoreJobRequest;
	}
    
    public void setSkipStoreJobRequest(boolean isSkipStoreJobRequest) {
		this.isSkipStoreJobRequest = isSkipStoreJobRequest;
	}
    
    public boolean isSkipStoreJobResponse() {
		return isSkipStoreJobResponse;
	}
    
    public void setSkipStoreJobResponse(boolean isSkipStoreJobResponse) {
		this.isSkipStoreJobResponse = isSkipStoreJobResponse;
	}

}
