package database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import main.Ref;

public class Database {
	/**If != 0, the database connection is being used 
	 * Being used => connection is open
	 * */
	private static int isBeingUsed = 0;
	
	private static final String DRIVER = "com.mysql.jdbc.Driver";
	private static Connection conn;
	
	// Must be run once after the program starts, before opening the connection
	public static void initializeDriver() {
		try {
			Class.forName(DRIVER);
		} catch (Exception e) {
			main.ErrorMessenger.inform("Failed to initialize the database driver");
			e.printStackTrace();
		}
	}
	
	/**
	 *  Opens the database connection if it is not yet open 
	 * @return the connection to the database
	 * */
	public static Connection getConnection() {
		if (isBeingUsed == 0)
			try {
				conn = DriverManager.getConnection("jdbc:mysql://" + Ref.databaseUrl + "/" + Ref.databaseName, Ref.databaseUsername, Ref.databasePassword);
			} catch (Exception e) {
				main.ErrorMessenger.inform("**Failed to open connection to the database**");
				if (Ref.databaseUrl.isEmpty()) {
					main.ErrorMessenger.inform("Please fill in the database url into config.txt");
				}
				if (Ref.databaseName.isEmpty()) {
					main.ErrorMessenger.inform("Please fill in the database name into config.txt");
				}
				if (Ref.databaseUsername.isEmpty()) {
					main.ErrorMessenger.inform("Please fill in the database username into config.txt");
				}
				if (Ref.databasePassword.isEmpty()) {
					main.ErrorMessenger.inform("Please fill in the database password into config.txt");
				}
				e.printStackTrace();
			}
		
		isBeingUsed++;
		return conn;
	}
	
	/** Closes the database connection if it is no longer being used */
	public static void closeConnection() {
		isBeingUsed--;
		if (isBeingUsed == 0)
			try {
				conn.close();
			} catch (SQLException e) {
				main.ErrorMessenger.inform("Failed to close the connection to the database: database access error occurred (SQLException)");
				e.printStackTrace();
			}
	}
	
	/** Closes the given ResultSet object for memory safety 
	 * @param rs the ResultSet object to be closed
	 * */
	public static void closeResultSet(ResultSet rs) {
		try {
			rs.close();
		} catch (SQLException e) {
			main.ErrorMessenger.inform("Failed to close a ResultSet object: database access error occurred (SQLException)");
			e.printStackTrace();
		}
	}
	
	/** Closes the given Statement object for memory safety 
	 * @param statement the Statement object to be closed
	 * */
	public static void closeStatement(Statement statement) {
		try {
			statement.close();
		} catch (SQLException e) {
			main.ErrorMessenger.inform("Failed to close a Statement object: database access error occurred (SQLException)");
			e.printStackTrace();
		}
	}
	
	/**Creates a new table (if one with the same name doesn't already exist) <br/><br/>
	 * <b>Column examples:</b> <br/>
	 * channelID bigint <br/>
	 * name varchar(2000)
	 * @param columns The columns that the table will have.
	 */
	public static void createTable(String tableName, String[] columns) {
		Connection connection = Database.getConnection();
		PreparedStatement mysqlStatement = null;
		StringBuilder statement = new StringBuilder("CREATE TABLE IF NOT EXISTS " + tableName + "(id int NOT NULL AUTO_INCREMENT");
		for(String column : columns) {
			statement.append(", " + column);
		}
		statement.append(", PRIMARY KEY(id))");
		try {
			mysqlStatement = connection.prepareStatement(statement.toString());
			mysqlStatement.executeUpdate();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			closeConnection();
			closeStatement(mysqlStatement);
		}
	}
}
