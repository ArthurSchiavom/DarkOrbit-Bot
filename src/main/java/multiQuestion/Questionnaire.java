package multiQuestion;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import database.PMQuestionnaireTable;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.events.message.priv.PrivateMessageReceivedEvent;

/**
 * This class can be used to make several questions to the user and retrieve his answers
 * <br>
 * <br>
 * Configurations:<br>
 * <br>
 * setQuestionnaireID()<br>
 * setIntroduction()<br>
 * addQuestion()<br>
 * setConclusion()<br>
 * setCancel()<br>
 * setErrorMessage - has a default value<br>
 * <br>
 * interpreteAnswer (Override)
 */
abstract class Questionnaire {
	private PMQuestionnaireTable database = new PMQuestionnaireTable();
	
	private int questionnaireID;
	private String introductionMessage = "";
	private String conclusionMessage = "";
	private String cancelMessage = "";
	private String cancelTrigger = "";
	private String unableToPMErrorMessage = "";
	private List<String> answers = new ArrayList<String>();
	private List<String> questions = new ArrayList<String>();
	private List<String> parameters = new ArrayList<String>();
	/** Questions range from 0 to questions.lenght()-1 */
	private int currentQuestion = 0;
	private long userID = 0;
	private long startChannelID = 0;
	private JDA api = main.Main.botAPI;
	

	/**
	 * @return the ID of this questionnaire.
	 */
	protected int getQuestionnaireID() {
		return questionnaireID;
	}
	
	/**
	 * Sets the questionnaire ID
	 * @param ID the ID of this questionnaire
	 */
	protected void setQuestionnaireID(int ID) {
		questionnaireID = ID;
	}
	
	/**
	 * @return the ID of the account seller.
	 */
	protected long getUserID() {
		return userID;
	}
	
	/**
	 * @return the bot's JDA object.
	 */
	protected JDA getApi() {
		return api;
	}
	
	/**
	 * Sets the bot to stop the questionnaire when a given message is sent by the user.<br>
	 * The trigger is not case-sensitive
	 * @param trigger the message that triggers the questionnaire to stop (not case-sensitive)
	 * @param message the message sent to the user after the questionnaire has stopped
	 */
	protected void setCancel(String trigger, String message) {
		cancelTrigger = trigger.toLowerCase();
		cancelMessage = message;
	}
	
	protected String getCancelmessage() {
		return cancelMessage;
	}
	
	/**
	 * Provides the answer for the given parameter.
	 * @param parameter the parameter that the answer answers to.
	 * @return the answer for the given parameter.
	 */
	protected String getAnswer(String parameter) {
		int index = 99999;
		for (int i = 0; i < parameters.size(); i++) {
			if (parameters.get(i).equals(parameter)) {
				index = i;
				break;
			}
		}
		if (answers.size() > index)
			return answers.get(index);
		else
			return "That question does not exist or was not answered";
	}
	
	/**
	 * Gets the number of the current question (ranges from 0 till numberOfQuestions -1)
	 * @return the number of the current question
	 */
	protected int getCurrentQuestionNumber() {
		return currentQuestion;
	}
	
	/**
	 * Retrieves the parameter that the current question answers to
	 * @returnthe parameter that the current question answers to
	 */
	protected String getCurrentParameter() {
		return parameters.get(currentQuestion);
	}
	
	/**
	 * Sets the introduction text that is sent to the user before the questioning starts
	 * @param intro the text to be sent to the user before the questioning starts
	 */
	protected void setIntroduction(String intro) {
		introductionMessage = intro;
	}
	
	/**
	 * Sets the conclusion text that is sent to the user after the questioning ends
	 * @param conclusion the text to be sent to the user after the questioning ends
	 */
	protected void setConclusion(String conclusion) {
		this.conclusionMessage = conclusion;
	}
	
	/**
	 * Defines the message that will be sent in the channel where the questionnaire was requested if an error happens.<br>
	 * <br>
	 * Defaults to: Could not DM you @User. Are DMs not allowed to be sent to you? Please check your account settings and try again.
	 * @param message the message to be sent in case an error happens
	 */
	protected void setErrorMessage(String message) {
		unableToPMErrorMessage = message;
	}
	
	/**
	 * Starts the questionnaire:
	 * <br>- Sets the sellerID.
	 * <br>- Sends a message to the user with the introduction message.
	 * <br>- Asks the first question.
	 * <br>- Adds the questionnaire to the database.
	 * @param userID the ID of the user to be questioned.
	 * @param startChannelID the ID of the channel where the request was made.
	 * @return true if the questionnaire started successfully and false otherwise.
	 */
	protected boolean startPM(int questionnaireID, long userID, long startChannelID) {
		boolean success = true;
		unableToPMErrorMessage = "Could not DM you <@!" + userID + ">."
				+ " Are DMs not allowed to be sent to you? Please check your account settings and try again.";

		this.questionnaireID = questionnaireID;
		this.userID = userID;
		this.startChannelID = startChannelID;
		
		if (!introductionMessage.isEmpty()) {
			success = messageUserSync(introductionMessage, false);

			if (success == true)
				messageUserAsync(questions.get(0), true);
		}
		else
			success = messageUserSync(questions.get(0), true);
		
		database.add(questionnaireID, userID, startChannelID, answers);
		return success;
	}
	
	/**
	 * Verifies if the message is the cancel trigger and, if so, message the user with the cancel message.
	 * @param possibleTrigger the message to be analyzed
	 * @return true if the message is the cancel trigger. If so, the questionnaire is removed from the database.
	 */
	protected boolean cancelVerification(String possibleTrigger) {
		if (possibleTrigger.toLowerCase().trim().equals(cancelTrigger)) {
			messageUserAsync(cancelMessage, true);
			database.remove(questionnaireID, userID);
			return true;
		}
		else
			return false;
	}
	
	/**
	 * Verifies the user's answer and, if it is a proper answer, add it to the answer list and move the cursor to the next question.
	 * @return true if the questionnaire has ended
	 */
	protected abstract boolean processAnswer(PrivateMessageReceivedEvent event);
	
	/**
	 * Will be called when all the questions were answered.
	 */
	protected abstract void conclusionActions();
	
	/**
	 * Adds a new answer to the answer list and move the question cursor to the next question.<br>
	 * Will skip the next skipAmount comments.
	 * @param answer the new answer. Empty string = no answer.
	 * @param skipAmount the amount of questions, immediately after this one, that should be skipped.
	 * @return true if there are no questions left to answer or if there was an error sending the message. If so, the questionnaire is removed from the database.
	 */
	protected boolean nextQuestion(String answer, int skipAmount) {
		answers.add(answer);
		currentQuestion++;
		database.update(questionnaireID, userID, answers);
		
		for (int i = 0; i < skipAmount; i++) {
			if (!isPastLastQuestion()) {
				answers.add("");
				currentQuestion++;
			}
			else
				break;
		}
		
		if (isPastLastQuestion()) {
			if (!conclusionMessage.isEmpty())
				messageUserAsync(conclusionMessage, true);
			
//			DEBUGGING
//			System.out.println("\n\n\n**Results**");
//			for (int i = 0; i < questions.size(); i++) {
//				if (!answers.get(i).isEmpty())
//					System.out.println("\nParameter: " + parameters.get(i) + "\nAnswer: " + answers.get(i));
//			}
			
			database.remove(questionnaireID, userID);
			conclusionActions();
			return true;
		}
		else {
			if (!messageUserSync(questions.get(currentQuestion), true)) {
				return true;
			}
			return false;
		}
	}
	
	/**
	 * Adds a new answer to the answer list and move the question cursor to the next question.<br>
	 * If the question has more characters than the limit defined, the user will be warned about it and the question will not be added.<br>
	 * Will skip the next skipAmount comments.
	 * @param answer the new answer.
	 * @param maxAmountOfChars the maximum amount of characters allowed for this answer.
	 * @param skipAmount the amount of questions, immediately after this one, that should be skipped.
	 * @return true if there are no questions left to answer.
	 */
	protected boolean nextQuestion(String answer, int maxAmountOfChars, int skipAmount) {
		int amountOfChars = answer.length();
		if (answer.length() > maxAmountOfChars) {
			messageUserAsync("Your question has " + amountOfChars + " characters, which exceeds the limit of " + maxAmountOfChars + " characters.", true);
			return false;
		}
		
		return nextQuestion(answer, skipAmount);
	}

	/**
	 * Sends a message to the user answering this questionnaire.<br>
	 * If unable to message the user, then a message will be sent on the channel where the process was requested.
	 * @param msg the message to be sent.
	 * @param sendErrorMessage if false, nothing happens when failed to deliver a message to an user. If true, on failure, an error message is sent to the channel where the process was requested.
	 */
	protected void messageUserAsync(String msg, boolean sendErrorMessage) {
		try {
			api.getUserById(userID).openPrivateChannel().complete().sendMessage(msg).queue(
					null, 
					
					(throwable) -> {
						if (sendErrorMessage)
							sendUnableToPMErrorMessage();
					});
		} catch (Exception e) {
			if (sendErrorMessage)
				sendUnableToPMErrorMessage();
			main.ErrorMessenger.inform("Failed to DM an user to start the questionnaire.");
			System.out.println("Could not DM user to start questionnaire.");
			e.printStackTrace();
		}
	}
	
	/**
	 * Sends a message to the user answering this questionnaire.<br>
	 * If unable to message the user, then a message will be sent on the channel where the process was requested.<br>
	 * The thread waits for the message to be sent or fail to send to then continue.
	 * @param msg the message to be sent.
	 * @param sendErrorMessage if false, nothing happens when failed to deliver a message to an user.
	 * @return true if the message was successfully sent.
	 */
	protected boolean messageUserSync(String msg, boolean sendErrorMessage) {
		boolean success = true;
		try {
			api.getUserById(userID).openPrivateChannel().complete().sendMessage(msg).complete();
		} catch (Exception e) {
			e.printStackTrace();
			success = false;
			System.out.println("Could not DM user of ID " + getUserID() + " to start/continue the questionnaire. Does the user have DMs blocked?");
			if (sendErrorMessage)
				sendUnableToPMErrorMessage();
		}
		return success;
	}
	
	/**
	 * Adds a new question and it's corresponding parameter to the list
	 * @param question the question to be added
	 * @param parameter the parameter that the question answers to
	 */
	protected void addQuestion(String question, String parameter) {
		questions.add(question);
		parameters.add(parameter);
	}
	
	/**
	 * Verifies if the last question was already successfully answered
	 * @return true if the last question was already successfully answered
	 */
	protected boolean isPastLastQuestion() {
		int lastQuestion = questions.size()-1;
		return (currentQuestion > lastQuestion);
	}
	
	/**
	 * Sends a message to the channel where the process was requesting informing the user that an error has occurred.
	 */
	protected void sendUnableToPMErrorMessage() {
		api.getTextChannelById(startChannelID).sendMessage(unableToPMErrorMessage).queue();
	}
	
	/**
	 * Returns the results of the questionnaire (contains only information about the questions that were answered)
	 * @return a HashMap with parameters as keys and answers as values
	 */
	protected LinkedHashMap<String, String> getResults() {
		LinkedHashMap<String, String> results = new LinkedHashMap<String, String>();
		
		for(int index = 0; index<answers.size(); index++) {
			String answer = answers.get(index);
			if (!answer.isEmpty())
				results.put(parameters.get(index), answer);
		}
		
		return results;
	}
	
	protected void load(long userID, long startChannelID, List<String> answers) {
		this.userID = userID;
		this.startChannelID = startChannelID;
		
		if (answers.size() == 1) {
			if (answers.get(0).isEmpty())
				currentQuestion = 0;
			else {
				this.answers = answers;
				currentQuestion = 1;
			}
		}
		else {
			// If answers has indexes 0-4 filled, 5 questions were answered. The one being currently answered is the one that comes after, the 6th question, of index 5.
			currentQuestion = answers.size();
			this.answers = answers;
		}

		unableToPMErrorMessage = "Could not DM you <@!" + userID + ">."
				+ " Are DMs not allowed to be sent to you? Please check your account settings and try again.";
	}
}
