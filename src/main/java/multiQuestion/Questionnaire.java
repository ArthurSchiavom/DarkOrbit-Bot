package multiQuestion;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.events.message.priv.PrivateMessageReceivedEvent;

/**
 * This class can be used to make several questions to the user and retrieve his answers
 * <br>
 * <br>
 * Configurations:<br>
 * <br>
 * setIntroduction()<br>
 * addQuestion()<br>
 * setConclusion()<br>
 * setCancel()<br>
 * <br>
 * interpreteAnswer (Override)
 */
abstract class Questionnaire {
	private String introductionMessage = "";
	private String conclusionMessage = "";
	private String cancelMessage = "";
	private String cancelTrigger = "";
	private List<String> answers = new ArrayList<String>();
	private List<String> questions = new ArrayList<String>();
	private List<String> parameters = new ArrayList<String>();
	/** Questions range from 0 to questions.lenght()-1 */
	private int currentQuestion = 0;
	private long sellerID = 0;
	private JDA api;
	
	/**
	 * @return the ID of the account seller
	 */
	protected long getSellerID() {
		return sellerID;
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
	 * @param intro the text to be sent to the user before the questioning starts
	 */
	protected void setConclusion(String conclusion) {
		this.conclusionMessage = conclusion;
	}
	
	/**
	 * Starts the questionnaire:
	 * <br>- Sets the sellerID
	 * <br>- Sends a message to the user with the introduction message
	 * <br>- Asks the first question
	 * @param userID the ID of the user to be questioned
	 */
	protected void start(long sellerID) {
		this.sellerID = sellerID;
		api = main.Main.botAPI;
		if (!introductionMessage.isEmpty()) {
			messageUser(introductionMessage);
			messageUser(questions.get(0));
		}
	}
	
	/**
	 * Verifies if the message is the cancel trigger and, if so, message the user with the cancel message.
	 * @param possibleTrigger the message to be analyzed
	 * @return true if the message is the cancel trigger
	 */
	protected boolean cancelVerification(String possibleTrigger) {
		if (possibleTrigger.toLowerCase().trim().equals(cancelTrigger)) {
			messageUser(cancelMessage);
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
	 * Adds a new answer to the answer list and move the question cursor to the next question<br>
	 * Will skip the next skipAmount comments.
	 * @param answer the new answer
	 * @param skipAmount the amount of questions, immediately after this one, that should be skipped
	 * @return true if there are no questions left to answer
	 */
	protected boolean nextQuestion(String answer, int skipAmount) {
		answers.add(answer);
		currentQuestion++;
		
		for (int i = 0; i < skipAmount; i++) {
			if (!isPastLastQuestion()) {
				answers.add("");
				currentQuestion++;
			}
		}
		
		if (isPastLastQuestion()) {
			if (!conclusionMessage.isEmpty())
				messageUser(conclusionMessage);
			
			System.out.println("\n\n\n**Results**");
			for (int i = 0; i < questions.size(); i++) {
				if (!answers.get(i).isEmpty())
					System.out.println("\nParameter: " + parameters.get(i) + "\nAnswer: " + answers.get(i));
			}
			
			conclusionActions();
			return true;
		}
		else {
			messageUser(questions.get(currentQuestion));
			return false;
		}
	}
	
	/**
	 * Adds a new answer to the answer list and move the question cursor to the next question.<br>
	 * If the question has more characters than the limit defined, the user will be warned about it and the question will not be added.<br>
	 * Will skip the next skipAmount comments.
	 * @param answer the new answer
	 * @param maxAmountOfChars the maximum amount of characters allowed for this answer
	 * @param skipAmount the amount of questions, immediately after this one, that should be skipped
	 * @return true if there are no questions left to answer
	 */
	protected boolean nextQuestion(String answer, int maxAmountOfChars, int skipAmount) {
		int amountOfChars = answer.length();
		if (answer.length() > maxAmountOfChars) {
			messageUser("Your question has " + amountOfChars + " characters, which exceeds the limit of " + maxAmountOfChars + " characters.");
			return false;
		}
		
		return nextQuestion(answer, skipAmount);
	}
	
	/**
	 * Sends a message to the user answering this questionnaire
	 * @param msg the message to be sent
	 */
	void messageUser(String msg) {
		api.getUserById(sellerID).openPrivateChannel().complete().sendMessage(msg).queue();
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
	 * Returns the results of the questionnaire (contains only information about the questions that were answered)
	 * @return a HashMap with parameters as keys and answers as values
	 */
	protected HashMap<String, String> getResults() {
		HashMap<String, String> results = new HashMap<String, String>();
		
		for(int index = 0; index<answers.size(); index++) {
			String answer = answers.get(index);
			if (!answer.isEmpty())
				results.put(parameters.get(index), answer);
		}
		
		return results;
	}
}
