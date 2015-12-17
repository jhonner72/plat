package com.fujixerox.aus.integration.store;

/**
 * @author Alex.Park
 * @since 8/12/2015
 */
public interface TrackingStore {

	public int deleteFileTransmissionLog(int retentionDays);

	public int deleteBatchAuditLog(int retentionDays);

}
