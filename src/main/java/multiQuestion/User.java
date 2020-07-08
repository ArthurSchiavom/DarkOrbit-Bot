package multiQuestion;

import java.util.List;

import net.dv8tion.jda.core.events.message.priv.PrivateMessageReceivedEvent;

public class User {
	/** The questionnaire currently being answered in private messages */
	private Questionnaire currentQuestionnairePM = null;
	private long userID;
	
	protected User(long userID) {
		this.userID = userID;
	}
	
	/**
	 * Starts a new questionnaire for the given user. Via private messages.<br>
	 * <b>This will remove current questionnaire, if any, being then irrecoverable.</b>
	 * @param questionnaire the questionnaire that the user shall be target of.
	 * @param startChannelID the ID of the channel where the request was made.
	 */
	protected void startNewQuestionnairePM(int questionnaireID, Questionnaire questionnaire, long startChannelID) {
		currentQuestionnairePM = questionnaire;
		if (!currentQuestionnairePM.startPM(questionnaireID, userID, startChannelID))
			currentQuestionnairePM = null;
	}
	
	protected void loadQuestionnairePM(Questionnaire questionnaire, long userID, long startChannelID, List<String> answers) {
		currentQuestionnairePM = questionnaire;
		questionnaire.load(userID, startChannelID, answers);
	}
	
	/**
	 * Verifies if the user is currently being questioned via private messages.
	 * @return true if the user is currently being questioned via private messages
	 */
	protected boolean isBeingQuestionedPM() {
		return !(currentQuestionnairePM == null);
	}
	
	/**
	 * Processes a new answer given by the user.
	 * @param event the PrivateMessageReceivedEvent containing the answer to be processed
	 */
	void processNewAnswerPM(PrivateMessageReceivedEvent event) {
		if (currentQuestionnairePM.processAnswer(event))
			currentQuestionnairePM = null;
	}
}
