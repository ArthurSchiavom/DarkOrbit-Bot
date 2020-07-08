package database;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLTimeoutException;

import main.Ref;
import sales.Account;
import sales.AccountSeller;

public class AccountsTable {
	public final static String NAME = "Accounts";
	private AccountSeller accSeller = Ref.accSeller;
	
	public AccountsTable() {
		createTable();
	}
	
	public void createTable() {
		String[] columns = {"marketID INT", "isForSale BOOLEAN", "ownerID BIGINT", "channelID BIGINT", "messageID BIGINT", "description VARCHAR(10000)"};
		Database.createTable(NAME, columns);
	}
	
	public void add(Account acc) {
		Connection conn = Database.getConnection();
		PreparedStatement stm = null;
		
		try {
			stm = conn.prepareStatement("INSERT INTO " + NAME + "(marketID, isForSale, ownerID, channelID, messageID, description) VALUES (?, ?, ?, ?, ?, ?)");
			stm.setInt(1, acc.getMarketID());
			stm.setBoolean(2, acc.isForSale());
			stm.setBigDecimal(3, BigDecimal.valueOf(acc.getOwnerID()));
			stm.setBigDecimal(4, BigDecimal.valueOf(acc.getChannelID()));
			stm.setBigDecimal(5, BigDecimal.valueOf(acc.getMessageID()));
			stm.setString(6, accSeller.formatAccInfo(acc.getAccountInfo()));
			stm.executeUpdate();
		} catch (SQLTimeoutException e) {
			main.ErrorMessenger.inform("Failed to add an account to the database. The information on this account will be lost on restart."
					+ "\n__**Timed out**__");
		} catch (SQLException e) {
			main.ErrorMessenger.inform("Failed to add an account to the database. The information on this account will be lost on restart.");
		} finally {
			Database.closeConnection();
			Database.closeStatement(stm);
		}
	}
	
	/**
	 * Replaces the description of a certain market account.
	 * @param marketID the ID of the account whose information will be updated.
	 * @param newValue the new description to replace the old description with.
	 */
	public void remove(int marketID) {
		Connection conn = Database.getConnection();
		PreparedStatement stm = null;
		try {
			stm = conn.prepareStatement("REMOVE FROM " + NAME + " WHERE marketID = ?");
			stm.setInt(1, marketID);
			stm.executeUpdate();
		} catch (SQLTimeoutException e) {
			main.ErrorMessenger.inform("Failed to add an account to the database. The information on this account will be lost on restart."
					+ "\n__**Timed out**__");
		} catch (SQLException e) {
			main.ErrorMessenger.inform("Failed to add an account to the database. The information on this account will be lost on restart.");
		} finally {
			Database.closeConnection();
			Database.closeStatement(stm);
		}
	}
	
	public void update(int marketID, String newValue) {
		Connection conn = Database.getConnection();
		PreparedStatement stm = null;
		try {
			stm = conn.prepareStatement("UPDATE " + NAME + " SET description = ? WHERE marketID = ?");
			stm.setString(1, newValue);
			stm.setInt(2, marketID);
			stm.executeUpdate();
		} catch (SQLTimeoutException e) {
			main.ErrorMessenger.inform("Failed to add an account to the database. The information on this account will be lost on restart."
					+ "\n__**Timed out**__");
		} catch (SQLException e) {
			main.ErrorMessenger.inform("Failed to add an account to the database. The information on this account will be lost on restart.");
		} finally {
			Database.closeConnection();
			Database.closeStatement(stm);
		}
	}
	
	/**
	 * Replaces the information present in a given column of a certain market account. This information must be of the type BIGINT
	 * @param marketID the ID of the account whose information will be updated.
	 * @param column one of the following columns to be updated: ownerID; channelID; messageID.
	 * @param newValue the new value to replace the old value with.
	 */
	public void update(int marketID, String column, long newValue) {
		Connection conn = Database.getConnection();
		PreparedStatement stm = null;
		try {
			stm = conn.prepareStatement("UPDATE " + NAME + " SET " + column + " = ? WHERE marketID = ?");
			stm.setBigDecimal(1, BigDecimal.valueOf(newValue));
			stm.setInt(2, marketID);
			stm.executeUpdate();
		} catch (SQLTimeoutException e) {
			main.ErrorMessenger.inform("Failed to add an account to the database. The information on this account will be lost on restart."
					+ "\n__**Timed out**__");
		} catch (SQLException e) {
			main.ErrorMessenger.inform("Failed to add an account to the database. The information on this account will be lost on restart.");
		} finally {
			Database.closeConnection();
			Database.closeStatement(stm);
		}
	}
	
	/**
	 * Loads the accounts present in the Accounts table into the memory
	 * @return true if the database information was successfully loaded
	 */
	public boolean load() {
		boolean success = true;
		
		Connection conn = Database.getConnection();
		PreparedStatement stm = null;
		ResultSet rs = null;
		
		try {
			stm = conn.prepareStatement("SELECT * FROM " + NAME);
			rs = stm.executeQuery();
			while(rs.next()) {
				accSeller.addAccount(false, rs.getBoolean(3), rs.getLong(4), rs.getLong(5), rs.getLong(6), accSeller.formatAccInfo(rs.getString(7)), rs.getInt(2));
			}
			// accSeller.add() for all rows
			
		} catch (SQLTimeoutException e) {
			main.ErrorMessenger.inform("Failed to load accounts information from the database. (SQLTimeoutException)"
					+ "\n__**Timed out**__");
			success = false;
		} catch (SQLException e) {
			main.ErrorMessenger.inform("Failed to load accounts information from the database. (SQLException)");
			success = false;
		} finally {
			Database.closeConnection();
			Database.closeStatement(stm);
			Database.closeResultSet(rs);
		}
		
		return success;
	}
}
