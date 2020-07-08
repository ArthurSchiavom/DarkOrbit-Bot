package multiQuestion;

import java.util.HashMap;
import java.util.Map;

import net.dv8tion.jda.core.events.message.priv.PrivateMessageReceivedEvent;

public class Questioner {
	private Map<Long, User> users = new HashMap<Long, User>();
	
	/**
	 * Starts a new questionnaire for the given user. Via private messages.<br>
	 * <b>This will remove current questionnaire, if any, being then irrecoverable.</b>
	 * @param userID the ID of the user that's going to answer the questions
	 * @param questionnaire the questionnaire that the user shall be target of
	 */
	public void startNewQuestionnairePM(long userID, Questionnaire questionnaire) {
		if (!users.containsKey(userID))
			users.put(userID, new User(userID));
		users.get(userID).startNewQuestionnairePM(questionnaire);
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
