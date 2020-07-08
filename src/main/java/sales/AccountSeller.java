package sales;

import java.awt.Color;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import database.AccountsTable;
import main.Ref;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.entities.MessageEmbed;
import net.dv8tion.jda.core.entities.User;

public class AccountSeller {
	AccountsTable database;
	
	public final static String SEPARATOR_BORDER = "!9u5D";
	public final static String SEPARATOR_MIDDLE = "!0u3D";
	
	/** The biggest number identifier */
	private int latestIdentifier;
	private Map<Integer, Account> accounts;
	JDA api;
	
	public void load() {
		// Must be loaded before the latestIdentifier
		accounts = new HashMap<Integer, Account>();
		database = new AccountsTable();
		database.load();
		latestIdentifier = getGreatestIdentifier();
		
		api = main.Main.botAPI;
	}
	
	/**
	 * Adds a new account to the list.
	 * @param isSelling if the account is being sold or not. (false = is being traded)
	 * @param sellerID the ID of the account seller.
	 * @param channelID the ID of channel where the account insertion in the market was announced.
	 * @param messageID the announcement message ID.
	 * @param accountInformation the hashmap containing the parameters as keys and parameter information as values.
	 * @param identifier the account's identifier number on the market.
	 */
	public void addAccount(boolean isNew, boolean isSelling, long sellerID, long channelID, long messageID, LinkedHashMap<String, String> accountInformation, int identifier) {
		accounts.put(identifier, new Account(identifier, isSelling, sellerID, channelID, messageID, accountInformation));
		if (isNew)
			database.add(accounts.get(identifier));
	}
	
	/**
	 * Provides a new identifier for an account to be added to the marketplace
	 * @return a new identifier for an account to be added to the marketplace
	 */
	public int nextIdentifier() {
		latestIdentifier++;
		return latestIdentifier;
	}
	
	/**
	 * Gets the account whose identifier is the given integer.
	 * @param identifier the account's market identifier.
	 * @return the account associated with the given identifier or null if there is no account with the given ID;
	 */
	public Account getAccount(Integer identifier) {
		if (accounts.containsKey(identifier))
			return accounts.get(identifier);
		else
			return null;
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
	
	/**
	 * Provides an embed containing the compiled information about the account of the given marketID
	 * @param marketID the market ID of the account
	 * @return an embed containing the compiled information about the account of the given marketID
	 */
	public MessageEmbed compileAccountInfo(int marketID) {
		Account acc = getAccount(marketID);
		
		EmbedBuilder eb = new EmbedBuilder();
		eb.setTitle("Account Info");
		if (acc == null) {
			eb.setDescription("There is no account of ID " + marketID + ".");
			eb.setColor(Color.RED);
			return eb.build();
		}

		User owner = api.getUserById(acc.getOwnerID());
		if (owner != null)
			eb.setDescription("Owner: " + owner.getName() + "#" + owner.getDiscriminator() 
				+ "\nMarket ID: " + marketID);
		else
			eb.setDescription("Owner: unable to retrieve");
		
		Map<String, String> accountInfo = acc.getAccountInfo();
		for (String parameter : accountInfo.keySet()) {
			eb.addField(parameter, accountInfo.get(parameter), false);
		}
		
		if (acc.isForSale()) {
			eb.setColor(Ref.salesEmbedColor);
			eb.setFooter("Account sales", Ref.salesEmbedFooterImageUrl);
		}
		else {
			eb.setColor(Ref.tradesEmbedColor);
			eb.setFooter("Account trades", Ref.tradesEmbedFooterImageUrl);
		}
		eb.setThumbnail(Ref.darkOrbitLogoUrl);
		
		return eb.build();
	}
	
	/**
	 * Compiles the given list of answers into the String format.
	 * @param answersRaw the list of answers in the List object format.
	 * @return the given list of answers in the String format.
	 */
	public String formatAnswers(List<String> answersRaw) {
		StringBuilder answers = new StringBuilder();
		if (answersRaw.isEmpty())
			return "";
		
		answers.append(answersRaw.get(0));
		for (int i = 1; i<answersRaw.size(); i++) {
			answers.append(SEPARATOR_BORDER + answersRaw.get(i));
		}
		
		return answers.toString();
	}
	
	/**
	 * Compiles the given list of answers into the List object format.
	 * @param answersRaw the list of answers in the String format.
	 * @return the given list of answers in the List object format.
	 */
	public List<String> formatAnswers(String answersRaw) {
		List<String> answers = new ArrayList<String>();
		for (String answer : answersRaw.split(SEPARATOR_BORDER))
			answers.add(answer);
		
		return answers;
	}
	
	/**
	 * Compiles the given account information Map into a String.
	 * @param accInfo the account info stored in the Map format. (Parameter, parameter info)
	 * @return a string containing the account information. Returns an empty string if there is no information available.
	 */
	public String formatAccInfo(Map<String, String> accInfo) {
		if (accInfo.isEmpty())
			return "";
		
		StringBuilder result = new StringBuilder();
		List<String> parameters = new ArrayList<String>();
		
		for (String parameterName : accInfo.keySet()) {
			parameters.add(parameterName + SEPARATOR_MIDDLE + accInfo.get(parameterName));
		}
		
		result.append(parameters.get(0));
		for (int i = 1; i<parameters.size(); i++) {
			result.append(SEPARATOR_BORDER + parameters.get(i));
		}
		
		return result.toString();
	}
	
	/**
	 * Compiles the given account information into the Map format. (Parameter, parameter info)
	 * @param accInfo the account information in the String format.
	 * @return the given account information in the Map format.
	 */
	public LinkedHashMap<String, String> formatAccInfo(String accInfo) {
		LinkedHashMap<String, String> result = new LinkedHashMap<String, String>();
		String[] parameters = accInfo.split(SEPARATOR_BORDER);
		
		for (String parameter : parameters) {
			String[] parameterInfo = parameter.split(SEPARATOR_MIDDLE);
			if (parameterInfo.length > 1)
				result.put(parameterInfo[0], parameterInfo[1]);
			else
				main.ErrorMessenger.inform("Failed to load an account from the database.");
		}
		
		return result;
	}
}
