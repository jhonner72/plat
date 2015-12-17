package com.fujixerox.aus.lombard.outclearings.iereconciliation;

import com.fujixerox.aus.integration.store.JobStore;
import com.fujixerox.aus.lombard.common.job.Activity;
import com.fujixerox.aus.lombard.common.job.Job;
import com.fujixerox.aus.lombard.common.receipt.ReceivedFile;
import com.fujixerox.aus.lombard.inclearings.matchfiles.MatchFilesResponse;
import com.fujixerox.aus.lombard.repository.getreceivedfiles.GetReceivedFilesResponse;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.*;
import java.util.*;

/**
 * A helper class for the Voucher Processing business process.
 * Created by warwick on 20/05/2015.
 */
public class IEReconciliationJobAdapter{
    private JobStore jobStore;

    private String lockerPath;

    Log log = LogFactory.getLog(IEReconciliationJobAdapter.class);


    /**
     * Determine if this inbound file for voucher processing has been presented before.
     * 1. Check if a camunda process is in flight
     * 2.
     * @param processBusinessKey
     * @return
     */
    public boolean matchFile(String processBusinessKey)
    {
        boolean isMatched = true;
        Job job = jobStore.findJob(processBusinessKey);

        GetReceivedFilesResponse getReceivedFilesResponse = getReceivedFilesResponse(job);
        if (getReceivedFilesResponse == null) return false;

        File sourceFile = new File(lockerPath, processBusinessKey);
        log.info("MatchReceivedFilesRoute || sourceFile path - " + sourceFile.getAbsolutePath());

        if (!sourceFile.exists()) {
            log.error("SourceFile Does not  exist --" + sourceFile.getAbsolutePath());
            return false;
        }

        File[] receivedFile = sourceFile.listFiles(new FilenameFilter() {
            public boolean accept(File dir, String filename) {
                return filename.endsWith(".DAT");
            }
        });

        File receivedFileName = receivedFile[0];
        log.info("MatchReceivedFilesRoute || receivedDatFile name - " + receivedFileName.getName());

        List<String> fileNameInFileList = getFileNamesInFile(receivedFileName);
        log.info("MatchReceivedFilesRoute || fileNameInDATFile - Size - " + fileNameInFileList.size());

        MatchFilesResponse matchFilesResponse = matchReceivedFileNames(getReceivedFilesResponse.getReceivedFiles(), fileNameInFileList);

        Activity parameterActivity = new Activity();
        parameterActivity.setPredicate("match");
        parameterActivity.setSubject("receivedfiles");
        parameterActivity.setJobIdentifier(processBusinessKey);
        parameterActivity.setResponse(matchFilesResponse);
        parameterActivity.setRequestDateTime(new Date());
        job.getActivities().add(parameterActivity);


        if((matchFilesResponse.getUnmatchedFilesFromDats().size() != 0) || (matchFilesResponse.getUnmatchedFilesReceiveds().size() != 0)){
            isMatched = false;
        }

        log.info("MatchReceivedFilesRoute || isMatched - " + isMatched);

        return isMatched;
    }

    private GetReceivedFilesResponse getReceivedFilesResponse(Job job) {

        for (Activity activity : job.getActivities()) {
            if (activity.getSubject().equals("receivedfiles") && activity.getPredicate().equals("get")) {
                return (GetReceivedFilesResponse) activity.getResponse();
            }
        }
        return null;
    }


    private MatchFilesResponse matchReceivedFileNames(List<ReceivedFile> receivedFilenames, List<String> fileNameInFileList) {
        MatchFilesResponse response = new MatchFilesResponse();
        List<String> fileNameList = new ArrayList<>();

        for(ReceivedFile receivedFilename: receivedFilenames){
            fileNameList.add(receivedFilename.getFileIdentifier());

            boolean isMatched = findReceivedInFile(receivedFilename.getFileIdentifier(), fileNameInFileList);
            if(!isMatched){
                log.info("MatchReceivedFilesRoute || not matched || getUnmatchedFilesReceiveds --" + receivedFilename);
                response.getUnmatchedFilesReceiveds().add(receivedFilename.getFileIdentifier());
            }
        }

        for(String fileName: fileNameInFileList){

            boolean isMatched = findReceivedInFile(fileName, fileNameList);
            if(!isMatched){
                log.info("MatchReceivedFilesRoute || not matched || getUnmatchedFilesFromDats --" + fileName);
                response.getUnmatchedFilesFromDats().add(fileName);
            }
        }

        return response;
    }

    private boolean findReceivedInFile(String receivedFilename, List<String> fileNameInFileList) {
        for(String receivedFile: fileNameInFileList){
            //comparing only without the extensions
            if(getFileNameWithoutExtension(receivedFile).trim().equalsIgnoreCase(getFileNameWithoutExtension(receivedFilename).trim()))
            {
                return true;

            }
        }

        return false;

    }

    private String getFileNameWithoutExtension(String fileName) {
        int extensionPos = fileName.lastIndexOf(".");
        if (extensionPos > 0) {
            fileName = fileName.substring(0, extensionPos);

        }
        return fileName;
    }


    private List<String> getFileNamesInFile(File receivedFileName) {
        List<String> fileNamesList = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(receivedFileName))) {
            String line;
            while ((line = br.readLine()) != null) {
                if (!line.trim().isEmpty()) {
                    fileNamesList.add(line);
                    log.info("IEReconciliationJobAdapter || File Name in DAT ----" + line);
                }
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return fileNamesList;
    }


    public void setLockerPath(String lockerPath) {
        this.lockerPath = lockerPath;
    }


    public void setJobStore(JobStore jobStore) {
        this.jobStore = jobStore;
    }

}
