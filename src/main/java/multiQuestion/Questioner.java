package multiQuestion;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.dv8tion.jda.core.events.message.priv.PrivateMessageReceivedEvent;

public class Questioner {
	public static final int DO_TRADE_QUESTIONNAIRE = 1;
	private static Integer[] PMQuestionnaireIDs = {DO_TRADE_QUESTIONNAIRE};
	
	private Map<Long, User> users = new HashMap<Long, User>();
	
	/**
	 * @return an array of integers containing the IDs of the PM questionnaires
	 */
	public static Integer[] getPMQuestionnaireIDs() {
		return PMQuestionnaireIDs;
	}
	
	/**
	 * Starts a new questionnaire for the given user. Via private messages.<br>
	 * <b>This will remove current questionnaire, if any, being then irrecoverable.</b>
	 * @param userID the ID of the user that's going to answer the questions
	 * @param questionnaire the questionnaire that the user shall be target of
	 * @param startChannelID the ID of the channel where the request was made.
	 */
	public void startNewQuestionnairePM(int questionnaireID, long userID, long startChannelID) {
		Questionnaire questionnaire = getQuestionnaire(questionnaireID);
		
		if (!users.containsKey(userID))
			users.put(userID, new User(userID));
		users.get(userID).startNewQuestionnairePM(questionnaireID, questionnaire, startChannelID);
	}
	
	/**
	 * Loads a new questionnaire for the given user according to the given info. Via private messages.<br>
	 * <b>This will remove current questionnaire, if any, being then irrecoverable.</b>
	 * @param userID the ID of the user that's going to answer the questions
	 * @param questionnaire the ID of questionnaire that the user shall be target of
	 * @param startChannelID the ID of the channel where the request was made.
	 * @param answers the list of answers, by order of questioning/answering
	 */
	public void loadQuestionnairePM(int questionnaireID, long userID, long startChannelID, List<String> answers) {
		users.put(userID, new User(userID));
		users.get(userID).loadQuestionnairePM(getQuestionnaire(questionnaireID), userID, startChannelID, answers);
	}
	
	/**
	 * Provides the questionnaire that possesses the given ID.
	 * @param questionnaireID the ID of the questionnaire.
	 * @return the questionnaire of given ID or null if no questionnaire has such ID.
	 */
	private Questionnaire getQuestionnaire(int questionnaireID) {
		switch (questionnaireID) {
		case DO_TRADE_QUESTIONNAIRE:
			return new DOTradeQuestionnaire();
		default:
			return null;
		}
	}
	
	/**
	 * Processes a new answer given by the user.
	 * @param event the PrivateMessageReceivedEvent containing the answer to be processed
	 */
	public void processAnswerPM(PrivateMessageReceivedEvent event) {
		users.get(event.getAuthor().getIdLong()).processNewAnswerPM(event);
	}
	
	/**
	 * Verifies if a specified user is currently being questioned via private messages.
	 * @param userID the ID of the target user of the verification
	 * @return true if the given user is currently being questioned via private messages
	 */
	public boolean userIsBeingQuestionedPM(long userID) {
		if (!users.containsKey(userID))
			return false;
		else
			return users.get(userID).isBeingQuestionedPM();
	}
}
