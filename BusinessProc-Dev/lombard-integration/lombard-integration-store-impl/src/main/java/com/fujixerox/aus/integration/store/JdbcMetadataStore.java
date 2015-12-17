package com.fujixerox.aus.integration.store;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationConfig;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationConfig;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.type.TypeFactory;
import com.fasterxml.jackson.module.jaxb.JaxbAnnotationIntrospector;
import com.fujixerox.aus.lombard.common.metadata.BusinessCalendar;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by warwick on 12/05/2015.
 */
public class JdbcMetadataStore implements MetadataStore {

    ObjectMapper objectMapper;
    private JdbcTemplate template;

    public JdbcMetadataStore()
    {
        objectMapper = new ObjectMapper();
        JaxbAnnotationIntrospector ai=new JaxbAnnotationIntrospector(TypeFactory.defaultInstance());
        DeserializationConfig deserializationConfig = objectMapper.getDeserializationConfig();
        deserializationConfig.with(ai);
    }

    @Override
    public <T> T getMetadata(Class<T> clazz) {

        String pkgName = clazz.getName();
        String className = pkgName.substring(pkgName.lastIndexOf(".")+1);
            try {
                String jsonObject = template.queryForObject("select ref_value from ref_metadata where ref_name=?", new Object[]{className},
                        new RowMapper<String>() {
                            @Override
                            public String mapRow(ResultSet rs, int rowNum) throws SQLException {
                                String json = rs.getString("ref_value");
                                return json;
                            }
                        });
                return unmarshal(jsonObject, clazz);
            } catch (EmptyResultDataAccessException ex) {
                if (clazz == BusinessCalendar.class)
                {
                    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                    BusinessCalendar businessCalendar = new BusinessCalendar();
                    try {
                        businessCalendar.setBusinessDay(dateFormat.parse(dateFormat.format(new Date())));
                    } catch (ParseException e) {
                        throw new RuntimeException("Cant happen", e);
                    }
                    try {
                        String json = objectMapper.writeValueAsString(businessCalendar);
                        template.update("insert into ref_metadata (ref_name, ref_value) values(?, ?)", new Object[]{className, json});
                    } catch (JsonProcessingException e) {
                        throw new RuntimeException("Failed to marshal", e);
                    }
                    return (T) businessCalendar;
                } else {
                    throw new RuntimeException("Metadata has not been seeded:" + className);
                }
        }
    }

    @Override
    public <T> int storeMetadata(T clazz) {
        String pkgName = (clazz.getClass()).getName();
        String className = pkgName.substring(pkgName.lastIndexOf(".") + 1);
        int recordUpdated = 0;

        try {
            objectMapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
            String json = objectMapper.writeValueAsString(clazz);
            if (getMetadata(clazz.getClass()) != null) {
                recordUpdated = template.update("update ref_metadata set ref_value=? where ref_name=?", new Object[]{json, className});
            }
            return recordUpdated;
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Unable to marshal "+pkgName+ " object to a JSON string.", e);
        }
    }

    public <T> T unmarshal(String jsonString, Class<T> clazz)  {
        try {
            return objectMapper.readValue(jsonString, clazz);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Unable to unmarshal JSON string " +jsonString+ " to " +clazz.getName()+ " object.", e);
        } catch (IOException e) {
            throw new RuntimeException("Unable to unmarshal JSON string " +jsonString+ " to " +clazz.getName()+ " object.", e);
        }
    }

    public void setTemplate(JdbcTemplate template) {
        this.template = template;
    }
}
