package com.phamvinh.alo.server.db;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.phamvinh.alo.server.common.PasswordUtil;
import com.phamvinh.alo.server.common.PhoneFormat;

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

	


	public String findLoginedAddr(int userId){
		Connection connection = null;
		CallableStatement cstm = null;
		ResultSet rs = null;
		try{
			connection = DbAccess.getInstance(LOGGER).getConn();
			String query = "{call sp_findLoginedAddrById(?)}";
			cstm = connection.prepareCall(query);
			cstm.setInt(1, userId);
			rs = cstm.executeQuery();
			if(rs.next()){
				String ip = rs.getString("ip");
				String port = rs.getString("port");
				return ip+":"+port;
			}
			return null;
		}catch(SQLException exception){
			LOGGER.error("Create account :{}", exception.toString());
			return null;
		}finally{
			DbAccess.getInstance(LOGGER).closeCallableStatement(cstm);
			DbAccess.getInstance(LOGGER).closeConn(connection);
		}
	}
	public int insertContact(int user_id, ArrayList<String> phones, ArrayList<String> names){
		Connection cn = null;
		CallableStatement cstm = null;
		try {
			cn = DbAccess.getInstance(LOGGER).getConn();
			cn.setAutoCommit(false);
			String sql = "{call sp_insert_update_contact(?,?,?)}";
			cstm = cn.prepareCall(sql);
			cstm.setInt(1, user_id);
			for (int i = 0; i < phones.size(); i++) {				
				String formated = PhoneFormat.formatPhone(phones.get(i));
				cstm.setString(2,formated);
				cstm.setString(3,names.get(i));
				cstm.addBatch();
			}
			cstm.executeBatch();
			cn.commit();
			return 0;
		} catch (SQLException e) {
			try {
				cn.rollback();
			} catch (SQLException e1) {
				LOGGER.error(e1);
			}
			return -1;
			
		}finally{			
			DbAccess.getInstance(LOGGER).closeCallableStatement(cstm);
			DbAccess.getInstance(LOGGER).closeConn(cn);
		}
	}
	
}
