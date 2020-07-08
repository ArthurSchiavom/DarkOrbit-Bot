package sales;

import java.util.LinkedHashMap;
import java.util.Map;

public class Account {
	private int marketID;
	private boolean isSelling;
	private long ownerID;
	private long messageID;
	private long channelID;
	private Map<String, String> parameters = new LinkedHashMap<String, String>();
	
	/**
	 * Creates a new account object with the given seller ID and description.
	 * @param isSelling if the account is being sold or not. (false = is being traded)
	 * @param sellerID the ID of the user selling this account.
	 * @param messageID the announcement message ID.
	 * @param accountInformation the hashmap containing the parameters as keys and parameter information as values.
	 * @param description the account's information.
	 */
	Account(int marketID, boolean isSelling, long ownerID, long channelID, long messageID, LinkedHashMap<String, String> description) {
		this.marketID = marketID;
		this.isSelling = isSelling;
		this.ownerID = ownerID;
		this.parameters = description;
		this.channelID = channelID;
		this.messageID = messageID;
	}
	
	/**
	 * If this account is, or not, for sale.
	 * @return true if the account is for and false if it's for trade.
	 */
	public boolean isForSale() {
		return isSelling;
	}
	
	/**
	 * Provides this account's market ID.
	 * @return this account's market ID.
	 */
	public int getMarketID() {
		return marketID;
	}
	
	/**
	 * Retrieves the ID of the user selling this account.
	 * @return the ID of the user selling this account.
	 */
	public long getOwnerID() {
		return ownerID;
	}
	
	/**
	 * Provides the ID of the marketplace channel where this account was announced.
	 * @return the ID of the marketplace channel where this account was announced.
	 */
	public long getChannelID() {
		return channelID;
	}
	
	/**
	 * Provides the ID of the announcement message of when this account was introduced in the market.
	 * @return the ID of the announcement message of when this account was introduced in the market.
	 */
	public long getMessageID() {
		return messageID;
	}
	
	/**
	 * Retrieves information on this account
	 * @return the information on this account, with the parameters as keys and parameter information as values
	 */
	public Map<String, String> getAccountInfo() {
		return parameters;
	}
}
