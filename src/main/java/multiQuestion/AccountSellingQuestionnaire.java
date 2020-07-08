package multiQuestion;

import net.dv8tion.jda.core.events.message.priv.PrivateMessageReceivedEvent;

public class AccountSellingQuestionnaire extends Questionnaire {
	
	int additionalInfoMaxChars = 0;
	public AccountSellingQuestionnaire() {
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
		additionalInfoMaxChars = 600;
		addQuestion("Would you like to include any additional information? Max of " + additionalInfoMaxChars + " characters", "Additional information");
		
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
			interpreteAnswerNumberRange(answer, 0, 50, 0);
			hasQuestionnaireEnded = false;
			break;
		case "Energy leaches in the tech":
			interpreteAnswerNumberRange(answer, 0, 9999, 0);
			hasQuestionnaireEnded = false;
			break;
		case "Additional information":
			hasQuestionnaireEnded = nextQuestion(answer, additionalInfoMaxChars, 0);
			break;
		default:
			hasQuestionnaireEnded = nextQuestion(answer, 150, 0);
		}
		
		return hasQuestionnaireEnded;
	}
	
	/**
	 * Only allows the user to proceed if the answer only contains numbers and is a number in the range minimum-maximum
	 * @param answer the answer to be analyzed
	 * @param minimum
	 * @param maximum
	 * @param skipAmount
	 */
	void interpreteAnswerNumberRange(String answer, int minimum, int maximum, int skipAmount) {
		try {
			int number = Integer.parseInt(answer);
			if (number >= minimum && number <= maximum)
				nextQuestion(answer, skipAmount);
			else
				messageUser("Please enter a **__number in the range " + minimum + " to " + maximum + "__**. **Numbers only!**");
		} catch (NumberFormatException e) {
			messageUser("Please enter a **number in the range " + minimum + " to " + maximum + "**. **__Numbers only!__**");
		}
	}
	
	/**
	 * If the answer contains "trad" then it is considered as trading and if it contains "sell", it is considered as selling.<br>
	 * If both, or none, are present, the questionnaire does not proceed.<br>
	 * If trading, the price question is skipped
	 * @param answer the user's answer to be analyzed
	 */
	void interpreteTradingAnswer(String answer) {
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
				messageUser("Could not understand your answer. Please answer with `trading` **__or__** `selling`.");
			}
		} else {
			messageUser("Could not understand your answer. Please answer with `trading` or `selling`.");
		}
	}

	
	@Override
	protected void conclusionActions() {
		sales.AccountSeller.addAccount(Long.toString(getSellerID()), getResults());
	}
}
