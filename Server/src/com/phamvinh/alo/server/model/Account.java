package com.phamvinh.alo.server.model;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.phamvinh.alo.server.common.PasswordUtil;
import com.phamvinh.alo.server.db.DbAccess;

public class Account {
	private static final Logger LOGGER = LogManager.getLogger();

	private int userID;
	private String phoneNumber;
	private String avatarSrc;
	private boolean isLock;
	private Calendar dateOfBirth;
	private String displayName;
	private String nameInDevice;

	
	public int checkToken(String token) {
		Connection connection = null;
		PreparedStatement pstm = null;
		ResultSet rs = null;
		try {
			connection = DbAccess.getInstance(LOGGER).getConn();
			String checkSQL = "select token from login_history where user_id=? order by login_time desc limit 1";
			pstm = connection.prepareStatement(checkSQL);
			pstm.setInt(1, this.userID);
			rs = pstm.executeQuery();
			@SuppressWarnings("unused")
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
	public int insertLoginInfo(String token, String remoteIP,
			int remotePort) {
		Connection connection = null;
		PreparedStatement pstm = null;
		try {
			connection = DbAccess.getInstance(LOGGER).getConn();
			String sql = "insert into login_history(`user_id`,`remote_ip`,`remote_port`,`token`) values(?,?,?,?)";
			pstm = connection.prepareStatement(sql);
			pstm.setInt(1, this.userID);
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
	public int getUserID() {
		return userID;
	}

	public void setUserID(int userID) {
		this.userID = userID;
	}

	public String getPhoneNumber() {
		return phoneNumber;
	}

	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}

	public String getAvatarSrc() {
		return avatarSrc;
	}

	public void setAvatarSrc(String avatarSrc) {
		this.avatarSrc = avatarSrc;
	}

	public boolean isLock() {
		return isLock;
	}

	public void setLock(boolean isLock) {
		this.isLock = isLock;
	}

	public Calendar getDateOfBirth() {
		return dateOfBirth;
	}

	public void setDateOfBirth(Calendar dateOfBirth) {
		this.dateOfBirth = dateOfBirth;
	}

	public String getDisplayName() {
		return displayName;
	}

	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}
	
	public String getNameInDevice() {
		return nameInDevice;
	}

	public void setNameInDevice(String nameInDevice) {
		this.nameInDevice = nameInDevice;
	}

	public void save() {
		
	}

	public static Account findOneByPhoneNumber(String phoneNumber) {
		Account account = null;
		Connection conn = null;
		PreparedStatement pstm = null;
		ResultSet rs = null;
		try {
			conn = DbAccess.getInstance(LOGGER).getConn();
			String sql = "select t.id, t.display, t.isLock from account as t where t.phone = ? limit 1";
			pstm = conn.prepareStatement(sql);
			pstm.setString(1, phoneNumber);
			rs = pstm.executeQuery();
			if (rs.next()) {
				account = new Account();
				account.setUserID(rs.getInt("id"));
				account.setDisplayName(rs.getString("display"));
				account.setPhoneNumber(phoneNumber);
				account.setLock(rs.getBoolean("isLock"));
			}
		} catch (SQLException e) {
			LOGGER.error(e);
		} finally {
			DbAccess.getInstance(LOGGER).closeResultSet(rs);
			DbAccess.getInstance(LOGGER).closeStatement(pstm);
			DbAccess.getInstance(LOGGER).closeConn(conn);

		}

		return account;
	}

	public static Account findOneById(int id) {
		Account account = null;
		Connection conn = null;
		PreparedStatement pstm = null;
		ResultSet rs = null;
		try {
			conn = DbAccess.getInstance(LOGGER).getConn();
			String sql = "select t.phone, t.display, t.isLock from account as t where t.id = ?";
			pstm = conn.prepareStatement(sql);
			pstm.setInt(1, id);
			rs = pstm.executeQuery();
			if (rs.next()) {
				account = new Account();
				account.setUserID(id);
				account.setDisplayName(rs.getString("display"));
				account.setPhoneNumber(rs.getString("phone"));
				account.setLock(rs.getBoolean("isLock"));
			}
		} catch (Exception e) {
			LOGGER.error(e);
		} finally {
			DbAccess.getInstance(LOGGER).closeResultSet(rs);
			DbAccess.getInstance(LOGGER).closeStatement(pstm);
			DbAccess.getInstance(LOGGER).closeConn(conn);
		}
		return account;
	}
	
	public Relationship findRelationShipWith(Account another){
		Relationship relationship = null;
		Connection conn = null;
		PreparedStatement pstm = null;
		ResultSet rs = null;
		try {
			conn = DbAccess.getInstance(LOGGER).getConn();
			String sql = "select relation.id, relation.name, relation.description from relation"
					+ "	inner join user_relation as ur on ur.relation_type_id = relation.id "
					+ "	where ur.user_id_1 = ? and ur.user_id_2 = ? ";
			pstm = conn.prepareStatement(sql);
			pstm.setInt(1, this.getUserID());
			pstm.setInt(2, another.getUserID());
			rs = pstm.executeQuery();
			if (rs.next()) {
				relationship = new Relationship();
				relationship.setId(rs.getInt("id"));
				relationship.setName(rs.getString("name"));
				relationship.setDescription(rs.getString("description"));
			}
		} catch (Exception e) {
			LOGGER.error(e);
		} finally {
			DbAccess.getInstance(LOGGER).closeResultSet(rs);
			DbAccess.getInstance(LOGGER).closeStatement(pstm);
			DbAccess.getInstance(LOGGER).closeConn(conn);
		}
		return relationship;
	}
	
	public boolean requestFriend(Account findedAccount) {
		if (findedAccount.getUserID() == this.userID) {
			return false;
		}
		Connection conn = null;
		CallableStatement cstm = null;

		try {
			conn = DbAccess.getInstance(LOGGER).getConn();
			
			String sql = "{call sp_insert_update_contact(?,?,?)}";
			cstm = conn.prepareCall(sql);
			cstm.setInt(1, this.userID);
			cstm.setString(2, findedAccount.getPhoneNumber());
			cstm.setString(3, findedAccount.getDisplayName());
			@SuppressWarnings("unused")
			int kq = cstm.executeUpdate();
			if (!conn.getAutoCommit()) {
				conn.commit();
			}
			return true;
		} catch (Exception e) {
			try {
				if (!conn.getAutoCommit()) {
					conn.rollback();
				}
			} catch (SQLException e1) {
				LOGGER.error(e1);
			}
			LOGGER.error(e);
			return false;
		} finally {
			DbAccess.getInstance(LOGGER).closeCallableStatement(cstm);
			DbAccess.getInstance(LOGGER).closeConn(conn);
		}
	}

	public List<Account> findFriends() {
		List<Account> friends = new ArrayList<Account>();
		Connection conn = null;
		CallableStatement cstm = null;
		ResultSet rs = null;
		try {
			conn = DbAccess.getInstance(LOGGER).getConn();
			String sql = "{call sp_select_friends_of(?)}";
			cstm = conn.prepareCall(sql);
			cstm.setInt(1, this.userID);

			rs = cstm.executeQuery();
			Account friend = null;
			while (rs.next()) {
				friend = new Account();
				friend.setUserID(rs.getInt("id"));
				friend.setPhoneNumber(rs.getString("phone"));
				friend.setDisplayName(rs.getString("display"));
				friend.setNameInDevice(rs.getString("nameInDevice"));
				friends.add(friend);
			}
		} catch (Exception e) {
			LOGGER.error(e);
		} finally {
			DbAccess.getInstance(LOGGER).closeResultSet(rs);
			DbAccess.getInstance(LOGGER).closeCallableStatement(cstm);
			DbAccess.getInstance(LOGGER).closeConn(conn);
		}
		return friends;
	}
	
	public int checkPassword(String pRawPassword) {
		Connection connection = null;
		PreparedStatement pstm = null;
		ResultSet rs = null;
		PreparedStatement getSaltStm = null;
		ResultSet getSaltRs = null;
		try {
			connection = DbAccess.getInstance(LOGGER).getConn();
			String getSaltSQL = "select salt from account where phone=? and isLock=0";
			getSaltStm = connection.prepareStatement(getSaltSQL);
			getSaltStm.setString(1, this.phoneNumber);
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
			pstm.setString(1, this.phoneNumber);
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
	
}
