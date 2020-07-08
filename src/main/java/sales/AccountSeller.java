package sales;

import java.util.HashMap;
import java.util.Map;

public class AccountSeller {
	/** The biggest number identifier */
	private static int latestIdentifier;
	private static Map<Integer, Account> accounts;
	
	public AccountSeller() {
		// Must be loaded before the latestIdentifier
		accounts = new HashMap<Integer, Account>();
		// loadSavedAccounts - from the database class
		latestIdentifier = getGreatestIdentifier();
	}
	
	/**
	 * Adds a new account to the list
	 * @param sellerID the ID of the account seller
	 * @param accountInformation the hashmap containing the parameters as keys and parameter information as values
	 */
	public static void addAccount(String sellerID, Map<String, String> accountInformation) {
		latestIdentifier++;
		accounts.put(latestIdentifier, new Account(sellerID, accountInformation));
	}
	
	/**
	 * Gets the account whose identifier is the given integer
	 * @param identifier the account's identifier
	 * @return the Account associated with the given identifier
	 */
	public Account getAccount(Integer identifier) {
		return accounts.get(identifier);
	}
	
	/**
	 * Retrieves the biggest number identifier based on accounts listed
	 * @return the biggest number identifier based on accounts listed
	 */
	private int getGreatestIdentifier() {
		int greatest = 0;
		if (accounts != null) {
			for(int i : accounts.keySet()) {
				if (i > greatest)
					greatest = i;
			}
		}
		else
			System.out.println("!! The account list must be defined before the latest identifier is retrieved !!");
		
		return greatest;
	}
}
