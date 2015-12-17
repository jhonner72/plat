package com.fujixerox.aus.asset.ingestion.bean;

import java.text.MessageFormat;

/**
 * @author Andrey B. Panfilov <andrew@panfilov.tel>
 */
public class Manifest {

    private int _manifestId;

    private String _processingDate;

    private String _entryNumber;

    private String _outputPath;

    private String _created;

    private String _batchName;

    private volatile String _status;

    private String _dctmIngestStatus;

    private String _dctmIngestedDates;

    private String _folderId;

    public Manifest() {
        super();
    }

    public Manifest(int manifestId, String processingDate, String entryNumber,
            String outputPath) {
        _manifestId = manifestId;
        _processingDate = processingDate;
        _entryNumber = entryNumber;
        _outputPath = outputPath;
        _batchName = "NAB_" + processingDate + "_" + entryNumber;
    }

    public Manifest(int manifestId, String processingDate, String entryNumber,
            String outputPath, String batchName) {
        _manifestId = manifestId;
        _processingDate = processingDate;
        _entryNumber = entryNumber;
        _outputPath = outputPath;
        _batchName = batchName;
    }

    public Manifest(int manifestId, String processingDate, String entryNumber,
            String outputPath, String batchName, String status,
            String dctmIngestStatus, String dctmIngestedDates) {
        _manifestId = manifestId;
        _processingDate = processingDate;
        _entryNumber = entryNumber;
        _outputPath = outputPath;
        _batchName = batchName;
        _status = status;
        _dctmIngestStatus = dctmIngestStatus;
        _dctmIngestedDates = dctmIngestedDates;
    }

    public int getManifestId() {
        return _manifestId;
    }

    public void setManifestId(int manifestId) {
        _manifestId = manifestId;
    }

    public String getProcessingDate() {
        return _processingDate;
    }

    public void setProcessingDate(String processingDate) {
        _processingDate = processingDate;
    }

    public String getEntryNumber() {
        return _entryNumber;
    }

    public void setEntryNumber(String entryNumber) {
        _entryNumber = entryNumber;
    }

    public String getOutputPath() {
        return _outputPath;
    }

    public void setOutputPath(String outputPath) {
        _outputPath = outputPath;
    }

    public String getCreated() {
        return _created;
    }

    public void setCreated(String created) {
        _created = created;
    }

    public String getBatchName() {
        return _batchName;
    }

    public void setBatchName(String batchName) {
        _batchName = batchName;
    }

    public void setBatchName(String processingDate, String entryNumber) {
        _batchName = "NAB_" + processingDate + "_" + entryNumber;
    }

    public String getStatus() {
        return _status;
    }

    public void setStatus(String status) {
        _status = status;
    }

    public String getDctmIngestStatus() {
        return _dctmIngestStatus;
    }

    public void setDctmIngestStatus(String dctmIngestStatus) {
        _dctmIngestStatus = dctmIngestStatus;
    }

    public String getDctmIngestedDates() {
        return _dctmIngestedDates;
    }

    public void setDctmIngestedDates(String dctmIngestedDates) {
        _dctmIngestedDates = dctmIngestedDates;
    }

    public String getFolderId() {
        return _folderId;
    }

    public void setFolderId(String folderId) {
        _folderId = folderId;
    }

    @Override
    public String toString() {
        return MessageFormat.format(
                "ManifestID: {0,number,#}, Day: {1}, Entry: {2}, "
                        + "OutputPath: {3}, Created: {4}, "
                        + "BatchName: {5}, Status: {6}, "
                        + "DctmIngestStatus: {7}, DctmIngestedDates: {8}",
                _manifestId, _processingDate, _entryNumber, _outputPath,
                _created, _batchName, _status, _dctmIngestStatus,
                _dctmIngestedDates);
    }

}
