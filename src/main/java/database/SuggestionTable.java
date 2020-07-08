package database;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class SuggestionTable {
	
	public SuggestionTable() {
		createTable();
	}

	/**
	 * Creates the table in the database if one doesn't exist yet
	 */
	public void createTable() {
		String[] columns = {"channelID bigint", "messageID bigint", "endVoteDate bigint"};
		Database.createTable(TABLE_NAME, columns);
	}
	
	/**
	 * Shared across all objects <br>
	 * removeDate format: milliseconds since January 1, 1970, 00:00:00 GMT (Date object time format).
	 */
	public static List<suggestions.SuggestionIdentifier> suggestions = new ArrayList<suggestions.SuggestionIdentifier>();
	private static final String TABLE_NAME = "Suggestions";
	
	/**
	 * @return a List of all the suggestions' identifiers
	 */
	public List<suggestions.SuggestionIdentifier> getSuggestionsIdentifiers() {
		return suggestions;
	}

	
	
	/**
	 * Adds, to the database, information about when the voting period for a message will be over
	 * @param messageID the suggestion message's ID
	 * @param date the date object representing when the voting period is over
	 * @param channelID the ID of channel where the suggestion was made
	 */
	public void add(long channelID, long messageID, Date date) {
		suggestions.add(new suggestions.SuggestionIdentifier(channelID, messageID, date));
		
		Connection conn = Database.getConnection();
		PreparedStatement statement = null;
		try {
			statement = conn.prepareStatement("INSERT INTO " + TABLE_NAME + "(channelID, messageID, endVoteDate) values (?, ?, ?)");
			statement.setBigDecimal(1, BigDecimal.valueOf(channelID));
			statement.setBigDecimal(2, BigDecimal.valueOf(messageID));
			statement.setBigDecimal(3, BigDecimal.valueOf(date.getTime()));
			statement.executeUpdate();
		} catch (Exception e) {
			main.ErrorMessenger.inform("Failed to add a suggestion to the database");
			e.printStackTrace();
		} finally {
			Database.closeConnection();
			Database.closeStatement(statement);
		}
	}
	
	/**
	 * Loads the database information into the memory for faster access<br>
	 * Note: this operation must be performed before using the methods to get or remove info from the database
	 */
	public static void load() {
		Connection conn = Database.getConnection();
		PreparedStatement statement = null;
		ResultSet rs = null;
		try {
			statement = conn.prepareStatement("SELECT * FROM " + TABLE_NAME);
			rs = statement.executeQuery();
			while (rs.next()) {
				suggestions.SuggestionIdentifier suggestion = new suggestions.SuggestionIdentifier(
						rs.getLong(2),
						rs.getLong(3), 
						new Date(rs.getLong(4)));
				suggestions.add(suggestion);
				
				// Debug messages
//				Calendar cal = Calendar.getInstance(TimeZone.getTimeZone(Ref.timezone));
//				cal.setTime(new Date(rs.getLong(4)));
//				System.out.println("Day: " + cal.get(Calendar.DAY_OF_MONTH) + "/" + cal.get(Calendar.MONTH) + "/" + cal.get(Calendar.YEAR) + " | Time: " + cal.get(Calendar.HOUR_OF_DAY) + ":" + cal.get(Calendar.MINUTE));
			}	
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			Database.closeConnection();
			Database.closeStatement(statement);
			Database.closeResultSet(rs);
		}
	}
	
	                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                      
	/**
	 * Deletes the suggestion information from the memory and from the database
	 * @param ID the ID of the suggestion message
	 */
	public void remove(long channelID, long messageID) {
		
		Connection conn = Database.getConnection();
		PreparedStatement statement = null;
		try {
			statement = conn.prepareStatement("DELETE FROM " + TABLE_NAME + " WHERE messageID = " + messageID + " AND channelID = " + channelID);
			statement.executeUpdate();
		} catch (Exception e) {
			main.ErrorMessenger.inform("Failed to delete, from " + TABLE_NAME + ", the row where messageID = " + messageID
					+ " and channel = " + main.Main.botAPI.getTextChannelById(channelID).getAsMention() + " from the database");
			e.printStackTrace();
		} finally {
			Database.closeConnection();
			Database.closeStatement(statement);
		}
		

		suggestions.SuggestionIdentifier suggestionForRemoval = null;
		for (suggestions.SuggestionIdentifier suggestion : suggestions) {
			if (suggestion.messageID == messageID && suggestion.channelID == channelID)
				suggestionForRemoval = suggestion;
		}
		
		suggestions.remove(suggestionForRemoval);
	}
}
