package multiQuestion;

import main.Ref;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.MessageEmbed;
import net.dv8tion.jda.core.events.message.priv.PrivateMessageReceivedEvent;
import sales.AccountSeller;

public class DOTradeQuestionnaire extends Questionnaire {
	AccountSeller accSeller = Ref.accSeller;
	
	int additionalInfoMaxChars = 600;
	public DOTradeQuestionnaire() {
		setQuestionnaireID(Questioner.DO_TRADE_QUESTIONNAIRE);
		
		setIntroduction("Hello! I am here to guide you trough the process of adding an account to the marketplace."
				+ "\nFor this, I will ask you some questions about your account."
				+ "\nPlease be objective, your answers will be displayed to the user under the appropiate categories when requested."
				+ "\n"
				+ "\nExample:"
				+ "\nFor the questions about price, lasers and shields, the answers were the following:"
				+ "\n`30 USD`, `Laser1, Laser2 and Laser3` and `Shield1 and Shield2`"
				+ "\n"
				+ "\nDisplayed to buyer:"
				+ "\n**Account price:** 30 USD"
				+ "\n**Lasers**"
				+ "\nLaser1, Laser2 and Laser3"
				+ "\n**Shields**"
				+ "\nShield1 and Shield2"
				+ "\netc."
				+ "\n\n**__Type `cancel` to cancel the process at any time__**"
				+ "\n**__Type `cancel` to cancel the process at any time__**");
		
		addQuestion("Will you be trading or selling the account?", "Trading or selling");
		addQuestion("What's the account price and it's respective currency?"
				+ "\n"
				+ "\nExamples:"
				+ "\n30 USD"
				+ "\n50 EUR", 
				"Price");
		
		addQuestion("In what server did you play with this account?", "Server");
		addQuestion("What lasers does the account possess?", "Lasers");
		addQuestion("Which shields does the account have?", "Shields");
		addQuestion("What drones does the account have?", "Drones");
		addQuestion("Which extras does the account have?", "Extras");
		addQuestion("Noted! Now please specify what P.E.T. Gear the account has.", "P.E.T. Gear");
		addQuestion("Ok, please tell me which P.E.T. Protocols the account has.", "P.E.T. Protocols");
		addQuestion("What ship designs does the account own?", "Ship Designs");
		addQuestion("Now please tell me which drone designs the account has.", "Drone Designs");
		addQuestion("What ammunition does the account possess?", "Ammunition");
		addQuestion("How many pilot points does the account have?"
				+ "\nPlease answer with numbers only! **A number between 0 and 50**, inclusive"
				, "Pilot Points");
		addQuestion("How many energy leaches do you have?"
				+ "\nPlease answer with numbers only!"
				, "Energy leaches in the tech");
		addQuestion("How many booty keys does the account have?", "Booty Keys");
		addQuestion("How many credits does the account have?", "Credits");
		addQuestion("How much Uridium does the account have?", "Uridium");
		addQuestion("How many Galaxy Gate spins do you have and what Galaxy Gates do you have open?", "Galaxy Gates");
		addQuestion("How much Seprom is on your lasers?", "Seprom");
		addQuestion("How much experience do you have?", "Experience");
		addQuestion("How many rankpoints do you have?", "Rank Points");
		addQuestion("Would you like to include any additional information? **If not, answer with `no`.**"
				+ "\nMax of " + additionalInfoMaxChars + " characters", "Additional information");
		
		setConclusion("```md" + 
				"\n##   Thanks! Your account has been added to the marketplace!   ##```");
		
		setCancel("cancel", 
				"```md"
				+ "\n#  The questionnaire has been canceled  #```");
	}
	
	@Override
	protected boolean processAnswer(PrivateMessageReceivedEvent event) {
		String answer = event.getMessage().getContentRaw();
		
		if (cancelVerification(answer))
			return true;
		
		boolean hasQuestionnaireEnded = false;
		
		switch (getCurrentParameter()) {
		case "Trading or selling":
			interpreteTradingAnswer(answer);
			hasQuestionnaireEnded = false;
			break;
		case "Pilot Points":
			interpreteNumberRangeAnswer(answer, 0, 50, 0);
			hasQuestionnaireEnded = false;
			break;
		case "Energy leaches in the tech":
			interpreteNumberRangeAnswer(answer, 0, 9999, 0);
			hasQuestionnaireEnded = false;
			break;
		case "Additional information":
			hasQuestionnaireEnded = interpreteAdditionalInformationAnswer(answer);
			break;
		default:
			hasQuestionnaireEnded = nextQuestion(answer, 150, 0);
		}
		
		return hasQuestionnaireEnded;
	}
	
	/**
	 * Only allows the user to proceed if the answer only contains numbers and is a number in the range minimum-maximum and skips the next 
	 * skipAmount questions
	 * @param answer the answer to be analyzed
	 * @param minimum the minimum number that the answer might be
	 * @param maximum the maximum number that the answer might be
	 * @param skipAmount the amount of questions, immediately after this one, that should be skipped
	 */
	private void interpreteNumberRangeAnswer(String answer, int minimum, int maximum, int skipAmount) {
		try {
			int number = Integer.parseInt(answer);
			if (number >= minimum && number <= maximum)
				nextQuestion(answer, skipAmount);
			else
				messageUserAsync("Please enter a **__number in the range " + minimum + " to " + maximum + "__**. **Numbers only!**", true);
		} catch (NumberFormatException e) {
			messageUserAsync("Please enter a **number in the range " + minimum + " to " + maximum + "**. **__Numbers only!__**", true);
		}
	}
	
	/**
	 * If the answer contains "trad" then it is considered as trading and if it contains "sell", it is considered as selling.<br>
	 * If both, or none, are present, the questionnaire does not proceed.<br>
	 * If trading, the price question is skipped
	 * @param answer the user's answer to be analyzed
	 */
	private void interpreteTradingAnswer(String answer) {
		answer = answer.toLowerCase();
		boolean isTrading = (answer.contains("trad"));
		boolean isSelling = (answer.contains("sell"));
		if (isTrading || isSelling) {
			if (!(isTrading && isSelling)) {
				if (isTrading) {
					nextQuestion("Trading", 1);
				} else if (isSelling)
					nextQuestion("Selling", 0);
			} else {
				messageUserAsync("Could not understand your answer. Please answer with `trading` **__or__** `selling`.", true);
			}
		} else {
			messageUserAsync("Could not understand your answer. Please answer with `trading` or `selling`.", true);
		}
	}

	/**
	 * Error if the question has more characters than the limit for additional info.<br>
	 * If answer is "no", then this parameter is skipped.
	 * @param answer the user's answer.
	 * @return true if there are no more questions left to answer.
	 */
	private boolean interpreteAdditionalInformationAnswer(String answer) {
		boolean success;
		int answerLenght = answer.length();
		if (answerLenght > additionalInfoMaxChars) {
			success = false;
			messageUserAsync("Your question has " + answerLenght + " characters, which exceeds the limit of " + additionalInfoMaxChars + " characters.", success);
		}
		else if (answer.toLowerCase().equals("no")) {
			success = nextQuestion("", 0);
		}
		else {
			success = nextQuestion(answer, 0);
		}
		return success;
	}
	
	@Override
	protected void conclusionActions() {
		int marketID = Ref.accSeller.nextIdentifier();
		MessageEmbed embed;
		if (getAnswer("Trading or selling").equals("Selling")) {
			embed = buildAnnouncementMessage(true, getAnswer("Server"), getAnswer("Price"), Integer.toString(marketID), Long.toString(getUserID()));
			getApi().getTextChannelById(Ref.salesChannelID).sendMessage(embed).queue(
					(msg) -> {
						Message message = msg;
						Ref.accSeller.addAccount(true, true, getUserID(), message.getChannel().getIdLong(), message.getIdLong(), getResults(), marketID);
						getApi().getTextChannelById(Ref.logChannelID).sendMessage(accSeller.compileAccountInfo(marketID)).queue();
					},
					(throwable) -> {
						main.ErrorMessenger.inform("Unable to post the following into the Dark Orbit sales channel: ");
						main.ErrorMessenger.inform(embed);
					});
		}
		else {
			embed = buildAnnouncementMessage(false, getAnswer("Server"), null, Integer.toString(marketID), Long.toString(getUserID()));
			getApi().getTextChannelById(Ref.tradesChannelID).sendMessage(embed).queue(
					(msg) -> {
						Message message = msg;
						Ref.accSeller.addAccount(true, false, getUserID(), message.getChannel().getIdLong(), message.getIdLong(), getResults(), marketID);
						getApi().getTextChannelById(Ref.logChannelID).sendMessage(accSeller.compileAccountInfo(marketID)).queue();
					},
					(throwable) -> {
						main.ErrorMessenger.inform("Unable to post the following into the Dark Orbit trades channel: ");
						main.ErrorMessenger.inform(embed);
					});
		}
	}
	
	/**
	 * Compiles an announcement message embed with the given information.
	 * @param selling if the user is selling the account.
	 * @param server the server where the ship in question is.
	 * @param price if selling, the account's price, can be null otherwise.
	 * @param announcementID the ID of the announcement
	 * @param sellerID the ID of the seller
	 * @return an announcement message embed with the given information.
	 */
	protected MessageEmbed buildAnnouncementMessage(boolean isForSale, String server, String price, String announcementID, String sellerID) {
		EmbedBuilder eb = new EmbedBuilder();
		if (isForSale) {
			eb.setTitle("For sale: Dark Orbit Reloaded account");
			eb.setDescription("Server: " + server
			+ "\nPrice: " + price
			+ "\nID: " + announcementID
			+ "\n"
			+ "\nOwner: <@!" + sellerID + ">");
			eb.setThumbnail(Ref.darkOrbitLogoUrl);
			eb.setColor(Ref.salesEmbedColor);
			eb.setFooter("Type !at Info " + announcementID + " to see more information about this account.", Ref.salesEmbedFooterImageUrl);
			return eb.build();
		}
		else {
			eb.setTitle("For trade: Dark Orbit Reloaded account");
			eb.setDescription("Server: " + server
			+ "\nID: " + announcementID
			+ "\n"
			+ "\nSeller: <@!" + sellerID + ">");
			eb.setThumbnail(Ref.darkOrbitLogoUrl);
			eb.setColor(Ref.tradesEmbedColor);
			eb.setFooter("Type !at Info " + announcementID + " to see more information about this account.", Ref.tradesEmbedFooterImageUrl);
			return eb.build();
		}
	}
}
