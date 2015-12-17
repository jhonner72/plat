package com.fujixerox.aus.repository.util.dfc.recordextactor;

import com.documentum.fc.client.IDfCollection;
import com.documentum.fc.client.IDfSession;
import com.fujixerox.aus.lombard.repository.getvouchers.GetVouchersRequest;
import com.fujixerox.aus.repository.util.exception.NonRetriableException;

import java.util.HashMap;
import java.util.List;

public interface VoucherRecordExtractor {
	
	public IDfCollection extractRecords(GetVouchersRequest request, IDfSession session) throws NonRetriableException;
	public List<HashMap> extractVIFRecords(GetVouchersRequest request, IDfSession session) throws NonRetriableException;

}
