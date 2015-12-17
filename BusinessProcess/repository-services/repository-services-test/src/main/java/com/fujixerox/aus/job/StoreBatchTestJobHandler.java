package com.fujixerox.aus.job;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.text.ParseException;
import java.util.Date;

import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.Resource;
import org.springframework.util.FileCopyUtils;
import org.springframework.util.StringUtils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fujixerox.aus.helper.StoreBatchVoucherFactory;
import com.fujixerox.aus.lombard.JaxbMapperFactory;
import com.fujixerox.aus.lombard.repository.storebatchvoucher.StoreBatchVoucherRequest;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

    public class StoreBatchTestJobHandler {
        private String lockerPath;
        private ConnectionFactory connectionFactory;
        
        private ObjectMapper objectMapper = JaxbMapperFactory.createWithAnnotations();
        private static String EXCHANGE_NAME = "lombard.service.repository.storevouchers.request";
    	protected DefaultResourceLoader defaultResourceLoader = new DefaultResourceLoader();

    	public StoreBatchTestJobHandler() {
    		super();
    	}

        public String getLockerPath() {
			return lockerPath;
		}

		public void setLockerPath(String lockerPath) {
			this.lockerPath = lockerPath;
		}

		public ConnectionFactory getConnectionFactory() {
			return connectionFactory;
		}

		public void setConnectionFactory(ConnectionFactory connectionFactory) {
			this.connectionFactory = connectionFactory;
		}

		public StoreBatchTestJobDetails createRequests(int voucherCount) throws IOException, ParseException {
                StoreBatchTestJobDetails jobDetails = new StoreBatchTestJobDetails(new Date(), 1);
                
                String[] drn = new String[voucherCount];
                for (int i = 0; i < drn.length && i < voucherCount; i++)
                {
                    drn[i] = jobDetails.getDocumentReferenceNumber();
                }
                
                createVouchers(jobDetails, voucherCount, drn);

                StoreBatchVoucherRequest storeBatchVoucherRequest = StoreBatchVoucherFactory.buildStoreBatchVoucherRequest(jobDetails.getBatchNumber(),
                        jobDetails.getJobIdentifier(), jobDetails.getProcessingDateValue(), drn, true, voucherCount);
                
                jobDetails.addStoreBatchVoucherRequest(storeBatchVoucherRequest);
                return jobDetails;
        }

        /**
         * Creates voucher files in bit locker under the job id folder.
         * @param voucherCount 
         * @param drn 
         * @param jobIdentifier
         * @throws IOException 
         */
        public void createVouchers(StoreBatchTestJobDetails jobDetails, int voucherCount, String[] drn) throws IOException{
            for (int i = 0; i < drn.length && i < voucherCount; i++) {
                collectFile(jobDetails, String.format("classpath:data/CLEAN_%02d_FRONT.jpg", i + 1), jobDetails.getFrontValue(drn[i]));
                collectFile(jobDetails, String.format("classpath:data/CLEAN_%02d_REAR.jpg", i + 1), jobDetails.getBackValue(drn[i]));
            }

            transferFiles("target", jobDetails.getJobIdentifier());
            
            System.out.println("----------------------------------------------");
        }

        public void transferFiles(String sourcePath, String jobIdentifier) throws IOException {
        	//1. Create a folder in target dir.
			File sourceFolder = new File(sourcePath, jobIdentifier);
			
			//2.Check bit locker path
			String targetDropPath = lockerPath;
			if (StringUtils.isEmpty(targetDropPath)){
			     throw new RuntimeException("Target path is not specified.");
			}
			
        	File targetFolder = new File(targetDropPath);
			if (!targetFolder.exists()){
			     throw new RuntimeException(targetFolder.getAbsolutePath() + " does not exist");
			}
        	
			File targetDir = new File(targetDropPath, jobIdentifier);
			
			//3. Copy the files to targetFolder.
			copyFolder(sourceFolder, targetDir);
        	
			//4. Do clean up.
			sourceFolder.delete();
			sourceFolder.getParentFile().delete();
        }
        
        public static void copyFolder(File src, File dest)
            	throws IOException{
         
            	if(src.isDirectory()){
            		//if directory not exists, create it
            		if(!dest.exists()){
            		   dest.mkdir();
            		   System.out.println("Directory copied from " 
                                      + src + "  to " + dest);
            		}
         
            		//list all the directory contents
            		String files[] = src.list();
         
            		for (String file : files) {
            		   //construct the src and dest file structure
            		   File srcFile = new File(src, file);
            		   File destFile = new File(dest, file);
            		   //recursive copy
            		   copyFolder(srcFile,destFile);
            		}
         
            	}else{
            		//if file, then copy it
            		//Use bytes stream to support all file types
            		try (InputStream in = new FileInputStream(src);
            		     OutputStream out = new FileOutputStream(dest); ) {
            			
            			byte[] buffer = new byte[1024];
            			
            			int length;
            			//copy the file content in bytes 
            			while ((length = in.read(buffer)) > 0){
            				out.write(buffer, 0, length);
            			}
            			
            			System.out.println("File copied from " + src + " to " + dest);
            			//Delete the source file.
            			src.delete();
            		}
            	}
            }
        
		protected void collectFile(StoreBatchTestJobDetails jobDetails, String resourceName, String targetName)
				throws IOException {
				    Resource fileFront = defaultResourceLoader.getResource(resourceName);
				
				    try (FileOutputStream outs = new FileOutputStream(new File(jobDetails.getImagesFolder(), targetName));
					     InputStream ins = fileFront.getURL().openStream();) {
				    	
				    	FileCopyUtils.copy(ins, outs);
				    }
				}

		
        public void publishMessages(StoreBatchTestJobDetails jobDetails) throws IOException {
            for(StoreBatchVoucherRequest storeBatchVoucherRequest : jobDetails.getStoreBatchVoucherRequestList()){
                publishMessage(storeBatchVoucherRequest);
            }
        }

        public void publishMessage(StoreBatchVoucherRequest storeBatchVoucherRequest) throws IOException {
        	Connection connection = null;
            try {
	                connection = connectionFactory.newConnection();
	                Channel channel = connection.createChannel();
	                String json = objectMapper.writeValueAsString(storeBatchVoucherRequest);
	
	                String fileName = "request-"+storeBatchVoucherRequest.getJobIdentifier()+".json"; 
	                writeRequestFile(fileName, json);
//	                AMQP.BasicProperties outProperties = new AMQP.BasicProperties();
//                  String correlationId = storeBatchVoucherRequest.getJobIdentifier();
//					outProperties.setCorrelationId(correlationId );
                    
	                channel.basicPublish(EXCHANGE_NAME,"", null, json.getBytes());
	                System.out.println(" [x] Sent '" + json + "'");
	                System.out.println("----------------------------------------------");
	
	                channel.close();
	                connection.close();

	            } catch (IOException e) {
	                System.out.println(e.getMessage());
	                System.out.println(e);
	            e.printStackTrace();
	        }

        }
        
        private void writeRequestFile(String name, String json) {
        	System.out.println("\n Creating request file - " + name);
        	PrintWriter out = null;
        	try {
				out = new PrintWriter(name);
				out.println(json);
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} finally {
				if(out != null){
					out.close();
				}
			}
        }
}
