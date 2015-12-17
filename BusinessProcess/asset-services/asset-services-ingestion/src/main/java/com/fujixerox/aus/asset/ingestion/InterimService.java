package com.fujixerox.aus.asset.ingestion;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.documentum.fc.client.IDfFolder;
import com.documentum.fc.client.IDfSession;
import com.documentum.fc.common.DfException;
import com.documentum.fc.common.DfUtil;
import com.documentum.fc.impl.util.StringUtil;
import com.fujixerox.aus.asset.ingestion.bean.Manifest;
import com.fujixerox.aus.asset.ingestion.util.IngestionUtil;

/**
 * @author Andrey B. Panfilov <andrew@panfilov.tel>
 */
public class InterimService {

    public static final String DCTM_COMPLETED = "COMPLETED";

    private final IngestionProperties _ingestionProperties;

    private Connection _connection;

    public InterimService(IngestionProperties ingestionProperties) {
        _ingestionProperties = ingestionProperties;
    }

    public List<Manifest> acquireTasks(IDfSession session, int recordCount)
        throws SQLException, DfException {
        Connection connection = getConnection();
        synchronized (connection) {
            Statement statement = null;
            ResultSet resultSet = null;
            boolean committed = false;
            try {
                statement = getConnection().createStatement();
                String query = String.format(_ingestionProperties
                        .getPickupQuery(), recordCount, _ingestionProperties
                        .getPickupStatus(), _ingestionProperties
                        .getPickupReconcileStatus());
                resultSet = statement.executeQuery(query);
                // we want to perverse order specified in query
                Map<Integer, Manifest> manifests = new LinkedHashMap<Integer, Manifest>();
                while (resultSet.next()) {
                    int id = resultSet.getInt("ManifestID");
                    Manifest manifest = new Manifest(id, IngestionUtil
                            .trimOrEmpty(resultSet.getString("Day")),
                            IngestionUtil.trimOrEmpty(resultSet
                                    .getString("Entry")), IngestionUtil
                                    .trimOrEmpty(resultSet
                                            .getString("OutputPath")),
                            IngestionUtil.trimOrEmpty(resultSet
                                    .getString("BatchName")),
                            IngestionUtil.trimOrEmpty(resultSet
                                    .getString("Status")), IngestionUtil
                                    .trimOrEmpty(resultSet
                                            .getString("DctmIngestStatus")),
                            IngestionUtil.trimOrEmpty(resultSet
                                    .getString("DctmIngestedDates")));
                    // trying to compare status - kind of optimistic locking if
                    // query does not contain updlock hint
                    if (changeStatus(connection, manifest,
                            manifest.getStatus(), _ingestionProperties
                                    .getUploadInprogressStatus())) {
                        // trying to create folders in documentum before
                        // committing
                        preProcess(session, manifest);
                        manifests.put(id, manifest);
                    }
                }
                connection.commit();
                committed = true;
                return new ArrayList<Manifest>(manifests.values());
            } finally {
                try {
                    if (!committed) {
                        connection.rollback();
                    }
                } catch (SQLException ex) {
                    IngestionUtil.log("Exception on rollback:\n"
                            + IngestionUtil.asString(ex));
                }
                try {
                    if (resultSet != null) {
                        resultSet.close();
                    }
                } catch (SQLException ex) {
                    IngestionUtil.log("Exception on close resultset:\n"
                            + IngestionUtil.asString(ex));
                }
                try {
                    if (statement != null) {
                        statement.close();
                    }
                } catch (SQLException ex) {
                    IngestionUtil.log("Exception on close statement:\n"
                            + IngestionUtil.asString(ex));
                }
            }
        }
    }

    public boolean updateStatus(Manifest manifest, String newStatus) {
        String query;
        if (_ingestionProperties.getUploadCompleteStatus().equals(newStatus)) {
            query = String
                    .format(
                            "update Manifest set Status='%s', DctmIngestStatus='%s', DctmIngestedDates='%s'"
                                    + " where ManifestID = %s", DfUtil
                                    .escapeQuotedString(newStatus), DfUtil
                                    .escapeQuotedString(DCTM_COMPLETED), DfUtil
                                    .escapeQuotedString(covertDate(manifest
                                            .getDctmIngestedDates())), manifest
                                    .getManifestId());
        } else {
            query = String.format("update Manifest set Status='%s'"
                    + " where ManifestID = %s", DfUtil
                    .escapeQuotedString(newStatus), manifest.getManifestId());
        }

        try {
            Connection connection = getConnection();
            boolean committed = false;
            synchronized (connection) {
                try {
                    boolean result = updateQuery(connection, query) > 0;
                    connection.commit();
                    committed = true;
                    return result;
                } finally {
                    try {
                        if (!committed) {
                            connection.rollback();
                        }
                    } catch (SQLException ex) {
                        IngestionUtil.log("Exception on rollback:\n"
                                + IngestionUtil.asString(ex));
                    }
                }
            }
        } catch (SQLException ex) {
            IngestionUtil.log("Failed to update status to: " + newStatus
                    + ", for manifest: " + manifest + ", exception was:\n "
                    + IngestionUtil.asString(ex));
        }
        return false;
    }

    private boolean changeStatus(Connection connection, Manifest manifest,
            String oldStatus, String newStatus) throws SQLException {
        String query = String.format("update Manifest set Status='%s'"
                + " where ManifestID = %s and Status='%s'", DfUtil
                .escapeQuotedString(newStatus), manifest.getManifestId(),
                DfUtil.escapeQuotedString(oldStatus));
        return updateQuery(connection, query) > 0;
    }

    private int updateQuery(Connection connection, String query)
        throws SQLException {
        Statement statement = null;
        try {
            statement = connection.createStatement();
            int result = statement.executeUpdate(query);
            IngestionUtil.log("Issued database update: " + query);
            return result;
        } finally {
            try {
                if (statement != null) {
                    statement.close();
                }
            } catch (SQLException ex) {
                IngestionUtil.log("Exception on close statement:\n"
                        + IngestionUtil.asString(ex));
            }
        }
    }

    private synchronized Connection getConnection() throws SQLException {
        if (_connection == null) {
            _connection = DriverManager.getConnection(_ingestionProperties
                    .getInterimDatabaseConnectionString());
            _connection.setAutoCommit(false);
        }
        return _connection;
    }

    public String covertDate(String dctmIngestedDates) {
        String result = dctmIngestedDates;
        String today = new SimpleDateFormat("dd-MM-yyyy").format(new Date());
        if (!StringUtil.isEmptyOrNull(result)) {
            result += ";" + today;
        } else {
            result = today;
        }
        return result;
    }

    private boolean preProcess(IDfSession session, Manifest manifest)
        throws DfException {
        String entryNumber = manifest.getEntryNumber();
        String processingDate = manifest.getProcessingDate();
        String year = processingDate.substring(0, 4).replace("/", "");
        String month = processingDate.substring(4, 6).replace("/", "");
        String day = processingDate.substring(6).replace("/", "");
        String[] chunks = new String[] {year, month, day, entryNumber };
        String path = _ingestionProperties.getTargetCabinet();
        for (int i = 0; i < chunks.length; i++) {
            String chunk = chunks[i];
            IDfFolder folder = session.getFolderByPath(path + "/" + chunk);
            if (folder == null) {
                folder = (IDfFolder) session.newObject(_ingestionProperties
                        .getTargetFolderType());
                folder.setObjectName(chunk);
                if (!StringUtil.isEmptyOrNull(_ingestionProperties
                        .getFolderAclName())) {
                    folder.setACLName(_ingestionProperties.getFolderAclName());
                    folder.setACLDomain("dm_dbo");
                }
                folder.link(path);
                folder.save();
            }
            path += "/" + chunk;
            if (i == chunks.length - 1) {
                // we are storing id of target folder to avoid extra selects
                // when linking object into folder
                manifest.setFolderId(folder.getObjectId().getId());
            }
        }
        return true;
    }

}
