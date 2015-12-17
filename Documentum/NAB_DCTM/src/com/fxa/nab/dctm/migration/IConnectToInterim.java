package com.fxa.nab.dctm.migration;

import java.sql.*;

public interface IConnectToInterim {

	public Connection getConnection() throws Exception;
	public ResultSet readQuery(String query) throws Exception;
	public int updateQuery(String query) throws Exception;
}
