package sales;

import java.util.HashMap;
import java.util.Map;

public class Account {
	private String sellerID;
	private Map<String, String> description = new HashMap<String, String>();
	
	/**
	 * Creates a new account object with the given seller ID and description
	 * @param sellerID the ID of the user selling this account
	 * @param description the account's information
	 */
	Account(String sellerID, Map<String, String> description) {
		this.sellerID = sellerID;
		this.description = description;
	}
	
	/**
	 * Retrieves the ID of the user selling this account
	 * @return the ID of the user selling this account
	 */
	public String getSellerID() {
		return sellerID;
	}
	
	/**
	 * Retrieves information on this account
	 * @return the information on this account, with the parameters as keys and parameter information as values
	 */
	public Map<String, String> getDescription() {
		return description;
	}
}
