package com.phamvinh.alo.server.db;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.phamvinh.alo.server.common.PasswordUtil;

public class DbAction {
	private static final Logger LOGGER = LogManager.getLogger(DbAction.class
			.getSimpleName());
	private static DbAction instance = new DbAction();

	/**
	 * @return the instance
	 */
	public static DbAction getInstance() {
		return instance;
	}

	public int checkLoginAsPhone(String pPhone, String pRawPassword) {
		Connection connection = null;
		PreparedStatement pstm = null;
		ResultSet rs = null;
		PreparedStatement getSaltStm = null;
		ResultSet getSaltRs = null;
		try {
			connection = DbAccess.getInstance(LOGGER).getConn();
			String getSaltSQL = "select salt from account where phone=? and isLock=0";
			getSaltStm = connection.prepareStatement(getSaltSQL);
			getSaltStm.setString(1, pPhone);
			getSaltRs = getSaltStm.executeQuery();
			String salt = null;
			if (getSaltRs.next()) {
				salt = getSaltRs.getString("salt");
			} else {
				return 1;
			}
			String encrytedPass = PasswordUtil.encryptPassword(pRawPassword,
					salt);

			String checkSQL = "select count(id) as c from account where phone=? and password=? ";
			pstm = connection.prepareStatement(checkSQL);
			pstm.setString(1, pPhone);
			pstm.setString(2, encrytedPass);
			rs = pstm.executeQuery();
			if (rs.next() && rs.getInt("c") == 1) {
				return 0;
			} else {
				return 1;
			}

		} catch (SQLException sqlex) {
			LOGGER.error(sqlex.toString());
			return -1;
		} finally {
			DbAccess.getInstance(LOGGER).closeResultSet(getSaltRs);
			DbAccess.getInstance(LOGGER).closePreparedStatement(getSaltStm);
			DbAccess.getInstance(LOGGER).closeResultSet(rs);
			DbAccess.getInstance(LOGGER).closePreparedStatement(pstm);
			DbAccess.getInstance(LOGGER).closeConn(connection);
		}

	}

	public int checkLoginAsToken(int userId, String token) {
		Connection connection = null;
		PreparedStatement pstm = null;
		ResultSet rs = null;
		try {
			connection = DbAccess.getInstance(LOGGER).getConn();
			String checkSQL = "select token from login_history where user_id=? order by login_time desc limit 1";
			pstm = connection.prepareStatement(checkSQL);
			pstm.setInt(1, userId);
			rs = pstm.executeQuery();
			String dbToken ;
			if (rs.next() && (dbToken = rs.getString("token")).equals(token)) {
				return 0;
			} else {
				return 1;
			}

		} catch (SQLException sqlex) {
			LOGGER.error(sqlex.toString());
			return -1;
		} finally {
			DbAccess.getInstance(LOGGER).closeResultSet(rs);
			DbAccess.getInstance(LOGGER).closePreparedStatement(pstm);
			DbAccess.getInstance(LOGGER).closeConn(connection);
		}

	}

	/**
	 * Insert new account in database configure in {@link DbAccess} instance
	 * <hr>
	 * 
	 * @param pPass
	 *            {@link String}: 32byte password encrypted
	 * @param pPhone
	 *            {@link String}: phone number of account
	 * @param pSalt
	 *            {@link String}: 6 byte salt when encrypt password
	 * @return Return {@link boolean}
	 *         <ul>
	 *         <li><b>true</b> if create new account success</li> <li>
	 *         <b>false</b> if account is exist or {@link DbAccess} is error
	 *         </li>
	 *         </ul>
	 * 
	 * 
	 * @author via
	 */
	public int createAccount(String pPass, String pPhone, String pSalt) {
		Connection connection = null;
		CallableStatement pstm = null;
		try {
			connection = DbAccess.getInstance(LOGGER).getConn();
			String sql = "{call create_account(?,?,?,?)}";
			pstm = connection.prepareCall(sql);
			pstm.setString(1, pPhone);
			pstm.setString(2, pPass);
			pstm.setString(3, pSalt);			
			pstm.registerOutParameter(4, java.sql.Types.INTEGER);
		    pstm.execute();	
			if (!connection.getAutoCommit()){
				connection.commit();
			}
			int rowCount = pstm.getInt(4);
			if (rowCount >= 1){
				return 0;
			}else{
				return 1;
			}			
				
		} catch (SQLException sqlex) {
			try {
				if (!connection.getAutoCommit())
					connection.rollback();
			} catch (SQLException e) {
				LOGGER.error(e.toString());
			}
			LOGGER.error("Create account :{}", sqlex.toString());
			return -1;
		} finally {
			DbAccess.getInstance(LOGGER).closePreparedStatement(pstm);
			DbAccess.getInstance(LOGGER).closeConn(connection);
		}
		
	}

	/**
	 * 
	 * @param pUsername
	 * @param pPhone
	 * @return <b>int</b>
	 * 
	 *         <ul>
	 *         <li><b>-1</b></li> if error when use database query
	 *         <li><b>0</b></li> if account not exist
	 *         <li>1</li> if user name is exist
	 *         <li>2</li> if phone number is exist
	 *         </ul>
	 * @author pham dinh vinh
	 */
	public int checkAccountExist(String pPhone) {
		Connection connection = null;
		PreparedStatement pstm = null;
		ResultSet rs = null;
		try {
			connection = DbAccess.getInstance(LOGGER).getConn();
			String sql = "select count(id) as c from account where phone=? and isLock=0";
			pstm = connection.prepareStatement(sql);
			pstm.setString(1, pPhone);
			rs = pstm.executeQuery();
			rs.next();
			if (rs.getInt("c") == 0) {
				return 0;
			}
			return 1;
		} catch (SQLException sqlex) {
			LOGGER.error("Check user: {}", sqlex.toString());
			return -1;
		} finally {
			DbAccess.getInstance(LOGGER).closeResultSet(rs);
			DbAccess.getInstance(LOGGER).closePreparedStatement(pstm);
			DbAccess.getInstance(LOGGER).closeConn(connection);
		}

	}

	public int checkConfirmPasscode(String phone_number, String passcode) {
		Connection connection = null;
		java.sql.CallableStatement cstm = null;
		try {
			connection = DbAccess.getInstance(LOGGER).getConn();
			String sql = "{call confirmPasscode(?,?,?)}";
			cstm = connection.prepareCall(sql);
			cstm.setString(1, phone_number);
			cstm.setString(2, passcode);
			cstm.registerOutParameter(3, java.sql.Types.INTEGER);
			cstm.executeUpdate();
			int rs2 = cstm.getInt(3);
			return rs2;
		} catch (SQLException sqlex) {
			try {
				if (!connection.getAutoCommit())
					connection.rollback();
			} catch (SQLException e) {
				LOGGER.error(e.toString());
			}
			LOGGER.error("Create account :{}", sqlex.toString());
		} finally {
			DbAccess.getInstance(LOGGER).closePreparedStatement(cstm);
			DbAccess.getInstance(LOGGER).closeConn(connection);
		}
		return -1;
	}

	public int getUserIdByPhone(String phone) {
		Connection connection = null;
		PreparedStatement pstm = null;
		ResultSet rs = null;
		try {
			connection = DbAccess.getInstance(LOGGER).getConn();
			String sql = "select id from account where phone=?";
			pstm = connection.prepareStatement(sql);
			pstm.setString(1, phone);
			rs = pstm.executeQuery();
			if (rs.next()) {
				return rs.getInt("id");
			}
			return 0;
		} catch (SQLException sqlex) {
			LOGGER.error("Create account :{}", sqlex.toString());
		} finally {
			DbAccess.getInstance(LOGGER).closeResultSet(rs);
			DbAccess.getInstance(LOGGER).closePreparedStatement(pstm);
			DbAccess.getInstance(LOGGER).closeConn(connection);
		}
		return 0;
	}

	public int insertLoginInfo(int user_id, String token, String remoteIP,
			int remotePort) {
		Connection connection = null;
		PreparedStatement pstm = null;
		try {
			connection = DbAccess.getInstance(LOGGER).getConn();
			String sql = "insert into login_history(`user_id`,`remote_ip`,`remote_port`,`token`) values(?,?,?,?)";
			pstm = connection.prepareStatement(sql);
			pstm.setInt(1, user_id);
			pstm.setString(2, remoteIP);
			pstm.setInt(3, remotePort);
			pstm.setString(4, token);
			int count = pstm.executeUpdate();
			if (!connection.getAutoCommit()) {
				connection.commit();
			}
			if (count == 1) {
				return 0;
			} else {
				return 1;
			}
		} catch (SQLException sqlex) {
			try {
				if (!connection.getAutoCommit())
					connection.rollback();
			} catch (SQLException e) {
				LOGGER.error(e.toString());
			}
			LOGGER.error("Create account :{}", sqlex.toString());
		} finally {
			DbAccess.getInstance(LOGGER).closePreparedStatement(pstm);
			DbAccess.getInstance(LOGGER).closeConn(connection);
		}
		return -1;

	}
}
