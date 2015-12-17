package com.fujixerox.aus.job;

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPReply;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PropertiesLoaderUtils;
import org.springframework.expression.Expression;
import org.springframework.expression.common.TemplateParserContext;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.util.FileCopyUtils;
import org.springframework.util.StringUtils;

import java.io.*;
import java.nio.file.Files;
import java.util.Date;
import java.util.Properties;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * Created by warwick on 10/04/2015.
 */
public class JobTestHandler {
    Properties properties;
    DefaultResourceLoader defaultResourceLoader = new DefaultResourceLoader();

    private static final String DELIVERY_MODE = "delivery.mode";
    private static final String DELIVERY_MODE_FTP = "ftp";
    private static final String DELIVERY_MODE_DROP = "drop";
    private static final String DELIVERY_DROP_PATH = "delivery.drop.path";

    public JobTestHandler()
    {
        Resource resource = defaultResourceLoader.getResource("file:config.ini");
        if (!resource.exists())
        {
            resource = defaultResourceLoader.getResource("classpath:config.ini");
        }
        try {
            properties = PropertiesLoaderUtils.loadProperties(resource);
        } catch (IOException e) {
            throw new RuntimeException("Could not load config.ini");
        }
    }

    public JobDetails create(int voucherCount) throws IOException {

        JobDetails jobDetails = new JobDetails(new Date(), 1);

        SpelExpressionParser parser = new SpelExpressionParser();
        StandardEvaluationContext context = new StandardEvaluationContext();

        Resource resource = defaultResourceLoader.getResource(String.format("classpath:data/CLEAN_VOUCHERS%d.xml", voucherCount));

        byte[] bytes = FileCopyUtils.copyToByteArray(resource.getURL().openStream());
        String input = new String(bytes);

        Expression expression = parser.parseExpression(input, new TemplateParserContext());

        context.setVariable("batchNumber", jobDetails.getBatchNumber());

        String[] drn = new String[voucherCount];
        for (int i = 0; i < drn.length; i++)
        {
            drn[i] = jobDetails.getDocumentReferenceNumber();
        }
        context.setVariable("documentReferenceNumber", drn);
        context.setVariable("processingDate", jobDetails.getProcessingDateTimeValue());
        String xml = (String) expression.getValue(context);
        FileCopyUtils.copy(xml.getBytes(), new File(jobDetails.getJobFolder(), String.format(jobDetails.getMetadataFilenameValue())));

        for (int i = 0; i < drn.length; i++) {
            collectFile(jobDetails, String.format("classpath:data/CLEAN_%02d_FRONT.jpg", i + 1), jobDetails.getFrontValue(drn[i]));
            collectFile(jobDetails, String.format("classpath:data/CLEAN_%02d_REAR.jpg", i + 1), jobDetails.getBackValue(drn[i]));
            collectFile(jobDetails, String.format("classpath:data/CLEAN_%02d_FRONT.tif", i + 1), jobDetails.getFrontValueBitonal(drn[i]));
            collectFile(jobDetails, String.format("classpath:data/CLEAN_%02d_REAR.tif", i + 1), jobDetails.getBackValueBitonal(drn[i]));
        }

        zipFiles(jobDetails.getZipFile(), new File("target", jobDetails.getJobIdentifier()));
        return jobDetails;
    }

    protected void collectFile(JobDetails jobDetails, String resourceName, String targetName) throws IOException {
        Resource fileFront = defaultResourceLoader.getResource(resourceName);

        FileOutputStream outs = new FileOutputStream(new File(jobDetails.getImagesFolder(), targetName));
        InputStream ins = fileFront.getURL().openStream();
        FileCopyUtils.copy(ins, outs);
        ins.close();
        outs.close();
    }

    public void transferFile(File inputFile) throws IOException {
        String deliveryMode = (String) properties.get(DELIVERY_MODE);

        if (DELIVERY_MODE_FTP.equals(deliveryMode))
        {
            ftpFile(inputFile);
        }
        else if (DELIVERY_MODE_DROP.equals(deliveryMode))
        {
            dropFile(inputFile);
        }
        else
        {
            throw new RuntimeException("Invalid delivery mode:" + deliveryMode);
        }
    }

    private void dropFile(File inputFile) throws IOException {

        String targetValue = properties.getProperty(DELIVERY_DROP_PATH);
        if (StringUtils.isEmpty(targetValue))
        {
            throw new RuntimeException(DELIVERY_DROP_PATH + " not specified in config.ini");
        }
        File targetDir = new File(targetValue);
        if (!targetDir.exists())
        {
            throw new RuntimeException(targetDir.getAbsolutePath() + " does not exist");
        }
        File targetFile = new File(targetDir, inputFile.getName());
        Files.move(inputFile.toPath(), targetFile.toPath());
    }

    protected void ftpFile(File inputFile) throws IOException
    {
        FTPClient ftpClient = new FTPClient();
        ftpClient.connect("localhost");
        ftpClient.login("admin", "admin");

        int reply = ftpClient.getReplyCode();

        if(!FTPReply.isPositiveCompletion(reply)) {
            ftpClient.disconnect();
            System.err.println("FTP server refused connection.");
            return;
        }

        ftpClient.enterLocalPassiveMode();
        ftpClient.setFileType(FTP.BINARY_FILE_TYPE);

        System.out.println("Transferring file:" + inputFile.getAbsolutePath());
        FileInputStream fileInputStream = new FileInputStream(inputFile);
        if (!ftpClient.storeFile(inputFile.getName(), fileInputStream))
        {
            System.err.println("Failed to transfer file");
        }
        fileInputStream.close();
        System.out.println("Transferring Done");

        if (ftpClient.isConnected()) {
            ftpClient.logout();
            ftpClient.disconnect();
        }
    }

    public void zipFiles(File outputFile, File inputDir) throws IOException {

        FileOutputStream fos = new FileOutputStream(outputFile);
        ZipOutputStream zos = new ZipOutputStream(fos);
        zipFiles(zos, inputDir.listFiles(), "");
        zos.close();
    }

    private void zipFiles(ZipOutputStream zos, File[] files, String path) throws IOException {
        byte[] buffer = new byte[1024];
        for (File input : files) {

            if (input.isDirectory())
            {
                ZipEntry ze = new ZipEntry(input.getName() + "/");
                zos.putNextEntry(ze);
                zipFiles(zos, input.listFiles(), input.getName() + "/");
                continue;
            }
            ZipEntry ze = new ZipEntry(path + input.getName());
            zos.putNextEntry(ze);
            FileInputStream in = new FileInputStream(input);

            int len;
            while ((len = in.read(buffer)) > 0) {
                zos.write(buffer, 0, len);
            }

            in.close();
            zos.closeEntry();
        }
    }
}
