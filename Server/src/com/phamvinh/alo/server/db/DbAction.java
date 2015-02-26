package com.phamvinh.alo.server.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class DbAction {
	private static final Logger LOGGER = LogManager.getLogger(DbAction.class.getSimpleName());
	private static DbAction instance = new DbAction();
	/**
	 * @return the instance
	 */
	public static DbAction getInstance() {
		return instance;
	}
	
	public boolean checkLoginAsName(String uname, String pPassEncrypted){
		Connection connection = null;
		PreparedStatement pstm = null;
		ResultSet rs = null;
		try{
			connection = DbAccess.getInstance(LOGGER).getConn();
			String sql = "select count(id) as c from account where username=? and password=? ";
			pstm = connection.prepareStatement(sql);
			pstm.setString(1, uname);
			pstm.setString(2, pPassEncrypted);
			rs = pstm.executeQuery();
			while (rs.next()) {
				if(rs.getInt("c") == 1){
					return true;
				}else{
					return false;
				}
			}
		}catch(SQLException sqlex){
			
			LOGGER.error(sqlex.toString());
		}finally{
			DbAccess.getInstance(LOGGER).closeResultSet(rs);
			DbAccess.getInstance(LOGGER).closePreparedStatement(pstm);
			DbAccess.getInstance(LOGGER).closeConn(connection);			
		}		
		return false;
	}
	public boolean checkLoginAsPhone(String uname, String pPassEncrypted){
		Connection connection = null;
		PreparedStatement pstm = null;
		ResultSet rs = null;
		try{
			connection = DbAccess.getInstance(LOGGER).getConn();
			String sql = "select count(id) as c from account where phone=? and password=? ";
			pstm = connection.prepareStatement(sql);
			pstm.setString(1, uname);
			pstm.setString(2, pPassEncrypted);
			rs = pstm.executeQuery();
			while (rs.next()) {
				if(rs.getInt("c") == 1){
					return true;
				}else{
					return false;
				}
			}
		}catch(SQLException sqlex){
			
			LOGGER.error(sqlex.toString());
		}finally{
			DbAccess.getInstance(LOGGER).closeResultSet(rs);
			DbAccess.getInstance(LOGGER).closePreparedStatement(pstm);
			DbAccess.getInstance(LOGGER).closeConn(connection);			
		}		
		return false;
	}
	
	/**
	 * Insert new account in database configure in {@link DbAccess} instance
	 * <hr>
	 * 
	 * @param pUname {@link String}: user name of account, this is unique
	 * @param pPass	{@link String}:  32byte password encrypted 
	 * @param pPhone {@link String}: phone number of account
	 * @param pSalt {@link String}: 6 byte salt when encrypt password 
	 * @return Return {@link boolean}
	 * <ul>
	 * 	<li><b>true</b> if create new account success</li>
	 * 	<li><b>false</b> if account is exist or {@link DbAccess} is error</li>	 
	 * </ul>
	 * 			
	 * 			
	 * @author via
	 */
	public boolean createAccount(String pPass, String pPhone, String pSalt, String pPasscode){
		Connection connection = null;
		PreparedStatement pstm = null;		
		try{
			connection = DbAccess.getInstance(LOGGER).getConn();
			String sql = "{call create_account(?,?,?,?)}";
			pstm = connection.prepareCall(sql);
			pstm.setString(1, pPhone);
			pstm.setString(2, pPass);
			pstm.setString(3, pSalt);
			pstm.setString(4, pPasscode);
			int rs = pstm.executeUpdate();
			if(rs > 0) return true;
		}catch(SQLException sqlex){
			try {
				if(!connection.getAutoCommit())
					connection.rollback();
			} catch (SQLException e) {
				LOGGER.error(e.toString());
			}
			LOGGER.error("Create account :{}",sqlex.toString());
		}finally{			
			DbAccess.getInstance(LOGGER).closePreparedStatement(pstm);
			DbAccess.getInstance(LOGGER).closeConn(connection);			
		}		
		return false;
	}
	/**
	 * 
	 * @param pUsername
	 * @param pPhone
	 * @return <b>int</b>
	 * 
	 * <ul>
	 * 		<li><b>-1</b></li> if error when use database query 
	 * 		<li><b>0</b></li> if account not exist
	 * 		<li>1</li> if user name is exist
	 * 		<li>2</li> if phone number is exist	 	
	 * </ul>
	 * @author pham dinh vinh
	 */
	public int checkAccountExist(String pPhone){
		Connection connection = null;
		PreparedStatement pstm = null;
		PreparedStatement pstm2 = null;
		ResultSet rs = null;
		ResultSet rs2 = null;
		try{
			connection = DbAccess.getInstance(LOGGER).getConn();
			String sql = "select count(id) as c from account where phone=? and isLock=0";
			pstm = connection.prepareStatement(sql);
			pstm.setString(1, pPhone);
			
			rs = pstm.executeQuery();
			rs.next();
			if(rs.getInt("c") == 1){
				return 2;
			}			
			return 0;
		}catch(SQLException sqlex){			
			LOGGER.error("Check user: {}",sqlex.toString());
		}finally{
			DbAccess.getInstance(LOGGER).closeResultSet(rs);
			DbAccess.getInstance(LOGGER).closePreparedStatement(pstm);
			DbAccess.getInstance(LOGGER).closeResultSet(rs2);
			DbAccess.getInstance(LOGGER).closePreparedStatement(pstm2);
			DbAccess.getInstance(LOGGER).closeConn(connection);			
		}		
		return -1;
	}

	public int checkConfirmPasscode(String phone_number, String passcode) {
		Connection connection = null;
		java.sql.CallableStatement pstm = null;		
		try{
			connection = DbAccess.getInstance(LOGGER).getConn();
			String sql = "{call confirmPasscode(?,?,?)}";
			pstm = connection.prepareCall(sql);
			pstm.setString(1, phone_number);
			pstm.setString(2, passcode);			
			pstm.registerOutParameter(3, java.sql.Types.INTEGER);
			int rs = pstm.executeUpdate();
			int rs2 = pstm.getInt(3);
			return rs2;
		}catch(SQLException sqlex){
			try {
				if(!connection.getAutoCommit())
					connection.rollback();
			} catch (SQLException e) {
				LOGGER.error(e.toString());
			}
			LOGGER.error("Create account :{}",sqlex.toString());
		}finally{			
			DbAccess.getInstance(LOGGER).closePreparedStatement(pstm);
			DbAccess.getInstance(LOGGER).closeConn(connection);			
		}		
		return -1;
	}
}
