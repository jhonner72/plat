package com.fujixerox.aus.integration.store;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.jdbc.core.JdbcTemplate;

import com.fasterxml.jackson.databind.DeserializationConfig;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.TypeFactory;
import com.fasterxml.jackson.module.jaxb.JaxbAnnotationIntrospector;


/**
 * @author Alex.Park
 * @since 8/12/2015
 */
public class JdbcTrackingStore implements TrackingStore {

    ObjectMapper objectMapper;
    private JdbcTemplate template;
    Log log = LogFactory.getLog(JdbcTrackingStore.class);
    
    public JdbcTrackingStore()
    {
        objectMapper = new ObjectMapper();
        JaxbAnnotationIntrospector ai=new JaxbAnnotationIntrospector(TypeFactory.defaultInstance());
        DeserializationConfig deserializationConfig = objectMapper.getDeserializationConfig();
        deserializationConfig.with(ai);
    }

    public void setTemplate(JdbcTemplate template) {
        this.template = template;
    }

	@Override
	public int deleteFileTransmissionLog(int retentionDays) {
		String sql = "delete from ref_file_transmission where DATEDIFF(day, file_timestamp, GETDATE()) > ?";
        int recordCount = template.update(sql, new Object[]{retentionDays});
        return recordCount; 
	}
	
	@Override
	public int deleteBatchAuditLog(int retentionDays) {
    	String sql = "delete from ref_batch_audit where DATEDIFF(day, file_timestamp, GETDATE()) > ?";
        int recordCount = template.update(sql, new Object[]{retentionDays});
        return recordCount; 
	}
}
