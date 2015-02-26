package com.phamvinh.alo.server.db;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.mysql.jdbc.jdbc2.optional.MysqlDataSource;

public final class DbAccess {
	private static Logger LOG = LogManager.getLogger();
	private static DbAccess instance;
	private MysqlDataSource dataSource = null; 
	private DbAccess(Logger pLOG) {
		LOG = pLOG;
		dataSource = setupDataSource();
	}

	public synchronized static DbAccess getInstance(Logger pLOG) {
		 if (instance == null) {
			instance = new DbAccess(LOG);
		}
		return instance;
	}

	public Connection getConn() throws SQLException {
		Connection conn = null;
		if (dataSource != null) {
			conn = dataSource.getConnection();
		} else {
			dataSource = setupDataSource();
		}
		return conn;
	}

	public void closeConn(Connection conn) {
		if (conn != null) {
			try {
				conn.close();
			} catch (Exception e) {
				LOG.error("Database ==>" + e);
			}
		}
	}

	public void closePreparedStatement(PreparedStatement ps) {
		if (ps != null) {
			try {
				ps.close();
			} catch (SQLException e) {
				LOG.error("Database ==>" + e);
			}
		}
	}

	public void closeCallableStatement(CallableStatement cs) {
		if (cs != null) {
			try {
				cs.close();
			} catch (SQLException e) {
				LOG.error("Database ==>" + e);
			}
		}
	}

	public void closeResultSet(ResultSet rs) {
		if (rs != null) {
			try {
				rs.close();
			} catch (SQLException e) {
				LOG.error("Database ==>" + e);
			}
		}
	}

	public void closeStatement(Statement stmt) {
		if (stmt != null) {
			try {
				stmt.close();
			} catch (SQLException e) {
				LOG.error("Database ==>" + e);
			}
		}
	}

	public MysqlDataSource setupDataSource() {
		MysqlDataSource result = null;
		try {
			result = new MysqlDataSource();
			
			String url = "jdbc:mysql://" +"localhost"+ ":"
					+3306 + "/" + "aloserver";
			result.setURL(url);
			result.setUser("root");
			result.setPassword("1234");
			
			result.setConnectTimeout(2000);
			result.setMaxReconnects(5000);
			result.setMaxQuerySizeToLog(7000);
			LOG.info("Database ==> Login with MysqlDataSource");
		} catch (Exception ex) {
			LOG.error("{}", ex);
		}
		return result;
	}

	public MysqlDataSource getMysqlDataSource() {
		return dataSource;
	}

	public void setMysqlDataSource(MysqlDataSource dataSource) {
		this.dataSource = dataSource;
	}
}
