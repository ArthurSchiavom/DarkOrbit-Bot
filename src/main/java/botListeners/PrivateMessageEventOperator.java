package botListeners;

import main.Ref;
import multiQuestion.Questioner;
import net.dv8tion.jda.core.events.message.priv.PrivateMessageReceivedEvent;

public class PrivateMessageEventOperator {
	Questioner questioner = Ref.questioner;
	
	void operate(PrivateMessageReceivedEvent event) {
		long userID = event.getAuthor().getIdLong();
		
		if (questioner.userIsBeingQuestionedPM(userID))
			questioner.processAnswerPM(event);
	}
}
