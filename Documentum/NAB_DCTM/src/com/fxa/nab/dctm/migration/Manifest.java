package com.fxa.nab.dctm.migration;

public class Manifest {
	private int ManifestID;
	private String processingDate=null;
	private String entryNumber=null;
	private String outputPath=null;
	private String created=null;
	private String batchName=null;//NOT AVAILABLE IN THE TABLE. Computed on request
	private String status=null;
	private String dctmIngestStatus=null;
	private String dctmIngestedDates=null;
	
	public Manifest(){}
	
	public Manifest(int manifestID,String processingDate,String entryNumber,String outputPath){
		this.ManifestID=manifestID;
		this.processingDate=processingDate;
		this.entryNumber=entryNumber;
		this.outputPath=outputPath;
		this.batchName="NAB_"+processingDate+"_"+entryNumber;
	}
	
	public Manifest(int manifestID,String processingDate,String entryNumber,String outputPath,String batchName){
		this.ManifestID=manifestID;
		this.processingDate=processingDate;
		this.entryNumber=entryNumber;
		this.outputPath=outputPath;
		this.batchName=batchName;
	}
	
	
	public Manifest(int manifestID,String processingDate,String entryNumber,String outputPath,String batchName,String status,String dctmIngestStatus,String dctmIngestedDates){
		this.ManifestID=manifestID;
		this.processingDate=processingDate;
		this.entryNumber=entryNumber;
		this.outputPath=outputPath;
		this.batchName=batchName;
		this.status=status;
		this.dctmIngestStatus=dctmIngestStatus;
		this.dctmIngestedDates=dctmIngestedDates;
	}
	
	public int getManifestID() {
		return ManifestID;
	}
	
	public void setManifestID(int manifestID) {
		ManifestID = manifestID;
	}	
	
	public String getProcessingDate() {
		return processingDate;
	}
	
	public void setProcessingDate(String processingDate) {
		this.processingDate = processingDate;
	}
	
	public String getEntryNumber() {
		return entryNumber;
	}
	
	public void setEntryNumber(String entryNumber) {
		this.entryNumber = entryNumber;
	}
	
	public String getOutputPath() {
		return outputPath;
	}
	
	public void setOutputPath(String outputPath) {
		this.outputPath = outputPath;
	}
	
	public String getCreated() {
		return created;
	}
	
	public void setCreated(String created) {
		this.created = created;
	}
	
	public String getBatchName() {
		return batchName;
	}
	
	public void setBatchName(String batchName) {
		this.batchName = batchName;
	}
	
	public void setBatchName(String processingDate,String entryNumber) {
		this.batchName = "NAB_"+processingDate+"_"+entryNumber;
	}	

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getDctmIngestStatus() {
		return dctmIngestStatus;
	}

	public void setDctmIngestStatus(String dctmIngestStatus) {
		this.dctmIngestStatus = dctmIngestStatus;
	}

	public String getDctmIngestedDates() {
		return dctmIngestedDates;
	}

	public void setDctmIngestedDates(String dctmIngestedDates) {
		this.dctmIngestedDates = dctmIngestedDates;
	}


}
