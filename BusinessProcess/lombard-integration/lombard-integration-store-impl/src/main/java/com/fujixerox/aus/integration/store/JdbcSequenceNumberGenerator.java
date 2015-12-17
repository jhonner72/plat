package com.fujixerox.aus.integration.store;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcCall;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Created by warwick on 12/05/2015.
 */
public class JdbcSequenceNumberGenerator implements SequenceNumberGenerator {

    private JdbcTemplate template;

    @Override
    public synchronized int nextSequenceNumber(Class sequenceName) {

        String pkgName = sequenceName.getName();
        String className = pkgName.substring(pkgName.lastIndexOf(".")+1);
        int sequenceNumber = template.queryForObject("select sequence_number from ref_sequence where sequence_name=?", new Object[]{className},
                new RowMapper<Integer>() {
                    @Override
                    public Integer mapRow(ResultSet rs, int rowNum) throws SQLException {
                        int sequence_number = rs.getInt("sequence_number");
                        return sequence_number;
                    }
                });

        SimpleJdbcCall simpleJdbcCall = new SimpleJdbcCall(template).withProcedureName("usp_Sequence_Number_Update");

        Map<String, Object> inParamMap = new HashMap<String, Object>();
        inParamMap.put("sequence_name", className);
        SqlParameterSource in = new MapSqlParameterSource(inParamMap);

        Map<String, Object> simpleJdbcCallResult = simpleJdbcCall.execute(in);

        return sequenceNumber;
    }

    @Override
    public synchronized void resetSequenceNumber(Class sequenceName) {

        String pkgName = sequenceName.getName();
        String className = pkgName.substring(pkgName.lastIndexOf(".")+1);

        SimpleJdbcCall simpleJdbcCall = new SimpleJdbcCall(template).withProcedureName("usp_Sequence_Number_Reset");
        Map<String, Object> inParamMap = new HashMap<String, Object>();
        inParamMap.put("sequence_name", className);
        SqlParameterSource in = new MapSqlParameterSource(inParamMap);

        Map<String, Object> simpleJdbcCallResult = simpleJdbcCall.execute(in);
    }

    public void setTemplate(JdbcTemplate template) {
        this.template = template;
    }
}
