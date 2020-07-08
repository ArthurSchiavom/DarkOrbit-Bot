package database;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLTimeoutException;
import java.util.List;

import main.Ref;
import multiQuestion.Questioner;
import sales.AccountSeller;

public class PMQuestionnaireTable {
	
	public final static String NAME = "PMQuestionnaire";
	AccountSeller accSeller = Ref.accSeller;
	
	/**
	 * Creates the table in the database if one doesn't exist yet
	 */
	public static void createTable() {
		String[] columns = {"questionnaireID int", "userID bigint",  "startChannelID bigint", "answers varchar(10000)"};
		Database.createTable(NAME, columns);
	}
	
	public void add(int questionnaireID, long userID, long startChannelID, List<String> answers) {
		Connection conn = Database.getConnection();
		if (conn == null)
			return;
		PreparedStatement stm = null;
		
		
		try {
			Integer[] PMQuestionnaireIDs = Questioner.getPMQuestionnaireIDs();
			
			if (PMQuestionnaireIDs.length>0) {
				StringBuilder sb = new StringBuilder();
				sb.append("DELETE FROM " + NAME + " WHERE userID = ? AND questionnaireID IN (?");
				for (int i = 1; i < PMQuestionnaireIDs.length; i++) {
					sb.append(",?");
				}
				sb.append(")");
				
				stm = conn.prepareStatement(sb.toString());
				stm.setBigDecimal(1, BigDecimal.valueOf(userID));
				for (int i = 0; i<PMQuestionnaireIDs.length; i++) {
					stm.setInt(i+2, PMQuestionnaireIDs[i]);
				}
				
				stm.executeUpdate();
			}
		} catch (SQLTimeoutException e) {
			main.ErrorMessenger.inform("Failed to delete previous questionnaires for this user from the database: " + userID
					+ "\n**Timed out.**");
			e.printStackTrace();
		} catch (SQLException e) {
			main.ErrorMessenger.inform("Failed to delete previous questionnaires for this user from the database: " + userID);
			e.printStackTrace();
		}
		
		try {
			stm = conn.prepareStatement("INSERT INTO " + NAME + "(questionnaireID, userID, startChannelID, answers) VALUES (?, ?, ?, ?)");
			stm.setInt(1, questionnaireID);
			stm.setBigDecimal(2, BigDecimal.valueOf(userID));
			stm.setBigDecimal(3, BigDecimal.valueOf(startChannelID));
			stm.setString(4, accSeller.formatAnswers(answers));
			stm.executeUpdate();
		} catch (SQLTimeoutException e) {
			main.ErrorMessenger.inform("Failed to insert a new questionnaire into the database. (Questionnaire for the user <@!" + userID + ">."
					+ "\nIf the bot restarts while the questionnaire is being answered, it will be lost."
					+ "\n**Timed out.**");
			e.printStackTrace();
		} catch (SQLException e) {
			main.ErrorMessenger.inform("Failed to insert a new questionnaire into the database. (Questionnaire for the user <@!" + userID + ">."
					+ "\nIf the bot restarts while the questionnaire is being answered, it will be lost.");
			e.printStackTrace();
		} finally {
			Database.closeConnection();
			Database.closeStatement(stm);
		}
	}
	
	public void remove(int questionnaireID, long userID) {
		Connection conn = Database.getConnection();
		if (conn == null)
			return;
		
		PreparedStatement stm = null;
		try {
			stm = conn.prepareStatement("DELETE FROM " + NAME + " WHERE userID = ? AND questionnaireID = ?");
			stm.setBigDecimal(1, BigDecimal.valueOf(userID));
			stm.setBigDecimal(2, BigDecimal.valueOf(questionnaireID));
			stm.executeUpdate();
		} catch (SQLTimeoutException e) {
			main.ErrorMessenger.inform("Failed to delete a questionnaire from the database. **Timed out.**");
			e.printStackTrace();
		} catch (SQLException e) {
			main.ErrorMessenger.inform("Failed to delete a questionnaire from the database.");
			e.printStackTrace();
		} finally {
			Database.closeConnection();
			Database.closeStatement(stm);
		}
	}
	
	public void update(int questionnaireID, long userID, List<String> answers) {
		Connection conn = Database.getConnection();
		if (conn == null)
			return;
		
		PreparedStatement stm = null;
		try {
			stm = conn.prepareStatement("UPDATE " + NAME + " SET answers = ? WHERE userID = ? AND questionnaireID = ?");
			stm.setString(1, accSeller.formatAnswers(answers));
			stm.setBigDecimal(2, BigDecimal.valueOf(userID));
			stm.setInt(3, questionnaireID);
			stm.executeUpdate();
		} catch (SQLTimeoutException e) {
			main.ErrorMessenger.inform("Failed to update a questionnaire in the database. **Timed out.**");
			e.printStackTrace();
		} catch (SQLException e) {
			main.ErrorMessenger.inform("Failed to update a questionnaire in the database.");
			e.printStackTrace();
		} finally {
			Database.closeConnection();
			Database.closeStatement(stm);
		}
	}
	
	public static void load() {
		Connection conn = Database.getConnection();
		PreparedStatement stm = null;
		ResultSet rs = null;
		try {
			stm = conn.prepareStatement("SELECT * FROM " + NAME);
			rs = stm.executeQuery();
			while (rs.next()) {
				main.Ref.questioner.loadQuestionnairePM(rs.getInt(2), rs.getLong(3), rs.getLong(4), Ref.accSeller.formatAnswers(rs.getString(5)));
			}
			
		} catch (SQLTimeoutException e) {
			main.ErrorMessenger.inform("Failed to load questionnaires from the database. **Timed out.**");
			e.printStackTrace();
		} catch (SQLException e) {
			main.ErrorMessenger.inform("Failed to load questionnaires from the database.");
			e.printStackTrace();
		} finally {
			Database.closeConnection();
			Database.closeStatement(stm);
			Database.closeResultSet(rs);
		}
	}
}
